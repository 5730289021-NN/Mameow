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
public class TeachTask extends Thread
{
    private String out;
    private URL url;
    private String link;
    private Task task;
    public TeachTask(Task task)
    {
        this.task = task;
    }

    @Override
    public void run() {
        String qa = task.param;
        int sep = task.param.indexOf(":");
        if(sep == -1)
        {
            //Toast.makeText(context,"Please Make sure that you've send the correct syntax... Teach What's your name?:Julong",Toast.LENGTH_LONG).show();
            out = "Please Make sure that you've send the correct syntax, for example Teach What's your name? : Julong";
        }
        else {
            String q = qa.substring(0, sep).trim();
            String a = qa.substring(sep + 1, qa.length()).trim();
            HttpURLConnection urlConnection = null;
            try {
                link = "http://oaksmuth.esy.es/update.php?question=" + Helper.reString(q) + "&answer=" + Helper.reString(a);
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
        }
        task.returnMessage = out;
    }
}
