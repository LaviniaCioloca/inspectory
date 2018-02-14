package edu.lavinia.inspectory.utils;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.lavinia.inspectory.beans.FileMethodDynamics;

public class MethodDynamicsUtilsTest {

	private static MethodDynamicsUtils methodDynamicUtils = new MethodDynamicsUtils();
	private static Map<String, FileMethodDynamics> projectMethodDynamics = new HashMap<>();
	private static FileMethodDynamics fileMethodDynamics = new FileMethodDynamics();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		projectMethodDynamics.put("testFileName", fileMethodDynamics);
		methodDynamicUtils.setProjectMethodDynamics(projectMethodDynamics);
	}

	@Test
	public void testAddSupernovaMethodDynamics() {
		fileMethodDynamics.setSupernovaMethods(1);
		fileMethodDynamics.setSupernovaSeverity(10);
		methodDynamicUtils.addSupernovaMethodDynamics("\"testFileName\"", 10);
		assertEquals(fileMethodDynamics,
				methodDynamicUtils.getProjectMethodDynamics().get("testFileName"));
	}

	@Test
	public void testAddPulsarMethodDynamics() {
		fileMethodDynamics.setPulsarMethods(1);
		fileMethodDynamics.setPulsarSeverity(10);
		methodDynamicUtils.addPulsarMethodDynamics("\"testFileName\"", 10);
		assertEquals(fileMethodDynamics,
				methodDynamicUtils.getProjectMethodDynamics().get("testFileName"));
	}

	@Test
	public void testAddDefaultMethodDynamics() {
		fileMethodDynamics.setPulsarMethods(0);
		fileMethodDynamics.setPulsarSeverity(0);
		fileMethodDynamics.setSupernovaMethods(0);
		fileMethodDynamics.setSupernovaSeverity(0);
		methodDynamicUtils.addDefaultMethodDynamics("\"testFileName\"");
		assertEquals(fileMethodDynamics,
				methodDynamicUtils.getProjectMethodDynamics().get("testFileName"));
	}

}
