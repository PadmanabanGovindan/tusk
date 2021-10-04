package Lib_Source;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Table;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/User")
public class User extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	//Book newBook = new Book();


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		
		PrintWriter out = response.getWriter();
		JSONObject status_message = new JSONObject();
		
		//newBook.checkParam(getServletInfo());
		
		Cookie[] cookies = request.getCookies();
		status_message.put("Status", "UnSuccessful");
		
		if(checkAuthentication(cookies,true))
		{
		
			try {
				MetaDataUtil.getTableDefinitionByName("User");
			} catch (MetaDataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		
			String name = request.getParameter("USER_NAME");
			if(checkParam(name)) {
				status_message.put("Message","Please Enter User Name");
			}
			else 
			{
				
				String login_id = request.getParameter("USER_LOGIN_ID");
				if(checkParam(login_id)) {
					status_message.put("Message","Please Enter LoginId");
				}
				else
				{
					if(checkValue("USER_LOGIN_ID", login_id))
					{
						status_message.put("Message","Login ID already exists choose another Login ID");
					}
					
					else
					{
						String doj = java.time.LocalDate.now().toString();
						String mobile = request.getParameter("MOBILE_NUMBER");
						if(checkParam(mobile)) {
							status_message.put("Message","Please Enter Mobile Number");
						}
						else
						{
							if(checkValue("MOBILE_NUMBER", mobile))
							{
								status_message.put("Message","Mobile Number already exists choose another Mobile Number");
								//return;
							}
							else
							{
								String user_type = request.getParameter("USER_TYPE");
								if(checkParam(user_type)) {
									status_message.put("Message","Please Enter User type");
								}
								else if(!user_type.equals("User") && !user_type.equals("Member"))
								{
									status_message.put("Message","Please Enter User Type as User or Member");
								}
								else
								{
									String password = request.getParameter("PASSWORD");
									if(checkParam(password)) {
										status_message.put("Message","Please Enter Password");
									}
									else
									{
										Row r = new Row("User");
										r.set("USER_NAME",name);
										r.set("USER_LOGIN_ID",login_id);
										r.set("DOJ", doj);
										r.set("MOBILE_NUMBER", mobile);
										r.set("USER_TYPE", user_type);
										r.set("PASSWORD", password);
										DataObject dob=new WritableDataObject();
										
								
										try {
											dob.addRow(r);
											DataAccess.add(dob);
											status_message.put("Status", "Successful");
											status_message.put("Message","User Added Successully");
										} catch (DataAccessException e) {
											e.printStackTrace();
											status_message.put("Message","User Add UnSuccessful");
										}
										
										
									}
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
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		PrintWriter out = response.getWriter();
		JSONObject status_message = new JSONObject();
		JSONArray sq = new JSONArray();
		boolean error_flag = true;
		status_message.put("Status","Unsuccessful");

		Cookie[] cookies = request.getCookies();
		
		if(checkAuthentication(cookies,true))
		{
			
			try {
				MetaDataUtil.getTableDefinitionByName("User");
			} catch (MetaDataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		
			//----------------------------------------------------------------------------------------------------------------------
			
			SelectQuery query = new SelectQueryImpl(Table.getTable("User"));
			query.addSelectColumn(Column.getColumn(null,"*"));
			
			Column sbc1 = new Column("User","USER_NAME");
			SortColumn sbc = new SortColumn(sbc1,true);
			
			query.addSortColumn(sbc);
			
			//----------------------------------------------------------------------------------------------------------------------
			
			DataSet ds;
			
			try {
				Connection conn = RelationalAPI.getInstance().getConnection();
				ds = RelationalAPI.getInstance().executeQuery(query, conn);
				while (ds.next())
			    {
					JSONObject record = new JSONObject();
					record.put("User_Id", ds.getValue("USER_ID"));
					record.put("User_Name", ds.getValue("USER_NAME"));
					record.put("DOJ", ds.getValue("DOJ"));
					record.put("Mobile_Number", ds.getValue("MOBILE_NUMBER"));
					record.put("User_Login_Id", ds.getValue("USER_LOGIN_ID"));
					record.put("User_Type", ds.getValue("USER_TYPE"));
					sq.put(record);
			    }
				conn.close();
				status_message.put("Status","Successful");
				status_message.put("Message","Displaying User Details");
				error_flag = false;
				
			} catch (SQLException | QueryConstructionException e1) {
				status_message.put("Message","Error Retrieving User Details");
				e1.printStackTrace();
			}
			
			
			
			}
		else
		{
			
			status_message.put("Message","Login as Admin to Continue");

		}
		out.println(status_message);
		if(error_flag==false) 
		{
			out.println(sq);
		}
		
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		

    }
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		JSONObject status_message = new JSONObject();
		status_message.put("Status","Unsuccessful");
		
		PrintWriter out = response.getWriter();
		
		
		Cookie[] cookies = request.getCookies();
		
		if(checkAuthentication(cookies,true))
		{
			
			try {
				MetaDataUtil.getTableDefinitionByName("User");
			} catch (MetaDataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		
			String user_login_id = request.getParameter("USER_LOGIN_ID");
			
			if(checkParam(user_login_id)) {
				status_message.put("Message","Please Enter LoginId");
			}
			else
			{
			
		
			
			if(checkValue("USER_LOGIN_ID", user_login_id)==false)
			{
				status_message.put("Message","User Not Found");
			}
			else
			{
				//------------------------------------------------------------------------------
				
				DeleteQuery dq = new DeleteQueryImpl("User");
				
				Criteria c = new Criteria(Column.getColumn("User", "USER_LOGIN_ID"), user_login_id, QueryConstants.EQUAL);
			
				dq.setCriteria(c);
				
				
				//------------------------------------------------------------------------------
				
				try {
					DataAccess.delete(dq);
					status_message.put("Status","Successful");
					status_message.put("Message","User Deleted Successfully");
				} catch (DataAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					status_message.put("Message","Update UnSuccessful");
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
	
	public boolean checkParam(String input)
	{
		if(input == null || input.equals(""))
			return true;
		else
			return false;
		
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
}