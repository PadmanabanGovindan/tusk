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

@WebServlet("/UpdateMarks")
public class UpdateMarks extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Marks");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String student_name = request.getParameter("STUDENT_NAME");
		String subject_name = request.getParameter("SUBJECT_NAME");
		String mark = request.getParameter("MARK");
	
		PrintWriter out = response.getWriter();
		
		
		//------------------------------------------------------------------------------
		
		UpdateQuery uq = new UpdateQueryImpl("Marks");
		uq.setUpdateColumn("MARK", mark);
		Join  joinStudentDetails = new Join("Marks", "Student_Details", new String[] {"STUDENT_ID"}, new String[] {"STUDENT_ID"},Join.INNER_JOIN);
		uq.addJoin(joinStudentDetails);
		Criteria c1 = new Criteria(Column.getColumn("Student_Details", "STUDENT_NAME"), student_name, QueryConstants.EQUAL);
		Join  joinSubject = new Join("Marks", "Subject", new String[] {"SUBJECT_ID"}, new String[] {"SUBJECT_ID"},Join.INNER_JOIN);
		uq.addJoin(joinSubject);
		Criteria c2 = new Criteria(Column.getColumn("Subject", "SUBJECT_NAME"), subject_name, QueryConstants.EQUAL);
		Join  joinClassSubject = new Join("Marks", "Class_Vs_Subject", new String[] {"SUBJECT_ID"}, new String[] {"SUBJECT_ID"},Join.INNER_JOIN);
		uq.addJoin(joinClassSubject);
		Join  joinClass = new Join("Class_Vs_Subject", "Class", new String[] {"CLASS_ID"}, new String[] {"CLASS_ID"},Join.INNER_JOIN);
		uq.addJoin(joinClass);
		Criteria c3 = new Criteria(Column.getColumn("Class", "CLASS_NAME"), new int[]{10,12}, QueryConstants.IN);
		Criteria c = (c1.and(c2)).and(c3);
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