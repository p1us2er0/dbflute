package org.seasar.dbflute.helper.io.fileread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.seasar.dbflute.helper.mapstring.DfMapListStringImpl;

/**
 * @author jflute
 * @since 0.6.8 (2008/03/31 Monday)
 */
public class DfListStringFileReader {

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected boolean _saveInitialUnicodeBom;

    protected String _lineCommentMark = "#";

    // ===================================================================================
    //                                                                                Read
    //                                                                                ====
    public List<Object> readList(String path, String encoding) {
        final File file = new File(path);
        final StringBuilder sb = new StringBuilder();
        if (file.exists()) {
            java.io.FileInputStream fis = null;
            java.io.InputStreamReader ir = null;
            java.io.BufferedReader br = null;
            try {
                fis = new java.io.FileInputStream(file);
                ir = new java.io.InputStreamReader(fis, encoding);
                br = new java.io.BufferedReader(ir);

                int count = -1;
                while (true) {
                    ++count;

                    String lineString = br.readLine();
                    if (lineString == null) {
                        break;
                    }
                    if (count == 0 && !_saveInitialUnicodeBom) {
                        lineString = removeInitialUnicodeBomIfNeeds(encoding, lineString);
                    }
                    if (lineString.trim().length() == 0) {
                        continue;
                    }
                    // If the line is comment...
                    if (_lineCommentMark != null && lineString.trim().startsWith(_lineCommentMark)) {
                        continue;
                    }
                    sb.append(lineString);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (sb.toString().trim().length() == 0) {
            return new ArrayList<Object>();
        }
        final DfMapListStringImpl mapListString = new DfMapListStringImpl();
        return mapListString.generateList(sb.toString());
    }

    protected String removeInitialUnicodeBomIfNeeds(String encoding, String value) {
        if ("UTF-8".equalsIgnoreCase(encoding) && value.length() > 0 && value.charAt(0) == '\uFEFF') {
            value = value.substring(1);
        }
        return value;
    }

    // ===================================================================================
    //                                                                            Accessor
    //                                                                            ========
    public void setSaveInitialUnicodeBom(boolean saveInitialUnicodeBom) {
        _saveInitialUnicodeBom = saveInitialUnicodeBom;
    }

    public void setLineCommentMark(String lineCommentMark) {
        _lineCommentMark = lineCommentMark;
    }
}