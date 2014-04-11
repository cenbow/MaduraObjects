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

import nz.co.senanque.madura.sandbox.Customer;
import nz.co.senanque.madura.sandbox.Invoice;
import nz.co.senanque.madura.sandbox.Session;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Tests to verify that the heap is managed correctly.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.5 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/TestBase-spring.xml"})
public class HeapTest
{
    private static final Logger logger = LoggerFactory.getLogger(HeapTest.class);
    @Autowired private transient ValidationEngine m_validationEngine;
    @Autowired private transient CustomerDAO m_customerDAO;
    
    @Test
    public void test1() throws Exception
    {  
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);
        logger.debug(validationSession.getStats());
        Invoice invoice = new Invoice();
        invoice.setDescription("test invoice");
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        invoice = new Invoice();
        invoice.setDescription("test invoice2");
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        invoice = new Invoice();
        invoice.setDescription("test invoice3");
        customer.getInvoices().add(invoice);
        logger.debug(validationSession.getStats());
        
        customer.getInvoices().remove(0);
        logger.debug(validationSession.getStats());
        customer.getInvoices().remove(invoice);
        logger.debug(validationSession.getStats());
        customer.getInvoices().clear();
        logger.debug(validationSession.getStats());
        
        customer.setOneInvoice(invoice);
        logger.debug(validationSession.getStats());
        customer.setOneInvoice(null);
        logger.debug(validationSession.getStats());
        m_validationEngine.close(validationSession);
    }
    @Test
    public void test2() throws Exception
    {  
        ValidationSession validationSession = m_validationEngine.createSession();
        Session session = new Session();
        Customer customer = m_customerDAO.createCustomer();
        session.getCustomers().add(customer);
        customer.setOneInvoice(new Invoice());
        
        validationSession.bind(session);
        logger.debug(validationSession.getStats());
        
        validationSession.unbind(session);
        logger.debug(validationSession.getStats());
        m_validationEngine.close(validationSession);
    }
}
