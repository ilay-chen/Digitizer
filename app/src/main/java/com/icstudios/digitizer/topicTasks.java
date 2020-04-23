package com.icstudios.digitizer;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class topicTasks implements Serializable {
    ArrayList<Task> tasks;
    Calendar scheduledDate;
    long time;
    String title, subtitle;
    String id;
    Boolean toDo;

    public topicTasks(){
        tasks = new ArrayList<Task>();
        scheduledDate = Calendar.getInstance();
    }

    public topicTasks(String[] task, String [] text, String [] extra, int weeks, String title, String subtitle, String id, Boolean toDo){
        tasks = new ArrayList<Task>();
        for(int i = 0; i < task.length; i++)
        {
            this.tasks.add(new Task(task[i],text[i],i,false, extra[i]));
        }
        scheduledDate = Calendar.getInstance();
        scheduledDate.add(Calendar.WEEK_OF_YEAR, weeks);
        setScheduledDate(scheduledDate);
        this.title = title;
        this.subtitle = subtitle;
        this.id = id;
        this.toDo = toDo;
    }

    public topicTasks(String[] task, String [] text, int weeks, String title, String id){
        tasks = new ArrayList<Task>();
        for(int i = 0; i < task.length; i++)
        {
            this.tasks.add(new Task(task[i],text[i],i,false));
        }
        scheduledDate = Calendar.getInstance();
        scheduledDate.add(Calendar.WEEK_OF_YEAR, weeks);
        setScheduledDate(scheduledDate);
        this.title = title;
        this.id = id;
    }

    public topicTasks(ArrayList<Task> tasks, Calendar scheduledDate, String title, String id)
    {
        this.tasks = tasks;
        this.scheduledDate = scheduledDate;
        setScheduledDate(scheduledDate);
        this.id = id;
        this.title = title;
    }

    public void delayDate(int daysToAdd)
    {
        scheduledDate.add(Calendar.DAY_OF_YEAR, daysToAdd);
        setScheduledDate(scheduledDate);
    }

    public void setDone(int taskId, Boolean done, ArrayList<String> result)
    {
        tasks.get(taskId).setDone(done, result);
    }

    public ArrayList<String> getAllNames()
    {
        ArrayList<String> names = new ArrayList<>();
        for(int i = 0; i < tasks.size(); i++){
            names.add(tasks.get(i).getTaskName());
        }
        return names;
    }

    public int undoneTasks()
    {
        int count = 0;
        for(int i = 0; i < tasks.size(); i++)
            if(!tasks.get(i).isDone) count++;

        return count;
    }

    public void updateTask(String []Task, String []taskName, String []extraDetail)
    {
        for(int i = 0; i < tasks.size(); i++)
        {
            tasks.get(i).setTask(Task[i]);
            tasks.get(i).setTaskName(taskName[i]);
            tasks.get(i).setExtraDetail(extraDetail[i]);
        }
    }

    public void setId(String id)
    {
        this.id = id;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public void setSubtitle(String subtitle)
    {
        this.subtitle = subtitle;
    }

    @Exclude
    public void setScheduledDate(Calendar scheduledDate)
    {
        this.scheduledDate = scheduledDate;
        time = scheduledDate.getTime().getTime();
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    @Exclude
    public Calendar getScheduledDate() {
        long timestampLong = time;
        Date d = new Date(timestampLong);
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.setTimeZone(TimeZone.getDefault());
        scheduledDate = c;
        return scheduledDate;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public String getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Boolean getToDo() {
        return toDo;
    }

    public void setToDo(Boolean toDo) {
        this.toDo = toDo;
    }
}
