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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import nz.co.senanque.validationengine.annotations.Ignore;
import nz.co.senanque.validationengine.annotations.Unknown;

import org.springframework.util.StringUtils;

/**
 * Various utilities
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.6 $
 */
public final class ValidationUtils
{
    private ValidationUtils()
    {
        // Ensures class cannot be instantiated
    }
    private static boolean isUsefulField(String property, Class<?> clazz)
    {
    	if (property == null) {
    		return false;
    	}
        if (property.equals("validationSession"))
        {
            return false;
        }
        if (property.equals("class"))
        {
            return false;
        }
        if (property.equals("id"))
        {
            return false;
        }
        if (property.equals("parentid"))
        {
            return false;
        }
        if (property.equals("metadata"))
        {
            return false;
        }
        if (property.equals("version"))
        {
            return false;
        }
        if (clazz != null && List.class.isAssignableFrom(clazz))
        {
            return false;
        }
        return true;
    }
    
    
    public static Map<String,Property> getProperties(Class<? extends ValidationObject> class1) {
    	Map<String,Property> ret = new HashMap<>(class1.getMethods().length);
		for (Method method: class1.getMethods())
		{
		    final String fieldName = ValidationUtils.getFieldNameFromGetterMethodName(method.getName());
            if (!ValidationUtils.isUsefulField(fieldName, null))
            {
                continue;
            }
            Method getter = ValidationUtils.figureGetter(fieldName,class1);
            if (getter.isAnnotationPresent(Ignore.class)) {
            	continue;
            }
            Method setter = ValidationUtils.figureSetter(fieldName,class1);
            ret.put(fieldName,new Property(fieldName,getter,setter,class1));
        }
        return ret;
    	
    }

