package inspectory;

import org.junit.Test;

import com.lavinia.inspect.FileModelInspect;
import com.lavinia.inspect.RepoInspect;

public class FileModelInspectTest {

	@Test(expected = NullPointerException.class)
	public void testGetModelFunctionsAnalyze() {
		FileModelInspect fileModelInspect = new FileModelInspect(RepoInspect.getProject());
		fileModelInspect.getModelFunctionsAnalyze();
	}

}
