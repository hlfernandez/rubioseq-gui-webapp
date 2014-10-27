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

import javax.persistence.EntityManager;

import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.Messagebox;

import es.uvigo.ei.sing.rubioseq.RUbioSeqConfiguration;
import es.uvigo.ei.sing.rubioseq.gui.util.DBInitializer;
import es.uvigo.ei.sing.rubioseq.gui.util.Utils;

/**
 * 
 * @author hlfernandez
 *
 */
public class AdministratorViewModel {
	
	private EntityManager em = DBInitializer.getRUbioSeqEntityMananger();
	
	@Init
	public void init(){
		RUbioSeqConfiguration config = this.getConfiguration();
		
		if (config.isCreatePrivateDatastoresOnUserRegistration()
				&& !Utils.isValidDirectory(config
						.getPrivateDatastoresRootDirectory())) {
			Messagebox
					.show("Configuration warning: the specified path for the private datastores root directory ("
							+ config.getPrivateDatastoresRootDirectory()
							+ ") is not a valid directory. Please, go to the configuration and edit this property.",
							"Configuration problem", Messagebox.OK,
							Messagebox.EXCLAMATION);
		}

		if (!Utils.isValidFile(config.getRubioseqCommand())) {
			Messagebox
					.show("Configuration warning: the specified RUbioSeq Path ("
							+ config.getRubioseqCommand()
							+ ") is not a valid file. Please, go to the configuration and edit this property.",
							"Configuration problem", Messagebox.OK,
							Messagebox.EXCLAMATION);
		}
		this.em.close();
	}
	
	public RUbioSeqConfiguration getConfiguration(){
		return (RUbioSeqConfiguration) this.em
				.createQuery("SELECT c FROM RUbioSeqConfiguration c")
				.getResultList().get(0);
	}
}
