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

package com.sun.tools.xjc.addon;


import java.util.Collection;
import java.util.Collections;
import java.util.List;

import nz.co.senanque.validationengine.ValidationUtils;
import nz.co.senanque.validationengine.annotations.Digits;
import nz.co.senanque.validationengine.annotations.Length;
import nz.co.senanque.validationengine.annotations.Range;
import nz.co.senanque.validationengine.annotations.Regex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JMethod;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.ParticleImpl;
/**
 * 
 * This is the plugin that is called when you use the -Xvalidator switch on XJC
 * It derives validation information from the XSD and adds it as annotations to the target Java.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
public class ValidatorJAXBInterceptor extends Plugin {
	
	public static final String NAMESPACE = "http://madura.senanque.co.nz/schemas/customizations";
	private static final Logger log = LoggerFactory.getLogger(MaduraJAXBInterceptor.class);

	   public String getOptionName() {
	        return "Xvalidator";
	    }

	    public List<String> getCustomizationURIs() {
	        return Collections.singletonList(NAMESPACE);
	    }

	    public boolean isCustomizationTagName(String nsUri, String localName) {
	        return NAMESPACE.equals(nsUri) && "code".equals(localName);
	    }

	    public String getUsage() {
	        return "  -Xvalidator : inject validation annotations";
	    }

	    public boolean run(Outline model, Options opt, ErrorHandler errorHandler) {
	    	
	        for( ClassOutline co : model.getClasses() ) 
	        {
	            final FieldOutline[] fieldOutlineArray = co.getDeclaredFields();	            
	            final Collection<JMethod> allMethods = co.implClass.methods();
	            for (JMethod jMethod: allMethods)
	            {
	                final String fieldName = ValidationUtils.getFieldNameFromGetterMethodName(jMethod.name());
	                if (fieldName != null)
	            	{
	            		log.debug("Method name: {}",jMethod.name());
                        FieldOutline fieldOutline = null;
                        for (FieldOutline fo : fieldOutlineArray)
                        {
                            final String fname = ValidationUtils.getFieldNameWithJavaCase(fo.getPropertyInfo().getName(true));
                            if (fieldName.equals(fname))
                            {
                                fieldOutline = fo;
                                break;
                            }
                        }
                        if (fieldOutline != null)
                        {
                            final XSComponent xsxComponent = fieldOutline.getPropertyInfo().getSchemaComponent();
                            if (xsxComponent instanceof ParticleImpl)
                            {
                                processAnnotations((ParticleImpl)xsxComponent,jMethod);
                            }
                        }
	            	}
	            }
	        }

	        return true;
	    }
	    private void processAnnotations(ParticleImpl p,JMethod jMethod)
	    {
            final XSTerm term = p.getTerm();
            XSElementDecl xsElementDecl = term.asElementDecl();
            XSType xsType  = xsElementDecl.getType();
            XSSimpleType xsSimpleType = xsType.asSimpleType();
            if (xsSimpleType == null)
            {
                return;
            }
            XSFacet patternFacet=xsSimpleType.getFacet(XSFacet.FACET_PATTERN);
            if (patternFacet != null)
            {
                jMethod.annotate(Regex.class).param("pattern", patternFacet.getValue().value);
            }
            XSFacet totalDigitsFacet=xsSimpleType.getFacet(XSFacet.FACET_TOTALDIGITS);
            XSFacet maxExclusiveFacet=xsSimpleType.getFacet(XSFacet.FACET_MAXEXCLUSIVE);
            XSFacet minExclusiveFacet=xsSimpleType.getFacet(XSFacet.FACET_MINEXCLUSIVE);
            XSFacet maxInclusiveFacet=xsSimpleType.getFacet(XSFacet.FACET_MAXINCLUSIVE);
            XSFacet minInclusiveFacet=xsSimpleType.getFacet(XSFacet.FACET_MININCLUSIVE);
            JAnnotationUse rangeAnnotation = null;
            if (maxExclusiveFacet != null)
            {
                if (rangeAnnotation == null)
                {
                    rangeAnnotation = jMethod.annotate(Range.class);
                }
                rangeAnnotation.param("maxExclusive", maxExclusiveFacet.getValue().value);
            }
            if (minExclusiveFacet != null)
            {
                if (rangeAnnotation == null)
                {
                    rangeAnnotation = jMethod.annotate(Range.class);
                }
                rangeAnnotation.param("minExclusive", minExclusiveFacet.getValue().value);
            }
            if (maxInclusiveFacet != null)
            {
                if (rangeAnnotation == null)
                {
                    rangeAnnotation = jMethod.annotate(Range.class);
                }
                rangeAnnotation.param("maxInclusive", maxInclusiveFacet.getValue().value);
            }
            if (minInclusiveFacet != null)
            {
                if (rangeAnnotation == null)
                {
                    rangeAnnotation = jMethod.annotate(Range.class);
                }
                rangeAnnotation.param("minInclusive", minInclusiveFacet.getValue().value);
            }
            if (totalDigitsFacet != null)
            {
                JAnnotationUse digitsAnnotation = jMethod.annotate(Digits.class).param("integerDigits", String.valueOf(Integer.parseInt(totalDigitsFacet.getValue().value)));
                XSFacet fractionDigitsFacet=xsSimpleType.getFacet(XSFacet.FACET_FRACTIONDIGITS);
                if (fractionDigitsFacet != null)
                {
                    digitsAnnotation.param("fractionalDigits", String.valueOf(Integer.parseInt(fractionDigitsFacet.getValue().value)));
                }
            }
            int minLength=getIntegerFacet(xsSimpleType.getFacet(XSFacet.FACET_MINLENGTH));
            int maxLength=getIntegerFacet(xsSimpleType.getFacet(XSFacet.FACET_MAXLENGTH));
            int length=getIntegerFacet(xsSimpleType.getFacet(XSFacet.FACET_LENGTH));
            if (length == 0)
                length = maxLength;
            if (length < maxLength)
            {
                throw new RuntimeException("Inconsistent length and maxlength specified at line "+p.getLocator().getLineNumber());
            }
            if (maxLength == 0 && length > 0)
            {
                maxLength = length;
            }
            if (minLength != 0 || maxLength != 0)
            {
                jMethod.annotate(Length.class).param("minLength", String.valueOf(minLength)).param("maxLength",String.valueOf(maxLength));
            }
//            XSAnnotation xsAnnotation = term.getAnnotation();
//            if (xsAnnotation != null)
//            {
//                BindInfo bindInfo = (BindInfo) xsAnnotation.getAnnotation();
//            }
	    }
	    private int getIntegerFacet(XSFacet facet)
	    {
	        if (facet == null) return 0;
	        String ret = facet.getValue().value;
	        if (ret == null) return 0;
	        return Integer.parseInt(ret);
	    }


}
