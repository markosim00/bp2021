package validator;

import app.Main;
import gui.MainFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ValidatorImpl implements Validator {

   
    RuleList ruleList = new RuleList();
    String error = "";

    @Override
    public ArrayList<String> validateText(String val){

        try {
            String[] parts = val.split("new");

            StringBuilder sb = new StringBuilder(parts[1]);
            sb.deleteCharAt(0);
            String part = sb.toString();

            String[] pts = part.split("\\)\\.");

            for(Rule r:ruleList.getRules()){
                ruleList.checkRule(r,pts);
            }

            if(!ruleList.getErrors().isEmpty()){
                for(String str:ruleList.getErrors()){
                    error = error.concat(str + "\n");
                }
                MainFrame.getInstance().getTextArea2().setText(error);
                ruleList.getErrors().clear();
            }else{
                if(parts.length > 2) {
                    String[] strings = val.split("\n");
                    String query1 = MainFrame.getInstance().getAppCore().getQueryCompiler().translateValue(strings[0], true);
                    System.out.println(query1);
                    String query2 = MainFrame.getInstance().getAppCore().getQueryCompiler().translateValue(strings[1], true);
                    System.out.println(query2);
                    String subquery = MainFrame.getInstance().getAppCore().getQueryCompiler().generateSubquery(query1, query2);
                    System.out.println(subquery);
                    MainFrame.getInstance().getAppCore().executeSQLQuery(subquery);
                }
                else
                    MainFrame.getInstance().getAppCore().getQueryCompiler().translateValue(val, false);
            }
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Error");
        }

        return null;
        	
    }

	public RuleList getRuleList() {
		return ruleList;
	}


}
