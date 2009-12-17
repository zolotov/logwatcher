package org.bazhenov.logging.aggregator;

import org.bazhenov.logging.*;
import org.bazhenov.logging.storage.LogEntryMatcher;
import static org.bazhenov.logging.storage.MatcherUtils.isMatching;

import java.util.concurrent.*;
import java.util.*;
import static java.lang.Thread.interrupted;

public class ExecutorServiceAggregator implements Aggregator {

	private final ExecutorService service;
	private final int batchSize = 500;

	public ExecutorServiceAggregator(ExecutorService service) {
		this.service = service;
	}

	public Collection<AggregatedLogEntry> aggregate(Iterable<LogEntry> entries,
	                                          Collection<LogEntryMatcher> matchers) {
		LinkedList<Future<Collection<AggregatedLogEntry>>> futures = new LinkedList<Future<Collection<AggregatedLogEntry>>>();
		Iterator<LogEntry> iterator = entries.iterator();
		while ( iterator.hasNext() ) {
			LogEntry[] batch = new LogEntry[batchSize];
			int batchIndex = 0;
			while ( batchIndex < batchSize && iterator.hasNext() ) {
				batch[batchIndex++] = iterator.next();
			}
			futures.add(emit(batch, matchers));
		}
		Map<String, AggregatedLogEntry> result = new HashMap<String, AggregatedLogEntry>();
		for ( Future<Collection<AggregatedLogEntry>> future : futures ) {
			try {
				Collection<AggregatedLogEntry> aggregatedEntries = future.get();
				for ( AggregatedLogEntry entry : aggregatedEntries ) {
					String checksum = entry.getSampleEntry().getChecksum();
					if ( result.containsKey(checksum) ) {
						((AggregatedLogEntryImpl)result.get(checksum)).merge(entry);
					}else{
						result.put(checksum, entry);
					}
				}
			} catch ( InterruptedException e ) {
				interrupted();
			} catch ( ExecutionException e ) {
				throw new RuntimeException(e);
			}
		}
		return result.values();
	}

	private Future<Collection<AggregatedLogEntry>> emit(LogEntry[] batch, Collection<LogEntryMatcher> matchers) {
		return service.submit(new Task(batch, matchers));
	}
}

class Task implements Callable<Collection<AggregatedLogEntry>> {

	private final LogEntry[] batch;
	private final Collection<LogEntryMatcher> matchers;

	public Task(LogEntry[] batch, Collection<LogEntryMatcher> matchers) {
		this.batch = batch;
		this.matchers = matchers;
	}

	public Collection<AggregatedLogEntry> call() throws Exception {
		Map<String, AggregatedLogEntry> result = new HashMap<String, AggregatedLogEntry>();
		for ( LogEntry entry : batch ) {
			if ( entry == null ) {
				continue;
			}
			if ( isMatching(entry, matchers) ) {
				if ( result.containsKey(entry.getChecksum()) ) {
					AggregatedLogEntryImpl aggregated = (AggregatedLogEntryImpl) result.get(entry.getChecksum());
					aggregated.happensAgain(entry);
				}else{
					result.put(entry.getChecksum(), new AggregatedLogEntryImpl(entry));
				}
			}
		}
		return result.values();
	}
}
