package exercise_2_2;
import java.io.IOException;
import java.io.PrintWriter;

import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/AddStudentCocurrActivity")
public class AddStudentCocurrActivity extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Student_Cocurr_Activity");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String student_id = request.getParameter("STUDENT_ID");
		String activity_id = request.getParameter("ACTIVITY_ID");
	
		PrintWriter out = response.getWriter();
		
		
		Row r = new Row("Student_Cocurr_Activity");
		r.set("STUDENT_ID",student_id);
		r.set("ACTIVITY_ID",activity_id);
		
		DataObject dob=new WritableDataObject();
		

		try {
			dob.addRow(r);
			DataAccess.add(dob);
			out.println("Student Cocurriculat Activity Added Successfully");
		} catch (DataAccessException e) {
			e.printStackTrace();
			out.println("Add UnSuccessfull");
		}

    }
}