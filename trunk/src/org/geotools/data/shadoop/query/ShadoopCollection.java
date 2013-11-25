package org.geotools.data.shadoop.query;

import java.util.List;


// TODO: Auto-generated Javadoc
/**
 * The Class ShadoopCollection.
 */
public class ShadoopCollection {

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return new String ("shadoop-collection-001");
	}

	/**
	 * Map reduce.
	 *
	 * @param metaMapFunc the meta map func
	 * @param metaReduceFunc the meta reduce func
	 * @param metaResultsColl the meta results coll
	 * @param baseShadoopQueryObject the base shadoop query object
	 */
	public void mapReduce(String metaMapFunc, String metaReduceFunc,
			String metaResultsColl,
			BaseShadoopQueryObject baseShadoopQueryObject) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Find.
	 *
	 * @param query the query
	 * @return the dS cursor
	 */
	public DSCursor find(BaseShadoopQueryObject query) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Distinct.
	 *
	 * @param string the string
	 * @return the list
	 */
	public List distinct(String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
