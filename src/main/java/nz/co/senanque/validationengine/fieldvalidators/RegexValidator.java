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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nz.co.senanque.validationengine.ValidationException;
import nz.co.senanque.validationengine.annotations.Regex;
import nz.co.senanque.validationengine.metadata.PropertyMetadata;


/**
 * Checks the value against the regex expression
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
public class RegexValidator implements FieldValidator<Regex>
{
    private Pattern m_pattern;
    private String m_message;
    private PropertyMetadata m_propertyMetadata;
    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.annotations1.FieldValidator#init(java.lang.annotation.Annotation)
     */
    public void init(Regex annotation, PropertyMetadata propertyMetadata)
    {
        m_propertyMetadata = propertyMetadata;
        m_pattern = java.util.regex.Pattern.compile(annotation.pattern());
        m_message = annotation.message();
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.annotations1.FieldValidator#validate(java.lang.Object)
     */
    public void validate(Object o)
    {
        Matcher m = m_pattern.matcher(String.valueOf(o));
        if (!m.matches())
        {
            String message = m_propertyMetadata.getMessageSourceAccessor().getMessage(m_message, new Object[]{ m_propertyMetadata.getLabelName(), String.valueOf(o) });
            throw new ValidationException(message);
        }
    }

}
