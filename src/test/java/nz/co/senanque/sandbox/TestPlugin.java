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
package nz.co.senanque.sandbox;

import java.util.Map;

import nz.co.senanque.validationengine.Plugin;
import nz.co.senanque.validationengine.ProxyField;
import nz.co.senanque.validationengine.ProxyObject;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationSession;
import nz.co.senanque.validationengine.metadata.EngineMetadata;

/**
 * @author Roger Parkinson
 *
 */
public class TestPlugin implements Plugin {

	/* (non-Javadoc)
	 * @see nz.co.senanque.validationengine.Plugin#set(nz.co.senanque.validationengine.ValidationSession, nz.co.senanque.validationengine.ProxyField, java.lang.Object)
	 */
	@Override
	public void set(ValidationSession session, ProxyField proxyField,
			Object value) {
		String fieldName = proxyField.getFieldName();
		if ("amountWithHistory".equals(fieldName)) {
			if (((Double)value).doubleValue() > 500) {
				throw new RuntimeException("failed value");
			}
		}

	}

	/* (non-Javadoc)
	 * @see nz.co.senanque.validationengine.Plugin#clean(nz.co.senanque.validationengine.ValidationSession, nz.co.senanque.validationengine.ProxyObject)
	 */
	@Override
	public void clean(ValidationSession session, ProxyObject proxyObject) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nz.co.senanque.validationengine.Plugin#close(nz.co.senanque.validationengine.ValidationSession)
	 */
	@Override
	public void close(ValidationSession validationSession) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nz.co.senanque.validationengine.Plugin#bind(nz.co.senanque.validationengine.ValidationSession, java.util.Map, nz.co.senanque.validationengine.ProxyField, nz.co.senanque.validationengine.ValidationObject)
	 */
	@Override
	public void bind(ValidationSession session,
			Map<ValidationObject, ProxyObject> bound, ProxyField proxyField,
			ValidationObject owner) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nz.co.senanque.validationengine.Plugin#init(nz.co.senanque.validationengine.metadata.EngineMetadata)
	 */
	@Override
	public void init(EngineMetadata engineMetadata) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nz.co.senanque.validationengine.Plugin#unbind(nz.co.senanque.validationengine.ValidationSession, nz.co.senanque.validationengine.ProxyField, nz.co.senanque.validationengine.ValidationObject)
	 */
	@Override
	public void unbind(ValidationSession session, ProxyField owner,
			ValidationObject object) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nz.co.senanque.validationengine.Plugin#getStats(nz.co.senanque.validationengine.ValidationSession)
	 */
	@Override
	public String getStats(ValidationSession session) {
		// TODO Auto-generated method stub
		return null;
	}

}
