package org.lavinia.inspect;

import org.junit.Test;
import org.lavinia.inspect.FileModelInspect;
import org.lavinia.inspect.RepoInspect;

public class FileModelInspectTest {

	@Test(expected = NullPointerException.class)
	public void testGetModelFunctionsAnalyze() {
		FileModelInspect fileModelInspect = new FileModelInspect(RepoInspect.getProject());
		fileModelInspect.getModelFunctionsAnalyze();
	}

}
