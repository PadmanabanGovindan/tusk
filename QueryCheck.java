import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.swissqlapi.sql.statement.insert.CommonTableExpression;
import com.me.devicemanagement.framework.server.exception.SyMException;
import com.me.devicemanagement.framework.server.task.DeleteDataDetails;
import com.me.devicemanagement.framework.server.task.DeletionTaskUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/QueryCheck")
public class QueryCheck extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		
		PrintWriter out = response.getWriter();
		
	
		
		
		//----------------------------------------------------------------------------------------------------------------------
		
		Properties properties = new Properties();
        properties.put("OperationType", com.me.devicemanagement.framework.server.general.OperationConstant.DELETE_WITH_SELECT_QUERY);
        properties.put("StatusKey", "Delete_Duplicate_Data_DCAlert"+ System.currentTimeMillis());
        DeleteDataDetails deleteDataDetails = new DeleteDataDetails();
        deleteDataDetails.tableName = "DCALERT";

        SelectQuery query = new SelectQueryImpl(Table.getTable("DCALERT"));
        Column maxAlertId = (Column.getColumn("DCALERT", "ALERT_ID").maximum());
        query.addSelectColumn(maxAlertId);

        List groupColList = new ArrayList();
        groupColList.add(Column.getColumn("DCALERT", "ALERT_REMARKS"));
        groupColList.add(Column.getColumn("DCALERT", "ALERT_REMARKS_ARGS"));
        groupColList.add(Column.getColumn("DCALERT", "ALERT_TIMESTAMP"));
        GroupByClause groupByClause = new GroupByClause(groupColList);
        query.setGroupByClause(groupByClause);

        DerivedColumn derivedColumn = new DerivedColumn("deriv_col", query);//No I18N

        SelectQuery select_query = new SelectQueryImpl(Table.getTable("DCALERT"));
        select_query.addSelectColumn(Column.getColumn("DCALERT","*"));
        Join typeJoin = new Join("DCALERT", "DCALERTTYPE", new String[]{"ALERT_TYPE_ID"}, new String[]{"ALERT_TYPE_ID"}, Join.INNER_JOIN);
        select_query.addJoin(typeJoin);
        Criteria criteria = new Criteria(new Column("DCALERT", "ALERT_ID"), derivedColumn, QueryConstants.NOT_IN);
        criteria = criteria.and(new Criteria(new Column("DCALERTTYPE", "MODULE_ID"), 5, QueryConstants.EQUAL));
        select_query.setCriteria(criteria);

        deleteDataDetails.selectQuery = select_query;
        deleteDataDetails.chunkThreshold=10000;
        properties.put("DeleteDataDetails", deleteDataDetails);

        
        try {
			System.out.println(RelationalAPI.getInstance().getSelectSQL(select_query));
		} catch (QueryConstructionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//---------------------------------------------------------------------------------------------------------------------
		
		
		

		
		
		
		
		
		
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		
		out.println(query);

    }
		

    }
