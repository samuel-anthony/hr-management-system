package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HRProjectMemberEdit extends AppCompatActivity {
    String projectID;
    Bundle bundle;
    UtilHelper utilHelper;
    LinearLayout assignedEmployee,unassignedEmployee;
    ArrayList<HashMap<String,String>> completeEmployeeData = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> completAssignedEmployeeData = new ArrayList<HashMap<String,String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hrproject_member_edit);
        bundle = getIntent().getExtras();
        projectID = bundle.getString("id");
        TextView name = findViewById(R.id.projectName);
        name.setText(projectID + " - " + bundle.getString("name"));
        utilHelper = new UtilHelper(this);
        assignedEmployee = findViewById(R.id.projectMember);
        unassignedEmployee = findViewById(R.id.search_result_scrollProjectAssigned);
        getData(this);
    }

    public void getData(Context context){
        class getDataFromDB extends AsyncTask<Void,Void,String> {
            Context context;
            ProgressDialog loading;
            public getDataFromDB(Context context){
                this.context = context;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context,"Getting employee's data...","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);
                    JSONArray resultEmployee = output.getJSONArray("unassignedEmployee");
                    JSONArray resultAssignedEmployee = output.getJSONArray("assignedEmployee");
                    if(resultAssignedEmployee.length()>0){
                        for(int i = 0; i<resultAssignedEmployee.length() ; i++){
                            JSONObject jo = resultAssignedEmployee.getJSONObject(i);
                            HashMap<String,String> data = new HashMap<>();
                            data.put("employee_id",jo.getString("employee_id"));
                            data.put("employee_name",jo.getString("employee_name"));
                            completAssignedEmployeeData.add(data);
                        }
                    }
                    if(resultEmployee.length()>0){
                        for(int i = 0; i<resultEmployee.length() ; i++){
                            JSONObject jo = resultEmployee.getJSONObject(i);
                            HashMap<String,String> data = new HashMap<>();
                            data.put("employee_id",jo.getString("employee_id"));
                            data.put("employee_name",jo.getString("employee_name"));
                            data.put("action",jo.getString("action"));
                            data.put("id",jo.getString("id"));
                            completeEmployeeData.add(data);
                        }
                    }
                    addResultToView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("project_id",projectID);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.SearcProjectMemberforAdmin, params);
                return res;
            }
        }

        getDataFromDB ae = new getDataFromDB(context);
        ae.execute();
    }

    public void addResultToView(){
        assignedEmployee.removeAllViews();
        unassignedEmployee.removeAllViews();
        for(int i = 0; i < completAssignedEmployeeData.size();i++){

            TextView employeeName = utilHelper.createTextView(completAssignedEmployeeData.get(i).get("employee_id") + " - " +completAssignedEmployeeData.get(i).get("employee_name"));
            assignedEmployee.setBackgroundColor(Color.parseColor("#d6e5fa"));
            assignedEmployee.addView(employeeName);
        }
        for(int i = 0; i < completeEmployeeData.size();i++) {
            final String currentID = completeEmployeeData.get(i).get("id");
            final String empID = completeEmployeeData.get(i).get("employee_id");
            final String actionString = completeEmployeeData.get(i).get("action");
            LinearLayout linearLayout = utilHelper.createLinearLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,1f,10f,false,false,10,5,10,5);
            //ini yang left
            LinearLayout subLinearLayout = utilHelper.createLinearLayout(0,LinearLayout.LayoutParams.MATCH_PARENT,5f,10f,true,false,10,5,10,5);
            TextView employeeName = utilHelper.createTextView(empID + " - " +completeEmployeeData.get(i).get("employee_name"));
            TextView action = utilHelper.createTextView("Action : " + actionString);
            subLinearLayout.addView(employeeName);
            subLinearLayout.addView(action);
            //ini yang right
            LinearLayout subLinearLayout1 = utilHelper.createLinearLayout(0,LinearLayout.LayoutParams.MATCH_PARENT,5f,10f,true,false,10,5,10,5);
            RelativeLayout relativeLayout = utilHelper.createRelativeLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,1f,false,0,0,0,0);
            LinearLayout centerContainer = utilHelper.createLinearLayout(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT,1f,10f,false,false, true,0,0,0,0);
            LinearLayout checkContainer = utilHelper.createLinearLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1f,10f,false,true,5,0,5,0);
            checkContainer.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    submitHREmployee(HRProjectMemberEdit.this,empID,currentID,actionString,"7");
                }
            });
            ImageView checkSign = utilHelper.createImageViewOnLinear(R.drawable.ic_approve,50,50);
            LinearLayout crossContainer = utilHelper.createLinearLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,1f,10f,false,true,5,0,5,0);
            crossContainer.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    submitHREmployee(HRProjectMemberEdit.this,empID,currentID,actionString,"9");
                }
            });
            ImageView crossSign = utilHelper.createImageViewOnLinear(R.drawable.ic_reject,50,50);
            checkContainer.addView(checkSign);
            crossContainer.addView(crossSign);
            centerContainer.addView(checkContainer);
            centerContainer.addView(crossContainer);
            relativeLayout.addView(centerContainer);
            subLinearLayout1.addView(relativeLayout);

            linearLayout.addView(subLinearLayout);
            linearLayout.addView(subLinearLayout1);
            linearLayout.setBackground(getDrawable(R.drawable.rounded_rec));
            linearLayout.setBackgroundColor(Color.parseColor("#eafbea"));
            unassignedEmployee.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#eafbea")));



            unassignedEmployee.addView(linearLayout);
        }
    }

    public void addResultToView(String id,String actionString){
        for(int i = 0; i <completeEmployeeData.size(); i++){
            HashMap<String,String> data = completeEmployeeData.get(i);
            if(id.equalsIgnoreCase(data.get("id"))){
                if(actionString.equalsIgnoreCase("Add"))//ini approve dong
                    completAssignedEmployeeData.add(data);
                completeEmployeeData.remove(data);
                addResultToView();
                return;
            }
        }
    }

    public void onClickBackButton(View view){finish();}

    public void submitHREmployee(Context context,String empID,String id,String actionString,String status){
        class insertToDB extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            Context context;
            String id,actionString,status,empID;
            public insertToDB(Context context,String empID,String id,String actionString,String status){
                this.context = context;
                this.id = id;
                this.actionString = actionString;
                this.status = status;
                this.empID = empID;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context,"Inserting employee's data...","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);
                    if(output.getString("value").equalsIgnoreCase("1")){
                        //sukses submit..
                        utilHelper.createPopUpDialogCloseActivity("Success Message",output.getString("message"));
                        //addResultToView(id,actionString);
                    }
                    else{
                        utilHelper.createPopUpDialog("Ooopsss",output.getString("message"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("empID",empID);
                params.put("id",id);
                params.put("status",status);
                params.put("action_string",actionString);
                params.put("project_id",projectID);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.EditEmployeeProjectMember, params);
                return res;
            }
        }

        insertToDB ae = new insertToDB(context,empID,id,actionString,status);
        ae.execute();
    }


}
