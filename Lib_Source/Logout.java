package Lib_Source;

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

@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		JSONObject status_message = new JSONObject();
		status_message.put("Status","Successful");
		
		HttpSession session = request.getSession();
		session.removeAttribute("userid");
		session.invalidate();
		Cookie loginidCookie = null;
		Cookie passCookie = null;
    	Cookie[] cookies = request.getCookies();
    	if(cookies != null){
    	for(Cookie cookie : cookies){
    		if(cookie.getName().equals("user")){
    			loginidCookie = cookie;
    			break;
    		}
    	}
    	for(Cookie cookie : cookies){
    		if(cookie.getName().equals("pass")){
    			passCookie = cookie;
    			break;
    		}
    	}
    	}
    	if(loginidCookie != null){
    		loginidCookie.setMaxAge(0);
        	response.addCookie(loginidCookie);
    	}
    	if(passCookie != null){
    		passCookie.setMaxAge(0);
        	response.addCookie(passCookie);
    	}
    	status_message.put("Message","Successfully Logged Out!!!");
		out.println(status_message);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		
	}

}
