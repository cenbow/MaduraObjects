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

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nz.co.senanque.localemanagement.LocaleAwareRuntimeException;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * 
 * Convert an object to a specific class
 * The target class has to be Comparable
 * The source class can be anything, but if there is no conversion we return null.
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public class ConvertUtils
{
    private static String s_message = "nz.co.senanque.validationengine.conversion.failure";
    
    public static Comparable<?> convertToComparable(final Class<?> clazz, Object obj, MessageSourceAccessor messageSourceAccessor)
    {
        try
        {
            return convertTo(clazz,obj);
        }
        catch (Exception e)
        {
            String message = messageSourceAccessor.getMessage(s_message, new Object[]{ obj.getClass().getName(),clazz.getName() });
            throw new ValidationException(message);
        }
    }
    public static Comparable<?> convertTo(Class<?> clazz, Object obj)
    {
        if (obj == null)
        {
            return null;
        }
        if (clazz.isAssignableFrom(obj.getClass()))
        {
            return (Comparable<?>)obj;
        }
        if (clazz.isPrimitive())
        {
            if (clazz.equals(Long.TYPE))
            {
                clazz = Long.class;
            }
            else if (clazz.equals(Integer.TYPE))
            {
                clazz = Integer.class;
            }
            else if (clazz.equals(Float.TYPE))
            {
                clazz = Float.class;
            }
            else if (clazz.equals(Double.TYPE))
            {
                clazz = Double.class;
            }
            else if (clazz.equals(Boolean.TYPE))
            {
                clazz = Boolean.class;
            }
        }
        if (Number.class.isAssignableFrom(clazz))
        {
            if (obj.getClass().equals(String.class))
            {
                obj = new Double((String)obj);
            }
            if (!Number.class.isAssignableFrom(obj.getClass()))
            {
                throw new RuntimeException("Cannot convert from "+obj.getClass().getName()+" to "+clazz.getName());
            }
            Number number = (Number)obj;
            if (clazz.equals(Long.class))
            {
                return new Long(number.longValue());
            }
            if (clazz.equals(Integer.class))
            {
                return new Integer(number.intValue());
            }
            if (clazz.equals(Float.class))
            {
                return new Float(number.floatValue());
            }
            if (clazz.equals(Double.class))
            {
                return new Double(number.doubleValue());
            }
            if (clazz.equals(BigDecimal.class))
            {
                return new BigDecimal(number.doubleValue());
            }
        }
        final String oStr = String.valueOf(obj);
        if (clazz.equals(String.class))
        {
            return oStr;
        }
        if (clazz.equals(java.util.Date.class))
        {
            return java.sql.Date.valueOf(oStr);
        }
        if (clazz.equals(java.sql.Date.class))
        {
            return java.sql.Date.valueOf(oStr);
        }
        if (clazz.equals(Boolean.class))
        {
            return new Boolean(oStr);
        }
        throw new RuntimeException("Cannot convert from "+obj.getClass().getName()+" to "+clazz.getName());
    }
    
    public static Object convertToObject(Class<?> clazz, Object obj, MessageSourceAccessor messageSourceAccessor)
    {
        try
        {
            return convertTo(clazz,obj);
        }
        catch (RuntimeException e)
        {
            if (clazz.isEnum())
            {
                Object o;
                try
                {
//                    Method[] methods = clazz.getMethods();
                    Method fromValueMethod = clazz.getMethod("fromValue",String.class);
                    final String oStr = String.valueOf(obj);
                    o = fromValueMethod.invoke(null,oStr);
                    return o;
                }
                catch (Exception e1)
                {
                }
            }
            if (messageSourceAccessor != null) {
	            String message = messageSourceAccessor.getMessage(s_message, new Object[]{ obj.getClass().getSimpleName(),clazz.getSimpleName() });
	            throw new ValidationException(message);
            } else {
            	throw new RuntimeException("Cannot convert from "+obj.getClass().getName()+" to "+clazz.getName());
            }
        }
    }
    private static final SimpleDateFormat s_dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat s_dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final Date parseDate(String d)
    {
        if (d == null)
        {
            return null;
        }
        try {
            return s_dateFormat.parse(d);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public static final Long parseTimestamp(String d)
    {
        if (d == null)
        {
            return null;
        }
        try {
            return s_dateFormat.parse(d).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public static final String printDate(Date c)
    {
        if (c == null)
        {
            return null;
        }
        return s_dateFormat.format(c.getTime());
    }
    public static final String printTimestamp(Long c)
    {
        if (c == null)
        {
            return null;
        }
        return s_dateFormat.format(c);
    }
    public static final Timestamp parseDateTime(String d)
    {
        if (d == null)
        {
            return null;
        }
         try {
            return new Timestamp(s_dateTimeFormat.parse(d).getTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public static final String printDateTime(Timestamp c)
    {
        if (c == null)
        {
            return null;
        }
        return s_dateTimeFormat.format(c.getTime());
    }
    public static Object getPrimitiveTypeForNull(Class<?> clazz)
    {
        if (clazz.equals(Long.TYPE))
        {
            return 0L;
        }
        else if (clazz.equals(Integer.TYPE))
        {
            return 0;
        }
        else if (clazz.equals(Float.TYPE))
        {
            return 0.0F;
        }
        else if (clazz.equals(Double.TYPE))
        {
            return 0.0D;
        }
        else if (clazz.equals(Boolean.TYPE))
        {
            return false;
        }
        return null;
    }
    public static Object getObjectForNull(Class<?> clazz)
    {
        if (clazz.equals(Long.TYPE))
        {
            return new Long(0);
        }
        else if (clazz.equals(Integer.TYPE))
        {
            return new Integer(0);
        }
        else if (clazz.equals(Float.TYPE))
        {
            return new Float(0.0F);
        }
        else if (clazz.equals(Double.TYPE))
        {
            return new Double(0.0D);
        }
        else if (clazz.equals(Boolean.TYPE))
        {
            return new Boolean(false);
        }
        return null;
    }
    

}
