package com.oaksmuth.mameow.task;

/**
 * Created by Oak on 18/3/2559.
 */
public class Task{
    public static final int ASK = 1;
    public static final int TEACH = 2;
    public static final int FEEDBACK = 3;
    public static final int COUNTER = 4;

    public int Task_ID;
    public String param;
    public String returnMessage;
    public Task(int Task_ID,String param)
    {
        this.Task_ID = Task_ID;
        this.param = param;
        returnMessage = "";
    }

    @Override
    public String toString() {
        return "ID: " + Task_ID + ", param: " + param + ", returnMessage: " + returnMessage;
    }
}
