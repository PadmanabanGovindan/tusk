package Lib_Source;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.GroupByColumn;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/Borrow")
public class Borrow extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		PrintWriter out = response.getWriter();
		JSONObject status_message = new JSONObject();
		status_message.put("Status","Unsuccessful");
		
		
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
					status_message.put("Message","Please Enter the User Id");
				}
				else
				{
				
				//---------------------------------------------Book ID-------------------------------------------------------------------
				
				
				SelectQuery query = new SelectQueryImpl(Table.getTable("Book"));
				query.addSelectColumn(Column.getColumn("Book","BOOK_NAME"));
				query.addSelectColumn(Column.getColumn("Book","BOOK_ID"));
				
				query.setCriteria(new Criteria(Column.getColumn("Book","BOOK_NAME"), book_name, QueryConstants.EQUAL));
				
				DataSet ds;
				String book_id = null;
				Connection conn = null;
				
				try {
					conn = RelationalAPI.getInstance().getConnection();
					ds = RelationalAPI.getInstance().executeQuery(query, conn);
					while (ds.next())
				    {
						book_id =  ds.getValue("BOOK_ID").toString();
						
				    }
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
				
						JSONObject details = new JSONObject();
						details.put("Book_ID: " ,book_id);
						details.put("User_ID: " ,user_id);
						details.put("User_Type: " ,user_type);
						
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
						else {
							
							int available = book_availability(book_id);
							boolean available_flag = false;
							
							
							//////////Work on this
							if(available>0) {
								available_flag =  false;
							}
							else {
								available_flag = true;
							}
							
							if(available_flag)
							{
								status_message.put("Message","The Book is Currently Unavailable");
							}
							else
							{
								if(checkCount(user_id, user_type)) {
									status_message.put("Message","Borrow Limit Exceeded");
									}
									else
									{
										if(checkDoubleEntry(user_id, book_id)) {
											status_message.put("Message","Book already borrowed, Return the book to borrow again !!");
										}
										else
										{
											if(addBorrow(user_id,book_id,date,available))
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
		
		if(checkAuthentication(cookies,true))
		{
			
			try {
				MetaDataUtil.getTableDefinitionByName("Borrow");
			} catch (MetaDataException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		
		//----------------------------------------------------------------------------------------------------------------------
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("Borrow"));
		query.addSelectColumn(Column.getColumn("Borrow","BORROW_ID"));
		query.addSelectColumn(Column.getColumn("Borrow","USER_ID"));
		query.addSelectColumn(Column.getColumn("Borrow","BOOK_ID"));
		query.addSelectColumn(Column.getColumn("Borrow","BORROW_DATE"));
		query.addSelectColumn(Column.getColumn("Borrow","RETURN_DATE"));
		query.addSelectColumn(Column.getColumn("Borrow","STATUS"));
		query.addSelectColumn(Column.getColumn("Borrow","FINE"));
		
		Join  joinUser = new Join("Borrow", "User", new String[] {"USER_ID"}, new String[] {"USER_ID"},Join.INNER_JOIN);
		query.addJoin(joinUser);
		
		Join  joinBook = new Join("Borrow", "Book", new String[] {"BOOK_ID"}, new String[] {"BOOK_ID"},Join.INNER_JOIN);
		query.addJoin(joinBook);
		
		query.addSelectColumn(Column.getColumn("User","USER_NAME"));
		query.addSelectColumn(Column.getColumn("User","USER_TYPE"));
		query.addSelectColumn(Column.getColumn("Book","BOOK_NAME"));
		
		Column sbc1 = new Column("Borrow","BORROW_DATE");
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
				record.put("Borrow_Id", ds.getValue("BORROW_ID"));
				record.put("User_Name", ds.getValue("USER_NAME"));
				record.put("Book_Name", ds.getValue("BOOK_NAME"));
				record.put("Return_Date", ds.getValue("RETURN_DATE"));
				
				String borrow_date =  ds.getValue("BORROW_DATE").toString();
				String user_type =  ds.getValue("USER_TYPE").toString();
				String status =  ds.getValue("STATUS").toString();
				String fine =  ds.getValue("FINE").toString();
				
				record.put("Borrow_Date", borrow_date);
				
				record.put("Status", status);
				if(status.equals("Returned")) {
					record.put("Fine", fine);
				}
				else {
					fine = calculateFine(borrow_date,user_type);
					record.put("Fine", fine);
				}
				sq.put(record);
		    }
			status_message.put("Status","Successful");
			error_flag=false;
			conn.close();
		} catch (SQLException | QueryConstructionException e1) {
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
	
	public boolean checkParam(String input)
	{
		if(input == null || input.equals(""))
			return true;
		else
			return false;
		
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

	public int book_availability(String book_id) {
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("Book"));
		query.addSelectColumn(Column.getColumn("Book","AVAILABLE"));
		
		Criteria c1 = new Criteria(Column.getColumn("Book","BOOK_ID"), book_id, QueryConstants.EQUAL);
		query.setCriteria(c1);
		
		DataSet ds;
		int available = 0;
		
		try {
			Connection conn = RelationalAPI.getInstance().getConnection();
			ds = RelationalAPI.getInstance().executeQuery(query, conn);
			while (ds.next())
		    {
				available =  Integer.parseInt(ds.getValue("AVAILABLE").toString());
				
		    }
			conn.close();
		}catch (SQLException | QueryConstructionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try {
			System.out.println(RelationalAPI.getInstance().getSelectSQL(query));
		} catch (QueryConstructionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return available;
		
		
	}

	public boolean checkDoubleEntry(String user_id, String book_id) {
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("Borrow"));
		
		Column c = new Column(null,"*").count();
		c.setColumnAlias("BORROW_COUNT");
		query.addSelectColumn(c);
		
		Criteria c1 = new Criteria(Column.getColumn("Borrow","USER_ID"), user_id, QueryConstants.EQUAL);
		Criteria c2 = new Criteria(Column.getColumn("Borrow","BOOK_ID"), book_id, QueryConstants.EQUAL);
		Criteria c3 = new Criteria(Column.getColumn("Borrow","STATUS"), "Borrowed", QueryConstants.EQUAL);
		Criteria c4 = c1.and(c2).and(c3);
		query.setCriteria(c4);
		
//		GroupByColumn gbc1 = new GroupByColumn(new Column("Borrow","USER_ID"),true);
//		
//		
//		List list = new ArrayList();
//		list.add(gbc1);
//		
//		GroupByClause gbc = new GroupByClause(list);
//		query.setGroupByClause(gbc);
		
		DataSet ds;
		int count = 0;
		try {
			Connection conn = RelationalAPI.getInstance().getConnection();
			ds = RelationalAPI.getInstance().executeQuery(query, conn);
			while (ds.next())
		    {
				count =  Integer.parseInt(ds.getValue("BORROW_COUNT").toString());
				
		    }
			conn.close();
		}catch (SQLException | QueryConstructionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try {
			System.out.println(RelationalAPI.getInstance().getSelectSQL(query));
		} catch (QueryConstructionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(count==0) {
			return false;
		}
		else
		{
			return true;
		}
		
	}

	public boolean addBorrow(String user_id, String book_id, String date, int available) {
		
		Row r = new Row("Borrow");
		r.set("USER_ID",user_id);
		r.set("BOOK_ID",book_id);
		r.set("BORROW_DATE", date);
		DataObject dob=new WritableDataObject();
		
		
		UpdateQuery uq = new UpdateQueryImpl("Book");
		uq.setUpdateColumn("AVAILABLE", (available-1));
	
		Criteria c = new Criteria(Column.getColumn("Book", "BOOK_ID"), book_id, QueryConstants.EQUAL);
		uq.setCriteria(c);
		

		try {
			dob.addRow(r);
			DataAccess.update(uq);
			DataAccess.add(dob);
			return true;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return false;
		}
		
		
	}

	public boolean checkCount(String user_id, String user_type) {
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("Borrow"));
		query.addSelectColumn(Column.getColumn("Borrow","USER_ID"));
		
		Column c = new Column("Borrow","BORROW_ID").count();
		c.setColumnAlias("BORROW_COUNT");
		query.addSelectColumn(c);
		
		Criteria c1 = new Criteria(Column.getColumn("Borrow","USER_ID"), user_id, QueryConstants.EQUAL);
		Criteria c2 = new Criteria(Column.getColumn("Borrow","STATUS"), "Borrowed", QueryConstants.EQUAL);
		Criteria c3 = c1.and(c2);
		query.setCriteria(c3);
		
		GroupByColumn gbc1 = new GroupByColumn(new Column("Borrow","USER_ID"),true);
		
		
		List list = new ArrayList();
		list.add(gbc1);
		
		GroupByClause gbc = new GroupByClause(list);
		query.setGroupByClause(gbc);
		
		DataSet ds;
		int count = 0;
		
		try {
			Connection conn = RelationalAPI.getInstance().getConnection();
			ds = RelationalAPI.getInstance().executeQuery(query, conn);
			while (ds.next())
		    {
				count =  Integer.parseInt(ds.getValue("BORROW_COUNT").toString());
				
		    }
			conn.close();
		}catch (SQLException | QueryConstructionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		try {
			System.out.println(RelationalAPI.getInstance().getSelectSQL(query));
		} catch (QueryConstructionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(user_type.equals("User") & count<1) {
			return false;
		}
		else if(user_type.equals("Member") & count<5) {
			return false;
		}
		else {
			return true;
		}
		
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