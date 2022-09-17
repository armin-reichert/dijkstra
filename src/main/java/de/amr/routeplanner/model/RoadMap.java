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
import java.util.function.Consumer;
import java.util.stream.Stream;

import de.amr.routeplanner.graph.Edge;
import de.amr.routeplanner.graph.Graph;
import de.amr.routeplanner.graph.Vertex;

/**
 * @author Armin Reichert
 */
public class RoadMap extends Graph<String> {

	public static int orderByLocationName(RoadMapPoint u, RoadMapPoint v) {
		return u.location().name().compareTo(v.location().name());
	}

	public RoadMapPoint getOrCreatePoint(String key, float latitude, float longitude) {
		var v = vertex(key);
		if (v.isPresent()) {
			return (RoadMapPoint) v.get();
		}
		var p = new RoadMapPoint(key, latitude, longitude);
		addVertex(key, p);
		return p;
	}

	public void addConnection(RoadMapPoint either, RoadMapPoint other, float cost) {
		addEdge(either, other, cost);
	}

	public Stream<RoadMapPoint> points(Comparator<RoadMapPoint> order) {
		return vertices().map(RoadMapPoint.class::cast).sorted(order);
	}

	public Stream<RoadMapPoint> pointsOrderedByLocationName() {
		return points(RoadMap::orderByLocationName);
	}

	public Stream<String> pointNames() {
		return pointsOrderedByLocationName().map(RoadMapPoint::location).map(Location::name);
	}

	public void print(Consumer<String> destination, Comparator<RoadMapPoint> order) {
		points(order).map(RoadMapPoint::toString).forEach(destination::accept);
		points(order).flatMap(Vertex::outgoingEdges).map(this::formatEdge).forEach(destination::accept);
	}

	private String formatEdge(Edge edge) {
		return "[%s -> %s %.1f km]".formatted(((RoadMapPoint) edge.from()).location().name(),
				((RoadMapPoint) edge.to()).location().name(), edge.cost());
	}
}