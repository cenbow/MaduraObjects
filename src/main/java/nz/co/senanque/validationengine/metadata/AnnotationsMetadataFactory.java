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

package nz.co.senanque.validationengine.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;

import nz.co.senanque.validationengine.ValidationUtils;
import nz.co.senanque.validationengine.annotations.ChoiceList;
import nz.co.senanque.validationengine.annotations.Description;
import nz.co.senanque.validationengine.annotations.Inactive;
import nz.co.senanque.validationengine.annotations.Label;
import nz.co.senanque.validationengine.annotations.MapField;
import nz.co.senanque.validationengine.annotations.ReadOnly;
import nz.co.senanque.validationengine.annotations.ReadPermission;
import nz.co.senanque.validationengine.annotations.Required;
import nz.co.senanque.validationengine.annotations.Secret;
import nz.co.senanque.validationengine.annotations.Unknown;
import nz.co.senanque.validationengine.annotations.WritePermission;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;
import nz.co.senanque.validationengine.choicelists.ChoiceListFactory;
import nz.co.senanque.validationengine.choicelists.ChoiceListFactoryImpl;
import nz.co.senanque.validationengine.fieldvalidators.FieldValidator;
import nz.co.senanque.validationengine.fieldvalidators.Validators;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.io.Resource;

/**
 * Generates the static metadata for all relevant classes
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.17 $
 */
public class AnnotationsMetadataFactory implements FactoryBean<EngineMetadata>, BeanFactoryAware, InitializingBean, MessageSourceAware
{
	private static final Logger log = LoggerFactory.getLogger(AnnotationsMetadataFactory.class);
	
    private transient Set<Class<?>> m_classes;
    private transient List<String> m_packages = new ArrayList<String>();
    private transient String m_package;
    private transient Object m_objectFactory;
    private transient Document m_choicesDocument;
    private transient Map<String,List<ChoiceBase>> m_choicesMap;
    private transient Map<String,ChoiceListFactory> m_choiceListFactories = new HashMap<String,ChoiceListFactory>();

	private transient BeanFactory m_beanFactory;
    
	public EngineMetadata getObject() throws Exception {
	    
	    createChoicesMap();
	    
	    final Map<Class<Annotation>, Class<? extends FieldValidator<Annotation>>> validatorMap = getValidatorMap();
	    final Map<Class<?>,ClassMetadata> classMap = new HashMap<Class<?>,ClassMetadata>();
	    final MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(getMessageSource());

		for (Class<?> clazz: m_classes)
		{
			log.debug("class name {}",clazz);
			boolean classNeeded = true;
			final ClassMetadata classMetadata = new ClassMetadata();		
			for (Method method: clazz.getMethods())
			{

                log.debug("method.getName() {}",method.getName());
                String mname = method.getName();
			    final String fieldName = ValidationUtils.getFieldNameFromGetterMethodName(method.getName());
			    if (fieldName == null)
			    {
			        continue;
			    }
                if (fieldName.equals("metadata"))
                {
                    continue;
                }
                if (fieldName.equals("class"))
                {
                    continue;
                }
                final PropertyMetadataImpl fieldMetadata = new PropertyMetadataImpl(clazz,fieldName, messageSourceAccessor);
				boolean fieldNeeded = false;
				for (Annotation fieldAnnotation: method.getAnnotations())
				{
					//log.debug("field annotation {}",fieldAnnotation);
                    if (fieldAnnotation instanceof Label)
                    {
                        fieldMetadata.setLabelName(((Label)fieldAnnotation).labelName());
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof Inactive)
                    {
                        fieldMetadata.setInactive(true);
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof ReadOnly)
                    {
                        fieldMetadata.setReadOnly(true);
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof Secret)
                    {
                        fieldMetadata.setSecret(true);
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof Unknown)
                    {
                        fieldMetadata.setUnknown(true);
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof Required)
                    {
                        fieldMetadata.setRequired(true);
                        fieldNeeded = true;
                    }
//						if (fieldAnnotation instanceof Range)
//						{
//							fieldMetadata.setMinValue(((Range)fieldAnnotation).minValue());
//							fieldMetadata.setMaxValue(((Range)fieldAnnotation).maxValue());
//							fieldNeeded = true;
//						}
					if (fieldAnnotation instanceof Description)
					{
						fieldMetadata.setDescription(((Description)fieldAnnotation).name());
						fieldNeeded = true;
					}
					if (fieldAnnotation instanceof Id)
					{
						fieldMetadata.setIdentifier(true);
					}
//						if (fieldAnnotation instanceof Regex)
//						{
//							fieldMetadata.setRegex(((Regex)fieldAnnotation).regex());
//							fieldNeeded = true;
//						}
//						if (fieldAnnotation instanceof BeanValidator)
//						{
//							fieldMetadata.setBean(((BeanValidator)fieldAnnotation).bean());
//							fieldMetadata.setParam(((BeanValidator)fieldAnnotation).param());
//							fieldNeeded = true;
//						}
                    if (fieldAnnotation instanceof MapField)
                    {
                        fieldMetadata.setMapField(((MapField)fieldAnnotation).name());
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof WritePermission)
                    {
                        fieldMetadata.setPermission(((WritePermission)fieldAnnotation).name());
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof ReadPermission)
                    {
                        fieldMetadata.setReadPermission(((ReadPermission)fieldAnnotation).name());
                        fieldNeeded = true;
                    }
                    if (fieldAnnotation instanceof ChoiceList)
                    {
                        final List<ChoiceBase> choiceList = m_choicesMap.get(((ChoiceList)fieldAnnotation).name());
                        fieldMetadata.setChoiceList(choiceList);
                        fieldNeeded = true;
                    }
                    Class<? extends FieldValidator<Annotation>> fvClass = validatorMap.get(fieldAnnotation.annotationType());
                    if (fvClass != null)
                    {
                        final FieldValidator<Annotation> fv = fvClass.newInstance();
                        fv.init(fieldAnnotation,fieldMetadata);
                        fieldMetadata.addConstraintValidator(fv);
                        fieldNeeded = true;
                   }                        
				}
				Field field;
				try {
					field = clazz.getField(mname);
					for (Annotation fieldAnnotation: field.getAnnotations())
					{
						if (fieldAnnotation instanceof XmlElement)
						{
							if (((XmlElement)fieldAnnotation).required())
							{
		                        fieldMetadata.setRequired(true);
		                        fieldNeeded = true;							
							}
						}
					}
				} catch (NoSuchFieldException e) {
					// ignore
				}
                Class<?> returnClass = method.getReturnType();
                Object[] t = returnClass.getEnumConstants();
                if (t != null)
                {
                    fieldNeeded = true;
                    fieldMetadata.setChoiceList(t);
                }
				if (!fieldNeeded)
				{
					if (m_classes.contains(returnClass) || returnClass.isAssignableFrom(List.class))
					{
						fieldNeeded = true;
					}
				}
				if (fieldNeeded)
				{
	                log.debug("fieldName added to metadata {}",fieldName);
					classMetadata.addField(fieldName,fieldMetadata);
					classNeeded = true;
				}
			}
			if (classNeeded)
			{
                log.debug("Class added to metadata {}",clazz.getName());
				classMap.put(clazz, classMetadata);
			}
		}
		return new EngineMetadata(classMap, m_choicesDocument);
	}
	
