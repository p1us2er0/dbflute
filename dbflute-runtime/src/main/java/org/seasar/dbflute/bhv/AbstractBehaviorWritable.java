/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
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
package org.seasar.dbflute.bhv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.seasar.dbflute.Entity;
import org.seasar.dbflute.bhv.batch.TokenFileReflectionFailure;
import org.seasar.dbflute.bhv.batch.TokenFileReflectionOption;
import org.seasar.dbflute.bhv.batch.TokenFileReflectionResult;
import org.seasar.dbflute.bhv.core.CommonColumnAutoSetupper;
import org.seasar.dbflute.bhv.core.command.AbstractEntityCommand;
import org.seasar.dbflute.bhv.core.command.AbstractListEntityCommand;
import org.seasar.dbflute.bhv.core.command.BatchDeleteEntityCommand;
import org.seasar.dbflute.bhv.core.command.BatchDeleteNonstrictEntityCommand;
import org.seasar.dbflute.bhv.core.command.BatchInsertEntityCommand;
import org.seasar.dbflute.bhv.core.command.BatchUpdateEntityCommand;
import org.seasar.dbflute.bhv.core.command.BatchUpdateNonstrictEntityCommand;
import org.seasar.dbflute.bhv.core.command.DeleteEntityCommand;
import org.seasar.dbflute.bhv.core.command.DeleteNonstrictEntityCommand;
import org.seasar.dbflute.bhv.core.command.InsertEntityCommand;
import org.seasar.dbflute.bhv.core.command.QueryDeleteCBCommand;
import org.seasar.dbflute.bhv.core.command.QueryUpdateEntityCBCommand;
import org.seasar.dbflute.bhv.core.command.UpdateEntityCommand;
import org.seasar.dbflute.bhv.core.command.UpdateNonstrictEntityCommand;
import org.seasar.dbflute.cbean.ConditionBean;
import org.seasar.dbflute.dbmeta.DBMeta;
import org.seasar.dbflute.exception.EntityAlreadyDeletedException;
import org.seasar.dbflute.exception.EntityAlreadyUpdatedException;
import org.seasar.dbflute.exception.IllegalBehaviorStateException;
import org.seasar.dbflute.exception.OptimisticLockColumnValueNullException;
import org.seasar.dbflute.exception.factory.ExceptionMessageBuilder;
import org.seasar.dbflute.helper.mapstring.impl.ColumnValueMapStringImpl;
import org.seasar.dbflute.helper.token.file.FileToken;
import org.seasar.dbflute.helper.token.file.FileTokenizingCallback;
import org.seasar.dbflute.helper.token.file.FileTokenizingHeaderInfo;
import org.seasar.dbflute.helper.token.file.FileTokenizingOption;
import org.seasar.dbflute.helper.token.file.FileTokenizingRowResource;
import org.seasar.dbflute.helper.token.file.impl.FileTokenImpl;

/**
 * The abstract class of writable behavior.
 * @author jflute
 */
public abstract class AbstractBehaviorWritable extends AbstractBehaviorReadable implements BehaviorWritable {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** Map-string map-mark. */
    private static final String MAP_STRING_MAP_MARK = "map:";

    /** Map-string start-brace. */
    private static final String MAP_STRING_START_BRACE = "@{";

    /** Map-string end-brace. */
    private static final String MAP_STRING_END_BRACE = "@}";

    /** Map-string delimiter. */
    private static final String MAP_STRING_DELIMITER = "@;";

    /** Map-string equal. */
    private static final String MAP_STRING_EQUAL = "@=";

    // =====================================================================================
    //                                                                             Attribute
    //                                                                             =========
    protected CommonColumnAutoSetupper _commonColumnAutoSetupper;

    // ===================================================================================
    //                                                                       Entity Update
    //                                                                       =============
    // -----------------------------------------------------
    //                                                Create
    //                                                ------
    /**
     * Create.
     * @param entity Entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void create(Entity entity) {
        doCreate(entity);
    }

    protected abstract void doCreate(Entity entity);

    // -----------------------------------------------------
    //                                                Modify
    //                                                ------
    /**
     * Modify.
     * @param entity Entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void modify(Entity entity) {
        doModify(entity);
    }

    protected abstract void doModify(Entity entity);

    /**
     * Modify non strict.
     * @param entity Entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyExistsException When the entity already exists. (Unique Constraint Violation)
     */
    public void modifyNonstrict(Entity entity) {
        doModifyNonstrict(entity);
    }

    protected abstract void doModifyNonstrict(Entity entity);

