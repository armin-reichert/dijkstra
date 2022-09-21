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

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

import de.amr.routeplanner.graph.Edge;
import de.amr.routeplanner.graph.Graph;
import de.amr.routeplanner.graph.Vertex;

/**
 * @author Armin Reichert
 */
public class RoadMap extends Graph<RoadMapPoint> {

	public static int orderedByLocationName(RoadMapPoint u, RoadMapPoint v) {
		return u.locationName().compareTo(v.locationName());
	}

	private RoadMapPoint source;

	public List<RoadMapPoint> computeRoute(String sourceLocationName, String goalLocationName) {
		var sourcePoint = findPoint(sourceLocationName);
		var goalPoint = findPoint(goalLocationName);
		return computeRoute(sourcePoint, goalPoint);
	}

	private RoadMapPoint findPoint(String locationName) {
		return pointsOrderedByLocationName().filter(p -> p.locationName().equals(locationName)).findFirst().orElse(null);
	}

	public List<RoadMapPoint> computeRoute(RoadMapPoint sourcePoint, RoadMapPoint goalPoint) {
		if (sourcePoint == null || goalPoint == null) {
			return List.of();
		}
		if (sourcePoint != this.source) {
			this.source = sourcePoint;
			computeShortestPathsFrom(sourcePoint);
		}
		var route = new LinkedList<RoadMapPoint>();
		for (RoadMapPoint v = goalPoint; v != null; v = (RoadMapPoint) v.getParent()) {
			route.addFirst(v);
		}
		return route;
	}

	public RoadMapPoint createAndAddPoint(String key, String locationName, float latitude, float longitude) {
		var point = new RoadMapPoint(Objects.requireNonNull(key), locationName, latitude, longitude);
		addVertex(point);
		return point;
	}

	public Stream<RoadMapPoint> points(Comparator<RoadMapPoint> order) {
		return vertices().map(RoadMapPoint.class::cast).sorted(order);
	}

	public Stream<RoadMapPoint> pointsOrderedByLocationName() {
		return points(RoadMap::orderedByLocationName);
	}

	public Stream<String> locationNames() {
		return pointsOrderedByLocationName().map(RoadMapPoint::locationName);
	}

	public void print(Consumer<String> destination, Comparator<RoadMapPoint> order) {
		points(order).map(RoadMapPoint::toString).forEach(destination::accept);
		points(order).flatMap(Vertex::outgoingEdges).map(this::formatEdge).forEach(destination::accept);
	}

	private String formatEdge(Edge edge) {
		return "[%s -> %s %.1f km]".formatted(((RoadMapPoint) edge.from()).locationName(),
				((RoadMapPoint) edge.to()).locationName(), edge.cost());
	}
}