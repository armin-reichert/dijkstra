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

package de.amr.schule.routeplanner.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Armin Reichert
 */
public class Graph {

	private final Map<Object, Vertex> vertexByKey = new HashMap<>();

	public void addVertex(Object key, Vertex vertex) {
		if (vertexByKey.containsKey(key)) {
			throw new IllegalArgumentException("Vertex with key '" + key + "' already exists.");
		}
		vertexByKey.put(key, vertex);
	}

	public Optional<Vertex> vertex(Object key) {
		return Optional.ofNullable(vertexByKey.get(key));
	}

	public Stream<Vertex> vertices() {
		return vertexByKey.values().stream();
	}

	public Stream<Edge> edges() {
		return vertices().flatMap(Vertex::outgoingEdges);
	}

	public void addEdge(Vertex either, Vertex other, float cost) {
		addDirectedEdge(either, other, cost);
		addDirectedEdge(other, either, cost);
	}

	public void addDirectedEdge(Vertex source, Vertex target, float cost) {
		source.addEdge(target, cost);
	}
}