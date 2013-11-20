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

package nz.co.senanque.validationengine;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Test;

public class ConvertUtilsTest
{

    @Test
    public void testConvertTo()
    {
        Object result;
        
        result = ConvertUtils.convertTo(Long.TYPE, new Double(123.45));
        assertTrue(result instanceof Long);
        
        result = ConvertUtils.convertTo(Long.class, "123.45");
        assertTrue(result instanceof Long);
        
        result = ConvertUtils.convertTo(Double.class, "123.45");
        assertTrue(result instanceof Double);
        
        result = ConvertUtils.convertTo(Long.class, 123L);
        assertTrue(result instanceof Long);
        
        result = ConvertUtils.convertTo(Long.class, new Long(123));
        assertTrue(result instanceof Long);
        
        result = ConvertUtils.convertTo(Double.class, 123L);
        assertTrue(result instanceof Double);

        result = ConvertUtils.convertTo(Double.class, "123.45");
        assertTrue(result instanceof Double);

        result = ConvertUtils.convertTo(String.class, 123L);
        assertTrue(result instanceof String);

        result = ConvertUtils.convertTo(String.class, new BigDecimal("100"));
        assertTrue(result instanceof String);

        result = ConvertUtils.convertTo(Date.class, "2000-08-01");
        assertTrue(result instanceof Date);
        
        Date date = (Date)result;
        result = ConvertUtils.convertTo(Date.class, date);
        assertTrue(result instanceof Date);

        boolean exception = false;
        try
        {
            result = ConvertUtils.convertTo(BigDecimal.class, date);
            assertTrue(result instanceof BigDecimal);
        }
        catch (Exception e)
        {
            exception = true;
        }
        assertTrue(exception);

        result = ConvertUtils.convertTo(BigDecimal.class, "100");
        assertTrue(result instanceof BigDecimal);
    }

}
