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
import nz.co.senanque.validationengine.annotations.Digits;
import nz.co.senanque.validationengine.metadata.PropertyMetadata;


/**
 * Used to validate the number of digits. There are two components: integer digits and fractonal digits.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
public class DigitsValidator implements FieldValidator<Digits>
{
    private int m_fractionalDigits;
    private int m_integerDigits;
    private String m_message;
    private PropertyMetadata m_propertyMetadata;
    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.annotations1.FieldValidator#init(java.lang.annotation.Annotation)
     */
    public void init(Digits annotation, PropertyMetadata propertyMetadata)
    {
        m_integerDigits = (annotation.integerDigits()==null)?-1:Integer.parseInt(annotation.integerDigits());
        m_fractionalDigits = (annotation.fractionalDigits()==null)?-1:Integer.parseInt(annotation.fractionalDigits());
        m_propertyMetadata = propertyMetadata;
        m_message = annotation.message();
    }
    public void init(DigitDTO annotation, PropertyMetadata propertyMetadata)
    {
        m_integerDigits = (annotation.integerDigits()==null)?-1:Integer.parseInt(annotation.integerDigits());
        m_fractionalDigits = (annotation.fractionalDigits()==null)?-1:Integer.parseInt(annotation.fractionalDigits());
        m_propertyMetadata = propertyMetadata;
        m_message = annotation.message();
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.annotations1.FieldValidator#validate(java.lang.Object)
     */
    public void validate(Object o)
    {
        String s = String.valueOf(o);
        int i = s.indexOf(".");
        if (i == -1)
        {
            if ((m_integerDigits != -1 && s.length() > m_integerDigits))
            {            	
                String message = m_propertyMetadata.getMessageSourceAccessor().getMessage(m_message, new Object[]{ m_propertyMetadata.getLabelName(), m_integerDigits,m_fractionalDigits, String.valueOf(o) });
                throw new ValidationException(message);
            }
        }
        else
        {
            if (m_integerDigits != -1 && s.length() > m_integerDigits)
            {
                String message = m_propertyMetadata.getMessageSourceAccessor().getMessage(m_message, new Object[]{ m_propertyMetadata.getLabelName(), m_integerDigits,m_fractionalDigits,String.valueOf(o) });
                throw new ValidationException(message);
            }
            if (m_fractionalDigits != -1 && (s.length()-(i+1)) > m_fractionalDigits)
            {
                String message = m_propertyMetadata.getMessageSourceAccessor().getMessage(m_message, new Object[]{ m_propertyMetadata.getLabelName(),m_integerDigits,m_fractionalDigits, String.valueOf(o) });
                throw new ValidationException(message);
            }
        }
   }

}
