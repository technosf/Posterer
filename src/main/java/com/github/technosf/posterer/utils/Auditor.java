/*
 * Copyright 2015 technosf [https://github.com/technosf]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.technosf.posterer.utils;

import java.util.concurrent.TimeUnit;

/**
 * {@code Auditor} is a souped up {@code StringBuilder}.
 * <p>
 * Can do formating and timing.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class Auditor
{
    /**
     * The log
     */
    private final StringBuilder audit = new StringBuilder();

    /**
     * Creation time stamp
     */
    private final long tsCreated = System.nanoTime();

    /**
     * Start timestamp
     */
    private long tsStart;

    /**
     * Stop timestamp
     */
    private long tsStop;

    /**
     * Elapsed between stat and stop
     */
    private long elapsed;


    /**
     * Starts the timer
     */
    public void start()
    {
        tsStart = System.nanoTime();
    }


    /**
     * Starts the timer
     */
    public long stop()
    {
        if (elapsed == 0)

            elapsed = TimeUnit.NANOSECONDS
                    .toMillis((tsStop = System.nanoTime()) - tsStart);
        return elapsed;
    }


    public long elapsedMillis()
    {
        return elapsed;
    }


    /**
     * Appends the given string to a new audit line, potentially with a timing.
     * 
     * @param chrono
     *            append timing?
     * @param status
     *            the status to audit
     * @return the Audit
     */
    public Auditor append(boolean chrono, String status)
    {
        if (tsStart == 0)
        {
            start();
        }

        if (chrono)
            audit.append("@").append(TimeUnit.NANOSECONDS
                    .toMillis(System.nanoTime() - tsStart)).append("ms - ");
        audit.append(status).append("\n");
        return this;
    }


    /**
     * Appends the given string to a new audit line, potentially with a timing.
     * 
     * @param chrono
     *            append timing?
     * @param status
     *            the status to audit
     * @param args
     * @return
     */
    @SuppressWarnings("null")
    public Auditor append(boolean chrono, String format, Object... args)
    {
        return this.append(chrono, String.format(format, args));
    }


    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("null")
    public final String toString()
    {
        return audit.toString();
    }
}
