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

// TODO: Auto-generated Javadoc
/**
 * The Class ShadoopLayer.
 */
public class ShadoopLayer
{

    /** The config. */
    private ShadoopPluginConfig         config    = null;
    
    /** The layer name. */
    private String                    layerName = null;
    
    /** The schema. */
    private SimpleFeatureType         schema    = null;
    
    /** The keywords. */
    private Set<String>               keywords  = null;
    
    /** The crs. */
    private CoordinateReferenceSystem crs       = null;
    
    /** meta data for layer defining geometry type, property field names and types. */

    private BaseShadoopQueryObject                  metaData  = null;

    /**
     * Supported GeoJSON geometry types.
     */
    static public enum GeometryType
    {
        
        /** The Geometry collection. */
        GeometryCollection, 
 /** The Line string. */
 LineString, 
 /** The Point. */
 Point, 
 /** The Polygon. */
 Polygon, 
 /** The Multi line string. */
 MultiLineString, 
 /** The Multi point. */
 MultiPoint, 
 /** The Multi polygon. */
 MultiPolygon, 
 /** The Unknown. */
 Unknown;
    }

    /** Geometry type for this layer. */
    private GeometryType geometryType = null;

    /**
     * How to calculate collection record fields and types Majority: for same named fields with
     * different types use major instance to determine which type to assign String: if same named
     * fields with different types exist; store them as Strings.
     */
    static public enum RecordBuilder
    {
        
        /** The majority. */
        MAJORITY, 
 /** The string. */
 STRING;
    }

    /** How to build records with potentially different types for this layer. */
    private RecordBuilder                  buildRule       = RecordBuilder.MAJORITY;

    /** Metadata map function (ensure no comments). */
    private String                         metaMapFunc     = "function() { mapfields_recursive (\"\", this);}";
    
    /** Metadata reduce function (ensure no comments). */
    private String                         metaReduceFunc  = "function (key, vals) {"
                                                                   + "  sum = 0;"
                                                                   + "  for (var i in vals) sum += vals[i];"
                                                                   + "  return sum;" + "}";
    
    /** Name of collection holding metadata results. */
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

    /** Package logger. */
    static private final Logger            log             = ShadoopPluginConfig.getLog();

    /**
     * Instantiates a new shadoop layer.
     *
     * @param coll the coll
     * @param config the config
     */
    public ShadoopLayer (String name, ShadoopPluginConfig config)
    {
    	log.info( "ShadoopLayer; layerName " + name );
        this.config = config;
        log.info("###### config:" + config.toString() + "######");
//        layerName = coll.getName();
        layerName = name;
        
        keywords = new HashSet<String>();
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName( layerName );
        builder.setNamespaceURI( config.getNamespace() );
        // Always add _id...
        AttributeTypeBuilder b = new AttributeTypeBuilder();
        b.setBinding( String.class );
        b.setName( "_id" );
        b.setNillable( false );
        b.setDefaultValue( "default ID" );
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
        b.setDefaultValue( new String("0,0") );
        b.setCRS( crs );
        // determine metadata for this collection
        //metaData = getCollectionModel( coll, buildRule );
        // determine geometry type
        //setGeometryType( metaData );
        geometryType = GeometryType.Point;
        log.info("###### b:" + geometryType.toString() + "######");
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
        log.info("###### b:" + b.toString() + "######");
        builder.add( a );

        // Add the 2 known keywords...
        keywords.add( "_id" );
        keywords.add( "geometry" );

        // Now get all the properties...
        // AJG - TODO here
        schema = builder.buildFeatureType();
    }


    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName ()
    {
        return layerName;
    }

    /**
     * Gets the schema.
     *
     * @return the schema
     */
    public SimpleFeatureType getSchema ()
    {
        return schema;
    }

    /**
     * Gets the keywords.
     *
     * @return the keywords
     */
    public Set<String> getKeywords ()
    {
        return keywords;
    }

    /**
     * Gets the crs.
     *
     * @return the crs
     */
    public CoordinateReferenceSystem getCRS ()
    {
        return crs;
    }

    /**
     * Gets the config.
     *
     * @return the config
     */
    public ShadoopPluginConfig getConfig ()
    {
        return config;
    }

    /**
     * Get GeometryType.
     *
     * @return GeometryType, may be null if not set to valid and supported GeoJSON geometry
     */
    public GeometryType getGeometryType ()
    {
        return geometryType;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString ()
    {
        return super.toString();
    }
}
