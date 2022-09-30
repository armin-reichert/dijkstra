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

import java.awt.event.ActionEvent;
import java.util.MissingResourceException;

import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.KeyStroke;
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
		var app = new RoutePlannerApp("saarland.txt");
		SwingUtilities.invokeLater(app::createAndShowUI);
	}

	private RoadMap map;
	private RoadMapPathFinder pathFinder;

	public RoutePlannerApp(String mapFile) {
		var resource = getClass().getResourceAsStream("/" + mapFile);
		if (resource == null) {
			throw new MissingResourceException("Could not read map from file '%s'".formatted(mapFile),
					RoutePlannerApp.class.getName(), mapFile);
		}
		map = RoadMapReader.readMap(resource);
		pathFinder = new RoadMapPathFinder();
	}

	private void createAndShowUI() {
		try {
			UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
		} catch (Exception e) {
			LOGGER.error("Could not set Nimbus look");
		}
		var window = new RoutePlannerWindow();
		var locationNames = map.locations().toArray(String[]::new);
		window.comboStart().setModel(new DefaultComboBoxModel<>(locationNames));
		window.comboGoal().setModel(new DefaultComboBoxModel<>(locationNames));
		window.listRoute().setModel(new DefaultListModel<>());
		window.setMap(map, pathFinder);
		window.comboStart().setSelectedItem("Losheim am See");
		window.comboGoal().setSelectedItem("St. Ingbert");

		window.listRoute().getActionMap().put("printAll", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pathFinder.printAllRoutes(map, LOGGER::info);
			}
		});
		window.listRoute().getInputMap().put(KeyStroke.getKeyStroke('p'), "printAll");
	}
}