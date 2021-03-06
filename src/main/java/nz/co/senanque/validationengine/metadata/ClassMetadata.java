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
package nz.co.senanque.validationengine.metadata;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates the static metadata for a class
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.9 $
 */
public class ClassMetadata
{
    private final transient Map<String, PropertyMetadata> m_fieldMap = new HashMap<String, PropertyMetadata>();

    protected void addField(final String fieldName, final PropertyMetadata fieldMetadata)
    {
        m_fieldMap.put(fieldName, fieldMetadata);
    }

    public PropertyMetadata getField(final String name)
    {
        return m_fieldMap.get(name);
    }
    
    public Collection<PropertyMetadata> getAllFields() {
    	return Collections.unmodifiableCollection(m_fieldMap.values());
    }

}
