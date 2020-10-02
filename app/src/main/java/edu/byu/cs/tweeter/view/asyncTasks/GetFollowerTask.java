package edu.byu.cs.tweeter.view.asyncTasks;

import android.os.AsyncTask;

import java.io.IOException;

import edu.byu.cs.tweeter.model.service.request.FollowerRequest;
import edu.byu.cs.tweeter.model.service.response.FollowerResponse;
import edu.byu.cs.tweeter.presenter.FollowerPresenter;

public class GetFollowerTask extends AsyncTask<FollowerRequest, Void, FollowerResponse> {

    private final FollowerPresenter presenter;
    private final Observer observer;
    private Exception exception;

    public interface Observer {
        void followersRetrieved(FollowerResponse followerResponse);
        void handleException(Exception exception);
    }

    public GetFollowerTask(FollowerPresenter presenter, Observer observer) {
        if (observer == null) {
            throw new NullPointerException();
        }

        this.presenter = presenter;
        this.observer = observer;
    }

    @Override
    protected FollowerResponse doInBackground(FollowerRequest... followerRequests) {

        FollowerResponse response = null;

        try {
            response = presenter.getFollower(followerRequests[0]);
        } catch (IOException e) {
            exception = e;
        }

        return  response;
    }

    @Override
    protected void onPostExecute(FollowerResponse followerResponse) {
        if(exception != null) {
            observer.handleException(exception);
        } else {
            observer.followersRetrieved(followerResponse);
        }
    }
}
