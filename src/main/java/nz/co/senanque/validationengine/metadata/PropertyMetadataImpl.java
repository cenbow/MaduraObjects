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
package nz.co.senanque.validationengine.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import nz.co.senanque.validationengine.ValidationUtils;
import nz.co.senanque.validationengine.choicelists.Choice;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;
import nz.co.senanque.validationengine.fieldvalidators.FieldValidator;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * Describes the static metadata for a field and also implements validation.
 * It implements Spring interfaces so that it can get to messages as well as
 * so that it can delegate validation to a custom bean
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.9 $
 */
public class PropertyMetadataImpl implements PropertyMetadata {
	
	private final transient String m_name;
    private final transient Class<?> m_clazz;
    private final transient Method m_getMethod;
    private final transient Method m_setMethod;
    private final transient Method m_valueOfMethod;
    private transient boolean m_inactive;
    private transient boolean m_readOnly;
    private transient boolean m_required;
//	private String m_regex;
	private transient String m_labelName;
//	private String m_minValue;
//	private String m_maxValue;
    private transient String m_description;
    private transient String m_permission;
//	private String m_bean;
//	private String m_param;
//	private Pattern m_regexPattern;
	private transient String m_mapName;
	private transient List<ChoiceBase> m_choiceList;
    private final transient List<FieldValidator<Annotation>> m_constraintValidators = new ArrayList<FieldValidator<Annotation>>();
    private String m_readPermission;
    private transient boolean m_secret;
	private MessageSourceAccessor m_messageSourceAccessor;
	private boolean m_unknown;
	
	protected PropertyMetadataImpl(final Class<?> clazz, final String fieldName, MessageSourceAccessor messageSourceAccessor)
	{
	    m_name = fieldName;
	    m_labelName = fieldName;
	    m_clazz = clazz;
        m_setMethod = ValidationUtils.figureSetter(fieldName,m_clazz);
        m_getMethod = ValidationUtils.figureGetter(fieldName,m_clazz);
        final Class<?>[] c = m_setMethod.getParameterTypes();
        m_valueOfMethod = ValidationUtils.figureValueOf(c[0]);
        m_messageSourceAccessor = messageSourceAccessor;
	}
	
	public String getName() {
		return m_name;
	}
	/* (non-Javadoc)
     * @see nz.co.senanque.validationengine.metadata.PropertyMetadata#getLabelName(java.util.Locale)
     */
	public String getLabelName()
	{
        return m_messageSourceAccessor.getMessage(
                m_labelName, new Object[]{}, 
                m_labelName);
	}
	public void setLabelName(final String name) {
		m_labelName = name;
	}
//	public String getMinValue() {
//		return m_minValue;
//	}
//	public void setMinValue(String value) {
//		m_minValue = value;
//	}
//	public String getMaxValue() {
//		return m_maxValue;
//	}
//	public void setMaxValue(String value) {
//		m_maxValue = value;
//	}
	public String getDescription() {
        return m_messageSourceAccessor.getMessage(
                m_description, new Object[]{}, 
                m_description);
	}
	public void setDescription(String description) {
		m_description = description;
	}
//	public String getBean() {
//		return m_bean;
//	}
//	public void setBean(String bean) {
//		m_bean = bean;
//	}
//	public String getParam() {
//		return m_param;
//	}
//	public void setParam(String param) {
//		m_param = param;
//	}
//	public String getRegex() {
//		return m_regex;
//	}
//	public void setRegex(String regex) {
//		m_regex = regex;
//		m_regexPattern = java.util.regex.Pattern.compile(regex);
//	}
//	public Pattern getRegexPattern() {
//		return m_regexPattern;
//	}
	public void setMapField(String name) {
		m_mapName = name;
	}
	public String getMapField() {
		return m_mapName;
	}
    public void setChoiceList(List<ChoiceBase> list2)
    {
        m_choiceList = list2;
    }
    public void setChoiceList(final Object[] t)
    {
        final List<ChoiceBase> list = getInternalChoiceList();
        list.clear();
        for (Object o: t)
        {
            try
            {
                final Method m = o.getClass().getMethod("value");
                final String s = (String)m.invoke(o);
                list.add(new ChoiceBase(o,s,m_messageSourceAccessor));
            }
            catch (Exception e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private List<ChoiceBase> getInternalChoiceList()
    {
        if (m_choiceList == null)
        {
            m_choiceList = new ArrayList<ChoiceBase>();
        }
        return m_choiceList;
    }
    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.metadata.PropertyMetadata#getChoiceList()
     */
    public List<ChoiceBase> getChoiceList()
    {
        return m_choiceList;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.metadata.PropertyMetadata#findChoice(java.lang.String)
     */
    public Choice findChoice(String key)
    {
        for (Choice choice: m_choiceList)
        {
            if (choice.getKey().equals(key))
            {
                return choice;
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.metadata.PropertyMetadata#getClazz()
     */
    public Class<?> getClazz()
    {
        return m_clazz;
    }

    public Method getGetMethod()
    {
        return m_getMethod;
    }

    public Method getSetMethod()
    {
        return m_setMethod;
    }

    public void addConstraintValidator(FieldValidator<Annotation> constraintValidator)
    {
        m_constraintValidators.add(constraintValidator);
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.metadata.PropertyMetadata#getConstraintValidators()
     */
    public List<FieldValidator<Annotation>> getConstraintValidators()
    {
        return m_constraintValidators;
    }

    public boolean isInactive()
    {
        return m_inactive;
    }

    public void setInactive(boolean inactive)
    {
        m_inactive = inactive;
    }

    public boolean isReadOnly()
    {
        return m_readOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        m_readOnly = readOnly;
    }

    public boolean isRequired()
    {
        return m_required;
    }

    public void setRequired(boolean required)
    {
        m_required = required;
    }

    public Method getValueOfMethod()
    {
        return m_valueOfMethod;
    }
    public Object convertFromString(String value)
    {
        if (m_valueOfMethod == null)
        {
            return value;
        }
        try
        {
            return m_valueOfMethod.invoke(null, value);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getPermission()
    {
        return m_permission;
    }

    public void setPermission(String permission)
    {
        m_permission = permission;
    }

    public void setReadPermission(String name)
    {
        m_readPermission = name;
        
    }

    public String getReadPermission()
    {
        return m_readPermission;
    }

    public boolean isSecret()
    {
        return m_secret;
    }

    public void setSecret(boolean secret)
    {
        m_secret = secret;
    }

	public MessageSourceAccessor getMessageSourceAccessor() {
		return m_messageSourceAccessor;
	}

	public void setUnknown(boolean unknown) {
		m_unknown = unknown;
	}

	public boolean isUnknown() {
		return m_unknown;
	}

}