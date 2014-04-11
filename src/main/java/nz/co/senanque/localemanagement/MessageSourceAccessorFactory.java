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
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;

/**
 * 
 * Simple factory to deliver the message source accessor for non-spring objects
 * which cannot be injected. Yes, I do know about Configurable and I also know it doesn't work everywhere/when
 * Sorry about the static, though.
 * 
 * @author Roger Parkinson
 * @version $Revision:$
 */
@Deprecated
public class MessageSourceAccessorFactory implements MessageSourceAware
{
    private static MessageSourceAccessor m_messageSourceAccessor;
    
    public static MessageSourceAccessor getMessageSourceAccessor()
    {
        return m_messageSourceAccessor;
    }

    public void setMessageSource(MessageSource messageSource)
    {
        m_messageSourceAccessor = new MessageSourceAccessor(messageSource);
    }

}
