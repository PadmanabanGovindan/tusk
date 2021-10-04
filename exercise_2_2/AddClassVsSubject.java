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

@WebServlet("/AddClassVsSubject")
public class AddClassVsSubject extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Class_Vs_Subject");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String class_id = request.getParameter("CLASS_ID");
		String subject_id = request.getParameter("SUBJECT_ID");
	
		PrintWriter out = response.getWriter();
		
		
		Row r = new Row("Class_Vs_Subject");
		r.set("CLASS_ID",class_id);
		r.set("SUBJECT_ID",subject_id);
		
		DataObject dob=new WritableDataObject();

		try {
			dob.addRow(r);
			DataAccess.add(dob);
			out.println("Class Subject Added Successfully");
		} catch (DataAccessException e) {
			e.printStackTrace();
			out.println("Add UnSuccessfull");
		}

    }
}