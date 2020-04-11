package edu.byu.cs.tweeter.presenter;

import edu.byu.cs.tweeter.model.service.LoginService;
import edu.byu.cs.tweeter.model.service.request.LoginRequest;
import edu.byu.cs.tweeter.model.service.response.LoginResponse;

/**
 * The presenter for the login functionality of the application.
 */
public class LoginPresenter implements LoginService.Observer {

    private final View view;

    /**
     * The interface by which this presenter communicates with it's view.
     */
    public interface View {
        void loginSuccessful(LoginResponse loginResponse);
        void loginUnsuccessful(LoginResponse loginResponse);
        void handleException(Exception exception);
    }

    /**
     * Creates an instance.
     *
     * @param view the view for which this class is the presenter.
     */
    public LoginPresenter(View view) {
        // An assertion would be better, but Android doesn't support Java assertions
        if(view == null) {
            throw new NullPointerException();
        }
        this.view = view;
    }

    /**
     * Makes an asynchronous login request.
     *
     * @param loginRequest the request.
     */
    public void login(LoginRequest loginRequest) {
        LoginService loginService = new LoginService(this);
        loginService.login(loginRequest);
    }

    /**
     * Invoked when the login request completes if the login was successful. Notifies the view of
     * the successful login.
     *
     * @param loginResponse the response.
     */
    @Override
    public void loginSuccessful(LoginResponse loginResponse) {
        view.loginSuccessful(loginResponse);
    }

    /**
     * Invoked when the login request completes if the login request was unsuccessful. Notifies the
     * view of the unsuccessful login.
     *
     * @param loginResponse the response.
     */
    @Override
    public void loginUnsuccessful(LoginResponse loginResponse) {
        view.loginUnsuccessful(loginResponse);
    }

    /**
     * A callback indicating that an exception occurred in an asynchronous method this class is
     * observing.
     *
     * @param exception the exception.
     */
    @Override
    public void handleException(Exception exception) {
        view.handleException(exception);
    }
}
