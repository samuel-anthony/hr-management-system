package com.example.eims;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment {
    boolean isMinimumDate;
    String tempDate;
    public DatePickerFragment(String tempDate){
        isMinimumDate = false;
        this.tempDate = tempDate;
    }
    public DatePickerFragment(boolean isMinimumDate, String tempDate){
        this.isMinimumDate = isMinimumDate;
        this.tempDate = tempDate;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int year,month,day;
        if(!tempDate.isEmpty()){
            String[] myTempDate = tempDate.split("-");
            day = Integer.parseInt(myTempDate[0]);
            month = Integer.parseInt(myTempDate[1])-1;
            year = Integer.parseInt(myTempDate[2]);
        }
        else{
            Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
        }
        DatePickerDialog calendarDatePicker =  new DatePickerDialog(getActivity(),(DatePickerDialog.OnDateSetListener) getActivity(), year,month,day);
        if(isMinimumDate){
            calendarDatePicker.getDatePicker().setMinDate(new Date().getTime());
        }
        return calendarDatePicker;
    }
}
