package edu.byu.cs.tweeter.model.service;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.service.request.FollowerRequest;
import edu.byu.cs.tweeter.model.service.response.FollowerResponse;
import edu.byu.cs.tweeter.util.ByteArrayUtils;

public class FollowerService {

    public FollowerResponse getFollowers(FollowerRequest request) throws IOException {
        FollowerResponse response = getServerFacade().getFollowers(request);

        if (response.isSuccess()) {
            loadImages(response);
        }

        return response;
    }

    private void loadImages(FollowerResponse response) throws IOException {
        for (User user : response.getFollowers()) {
            byte [] bytes = ByteArrayUtils.bytesFromUrl(user.getImageUrl());
            user.setImageBytes(bytes);
        }
    }

    ServerFacade getServerFacade() { return new ServerFacade(); }
}
