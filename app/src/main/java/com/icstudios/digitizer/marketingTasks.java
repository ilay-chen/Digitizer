package com.icstudios.digitizer;

import java.util.ArrayList;
import java.util.Calendar;
import java.io.Serializable;

public class marketingTasks implements Serializable{
    ArrayList<topicTasks> allTopics;

    public marketingTasks(){
        allTopics = new ArrayList<topicTasks>();
    }

    public ArrayList<topicTasks> getAllTopics() {
        return allTopics;
    }

    public void setAllTopics(ArrayList<topicTasks> allTopics) {
        this.allTopics = allTopics;
    }

    public marketingTasks (ArrayList<topicTasks> allTopics)
    {
        this.allTopics = allTopics;
    }

    public void addTopic(topicTasks topic)
    {
        allTopics.add(topic);
    }

    public topicTasks getTopicById(String id)
    {
        for(int i = 0; i < allTopics.size(); i++)
            if(allTopics.get(i).id.equals(id))
                return allTopics.get(i);
        return null;
    }

    public int getTopicNumById(String id){
        for(int i = 0; i < allTopics.size(); i++)
            if(allTopics.get(i).id.equals(id))
                return i;
        return 0;
    }

    public void delayDate(int pos, Calendar calendar)
    {
        allTopics.get(pos).setScheduledDate(calendar);
        pos += 1;
        int weeks = 1;
        for(int i = pos; i < allTopics.size(); i++)
        {
            if(allTopics.get(i).getToDo())
            {
                calendar.add(Calendar.WEEK_OF_YEAR, weeks);
                allTopics.get(i).setScheduledDate(calendar);
            }
        }
    }

    public void setDone(int topicId, int taskId, Boolean done, ArrayList<String> result)
    {
        allTopics.get(topicId).setDone(taskId, done, result);
    }

    /*
    public List<ArrayList<String>> getAllTasksNames()
    {
        List<ArrayList<String>> names = new ArrayList<ArrayList<String>>();
        for(int i = 0; i < allTopics.size(); i++){
            names.add(allTopics.get(i).getAllNames());
        }
        return names;
    }
*/
    public void updateStrings(String Id, String title, String subtitle, int topicId, String []Task, String []taskName, String []extraDetail, Boolean toDo)
    {
        if(allTopics.get(topicId).tasks.size() != Task.length) {
            allTopics.add(topicId, new topicTasks(Task, taskName, extraDetail, topicId+1, title, subtitle, Id, toDo));
        }
            allTopics.get(topicId).setId(Id);
            allTopics.get(topicId).setTitle(title);
            allTopics.get(topicId).setSubtitle(subtitle);
            allTopics.get(topicId).updateTask(Task, taskName, extraDetail);
    }
}