    private void createChoicesMap()
    {
        // TODO Auto-generated method stub
        
    }

//    private void figureTableRestriction(TableRestrictionBuilder tableRestrictionBuilder,Map<Class<?>,ClassMetadata> classMap) throws Exception
//    {
//        Class<?> clazz = tableRestrictionBuilder.getClazz();
//        ClassMetadata classMetadata = classMap.get(clazz);
//        if (classMetadata == null)
//        {
//            throw new RuntimeException("Invalid class specified: "+clazz);
//        }
//        Map<PropertyMetadataImpl,Object> fieldMap = new HashMap<PropertyMetadataImpl,Object>();
//        
//        for (String f:tableRestrictionBuilder.getColumns())
//        {
//            PropertyMetadataImpl pm = classMetadata.getField(f);
//            if (pm == null)
//            {
//                throw new RuntimeException("Invalid field name: "+f+" on class "+clazz);
//            }
//            fieldMap.put(pm,pm);
//        }
//        Set<PropertyMetadataImpl> fields = fieldMap.keySet();
//        PropertyMetadataImpl[] propertyMetadataArray = fields.toArray(new PropertyMetadataImpl[fields.size()]);
//        List<TableRestrictionRow> rows = tableRestrictionBuilder.populateTableRestriction(propertyMetadataArray);
//        TableRestriction tr = new TableRestriction(classMetadata,fieldMap,rows);
//        for (PropertyMetadata pm: fieldMap.keySet())
//        {
//            pm.addTableRestriction(tr);
//        }
//    }
//

	public Class<EngineMetadata> getObjectType() {
		return EngineMetadata.class;
	}

	public boolean isSingleton() {
		return true;
	}

    public Map<String,List<ChoiceBase>> getChoicesMap()
    {
        return m_choicesMap;
    }

