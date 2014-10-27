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

import es.uvigo.ei.sing.rubioseq.gui.util.Utils;

/**
 * 
 * @author hlfernandez
 *
 */
public class RUbioSeqEvent {
	
	public static final String RUBIOSEQ_EVENT = "RUBIOSEQ_EVENT";
	
	private String message;
	private EventType type;
	private String date;
	private Integer step, totalSteps;
	private Integer executionLevel;
	private String sampleName;
	private ExtraStage extraStage;
	
	public enum EventType {
		STEP_STARTS("STEP_STARTS"), 
		STEP_ENDS("STEP_ENDS"),
		STEP_ABORTED("STEP_ABORTED");
		
		private final String displayName;
		
		private EventType(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
	}
	
	public enum ExtraStage {
		SPLIT_FILES_CONCAT,
		SNV_TC_ANALYSIS
	}
	
	public static final String SPLIT_FILES_CONCAT_MESSAGE = "Concat Samples";
	
	/**
	 * Converts a message providing from a file called fileName into a RUBioSeqEvent.
	 * 
	 * Messages format:
	 * 	RUBIOSEQ_EVENT::1/1::Indexing reference::STEP_ENDS::Tue Jun 17 09:59:51 2014
	 */
	public RUbioSeqEvent(String msg, String fileName) throws Exception{
		try{
			String[] msgSplit = msg.split("::");
			
			String step = msgSplit[1];
			String[] steps = step.split("/");
			this.step = Integer.valueOf(steps[0]);
			this.totalSteps = Integer.valueOf(steps[1]);
			
			this.message = msgSplit[2];
			
			String eventType = msgSplit[3];
			if(eventType.equals(EventType.STEP_STARTS.toString())){
				this.type = EventType.STEP_STARTS;
			} else if(eventType.equals(EventType.STEP_ENDS.toString())){
				this.type = EventType.STEP_ENDS;
			} else if(eventType.equals(EventType.STEP_ABORTED.toString())){
				this.type = EventType.STEP_ABORTED;
			} else {
				System.err.println("Warning: received an event with an unidentifiable type (" + this.type + ")");
			}
			
			this.date = msgSplit[4];
			
			if(fileName.contains("log_S")){
				String[] fileNameSplit = Utils.removeFileExtension(fileName).split("_");
				this.executionLevel = Integer.valueOf(fileNameSplit[1].replace("S", ""));
				if(fileNameSplit.length > 2){
					this.sampleName = fileNameSplit[2];
					/*
					 *  Note that in ChipSeqLogs the ExperimentName will be stored in the sampleName variable.
					 */
				}
				
				// ChipSeq branch checks
				if (this.executionLevel == 4 && this.sampleName != null && this.sampleName.equals("control")){
					this.sampleName = fileNameSplit[3];
				}
				
				// Methylation branch checks
				if ((this.executionLevel == 1 && this.sampleName != null && this.sampleName.equals("trim")) ||
						(this.executionLevel == 2 && this.sampleName != null && this.sampleName.equals("format"))	){
					this.sampleName = fileNameSplit[3];
				}
			}else{
				this.executionLevel = -1;
				if(fileName.startsWith("log_CON_")){
					/*
					 * Concatenation log for split files. We must extract the SampleName and set the extraStage SPLIT_FILES_CONCAT
					 */
					this.sampleName = Utils.removeFileExtension(fileName.replace("log_CON_", ""));
					this.extraStage = ExtraStage.SPLIT_FILES_CONCAT;
				} else if(fileName.startsWith("log_Int_")){
					/*
					 * 	TC analysis log. We don't know the sample.
					 */
					this.extraStage = ExtraStage.SNV_TC_ANALYSIS;
				}
			}
		}catch(Exception ex){
			throw ex;
		}
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getStep() {
		return step;
	}

	public void setStep(Integer step) {
		this.step = step;
	}

	public Integer getTotalSteps() {
		return totalSteps;
	}

	public void setTotalSteps(Integer totalSteps) {
		this.totalSteps = totalSteps;
	}
	
	public Integer getExecutionLevel() {
		return executionLevel;
	}
	
	public void setExecutionLevel(Integer executionLevel) {
		this.executionLevel = executionLevel;
	}
	
	public String getSampleName() {
		return sampleName;
	}
	
	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}
	
	public ExtraStage getExtraStage() {
		return extraStage;
	}
	
	public void setExtraStage(ExtraStage extraStage) {
		this.extraStage = extraStage;
	}

	@Override
	public String toString() {
		return RUBIOSEQ_EVENT + "::" + this.getStep() + "/"
				+ this.getTotalSteps() + "::" + this.getMessage() + "::"
				+ this.getType() + "::" + this.getDate();
	}
}
