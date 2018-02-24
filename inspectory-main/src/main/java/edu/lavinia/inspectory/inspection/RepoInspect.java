/*******************************************************************************
 * Copyright (c) 2017, 2018 Lavinia Cioloca
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************/
package edu.lavinia.inspectory.inspection;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.metanalysis.core.project.PersistentProject;

public class RepoInspect {

	public final static Logger LOGGER = Logger.getLogger(RepoInspect.class.getName());

	/**
	 * Static method that loads the .metanalysis persistent project for the
	 * current repository to be analyzed.
	 * 
	 * @return A PersistentProject instance of the current repository.
	 */
	public static PersistentProject getProject() {
		try {
			return PersistentProject.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Main method of inspectory project.
	 * 
	 * @param args
	 *            The argument needed for inspectory to run is the csv file name
	 *            to store the results.
	 */
	public static void main(String[] args) {
		final PersistentProject project = getProject();
		if (project == null) {
			System.out.println(
					"Error! Project persistency not found: .metanalysis folder! Use metanalysis to generate the model first!");
		} else {
			final Commands commands = new Commands(args, project);
			commands.parse();
		}
	}
}
