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
package nz.co.senanque.validationengine.fieldvalidators;

import nz.co.senanque.validationengine.ConvertUtils;
import nz.co.senanque.validationengine.ValidationException;
import nz.co.senanque.validationengine.annotations.Range;
import nz.co.senanque.validationengine.metadata.PropertyMetadata;


/**
 * Used to validate a range. The field can be anything that implements the Comparable interface.
 * Ranges are specified as min/max with an inclusive or exclusive option.
 * Each can have a message associated with it.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.6 $
 */
public class RangeValidator implements FieldValidator<Range>
{
    private  String m_maxExclusive;
    private  String m_minExclusive;
    private  String m_maxInclusive;
    private  String m_minInclusive;
    private  String m_maxExclusiveMessage;
    private  String m_minExclusiveMessage;
    private  String m_maxInclusiveMessage;
    private  String m_minInclusiveMessage;
    private  PropertyMetadata m_propertyMetadata;

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.annotations1.FieldValidator#init(java.lang.annotation.Annotation)
     */
    public void init(Range annotation, PropertyMetadata propertyMetadata)
    {
        m_maxExclusive = getValue(annotation.maxExclusive());
        m_minExclusive = getValue(annotation.minExclusive());
        m_maxInclusive = getValue(annotation.maxInclusive());
        m_minInclusive = getValue(annotation.minInclusive());
        
        m_maxExclusiveMessage = annotation.maxExclusiveMessage();
        m_minExclusiveMessage = annotation.minExclusiveMessage();
        m_maxInclusiveMessage = annotation.maxInclusiveMessage();
        m_minInclusiveMessage = annotation.minInclusiveMessage();
        m_propertyMetadata = propertyMetadata;
    }
    public void init(RangeDTO annotation, PropertyMetadata propertyMetadata)
    {
        m_maxExclusive = getValue(annotation.maxExclusive());
        m_minExclusive = getValue(annotation.minExclusive());
        m_maxInclusive = getValue(annotation.maxInclusive());
        m_minInclusive = getValue(annotation.minInclusive());
        
        m_maxExclusiveMessage = annotation.maxExclusiveMessage();
        m_minExclusiveMessage = annotation.minExclusiveMessage();
        m_maxInclusiveMessage = annotation.maxInclusiveMessage();
        m_minInclusiveMessage = annotation.minInclusiveMessage();
        m_propertyMetadata = propertyMetadata;
    }
    private String getValue(String v)
    {
        if (v == null) return null;
        if ("none".equals(v)) return null;
        return v;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.annotations1.FieldValidator#validate(java.lang.Object)
     */
    public void validate(Object o)
    {
        if (o != null && o instanceof Comparable)
        {
            compareMinExclusive((Comparable<?>)o);
            compareMinInclusive((Comparable<?>)o);
            compareMaxExclusive((Comparable<?>)o);
            compareMaxInclusive((Comparable<?>)o);
        }
    }
    private <T> void compareMinExclusive(Comparable<T> o)
    {
        if (m_minExclusive == null) return;
        if (o.compareTo((T) ConvertUtils.convertToComparable((Class<Comparable>)o.getClass(), m_minExclusive, m_propertyMetadata.getMessageSourceAccessor())) <= 0)
        {
            String message = m_propertyMetadata.getMessageSourceAccessor().getMessage(m_minExclusiveMessage, new Object[]{ m_propertyMetadata.getLabelName(),m_minExclusive, String.valueOf(o) });
            throw new ValidationException(message);
        }
    }
    private <T> void compareMinInclusive(Comparable<T> o)
    {
        if (m_minInclusive == null)
        {
            return;
        }
        if (o.compareTo((T) ConvertUtils.convertToComparable((Class<Comparable>)o.getClass(), m_minInclusive, m_propertyMetadata.getMessageSourceAccessor())) < 0)
        {
            String message = m_propertyMetadata.getMessageSourceAccessor().getMessage(m_minInclusiveMessage, new Object[]{ m_propertyMetadata.getLabelName(),m_minInclusive, String.valueOf(o) });
            throw new ValidationException(message);
        }
    }
    private <T> void compareMaxExclusive(Comparable<T> o)
    {
        if (m_maxExclusive == null)
        {
            return;
        }
        if (o.compareTo((T) ConvertUtils.convertToComparable((Class<Comparable>)o.getClass(), m_maxExclusive, m_propertyMetadata.getMessageSourceAccessor())) >= 0)
        {
            String message = m_propertyMetadata.getMessageSourceAccessor().getMessage(m_maxExclusiveMessage, new Object[]{ m_propertyMetadata.getLabelName(),m_maxExclusive, String.valueOf(o) });
            throw new ValidationException(message);
        }
    }
    private <T> void compareMaxInclusive(Comparable<T> o)
    {
        if (m_maxInclusive == null)
        {
            return;
        }
        if (o.compareTo((T) ConvertUtils.convertToComparable( (Class<Comparable>)o.getClass(), m_maxInclusive, m_propertyMetadata.getMessageSourceAccessor())) > 0)
        {
            String message = m_propertyMetadata.getMessageSourceAccessor().getMessage(m_maxInclusiveMessage, new Object[]{ m_propertyMetadata.getLabelName(),m_maxInclusive, String.valueOf(o) });
            throw new ValidationException(message);
        }
    }

}
