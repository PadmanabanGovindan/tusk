package exercise_3;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
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
import com.adventnet.ds.query.Range;
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

@WebServlet("/DisplayClassToppers")
public class DisplayClassToppers extends HttpServlet {

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
		
		Join  joinClassSubject = new Join("Marks", "Class_Vs_Subject", new String [] {"SUBJECT_ID"}, new String [] {"SUBJECT_ID"}, Join.INNER_JOIN);
		query.addJoin(joinClassSubject);
		
		Join  joinStudent = new Join("Marks", "Student_Details", new String [] {"STUDENT_ID"}, new String [] {"STUDENT_ID"}, Join.INNER_JOIN);
		query.addJoin(joinStudent);
		
		Join  joinClass = new Join("Student_Details", "Class", new String [] {"CLASS_ID"}, new String [] {"CLASS_ID"}, Join.INNER_JOIN);
		query.addJoin(joinClass);
		
		
		
		query.addSelectColumn(Column.getColumn("Student_Details","STUDENT_NAME"));
		query.addSelectColumn(Column.getColumn("Class","CLASS_NAME"));
		
		Column c1 = new Column("Marks","MARK").summation();
		c1.setColumnAlias("TOTAL_MARKS");
		query.addSelectColumn(c1);
		
		
		Column winColumn = Column.createFunction("RANK_OVER", Column.getColumn("Class_Vs_Subject", "CLASS_ID"), Column.getColumn("Marks", "MARK"));
		winColumn.setType(Types.INTEGER);
		winColumn.setColumnAlias("CLASS_RANK");
		query.addSelectColumn(winColumn);
		
		GroupByColumn gbc1 = new GroupByColumn(new Column("Student_Details","CLASS_ID"),true);
		GroupByColumn gbc2 = new GroupByColumn(new Column("Student_Details","STUDENT_NAME"),true);
		GroupByColumn gbc3 = new GroupByColumn(new Column("Class","CLASS_NAME"),true);
		GroupByColumn gbc4 = new GroupByColumn(new Column("Class_Vs_Subject","CLASS_ID"),true);
		
		
		
		
		List list = new ArrayList();
		list.add(gbc1);
		list.add(gbc2);
		list.add(gbc3);
		list.add(gbc4);

		
		GroupByClause gbcl = new GroupByClause(list);
		query.setGroupByClause(gbcl);
		
		SortColumn sbc1 = new SortColumn(winColumn,true);
		SortColumn sbc2 = new SortColumn(new Column("Class_Vs_Subject","CLASS_ID"), true);
		query.addSortColumn(sbc1);
		query.addSortColumn(sbc2);
		
		query.setRange(new Range(0,4));
		
		
		
		//----------------------------------------------------------------------------------------------------------------------
		JSONArray sq = new JSONArray();
		DataSet ds;
		
		try {
			Connection conn = RelationalAPI.getInstance().getConnection();
			ds = RelationalAPI.getInstance().executeQuery(query, conn);
			while (ds.next())
		    {
				JSONObject record = new JSONObject();
				record.put("Student_Name", ds.getValue("STUDENT_NAME"));
				record.put("Class", ds.getValue("CLASS_NAME"));
				record.put("Total_Marks", ds.getValue("TOTAL_MARKS"));
				record.put("Rank", ds.getValue("CLASS_RANK"));
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
