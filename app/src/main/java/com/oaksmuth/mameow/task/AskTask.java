package com.oaksmuth.mameow.task;

import com.oaksmuth.mameow.Helper;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Oak on 18/3/2559.
 */
public class AskTask extends Thread{

    private String out;
    private URL url;
    private String link;
    private Task task;

    public AskTask(Task task)
    {
        this.task = task;
    }

    @Override
    public void run() {
        HttpURLConnection urlConnection = null;
        //Log.i("ask task", "receive job2");
        try {
            //Log.i("ask task", "receive job3");
            link = "http://oaksmuth.esy.es/select.php?question=" + Helper.reString(task.param);
            url = new URL(link);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            out = IOUtils.toString(in, "UTF-8");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        task.returnMessage = out;
        //Log.i("ask task", "job finished");
    }
}
