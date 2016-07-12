package rnd.data.etl.extract.json;

import java.util.Map;

import rnd.data.AbstractDataProcessor;

@SuppressWarnings("rawtypes")
public class JSONExtractor extends AbstractDataProcessor<Map, Map> {

	@Override
	public Map process(Map requestPayLoad) throws Throwable {
		return requestPayLoad;

	}

}