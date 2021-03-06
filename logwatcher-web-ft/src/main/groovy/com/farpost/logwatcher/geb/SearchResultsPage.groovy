package com.farpost.logwatcher.geb

import geb.Page

class SearchResultsPage extends Page {

	static at = { title.startsWith("LogWatcher Search:") }

	static content = {
		results { $("div.logEntry")*.text() }
		result { index -> results[index] }
	}

}
