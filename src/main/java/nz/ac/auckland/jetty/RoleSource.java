package nz.ac.auckland.jetty;

import java.util.List;

public interface RoleSource {
	/**
	 * Primarily we want a list of roles back, but if there is other data that can come along for the ride...
	 *
	 * The role information is the only information the servlet engine can actually use.
	 *
	 * @param user
	 * @param request
	 * @return
	 */
  List<String> getRoles(String user, javax.servlet.http.HttpServletRequest request);
}
