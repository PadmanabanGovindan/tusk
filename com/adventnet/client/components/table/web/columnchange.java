package com.adventnet.client.components.table.web;

import com.adventnet.client.components.table.web.DefaultTransformer;
import com.adventnet.client.components.web.TransformerContext;
import com.adventnet.client.view.web.ViewContext;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.i18n.I18N;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.persistence.DataObject;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;


public class columnchange extends DefaultTransformer {

    @Override
    public void renderCell(TransformerContext tableContext) {

        try {
            String viewName = tableContext.getViewContext().getUniqueId();
            HttpServletRequest request = tableContext.getRequest();
            Object data = tableContext.getPropertyValue();
            super.renderCell(tableContext);
            HashMap columnProperties = tableContext.getRenderedAttributes();
            String displyColumn = tableContext.getPropertyName();
            
            
            System.out.println("viewName " + viewName);
            System.out.println("request " + request);
            System.out.println("data "+data);
            System.out.println("columnProperties" + columnProperties);
            System.out.println("displaycolumn " +displyColumn);

            if (("TYPE").equals(displyColumn)) {
            	int value = (int) data;
                if(value == 1) {
                    columnProperties.put("VALUE", "Fiction");
                }
                if(value == 2) {
                    columnProperties.put("VALUE", "Sci-Fantacy");
                }
                if(value == 3) {
                    columnProperties.put("VALUE", "Mystery");
                }
                if(value == 4) {
                    columnProperties.put("VALUE", "Thriller");
                }
                if(value == 5) {
                    columnProperties.put("VALUE", "Romance");
                }
            }
	    

        } catch (Exception e) {
            e.printStackTrace();
        }

    }}
