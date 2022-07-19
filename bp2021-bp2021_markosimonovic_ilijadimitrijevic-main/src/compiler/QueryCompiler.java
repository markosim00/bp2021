package compiler;

import app.Main;
import database.MSSQLrepository;
import gui.MainFrame;
import gui.TableFrame;
import gui.table.ResultSetTable;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class QueryCompiler implements Compiler{

    Map<String,Object> tokens = new HashMap<String, Object>();
    int idx = -1;
    ArrayList<String> arr = new ArrayList<>();
    Map<String,Boolean> flags = new HashMap<String,Boolean>();
    String having = "";
    String agr = "";
    String globalAlias = "§";

    @Override
    public String translateValue(String val, boolean flag){

        String[] parts = val.split("new");


        StringBuilder sb = new StringBuilder(parts[1]);
        sb.deleteCharAt(0);
        String part = sb.toString();

        String[] pts = part.split("\\)\\.");

        for(String s:pts){
            arr.add(s);
        }

        for(String s:pts){

            idx++;

            //parsiranje za projekciju (Select)
            if(s.startsWith("Select(")){
                String[] p = s.split("\\(");
                ArrayList<String> selects = splitSelection(p);
                tokens.put("Select",selects);
            }

            //parsiranje za upit nad tabelom
            if(s.startsWith("Query(")){
                String[] p = s.split("\\(\"");
                p[1] = this.deleteCharacters(p[1]);
                tokens.put("Query",p[1]);
            }

            //parsiranje za Join i za On
            if(s.startsWith("Join(")){
                HashMap<String,String> vals = new HashMap<String, String>();
                String on = arr.get(idx+1);
                String[] p1 = s.split("\\(\"");
                p1[1] = this.deleteCharacters(p1[1]);
                vals.put("tableName",p1[1]);
                on = on.replace("On","");
                on = on.replace("(","");
                on = on.replace(")","");
                String[] p2;
                p2 = on.split(",");
                int index = -1;
                for(String ss:p2){
                    index++;
                    ss = this.deleteCharacters(ss);
                    if(index==0)
                        vals.put("column1",ss);
                    else if(index==1) {
                        vals.put("operator", ss);
                    }
                    else if(index==2) {

                        vals.put("column2", ss);
                    }

                }

                tokens.put("Join",vals);

            }

            if(s.startsWith("Avg(") || s.startsWith("Count(") || s.startsWith("Min(") || s.startsWith("Max(")){
                HashMap<String,String> agr = new HashMap<String, String>();
                String[] p = s.split("\\(");
                String tittle = p[0];
                String[] p1 = p[1].split(",");
                int idx = -1;
                for(String ss:p1){
                    idx++;
                    ss = this.deleteCharacters(ss);
                    if(idx==0)
                        agr.put("columnName",ss);
                    if(idx==1)
                        globalAlias = ss;
                        agr.put("alias",ss);

                }

                tokens.put(tittle,agr);

            }

            if(s.startsWith("Having") || s.startsWith("OrHaving") || s.startsWith("AndHaving")){
                HashMap<String,String> agr = new HashMap<String, String>();
                String[] p = s.split("\\(");
                having = p[0];
                String[] p1 = p[1].split(",");
                int idx = -1;
                for(String ss:p1){
                    idx++;
                    ss = this.deleteCharacters(ss);
                    if(idx==0)
                        agr.put("alias",ss);
                    if(idx==1)
                        agr.put("operator",ss);
                    if(idx==2)
                        agr.put("criteria",ss);
                }
                tokens.put(having,agr);
            }

            if(s.startsWith("GroupBy")){
                ArrayList<String> arr = new ArrayList<>();
                String[] p1 = s.split("\\(");
                String[] p2 = p1[1].split(",");

                for(String ss:p2){
                    ss = this.deleteCharacters(ss);
                    arr.add(ss);
                }
                tokens.put("GroupBy",arr);
            }

            if(s.startsWith("OrderBy(") || s.startsWith("OrderByDesc(")) {
                String [] p = s.split("\"");
                if(s.startsWith("OrderBy(")) {
                    tokens.put("OrderBy", p[1]);
                }
                else {
                    tokens.put("OrderByDesc", p[1]);
                }
            }

            if(s.startsWith("WhereInQ(")) {
                ArrayList<String> strings = new ArrayList<String>();
                String [] p = s.split(",");
                StringBuilder sb1 = new StringBuilder(p[0]);
                sb1.delete(0, 9);
                String part1 = sb1.toString();
                part1 = this.deleteCharacters(part1);
                StringBuilder sb2 = new StringBuilder(p[1]);
                String part2 = sb2.toString();
                part2 = this.deleteCharacters(part2);
                System.out.println(part1);
                System.out.println(part2);
                tokens.put("WhereInQ", strings);
            }

            if(s.startsWith("WhereEqQ(")) {
                ArrayList<String> strings = new ArrayList<String>();
                String [] p = s.split(",");
                StringBuilder sb1 = new StringBuilder(p[0]);
                sb1.delete(0, 9);
                String part1 = sb1.toString();
                part1 = this.deleteCharacters(part1);
                StringBuilder sb2 = new StringBuilder(p[1]);
                String part2 = sb2.toString();
                part2 = this.deleteCharacters(part2);
                System.out.println(part1);
                System.out.println(part2);
                tokens.put("WhereEqQ", strings);
            }

            if(s.startsWith("Where") || s.startsWith("OrWhere") || s.startsWith("AndWhere") || s.startsWith("WhereBetween")){
                ArrayList<String> strings = new ArrayList<String>();
                String[] p1 = s.split("\\(");
                String tittle = p1[0];
                String[] p2 = p1[1].split(",");
                for(String ss:p2){
                    ss = this.deleteCharacters(ss);
                    strings.add(ss);
                }
                tokens.put(tittle,strings);
            }

//  var a = new Query("Employees").WhereIn("job_id").("PU_CLERK","aa","vvv")
            if(s.startsWith("WhereIn(")) {
                ArrayList<String> strings = new ArrayList<>();
                String [] p = s.split("\\(");
                String in = arr.get(idx+1);
                System.out.println(in);
                in = this.deleteCharacters(in);
                System.out.println(in);
                p[1] = this.deleteCharacters(p[1]);
                strings.add(p[1]);
                String [] p2 = in.split(",");
                for(String ss:p2){
                    strings.add(ss);
                }
                tokens.put("WhereIn", strings);
            }

            if(s.startsWith("WhereEndsWith(")) {
                String [] p = s.split(",");
                StringBuilder sb1 = new StringBuilder(p[0]);
                StringBuilder sb2 = new StringBuilder(p[1]);
                String part2;
                ArrayList<String> strings = new ArrayList<String>();

                sb1.delete(0, 14);
                if(sb2.charAt(sb2.length() - 1) == ')')
                    sb2.deleteCharAt(sb2.length() - 1);

                if(sb1.charAt(0) == '\"') {
                    sb1.deleteCharAt(0);
                    sb1.deleteCharAt(sb1.length() - 1);
                }
                else if(sb1.charAt(0) == ' ') {
                    sb1.deleteCharAt(0);
                    sb1.deleteCharAt(0);
                    sb1.deleteCharAt(sb1.length() - 1);
                }

                if(sb2.charAt(0) == '\"') {
                    sb2.deleteCharAt(0);
                    sb2.deleteCharAt(sb2.length() - 1);
                }
                else if(sb2.charAt(0) == ' ') {
                    sb2.deleteCharAt(0);
                    sb2.deleteCharAt(0);
                    sb2.deleteCharAt(sb2.length() - 1);
                }

                part = sb1.toString();
                part2 = sb2.toString();

                strings.add(part);
                strings.add(part2);
//                System.out.println(part);
//                System.out.println(part2);
                tokens.put("WhereEndsWith", strings);
            }

            if(s.startsWith("WhereStartsWith(")) {
                String [] p = s.split(",");
                StringBuilder sb1 = new StringBuilder(p[0]);
                StringBuilder sb2 = new StringBuilder(p[1]);
                String part2;
                ArrayList<String> strings = new ArrayList<String>();

                sb1.delete(0, 16);

                if(sb2.charAt(sb2.length() - 1) == ')')
                    sb2.deleteCharAt(sb2.length() - 1);

                if(sb1.charAt(0) == '\"') {
                    sb1.deleteCharAt(0);
                    sb1.deleteCharAt(sb1.length() - 1);
                }
                else if(sb1.charAt(0) == ' ') {
                    sb1.deleteCharAt(0);
                    sb1.deleteCharAt(0);
                    sb1.deleteCharAt(sb1.length() - 1);
                }

                if(sb2.charAt(0) == '\"') {
                    sb2.deleteCharAt(0);
                    sb2.deleteCharAt(sb2.length() - 1);
                }
                else if(sb2.charAt(0) == ' ') {
                    sb2.deleteCharAt(0);
                    sb2.deleteCharAt(0);
                    sb2.deleteCharAt(sb2.length() - 1);
                }

                part = sb1.toString();
                part2 = sb2.toString();

                strings.add(part);
                strings.add(part2);
//                System.out.println(part);
//                System.out.println(part2);
                tokens.put("WhereStartsWith", strings);
            }

            if(s.startsWith("WhereContains(")) {
                String [] p = s.split(",");
                StringBuilder sb1 = new StringBuilder(p[0]);
                StringBuilder sb2 = new StringBuilder(p[1]);
                String part2;
                ArrayList<String> strings = new ArrayList<String>();

                sb1.delete(0, 14);
                if(sb2.charAt(sb2.length() - 1) == ')')
                    sb2.deleteCharAt(sb2.length() - 1);

                if(sb1.charAt(0) == '\"') {
                    sb1.deleteCharAt(0);
                    sb1.deleteCharAt(sb1.length() - 1);
                }
                else if(sb1.charAt(0) == ' ') {
                    sb1.deleteCharAt(0);
                    sb1.deleteCharAt(0);
                    sb1.deleteCharAt(sb1.length() - 1);
                }

                if(sb2.charAt(0) == '\"') {
                    sb2.deleteCharAt(0);
                    sb2.deleteCharAt(sb2.length() - 1);
                }
                else if(sb2.charAt(0) == ' ') {
                    sb2.deleteCharAt(0);
                    sb2.deleteCharAt(0);
                    sb2.deleteCharAt(sb2.length() - 1);
                }

                part = sb1.toString();
                part2 = sb2.toString();

                strings.add(part);
                strings.add(part2);
//                System.out.println(part);
//                System.out.println(part2);
                tokens.put("WhereContains", strings);
            }


        }

        String query = generateSQL(tokens);
        if(!flag)
            MainFrame.getInstance().getAppCore().executeSQLQuery(query);
        return query;
    }

    public String generateSubquery(String query1, String query2) {
        String subquery = query2.concat("(" + query1 + ")");
        MainFrame.getInstance().getTextArea2().setText(subquery);
        return subquery;
    }




    private ArrayList<String> splitSelection(String[] parts){

        ArrayList<String> selects = new ArrayList<>();

        String[] pts1 = parts[1].split(",");
        parts[1] = parts[1].replace(")","");
        String res;

        for(String part:pts1){
            part = part.replace(" ","");
            part = part.replace("\"","");
            part = part.replace(")","");
            part = part.replace("\n","");
            selects.add(part);
        }

        return selects;
    }

    private String generateSQL(Map<String, Object> tokens) {

        String query = "SELECT";

        if (tokens.get("Select") != null) {
            ArrayList<String> selected = ((ArrayList<String>) tokens.get("Select"));
            if(selected.contains(globalAlias)){
                selected.remove(globalAlias);
            }
            for(int i=0;i<selected.size();i++){
                query = query.concat(" [" + selected.get(i) + "]");
                if(i!=selected.size()-1){
                    query = query.concat(",");
                }
            }
        }else if(tokens.get("Select") == null && tokens.get("Avg") == null && tokens.get("Count") == null && tokens.get("Min") == null && tokens.get("Max") == null)
            query = query.concat(" *");

        if(tokens.get("Avg") != null || tokens.get("Count") != null || tokens.get("Min") != null || tokens.get("Max") != null){

            String key = "";
            HashMap<String ,String> map;

            for(Map.Entry<String, Object> set: tokens.entrySet()) {
                if(set.getKey().equals("Avg") || set.getKey().equals("Count") || set.getKey().equals("Min") || set.getKey().equals("Max")){
                    key = set.getKey();
                    break;
                }
            }

            map = ((HashMap<String, String>) tokens.get(key));
            String alias = map.get("alias");
            System.out.println(alias);
            StringBuilder sb = new StringBuilder(key);
            key = sb.toString();
            key = key.toLowerCase();
            if(tokens.get("Select") != null)
                query = query.concat(",");
            agr = key.toUpperCase() + "(" + map.get("columnName") + ")";
            query = query.concat(" " + agr + " as " + alias);
        }

        if(tokens.get("Query") != null){
            String s = (String) tokens.get("Query");
            query = query.concat(" FROM [" + s + "]");
        }

        if(tokens.get("Join") != null){

            HashMap<String,String> parts = ((HashMap<String, String>) tokens.get("Join"));
            query = query.concat(" JOIN ");
            query = query.concat("[" + parts.get("tableName") + "]" + " ON ");
            query = query.concat("(" + parts.get("column1") + " " + parts.get("operator") + " " + parts.get("column2") + ")");


        }

        if(tokens.get("Where") != null){
            query = concatFilter(query,"WHERE","Where");
        }

        if(tokens.get("AndWhere") != null){
            query = concatFilter(query,"AND","AndWhere");
        }

        if(tokens.get("OrWhere") != null){
            query = concatFilter(query,"OR","OrWhere");
        }

        if(tokens.get("WhereBetween") != null) {
            query = query.concat(" WHERE ");
            ArrayList<String> strings = (ArrayList<String>) tokens.get("WhereBetween");
            query = query.concat("[" + strings.get(0) + "] " + "BETWEEN " + strings.get(1) + " AND " + strings.get(2));
        }

        if(tokens.get("WhereIn") != null) {
            query = query.concat(" WHERE ");
            ArrayList<String> stringArrayList = (ArrayList<String>) tokens.get("WhereIn");
            System.out.println(stringArrayList.size());
            query = query.concat("[" + stringArrayList.get(0) + "] " + "IN (");
            for(int i=1;i<stringArrayList.size();i++){
                query = query.concat("'" + stringArrayList.get(i) + "'");
                if(i!= stringArrayList.size()-1)
                    query = query.concat(",");
            }
            query = query.concat(")");
        }

        if(tokens.get("WhereEndsWith") != null) {
            query = query.concat(" WHERE [");
            ArrayList<String> strings = (ArrayList<String>) tokens.get("WhereEndsWith");
            query = query.concat(strings.get(0) + "] LIKE '%" + strings.get(1) + "'");
        }

        if(tokens.get("WhereStartsWith") != null) {
            query = query.concat(" WHERE [");
            ArrayList<String> strings = (ArrayList<String>) tokens.get("WhereStartsWith");
            query = query.concat(strings.get(0) + "] LIKE '" + strings.get(1) + "%'");
        }

        if(tokens.get("WhereContains") != null) {
            query = query.concat(" WHERE [");
            ArrayList<String> strings = (ArrayList<String>) tokens.get("WhereContains");
            query = query.concat(strings.get(0) + "] LIKE '%" + strings.get(1) + "%'");
        }

        if(tokens.get("GroupBy") != null){
            query = query.concat(" GROUP BY ");
            ArrayList<String> arr = ((ArrayList<String>) tokens.get("GroupBy"));
            for(int i=0;i<arr.size();i++){
                query = query.concat("[" + arr.get(i) + "]");
                if(i!=arr.size()-1)
                    query = query.concat(",");
            }
        }

        if(tokens.get("Having") != null){

            HashMap<String,String> map = ((HashMap<String, String>) tokens.get("Having"));
            query = query.concat(" HAVING " + agr + map.get("operator") + map.get("criteria"));

        }

        if(tokens.get("OrHaving") != null){

            HashMap<String,String> map = ((HashMap<String, String>) tokens.get("OrHaving"));
            query = query.concat(" OR HAVING " + agr + map.get("operator") + map.get("criteria"));

        }

        if(tokens.get("AndHaving") != null){

            HashMap<String,String> map = ((HashMap<String, String>) tokens.get("AndHaving"));
            query = query.concat(" AND HAVING " + agr + map.get("operator") + map.get("criteria"));

        }

        if(tokens.get("WhereInQ") != null) {
            ArrayList<String> strings = (ArrayList<String>) tokens.get("WhereInQ");
            query = query.concat(" WHERE " + strings.get(0) + " IN ");
        }

        //ovo se konkatenira poslednje
        if(tokens.get("OrderBy") != null) {
            String s = (String) tokens.get("OrderBy");
            query = query.concat(" ORDER BY [" + s + "]");
        }

        if(tokens.get("OrderByDesc") != null) {
            String s = (String) tokens.get("OrderByDesc");
            query = query.concat(" ORDER BY [" + s + "] DESC");
        }


        //query = query.concat(";");
        tokens.clear();

        System.out.println(query);
        MainFrame.getInstance().getTextArea2().setText(query);
        //MainFrame.getInstance().getAppCore().executeSQLQuery(query);
        return query;
    }

    private String  concatFilter(String query, String prefix, String type) {

        ArrayList<String> arr = ((ArrayList<String>) tokens.get(type));
        String columnName = arr.get(0);
        String operator = arr.get(1);
        String criteria = arr.get(2);

        try{
            Integer.parseInt(arr.get(2));
        }catch (NumberFormatException e){
            criteria = "'" + arr.get(2) + "'";
        }

        query = query.concat( " " + prefix);
        query = query.concat(" [" + columnName + "]");
        query = query.concat(" " + operator + " ");
        query = query.concat(criteria);

        return query;

    }

    private String deleteCharacters(String part){
        part = part.replace(" ","");
        part = part.replace("\"","");
        part = part.replace(")","");
        part = part.replace("(","");
        part = part.replace("\n","");
        return part;
    }


}

    

