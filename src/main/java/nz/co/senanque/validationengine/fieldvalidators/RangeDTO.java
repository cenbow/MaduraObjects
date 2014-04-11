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

import nz.co.senanque.validationengine.metadata.PropertyMetadata;

/**
 * Used to validate a range. The field can be anything that implements the Comparable interface.
 * Ranges are specified as min/max with an inclusive or exclusive option.
 * Each can have a message associated with it.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.5 $
 */
public class RangeDTO
{
    private  String m_maxExclusive;
    private  String m_minExclusive;
    private  String m_maxInclusive;
    private  String m_minInclusive;
    private String m_maxExclusiveMessage="nz.co.senanque.validationengine.range.maxExclusive";
    private String m_minExclusiveMessage="nz.co.senanque.validationengine.range.minExclusive";
    private String m_maxInclusiveMessage="nz.co.senanque.validationengine.range.maxInclusive";
    private String m_minInclusiveMessage="nz.co.senanque.validationengine.range.minInclusive";
    private  PropertyMetadata m_propertyMetadata;

    public String maxExclusive()
    {
        return m_maxExclusive;
    }
    public void setMaxExclusive(String maxExclusive)
    {
        m_maxExclusive = maxExclusive;
    }
    public String minExclusive()
    {
        return m_minExclusive;
    }
    public void minExclusive(String minExclusive)
    {
        m_minExclusive = minExclusive;
    }
    public String getMaxInclusive()
    {
        return m_maxInclusive;
    }
    public void setMaxInclusive(String maxInclusive)
    {
        m_maxInclusive = maxInclusive;
    }
    public String minInclusive()
    {
        return m_minInclusive;
    }
    public void setMinInclusive(String minInclusive)
    {
        m_minInclusive = minInclusive;
    }
    public String maxExclusiveMessage()
    {
        return m_maxExclusiveMessage;
    }
    public String minExclusiveMessage()
    {
        return m_minExclusiveMessage;
    }
    public String maxInclusiveMessage()
    {
        return m_maxInclusiveMessage;
    }
    public String minInclusiveMessage()
    {
        return m_minInclusiveMessage;
    }
     public PropertyMetadata getPropertyMetadata()
    {
        return m_propertyMetadata;
    }
    public void setPropertyMetadata(final PropertyMetadata propertyMetadata)
    {
        m_propertyMetadata = propertyMetadata;
    }
    public String maxInclusive()
    {
        return m_maxInclusive;
    }
    public void setMinExclusive(String string)
    {
        m_minExclusive = string;
        
    }

}
