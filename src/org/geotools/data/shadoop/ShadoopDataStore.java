package org.geotools.data.shadoop;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


import org.geotools.data.DataStore;
import org.geotools.data.DefaultServiceInfo;
import org.geotools.data.EmptyFeatureWriter;
import org.geotools.data.FeatureListener;
import org.geotools.data.FeatureListenerManager;
import org.geotools.data.FeatureReader;
import org.geotools.data.FeatureSource;
import org.geotools.data.FeatureWriter;
import org.geotools.data.LockingManager;
import org.geotools.data.Query;
import org.geotools.data.ServiceInfo;
import org.geotools.data.Transaction;
import org.geotools.data.shadoop.ShadoopLayer.GeometryType;
import org.geotools.data.shadoop.query.BaseShadoopQueryObject;
import org.geotools.data.shadoop.query.Shadoop;
import org.geotools.data.shadoop.query.ShadoopDS;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.NameImpl;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.Name;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotools.data.shadoop.query.ShadoopCollection;


public class ShadoopDataStore implements DataStore
{

    /** List of shadoop layers for this shadoop store */
    private ArrayList<ShadoopLayer>     layers = null;
    private CoordinateReferenceSystem crs    = null;
    /** Config for this shadoop plugin */
    private ShadoopPluginConfig         config = null;
    private FeatureListenerManager    lsnMgr = null;

    /** Package logger */
    private final static Logger       log    = ShadoopPluginConfig.getLog();

    public ShadoopDataStore (ShadoopPluginConfig config)
    {
        this.config = config;
        lsnMgr = new FeatureListenerManager();
        layers = new ArrayList<ShadoopLayer>();
        log.info( "ShadoopDataStore; layers=" + layers );
        try
        {
            crs = CRS.decode( "EPSG:4326" );
        }
        catch (Throwable t)
        {
            crs = DefaultGeographicCRS.WGS84;
        }
        // TODO when to look for and detect changes to layers
        if (layers.size() == 0)
        {
            getLayers();
        }
    }

