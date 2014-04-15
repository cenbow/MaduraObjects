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


import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import nz.co.senanque.madura.sandbox.Customer;
import nz.co.senanque.madura.sandbox.ObjectFactory;

import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * Customer DAO which uses JPA
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
public class CustomerJPAImpl implements CustomerDAO 
{
	@PersistenceContext
	private EntityManager m_entityManager;
    private transient ObjectFactory m_objectFactory;

    public Customer createCustomer()
    {
        return getObjectFactory().createCustomer();

    }

    @Transactional
    public long save(final Customer customer)
    {
    	m_entityManager.merge(customer);
    	m_entityManager.flush();
        return customer.getId();
    }
	public ObjectFactory getObjectFactory() {
		return m_objectFactory;
	}
	public void setObjectFactory(final ObjectFactory objectFactory) {
		m_objectFactory = objectFactory;
	}

	@Transactional(readOnly=true)
    public Customer getCustomer(long id)
    {
		Customer ret = null;
		try {
			ret = m_entityManager.find(Customer.class, id, LockModeType.PESSIMISTIC_WRITE);
			if (ret == null) {
				throw new RuntimeException("Could not find customer "+id);
			}
			ret.getInvoices().size();
		} catch (Exception e) {
			throw e;
		}
        return ret;
    }
}
