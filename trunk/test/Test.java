
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("here");
		String filter = "FastBBOX [property=geometry, envelope=ReferencedEnvelope[10.1 : 125.9, -0.87 : 120.6]]";
		int points[] = extract(filter);
	}
	
	private static int[] extract(String filter){
		int p[] = new int[4];
		String lower[] = new String[2];
		String upper[] = new String[2];
		
		double x1;
		double x2;
		double y1;
		double y2;
		int X1;
		int X2;
		int Y1;
		int Y2;
		String arr[] = filter.split("\\[")[2].toString().split("\\]")[0].toString().split(",");
		for(int x = 0; x < arr.length; x++){
			//System.out.println();
			if(x == 0)
				lower = arr[x].split(":");
			if(x == 1)
				upper = arr[x].split(":");
			
		}
		
		x1 = Double.parseDouble(lower[0].toString());
		x2 = Double.parseDouble(upper[0].toString());
		y1 = Double.parseDouble(lower[1].toString());
		y2 = Double.parseDouble(upper[1].toString());
		System.out.println(x1);
		System.out.println(y1);
		System.out.println(x2);
		System.out.println(y2);
		X1 = (int)Math.round(x1);
		X2 = (int)Math.round(x2);
		Y1 = (int)Math.round(y1);
		Y2 = (int)Math.round(y2);
		System.out.println(X1);
		System.out.println(X2);
		System.out.println(Y1);
		System.out.println(Y2);
		p[0]=X1;
		p[1]=Y1;
		p[2]=X2;
		p[3]=Y2;
		for(int s : p)
			System.out.println(s);
//		
//		for(String d : upper)
//			System.out.println("upper "+d);
			
//		String arr2[] = arr[2].split("\\]");
//		for(String s : arr2)
//			System.out.println(s);
		return p;
	}

}
