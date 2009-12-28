package org.bazhenov.logging.aggregator;

import org.bazhenov.logging.AggregatedLogEntry;
import org.bazhenov.logging.marshalling.MarshallerException;
import org.bazhenov.logging.storage.LogEntryMatcher;

import java.util.Collection;

/**
 * Имплементации этого интферйеса занимаются тем, что фильтрую и аггрегируют объекты типа
 * {@link org.bazhenov.logging.LogEntry} в объекты типы {@link AggregatedLogEntry}.
 * <p />
 * На входе аггрегатор принимает итератор по строчкам и условия фильтрации. Итератор по строчкам
 * нужен для того чтобы можно было делегировать десериализацию объектов типа {@code LogEntry}
 * в параллельные потоки. Это необходимо для того, чтобы не блокировать основной поток, который
 * занимается тем, что читает данные из хранилища.
 */
public interface Aggregator {

	/**
	 * Аггрегирует и фильтрует объекты типа {@link org.bazhenov.logging.LogEntry}. На выходе
	 * возвращает аггрегированные объекты типа {@link AggregatedLogEntry}.
	 *
	 * @param entries итератор по строковым представлениям объектов {@link org.bazhenov.logging.LogEntry}
	 * @param matchers условия отбора
	 * @return коллекцию отфильрованных и саггрегированных записей
	 * @throws MarshallerException в случае ошибок десирализации
	 */
	Collection<AggregatedLogEntry> aggregate(Iterable<String> entries, Collection<LogEntryMatcher> matchers)
		throws MarshallerException;
}
