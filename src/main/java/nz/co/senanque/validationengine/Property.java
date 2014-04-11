/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nz.co.senanque.validationengine;

import java.lang.reflect.Method;

/**
 * @author Roger Parkinson
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
