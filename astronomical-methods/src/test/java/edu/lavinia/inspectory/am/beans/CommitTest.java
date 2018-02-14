package edu.lavinia.inspectory.am.beans;

import java.beans.IntrospectionException;

import org.junit.Assert;
import org.junit.Test;

import edu.lavinia.inspectory.am.beans.Commit;
import net.codebox.javabeantester.JavaBeanTester;

public class CommitTest {
	@Test
	public void testBeanProperties() throws IntrospectionException {
		JavaBeanTester.test(Commit.class);
	}

	@Test
	public void testToString() {
		Commit commit = new Commit();
		String expected = "Commit [revision=null, date=null, author=null]";
		Assert.assertEquals(expected, commit.toString());
	}
}
