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

/**
 * 
 * @author hlfernandez
 *
 */
public class ChipSeqUnit {

	private Sample sampleTreatment = new Sample(); /* Compulsory */
	private Sample sampleInput = new Sample();		/* Optional */
	
	public Sample getSampleTreatment() {
		return sampleTreatment;
	}
	
	public void setSampleTreatment(Sample sampleTreatment) {
		this.sampleTreatment = sampleTreatment;
	}
	
	public Sample getSampleInput() {
		return sampleInput;
	}
	
	public void setSampleInput(Sample sampleInput) {
		this.sampleInput = sampleInput;
	}
}
