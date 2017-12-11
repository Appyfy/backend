package rnd.data.analyze;

import java.util.Collection;
import java.util.Map;

import rnd.data.AbstractDataProcessor;
import rnd.data.analyze.Analyzer;

@SuppressWarnings("rawtypes")
public class AbstractAnalyzer extends AbstractDataProcessor<Map<?, Collection<?>>, Map> implements Analyzer {

	@Override
	public Map process(Map<?, Collection<?>> requestPayload) throws Throwable {
		return null;
	}

}
