package com.example.finalyearproject2;

public class LeaveApplication {
    String id;
    String name;
    String startDate;
    String endDate;
    String leaveType;
    String currentTime;
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LeaveApplication(String id, String name, String startDate, String endDate, String leaveType, String currentTime, String status){
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.leaveType = leaveType;
        this.id = id;
        this.currentTime = currentTime;
        this.status = status;
    }

}