    // -----------------------------------------------------
    //                                      Create or Modify
    //                                      ----------------
    /**
     * {@inheritDoc}
     */
    public void createOrModify(org.seasar.dbflute.Entity entity) {
        assertEntityNotNull(entity);
        doCreateOrUpdate(entity);
    }

    protected abstract void doCreateOrUpdate(Entity entity);

    /**
     * {@inheritDoc}
     */
    public void createOrModifyNonstrict(org.seasar.dbflute.Entity entity) {
        assertEntityNotNull(entity);
        doCreateOrUpdateNonstrict(entity);
    }

    protected abstract void doCreateOrUpdateNonstrict(Entity entity);

    // -----------------------------------------------------
    //                                                Remove
    //                                                ------
    /**
     * Remove.
     * @param entity Entity. (NotNull)
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException When the entity has already been updated.
     * @exception org.seasar.dbflute.exception.EntityAlreadyDeletedException When the entity has already been deleted.
     * @exception org.seasar.dbflute.exception.EntityDuplicatedException When the entity has been duplicated.
     */
    public void remove(org.seasar.dbflute.Entity entity) {
        assertEntityNotNull(entity);
        doRemove(entity);
    }

    protected abstract void doRemove(Entity entity);

    // ===================================================================================
    //                                                       Entity Update Internal Helper
    //                                                       =============================
    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    protected <ENTITY extends Entity> void helpUpdateInternally(ENTITY entity, InternalUpdateCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        assertEntityHasVersionNoValue(entity);
        assertEntityHasUpdateDateValue(entity);
        final int updatedCount = callback.callbackDelegateUpdate(entity);
        if (updatedCount == 0) {
            throwUpdateEntityAlreadyDeletedException(entity);
        } else if (updatedCount > 1) {
            throwUpdateEntityDuplicatedException(entity, updatedCount);
        }
    }

    protected static interface InternalUpdateCallback<ENTITY extends Entity> {
        public int callbackDelegateUpdate(ENTITY entity);
    }

    protected <ENTITY extends Entity> void helpUpdateNonstrictInternally(ENTITY entity,
            InternalUpdateNonstrictCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        final int updatedCount = callback.callbackDelegateUpdateNonstrict(entity);
        if (updatedCount == 0) {
            throwUpdateEntityAlreadyDeletedException(entity);
        } else if (updatedCount > 1) {
            throwUpdateEntityDuplicatedException(entity, updatedCount);
        }
    }

    protected static interface InternalUpdateNonstrictCallback<ENTITY extends Entity> {
        public int callbackDelegateUpdateNonstrict(ENTITY entity);
    }

    protected <ENTITY extends Entity> void throwUpdateEntityAlreadyDeletedException(ENTITY entity) {
        createBhvExThrower().throwUpdateEntityAlreadyDeletedException(entity);
    }

    protected <ENTITY extends Entity> void throwUpdateEntityDuplicatedException(ENTITY entity, int count) {
        createBhvExThrower().throwUpdateEntityDuplicatedException(entity, count);
    }

    // -----------------------------------------------------
    //                                        InsertOrUpdate
    //                                        --------------
    protected <ENTITY extends Entity, CB_TYPE extends ConditionBean> void helpInsertOrUpdateInternally(ENTITY entity,
            InternalInsertOrUpdateCallback<ENTITY, CB_TYPE> callback) {
        assertEntityNotNull(entity);
        if (!entity.hasPrimaryKeyValue()) {
            callback.callbackInsert(entity);
        } else {
            RuntimeException exception = null;
            try {
                callback.callbackUpdate(entity);
            } catch (org.seasar.dbflute.exception.EntityAlreadyUpdatedException e) {
                if (e.getRows() == 0) {
                    exception = e;
                }
            } catch (org.seasar.dbflute.exception.EntityAlreadyDeletedException e) {
                exception = e;
            } catch (OptimisticLockColumnValueNullException e) {
                exception = e;
            }
            if (exception != null) {
                final CB_TYPE cb = callback.callbackNewMyConditionBean();
                cb.acceptPrimaryKeyMapString(getDBMeta().extractPrimaryKeyMapString(entity));
                if (callback.callbackSelectCount(cb) == 0) {
                    callback.callbackInsert(entity);
                } else {
                    throw exception;
                }
            }
        }
    }

    protected static interface InternalInsertOrUpdateCallback<ENTITY extends Entity, CB_TYPE extends ConditionBean> {
        public void callbackInsert(ENTITY entity);

        public void callbackUpdate(ENTITY entity);

        public CB_TYPE callbackNewMyConditionBean();

        public int callbackSelectCount(CB_TYPE cb);
    }

