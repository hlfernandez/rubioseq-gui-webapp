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

import java.util.List;

import javax.persistence.EntityManager;

import es.uvigo.ei.sing.rubioseq.DataStore;
import es.uvigo.ei.sing.rubioseq.RUbioSeqConfiguration;

/**
 * 
 * @author hlfernandez
 *
 */
public class DBUtils {
	
	@SuppressWarnings("unchecked")
	public static List<DataStore> getDatastores(EntityManager em) {
		return (List<DataStore>) em.createQuery("SELECT d FROM DataStore d")
				.getResultList();
	}
	
	public static RUbioSeqConfiguration getConfiguration(EntityManager em){
		return (RUbioSeqConfiguration) em
				.createQuery("SELECT c FROM RUbioSeqConfiguration c")
				.getResultList().get(0);
	}
}
