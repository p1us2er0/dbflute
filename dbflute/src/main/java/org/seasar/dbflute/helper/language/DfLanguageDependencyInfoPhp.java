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
package org.seasar.dbflute.helper.language;

import java.io.File;

import org.seasar.dbflute.helper.language.grammar.DfGrammarInfo;
import org.seasar.dbflute.helper.language.grammar.DfGrammarInfoPhp;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaData;
import org.seasar.dbflute.helper.language.metadata.LanguageMetaDataPhp;
import org.seasar.dbflute.helper.language.properties.DfDBFluteDiconInfo;
import org.seasar.dbflute.helper.language.properties.DfDBFluteDiconInfoPhp;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefault;
import org.seasar.dbflute.helper.language.properties.DfGeneratedClassPackageDefaultPhp;

/**
 * @author jflute
 */
public class DfLanguageDependencyInfoPhp implements DfLanguageDependencyInfo {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    protected static final String PATH_MAVEN_SRC_MAIN_PHP = "src/main/php";

    // ===================================================================================
    //                                                                               Basic
    //                                                                               =====
    public String getLanguageTitle() {
        return "Php";
    }

    // ===================================================================================
    //                                                                    Program Handling
    //                                                                    ================
    public DfGrammarInfo getGrammarInfo() {
        return new DfGrammarInfoPhp();
    }

    public String getIntegerConvertExpression(String value) {
        return value;
    }

    public String getSequenceType() {
        return "";
    }

    public DfDBFluteDiconInfo getDBFluteDiconInfo() {
        return new DfDBFluteDiconInfoPhp();
    }

    public LanguageMetaData createLanguageMetaData() {
        return new LanguageMetaDataPhp();
    }

    // ===================================================================================
    //                                                                 Compile Environment
    //                                                                 ===================
    public String getMainProgramDirectory() {
        return PATH_MAVEN_SRC_MAIN_PHP;
    }

    public String getMainResourceDirectory() {
        return getMainProgramDirectory();
    }

    public boolean isCompileTargetFile(File file) {
        return true;
    }

    public boolean isFlatOrOmitDirectorySupported() {
        return false;
    }

    // ===================================================================================
    //                                                                Generate Environment
    //                                                                ====================
    public String getGenerateControl() {
        throw new UnsupportedOperationException("Unsupported language Php");
    }

    public String getGenerateControlBhvAp() {
        throw new UnsupportedOperationException("Unsupported language Php");
    }

    public String getSql2EntityControl() {
        throw new UnsupportedOperationException("Unsupported language Php");
    }

    public String getOutsideSqlDirectory() {
        return getMainProgramDirectory();
    }

    public String convertToSecondaryOutsideSqlDirectory(String sqlDirectory) {
        return null; // no secondary
    }

    public String getGenerateOutputDirectory() {
        return "../" + getMainProgramDirectory();
    }

    public String getResourceOutputDirectory() {
        return "";
    }

    public String getTemplateFileExtension() {
        return "vmphp";
    }

    public DfGeneratedClassPackageDefault getGeneratedClassPackageInfo() {
        return new DfGeneratedClassPackageDefaultPhp();
    }
}
