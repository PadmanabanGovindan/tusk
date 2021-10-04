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
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.GroupByClause;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Table;
import com.adventnet.inventorymanagement.INVTECHPARAMS;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.sym.server.util.SyMUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/DisplayMarks")
public class DisplayMarks extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Marks");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		PrintWriter out = response.getWriter();
		
		
		String class_name = request.getParameter("CLASS_NAME");
		
		//----------------------------------------------------------------------------------------------------------------------
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("Marks"));
		Join  joinStudent = new Join("Marks", "Student_Details", new String [] {"STUDENT_ID"}, new String [] {"STUDENT_ID"}, Join.INNER_JOIN);
		query.addJoin(joinStudent);
		
		Join  joinClass = new Join("Student_Details", "Class", new String [] {"CLASS_ID"}, new String [] {"CLASS_ID"}, Join.INNER_JOIN);
		query.addJoin(joinClass);
		query.setCriteria(new Criteria(Column.getColumn("Class","CLASS_NAME"), class_name, QueryConstants.EQUAL));
		
		Join  joinSubject = new Join("Marks", "Subject", new String [] {"SUBJECT_ID"}, new String [] {"SUBJECT_ID"}, Join.INNER_JOIN);
		query.addJoin(joinSubject);
		
		query.addSelectColumn(Column.getColumn("Student_Details","STUDENT_ID"));
		query.addSelectColumn(Column.getColumn("Student_Details","STUDENT_NAME"));
		query.addSelectColumn(Column.getColumn("Subject","SUBJECT_NAME"));
		query.addSelectColumn(Column.getColumn("Class","CLASS_NAME"));
		query.addSelectColumn(Column.getColumn("Marks","MARK"));
		

		SortColumn sbc1 = new SortColumn( new Column("Student_Details","STUDENT_NAME"),true);
		SortColumn sbc2 = new SortColumn(new Column("Subject","SUBJECT_NAME"),true);
		
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
				record.put("Subject", ds.getValue("SUBJECT_NAME"));
				record.put("Class", ds.getValue("CLASS_NAME"));
				record.put("Mark", ds.getValue("MARK"));
				sq.put(record);
		    }
		} catch (SQLException | QueryConstructionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
//		 DeleteQuery deleteQuery = new DeleteQueryImpl(INVTECHPARAMS.TABLE);
//
//      
//        Column paramValue = new Column(INVTECHPARAMS.TABLE, INVTECHPARAMS.PARAM_VALUE);
//        Column paramValueCount = paramValue.count();
//
//        Criteria criteria_count = new Criteria(paramValueCount, 2, QueryConstants.EQUAL);
//        Criteria criteria_param = new Criteria(new Column(INVTECHPARAMS.TABLE, INVTECHPARAMS.PARAM_NAME), "LAST_MAIL_ALERT_GEN%", QueryConstants.LIKE);
//        criteria_param = criteria_param.and(new Criteria(new Column(INVTECHPARAMS.TABLE, INVTECHPARAMS.PARAM_NAME), "LAST_ALERT_GEN%", QueryConstants.LIKE));
//        Criteria criteria = criteria_count.or(criteria_param);
//        
//        
//
//        List groupColList = new ArrayList();
//        groupColList.add(Column.getColumn(INVTECHPARAMS.TABLE, INVTECHPARAMS.INV_PARAM_ID));
//        groupColList.add(Column.getColumn(INVTECHPARAMS.TABLE, INVTECHPARAMS.TECH_ID));
//        groupColList.add(Column.getColumn(INVTECHPARAMS.TABLE, INVTECHPARAMS.PARAM_NAME));
//        GroupByClause groupByClause = new GroupByClause(groupColList);
//        deleteQuery.setGroupByClause(groupByClause);
//
//        
//        try {
//            SyMUtil.getPersistence().delete(deleteQuery);
//        } catch (Exception e) {
//    }
		
		
		
		
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		out.println(sq);

    }
		

    }
