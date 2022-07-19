package validator;

import java.util.ArrayList;

public class RuleList {

    private ArrayList<Rule> rules = new ArrayList<>();
    private RuleMaker ruleMaker = new RuleMaker();
    private ArrayList<String> errors = new ArrayList<>();

    public void addRule(Rule rule){
        this.rules.add(rule);
    }

    public void makeRules(){
        rules = ruleMaker.createRules();
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public void checkRule(Rule r,String[] parts){

        //provera pravila za JOIN
        if(r.getRuleID()==1){
            for(int i=0;i<parts.length-1;i++){
                if((parts[i].startsWith(r.getContext()) && !parts[i+1].startsWith("On"))
                        || parts[parts.length-1].startsWith(r.getContext())){
                    errors.add(r.getMessage());
                    break;
                }
            }
        }

        //provera pravila za sve što je selektovano a nije pod funkcijom agregacije
        if(r.getRuleID()==2){
            ArrayList<String> select = new ArrayList<>();
            ArrayList<String> agr = new ArrayList<>();
            ArrayList<String> gb = new ArrayList<>();

            for(int j=0;j<parts.length-1;j++) {
                if(parts[j].startsWith("Select")) {
                    select = this.parseStr(parts[j]);
                }
            }

            for(int i=0;i<parts.length-1;i++){
                if(parts[i].startsWith("Avg")  || parts[i].startsWith("Count") || parts[i].startsWith("Min") || parts[i].startsWith("Max")){
                    agr = this.parseStr(parts[i]);
                }
            }

            for(int i=0;i<parts.length;i++){
                if(parts[i].startsWith("GroupBy")){
                    gb = this.parseStr(parts[i]);
                }
            }

            if(!select.isEmpty() && !agr.isEmpty()){
                System.out.println(gb.size());
                for(String s:select){
                    if(!agr.contains(s) && !gb.contains(s)){
                        errors.add(r.getMessage());
                        break;
                    }
                }
            }
        }

        //ovde verovatno treba da se okrene redosled
        if(r.getRuleID() == 3 || r.getRuleID() == 4) {

        	for(int i = 1; i < parts.length; i++) {
        		if(parts[i].startsWith(r.getContext()) && !parts[i-1].startsWith("Where") && !parts[i-1].startsWith("OrWhere")
        				&& !parts[i-1].startsWith("AndWhere")) {
        			errors.add(r.getMessage());
        			break;
        		}
        	}
        }

        if(r.getRuleID() == 5){
            ArrayList<String> having = new ArrayList<>();
            ArrayList<String> agr = new ArrayList<>();

            for(int i=0;i<parts.length;i++){
                if(parts[i].startsWith("Avg")  || parts[i].startsWith("Count") || parts[i].startsWith("Min") || parts[i].startsWith("Max")) {
                    agr = this.parseStr(parts[i]);
                }
                if(parts[i].startsWith("Having") || parts[i].startsWith("OrHaving") || parts[i].startsWith("AndHaving")){
                    having = this.parseStr(parts[i]);
                }
            }

            if(!having.isEmpty() && agr.size()>1){
                if(!agr.get(1).equals(having.get(0)))
                    errors.add(r.getMessage());
            }
        }

        if(r.getRuleID() == 6){

            ArrayList<String> having = new ArrayList<>();
            ArrayList<String> agr = new ArrayList<>();
            boolean flag = false;

            for(int i=0;i<parts.length;i++){
                if(parts[i].startsWith("Avg")  || parts[i].startsWith("Count") || parts[i].startsWith("Min") || parts[i].startsWith("Max")) {
                    agr = this.parseStr(parts[i]);
                }
                if(parts[i].startsWith("Having") || parts[i].startsWith("OrHaving") || parts[i].startsWith("AndHaving")){
                    flag = true;
                    having = this.parseStr(parts[i]);
                }
            }

            if(agr.size()>1 && having.size()>1){
                if(!having.get(0).equals(agr.get(1)))
                    errors.add(r.getMessage());
            }else if(flag && agr.size()<2){
                    errors.add(r.getMessage());
            }

        }

        if(r.getRuleID() == 7){
            boolean flag = false;
            for(int i=0;i<parts.length;i++){
                if(parts[i].startsWith("Query")){
                    flag = true;
                    break;
                }
            }
            if(flag == false){
                errors.add(r.getMessage());
            }

        }
        
        if(r.getRuleID() == 8 || r.getRuleID() == 9) {
        	
        	if(parts[parts.length - 1].startsWith(r.getContext()))
        		errors.add(r.getMessage());
        	
        }
        
        if(r.getRuleID() == 10 || r.getRuleID() == 11) {
        	
        	
        }




    }

    private ArrayList<String> parseStr(String str){


        ArrayList<String> parts = new ArrayList<>();
        String[] s1 = str.split("\\(");
        String[] s2 = s1[1].split(",");
        for(String ss:s2){
            if(ss.contains(")")){
                StringBuilder sb = new StringBuilder(ss);
                sb.deleteCharAt(ss.length()-1);
                ss = sb.toString();
            }
            parts.add(ss);
        }
        return parts;
    }

    public ArrayList<String> getErrors() {
        return errors;
    }
}