    protected <ENTITY extends Entity> void helpInsertOrUpdateInternally(ENTITY entity,
            InternalInsertOrUpdateNonstrictCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        if (!entity.hasPrimaryKeyValue()) {
            callback.callbackInsert(entity);
        } else {
            try {
                callback.callbackUpdateNonstrict(entity);
            } catch (EntityAlreadyUpdatedException e) {
                callback.callbackInsert(entity);
            } catch (EntityAlreadyDeletedException e) {
                callback.callbackInsert(entity);
            }
        }
    }

    protected static interface InternalInsertOrUpdateNonstrictCallback<ENTITY extends Entity> {
        public void callbackInsert(ENTITY entity);

        public void callbackUpdateNonstrict(ENTITY entity);
    }

    // -----------------------------------------------------
    //                                                Delete
    //                                                ------
    protected <ENTITY extends Entity> void helpDeleteInternally(ENTITY entity, InternalDeleteCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        assertEntityHasVersionNoValue(entity);
        assertEntityHasUpdateDateValue(entity);
        final int deletedCount = callback.callbackDelegateDelete(entity);
        if (deletedCount == 0) {
            throwUpdateEntityAlreadyDeletedException(entity);
        } else if (deletedCount > 1) {
            throwUpdateEntityDuplicatedException(entity, deletedCount);
        }
    }

    protected static interface InternalDeleteCallback<ENTITY extends Entity> {
        public int callbackDelegateDelete(ENTITY entity);
    }

    protected <ENTITY extends Entity> void helpDeleteNonstrictInternally(ENTITY entity,
            InternalDeleteNonstrictCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        final int deletedCount = callback.callbackDelegateDeleteNonstrict(entity);
        if (deletedCount == 0) {
            throwUpdateEntityAlreadyDeletedException(entity);
        } else if (deletedCount > 1) {
            throwUpdateEntityDuplicatedException(entity, deletedCount);
        }
    }

    protected static interface InternalDeleteNonstrictCallback<ENTITY extends Entity> {
        public int callbackDelegateDeleteNonstrict(ENTITY entity);
    }

    protected <ENTITY extends Entity> void helpDeleteNonstrictIgnoreDeletedInternally(ENTITY entity,
            InternalDeleteNonstrictIgnoreDeletedCallback<ENTITY> callback) {
        assertEntityNotNull(entity);
        final int deletedCount = callback.callbackDelegateDeleteNonstrict(entity);
        if (deletedCount == 0) {
            return;
        } else if (deletedCount > 1) {
            throwUpdateEntityDuplicatedException(entity, deletedCount);
        }
    }

    protected static interface InternalDeleteNonstrictIgnoreDeletedCallback<ENTITY extends Entity> {
        public int callbackDelegateDeleteNonstrict(ENTITY entity);
    }

    // ===================================================================================
    //                                                                         Lump Update
    //                                                                         ===========
    /**
     * Lump create the list.
     * @param entityList Entity list. (NotNull and NotEmpty)
     * @return The array of created count.
     */
    public int[] lumpCreate(List<Entity> entityList) {
        assertListNotNullAndNotEmpty(entityList);
        return callCreateList(entityList);
    }

    /**
     * Lump Modify the list.
     * @param entityList Entity list. (NotNull and NotEmpty)
     * @return Modified count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException If s2dao's version is over 1.0.47 (contains 1.0.47).
     */
    public int[] lumpModify(List<Entity> entityList) {
        assertListNotNullAndNotEmpty(entityList);
        return callModifyList(entityList);
    }

    /**
     * Lump remove the list.
     * @param entityList Entity list. (NotNull and NotEmpty)
     * @return Removed count.
     * @exception org.seasar.dbflute.exception.EntityAlreadyUpdatedException If s2dao's version is over 1.0.47 (contains 1.0.47).
     */
    public int[] lumpRemove(List<Entity> entityList) {
        assertListNotNullAndNotEmpty(entityList);
        return callRemoveList(entityList);
    }

    /**
     * Inject sequence to primary key if it needs.
     * @param entity Entity. (NotNull)
     */
    protected void injectSequenceToPrimaryKeyIfNeeds(Entity entity) {
        final DBMeta dbmeta = entity.getDBMeta();
        if (!dbmeta.hasSequence() || dbmeta.hasTwoOrMorePrimaryKeys() || entity.hasPrimaryKeyValue()) {
            return;
        }
        // basically property(column) type is same as next value type
        // so there is NOT type conversion cost when writing to the entity
        dbmeta.getPrimaryUniqueInfo().getFirstColumn().write(entity, readNextVal());
    }

    // =====================================================================================
    //                                                                            Token File
    //                                                                            ==========
    /**
     * Get the executor of token file reflection.
     * @return The executor of token file output. (NotNull)
     */
    public TokenFileReflectionExecutor tokenFileReflection() {
        return new TokenFileReflectionExecutor();
    }

