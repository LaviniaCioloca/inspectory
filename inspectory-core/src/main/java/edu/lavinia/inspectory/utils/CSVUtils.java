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
package edu.lavinia.inspectory.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CSVUtils {
	private static final char DEFAULT_SEPARATOR = ',';
	private static final char SPACE = ' ';

	public static void writeLine(final Writer writer, final List<String> values)
			throws IOException {

		writeLine(writer, values, DEFAULT_SEPARATOR, ' ');
	}

	public static String followCVSformat(final String value) {
		String result = value;

		if (result.contains("\"")) {
			result = result.replace("\"", "\"\"");
		}

		return result;
	}

	/**
	 * Writes String values separated using a separator in a CSV format file.
	 *
	 * @param writer
	 *            The writer of the CSV file.
	 * @param values
	 *            List of String values to be written.
	 * @param separator
	 *            Separator of the values: can be empty.
	 * @param customQuote
	 *            Custom quote for the values: can be empty.
	 * @throws IOException
	 */
	public static void writeLine(final Writer writer, final List<String> values,
			char separator, final char customQuote) throws IOException {

		if (separator == SPACE) {
			separator = DEFAULT_SEPARATOR;
		}

		final StringBuilder stringBuilder = new StringBuilder();

		parseEachValue(values, separator, customQuote, stringBuilder);

		stringBuilder.append('\n');
		writer.append(stringBuilder.toString());
	}

	private static void parseEachValue(final List<String> values,
			final char separator, final char customQuote,
			final StringBuilder stringBuilder) {

		boolean first = true;

		for (final String value : values) {
			if (!first) {
				stringBuilder.append(separator);
			}

			if (customQuote == SPACE) {
				stringBuilder.append(followCVSformat(value));
			} else {
				stringBuilder.append(customQuote).append(followCVSformat(value))
						.append(customQuote);
			}

			first = false;
		}
	}
}