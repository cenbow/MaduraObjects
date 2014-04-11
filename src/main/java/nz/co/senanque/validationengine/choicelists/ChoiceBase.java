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
package nz.co.senanque.validationengine.choicelists;

import org.springframework.context.support.MessageSourceAccessor;


/**
 * This is the usual encapsulation of a choice (from a list of choices) on a field.
 * But anything that implements the Choice interface can act as one.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.5 $
 */
public class ChoiceBase implements Choice
{
    private final transient String m_key;
    private final transient String m_description;
    private final transient MessageSourceAccessor m_messageSourceAccessor;

    public ChoiceBase(final Object o, final String description, final MessageSourceAccessor messageSourceAccessor)
    {
        m_key = o.toString();
        m_description = description;
        m_messageSourceAccessor = messageSourceAccessor;
    }

    /* (non-Javadoc)
     * @see nz.co.senanque.validationengine.choicelists.Choice#getKey()
     */
    public Object getKey()
    {
        return m_key;
    }

    public String getDescription()
    {
        return m_description;
    }
    public boolean equals(final String key)
    {
        return m_key.equals(key);
    }
    
    public String toString()
    {
        return m_messageSourceAccessor.getMessage(m_description, null, m_description);
    }

}
