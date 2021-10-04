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

@WebServlet("/AddStudent")
public class AddStudent extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Student_Details");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String name = request.getParameter("STUDENT_NAME");
		String class_id = request.getParameter("CLASS_ID");
		String gender = request.getParameter("GENDER");
		String DOB = request.getParameter("DOB");
		String mobile = request.getParameter("MOBILE_NUMBER");
		String blood_group = request.getParameter("BLOOD_GROUP");
		String DOA = request.getParameter("DOA");
		
		PrintWriter out = response.getWriter();
		
		
		Row r = new Row("Student_Details");
		r.set("STUDENT_NAME",name);
		r.set("CLASS_ID",class_id);
		r.set("GENDER",gender);
		r.set("DOB", DOB);
		r.set("MOBILE_NUMBER", mobile);
		r.set("BLOOD_GROUP", blood_group);
		r.set("DOA", DOA);
		DataObject dob=new WritableDataObject();
		

		try {
			dob.addRow(r);
			DataAccess.add(dob);
			out.println("Student Added Successfully");
		} catch (DataAccessException e) {
			e.printStackTrace();
			out.println("Student Add UnSuccessfull");
		}

    }
}