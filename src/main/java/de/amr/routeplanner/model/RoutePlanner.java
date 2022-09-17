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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.amr.routeplanner.graph.MinVertexPQ;

/**
 * @author Armin Reichert
 * 
 * @see https://cs.au.dk/~gerth/papers/fun22.pdf
 */
public class RoutePlanner {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private final RoadMap map;
	private MinVertexPQ<RoadMapPoint> q;
	private RoadMapPoint source;

	public RoutePlanner(RoadMap map) {
		this.map = Objects.requireNonNull(map);
		q = new MinVertexPQ<>();
	}

	public List<RoadMapPoint> computeRoute(String sourceName, String goalName) {
		return computeRoute(map.vertex(sourceName).orElse(null), map.vertex(goalName).orElse(null));
	}

	public List<RoadMapPoint> computeRoute(RoadMapPoint source, RoadMapPoint goal) {
		if (source == null || goal == null) {
			return List.of();
		}
		if (source != this.source) {
			this.source = source;
			dijkstra();
		}
		return buildRoute(goal);
	}

	/**
	 * Computes the shortest path from the current source to all locations of the map.
	 * 
	 * TODO: I am not sure if the "visited" set is really needed
	 */
	private void dijkstra() {
		LOGGER.info(() -> "*** Compute all shortest paths from %s using Dijkstra's algorithm".formatted(source));
		var visited = new HashSet<RoadMapPoint>();
		map.vertices().forEach(v -> {
			v.setCost(Float.POSITIVE_INFINITY);
			v.setParent(null);
		});
		q = new MinVertexPQ<>();
		q.update(source, 0);
		while (!q.isEmpty()) {
			var u = q.extractMinCostVertex();
			if (!visited.contains(u)) {
				visited.add(u);
				u.outgoingEdges().forEach(edge -> {
					var v = (RoadMapPoint) edge.to(); // edge = (u, v)
					var altCost = u.getCost() + edge.cost(); // cost of path (source, ..., u, v)
					if (v.getCost() > altCost) {
						tracePathUpdated(u, v, v.getCost(), altCost);
						q.update(v, altCost);
						v.setParent(u);
					}
				});
			}
		}
	}

	private List<RoadMapPoint> buildRoute(RoadMapPoint goal) {
		var route = new LinkedList<RoadMapPoint>();
		for (RoadMapPoint v = goal; v != null; v = (RoadMapPoint) v.getParent()) {
			route.addFirst(v);
		}
		return route;
	}

	private void tracePathUpdated(RoadMapPoint u, RoadMapPoint v, float oldCost, float newCost) {
		if (oldCost == Float.POSITIVE_INFINITY) {
			LOGGER.trace(() -> "Found path to %s (%.1f km) via %s".formatted(v, newCost, u));
		} else {
			LOGGER.trace(() -> "Found shorter path to %s (%.1f km instead of %.1f km) via %s instead via %s".formatted(v,
					newCost, oldCost, u, v.getParent()));
		}
	}
}