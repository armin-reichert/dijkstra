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

import de.amr.routeplanner.graph.Vertex;

/**
 * @author Armin Reichert
 */
public class RoadMapPoint extends Vertex {

	private final String locationName;
	private final GeoCoord coord;

	public RoadMapPoint(String locationName, float latitude, float longitude) {
		this.locationName = locationName;
		this.coord = new GeoCoord(latitude, longitude);
	}

	public String locationName() {
		return locationName;
	}

	public GeoCoord coord() {
		return coord;
	}

	@Override
	public String toString() {
		return "[%s %.3f %.3f]".formatted(locationName, coord.latitude(), coord.longitude());
	}
}