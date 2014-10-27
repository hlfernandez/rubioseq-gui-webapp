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
package es.uvigo.ei.sing.rubioseq.gui.macros;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;

import es.uvigo.ei.sing.rubioseq.DataStore;
import es.uvigo.ei.sing.rubioseq.DataStoreMode;
import es.uvigo.ei.sing.rubioseq.DataStoreType;
import es.uvigo.ei.sing.rubioseq.User;

/**
 * 
 * @author hlfernandez
 *
 */
public class FileChooserVM {

	private static final String DATASTORE = "datastore";
	private static final String ROOT_FILE_LIST_FILES = "root-file-list-files";
	
	private Session currentSession;
	
	@PersistenceContext(type=PersistenceContextType.EXTENDED) 
	private EntityManager em;
	
	private FileChooser fileChooserController;
	private DataStore currentDataStore;
	private File selectedFile;
	
	private List<File> files = new LinkedList<File>();
	
	@Init
	public void init(@ContextParam(ContextType.COMPONENT) Component component,
			@ContextParam(ContextType.SESSION) Session session){
		fileChooserController = (FileChooser) component.getParent();
		currentSession = session;
	}
	
	public List<DataStore> getDataStores(){
		@SuppressWarnings("unchecked")
		List<DataStore> dataStores = (List<DataStore>) this.em.createQuery(
				"SELECT e FROM DataStore e").getResultList();
		List<DataStore> toret = new LinkedList<DataStore>();
		String type = fileChooserController.getType();
		User currentUser = (User) Sessions.getCurrent().getAttribute("user");
		for(DataStore dt : dataStores){
			if (type.toUpperCase().equals("INPUT")){
				/*
				 *  Show only input datastores, that is:
				 *  	- Mode = Shared && Type = Input
				 *  	- Mode = Private && (Type = Input || Type = Input_Output) && User = Current User
				 */
				if ((dt.getType().equals(DataStoreType.Input) && 
						dt.getMode().equals(DataStoreMode.Shared)) || 
						((dt.getType().equals(DataStoreType.Input) || 
								dt.getType().equals(DataStoreType.Input_Output)) && 
								dt.getMode().equals(DataStoreMode.Private) && 
									dt.getUser().equals(currentUser))){
					toret.add(dt);
				} 
			} else if (type.toUpperCase().equals("OUTPUT")){
				/*
				 *  Show only output datastores, that is:
				 *  	- Mode = Shared && Type = Output
				 *  	- Mode = Private && (Type = Output || Type = Input_Output) && User = Current User
				 */
				if ((dt.getType().equals(DataStoreType.Output) && 
						dt.getMode().equals(DataStoreMode.Shared)) || 
						((dt.getType().equals(DataStoreType.Output) || 
								dt.getType().equals(DataStoreType.Input_Output)) && 
								dt.getMode().equals(DataStoreMode.Private) && 
									dt.getUser().equals(currentUser))){
					toret.add(dt);
				} 
			}
		}
		return toret;
	}
	
	public List<RUbioSeqFile> getCurrentFiles() {
		List<RUbioSeqFile> toret = new LinkedList<RUbioSeqFile>();
		for (File f : this.files) {
			RUbioSeqFile currentRSFile = new RUbioSeqFile(f, getCurrentDataStore());
			if (validFile(currentRSFile)) {
				toret.add(currentRSFile);
			}
		}
		return toret;
	}
	
	private boolean validFile(RUbioSeqFile f) {
		if (this.getFilesFilter().equals("")) {
			// Not use filter, accept all files
			return true;
		}
		String fileName = f.getAbsolutePathWithDatastoreName();
		if (fileName.contains(this.getFilesFilter())) {
			return true;
		} else {
			return false;
		}
	}

	@NotifyChange({"selectedFile"})
	@Command
	public void changeSelectedFile(@BindingParam("file") File file){
//		fileChooserController.getPathbox().setText(file.getAbsolutePath());
		fileChooserController.setSelectedFile(new RUbioSeqFile(selectedFile, this.getCurrentDataStore()));
//		fileChooserController.getPathbox().setText(new RUbioSeqFile(selectedFile, this.getCurrentDataStore()).getAbsolutePathWithDatastoreName());
		this.selectedFile = file;
	}
	
