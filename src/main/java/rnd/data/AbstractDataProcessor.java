package rnd.data;

import java.util.HashMap;
import java.util.Map;

import rnd.process.ProcessorCallback;

public abstract class AbstractDataProcessor<Rq, Rs> implements DataProcessor<Rq, Rs> {

	private Map<Object, DataProcessor<Rq, Rs>> delegates = new HashMap<Object, DataProcessor<Rq, Rs>>();

	public void setDelegates(Map<Object, DataProcessor<Rq, Rs>> delegates) {
		this.delegates.putAll(delegates);
	}

	public Map<Object, DataProcessor<Rq, Rs>> getDelegates() {
		return delegates;
	}

	public void setDelegate(String type, DataProcessor<Rq, Rs> delegate) {
		delegates.put(type, delegate);
	}

	public DataProcessor<Rq, Rs> getDelegate(Object type) {
		return delegates.get(type);
	}

	public void setDelegate(DataProcessor<Rq, Rs> delegate) {
		delegates.put(null, delegate);
	}

	public DataProcessor<Rq, Rs> getDelegate() {
		return delegates.get(null);
	}

	public ProcessorCallback<?> getProcessorCallback(Rq requestPayload) {
		return null;
	}

}