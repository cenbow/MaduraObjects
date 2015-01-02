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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import nz.co.senanque.madura.sandbox.Customer;
import nz.co.senanque.validationengine.History;
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
 * Tests to verify that the XSD generated objects actually do serialise properly
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.5 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/TestBase-spring.xml"})
public class HistoryTest
{
	private static final Logger log = LoggerFactory.getLogger(HistoryTest.class);

    @Autowired private transient ValidationEngine m_validationEngine;
    
    /**
     * Verifies that history is being stored and pruned
     * @throws Exception
     */
    @Test
    public void test1() throws Exception
    {  
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = new Customer();
        validationSession.bind(customer);
        customer.setAmountWithHistory(10.00);
        customer.setAmountWithHistory(20.00);
        customer.setAmountWithHistory(30.00);
        customer.setAmountWithHistory(40.00);
        List<History> history = customer.getMetadata().getProxyField(Customer.AMOUNTWITHHISTORY).getHistory();
        assertEquals(3,history.size());
        validationSession.close();
    }
    /**
     * Tests for what happens when there is a validation error
     * ie that history is properly cleared up.
     * @throws Exception
     */
    @Test
    public void test2() throws Exception
    {  
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = new Customer();
        validationSession.bind(customer);
        customer.setAmountWithHistory(10.00);
        customer.setAmountWithHistory(20.00);
        boolean exception = false;
        try {
			customer.setAmountWithHistory(1030.00);// this ought to fail
		} catch (Exception e) {
			exception = true;
		}
        assertTrue(exception);
        customer.setAmountWithHistory(30.00);
        customer.setAmountWithHistory(40.00);
        List<History> history = customer.getMetadata().getProxyField(Customer.AMOUNTWITHHISTORY).getHistory();
        assertEquals(3,history.size());
        assertTrue(((Double)history.get(0).getValue()).equals(20.00D));
        assertTrue(((Double)history.get(1).getValue()).equals(30.00D));
        assertTrue(((Double)history.get(2).getValue()).equals(40.00D));
        validationSession.close();
    }
    /**
     * Tests for what happens when there is an error throw from the plugin
     * ie that history is properly cleared up.
     * @throws Exception
     */
    @Test
    public void test3() throws Exception
    {  
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = new Customer();
        validationSession.bind(customer);
        customer.setAmountWithHistory(10.00);
        customer.setAmountWithHistory(20.00);
        boolean exception = false;
        try {
			customer.setAmountWithHistory(530.00);// this ought to fail
		} catch (Exception e) {
			exception = true;
		}
        assertTrue(exception);
        List<History> history = customer.getMetadata().getProxyField(Customer.AMOUNTWITHHISTORY).getHistory();
        assertEquals(2,history.size());
        assertEquals(10.00D,((Double)history.get(0).getValue()), 0.1D);
        assertEquals(20.00D,((Double)history.get(1).getValue()), 0.1D);
        assertEquals(20.00D, customer.getAmountWithHistory(), 0.1D);
        validationSession.close();
    }
}
