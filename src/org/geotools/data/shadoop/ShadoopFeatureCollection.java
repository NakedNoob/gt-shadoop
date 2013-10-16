package org.geotools.data.shadoop;

import org.geotools.data.FeatureReader;
import org.geotools.data.store.DataFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

public class ShadoopFeatureCollection extends DataFeatureCollection
{

    private ShadoopResultSet results = null;

    public ShadoopFeatureCollection (ShadoopResultSet rs)
    {
        results = rs;
    }

    @Override
    public int getCount ()
    {
        return results.getCount();
    }

    @Override
    public ReferencedEnvelope getBounds ()
    {
        return results.getBounds();
    }

    @Override
    public SimpleFeatureType getSchema ()
    {
        return results.getSchema();
    }

    @Override
    public FeatureReader<SimpleFeatureType, SimpleFeature> reader ()
    {
        return new ShadoopFeatureReader( results );
    }
}
