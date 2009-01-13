package org.dbflute.bhv;

import java.util.List;

import org.dbflute.Entity;


/**
 * The interface of entity list setupper.
 * @param <ENTITY> The type of entity.
 * @author DBFlute(AutoGenerator)
 */
public interface EntityListSetupper<ENTITY extends Entity> {

    /**
     * Set up the list of entity.
     * @param entityList The list of entity. (NotNull)
     */
    public void setup(List<ENTITY> entityList);
}
