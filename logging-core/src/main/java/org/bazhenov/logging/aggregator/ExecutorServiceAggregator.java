package org.bazhenov.logging.aggregator;

import com.farpost.logging.marshalling.Marshaller;
import org.bazhenov.logging.AggregatedEntry;
import org.bazhenov.logging.AggregatedEntryImpl;
import org.bazhenov.logging.LogEntry;
import org.bazhenov.logging.storage.LogEntryMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.interrupted;
import static org.bazhenov.logging.storage.MatcherUtils.isMatching;

public class ExecutorServiceAggregator implements Aggregator {

	private final Marshaller marshaller;
	private final ExecutorService service;
	private volatile int batchSize = 500;
	private final Logger log = LoggerFactory.getLogger(ExecutorServiceAggregator.class);

	public ExecutorServiceAggregator(Marshaller marshaller, ExecutorService service) {
		this.marshaller = marshaller;
		this.service = service;
	}

	public ExecutorServiceAggregator(Marshaller marshaller) {
		this.marshaller = marshaller;
		int threads = Runtime.getRuntime().availableProcessors();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(threads, threads, 0L,
			TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		this.service = executor;
	}

	public Collection<AggregatedEntry> aggregate(Iterable<String> entries,
	                                             Collection<LogEntryMatcher> matchers) {
		long start = currentTimeMillis();
		List<Future<Collection<AggregatedEntry>>> futures = emitTasks(entries, matchers);
		Map<String, AggregatedEntry> result = aggregateResults(futures);
		long end = currentTimeMillis();
		if ( log.isInfoEnabled() ) {
			log.info("Filtering and aggregating complete in " + (end - start) + "ms.");
		}
		return result.values();
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	private Map<String, AggregatedEntry> aggregateResults(
		List<Future<Collection<AggregatedEntry>>> futures) {
		Map<String, AggregatedEntry> result = new HashMap<String, AggregatedEntry>();
		long start = currentTimeMillis();
		for ( Future<Collection<AggregatedEntry>> future : futures ) {
			try {
				Collection<AggregatedEntry> aggregatedEntries = future.get();
				for ( AggregatedEntry entry : aggregatedEntries ) {
					String checksum = entry.getChecksum();
					if ( result.containsKey(checksum) ) {
						AggregatedEntryImpl impl = (AggregatedEntryImpl) result.get(checksum);
						impl.happensAgain(entry.getCount(), entry.getLastTime());
					} else {
						result.put(checksum, entry);
					}
				}
			} catch ( InterruptedException e ) {
				interrupted();
			} catch ( ExecutionException e ) {
				throw new RuntimeException(e);
			}
		}
		long end = currentTimeMillis();
		if ( log.isInfoEnabled() ) {
			log.info("Waiting results and aggregating complete in " + (end - start) + "ms.");
		}
		return result;
	}

	private List<Future<Collection<AggregatedEntry>>> emitTasks(Iterable<String> entries,
	                                                            Collection<LogEntryMatcher> matchers) {
		LinkedList<Future<Collection<AggregatedEntry>>> futures = new LinkedList<Future<Collection<AggregatedEntry>>>();
		Iterator<String> iterator = entries.iterator();
		int size = 0;
		long start = currentTimeMillis();
		while ( iterator.hasNext() ) {
			String[] batch = new String[batchSize];
			int batchIndex = 0;
			do {
				batch[batchIndex++] = iterator.next();
				size++;
			} while ( batchIndex < batchSize && iterator.hasNext() );
			futures.add(service.submit(new Task(batch, matchers)));
		}
		long end = currentTimeMillis();
		if ( log.isInfoEnabled() ) {
			log.info("Emiting tasks complete in " + (end - start) + "ms. (" + size + " entries)");
		}
		return futures;
	}

	private class Task implements Callable<Collection<AggregatedEntry>> {

		private final Collection<LogEntryMatcher> matchers;
		private String[] batch;

		public Task(String[] batch, Collection<LogEntryMatcher> matchers) {
			this.batch = batch;
			this.matchers = matchers;
		}

		public Collection<AggregatedEntry> call() throws Exception {
			Map<String, AggregatedEntry> result = new HashMap<String, AggregatedEntry>();
			long start = currentTimeMillis();
			int processedCnt = 0;
			for ( String marshalledEntry : batch ) {
				if ( marshalledEntry == null ) {
					continue;
				}
				LogEntry entry = marshaller.unmarshall(marshalledEntry);
				if ( isMatching(entry, matchers) ) {
					if ( result.containsKey(entry.getChecksum()) ) {
						AggregatedEntryImpl aggregated = (AggregatedEntryImpl) result.get(entry.getChecksum());
						aggregated.happensAgain(1, entry.getDate());
					} else {
						result.put(entry.getChecksum(), new AggregatedEntryImpl(entry));
					}
				}
				processedCnt++;
			}
			/**
			 * Данный null необходим, чтобы GC мог высвободить память отводимую под пакеты досрочно.
			 */
			batch = null;
			long end = currentTimeMillis();
			if ( log.isInfoEnabled() ) {
				log.info(
					"Batch processing of " + processedCnt + " entries done in " + (end - start) + "ms.");
			}
			return result.values();
		}
	}
}


