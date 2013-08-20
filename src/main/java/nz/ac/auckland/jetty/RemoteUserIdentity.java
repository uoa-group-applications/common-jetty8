package nz.ac.auckland.jetty;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.UserIdentity;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.List;
import java.util.Set;

public class RemoteUserIdentity implements UserIdentity {
  private final Principal user;
  private List<String> roles;
	private final Request request;

  public RemoteUserIdentity(final String username, final Request request) {
	  this.request = request;

    user = new Principal() {
      @Override
      public String getName() {
        return username;
      }
    };
  }

  public List<String> getRoles() {
    return roles;
  }

  @Override
  public Subject getSubject() {
    return null;
  }

  @Override
  public Principal getUserPrincipal() {
    return user;
  }

  @Override
  public boolean isUserInRole(String role, Scope scope) {
    return RemoteUserUtils.isUserInRole(this, role, scope, request);
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }
}
