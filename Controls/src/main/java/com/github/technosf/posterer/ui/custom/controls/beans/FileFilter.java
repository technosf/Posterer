/*
 * Copyright 2016 technosf [https://github.com/technosf]
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
package com.github.technosf.posterer.ui.custom.controls.beans;

import java.util.List;

import javafx.beans.NamedArg;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * Bean to expose the generation of {@code FileChooser.ExtensionFilter} in FXML.
 * 
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public final class FileFilter
{
    /**
     * The underlying FileChooser.ExtensionFilter;
     */
    ExtensionFilter extensionFilter;


    /**
     * Constructor with argument annotations
     * 
     * @param description
     * @param extensions
     */
    public FileFilter(@NamedArg("description") final String description,
            @NamedArg("extension") final String... extensions)
    {
        extensionFilter = new ExtensionFilter(description,
                extensions);
    }


    /**
     * Constructor with argument annotations
     * 
     * @param description
     * @param extensions
     */
    public FileFilter(@NamedArg("description") final String description,
            @NamedArg("extension") final List<String> extensions)
    {
        extensionFilter = new ExtensionFilter(description,
                extensions);
    }


    /**
     * @return the ExtensionFilter
     */
    public ExtensionFilter getExtensionFilter()
    {
        return extensionFilter;
    };

}
