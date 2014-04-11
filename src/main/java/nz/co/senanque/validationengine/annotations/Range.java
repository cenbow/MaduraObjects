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
 * Checks the value passes the range qualifications on this field.
 * There are four possible messages, so four sample messages:
 * <code><br/>
 * nz.co.senanque.validationengine.range.maxExclusive=Failed range: label={0} must be less than {1} attempted={2}<br/>
 * nz.co.senanque.validationengine.range.maxInclusive=Failed range: label={0} must be less than or equal to {1} attempted={2}<br/>
 * nz.co.senanque.validationengine.range.minExclusive=Failed range: label={0} must be greater than {1} attempted={2}<br/>
 * nz.co.senanque.validationengine.range.minInclusive=Failed range: label={0} must be greater than or equal to {1} attempted={2}<br/>
 * </code>
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.5 $
 */
@Retention(RetentionPolicy.RUNTIME)

public @interface Range {
    String maxExclusive() default "none";
    String minExclusive() default "none";
    String maxInclusive() default "none";
    String minInclusive() default "none";
    String maxExclusiveMessage() default "nz.co.senanque.validationengine.range.maxExclusive";
    String minExclusiveMessage() default "nz.co.senanque.validationengine.range.minExclusive";
    String maxInclusiveMessage() default "nz.co.senanque.validationengine.range.maxInclusive";
    String minInclusiveMessage() default "nz.co.senanque.validationengine.range.minInclusive";

}
