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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Armin Reichert
 * 
 * @param K type of keys for identifying vertices
 * @param V vertex class (subclass of {@link Vertex})
 */
public class Graph<K, V extends Vertex> {

	private final Map<K, V> vertexByKey = new HashMap<>();

	public void addVertex(K key, V vertex) {
		if (vertexByKey.containsKey(key)) {
			throw new IllegalArgumentException("Vertex with key '" + key + "' already exists.");
		}
		vertexByKey.put(key, vertex);
	}

	public Optional<V> vertex(K key) {
		return Optional.ofNullable(vertexByKey.get(key));
	}

	public Stream<V> vertices() {
		return vertexByKey.values().stream();
	}

	public Stream<Edge> edges() {
		return vertices().flatMap(Vertex::outgoingEdges);
	}

	public void addEdge(V either, V other, float cost) {
		addDirectedEdge(either, other, cost);
		addDirectedEdge(other, either, cost);
	}

	public void addDirectedEdge(V source, V target, float cost) {
		source.addEdge(target, cost);
	}
}