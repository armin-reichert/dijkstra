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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Armin Reichert
 */
public class Vertex {

	private List<Edge> adjEdges;
	private final String id;
	private Vertex parent;
	private float cost;
	private boolean visited;

	public Vertex(String id) {
		this.id = Objects.requireNonNull(id);
	}

	public String id() {
		return id;
	}

	public Vertex parent() {
		return parent;
	}

	public void setParent(Vertex parent) {
		this.parent = parent;
	}

	public float cost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public void addOutgoingEdge(Vertex to, float cost) {
		if (adjEdges == null) {
			adjEdges = new ArrayList<>(3);
		}
		adjEdges.add(new Edge(this, to, cost));
	}

	public Stream<Edge> outgoingEdges() {
		return adjEdges == null ? Stream.empty() : adjEdges.stream();
	}
}