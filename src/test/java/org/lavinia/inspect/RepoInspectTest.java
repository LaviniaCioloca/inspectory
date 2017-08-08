package org.lavinia.inspect;

import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.lavinia.inspect.RepoInspect;

public class RepoInspectTest {

	@Test
	public void testGetProject() {
		assertNull(RepoInspect.getProject());
	}

}
