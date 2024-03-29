// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.datetime;

import jodd.typeconverter.Convert;
import jodd.typeconverter.impl.SqlDateConverter;
import jodd.typeconverter.impl.SqlTimestampConverter;
import junit.framework.TestCase;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.sql.Timestamp;

public class TimeZoneTest extends TestCase {

	public void testTimeZones() {
		GregorianCalendar gc = new GregorianCalendar();
		JDateTime jdt1 = new JDateTime();
	    gc.setTimeInMillis(jdt1.getTimeInMillis());

		TimeZone tz = TimeZone.getTimeZone("GMT+01:00");
		jdt1.changeTimeZone(tz);
		gc.setTimeZone(tz);
		assertEquals(gc.getTimeInMillis(), jdt1.getTimeInMillis());

		JDateTime jdt2 = jdt1.clone();
		assertEquals(jdt1, jdt2);

		tz = TimeZone.getTimeZone("GMT+02:00");
		jdt2.changeTimeZone(tz);
		gc.setTimeZone(tz);
		assertEquals(gc.getTimeInMillis(), jdt1.getTimeInMillis());

		if (jdt2.getHour() != 0) {
			assertEquals(jdt1.getHour() + 1, jdt2.getHour());
			assertEquals(jdt1.getMinute(), jdt2.getMinute());
			assertEquals(jdt1.getSecond(), jdt2.getSecond());
			assertEquals(jdt1.getMillisecond(), jdt2.getMillisecond());
		}

		tz = TimeZone.getTimeZone("GMT-12:00");
		jdt1.changeTimeZone(tz);
		gc.setTimeZone(tz);
		assertEquals(gc.getTimeInMillis(), jdt1.getTimeInMillis());

		tz = TimeZone.getTimeZone("GMT+10:00");
		jdt2.changeTimeZone(tz);
		gc.setTimeZone(tz);
		assertEquals(gc.getTimeInMillis(), jdt2.getTimeInMillis());
		assertEquals(jdt1.getTimeInMillis(), jdt2.getTimeInMillis());

		java.util.Date date = Convert.toDate(jdt1);
		assertEquals(date.getTime(), jdt1.getTimeInMillis());

		GregorianCalendar cal = (GregorianCalendar) Convert.toCalendar(jdt1);
		assertEquals(cal.getTimeInMillis(), jdt1.getTimeInMillis());
		assertEquals(cal.getTimeZone(), jdt1.getTimeZone());

		SqlDateConverter sqlDateConverter = new SqlDateConverter();
		java.sql.Date sqlDate = sqlDateConverter.convert(jdt1);
		assertEquals(sqlDate.getTime(), jdt1.getTimeInMillis());

		SqlTimestampConverter sqlTimestampConverter = new SqlTimestampConverter();
		Timestamp sqlTimestamp = sqlTimestampConverter.convert(jdt1);
		assertEquals(sqlTimestamp.getTime(), jdt1.getTimeInMillis());
	}

	public void testTzOffset() {
		JDateTime now = new JDateTime(2009, 5, 1, 23, 45, 1, 0);
		now.changeTimeZone(TimeZone.getTimeZone("Europe/Belgrade"));
		TimeZone tz1 = now.getTimeZone();
		TimeZone tz2 = TimeZone.getTimeZone("GMT+01:00");
		TimeZone tz3 = TimeZone.getTimeZone("Japan");

		assertEquals(0, TimeZoneUtil.getRawOffsetDifference(tz1, tz2));
		assertEquals(-3600000, TimeZoneUtil.getOffsetDifference(now.getTimeInMillis(), tz1, tz2));
		assertEquals(-3600000, TimeZoneUtil.getOffsetDifference(now, tz1, tz2));

		assertEquals(8 * 3600000, TimeZoneUtil.getRawOffsetDifference(tz1, tz3));
		assertEquals(7 * 3600000, TimeZoneUtil.getOffsetDifference(now.getTimeInMillis(), tz1, tz3));
		assertEquals(7 * 3600000, TimeZoneUtil.getOffsetDifference(now, tz1, tz3));
	}

	public void testDlt() {
		TimeZone cetTimeZone = TimeZone.getTimeZone("CET");
		TimeZone cestTimeZone = TimeZone.getTimeZone("CEST");

		JDateTime jDateTime = new JDateTime(2012, 6, 1, 11, 44, 55, 0);
		jDateTime.setTimeZone(cetTimeZone);
		Date date = jDateTime.convertToDate();

		assertEquals(cetTimeZone.inDaylightTime(date), jDateTime.isInDaylightTime());

		jDateTime.setTimeZone(cestTimeZone);
		assertEquals(cestTimeZone.inDaylightTime(date), jDateTime.isInDaylightTime());
	}

}
