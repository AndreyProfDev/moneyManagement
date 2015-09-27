package com.moneyManagement.gui.utils;

import com.moneyManagement.exceptions.CValidationException;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CEventQueueProxy extends EventQueue
{
    protected void dispatchEvent(AWTEvent newEvent)
    {
        try
        {
            super.dispatchEvent(newEvent);
        }
        catch (Throwable t)
        {
            if (t instanceof CValidationException)
            {
                CDisplay.warning(t.getLocalizedMessage());
                t.printStackTrace();
                return;
            }
            try
            {
                File logFile = File.createTempFile("money_managing", ".log");

                try (FileWriter writer = new FileWriter(logFile))
                {
                    Throwable cause = t;

                    while (cause != null)
                    {
                        if (cause != t)
                        {
                            writer.append("\n\n");
                            writer.append("caused by: " + cause.getLocalizedMessage()).append("\n");
                        }

                        for (StackTraceElement stackTraceElement : cause.getStackTrace())
                        {
                            writer.append(stackTraceElement.toString()).append("\n");
                        }
                        cause = cause.getCause();
                    }

                    writer.flush();
                }

                CDisplay.error("An unexpected error occured in EDT.\nPlease copy file " +
                        logFile.getAbsolutePath() + " and send it to administrator");
                t.printStackTrace();
            }
            catch (IOException e)
            {
                CDisplay.error("An unexpected error occured in EDT", t);
                e.printStackTrace();
            }


        }
    }

    public void handle(Throwable thrown)
    {
        CDisplay.error("An unexpected error occured in EDT", thrown);
        throw new RuntimeException(thrown);
    }
}
