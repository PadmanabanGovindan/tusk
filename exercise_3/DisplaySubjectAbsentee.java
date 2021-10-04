package exercise_3;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.PersistenceUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/DisplaySubjectAbsentee")
public class DisplaySubjectAbsentee extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Student_Details");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		PrintWriter out = response.getWriter();
		
		String subject_name = request.getParameter("SUBJECT_NAME");
		
		
		//----------------------------------------------------------------------------------------------------------------------
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("Student_Details"));
		
		Join  joinClassSubject = new Join("Student_Details", "Class_Vs_Subject", new String [] {"CLASS_ID"}, new String [] {"CLASS_ID"}, Join.INNER_JOIN);
		query.addJoin(joinClassSubject);
		
		Join  joinSubject = new Join("Class_Vs_Subject","Subject", new String [] {"SUBJECT_ID"}, new String [] {"SUBJECT_ID"}, Join.INNER_JOIN);
		query.addJoin(joinSubject);
		
		Criteria c1 = new Criteria(Column.getColumn("Student_Details", "STUDENT_ID"), Column.getColumn("Marks", "STUDENT_ID") , QueryConstants.EQUAL);
		Criteria c2 = new Criteria(Column.getColumn("Class_Vs_Subject", "SUBJECT_ID"), Column.getColumn("Marks", "SUBJECT_ID") , QueryConstants.EQUAL);
		
		Criteria c = c1.and(c2);
		
		Join  joinMarks = new Join("Student_Details","Marks", c, Join.LEFT_JOIN);
		query.addJoin(joinMarks);
	
		query.addSelectColumn(Column.getColumn("Student_Details","STUDENT_ID"));
		query.addSelectColumn(Column.getColumn("Student_Details","STUDENT_NAME"));
		query.addSelectColumn(Column.getColumn("Subject","SUBJECT_NAME"));
		
		query.setCriteria(new Criteria(Column.getColumn("Subject","SUBJECT_NAME"), subject_name, QueryConstants.EQUAL));

		Column m1 = new Column("Marks","MARK");
	
		GroupByColumn gbc1 = new GroupByColumn(new Column("Student_Details","STUDENT_ID"),true);
		GroupByColumn gbc2 = new GroupByColumn(new Column("Student_Details","STUDENT_NAME"),true);
		GroupByColumn gbc3 = new GroupByColumn(new Column("Subject","SUBJECT_NAME"),true);
		GroupByColumn gbc4 = new GroupByColumn(new Column("Marks","MARK"),true);
		
	
		
		List list = new ArrayList();
		list.add(gbc1);
		list.add(gbc2);
		list.add(gbc3);
		list.add(gbc4);
		
		GroupByClause gbcl = new GroupByClause(list, new Criteria(m1,null,QueryConstants.LIKE));
		query.setGroupByClause(gbcl);
		
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
				record.put("Student_ID", ds.getValue("STUDENT_ID"));
				record.put("Student_Name", ds.getValue("STUDENT_NAME"));
				record.put("SUBJECT_Name", ds.getValue("SUBJECT_NAME"));
				sq.put(record);
		    }
		} catch (SQLException | QueryConstructionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		out.println(sq);
		
		
		try {
			out.println(RelationalAPI.getInstance().getSelectSQL(query));
		} catch (QueryConstructionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }

	
}