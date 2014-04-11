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

import static org.junit.Assert.assertEquals;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import nz.co.senanque.validationengine.annotations.Email;
import nz.co.senanque.validationengine.annotations.Length;
import nz.co.senanque.validationengine.fieldvalidators.EmailValidator;
import nz.co.senanque.validationengine.fieldvalidators.LengthValidator;

import org.junit.Test;

public class AnnotationsTest
{

	@Test
	public void testEmail() {
		checkAnnotation(EmailValidator.class, Email.class);
	}
	@Test
	public void testLength() {
		checkAnnotation(LengthValidator.class, Length.class);
	}
	
	private void checkAnnotation(Class<?> class_, Class<?> annotation) {
        Method[] methods = class_.getMethods();
        for (Method method: methods)
        {
            if (method.getName().equals("init"))
            {
                final Class<Annotation> p = (Class<Annotation>)method.getParameterTypes()[0];
                assertEquals(annotation,p);
                break;
            }
        }		
	}

}
