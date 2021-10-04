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

@WebServlet("/AddMarks")
public class AddMarks extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Marks");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String student_id = request.getParameter("STUDENT_ID");
		String subject_id = request.getParameter("SUBJECT_ID");
		String mark = request.getParameter("MARK");
	
		PrintWriter out = response.getWriter();
		
		
		Row r = new Row("Marks");
		r.set("STUDENT_ID",student_id);
		r.set("SUBJECT_ID",subject_id);
		r.set("MARK", mark);
		
		DataObject dob=new WritableDataObject();
		
		try {
			dob.addRow(r);
			DataAccess.add(dob);
			out.println("Marks Added Successfully");
		} catch (DataAccessException e) {
			e.printStackTrace();
			out.println("Marks Add UnSuccessfull");
		}

    }
}