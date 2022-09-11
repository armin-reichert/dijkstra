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

package de.amr.schule.routeplanner.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.amr.schule.routeplanner.model.GeoCoord;
import de.amr.schule.routeplanner.model.RoadMap;
import de.amr.schule.routeplanner.model.RoadMapLocation;
import de.amr.schule.routeplanner.model.RoutePlanner;
import net.miginfocom.swing.MigLayout;

/**
 * @author Armin Reichert
 */
public class RoutePlannerWindow extends JFrame {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private static final float MAP_LATITUDE_MIN = 49.111948f; // bottom
	private static final float MAP_LATITUDE_MAX = 49.639407f; // top
	private static final float MAP_LONGITUDE_MIN = 6.356f; // left
	private static final float MAP_LONGITUDE_MAX = 7.402f; // right

	private static final Color COLOR_START = new Color(0, 255, 0, 100);
	private static final Color COLOR_GOAL = new Color(0, 0, 255, 100);

	private final Action actionComputeRoute = new AbstractAction("Route") {
		@Override
		public void actionPerformed(ActionEvent e) {
			String start = (String) comboStart().getSelectedItem();
			String goal = (String) comboGoal().getSelectedItem();
			var route = routePlanner.computeRoute(start, goal);
			var sections = route.stream()
					.map(location -> "%s %.1f km".formatted(location.name(), routePlanner.cost(location))).toList();
			var data = new DefaultListModel<String>();
			data.addAll(sections);
			listRoute().setModel(data);
			mapImage.repaint();
		}
	};

	private RoadMap map;
	private RoutePlanner routePlanner;
	private JComboBox<String> comboStart;
	private JComboBox<String> comboGoal;
	private JList<String> listRoute;
	private ImagePanel mapImage;
	private Point mousePosition;
	private boolean shiftPressed;

	public RoutePlannerWindow() {
		setTitle("Routenplaner Saarland");
		setResizable(false);
		setSize(1010, 670);
		setLocation(30, 30);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		getContentPane().setLayout(new MigLayout("", "[][grow]", "[][grow]"));

		JPanel panelStartGoal = new JPanel();
		getContentPane().add(panelStartGoal, "cell 0 0");
		panelStartGoal.setLayout(new MigLayout("", "[][]", "[][]"));

		JLabel lblStart = new JLabel("Start");
		panelStartGoal.add(lblStart, "cell 0 0,alignx left,aligny center");

		comboStart = new JComboBox<>();
		comboStart.setMaximumRowCount(20);
		comboStart.setModel(new DefaultComboBoxModel<>(new String[] { "Wadern" }));
		panelStartGoal.add(comboStart, "cell 1 0,growx,aligny center");
		comboStart.setAction(actionComputeRoute);

		JLabel lblGoal = new JLabel("Ziel");
		panelStartGoal.add(lblGoal, "cell 0 1,alignx left,aligny center");

		comboGoal = new JComboBox<>();
		comboGoal.setMaximumRowCount(20);
		comboGoal.setModel(new DefaultComboBoxModel<>(new String[] { "Neunkirchen" }));
		panelStartGoal.add(comboGoal, "cell 1 1,growx,aligny center");
		comboGoal.setAction(actionComputeRoute);

		mapImage = new ImagePanel();
		try {
			mapImage.setImage(ImageIO.read(getClass().getResourceAsStream("/saarlandkarte.jpg")));
		} catch (Exception x) {
			x.printStackTrace();
		}
		mapImage.setBackground(new Color(255, 255, 255));
		getContentPane().add(mapImage, "cell 1 0 1 2,grow");
		mapImage.setLayout(new MigLayout("", "[]", "[]"));
		mapImage.setOnMouseClicked(this::onMouseClicked);
		mapImage.setOnMouseMoved(this::onMouseMoved);
		mapImage.setOnKeyPressed(e -> {
			shiftPressed = e.isShiftDown();
			mapImage.repaint();
		});
		mapImage.setOnKeyReleased(e -> {
			shiftPressed = e.isShiftDown();
			mapImage.repaint();
		});

		listRoute = new JList<>();
		listRoute.setVisibleRowCount(20);
		listRoute.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getContentPane().add(listRoute, "cell 0 1,grow");
		listRoute.setModel(new AbstractListModel<>() {
			String[] values = new String[] { "Wadern 0.0 km", "Schmelz 18.0 km", "Eppelborn 31.0 km", "Neunkirchen 52.5 km" };

			@Override
			public int getSize() {
				return values.length;
			}

			@Override
			public String getElementAt(int index) {
				return values[index];
			}
		});
	}

	public void setMap(RoadMap map) {
		this.map = Objects.requireNonNull(map);
		this.routePlanner = new RoutePlanner(map);
		var locationNames = map.locationNames().toList();
		if (!locationNames.isEmpty()) {
			comboStart().setSelectedItem(locationNames.get(0));
			comboGoal().setSelectedItem(locationNames.get(locationNames.size() - 1));
		}
		mapImage.setOnRepaint(this::onRepaint);
	}

	public JComboBox<String> comboStart() {
		return comboStart;
	}

