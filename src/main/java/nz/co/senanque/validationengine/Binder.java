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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import nz.co.senanque.localemanagement.LocaleAwareRuntimeException;
import nz.co.senanque.validationengine.annotations.Ignore;
import nz.co.senanque.validationengine.metadata.ClassMetadata;
import nz.co.senanque.validationengine.metadata.PropertyMetadata;
import nz.co.senanque.validationengine.metadata.PropertyMetadataImpl;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * 
 * This class is responsible for binding the graphed objects into the validator
 * session The arrays being used must be converted to listener arrays. 
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.11 $
 */
public class Binder
{
    private final transient ValidationEngine m_validationEngine;

    public Binder(final ValidationEngine validationEngine)
    {
        m_validationEngine = validationEngine;
    }

    public Map<ValidationObject, ProxyObject> bind(final ValidationObject object,
            final ValidationSession session, ValidationObject owner)
    {
        return bind(object, session, null, null,owner);
    }

    public Map<ValidationObject, ProxyObject> bind(final ValidationObject object,
            final ValidationSession session, final ProxyField parent, final Integer index, ValidationObject owner)
    {
        MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(m_validationEngine.getMessageSource());
        final Map<ValidationObject, ProxyObject> ret = new IdentityHashMap<ValidationObject, ProxyObject>();
        if (object == null)
        {
            return ret;
        }
        Class<?> clazz = object.getClass();
        final ClassMetadata classMetadata = m_validationEngine
                .getClassMetadata(clazz);
        if (classMetadata == null)
        {
            throw new LocaleAwareRuntimeException("nz.co.senanque.validationengine.class.not.recognised",new Object[]{clazz.getName()},m_validationEngine.getMessageSource());
        }
        object.setValidationSession(session);
        final ProxyObject proxyObject = new ProxyObject(object, parent, index,session);
        ret.put(object, proxyObject);
        final ObjectMetadata objectMetadata = (ObjectMetadata)object.getMetadata();
        objectMetadata.setClassMetadata(proxyObject, classMetadata);
        Map<String,Property> propertyMap = ValidationUtils.getProperties(object.getClass());
        for (Property property: propertyMap.values()) {
        	String fieldName = property.getFieldName();
            final PropertyMetadataImpl propertyMetadata = (PropertyMetadataImpl)classMetadata
                    .getField(fieldName);
            if (propertyMetadata == null)
            {
                continue;
            }
            final ProxyFieldImpl proxyField = new ProxyFieldImpl(fieldName,
                    proxyObject, parent, propertyMetadata, messageSourceAccessor);
            proxyObject.put(fieldName, proxyField);
            Method method = property.getGetter();
            if (property.getGetter().getReturnType().isAssignableFrom(List.class))
            {
                try
                {
                    final List<ValidationObject> validationObjects = (List<ValidationObject>) method.invoke(object,new Object[]{});
                    final ListeningArray<Object> array = new ListeningArray<Object>();
                    array.addAll(validationObjects);
                    array.setValidationSession(session);
                    array.setProxyField(proxyField);
                    final Method setterMethod = property.getSetter();
                    setterMethod.invoke(object, array);
                    int index1 = 0;
                    for (ValidationObject child : validationObjects)
                    {
                        ret.putAll(bind(child, session, proxyField,index1++,owner));
                    }
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                continue;
            }
            if (m_validationEngine.getClassMetadata(method.getReturnType()) != null)
            {
                try
                {
                    final ValidationObject child = (ValidationObject)method.invoke(object, new Object[]{});
                    ret.putAll(bind(child, session, proxyField,null,owner));
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                continue;
            }
            try
            {
                Method getter = ValidationUtils.figureGetter(fieldName,clazz);
                if (getter.isAnnotationPresent(Ignore.class)) {
                	continue;
                }
                java.lang.reflect.Field propertyField = getField(clazz,fieldName);
            	Object value = ConvertUtils.convertToObject(propertyField.getType());
                proxyField.setInitialValue(value);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        return ret;
    }
    private Field getField(Class<?> clazz, String fieldName) throws Exception
    {
        try
        {
            java.lang.reflect.Field propertyField = clazz.getDeclaredField(fieldName);
            return propertyField;
        }
        catch (Exception e)
        {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null)
            {
                return getField(superClass,fieldName);
            }
            else
            {
                throw e;
            }
        }
    }
    public void unbindAll(final Map<ValidationObject, ProxyObject> boundMap)
    {
        boundMap.clear();
    }

	/**
	 * Unbind the given object. We have to locate any attached objects and unbind them first.
	 * @param session
	 * @param validationObject
	 * @param boundMap
	 */
	public void unbind(ValidationSession session, ValidationObject validationObject,
			Map<ValidationObject, ProxyObject> boundMap) {
		if (validationObject == null) {
			return;
		}
		// For all the fields: look for attached objects.
		ProxyObject proxyObject = session.getProxyObject(validationObject);
		Map<String,ProxyField> fieldMap = proxyObject.getFieldMap();
		for (String fieldName: fieldMap.keySet()) {
			if (fieldName != null) {
				final ClassMetadata classMetadata = m_validationEngine.getClassMetadata(validationObject.getClass());
				final PropertyMetadata fieldMetadata = classMetadata.getField(fieldName);
				if (fieldMetadata == null) {
					continue;
				}
				ProxyField proxyField = proxyObject.getProxyField(fieldName);
				Method getter = proxyField.getGetter();
				if (getter.getReturnType().isAssignableFrom(List.class)) {
					// if this is a list then walk the list and unbind the objects there.
					try {
						final List<ValidationObject> validationObjects = 
								(List<ValidationObject>) getter.invoke(validationObject, new Object[] {});
						for (ValidationObject child : validationObjects) {
							m_validationEngine.unbind(session, proxyField, child, boundMap);
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					continue;
				}
				if (m_validationEngine.getClassMetadata(getter.getReturnType()) != null) {
					// if this is a known object then unbind it
					try {
						ValidationObject child = (ValidationObject) getter
								.invoke(validationObject, new Object[] {});
						m_validationEngine.unbind(session, proxyField, child, boundMap);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					continue;
				}
			}
		}
	}
}
