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

import java.util.function.BiConsumer;

/**
 * @author Armin Reichert
 */
public interface Algorithms {
	/**
	 * Computes the shortest path from the given source vertex to all vertices of the graph.
	 * <p>
	 * TODO: Not sure if using "visited" and if removing vertices from queue are needed
	 * 
	 * @see https://cs.au.dk/~gerth/papers/fun22.pdf
	 * 
	 * @param g              a graph with non-negative edge weights
	 * @param source         the source vertex
	 * @param onNewPathFound callback function, may be used for tracing
	 */
	public static void computeShortestPathsFrom(Graph<?> g, Vertex source, BiConsumer<Edge, Float> onNewPathFound) {
		var q = new VertexPQ();
		g.vertices().forEach(v -> {
			v.setCost(Float.POSITIVE_INFINITY);
			v.setParent(null);
			v.setVisited(false);
		});
		source.setCost(0);
		q.insert(source);
		while (!q.isEmpty()) {
			var u = q.extractMin();
			if (!u.isVisited()) {
				u.setVisited(true);
				u.outgoingEdges().forEach(edge -> {
					var v = edge.to(); // edge = (u, v)
					var altCost = u.cost() + edge.cost(); // cost of path (source, ..., u, v)
					if (v.cost() > altCost) {
						onNewPathFound.accept(edge, altCost);
						q.remove(v); // if vertex not in queue, does nothing
						v.setCost(altCost);
						v.setParent(u);
						q.insert(v);
					}
				});
			}
		}
	}

	public static void computeShortestPathsFrom(Graph<?> g, Vertex source) {
		computeShortestPathsFrom(g, source, (edge, cost) -> {
		});
	}
}