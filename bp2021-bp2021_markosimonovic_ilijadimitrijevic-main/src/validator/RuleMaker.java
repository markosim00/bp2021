package validator;

import java.util.ArrayList;

public class RuleMaker {

    public ArrayList<Rule> createRules(){

        ArrayList<Rule> rules = new ArrayList<>();

        Rule rule1 = new Rule(1,"Join","\"Join\" must be followed by the \"On\"");
        Rule rule2 = new Rule(2,"GroupBy","Any argument that is not under \"aggregation\" must be in \"groupBy\"\n");
        Rule rule3 = new Rule(3, "OrWhere", "Where must be before OrWhere");
        Rule rule4 = new Rule(4, "AndWhere", "Where must be before AndWhere");
        Rule rule5 = new Rule(5,"Having","Only those arguments are under aggregacion can be found in having");
        Rule rule6 = new Rule(6,"Alias","Alias must be set in Aggregate function");
        Rule rule7 = new Rule(7,"Query","Query must be set");
        Rule rule8 = new Rule(8, "WhereInQ", "WhereInQ must be the last operator in query");
        Rule rule9 = new Rule(9, "WhereEqQ", "WhereEqQ must be the last operator in query");
        Rule rule10 = new Rule(10, "WhereInQ", "WhereInQ must be in the second query");
        Rule rule11 = new Rule(11, "WhereEqQ", "WhereEqQ must be in the second query");

        rules.add(rule1);
        rules.add(rule2);
        rules.add(rule3);
        rules.add(rule4);
        rules.add(rule5);
        rules.add(rule6);
        rules.add(rule7);
        rules.add(rule8);
        rules.add(rule9);
        rules.add(rule10);
        rules.add(rule11);

        return rules;
    }

}
