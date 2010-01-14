// Copyright (c) 2003-2010, Jodd Team (jodd.org). All Rights Reserved.

package jodd.db.orm.meta;

import jodd.db.type.SqlType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * {@link jodd.db.orm.DbOrmManager} column mapping.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DbColumn {

	/**
	 * Name of bean to inject.
	 */
	String value() default "";

	/**
	 * SqlType class.
	 */
	Class<? extends SqlType> sqlType() default SqlType.class;

}
