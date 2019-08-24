package net.bmahe.genetics4j.samples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TSPLIBParser {

	public TSPLIBParser() {
	}

	TSPLIBProblem parse(final String filename) throws IOException {

		final FileReader fileReader = new FileReader(filename, StandardCharsets.UTF_8);

		final Map<String, String> attributes = new HashMap<String, String>();
		final List<Position> cities = new ArrayList<Position>();

		try (final BufferedReader bufferReader = new BufferedReader(fileReader)) {

			boolean parsingHeaders = true;
			String line;
			while ((line = bufferReader.readLine()) != null) {
				System.out.println(line);

				if ("NODE_COORD_SECTION".equals(line)) {
					parsingHeaders = false;
				} else if ("EOF".equals(line)) {

				} else if (parsingHeaders) {
					final String[] strings = line.split(": ", 2);
					attributes.put(strings[0], strings[1]);
				} else {
					final String[] strings = line.split("\\s");
					cities.add(new Position(Double.parseDouble(strings[1]), Double.parseDouble(strings[2])));
				}

			}
		}
		return new TSPLIBProblem(attributes, cities);
	}
}