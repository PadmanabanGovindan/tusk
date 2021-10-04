package Lib_Source;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.adventnet.db.api.RelationalAPI;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
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
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

@WebServlet("/ReturnBorrow")
public class ReturnBorrow extends HttpServlet {

	private static final long serialVersionUID = 1L;
	

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		
		JSONObject status_message = new JSONObject();
		status_message.put("Status","Unsuccessful");
		
		PrintWriter out = response.getWriter();
		
		
		Cookie[] cookies = request.getCookies();
		
		if(checkAuthentication(cookies,true))
		{
			
			try {
				MetaDataUtil.getTableDefinitionByName("Borrow");
			} catch (MetaDataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String book_name = request.getParameter("BOOK_NAME");
			if(checkParam(book_name)) {
				status_message.put("Message","Please Enter the Book Name");
			}
			else
			{
				String user_login_id = request.getParameter("USER_LOGIN_ID");
				if(checkParam(user_login_id)) {
					status_message.put("Message","Please Enter the User Login Id");
				}
				else
				{
				
				//---------------------------------------------Book ID-------------------------------------------------------------------
				
				
				SelectQuery query = new SelectQueryImpl(Table.getTable("Book"));
				query.addSelectColumn(Column.getColumn("Book","BOOK_NAME"));
				query.addSelectColumn(Column.getColumn("Book","BOOK_ID"));
				query.addSelectColumn(Column.getColumn("Book","AVAILABLE"));
				
				query.setCriteria(new Criteria(Column.getColumn("Book","BOOK_NAME"), book_name, QueryConstants.EQUAL));
				
				DataSet ds;
				String book_id = null;
				int available = 0;
				
				try {
					Connection conn = RelationalAPI.getInstance().getConnection();
					ds = RelationalAPI.getInstance().executeQuery(query, conn);
					while (ds.next())
				    {
						book_id =  ds.getValue("BOOK_ID").toString();
						available = Integer.parseInt(ds.getValue("AVAILABLE").toString());
						
				    }
					conn.close();
				}catch (SQLException | QueryConstructionException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				//---------------------------------------------Book ID-------------------------------------------------------------------
				
				
				
				//---------------------------------------------User ID-------------------------------------------------------------------
						
						
						SelectQuery query1 = new SelectQueryImpl(Table.getTable("User"));
						query1.addSelectColumn(Column.getColumn("User","USER_NAME"));
						query1.addSelectColumn(Column.getColumn("User","USER_ID"));
						query1.addSelectColumn(Column.getColumn("User","USER_TYPE"));
						
						query1.setCriteria(new Criteria(Column.getColumn("User","USER_LOGIN_ID"), user_login_id, QueryConstants.EQUAL));
						
						DataSet ds1;
						String user_id = null;
						String user_type = null;
						
						try {
							Connection conn = RelationalAPI.getInstance().getConnection();
							ds1 = RelationalAPI.getInstance().executeQuery(query1, conn);
							while (ds1.next())
						    {
								user_id =  ds1.getValue("USER_ID").toString();
								user_type =  ds1.getValue("USER_TYPE").toString();
								
						    }
						}catch (SQLException | QueryConstructionException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								
							}
						//---------------------------------------------User ID-------------------------------------------------------------------
						
						
	                    //---------------------------------------------Borrow ID-------------------------------------------------------------------
						
						
						SelectQuery query2 = new SelectQueryImpl(Table.getTable("Borrow"));
						query2.addSelectColumn(Column.getColumn("Borrow","BORROW_ID"));
						query2.addSelectColumn(Column.getColumn("Borrow","BORROW_DATE"));
						
						Criteria user_criteria = new Criteria(Column.getColumn("Borrow","USER_ID"), user_id, QueryConstants.EQUAL);
						
						Criteria book_criteria = new Criteria(Column.getColumn("Borrow","BOOK_ID"), book_id, QueryConstants.EQUAL);
						
						Criteria status_criteria = new Criteria(Column.getColumn("Borrow","STATUS"), "Borrowed", QueryConstants.EQUAL);
						
						Criteria borrow_criteria = user_criteria.and(book_criteria).and(status_criteria);
						
						query2.setCriteria(borrow_criteria);
						
						DataSet ds2;
						String borrow_id = null;
						String borrow_date = null;
						
						try {
							Connection conn = RelationalAPI.getInstance().getConnection();
							ds2 = RelationalAPI.getInstance().executeQuery(query2, conn);
							while (ds2.next())
						    {
								borrow_id =  ds2.getValue("BORROW_ID").toString();
								borrow_date = ds2.getValue("BORROW_DATE").toString();
								
						    }
							conn.close();
						}catch (SQLException | QueryConstructionException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								
							}
						//---------------------------------------------User ID-------------------------------------------------------------------
				
						
						JSONObject details = new JSONObject();
						details.put("Book_ID: " ,book_id);
						details.put("User_ID: " ,user_id);
						details.put("User_Type: " ,user_type);
						details.put("Borrow_ID: " , borrow_id);
						out.println(details);
						
						String date = java.time.LocalDate.now().toString();
						
						//---------------------------------------------Validation-----------------------------------------------------------------
						
						if(user_id==null) {
							status_message.put("Message","User Not Found!!");
						}
						else if(book_id == null)
						{
							status_message.put("Message","Book Not Found!!");
						}
						else if(borrow_id == null)
						{
							status_message.put("Message","Borrow details not found for the given details!!");
						}
						else
						{
							String fine = calculateFine(borrow_date, user_type);
							
							UpdateQuery uq = new UpdateQueryImpl("Book");
							uq.setUpdateColumn("AVAILABLE", (available+1));
						
							Criteria c = new Criteria(Column.getColumn("Book", "BOOK_ID"), book_id, QueryConstants.EQUAL);
							uq.setCriteria(c);
							
							
							UpdateQuery uq1 = new UpdateQueryImpl("Borrow");
							uq1.setUpdateColumn("RETURN_DATE", date);
							uq1.setUpdateColumn("STATUS", "Returned");
							uq1.setUpdateColumn("FINE", fine);
				
							uq1.setCriteria(borrow_criteria);
							
							try {
								DataAccess.update(uq);
								DataAccess.update(uq1);
								status_message.put("Message","Book Returned!!");
								status_message.put("Status","Successful");
							} catch (DataAccessException e) {
								e.printStackTrace();
								status_message.put("Message","Book Return Unsuccessful!!");
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

	private String calculateFine(String borrow_date, String user_type) {
		
		long fine = 0;
		LocalDate borrowDate = LocalDate.parse(borrow_date);
		LocalDate today =LocalDate.now();
		long numDays = ChronoUnit.DAYS.between(borrowDate, today);
		
		System.out.println("Borrow Date:" + borrowDate + " today:" + today);
		System.out.println(numDays);
		
		if(user_type.equals("User") && numDays >30) {
			fine = (numDays-30)*5;
		}
		else if(user_type.equals("Member") && numDays >50)
		{
			fine = (numDays-50)*5;
		}
		
		System.out.println("Fine :" + fine);
		
		return (String.valueOf(fine));
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

	public boolean checkParam(String input)
	{
		if(input == null || input.equals(""))
			return true;
		else
			return false;
		
	}
}

