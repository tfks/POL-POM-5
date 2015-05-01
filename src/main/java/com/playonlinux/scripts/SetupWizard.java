package com.playonlinux.scripts;

import com.playonlinux.api.Controller;
import com.playonlinux.api.ProgressStep;
import com.playonlinux.api.SetupWindow;
import com.playonlinux.api.UIMessageSender;
import com.playonlinux.utils.Injectable;
import com.playonlinux.utils.messages.*;

import java.util.List;


public class SetupWizard {
    @Injectable
    static Controller controller;
    private final String title;

    SetupWindow setupWindow;
    UIMessageSender messageSender;

    /**
     * Jython needs static injection, Spring won't work for that purpose
     *
     * @param controller controller to be injected
     */
    public static void injectMainController(Controller controller) {
        SetupWizard.controller = controller;
    }

    /**
     * Create the setupWindow
     *
     * @param title title of the setupWindow
     */
    public SetupWizard(String title) {
        this.messageSender = controller.createUIMessageSender();
        this.title = title;

        messageSender.synchroneousSend(
                new SynchroneousMessage() {
                    @Override
                    public void execute(Message message) {
                        setupWindow = controller.createSetupWindowGUIInstance(title);
                    }
                }
        );
    }

    /**
     * Closes the setupWindow
     */
    public void close() {
        messageSender.synchroneousSend(
                new SynchroneousMessage() {
                    @Override
                    public void execute(Message message) {
                        setupWindow.close();
                    }
                }
        );
    }

    /**
     * Shows a simple showSimpleMessageStep
     * @param textToShow the text to show
     * @throws InterruptedException
     * @throws CancelException
     */
    public void message(String textToShow) throws InterruptedException, CancelException {
        messageSender.synchroneousSendAndGetResult(
                new CancelerSynchroneousMessage() {
                    @Override
                    public void execute(Message message) {
                        setupWindow.showSimpleMessageStep((CancelerSynchroneousMessage) message, textToShow);
                    }
                }
        );
    }

    public void presentation(String textToShow) throws CancelException, InterruptedException {
        messageSender.synchroneousSendAndGetResult(
                new CancelerSynchroneousMessage() {
                    @Override
                    public void execute(Message message) {
                        setupWindow.showPresentationStep((CancelerSynchroneousMessage) message, textToShow);
                    }
                }
        );
    }
    /**
     * Ask the user to enter a value
     * @param textToShow a text that will be shown
     * @return the value the user entered
     * @throws InterruptedException
     * @throws CancelException
     */
    public String textbox(String textToShow) throws InterruptedException, CancelException {
        return this.textbox(textToShow, "");
    }

    /**
     * Asks the user to enter a value
     * @param textToShow a text that will be shown
     * @param defaultValue a default value
     * @return the value the user entered
     * @throws InterruptedException
     * @throws CancelException
     */
    public String textbox(String textToShow, String defaultValue) throws InterruptedException, CancelException {
        return (String) messageSender.synchroneousSendAndGetResult(
                new CancelerSynchroneousMessage<String>() {
                    @Override
                    public void execute(Message message) {
                        setupWindow.showTextBoxStep((CancelerSynchroneousMessage) message, textToShow, defaultValue);
                    }
                }
        );
    }

    /**
     * Displays a showMenuStep so that the user can make a choice
     * @param textToShow a text that will be shown
     * @param menuItems a list containing the elements of the showMenuStep
     * @return the value the user entered (as string)
     * @throws InterruptedException
     * @throws CancelException
     */
    public String menu(String textToShow, List<String> menuItems) throws InterruptedException, CancelException {
        return (String) messageSender.synchroneousSendAndGetResult(
                new CancelerSynchroneousMessage<String>() {
                    @Override
                    public void execute(Message message) {
                        setupWindow.showMenuStep((CancelerSynchroneousMessage) message, textToShow, menuItems);
                    }
                }
        );
    }

    /**
     * Displays a showSimpleMessageStep to the user with a waiting symbol, and releases the script just afterward
     * @param textToShow a text that will be shown
     */
    public void wait(String textToShow) {
        messageSender.asynchroneousSend(
                new InterrupterAsynchroneousMessage() {
                    @Override
                    public void execute(Message message) {
                        setupWindow.showSpinnerStep((InterrupterAsynchroneousMessage) message, textToShow);
                    }
                }
        );
    }

    public ProgressStep progressBar(String textToShow) throws CancelException, InterruptedException {
        return (ProgressStep) messageSender.synchroneousSendAndGetResult(
                new InterrupterSynchroneousMessage() {
                    @Override
                    public void execute(Message message) {
                        this.setResponse(setupWindow.showProgressBar((InterrupterSynchroneousMessage) message,
                                textToShow));
                    }
                }
        );
    }


}