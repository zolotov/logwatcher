package org.bazhenov.logging.storage;

import com.farpost.timepoint.Date;
import org.bazhenov.logging.LogEntry;
import org.bazhenov.logging.AggregatedLogEntry;

import java.util.*;

/**
 * Имплементации этого интерфейса сохраняют обьекты типа LogEntry в постоянном хранилище? будь то
 * реляционная база данных или XML файл.
 */
public interface LogStorage {

	/**
	 * Сохраняет обьект {@link LogEntry} в постоянном хранилище.
	 * <p/>
	 * Учтите что сохранение не влияет на обьект LogEntry. Ему не выдается никакого
	 * идентификационного номера. Если вы передадите один и тот же LogEntry в этот метод два раза,
	 * то произойдет две записи в постоянное хранилище.
	 *
	 * @param entry запись лога
	 * @throws LogStorageException в случае внутренней ошибки
	 */
	void writeEntry(LogEntry entry) throws LogStorageException;

	/**
	 * Возвращает колличество записей в хранилище за указанную дату
	 *
	 * @param date дата
	 * @return колличество записей
	 * @throws LogStorageException в случае внутренней ошибки
	 * @deprecated
	 */
	int getEntryCount(Date date) throws LogStorageException;

	/**
	 * Возвращает список записей за указанную дату. Возвращаемый вам список принадлежит вам. Вы имеете
	 * право использовать и менять его по собственному усмотрению.
	 *
	 * @param date дата
	 * @return список записей за заданную дату
	 * @throws LogStorageException в случае внутренней ошибки
	 */
	List<AggregatedLogEntry> getEntries(Date date) throws LogStorageException;

	/**
	 * Подсчитывает колличество записей в хранилище с заданными условиями.
	 *
	 * @param criterias условия отбора записей или {@code null} если интересует общее количество
	 *                  записей в хранилище
	 * @return колличество записей
	 * @throws LogStorageException      в случае внутренней ошибки
	 * @throws InvalidCriteriaException в случае если заданные критерии неверны
	 */
	int countEntries(Collection<LogEntryMatcher> criterias)
		throws LogStorageException, InvalidCriteriaException;

	/**
	 * Удаляет из хранилища записи с указанной контрольной суммой за указанную дату
	 *
	 * @param checksum контрольная сумма
	 * @param date     дата
	 * @throws LogStorageException в случае внутренней ошибки
	 */
	void removeEntries(String checksum, Date date) throws LogStorageException;

	/**
	 * Создает синоним для контрольной суммы. Все записи с первой переданной контрольной суммой
	 * будут превращены в записи со второй контрольной суммой. Так же имплементации обязаны
	 * предоставлять гарантии для сохранения этого маппинга в будущем. То есть если позже
	 * приходят записи с первой контрольной суммой, им автоматически будет переписыватся контрольная
	 * сумма на alias.
	 *
	 * @param checksum контролная сумма, которую надо смапить
	 * @param alias    контрольная сумма - синоним
	 */
	void createChecksumAlias(String checksum, String alias) throws LogStorageException;

	List<AggregatedLogEntry> getEntries(Collection<LogEntryMatcher> criterias)
		throws LogStorageException, InvalidCriteriaException;
}
