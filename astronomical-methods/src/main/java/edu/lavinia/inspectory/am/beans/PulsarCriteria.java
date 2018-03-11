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

/**
 * Bean class having values of Pulsar metric.
 * 
 * @author Lavinia Cioloca
 *
 */
public class PulsarCriteria {
	private Integer recentCyclesPoints = 0;
	private Integer averageSizeIncreasePoints = 0;
	private Integer methodSizePoints = 0;
	private Integer activityStatePoints = 0;

	public Integer getRecentCyclesPoints() {
		return recentCyclesPoints;
	}

	public void setRecentCyclesPoints(Integer recentCyclesPoints) {
		this.recentCyclesPoints = recentCyclesPoints;
	}

	public Integer getAverageSizeIncreasePoints() {
		return averageSizeIncreasePoints;
	}

	public void setAverageSizeIncreasePoints(
			Integer averageSizeIncreasePoints) {
		this.averageSizeIncreasePoints = averageSizeIncreasePoints;
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
		return "PulsarCriteria [recentCyclesPoints=" + recentCyclesPoints
				+ ", averageSizeIncreasePoints=" + averageSizeIncreasePoints
				+ ", methodSizePoints=" + methodSizePoints
				+ ", activityStatePoints=" + activityStatePoints + "]";
	}

}
