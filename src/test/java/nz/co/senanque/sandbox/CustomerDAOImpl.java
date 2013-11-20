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

package nz.co.senanque.sandbox;


import java.util.List;

import nz.co.senanque.madura.sandbox.Customer;
import nz.co.senanque.madura.sandbox.ObjectFactory;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.ResourceTransactionManager;

/**
 * 
 * Short description
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
public class CustomerDAOImpl implements CustomerDAO 
{
    private transient SessionFactory m_sessionFactory;
    private transient ObjectFactory m_objectFactory;
    private transient SubTransaction m_subTransaction;
    private transient ResourceTransactionManager m_txManager;

    /* (non-Javadoc)
     * @see nz.co.senanque.sandbox.CustomerDAO#createCustomer()
     */
    public Customer createCustomer()
    {
        return getObjectFactory().createCustomer();

    }
    /* (non-Javadoc)
     * @see nz.co.senanque.sandbox.CustomerDAO#save(nz.co.senanque.madura.sandbox.Customer, org.hibernate.Session)
     */
    @Transactional
    public long save(final Customer customer)
    {
        final Session session = getSessionFactory().getCurrentSession();
        session.saveOrUpdate(customer);
        session.flush();
        return customer.getId();
    }
	public SessionFactory getSessionFactory() {
		return m_sessionFactory;
	}
	public void setSessionFactory(final SessionFactory sessionFactory) {
		m_sessionFactory = sessionFactory;
	}
	public ObjectFactory getObjectFactory() {
		return m_objectFactory;
	}
	public void setObjectFactory(final ObjectFactory objectFactory) {
		m_objectFactory = objectFactory;
	}
    /* (non-Javadoc)
     * @see nz.co.senanque.sandbox.CustomerDAO#getCustomer(long, org.hibernate.Session)
     */
	@Transactional(readOnly=true)
    public Customer getCustomer(long id)
    {
        final Session session = getSessionFactory().getCurrentSession();
        //SessionFactoryUtils.getSession(getSessionFactory(), false);
        final Customer customer = (Customer)session.get("nz.co.senanque.madura.sandbox.Customer", id);
        customer.getInvoices().size();
        return customer;
    }
	public void transactionTester()
	{
	    final DefaultTransactionDefinition def = new DefaultTransactionDefinition();
	    // explicitly setting the transaction name is something that can only be done programmatically
	    def.setName("SomeTxName");
	    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
	    def.setReadOnly(true);

	    final TransactionStatus status = m_txManager.getTransaction(def);
        Session session = getSessionFactory().getCurrentSession();
        final Transaction transaction = session.getTransaction();
        Query query = session.createQuery("select p from nz.co.senanque.madura.sandbox.Customer p");
        List<Customer> customers = query.list();
        for (Customer customer: customers)
        {
            final Session session1 = getSessionFactory().openSession();
            final Session currentSession = getSessionFactory().getCurrentSession();
            final Transaction transaction1 = currentSession.getTransaction();
            getSubTransaction().process(customer);
        }
        
	    
	}
    public SubTransaction getSubTransaction()
    {
        return m_subTransaction;
    }
    public void setSubTransaction(final SubTransaction subTransaction)
    {
        m_subTransaction = subTransaction;
    }
    public ResourceTransactionManager getTxManager()
    {
        return m_txManager;
    }
    public void setTxManager(final ResourceTransactionManager txManager)
    {
        m_txManager = txManager;
    }

}
