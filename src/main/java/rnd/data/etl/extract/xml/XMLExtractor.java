package rnd.data.etl.extract.xml;

import java.util.Map;

import rnd.data.AbstractDataProcessor;

@SuppressWarnings("rawtypes")
public class XMLExtractor extends AbstractDataProcessor<Map, Map> {

	@Override
	public Map process(Map requestPayLoad) throws Throwable {
		return requestPayLoad;
	}
}
