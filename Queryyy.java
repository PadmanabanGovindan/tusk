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
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.DerivedTable;
import com.adventnet.ds.query.GroupByClause;
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
import com.adventnet.swissqlapi.sql.statement.insert.CommonTableExpression;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/Queryyy")
public class Queryyy extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		
		PrintWriter out = response.getWriter();
		
		
		
		
		//----------------------------------------------------------------------------------------------------------------------
		
		SelectQuery query = new SelectQueryImpl(Table.getTable("INVTECHPARAMS"));
        query.addSelectColumn(Column.getColumn("INVTECHPARAMS", "INV_PARAM_ID"));
        Column paramValueCount = (Column.getColumn("INVTECHPARAMS", "PARAM_VALUE").count());
        
        Criteria criteria_count = new Criteria(paramValueCount, 1, QueryConstants.GREATER_THAN);
        Criteria criteria_param = new Criteria(new Column("INVTECHPARAMS", "PARAM_NAME"), "LAST_MAIL_ALERT_GEN*", QueryConstants.LIKE);
        criteria_param = criteria_param.or(new Criteria(new Column("INVTECHPARAMS", "PARAM_NAME"), "LAST_ALERT_GEN*", QueryConstants.LIKE));
        //Criteria criteria = criteria_count.and(criteria_param);
        
        List groupColList = new ArrayList();
        groupColList.add(Column.getColumn("INVTECHPARAMS", "INV_PARAM_ID"));
        groupColList.add(Column.getColumn("INVTECHPARAMS", "TECH_ID"));
        groupColList.add(Column.getColumn("INVTECHPARAMS", "PARAM_NAME"));
        GroupByClause groupByClause = new GroupByClause(groupColList, criteria_count);
        query.setGroupByClause(groupByClause);
        query.setCriteria(criteria_param);
        //Comment
        DeleteQuery deleteQuery = new DeleteQueryImpl("INVTECHPARAMS");
        
        DerivedColumn dc = new DerivedColumn("derv_col",query);
        
        Criteria cri = new Criteria(new Column("INVTECHPARAMS", "INV_PARAM_ID"),dc,QueryConstants.IN);
        deleteQuery.setCriteria(cri);
        
        try {
			System.out.println(RelationalAPI.getInstance().getDeleteSQL(deleteQuery));
			DataAccess.delete(deleteQuery);
		} catch (QueryConstructionException | DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//---------------------------------------------------------------------------------------------------------------------
		
		
		

		
		
		
		
		
		
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		out.println(query);

    }
		

    }
