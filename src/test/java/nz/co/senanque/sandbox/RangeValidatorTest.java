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
import nz.co.senanque.validationengine.fieldvalidators.RangeDTO;
import nz.co.senanque.validationengine.fieldvalidators.RangeValidator;

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
public class RangeValidatorTest
{
    @Autowired private transient MessageSource m_messageSource;

    @Test
    public void testValidate()
    {
        final RangeValidator rangeValidator = new RangeValidator();
        RangeDTO rangeDTO = new RangeDTO();
        rangeDTO.setMaxInclusive("100");
        rangeDTO.setMinInclusive("10");

        final PropertyMetadataMock propertyMetadata = new PropertyMetadataMock(new MessageSourceAccessor(m_messageSource));
        propertyMetadata.setClass(Integer.class);

        rangeValidator.init(rangeDTO, propertyMetadata);
        
        rangeValidator.validate(Integer.valueOf(90));
        boolean exception = false;
        try
        {
            rangeValidator.validate(Integer.valueOf(190));
        }
        catch (ValidationException e)
        {
            exception =true;
        }
        assertTrue(exception);
        exception = false;
        try
        {
            rangeValidator.validate(Integer.valueOf(5));
        }
        catch (ValidationException e)
        {
            exception =true;
        }
        assertTrue(exception);
        rangeDTO = new RangeDTO();
        rangeDTO.setMaxExclusive("100");
        rangeDTO.setMinExclusive("10");
        rangeValidator.init(rangeDTO, propertyMetadata);
        rangeValidator.validate(Long.valueOf(99));
        exception = false;
        try
        {
            rangeValidator.validate(Long.valueOf(100));
        }
        catch (ValidationException e)
        {
            exception = true;
        }
        assertTrue(exception);
        rangeDTO = new RangeDTO();
        rangeDTO.setMaxExclusive("2009-11-01");
        rangeDTO.setMinExclusive("2009-10-01");
        rangeValidator.init(rangeDTO, propertyMetadata);
        rangeValidator.validate(java.sql.Date.valueOf("2009-10-02"));
        exception = false;
        try
        {
            rangeValidator.validate(java.sql.Date.valueOf("2009-10-01"));
        }
        catch (Exception e)
        {
            exception = true;
        }
        assertTrue(exception);
        exception = false;
        try
        {
            rangeValidator.validate(java.sql.Date.valueOf("2009-11-01"));
        }
        catch (Exception e)
        {
            exception = true;
        }
        assertTrue(exception);
    }

}
