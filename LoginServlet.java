import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ThreadLocalRandom;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;



@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	Util util = new Util();
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		JSONObject status_message = new JSONObject();
		status_message.put("Status","Unsuccessful");
		
		
		
		String userid = request.getParameter("user_id");
		if(util.checkParam(userid))
		{
			status_message.put("Message","Please Enter Login Id ");
		}
		else {
			
		String pass = request.getParameter("password");
		if(util.checkParam(pass))
		{
			status_message.put("Message","Please Enter Password");
		}
		
		else {
			
			String password = util.encryptPassword(pass);
			
			String token = String.valueOf(ThreadLocalRandom.current().nextInt());
			
		
		Cookie loginIdCookie = new Cookie("user",userid);
		Cookie loginPassCookie = new Cookie("token", token);
		loginIdCookie.setMaxAge(30*60);
		loginPassCookie.setMaxAge(30*60);
		
		
		if(util.checkLogin(userid, password)==true)
		{
			response.addCookie(loginIdCookie);
			response.addCookie(loginPassCookie);
			
			util.addAuthToken(userid,token);
			status_message.put("Status","Successful");
			status_message.put("Message","Successfully Logged In !!!!!");
		}
		else
		{
			status_message.put("Message","Login Failed");
		}
		
	}
	}
		
		out.println(status_message);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		

	
	}
	

}
