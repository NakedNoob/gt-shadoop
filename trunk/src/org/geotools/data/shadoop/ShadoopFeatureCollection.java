package org.geotools.data.shadoop;

import org.geotools.data.FeatureReader;
import org.geotools.data.store.DataFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

// TODO: Auto-generated Javadoc
/**
 * The Class ShadoopFeatureCollection.
 */
public class ShadoopFeatureCollection extends DataFeatureCollection
{

    /** The results. */
    private ShadoopResultSet results = null;

    /**
     * Instantiates a new shadoop feature collection.
     *
     * @param rs the rs
     */
    public ShadoopFeatureCollection (ShadoopResultSet rs)
    {
        results = rs;
    }

    /**
     * Gets the count.
     *
     * @return the count
     */
    @Override
    public int getCount ()
    {
        return results.getCount();
    }

    /**
     * Gets the bounds.
     *
     * @return the bounds
     */
    @Override
    public ReferencedEnvelope getBounds ()
    {
        return results.getBounds();
    }

    /**
     * Gets the schema.
     *
     * @return the schema
     */
    @Override
    public SimpleFeatureType getSchema ()
    {
        return results.getSchema();
    }

    /**
     * Reader.
     *
     * @return the feature reader
     */
    @Override
    public FeatureReader<SimpleFeatureType, SimpleFeature> reader ()
    {
        return new ShadoopFeatureReader( results );
    }
}
