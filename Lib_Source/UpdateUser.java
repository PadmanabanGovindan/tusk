package Lib_Source;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

@WebServlet("/UpdateUser")
public class UpdateUser extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public boolean checkParam(String input)
	{
		if(input == null || input.equals(""))
			return true;
		else
			return false;
		
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		

		PrintWriter out = response.getWriter();
		JSONObject status_message = new JSONObject();
		Cookie[] cookies = request.getCookies();
		status_message.put("Status", "UnSuccessful");
		
		if(checkAuthentication(cookies,true))
		{
		
		String jsonString = IOUtils.toString(request.getInputStream());
		
		  
		JSONObject jsonObject = new JSONObject(jsonString);
		
		//out.println(jsonObject);
		
		String old_user_login_id = null;
		String new_login_id = null;
		String name = null;
		String mobile = null;
		String user_type = null;
		String password = null;
		
		UpdateQuery uq = new UpdateQueryImpl("User");
		
		if(jsonObject.has("LOGIN_ID")) {
			old_user_login_id = jsonObject.get("LOGIN_ID").toString();
		}
		if(checkParam(old_user_login_id))
		{
			status_message.put("Message","Please enter User Login Id");
		}
		else
		{
			if(checkValue("USER_LOGIN_ID", old_user_login_id)==false)
			{
				status_message.put("Message","User Not Found");
			}
			else
				{
				
				if(jsonObject.has("USER_LOGIN_ID")) {
					new_login_id = jsonObject.get("USER_LOGIN_ID").toString();
					uq.setUpdateColumn("USER_LOGIN_ID",new_login_id);
				}
				if(checkValue("USER_LOGIN_ID", new_login_id))
				{
					status_message.put("Message","Login ID already exists choose another Login ID");
				}
				
				else
				{
				
				if(jsonObject.has("USER_NAME")) {
					name = jsonObject.get("USER_NAME").toString();
					uq.setUpdateColumn("USER_NAME",name);
				}
				boolean mobile_flag = true;
				if(jsonObject.has("MOBILE_NUMBER")) {
					mobile = jsonObject.get("MOBILE_NUMBER").toString();
					uq.setUpdateColumn("MOBILE_NUMBER",mobile);
					if(checkValue("MOBILE_NUMBER", mobile))
					{
						mobile_flag = false;
					}
				}
				if(mobile_flag == false)
				{
					status_message.put("Message","Mobile Number already exists choose another !!!");
				}
				
				else
				{
				boolean user_flag = true;
				if(jsonObject.has("USER_TYPE")) {
					user_type = jsonObject.get("USER_TYPE").toString();
					uq.setUpdateColumn("USER_TYPE",user_type);
					if(!user_type.equals("User") && !user_type.equals("Member"))
					{
						user_flag=false;
					}
					
				}
				if(user_flag == false)
				{
					status_message.put("Message","Please Enter User Type as User or Member");
				}
				else
				{
				
				if(jsonObject.has("PASSWORD")) {
					password = jsonObject.get("PASSWORD").toString();
					uq.setUpdateColumn("PASSWORD",password);
				}
				
										
										Criteria c = new Criteria(Column.getColumn("User", "USER_LOGIN_ID"), old_user_login_id, QueryConstants.EQUAL);
										uq.setCriteria(c);
										
								
										try {
											DataAccess.update(uq);
											status_message.put("Message","Update Successful");
											status_message.put("Status", "Successful");
										} catch (DataAccessException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
											status_message.put("Message","Update UnSuccessful");
										}
							}
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
	
public boolean checkAuthentication(Cookie[] cookies, boolean admin_flag) {
		
		String loginid = null;
		String pass = null;
		
		
		if(cookies !=null){
		for(Cookie cookie : cookies){
			if(cookie.getName().equals("user")) loginid = cookie.getValue();
			if(cookie.getName().equals("pass")) pass = cookie.getValue();
			}
		}
		
		
		//Select Query for validating the User and Password----------------------------------------------------------
		
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
			conn.close();
		}catch (SQLException | QueryConstructionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		boolean cred_flag;
		boolean auth_flag = false;
		
		
		if(count==0) {
			cred_flag = false;
		}
		else
		{
			cred_flag =  true;
		}
		
		
		//---------------------------Authenticating Cookies------------
		
		if (admin_flag == true) 
		{
			if(loginid != null && cred_flag && loginid.equals("Admin")) 
			{
				auth_flag = true;
			}
		}
		else if(admin_flag == false) 
		{
			if(loginid != null && cred_flag ) 
			{
				auth_flag = true;
			}
		}
		return auth_flag;
	}
	
public boolean checkValue(String column, String value) {
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("User"));
		
		Column c = new Column(null,"*").count();
		c.setColumnAlias("COUNT");
		query.addSelectColumn(c);
		
		Criteria c1 = new Criteria(Column.getColumn("User",column), value, QueryConstants.EQUAL);
		query.setCriteria(c1);
		

		DataSet ds;
		int count = 0;
		try {
			Connection conn = RelationalAPI.getInstance().getConnection();
			ds = RelationalAPI.getInstance().executeQuery(query, conn);
			while (ds.next())
		    {
				count =  Integer.parseInt(ds.getValue("COUNT").toString());
				
		    }
			conn.close();
		}catch (SQLException | QueryConstructionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		
		if(count==0) {
			return false;
		}
		else
		{
			return true;
		}
		
	}
	
}

