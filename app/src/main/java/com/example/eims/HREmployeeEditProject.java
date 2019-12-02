package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class HREmployeeEditProject extends AppCompatActivity {
    UtilHelper utilHelper;
    int selectedIDRadioButton = 0;
    Bundle bundle;
    String nameAndEmail,employeeID;
    LinearLayout assignedProject;
    RadioGroup unassignedProject;
    EditText searchBox;
    ArrayList<HashMap<String,String>> completeProjectData = new ArrayList<HashMap<String,String>>();
    ArrayList<HashMap<String,String>> completAssignedProjectData = new ArrayList<HashMap<String,String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hremployee_edit_project);
        bundle = getIntent().getExtras();
        nameAndEmail = bundle.getString("name");
        TextView name = findViewById(R.id.nameAndEmail);
        name.setText(nameAndEmail);
        employeeID = bundle.getString("id");
        utilHelper = new UtilHelper(this);
        assignedProject = findViewById(R.id.search_result_scrollProjectAssigned);
        unassignedProject = findViewById(R.id.radioGroupProject);
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

    public void onClickBackButton(View view){finish();}

    public void onclickAdd(View view){
        String idx  = Integer.toString(unassignedProject.getCheckedRadioButtonId());

        for(int i = 0; i <completeProjectData.size(); i++){
            HashMap<String,String> data = completeProjectData.get(i);
            if(idx.equalsIgnoreCase(data.get("project_id"))){
                completAssignedProjectData.add(data);
                completeProjectData.remove(data);

                searchBox.setText("");
                addResultToView("");
                addAssignedResultToView();
                return;
            }
        }
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
                    JSONArray resultProject = output.getJSONArray("unassignedProject");
                    JSONArray resultAssignedProject = output.getJSONArray("assignedProject");
                    if(resultProject.length()>0){
                        for(int i = 0; i<resultProject.length() ; i++){
                            JSONObject jo = resultProject.getJSONObject(i);
                            HashMap<String,String> data = new HashMap<>();
                            data.put("project_id",jo.getString("project_id"));
                            data.put("project_name",jo.getString("project_name"));
                            completeProjectData.add(data);
                        }
                    }
                    if(resultAssignedProject.length()>0){
                        for(int i = 0; i<resultAssignedProject.length() ; i++){
                            JSONObject jo = resultAssignedProject.getJSONObject(i);
                            HashMap<String,String> data = new HashMap<>();
                            data.put("project_id",jo.getString("project_id"));
                            data.put("project_name",jo.getString("project_name"));
                            completAssignedProjectData.add(data);
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
                String res = rh.sendPostRequest(ConfigURL.SearchEmployeeProjectForAdmin, params);
                return res;
            }
        }

        getDataFromDB ae = new getDataFromDB(context);
        ae.execute();
    }


    public void onclickSubmit(View view){
        String input = arrayListToString();
        if(completAssignedProjectData.size()>4){
            utilHelper.createPopUpDialog("Maximum Project Execeeded","The maximum project can be assigned is 4");
        }else{
            submitHREmployee(this,input);
        }
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
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.EditEmployeeProject, params);
                return res;
            }
        }

        insertToDB ae = new insertToDB(context,data);
        ae.execute();
    }

    public void addResultToView(String search){
        unassignedProject.removeAllViews();
        if(search.isEmpty()){
            for(int i = 0; i < completeProjectData.size();i++){
                int radioButtonId = Integer.parseInt(completeProjectData.get(i).get("project_id"));
                RadioButton radioButton = utilHelper.createRadioButton(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,radioButtonId,0,0,0,0,completeProjectData.get(i).get("project_name"));
                unassignedProject.addView(radioButton);
            }
        }
        else{
            for(int i = 0; i < completeProjectData.size();i++){
                int radioButtonId = Integer.parseInt(completeProjectData.get(i).get("project_id"));
                if(completeProjectData.get(i).get("project_name").contains(search)){
                    RadioButton radioButton = utilHelper.createRadioButton(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,radioButtonId,0,0,0,0,completeProjectData.get(i).get("project_name"));
                    unassignedProject.addView(radioButton);
                }
            }
        }
    }

    public void addAssignedResultToView(){
        assignedProject.removeAllViews();
        for(int i = 0; i < completAssignedProjectData.size();i++){
            LinearLayout container = utilHelper.createLinearLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,1f,10f,false,false,10,5,10,0);

            final int idx = i;
            RelativeLayout relativeLayout = utilHelper.createRelativeLayout(false,3f,false);
            ImageView deleteButton = utilHelper.createImageViewOnRelative(R.drawable.ic_delete,100,100);
            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    HashMap<String,String> data = completAssignedProjectData.get(idx);
                    completeProjectData.add(data);
                    completAssignedProjectData.remove(data);

                    String search = searchBox.getText().toString();
                    addResultToView(search);
                    addAssignedResultToView();
                }
            });
            relativeLayout.addView(deleteButton);

            TextView projectName = utilHelper.createTextView(completAssignedProjectData.get(i).get("project_name"),7f);
            container.addView(relativeLayout);
            container.addView(projectName);
            assignedProject.addView(container);
        }
    }

    public String arrayListToString(){
        String result = "";
        for(int count = 0; count < completeProjectData.size() ; count++){
            result += completeProjectData.get(count).get("project_id")+",0";
            if(count!=(completeProjectData.size()+completAssignedProjectData.size())-1){
                result +=";";
            }

        }
        for(int count = 0; count < completAssignedProjectData.size() ; count++){
            result += completAssignedProjectData.get(count).get("project_id")+",1";
            if(count!=(completAssignedProjectData.size())-1){
                result +=";";
            }
        }
        return result;
    }

}
