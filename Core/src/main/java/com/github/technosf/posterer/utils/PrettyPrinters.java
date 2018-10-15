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

import org.eclipse.jdt.annotation.Nullable;

/**
 * PrettyPrinters that format different document types
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class PrettyPrinters
{
    private static String BREAK = "\n";
    private static int INDENT = 4;
    private static String INDENT_STRING =
            String.format("%1$" + INDENT + "s", " ");


    /**
     * Pretty print XML
     * 
     * @param xml
     *            the xml to prettify
     * @param stripXmlDef
     *            strip the XML preamble?
     * @return pretty xml
     */
    public static String xml(@Nullable String xml, boolean stripXmlDef)
    {
        if (xml == null || xml.isEmpty())
            return "";

        StringBuilder indent = new StringBuilder();
        StringBuilder pretty = new StringBuilder();
        String row;
        String[] rows = xml.trim().replaceAll(">", ">\n").replaceAll("<", "\n<")
                .split("\n");
        boolean wasData = false, wasClose = false, wasFirst = true;

        for (int i = 0; i < rows.length; i++)
        {
            if ((row = rows[i].trim()).isEmpty())
                continue;

            if (row.startsWith("<?"))
            /*
             * XML def
             */
            {
                if (stripXmlDef)
                {
                    continue;
                }
                pretty.append(row).append(BREAK);
                row = "";
            }
            else if (row.startsWith("<") && row.endsWith("/>"))
            /*
             * Enclosing tag
             */
            {
                if (wasClose)
                /*
                 * Indent from last tag
                 */
                {
                    pretty.append(BREAK);
                }
                if (!wasData)
                /*
                 * Indent from last tag
                 */
                {
                    pretty.append(indent);
                }
                wasData = wasClose = wasFirst = false;
            }
            else if (row.startsWith("</"))
            /*
             * Closing tag
             */
            {
                indent.replace(0, INDENT, "");
                if (wasClose)
                /*
                 * Indent from last tag
                 */
                {
                    pretty.append(BREAK);
                }
                if (!wasData)
                /*
                 * Indent from last tag
                 */
                {
                    pretty.append(indent);
                }
                wasData = wasFirst = false;
                wasClose = true;
            }
            else if (row.startsWith("<"))
            /*
             * Opening tag
             */
            {
                if (!wasFirst)
                /*
                 * No break on first line
                 */
                {
                    pretty.append(BREAK);
                }

                if (!wasData)
                /*
                 * Indent if last row was data or a close tag
                 */
                {
                    pretty.append(indent.toString());
                }
                indent.insert(0, INDENT_STRING);
                wasClose = wasData = wasFirst = false;
                ;
            }
            else
            /*
             * Data
             */
            {
                if (wasData)
                /*
                 * floating data, so newline and indent
                 */
                {
                    pretty.append(BREAK).append(indent.toString());
                }
                wasData = true;
                wasFirst = false;
            }

            pretty.append(row);
        }

        return pretty.toString();
    }

}
