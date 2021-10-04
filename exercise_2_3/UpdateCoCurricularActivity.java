package exercise_2_3;

import java.io.IOException;
import java.io.PrintWriter;

import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/UpdateCoCurricularActivity")
public class UpdateCoCurricularActivity extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Student_Cocurr_Activity");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String student_name = request.getParameter("STUDENT_NAME");
		int activity_id = 2;
	
		PrintWriter out = response.getWriter();
		
		
		//------------------------------------------------------------------------------
		
		UpdateQuery uq = new UpdateQueryImpl("Student_Cocurr_Activity");
		uq.setUpdateColumn("ACTIVITY_ID", activity_id);
		Join  joinStudentDetails = new Join("Student_Cocurr_Activity", "Student_Details", new String[] {"STUDENT_ID"}, new String[] {"STUDENT_ID"},Join.INNER_JOIN);
		uq.addJoin(joinStudentDetails);
		Criteria c1 = new Criteria(Column.getColumn("Student_Details", "STUDENT_NAME"), student_name, QueryConstants.EQUAL);
		Join  joinClass = new Join("Student_Details", "Class", new String[] {"CLASS_ID"}, new String[] {"CLASS_ID"},Join.INNER_JOIN);
		uq.addJoin(joinClass);
		Criteria c2 = new Criteria(Column.getColumn("Class", "CLASS_NAME"), "11", QueryConstants.EQUAL);
		Join  joinActivity = new Join("Student_Cocurr_Activity", "Cocurricular_Activities", new String[] {"ACTIVITY_ID"}, new String[] {"ACTIVITY_ID"},Join.INNER_JOIN);
		uq.addJoin(joinActivity);
		Criteria c3 = new Criteria(Column.getColumn("Cocurricular_Activities", "ACTIVITY_NAME"), "Swimming", QueryConstants.EQUAL);
		
		Criteria c = c1.and(c2).and(c3);
		uq.setCriteria(c);
		
		
		//------------------------------------------------------------------------------
		
		out.println(uq);
		
		

		try {
			DataAccess.update(uq);
			out.println("Update Successful");
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			out.println("Update UnSuccessful");
		}

    }
}