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
	private MinVertexPQ<RoadMapLocation> q;
	private RoadMapLocation startLocation;

	public RoutePlanner(RoadMap map) {
		this.map = Objects.requireNonNull(map);
		q = new MinVertexPQ<>();
	}

	public float cost(RoadMapLocation loc) {
		return q.cost(loc);
	}

	public List<RoadMapLocation> computeRoute(String startName, String goalName) {
		var start = (RoadMapLocation) map.vertex(startName).orElse(null);
		var goal = (RoadMapLocation) map.vertex(goalName).orElse(null);
		return computeRoute(start, goal);
	}

	public List<RoadMapLocation> computeRoute(RoadMapLocation start, RoadMapLocation goal) {
		if (start == null || goal == null) {
			return List.of();
		}
		if (start != startLocation) {
			startLocation = start;
			LOGGER.info(() -> "*** Compute shortest paths starting at %s".formatted(startLocation));
			dijkstra();
		}
		return buildRoute(goal);
	}

	private List<RoadMapLocation> buildRoute(RoadMapLocation goal) {
		var route = new LinkedList<RoadMapLocation>();
		for (RoadMapLocation v = goal; v != null; v = (RoadMapLocation) v.getParent()) {
			route.addFirst(v);
		}
		return route;
	}

	/**
	 * Computes the shortest path from the given start vertex to all vertices using the Dijkstra algorithm.
	 * 
	 * TODO: not sure if visited set is really needed
	 */
	private void dijkstra() {
		var visited = new HashSet<RoadMapLocation>();
		map.vertices().forEach(v -> v.setParent(null));
		q = new MinVertexPQ<>();
		q.update(startLocation, 0);
		while (!q.isEmpty()) {
			var u = q.extractMinCostVertex();
			if (!visited.contains(u)) {
				visited.add(u);
				u.outgoingEdges().forEach(edge -> {
					var v = (RoadMapLocation) edge.to(); // edge = (u, v)
					var altCost = cost(u) + edge.cost();
					if (altCost < cost(v)) {
						onPathUpdated(u, v, cost(v), altCost);
						q.update(v, altCost);
						v.setParent(u);
					}
				});
			}
		}
	}

	private void onPathUpdated(RoadMapLocation u, RoadMapLocation v, float oldCost, float newCost) {
		if (oldCost == Float.POSITIVE_INFINITY) {
			LOGGER.trace(() -> "Found path to %s (%.1f km) via %s".formatted(v, newCost, u));
		} else {
			LOGGER.trace(() -> "Found shorter path to %s (%.1f km instead of %.1f km) via %s instead via %s".formatted(v,
					newCost, oldCost, u, v.getParent()));
		}
	}
}