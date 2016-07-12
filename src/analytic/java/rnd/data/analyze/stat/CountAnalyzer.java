package rnd.data.analyze.stat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rnd.data.analyze.AbstractAnalyzer;

public class CountAnalyzer extends AbstractAnalyzer {

	@Override
	public Map<String, Number> process(Map aggregateInfo) throws Throwable {

		String key = (String) aggregateInfo.get("key");

		Map extractedData = (Map) getDelegate().process(aggregateInfo);

		List headers = (List) extractedData.get("headers");
		List<List> data = (List<List>) extractedData.get("data");

		int keyIndx = headers.indexOf(key);

		Map<String, Number> aggrData = new HashMap<String, Number>();

		for (List columnValues : data) {

			String keyClmn = columnValues.get(keyIndx).toString();

			Number aggrValue = aggrData.get(keyClmn);
			if (aggrValue == null) {
				aggrValue = new Double(0);
			}

			aggrValue = aggrValue.doubleValue() + 1;

			aggrData.put(keyClmn, aggrValue);
		}

		return aggrData;

		// Map<String, Collection> newProcessedData = new HashMap<String, Collection>();
		// newProcessedData.put("keys", aggrData.keySet());
		// newProcessedData.put("values", aggrData.values());
		// return newProcessedData;

	}

}