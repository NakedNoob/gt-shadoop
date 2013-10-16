package org.geotools.data.shadoop;

import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.geotools.data.shadoop.ShadoopLayer.GeometryType;
import org.geotools.data.shadoop.query.BaseShadoopQueryObject;
import org.geotools.data.shadoop.query.DSCursor;
import org.geotools.data.shadoop.query.DSObject;
import org.geotools.data.shadoop.query.Shadoop;
import org.geotools.data.shadoop.query.ShadoopCollection;
import org.geotools.data.shadoop.query.ShadoopDS;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.identity.ObjectId;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

public class ShadoopResultSet
{

    private ShadoopLayer                   layer      = null;
    private ArrayList<SimpleFeature>     features   = null;
    private ReferencedEnvelope           bounds     = null;
    double                               minX       = 180;
    double                               maxX       = -180;
    double                               minY       = 90;
    double                               maxY       = -90;
    /** Package logger */
    static private final Logger          log        = ShadoopPluginConfig.getLog();

    static private final PrecisionModel  pm         = new PrecisionModel();
    /** GeometryFactory with given precision model */
    static private final GeometryFactory geoFactory = new GeometryFactory( pm, -1 );

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
     * SimpleFeatureBuilder
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
            if (layer.getGeometryType() == null)
            {
                return;
            }
            shadoop = new Shadoop( layer.getConfig() );
            ShadoopDS db = shadoop.getDS( layer.getConfig().getDB() );
            ShadoopCollection coll = db.getCollection( layer.getName() );
            DSCursor cur = coll.find( query );
            minX = 180;
            maxX = -180;
            minY = 90;
            maxY = -90;
            SimpleFeatureBuilder fb = new SimpleFeatureBuilder( layer.getSchema() );
            // use SimpleFeatureBuilder.set(name, value) rather than add(value) since
            // attributes not in guaranteed order
            log.finer( "cur.count()=" + cur.count() );

            while (cur.hasNext())
            {
                DSObject dbo = cur.next();
                if (dbo == null)
                {
                    continue;
                }

                // get shadoop id and ensure valid
                if (dbo.get( "_id" ) instanceof ObjectId)
                {
                    ObjectId oid = (ObjectId) dbo.get( "_id" );
                    fb.set( "_id", oid.toString() );
                }
                 else
                {
                    throw new ShadoopPluginException( "_id is invalid type: "
                            + dbo.get( "_id" ).getClass() );
                }

                // ensure geometry defined
                DSObject geo = (DSObject) dbo.get( "geometry" );
                if (geo == null || geo.get( "type" ) == null
                        || (geo.get( "coordinates" ) == null && geo.get( "geometries" ) == null))
                {
                    continue;
                }

                // GeometryType of current record
                GeometryType recordGeoType = GeometryType.valueOf( geo.get( "type" ).toString() );
                // skip record if its geo type does not match layer geo type
                if (!layer.getGeometryType().equals( recordGeoType ))
                {
                    continue;
                }

                // create Geometry for given type
                Geometry recordGeometry = createGeometry( recordGeoType, geo );
                if (recordGeometry != null)
                {
                    fb.set( "geometry", recordGeometry );
                    // set non-geometry properties for feature (GeoJSON.properties)
                    DSObject props = (DSObject) dbo.get( "properties" );
                    setProperties( fb, "properties", props );
                    features.add( fb.buildFeature( null ) );
                    bounds = new ReferencedEnvelope( minX, maxX, minY, maxY, layer.getCRS() );
                }
                else
                {
                    fb.reset();
                }
            }
        }
        catch (Throwable t)
        {
            log.severe( "Error building layer " + layer.getName() + "; " + t.getLocalizedMessage() );
        }
        if (shadoop != null)
        {
            shadoop.close();
        }
    }

    private void setProperties(SimpleFeatureBuilder fb, String string,
			DSObject props) {
		// TODO Auto-generated method stub
		
	}

	private Geometry createGeometry(GeometryType recordGeoType, DSObject geo) {
		// TODO Auto-generated method stub
		return null;
	}

	public SimpleFeatureType getSchema ()
    {
        return layer.getSchema();
    }

    /**
     * Get Feature references by index
     * @param idx
     * @return SimpleFeature, null if idx out of bounds
     */
    public SimpleFeature getFeature (int idx) throws IndexOutOfBoundsException
    {
        if (idx < 0 || idx >= features.size())
            throw new IndexOutOfBoundsException( "Index " + idx + " exceeds features size of "
                    + features.size() );
        return features.get( idx );
    }

    public int getCount ()
    {
        return features.size();
    }

    public ReferencedEnvelope getBounds ()
    {
        return bounds;
    }

    /**
     * Paginate result features using startIndex and maxFeatures
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
