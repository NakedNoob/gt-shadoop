package org.geotools.data.shadoop;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.geotools.data.shadoop.query.BaseShadoopQueryObject;
import org.geotools.data.shadoop.query.ShadoopCollection;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class ShadoopLayer
{

    private ShadoopPluginConfig         config    = null;
    private String                    layerName = null;
    private SimpleFeatureType         schema    = null;
    private Set<String>               keywords  = null;
    private CoordinateReferenceSystem crs       = null;
    /** meta data for layer defining geometry type, property field names and types */

    private BaseShadoopQueryObject                  metaData  = null;

    /** Supported GeoJSON geometry types */
    static public enum GeometryType
    {
        GeometryCollection, LineString, Point, Polygon, MultiLineString, MultiPoint, MultiPolygon, Unknown;
    }

    /** Geometry type for this layer */
    private GeometryType geometryType = null;

    /**
     * How to calculate collection record fields and types Majority: for same named fields with
     * different types use major instance to determine which type to assign String: if same named
     * fields with different types exist; store them as Strings
     */
    static public enum RecordBuilder
    {
        MAJORITY, STRING;
    }

    /** How to build records with potentially different types for this layer */
    private RecordBuilder                  buildRule       = RecordBuilder.MAJORITY;

    /** Metadata map function (ensure no comments) */
    private String                         metaMapFunc     = "function() { mapfields_recursive (\"\", this);}";
    /** Metadata reduce function (ensure no comments) */
    private String                         metaReduceFunc  = "function (key, vals) {"
                                                                   + "  sum = 0;"
                                                                   + "  for (var i in vals) sum += vals[i];"
                                                                   + "  return sum;" + "}";
    /** Name of collection holding metadata results */
    private String                         metaResultsColl = "FieldsAndTypes";

    /**
     * Mapping from class string names from shadoop map-reduce to corresponding Java Class NB needs to
     * be synced with MetaDataCompute.js javascript file
     */
    static private HashMap<String, String> classNameMap    = new HashMap<String, String>();
    static
    {
    	//TODO find the datatypes in spatial hadoop to add to this mapping.
        //classNameMap.put( "array", BasicDBList.class.getCanonicalName() );
        classNameMap.put( "boolean", Boolean.class.getCanonicalName() );
        classNameMap.put( "date", Date.class.getCanonicalName() );
        classNameMap.put( "double", Double.class.getCanonicalName() );
        classNameMap.put( "long", Long.class.getCanonicalName() );
        //classNameMap.put( "object", BaseDSObject.class.getCanonicalName() );
        classNameMap.put( "string", String.class.getCanonicalName() );
    }

    /** Package logger */
    static private final Logger            log             = ShadoopPluginConfig.getLog();

    public ShadoopLayer (ShadoopCollection coll, ShadoopPluginConfig config)
    {
        this.config = config;
        layerName = coll.getName();
        log.fine( "ShadoopLayer; layerName " + layerName );
        keywords = new HashSet<String>();
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName( layerName );
        builder.setNamespaceURI( config.getNamespace() );
        // Always add _id...
        AttributeTypeBuilder b = new AttributeTypeBuilder();
        b.setBinding( String.class );
        b.setName( "_id" );
        b.setNillable( false );
        b.setDefaultValue( null );
        b.setLength( 1024 );
        AttributeDescriptor a = b.buildDescriptor( "_id" );
        builder.add( a );

        // We could get this out of the table, exercise for the reader... TODO
        try
        {
            crs = CRS.decode( "EPSG:4326" );
        }
        catch (Throwable t)
        {
            crs = DefaultGeographicCRS.WGS84;
        }

        b = new AttributeTypeBuilder();
        b.setName( "geometry" );
        b.setNillable( false );
        b.setDefaultValue( null );
        b.setCRS( crs );
        // determine metadata for this collection
        metaData = getCollectionModel( coll, buildRule );
        // determine geometry type
        //setGeometryType( metaData );

        switch (geometryType)
        {
        case GeometryCollection:
            b.setBinding( GeometryCollection.class );
            break;
        case LineString:
            b.setBinding( LineString.class );
            break;
        case Point:
            b.setBinding( Point.class );
            break;
        case Polygon:
            b.setBinding( Polygon.class );
            break;
        case MultiLineString:
            b.setBinding( MultiLineString.class );
            break;
        case MultiPoint:
            b.setBinding( MultiPoint.class );
            break;
        case MultiPolygon:
            b.setBinding( MultiPolygon.class );
            break;
        case Unknown:
            log.warning( "Unknown geometry for layer " + layerName
                    + " (but has valid distinct geometry.type)" );
            return;
        }

        a = b.buildDescriptor( "geometry" );
        builder.add( a );

        // Add the 2 known keywords...
        keywords.add( "_id" );
        keywords.add( "geometry" );

        // Now get all the properties...
        // AJG - TODO here
        schema = builder.buildFeatureType();
    }


    public String getName ()
    {
        return layerName;
    }

    public SimpleFeatureType getSchema ()
    {
        return schema;
    }

    public Set<String> getKeywords ()
    {
        return keywords;
    }

    public CoordinateReferenceSystem getCRS ()
    {
        return crs;
    }

    public ShadoopPluginConfig getConfig ()
    {
        return config;
    }

    /**
     * Get GeometryType
     * 
     * @return GeometryType, may be null if not set to valid and supported GeoJSON geometry
     */
    public GeometryType getGeometryType ()
    {
        return geometryType;
    }

    /**
     * Generate model of collection records' data fields and types
     * 
     * @param coll shadoop collection
     * @param buildRule which rule to apply if same named fields with different types exist
     * @return JSON object describing collection record
     */
    private BaseShadoopQueryObject getCollectionModel (ShadoopCollection coll, RecordBuilder buildRule)
    {
        BaseShadoopQueryObject qo = null;

        return metaData;
    }



    @Override
    public String toString ()
    {
        return this.toString();
    }



}
