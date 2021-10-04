import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;


@WebServlet("/BookServlet")
public class BookServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
 
	Util util = new Util();
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		JSONObject status_message = new JSONObject();
		
		Cookie[] cookies = request.getCookies();
		
		String table = "Book";
		boolean update = false;

		if(util.checkAuthentication(cookies,true))
		{
			//Book Name
			String book_name = request.getParameter("BOOK_NAME");
			status_message = util.checkParamValidation(book_name,"BOOK_NAME", table, update, true);
			if(status_message.get("Status").equals("Unsuccessful"))
			{
				out.println(status_message);
				return;
			}
			
			//ISBN
			String isbn = request.getParameter("ISBN");
			status_message = util.checkParamValidation(isbn,"ISBN", table, update, true);
			if(status_message.get("Status").equals("Unsuccessful"))
			{
				out.println(status_message);
				return;
			}
			
			//Quantity
			String quantity = request.getParameter("QUANTITY");
			status_message = util.checkParamValidation(quantity,"QUANTITY", table, update, false);
			if(status_message.get("Status").equals("Unsuccessful"))
			{
				out.println(status_message);
				return;
			}
			
			//Contributor Id
			String contributor_login_id = request.getParameter("CONTRIBUTOR_LOGIN_ID");
			if(contributor_login_id != null)
			{
				status_message = util.checkParamValidation(contributor_login_id,"CONTRIBUTOR_ID", "USER", update, false);
				if(status_message.get("Status").equals("Unsuccessful"))
				{
					out.println(status_message);
					return;
				}
			}
			
			//DBcall
			if(util.insertBook(book_name, isbn, quantity, contributor_login_id))
			{
				status_message.put("Message","Book Added Successfully");
			}
			else
			{
				status_message.put("Message","Book Add Unsuccessful");
			}
	
		}
		else
		{
			status_message.put("Status","Unsuccessful");
			status_message.put("Message","Login as Admin to Continue");
		}
		
		out.println(status_message);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
			JSONObject status_message = new JSONObject();
			boolean out_flag = false;;
			
			JSONArray sq = new JSONArray();
			status_message.put("Status","Successful");
	
			Cookie[] cookies = request.getCookies();
			
			String value = null;
			String column =  null;
			
			if(util.checkAuthentication(cookies,false))
			{
				sq=util.getBook(value, column);
				
				if(sq==null) 
				{
					status_message.put("Status","Unsuccessful");
					status_message.put("Message","Unable to retrieve Book Data");
				}
				else
				{
					out_flag = true;
					status_message.put("Message","Displaying Book Data");
				}
			}
				
			else
			{
				status_message.put("Message","Login as Admin to Continue");
			}
			if(out_flag) 
			{
				out.println(status_message);
				out.println(sq);
			}
			else
				out.println(status_message);
			

	    }
		
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			PrintWriter out = response.getWriter();
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
			JSONObject status_message = new JSONObject();
			status_message.put("Status","Unsuccessful");
	
			Cookie[] cookies = request.getCookies();
			
			String table = "Book";
			String column = "BOOK_NAME";
			
			if(util.checkAuthentication(cookies,true))
			{
				String book_name = request.getParameter(column);
				status_message = util.checkParamValidation(book_name, column, table, false, false);
				if(status_message.get("Status").equals("Unsuccessful"))
				{
					out.println(status_message);
					return;
				}
				//DBcall
				if(util.deleteUser(book_name, column, table))
				{
					status_message.put("Message","Book Deleted Successfully");
				}
				else
				{
					status_message.put("Message","Book Delete Unsuccessful");
				}
			}
			else
			{
				status_message.put("Message","Login as Admin to Continue");
			}
			out.println(status_message);
		}
}

