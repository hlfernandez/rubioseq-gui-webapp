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
package es.uvigo.ei.sing.rubioseq.gui.view.models.experiments;

import es.uvigo.ei.sing.rubioseq.gui.macros.RUbioSeqFile;

/**
 * 
 * @author hlfernandez
 *
 */
public class MethylationSample {
	
	private RUbioSeqFile sample1; /* Compulsory */
	private RUbioSeqFile sample2; /* Optional */
	
	public RUbioSeqFile getSample1() {
		return sample1;
	}
	
	public void setSample1(RUbioSeqFile sample1) {
		this.sample1 = sample1;
	}
	
	public RUbioSeqFile getSample2() {
		return sample2;
	}
	
	public void setSample2(RUbioSeqFile sample2) {
		this.sample2 = sample2;
	}
}
