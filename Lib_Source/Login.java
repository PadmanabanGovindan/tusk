package Lib_Source;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;



@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		JSONObject status_message = new JSONObject();
		
		status_message.put("Status","Unsuccessful");
		
		String userid = request.getParameter("user_id");
		if(userid == null || userid.equals(""))
		{
			status_message.put("Message","Please Enter Login Id ");
		}
		else {
			
		String pass = request.getParameter("password");
		if(pass == null ||pass.equals(""))
		{
			status_message.put("Message","Please Enter Password");
		}
		else {
		
		Cookie loginIdCookie = new Cookie("user",userid);
		Cookie loginPassCookie = new Cookie("pass", pass);
		loginIdCookie.setMaxAge(30*60);
		loginPassCookie.setMaxAge(30*60);
		
		
		
		if(checkLogin(userid, pass)==true)
		{
			response.addCookie(loginIdCookie);
			response.addCookie(loginPassCookie);
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
	
	public boolean checkLogin(String loginid, String pass) {
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("User"));
		
		Column c = new Column(null,"*").count();
		c.setColumnAlias("COUNT");
		query.addSelectColumn(c);
		
		Criteria c1 = new Criteria(Column.getColumn("User","USER_LOGIN_ID"), loginid, QueryConstants.EQUAL);
		Criteria c2 = new Criteria(Column.getColumn("User","PASSWORD"), pass, QueryConstants.EQUAL);
		Criteria c3 = c1.and(c2);
		query.setCriteria(c3);
		

		DataSet ds;
		int count = 0;
		try {
			Connection conn = RelationalAPI.getInstance().getConnection();
			ds = RelationalAPI.getInstance().executeQuery(query, conn);
			while (ds.next())
		    {
				count =  Integer.parseInt(ds.getValue("COUNT").toString());
				
		    }
		}catch (SQLException | QueryConstructionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//		try {
//			System.out.println(RelationalAPI.getInstance().getSelectSQL(query));
//		} catch (QueryConstructionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		if(count==0) {
			return false;
		}
		else
		{
			return true;
		}
		
	}
	
		
	

}