	public JComboBox<String> comboGoal() {
		return comboGoal;
	}

	public JList<String> listRoute() {
		return listRoute;
	}

	private void onMouseMoved(MouseEvent e) {
		mapImage.requestFocus();
		mousePosition = e.getPoint();
		mapImage.repaint();
	}

	private void onMouseClicked(MouseEvent e) {
		var coord = getCoordAtPosition(e.getX(), e.getY());
		var nearest = getNearestLocation(coord, 50);
		if (nearest != null) {
			if (e.isShiftDown()) {
				comboGoal().setSelectedItem(nearest.name());
			} else {
				comboStart().setSelectedItem(nearest.name());
			}
		}
		mousePosition = e.getPoint();
		mapImage.requestFocus();
		mapImage.repaint();
	}

	private void onRepaint(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		drawRoads(g);
		drawRoute(g);
		drawStartAndGoalLocations(g);
		drawMouseCoord(g);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	private void drawMouseCoord(Graphics2D g) {
		if (mousePosition != null) {
			GeoCoord coord = getCoordAtPosition(mousePosition.x, mousePosition.y);
			g.setColor(Color.BLACK);
			g.setFont(new Font("Sans", Font.PLAIN, 10));
			g.drawString("%.3f %.3f".formatted(coord.latitude(), coord.longitude()), mousePosition.x, mousePosition.y);
		}
	}

	public void drawStartAndGoalLocations(Graphics2D g) {
		RoadMapLocation nearestLocation = null;
		if (mousePosition != null) {
			GeoCoord coord = getCoordAtPosition(mousePosition.x, mousePosition.y);
			nearestLocation = getNearestLocation(coord, 50);
		}
		for (var location : map.locations().toList()) {
			Point p = getPointAtCoord(location.coord());
			if (location.name().equals(comboStart().getSelectedItem())) {
				circle(g, p, COLOR_START, 6);
			} else if (location.name().equals(comboGoal().getSelectedItem())) {
				circle(g, p, COLOR_GOAL, 6);
			} else if (location == nearestLocation) {
				circle(g, p, shiftPressed ? COLOR_GOAL : COLOR_START, 8);
			} else {
				circle(g, p, Color.BLACK, 3);
			}
		}
	}

	public void drawRoute(Graphics2D g) {
		String start = (String) comboStart().getSelectedItem();
		String goal = (String) comboGoal().getSelectedItem();
		var route = routePlanner.computeRoute(start, goal);
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(1f));
		for (int i = 0; i < route.size(); ++i) {
			var p = getPointAtCoord(route.get(i).coord());
			if (i > 0) {
				var q = getPointAtCoord(route.get(i - 1).coord());
				g.drawLine(p.x, p.y, q.x, q.y);
			}
		}
	}

	private void drawRoads(Graphics2D g) {
		g.setColor(Color.DARK_GRAY);
		g.setStroke(new BasicStroke(0.1f));
		map.edges().forEach(road -> {
			Point from = getPointAtCoord(((RoadMapLocation) road.from()).coord());
			Point to = getPointAtCoord(((RoadMapLocation) road.to()).coord());
			g.drawLine(from.x, from.y, to.x, to.y);
		});
	}

	private Point getPointAtCoord(GeoCoord coord) {
		float tx = (coord.longitude() - MAP_LONGITUDE_MIN) / (MAP_LONGITUDE_MAX - MAP_LONGITUDE_MIN);
		float ty = (coord.latitude() - MAP_LATITUDE_MIN) / (MAP_LATITUDE_MAX - MAP_LATITUDE_MIN);
		return new Point((int) (tx * mapImage.getWidth()), (int) ((1 - ty) * mapImage.getHeight()));
	}

	private GeoCoord getCoordAtPosition(int x, int y) {
		int width = mapImage.getWidth();
		int height = mapImage.getHeight();
		float tx = (float) x / width;
		float ty = (float) y / height;
		float longitude = MAP_LONGITUDE_MIN + tx * (MAP_LONGITUDE_MAX - MAP_LONGITUDE_MIN);
		float latitude = MAP_LATITUDE_MAX + ty * (MAP_LATITUDE_MIN - MAP_LATITUDE_MAX);
		return new GeoCoord(latitude, longitude);
	}

	private RoadMapLocation getNearestLocation(GeoCoord coord, double range) {
		double minDist = Double.POSITIVE_INFINITY;
		RoadMapLocation nearest = null;
		for (var location : map.locations().toList()) {
			double dist = distance(coord, location.coord());
			if (dist < minDist) {
				nearest = location;
				minDist = dist;
			}
		}
		return minDist < range ? nearest : null;
	}

	private double distance(GeoCoord c1, GeoCoord c2) {
		Point p1 = getPointAtCoord(c1);
		Point p2 = getPointAtCoord(c2);
		return Math.hypot(p1.x - p2.x, p1.y - p2.y);
	}

	private void circle(Graphics2D g, Point p, Color color, int radius) {
		g.setColor(color);
		g.fillOval(p.x - radius, p.y - radius, 2 * radius, 2 * radius);
	}
}