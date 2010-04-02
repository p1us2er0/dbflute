package org.seasar.dbflute.helper.dataset.states;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.dbflute.helper.dataset.DfDataRow;

/**
 * {Refers to S2Container and Extends it}
 * @author jflute
 * @since 0.8.3 (2008/10/28 Tuesday)
 */
public abstract class DfDtsAbstractRowState implements DfDtsRowState {

    /** Log instance. */
    private static final Log _log = LogFactory.getLog(DfDtsAbstractRowState.class);

    DfDtsAbstractRowState() {
    }

    public void update(DataSource dataSource, DfDataRow row) {
        final DfDtsSqlContext ctx = getSqlContext(row);
        execute(dataSource, ctx.getSql(), ctx.getArgs(), ctx.getArgTypes(), row);
    }

    protected void execute(DataSource dataSource, String sql, Object[] args, Class<?>[] argTypes, DfDataRow row) {
        final String tableName = row.getTable().getTableName();
        final Connection conn = getConnection(dataSource);
        try {
            final PreparedStatement ps = prepareStatement(conn, sql);
            try {
                _log.info(getSql4Log(tableName, Arrays.asList(args)));
                bindArgs(ps, args, argTypes);
                ps.executeUpdate();
            } catch (SQLException e) {
                String msg = "The SQL threw the exception: " + sql;
                throw new IllegalStateException(msg, e);
            } finally {
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (SQLException ignored) {
                    }
                }
            }
        } finally {
            close(conn);
        }
    }

    protected String getSql4Log(String tableName, final List<? extends Object> bindParameters) {
        String bindParameterString = bindParameters.toString();
        bindParameterString = bindParameterString.substring(1, bindParameterString.length() - 1);
        return tableName + ":{" + bindParameterString + "}";
    }

    protected void bindArgs(PreparedStatement ps, Object[] args, Class<?>[] argTypes) throws SQLException {
        if (args == null) {
            return;
        }
        for (int i = 0; i < args.length; ++i) {
            final Object value = args[i];
            final Class<?> type = argTypes[i];
            final int parameterIndex = (i + 1);
            if (String.class.isAssignableFrom(type)) {
                if (value != null) {
                    if (isTimestampValue((String) value)) {
                        final Timestamp timestamp = getTimestampValue((String) value);
                        ps.setTimestamp(parameterIndex, timestamp);
                    } else {
                        ps.setString(parameterIndex, (String) value);
                    }
                } else {
                    ps.setNull(parameterIndex, Types.VARCHAR);
                }
            } else if (Number.class.isAssignableFrom(type)) {
                if (value != null) {
                    ps.setBigDecimal(parameterIndex, new BigDecimal(value.toString()));
                } else {
                    ps.setNull(parameterIndex, Types.NUMERIC);
                }
            } else if (java.util.Date.class.isAssignableFrom(type)) {
                if (value != null) {
                    if (value instanceof String) {
                        final Timestamp timestamp = getTimestampValue((String) value);
                        ps.setTimestamp(parameterIndex, timestamp);
                    } else {
                        if (value instanceof Timestamp) {
                            ps.setTimestamp(parameterIndex, (Timestamp) value);
                        } else {
                            ps.setDate(parameterIndex, new java.sql.Date(((java.util.Date) value).getTime()));
                        }
                    }
                } else {
                    ps.setNull(parameterIndex, Types.DATE);
                }
            } else {
                if (value != null) {
                    ps.setObject(parameterIndex, value);
                } else {
                    ps.setNull(parameterIndex, Types.VARCHAR);
                }
            }
        }
    }

    protected boolean isTimestampValue(String value) {
        if (value == null) {
            return false;
        }
        value = filterTimestampValue(value);
        try {
            Timestamp.valueOf(value);
            return true;
        } catch (RuntimeException e) {
        }
        return false;
    }

    protected Timestamp getTimestampValue(String value) {
        final String filteredTimestampValue = filterTimestampValue(value);
        try {
            return Timestamp.valueOf(filteredTimestampValue);
        } catch (RuntimeException e) {
            String msg = "The value cannot be convert to timestamp:";
            msg = msg + " value=" + value + " filtered=" + filteredTimestampValue;
            throw new IllegalStateException(msg, e);
        }
    }

    protected String filterTimestampValue(String value) {
        value = value.trim();
        if (value.indexOf("/") == 4 && value.lastIndexOf("/") == 7) {
            value = value.replaceAll("/", "-");
        }
        if (value.indexOf("-") == 4 && value.lastIndexOf("-") == 7) {
            if (value.length() == "2007-07-09".length()) {
                value = value + " 00:00:00";
            }
        }
        return value;
    }

    protected abstract DfDtsSqlContext getSqlContext(DfDataRow row);

    private static Connection getConnection(DataSource dataSource) {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static PreparedStatement prepareStatement(Connection conn, String sql) {
        try {
            return conn.prepareStatement(sql);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void close(Connection conn) {
        if (conn == null)
            return;
        try {
            conn.close();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}