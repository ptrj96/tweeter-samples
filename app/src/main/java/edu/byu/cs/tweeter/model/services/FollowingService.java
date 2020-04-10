package edu.byu.cs.tweeter.model.services;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.net.ServerFacade;
import edu.byu.cs.tweeter.net.request.FollowingRequest;
import edu.byu.cs.tweeter.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.ByteArrayUtils;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowingService {

    private final Observer observer;

    /**
     * An observer interface to be implemented by observers who want to be notified when
     * asynchronous operations complete.
     */
    public interface Observer {
        void followeesRetrieved(FollowingResponse followingResponse);
    }

    /**
     * Creates an instance.
     *
     * @param observer the observer who wants to be notified when any asynchronous operations complete.
     */
    public FollowingService(Observer observer) {
        // An assertion would be better, but Android doesn't support Java assertions
        if(observer == null) {
            throw new NullPointerException();
        }

        this.observer = observer;
    }

    /**
     * Requests the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link ServerFacade}
     * to get the followees from the server. This is an asynchronous operation.
     *
     * @param request contains the data required to fulfill the request.
     */
    public void getFollowees(FollowingRequest request) {
        AsyncTask<FollowingRequest, Void, FollowingResponse> followingTask = new AsyncTask<FollowingRequest, Void, FollowingResponse>() {

            /**
             * The method that is invoked on the background thread to retrieve followees. This method is
             * invoked indirectly by calling {@link #execute(FollowingRequest...)}.
             *
             * @param followingRequests the request object (there will only be one).
             * @return the response.
             */
            @Override
            protected FollowingResponse doInBackground(FollowingRequest... followingRequests) {
                ServerFacade serverFacade = new ServerFacade();
                FollowingResponse response = serverFacade.getFollowees(followingRequests[0]);

                if(response.isSuccess()) {
                    loadImages(response);
                }

                return response;
            }

            /**
             * Loads the profile image for each followee included in the response.
             *
             * @param response the response from the followee request.
             */
            private void loadImages(FollowingResponse response) {
                for(User user : response.getFollowees()) {
                    try {
                        byte [] bytes = ByteArrayUtils.bytesFromUrl(user.getImageUrl());
                        user.setImageBytes(bytes);
                    } catch (IOException e) {
                        Log.e(this.getClass().getName(), e.toString(), e);
                    }
                }
            }

            /**
             * Notifies the observer (on the thread of the invoker of the
             * {@link #execute(FollowingRequest...)} method) when the task completes.
             *
             * @param followingResponse the response that was received by the task.
             */
            @Override
            protected void onPostExecute(FollowingResponse followingResponse) {
                observer.followeesRetrieved(followingResponse);
            }
        };

        followingTask.execute(request);
    }
}
