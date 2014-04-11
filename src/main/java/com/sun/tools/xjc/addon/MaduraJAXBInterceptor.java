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
package com.sun.tools.xjc.addon;


import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import nz.co.senanque.validationengine.ListeningArray;
import nz.co.senanque.validationengine.ValidationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldRef;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.Outline;

/**
 * 
 * This is the plugin that is called when you use the -Xmadura-objects switch on XJC
 * It injects the validation interceptors into the code as well as the metadata interface.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.5 $
 */
public class MaduraJAXBInterceptor extends Plugin {
	
	public static final String NAMESPACE = "http://madura.senanque.co.nz/schemas/customizations";
	private static final Logger log = LoggerFactory.getLogger(MaduraJAXBInterceptor.class);

	   public String getOptionName() {
	        return "Xmadura-objects";
	    }

	    public List<String> getCustomizationURIs() {
	        return Collections.singletonList(NAMESPACE);
	    }

	    public boolean isCustomizationTagName(final String nsUri, final String localName) {
	        return NAMESPACE.equals(nsUri) && "code".equals(localName);
	    }

	    public String getUsage() {
	        return "  -Xmadura-objects      :  inject madura code fragments into the generated code";
	    }

//        public boolean run(Outline outline, com.sun.tools.xjc.Options opt,
//                ErrorHandler errorHandler) throws SAXException
//        {
//            // TODO Auto-generated method stub
//            return false;
//        }
	    public boolean run(final Outline model, final Options opt, final ErrorHandler errorHandler) {

	    	
//            CPluginCustomization c = co.target.getCustomizations().find(NS,"code");
//            if(c==null)
//                continue;   // no customization --- nothing to inject here

//            c.markAsAcknowledged();
            // TODO: ideally you should validate this DOM element to make sure
            // that there's no typo/etc. JAXP 1.3 can do this very easily.
//            String codeFragment = DOMUtils.getElementText(c.element);

            // inject the specified code fragment into the implementation class.
//            co.implClass.direct("private ValidationEngineFactory m_validationEngineFactory = ValidationEngineFactory.getInstance();");
	    	//JClass validationEngineFactoryClass=model.getCodeModel().ref(nz.co.senanque.validationengine.ValidationEngineFactory.class);
            //final JClass validationEngineClass=model.getCodeModel().ref(nz.co.senanque.validationengine.ValidationEngine.class);
            final JClass listeningArrayClass=model.getCodeModel().ref(ListeningArray.class);
            final JClass ObjectMetadataClass=model.getCodeModel().ref(nz.co.senanque.validationengine.ObjectMetadata.class);
//            final JClass ValidationSessionClass=model.getCodeModel().ref(nz.co.senanque.validationengine.ValidationSession.class);
            final JClass ValidationUtilsClass=model.getCodeModel().ref(nz.co.senanque.validationengine.ValidationUtils.class);
            final JClass businessObjectInterface=model.getCodeModel().ref(nz.co.senanque.validationengine.ValidationObject.class);
//            final JType[] jTypes = JTypeUtils.getBasicTypes(model.getCodeModel());
            Collection<ClassOutline> allClasses = (Collection<ClassOutline>) model.getClasses();
            for( ClassOutline co : model.getClasses() ) 
            {
                ClassOutline extendingClass = co.getSuperClass();
                boolean notExtends = (extendingClass == null);
                if (notExtends)
                {
                    processClass(co, allClasses, listeningArrayClass, model, ObjectMetadataClass, businessObjectInterface,notExtends,ValidationUtilsClass);
                }
            }
            for( ClassOutline co : model.getClasses() ) 
            {
                ClassOutline extendingClass = co.getSuperClass();
                boolean notExtends = (extendingClass == null);
                if (!notExtends)
                {
                    processClass(co, allClasses, listeningArrayClass, model, ObjectMetadataClass, businessObjectInterface,notExtends,ValidationUtilsClass);
                }
            }

	        return true;
	    }

