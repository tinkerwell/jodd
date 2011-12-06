// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import jodd.datetime.format.JdtFormatter;
import jodd.datetime.format.DefaultFormatter;

import java.util.TimeZone;
import java.util.Locale;

/**
 * Defaults for {@link jodd.datetime.JDateTime}.
 */
@SuppressWarnings({"RedundantFieldInitialization"})
public class JDateTimeDefault {

	public static boolean monthFix = true;

	public static TimeZone timeZone = null;		// system default

	public static Locale locale = null;			// system default

	public static String format = JDateTime.DEFAULT_FORMAT;

	public static JdtFormatter formatter = new DefaultFormatter();

	public static int firstDayOfWeek = JDateTime.MONDAY;

	public static int mustHaveDayOfFirstWeek = 4;

	public static int minDaysInFirstWeek = 4;

	public static boolean trackDST = false;
}
