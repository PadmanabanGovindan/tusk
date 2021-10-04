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

@WebServlet("/DisplayActivityStrength")
public class DisplayActivityStrength extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Student_Cocurr_Activity");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		PrintWriter out = response.getWriter();
		
		
		//----------------------------------------------------------------------------------------------------------------------
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("Student_Cocurr_Activity"));
		Join  joinStudent = new Join("Student_Cocurr_Activity", "Cocurricular_Activities", new String [] {"ACTIVITY_ID"}, new String [] {"ACTIVITY_ID"}, Join.INNER_JOIN);
		query.addJoin(joinStudent);
		
	
		query.addSelectColumn(Column.getColumn("Cocurricular_Activities","ACTIVITY_ID"));
		query.addSelectColumn(Column.getColumn("Cocurricular_Activities","ACTIVITY_NAME"));
		

		Column c1 = new Column("Student_Cocurr_Activity","ACTIVITY_ID").count();
		c1.setColumnAlias("ACTIVITY_STRENGTH");
		query.addSelectColumn(c1);
	
		GroupByColumn gbc1 = new GroupByColumn(new Column("Cocurricular_Activities","ACTIVITY_ID"),true);
		GroupByColumn gbc2 = new GroupByColumn(new Column("Cocurricular_Activities","ACTIVITY_NAME"),true);
		
	
		
		List list = new ArrayList();
		list.add(gbc1);
		list.add(gbc2);
		//list.add(gbc3);
		//list.add(gbc4);
		
		GroupByClause gbcl = new GroupByClause(list);
		query.setGroupByClause(gbcl);
		
		Column sbc1 = new Column("Cocurricular_Activities","ACTIVITY_NAME");
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
				record.put("Activity_Id", ds.getValue("ACTIVITY_ID"));
				record.put("Activity_Name", ds.getValue("ACTIVITY_NAME"));
				record.put("Activity_Strength", ds.getValue("ACTIVITY_STRENGTH"));
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