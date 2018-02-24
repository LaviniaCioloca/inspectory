package edu.lavinia.inspectory.op.beans;

import java.beans.IntrospectionException;

import org.junit.Test;

import net.codebox.javabeantester.JavaBeanTester;

public class FileOwnershipInformationTest {

	/**
	 * JavaBeanTester tests bean class MethodChangesInformation and might throw
	 * {@code IntrospectionException} if any error appears.
	 * 
	 * @throws IntrospectionException
	 */
	@Test
	public void testBeanProperties() throws IntrospectionException {
		JavaBeanTester.test(FileOwnershipInformation.class);
	}
}
