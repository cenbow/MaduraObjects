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

package nz.co.senanque.validationengine.fieldvalidators;

import nz.co.senanque.validationengine.ValidationException;
import nz.co.senanque.validationengine.annotations.Length;
import nz.co.senanque.validationengine.metadata.PropertyMetadata;


/** 
 * Used to validate the length of the field. There can be a max and/or min length.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.5 $
 */
public class LengthValidator implements FieldValidator<Length>
{
    private int m_minLength;
    private int m_maxLength;
    private String m_message;
    private PropertyMetadata m_propertyMetadata;
    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.annotations1.FieldValidator#init(java.lang.annotation.Annotation)
     */
    public void init(Length annotation, PropertyMetadata propertyMetadata)
    {
        m_minLength = annotation.minLength()==null?-1:Integer.valueOf(annotation.minLength());
        m_maxLength = annotation.maxLength()==null?-1:Integer.valueOf(annotation.maxLength());
        m_propertyMetadata = propertyMetadata;
        m_message = annotation.message();
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.annotations1.FieldValidator#validate(java.lang.Object)
     */
    public void validate(Object o)
    {
        if (o != null && o instanceof String)
        {
            int l = ((String)o).length();
            if (m_minLength != -1 && l < m_minLength)
            {
                String message = m_propertyMetadata.getMessageSourceAccessor().getMessage(m_message, new Object[]{ m_propertyMetadata.getLabelName(), m_minLength,m_maxLength,String.valueOf(o) });
                throw new ValidationException(message);                
            }
            if (m_maxLength != -1 && l > m_maxLength)
            {
                String message = m_propertyMetadata.getMessageSourceAccessor().getMessage(m_message, new Object[]{ m_propertyMetadata.getLabelName(),m_minLength,m_maxLength, String.valueOf(o) });
                throw new ValidationException(message);                
            }
        }
    }

}
