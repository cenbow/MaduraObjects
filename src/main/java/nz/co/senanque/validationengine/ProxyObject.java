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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * One of these is created for each bound object instance to hold the proxy fields for it.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.10 $
 */
public class ProxyObject implements Serializable
{

    private final transient ValidationObject m_object;

    private final transient ProxyField m_parent;

    private final transient Map<String, ProxyField> m_fieldMap = new HashMap<String, ProxyField>();

    private final transient Integer m_index;

    private final transient ValidationSession m_session;

    protected ProxyObject(ValidationObject object, ProxyField parent, Integer index, ValidationSession session)
    {
        m_object = object;
        m_parent = parent;
        m_index = index;
        m_session = session;
    }

    protected void put(final String fieldName, final ProxyField proxyField)
    {
        m_fieldMap.put(fieldName, proxyField);
    }

    protected ProxyField getProxyField(final String fieldName)
    {
        return m_fieldMap.get(fieldName);
    }

    protected String getIndex()
    {
        if (m_index != null)
        {
            return "[" + m_index + "]";
        }
        return "";
    }

    public Map<String,ProxyField> getFieldMap()
    {
        return m_fieldMap;
    }

    public Object getObject()
    {
        return m_object;
    }

    public ValidationSession getSession()
    {
        return m_session;
    }

    public ProxyField getParent()
    {
        return m_parent;
    }

	public ObjectMetadata getObjectMetadata() {
		return m_object.getMetadata();
	}

}
