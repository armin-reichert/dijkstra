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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Armin Reichert
 * 
 * @see https://cs.au.dk/~gerth/papers/fun22.pdf
 */
public class RoutePlanner {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private final RoadMap map;
	private Map<RoadMapLocation, Float> cost;
	private Map<RoadMapLocation, RoadMapLocation> parent;
	private RoadMapLocation startLocation;

	public RoutePlanner(RoadMap map) {
		this.map = Objects.requireNonNull(map);
	}

	public float cost(RoadMapLocation location) {
		return cost.getOrDefault(location, Float.POSITIVE_INFINITY);
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
			LOGGER.info(() -> "Compute shortest paths starting at %s".formatted(startLocation));
			dijkstra();
		}
		return buildRoute(goal);
	}

	private List<RoadMapLocation> buildRoute(RoadMapLocation goal) {
		var route = new LinkedList<RoadMapLocation>();
		for (RoadMapLocation v = goal; v != null; v = parent.get(v)) {
			route.addFirst(v);
		}
		return route;
	}

	/**
	 * Computes the shortest path from the given start vertex to all vertices using the Dijkstra algorithm.
	 */
	private void dijkstra() {
		var q = new PriorityQueue<RoadMapLocation>((loc1, loc2) -> Float.compare(cost(loc1), cost(loc2)));
		parent = new HashMap<>();
		cost = new HashMap<>();
		cost.put(startLocation, 0.0f);
		q.add(startLocation);
		while (!q.isEmpty()) {
			var u = q.poll(); // extract min cost vertex from queue
			u.outgoingEdges().forEach(edge -> {
				var v = (RoadMapLocation) edge.to(); // edge = (u, v)
				var altCost = cost(u) + edge.cost();
				if (altCost < cost(v)) {
					LOGGER.trace("Shorter path to %s found: old cost=%.1f new cost=%.1f".formatted(v, cost(v), altCost));
					// "decrease key" is not supported by Java priority queue, use remove/add instead
					boolean removed = q.remove(v);
					if (removed) {
						LOGGER.trace("%s (cost=%.1f) removed from priority queue".formatted(v, cost(v)));
					}
					cost.put(v, altCost);
					parent.put(v, u);
					q.add(v);
					LOGGER.trace("%s (cost=%.1f) added to priority queue".formatted(v, cost(v)));
				}
			});
		}
	}
}