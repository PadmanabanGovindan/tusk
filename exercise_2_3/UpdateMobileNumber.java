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

@WebServlet("/UpdateMobileNumber")
public class UpdateMobileNumber extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Student_Details");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String mobile_number = request.getParameter("MOBILE_NUMBER");
		String class_name = request.getParameter("CLASS_NAME");
		String student_name = request.getParameter("STUDENT_NAME");
	
		PrintWriter out = response.getWriter();
		
		
		//------------------------------------------------------------------------------
		
		UpdateQuery uq = new UpdateQueryImpl("Student_Details");
		uq.setUpdateColumn("MOBILE_NUMBER", mobile_number);
		Join  join = new Join("Student_Details", "Class", new String[] {"CLASS_ID"}, new String[] {"CLASS_ID"},Join.INNER_JOIN);
		uq.addJoin(join);
		Criteria c1 = new Criteria(Column.getColumn("Class", "CLASS_NAME"), class_name, QueryConstants.EQUAL);
		Criteria c2 = new Criteria(Column.getColumn("Student_Details", "STUDENT_NAME"), student_name, QueryConstants.EQUAL);
		Criteria c = c1.and(c2);
		uq.setCriteria(c);
		
		
		//------------------------------------------------------------------------------
		
		
		

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