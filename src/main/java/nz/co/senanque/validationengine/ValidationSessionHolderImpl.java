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

import java.io.Serializable;

/**
 * @author Roger Parkinson
 *
 */
public class ValidationSessionHolderImpl implements ValidationSessionHolder, Serializable {
	
	private final ValidationEngine m_validationEngine;
	private ValidationSession m_validationSession;

	public ValidationSessionHolderImpl(ValidationEngine validationEngine) {
		m_validationEngine = validationEngine;
	}
	
	/* (non-Javadoc)
	 * @see nz.co.senanque.validationengine.ValidationSessionHolder#bind(java.lang.Object)
	 */
	@Override
	public void bind(Object context) {
		if (m_validationEngine != null && context instanceof ValidationObject) {
	        m_validationSession = m_validationEngine.createSession();
	        m_validationSession.bind((ValidationObject) context);
		}
	}
	/* (non-Javadoc)
	 * @see nz.co.senanque.validationengine.ValidationSessionHolder#unbind(java.lang.Object)
	 */
	@Override
	public void unbind(Object context) {
		if (m_validationSession != null && context instanceof ValidationObject) {
	        m_validationSession.unbind((ValidationObject) context);
		}
	}
	/* (non-Javadoc)
	 * @see nz.co.senanque.validationengine.ValidationSessionHolder#close()
	 */
	@Override
	public void close() {
		if (m_validationSession != null) {
	        m_validationSession.close();
		}
		m_validationSession = null;
	}

}
