package com.farpost.logwatcher;

import org.testng.annotations.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;

public class QueryParserTest {

	QueryParser parser = new QueryParser();

	@Test
	public void parserCanParseSimpleQueries() throws InvalidQueryException {
		Map<String, String> result = parser.parse("occurred: 2 days ago at:frontend");

		assertThat(result, hasEntry("occurred", "2 days ago"));
		assertThat(result, hasEntry("at", "frontend"));
	}
}
