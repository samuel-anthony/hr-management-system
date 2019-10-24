package com.example.eims;

public class ConfigURL {
    //ini buat constant yang di pake di db nantinya
    //ip wifi arenda =  192.168.0.107
    //ip hape gue = 192.168.43.91
    private final static String staticIP = "192.168.0.113";
    public final static String Login = "http://" + staticIP + "/eims/login.php/";
    public final static String CheckAttendanceEmployee = "http://" + staticIP + "/eims/checkAttendanceEmployee.php/";
    public final static String CheckProjectEmployee = "http://" + staticIP + "/eims/checkProjectEmployee.php/";
    public final static String GetLeavePageDataEmployee = "htpp://" + staticIP + "/eims/getLeavePageDataEmployee.php/";
    public final static String SubmitLeaveDataEmployee = "http://" + staticIP + "/eims/submitLeavePageDataEmployee.php/";
    public final static String GetClaimPageDataEmployee = "http://" +staticIP + "/eims/getClaimPageDataEmployee.php/";
}
