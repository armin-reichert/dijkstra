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

import java.util.PriorityQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Armin Reichert
 */
public class MinVertexPQ {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private PriorityQueue<Vertex> pq;

	public MinVertexPQ() {
		pq = new PriorityQueue<>((u, v) -> Float.compare(u.cost(), v.cost()));
	}

	public boolean isEmpty() {
		return pq.isEmpty();
	}

	public Vertex extractMinCostVertex() {
		var min = pq.poll();
		LOGGER.trace(() -> "Extract min: %s (cost=%.1f)".formatted(min, min.cost()));
		return min;
	}

	public void update(Vertex v, float cost) {
		boolean removed = pq.remove(v);
		if (removed) {
			LOGGER.trace(() -> "Remove: %s (cost=%.1f)".formatted(v, v.cost()));
		}
		v.setCost(cost);
		pq.add(v);
		LOGGER.trace(() -> "Add: %s (cost=%.1f)".formatted(v, v.cost()));
	}
}