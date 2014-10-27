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

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.DesktopCleanup;
import org.zkoss.zul.Messagebox;

import es.uvigo.ei.sing.rubioseq.Experiment;
import es.uvigo.ei.sing.rubioseq.ExperimentStatus;
import es.uvigo.ei.sing.rubioseq.ExperimentType;
import es.uvigo.ei.sing.rubioseq.gui.util.Utils;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.ChipSeqExperiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.CopyNumberVariationExperiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.InvalidRubioSeqParameterException;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.MethylationExperiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.SingleNucleotideVariantExperiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.ExperimentDirectoryWatcher;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.ExperimentProgress;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.Measurable;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.OutputLogFileWatcher;

/**
 * 
 * @author hlfernandez
 *
 */
public class ViewExperimentModel {

	public static final String EVENT_UPDATE_LOG = "updateLogEvent";
	
	private Experiment experiment;
	private ExecutionMode executionMode;
	private String executionModeString;
	private String experimentLog = "";
	
	private ExperimentProgress experimentProgress;
	private Measurable experimentData;
	private ExperimentDirectoryWatcher edWatcher;
	private OutputLogFileWatcher outputLogFileWatcher;
	private boolean xmlConfigurationFileLoaded = false;
	private String workingDirectory;
	private long experimentStartTimestamp;

	@PersistenceContext(type=PersistenceContextType.EXTENDED) 
	private EntityManager em;
	
	@Init
	public void initModel(@ContextParam(ContextType.DESKTOP) final Desktop desktop) {
		this.experiment = (Experiment) Sessions.getCurrent().getAttribute("experiment");
		if(this.experiment!=null){
			
			this.experimentProgress = new ExperimentProgress(this);
			if(this.experiment.getExecutionLevel()==0){
				this.executionMode = ExecutionMode.Complete;
				this.executionModeString = this.executionMode.getDisplayName();
			} else{
				this.executionMode = ExecutionMode.Level;
				this.executionModeString = this.executionMode.getDisplayName() + " - " + this.experiment.getExecutionLevel();
			}
			
			if(this.experiment.getStatus().equals(ExperimentStatus.Finished)){
				this.experimentProgress.setProgress(100);
			} else if(this.experiment.getStatus().equals(ExperimentStatus.Aborted)){
				this.experimentProgress.setProgress(0);
				} else if(this.experiment.getStatus().equals(ExperimentStatus.Running)){
					
					this.workingDirectory = experiment.getWorkingDirectory();
					List<String> pathsToWatch = new LinkedList<String>();
					pathsToWatch.add(workingDirectory);
					this.outputLogFileWatcher = new OutputLogFileWatcher(this.workingDirectory, this);
					edWatcher = new ExperimentDirectoryWatcher(pathsToWatch, this.experimentProgress, this.outputLogFileWatcher);
					
					desktop.addListener(new DesktopCleanup() {
						@Override
						public void cleanup(Desktop desktop) throws Exception {
							edWatcher.getWatcherThread().interrupt();
						}
					});
				}
		}
	}
	
	public static File lookUpXMLConfigurationFile(String dir){
		File directory = new File(dir);
		File toret = null;
		for (File f : directory.listFiles()) {
			if (f.canRead() && f.getName().endsWith(".xml")) {
				toret = f;
			}
		}
		return toret;
	}
	
	public Experiment getExperiment() {
		return experiment;
	}
	
	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	
	public enum ExecutionMode {
		Complete("Complete workflow"), 
		Level("By Level");
		
		private final String displayName;
		
		private ExecutionMode(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
	}
	
	public String getExecutionModeString() {
		return executionModeString;
	}
	
	public void setExecutionModeString(String executionModeString) {
		this.executionModeString = executionModeString;
	}
	
	public String getExperimentLog() {
		return experimentLog;
	}
	
	public void setExperimentLog(String experimentLog) {
		this.experimentLog = experimentLog;
	}
	
