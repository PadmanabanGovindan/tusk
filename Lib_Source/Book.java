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

@WebServlet("/Book")
public class Book extends HttpServlet {

	private static final long serialVersionUID = 1L;
	

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		PrintWriter out = response.getWriter();
		JSONObject status_message = new JSONObject();
		status_message.put("Status", "UnSuccessful");
		
		Cookie[] cookies = request.getCookies();
		
		if(checkAuthentication(cookies,true))
		{
			try {
				MetaDataUtil.getTableDefinitionByName("Book");
			} catch (MetaDataException e1) {
				e1.printStackTrace();
			}
		
		
			String book_name = request.getParameter("BOOK_NAME");
			if(checkParam(book_name)) {
				status_message.put("Message","Please Enter Book Name");
			}
			else 
			{
				if(checkValue("BOOK_NAME", book_name, "Book"))
				{
					status_message.put("Message","Book Name Already Exists !!");
					
				}
				else
				{
				
				String isbn = request.getParameter("ISBN");
				if(checkParam(isbn)) {
					status_message.put("Message","Please Enter ISBN");
				}
				else
				{
					if(checkValue("ISBN", isbn,"Book"))
					{
						status_message.put("Message","Book with this ISBN already exists !!");
						
					}
					else
					{
					String quantity = request.getParameter("QUANTITY");
					if(checkParam(quantity)) {
						status_message.put("Message","Please Enter Quantity");
					}
					else
					{
						String contributor_login_id = request.getParameter("CONTRIBUTOR_LOGIN_ID");
						
								Row r = new Row("Book");
								r.set("BOOK_NAME",book_name);
								r.set("ISBN",isbn);
								r.set("QUANTITY", quantity);
								r.set("AVAILABLE", quantity);
								
								boolean contributor_flag = true;
								boolean member_flag = true;
								int contributor_id;
								
								if(checkParam(contributor_login_id)== false) {
									if(checkValue("USER_LOGIN_ID", contributor_login_id, "User")==false)
									{
										contributor_flag = false;
									}
									else {
										contributor_id = checkMember(contributor_login_id);
										if(contributor_id == 0)
										{
											member_flag = false;
										}
										else
										{
											r.set("CONTRIBUTOR_ID", contributor_id);
										}
									}
								}
								
								if(contributor_flag == false)
								{
									status_message.put("Message","Contributor is not found !!");
								}
								else if(member_flag == false)
								{
									status_message.put("Message","Contributor is not a Member !!");
								}
									
									else
									{
									
								DataObject dob=new WritableDataObject();
								
						
								try {
									dob.addRow(r);
									DataAccess.add(dob);
									status_message.put("Status", "Successful");
									status_message.put("Message","Book Added Successfully");
								} catch (DataAccessException e) {
									e.printStackTrace();
									status_message.put("Message","Book Add UnSuccessfull");
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
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

		
		PrintWriter out = response.getWriter();
		JSONObject status_message = new JSONObject();
		JSONArray sq = new JSONArray();
		boolean error_flag = true;
		status_message.put("Status","Unsuccessful");
		
		Cookie[] cookies = request.getCookies();
		
		if(checkAuthentication(cookies,false))
		{
			
			try {
				MetaDataUtil.getTableDefinitionByName("Book");
			} catch (MetaDataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		
			//----------------------------------------------------------------------------------------------------------------------
			
			SelectQuery query = new SelectQueryImpl(Table.getTable("Book"));
			query.addSelectColumn(Column.getColumn(null,"*"));
			
			Column sbc1 = new Column("Book","BOOK_NAME");
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
					record.put("Book_Id", ds.getValue("BOOK_ID"));
					record.put("BOOK_Name", ds.getValue("BOOK_NAME"));
					record.put("ISBN", ds.getValue("ISBN"));
					record.put("Quantity", ds.getValue("QUANTITY"));
					record.put("Available", ds.getValue("AVAILABLE"));
					sq.put(record);
			    }
				status_message.put("Status","Successful");
				status_message.put("Message","Displaying Book Details");
				error_flag = false;
				conn.close();
			} catch (SQLException | QueryConstructionException e1) {
				// TODO Auto-generated catch block
				status_message.put("Message","Error Retrieving Book Details");
				e1.printStackTrace();
			}
			
			
			}
		else
		{
			status_message.put("Message","Login to Continue");

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
		
		PrintWriter out = response.getWriter();
		JSONObject status_message = new JSONObject();
		status_message.put("Status","Unsuccessful");

		Cookie[] cookies = request.getCookies();
		
		if(checkAuthentication(cookies,true))
		{
			try {
				MetaDataUtil.getTableDefinitionByName("Book");
			} catch (MetaDataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
			String book_name = request.getParameter("BOOK_NAME");
			
	
			if(checkParam(book_name)) {
				status_message.put("Message","Please Enter Book Name");
			}
			else
			{
			
			if(checkValue("BOOK_NAME", book_name, "Book")==false)
			{
				status_message.put("Message","Book Not Found");
			}
			else
			{
			
			
			//------------------------------------------------------------------------------
			
			DeleteQuery dq = new DeleteQueryImpl("Book");
			
			Criteria c = new Criteria(Column.getColumn("Book", "BOOK_NAME"), book_name, QueryConstants.EQUAL);
		
			dq.setCriteria(c);
			
			
			//------------------------------------------------------------------------------
			
			
			
	
			try {
				DataAccess.delete(dq);
				status_message.put("Status","Successful");
				status_message.put("Message","Book Deleted Successfully");
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
		
		public boolean checkValue(String column, String value, String table) {
			
			SelectQuery query = new SelectQueryImpl(Table.getTable(table));
			
			Column c = new Column(null,"*").count();
			c.setColumnAlias("COUNT");
			query.addSelectColumn(c);
			
			Criteria c1 = new Criteria(Column.getColumn(table,column), value, QueryConstants.EQUAL);
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

		public int checkMember(String user_login_id) {
			
			SelectQuery query = new SelectQueryImpl(Table.getTable("User"));
			
			Column c = new Column("User","USER_ID");
			query.addSelectColumn(c);
			
			Criteria c1 = new Criteria(Column.getColumn("User","USER_LOGIN_ID"), user_login_id, QueryConstants.EQUAL);
			Criteria c2 = new Criteria(Column.getColumn("User","USER_TYPE"), "Member", QueryConstants.EQUAL);
			query.setCriteria(c1.and(c2));
			

			DataSet ds;
			int user_id = 0;
			try {
				Connection conn = RelationalAPI.getInstance().getConnection();
				ds = RelationalAPI.getInstance().executeQuery(query, conn);
				while (ds.next())
			    {
					user_id =  Integer.parseInt(ds.getValue("USER_ID").toString());
					
			    }
				conn.close();
			}catch (SQLException | QueryConstructionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			
			return user_id;
			
		}
}