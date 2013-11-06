package org.geotools.data.shadoop;

import java.net.URI;
import java.util.Set;

import org.geotools.data.ResourceInfo;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

// TODO: Auto-generated Javadoc
/**
 * The Class ShadoopResourceInfo.
 */
public class ShadoopResourceInfo implements ResourceInfo
{

    /** The my fs. */
    private ShadoopFeatureSource myFS;
    
    /** The my uri. */
    private URI                myURI;

    /**
     * Instantiates a new shadoop resource info.
     *
     * @param fs the fs
     */
    public ShadoopResourceInfo (ShadoopFeatureSource fs)
    {
        myFS = fs;
        myURI = URI.create( myFS.getLayer().getSchema().getName().getNamespaceURI() );
    }

    /**
     * Gets the crs.
     *
     * @return the crs
     */
    public CoordinateReferenceSystem getCRS ()
    {
        return myFS.getLayer().getCRS();
    }

    /**
     * Gets the bounds.
     *
     * @return the bounds
     */
    public ReferencedEnvelope getBounds ()
    {
        return myFS.getBounds();
    }

    /**
     * Gets the schema.
     *
     * @return the schema
     */
    public URI getSchema ()
    {
        return myURI;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName ()
    {
        return myFS.getLayer().getName();
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription ()
    {
        return "MongoDB Resource";
    }

    /**
     * Gets the keywords.
     *
     * @return the keywords
     */
    public Set<String> getKeywords ()
    {
        return myFS.getLayer().getKeywords();
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle ()
    {
        return myFS.getLayer().getSchema().getDescription().toString();
    }
}
