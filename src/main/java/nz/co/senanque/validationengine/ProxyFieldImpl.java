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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nz.co.senanque.validationengine.choicelists.ChoiceBase;
import nz.co.senanque.validationengine.metadata.PropertyMetadataImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * Every field instance gets one of these created to manage the dynamic settings.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.18 $
 */
public class ProxyFieldImpl implements ProxyField
{
    protected enum Required {
        TRUE, TEMP, FALSE
    }

    private final transient String m_fieldName;
    private final transient ProxyObject m_proxyObject;
    private final transient PropertyMetadataImpl m_propertyMetadata;
    private final transient ProxyField m_parent;
    private transient boolean m_derived;
    private boolean m_readOnly;
    private boolean m_required;
    private boolean m_inActive;
    private final static Logger log = LoggerFactory.getLogger(ProxyFieldImpl.class);
    private Object m_currentValue = null;
    private boolean m_useCurrentValue = false;
    private Set<String> m_excludes = null;
    private List<ChoiceBase> m_cachedList = null;
    private boolean m_secret;
	private Object m_initialValue;
	private final transient MessageSourceAccessor m_messageSourceAccessor;
	private boolean m_notKnown;
	private boolean m_lastUnknown;

    protected ProxyFieldImpl(final String fieldName, final ProxyObject proxyObject,
            final ProxyField parent, final PropertyMetadataImpl fieldMetadata, MessageSourceAccessor messageSourceAccessor)
    {
        m_fieldName = fieldName;
        m_proxyObject = proxyObject;
        m_propertyMetadata = fieldMetadata;
        m_parent = parent;
        m_currentValue = fetchValue();
        m_readOnly = fieldMetadata.isReadOnly();
        m_inActive = fieldMetadata.isInactive();
        m_required = fieldMetadata.isRequired();
        m_secret = fieldMetadata.isSecret();
        m_messageSourceAccessor = messageSourceAccessor;
        m_lastUnknown = isUnknown();
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#getPath()
     */
    public String getPath()
    {
        return ((m_parent==null)?"":m_parent.getPath()+ m_proxyObject.getIndex() + ".") 
                + m_fieldName;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#getPropertyMetadata()
     */
    public PropertyMetadataImpl getPropertyMetadata()
    {
        return m_propertyMetadata;
    }
    
    public FieldMetadata getFieldMetadata()
    {
    	return new FieldMetadata(this,m_propertyMetadata);
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#isReadOnly()
     */
    
    public boolean isReadOnly()
    {
        return m_readOnly;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#setReadOnly(boolean)
     */
    public void setReadOnly(final boolean b)
    {
        m_readOnly = b;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#setInActive(boolean)
     */
    
    public void setInActive(final boolean b)
    {
        m_inActive = b;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#isInActive()
     */
    public boolean isInActive()
    {
        return m_inActive;
    }
    
    private boolean hasExcludes()
    {
        return (m_excludes != null && m_excludes.size()>0);
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#getChoiceList()
     */
    public List<ChoiceBase> getChoiceList()
    {
        if (hasExcludes())
        {
            if (m_cachedList != null)
            {
                return m_cachedList;
            }
            final List<ChoiceBase> retList = new ArrayList<ChoiceBase>();
            for (ChoiceBase choiceBase:m_propertyMetadata.getChoiceList())
            {
                final String key = choiceBase.getKey().toString();
                if (!m_excludes.contains(key))
                {
                    retList.add(choiceBase);
                }
            }
            m_cachedList = retList;
            return retList;
        }
        return m_propertyMetadata.getChoiceList();
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#setValue(java.lang.Object)
     */
    public void setValue(Object newValue)
    {
    	if (FieldMetadata.NOT_KNOWN.equals(newValue))
    	{
            setUnknown(false);
            m_notKnown = true;
            return;
    		//throw new RuntimeException("Inconsistent use of NOT_KNOWN");
    	}
    	if (FieldMetadata.UNKNOWN.equals(newValue))
    	{
            setUnknown(true);
            m_notKnown = false;
            return;
    		//throw new RuntimeException("Inconsistent use of UNKNOWN");
    	}
        if (isDerived() && !ValidationUtils.equals(m_currentValue,newValue))
        {
            final String labelName = getPropertyMetadata().getLabelName();
            final String message = m_messageSourceAccessor.getMessage(
                    "nz.co.senanque.validationengine.derivedValue",
                    new Object[]
                    { labelName, newValue }, 
                    "Cannot set derived field.");
            throw new ValidationException(message);
        }
        m_currentValue = newValue;
        useCurrentValue(true);
        setUnknown(false);
        m_notKnown = false;
    }
    
    public ObjectMetadata getObjectMetadata()
    {
    	return m_proxyObject.getObjectMetadata();
    }
    
    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#reset()
     */
    
    public void reset()
    {
        m_currentValue = null;
        useCurrentValue(false);
        setDerived(false);
        m_notKnown = false;
        setUnknown(m_lastUnknown);
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#getValue()
     */
    public Object getValue()
    {
        if (!m_useCurrentValue)
        {
            m_currentValue = fetchValue();
            useCurrentValue(true);
        }
        return m_currentValue;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#assign(java.lang.Object)
     */
	public void assign(final Object a) {
		setDerived(false);
		setValue(a);
		setDerived((a == null) ? false : true);
		m_notKnown = false;
	}

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#toString()
     */
    public String toString()
    {
        return getPath()+": unknown:"+isUnknown();
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#isRequired()
     */
    public boolean isRequired()
    {
        return m_required;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#isDerived()
     */
    
    public boolean isDerived()
    {
        return m_derived;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#setDerived(boolean)
     */
    public void setDerived(boolean derived)
    {
        m_derived = derived;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#getFieldName()
     */
    public String getFieldName()
    {
        return m_fieldName;
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#updateValue()
     */
    public void updateValue()
    {
        try
        {
            Method method = m_propertyMetadata.getSetMethod();
            //Object convertedValue = ValidationUtils.convertTo(getClazz(), m_currentValue);
            if (m_currentValue == null && method.getParameterTypes()[0].isPrimitive())
            {
                method.invoke(m_proxyObject.getObject(), ConvertUtils.getPrimitiveTypeForNull(method.getParameterTypes()[0]));
            }
            else
            {
                method.invoke(m_proxyObject.getObject(), m_currentValue);
            }
            m_useCurrentValue = false;
            m_notKnown = false;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#fetchValue()
     */
    public Object fetchValue()
    {
        try
        {
            return m_propertyMetadata.getGetMethod().invoke(m_proxyObject.getObject());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#useCurrentValue(boolean)
     */
    public void useCurrentValue(boolean useCurrentValue)
    {
        m_useCurrentValue = useCurrentValue;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#setRequired(boolean)
     */
    public void setRequired(final boolean b)
    {
        m_required = b;        
    }

    public boolean isSecret()
    {
        return m_secret;
    }

    public void setSecret(boolean secret)
    {
        m_secret = secret;
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#isExcluded(java.lang.String)
     */
    public boolean isExcluded(final String key)
    {
        return getExcludes().contains(key);
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#exclude(java.lang.String)
     */
    public void exclude(final String key)
    {
        getExcludes().add(key);
        m_cachedList = null;
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#getExcludes()
     */
    public Set<String> getExcludes()
    {
        if (m_excludes == null)
            m_excludes = new HashSet<String>();
        return m_excludes;
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#clearExclude(java.lang.String)
     */
    public void clearExclude(final String key)
    {
        getExcludes().remove(key);
        m_cachedList = null;
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#clearExcludes()
     */
    public void clearExcludes()
    {
        m_excludes = null;
        m_cachedList = null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ProxyField#getProxyObject()
     */
    public ProxyObject getProxyObject()
    {
        return m_proxyObject;
    }
	public void setInitialValue(Object value) {
		m_initialValue = value;
	}

	public Object getInitialValue() {
		return m_initialValue;
	}

	public void setNotKnown(boolean b) {
		m_notKnown = b;
		setUnknown(false);
	}

	public boolean isNotKnown() {
		return m_notKnown;
	}

	public boolean isUnknown() {
		return getProxyObject().getObjectMetadata().isUnknown(m_fieldName);
	}

	public void setUnknown(boolean unknown) {
		ObjectMetadata om = getProxyObject().getObjectMetadata();		
		if (unknown)
		{
			om.addUnknown(m_fieldName);
			m_notKnown = false;
		}
		else
		{
			om.removeUnknown(m_fieldName);
		}
	}

	public boolean isIdentifier() {
		return m_propertyMetadata.isIdentifier();
	}

	@Override
	public Method getGetter() {
		return m_propertyMetadata.getGetMethod();
	}

}
