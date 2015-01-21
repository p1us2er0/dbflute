/*
 * Copyright 2014-2015 the original author or authors.
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
package org.seasar.dbflute.logic.generate.refresh;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.seasar.dbflute.util.Srl;

/**
 * The request of (Eclipse's) refresh resource. <br>
 * You can refresh automatically by this.
 * <pre>
 * DfRefreshResourceRequest request
 *     = new DfRefreshResourceRequest(projectNameList, requestUrl);
 * request.refreshResources();
 * </pre>
 * @author jflute
 */
public class DfBackportRefreshResourceRequest {

    // ===================================================================================
    //                                                                          Definition
    //                                                                          ==========
    /** The mark of auto-detect, e.g. searching Eclipse .project file. (NotNull) */
    public static final String AUTO_DETECT_MARK = "$$AutoDetect$$"; // basically for engine

    /** The key of result map for response body. (NotNull) */
    public static final String KEY_BODY = "body";

    /** The key of result map for header fields. (NotNull) */
    public static final String KEY_HEADER_FIELDS = "headerFields";

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    protected final List<String> _projectNameList;
    protected final String _requestUrl;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    /**
     * @param projectNameList The list of project name for refresh. (NotNull)
     * @param requestUrl The request URL for refresh to synchronizer. (NotNull)
     */
    public DfBackportRefreshResourceRequest(List<String> projectNameList, String requestUrl) {
        if (projectNameList == null || projectNameList.isEmpty()) {
            String msg = "The argument 'projectNameList' should not be null or empty: " + projectNameList;
            throw new IllegalArgumentException(msg);
        }
        if (Srl.is_Null_or_TrimmedEmpty(requestUrl)) {
            String msg = "The argument 'requestUrl' should not be null or empty: " + requestUrl;
            throw new IllegalArgumentException(msg);
        }
        _projectNameList = projectNameList;
        _requestUrl = requestUrl;
    }

    // ===================================================================================
    //                                                                             Refresh
    //                                                                             =======
    /**
     * Refresh resources. (request to synchronizer)
     * @return The map of result. map:{projectName = map:{body = ...}} (NotNull)
     * @throws IOException When the refresh failed.
     */
    public Map<String, Map<String, Object>> refreshResources() throws IOException {
        final Map<String, Map<String, Object>> resultMap = new LinkedHashMap<String, Map<String, Object>>();
        for (String projectName : _projectNameList) {
            resultMap.put(projectName, doRefreshResources(projectName));
        }
        return resultMap;
    }

    protected Map<String, Object> doRefreshResources(String projectName) throws IOException {
        final StringBuilder sb = new StringBuilder();
        sb.append("refresh?").append(projectName).append("=INFINITE");

        final URL url = createRefreshRequestURL(sb.toString());
        if (url == null) {
            return null;
        }

        InputStream ins = null;
        try {
            final URLConnection conn = url.openConnection();
            conn.setReadTimeout(getRefreshRequestReadTimeout());
            conn.connect();
            ins = conn.getInputStream();
            final Map<String, Object> elementMap = new LinkedHashMap<String, Object>();
            final String body = buildResult(ins);
            elementMap.put(KEY_BODY, body);
            elementMap.put(KEY_HEADER_FIELDS, conn.getHeaderFields());
            handleConnectedConnection(conn, elementMap);
            return elementMap;
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException ignored) {}
            }
        }
    }

    protected String buildResult(InputStream ins) throws IOException, UnsupportedEncodingException {
        final byte[] body = new byte[1024];
        ins.read(body);
        return new String(body, "UTF-8");
    }

    protected void handleConnectedConnection(URLConnection conn, Map<String, Object> elementMap) throws IOException {
        // do nothing as default: for overriding
    }

    // ===================================================================================
    //                                                                    Refresh Resource
    //                                                                    ================
    protected URL createRefreshRequestURL(String path) throws MalformedURLException {
        String requestUrl = _requestUrl;
        if (!requestUrl.endsWith("/")) {
            requestUrl = requestUrl + "/";
        }
        return new URL(requestUrl + path);
    }

    protected int getRefreshRequestReadTimeout() {
        return 3 * 1000;
    }
}
