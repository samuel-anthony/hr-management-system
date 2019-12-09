package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

public class HRProjectMain extends AppCompatActivity {
    UtilHelper utilHelper;
    LinearLayout scrollViewLayout;
    JSONArray result;
    TextView exporter,export2;
    File pdfFile;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hrproject_main);
        exporter = findViewById(R.id.export);
        exporter.setVisibility(TextView.GONE);
        export2 = findViewById(R.id.export2);
        export2.setVisibility(TextView.GONE);
        utilHelper  = new UtilHelper(this);
        scrollViewLayout = findViewById(R.id.search_result_scroll);
    }
    public void onClickSearchButton(View view){
        scrollViewLayout.removeAllViews();
        String projectName = ((EditText)findViewById(R.id.projectName)).getText().toString();
        String projectId = ((EditText)findViewById(R.id.projectID)).getText().toString();

        searchProjectData(this,projectName,projectId);
    }


    public void onClickClearButton(View view){
        scrollViewLayout.removeAllViews();
    }

    public void onClickAddNewButton(View view){
        Intent mainActivity = new Intent(this, HRProjectAddEdit.class);
        mainActivity.putExtra("sub_menu","add");
        mainActivity.putExtra("id","");
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

                    result = output.getJSONArray("project");
                    if(result.length()>0){
                        exporter.setVisibility(TextView.VISIBLE);
                        export2.setVisibility(TextView.VISIBLE);
                        for(int i = 0; i<result.length() ; i++){
                            final JSONObject jo = result.getJSONObject(i);
                            LinearLayout container = utilHelper.createLinearLayout(false,false,20.0f,0,5,0,5);
                            //layoutleft
                            LinearLayout leftSubContainer = utilHelper.createLinearLayout(true,true,15.0f,false,true);
                            TextView rowOne = utilHelper.createTextView("Project ID : " + jo.getString("projectId"));
                            TextView rowTwo = utilHelper.createTextView("Project Name : " + jo.getString("projectName"));
                            TextView rowThree = utilHelper.createTextView("PM Name : "+ jo.getString("pmName"));
                            TextView rowFour = utilHelper.createTextView( jo.getString("projectLoc"));
                            TextView rowFive = utilHelper.createTextView( "Request : "+ jo.getString("request"));
                            leftSubContainer.addView(rowOne);
                            leftSubContainer.addView(rowTwo);
                            leftSubContainer.addView(rowThree);
                            leftSubContainer.addView(rowFour);
                            leftSubContainer.addView(rowFive);
                            leftSubContainer.setBackground(getDrawable(R.drawable.rounded_rec));
                            if (i % 2 == 0){
                                leftSubContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#d6e5fa")));
                            }else{
                                leftSubContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#eafbea")));
                            }
                            //RelativeRight
                            LinearLayout rightContainer = utilHelper.createLinearLayout(0,LinearLayout.LayoutParams.MATCH_PARENT,5f,10f,true,false,0,0,0,0);
                            RelativeLayout rightSubContainer = utilHelper.createRelativeLayout(LinearLayout.LayoutParams.MATCH_PARENT,0,7f,true,0,0,0,0);
                            ImageView editButton = utilHelper.createImageViewOnRelative(R.drawable.ic_edit,60,60);
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
                            rightSubContainer1.setBackground(getDrawable(R.drawable.rounded_rec));
                            rightSubContainer1.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f1d6ab")));

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


    public void exportExcel(View view){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            return;
        }

        Workbook wb=new HSSFWorkbook();
        Cell cell=null;
        Sheet sheet =null;
        sheet = wb.createSheet("Project Data");
        Font headerfont = wb.createFont();
        headerfont.setBold(true);
        headerfont.setFontHeightInPoints((short)14);

        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFont(headerfont);

        String[] header ={"Project ID", "Project Name","Project Manager","Project Location"};
        Row headerRow = sheet.createRow(0);
        for(int i = 0; i<header.length ; i++){
            cell = headerRow.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(headerStyle);
        }

        try {
            if(result.length()>0){
                for(int i = 0; i<=result.length() ; i++) {
                    final JSONObject jo = result.getJSONObject(i);
                    Row row = sheet.createRow(i+1);
                    cell = row.createCell(0);
                    cell.setCellValue(jo.getString("projectId"));
                    cell = row.createCell(1);
                    cell.setCellValue(jo.getString("projectName"));
                    cell = row.createCell(2);
                    cell.setCellValue(jo.getString("pmName"));
                    cell = row.createCell(3);
                    cell.setCellValue(jo.getString("projectLoc"));
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String filename = "Project Data - " + utilHelper.getTimeStamp() + ".xls" ;
        File file = new File(getExternalFilesDir(null),filename);
        FileOutputStream outputStream =null;

        try {
            outputStream=new FileOutputStream(file);
            wb.write(outputStream);
            Toast.makeText(getApplicationContext(),"File Exported Successfully",Toast.LENGTH_LONG).show();
        } catch (java.io.IOException e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(),"Upss.. ",Toast.LENGTH_LONG).show();
            try {
                outputStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public void createPdf(View view) throws FileNotFoundException, DocumentException {

        File docsFolder = new File(Environment.getExternalStorageDirectory()+"/Documents");
        if(!docsFolder.exists()){
            docsFolder.mkdir();
            //Log.i(TAG,"Created a new directory for PDF");
        }
        String pdfname = "Project Data - " + utilHelper.getTimeStamp() + ".pdf" ;
        pdfFile = new File(docsFolder.getAbsolutePath(),pdfname);
        OutputStream output = new FileOutputStream(pdfFile);
        Document document =new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(new float[]{3,3,3,3});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(50);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        String[] header ={"Project ID", "Project Name","Project Manager","Project Location"};
        for(int i = 0; i<header.length ; i++){
            table.addCell(header[i]);
        }
        table.setHeaderRows(1);
        PdfPCell[] cells = table.getRow(0).getCells();
        for (int j = 0; j < cells.length; j++) {
            cells[j].setBackgroundColor(BaseColor.GRAY);
        }
        try {
            if(result.length()>0){
                for(int i = 0; i<=result.length() ; i++) {
                    final JSONObject jo = result.getJSONObject(i);
                    table.addCell(jo.getString("projectId"));
                    table.addCell(jo.getString("projectName"));
                    table.addCell(jo.getString("pmName"));
                    table.addCell(jo.getString("projectLoc"));
                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        PdfWriter.getInstance(document,output);
        document.open();
        document.add(new Paragraph("Project Data \n\n"));
        document.add(table);
        document.close();
        Toast.makeText(getApplicationContext(),"File Exported Successfully",Toast.LENGTH_LONG).show();

    }




}
