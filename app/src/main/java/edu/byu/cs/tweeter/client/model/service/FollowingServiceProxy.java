package edu.byu.cs.tweeter.client.model.service;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.service.request.FollowingRequest;
import edu.byu.cs.tweeter.model.service.response.FollowingResponse;
import edu.byu.cs.tweeter.client.util.ByteArrayUtils;
import edu.byu.cs.tweeter.model.service.FollowingService;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowingServiceProxy implements FollowingService {

    private static final String URL_PATH = "/getfollowing";

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link ServerFacade} to
     * get the followees from the server.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    @Override
    public FollowingResponse getFollowees(FollowingRequest request) throws IOException, TweeterRemoteException {
        ServerFacade serverFacade = new ServerFacade();
        FollowingResponse response = serverFacade.getFollowees(request, URL_PATH);

        if(response.isSuccess()) {
            loadImages(response);
        }

        return response;
    }

    /**
     * Loads the profile image data for each followee included in the response.
     *
     * @param response the response from the followee request.
     */
    private void loadImages(FollowingResponse response) throws IOException {
        for(User user : response.getFollowees()) {
            byte [] bytes = ByteArrayUtils.bytesFromUrl(user.getImageUrl());
            user.setImageBytes(bytes);
        }
    }
}
