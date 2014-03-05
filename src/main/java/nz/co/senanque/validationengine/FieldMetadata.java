/*******************************************************************************
 * Copyright (c)2013 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
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
import java.util.List;

import nz.co.senanque.validationengine.choicelists.ChoiceBase;
import nz.co.senanque.validationengine.metadata.PropertyMetadataImpl;

/**
 * 
 * Stores metadata for the field. Some of the metadata is dynamic so there is one of these for each monitored field instance.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.10 $
 */
public class FieldMetadata implements Serializable
{
	/**
	 * The Unknown state means that the value for the field has not been established.
	 * This implies that we can take steps to find out what it is.
	 */
	static class UnKnown
	{
		public String getName()
		{
			return "unknown";
		}
	}
	public static UnKnown UNKNOWN = new FieldMetadata.UnKnown();

	/**
	 * The not known state means that we have already tried to establish a value
	 * for the field and did not get a result, implying there is no point trying further.
	 */
	static class NotKnown
	{
	}
	public static NotKnown NOT_KNOWN = new FieldMetadata.NotKnown();
	
	private static final long serialVersionUID = -4725161053152207156L;

	private final ProxyFieldImpl m_proxyField;

    private final transient PropertyMetadataImpl m_propertyMetadata;

    public FieldMetadata(final ProxyFieldImpl fieldProxyObject,
            final PropertyMetadataImpl propertyMetadata)
    {
        m_proxyField = fieldProxyObject;
        m_propertyMetadata = propertyMetadata;
        if (m_propertyMetadata == null)
        {
            throw new NullPointerException("m_propertyMetadata is null");
        }
    }

    public String getDescription()
    {
        return m_propertyMetadata.getDescription();
    }

    public String getPermission()
    {
        return m_propertyMetadata.getPermission();
    }

    public String getReadPermission()
    {
        return m_propertyMetadata.getReadPermission();
    }

    public String getLabelName()
    {
        return m_propertyMetadata.getLabelName();
    }

    public String getName()
    {
        return m_propertyMetadata.getName();
    }

    public boolean isReadOnly()
    {
        return m_proxyField == null ? false : m_proxyField.isReadOnly();
    }

    public boolean isRequired()
    {
        return m_proxyField == null ? false : m_proxyField.isRequired();
    }

    public boolean isSecret()
    {
        return m_proxyField == null ? false : m_proxyField.isSecret();
    }

    public boolean isActive()
    {
        return m_proxyField == null ? true : !(m_proxyField.isInActive());
    }

    public List<ChoiceBase> getChoiceList()
    {
        return m_proxyField == null ? m_propertyMetadata.getChoiceList()
                : m_proxyField.getChoiceList();

    }
    public void setValue(Object newValue)
    {
    	if (NOT_KNOWN.equals(newValue))
    	{
        	m_proxyField.updateValue();
    		m_proxyField.setNotKnown(true);
    		return;
    	}
    	if (UNKNOWN.equals(newValue))
    	{
    		m_proxyField.reset();
        	m_proxyField.updateValue();
    		return;
    	}
    	m_proxyField.setValue(newValue);
    	m_proxyField.updateValue();
    }
    
    public void setUnknown(boolean b)
    {
    	m_proxyField.setUnknown(b);
    }
    
	public ValidationSession getValidationSession()
    {
    	return m_proxyField.getProxyObject().getSession();
    }

	protected ProxyField getProxyField() {
		return m_proxyField;
	}
	public String toString()
	{
		return m_proxyField.toString();
	}

	public boolean isIdentifier() {
		return m_proxyField.isIdentifier();
	}
	
}
