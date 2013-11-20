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

import java.lang.annotation.Annotation;

import nz.co.senanque.validationengine.metadata.PropertyMetadata;

/**
 * Describes the various Field Level validators. Each validator has an annotation associated with it
 * and the annotation indicates that a field should be validated with the associated validator.
 * The annotation also supplies field-specific arguments to the validator.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.3 $
 */
public interface FieldValidator<A extends Annotation>
{
    public void validate(Object o);
    public void init(A annotation, PropertyMetadata propertyMetadata);
}
