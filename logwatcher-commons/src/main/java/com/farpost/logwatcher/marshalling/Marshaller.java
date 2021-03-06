package com.farpost.logwatcher.marshalling;

import com.farpost.logwatcher.LogEntry;

/**
 * Имплементации данного интерфейса сериализуют и десериализуют обьекты типа {@link com.farpost.logwatcher.LogEntryImpl}
 * в формат wire протокола.
 * <p/>
 * Внимание, все имплементации этого интерфейса должны быть потокобезопасны.
 */
public interface Marshaller {

	byte[] marshall(LogEntry entry) throws MarshallerException;

	LogEntry unmarshall(byte[] data) throws MarshallerException;
}
