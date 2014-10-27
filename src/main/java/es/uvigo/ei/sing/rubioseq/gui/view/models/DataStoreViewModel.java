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
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.Messagebox;

import es.uvigo.ei.sing.rubioseq.DataStore;
import es.uvigo.ei.sing.rubioseq.DataStoreMode;
import es.uvigo.ei.sing.rubioseq.DataStoreType;
import es.uvigo.ei.sing.rubioseq.User;

/**
 * 
 * @author hlfernandez
 *
 */
public class DataStoreViewModel {
	
	private DataStore currentDatastore;
	private boolean edit = false; // True: update (merge); False: persist
	
	@PersistenceContext(type=PersistenceContextType.EXTENDED) 
	private EntityManager em;
	
	public DataStore getCurrentDatastore() {
		return this.currentDatastore;
	}
	
	public void setCurrentDatastore(DataStore currentDatastore) {
		this.currentDatastore = currentDatastore;
	}
	
	@SuppressWarnings("unchecked")
	public List<DataStore> getDatastores(){
		return (List<DataStore>) this.em
				.createQuery("SELECT d FROM DataStore d")
				.getResultList();
	}
	
	@NotifyChange({"currentDatastore", "datastores"})	
	@Command
	public void edit(@BindingParam("datastore") DataStore dataStore) {
		if(canEditOrDeleteDataStore(dataStore)){
			this.edit = true;
			this.currentDatastore = dataStore;
			this.em.detach(this.currentDatastore);
		}
	}
	
	@NotifyChange({"datastores"})	
	@Command
	public void delete(@BindingParam("datastore") DataStore dataStore){
		if(canEditOrDeleteDataStore(dataStore)){
			this.em.getTransaction().begin();
			this.em.remove(dataStore);
			this.em.getTransaction().commit();
			this.currentDatastore = null;
		}
	}
	
	private boolean canEditOrDeleteDataStore(DataStore d){
		if(d.getPath().equals("/") && d.getUser().isAdmin() && d.getType().equals(DataStoreType.Input_Output)){
			Messagebox.show("This datastore cannot be edited or removed.",
					"Invalid datastore", Messagebox.OK, Messagebox.ERROR);
			return false;
		} else {
			return true;
		}
	}
	
	@NotifyChange({"currentDatastore", "datastores"})	
	@Command
	public void save(){
		
		if(this.currentDatastore.getPath().endsWith("/") && !this.currentDatastore.getPath().equals("/")){
			this.currentDatastore
					.setPath(this.currentDatastore.getPath().substring(0,
							this.currentDatastore.getPath().lastIndexOf("/")));
		}
		
		if(this.currentDatastore.getMode().equals(DataStoreMode.Shared)){
			this.currentDatastore.setType(DataStoreType.Input);
			this.currentDatastore.setUser(null);
		}
		
		if (!this.edit){
			if(!validDataStoreName()){
				Messagebox.show("The datastore name is in use, please, choose another datastore name.",
						"Invalid datastore name", Messagebox.OK, Messagebox.ERROR);
				return;
			}
			this.em.getTransaction().begin();
			this.em.persist(currentDatastore);
			this.em.flush();
			this.em.getTransaction().commit();
		} else{
			this.em.getTransaction().begin();
			this.em.merge(currentDatastore);
			this.em.flush();
			this.em.getTransaction().commit();
		}
		
		this.edit = false;
		this.currentDatastore = null;
	}
	
	private boolean validDataStoreName() {
		for(DataStore d : this.getDatastores()){
			if(d.getName().equals(this.currentDatastore.getName())){
				return false;
			}
		}
		return true;
	}

	@NotifyChange({"currentDatastore"})	
	@Command
	public void cancelEdit(){		
		this.currentDatastore = null;
	}
	
	@NotifyChange({"currentDatastore"})
	@Command
	public void newDatastore(){
		this.edit = false;
		this.currentDatastore = new DataStore();
	}
	
	public List<DataStoreMode> getModeList(){
		return Arrays.asList(DataStoreMode.values());
	}
	
	@DependsOn("currentDatastore")
	public List<DataStoreType> getTypeList(){
		if(this.currentDatastore!=null && this.currentDatastore.getMode()!=null && this.currentDatastore.getMode().equals(DataStoreMode.Shared)){
			return Arrays.asList(new DataStoreType[]{DataStoreType.Input});
		}
		return Arrays.asList(DataStoreType.values());
	}
	
	@SuppressWarnings("unchecked")
	public List<User> getUsersList(){
		return (List<User>) this.em
				.createQuery("SELECT d FROM User d")
				.getResultList();
	}

	public boolean isValidDataStore(){
		if(this.currentDatastore == null || this.currentDatastore.getPath() == null){
			return false;
		}
		if(new File(this.currentDatastore.getPath()).isDirectory()){
			return true;
		}
		return false;
	}

	@DependsOn({"currentDatastore.mode"})
	public boolean isTypeRowVisible(){
		if (this.currentDatastore!=null && this.currentDatastore.getMode()!=null && this.currentDatastore.getMode().equals(DataStoreMode.Private)){
			return true;
		}else{
			return false;
		}
	}

	@DependsOn({"currentDatastore.path","currentDatastore.type","currentDatastore.mode","currentDatastore.user"})
	public boolean isDataStoreOk(){
		if(isValidDataStore()){
			if(this.currentDatastore.getMode() == null){
				return false;
			}
			if(this.currentDatastore.getMode().equals(DataStoreMode.Shared)){
				return true;
			} else{
				if(this.currentDatastore.getUser() == null){
					return false;
				} else return true;
			}
		} else return false;
	}
	
	private boolean dataStoreInfoVisible = true;
	
	public boolean isDataStoreInfoVisible() {
		return dataStoreInfoVisible;
	}

	@NotifyChange({"dataStoreInfoVisible"})
	@Command
	public void hideDataStoreInfo(){
		this.dataStoreInfoVisible = false;
	}
}
