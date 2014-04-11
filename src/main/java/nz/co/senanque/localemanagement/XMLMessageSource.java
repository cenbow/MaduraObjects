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
package nz.co.senanque.localemanagement;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.Resource;

/**
 * 
 * Acts as a message source, drawing data from an XML file. This is used for testing rather
 * than production because you can use .properties files just as well as XML.
 * But it is useful as a template for getting the information from a database
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public class XMLMessageSource extends AbstractMessageSource implements InitializingBean
{
    private static final Logger logger = LoggerFactory.getLogger(XMLMessageSource.class);
    private Resource m_resource;
    private Map<Locale,Map<String,String>> m_map = new HashMap<Locale,Map<String,String>>();

    /* (non-Javadoc)
     * @see org.springframework.context.support.AbstractMessageSource#resolveCode(java.lang.String, java.util.Locale)
     */
    @Override
    protected MessageFormat resolveCode(String code, Locale locale)
    {
        String ret = null;
        String country = locale.getCountry();
        String language = locale.getLanguage();
        String variant = locale.getVariant();
        logger.debug("Code {} Initial locale {}",code,locale.toString());
        Locale thisLocale = null;
        if (!StringUtils.isEmpty(variant))
        {
            thisLocale = new Locale(language,country,variant);
            Map<String,String> m = m_map.get(thisLocale);
            if (m != null)
            {
                ret = m.get(code);
            }
            logger.debug("tried locale {} result: {}",thisLocale.toString(),ret);

        }
        if (ret == null)
        {
            if (!StringUtils.isEmpty(country))
            {
                thisLocale = new Locale(language,country);
                Map<String,String> m = m_map.get(thisLocale);
                if (m != null)
                {
                    ret = m.get(code);
                }
                logger.debug("tried locale {} result: {}",thisLocale.toString(),ret);
            }
        }
        if (ret == null)
        {
            if (!StringUtils.isEmpty(language))
            {
                thisLocale = new Locale(language);
                Map<String,String> m = m_map.get(thisLocale);
                if (m != null)
                {
                    ret = m.get(code);
                }
                logger.debug("tried locale {} result: {}",thisLocale.toString(),ret);
            }
        }
        if (ret == null)
        {
           thisLocale = Locale.getDefault();
            Map<String,String> m = m_map.get(thisLocale);
            if (m != null)
            {
                ret = m.get(code);
            }
            logger.debug("tried locale {} result: {}",thisLocale.toString(),ret);
        }
        if (ret == null)
        {
            return null;
        }
        return new MessageFormat(ret, locale);
    }

    public Resource getResource()
    {
        return m_resource;
    }

    public void setResource(Resource resource)
    {
        m_resource = resource;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        m_resource.getInputStream();
        Document doc = null;
        try {
            InputStream in = getResource().getInputStream();
            SAXBuilder sax = new SAXBuilder();
            doc = sax.build(in);
        } 
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        Locale defaultLocale = Locale.getDefault();
        for (Element e: (List<Element>)doc.getRootElement().getChildren("locale"))
        {
            String l = e.getAttributeValue("name");
            if (l == null)
            {
                l = defaultLocale.toString();
            }
            for (Locale locale: Locale.getAvailableLocales())
            {
                if (locale.toString().equals(l))
                {
                    Map<String,String> values = new HashMap<String,String>();
                    m_map.put(locale, values);
                    for (Element e1: (List<Element>)e.getChildren("entry"))
                    {
                        values.put(e1.getAttributeValue("name"), e1.getAttributeValue("value"));
                    }
                }
            }
        }
    }

}
