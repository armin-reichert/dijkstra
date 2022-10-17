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

package de.amr.routeplanner.graph.search;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.amr.routeplanner.graph.Edge;
import de.amr.routeplanner.graph.Graph;
import de.amr.routeplanner.graph.Vertex;

/**
 * @author Armin Reichert
 */
public class PathFinder<V extends Vertex> {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private Map<V, SearchNode<V>> nodes;
	private V currentSource;

	public SearchNode<V> node(V v) {
		return nodes.get(v);
	}

	protected void onNewPathFound(Edge edge, float cost) {
		LOGGER.trace(() -> "Found new path of cost %f ending with edge %s".formatted(edge, cost));
	}

	/**
	 * Computes the shortest path from the given source vertex to all vertices of the graph using Dijkstra's algorithm.
	 * <p>
	 * TODO: Not sure if using "visited" and if removing vertices from queue are really needed
	 * 
	 * @see https://cs.au.dk/~gerth/papers/fun22.pdf
	 * @see https://www.aaai.org/ocs/index.php/SOCS/SOCS11/paper/viewFile/4017/4357
	 * 
	 * @param g              a graph with non-negative edge weights
	 * @param sourceVertex   the source vertex
	 * @param onNewPathFound callback function, may be used for tracing
	 */
	@SuppressWarnings("unchecked")
	public void computeAllPaths(Graph<V> g, V sourceVertex) {
		LOGGER.info(() -> "Compute shortest paths from %s using Dijkstra's algorithm".formatted(sourceVertex));
		var pq = new SearchNodeMinPQ<V>();
		// create search nodes for all vertices
		nodes = g.vertices().collect(Collectors.toMap(Function.<V>identity(), SearchNode::new));
		var source = node(sourceVertex);
		source.cost = 0;
		pq.insert(source);
		while (!pq.isEmpty()) {
			var u = pq.extractMin();
			if (!u.visited) {
				u.visited = true;
				u.vertex.outgoingEdges().forEach(edge -> {
					var v = node((V) edge.to());
					var altCost = u.cost + edge.cost();
					if (v.cost > altCost) {
						onNewPathFound(edge, altCost);
						pq.remove(v); // if vertex not in queue, does nothing
						v.cost = altCost;
						v.parent = u;
						pq.insert(v);
					}
				});
			}
		}
	}

	public List<V> findPath(Graph<V> g, V source, V goal) {
		if (source == null || goal == null) {
			return List.of();
		}
		if (source != currentSource) {
			currentSource = source;
			computeAllPaths(g, currentSource);
		}
		var route = new LinkedList<V>();
		for (var node = node(goal); node != null; node = node.parent) {
			route.addFirst(node.vertex);
		}
		return route;
	}
}