    /**
     * The executor of token file reflection.
     */
    public class TokenFileReflectionExecutor {

        /**
         * Reflect(insert or update) token file to this table. <br />
         * The supported column types are String, Number and Date.
         * @param filename The name of the file. (NotNull and NotEmpty)
         * @param tokenFileReflectionOption token-file-reflection-option. (NotNull and Required{delimiter and encoding})
         * @return The result of token file reflection. (NotNull)
         * @throws java.io.FileNotFoundException The file is not found.
         * @throws java.io.IOException The IO exception occurred.
         */
        public TokenFileReflectionResult reflectTokenFile(String filename,
                TokenFileReflectionOption tokenFileReflectionOption) throws java.io.FileNotFoundException, IOException {
            assertStringNotNullAndNotTrimmedEmpty("filename", filename);
            assertFileTokenReflectionOption(tokenFileReflectionOption);

            final TokenFileReflectionResult result = buildTokenFileReflectionResult();
            final FileTokenizingCallback fileTokenizingCallback = buildFileTokenReflectionFileTokenizingCallback(
                    tokenFileReflectionOption, result);
            final FileTokenizingOption fileTokenizingOption = buildFileTokenReflectionFileTokenizingOption(tokenFileReflectionOption);
            final FileToken fileToken = new FileTokenImpl();
            fileToken.tokenize(filename, fileTokenizingCallback, fileTokenizingOption);
            return result;
        }

        /**
         * Reflect(insert or update) token file to this table. <br />
         * The supported column types are String, Number and Date.
         * @param inputStream The input stream. (NotNull and NotClosed)
         * @param tokenFileReflectionOption token-file-reflection-option. (NotNull and Required{delimiter and encoding})
         * @return The result of token file reflection. (NotNull)
         * @throws java.io.FileNotFoundException The file is not found.
         * @throws java.io.IOException The IO exception occurred.
         */
        public TokenFileReflectionResult reflectTokenFile(java.io.InputStream inputStream,
                TokenFileReflectionOption tokenFileReflectionOption) throws java.io.FileNotFoundException, IOException {
            assertObjectNotNull("inputStream", inputStream);
            assertFileTokenReflectionOption(tokenFileReflectionOption);

            final TokenFileReflectionResult result = buildTokenFileReflectionResult();
            final FileTokenizingCallback fileTokenizingCallback = buildFileTokenReflectionFileTokenizingCallback(
                    tokenFileReflectionOption, result);
            final FileTokenizingOption fileTokenizingOption = buildFileTokenReflectionFileTokenizingOption(tokenFileReflectionOption);
            final FileToken fileToken = new FileTokenImpl();
            fileToken.tokenize(inputStream, fileTokenizingCallback, fileTokenizingOption);
            return result;
        }

        protected void assertFileTokenReflectionOption(TokenFileReflectionOption tokenFileReflectionOption) {
            assertObjectNotNull("tokenFileReflectionOption", tokenFileReflectionOption);

            final String encoding = tokenFileReflectionOption.getEncoding();
            final String delimiter = tokenFileReflectionOption.getDelimiter();
            assertStringNotNullAndNotTrimmedEmpty("encoding", encoding);
            assertObjectNotNull("delimiter", delimiter);
        }

        protected TokenFileReflectionResult buildTokenFileReflectionResult() {
            final TokenFileReflectionResult result = new TokenFileReflectionResult();
            final java.util.List<TokenFileReflectionFailure> failureList = new java.util.ArrayList<TokenFileReflectionFailure>();
            result.setFailureList(failureList);
            return result;
        }

