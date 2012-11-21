package org.comtor.comtoradmin;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.dynamodb.model.AttributeValue;

/**
 * Servlet implementation class ConsoleServlet
 */
@WebServlet("/ConsoleServlet")
public class ConsoleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConsoleServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get action to perform if any from URL
		if(request.getParameterMap().containsKey("do")){
			String action = request.getParameter("do");
			
			if(action.equals("delete")){
				String apikey = request.getParameter("key");
				String email = request.getParameter("email");
			
				DynamoHelper.deleteItem(action, apikey, email);
			}
		}
				
		List<Map<String, AttributeValue>> result = DynamoHelper.scanDynamo("comtorusers");
		request.setAttribute("result", result);
		
		// Designate what JSP to assign Java Bean to
		RequestDispatcher rd = request.getRequestDispatcher("/index.jsp");
		
		// Send Java Bean
        rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
