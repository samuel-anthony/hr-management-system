package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class HREmployeeMain extends AppCompatActivity {
    UtilHelper utilHelper;
    LinearLayout scrollViewLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hremployee_main);

        utilHelper  = new UtilHelper(this);
        scrollViewLayout = findViewById(R.id.search_result_scroll);
        scrollViewLayout.removeAllViews();
    }

    public void onClickAddNewButton(View view){
        Intent mainActivity = new Intent(this, HREmployeeAdd.class);
        startActivity(mainActivity);
    }
    public void onClickBackButton(View view){
        finish();
    }


    public void onClickSearchButton(View view){
        scrollViewLayout.removeAllViews();
        String employeeName = ((EditText)findViewById(R.id.name)).getText().toString();
        String employeeID = ((EditText)findViewById(R.id.employeeID)).getText().toString();

        searchEmployeeData(employeeName,employeeID);
    }


    public void onClickClearButton(View view){
        scrollViewLayout.removeAllViews();
    }
    public void searchEmployeeData(String employeeName, String employeeID){
        class searchDB extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            String employeeName,employeeID;
            public  searchDB (String employeeName,String employeeID){
                this.employeeName = employeeName;
                this.employeeID = employeeID;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(HREmployeeMain.this,"Getting employee's data...","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);

                    JSONArray result = output.getJSONArray("employee");
                    if(result.length()>0){
                        for(int i = 0; i<result.length() ; i++){
                            final JSONObject jo = result.getJSONObject(i);
                            LinearLayout container = utilHelper.createLinearLayout(false,false,20.0f,0,5,0,5);
                            //layoutleft
                            LinearLayout leftSubContainer = utilHelper.createLinearLayout(true,true,15.0f,false,true);
                            TextView rowOne = utilHelper.createTextView(jo.getString("empID") + " - " + jo.getString("name"));
                            TextView rowTwo = utilHelper.createTextView(jo.getString("email"));
                            TextView rowThree = utilHelper.createTextView("Is PM : "+ jo.getString("isPM"));
                            leftSubContainer.addView(rowOne);
                            leftSubContainer.addView(rowTwo);
                            leftSubContainer.addView(rowThree);
                            //RelativeRight
                            LinearLayout rightSubContainer = utilHelper.createLinearLayout(true,false,10.0f,5.0f,false);
                            LinearLayout firstRow = utilHelper.createLinearLayout(false,false);
                            RelativeLayout subFirstRow = utilHelper.createRelativeLayout(false);
                            LinearLayout secondRow = utilHelper.createLinearLayout(false,true);
                            LinearLayout thirdRow = utilHelper.createLinearLayout(false,true);

                            ImageView editButton = utilHelper.createImageViewOnRelative(R.drawable.ic_edit_black_24dp,100,100);
                            editButton.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    // Do some job here
                                    Intent detailActivity = new Intent(HREmployeeMain.this, HREmployeeEdit.class);
                                    try {
                                        detailActivity.putExtra("id",jo.getString("empID"));
                                        detailActivity.putExtra("name",jo.getString("name"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(detailActivity);
                                }
                            });
                            subFirstRow.addView(editButton);
                            firstRow.addView(subFirstRow);

                            TextView leaveText = utilHelper.createTextView("Leave");
                            secondRow.addView(leaveText);
                            secondRow.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    // Do some job here
                                    Intent detailActivity = new Intent(HREmployeeMain.this, HREmployeeEditLeave.class);
                                    try {
                                        detailActivity.putExtra("id",jo.getString("empID"));
                                        detailActivity.putExtra("name",jo.getString("name"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(detailActivity);
                                }
                            });

                            TextView projecText = utilHelper.createTextView("Project");
                            thirdRow.addView(projecText);
                            thirdRow.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    // Do some job here
                                    Intent detailActivity = new Intent(HREmployeeMain.this, HREmployeeEditProject.class);
                                    try {
                                        detailActivity.putExtra("id",jo.getString("empID"));
                                        detailActivity.putExtra("name",jo.getString("name"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(detailActivity);
                                }
                            });
                            rightSubContainer.addView(firstRow);
                            rightSubContainer.addView(secondRow);
                            rightSubContainer.addView(thirdRow);

                            container.addView(leftSubContainer);
                            container.addView(rightSubContainer);
                            scrollViewLayout.addView(container);
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
                params.put("employee_name",employeeName);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.SearchEmployeeForAdmin, params);
                return res;
            }
        }

        searchDB ae = new searchDB(employeeName,employeeID);
        ae.execute();
    }

}
