package exercise_3;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import com.adventnet.db.api.RelationalAPI;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.CaseExpression;
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
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/DisplayGrades")
public class DisplayGrades extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Marks");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		PrintWriter out = response.getWriter();
		
		String subject_name = request.getParameter("SUBJECT_NAME");
		
		
		//----------------------------------------------------------------------------------------------------------------------
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("Marks"));
		Join  joinStudent = new Join("Marks", "Student_Details", new String [] {"STUDENT_ID"}, new String [] {"STUDENT_ID"}, Join.INNER_JOIN);
		query.addJoin(joinStudent);
		
		Join  joinSubject = new Join("Marks", "Subject", new String [] {"SUBJECT_ID"}, new String [] {"SUBJECT_ID"}, Join.INNER_JOIN);
		query.addJoin(joinSubject);
		
		query.addSelectColumn(Column.getColumn("Marks","STUDENT_ID"));
		query.addSelectColumn(Column.getColumn("Student_Details","STUDENT_NAME"));
		query.addSelectColumn(Column.getColumn("Subject","SUBJECT_NAME"));
		query.addSelectColumn(Column.getColumn("Marks","MARK"));
		
		CaseExpression ce = new CaseExpression("GRADE"); 
		ce.addWhen(new Criteria(Column.getColumn("Marks", "MARK"), 90, QueryConstants.GREATER_EQUAL), "A"); 
		ce.addWhen(new Criteria(Column.getColumn("Marks", "MARK"), 80, QueryConstants.GREATER_EQUAL), "B"); 
		ce.addWhen(new Criteria(Column.getColumn("Marks", "MARK"), 70, QueryConstants.GREATER_EQUAL), "C"); 
		ce.addWhen(new Criteria(Column.getColumn("Marks", "MARK"), 60, QueryConstants.GREATER_EQUAL), "D"); 
		ce.addWhen(new Criteria(Column.getColumn("Marks", "MARK"), 51, QueryConstants.GREATER_EQUAL), "E"); 
		ce.addWhen(new Criteria(Column.getColumn("Marks", "MARK"), 50, QueryConstants.LESS_EQUAL), "F"); 
		query.addSelectColumn(ce);
		
		query.setCriteria(new Criteria(Column.getColumn("Subject","SUBJECT_NAME"), subject_name, QueryConstants.EQUAL));
		
		Column sbc1 = new Column("Student_Details","STUDENT_NAME");
		SortColumn sbc = new SortColumn(sbc1,true);
		
		query.addSortColumn(sbc);
		
		//----------------------------------------------------------------------------------------------------------------------
		
		JSONArray sq = new JSONArray();
		DataSet ds;
		
		try {
			Connection conn = RelationalAPI.getInstance().getConnection();
			ds = RelationalAPI.getInstance().executeQuery(query, conn);
			while (ds.next())
		    {
				JSONObject record = new JSONObject();
				record.put("Student_Id", ds.getValue("STUDENT_ID"));
				record.put("Student_Name", ds.getValue("STUDENT_NAME"));
				record.put("Subject", ds.getValue("SUBJECT_NAME"));
				record.put("Mark", ds.getValue("MARK"));
				record.put("Grade", ds.getValue("GRADE"));
				sq.put(record);
		    }
		} catch (SQLException | QueryConstructionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		out.println(sq);

    }
		
		
}