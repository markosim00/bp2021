package validator;

public class Rule {

    private int ruleID;
    private String context;
    private String message;

    public Rule(int ruleID, String context, String message) {
        this.ruleID = ruleID;
        this.context = context;
        this.message = message;
    }

    public int getRuleID() {
        return ruleID;
    }

    public String getContext() {
        return context;
    }

    public String getMessage() {
        return message;
    }

    public void setRuleID(int ruleID) {
        this.ruleID = ruleID;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
