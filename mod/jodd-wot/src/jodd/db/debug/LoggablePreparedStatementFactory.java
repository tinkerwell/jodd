// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.debug;

import jodd.db.DbSqlException;
import jodd.proxetta.MethodInfo;
import jodd.proxetta.ProxyAspect;
import jodd.proxetta.asm.ProxettaAsmUtil;
import jodd.proxetta.impl.WrapperProxettaBuilder;
import jodd.proxetta.impl.WrapperProxetta;
import jodd.proxetta.pointcuts.ProxyPointcutSupport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Factory for loggable prepared statements - a <code>PreparedStatement</code> with added logging capability.
 * <p>
 * In addition to the methods declared in <code>PreparedStatement</code>,
 * <code>LoggablePreparedStatement</code> provides a method {@link #getQueryString} that can be used to get
 * the query string in a format suitable for logging.
 * <p>
 * Should not be used in production!
 */
@SuppressWarnings("MagicConstant")
public class LoggablePreparedStatementFactory {

	public static PreparedStatement create(Connection connection, String sql) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		return wrap(preparedStatement, sql);
	}

	public static PreparedStatement create(Connection connection, String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
		return wrap(preparedStatement, sql);
	}

	public static PreparedStatement create(Connection connection, String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		return wrap(preparedStatement, sql);
	}

	public static PreparedStatement create(Connection connection, String sql, int autoGeneratedKeys) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(sql, autoGeneratedKeys);
		return wrap(preparedStatement, sql);
	}

	public static PreparedStatement create(Connection connection, String sql, int[] columnIndexes) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(sql, columnIndexes);
		return wrap(preparedStatement, sql);
	}

	public static PreparedStatement create(Connection connection, String sql, String[] columnNames) throws SQLException {
		PreparedStatement preparedStatement = connection.prepareStatement(sql, columnNames);
		return wrap(preparedStatement, sql);
	}

	// ---------------------------------------------------------------- wrap

	protected static Class<PreparedStatement> wrappedPreparedStatement;
	protected static WrapperProxettaBuilder builder;
	protected static Field sqlTemplateField;
	protected static Method getQueryStringMethod;
	protected static WrapperProxetta proxetta;

	/**
	 * Returns {@link WrapperProxetta} used for building loggable prepared statements.
	 * Initializes proxetta when called for the first time.
	 */
	public static WrapperProxetta getProxetta() {
		if (proxetta == null) {
			proxetta = WrapperProxetta.withAspects(new ProxyAspect(LoggableAdvice.class, new ProxyPointcutSupport() {
				public boolean apply(MethodInfo methodInfo) {
					int argumentsCount = methodInfo.getArgumentsCount();
					char argumentType = 0;
					if (argumentsCount >= 1) {
						argumentType = methodInfo.getArgumentOpcodeType(1);
					}
					return
							methodInfo.getReturnOpcodeType() == 'V' &&				// void-returning method
							argumentType == 'I' &&									// first argument type
							methodInfo.isTopLevelMethod() &&						// top-level
							methodInfo.getMethodName().startsWith("set") &&			// set*
							(argumentsCount == 2 || argumentsCount == 3);			// number of arguments
				}
			}));
		}

		return proxetta;
	}

	/**
	 * Wraps prepared statement.
	 */
	@SuppressWarnings("unchecked")
	protected static PreparedStatement wrap(PreparedStatement preparedStatement, String sql) {
		if (wrappedPreparedStatement == null) {
			proxetta = getProxetta();

			builder = proxetta.builder();

			// use just interface
			builder.setTarget(PreparedStatement.class);

			// define different package
			builder.setTargetProxyClassName(LoggablePreparedStatementFactory.class.getPackage().getName() + '.');

			wrappedPreparedStatement = builder.define();

			// lookup fields
			try {
				String fieldName = ProxettaAsmUtil.adviceFieldName("sqlTemplate", 0);
				sqlTemplateField = wrappedPreparedStatement.getField(fieldName);

				String methodName = ProxettaAsmUtil.adviceMethodName("getQueryString", 0);
				getQueryStringMethod = wrappedPreparedStatement.getMethod(methodName);
			} catch (Exception ex) {
				throw new DbSqlException(ex);
			}
		}

		// wrap prepared statement instance

		PreparedStatement wrapper;
		try {
			wrapper = wrappedPreparedStatement.newInstance();
		} catch (Exception ex) {
			throw new DbSqlException(ex);
		}

		builder.injectTargetIntoWrapper(preparedStatement, wrapper);

		try {
			sqlTemplateField.set(wrapper, sql);
		} catch (Exception ex) {
			throw new DbSqlException(ex);
		}

		return wrapper;
	}

	/**
	 * Returns the query string from loggable prepared statement.
	 */
	public static String getQueryString(PreparedStatement preparedStatement) {
		try {
			return (String) getQueryStringMethod.invoke(preparedStatement);
		} catch (Exception ex) {
			throw new DbSqlException(ex);
		}
	}
}
