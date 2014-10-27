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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author hlfernandez
 *
 */
public class CSExperiment {

	private List<ChipSeqUnit> chipSeqUnits = new LinkedList<ChipSeqUnit>();
	private boolean replicatesFlag;
	
	public List<ChipSeqUnit> getChipSeqUnits() {
		return chipSeqUnits;
	}
	
	public void setChipSeqUnits(List<ChipSeqUnit> chipSeqUnits) {
		this.chipSeqUnits = chipSeqUnits;
	}
	
	public boolean isReplicatesFlag() {
		return replicatesFlag;
	}
	
	public void setReplicatesFlag(boolean replicatesFlag) {
		this.replicatesFlag = replicatesFlag;
	}
	
	public List<Integer> getReplicatesFlagValues(){
		return Arrays.asList(new Integer[]{0,1});
	}
	
}
