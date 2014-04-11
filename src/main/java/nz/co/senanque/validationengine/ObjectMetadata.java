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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import nz.co.senanque.validationengine.metadata.ClassMetadata;
import nz.co.senanque.validationengine.metadata.PropertyMetadata;
import nz.co.senanque.validationengine.metadata.PropertyMetadataImpl;

/**
 * Wraps the proxy object so it can be accessed for public use
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.7 $
 */
public class ObjectMetadata implements Serializable
{
	private static final long serialVersionUID = -6502825487182156092L;

	private transient ProxyObject m_proxyObject;

    private transient ClassMetadata m_classMetadata;
    
    private transient List<String> m_unknowns = new ArrayList<String>();

    public ObjectMetadata()
    {
    }
    protected void setClassMetadata(final ProxyObject proxyObject, final ClassMetadata classMetadata)
    {
        m_proxyObject = proxyObject;
        m_classMetadata = classMetadata;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ObjectMetadata#getFieldMetadata(java.lang.String)
     */
    public FieldMetadata getFieldMetadata(final String name)
    {
        return new FieldMetadata((ProxyFieldImpl)m_proxyObject.getProxyField(name),
                (PropertyMetadataImpl) m_classMetadata.getField(name));
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ObjectMetadata#getProxyField(java.lang.String)
     */
    public ProxyField getProxyField(final String name)
    {
        return m_proxyObject.getProxyField(name);
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ObjectMetadata#getProxyFields(java.lang.String, java.lang.String)
     */
    public List<ProxyField> getProxyFields(final String ref, final String field)
    {
        List<ProxyField> ret = new ArrayList<ProxyField>();
        ProxyField refField = m_proxyObject.getProxyField(ref);
        Object o = refField.getValue();
        ValidationSession vs = m_proxyObject.getSession();
        if (o instanceof ValidationObject)
        {
            ProxyObject po = vs.getProxyObject((ValidationObject)o);
            if (po != null)
            {
                po.getProxyField(field);
                ret.add(po.getProxyField(field));
            }            
        }
        else if (o instanceof ListeningArray<?>)
        {
            ListeningArray<ValidationObject> la = (ListeningArray)o;
            for (ValidationObject vo : la)
            {
                ProxyObject po = vs.getProxyObject(vo);
                if (po != null)
                {
                    po.getProxyField(field);
                    ret.add(po.getProxyField(field));
                }                
            }
        }
        return ret;
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.ObjectMetadata#getProxyObject()
     */
    public ProxyObject getProxyObject()
    {
        return m_proxyObject;
    }
	public void addUnknown(String name) {
		if (!isUnknown(name))
		{
			m_unknowns.add(name);	
		}
	}
	public void removeUnknown(String name) {
		m_unknowns.remove(name);	
	}
	public boolean isUnknown(String name) {
		return m_unknowns.contains(name);	
	}
	public Collection<PropertyMetadata> getAllPropertyMetadata() {
		// TODO Auto-generated method stub
		return m_classMetadata.getAllFields();
	}
}
