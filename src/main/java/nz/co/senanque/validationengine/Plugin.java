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

import java.util.Map;

import nz.co.senanque.validationengine.metadata.EngineMetadata;

/**
 * This describes the plugins to the validation engine
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.7 $
 */
public interface Plugin
{
    void set(ValidationSession session, ProxyField proxyField, Object value);
    void clean(ValidationSession session,ProxyObject proxyObject);
    void close(ValidationSession validationSession);
    void bind(ValidationSession session, Map<ValidationObject, ProxyObject> bound, ProxyField proxyField, ValidationObject owner);
    void init(EngineMetadata engineMetadata);
	void unbind(ValidationSession session, ProxyField owner,ValidationObject object);
	String getStats(ValidationSession session);

}
