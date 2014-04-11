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
package nz.co.senanque.validationengine.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * Checks the value passes the length qualification on this field
 * Failed length: label={0} minlength value={1} maxlength value={2} attempted={3}
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.3 $
 */
@Retention(RetentionPolicy.RUNTIME)

public @interface Length {
	String minLength();
	String maxLength();
    String message() default "nz.co.senanque.validationengine.length";

}
