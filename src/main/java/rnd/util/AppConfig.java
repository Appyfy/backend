package rnd.util;

import java.io.IOException;

import org.json.JSONObject;

public class AppConfig {

	private static JSONObject queryConfig = null;
	private static JSONObject queryCacheConfig = null;

	private static JSONObject reportConfig = null;

	static {
		load();
	}

	public static void load() {

		String queryConfigPath = "/query/query.json";
		String queryCacheConfigPath = "/query/cache.json";

		String reportConfigPath = "/report/report.json";

		try {
			queryConfig = new JSONObject(IOUtils.readPlainContent(queryConfigPath));
			queryCacheConfig = new JSONObject(IOUtils.readPlainContent(queryCacheConfigPath));

			reportConfig = new JSONObject(IOUtils.readPlainContent((reportConfigPath)));

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static JSONObject getQueryInfo(String queryName) {
		return new JSONObject(queryConfig.getJSONObject(queryName).toString());
	}

	public static void registerQueryInfo(String queryName, JSONObject queryJSON) {
		if (queryConfig == null) {
			queryConfig = new JSONObject();
		}
		queryConfig.put(queryName, queryJSON);
	}

	public static String getQueryCacheInfo(String queryName) {
		String cacheName = queryCacheConfig.optString(queryName);
		if (cacheName != null && !cacheName.isEmpty()) {
			return cacheName;
		}
		return null;
	}

	public static void registerQueryCacheInfo(String queryName, String cacheName) {
		if (queryCacheConfig == null) {
			queryCacheConfig = new JSONObject();
		}
		queryCacheConfig.put(queryName, cacheName);
	}

	public static JSONObject getReportInfo(String reportName) {
		return new JSONObject(reportConfig.getJSONObject(reportName).toString());
	}

}
