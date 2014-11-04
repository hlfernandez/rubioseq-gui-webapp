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
package es.uvigo.ei.sing.rubioseq.gui.view.models;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.commons.codec.digest.DigestUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Messagebox;

import es.uvigo.ei.sing.rubioseq.DataStore;
import es.uvigo.ei.sing.rubioseq.DataStoreMode;
import es.uvigo.ei.sing.rubioseq.DataStoreType;
import es.uvigo.ei.sing.rubioseq.RUbioSeqConfiguration;
import es.uvigo.ei.sing.rubioseq.User;
import es.uvigo.ei.sing.rubioseq.gui.util.Utils;
import es.uvigo.ei.sing.rubioseq.gui.util.Validator;

/**
 * 
 * @author hlfernandez
 *
 */
public class UserViewModel {
	
	private DataStoreType DEFAULT_DS_TYPE = DataStoreType.Input_Output;
	
	private String loginUserName;
	private String loginPassword;
	
	private String singUpUsername = "";
	private String singUpEmail = "";
	private String singUpPassword = "";
	private String singUpRepeatPassword = "";
	
	@PersistenceContext(type=PersistenceContextType.EXTENDED)
	private EntityManager em;
	
	public void setloginUserName(String loginUser) {
		this.loginUserName = loginUser;
	}
	
