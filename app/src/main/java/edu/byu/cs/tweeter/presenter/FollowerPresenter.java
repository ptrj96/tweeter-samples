package edu.byu.cs.tweeter.presenter;

import android.view.View;

import java.io.IOException;

import edu.byu.cs.tweeter.model.service.FollowerService;
import edu.byu.cs.tweeter.model.service.FollowingService;
import edu.byu.cs.tweeter.model.service.request.FollowerRequest;
import edu.byu.cs.tweeter.model.service.response.FollowerResponse;

public class FollowerPresenter {

    private final View view;

    public interface View {

    }

    public FollowerPresenter(View view) { this.view = view; }

    public FollowerResponse getFollower(FollowerRequest request) throws IOException {
        FollowerService followerService = getFollowerService();
        return followerService.getFollowers(request);
    }

    FollowerService getFollowerService() { return new FollowerService(); }
}