    /**
     * Get list of valid layers for this shadoop DB; those containing at least one valid, non-null
     * GeoJSON geometry
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void getLayers ()
    {
        Shadoop shadoop = null;
        try
        {
            // Get the list of collections from Mongo...
            shadoop = new Shadoop( config );
            ShadoopDS db = shadoop.getDS( config.getDB() ); // TODO add authentication
            Set<String> colls = db.getCollectionNames();
            for (String s : colls)
            {
                ShadoopCollection dbc = db.getCollection( s );
                log.info( "getLayers; collection=" + dbc );
                // find distinct non-null geometry to determine if valid layer
                // TODO branch point for separate geometry-specific layers per collection
                List geoList = dbc.distinct( "geometry.type" );
                // distinct returns single BSON List, may barf if results large, > max doc. size
                // trap exception on props distinct and assume it's valid since there's obviously
                // something there (http://www.shadoopdb.org/display/DOCS/Aggregation)
                List propList = null;
                try
                {
                    propList = dbc.distinct( "properties" );
                }
                catch (Exception ex)
                {
                	System.out.println(ex.toString());
                }
                // check that layer has valid geometry and some properties defined
                if (geoList != null && propList != null && propList.size() > 0)
                {
                    boolean hasValidGeo = false;
                    for (GeometryType type : GeometryType.values())
                    {
                        if (geoList.contains( type.toString() ))
                        {
                            hasValidGeo = true;
                            break;
                        }
                    }
                    if (hasValidGeo)
                    {
                        layers.add( new ShadoopLayer( dbc, config ) );
                    }
                }
            }
        }
        catch (Throwable t)
        {
            log.severe( "getLayers error; " + t.getLocalizedMessage() );
        }
        if (shadoop != null)
        {
            shadoop.close();
        }
    }

    public CoordinateReferenceSystem getCRS ()
    {
        return crs;
    }

    public ShadoopPluginConfig getConfig ()
    {
        return config;
    }

    public void addListener (FeatureSource<?, ?> src, FeatureListener listener)
    {
        lsnMgr.addFeatureListener( src, listener );
    }

    public void removeListener (FeatureSource<?, ?> src, FeatureListener listener)
    {
        lsnMgr.removeFeatureListener( src, listener );
    }

    public Set<String> getKeywords (String typeName)
    {
        Set<String> result = null;

        for (ShadoopLayer ml : layers)
        {
            if (ml.getName().equals( typeName ))
            {
                result = ml.getKeywords();
                break;
            }
        }

        return result;
    }

    public LockingManager getLockingManager ()
    {
        // returning null as per DataStore.getLockingManager() contract
        return null;
    }

    public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriterAppend (final String typeName,
                                                                                   final Transaction transaction)
    {
        return new EmptyFeatureWriter( new SimpleFeatureTypeBuilder().buildFeatureType() );
    }

    public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriter (final String typeName,
                                                                             final Transaction transaction)
    {
        return new EmptyFeatureWriter( new SimpleFeatureTypeBuilder().buildFeatureType() );
    }

    public FeatureWriter<SimpleFeatureType, SimpleFeature> getFeatureWriter (final String typeName,
                                                                             final Filter filter,
                                                                             final Transaction transaction)
    {
        return new EmptyFeatureWriter( new SimpleFeatureTypeBuilder().buildFeatureType() );
    }

    public FeatureReader<SimpleFeatureType, SimpleFeature> getFeatureReader (final Query query,
                                                                             final Transaction transaction)
    {
        FilterToShadoopQuery f2m = new FilterToShadoopQuery();
        Filter filter = query.getFilter();
        BaseShadoopQueryObject dbo = (BaseShadoopQueryObject) filter.accept( f2m, null );
        ShadoopLayer layer = getMongoLayer( query.getTypeName() );
        ShadoopResultSet rs = new ShadoopResultSet( layer, dbo );
        return new ShadoopFeatureReader( rs );
    }

    public SimpleFeatureSource getFeatureSource (final String typeName) throws IOException
    {
        ShadoopLayer layer = getMongoLayer( typeName );
        return new ShadoopFeatureSource( this, layer );
    }

    public FeatureSource<SimpleFeatureType, SimpleFeature> getView (final Query query)
    {
        FilterToShadoopQuery f2m = new FilterToShadoopQuery();
        Filter filter = query.getFilter();
        BaseShadoopQueryObject dbo = (BaseShadoopQueryObject) filter.accept( f2m, null );
        ShadoopLayer layer = getMongoLayer( query.getTypeName() );
        return new ShadoopFeatureSource( this, layer, dbo );
    }

    public SimpleFeatureType getSchema (final String typeName)
    {
        SimpleFeatureType sft = null;

        for (ShadoopLayer ml : layers)
        {
            if (ml.getName().equals( typeName ))
            {
                sft = ml.getSchema();
            }
        }

        return sft;
    }

    public String[] getTypeNames ()
    {
        String[] names = new String[layers.size()];
        int idx = 0;
        for (ShadoopLayer ml : layers)
        {
            names[idx++] = ml.getName();
        }
        return names;
    }

    public void updateSchema (final String typeName, final SimpleFeatureType featureType)
                                                                                         throws IOException
    {
        throw new UnsupportedOperationException( "Schema modification not supported" );
    }

    public void dispose ()
    {

    }

    public SimpleFeatureSource getFeatureSource (Name name) throws IOException
    {
        return getFeatureSource( name.getLocalPart() );
    }

    public SimpleFeatureType getSchema (Name name) throws IOException
    {
        return getSchema( name.getLocalPart() );
    }

    public List<Name> getNames () throws IOException
    {
        List<Name> names = new ArrayList<Name>( layers.size() );
        for (ShadoopLayer ml : layers)
        {
            names.add( new NameImpl( ml.getName() ) );
        }
        return names;
    }

    public void updateSchema (Name typeName, SimpleFeatureType featureType) throws IOException
    {
        updateSchema( typeName.getLocalPart(), featureType );
    }

    public void createSchema (final SimpleFeatureType featureType) throws IOException,
                                                                  IllegalArgumentException
    {

    }

    public ServiceInfo getInfo ()
    {
        DefaultServiceInfo info = new DefaultServiceInfo();
        info.setTitle( "MongoDB Data Store" );
        info.setDescription( "Features from MongoDB" );
        try
        {
            info.setSchema( new URI( config.getNamespace() ) );
        }
        catch (Throwable t)
        {
        }
        return info;
    }

    public ShadoopLayer getMongoLayer (String typeName)
    {
        ShadoopLayer layer = null;
        for (ShadoopLayer ml : layers)
        {
            if (ml.getName().equals( typeName ))
            {
                layer = ml;
                break;
            }
        }
        return layer;
    }

}
