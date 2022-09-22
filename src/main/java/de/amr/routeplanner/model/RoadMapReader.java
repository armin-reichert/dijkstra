/*
MIT License

Copyright (c) 2022 Armin Reichert

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package de.amr.routeplanner.model;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Armin Reichert
 */
public class RoadMapReader {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private static final int STATE_READ = 0;
	private static final int STATE_READ_LOCATIONS = 1;
	private static final int STATE_READ_ROADS = 2;

	private static String[] splitAndTrimCSV(String line) {
		return Stream.of(line.split(",")).map(String::trim).toArray(String[]::new);
	}

	public static RoadMap readMap(InputStream is) {
		return new RoadMapReader().read(is);
	}

	private int state = STATE_READ;
	private int lineNumber;

	private RoadMap read(InputStream is) {
		lineNumber = 0;
		var map = new RoadMap();
		try (var rdr = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
			rdr.lines().forEach(line -> {
				++lineNumber;
				if (line.startsWith("#") || line.isBlank()) {
					// skip line
				} else {
					processLine(line.trim(), map);
				}
			});
		} catch (Exception x) {
			x.printStackTrace();
		}
		return map;
	}

	private void processLine(String line, RoadMap map) {
		if (".locations".equals(line)) {
			state = STATE_READ_LOCATIONS;
		} else if (".roads".equals(line)) {
			state = STATE_READ_ROADS;
		} else if (state == STATE_READ_LOCATIONS) {
			parseLocation(line, map);
		} else if (state == STATE_READ_ROADS) {
			parseRoad(line, map);
		}
	}

	private void parseLocation(String line, RoadMap map) {
		// <key> <location name> <latitude> <longitude>
		String[] tokens = splitAndTrimCSV(line);
		if (tokens.length != 4) {
			LOGGER.error(() -> "Line %d: '%s': Invalid location spec".formatted(lineNumber, line));
			return;
		}
		String key = tokens[0];
		String name = tokens[1];
		float latitude;
		try {
			latitude = Float.parseFloat(tokens[2]);
		} catch (NumberFormatException x) {
			LOGGER.error("Line %d: '%s': Invalid latitude: '%s'".formatted(lineNumber, line, tokens[2]));
			return;
		}
		float longitude = Float.parseFloat(tokens[3]);
		try {
			longitude = Float.parseFloat(tokens[3]);
		} catch (NumberFormatException x) {
			LOGGER.error("Line %d: '%s': Invalid longitude: '%s'".formatted(lineNumber, line, tokens[3]));
			return;
		}
		map.createAndAddPoint(key, name, latitude, longitude);
	}

	private void parseRoad(String line, RoadMap map) {
		// <from> <to> <cost>
		String[] tokens = splitAndTrimCSV(line);
		if (tokens.length != 3) {
			LOGGER.error(() -> "Line %d: '%s': Invalid road spec".formatted(lineNumber, line));
			return;
		}
		var from = map.vertex(tokens[0]);
		if (from.isEmpty()) {
			LOGGER.error(() -> "Line %d: '%s': Undefined road start point: '%s'".formatted(lineNumber, line, tokens[0]));
			return;
		}
		var to = map.vertex(tokens[1]);
		if (to.isEmpty()) {
			LOGGER.error(() -> "Line %d: '%s': Undefined road end point: '%s'".formatted(lineNumber, line, tokens[1]));
			return;
		}
		float dist;
		try {
			dist = Float.parseFloat(tokens[2]);
		} catch (NumberFormatException x) {
			LOGGER.error("Line %d: '%s': Invalid distance: '%s'".formatted(lineNumber, line, tokens[2]));
			return;
		}
		map.addEdge(from.get(), to.get(), dist);
	}
}