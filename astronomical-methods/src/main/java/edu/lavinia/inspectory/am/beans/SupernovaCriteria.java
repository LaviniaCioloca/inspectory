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
package edu.lavinia.inspectory.am.beans;

public class SupernovaCriteria {
	private Integer leapsSizePoints = 0;
	private Integer recentLeapsSizePoints = 0;
	private Integer subsequentRefactoringPoints = 0;
	private Integer methodSizePoints = 0;
	private Integer activityStatePoints = 0;

	public Integer getLeapsSizePoints() {
		return leapsSizePoints;
	}

	public void setLeapsSizePoints(Integer leapsSizePoints) {
		this.leapsSizePoints = leapsSizePoints;
	}

	public Integer getRecentLeapsSizePoints() {
		return recentLeapsSizePoints;
	}

	public void setRecentLeapsSizePoints(Integer recentLeapsSizePoints) {
		this.recentLeapsSizePoints = recentLeapsSizePoints;
	}

	public Integer getSubsequentRefactoringPoints() {
		return subsequentRefactoringPoints;
	}

	public void setSubsequentRefactoringPoints(Integer subsequentRefactoringPoints) {
		this.subsequentRefactoringPoints = subsequentRefactoringPoints;
	}

	public Integer getMethodSizePoints() {
		return methodSizePoints;
	}

	public void setMethodSizePoints(Integer methodSizePoints) {
		this.methodSizePoints = methodSizePoints;
	}

	public Integer getActivityStatePoints() {
		return activityStatePoints;
	}

	public void setActivityStatePoints(Integer activityStatePoints) {
		this.activityStatePoints = activityStatePoints;
	}

	@Override
	public String toString() {
		return "SupernovaCriteria [leapsSizePoints=" + leapsSizePoints + ", recentLeapsSizePoints="
				+ recentLeapsSizePoints + ", subsequentRefactoringPoints=" + subsequentRefactoringPoints
				+ ", methodSizePoints=" + methodSizePoints + ", activityStatePoints=" + activityStatePoints + "]";
	}

}
