package org.geotools.data.shadoop;

import java.net.URI;
import java.util.Set;
import java.util.logging.Logger;

import org.geotools.data.ResourceInfo;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * The Class ShadoopResourceInfo.
 */
public class ShadoopResourceInfo implements ResourceInfo
{

    /** The my fs. */
    private ShadoopFeatureSource myFS;
    
    /** The my uri. */
    private URI                myURI;
    
    /** Package logger. */
    private final static Logger       log    = ShadoopPluginConfig.getLog();

    /**
     * Instantiates a new shadoop resource info.
     *
     * @param fs the fs
     */
    public ShadoopResourceInfo (ShadoopFeatureSource fs){
    	if(fs == null){
    		log.warning("##### ShadoopFeatureSource was null in ShadoopResourceInfo Constructor #####");
//    		ShadoopPluginConfig config = new ShadoopPluginConfig(new HashMap<String,Serializable>());
//    		fs = new ShadoopFeatureSource(new ShadoopLayer("shadoop layer",config));
    	}
    	log.info("##### ShadoopFeatureSource : " + fs.toString());
        myFS = fs;
        myURI = URI.create( myFS.getLayer().getSchema().getName().getNamespaceURI() );
        log.info("##### URI : " + myURI.toString());
    }

    /**
     * Gets the crs.
     *
     * @return the crs
     */
    public CoordinateReferenceSystem getCRS (){
        return myFS.getLayer().getCRS();
    }

    /**
     * Gets the bounds.
     *
     * @return the bounds
     */
    public ReferencedEnvelope getBounds (){
        return myFS.getBounds();
    }

    /**
     * Gets the schema.
     *
     * @return the schema
     */
    public URI getSchema (){
        return myURI;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName (){
        return myFS.getLayer().getName();
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription (){
        return "Shadoop Resource";
    }

    /**
     * Gets the keywords.
     *
     * @return the keywords
     */
    public Set<String> getKeywords (){
        return myFS.getLayer().getKeywords();
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle (){
    	if ( myFS.getLayer().getSchema().getDescription() != null) {
    		log.info("Layer Schema Description: " + myFS.getLayer().getSchema().getDescription().toString());
    		return myFS.getLayer().getSchema().getDescription().toString();
    	}
    	log.info("Layer: " + myFS.getLayer().toString());
    	log.info("Layer Schema: " + myFS.getLayer().getSchema().toString());
        return "Shadoop Resource Title" ;
    }
}
