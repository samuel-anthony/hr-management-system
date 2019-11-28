package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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

public class TaskProject extends AppCompatActivity {
    UtilHelper utilHelper;
    Bundle bundle;
    JSONObject output;
    String employeeID,projectID;
    LinearLayout assignedEmployee;
    RadioGroup unassignedEmployee;
    EditText searchBox;
    ArrayList<HashMap<String,String>> completeEmployeeData = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> completAssignedEmployeeData = new ArrayList<HashMap<String,String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_project);
        bundle = getIntent().getExtras();
        try {
            output = new JSONObject(bundle.getString("employee_data"));
            employeeID = output.getString("employee_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        utilHelper = new UtilHelper(this);
        assignedEmployee = findViewById(R.id.search_result_scrollProjectAssigned);
        unassignedEmployee = findViewById(R.id.radioGroupProject);
        searchBox = findViewById(R.id.search_bar);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchText = searchBox.getText().toString();
                addResultToView(searchText);
            }
        });
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
                    projectID = output.getString("project_id");
                    ((TextView)findViewById(R.id.projectNameAndId)).setText(output.getString("project_name")+" - "+projectID);
                    if(resultEmployee.length()>0){
                        for(int i = 0; i<resultEmployee.length() ; i++){
                            JSONObject jo = resultEmployee.getJSONObject(i);
                            HashMap<String,String> data = new HashMap<>();
                            data.put("employee_id",jo.getString("employee_id"));
                            data.put("employee_name",jo.getString("employee_name"));
                            completeEmployeeData.add(data);
                        }
                    }
                    if(resultAssignedEmployee.length()>0){
                        for(int i = 0; i<resultAssignedEmployee.length() ; i++){
                            JSONObject jo = resultAssignedEmployee.getJSONObject(i);
                            HashMap<String,String> data = new HashMap<>();
                            data.put("employee_id",jo.getString("employee_id"));
                            data.put("employee_name",jo.getString("employee_name"));
                            completAssignedEmployeeData.add(data);
                        }
                    }
                    addResultToView("");
                    addAssignedResultToView();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("employee_id",employeeID);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.SearchEmployeeProjectForPM, params);
                return res;
            }
        }

        getDataFromDB ae = new getDataFromDB(context);
        ae.execute();
    }


    public void onclickAdd(View view){
        String idx  = Integer.toString(unassignedEmployee.getCheckedRadioButtonId());

        for(int i = 0; i <completeEmployeeData.size(); i++){
            HashMap<String,String> data = completeEmployeeData.get(i);
            if(idx.equalsIgnoreCase(data.get("employee_id"))){
                completAssignedEmployeeData.add(data);
                completeEmployeeData.remove(data);

                searchBox.setText("");
                addResultToView("");
                addAssignedResultToView();
                return;
            }
        }
    }

    public void onclickSubmit(View view){
        String input = arrayListToString();
        submitHREmployee(this,input);
    }


    public void submitHREmployee(Context context,String data){
        class insertToDB extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            Context context;
            String data;
            public insertToDB(Context context,String data){
                this.context = context;
                this.data = data;
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
                params.put("data",arrayListToString());
                params.put("employee_id",employeeID);
                params.put("project_id",projectID);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.RequestEditEmployeeProject, params);
                return res;
            }
        }

        insertToDB ae = new insertToDB(context,data);
        ae.execute();
    }


    public void addResultToView(String search){
        unassignedEmployee.removeAllViews();
        if(search.isEmpty()){
            for(int i = 0; i < completeEmployeeData.size();i++){
                int radioButtonId = Integer.parseInt(completeEmployeeData.get(i).get("employee_id"));
                RadioButton radioButton = utilHelper.createRadioButton(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,radioButtonId,0,0,0,0,completeEmployeeData.get(i).get("employee_name"));
                unassignedEmployee.addView(radioButton);
            }
        }
        else{
            for(int i = 0; i < completeEmployeeData.size();i++){
                int radioButtonId = Integer.parseInt(completeEmployeeData.get(i).get("employee_id"));
                if(completeEmployeeData.get(i).get("employee_name").contains(search)){
                    RadioButton radioButton = utilHelper.createRadioButton(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,radioButtonId,0,0,0,0,completeEmployeeData.get(i).get("employee_name"));
                    unassignedEmployee.addView(radioButton);
                }
            }
        }
    }

    public void addAssignedResultToView(){
        assignedEmployee.removeAllViews();
        for(int i = 0; i < completAssignedEmployeeData.size();i++){
            LinearLayout container = utilHelper.createLinearLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,1f,10f,false,false,10,5,10,0);

            final int idx = i;
            RelativeLayout relativeLayout = utilHelper.createRelativeLayout(false,3f,false);
            ImageView deleteButton = utilHelper.createImageViewOnRelative(R.drawable.ic_delete_black_24dp,100,100);
            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    HashMap<String,String> data = completAssignedEmployeeData.get(idx);
                    completeEmployeeData.add(data);
                    completAssignedEmployeeData.remove(data);

                    String search = searchBox.getText().toString();
                    addResultToView(search);
                    addAssignedResultToView();
                }
            });
            relativeLayout.addView(deleteButton);

            TextView employeeName = utilHelper.createTextView(completAssignedEmployeeData.get(i).get("employee_name"),7f);
            container.addView(relativeLayout);
            container.addView(employeeName);
            assignedEmployee.addView(container);
        }
    }

    public String arrayListToString(){
        String result = "";
        for(int count = 0; count < completeEmployeeData.size() ; count++){
            result += completeEmployeeData.get(count).get("employee_id")+",0";
            if(count!=(completeEmployeeData.size()+completeEmployeeData.size())-1){
                result +=";";
            }

        }
        for(int count = 0; count < completAssignedEmployeeData.size() ; count++){
            result += completAssignedEmployeeData.get(count).get("employee_id")+",1";
            if(count!=(completAssignedEmployeeData.size())-1){
                result +=";";
            }
        }
        return result;
    }
}
