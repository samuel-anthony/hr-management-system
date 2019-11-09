package com.example.eims;

public class ConfigURL {
    //ini buat constant yang di pake di db nantinya
    //ip wifi arenda =  192.168.0.107
    //ip hape gue = 192.168.43.91
    //ip starbuk tj duren= 10.107.166.52
    private final static String staticIP = "192.168.0.107";
    public final static String Login = "http://" + staticIP + "/eims/login.php/";
    public final static String CheckAttendanceEmployee = "http://" + staticIP + "/eims/checkAttendanceEmployee.php/";
    public final static String CheckProjectEmployee = "http://" + staticIP + "/eims/checkProjectEmployee.php/";
    public final static String GetLeavePageDataEmployee = "http://" + staticIP + "/eims/getLeavePageDataEmployee.php/";
    public final static String SubmitLeaveDataEmployee = "http://" + staticIP + "/eims/submitLeavePageDataEmployee.php/";
    public final static String GetClaimPageDataEmployee = "http://" +staticIP + "/eims/getClaimPageDataEmployee.php/";
    public final static String SubmitClaimDataEmployee = "http://" + staticIP + "/eims/submitClaimPageDataEmployee.php/";
    public final static String SearchAttendanceDataEmployee = "http://" + staticIP + "/eims/searchAttendanceDataEmployee.php/";
    public final static String SearchLeaveDataEmployee = "http://" + staticIP + "/eims/searchLeaveDataEmployee.php/";
    public final static String SearchClaimDataEmployee = "http://" + staticIP + "/eims/searchClaimDataEmployee.php/";
    public final static String GetTypeAndStatusReportMenu = "http://" + staticIP + "/eims/getTypeAndStatusReportMenu.php/";
}
