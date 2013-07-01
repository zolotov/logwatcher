CREATE TABLE IF NOT EXISTS aggregated_entry (
	date DATE NOT NULL,
	checksum VARCHAR(255) NOT NULL,
	last_time TIMESTAMP NOT NULL,
	category VARCHAR(255) NULL,
	severity INT NOT NULL,
	application_id VARCHAR(255) NOT NULL,
	count INT NOT NULL,
	content TEXT NOT NULL,
	PRIMARY KEY(date, checksum)
);

CREATE TABLE IF NOT EXISTS entry (
	id INT NOT NULL,
	value BLOB NOT NULL,
	checksum VARCHAR(255) NOT NULL,
	date DATE NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS cluster_day_stat (
	application VARCHAR(255) NOT NULL,
	checksum VARCHAR(255) NOT NULL,
	date DATE NOT NULL,
	count INTEGER NOT NULL,
	PRIMARY KEY(application, checksum, date)
);

CREATE TABLE IF NOT EXISTS cluster_general_stat (
	application VARCHAR(255) NOT NULL,
	checksum VARCHAR(255) NOT NULL,
	last_seen DATETIME NOT NULL,
	first_seen DATETIME NOT NULL,
	PRIMARY KEY(application, checksum)
);