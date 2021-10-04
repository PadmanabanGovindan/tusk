import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;

import org.json.JSONArray;
import org.json.JSONObject;

import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
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
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;

public class Util {
	
	public boolean checkAuthentication(Cookie[] cookies, boolean admin_flag) {
		
		String loginid = null;
		String token = null;
		
		
		if(cookies !=null){
		for(Cookie cookie : cookies){
			if(cookie.getName().equals("user")) loginid = cookie.getValue();
			if(cookie.getName().equals("token")) token = cookie.getValue();
			}
		}
		
		
		//Select Query for validating the User and Token----------------------------------------------------------
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("Authtoken"));
		
		Column c = new Column(null,"*").count();
		c.setColumnAlias("COUNT");
		query.addSelectColumn(c);
		
		Criteria c1 = new Criteria(Column.getColumn("Authtoken","USER_LOGIN_ID"), loginid, QueryConstants.EQUAL);
		Criteria c2 = new Criteria(Column.getColumn("Authtoken","TOKEN"), token, QueryConstants.EQUAL);
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

	public JSONObject checkParamValidation(String value, String column, String table, boolean update, boolean unique) {
		
		JSONObject status_message = new JSONObject();
		status_message.put("Status", "Unsuccessful");
		
		if(checkParam(value)) {
			if(update == false) {
				
				status_message.put("Message",  "Please Enter "+ column);
				//status_message.put("Status", "Unsuccessful");
				return status_message;
			}
		}
		else
		{
			if(unique == true)
			{
				if(checkValue(column, value, table))
				{
					status_message.put("Message",  column + " already exists choose another "+ column);
					//status_message.put("Status", "Unsuccessful");
					return status_message;
				}
			}
			
			if(table.equals("User"))
			{
				if(column.equals("USER_LOGIN_ID") && update == false && unique == false)
				{
					if(checkValue("USER_LOGIN_ID", value, table)==false)
					{
						status_message.put("Message", "User Not Found");
						return status_message;
					}
				}
				
				if(column.equals("USER_TYPE"))
				{
					if(!value.equals("User") && !value.equals("Member"))
					{
						status_message.put("Message",  "Please Enter User Type as User or Member");
						//status_message.put("Status", "Unsuccessful");
						return status_message;
					}
				}
				
				if(column.equals("MOBILE_NUMBER"))
				{
					if(value.length() < 10)
					{
						status_message.put("Message",  "Please Enter Valid Mobile Number");
						
						return status_message;
					}
				}
			}
			
			if(column.equals("CONTRIBUTOR_ID"))
			{
				if(checkValue("USER_LOGIN_ID", value, "User")==false)
				{
					status_message.put("Message","Contributor is not found !!");
					return status_message;
				}
				else 
				{
					int contributor_id = checkMember(value);
					if(contributor_id == 0)
					{
						status_message.put("Message","Contributor is not a Member !!");
						return status_message;
					}
				}
			}
			
			if(column.equals("BOOK_NAME") && update == false && unique == false)
			{
				if(checkValue("BOOK_NAME", value, table)==false)
				{
					status_message.put("Message", "Book Not Found");
					return status_message;
				}
			}
			
		}
		status_message.put("Status", "Successful");
		return status_message;
	}
	
	public boolean checkParam(String input)
	{
		if(input == null || input.equals(""))
			return true;
		else
			return false;
		
	}

	public boolean checkValue(String column, String value, String table) {
		
		SelectQuery query = new SelectQueryImpl(Table.getTable(table));
		
		Column c = new Column(null,"*").count();
		c.setColumnAlias("COUNT");
		query.addSelectColumn(c);
		
		Criteria c1 = new Criteria(Column.getColumn(table, column), value, QueryConstants.EQUAL);
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

		
		if(count==0) {
			return false;
		}
		else
		{
			return true;
		}
		
	}