	    private void processClass(ClassOutline co, Collection<ClassOutline> allClasses, final JClass listeningArrayClass, final Outline model, final JClass ObjectMetadataClass, final JClass businessObjectInterface, boolean notExtends, JClass ValidationUtilsClass)
	    {
            JFieldVar metadata = null;
            JFieldVar validationSession = null;
            JMethod metadataGetter = null;
            if (notExtends)
            {
                validationSession = co.implClass.field(JMod.PROTECTED, nz.co.senanque.validationengine.ValidationSession.class, "m_validationSession");
                validationSession.annotate(XmlTransient.class);
                metadata = co.implClass.field(JMod.PROTECTED, nz.co.senanque.validationengine.ObjectMetadata.class, "m_metadata");
                metadata.annotate(XmlTransient.class);
                metadataGetter = co.implClass.method(JMod.PUBLIC, ObjectMetadataClass, "getMetadata");
                JBlock jBlock = metadataGetter.body();
                JConditional ifBlock = jBlock._if(metadata.eq(JExpr._null()));
                ifBlock._then().block().assign(metadata, JExpr._new(ObjectMetadataClass));
                metadataGetter.body()._return(metadata);
                metadataGetter.annotate(Transient.class);

//                metadataGetter = co.implClass.getMethod("getMetadata",new JType[0]);
            }
            else
            {
                Map<String,JFieldVar> superFields = co.getSuperClass().implClass.fields();
                validationSession = superFields.get("m_validationSession");
                metadata = superFields.get("m_metadata");
                metadataGetter = co.getSuperClass().implClass.getMethod("getMetadata",new JType[0]);
            }
            
            Map<String,JFieldVar> fields = co.implClass.fields();
//            XSComponent xsComponent = co.target.getSchemaComponent();
            Collection<JMethod> allMethods = co.implClass.methods();
            for (JMethod jMethod: allMethods)
            {
                String fieldName = ValidationUtils.getFieldNameFromSetterMethodName(jMethod.name());
                if (fieldName != null)
                {
                    JBlock jBlock = jMethod.body();
                    log.debug("Method name: {} pos {}",jMethod.name(),jBlock.pos());
                    JFieldVar constant = co.implClass.field(JMod.PUBLIC|JMod.STATIC|JMod.FINAL, String.class, fieldName.toUpperCase(),JExpr.lit(fieldName));
                    constant.annotate(XmlTransient.class);
                    JFieldVar fieldVar = fields.get(fieldName);
                    if (fieldVar == null)
                    {
                        continue;
                    }
                    jBlock.pos(0);
//                    jBlock.invoke(metadataGetter).invoke(ObjectMetadataClass.).arg(fieldName);
                    jBlock.directStatement("getMetadata().removeUnknown(\""+fieldName+"\");");
                    JConditional ifBlock = jBlock._if(validationSession.ne(JExpr._null()));
                    JVar[] params = jMethod.listParams();
                    ifBlock._then().block().invoke(validationSession, "set")
                        .arg(JExpr._this())
                        .arg(fieldName)
                        .arg(params[0])
                        .arg(fieldVar);
                }
                fieldName = ValidationUtils.getFieldNameFromGetterMethodName(jMethod.name());
                if (fieldName != null)
                {
                    JBlock jBlock = jMethod.body();
                    log.debug("Method name: {} pos {}",jMethod.name(),jBlock.pos());
                    jBlock.pos(0);
                    JConditional ifBlock = jBlock._if(validationSession.ne(JExpr._null()));
                    ifBlock._then().block().invoke(validationSession, "clean").arg(JExpr._this());
                    String methodTypeName = jMethod.type().name();
                    if (methodTypeName.startsWith("List"))
                    {
                        log.debug("return type: {}",methodTypeName);
                        JFieldRef jFieldRef = JExpr.ref(fieldName);
                        ifBlock = jBlock._if(jFieldRef.eq(JExpr._null()));
                        String elementClass = methodTypeName.substring(5,methodTypeName.length()-1);
                        JClass clazz = null;
                        for (ClassOutline classOutline: allClasses)
                        {
                            String cname = classOutline.implClass.name();
                            if (cname.equals(elementClass))
                            {
                                clazz = classOutline.implClass;
                                break;
                            }
                        }
                        if (clazz == null)
                        {
                            throw new RuntimeException("No class found for "+elementClass);
                        }
                        JType ret = listeningArrayClass.narrow(clazz);
                        ifBlock._then().block().assign(JExpr.ref(fieldName), JExpr._new(ret));
                    }
                }
            }
            if (notExtends)
            {
            	JMethod constructor = co.implClass.constructor(JMod.PUBLIC);
            	constructor.body().add(ValidationUtilsClass.staticInvoke("setDefaults").arg(JExpr._this()));
                JMethod validateSessionSetter = co.implClass.method(JMod.PUBLIC, model.getCodeModel().VOID, "setValidationSession");
                JVar validateSessionParam = validateSessionSetter.param(nz.co.senanque.validationengine.ValidationSession.class, "validationSession");
                validateSessionSetter.body().assign(validationSession, validateSessionParam);
                validateSessionSetter.annotate(XmlTransient.class);

//                JMethod metadataGetter = co.implClass.method(JMod.PUBLIC, ObjectMetadataClass, "getMetadata");
//                JBlock jBlock = metadataGetter.body();
//                JConditional ifBlock = jBlock._if(metadata.eq(JExpr._null()));
//                ifBlock._then().block().assign(metadata, JExpr._new(ObjectMetadataClass));
//                metadataGetter.body()._return(metadata);
//                metadataGetter.annotate(Transient.class);
            }
            
            co.implClass._implements(businessObjectInterface);

	    }
}
