package com.lavinia.inspect;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.metanalysis.core.project.PersistentProject;

public class RepoInspect {

	public final static Logger logger = Logger.getLogger(RepoInspect.class.getName());

	public static PersistentProject getProject() {
		try {
			return PersistentProject.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		/*FileModelInspect fileModelInspect = new FileModelInspect(getProject());
		fileModelInspect.getModelFunctionsAnalyze();*/
		
		FileHistoryInspect fileHistoryInspect = new FileHistoryInspect(getProject());
		fileHistoryInspect.getHistoryFunctionsAnalyze();
	}

}
