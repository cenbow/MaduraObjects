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
package nz.co.senanque.sandbox;

import static org.junit.Assert.assertTrue;
import nz.co.senanque.validationengine.ValidationException;
import nz.co.senanque.validationengine.fieldvalidators.DigitDTO;
import nz.co.senanque.validationengine.fieldvalidators.DigitsValidator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * Short description
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/TestBase-spring.xml"})
public class DigitsValidatorTest
{
    @Autowired private transient MessageSource m_messageSource;

    @Test
    public void testValidate()
    {
        final DigitDTO digitDTO = new DigitDTO();
        final PropertyMetadataMock propertyMetadata = new PropertyMetadataMock(new MessageSourceAccessor(m_messageSource));
        propertyMetadata.setClass(Integer.class);
        final DigitsValidator digitsValidator = new DigitsValidator();
        digitDTO.setIntegerDigits("9");
        digitDTO.setFractionalDigits("2");
        digitsValidator.init(digitDTO, propertyMetadata);
        
        digitsValidator.validate(new Double("12.34"));
        boolean exception = false;
        try
        {
            digitsValidator.validate(new Double("12.345"));
        }
        catch (ValidationException e)
        {
            exception =true;
        }
        assertTrue(exception);
        digitsValidator.validate(new Double("1234.34"));
        
    }

	public MessageSource getMessageSource() {
		return m_messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
	}

}