        protected FileTokenizingCallback buildFileTokenReflectionFileTokenizingCallback(
                TokenFileReflectionOption tokenFileReflectionOption, final TokenFileReflectionResult result)
                throws java.io.FileNotFoundException, java.io.IOException {
            assertObjectNotNull("tokenFileReflectionOption", tokenFileReflectionOption);

            final String encoding = tokenFileReflectionOption.getEncoding();
            final String delimiter = tokenFileReflectionOption.getDelimiter();
            final boolean interruptIfError = tokenFileReflectionOption.isInterruptIfError();
            assertStringNotNullAndNotTrimmedEmpty("encoding", encoding);
            assertObjectNotNull("delimiter", delimiter);
            final java.util.List<TokenFileReflectionFailure> failureList = result.getFailureList();
            assertObjectNotNull("failureList", failureList);

            final FileTokenizingCallback fileTokenizingCallback = new FileTokenizingCallback() {
                public void handleRowResource(FileTokenizingRowResource fileTokenizingRowResource) {
                    final FileTokenizingHeaderInfo fileTokenizingHeaderInfo = fileTokenizingRowResource
                            .getFileTokenizingHeaderInfo();
                    final java.util.List<String> columnNameList = fileTokenizingHeaderInfo.getColumnNameList();
                    final java.util.List<String> valueList = fileTokenizingRowResource.getValueList();

                    // Set up columnNameList of result object.
                    if (result.getColumnNameList() == null) {
                        result.setColumnNameList(columnNameList);
                    }

                    Entity entity = null;
                    try {
                        // Create entity by the list of value composed of String.
                        entity = createEntityByStringValueList(columnNameList, valueList);

                        // Create or modify as non-strict.
                        doCreateOrUpdateNonstrict(entity);

                        // Increment successCount of result object.
                        result.incrementSuccessCount();
                    } catch (RuntimeException e) {
                        if (interruptIfError) {
                            throw e;
                        }
                        final TokenFileReflectionFailure failure = new TokenFileReflectionFailure();
                        failure.setColumnNameList(columnNameList);
                        failure.setValueList(valueList);
                        failure.setRowString(fileTokenizingRowResource.getRowString());
                        failure.setRowNumber(fileTokenizingRowResource.getRowNumber());
                        failure.setLineNumber(fileTokenizingRowResource.getLineNumber());
                        if (entity != null) {
                            failure.setEntity(entity);
                        }
                        failure.setException(e);
                        failureList.add(failure);
                    }
                }
            };
            return fileTokenizingCallback;
        }

        protected Entity createEntityByStringValueList(java.util.List<String> columnNameList,
                java.util.List<String> valueList) {
            final ColumnValueMapStringImpl builder = new ColumnValueMapStringImpl();
            builder.setMsMapMark(MAP_STRING_MAP_MARK);
            builder.setMsStartBrace(MAP_STRING_START_BRACE);
            builder.setMsEndBrace(MAP_STRING_END_BRACE);
            builder.setMsDelimiter(MAP_STRING_DELIMITER);
            builder.setMsEqual(MAP_STRING_EQUAL);
            builder.setColumnNameList(columnNameList);
            final String mapString = builder.buildFromList(valueList);

            final Entity entity = getDBMeta().newEntity();
            getDBMeta().acceptColumnValueMapString(entity, mapString);
            return entity;
        }

        protected FileTokenizingOption buildFileTokenReflectionFileTokenizingOption(
                TokenFileReflectionOption tokenFileReflectionOption) throws java.io.FileNotFoundException,
                java.io.IOException {
            assertObjectNotNull("tokenFileReflectionOption", tokenFileReflectionOption);

            final String encoding = tokenFileReflectionOption.getEncoding();
            final String delimiter = tokenFileReflectionOption.getDelimiter();
            assertStringNotNullAndNotTrimmedEmpty("encoding", encoding);
            assertObjectNotNull("delimiter", delimiter);

            final FileTokenizingOption fileTokenizingOption = new FileTokenizingOption();
            fileTokenizingOption.setEncoding(encoding);
            fileTokenizingOption.setDelimiter(delimiter);
            if (tokenFileReflectionOption.isHandleEmptyAsNull()) {
                fileTokenizingOption.handleEmptyAsNull();
            }
            return fileTokenizingOption;
        }
    }

    // =====================================================================================
    //                                                                        Process Method
    //                                                                        ==============
    // -----------------------------------------------------
    //                                                Insert
    //                                                ------
    /**
     * Process before insert.
     * @param entity Entity that the type is entity interface. (NotNull)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean processBeforeInsert(Entity entity) {
        if (!determineExecuteInsert(entity)) {
            return false;
        }
        assertEntityNotNull(entity);// If this table use identity, the entity does not have primary-key.
        frameworkFilterEntityOfInsert(entity);
        filterEntityOfInsert(entity);
        assertEntityOfInsert(entity);
        return true;
    }

    /**
     * Determine execution of insert.
     * @param entity Entity. (NotNull)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean determineExecuteInsert(Entity entity) {
        return true;
    }

    /**
     * {Framework Method} Filter the entity of insert.
     * @param targetEntity Target entity that the type is entity interface. (NotNull)
     */
    protected void frameworkFilterEntityOfInsert(Entity targetEntity) {
        injectSequenceToPrimaryKeyIfNeeds(targetEntity);
        setupCommonColumnOfInsertIfNeeds(targetEntity);
    }

