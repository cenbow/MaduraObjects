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

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;

import nz.co.senanque.validationengine.metadata.ClassMetadata;

/**
 * Validation engines all implement this
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.12 $
 */
public interface ValidationEngine 
{
	boolean clean(ValidationObject object);
	void set(ValidationObject object, String name, Object newValue, Object currentValue, ValidationSession validationSession) throws ValidationException;
	Map<ValidationObject, ProxyObject> add(ListeningArray<ValidationObject> object, ValidationObject newValue, ValidationSession validationSession) throws ValidationException;
	ClassMetadata getClassMetadata(Class<?> clazz);
	Map<ValidationObject, ProxyObject> bind(ValidationObject object, ValidationSession session, ValidationObject owner);
    void removedFrom(ListeningArray<?> array, ValidationObject o, ValidationSession session);
    void unbind(ValidationSession session, ProxyField proxyField, ValidationObject validationObject, Map<ValidationObject, ProxyObject> boundMap);
    Map<ValidationObject, ProxyObject> addAll(ListeningArray<ValidationObject> listeningArray,
            List<ValidationObject> o, ValidationSession validationSession);
    ValidationSession createSession();
    void close(ValidationSession validationSession);
    MessageSource getMessageSource();
    ValidationSession createSession(Locale locale);
    void unbindAll(ValidationSession session, Map<ValidationObject, ProxyObject> boundMap);
    String getStats(ValidationSession session);
}
