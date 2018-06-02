package edu.lavinia.inspectory.am.beans;

import java.beans.IntrospectionException;

import org.junit.Test;

import net.codebox.javabeantester.JavaBeanTester;

public class FileWithAstronomicalMethodsTest {
	@Test
	public void testBeanProperties() throws IntrospectionException {
		JavaBeanTester.test(FileWithAstronomicalMethods.class);
	}
}
