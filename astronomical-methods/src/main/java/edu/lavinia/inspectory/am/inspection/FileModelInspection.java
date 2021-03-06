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
package edu.lavinia.inspectory.am.inspection;

import java.io.IOException;
import java.util.Set;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.metanalysis.core.model.Node;
import org.metanalysis.core.model.SourceFile;
import org.metanalysis.core.project.PersistentProject;

import edu.lavinia.inspectory.am.visitor.NodeVisitor;

public class FileModelInspection {
	private final PersistentProject project;

	public FileModelInspection(final PersistentProject project) {
		this.project = project;
	}

	/**
	 * Creates the .model file with the method's current model based on
	 * model.json file from .metanalysis folder.
	 */
	public void getModelFunctionsAnalyze() {
		try {
			final String logFolderName = ".inspectory_results";
			final Set<String> filesList = project.listFiles();

			for (final String file : filesList) {
				if (file.startsWith(".") || !file.endsWith(".java")) {
					continue;
				}

				final SourceFile fileModel = project.getFileModel(file);
				final String logFilePath = "./" + logFolderName + "/" + file
						+ ".model";
				final Logger logger = Logger.getRootLogger();
				final FileAppender appender = (FileAppender) logger
						.getAppender("file");
				appender.setFile(logFilePath);
				appender.activateOptions();

				final Set<Node> fileModelNodes = fileModel.getNodes();
				final NodeVisitor visitor = new NodeVisitor(file);

				for (final Node n : fileModelNodes) {
					visitor.visit(n);
				}

				/*
				 * final Map<String, Integer> map = visitor.getFunctionSize();
				 *
				 * for (final Map.Entry<String, Integer> entry : map.entrySet())
				 * { logger.info(entry.getKey() + " - " + entry.getValue()); }
				 */
			}
		} catch (final IOException e) {
			/*
			 * Need to have a NOP here because of the files that do not have a
			 * model -> they have static initializers and getModel(file) throws
			 * IOException
			 */
		}
	}
}
