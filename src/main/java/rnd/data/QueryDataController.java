package rnd.data;

import java.io.IOException;

import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;

import rnd.dao.nosql.mongodb.MongoDBAccessObject;

import com.mongodb.BasicDBList;

@Named
public class QueryDataController extends HttpServlet {

	private static final long serialVersionUID = 7490332748896525227L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			BasicDBList dataList = getQueryData(request);
			response.getWriter().write(dataList.toString());
		} catch (JSONException e) {
			throw new ServletException(e);
		}

	}

	public static BasicDBList getQueryData(HttpServletRequest request) throws IOException, JSONException {

		String queryName = request.getParameter("name");

		Object value = request.getParameter("value");
		if (value != null && value.toString().startsWith("[")) {
			value = new JSONArray(value.toString());
		}

		// Pagination
		int skip = 0;
		if (request.getParameter("start") != null) {
			skip = Integer.parseInt(request.getParameter("start"));
		}

		int limit = 0;
		if (request.getParameter("length") != null) {
			limit = Integer.parseInt(request.getParameter("length"));
		}

		return MongoDBAccessObject.select(queryName, null, value, skip, limit, null);

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

}