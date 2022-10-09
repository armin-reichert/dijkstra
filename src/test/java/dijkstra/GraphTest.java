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

package dijkstra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.amr.routeplanner.graph.Graph;
import de.amr.routeplanner.graph.Vertex;

/**
 * @author Armin Reichert
 */
public class GraphTest {

	private Graph<Vertex> g;

	@Before
	public void setup() {
		g = new Graph<>();
	}

	@Test
	public void testAddVertices() {
		assertEquals(0, g.numVertices());
		g.addVertex("A", new Vertex());
		assertEquals(1, g.numVertices());
		assertTrue(g.vertex("A").isPresent());
		assertFalse(g.vertex("a").isPresent());
		g.addVertex("a", new Vertex());
		assertTrue(g.vertex("a").isPresent());
		assertEquals(2, g.numVertices());
	}

	@Test
	public void testAddVertexWithNullID() {
		var v = new Vertex();
		assertThrows(NullPointerException.class, () -> g.addVertex(null, v));
	}

	@Test
	public void testAddNullVertex() {
		assertThrows(NullPointerException.class, () -> g.addVertex("id", null));
	}

	@Test
	public void testAddEdges() {
		var a = new Vertex();
		var b = new Vertex();
		var c = new Vertex();
		g.addVertex("A", a);
		g.addVertex("B", b);
		g.addVertex("C", c);
		assertEquals(0, g.numEdges());

		g.addDirectedEdge(a, b, 0);
		assertTrue(g.edge(a, b).isPresent());
		assertEquals(1, g.numEdges());

		g.addDirectedEdge(b, c, 0);
		assertTrue(g.edge(b, c).isPresent());
		assertEquals(2, g.numEdges());

		g.addDirectedEdge(c, a, 0);
		assertTrue(g.edge(c, a).isPresent());
		assertEquals(3, g.numEdges());

		assertFalse(g.edge(c, b).isPresent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddDuplicateVertex() {
		var a = new Vertex();
		g.addVertex("A", a);
		g.addVertex("A", a);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddDuplicateEdge() {
		var a = new Vertex();
		var b = new Vertex();
		g.addVertex("A", a);
		g.addVertex("B", b);
		g.addDirectedEdge(a, b, 0);
		g.addDirectedEdge(a, b, 0);
	}
}