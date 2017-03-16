package rnd.dao.nosql.mongodb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rnd.data.integrator.nosql.mongodb.MongoDBIntegrator;
import rnd.util.AppConfig;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MongoDBAccessObject {

	//private static CacheManager cacheManager;

	public static BasicDBList select(String queryName, JSONArray filtersJSON, Object value) throws JSONException, IOException {
		return select(queryName, filtersJSON, value, 0, 0, null);
	}

	public static BasicDBList select(String queryName, JSONArray filtersJSON, Object value, int skip, int limit, Map<String, Integer> sortOrderMap) throws JSONException, IOException {

		BasicDBList list = new BasicDBList();

		List queryNames = new ArrayList();
		if (queryName != null && queryName.toString().startsWith("[")) {
			queryNames = toList(new JSONArray(queryName.toString()));
		} else {
			queryNames = Arrays.asList(queryName);
		}

		for (Iterator iterator = queryNames.iterator(); iterator.hasNext();) {

			queryName = iterator.next().toString();

			String cacheName = AppConfig.getQueryCacheInfo(queryName);
//			if (cacheName != null) {
//				Results queryResults = getFromCache(cacheName);
//				if (queryResults != null && queryResults.all().size() > 0) {
//					for (Result result : queryResults.all()) {
//						list.add(result.getValue());
//					}
//					queryResults.discard();
//					continue;
//				}
//			}

			Iterable<DBObject> results = select(AppConfig.getQueryInfo(queryName), filtersJSON, value, skip, limit, sortOrderMap);
			for (DBObject result : results) {
				list.add(result);
				if (cacheName != null) {
					//putInCache(cacheName, result);
				}
			}

		}

		return list;
	}
/*
	private static Results getFromCache(String cacheName) {
		Ehcache cache = getOrCreateCache(cacheName);
		Query query = cache.createQuery();
		query.includeValues();
		// query.addCriteria(state.eq(reqState));
		Results results = query.execute();
		return results;
	}

	private static void putInCache(String cacheName, DBObject result) {
		Ehcache cache = getOrCreateCache(cacheName);
		cache.put(new Element(result.get("_id"), result));
	}

	private static Ehcache getOrCreateCache(String cacheName) {
		Ehcache cache = getOrCreateCacheManager().getEhcache(cacheName);
		if (cache == null) {
			Ehcache defaultCache = cacheManager.getEhcache("query-default");
			CacheConfiguration defaultCacheConfig = defaultCache.getCacheConfiguration();
			CacheConfiguration cacheConfig = defaultCacheConfig.clone();
			cacheConfig.setName(cacheName);
			cache = new Cache(cacheConfig);
			cacheManager.addCache(cache);
		}
		return cache;
	}

	private static CacheManager getOrCreateCacheManager() {
		if (cacheManager == null) {
			cacheManager = new CacheManager();
		}
		return cacheManager;
	}
*/
	private static Iterable select(JSONObject queryJSON, JSONArray filtersJSON, Object value, int skip, int limit, Map<String, Integer> sortOrderMap) throws JSONException, IOException {
		if (queryJSON.has("GROUPBY")) {
			return aggregate(queryJSON, filtersJSON, value, skip, limit, sortOrderMap);
		} else {
			return find(queryJSON, filtersJSON, value, skip, limit, sortOrderMap);
		}
	}

	private static Iterable<DBObject> aggregate(JSONObject queryJSON, JSONArray filtersJSON, Object value, int skip, int limit, Map<String, Integer> sortOrderMap) throws JSONException, IOException {

		JSONArray select = queryJSON.getJSONArray("SELECT");
		String from = queryJSON.getString("FROM");
		JSONArray groupBy = queryJSON.getJSONArray("GROUPBY");

		List pipeline = new ArrayList();

		BasicDBObjectBuilder projectOB = ob();

		BasicDBObjectBuilder groubByOB = ob();

		BasicDBObjectBuilder idOB = ob();

		for (int i = 0, len = groupBy.length(); i < len; i++) {
			String field = groupBy.getString(i);
			idOB.add(field, "$" + field);
		}
		groubByOB.add("_id", idOB.get());

		for (int i = 0, len = select.length(); i < len; i++) {

			String column = select.getString(i);

			// Function - fn(x), Function Alias fn(x) y
			if (column.contains("(")) {

				int start = column.indexOf("(");
				int end = column.indexOf(")");

				Object columnExp;

				String fn = column.substring(0, start);
				columnExp = column.substring(start + 1, end);

				if (end + 1 < column.length()) {
					column = column.substring(end + 2, column.length());
				} else {
					column = fn + "_" + columnExp;
				}

				if (Character.isDigit(columnExp.toString().charAt(0))) {
					columnExp = new Integer(columnExp.toString());
				} else {
					columnExp = "$" + columnExp;
				}

				groubByOB.add(column, ob().add("$" + fn, columnExp).get());
				projectOB.add(column, "$" + column);
				continue;
			}
			// Literal Alias - 'a' x
			else if (column.contains("'")) {

				int end = column.lastIndexOf("'");

				String columnExp = column.substring(1, end);
				column = column.substring(end + 2, column.length());

				projectOB.add(column, ob().add("$literal", columnExp).get());
			}
			// Simple - x, Simple Alias - x y
			else {

				int end = column.indexOf(" ");
				Object columnExp;
				if (end != -1) {
					columnExp = column.substring(0, end);
					column = column.substring(end + 1, column.length());
				} else {
					columnExp = column;
				}

				groubByOB.add(column, ob().add("$first", "$" + columnExp).get());
				projectOB.add(column, "$" + column);
			}
		}

		DBObject matchObj = ob().add("$match", match(queryJSON, filtersJSON, value)).get();
		pipeline.add(matchObj);

		DBObject groupByObj = ob().add("$group", groubByOB.get()).get();
		pipeline.add(groupByObj);

		DBObject projectObj = ob().add("$project", projectOB.get()).get();
		pipeline.add(projectObj);

		if (skip > 0) {
			DBObject skipObj = ob().add("$skip", skip).get();
			pipeline.add(skipObj);
		}

		if (limit > 0) {
			DBObject limitObj = ob().add("$limit", limit).get();
			pipeline.add(limitObj);
		}

		if (sortOrderMap != null && sortOrderMap.size() > 0) {

			Set<Entry<String, Integer>> sortOrders = sortOrderMap.entrySet();
			BasicDBObjectBuilder sortOB = ob();
			for (Entry<String, Integer> sortOrder : sortOrders) {
				sortOB.add(sortOrder.getKey(), sortOrder.getValue());
			}

			DBObject sortObj = ob().add("$sort", sortOB.get()).get();
			pipeline.add(sortObj);
		}

		DB db = MongoDBIntegrator.DBHolder.SINGLETON.getDB();
		Iterable<DBObject> results = db.getCollection(from).aggregate(pipeline).results();

		if (queryJSON.has("JOIN")) {
			results = join(queryJSON, results);
		}

		return results;
	}

	private static void addMatch(BasicDBObjectBuilder matchOB, JSONArray exprs, Iterator values) throws JSONException, IOException {

		for (int i = 0; i < exprs.length(); i++) {

			JSONObject expr = exprs.getJSONObject(i);

			String columnName = expr.getString("column");
			String op = expr.optString("op", "eq");

			Object value = null;
			String valueName = expr.optString("value", "?");
			if (op.equals("in")) {
				if (expr.has("query")) {
					value = innerValues(expr, values.next());
				} else {
					value = toList(new JSONArray(valueName));
				}
			} else if (valueName.startsWith("'")) {
				value = valueName.substring(1, valueName.length() - 1);
			} else if (valueName.equals("?")) {
				value = values.next();
			}

			matchOB.add(columnName, ob().add("$" + op, value).get());

		}
	}

	private static Object innerValues(JSONObject expr, Object value) throws JSONException, IOException {

		String queryName = expr.getString("query");
		String joinColumnName = expr.getString("join-column");

		List refValues = new ArrayList();
		List results = select(queryName, null, value);
		for (Object object : results) {
			refValues.add(((DBObject) object).get(joinColumnName));
		}

		return refValues.toArray();
	}

	private static Iterable find(JSONObject queryJSON, JSONArray filters, Object value, int skip, int limit, Map<String, Integer> sortOrderMap) throws JSONException, IOException {

		JSONArray select = queryJSON.getJSONArray("SELECT");

		BasicDBObjectBuilder selectOB = ob();

		for (int i = 0, len = select.length(); i < len; i++) {
			String field = select.getString(i);
			selectOB.add(field, 1);
		}
		// selectOB.add("_id", 0);

		if (queryJSON.has("PIVOT")) {
			JSONObject pivot = queryJSON.getJSONObject("PIVOT");
			String[] pivotKeys = JSONObject.getNames(pivot);
			for (int i = 0, len = pivotKeys.length; i < len; i++) {
				String pivotKey = pivotKeys[i];
				selectOB.add(pivotKey, 1);
				selectOB.add(pivot.getString(pivotKey), 1);
			}
		}

		String from = queryJSON.getString("FROM");
		DBObject matchObj = match(queryJSON, filters, value);
		DBObject selectObj = selectOB.get();

		DB db = MongoDBIntegrator.DBHolder.SINGLETON.getDB();
		DBCursor cursor = db.getCollection(from).find(matchObj, selectObj);

		if (skip > 0) {
			cursor.skip(skip);
		}

		if (limit > 0) {
			cursor.limit(limit);
		}

		if (sortOrderMap != null && sortOrderMap.size() > 0) {

			Set<Entry<String, Integer>> sortOrders = sortOrderMap.entrySet();
			BasicDBObjectBuilder orderByOB = ob();
			for (Entry<String, Integer> sortOrder : sortOrders) {
				orderByOB.add(sortOrder.getKey(), sortOrder.getValue());
			}
			cursor.sort(orderByOB.get());

		}

		Iterable<DBObject> result = cursor;

		if (queryJSON.has("JOIN")) {
			result = join(queryJSON, result);
		}

		if (queryJSON.has("PIVOT")) {
			result = pivot(queryJSON, result);
		}

		return result;
	}

	private static Iterable<DBObject> pivot(JSONObject queryJSON, Iterable<DBObject> results) throws JSONException {

		JSONObject pivot = queryJSON.getJSONObject("PIVOT");
		String[] pivotKeyNames = JSONObject.getNames(pivot);

		List<DBObject> pivotResults = new LinkedList();
		for (DBObject result : results) {

			for (int i = 0, len = pivotKeyNames.length; i < len; i++) {

				String pivotKeyName = pivotKeyNames[i];
				String pivotValueName = pivot.getString(pivotKeyName);

				Object pivotKey = result.get(pivotKeyName);
				if (!(pivotKey instanceof String)) {
					pivotKey = ((BasicDBList) pivotKey).get(0).toString();
				}
				String[] keyList = pivotKey.toString().split("~");

				Object pivotValue = "";

				if (pivotValueName.startsWith("'")) {
					pivotValue = pivotValueName.substring(1, pivotValueName.length() - 1);
				} else {
					pivotValue = result.get(pivotValueName);
					if (!(pivotValue instanceof String)) {
						pivotValue = ((BasicDBList) pivotValue).get(0).toString();
					}
				}

				String[] valueList = pivotValue.toString().split("~");

				for (int j = 0; j < keyList.length; j++) {
					result.put(keyList[j], valueList[j]);
				}
			}

			pivotResults.add(result);
		}

		return pivotResults;
	}

	private static Iterable<DBObject> join(JSONObject queryJSON, Iterable<DBObject> leftResults) throws JSONException, IOException {

		// TODO Handle different column name and join column name
		JSONObject joinConfig = queryJSON.getJSONObject("JOIN");

		String queryName = joinConfig.getString("query");
		String columnName = joinConfig.getString("column");
		String joinColumnName = joinConfig.getString("join-column");

		Set joinValueSet = new HashSet();
		for (DBObject result : leftResults) {
			joinValueSet.add(result.get(columnName));
		}

		List rightResults = select(queryName, new JSONArray().put(joinConfig.put("op", "in")), joinValueSet);

		Map<Object, DBObject> rightResultMap = new HashMap();
		for (Object result : rightResults) {
			rightResultMap.put(((DBObject) result).get(joinColumnName), (DBObject) result);
		}

		List<DBObject> joinedResults = new LinkedList();
		for (DBObject leftResult : leftResults) {
			String value = leftResult.get(columnName).toString();
			DBObject rightResult = rightResultMap.get(value);
			if (rightResult != null) {
				leftResult.putAll(rightResult);
			}
			joinedResults.add(leftResult);
		}
		return joinedResults;
	}

	private static DBObject match(JSONObject queryJSON, JSONArray filtersJSON, Object value) throws JSONException, IOException {

		BasicDBObjectBuilder matchOB = ob();

		Iterator values = null;
		if (value instanceof JSONArray) {
			values = toList((JSONArray) value).iterator();
		} else {
			values = Arrays.asList(value).iterator();
		}

		if (queryJSON.has("WHERE")) {
			JSONArray whereJSON = queryJSON.getJSONArray("WHERE");
			addMatch(matchOB, whereJSON, values);
		}

		if (filtersJSON != null && !"all".equalsIgnoreCase(value.toString())) {
			addMatch(matchOB, filtersJSON, values);
		}

		DBObject matchObj = matchOB.get();
		return matchObj;
	}

	public static List toList(JSONArray jsonArr) throws JSONException {
		List arrList = new ArrayList();
		for (int i = 0; i < jsonArr.length(); i++) {
			Object element = jsonArr.get(i);
			arrList.add(element);
		}
		return arrList;
	}

	private static BasicDBObjectBuilder ob() {
		return BasicDBObjectBuilder.start();
	}

}