package edu.lavinia.inspectory.op.beans;

import java.beans.IntrospectionException;

import org.junit.Test;

import net.codebox.javabeantester.JavaBeanTester;

public class MethodChangesDataTest {
	@Test
	public void testBeanProperties() throws IntrospectionException {
		JavaBeanTester.test(MethodChangesData.class);
	}
}
