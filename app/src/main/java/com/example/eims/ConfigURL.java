package com.example.eims;

public class ConfigURL {
    //ini buat constant yang di pake di db nantinya
    //ip wifi arenda =  192.168.0.107
    //ip hape gue = 192.168.43.91
    //ip starbuk tj duren= 10.107.166.52
    //ip kaisar = 192.168.1.134
    private final static String staticIP = "192.168.43.134";
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
    public final static String GetLeaveAndClaimTaskMenu = "http://" + staticIP + "/eims/getLeaveAndClaimTaskMenu.php/";
    public final static String GetDetailLeaveAndClaimTaskMenu = "http://" + staticIP + "/eims/getDetailLeaveAndClaimTaskMenu.php/";
    public final static String UpdateLeaveAndClaimSummary = "http://" + staticIP + "/eims/updateLeaveAndClaimSummary.php/";
    public final static String GetProjectManagerList = "http://" + staticIP + "/eims/getProjectManagerList.php/";
    public final static String GetProjectDataforAllocation = "http://" + staticIP + "/eims/getProjectDataForAllocationPage.php/";
    public final static String GetLeaveDataforAllocation = "http://" + staticIP + "/eims/getLeaveDataForAllocationPage.php/";
    public final static String EditEmployee = "http://" + staticIP + "/eims/editEmployee.php/";
    public final static String EditEmployeeProject = "http://" + staticIP + "/eims/editEmployeeProject.php/";
    public final static String EditEmployeeProjectMember = "http://" + staticIP + "/eims/editEmployeeProjectMember.php/";
    public final static String EditEmployeeLeave = "http://" + staticIP + "/eims/editEmployeeLeave.php/";
    public final static String AddEmployee = "http://" + staticIP + "/eims/addNewEmployee.php/";
    public final static String AddEditProject = "http://" + staticIP + "/eims/addEditNewProject.php/";
    public final static String SearchEmployeeForAdmin = "http://" + staticIP + "/eims/searchEmployeeData.php/";
    public final static String SearchEmployeeLeaveForAdmin = "http://" + staticIP + "/eims/searchEmployeeLeaveForAdmin.php/";
    public final static String SearchEmployeeProjectForAdmin = "http://" + staticIP + "/eims/searchEmployeeProjectForAdmin.php/";
    public final static String SearchEmployeeProjectForPM = "http://" + staticIP + "/eims/searchEmployeeProjectForPM.php/";
    public final static String SearcProjectforAdmin = "http://" + staticIP + "/eims/searchProjectData.php/";
    public final static String SearcProjectMemberforAdmin = "http://" + staticIP + "/eims/searchProjectMemberData.php/";
    public final static String SearchAttendanceforAdmin = "http://" + staticIP + "/eims/searchAttendanceDataForAdmin.php/";
    public final static String UpdatePassword = "http://" + staticIP + "/eims/UpdatePassword.php/";
    public final static String RequestEditEmployeeProject = "http://" + staticIP + "/eims/requestEditEmployeeProject.php/";




}
