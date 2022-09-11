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

package de.amr.schule.routeplanner.model;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import de.amr.schule.routeplanner.graph.Graph;
import de.amr.schule.routeplanner.graph.Vertex;

/**
 * @author Armin Reichert
 */
public class RoadMap extends Graph {

	public static int orderByName(RoadMapLocation v1, RoadMapLocation v2) {
		return v1.name().compareTo(v2.name());
	}

	public RoadMapLocation getOrCreateLocation(String name, float latitude, float longitude) {
		var location = vertex(name);
		if (location.isPresent()) {
			return (RoadMapLocation) location.get();
		}
		var newLocation = new RoadMapLocation(name, latitude, longitude);
		addVertex(name, newLocation);
		return newLocation;
	}

	public void addRoad(RoadMapLocation either, RoadMapLocation other, float cost) {
		addEdge(either, other, cost);
	}

	public Stream<RoadMapLocation> locations(Comparator<RoadMapLocation> order) {
		return vertices().map(RoadMapLocation.class::cast).sorted(order);
	}

	public Stream<RoadMapLocation> locations() {
		return locations(RoadMap::orderByName);
	}

	public Stream<String> locationNames() {
		return locations().map(RoadMapLocation::name);
	}

	public void print(Consumer<String> destination, Comparator<RoadMapLocation> order) {
		locations(order).map(RoadMapLocation::toString).forEach(destination::accept);
		locations(order)
				.flatMap(Vertex::outgoingEdges).map(edge -> "Edge[%s -> %s %.1f km]"
						.formatted(((RoadMapLocation) edge.from()).name(), ((RoadMapLocation) edge.to()).name(), edge.cost()))
				.forEach(destination::accept);
	}
}