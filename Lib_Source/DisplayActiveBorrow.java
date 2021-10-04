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
import com.adventnet.ds.query.Join;
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

@WebServlet("/DisplayActiveBorrow")
public class DisplayActiveBorrow extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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
			
			String borrow_status = request.getParameter("STATUS");
			if(borrow_status == null || borrow_status.equals(""))
			{
				status_message.put("Message","Please Enter Borrow Status ");
			}
			else {
				if(!borrow_status.equals("Borrowed") && !borrow_status.equals("Returned"))
				{
					status_message.put("Message","Entered status does not match  Borrowed or Returned  ");
				}
				else
				{
					
			
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
			
			Criteria user_criteria = new Criteria(Column.getColumn("Borrow","STATUS"), borrow_status, QueryConstants.EQUAL);
			
			query.setCriteria(user_criteria);
			
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
				conn.close();
				status_message.put("Status","Successful");
				status_message.put("Message","Displaying Borrow Details");
				error_flag = false;
			} catch (SQLException | QueryConstructionException e1) {
				status_message.put("Message","Error Retrieving Borrow Details");
				e1.printStackTrace();
			}
		
			}
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
}
