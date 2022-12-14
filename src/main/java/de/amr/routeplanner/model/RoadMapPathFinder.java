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

package de.amr.routeplanner.model;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.amr.routeplanner.graph.Edge;
import de.amr.routeplanner.graph.search.PathFinder;

/**
 * @author Armin Reichert
 */
public class RoadMapPathFinder extends PathFinder<RoadMapPoint> {

	private static final Logger LOGGER = LogManager.getFormatterLogger();

	@Override
	protected void onNewPathFound(Edge edge, float newCost) {
		var u = (RoadMapPoint) edge.from();
		var v = (RoadMapPoint) edge.to();
		if (node(v).cost == Float.POSITIVE_INFINITY) {
			LOGGER.trace(() -> "First path to %s (%.1f km) via %s".formatted(v.locationName(), newCost, u.locationName()));
		} else {
			LOGGER.trace(() -> "Shorter path to %s (%.1f km via %s instead of %.1f km via %s)".formatted(v.locationName(),
					newCost, u.locationName(), node(v).cost, node(v).parent.vertex.locationName()));
		}
	}

	public List<RoadMapPoint> computeRoute(RoadMap map, String sourceLocation, String goalLocation) {
		return findPath(map, map.point(sourceLocation).orElse(null), map.point(goalLocation).orElse(null));
	}

}