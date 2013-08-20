package nz.ac.auckland.jetty;

import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.UserIdentity;

public class RemoteUserAuthenticationUser implements Authentication.User {
  private final RemoteUserIdentity identity;
	private final Request request;

  public RemoteUserAuthenticationUser(String user, Request request) {
	  this.request = request;
	  identity = new RemoteUserIdentity(user, request);
  }

  @Override
  public String getAuthMethod() {
    return "FORM";
  }

  @Override
  public UserIdentity getUserIdentity() {
    return identity;
  }

	public RemoteUserIdentity getIdentity() {
		return identity;
	}

  @Override
  public boolean isUserInRole(UserIdentity.Scope scope, String role) {
    return RemoteUserUtils.isUserInRole(identity, role, scope, request);
  }

  @Override
  public void logout() {
    // not possible
  }
}