	public boolean insertUser(String login_id, String name, String mobile, String user_type, String password)
	{
		String doj = java.time.LocalDate.now().toString();
		
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
			return true;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return false;
		}
		
	}

	public boolean updateUser(String old_login_id, String login_id, String name, String mobile, String user_type, String password)
	{
		
		UpdateQuery uq = new UpdateQueryImpl("User");
		
		if(login_id != null)
			uq.setUpdateColumn("USER_LOGIN_ID",login_id);
		
		if(name != null)
			uq.setUpdateColumn("USER_NAME",name);
		
		if(mobile != null)
			uq.setUpdateColumn("MOBILE_NUMBER", mobile);
		
		if(user_type != null)
			uq.setUpdateColumn("USER_TYPE", user_type);
		
		if(password != null)
			uq.setUpdateColumn("PASSWORD", password);
		
//		System.out.println(old_login_id);
//		System.out.println(login_id);
//		System.out.println(name);
//		System.out.println(mobile);
//		System.out.println(user_type);
//		System.out.println(password);
		
		
		Criteria c = new Criteria(Column.getColumn("User", "USER_LOGIN_ID"), old_login_id, QueryConstants.EQUAL);
		uq.setCriteria(c);
		

		try {
			DataAccess.update(uq);
			return true;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public boolean deleteUser(String value, String column, String table){
		
		DeleteQuery dq = new DeleteQueryImpl(table);
		Criteria c = new Criteria(Column.getColumn(table, column), value, QueryConstants.EQUAL);
		dq.setCriteria(c);
		
		try {
			DataAccess.delete(dq);
			return true;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return false;
		}
	
	}
	
	public JSONArray getUser(String value, String column)
	{
		SelectQuery query = new SelectQueryImpl(Table.getTable("User"));
		query.addSelectColumn(Column.getColumn(null,"*"));
		
		if(value != null && column !=null)
		{
			Criteria c = new Criteria(Column.getColumn("User", column), value, QueryConstants.EQUAL);
			query.setCriteria(c);
		}
		else
		{
			Column sbc1 = new Column("User","USER_NAME");
			SortColumn sbc = new SortColumn(sbc1,true);
			
			query.addSortColumn(sbc);
		}
		
		DataSet ds;
		JSONArray sq = new JSONArray();
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
			} catch (SQLException | QueryConstructionException e1){
				sq = null;
				e1.printStackTrace();
		}
		return sq;
	}

	public boolean insertBook(String book_name, String isbn, String quantity, String contributor_login_id) {
		
		Row r = new Row("Book");
		r.set("BOOK_NAME",book_name);
		r.set("ISBN",isbn);
		r.set("QUANTITY", quantity);
		r.set("AVAILABLE", quantity);
		
		if(contributor_login_id != null)
		{
			int contributor_id = checkMember(contributor_login_id);
			r.set("CONTRIBUTOR_ID", contributor_id);
		}
		
		DataObject dob=new WritableDataObject();
		
		try {
			dob.addRow(r);
			DataAccess.add(dob);
			return true;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public JSONArray getBook(String value, String column)
	{
		SelectQuery query = new SelectQueryImpl(Table.getTable("Book"));
		query.addSelectColumn(Column.getColumn(null,"*"));
		
		
		if(value != null && column !=null)
		{
			Criteria c = new Criteria(Column.getColumn("Book", column), value, QueryConstants.EQUAL);
			query.setCriteria(c);
		}
		else
		{
			Column sbc1 = new Column("Book","BOOK_NAME");
			SortColumn sbc = new SortColumn(sbc1,true);
			
			query.addSortColumn(sbc);
		}
		
		DataSet ds;
		JSONArray sq = new JSONArray();
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
			conn.close();
			} catch (SQLException | QueryConstructionException e1){
				sq = null;
				e1.printStackTrace();
		}
		return sq;
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
				e1.printStackTrace();
			}

		
		return user_id;
		
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
					e1.printStackTrace();
				}
			try {
				System.out.println(RelationalAPI.getInstance().getSelectSQL(query));
			} catch (QueryConstructionException e) {
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
	
	public boolean checkBorrowLimit(String user_id, String user_type) {
			
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
	
	public boolean addBorrow(String user_id, String book_id, int available) {
			
			String date = java.time.LocalDate.now().toString();
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

	public JSONArray getBorrow(String value, String column) {
		
		JSONArray sq = new JSONArray();
		
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
		
		if(value != null && column !=null)
		{
			Criteria c = new Criteria(Column.getColumn("Borrow", column), value, QueryConstants.EQUAL);
			query.setCriteria(c);
		}
		else
		{
			Column sbc1 = new Column("Borrow","BORROW_DATE");
			SortColumn sbc = new SortColumn(sbc1,true);
			
			query.addSortColumn(sbc);
		}
		
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
		} catch (SQLException | QueryConstructionException e1) {
			e1.printStackTrace();
		}
		
		return sq;
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

	public JSONObject getBorrowDetails(String user_id, String book_id) {
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("Borrow"));
		query.addSelectColumn(Column.getColumn("Borrow","BORROW_ID"));
		query.addSelectColumn(Column.getColumn("Borrow","BORROW_DATE"));
		
		Criteria user_criteria = new Criteria(Column.getColumn("Borrow","USER_ID"), user_id, QueryConstants.EQUAL);
		
		Criteria book_criteria = new Criteria(Column.getColumn("Borrow","BOOK_ID"), book_id, QueryConstants.EQUAL);
		
		Criteria status_criteria = new Criteria(Column.getColumn("Borrow","STATUS"), "Borrowed", QueryConstants.EQUAL);
		
		Criteria borrow_criteria = user_criteria.and(book_criteria).and(status_criteria);
		
		query.setCriteria(borrow_criteria);
		
		DataSet ds;
		String borrow_id = "null";
		String borrow_date = "null";
	
		
		JSONObject record = new JSONObject();
		try {
			Connection conn = RelationalAPI.getInstance().getConnection();
			ds = RelationalAPI.getInstance().executeQuery(query, conn);
			if(ds.next())
		    {
				borrow_id = ds.getValue("BORROW_ID").toString();
				borrow_date = ds.getValue("BORROW_DATE").toString();
		    }
			conn.close();
		}catch (SQLException | QueryConstructionException e1) {
				e1.printStackTrace();
				
			}
		record.put("Borrow_Id", borrow_id);
		record.put("Borrow_Date", borrow_date);
		
		return record;
		
	}

	public JSONObject returnBorrow(String login_id, String book_name) {
		
		JSONObject status_message = new JSONObject();
		status_message.put("Status","Unsuccessful");
		
		JSONObject user_details=(getUser(login_id, "USER_LOGIN_ID")).getJSONObject(0);
		String user_id = user_details.get("User_Id").toString();
		String user_type = user_details.get("User_Type").toString();
		
		JSONObject book_details=(getBook(book_name, "BOOK_NAME")).getJSONObject(0);
		String book_id = book_details.get("Book_Id").toString();
		int available = Integer.parseInt(book_details.get("Available").toString());
		
		JSONObject borrow_details=(getBorrowDetails(user_id, book_id));
		String borrow_id = borrow_details.get("Borrow_Id").toString();
		String borrow_date = borrow_details.get("Borrow_Date").toString();
		
		if(borrow_id.equals("null"))
		{
			status_message.put("Message", "Borrow Details not found for the given credentials");
			return status_message;
		}
		
		String fine = calculateFine(borrow_date, user_type);
		String date = java.time.LocalDate.now().toString();
		
		//Updating Book
		UpdateQuery uq = new UpdateQueryImpl("Book");
		uq.setUpdateColumn("AVAILABLE", (available+1));
	
		Criteria c = new Criteria(Column.getColumn("Book", "BOOK_ID"), book_id, QueryConstants.EQUAL);
		uq.setCriteria(c);
		
		//Updating Borrow
		UpdateQuery uq1 = new UpdateQueryImpl("Borrow");
		uq1.setUpdateColumn("RETURN_DATE", date);
		uq1.setUpdateColumn("STATUS", "Returned");
		uq1.setUpdateColumn("FINE", fine);

		Criteria borrow_criteria = new Criteria(Column.getColumn("Borrow","BORROW_ID"), borrow_id, QueryConstants.EQUAL);
	
		uq1.setCriteria(borrow_criteria);
		
		try {
			DataAccess.update(uq);
			DataAccess.update(uq1);
			status_message.put("Status","Successful");
			status_message.put("Message","Borrow Return Success");
		} catch (DataAccessException e) {
			e.printStackTrace();
			status_message.put("Message","Borrow Return Failed");
		}
		
		return status_message;
	}

	public void addAuthToken(String userid, String token) {
		
		Row r = new Row("Authtoken");
		r.set("USER_LOGIN_ID",userid);
		r.set("TOKEN",token);
		
		DataObject dob=new WritableDataObject();
		
		try {
			dob.addRow(r);
			DataAccess.add(dob);
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public String encryptPassword(String input)
    {
        try {
  
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) 
            {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } 
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
