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
 * 
 * @since 0.0.1
 * 
 * @version 0.0.1
 */
public class PrettyPrinters
{

	private static final String	BREAK			= "\n";
	private static String		INDENT_STRING	= "    ";
	private static final int	INDENT			= INDENT_STRING.length();


	public static String pretty ( @Nullable String data, boolean stripPreamble )
	{
		return data;

	}


	/**
	 * Pretty print XML
	 * 
	 * Split the string into rows based on XML delims, then prettify
	 * 
	 * @param xml
	 *                        the xml to prettify
	 * @param stripXmlDef
	 *                        strip the XML preamble?
	 * 
	 * @return pretty xml
	 */
	public static String xml ( @Nullable String xml, boolean stripXmlDef )
	{
		if ( xml == null || xml.isEmpty() )
			return "";

		String[] rows = xml.trim().replaceAll(">", ">\n").replaceAll("<", "\n<").split("\n");

		return pretty(rows, "<?", "<", "/>", "</", ">", stripXmlDef);
	}


	/**
	 * Pretty print JSON
	 * 
	 * Split the string into rows based on JSON delims, then prettify
	 * 
	 * @param xml
	 *                        the xml to prettify
	 * @param stripXmlDef
	 *                        strip the XML preamble?
	 * 
	 * @return pretty xml
	 */
	public static String json ( @Nullable String json )
	{
		if ( json == null || json.isEmpty() )
			return "";

		String[] rows = json.trim().replaceAll("\\},", "\n\\},").replaceAll("\\{", "\n\\{\n").split("\n");

		return pretty(rows, "", "{", "{", "}", "}", false);
	}


	/**
	 * Common pretty-fier
	 * 
	 * @param rows the input rows
	 * @param headerdef header definition 
	 * @param openClauseStart clause open start def
	 * @param openClauseEnd clause open end def
	 * @param closeClauseStart clause close start
	 * @param closeClauseEnd clause close end
	 * @param stripheader true if header should be stripped
	 * 
	 * @return the prettified input rows
	 */
	private static String pretty (
			String[] rows, String headerdef, 
			String openClauseStart, String openClauseEnd, 
			String closeClauseStart,String closeClauseEnd, 
			boolean stripheader
	)
	{

		StringBuilder	indent	= new StringBuilder();
		StringBuilder	pretty	= new StringBuilder();
		String			row;

		boolean			wasData	= false, wasClose = false, wasFirst = true;

		for ( int i = 0; i < rows.length; i++ )
		{
			if ( ( row = rows[i].trim() ).isEmpty() )
				continue;

			if ( !headerdef.isEmpty() && row.startsWith(headerdef) )
			/*
			 * Header def
			 */
			{
				if ( stripheader )
				{
					continue;
				}
				pretty.append(row).append(BREAK);
				row = "";
			}
			else if ( row.startsWith(openClauseStart) && ( row.endsWith(openClauseEnd) || openClauseEnd.isEmpty() )
			)
			/*
			 * Enclosing tag
			 */
			{
				if ( wasClose )
				/*
				 * Indent from last tag
				 */
				{
					pretty.append(BREAK);
				}
				if ( !wasData )
				/*
				 * Indent from last tag
				 */
				{
					pretty.append(indent);
				}
				wasData = wasClose = false;
			}
			else if ( row.startsWith(closeClauseStart) && ( row.endsWith(closeClauseEnd) || closeClauseEnd.isEmpty() )
			)
			/*
			 * Closing tag
			 */
			{
				indent.replace(0, INDENT, "");
				if ( wasClose )
				/*
				 * Indent from last tag
				 */
				{
					pretty.append(BREAK);
				}
				if ( !wasData )
				/*
				 * Indent from last tag
				 */
				{
					pretty.append(indent);
				}
				wasData		= false;
				wasClose	= true;
			}
			else if ( row.startsWith(openClauseStart) )
			/*
			 * Opening tag
			 */
			{
				if ( !wasFirst )
				/*
				 * No break on first line
				 */
				{
					pretty.append(BREAK);
				}

				if ( !wasData )
				/*
				 * Indent if last row was data or a close tag
				 */
				{
					pretty.append(indent.toString());
				}
				indent.insert(0, INDENT_STRING);
				wasClose = wasData = false;
			}
			else
			/*
			 * Data
			 */
			{
				if ( wasData )
				/*
				 * floating data, so newline and indent
				 */
				{
					pretty.append(BREAK).append(indent.toString());
				}
				wasData		= true;
			}

			wasFirst	= false;	
			
			pretty.append(row);

		}

		return pretty.toString();
	}

}