	@NotifyChange({"currentFiles", "filesFilter"})
	@Command
	public void changeListFiles(@BindingParam("file") File file){
		if (file.isDirectory()){
			this.filesFilter = "";
//			if(file!=null){
//				System.err.println("[DEBUG] file is " + file.getAbsolutePath() );
//				if(file.listFiles() == null){
//					System.err.println("[DEBUG] file.listFiles() is null" );
//				} else{
//					System.err.println("[DEBUG] file.listFiles() isn't null" );
//				}
//			}
			this.files = Arrays.asList(file.listFiles());
			Collections.sort(this.files, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					if ((o1.isDirectory() && o2.isDirectory())
							|| (o1.isFile() && o2.isFile())) {
						return o1.getAbsolutePath().compareTo(
								o2.getAbsolutePath());
					} else{
						if(o1.isDirectory()){
							return -1;
						} else{
							return 1;
						}
					}
				}
			});
			this.currentSession.setAttribute(ROOT_FILE_LIST_FILES, file);
		}
	}
	
	@NotifyChange({"currentFiles", "filesFilter"})
	@Command
	public void upDirectory(){
		if(this.files.size()>0){
			this.changeListFiles(this.files.get(0).getParentFile().getParentFile());
		} else {
			this.changeListFiles(this.selectedFile.getParentFile());
		}
	}
	
	@DependsOn({"currentFiles", "currentDataStore" })
	public boolean isAllowedUpDirectory(){
		if(!this.files.isEmpty() && this.currentDataStore != null){
			return !this.files.get(0).getParentFile().equals(new File(this.currentDataStore.getPath()));
		} else if(this.selectedFile!=null && this.currentDataStore!=null){
			return !this.selectedFile.getParentFile().equals(new File(this.currentDataStore.getPath())) ||
					!this.selectedFile.equals(new File(this.currentDataStore.getPath()));
		}
		return false;
	}
	
	private String folderName;
	private boolean createFolder = false;
	
	public boolean isCreateFolder() {
		return createFolder;
	}
	
	@NotifyChange({"createFolder"})	
	@Command
	public void showCreateFolder(){
		this.createFolder = true;
	}
	
	public String getFolderName() {
		return folderName;
	}
	
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	
	@NotifyChange({"createFolder"})	
	@Command 
	public void cancelCreateFolder(){
		this.createFolder = false;
	}

	@DependsOn({"currentFiles", "currentDataStore"})
	public boolean isAllowedCreateFolder(){
		return this.files!=null && this.currentDataStore!=null;
	}
	
	@DependsOn("folderName")
	public boolean isEnabledCreateFolder(){
		return this.folderName!=null && !this.folderName.equals("");
	}
	
	@NotifyChange({"createFolder", "currentFiles"})	
	@Command
	public void createFolder(){
		this.createFolder = false;
		File base = null;
		if(this.files.size()>0){
			base = this.files.get(0).getParentFile();
		} else {
			base = this.selectedFile;
		}
		File newFolder = new File(base, folderName);
		newFolder.mkdir();
		this.changeListFiles(base);
	}
	
	
	@NotifyChange({"currentFiles", "filesFilter"})
	@Command
	public void changeDataStore(){
		File currentDataStoreFile = new File(this.currentDataStore.getPath());
		if(currentDataStoreFile.isDirectory()){
			this.changeListFiles(currentDataStoreFile);
		}else{
			Messagebox.show(
				"This datastore cannot be used. Please, contact to the administrator of the system.", 
				"Invalid datastore", 
				Messagebox.OK, 
				Messagebox.ERROR
			);
		}
	}
	
	public DataStore getCurrentDataStore() {
		return currentDataStore;
	}
	
	public void setCurrentDataStore(DataStore currentDataStore) {
		this.currentDataStore = currentDataStore;
		this.currentSession.setAttribute(DATASTORE, this.currentDataStore);
	}
	
	public RUbioSeqFile getSelectedFile() {
		return new RUbioSeqFile(selectedFile, this.getCurrentDataStore());
	}
	
	public void setSelectedFile(RUbioSeqFile selectedFile) {
		this.selectedFile = selectedFile.getFile();
	}

	@GlobalCommand
	@NotifyChange({"currentFiles", "currentDataStore" })
	public void updateFileChooserVM() {
		DataStore d = (DataStore) this.currentSession.getAttribute(DATASTORE);
		if (d!=null && this.getDataStores().contains(d)){
			this.currentDataStore = d;
			File f = (File) this.currentSession.getAttribute(ROOT_FILE_LIST_FILES);
			if(f!=null){
				this.changeListFiles(f);
			}
		}
	}
	
	private String filesFilter = "";
	
	public String getFilesFilter() {
		return filesFilter;
	}
	
	public void setFilesFilter(String filesFilter) {
		this.filesFilter = filesFilter;
	}
	
	@DependsOn({"currentFiles", "filesFilter"})
	public boolean isAllowedResetFilesFilter(){
		return this.filterEnabled();
	}
	
	private boolean filterEnabled(){
		return !this.getFilesFilter().equals("") && this.files.size() > 0;
	}
	
	@NotifyChange({"currentFiles", "filesFilter"})
	@Command
	public void resetFilesFilter(){
		this.filesFilter = "";
	}
	
	@NotifyChange({"currentFiles"})
	@Command
	public void onFilesFilterTBChanged(){
	}
}
