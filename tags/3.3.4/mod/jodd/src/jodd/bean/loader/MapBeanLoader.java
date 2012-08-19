// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package jodd.bean.loader;

import java.util.Map;

/**
 * Populate java bean using objects that are implementation of <code>Map</code> interface.
 * <p>
 * Each key of <code>Map</code> is a <code>String</code> the represents a bean property name and
 * key value is an object that represents bean property value.
 */
public class MapBeanLoader extends BaseBeanLoader {

	@SuppressWarnings({"unchecked"})
	public void load(Object bean, Object source) {

		if (source instanceof Map) {
			Map<String, Object> map = (Map<String, Object>) source;

			for (Map.Entry<String, Object> entry : map.entrySet()) {

				Object propertyValue = entry.getValue();

				beanUtilBean.setPropertyForcedSilent(bean, entry.getKey(), propertyValue);
			}
		}
	}

}