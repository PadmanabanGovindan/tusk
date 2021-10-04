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

@WebServlet("/DeleteMarks")
public class DeleteMarks extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Marks");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String subject_name = request.getParameter("SUBJECT_NAME");
	
		PrintWriter out = response.getWriter();
		
		
		//------------------------------------------------------------------------------
		DeleteQuery dq = new DeleteQueryImpl("Marks");
		Join  joinSubject = new Join("Marks", "Subject", new String[] {"SUBJECT_ID"}, new String[] {"SUBJECT_ID"},Join.INNER_JOIN);
		dq.addJoin(joinSubject);
		Criteria c1 = new Criteria(Column.getColumn("Subject", "SUBJECT_NAME"), subject_name, QueryConstants.EQUAL);
		Join  joinClassId = new Join("Subject", "Class_Vs_Subject", new String[] {"SUBJECT_ID"}, new String[] {"SUBJECT_ID"},Join.INNER_JOIN);
		dq.addJoin(joinClassId);
		Join  joinClass = new Join("Class_Vs_Subject", "Class", new String[] {"CLASS_ID"}, new String[] {"CLASS_ID"},Join.INNER_JOIN);
		dq.addJoin(joinClass);
		Criteria c2 = new Criteria(Column.getColumn("Class", "CLASS_NAME"), "12", QueryConstants.EQUAL);
		Criteria c = c1.and(c2);
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