    public static void setDefaults(ValidationObject object)
    {
		for (Field field : object.getClass().getDeclaredFields())
		{
			XmlElement xmlElement = field.getAnnotation(XmlElement.class);
			if (xmlElement != null && hasText(xmlElement.defaultValue()))
			{
				String value = xmlElement.defaultValue();
				Method setter = figureSetter(field.getName(),object.getClass());
				Class<?> type = setter.getParameterTypes()[0];
				try {
					setter.invoke(object, ConvertUtils.convertToObject(type,value,null));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			String n = field.getName();
			if (n.startsWith("m_"))
			{
				n=n.substring(2);
			}
			try {
				Method getter = figureGetter(n,object.getClass());
				Annotation unknown = getter.getAnnotation(Unknown.class);
				if (unknown != null)
				{
					object.getMetadata().addUnknown(field.getName());
				}
			} catch (RuntimeException e) {
				// ignore
			}
		}
    }
    private static boolean hasText(String value)
    {
    	if (!StringUtils.hasText(value))
    	{
    		// this means no default was set
    		return false;
    	}
    	if (value.length()==1 && value.charAt(0) <= ' ')
    	{
    		// this means no default was set
    		return false;
    	}
//    	if (value.equals(FieldMetadata.UNKNOWN.getName()))
//    	{
//    		// ignore unknowns
//    		return false;
//    	}
    	return true;
    }
    public static String getFieldNameFromGetterMethodName(final String name)
    {
        if (name == null)
        {
            return null;
        }
        String n = null;
        if (name.startsWith("get"))
        {
            n = name.substring(3);
        } else if  (name.startsWith("is"))
        {
            n = name.substring(2);
        }
        return getFieldNameWithJavaCase(n);
    }
    public static String getFieldNameFromSetterMethodName(final String name)
    {
        if (name == null)
        {
            return null;
        }
        String n = null;
        if (name.startsWith("set"))
        {
            n = name.substring(3);
        }
        return getFieldNameWithJavaCase(n);
    }
    /**
     * Make the first character lower case and leave the rest alone.
     * @param name
     * @return the resulting name
     */
    public static String getFieldNameWithJavaCase(final String name)
    {
       return name==null?null:Character.toLowerCase(name.charAt(0))+name.substring(1);
    }
    private static String figureSetter(final String name)
    {
        return name==null?null:"set"+Character.toUpperCase(name.charAt(0))+name.substring(1);
    }
    private static String figureGetter(final String name)
    {
        return name==null?null:"get"+Character.toUpperCase(name.charAt(0))+name.substring(1);
    }
    private static String figureIsGetter(final String name)
    {
        return name==null?null:"is"+Character.toUpperCase(name.charAt(0))+name.substring(1);
    }
    private static Method figureSetter(final String name, final Class<?> clazz)
    {
        final String setter = figureSetter(name);
        for (Method method: clazz.getMethods())
        {
            if (method.getName().equals(setter))
            {
                return method;
            }
        }
        throw new RuntimeException("Could not find method "+setter+" on class "+clazz.getName());
    }
    public static Method figureGetter(final String name, final Class<?> clazz)
    {
        final String getter = figureGetter(name);
        for (Method method: clazz.getMethods())
        {
            if (method.getName().equals(getter))
            {
                return method;
            }
        }
        final String isGetter = figureIsGetter(name);
        for (Method method: clazz.getMethods())
        {
            if (method.getName().equals(isGetter))
            {
                return method;
            }
        }
        throw new RuntimeException("Could not find method "+getter+" on class "+clazz.getName());
    }
    public static Method figureValueOf(final Class<?> clazz)
    {
        Method method=null;
        try
        {
            method = clazz.getMethod("valueOf", java.lang.String.class);
        }
        catch (Exception e)
        {
        }
        return method;
    }
    public static boolean equals(final Object o, final Object o1)
    {
        if (o == null && o1 == null)
        {
            return true;
        }
        if (o == null || o1 == null)
        {
            return false;
        }
        if (o instanceof Number && o1 instanceof Number)
        {
            return ((Number)o).doubleValue() == ((Number)o1).doubleValue();
        }
        return o.equals(o1);
    }
    
    /**
     * From http://www.apply-templates.com/en/blog?title=Getting%20unique%20values%20from%20a%20list%20in%20Java
     * Note that HashMap uses the Object's hash code for a unique identifier.
     *  If this method is overridden, it is possible that HashMaps may not work properly with those Objects.
     * @param <T>
     * @param list
     * @return a list of unique objects from the list
     */
    public static <T extends Object> List<T> getUniques(final List<T> list) {
        //Initiate the necessary variables.
        final List<T> uniques = new ArrayList<T>();
        //We declare the map to be final and it will be garbage
        //collected at the end of this method's execution, making
        //the space used by the map temporary.
        final HashMap<T,T> hm = new HashMap<T,T>();
        //Loop through all of the elements.
        for (T t : list) {
            //If you don't find the object in the map, it is
            //unique and we add it to the uniques list.
            if (hm.get(t) == null) {
                hm.put(t,t);
                uniques.add(t);
            }
        }
        return uniques;
    }
    
    public static Comparable<?> convertTo(final Class<Comparable<?>> clazz, Object obj)
    {
        if (obj == null)
        {
            return null;
        }
        if (clazz.isAssignableFrom(obj.getClass()))
        {
            return (Comparable<?>)obj;
        }
        final String oStr = String.valueOf(obj);
        if (clazz.equals(String.class))
        {
            return oStr;
        }
        if (clazz.equals(Long.class) || clazz.equals(Long.TYPE))
        {
            if (Number.class.isAssignableFrom(obj.getClass()))
            {
                return new Long(((Number)obj).longValue());
            }
            return Long.valueOf(oStr);
        }
        if (clazz.equals(Integer.class) || clazz.equals(Integer.TYPE))
        {
            if (Number.class.isAssignableFrom(obj.getClass()))
            {
                return new Integer(((Number)obj).intValue());
            }
            return Integer.valueOf(oStr);
        }
        if (clazz.equals(Double.class) || clazz.equals(Double.TYPE))
        {
            if (Number.class.isAssignableFrom(obj.getClass()))
            {
                return new Double(((Number)obj).doubleValue());
            }
            return Double.valueOf(oStr);
        }
        if (clazz.equals(Float.class) || clazz.equals(Float.TYPE))
        {
            if (Number.class.isAssignableFrom(obj.getClass()))
            {
                return new Float(((Number)obj).floatValue());
            }
            return Float.valueOf(oStr);
        }
        if (clazz.equals(BigDecimal.class))
        {
            return new BigDecimal(oStr);
        }
        if (clazz.equals(java.util.Date.class))
        {
            return java.sql.Date.valueOf(oStr);
        }
        if (clazz.equals(java.sql.Date.class))
        {
            return java.sql.Date.valueOf(oStr);
        }
        return null;
    }
}
