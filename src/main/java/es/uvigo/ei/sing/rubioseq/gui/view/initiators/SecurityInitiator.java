/*
	This file is part of RUbioSeq-GUI.

	RUbioSeq-GUI is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	RUbioSeq-GUI is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with RUbioSeq-GUI.  If not, see <http://www.gnu.org/licenses/>.
*/
package es.uvigo.ei.sing.rubioseq.gui.view.initiators;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.DesktopCleanup;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zk.ui.util.SessionCleanup;

import es.uvigo.ei.sing.rubioseq.User;

/**
 * 
 * @author hlfernandez
 *
 */
public class SecurityInitiator implements Initiator, DesktopCleanup, SessionCleanup {
	private final static Set<String> IGNORE_PAGES = new HashSet<String>();
	private final static Set<String> ADMIN_PAGES = new HashSet<String>();
	
	static {
		IGNORE_PAGES.add("/login.zul");
		/*
		IGNORE_PAGES.add("/loginConfirmation.zul");
		IGNORE_PAGES.add("/passwordRecovery.zul");
		*/
		ADMIN_PAGES.add("/administrator.zul");
		ADMIN_PAGES.add("/administrator/manage-users.zul");
		ADMIN_PAGES.add("/administrator/manage-datastores.zul");
		ADMIN_PAGES.add("/administrator/configuration.zul");
	}
	
	@Override
	public void doInit(Page page, Map<String, Object> args) throws Exception {
		final String requestPath = page.getRequestPath();
		
		if (requestPath.equals("/logout.zul")) {
			final Session session = Sessions.getCurrent(false);
			if (session != null) session.invalidate();
			Executions.sendRedirect("/login.zul");
		} else 	if (!IGNORE_PAGES.contains(requestPath)) {
			final Session session = Sessions.getCurrent(false);
			if (session == null || !session.hasAttribute("user")) {
				Executions.sendRedirect("/login.zul");
			} 
			if (session.hasAttribute("user")){
				User currentUser = (User) session.getAttribute("user");
				if (!currentUser.isAdmin() &&
						ADMIN_PAGES.contains(requestPath)){
					Executions.sendRedirect("/index.zul");
				}
			}
		}
	}

	@Override
	public void cleanup(Session arg0) throws Exception {
	}

	@Override
	public void cleanup(Desktop arg0) throws Exception {
	}
	
}
