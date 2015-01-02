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
package nz.co.senanque.validationengine;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import nz.co.senanque.validationengine.choicelists.ChoiceBase;
import nz.co.senanque.validationengine.metadata.PropertyMetadataImpl;

public interface ProxyField
{

    public abstract String getPath();

    public abstract PropertyMetadataImpl getPropertyMetadata();
    
    public abstract FieldMetadata getFieldMetadata();
    
    public abstract ObjectMetadata getObjectMetadata();

    public abstract boolean isReadOnly();

    public abstract void setReadOnly(final boolean b);

    public abstract void setInActive(final boolean b);

    public abstract boolean isInActive();

    public abstract List<ChoiceBase> getChoiceList();

    public abstract void setValue(Object newValue);

    public abstract void reset();

    public abstract Object getValue();

    public abstract void assign(final Object a);

    public abstract String toString();

    public abstract boolean isRequired();

    public abstract boolean isDerived();

    public abstract void setDerived(boolean derived);

    public abstract String getFieldName();

    public abstract void updateValue();

    public abstract Object fetchValue();

    public abstract void useCurrentValue(boolean useCurrentValue);

    public abstract void setRequired(final boolean b);

    public abstract boolean isExcluded(final String key);

    public abstract void exclude(final String key);

    public abstract Set<String> getExcludes();

    public abstract void clearExclude(final String key);

    public abstract void clearExcludes();

    public abstract ProxyObject getProxyObject();

    public abstract boolean isSecret();

    public abstract Object getInitialValue();

	public abstract void setInitialValue(Object convertedDefaultValue);

	public abstract boolean isNotKnown();
	
	public abstract boolean isUnknown();

	public abstract Method getGetter();

	public abstract List<History> getHistory();
	public abstract void setHistory(List<History> history);
	public abstract boolean expire();
	
}