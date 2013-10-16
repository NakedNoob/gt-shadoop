package org.geotools.data.shadoop;

import java.net.URI;
import java.util.Set;

import org.geotools.data.ResourceInfo;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class ShadoopResourceInfo implements ResourceInfo
{

    private ShadoopFeatureSource myFS;
    private URI                myURI;

    public ShadoopResourceInfo (ShadoopFeatureSource fs)
    {
        myFS = fs;
        myURI = URI.create( myFS.getLayer().getSchema().getName().getNamespaceURI() );
    }

    public CoordinateReferenceSystem getCRS ()
    {
        return myFS.getLayer().getCRS();
    }

    public ReferencedEnvelope getBounds ()
    {
        return myFS.getBounds();
    }

    public URI getSchema ()
    {
        return myURI;
    }

    public String getName ()
    {
        return myFS.getLayer().getName();
    }

    public String getDescription ()
    {
        return "MongoDB Resource";
    }

    public Set<String> getKeywords ()
    {
        return myFS.getLayer().getKeywords();
    }

    public String getTitle ()
    {
        return myFS.getLayer().getSchema().getDescription().toString();
    }
}
