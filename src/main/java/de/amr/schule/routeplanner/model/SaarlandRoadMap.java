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

package de.amr.schule.routeplanner.model;

/**
 * @author Armin Reichert
 */
public class SaarlandRoadMap extends RoadMap {

	public SaarlandRoadMap() {
		var los = getOrCreateLocation("Losheim am See", 49.51f, 6.75f);
		var wad = getOrCreateLocation("Wadern", 49.534f, 6.89f);
		var mzg = getOrCreateLocation("Merzig", 49.449f, 6.643f);
		var bec = getOrCreateLocation("Beckingen", 49.391f, 6.705f);
		var dil = getOrCreateLocation("Dillingen", 49.353f, 6.714f);
		var slz = getOrCreateLocation("Schmelz", 49.432f, 6.843f);
		var leb = getOrCreateLocation("Lebach", 49.41f, 6.91f);
		var nal = getOrCreateLocation("Nalbach", 49.378f, 6.781f);
		var heu = getOrCreateLocation("Heusweiler", 49.338f, 6.929f);
		var sls = getOrCreateLocation("Saarlouis", 49.313f, 6.752f);
		var vlk = getOrCreateLocation("Völklingen", 49.255f, 6.859f);
		var sbr = getOrCreateLocation("Saarbrücken", 49.238f, 6.997f);
		var wnd = getOrCreateLocation("St. Wendel", 49.468f, 7.167f);
		var nkr = getOrCreateLocation("Neunkirchen", 49.349f, 7.177f);
		var epp = getOrCreateLocation("Eppelborn", 49.409f, 6.964f);
		var hom = getOrCreateLocation("Homburg", 49.329f, 7.339f);
		var igb = getOrCreateLocation("St. Ingbert", 49.278f, 7.112f);
		var tho = getOrCreateLocation("Tholey", 49.482f, 7.032f);

		addRoad(bec, dil, 3.5f);
		addRoad(epp, nkr, 21.5f);
		addRoad(epp, sbr, 31.0f);
		addRoad(epp, heu, 10.0f);
		addRoad(dil, nal, 6.0f);
		addRoad(dil, sls, 9.0f);
		addRoad(heu, vlk, 12.0f);
		addRoad(heu, sbr, 14.5f);
		addRoad(heu, sls, 20.5f);
		addRoad(heu, nkr, 24.0f);
		addRoad(igb, hom, 22.0f);
		addRoad(igb, nkr, 10.5f);
		addRoad(leb, epp, 8.0f);
		addRoad(leb, nal, 11.0f);
		addRoad(leb, tho, 17f);
		addRoad(leb, heu, 9.5f);
		addRoad(los, mzg, 13.2f);
		addRoad(los, wad, 15.0f);
		addRoad(los, slz, 17.0f);
		addRoad(mzg, bec, 12.5f);
		addRoad(nkr, hom, 19.0f);
		addRoad(sbr, igb, 11.0f);
		addRoad(slz, nal, 10.5f);
		addRoad(slz, wad, 18.0f);
		addRoad(slz, leb, 5f);
		addRoad(sls, nal, 11.0f);
		addRoad(sls, vlk, 14.0f);
		addRoad(vlk, sbr, 12.5f);
		addRoad(tho, wnd, 13.0f);
		addRoad(wad, tho, 19.5f);
		addRoad(wnd, nkr, 19.0f);
		addRoad(wnd, hom, 29.0f);
	}
}