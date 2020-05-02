package edu.byu.cs.tweeter.client.model.service;

import android.os.AsyncTask;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.service.request.LoginRequest;
import edu.byu.cs.tweeter.model.service.response.LoginResponse;
import edu.byu.cs.tweeter.client.util.ByteArrayUtils;
import edu.byu.cs.tweeter.model.service.LoginService;

/**
 * Contains the business logic to support the login operation. Not a pure proxy because
 * it doesn't implement the {@link LoginService} interface. It can't because this class is
 * called asynchronously, but we need the implementing class on the server to be synchronous.
 */
public class LoginServiceProxy {

    private static final String URL_PATH = "/login";

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
     public LoginServiceProxy(Observer observer) {
        this.observer = observer;
     }

    /**
     * Makes an asynchronous login request.
     *
     * @param loginRequest the login request.
     */
    public void login(LoginRequest loginRequest) {
        AsyncTask<LoginRequest, Void, LoginResponse> loginTask = getLoginAsyncTask();
        loginTask.execute(loginRequest);
    }

    /**
     * Returns an instance of {@link ServerFacade}. Allows mocking of the ServerFacade class for
     * testing purposes. All usages of ServerFacade should get their instance from this method to
     * allow for proper mocking.
     *
     * @return the instance.
     */
    ServerFacade getServerFacade() {
        return new ServerFacade();
    }


    /**
     * Returns an instance of {@link LoginAsyncTask}. Allows mocking of the LoginAsyncTask class for
     * testing purposes. All usages of LoginAsyncTask should get their instance from this method to
     * allow for proper mocking.
     *
     * @return the instance.
     */
    LoginAsyncTask getLoginAsyncTask() {
        return new LoginAsyncTask();
    }

    /**
     * The AsyncTask that makes the login request on a background thread.
     */
    private class LoginAsyncTask extends AsyncTask<LoginRequest, Void, LoginResponse> {

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
            LoginResponse loginResponse = null;

            try {
                loginResponse = getServerFacade().login(loginRequests[0], URL_PATH);

                if(loginResponse.isSuccess()) {
                        loadImage(loginResponse.getUser());
                }
            } catch (Exception ex) {
                exception = ex;
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
    }
}
