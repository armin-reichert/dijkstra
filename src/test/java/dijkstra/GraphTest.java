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

import org.junit.Assert;
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
		Assert.assertEquals(0, g.numVertices());
		g.addVertex("A", new Vertex());
		Assert.assertEquals(1, g.numVertices());
		Assert.assertTrue(g.vertex("A").isPresent());
		Assert.assertFalse(g.vertex("a").isPresent());
		g.addVertex("a", new Vertex());
		Assert.assertTrue(g.vertex("a").isPresent());
		Assert.assertEquals(2, g.numVertices());
	}

	@Test
	public void testAddVertexWithNullID() {
		var v = new Vertex();
		Assert.assertThrows(NullPointerException.class, () -> g.addVertex(null, v));
	}

	@Test
	public void testAddNullVertex() {
		Assert.assertThrows(NullPointerException.class, () -> g.addVertex("id", null));
	}

	@Test
	public void testAddEdges() {
		var a = new Vertex();
		var b = new Vertex();
		var c = new Vertex();
		g.addVertex("A", a);
		g.addVertex("B", b);
		g.addVertex("C", c);
		Assert.assertEquals(0, g.numEdges());

		g.addDirectedEdge(a, b, 0);
		Assert.assertTrue(g.edge(a, b).isPresent());
		Assert.assertEquals(1, g.numEdges());

		g.addDirectedEdge(b, c, 0);
		Assert.assertTrue(g.edge(b, c).isPresent());
		Assert.assertEquals(2, g.numEdges());

		g.addDirectedEdge(c, a, 0);
		Assert.assertTrue(g.edge(c, a).isPresent());
		Assert.assertEquals(3, g.numEdges());

		Assert.assertFalse(g.edge(c, b).isPresent());
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