package com.lavinia.inspect;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.metanalysis.core.project.PersistentProject;

public class RepoInspect {

	public final static Logger logger = Logger.getLogger(RepoInspect.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			/*
			 * Process process2 = Runtime.getRuntime().exec(
			 * "/home/lavinia/Licenta/bootique/metanalysis", null, new
			 * File("/home/lavinia/Licenta/bootique"));
			 */
			PersistentProject project = PersistentProject.load();
			logger.info("The resulted instance of Project after load is: " + project);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
