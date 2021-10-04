import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;


@WebServlet("/UserServlet")
public class UserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
  
	Util util = new Util();
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		JSONObject status_message = new JSONObject();
		
		Cookie[] cookies = request.getCookies();
		
		String table = "User";
		boolean update = false;

		if(util.checkAuthentication(cookies,true))
		{
			//Login ID
			String login_id =  request.getParameter("USER_LOGIN_ID");
			status_message = util.checkParamValidation(login_id,"USER_LOGIN_ID", table, update, true);
			if(status_message.get("Status").equals("Unsuccessful"))
			{
				out.println(status_message);
				return;
			}
			
			//User Name
			String user_name =  request.getParameter("USER_NAME");
			status_message = util.checkParamValidation(user_name,"USER_NAME", table, update, false);
			if(status_message.get("Status").equals("Unsuccessful"))
			{
				out.println(status_message);
				return;
			}
			
			//Mobile
			String mobile_number =  request.getParameter("MOBILE_NUMBER");
			status_message = util.checkParamValidation(mobile_number,"MOBILE_NUMBER", table, update, true);
			if(status_message.get("Status").equals("Unsuccessful"))
			{
				out.println(status_message);
				return;
			}
			
			//User Type
			String user_type =  request.getParameter("USER_TYPE");
			status_message = util.checkParamValidation(user_type,"USER_TYPE", table, update, false);
			if(status_message.get("Status").equals("Unsuccessful"))
			{
				out.println(status_message);
				return;
			}
			
			//Password
			String password =  request.getParameter("PASSWORD");
			status_message = util.checkParamValidation(password,"PASSWORD", table, update, false);
			if(status_message.get("Status").equals("Unsuccessful"))
			{
				out.println(status_message);
				return;
			}
			
			String passwordEncrypt = util.encryptPassword(password);
			
			//DBcall
			if(util.insertUser(login_id, user_name, mobile_number, user_type, passwordEncrypt))
			{
				status_message.put("Message","User Added Successfully");
			}
			else
			{
				status_message.put("Message","User Add Unsuccessful");
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
		
		boolean out_flag = false;
		JSONArray sq = new JSONArray();
		status_message.put("Status","Unsuccessful");

		Cookie[] cookies = request.getCookies();
		
		String value = null;
		String column = null;
		
		if(util.checkAuthentication(cookies,true))
		{
			sq=util.getUser(value, column);
			if(sq==null) 
			{
				status_message.put("Message","Unable to retrieve User Data");
			}
			else
			{
				out_flag = true;
				status_message.put("Status","Successful");
				status_message.put("Message","Displaying User Data");
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
	
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		JSONObject status_message = new JSONObject();
		status_message.put("Status","Unsuccessful");

		Cookie[] cookies = request.getCookies();
		
		if(util.checkAuthentication(cookies,true))
		{
			String stringInput = IOUtils.toString(request.getInputStream()); 
			JSONObject jsonObject = new JSONObject(stringInput);
			
			String old_user_login_id = null;
			String user_login_id = null;
			String user_name = null;
			String mobile_number = null;
			String user_type = null;
			String password = null;
			String table = "User";
			boolean update = true;
			
			//Old Login Id
			if(jsonObject.has("OLD_USER_LOGIN_ID")) {
				old_user_login_id =  jsonObject.get("OLD_USER_LOGIN_ID").toString();
				status_message = util.checkParamValidation(old_user_login_id,"USER_LOGIN_ID", table, false, false);
				if(status_message.get("Status").equals("Unsuccessful"))
				{
					out.println(status_message);
					return;
				}
			}
			else
			{
				status_message.put("Message","Please Enter User ID to update");
				out.println(status_message);
				return;
			}
			
			//Updated Login Id
			if(jsonObject.has("USER_LOGIN_ID")) {
				user_login_id =  jsonObject.get("USER_LOGIN_ID").toString();
				status_message = util.checkParamValidation(user_login_id,"USER_LOGIN_ID", table, update, true);
				if(status_message.get("Status").equals("Unsuccessful"))
				{
					out.println(status_message);
					return;
				}
			}
			
			//User Name
			if(jsonObject.has("USER_NAME")) {
				user_name =  jsonObject.get("USER_NAME").toString();
				status_message = util.checkParamValidation(user_name,"USER_NAME", table, update, false);
				if(status_message.get("Status").equals("Unsuccessful"))
				{
					out.println(status_message);
					return;
				}
			}
			
			//Mobile Number
			if(jsonObject.has("MOBILE_NUMBER")) {
				mobile_number =  jsonObject.get("MOBILE_NUMBER").toString();
				status_message = util.checkParamValidation(mobile_number,"MOBILE_NUMBER", table, update, true);
				if(status_message.get("Status").equals("Unsuccessful"))
				{
					out.println(status_message);
					return;
				}
			}
			
			//User Type
			if(jsonObject.has("USER_TYPE")) {
				user_type =  jsonObject.get("USER_TYPE").toString();
				status_message = util.checkParamValidation(user_type,"USER_TYPE", table, update, false);
				if(status_message.get("Status").equals("Unsuccessful"))
				{
					out.println(status_message);
					return;
				}
			}
			
			//Password
			if(jsonObject.has("PASSWORD")) {
				password =  jsonObject.get("PASSWORD").toString();
				status_message = util.checkParamValidation(password,"PASSWORD", table, update, false);
				if(status_message.get("Status").equals("Unsuccessful"))
				{
					out.println(status_message);
					return;
				}
			}
			String passwordEncrypt = util.encryptPassword(password);
			
			//DBcall
			if(util.updateUser(old_user_login_id, user_login_id, user_name, mobile_number, user_type, passwordEncrypt))
			{
				status_message.put("Message","User Updated Successfully");
			}
			else
			{
				status_message.put("Message","User Update Unsuccessful");
			}
	
		}
		else
		{
			status_message.put("Status","Unsuccessful");
			status_message.put("Message","Login as Admin to Continue");
		}
		
		
		out.println(status_message);
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		JSONObject status_message = new JSONObject();
		status_message.put("Status","Unsuccessful");

		Cookie[] cookies = request.getCookies();
		
		String table = "User";
		String column = "USER_LOGIN_ID";
		
		if(util.checkAuthentication(cookies,true))
		{
			String user_login_id = request.getParameter(column);
			status_message = util.checkParamValidation(user_login_id, column, table, false, false);
			if(status_message.get("Status").equals("Unsuccessful"))
			{
				out.println(status_message);
				return;
			}
			//DBcall
			if(util.deleteUser(user_login_id, column, table))
			{
				status_message.put("Message","User Deleted Successfully");
			}
			else
			{
				status_message.put("Message","User Delete Unsuccessful");
			}
		}
		else
		{
			status_message.put("Message","Login as Admin to Continue");
		}
		out.println(status_message);
	}
	
}
	
	
	
	
	
	
	
//	public JSONObject checkParam(JSONObject jsonInputParams, boolean update) {
//		
//		JSONObject flag = new JSONObject();
//		flag.put("Status", "UnSuccessful");
//		
//		String login_id = null;
//		String name = null;
//		String mobile = null;
//		String user_type = null;
//		String password = null;
//		
//		if(jsonInputParams.has("USER_LOGIN_ID"))
//		{
//			login_id = jsonInputParams.getString("USER_LOGIN_ID").toString();
//			flag = verifyUniqueParam(login_id);
//			if(flag.get("Status").equals("Unsuccessful"))
//			{
//				return flag;
//			}
//		}
//		else if(!jsonInputParams.has("USER_LOGIN_ID") && update == false)
//		{
//			flag.put("Message", "Please Enter User Login Id");
//			return flag;
//		}
//		
//		
//		if(jsonInputParams.has("USER_LOGIN_ID"))
//		{
//			login_id = jsonInputParams.getString("USER_LOGIN_ID").toString();
//			flag = verifyUniqueParam(login_id);
//			if(flag.get("Status").equals("Unsuccessful"))
//			{
//				return flag;
//			}
//		}
//		else if(!jsonInputParams.has("USER_LOGIN_ID") && update == false)
//		{
//			flag.put("Message", "Please Enter User Login Id");
//			return flag;
//		}
//		
//		
//		
//		return validationResponse;
//	}
	




//
//if(jsonInputParams.has("USER_LOGIN_ID"))
//{
//	login_id = jsonInputParams.getString("USER_LOGIN_ID").toString();
//	if(util.checkParam(login_id))
//	{
//		validationResponse.put("Message", "Please Enter User Login Id");
//		return validationResponse;
//	}
//	else
//	{
//		
//	}
//	
//}
//else if(!jsonInputParams.has("USER_LOGIN_ID") && update == false)
//{
//	validationResponse.put("Message", "Please Enter User Login Id");
//	return validationResponse;
//}
//
//validationResponse.put("Message", "Please Enter User Id");