    /**
     * Set up common columns of insert if it needs.
     * @param targetEntity Target entity that the type is entity interface. (NotNull)
     */
    protected void setupCommonColumnOfInsertIfNeeds(Entity targetEntity) {
        final CommonColumnAutoSetupper setupper = getCommonColumnAutoSetupper();
        assertCommonColumnAutoSetupperNotNull();
        setupper.handleCommonColumnOfInsertIfNeeds(targetEntity);
    }

    private void assertCommonColumnAutoSetupperNotNull() {
        if (_commonColumnAutoSetupper != null) {
            return;
        }
        final ExceptionMessageBuilder br = createExceptionMessageBuilder();
        br.addNotice("Not found the auto set-upper of common column in the behavior!");
        br.addItem("Advice");
        br.addElement("Please confirm the definition of the set-upper at your component configuration of DBFlute.");
        br.addItem("Behavior");
        br.addElement("Behavior for " + getTableDbName());
        br.addItem("Attribute");
        br.addElement("behaviorCommandInvoker   : " + _behaviorCommandInvoker);
        br.addElement("behaviorSelector         : " + _behaviorSelector);
        br.addElement("commonColumnAutoSetupper : " + _commonColumnAutoSetupper);
        final String msg = br.buildExceptionMessage();
        throw new IllegalBehaviorStateException(msg);
    }

    /**
     * Filter the entity of insert.
     * @param targetEntity Target entity that the type is entity interface. (NotNull)
     */
    protected void filterEntityOfInsert(Entity targetEntity) {
    }

    /**
     * Assert the entity of insert.
     * @param entity Entity that the type is entity interface. (NotNull)
     */
    protected void assertEntityOfInsert(Entity entity) {
    }

    // -----------------------------------------------------
    //                                                Update
    //                                                ------
    /**
     * Process before update.
     * @param entity Entity that the type is entity interface. (NotNull)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean processBeforeUpdate(Entity entity) {
        if (!determineExecuteUpdate(entity)) {
            return false;
        }
        assertEntityNotNullAndHasPrimaryKeyValue(entity);
        frameworkFilterEntityOfUpdate(entity);
        filterEntityOfUpdate(entity);
        assertEntityOfUpdate(entity);
        return true;
    }

    /**
     * Determine execution of update.
     * @param entity Entity. (NotNull)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean determineExecuteUpdate(Entity entity) {
        return true;
    }

    /**
     * {Framework Method} Filter the entity of update.
     * @param targetEntity Target entity that the type is entity interface. (NotNull)
     */
    protected void frameworkFilterEntityOfUpdate(Entity targetEntity) {
        setupCommonColumnOfUpdateIfNeeds(targetEntity);
    }

    /**
     * Set up common columns of update if it needs.
     * @param targetEntity Target entity that the type is entity interface. (NotNull)
     */
    protected void setupCommonColumnOfUpdateIfNeeds(Entity targetEntity) {
        final CommonColumnAutoSetupper setupper = getCommonColumnAutoSetupper();
        assertCommonColumnAutoSetupperNotNull();
        setupper.handleCommonColumnOfUpdateIfNeeds(targetEntity);
    }

    /**
     * Filter the entity of update.
     * @param targetEntity Target entity that the type is entity interface. (NotNull)
     */
    protected void filterEntityOfUpdate(Entity targetEntity) {
    }

    /**
     * Assert the entity of update.
     * @param entity Entity that the type is entity interface. (NotNull)
     */
    protected void assertEntityOfUpdate(Entity entity) {
    }

    // -----------------------------------------------------
    //                                                Delete
    //                                                ------
    /**
     * Process before delete.
     * @param entity Entity that the type is entity interface. (NotNull)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean processBeforeDelete(Entity entity) {
        if (!determineExecuteDelete(entity)) {
            return false;
        }
        assertEntityNotNullAndHasPrimaryKeyValue(entity);
        frameworkFilterEntityOfDelete(entity);
        filterEntityOfDelete(entity);
        assertEntityOfDelete(entity);
        return true;
    }

    /**
     * Determine execution of delete.
     * @param entity Entity. (NotNull)
     * @return Execution Determination. (true: execute / false: non)
     */
    protected boolean determineExecuteDelete(Entity entity) {
        return true;
    }

    /**
     * {Framework Method} Filter the entity of delete.
     * @param targetEntity Target entity that the type is entity interface. (NotNull)
     */
    protected void frameworkFilterEntityOfDelete(Entity targetEntity) {
    }

    /**
     * Filter the entity of delete.
     * @param targetEntity Target entity that the type is entity interface. (NotNull)
     */
    protected void filterEntityOfDelete(Entity targetEntity) {
    }

    /**
     * Assert the entity of delete.
     * @param entity Entity that the type is entity interface. (NotNull)
     */
    protected void assertEntityOfDelete(Entity entity) {
    }

