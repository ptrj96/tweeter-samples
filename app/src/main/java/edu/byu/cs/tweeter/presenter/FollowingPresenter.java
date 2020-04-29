package edu.byu.cs.tweeter.presenter;

import edu.byu.cs.tweeter.model.service.FollowingService;
import edu.byu.cs.tweeter.model.service.request.FollowingRequest;
import edu.byu.cs.tweeter.model.service.response.FollowingResponse;

/**
 * The presenter for the "following" functionality of the application.
 */
public class FollowingPresenter implements FollowingService.Observer {

    private final View view;

    /**
     * The interface by which this presenter communicates with it's view.
     */
    public interface View {
        void followeesRetrieved(FollowingResponse followingResponse);
        void handleException(Exception exception);
    }

    /**
     * Creates an instance.
     *
     * @param view the view for which this class is the presenter.
     */
    public FollowingPresenter(View view) {
        // An assertion would be better, but Android doesn't support Java assertions
        if(view == null) {
            throw new NullPointerException();
        }

        this.view = view;
    }

    /**
     * Requests the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned for a previous request. This is an asynchronous
     * operation.
     *
     * @param request contains the data required to fulfill the request.
     */
    public void getFollowing(FollowingRequest request) {
        getFollowingService(this).getFollowees(request);
    }

    /**
     * Returns an instance of {@link FollowingService}. Allows mocking of the FollowingService class
     * for testing purposes. All usages of FollowingService should get their FollowingService
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowingService getFollowingService(FollowingService.Observer observer) {
        return new FollowingService(observer);
    }

    /**
     * Notifies the view when the users requested by the call to
     * {@link #getFollowing(FollowingRequest)} have been retrieved.
     *
     * @param followingResponse the response.
     */
    @Override
    public void followeesRetrieved(FollowingResponse followingResponse) {
        view.followeesRetrieved(followingResponse);
    }

    /**
     * A callback indicating that an exception occurred in an asynchronous method this class is
     * observing. Notifies the view of the exception.
     *
     * @param exception the exception.
     */
    @Override
    public void handleException(Exception exception) {
        view.handleException(exception);
    }
}
