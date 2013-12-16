package org.geotools.data.shadoop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import org.geotools.data.shadoop.ShadoopLayer.GeometryType;
import org.geotools.data.shadoop.query.BaseShadoopQueryObject;
import org.geotools.data.shadoop.query.Query;
import org.geotools.data.shadoop.query.Shadoop;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;

// TODO: Auto-generated Javadoc
/**
 * The Class ShadoopResultSet.
 */
public class ShadoopResultSet
{

    /** The layer. */
    private ShadoopLayer                   layer      = null;
    
    /** The points information. */
    private int[] 							points	= null;
    
    /** The features. */
    private ArrayList<SimpleFeature>     features   = null;
    
    /** The bounds. */
    private ReferencedEnvelope           bounds     = null;
    
    /** The min x. */
    double                               minX       = 180;
    
    /** The max x. */
    double                               maxX       = -180;
    
    /** The min y. */
    double                               minY       = 90;
    
    /** The max y. */
    double                               maxY       = -90;
    
    /** Package logger. */
    static private final Logger          log        = ShadoopPluginConfig.getLog();

    /** The Constant pm. */
    static private final PrecisionModel  pm         = new PrecisionModel();
    
    /** GeometryFactory with given precision model. */
    static private final GeometryFactory geoFactory = new GeometryFactory( pm, -1 );

    /**
     * Instantiates a new shadoop result set.
     *
     * @param layer the layer
     * @param query the query
     */
    public ShadoopResultSet (ShadoopLayer layer, BaseShadoopQueryObject query)
    {
        this.layer = layer;
        points = new int[5];
        Arrays.fill(points, 0);
        points[4] = 1;
        bounds = new ReferencedEnvelope( 0, 0, 0, 0, layer.getCRS() );
        features = new ArrayList<SimpleFeature>();
        if (query != null){
        	if(points[4] == 1)
        		buildFeatures( query );
        	else
        		System.out.println("Query cannot be processed: Coordinates are either the same or contain negative values");
        }
    }

    /**
     * Instantiates a new shadoop result set.
     *
     * @param layer the layer
     * @param query the query
     * @param points the BBOX info
     */
    public ShadoopResultSet (ShadoopLayer layer, BaseShadoopQueryObject query, int[] points)
    {
        this.layer = layer;
        this.points = points;
        bounds = new ReferencedEnvelope( 0, 0, 0, 0, layer.getCRS() );
        features = new ArrayList<SimpleFeature>();
        if (query != null){
        	if(points[4] == 1)
        		buildFeatures( query );
        	else
        		System.out.println("Query cannot be processed: Coordinates are either the same or contain negative values");
        }
    }
    
    /**
     * Build features for given layer; convert shadoop collection records to equivalent geoTools
     * SimpleFeatureBuilder.
     *
     * @param query shadoopDB query (empty to find all)
     */
    private void buildFeatures (BaseShadoopQueryObject query)
    {
        if (layer == null)
        {
            log.warning( "buildFeatures called, but layer is null" );
            return;
        }
        Shadoop shadoop = null;
        String filePath = "";
        try 
        {
        	Query q = new Query();
        	try {
				filePath = q.runRangeQuery(points[0], points[1], points[2], points[3]);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	log.warning( "################################" );
        	log.warning( "################################" );
        	log.warning( "################################" );
        	log.warning( "################################" );
        	log.warning( "FILE PATH IS: "+filePath );
        	log.warning( "################################" );
        	log.warning( "################################" );
        	log.warning( "################################" );
        	log.warning( "################################" );
	        File file = new File(filePath);
	        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
	        builder.setName("Location");
	        builder.setSRS("EPSG:4326");
	        builder.add("Location", Point.class);
	        final SimpleFeatureType TYPE = builder.buildFeatureType();            
	        
	        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
	        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
	        
	        BufferedReader reader = new BufferedReader(new FileReader(file));
	        
	        try 
	        {
	        	String line;
	        	
	        	for (line = reader.readLine(); line != null; line = reader.readLine())
	        	{
	        		if (line.trim().length() > 0) // skip blank lines 
	        		{
	        			log.warning( "################################" );
	                	log.warning( "FILE LINE IS: "+line );
	                	log.warning( "################################" );
	        			String tokens[] = line.split(",");
	        			double latitude = Double.parseDouble(tokens[0]);
	        			double longitude = Double.parseDouble(tokens[1]);
	        			Point point = geometryFactory.createPoint(new Coordinate(longitude,latitude));
	        			
	        			featureBuilder.set(0,point);
	        			SimpleFeature feature = featureBuilder.buildFeature(null);
	        			log.warning( "################################" );
	                	log.warning( "FEATURE IS: "+feature.toString() );
	                	log.warning( "################################" );
	        			features.add(feature);
	        		}
	        	}
	        	log.warning( "################################" );
            	log.warning( "THERE ARE "+features.size()+" features" );
            	log.warning( "################################" );
	        } 
	        finally 
	        {
	        	reader.close();
	        }
	    }
        catch (IOException ioe)
        {
        	log.severe(ioe.toString());
        }

        
        if (shadoop != null)
        {
            shadoop.close();
        }
    }

//    /**
//     * Sets the properties.
//     *
//     * @param fb the fb
//     * @param string the string
//     * @param props the props
//     */
//    private void setProperties(SimpleFeatureBuilder fb, String string,
//			DSObject props) {
//		
//	}

//	/**
//	 * Creates the geometry.
//	 *
//	 * @param recordGeoType the record geo type
//	 * @param geo the geo
//	 * @return the geometry
//	 */
//	private Geometry createGeometry(GeometryType recordGeoType, DSObject geo) {
//		return null;
//	}

	/**
	 * Gets the schema.
	 *
	 * @return the schema
	 */
	public SimpleFeatureType getSchema ()
    {
        return layer.getSchema();
    }

    /**
     * Get Feature references by index.
     *
     * @param idx the idx
     * @return SimpleFeature, null if idx out of bounds
     * @throws IndexOutOfBoundsException the index out of bounds exception
     */
    public SimpleFeature getFeature (int idx) throws IndexOutOfBoundsException
    {
        if (idx < 0 || idx >= features.size())
            throw new IndexOutOfBoundsException( "Index " + idx + " exceeds features size of "
                    + features.size() );
        return features.get( idx );
    }

    /**
     * Gets the count.
     *
     * @return the count
     */
    public int getCount ()
    {
        return features.size();
    }

    /**
     * Gets the bounds.
     *
     * @return the bounds
     */
    public ReferencedEnvelope getBounds ()
    {
        return bounds;
    }

    /**
     * Paginate result features using startIndex and maxFeatures.
     *
     * @param startIndex starting index (>= 0)
     * @param maxFeatures max features to return (> 0)
     */
    public void paginateFeatures (int startIndex, int maxFeatures)
    {
        int endIndex = startIndex + maxFeatures;
        if (startIndex >= 0 && maxFeatures > 0 && endIndex < features.size())
        {
            features = new ArrayList<SimpleFeature>( features.subList( startIndex, endIndex ) );
        }
    }


}
