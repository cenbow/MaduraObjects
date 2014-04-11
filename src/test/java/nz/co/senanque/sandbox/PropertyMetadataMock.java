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
package nz.co.senanque.sandbox;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import nz.co.senanque.validationengine.choicelists.Choice;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;
import nz.co.senanque.validationengine.fieldvalidators.FieldValidator;
import nz.co.senanque.validationengine.metadata.PropertyMetadata;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * Short description
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.5 $
 */
public class PropertyMetadataMock implements PropertyMetadata
{
    private transient Class<?> m_class;
	private MessageSourceAccessor m_messageSourceAccessor;

    public PropertyMetadataMock(MessageSourceAccessor messageSourceAccessor) {
    	m_messageSourceAccessor = messageSourceAccessor;
	}

	/* (non-Javadoc)
     * @see nz.co.senanque.validationengine.metadata.PropertyMetadata#findChoice(java.lang.String)
     */
    public Choice findChoice(final String key)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.metadata.PropertyMetadata#getChoiceList()
     */
    public List<ChoiceBase> getChoiceList()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.metadata.PropertyMetadata#getClazz()
     */
    public Class<?> getClazz()
    {
        // TODO Auto-generated method stub
        return m_class;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.metadata.PropertyMetadata#getConstraintValidators()
     */
    public List<FieldValidator<Annotation>> getConstraintValidators()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.metadata.PropertyMetadata#getLabelName(java.util.Locale)
     */
    public String getLabelName()
    {
        // TODO Auto-generated method stub
        return "XYZ";
    }

    public void setClass(final Class<?> class1)
    {
        m_class = class1;
        
    }

	public MessageSourceAccessor getMessageSourceAccessor() {
		return m_messageSourceAccessor;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method getGetMethod() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Method getSetMethod() {
		// TODO Auto-generated method stub
		return null;
	}

}
