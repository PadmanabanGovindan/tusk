package exercise_2_4;
import java.io.IOException;
import java.io.PrintWriter;

import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DeleteQuery;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.QueryConstants;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DeleteCocurricularActivity")
public class DeleteCocurricularActivity extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Student_Cocurr_Activity");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	
		PrintWriter out = response.getWriter();
		
		
		//------------------------------------------------------------------------------
		
		DeleteQuery dq = new DeleteQueryImpl("Student_Cocurr_Activity");
		Join  joinStudent = new Join("Student_Cocurr_Activity", "Student_Details", new String[] {"STUDENT_ID"}, new String[] {"STUDENT_ID"},Join.INNER_JOIN);
		dq.addJoin(joinStudent);
		Join  joinClass = new Join("Student_Details", "Class", new String[] {"CLASS_ID"}, new String[] {"CLASS_ID"},Join.INNER_JOIN);
		dq.addJoin(joinClass);
		Criteria c1 = new Criteria(Column.getColumn("Class", "CLASS_NAME"), "10", QueryConstants.EQUAL);
		Join  joinActivity = new Join("Student_Cocurr_Activity", "Cocurricular_Activities", new String[] {"ACTIVITY_ID"}, new String[] {"ACTIVITY_ID"},Join.INNER_JOIN);
		dq.addJoin(joinActivity);
		Criteria c2 = new Criteria(Column.getColumn("Cocurricular_Activities", "ACTIVITY_NAME"), "Spoken English", QueryConstants.EQUAL);
		Criteria c3 = c2.negate();
		Criteria c = c1.and(c3);
		dq.setCriteria(c);
		
		
		//------------------------------------------------------------------------------
		
		
		

		try {
			DataAccess.delete(dq);
			out.println("Update Successful");
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out.println("Update UnSuccessful");
		}

    }
}