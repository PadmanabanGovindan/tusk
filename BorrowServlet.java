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

@WebServlet("/BorrowServlet")
public class BorrowServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Util util = new Util();
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		JSONObject status_message = new JSONObject();
		status_message.put("Status","Unsuccessful");
		
		JSONObject user_details = new JSONObject();
		JSONObject book_details = new JSONObject();
		
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
			
			user_details=(util.getUser(login_id, "USER_LOGIN_ID")).getJSONObject(0);
			book_details=(util.getBook(book_name, "BOOK_NAME")).getJSONObject(0);
			
			String user_id = user_details.get("User_Id").toString();
			String user_type = user_details.get("User_Type").toString();
			
			String book_id = book_details.get("Book_Id").toString();
			int available = Integer.parseInt(book_details.get("Available").toString());
			
			if(available<=0)
			{
				status_message.put("Message","The Book is Currently Unavailable");
			}
			else
			{
				if(util.checkBorrowLimit(user_id, user_type)) {
					status_message.put("Message","Borrow Limit Exceeded");
					}
					else
					{
						if(util.checkDoubleEntry(user_id, book_id)) {
							status_message.put("Message","Book already borrowed, Return the book to borrow again !!");
						}
						else
						{
							if(util.addBorrow(user_id,book_id,available))
							{
								status_message.put("Status","Successful");
								status_message.put("Message","Borrow Success");
							}
							else
							{
								status_message.put("Message","Borrow Failed");
							}
						}
						
					}
					
				}
			}
			else
			{
			status_message.put("Message","Login as Admin to Continue");
			}
			
			
			out.println(status_message);
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
		}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		JSONObject status_message = new JSONObject();
		
		JSONArray sq = new JSONArray();
		status_message.put("Status","Successful");

		Cookie[] cookies = request.getCookies();
		
		String value = null;
		String column = null;
		
		if(util.checkAuthentication(cookies,false))
		{
			
			for(Cookie cookie : cookies){
	    		if(cookie.getName().equals("user")){
	    			
	    				value = cookie.getValue();
	    				if(value.equals("Admin"))
	    				{
	    					value = request.getParameter("data");
	    					column = request.getParameter("column");
	    				}
	    				else
	    				{
	    					column = "USER_LOGIN_ID";
	    				}
	    				break;
	    		}
			}
			
			if(value != null && column != null)
			{
				if(column.equals("USER_LOGIN_ID"))
				{
					status_message = util.checkParamValidation(value,"USER_LOGIN_ID", "User", false, false);
					if(status_message.get("Status").equals("Unsuccessful"))
					{
						out.println(status_message);
						return;
					}
					
					JSONObject user_details=(util.getUser(value, column)).getJSONObject(0);
					value = user_details.get("User_Id").toString();
					column = "USER_ID";
				}
				
				if(column.equals("STATUS"))
				{
					if(!value.equals("Borrowed") && !value.equals("Returned"))
					{
						status_message.put("Message","Entered status does not match  Borrowed or Returned  ");
						out.println(status_message);
						return;
					}
				}
			}
			else
			{
				value = null;
				column = null;
			}
			
			sq=util.getBorrow(value, column);
		}
			
		else
		{
			status_message.put("Message","Login as Admin to Continue");
		}
		
		if(sq==null) 
		{
			status_message.put("Status","Unsuccessful");
			status_message.put("Message","Unable to retrieve Borrow Details");
			out.println(status_message);
		}
		else
		{
			status_message.put("Message","Displaying Borrow Details");
			out.println(status_message);
			out.println(sq);
		}
    }

}
