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

@WebServlet("/DisplaySubjectAverage")
public class DisplaySubjectAverage extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Marks");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String class_name = request.getParameter("CLASS_NAME");
		PrintWriter out = response.getWriter();
		
		
		//----------------------------------------------------------------------------------------------------------------------
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("Marks"));
		Join  joinClassVSSubject = new Join("Marks", "Class_Vs_Subject", new String [] {"SUBJECT_ID"}, new String [] {"SUBJECT_ID"}, Join.INNER_JOIN);
		query.addJoin(joinClassVSSubject);
		
		Join  joinClass = new Join("Class_Vs_Subject", "Class", new String [] {"CLASS_ID"}, new String [] {"CLASS_ID"}, Join.INNER_JOIN);
		query.addJoin(joinClass);
		
		Join  joinSubject = new Join("Marks", "Subject", new String [] {"SUBJECT_ID"}, new String [] {"SUBJECT_ID"}, Join.INNER_JOIN);
		query.addJoin(joinSubject);
	
		query.addSelectColumn(Column.getColumn("Marks","SUBJECT_ID"));
		query.addSelectColumn(Column.getColumn("Subject","SUBJECT_NAME"));
		
		query.setCriteria(new Criteria(Column.getColumn("Class","CLASS_NAME"), class_name, QueryConstants.EQUAL));

		Column c1 = new Column("Marks","MARK").average();
		c1.setColumnAlias("SUBJECT_AVERAGE");
		query.addSelectColumn(c1);
	
		GroupByColumn gbc1 = new GroupByColumn(new Column("Marks","SUBJECT_ID"),true);
		GroupByColumn gbc2 = new GroupByColumn(new Column("Subject","SUBJECT_NAME"),true);
		
	
		
		List list = new ArrayList();
		list.add(gbc1);
		list.add(gbc2);
		//list.add(gbc3);
		//list.add(gbc4);
		
		GroupByClause gbcl = new GroupByClause(list);
		query.setGroupByClause(gbcl);
		
		SortColumn sbc = new SortColumn(c1,false);
		
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
				record.put("Subject_Id", ds.getValue("SUBJECT_ID"));
				record.put("Subject_Name", ds.getValue("SUBJECT_NAME"));
				record.put("Subject_Average", ds.getValue("SUBJECT_AVERAGE"));
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