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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom.Document;

/**
 * Holds the static metadata for all classes
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.6 $
 */
public class EngineMetadata
{

    private final transient Map<Class<?>, ClassMetadata> m_classMap;
    private final transient Document m_choicesDocument;

    protected EngineMetadata(final Map<Class<?>, ClassMetadata> classMap, Document choicesDocument)
    {
        m_classMap = classMap;
        m_choicesDocument = choicesDocument;
    }

    public PropertyMetadata getField(Object object, String name)
    {
        final ClassMetadata classMetadata = m_classMap.get(object.getClass());
        if (classMetadata != null)
        {
            return classMetadata.getField(name);
        }
        return null;
    }

    public ClassMetadata getClassMetadata(final Class<?> clazz)
    {
        return m_classMap.get(clazz);
    }
    
    public List<Class<?>> getAllClasses()
    {
        List<Class<?>> ret = new ArrayList<Class<?>>();
        ret.addAll(m_classMap.keySet());
        return ret;
    }

    public Document getChoicesDocument()
    {
        return m_choicesDocument;
    }

}
