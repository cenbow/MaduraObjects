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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import nz.co.senanque.validationengine.annotations.Email;
import nz.co.senanque.validationengine.annotations.Length;
import nz.co.senanque.validationengine.annotations.Range;
import nz.co.senanque.validationengine.fieldvalidators.EmailValidator;
import nz.co.senanque.validationengine.fieldvalidators.LengthValidator;
import nz.co.senanque.validationengine.fieldvalidators.RangeValidator;

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
	@Test
	public void testRange() {
		checkAnnotation(RangeValidator.class, Range.class);
	}
	
	private void checkAnnotation(Class<?> class_, Class<?> annotation) {
        Method[] methods = class_.getMethods();
        Type[] types = class_.getGenericInterfaces();
        ParameterizedType t0 = (ParameterizedType)types[0];
        Type a = t0.getActualTypeArguments()[0];
        assertEquals(annotation,a);
//        Class<?>[] interfaces = class_.getInterfaces();
//        Class<?> interface_ = interfaces[0];
//        for (Method method: methods)
//        {
//            if (method.getName().equals("init"))
//            {
//                final Class<? extends Annotation> p = (Class<? extends Annotation>)method.getParameterTypes()[0];
//                assertEquals(annotation,p);
//                break;
//            }
//        }		
	}

}
