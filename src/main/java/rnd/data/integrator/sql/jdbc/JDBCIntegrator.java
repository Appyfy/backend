package rnd.data.integrator.sql.jdbc;

import java.sql.Connection;
import java.util.Map;

import rnd.data.AbstractDataProcessor;

public class JDBCIntegrator extends AbstractDataProcessor<Map, Connection> {

	private Connection getConnection(String dataSource) {
		return null;
	}

	@Override
	public Connection process(Map requestPayLoad) throws Throwable {
		return getConnection((String) requestPayLoad.get("datasource"));
	}

}