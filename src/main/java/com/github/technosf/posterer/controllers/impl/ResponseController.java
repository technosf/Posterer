/*
 * Copyright 2014 technosf [https://github.com/technosf]
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
package com.github.technosf.posterer.controllers.impl;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import com.github.technosf.posterer.controllers.AbstractController;
import com.github.technosf.posterer.controllers.Controller;
import com.github.technosf.posterer.models.impl.ResponseModel;

/**
 * @author technosf
 * @since 0.0.1
 * @version 0.0.1
 */
public class ResponseController
				extends AbstractController
				implements Controller
{

	/**
	 * The FXML definition of the View
	 */
	public static String FXML = "/fxml/Response.fxml";

	private final static String FORMAT_TITLE =
					"Poster :: Response #%1$d [%2$s %3$s]";

	private Task<?> responseModelTask;

	private boolean cancellable = true;

	/*
	 * ------------ FXML Components -----------------
	 */

	@FXML
	private Parent root;

	@FXML
	private TextArea headers, response;

	@FXML
	private TextField status;

	@FXML
	private ProgressIndicator progress;

	@FXML
	private Button button;


	// private String title;

	//
	// http://code.google.com/p/jfxee/source/browse/trunk/jfxee7/client/src/main/java/com/zenjava/jfxee7/HelloController.java?r=16
	//
	// http://www.zenjava.com/2011/11/14/file-downloading-in-javafx-2-0-over-http/
	//

	/**
     * 
     */
	public ResponseController()
	{
		super(FORMAT_TITLE);
	}


	/**
	 * @param responseModel
	 */
	public void updateStage(final ResponseModel responseModel)
	{
		setTitle(String.format(FORMAT_TITLE, responseModel.getReferenceId(),
						responseModel.getMethod(),
						responseModel.getUri()));

		if (Task.class.isInstance(responseModel))
		{
			responseModelTask = (Task<?>) responseModel;

			responseModelTask
							.setOnSucceeded(new EventHandler<WorkerStateEvent>()
								{
									@Override
									public void handle(WorkerStateEvent arg0)
									{
										requestSucceeded(responseModel);

									}
								});

			responseModelTask.setOnFailed(new EventHandler<WorkerStateEvent>()
				{
					@Override
					public void handle(WorkerStateEvent arg0)
					{
						requestFailed(responseModelTask.getException().getMessage());

					}
				});

			responseModelTask
							.setOnCancelled(new EventHandler<WorkerStateEvent>()
								{
									@Override
									public void handle(WorkerStateEvent arg0)
									{
										cancelOrClose();

									}
								});

			Platform.runLater(responseModelTask);
		}
	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see com.github.technosf.posterer.controllers.Controller#initialize()
	 */
	@Override
	public void initialize()
	{

	}


	/**
	 * {@inheritDoc}
	 * 
	 * @see com.github.technosf.posterer.controllers.AbstractController#getRoot()
	 */
	@Override
	public Parent getRoot()
	{
		return root;
	}


	/**
     * 
     */
	public void cancelOrClose()
	{
		if (cancellable)
		{
			cancellable = false;
			status.setText("Cancelling...");
			responseModelTask.cancel();
			progress.setVisible(false);
			button.setText("Close");
			status.setText("Cancelled.");
		}
		else
		{
			getStage().close();
		}
	}


	/**
     * 
     */
	private void requestFailed(String error)
	{
		status.setText("Fail: " + error);
		progress.setVisible(false);
		cancellable = false;
		button.setText("Close");
	}


	/**
     * 
     */
	private void requestSucceeded(ResponseModel responseModel)
	{
		headers.setText(responseModel.getHeaders());
		response.setText(responseModel.getBody());
		progress.setVisible(false);
		cancellable = false;
		button.setText("Close");
	}
}
