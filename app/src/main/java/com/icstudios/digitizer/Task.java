package com.icstudios.digitizer;

import java.io.Serializable;
import java.util.ArrayList;

public class Task implements Serializable {
    String taskName, task;
    int taskId;
    Boolean isDone;
    String extraDetail;
    ArrayList<String> result;

    public Task(){

    }

    public Task(String taskName, String task, int taskId, Boolean isDone, String extraDetail)
    {
        setTaskName(taskName);
        setTask(task);
        setTaskId(taskId);
        this.isDone = isDone;
        this.extraDetail = extraDetail;
    }

    public Task(String taskName, String task, int taskId, Boolean isDone)
    {
        setTaskName(taskName);
        setTask(task);
        setTaskId(taskId);
        this.isDone = isDone;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setDone(Boolean isDone, ArrayList<String> result) {
        this.isDone = isDone;
        this.result = result;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public void setResult(ArrayList<String> result) {
        this.result = result;
    }

    public void setExtraDetail(String extraDetail)
    {
        this.extraDetail = extraDetail;
    }


    public Boolean getDone() {
        return isDone;
    }

    public int getTaskId() {
        return taskId;
    }

    public String getTask() {
        return task;
    }

    public String getTaskName() {
        return taskName;
    }

    public ArrayList<String> getResult() {
        return result;
    }

    public String getExtraDetail() {
        return extraDetail;
    }
}