    /**
     * @param entityList Entity list that the type is entity interface. (NotNull)
     * @return Inserted count.
     */
    protected int[] callCreateList(List<Entity> entityList) {
        assertObjectNotNull("entityList", entityList);
        helpFilterBeforeInsertInternally(entityList);
        return doCreateList(entityList);
    }

    protected abstract int[] doCreateList(List<Entity> entityList);

    /**
     * @param entityList Entity list that the type is entity interface. (NotNull)
     * @return Updated count.
     */
    protected int[] callModifyList(List<Entity> entityList) {
        assertObjectNotNull("entityList", entityList);
        helpFilterBeforeUpdateInternally(entityList);
        return doModifyList(entityList);
    }

    protected abstract int[] doModifyList(List<Entity> entityList);

    /**
     * @param entityList Entity list that the type is entity interface. (NotNull)
     * @return Deleted count.
     */
    protected int[] callRemoveList(List<Entity> entityList) {
        assertObjectNotNull("entityList", entityList);
        helpFilterBeforeDeleteInternally(entityList);
        return doRemoveList(entityList);
    }

    protected abstract int[] doRemoveList(List<Entity> entityList);

    protected void assertEntityHasVersionNoValue(Entity entity) {
        if (!getDBMeta().hasVersionNo()) {
            return;
        }
        if (hasVersionNoValue(entity)) {
            return;
        }
        throwVersionNoValueNullException(entity);
    }

    protected void throwVersionNoValueNullException(Entity entity) {
        createBhvExThrower().throwVersionNoValueNullException(entity);
    }

    protected void assertEntityHasUpdateDateValue(Entity entity) {
        if (!getDBMeta().hasUpdateDate()) {
            return;
        }
        if (hasUpdateDateValue(entity)) {
            return;
        }
        throwUpdateDateValueNullException(entity);
    }

    protected void throwUpdateDateValueNullException(Entity entity) {
        createBhvExThrower().throwUpdateDateValueNullException(entity);
    }

    // ===================================================================================
    //                                                     Delegate Method Internal Helper
    //                                                     ===============================
    protected <ENTITY extends Entity> List<ENTITY> helpFilterBeforeInsertInternally(List<ENTITY> entityList) {
        final List<ENTITY> filteredList = new ArrayList<ENTITY>();
        for (final Iterator<ENTITY> ite = entityList.iterator(); ite.hasNext();) {
            final ENTITY entity = ite.next();
            if (!processBeforeInsert(entity)) {
                continue;
            }
            filteredList.add(entity);
        }
        return filteredList;
    }

    protected <ENTITY extends Entity> List<ENTITY> helpFilterBeforeUpdateInternally(List<ENTITY> entityList) {
        final List<ENTITY> filteredList = new ArrayList<ENTITY>();
        for (final Iterator<ENTITY> ite = entityList.iterator(); ite.hasNext();) {
            final ENTITY entity = ite.next();
            if (!processBeforeUpdate(entity)) {
                continue;
            }
            filteredList.add(entity);
        }
        return filteredList;
    }

    protected <ENTITY extends Entity> List<ENTITY> helpFilterBeforeDeleteInternally(List<ENTITY> entityList) {
        final List<ENTITY> filteredList = new ArrayList<ENTITY>();
        for (final Iterator<ENTITY> ite = entityList.iterator(); ite.hasNext();) {
            final ENTITY entity = ite.next();
            if (!processBeforeDelete(entity)) {
                continue;
            }
            filteredList.add(entity);
        }
        return filteredList;
    }

    // ===================================================================================
    //                                                                    Behavior Command
    //                                                                    ================
    protected InsertEntityCommand createInsertEntityCommand(Entity entity) {
        assertBehaviorCommandInvoker("createInsertEntityCommand");
        return xsetupEntityCommand(new InsertEntityCommand(), entity);
    }

    protected UpdateEntityCommand createUpdateEntityCommand(Entity entity) {
        assertBehaviorCommandInvoker("createUpdateEntityCommand");
        return xsetupEntityCommand(new UpdateEntityCommand(), entity);
    }

    protected UpdateNonstrictEntityCommand createUpdateNonstrictEntityCommand(Entity entity) {
        assertBehaviorCommandInvoker("createUpdateNonstrictEntityCommand");
        return xsetupEntityCommand(new UpdateNonstrictEntityCommand(), entity);
    }

    protected DeleteEntityCommand createDeleteEntityCommand(Entity entity) {
        assertBehaviorCommandInvoker("createDeleteEntityCommand");
        return xsetupEntityCommand(new DeleteEntityCommand(), entity);
    }

