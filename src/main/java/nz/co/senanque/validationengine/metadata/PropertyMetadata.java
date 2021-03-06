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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import nz.co.senanque.validationengine.choicelists.Choice;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;
import nz.co.senanque.validationengine.fieldvalidators.FieldValidator;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * 
 * Used to hide the metadata internals from callers.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.19 $
 */
public interface PropertyMetadata
{
    String getName();
    String getLabelName();
    List<ChoiceBase> getChoiceList();
    Choice findChoice(String key);
    Class<?> getClazz();
    List<FieldValidator<Annotation>> getConstraintValidators();
    MessageSourceAccessor getMessageSourceAccessor();
    Method getGetMethod();
    Method getSetMethod();
}
