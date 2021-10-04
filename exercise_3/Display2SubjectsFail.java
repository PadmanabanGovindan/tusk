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

@WebServlet("/Display2SubjectsFail")
public class Display2SubjectsFail extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Marks");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		PrintWriter out = response.getWriter();
		
		
		//----------------------------------------------------------------------------------------------------------------------
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("Marks"));
		Join  joinStudent = new Join("Marks", "Student_Details", new String [] {"STUDENT_ID"}, new String [] {"STUDENT_ID"}, Join.INNER_JOIN);
		query.addJoin(joinStudent);
		
		Join  joinClass = new Join("Student_Details", "Class", new String [] {"CLASS_ID"}, new String [] {"CLASS_ID"}, Join.INNER_JOIN);
		query.addJoin(joinClass);
	
		query.addSelectColumn(Column.getColumn("Student_Details","STUDENT_ID"));
		query.addSelectColumn(Column.getColumn("Student_Details","STUDENT_NAME"));
		query.addSelectColumn(Column.getColumn("Class","CLASS_NAME"));
		
		query.setCriteria(new Criteria(Column.getColumn("Marks","MARK"), 50, QueryConstants.LESS_THAN));

		Column c1 = new Column("Student_Details","STUDENT_ID").count();
	
		GroupByColumn gbc1 = new GroupByColumn(new Column("Student_Details","STUDENT_NAME"),true);
		GroupByColumn gbc2 = new GroupByColumn(new Column("Student_Details","STUDENT_ID"),true);
		GroupByColumn gbc3 = new GroupByColumn(new Column("Class","CLASS_NAME"),true);
		GroupByColumn gbc4 = new GroupByColumn(new Column("Class","CLASS_ID"),true);
		
	
		
		List list = new ArrayList();
		list.add(gbc1);
		list.add(gbc2);
		list.add(gbc3);
		list.add(gbc4);
		
		GroupByClause gbcl = new GroupByClause(list, new Criteria(c1,2,QueryConstants.EQUAL));
		query.setGroupByClause(gbcl);
		
		SortColumn sbc1 = new SortColumn(new Column("Class","CLASS_ID"),true);
		SortColumn sbc2 = new SortColumn(new Column("Student_Details","STUDENT_NAME"), true);
		query.addSortColumn(sbc1);
		query.addSortColumn(sbc2);
		
		
		
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
				record.put("Class", ds.getValue("CLASS_NAME"));
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