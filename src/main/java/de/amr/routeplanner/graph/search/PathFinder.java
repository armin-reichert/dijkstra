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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
		return nodes.computeIfAbsent(v, SearchNode::new);
	}

	protected void onNewPathFound(Edge edge, float cost) {
		LOGGER.trace(() -> "Found new path of cost %f ending with edge %s".formatted(edge, cost));
	}

	/**
	 * Computes the shortest path from the given source vertex to all vertices of the graph using Dijkstra's algorithm.
	 * (In fact this is not the classic Dijkstra algorithm but more a Uniform-Cost search, see the cited paper by Ariel
	 * Felner.)
	 * 
	 * @see <a href="http://www-m3.ma.tum.de/foswiki/pub/MN0506/WebHome/dijkstra.pdf">Edsger Dijkstra: A Note on Two
	 *      Problems in Connexion with Graphs</a>
	 * @see <a href="https://cs.au.dk/~gerth/papers/fun22.pdf">Gerth Stolting Brodal: Priority Queues with Decreasing
	 *      Keys</a>
	 * @see <a href="https://www.aaai.org/ocs/index.php/SOCS/SOCS11/paper/viewFile/4017/4357">Ariel Felner: Position
	 *      Paper: Dijkstra’s Algorithm versus Uniform Cost Search or a Case Against Dijkstra’s Algorithm</a>
	 * 
	 * @param g      directed graph with non-negative edge weights
	 * @param source the source vertex
	 */
	@SuppressWarnings("unchecked")
	public void computeAllPaths(Graph<V> g, V source) {
		LOGGER.info(() -> "Compute shortest paths from %s using Dijkstra's algorithm".formatted(source));
		this.nodes = new HashMap<>();
		var open = new SearchNodeMinPQ<V>();
		node(source).cost = 0;
		open.insert(node(source)); // in Dijkstra algorithm, *all* nodes are added to the queue!
		while (!open.isEmpty()) {
			var u = open.extractMin();
			if (!u.visited) {
				u.vertex.outgoingEdges().forEach(edge -> {
					var v = node((V) edge.to());
					var altCost = u.cost + edge.cost();
					if (v.cost > altCost) {
						onNewPathFound(edge, altCost);
						v.cost = altCost;
						v.parent = u;
						open.remove(v); // if vertex not in queue, does nothing
						open.insert(v);
					}
				});
				u.visited = true; // add to CLOSED list
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