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

import java.util.PriorityQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.amr.routeplanner.graph.Vertex;

/**
 * @author Armin Reichert
 */
public class SearchNodeMinPQ<V extends Vertex> {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	private PriorityQueue<SearchNode<V>> pq;

	public SearchNodeMinPQ() {
		pq = new PriorityQueue<>((u, v) -> Float.compare(u.cost, v.cost));
	}

	public boolean isEmpty() {
		return pq.isEmpty(); // constant time
	}

	public SearchNode<V> extractMin() {
		var min = pq.poll(); // log(n) time
		LOGGER.trace(() -> "Extract min: %s".formatted(min));
		return min;
	}

	public void insert(SearchNode<V> v) {
		pq.add(v); // log(n) time
		LOGGER.trace(() -> "Insert: %s".formatted(v));
	}

	public void remove(SearchNode<V> v) {
		boolean removed = pq.remove(v); // O(n) time
		if (removed) {
			LOGGER.trace(() -> "Remove: %s".formatted(v));
		}
	}
}