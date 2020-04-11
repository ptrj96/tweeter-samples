package edu.byu.cs.tweeter.model.services;

import android.os.AsyncTask;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.net.ServerFacade;
import edu.byu.cs.tweeter.net.request.LoginRequest;
import edu.byu.cs.tweeter.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.ByteArrayUtils;

/**
 * Contains the business logic to support the login operation.
 */
public class LoginService {

    private final Observer observer;

    /**
     * An observer interface to be implemented by observers who want to be notified when
     * asynchronous operations complete.
     */
    public interface Observer {
        void loginSuccessful(LoginResponse loginResponse);
        void loginUnsuccessful(LoginResponse loginResponse);
        void handleException(Exception exception);
    }

    /**
     * Creates an instance.
     *
     * @param observer the observer who wants to be notified when any asynchronous operations
     *                 complete.
     */
     public LoginService(Observer observer) {
        this.observer = observer;
     }

    /**
     * Makes an asynchronous login request.
     *
     * @param loginRequest the login request.
     */
    public void login(LoginRequest loginRequest) {
        AsyncTask<LoginRequest, Void, LoginResponse> loginTask = new AsyncTask<LoginRequest, Void, LoginResponse>() {

            private Exception exception;

            /**
             * The method that is invoked on a background thread to log the user in. This method is
             * invoked indirectly by calling {@link #execute(LoginRequest...)}.
             *
             * @param loginRequests the request object (there will only be one).
             * @return the response.
             */
            @Override
            protected LoginResponse doInBackground(LoginRequest... loginRequests) {
                ServerFacade serverFacade = new ServerFacade();
                LoginResponse loginResponse = serverFacade.login(loginRequests[0]);

                if(loginResponse.isSuccess()) {
                    try {
                        loadImage(loginResponse.getUser());
                    } catch (IOException ex) {
                        exception = ex;
                    }
                }

                return loginResponse;
            }

            /**
             * Loads the profile image for the user.
             *
             * @param user the user whose profile image is to be loaded.
             */
            private void loadImage(User user) throws IOException {
                byte [] bytes = ByteArrayUtils.bytesFromUrl(user.getImageUrl());
                user.setImageBytes(bytes);
            }

            /**
             * Notifies the observer (on the thread of the invoker of the
             * {@link #execute(LoginRequest...)} method) when the task completes.
             *
             * @param loginResponse the response that was received by the task.
             */
            @Override
            protected void onPostExecute(LoginResponse loginResponse) {
                if(exception != null) {
                    observer.handleException(exception);
                } else if(loginResponse.isSuccess()) {
                    observer.loginSuccessful(loginResponse);
                } else {
                    observer.loginUnsuccessful(loginResponse);
                }
            }
        };

        loginTask.execute(loginRequest);
    }
}
