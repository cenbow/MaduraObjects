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

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * 
 * A runtime exception that knows about the locale
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
public class LocaleAwareRuntimeException extends RuntimeException
{
    private final String m_localisedMessage;
    
    public LocaleAwareRuntimeException()
    {
        m_localisedMessage = null;
    }

    /**
     * @param message
     */
    public LocaleAwareRuntimeException(String message, Object[] args, MessageSource messageSource)
    {
        super(message);
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
        m_localisedMessage = messageSourceAccessor.getMessage(message,args);
    }

    /**
     * @param cause
     */
    public LocaleAwareRuntimeException(Throwable cause)
    {
        super(cause);
        m_localisedMessage = null;
    }

    /**
     * @param message
     * @param cause
     */
    public LocaleAwareRuntimeException(String message, Object[] args, Throwable cause, MessageSource messageSource)
    {
        super(message, cause);
		MessageSourceAccessor messageSourceAccessor = new MessageSourceAccessor(messageSource);
        m_localisedMessage = messageSourceAccessor.getMessage(message,args);
    }
    
    public String getLocalizedMessage()
    {
        return (m_localisedMessage == null)?getMessage():m_localisedMessage;
    }

}