	public String getloginUserName() {
		return loginUserName;
	}
	
	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}
	
	public String getLoginPassword() {
		return loginPassword;
	}
	
	public String getSingUpUsername() {
		return singUpUsername;
	}
	
	public void setSingUpUsername(String singUpUsername) {
		this.singUpUsername = singUpUsername;
	}
	
	public String getSingUpEmail() {
		return singUpEmail;
	}

	public void setSingUpEmail(String singUpEmail) {
		this.singUpEmail = singUpEmail;
	}

	public String getSingUpPassword() {
		return singUpPassword;
	}

	public void setSingUpPassword(String singUpPassword) {
		this.singUpPassword = singUpPassword;
	}

	public String getSingUpRepeatPassword() {
		return singUpRepeatPassword;
	}

	public void setSingUpRepeatPassword(String singUpRepeatPassword) {
		this.singUpRepeatPassword = singUpRepeatPassword;
	}
	
	@DependsOn({"singUpEmail", "singUpPassword", "singUpRepeatPassword" })
	public boolean isSingUpOk() {
		return Validator.isEmail(this.singUpEmail) &&
				Validator.isPassword(this.singUpPassword) &&
				this.singUpPassword.equals(this.singUpRepeatPassword);
	}

	@Command
	public void checkLogin() {
		final Object value = this.em.createQuery(
				"SELECT u FROM User u WHERE u.username = '"
						+ this.loginUserName + "'").getSingleResult();
		if (value instanceof User) {
			final User user = (User) value;
			
			if (
				user.getPassword().equals(DigestUtils.md5Hex(this.loginPassword))
			) {
				final Session webSession = Sessions.getCurrent();
				webSession.setAttribute("user", user);
				checkDatastores(user);
			} else {
				Messagebox.show("Incorrect password.", "Invalid login", Messagebox.OK, Messagebox.ERROR);
			}
		} else {
			Messagebox.show("User does not exists.", "Invalid login", Messagebox.OK, Messagebox.ERROR);
		}
	}
	
	private void redirectUser(User user) {
		if (user.isAdmin()){
			Executions.getCurrent().sendRedirect("administrator.zul");
		}else{
			Executions.getCurrent().sendRedirect("index.zul");
		}
	}
	
	public RUbioSeqConfiguration getConfiguration(){
		return (RUbioSeqConfiguration) this.em
				.createQuery("SELECT c FROM RUbioSeqConfiguration c")
				.getResultList().get(0);
	}

	private String getDataStoreName(String username) {
		String toret = username;
		int i = 1;
		while(!validDataStoreName(toret)){
			toret = toret+String.valueOf(i);
		}
		return toret;
	}

	private boolean validDataStoreName(String toret) {
		for(DataStore d : this.getDatastores()){
			if(toret.equals(d.getName())){
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<DataStore> getDatastores(){
		return (List<DataStore>) this.em
				.createQuery("SELECT d FROM DataStore d")
				.getResultList();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void checkDatastores(final User user) {
		
		List<DataStore> dataStores = (List<DataStore>) this.em
			.createQuery("SELECT d FROM DataStore d")
			.getResultList();
		boolean sharedDataStores = false;
		boolean userPrivateInputDataStores = false;
		boolean userPrivateOutputDataStores = false;
		boolean userPrivateInputOutputDataStores = false;
		for(DataStore d : dataStores){
			if(d.getMode().equals(DataStoreMode.Shared)){
				sharedDataStores = true;
			}else{
				if(d.getUser().equals(user)){
					if(d.getType().equals(DataStoreType.Input)){
						userPrivateInputDataStores = true;
					} else if(d.getType().equals(DataStoreType.Output)){
						userPrivateOutputDataStores = true;
					} else{
						userPrivateInputOutputDataStores = true;
					}
				}
			}
		}
		StringBuilder msgSB = new StringBuilder("");
		if(sharedDataStores && userPrivateInputDataStores && !userPrivateOutputDataStores && !userPrivateInputOutputDataStores){
			msgSB
			.append("There are no output datastores, so that you will not be able to select destination folders.\n")
			.append("If you are an administrator, you should go to the administration panel and manage the datastores.\n")
			.append("If you are a normal user, you should contact to the administrator and let him/her know that you need an output datastore.");
		} else if(sharedDataStores && !userPrivateInputDataStores && !userPrivateOutputDataStores && !userPrivateInputOutputDataStores){
			msgSB
			.append("There are no output datastores, so that you will not be able to select destination folders.\n")
			.append("If you are an administrator, you should go to the administration panel and manage the datastores.\n")
			.append("If you are a normal user, you should contact to the administrator and let him/her know that you need an output datastore.");
		} else if(!sharedDataStores && userPrivateInputDataStores && !userPrivateOutputDataStores && !userPrivateInputOutputDataStores){
			msgSB
			.append("There are no input datastores, so that you will not be able to select input files.\n")
			.append("If you are an administrator, you should go to the administration panel and manage the datastores.\n")
			.append("If you are a normal user, you should contact to the administrator and let him/her know that you need an input datastore.");
		}  else if(!sharedDataStores && !userPrivateInputDataStores && userPrivateOutputDataStores && !userPrivateInputOutputDataStores){
			msgSB
			.append("There are no input datastores, so that you will not be able to select input files.\n")
			.append("If you are an administrator, you should go to the administration panel and manage the datastores.\n")
			.append("If you are a normal user, you should contact to the administrator and let him/her know that you need an input datastore.");
		}  else if(!sharedDataStores && !userPrivateInputDataStores && !userPrivateOutputDataStores && !userPrivateInputOutputDataStores){
			msgSB
			.append("There are no datastores, so that you will not be able to select or save any data.\n")
			.append("If you are an administrator, you should go to the administration panel and manage the datastores.")
			.append("If you are a normal user, you should contact to the administrator in order to resolve this issue.");
		} else{
			redirectUser(user);
		}
		
		if(!msgSB.toString().equals("")){
			Messagebox.show(msgSB.toString(), "Warning", Messagebox.OK, Messagebox.EXCLAMATION, new org.zkoss.zk.ui.event.EventListener() {
			    public void onEvent(Event evt) throws InterruptedException {
			        if (evt.getName().equals("onOK")) {
			        	redirectUser(user);
			        } 
			    }
			});
		}
	}

	@Command
	@NotifyChange({ "singUpUsername", "singUpEmail", "singUpPassword",
			"singUpRepeatPassword", "showLogin", "adminLogin",
			"loginWindowTitle", "showNewAccount" })
	public void singUp() {
		try{
			this.em.createQuery(
				"SELECT u FROM User u where u.username = '"+ singUpUsername +"'")
				.getSingleResult();
			Messagebox.show(
				"User already exists. If you forgot your password press 'I forgot my password' in the login box.", 
				"User already exists", 
				Messagebox.OK, 
				Messagebox.ERROR
			);
		}
		catch(javax.persistence.NoResultException ex){
			// User does not exist in the DB so we can create it
			if (this.singUpPassword.equals(this.singUpRepeatPassword)) {
				
				User newUser = new User();
				newUser.setUsername(singUpUsername);
			newUser.setAdmin(false);
				newUser.setEmail(singUpEmail);
				newUser.setPassword(DigestUtils.md5Hex(singUpPassword));
				
				try {
					this.em.getTransaction().begin();
					this.em.persist(newUser);
					this.em.flush();
					this.em.getTransaction().commit();

					this.createUserDataStore(newUser);
					
					Messagebox.show(
						"Your account has been created. Please, try to log in with your username and password.", 
						"Account created", 
						Messagebox.OK, 
						Messagebox.INFORMATION
					);
					
					this.showNewAccount = false;
					this.userProfile();
					
					this.singUpUsername = "";
					this.singUpEmail = "";
					this.singUpPassword = "";
					this.singUpRepeatPassword = "";
					
				} catch (Exception e) {
					Messagebox.show(
						"Sorry, an error happened while trying to singing you up. Please, try again later.", 
						"Sing Up Error", 
						Messagebox.OK, 
						Messagebox.ERROR
					);
				}
			} else {
				Messagebox.show(
					"You must introduce the same password in both password fields.", 
					"Password does not match", 
					Messagebox.OK, 
					Messagebox.ERROR
				);
			}
		}
	}
	
	private void createUserDataStore(User newUser) {
		RUbioSeqConfiguration config = this.getConfiguration();
		if(config.isCreatePrivateDatastoresOnUserRegistration()){
			if(Utils.isValidDirectory(config.getPrivateDatastoresRootDirectory())){
				File dataStorePath = new File(config.getPrivateDatastoresRootDirectory()+"/"+newUser.getUsername());
				dataStorePath.mkdirs();
				DataStore newDataStore = new DataStore();
				newDataStore.setMode(DataStoreMode.Private);
				newDataStore.setName(getDataStoreName(newUser.getUsername()));
				newDataStore.setPath(dataStorePath.getAbsolutePath());
				newDataStore.setType(DEFAULT_DS_TYPE);
				newDataStore.setUser(newUser);
				
				this.em.getTransaction().begin();
				this.em.persist(newDataStore);
				this.em.flush();
				this.em.getTransaction().commit();
			}
		}
	}

	/*
	 * profile.zul
	 */
	public User getCurrentUser() {
		User user = (User) Sessions.getCurrent().getAttribute("user");
		return user;
	}
	
	private String currentUserEmailTmp;
	
	public String getCurrentUserEmailTmp() {
		return currentUserEmailTmp;
	}
	
	public void setCurrentUserEmailTmp(String currentUserEmailTmp) {
		this.currentUserEmailTmp = currentUserEmailTmp;
	}
	
	private String currentPassword, newPassword, newPasswordRepeat;
	
	public String getCurrentPassword() {
		return currentPassword;
	}
	
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	
	public String getNewPassword() {
		return newPassword;
	}
	
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	public String getNewPasswordRepeat() {
		return newPasswordRepeat;
	}
	
	public void setNewPasswordRepeat(String newPasswordRepeat) {
		this.newPasswordRepeat = newPasswordRepeat;
	}
	
	@DependsOn({"newPassword", "newPasswordRepeat", "currentPassword"})
	public boolean isChangePasswordOk() {
		return Validator.isPassword(this.newPassword)
				&& this.newPassword.equals(this.newPasswordRepeat)
				&& this.getCurrentUser().getPassword()
						.equals(DigestUtils.md5Hex(currentPassword));
	}
	
	@NotifyChange("currentUser")
	@Command
	public void updatePersonalInformation(){
		User currentUser = this.getCurrentUser();
		try{
			this.em.createQuery(
				"SELECT u FROM User u where u.username = '"+ currentUser.getUsername() +"'")
				.getSingleResult();
			currentUser.setEmail(currentUserEmailTmp);
			this.em.getTransaction().begin();
			this.em.merge(currentUser);
			this.em.flush();
			this.em.getTransaction().commit();
		}
		catch(javax.persistence.NoResultException ex){
			Messagebox.show(
					"User does not exist.", 
					"User does not exist.", 
					Messagebox.OK, 
					Messagebox.ERROR
					);
		}
	}
	
	@NotifyChange("currentUser")
	@Command
	public void changePassword(){
		if (!this.getCurrentUser().getPassword()
				.equals(DigestUtils.md5Hex(currentPassword))) {
			Messagebox.show(
					"Current password is incorrect.", 
					"Current password is incorrect", 
					Messagebox.OK, 
					Messagebox.ERROR
					);
		}else{
			User currentUser = this.getCurrentUser();
			currentUser.setPassword(DigestUtils.md5Hex(newPassword));
			this.em.getTransaction().begin();
			this.em.merge(currentUser);
			this.em.flush();
			this.em.getTransaction().commit();
		}
	}
	
	private boolean showLogin = false;
	private boolean adminLogin = false;
	private boolean showNewAccount = false;
	
	public boolean isShowLogin(){
		return this.showLogin;
	}
	
	public boolean isShowNewAccount(){
		return this.showNewAccount;
	}
	
	@NotifyChange({"showNewAccount"})
	@Command
	public void showNewAccount(){
		this.showNewAccount = true;
	}

	@NotifyChange({"showNewAccount"})
	@Command
	public void cancelNewAccount(){
		this.showNewAccount = false;
	}
	
	@NotifyChange({"showLogin", "adminLogin", "loginWindowTitle", "loginUserName", "loginPassword"})
	@Command
	public void adminProfile(){
		this.loginUserName = "admin";
		this.showLogin = true;
		this.adminLogin = true;
	}
	
	@NotifyChange({"showLogin", "adminLogin", "loginWindowTitle", "loginUserName", "loginPassword"})
	@Command
	public void cancelLogin(){
		this.loginUserName = "";
		this.showLogin = false;
		this.adminLogin = false;
	}
	
	@NotifyChange({"showLogin", "adminLogin", "loginWindowTitle"})
	@Command
	public void userProfile(){
		this.showLogin = true;
		this.adminLogin = false;
	}
	
	public boolean getAdminLogin(){
		return adminLogin;
	}
	
	public String getLoginWindowTitle(){
		return this.adminLogin?"Introduce the admin password":"Login with you username and password";
	}
}
