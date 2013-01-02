package org.yajul.util;

/**
 * Used by IdMap so it can get the id (surrogate key) from the entity.   Implemented
 * by entities (and pojos) that can be used with IdMap.
 * <br>
 * User: josh
 * Date: Aug 29, 2008
 * Time: 9:36:11 AM
 */
public interface EntityWithId<K>
{
    /**
     * Returns the id of the entity.
     * @return the id of the entity.
     */
    K getId();
}
