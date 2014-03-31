/**
 * 
 */
package nz.co.senanque.validationengine;

import java.lang.reflect.Method;

/**
 * @author roger
 *
 */
public class Property {

	private final String m_fieldName;
	private final Method m_getter;
	private final Class<?> m_clazz;
	private final Method m_setter;

	public Property(String fieldName, Method getter, Method setter,
			Class<?> clazz) {
		m_fieldName = fieldName;
		m_getter = getter;
		m_setter = setter;
		m_clazz = clazz;
	}

	public String getFieldName() {
		return m_fieldName;
	}

	public Method getGetter() {
		return m_getter;
	}

	public Class<?> getClazz() {
		return m_clazz;
	}

	public Method getSetter() {
		return m_setter;
	}
	
}
