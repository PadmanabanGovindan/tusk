import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	Util util = new Util();
       

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		JSONObject status_message = new JSONObject();
		status_message.put("Status","Successful");
		
		HttpSession session = request.getSession();
		session.invalidate();
		
		Cookie loginidCookie = null;
		Cookie tokenCookie = null;
		
    	Cookie[] cookies = request.getCookies();
    	if(cookies != null){
    		
    		loginidCookie = clearCookie(cookies, "user");
    		tokenCookie = clearCookie(cookies, "token");
    		
    		response.addCookie(loginidCookie);
    		response.addCookie(tokenCookie);
 		
    	status_message.put("Message","Successfully Logged Out!!!");
		out.println(status_message);
		
    	}
	}


	private Cookie clearCookie(Cookie[] cookies, String cookie_name) {
		
		Cookie authCookie = null;
		for(Cookie cookie : cookies){
    		if(cookie.getName().equals(cookie_name)){
    			authCookie = cookie;
    			if(cookie_name.equals("user"))
    			{
    				util.deleteUser(cookie.getValue(), "USER_LOGIN_ID", "Authtoken");
    			}
    			break;
    		}
    	}
		
		if(authCookie != null){
    		authCookie.setMaxAge(0);
    	}
		return authCookie;
		
	}

}