    public Map<Class<Annotation>, Class<? extends FieldValidator<Annotation>>> getValidatorMap()
    {
        Map<Class<Annotation>,Class<? extends FieldValidator<Annotation>>> ret = new HashMap<Class<Annotation>,Class<? extends FieldValidator<Annotation>>>();
        try
        {
            for (Class<? extends FieldValidator<Annotation>> class_:Validators.s_validators)
            {
                Method[] methods = class_.getMethods();
                for (Method method: methods)
                {
                    if (method.getName().equals("init"))
                    {
                        final Class<Annotation> p = (Class<Annotation>)method.getParameterTypes()[0];
                        ret.put(p,class_);
                        break;
                    }
                }
            }
        }
        catch (Exception e)
        {
            // we really don't expect any errors here
            throw new RuntimeException(e);
        }
        if (m_fieldValidators != null)
        {
            for (Class<? extends FieldValidator<Annotation>> class_: m_fieldValidators)
            {
                Method[] methods = class_.getMethods();
                for (Method method: methods)
                {
                    if (method.getName().equals("init"))
                    {
                        final Class<Annotation> p = (Class<Annotation>)method.getParameterTypes()[0];
                        ret.put(p,class_);
                        break;
                    }
                }
            }
        }
        return ret;
    }

    private List<Class<? extends FieldValidator<Annotation>>> m_fieldValidators;

	private MessageSource m_messageSource;

    public List<Class<? extends FieldValidator<Annotation>>> getFieldValidators()
    {
        return m_fieldValidators;
    }
    public void setFieldValidators(final List<Class<? extends FieldValidator<Annotation>>> fieldValidators)
    {
        m_fieldValidators = fieldValidators;
    }

    public void setChoicesDocument(final Resource choicesDocument) throws Exception
    {
		SAXBuilder saxBuilder = new SAXBuilder();
		m_choicesDocument = saxBuilder.build(choicesDocument.getInputStream());
    }
    public void createChoiceMap(final Document document)
    {
        m_choicesMap = new HashMap<String,List<ChoiceBase>>();
        ChoiceListFactory defaultFactory = new ChoiceListFactoryImpl(new MessageSourceAccessor(m_messageSource));
        for (Element choicebases: (List<Element>)document.getRootElement().getChildren("ChoiceList"))
        {
            String name = choicebases.getAttributeValue("name");
            ChoiceListFactory clf = getChoiceListFactories().get(name);
            List<ChoiceBase> choiceBases = ((clf==null)?defaultFactory:clf).getChoiceList(choicebases);
            m_choicesMap.put(name, choiceBases);
        }
    }

    public Map<String, ChoiceListFactory> getChoiceListFactories()
    {
        return m_choiceListFactories;
    }

    public void setChoiceListFactories(
            Map<String, ChoiceListFactory> choiceListFactories)
    {
        m_choiceListFactories = choiceListFactories;
    }
    public Set<Class<?>> getClasses() 
    {
        return m_classes;
    }

    public void setClasses(final Set<Class<?>> classes) 
    {
        m_classes = classes;
    }

    public List<String> getPackages()
    {
        return m_packages;
    }

    public void setPackages(List<String> packages)
    {
        m_packages = packages;
    }

    public void afterPropertiesSet() throws Exception
    {
        Set<Class<?>> classes = getClasses();
        if (classes == null)
        {
            classes = new HashSet<Class<?>>();
        }
        if (classes.isEmpty())
        {
        	// If we do not have an explicit list of classes then we use the package name.
        	// But if we do that then we make sure we also use the classloader the beanfactory is using
        	// falling back to the root classloader.
        	ClassLoader classLoader = this.getClass().getClassLoader();
        	if (m_beanFactory instanceof AbstractBeanFactory)
        	{
        		classLoader = ((AbstractBeanFactory)m_beanFactory).getBeanClassLoader();
        	}
	        List<String> packages = getPackages();
	        if (m_package != null)
	        {
	            packages.add(m_package);
	        }
	        for (String packageName: packages)
	        {
	            Class<?> clazz=null;
	            try
	            {
	                clazz = Class.forName(packageName+".ObjectFactory", true, classLoader);
	            }
	            catch (Exception e)
	            {
	                throw new RuntimeException("Failed to find/load ObjectFactory class in "+packageName);
	            }
	            for (Method method: clazz.getMethods())
	            {
	                if (method.getName().startsWith("create"))
	                {
	                    Class<?> c = method.getReturnType();
	                    if (c != javax.xml.bind.JAXBElement.class)
	                    {
	                        classes.add(c);
	                    }
	                }
	            }
	        }
        }
        setClasses(classes);
        createChoiceMap(m_choicesDocument);
    }

    public String getPackage()
    {
        return m_package;
    }

    public void setPackage(String package1)
    {
        m_package = package1;
    }

	public Object getObjectFactory() {
		return m_objectFactory;
	}

	public void setObjectFactory(Object objectFactory) {
		m_objectFactory = objectFactory;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		m_beanFactory = beanFactory;
		
	}

	public MessageSource getMessageSource() {
		return m_messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		m_messageSource = messageSource;
	}
    
}
