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
package es.uvigo.ei.sing.rubioseq.gui.util;

import java.io.File;

import org.zkoss.zk.ui.Sessions;

import es.uvigo.ei.sing.rubioseq.DataStore;
import es.uvigo.ei.sing.rubioseq.DataStoreMode;
import es.uvigo.ei.sing.rubioseq.User;
import es.uvigo.ei.sing.rubioseq.gui.macros.RUbioSeqFile;

/**
 * 
 * @author hlfernandez
 *
 */
public class Utils {
	
	/**
	 * css class to display invalid paths that is defined in file css/style.css
	 */
	public static final String SCLASS_INVALID_PATH = "invalidPath";

	/**
	 * Converts a path to a RUbioSeqFile. Considerations: the currentUser must
	 * be allocated in the current session.
	 * @param path 
	 * @return The corresponding RUbioSeqFile for the path introduced
	 */
	public static RUbioSeqFile getRUbioSeqFile(String path) {
		User currentUser = getCurrentUser();
		for (DataStore d : DBUtils.getDatastores(DBInitializer
				.getRUbioSeqEntityMananger())) {
			if (validDataStore(d, currentUser) && path.startsWith(d.getPath())) {
				RUbioSeqFile toret = new RUbioSeqFile(new File(path), d);
				return toret;
			}
		}
		return null;
	}
	
	private static boolean validDataStore(DataStore d, User currentUser) {
		return ((d.getMode().equals(DataStoreMode.Shared)) || (d.getMode()
				.equals(DataStoreMode.Private) && d.getUser()
				.equals(currentUser)));
	}

	public static User getCurrentUser(){
		User user = (User) Sessions.getCurrent().getAttribute("user");
		return user;
	}
	
	public static boolean isValidDirectory(String path) {
		File file = new File(path);
		return (file.isDirectory() && file.canWrite());
	}

	public static boolean isValidFile(String path) {
		File file = new File(path);
		return (file.exists() && file.isFile());
	}
	
	public static String removeFileExtension(String fileName){
		if(fileName.contains(".")){
			return fileName.substring(0, fileName.indexOf("."));
		} else return fileName;
	}
	
	public static String getDirectoryNameWithFinalSlash(String dir){
		if(dir.endsWith("/")){
			return dir;
		}
		else return dir + "/";
	}
	
	public static boolean existDirectory(String path) {
		File f = new File(path);
		return f.isDirectory();
	}
}
