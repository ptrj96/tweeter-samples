package edu.byu.cs.tweeter.model.service.request;

import edu.byu.cs.tweeter.model.domain.User;

public class FollowerRequest {

    private final User followee;
    private final int limit;
    private final User lastFollower;

    public FollowerRequest(User followee, int limit, User lastFollower) {
        this.followee = followee;
        this.limit = limit;
        this.lastFollower = lastFollower;
    }

    public User getFollowee() { return followee; }

    public int getLimit() { return limit; }

    public User getLastFollower() { return lastFollower; }
}
