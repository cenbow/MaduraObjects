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

import java.lang.annotation.Annotation;

/**
 * 
 * Holds a static array of all std validator classes
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public class Validators
{
    public static Class<FieldValidator<Annotation>>[] s_validators = (Class<FieldValidator<Annotation>>[])new Class<?>[]
    		{RegexValidator.class,
	    	LengthValidator.class,
	    	RangeValidator.class,
	    	EmailValidator.class,
	    	DigitsValidator.class};

}
