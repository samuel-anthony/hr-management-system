package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HREmployeeEditLeave extends AppCompatActivity {
    String nameAndEmail,employeeID;
    Bundle bundle;
    LinearLayout searchResult;
    UtilHelper utilHelper;
    boolean firstTimeCheckBoxClicked = true;
    ArrayList<HashMap<String,String>> completeLeaveData = new ArrayList<HashMap<String,String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hremployee_edit_leave);
        bundle = getIntent().getExtras();
        nameAndEmail = bundle.getString("name");
        employeeID = bundle.getString("id");
        TextView employee_data =  findViewById(R.id.nameAndEmail);
        employee_data.setText(nameAndEmail);

        searchResult = findViewById(R.id.search_result_scroll);
        utilHelper = new UtilHelper(this);
        searchEmployeeData(this);

        CheckBox checkBox = findViewById(R.id.checkboxIsPM);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if(isChecked){
                    if(firstTimeCheckBoxClicked)
                        utilHelper.createPopUpDialog("PM is checked","By Checking the PM, then this employee will automatically become user(not admin)");
                    firstTimeCheckBoxClicked = false;
                    ((CheckBox)findViewById(R.id.checkboxIsUser)).setChecked(true);
                }
            }
        });
    }

    public void onClickBackButton(View view){finish();}

    public void onclickSubmit(View view){
        String input = arrayListToString(completeLeaveData);
        if(!input.equalsIgnoreCase("1"))
            submitHREmployee(this,input);
    }
    public void searchEmployeeData(Context context){
        class searchDB extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            Context context;
            public  searchDB (Context context){
                this.context = context;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context ,"Getting employee's data...","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);

                    JSONArray result = output.getJSONArray("leave");
                    if(result.length()>0){
                        for(int i = 0; i<result.length() ; i++){
                            final JSONObject jo = result.getJSONObject(i);
                            LinearLayout container = utilHelper.createLinearLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,1f,10f,false,true,10,10,10,10);
                            //layoutleft
                            int idleaveku = (Integer.parseInt(jo.getString("leave_id"))-1);
                            LinearLayout subContainer = utilHelper.createLinearLayout(0,LinearLayout.LayoutParams.WRAP_CONTENT,2f,10f,false,false,0,0,0,0);
                            CheckBox checkBox = utilHelper.createCheckBox(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT,idleaveku,10,0,10,0);
                            if(jo.getString("is_avail").equalsIgnoreCase("1")) {
                                checkBox.setChecked(true);
                            }
                            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                            {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                                {
                                    int id = buttonView.getId();
                                    HashMap<String,String> data = completeLeaveData.get(id);
                                    if(isChecked){
                                        data.put("is_checked","1");
                                    }
                                    else{
                                        data.put("is_checked","0");
                                    }
                                    completeLeaveData.set(id,data);
                                }
                            });
                            subContainer.addView(checkBox);
                            //layoutright
                            LinearLayout subContainer2 = utilHelper.createLinearLayout(0,LinearLayout.LayoutParams.WRAP_CONTENT,8f,10f,true,false,0,0,0,0);
                            TextView leaveType = utilHelper.createTextView(jo.getString("leave_name"));
                            LinearLayout containerData = utilHelper.createLinearLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,1f,10f,false,false,0,0,0,0);
                            TextView leaveDuration = utilHelper.createTextView("Leave Duration",7f);
                            EditText leaveAmount = utilHelper.createEditText(0,LinearLayout.LayoutParams.WRAP_CONTENT,3f,0,0,0,0);
                            leaveAmount.setText(jo.getString("leave_balance"));
                            leaveAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
                            int idEditTextLeave = idleaveku+100;
                            leaveAmount.setId(idEditTextLeave);
                            containerData.addView(leaveDuration);
                            containerData.addView(leaveAmount);
                            subContainer2.addView(leaveType);
                            subContainer2.addView(containerData);
                            container.addView(subContainer);
                            container.addView(subContainer2);
                            searchResult.addView(container);
                            HashMap<String,String> data = new HashMap<>();
                            data.put("leave_id",jo.getString("leave_id"));
                            data.put("leave_balance",jo.getString("leave_balance"));
                            data.put("is_checked",jo.getString("is_avail"));
                            completeLeaveData.add(data);

                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("employee_id",employeeID);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.SearchEmployeeLeaveForAdmin, params);
                return res;
            }
        }

        searchDB ae = new searchDB(context);
        ae.execute();
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
                params.put("data",data);
                params.put("employee_id",employeeID);
                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.EditEmployeeLeave, params);
                return res;
            }
        }

        insertToDB ae = new insertToDB(context,data);
        ae.execute();
    }

    public String arrayListToString(ArrayList<HashMap<String,String>> a){
        String result = "";
        for(int count = 0; count < a.size() ; count++){
            EditText input = findViewById(100+count);
            String temp = input.getText().toString();
            if(!temp.isEmpty()){
                int leaveBalance = Integer.parseInt(temp);
                result += a.get(count).get("leave_id")+","+leaveBalance+","+a.get(count).get("is_checked");
                if(count!=a.size()-1){
                    result +=";";
                }
            }
            else{
                input.setError("please fill this field");
                return "1";
            }
        }
        return result;
    }

}
