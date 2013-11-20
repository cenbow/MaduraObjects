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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import nz.co.senanque.madura.sandbox.Customer;
import nz.co.senanque.madura.sandbox.IndustryType;
import nz.co.senanque.madura.sandbox.Invoice;
import nz.co.senanque.madura.sandbox.Session;
import nz.co.senanque.validationengine.FieldMetadata;
import nz.co.senanque.validationengine.ObjectMetadata;
import nz.co.senanque.validationengine.ProxyField;
import nz.co.senanque.validationengine.ProxyObject;
import nz.co.senanque.validationengine.ValidationEngine;
import nz.co.senanque.validationengine.ValidationException;
import nz.co.senanque.validationengine.ValidationObject;
import nz.co.senanque.validationengine.ValidationSession;
import nz.co.senanque.validationengine.ValidationUtils;
import nz.co.senanque.validationengine.choicelists.ChoiceBase;

import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
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
public class ObjectTest
{
	private static final Logger log = LoggerFactory.getLogger(ObjectTest.class);

    @Autowired private transient ValidationEngine m_validationEngine;
    @Autowired private transient CustomerDAO m_customerDAO;
    @Autowired private transient Marshaller m_marshaller;
    @Autowired private transient Unmarshaller m_unmarshaller;
    
    @Test
    public void test1() throws Exception
    {  
        Object en = IndustryType.fromValue("Ag");
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = m_customerDAO.createCustomer();
        validationSession.bind(customer);
        Invoice invoice = new Invoice();
        invoice.setDescription("test invoice");
        customer.getInvoices().add(invoice);
        boolean exceptionFound = false;
        try 
        {
            customer.setName("ttt");
        } 
        catch (ValidationException e) 
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
        
        final ObjectMetadata customerMetadata = validationSession.getMetadata(customer);
        final FieldMetadata customerTypeMetadata = customerMetadata.getFieldMetadata(Customer.CUSTOMERTYPE);
        
//        assertFalse(customerTypeMetadata.isActive());
//        assertFalse(customerTypeMetadata.isReadOnly());
//        assertFalse(customerTypeMetadata.isRequired());
        
        customer.setName("aaaab");
        exceptionFound = false;
        try
        {
            customer.setCustomerType("B");
        }
        catch (Exception e)
        {
            exceptionFound = true;
        }
//        assertTrue(exceptionFound);
        exceptionFound = false;
        try
        {
            customer.setCustomerType("XXX");
        }
        catch (Exception e)
        {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
        customer.setBusiness(IndustryType.AG);
        customer.setAmount(new Double(500.99));
        final long id = m_customerDAO.save(customer);
        log.info("{}",id);
        
        // fetch customer back
        customer = m_customerDAO.getCustomer(id);
        final int invoiceCount = customer.getInvoices().size();
        validationSession.bind(customer);
        invoice = new Invoice();
        ValidationUtils.setDefaults(invoice);
        invoice.setDescription("test invoice2");
        customer.getInvoices().add(invoice);
        assertEquals("xyz",invoice.getTestDefault());
        assertEquals("Ag",invoice.getTestEnumDefault().value());
        m_customerDAO.save(customer);
        
        // fetch customer again
        customer = m_customerDAO.getCustomer(id);
        customer.toString();
        validationSession.bind(customer);
        final Invoice inv = customer.getInvoices().get(0);
        customer.getInvoices().remove(inv);
        
        //ObjectMetadata metadata = validationSession.getMetadata(customer);
        ObjectMetadata metadata = customer.getMetadata();
        assertEquals("this is a description",metadata.getFieldMetadata(Customer.NAME).getDescription());
        assertEquals("ABC",metadata.getFieldMetadata(Customer.NAME).getPermission());
        List<ChoiceBase> choices = metadata.getFieldMetadata(Customer.BUSINESS).getChoiceList();
//        assertEquals(1,choices.size());
        List<ChoiceBase> choices2 = metadata.getFieldMetadata(Customer.CUSTOMERTYPE).getChoiceList();
//        assertEquals(1,choices2.size());
        choices2 = metadata.getFieldMetadata(Customer.CUSTOMERTYPE).getChoiceList();
//        assertEquals(1,choices2.size());
        
        for (ChoiceBase choice: choices2)
        {
            System.out.println(choice.getDescription());
        }
        
        customer.setName("aab");
        choices2 = metadata.getFieldMetadata(Customer.CUSTOMERTYPE).getChoiceList();
//        assertEquals(6,choices2.size());
        
        // Convert customer to XML
        QName qname = new QName("http://www.example.org/sandbox","Session");
        JAXBElement<Session> sessionJAXB =
                new JAXBElement<Session>(qname, Session.class, new Session() );
        sessionJAXB.getValue().getCustomers().add(customer); //??This fails to actually add
        StringWriter marshallWriter = new StringWriter();
        Result marshallResult = new StreamResult(marshallWriter);
        m_marshaller.marshal(sessionJAXB,marshallResult);
        marshallWriter.flush();       
        String result = marshallWriter.getBuffer().toString().trim();
        String xml = result.replaceAll("\\Qhttp://www.example.org/sandbox\\E", "http://www.example.org/sandbox");        
        log.info("{}",xml);
        
        // Convert customer back to objects
        SAXBuilder builder = new SAXBuilder();
        org.jdom.Document resultDOM = builder.build(new StringReader(xml));
        JAXBElement<Session> request  = (JAXBElement<Session>)m_unmarshaller.unmarshal(new JDOMSource(resultDOM));
        validationSession = m_validationEngine.createSession();
        validationSession.bind(request.getValue());
        assertEquals(3,validationSession.getProxyCount());
        List<Customer> customers = request.getValue().getCustomers();
        assertEquals(1,customers.size());
        customers.clear();
        assertEquals(1,validationSession.getProxyCount());
        request.toString();
        validationSession.close();
    }

    @Test
    public void test2() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = m_customerDAO.createCustomer();
        customer.setName("aaaab");
        validationSession.bind(customer);
//        assertEquals("A", customer.getCustomerType());
//        assertTrue(customer.getBusiness().compareTo(IndustryType.AG)==0);
        customer.setName(null);
//        assertNull(customer.getCustomerType());
//        assertNull(customer.getBusiness());
        customer.setName("aaaaab");
//        assertEquals("B", customer.getCustomerType());
//        assertTrue(customer.getBusiness().compareTo(IndustryType.FISH)==0);

        validationSession.close();
    }
    @Test
    public void test3() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = m_customerDAO.createCustomer();
        customer.setName("aaaab");
        validationSession.bind(customer);
//        assertEquals("A", customer.getCustomerType());
//        assertTrue(customer.getBusiness().compareTo(IndustryType.AG)==0);
        customer.getInvoices().add(new Invoice());
//        assertEquals(1, customer.getCount());
        validationSession.getProxyObject(customer);
        validationSession.close();
    }

    @Test
    public void test4() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = m_customerDAO.createCustomer();
        customer.setName("aaaab");
        validationSession.bind(customer);
        Invoice invoice = new Invoice();
        customer.getInvoices().add(invoice);
        invoice.setAmount(100);
        
        invoice = new Invoice();
        customer.setOneInvoice(invoice);
        invoice = customer.getOneInvoice();
        invoice.setAmount(200);
        
        ProxyObject proxyObject0 = validationSession.getProxyObject(invoice);

        ProxyObject proxyObject = validationSession.getProxyObject(customer);
        List<ProxyField> ret = getProxyObjects(validationSession,proxyObject,"invoices", "amount");
        ret = getProxyObjects(validationSession,proxyObject,"oneInvoice", "amount");
        validationSession.close();
    }
    @Test
    public void testDefaults() throws Exception
    {        
        ValidationSession validationSession = m_validationEngine.createSession();

        // create a customer
        Customer customer = m_customerDAO.createCustomer();
        assertEquals("400",customer.getAmountstr());
        assertEquals(0,Double.compare(400D, customer.getAmount()));
        customer.setName("aaaab");
        assertEquals(true,customer.getMetadata().isUnknown("address"));
        validationSession.bind(customer);
        assertEquals(true,customer.getMetadata().isUnknown("address"));
        customer.setAddress("whatever");
        assertEquals(false,customer.getMetadata().isUnknown("address"));
        assertEquals(true,customer.getMetadata().getProxyField("amountstr").isRequired());
        validationSession.close();
    }
    private List<ProxyField> getProxyObjects(ValidationSession validationSession,ProxyObject proxyObject,String ref,String name)
    {
        List<ProxyField> ret = new ArrayList<ProxyField>();
        Map<String, ProxyField> fieldMap = proxyObject.getFieldMap();
        ProxyField refField = fieldMap.get(ref);
        Object o = refField.fetchValue();
        if (o instanceof List<?>)
        {
            for (ValidationObject entry: (List<ValidationObject>)o)
            {
                ProxyObject po = validationSession.getProxyObject(entry);
                ProxyField pf = po.getFieldMap().get(name);
                if (pf != null)
                {
                    ret.add(pf);
                }
            }
        }
        else if (o instanceof ValidationObject)
        {
            ProxyObject po = validationSession.getProxyObject((ValidationObject)o);
            ProxyField pf = po.getFieldMap().get(name);
            if (pf != null)
            {
                ret.add(pf);
            }
        }
        return ret;
    }
}
