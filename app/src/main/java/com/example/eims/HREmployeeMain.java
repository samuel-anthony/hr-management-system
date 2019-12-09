package com.example.eims;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Time;
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

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
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
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class HREmployeeMain extends AppCompatActivity {
    UtilHelper utilHelper;
    LinearLayout scrollViewLayout;
    JSONArray result;
    TextView exporter, export2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hremployee_main);
        exporter = findViewById(R.id.export);
        exporter.setVisibility(TextView.GONE);
        export2 = findViewById(R.id.export2);
        export2.setVisibility(TextView.GONE);
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

                    result = output.getJSONArray("employee");
                    if(result.length()>0){
                        exporter.setVisibility(TextView.VISIBLE);
                        export2.setVisibility(TextView.VISIBLE);
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
                            leftSubContainer.setBackground(getDrawable(R.drawable.rounded_rec));
                            if (i % 2 == 0){
                                leftSubContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#d6e5fa")));
                            }else{
                                leftSubContainer.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#eafbea")));
                            }
                            //RelativeRight
                            LinearLayout rightSubContainer = utilHelper.createLinearLayout(0,LinearLayout.LayoutParams.MATCH_PARENT,5f,10f,true,false,5,0,5,0);
                            LinearLayout firstRow;
                            if(jo.getString("isUser").equalsIgnoreCase("0"))
                                firstRow = utilHelper.createLinearLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT,3f,10f,false,false,0,5,0,0);
                            else
                                firstRow = utilHelper.createLinearLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,3f,10f,false,false,0,5,0,0);
                            RelativeLayout subFirstRow = utilHelper.createRelativeLayout(false);
                            LinearLayout secondRow = utilHelper.createLinearLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,3f,10f,false,false,0,5,0,0);
                            LinearLayout thirdRow = utilHelper.createLinearLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,3f,10f,false,false,0,5,0,0);

                            ImageView editButton = utilHelper.createImageViewOnRelative(R.drawable.ic_edit,60,60);
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

                            rightSubContainer.addView(firstRow);
                            if(jo.getString("isUser").equalsIgnoreCase("1")){
                                TextView leaveText = utilHelper.createTextView("Leave");
                                secondRow.addView(leaveText);
                                secondRow.setBackground(getDrawable(R.drawable.rounded_rec));
                                secondRow.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f1d6ab")));
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
                                thirdRow.setBackground(getDrawable(R.drawable.rounded_rec));
                                thirdRow.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fa877f")));
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

                                rightSubContainer.addView(secondRow);
                                rightSubContainer.addView(thirdRow);
                            }

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
        sheet = wb.createSheet("Employee Data");
        Font headerfont = wb.createFont();
        headerfont.setBold(true);
        headerfont.setFontHeightInPoints((short)14);

        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFont(headerfont);

        String[] header ={"ID", "Name","Email","Address" ,"Gender","Hired Date","PM Flag"};
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
                    cell.setCellValue(jo.getString("empID"));
                    cell = row.createCell(1);
                    cell.setCellValue(jo.getString("name"));
                    cell = row.createCell(2);
                    cell.setCellValue(jo.getString("email"));
                    cell = row.createCell(3);
                    cell.setCellValue(jo.getString("address"));
                    cell = row.createCell(4);
                    cell.setCellValue(jo.getString("gender"));
                    cell = row.createCell(5);
                    cell.setCellValue(jo.getString("hired_date"));
                    cell = row.createCell(6);
                    cell.setCellValue(jo.getString("isPM"));                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }

        String filename = "Employee Data - " + utilHelper.getTimeStamp() + ".xls" ;
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
        String pdfname = "Employee Data - " + utilHelper.getTimeStamp() + ".pdf" ;
        File pdfFile = new File(docsFolder.getAbsolutePath(),pdfname);
        OutputStream output = new FileOutputStream(pdfFile);
        Document document =new Document(PageSize.A4);
        PdfPTable table = new PdfPTable(new float[]{3,3,3,3,3,3,3});
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        table.getDefaultCell().setFixedHeight(50);
        table.setTotalWidth(PageSize.A4.getWidth());
        table.setWidthPercentage(100);
        table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        String[] header ={"ID", "Name","Email","Address" ,"Gender","Hired Date","PM Flag"};
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
                    table.addCell(jo.getString("empID"));
                    table.addCell(jo.getString("name"));
                    table.addCell(jo.getString("email"));
                    table.addCell(jo.getString("address"));
                    table.addCell(jo.getString("gender"));
                    table.addCell(jo.getString("hired_date"));
                    table.addCell(jo.getString("isPM"));

            }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        PdfWriter.getInstance(document,output);
        document.open();
        document.add(new Paragraph("Employee Data \n\n"));
        document.add(table);
        document.close();
        Toast.makeText(getApplicationContext(),"File Exported Successfully",Toast.LENGTH_LONG).show();

    }

}