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
package es.uvigo.ei.sing.rubioseq.gui.view.models.progress;

import es.uvigo.ei.sing.rubioseq.gui.view.models.ViewExperimentModel;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.RUbioSeqEvent.EventType;

/**
 * 
 * @author hlfernandez
 *
 */
public class ExperimentProgress {

	private ViewExperimentModel viewExperimentModel;
	private double progress = 0d;
	private boolean aborted = false;
	
	public ExperimentProgress(ViewExperimentModel viewExperimentModel){
		this.progress = 0;
		this.viewExperimentModel = viewExperimentModel;
	}
	
	public int getProgress(){
		return (int) Math.round(progress);
	}
	
	public void setProgress(int progress){
		this.progress = progress;
	}
	
	public boolean isAborted() {
		return aborted;
	}
	
	public void setAborted(boolean aborted) {
		this.aborted = aborted;
	}
	
	public synchronized void updateProgress(RUbioSeqEvent event){
		if(event!=null){
			System.err.println("[Received RUbioSeq event] " + event.toString());
			if(event.getType().equals(EventType.STEP_ENDS) && event.getExecutionLevel() != 0){
				double partialProgress = this.viewExperimentModel
						.getExperimentData().getIncrement(event,
								this.viewExperimentModel.getStagesCount());
				if (Math.round(this.progress + partialProgress) <= 100){ // Decimals at high positions may cause 100% to be 100.00000001 or 99.99999999 so we need to round it
					this.progress+=partialProgress;
				}
				System.err.println("\t[Experiment Progress] Incrementing progress in " + partialProgress + " --> New progress is " + this.progress + "\n");
			} else if (event.getType().equals(EventType.STEP_ABORTED)){
				this.aborted = true;
			}
		}
	}
	
	public long getExperimentStartTimestamp(){
		return this.viewExperimentModel.getExperimentStartTimestamp();
	}
}
