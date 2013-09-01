package nz.ac.auckland.jetty;

import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.UserIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.Arrays;

public class RemoteUserUtils {
	private static final Logger log = LoggerFactory.getLogger(RemoteUserUtils.class);
	private static final String USER_HEADER = "X-Forwarded-User";
	private static final String REMOTEUSER_NAME = "remoteuser.name";
	private static final String REMOTEUSER_ROLES = "remoteuser.localroles";
	public static final String ROLE_CHECK_OVERRIDE_CLASS = "remoteuser.rolesource";
	private static RoleSource roleSource = null;

	public static void initialize() {
		String clazzName = System.getProperty(ROLE_CHECK_OVERRIDE_CLASS);

		if (clazzName != null) {
			try {
				Class clazz = Class.forName(clazzName);
				Object ru = clazz.newInstance();
				if (ru instanceof RoleSource) {
					roleSource = (RoleSource) ru;
				}
			} catch (Exception ex) {
				log.error("Unable to find role class " + clazzName, ex);
			}
		}
	}

	public static Authentication ensureRemoteUserSetIfIncluded(Request request) {
		String remoteUser = request.getHeader(USER_HEADER);

		// deal with the case where Apache sends through "" as the remote user
		if (remoteUser != null && remoteUser.trim().length() == 0)
			remoteUser = null;

		if (remoteUser == null) {
			remoteUser = System.getProperty(REMOTEUSER_NAME);
		}

		RemoteUserAuthenticationUser user = null;

		if (remoteUser != null) {
			user = new RemoteUserAuthenticationUser(remoteUser, request);

			if (roleSource != null) { // if we have a role source, force getting it
				user.getIdentity().setRoles(roleSource.getRoles(remoteUser, request));
			}
		}

		return user;
	}

	public static boolean isRoleOverrideSet() {
		return System.getProperty(REMOTEUSER_ROLES) != null;
	}

	public static boolean inRole(String role) {
		String rSplit = System.getProperty(REMOTEUSER_ROLES);

		if (rSplit != null) {
			String[] roles = rSplit.split(",");
			return Arrays.asList(roles).contains(role);
		} else
			return false;
	}

	public static boolean isUserInRole(RemoteUserIdentity remoteUserIdentity, String role, UserIdentity.Scope scope, Request request) {
		if (isRoleOverrideSet()) {
			log.trace("isUserInRole: using overridden roles");
			return inRole(role);
		} else {
			if (remoteUserIdentity != null && remoteUserIdentity.getUserPrincipal().getName() != null && roleSource != null) {
				log.trace("isUserInRole: asking for roles from roleSource");

				if (remoteUserIdentity.getRoles() == null)
					remoteUserIdentity.setRoles(roleSource.getRoles(remoteUserIdentity.getUserPrincipal().getName(), request));

				if (remoteUserIdentity.getRoles() == null) {
					return false;
				} else {
					return remoteUserIdentity.getRoles().contains(role);
				}
			} else {
				log.trace("isUserInRole: unknown user or user principal not understood");
				return false;
			}
		}
	}
}
