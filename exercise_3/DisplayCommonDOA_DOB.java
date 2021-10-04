package exercise_3;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;

import com.adventnet.db.api.RelationalAPI;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.QueryConstructionException;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Table;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/DisplayCommonDOA_DOB")
public class DisplayCommonDOA_DOB extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Student_Details");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
		PrintWriter out = response.getWriter();
		
		
		//----------------------------------------------------------------------------------------------------------------------
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("Student_Details"));
	
		query.addSelectColumn(Column.getColumn("Student_Details","STUDENT_NAME"));
		query.addSelectColumn(Column.getColumn("Student_Details","DOB"));
		query.addSelectColumn(Column.getColumn("Student_Details","DOA"));
		
		//Column DOA = Column.createFunction("DATE_MONTH", Column.getColumn("Student_Details","DOA"));
		//DOA.setType(Types.CHAR);
		//Column DOB = Column.createFunction("DATE_MONTH", Column.getColumn("Student_Details","DOB"));
		//DOB.setType(Types.CHAR);


		
		//query.setCriteria(new Criteria(DOB, DOA, QueryConstants.EQUAL));
		
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
				String DOB = ds.getValue("DOB").toString();
				String DOA = ds.getValue("DOA").toString();
				
				String[] dateParts_1 = DOB.split("-");
			    String dayDOB = dateParts_1[1]; 
			    String monthDOB = dateParts_1[2]; 
			    
			    String[] dateParts_2 = DOA.split("-");
			    String dayDOA = dateParts_2[1]; 
			    String monthDOA = dateParts_2[2]; 
				
				if(dayDOA.equals(dayDOB) && monthDOA.equals(monthDOB))
				{
					record.put("Student_Name", ds.getValue("STUDENT_NAME"));
					record.put("DOB", DOB);
					record.put("DOA", DOA);
					sq.put(record);
				}
				
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