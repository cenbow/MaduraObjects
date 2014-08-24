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
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationException;
import nz.co.senanque.validationengine.ValidationSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * Short description
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.4 $
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/TestBase-spring.xml"})
public class RegexTest
{
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
        customer.setName("aaaab");
        validationSession.close();
    }
    @Test(expected=ValidationException.class)
    public void test2() throws Exception
    {  
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = new Customer();
        validationSession.bind(customer);
		customer.setName("1234");
        validationSession.close();
    }

}
