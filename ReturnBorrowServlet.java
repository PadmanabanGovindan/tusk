import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;


@WebServlet("/ReturnBorrowServlet")
public class ReturnBorrowServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	

	Util util = new Util();
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		JSONObject status_message = new JSONObject();
		status_message.put("Status","Unsuccessful");
		
		Cookie[] cookies = request.getCookies();
		
		boolean update = false;

		if(util.checkAuthentication(cookies,true))
		{
			//Login ID
			String login_id =  request.getParameter("USER_LOGIN_ID");
			status_message = util.checkParamValidation(login_id,"USER_LOGIN_ID", "User", update, false);
			if(status_message.get("Status").equals("Unsuccessful"))
			{
				out.println(status_message);
				return;
			}
			
			//Book Name
			String book_name =  request.getParameter("BOOK_NAME");
			status_message = util.checkParamValidation(book_name,"BOOK_NAME", "Book", update, false);
			if(status_message.get("Status").equals("Unsuccessful"))
			{
				out.println(status_message);
				return;
			}
			
			
			status_message = util.returnBorrow(login_id, book_name);
			
				
		}
		else
		{
		status_message.put("Message","Login as Admin to Continue");
		}
		
		
		out.println(status_message);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
	}

}
