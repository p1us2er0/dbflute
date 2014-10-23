/*
 * Copyright 2004-2014 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute;

import java.util.Set;

import org.seasar.dbflute.dbmeta.DBMeta;

/**
 * The interface of entity.
 * @author jflute
 */
public interface Entity {

    // ===================================================================================
    //                                                                              DBMeta
    //                                                                              ======
    /**
     * Get the target DB meta.
     * @return The instance of DBMeta type. (NotNull)
     */
    DBMeta getDBMeta();

    // ===================================================================================
    //                                                                          Table Name
    //                                                                          ==========
    /**
     * Get table DB name.
     * @return The string for name. (NotNull)
     */
    String getTableDbName();

    /**
     * Get table property name according to Java Beans rule.
     * @return The string for name. (NotNull)
     */
    String getTablePropertyName();

    // ===================================================================================
    //                                                                         Primary Key
    //                                                                         ===========
    /**
     * Does it have the value of primary keys?
     * @return The determination, true or false. (if all PK values are not null, returns true)
     */
    boolean hasPrimaryKeyValue();

    /**
     * Get the properties of specified unique columns as unique-driven.
     * @return The set of property name for specified unique columns. (NotNull)
     */
    Set<String> myuniqueDrivenProperties(); // prefix 'my' not to show when uniqueBy() completion

    // ===================================================================================
    //                                                                 Modified Properties
    //                                                                 ===================
    // -----------------------------------------------------
    //                                              Modified
    //                                              --------
    /**
     * Get the set of modified properties. (basically for Framework) <br />
     * The properties needs to be according to Java Beans rule.
     * @return The set of property name for modified columns. (NotNull)
     */
    Set<String> mymodifiedProperties();

    /**
     * Clear the information of modified properties. (basically for Framework)
     */
    void clearModifiedInfo();

    /**
     * Does it have modifications of property names. (basically for Framework)
     * @return The determination, true or false.
     */
    boolean hasModification();

    // -----------------------------------------------------
    //                                             Specified
    //                                             ---------
    /**
     * Copy to modified properties to specified properties. <br />
     * It means non-specified columns are checked
     */
    void modifiedToSpecified();

    /**
     * Get the set of specified properties. (basically for Framework) <br />
     * The properties needs to be according to Java Beans rule.
     * @return The set of property name for specified columns, read-only. (NotNull: if empty, no check)
     */
    Set<String> myspecifiedProperties(); // 'my' take on unique-driven

    /**
     * Clear the information of specified properties. (basically for Framework) <br />
     * It means no check of access to non-specified columns.
     */
    void clearSpecifiedInfo();

    // ===================================================================================
    //                                                                     Birthplace Mark
    //                                                                     ===============
    /**
     * Mark as select that means the entity is created by DBFlute select process. (basically for Framework)
     */
    void markAsSelect();

    /**
     * Is the entity created by DBFlute select process? (basically for Framework)
     * @return The determination, true or false.
     */
    boolean createdBySelect();

    // ===================================================================================
    //                                                                    Extension Method
    //                                                                    ================
    /**
     * Calculate the hash-code, which is a default hash code, to identify the instance.
     * @return The hash-code from super.hashCode().
     */
    int instanceHash();

    /**
     * Convert the entity to display string with relation information.
     * @return The display string of basic informations with one-nested relation values. (NotNull)
     */
    String toStringWithRelation();

    /**
     * Build display string flexibly.
     * @param name The name for display. (NullAllowed: If it's null, it does not have a name)
     * @param column Does it contains column values or not?
     * @param relation Does it contains relation existences or not?
     * @return The display string for this entity. (NotNull)
     */
    String buildDisplayString(String name, boolean column, boolean relation);
}
