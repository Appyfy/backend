package rnd.service.rest;

import java.util.HashMap;
import java.util.Map;

import rnd.data.DataProcessor;

public class APIRegistry {

	private Map<APIInfo, DataProcessor> registry = new HashMap<APIInfo, DataProcessor>();

	public void setDependencies(Map<APIInfo, DataProcessor> registry) {
		this.registry = registry;
	}

	public DataProcessor getDataProcessor(APIInfo api) {
		return registry.get(api);
	}

	
//	private Map<API, API> dependencies = new HashMap<API, API>();
//
//	public void setDependencies(Map<API, API> dependencies) {
//		this.dependencies = dependencies;
//	}
//
//	public API getDependency(API api) {
//		return dependencies.get(api);
//	}

}