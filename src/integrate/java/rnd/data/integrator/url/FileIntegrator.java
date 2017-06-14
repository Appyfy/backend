package rnd.data.integrator.url;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import rnd.data.AbstractDataProcessor;
import rnd.data.integrator.Integrator;

public class FileIntegrator extends AbstractDataProcessor<Map, InputStream> implements Integrator {

	@Override
	public InputStream process(Map requestPayLoad) throws Throwable {
		InputStream is = new FileInputStream(requestPayLoad.get("fileName") + ".xls");
		return is;
	}

}