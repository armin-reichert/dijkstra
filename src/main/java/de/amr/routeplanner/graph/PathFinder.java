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

package de.amr.routeplanner.graph;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Armin Reichert
 */
public class PathFinder {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private PathFinder() {
	}

	/**
	 * Computes the shortest path from the given source vertex to all vertices of the graph.
	 * <p>
	 * TODO: I am not sure if using "visited" is really needed
	 * 
	 * @see https://cs.au.dk/~gerth/papers/fun22.pdf
	 * 
	 * @param g      the graph
	 * @param source the source vertex
	 */
	public static void dijkstra(Graph<?, ?> g, Vertex source) {
		LOGGER.info(() -> "*** Compute shortest paths from %s using Dijkstra's algorithm".formatted(source));
		g.vertices().forEach(v -> {
			v.setCost(Float.POSITIVE_INFINITY);
			v.setParent(null);
			v.setVisited(false);
		});
		var q = new MinVertexPQ();
		q.update(source, 0);
		while (!q.isEmpty()) {
			var u = q.extractMinCostVertex();
			if (!u.isVisited()) {
				u.setVisited(true);
				u.outgoingEdges().forEach(edge -> {
					var v = edge.to(); // edge = (u, v)
					var altCost = u.getCost() + edge.cost(); // cost of path (source, ..., u, v)
					if (v.getCost() > altCost) {
						traceNewPathFound(u, v, v.getCost(), altCost);
						q.update(v, altCost);
						v.setParent(u);
					}
				});
			}
		}
	}

	private static void traceNewPathFound(Vertex u, Vertex v, float oldCost, float newCost) {
		if (oldCost == Float.POSITIVE_INFINITY) {
			LOGGER.trace(() -> "Found path to %s (%.1f km) via %s".formatted(v, newCost, u));
		} else {
			LOGGER.trace(() -> "Found shorter path to %s (%.1f km instead of %.1f km) via %s instead via %s".formatted(v,
					newCost, oldCost, u, v.getParent()));
		}
	}
}