    @Command
    @NotifyChange({"experimentLog","progress", "experiment", "timerActive"})
    public void updateLog(){
    	
    	if(!this.xmlConfigurationFileLoaded && !this.getExperimentProgress().isAborted()){
			File configurationFile = lookUpXMLConfigurationFile(workingDirectory);
			if(configurationFile!=null){
				try{
					if(ViewExperimentModel.this.experiment.getType().equals(ExperimentType.SNV)){
						SingleNucleotideVariantExperiment snv = new SingleNucleotideVariantExperiment();
						snv.loadDataFromFile(configurationFile);
						ViewExperimentModel.this.experimentData = snv;
					} else if(ViewExperimentModel.this.experiment.getType().equals(ExperimentType.CNV)){
						CopyNumberVariationExperiment cnv = new CopyNumberVariationExperiment();
						cnv.loadDataFromFile(configurationFile);
						ViewExperimentModel.this.experimentData = cnv;
					} else if(ViewExperimentModel.this.experiment.getType().equals(ExperimentType.CHIPSeq)){
						ChipSeqExperiment cs = new ChipSeqExperiment();
						cs.loadDataFromFile(configurationFile);
						ViewExperimentModel.this.experimentData = cs;
					} else if(ViewExperimentModel.this.experiment.getType().equals(ExperimentType.Methylation)){
						MethylationExperiment met = new MethylationExperiment();
						met.loadDataFromFile(configurationFile);
						ViewExperimentModel.this.experimentData = met;
					} 
					
					List<String> pathsToWatch = ViewExperimentModel.this.experimentData.getPathsToWatch();
					System.err.println("\t\t [ViewExperimentModel@updateLog] XML loaded. Adding " + pathsToWatch.size() + " paths to watch.");
					edWatcher.addPathsToWatch(pathsToWatch);
					
					this.xmlConfigurationFileLoaded = true;
				} catch(InvalidRubioSeqParameterException e){
					
				}
			}
    	}
    	
    	if(this.getExperimentProgress().isAborted()){
    		this.interruptWatcherThread();
			Messagebox.show("Execution aborted", "Execution aborted",
					Messagebox.OK, Messagebox.ERROR);
			this.getExperimentProgress().setAborted(false);
			this.experiment.setStatus(ExperimentStatus.Aborted);
			this.persistExperiment();
    	} else if(this.getProgress() == 100){
    		if(!this.experiment.getStatus().equals(ExperimentStatus.Finished)){
	    		this.experiment.setStatus(ExperimentStatus.Finished);
				this.interruptWatcherThread();
					Messagebox.show("Execution finished", "Execution finished",
						Messagebox.OK, Messagebox.INFORMATION);
				this.persistExperiment();
			}
    	}
    }
    
    private void interruptWatcherThread(){
    	if(this.edWatcher != null){
	    	this.edWatcher.getWatcherThread().interrupt();
    	}
    }
    
    private void persistExperiment(){
    	this.em.getTransaction().begin();
		this.em.merge(this.experiment);
		this.em.flush();
		this.em.getTransaction().commit();
    }
    
    public ExperimentProgress getExperimentProgress() {
		return experimentProgress;
	}
    
    public void setExperimentProgress(ExperimentProgress ep){
    	this.experimentProgress = ep;
    }
    
    public int getProgress(){
    	return this.getExperimentProgress().getProgress();
    }
    
    public Measurable getExperimentData() {
		return experimentData;
	}

    private int stagesCount = -1;
    
    private void initializeStagesCount(){
		if (this.experiment.getType().equals(ExperimentType.SNV)) {
			if (this.experiment.getExecutionLevel() > 0) {
				stagesCount = 1;
			} else {
				stagesCount = 4;
			}
			if (((SingleNucleotideVariantExperiment) this.experimentData)
					.isTCAnalysisStageEnabled()) {
				stagesCount++;
			}
		} else if (this.experiment.getType().equals(ExperimentType.CNV)) {
			if (this.experiment.getExecutionLevel() > 0) {
				stagesCount = 1;
			} else {
				stagesCount = 3;
			}
		} else if (this.experiment.getType().equals(ExperimentType.CHIPSeq)) {
			if (this.experiment.getExecutionLevel() > 0) {
				stagesCount = 1;
			} else {
				stagesCount = 4;
			}
		} else if (this.experiment.getType().equals(ExperimentType.Methylation)) {
			if (this.experiment.getExecutionLevel() > 0) {
				stagesCount = 1;
			} else {
				stagesCount = 2;
			}
			if (((MethylationExperiment) this.experimentData)
					.getIntervalsPath() != null) {
				stagesCount++;
			}
		}
    }
    
	public int getStagesCount() {
		if(stagesCount == -1){
			this.initializeStagesCount();
		}
		return stagesCount;
	}
	
	public boolean isTimerActive(){
		return this.experiment.getStatus().equals(ExperimentStatus.Running);
	}
	
	public long getExperimentStartTimestamp(){
		if(this.experimentStartTimestamp == 0){
			File f = new File(Utils.getDirectoryNameWithFinalSlash(this.workingDirectory) + "run-rubioseq.sh");
			if(f.exists()){
				this.experimentStartTimestamp = f.lastModified();
			}
		}
		return this.experimentStartTimestamp;
	}

	public void setWorkingDirectory(String wd) {
		this.workingDirectory = wd;
	}

	public void setExperimentData(Measurable ed) {
		this.experimentData = ed;
	}
}
