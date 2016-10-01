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
package com.github.technosf.posterer.models;

/**
 * Status behavior model
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public interface StatusModel
{

    /**
     * Replace the Status window text
     * 
     * @param message
     *            the message to set the status window to
     */
    void write(String message);


    /**
     * Replace the Status window text
     * 
     * @param format
     *            the message format
     * @param args
     *            the objects to place in the message format
     */
    void write(String format, Object... args);


    /**
     * Append a message to the Status window.
     * 
     * @param message
     *            the message to append to the status window
     */
    void append(String message);


    /**
     * Append a message to the Status window.
     * 
     * @param format
     *            the message format
     * @param args
     *            the objects to place in the message format
     */
    void append(String format, Object... args);

    /**
     * Returns last message
     * 
     * @return the last message
     */
    String lastMessage();
}
