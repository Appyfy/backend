package rnd.data;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import rnd.util.IOUtils;

/**
 * @author Vinod Pahuja
 * 
 */

// @Named
// @RequestScoped
// @WebServlet(asyncSupported = true, urlPatterns = "/data")
public class FileController extends HttpServlet {

	private static final long serialVersionUID = -1514025477286265688L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		uploadFile(request);
	}

	protected void uploadFile(HttpServletRequest request) throws IOException {
		InputStream is = request.getInputStream();
		String data = IOUtils.readPlainContent(is);
		System.out.println(data);
	}

}