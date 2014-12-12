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

import es.uvigo.ei.sing.rubioseq.DataStore;
import es.uvigo.ei.sing.rubioseq.gui.util.Utils;

/**
 * Represents a file belonging to a specific DataStore.
 * @author hlfernandez
 * @see DataStore
 */
public class RUbioSeqFile {

	private File file;
	private DataStore datastore;
	
	public RUbioSeqFile(File f, DataStore d){
		this.file = f;
		this.datastore = d;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public String getAbsolutePathWithDatastoreName(){
		if(this.datastore.getPath().equals("/")){
			return datastore.getName() + file.getAbsolutePath();
		}else {
			return file.getAbsolutePath().replace(datastore.getPath(), datastore.getName());
		}
	}
	
	public String getImage() {
		if (file.isDirectory()) {
			return "/imgs/icons/folder-16.png";
		} else
			return "/imgs/icons/file-16.png";
	}

	public DataStore getDatastore() {
		return datastore;
	}
	
	public String getSclass(){
		return this.file == null || !this.file.exists() ? Utils.SCLASS_INVALID_PATH
				: "";
	}
}

