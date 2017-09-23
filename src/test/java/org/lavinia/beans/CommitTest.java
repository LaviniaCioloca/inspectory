package org.lavinia.beans;

import java.beans.IntrospectionException;

import org.junit.Test;

import net.codebox.javabeantester.JavaBeanTester;

public class CommitTest {
	@Test
	public void testBeanProperties() throws IntrospectionException{
	    JavaBeanTester.test(Commit.class);
	}
}
