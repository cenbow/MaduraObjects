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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import nz.co.senanque.validationengine.choicelists.Choice;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;
import nz.co.senanque.validationengine.fieldvalidators.FieldValidator;
import nz.co.senanque.validationengine.metadata.ClassMetadata;
import nz.co.senanque.validationengine.metadata.EngineMetadata;
import nz.co.senanque.validationengine.metadata.PropertyMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

/** 
 * This is the validation engine implementation. 
 * It implements validation on individual fields, including custom validation, as well as labels etc.
 * It accepts plugins for cross field validation and generation of new data.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.6 $
 */
public final class ValidationEngineImpl implements ValidationEngine,
        MessageSourceAware
{
    private final static Logger log = LoggerFactory.getLogger(ValidationEngineImpl.class);

    private transient EngineMetadata m_metadata;
    private transient MessageSource m_messageSource;
    private transient Binder m_binder = new Binder(this);
    private transient List<Plugin> m_plugins = new ArrayList<Plugin>();
    private transient String m_identifier="not set";

    public Map<ValidationObject, ProxyObject> add(final ListeningArray<ValidationObject> array, final ValidationObject value,
            final ValidationSession session) throws ValidationException
    {
        log.debug("add {} {}", array.getClass(), value);
        final ProxyField proxyField = array.getProxyField();
        proxyField.useCurrentValue(false);
        final Map<ValidationObject, ProxyObject> bound = getBinder().bind(value, session, proxyField, new Integer(array.size()),null);
        session.provisionalObjects(bound);
        try
        {
            for (Plugin plugin: getPlugins())
            {
                ProxyObject ownerProxyObject = proxyField.getProxyObject();
                plugin.bind(session, bound, proxyField,(ValidationObject)ownerProxyObject.getObject());
            }
        }
        finally
        {
            session.provisionalObjects(null);
        }
        return bound;
    }
    public Map<ValidationObject, ProxyObject> addAll(final ListeningArray<ValidationObject> listeningArray,
            final List<ValidationObject> o, final ValidationSession validationSession)
    {
        final Map<ValidationObject, ProxyObject> bound = new IdentityHashMap<ValidationObject, ProxyObject>();
        for (ValidationObject object: o)
        {
            bound.putAll(add(listeningArray,object,validationSession));
        }
        return bound;
    }
    public void removedFrom(final ListeningArray<?> array, final ValidationObject value,
            final ValidationSession session)
    {
        log.debug("remove {} {}", array.getClass(), value);
        final ProxyField proxyField = array.getProxyField();
        session.unbind(proxyField,value);
        return;
    }

    public String getStats(final ValidationSession session)
    {
    	StringBuilder ret = new StringBuilder();
        for (Plugin plugin: getPlugins())
        {
        	ret.append(plugin.getStats(session));
        }
    	return ret.toString();
    }
    public boolean clean(final ValidationSession session, final ValidationObject object)
    {
        ProxyObject proxyObject = session.getProxyObject(object);
        for (Plugin plugin: getPlugins())
        {
            plugin.clean(session,proxyObject);
        }
        return false;
    }

    public void set(final ValidationObject object, final String name, final Object newValue, final Object currentValue, final ValidationSession session)
            throws ValidationException
    {
        log.debug("set {} {}", name, newValue);
        if (newValue != null && newValue.equals(currentValue)) 
        {
            return;
        }
        PropertyMetadata fieldMetadata = getMetadata().getField(object, name);
        if (fieldMetadata == null)
        {
            return;
        }
        
        if (newValue == null && currentValue == null)
        {
            return;
        }
        ObjectMetadata objectMetadata = session.getMetadata(object);
        ProxyField proxyField = objectMetadata.getProxyField(name);
        proxyField.useCurrentValue(false);
        final ClassMetadata clazz = getMetadata().getClassMetadata(newValue!=null?newValue.getClass():currentValue.getClass());
        if (clazz != null)
        {
            // We are setting a known object, as opposed to a primitive or a String etc
            Map<ValidationObject, ProxyObject> bound = null;
            if (currentValue != null)
            {
                session.unbind(proxyField, (ValidationObject)currentValue);
            }
            if (newValue != null)
            {
                validate(fieldMetadata, newValue,proxyField);
                proxyField.setValue(newValue);
            }
            else
            {
                proxyField.reset();
            }
            bound = session.bind((ValidationObject)newValue,object);
            session.provisionalObjects(bound);
            try
            {
                for (Plugin plugin: getPlugins())
                {
                    plugin.bind(session, bound,null,object);
                }
            }
            finally
            {
                session.provisionalObjects(null);
            }
        }
        else
        {
            Object oldValue = proxyField.getValue();
            List<History> oldHistory = proxyField.getHistory();
            if (proxyField.getChoiceList() != null && oldValue != null)
            {
                for (Plugin plugin: getPlugins())
                {
                    plugin.set(session, proxyField, null);
                }               
            }
            if (newValue != null)
            {
                try
                {
                    validate(fieldMetadata, newValue, proxyField);
                }
                catch (ValidationException e)
                {
                    for (Plugin plugin: getPlugins())
                    {
                        plugin.set(session, proxyField, oldValue);
                    }
                    throw e;
                }
                proxyField.setValue(newValue);
            }
            else
            {
                proxyField.reset();
                proxyField.setHistory(oldHistory);
            }
            for (Plugin plugin: getPlugins())
            {
                plugin.set(session, proxyField, newValue);
            }
        }
    }

    public void validate(PropertyMetadata fieldMetadata, Object value, ProxyField proxyField)
    {
        for (FieldValidator<Annotation> fv : fieldMetadata.getConstraintValidators())
        {
            fv.validate(value);
        }
        List<ChoiceBase> choiceList = proxyField.getChoiceList();
        if (choiceList != null)
        {
            boolean found = false;
            for (Choice choice : choiceList)
            {
                if (choice.getKey().equals(value.toString()))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                String labelName = fieldMetadata.getLabelName();
        		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_messageSource);
                String message = messageSourceAccessor.getMessage(
                        "nz.co.senanque.validationengine.choicelist",
                        new Object[]
                        { labelName, value }, "Failed choicelist validation.");
                throw new ValidationException(message);
            }
        }
    }

    public void setMessageSource(MessageSource arg0)
    {
        m_messageSource = arg0;
    }

    public ClassMetadata getClassMetadata(Class<?> clazz)
    {
        return getMetadata().getClassMetadata(clazz);
    }

    public Map<ValidationObject, ProxyObject> bind(ValidationObject object,
            ValidationSession session, ValidationObject owner)
    {
        Map<ValidationObject, ProxyObject> ret = getBinder().bind(object,
                session,owner);
        session.provisionalObjects(ret);
        try
        {
            for (Plugin plugin : getPlugins())
            {
                plugin.bind(session, ret,null,owner);
            }
        }
        finally
        {
            session.provisionalObjects(null);
        }
        return ret;
    }
    public void unbind(ValidationSession session, ProxyField proxyField, ValidationObject validationObject, Map<ValidationObject, ProxyObject> boundMap)
    {
    	if (validationObject == null)
    	{
    		return;
    	}
    	// this actually unbinds all child objects, not the current object
        getBinder().unbind(session, validationObject, boundMap);
        // once all the child objects are unbound then tell the plugins
        // to unbind this one
        ProxyObject proxyObject = boundMap.get(validationObject);
    	if (proxyObject == null)
    	{
    		return;
    	}
        for (Plugin plugin : getPlugins())
        {
        	plugin.unbind(session, proxyField, validationObject);
        }
        ProxyObject removed = boundMap.remove(validationObject);
        if (removed == null)
        {
        	log.warn("failed to remove {} from boundMap",validationObject.toString());
        }
    }

    public void unbindAll(ValidationSession session, Map<ValidationObject, ProxyObject> boundMap)
    {
        for (ValidationObject validationObject: boundMap.keySet())
        {
            for (Plugin plugin : getPlugins())
            {
                plugin.unbind(session,null, validationObject);
            }
        }
        getBinder().unbindAll(boundMap);
    }

    private Binder getBinder()
    {
        return m_binder;
    }

    public EngineMetadata getMetadata()
    {
        return m_metadata;
    }

    public void setMetadata(EngineMetadata metadata)
    {
        m_metadata = metadata;
    }

    public List<Plugin> getPlugins()
    {
        return m_plugins;
    }
    public void setPlugins(List<Plugin> plugins)
    {
        m_plugins = plugins;
    }
    public MessageSource getMessageSource()
    {
        return m_messageSource;
    }
    public boolean clean(ValidationObject object)
    {
        return false;
    }
    public ValidationSession createSession()
    {
        return new ValidationSession(this,Locale.getDefault());
    }
    public ValidationSession createSession(Locale locale)
    {
        return new ValidationSession(this,locale);
    }
    public void close(ValidationSession validationSession)
    {
      for (Plugin plugin: getPlugins())
      {
          plugin.close(validationSession);
      }
      validationSession.unbindAll();
    }
    @PostConstruct
    public void init()
    {
        for (Plugin plugin: getPlugins())
        {
            plugin.init(getMetadata());
        }
    }
	public String getIdentifier() {
		return m_identifier;
	}
	public void setIdentifier(String identifier) {
		m_identifier = identifier;
	}
}
