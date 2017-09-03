package org.lavinia.inspect;

import java.util.ArrayList;

import org.lavinia.beans.CSVData;
import org.lavinia.beans.Commit;

public class MethodMetrics {
	private final static Integer TIME_FRAME = 14; // days
	private final static Integer SHORT_TIMESPAN =  1 * TIME_FRAME;
	private final static Integer MAJOR_SIZE_CHANGE =  100; //lines

	public Boolean isPulsar() {
		return false;
	}
	
	public Boolean isSupernova(CSVData csvData) {
		ArrayList<Commit> commits = csvData.getCommits();
		ArrayList<Integer> changesList = csvData.getChangesList();
		for (int i = 1; i < commits.size(); ++i) {
			Integer sum = changesList.get(i);
			for (int j = i + 1; j < commits.size() - 1; ++j) {
				Long startTime = commits.get(i).getDate().getTime();
				Long endTime = commits.get(j).getDate().getTime();
				Long diffTime = endTime - startTime;
				Long diffDays = diffTime / (1000 * 60 * 60 * 24);
				if (diffDays <= TIME_FRAME) {
					sum += changesList.get(j);
					if (sum >= MAJOR_SIZE_CHANGE) {
						return true;
					}
				} else {
					break;
				}
			}
		}
		return false;
	}
}
