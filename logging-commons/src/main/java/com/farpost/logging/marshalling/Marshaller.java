package com.farpost.logging.marshalling;

import org.bazhenov.logging.LogEntry;

/**
 * Имплементации данного интерфейса сериализуют и десериализуют обьекты типа {@link org.bazhenov.logging.LogEntryImpl}
 * в формат wire протокола.
 * <p />
 * Внимание, все имплементации этого интерфейса должны быть потокобезопасны.
 */
public interface Marshaller {

	String marshall(LogEntry entry) throws MarshallerException;

	LogEntry unmarshall(String data) throws MarshallerException;
}
