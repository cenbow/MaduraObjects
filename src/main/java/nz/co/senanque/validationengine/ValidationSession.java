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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * This describes the session and is the main interface the application uses
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.15 $
 */
public class ValidationSession implements Serializable
{
	private static final long serialVersionUID = 1L;

	private final transient ValidationEngine m_validationEngine;

    private final transient Map<ValidationObject, ProxyObject> m_boundMap = new IdentityHashMap<ValidationObject, ProxyObject>();
    private transient boolean m_enabled = true;
    private Map<ValidationObject, ProxyObject> m_provisionalObjects;
    private transient List<ProxyField> m_history = new ArrayList<>();

    private final Locale m_locale;

    protected ValidationSession(ValidationEngine validationEngine, Locale locale)
    {
        m_validationEngine = validationEngine;
        if (validationEngine == null)
        {
            throw new RuntimeException("Validation session has to have a validation engine, not null");
        }
        m_locale = (locale==null)?Locale.getDefault():locale;
    }

    public ValidationEngine getValidationEngine()
    {
        return m_validationEngine;
    }

    public void bind(ValidationObject validationObject)
    {
    	Map<ValidationObject,ProxyObject> ret = bind(validationObject,null);
    }

    public Map<ValidationObject,ProxyObject> bind(final ValidationObject object, ValidationObject owner)
    {
        Map<ValidationObject,ProxyObject> ret = getValidationEngine().bind(object, this, owner);
        m_boundMap.putAll(ret);
        return ret;
    }
    public void unbindAll()
    {
        if (m_enabled)
        {
            m_validationEngine.unbindAll(this,m_boundMap);
        }
        
    }

    public void set(final ValidationObject object, final String fieldName, final Object newValue, final Object currentValue)
    {
        if (m_enabled)
        {
            for (ProxyField proxyField:m_history) {
            	proxyField.expire();
            }
            m_validationEngine.set(object, fieldName, newValue, currentValue, this);
        }
    }

    /**
     * The clean method ensures that the object and its attached objects are up to date
     * ie that any mapping is updated. This means the getters on the object are safe to use
     * @param object
     */
    public void clean(final ValidationObject object)
    {
        if (m_enabled)
        {
            m_validationEngine.clean(object);
            for (ProxyField proxyField:m_history) {
            	proxyField.expire();
            }
        }
    }

    /**
     * Locate the metadata for the given object
     * The object is cleaned first and then a wrapper for the metadata is returned
     * @param object
     * @return the metadata for the object
     */
    public ObjectMetadata getMetadata(final ValidationObject object)
    {
        if (m_enabled)
        {
            m_validationEngine.clean(object);
        }
        return object.getMetadata();
    }

    public boolean add(final ListeningArray<?> listeningArray, final ValidationObject o)
    {
        boolean ret = true;
        if (m_enabled)
        {
            m_boundMap.putAll(m_validationEngine.add((ListeningArray<ValidationObject>)listeningArray, o, this));
        }
        return ret;
    }
    public void add(final ListeningArray<?> listeningArray, final List<ValidationObject> o)
    {
        if (m_enabled)
        {
            m_boundMap.putAll(m_validationEngine.addAll((ListeningArray<ValidationObject>)listeningArray, o, this));
        }
    }

    /**
     * Remove an object from an array
     * @param array
     * @param o
     */
    protected void removedFrom(final ListeningArray<?> array, final ValidationObject o)
    {
        if (m_enabled)
        {
            m_validationEngine.removedFrom(array, o,this);
        }
    }    

    public void clearArray(final ListeningArray<?> listeningArray)
    {
        if (m_enabled)
        {
            for (Object o:listeningArray)
            {
            	removedFrom(listeningArray, (ValidationObject)o);
            }
        }
    }

	public void unbind(ValidationObject validationObject) {
		unbind(null,validationObject);
	}
    public void unbind(ProxyField proxyField, ValidationObject currentValue)
    {
        if (m_enabled)
        {
        	m_validationEngine.unbind(this,proxyField,currentValue,m_boundMap);
        	m_history.remove(proxyField);
        }
        
    }
    public int getProxyCount()
    {
        return m_boundMap==null?0:m_boundMap.size();
    }
    protected void finalize()
    {
        close();
    }
    
    public void close()
    {
        if (m_enabled)
        {
            m_validationEngine.close(this);
        }
    }

    public ProxyObject getProxyObject(final ValidationObject object)
    {
        ProxyObject ret = m_boundMap.get(object);
        if (ret == null && m_provisionalObjects != null)
        {
            ret = m_provisionalObjects.get(object);
        }
        return ret;
    }
    public ProxyField getProxyField(final FieldMetadata fieldMetadata)
    {
    	return fieldMetadata.getProxyField();
    }

    public List<ProxyObject> getProxyObjects()
    {
        // TODO: this may not be efficient enough
        final List<ProxyObject> ret = new ArrayList<ProxyObject>(m_boundMap.size());
        for (Map.Entry<ValidationObject, ProxyObject> entry: m_boundMap.entrySet())
        {
            ret.add(entry.getValue());
        }
        return ret;
    }

    public boolean isEnabled()
    {
        return m_enabled;
    }

    public void setEnabled(final boolean enabled)
    {
        m_enabled = enabled;
    }

    protected void provisionalObjects(Map<ValidationObject, ProxyObject> provisionalObjects)
    {
        m_provisionalObjects = provisionalObjects;
    }

    public Locale getLocale()
    {
        return m_locale;
    }

	public String getStats() {
    	StringBuilder ret = new StringBuilder();
    	ret.append(MessageFormat.format("\nValidation session has {0} objects bound", m_boundMap.size()));
    	ret.append(getValidationEngine().getStats(this));
    	return ret.toString();
	}

	/**
	 * We detected a field keeping an expiry history so record it here.
	 * 
	 * @param m_proxyObject
	 * @param proxyField
	 */
	public void addExpiry(ProxyField proxyField) {
		m_history.add(proxyField);		
	}
	
}
