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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.commons.codec.digest.DigestUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;

import es.uvigo.ei.sing.rubioseq.DataStore;
import es.uvigo.ei.sing.rubioseq.User;
import es.uvigo.ei.sing.rubioseq.gui.util.Validator;

/**
 * 
 * @author hlfernandez
 *
 */
public class UsersViewModel {
	@PersistenceContext(type=PersistenceContextType.EXTENDED) 
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<User> getUsers(){
		return (List<User>) this.em.createQuery("SELECT e FROM User e").getResultList();
	}
	
	@NotifyChange({"users"})	
	@Command
	public void delete(@BindingParam("user") final User user){
		@SuppressWarnings("unchecked")
		List<DataStore> dataStores = (List<DataStore>) this.em
				.createQuery("SELECT d FROM DataStore d")
				.getResultList();
		for(DataStore d : dataStores){
			if(d.getUser().equals(user)){
				this.em.getTransaction().begin();
				this.em.remove(d);
				this.em.flush();
				this.em.getTransaction().commit();
			}
		}
		this.em.getTransaction().begin();
		this.em.remove(user);
		this.em.flush();
		this.em.getTransaction().commit();
	}

	@NotifyChange({"users"})
	@Command("editUser")
	public void editUser(@BindingParam("user") User user) {
		this.em.getTransaction().begin();
		this.em.merge(user);
		this.em.getTransaction().commit();
	}
	
	private User changePasswordUser;
	private String newPassword;
	private String repeatNewPassword;
	
	public User getChangePasswordUser() {
		return changePasswordUser;
	}
	
	public void setChangePasswordUser(User changePasswordUser) {
		this.changePasswordUser = changePasswordUser;
	}
	
	public String getNewPassword() {
		return newPassword;
	}
	
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	public String getRepeatNewPassword() {
		return repeatNewPassword;
	}
	
	public void setRepeatNewPassword(String repeatNewPassword) {
		this.repeatNewPassword = repeatNewPassword;
	}
	
	@NotifyChange({"changePasswordUser"})
	@Command
	public void changePassword(@BindingParam("user") User user) {
		this.changePasswordUser = user;
	}
	
	@NotifyChange({"changePasswordUser"})
	@Command
	public void cancelChangePassword() {
		this.changePasswordUser = null;
	}
	
	@NotifyChange({"changePasswordUser"})
	@Command
	public void confirmChangePassword() {
		this.changePasswordUser.setPassword(DigestUtils.md5Hex(newPassword));
		this.em.getTransaction().begin();
		this.em.merge(this.changePasswordUser);
		this.em.flush();
		this.em.getTransaction().commit();
		this.changePasswordUser = null;
	}
	
	@DependsOn({"newPassword", "repeatNewPassword"})
	public boolean isChangePasswordOk() {
		return Validator.isPassword(this.newPassword)
				&& this.newPassword.equals(this.repeatNewPassword);
	}
}