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

package de.amr.routeplanner;

import java.util.MissingResourceException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.amr.routeplanner.model.RoadMap;
import de.amr.routeplanner.model.RoadMapPathFinder;
import de.amr.routeplanner.model.RoadMapReader;
import de.amr.routeplanner.ui.RoutePlannerWindow;

/**
 * @author Armin Reichert
 */
public class RoutePlannerApp {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	public static void main(String[] args) {
		var app = new RoutePlannerApp();
		SwingUtilities.invokeLater(app::createAndShowUI);
	}

	private final RoadMap map;
	private final RoadMapPathFinder pathFinder;

	public RoutePlannerApp() {
		map = loadMapFile("saarland.txt");
		pathFinder = new RoadMapPathFinder();
	}

	private RoadMap loadMapFile(String fileName) {
		var res = getClass().getResourceAsStream("/" + fileName);
		if (res == null) {
			throw new MissingResourceException("Could not read map from file '%s'".formatted(fileName),
					RoutePlannerApp.class.getName(), fileName);
		}
		return RoadMapReader.readMap(res);
	}

	private void createAndShowUI() {
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
		} catch (Exception e) {
			LOGGER.error("Could not set Nimbus look");
		}
		var window = new RoutePlannerWindow();
		window.init(map, pathFinder);
	}
}