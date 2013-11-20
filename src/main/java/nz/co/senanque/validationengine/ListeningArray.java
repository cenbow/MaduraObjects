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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * This is the array that holds all the object collections
 * It notifies the validation session if an object is added or removed
 * Clearing the array and adding multiple objects at once (addAll) are disabled.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.11 $
 */
public class ListeningArray<E> extends LinkedList<E>
{
    private transient ValidationSession m_validationSession;
    private transient ProxyField m_proxyField;

    public void add(final int index, final E o)
    {
        if (m_validationSession != null)
        {
            m_validationSession.add(this, (ValidationObject)o);
        }
        super.add(index, o);
    }

    public boolean add(final Object o)
    {
        boolean ret = super.add((E)o);
        if (m_validationSession != null)
        {
            try
            {
                m_validationSession.add(this, (ValidationObject)o);
            }
            catch (ValidationException e)
            {
                super.remove((E)o);
                throw e;
            }
        }
        return ret;
    }

    public E remove(final int index)
    {
    	E ret = super.get(index);
    	remove(ret);
        return ret;
    }

    public boolean remove(final Object o)
    {
        boolean ret = false;
        if (m_validationSession != null)
        {
            try
            {
                ret = super.remove(o);
                if (ret)
                {
                	m_validationSession.removedFrom(this, (ValidationObject) o);
                }
            }
            catch (ValidationException e)
            {
            	super.add((E)o);
                throw e;
            }
        }
        else
        {
        	ret = super.remove(o);
        }
        return ret;
    }

    public void clear()
    {
        if (m_validationSession != null)
        {
        	List<E> removed = new ArrayList<E>();
        	List<E> removing = new ArrayList<E>();
        	for (E o: this)
        	{
        		removing.add(o);
        	}
        	for (E o: removing)
        	{
                try
                {
                	if (super.remove(o))
                	{
	                	ValidationObject vo = (ValidationObject)o;
	                    m_validationSession.removedFrom(this, vo);
	                    removed.add((E)vo);
                	}
                }
                catch (ValidationException e)
                {
                	addAll(removed);
                    throw e;
                }
        	}
        	for (E vo: removed)
        	{
        		this.remove(vo);
        	}
        }
        else
        {
            super.clear();
        }
    }

    public void addAll(final List<E> o)
    {
        if (m_validationSession != null)
        {
            super.addAll(o);
            m_validationSession.add(this, (List<ValidationObject>)o);
            super.removeAll(o);
        }
        else
        {
            super.addAll(o);
        }
    }

    public void setProxyField(ProxyField proxyField)
    {
        m_proxyField = proxyField;
    }

    public ProxyField getProxyField()
    {
        return m_proxyField;
    }

    public ValidationSession getValidationSession()
    {
        return m_validationSession;
    }

    public void setValidationSession(ValidationSession validationSession)
    {
        m_validationSession = validationSession;
    }
}
