package org.geotools.data.shadoop.query;

import java.util.Set;
import java.util.TreeSet;

// TODO: Auto-generated Javadoc
/**
 * The Class ShadoopDS.
 */
public class ShadoopDS {

	/**
	 * Gets the collection.
	 *
	 * @param name the name
	 * @return the collection
	 */
	public ShadoopCollection getCollection(String name) {
		ShadoopCollection s = new ShadoopCollection();
		return s;
	}

	/**
	 * Gets the collection names.
	 *
	 * @return the collection names
	 */
	public Set<String> getCollectionNames() {
		Set<String> collectionNames = new TreeSet<String>();
		collectionNames.add("collection-001");
		
		return collectionNames;
	}

}
