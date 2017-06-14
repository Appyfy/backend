package rnd.data;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;

import rnd.data.integrator.nosql.mongodb.MongoDBIntegrator;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

// @Named
// @RequestScoped
// @WebServlet(asyncSupported = true, urlPatterns = "/data")
public class DataController extends HttpServlet {

	private static final long serialVersionUID = -1514025477286265688L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		fetchData(request, response);
	}

	protected void fetchData(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String[] args = URLDecoder.decode(request.getRequestURI(),"utf-8").split("/");

		DBObject queryObj = new BasicDBObject();
		
		if(args.length > 4) {
			String queryStr = args[4];
			String[] params = queryStr.split("&");
			for (String param : params) {
				String[] keyVal = param.split("=");
				if(keyVal.length > 1){
					queryObj.put(keyVal[0], keyVal[1]);
				}
			}
		}
		
//		Map paramMap = request.getParameterMap();
//		for (Iterator iterator = paramMap.entrySet().iterator(); iterator.hasNext();) {
//			Entry entry = (Entry) iterator.next();
//			queryObj.put(entry.getKey().toString(), entry.getValue());
//		}

		DBCursor cursor = MongoDBIntegrator.DBHolder.SINGLETON.getDB().getCollection(args[3]).find(queryObj);

		List list = new ArrayList();
		for (DBObject dbObject : cursor) {
			ObjectId id = (ObjectId) dbObject.removeField("_id");
			((BasicDBObject) dbObject).put("ID", id.toString());
			list.add(dbObject);
		}

		response.getWriter().append(list.toString());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		saveObject(request);
	}

	protected void saveObject(HttpServletRequest request) throws IOException {

		String[] args = request.getRequestURI().split("/");
		String data = request.getReader().readLine();

		DBObject dbObject = (DBObject) JSON.parse(data);
		if (dbObject.containsField("ID")) {
			((BasicDBObject) dbObject).put("_id", new ObjectId(dbObject.removeField("ID").toString()));
		}
		MongoDBIntegrator.DBHolder.SINGLETON.getDB().getCollection(args[3]).save(dbObject);
	}

}