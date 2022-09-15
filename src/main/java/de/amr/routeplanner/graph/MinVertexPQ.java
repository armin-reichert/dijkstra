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
import java.util.PriorityQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Armin Reichert
 */
public class MinVertexPQ {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private PriorityQueue<Vertex> q;
	private Map<Vertex, Float> vertexCost;

	public MinVertexPQ() {
		q = new PriorityQueue<>((u, v) -> Float.compare(cost(u), cost(v)));
		vertexCost = new HashMap<>();
	}

	public float cost(Vertex location) {
		return vertexCost.getOrDefault(location, Float.POSITIVE_INFINITY);
	}

	public boolean isEmpty() {
		return q.isEmpty();
	}

	public Vertex extractMinCostVertex() {
		var min = q.poll();
		LOGGER.trace(() -> "Extract min: %s (cost=%.1f)".formatted(min, cost(min)));
		return min;
	}

	public void update(Vertex v, float cost) {
		remove(v);
		vertexCost.put(v, cost);
		q.add(v);
		LOGGER.trace(() -> "Added: %s (cost=%.1f)".formatted(v, cost(v)));
	}

	private void remove(Vertex v) {
		boolean removed = q.remove(v);
		if (removed) {
			LOGGER.trace(() -> "Removed: %s (cost=%.1f)".formatted(v, cost(v)));
		}
	}
}