    protected DeleteNonstrictEntityCommand createDeleteNonstrictEntityCommand(Entity entity) {
        assertBehaviorCommandInvoker("createDeleteNonstrictEntityCommand");
        return xsetupEntityCommand(new DeleteNonstrictEntityCommand(), entity);
    }

    protected <COMMAND extends AbstractEntityCommand> COMMAND xsetupEntityCommand(COMMAND command, Entity entity) {
        command.setTableDbName(getTableDbName());
        _behaviorCommandInvoker.injectComponentProperty(command);
        command.setEntityType(entity.getClass());
        command.setEntity(entity);
        return command;
    }

    protected BatchInsertEntityCommand createBatchInsertEntityCommand(List<? extends Entity> entityList) {
        assertBehaviorCommandInvoker("createBatchInsertEntityCommand");
        return xsetupListEntityCommand(new BatchInsertEntityCommand(), entityList);
    }

    protected BatchUpdateEntityCommand createBatchUpdateEntityCommand(List<? extends Entity> entityList) {
        assertBehaviorCommandInvoker("createBatchUpdateEntityCommand");
        return xsetupListEntityCommand(new BatchUpdateEntityCommand(), entityList);
    }

    protected BatchUpdateNonstrictEntityCommand createBatchUpdateNonstrictEntityCommand(
            List<? extends Entity> entityList) {
        assertBehaviorCommandInvoker("createBatchUpdateNonstrictEntityCommand");
        return xsetupListEntityCommand(new BatchUpdateNonstrictEntityCommand(), entityList);
    }

    protected BatchDeleteEntityCommand createBatchDeleteEntityCommand(List<? extends Entity> entityList) {
        assertBehaviorCommandInvoker("createBatchDeleteEntityCommand");
        return xsetupListEntityCommand(new BatchDeleteEntityCommand(), entityList);
    }

    protected BatchDeleteNonstrictEntityCommand createBatchDeleteNonstrictEntityCommand(
            List<? extends Entity> entityList) {
        assertBehaviorCommandInvoker("createBatchDeleteNonstrictEntityCommand");
        return xsetupListEntityCommand(new BatchDeleteNonstrictEntityCommand(), entityList);
    }

    /**
     * @param <COMMAND> The type of behavior command for list entity.
     * @param command The command of behavior. (NotNull)
     * @param entityList The list of entity. (NotNull, NotEmpty)
     * @return The command of behavior. (NotNull)
     */
    protected <COMMAND extends AbstractListEntityCommand> COMMAND xsetupListEntityCommand(COMMAND command,
            List<? extends Entity> entityList) {
        if (entityList.isEmpty()) {
            String msg = "The argument 'entityList' should not be empty: " + entityList;
            throw new IllegalStateException(msg);
        }
        command.setTableDbName(getTableDbName());
        _behaviorCommandInvoker.injectComponentProperty(command);
        command.setEntityType(entityList.get(0).getClass()); // *The list should not be empty!
        command.setEntityList(entityList);
        return command;
    }

    protected QueryDeleteCBCommand createQueryDeleteCBCommand(ConditionBean cb) {
        assertBehaviorCommandInvoker("createQueryDeleteCBCommand");
        final QueryDeleteCBCommand cmd = new QueryDeleteCBCommand();
        cmd.setTableDbName(getTableDbName());
        _behaviorCommandInvoker.injectComponentProperty(cmd);
        cmd.setConditionBeanType(cb.getClass());
        cmd.setConditionBean(cb);
        return cmd;
    }

    protected QueryUpdateEntityCBCommand createQueryUpdateEntityCBCommand(Entity entity, ConditionBean cb) {
        assertBehaviorCommandInvoker("createQueryUpdateEntityCBCommand");
        final QueryUpdateEntityCBCommand cmd = new QueryUpdateEntityCBCommand();
        cmd.setTableDbName(getTableDbName());
        _behaviorCommandInvoker.injectComponentProperty(cmd);
        cmd.setConditionBeanType(cb.getClass());
        cmd.setConditionBean(cb);
        cmd.setEntityType(entity.getClass());
        cmd.setEntity(entity);
        return cmd;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    /**
     * Get the auto set-upper of common column.
     * @return The auto set-upper of common column. (Nullable: But normally NotNull)
     */
    protected CommonColumnAutoSetupper getCommonColumnAutoSetupper() {
        return _commonColumnAutoSetupper;
    }

    /**
     * Set the auto set-upper of common column.
     * @param commonColumnAutoSetupper The auto set-upper of common column. (NotNull)
     */
    public void setCommonColumnAutoSetupper(CommonColumnAutoSetupper commonColumnAutoSetupper) {
        this._commonColumnAutoSetupper = commonColumnAutoSetupper;
    }
}
