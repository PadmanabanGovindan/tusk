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

@WebServlet("/AddCocurricularActivity")
public class AddCocurricularActivity extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try {
			MetaDataUtil.getTableDefinitionByName("Cocurricular_Activities");
		} catch (MetaDataException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String activity_name = request.getParameter("ACTIVITY_NAME");
	
		PrintWriter out = response.getWriter();
		
		
		Row r = new Row("Cocurricular_Activities");
		r.set("ACTIVITY_NAME",activity_name);
		
		DataObject dob=new WritableDataObject();

		try {
			dob.addRow(r);
			DataAccess.add(dob);
			out.println("Cocurricular Activity Added Successfully");
		} catch (DataAccessException e) {
			e.printStackTrace();
			out.println("Add UnSuccessfull");
		}

    }
}