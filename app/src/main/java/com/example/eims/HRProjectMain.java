package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
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

public class HRProjectMain extends AppCompatActivity {
    UtilHelper utilHelper;
    LinearLayout scrollViewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hrproject_main);
        utilHelper  = new UtilHelper(this);
        scrollViewLayout = findViewById(R.id.search_result_scroll);
    }
    public void onClickSearchButton(View view){
        scrollViewLayout.removeAllViews();
        String projectName = ((EditText)findViewById(R.id.projectName)).getText().toString();
        String projectId = ((EditText)findViewById(R.id.projectID)).getText().toString();

        searchProjectData(this,projectId,projectName);
    }


    public void onClickClearButton(View view){
        scrollViewLayout.removeAllViews();
    }

    public void onClickAddNewButton(View view){
        Intent mainActivity = new Intent(this, HREmployeeAdd.class);
        mainActivity.putExtra("sub_menu","add");
        startActivity(mainActivity);
    }


    public void onClickBackButton(View view){
        finish();
    }


    public void searchProjectData(Context context,String projectName, String projectID){
        class searchDB extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            Context context;
            String projectName,projectID;
            public  searchDB (Context context,String projectName,String projectID){
                this.projectName = projectName;
                this.projectID = projectID;
                this.context = context;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(context,"Getting project's data...","Please wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    JSONObject output = new JSONObject(s);

                    JSONArray result = output.getJSONArray("project");
                    if(result.length()>0){
                        for(int i = 0; i<result.length() ; i++){
                            final JSONObject jo = result.getJSONObject(i);
                            LinearLayout container = utilHelper.createLinearLayout(false,false,20.0f,0,5,0,5);
                            //layoutleft
                            LinearLayout leftSubContainer = utilHelper.createLinearLayout(true,true,15.0f,false,true);
                            TextView rowOne = utilHelper.createTextView("Project ID : " + jo.getString("projectId"));
                            TextView rowTwo = utilHelper.createTextView("Project Name : " + jo.getString("projectName"));
                            TextView rowThree = utilHelper.createTextView("PM Name : "+ jo.getString("pmName"));
                            TextView rowFour = utilHelper.createTextView("Project Location : "+ jo.getString("projectLoc"));
                            leftSubContainer.addView(rowOne);
                            leftSubContainer.addView(rowTwo);
                            leftSubContainer.addView(rowThree);
                            leftSubContainer.addView(rowFour);
                            //RelativeRight
                            LinearLayout rightContainer = utilHelper.createLinearLayout(0,LinearLayout.LayoutParams.MATCH_PARENT,5f,10f,true,false,0,0,0,0);
                            RelativeLayout rightSubContainer = utilHelper.createRelativeLayout(LinearLayout.LayoutParams.MATCH_PARENT,0,7f,true,0,0,0,0);
                            ImageView editButton = utilHelper.createImageViewOnRelative(R.drawable.ic_edit,100,100);
                            editButton.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    // Do some job here
                                    Intent detailActivity = new Intent(context, HRProjectAddEdit.class);
                                    try {
                                        detailActivity.putExtra("id",jo.getString("projectId"));
                                        detailActivity.putExtra("sub_menu","edit");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(detailActivity);
                                }
                            });
                            LinearLayout rightSubContainer1 = utilHelper.createLinearLayout(LinearLayout.LayoutParams.MATCH_PARENT,0,3f,10f,false,true,0,10,0,0);
                            TextView memberButton = utilHelper.createTextView("Members");
                            memberButton.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    // Do some job here
                                    Intent detailActivity = new Intent(context, HRProjectMemberEdit.class);
                                    try {
                                        detailActivity.putExtra("id",jo.getString("projectId"));
                                        detailActivity.putExtra("name",jo.getString("projectName"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(detailActivity);
                                }
                            });
                            rightSubContainer1.addView(memberButton);
                            rightSubContainer.addView(editButton);
                            rightContainer.addView(rightSubContainer);
                            rightContainer.addView(rightSubContainer1);
                            container.addView(leftSubContainer);
                            container.addView(rightContainer);
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
                params.put("project_id",projectID);
                params.put("project_name",projectName);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(ConfigURL.SearcProjectforAdmin, params);
                return res;
            }
        }

        searchDB ae = new searchDB(context,projectName,projectID);
        ae.execute();
    }
}
