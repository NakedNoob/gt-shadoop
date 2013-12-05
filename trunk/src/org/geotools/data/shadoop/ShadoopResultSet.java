package org.geotools.data.shadoop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.geotools.data.shadoop.ShadoopLayer.GeometryType;
import org.geotools.data.shadoop.query.BaseShadoopQueryObject;
import org.geotools.data.shadoop.query.DSObject;
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
        bounds = new ReferencedEnvelope( 0, 0, 0, 0, layer.getCRS() );
        features = new ArrayList<SimpleFeature>();
        if (query != null)
        {
            buildFeatures( query );
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
        
        try 
        {
	        File file = new File("C:\\Documents and Settings\\j16727\\gt-shadoop\\src\\points.txt");
	        // TODO - change the above to whatever Gordon outputs
	        
	        // http://sourceforge.net/mailarchive/message.php?msg_id=31504683
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
	        			String tokens[] = line.split(",");
	        			double latitude = Double.parseDouble(tokens[0]);
	        			double longitude = Double.parseDouble(tokens[1]);
	        			Point point = geometryFactory.createPoint(new Coordinate(longitude,latitude));
	        			
	        			featureBuilder.set(0,point);
	        			SimpleFeature feature = featureBuilder.buildFeature(null);
	        			features.add(feature);
	        		}
	        	}
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

        // The below WILL NOT WORK for GeoServer's Layer Preview
//        try
//        {
//            if (layer.getGeometryType() == null)
//            {
//                return;
//            }
//            shadoop = new Shadoop( layer.getConfig() );
//            ShadoopDS db = shadoop.getDS( layer.getConfig().getDB() );
//            ShadoopCollection coll = db.getCollection( layer.getName() );
//            DSCursor cur = coll.find( query );  // That ShadoopCollection find method is the key. That is where your 'request' logic is truly implemented.
//            minX = 180;
//            maxX = -180;
//            minY = 90;
//            maxY = -90;
//            
//            File file = new File("C:\\Documents and Settings\\j16727\\gt-shadoop\\src\\points.txt");
//
//            // http://docs.geotools.org/latest/tutorials/feature/csv2shp.html
////            final SimpleFeatureType TYPE = DataUtilities.createType("Location", "location:Point:srid=4326");
//            
//            // http://sourceforge.net/mailarchive/message.php?msg_id=31504683
//            SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
//            builder.setName("Location");
//            builder.setSRS("EPSG:4326");
//            builder.add("Location", Point.class);
//            final SimpleFeatureType TYPE = builder.buildFeatureType();            
//            
//            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
//            SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
////            List<SimpleFeature> features = new ArrayList<SimpleFeature>();
//            
//            BufferedReader reader = new BufferedReader(new FileReader(file));
//            
//            int i = 0;
//            try 
//            {
//            	String line;
//            	
//            	for (line = reader.readLine(); line != null; line = reader.readLine())
//            	{
//            		if (line.trim().length() > 0) // skip blank lines 
//            		{
//            			String tokens[] = line.split(",");
//            			double latitude = Double.parseDouble(tokens[0]);
//            			double longitude = Double.parseDouble(tokens[1]);
//            			Point point = geometryFactory.createPoint(new Coordinate(longitude,latitude));
//            			
//            			featureBuilder.set(i,point);
//            			SimpleFeature feature = featureBuilder.buildFeature(null);
//            			features.add(feature);
//            		}
//            		i++;
//            	}
//            } 
//	        catch (IOException ioe)
//	        {
//	        	log.severe(ioe.toString());
//	        }
//            finally 
//            {
//            	reader.close();
//            }
//
//            SimpleFeatureBuilder fb = new SimpleFeatureBuilder( layer.getSchema() );
//
//            // use SimpleFeatureBuilder.set(name, value) rather than add(value) since
//            // attributes not in guaranteed order
//            log.finer( "cur.count()=" + cur.count() );
//
//            while (cur.hasNext())
//            {
//                DSObject dbo = cur.next();
//                if (dbo == null)
//                {
//                    continue;
//                }
//
//                // get shadoop id and ensure valid
//                if (dbo.get( "_id" ) instanceof ObjectId)
//                {
//                    ObjectId oid = (ObjectId) dbo.get( "_id" );
//                    fb.set( "_id", oid.toString() );
//                }
//                 else
//                {
//                    throw new ShadoopPluginException( "_id is invalid type: "
//                            + dbo.get( "_id" ).getClass() );
//                }
//
//                // ensure geometry defined
//                DSObject geo = (DSObject) dbo.get( "geometry" );
//                if (geo == null || geo.get( "type" ) == null
//                        || (geo.get( "coordinates" ) == null && geo.get( "geometries" ) == null))
//                {
//                    continue;
//                }
//
//                // GeometryType of current record
//                GeometryType recordGeoType = GeometryType.valueOf( geo.get( "type" ).toString() );
//                // skip record if its geo type does not match layer geo type
//                if (!layer.getGeometryType().equals( recordGeoType ))
//                {
//                    continue;
//                }
//
//                // create Geometry for given type
//                Geometry recordGeometry = createGeometry( recordGeoType, geo );
//                if (recordGeometry != null)
//                {
//                    fb.set( "geometry", recordGeometry );
//                    // set non-geometry properties for feature (GeoJSON.properties)
//                    DSObject props = (DSObject) dbo.get( "properties" );
//                    setProperties( fb, "properties", props );
//                    features.add( fb.buildFeature( null ) );
//                    bounds = new ReferencedEnvelope( minX, maxX, minY, maxY, layer.getCRS() );
//                }
//                else
//                {
//                    fb.reset();
//                }
//            }
//        }
//        catch (Throwable t)
//        {
//            log.severe( "Error building layer " + layer.getName() + "; " + t.getLocalizedMessage() );
//        }
        if (shadoop != null)
        {
            shadoop.close();
        }
    }

    /**
     * Sets the properties.
     *
     * @param fb the fb
     * @param string the string
     * @param props the props
     */
    private void setProperties(SimpleFeatureBuilder fb, String string,
			DSObject props) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Creates the geometry.
	 *
	 * @param recordGeoType the record geo type
	 * @param geo the geo
	 * @return the geometry
	 */
	private Geometry createGeometry(GeometryType recordGeoType, DSObject geo) {
		// TODO Auto-generated method stub
		return null;
	}

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
