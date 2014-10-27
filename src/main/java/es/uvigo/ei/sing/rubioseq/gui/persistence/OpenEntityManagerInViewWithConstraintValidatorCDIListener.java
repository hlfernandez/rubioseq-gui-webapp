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
package es.uvigo.ei.sing.rubioseq.gui.persistence;

import org.zkoss.zk.ui.Execution;
import org.zkoss.zkplus.jpa.JpaUtil;
import org.zkoss.zkplus.jpa.OpenEntityManagerInViewListener;

import es.uvigo.ei.sing.rubioseq.validation.EMInjectorConstraintValidatorFactory;

/**
 * 
 * @author dgpena
 *
 */
public class OpenEntityManagerInViewWithConstraintValidatorCDIListener extends
		OpenEntityManagerInViewListener {

	@Override
	public void init(Execution exec, Execution parent) throws Exception {
		super.init(exec, parent);
		
		EMInjectorConstraintValidatorFactory.setThreadLocalEntityManager(JpaUtil.getEntityManager());
	}
}
