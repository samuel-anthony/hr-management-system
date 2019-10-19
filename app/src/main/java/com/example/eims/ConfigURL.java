package com.example.eims;

public class ConfigURL {
    //ini buat constant yang di pake di db nantinya
    //ip wifi arenda =  192.168.0.107 | IP Kaisar 2 = 192.168.1.140
    //ip hape gue = 192.168.43.91  | IP Hp Cindy = 192.168.43.134
//    private final static String staticIP = "192.168.43.91";
    private final static String staticIP = "192.168.1.140";
    public final static String Login = "http://" + staticIP + "/eims/login.php/";
    public final static String CheckAttendanceEmployee = "http://" + staticIP + "/eims/checkAttendanceEmployee.php/";
    public final static String CheckProjectEmployee = "http://" + staticIP + "/eims/checkProjectEmployee.php/";
}
