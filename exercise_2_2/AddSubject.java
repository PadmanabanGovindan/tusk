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

@WebServlet("/AddSubject")
public class AddSubject extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Subject");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String subject_name = request.getParameter("SUBJECT_NAME");
		String marks = request.getParameter("TOTAL_MARKS");
	
		PrintWriter out = response.getWriter();
		
		
		Row r = new Row("Subject");
		r.set("SUBJECT_NAME",subject_name);
		r.set("TOTAL_MARKS",marks);
		
		DataObject dob=new WritableDataObject();

		try {
			dob.addRow(r);
			DataAccess.add(dob);
			out.println("Subject Added Successfully");
		} catch (DataAccessException e) {
			e.printStackTrace();
			out.println("Subject Add UnSuccessfull");
		}

    }
}