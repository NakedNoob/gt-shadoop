package org.geotools.data.shadoop;

public class CoordUtil {

	/**
	 * Takes in a filter string from Geoserver and builds the BBOX for Shadoop's rangequery
	 * element 0 is X1
	 * element 1 is Y1
	 * element 2 is the width
	 * element 3 is the height
	 * element 4 is a check to see if the bounds are correct (no negative numbers); 0 for invalid, 1 for valid
	 * 
	 * @param filter the String from which to extract the points information needed for the rangequery
	 * @return p[] an Integer array containing the information needed for the rangequery BBOX
	 */
	public static int[] extract(String filter){
		int p[] = new int[5];
		String y[] = new String[2];
		String x[] = new String[2];
		p[4] = 1;
		double x1,x2,y1,y2;
		int X1,X2,Y1,Y2;
		String arr[] = filter.split("\\[")[2].toString().split("\\]")[0].toString().split(",");
		
		for(int a = 0; a < arr.length; a++){
			if(a == 0)
				x = arr[a].split(":");
			if(a == 1)
				y = arr[a].split(":");
		}
		
		if(x[0].equals(y[0]) && x[1].equals(y[1]))
			p[4] = 0;
		
		x1 = Double.parseDouble(x[0].toString());
		x2 = Double.parseDouble(x[1].toString());
		y1 = Double.parseDouble(y[0].toString());
		y2 = Double.parseDouble(y[1].toString());
		
		X1 = (int)Math.round(x1);
		X2 = (int)Math.round(x2);
		Y1 = (int)Math.round(y1);
		Y2 = (int)Math.round(y2);

		p[0] = X1;
		p[1] = Y1;
		p[2] = (X2 - X1);
		p[3] = (Y2 - Y1);
		
		for(int b=0; b<4;b++)
			if(p[b] < 0)
				p[4] = 0;
		
		return p;
	}

}
