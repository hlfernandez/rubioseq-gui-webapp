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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import es.uvigo.ei.sing.rubioseq.Experiment;
import es.uvigo.ei.sing.rubioseq.ExperimentStatus;
import es.uvigo.ei.sing.rubioseq.ExperimentType;
import es.uvigo.ei.sing.rubioseq.RUbioSeqConfiguration;
import es.uvigo.ei.sing.rubioseq.RubioSeqCommand;
import es.uvigo.ei.sing.rubioseq.User;
import es.uvigo.ei.sing.rubioseq.gui.macros.RUbioSeqFile;
import es.uvigo.ei.sing.rubioseq.gui.util.DBUtils;
import es.uvigo.ei.sing.rubioseq.gui.util.Utils;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.ChipSeqExperiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.CopyNumberVariationExperiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.ExperimentUtils;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.InvalidRubioSeqParameterException;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.MethylationExperiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.RUbioSeqExperiment;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.SingleNucleotideVariantExperiment;

/**
 * 
 * @author hlfernandez
 *
 */
public class ExecuteExperimentModel {
	
	public static final String EXPERIMENT_FILE = "current_experiment_file";
	
	@PersistenceContext(type=PersistenceContextType.EXTENDED) 
	private EntityManager em;
	
	private RUbioSeqFile configFile;
	private ExperimentType experimentType;
	private ExecutionMode executionMode;
	private ExecutionLevel executionLevel;
	
	private User user;
	private RUbioSeqConfiguration rubioseqConfiguration;
	
	private static final List<ExecutionLevel> snvExecutionLevels;
	private static final List<ExecutionLevel> cnvExecutionLevels;
	private static final List<ExecutionLevel> chipseqExecutionLevels;
	private static final List<ExecutionLevel> methylationExecutionLevels;
	
	static{
		snvExecutionLevels = new LinkedList<ExecutionLevel>();
		snvExecutionLevels.add(new ExecutionLevel(1, "Alignment phase and FastQC analysis"));
		snvExecutionLevels.add(new ExecutionLevel(2, "Duplicates marking, GATK realignment and recalibration"));
		snvExecutionLevels.add(new ExecutionLevel(3, " Variant calling"));
		snvExecutionLevels.add(new ExecutionLevel(4, "Tumor/control somatic/germline calls detection andvariant effect predictor"));
		
		cnvExecutionLevels = new LinkedList<ExecutionLevel>();
		cnvExecutionLevels.add(new ExecutionLevel(1, "Alignment phase and FastQC analysis"));
		cnvExecutionLevels.add(new ExecutionLevel(2, "Duplicates marking, GATK realignment and recalibration"));
		cnvExecutionLevels.add(new ExecutionLevel(3, "Cnv calling with CONTRA software"));
		
		chipseqExecutionLevels = new LinkedList<ExecutionLevel>();
		chipseqExecutionLevels.add(new ExecutionLevel(1, "Alignment phase and FastQC analysis"));
		chipseqExecutionLevels.add(new ExecutionLevel(2, "Duplicates marking"));
		chipseqExecutionLevels.add(new ExecutionLevel(3, "Normalization steps"));
		chipseqExecutionLevels.add(new ExecutionLevel(4, "ChIPseq calling , Peaks annotation and IDR control (if it corresponds)"));
		
		methylationExecutionLevels = new LinkedList<ExecutionLevel>();
		methylationExecutionLevels.add(new ExecutionLevel(1, "Sequence alignment and methylation calling"));
		methylationExecutionLevels.add(new ExecutionLevel(2, "Methylation calls extraction"));
		methylationExecutionLevels.add(new ExecutionLevel(3, " Intervals methylation calculation"));
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

	public static class ExperimentFile{
		private RUbioSeqFile file;
		private ExperimentType experimentType;
		
		public ExperimentFile(RUbioSeqFile f, ExperimentType e){
			this.file = f;
			this.experimentType = e;
		}
		
		public RUbioSeqFile getFile() {
			return file;
		}
		
		public ExperimentType getExperimentType() {
			return experimentType;
		}
	}
	
	public static class ExecutionLevel{
		private String description;
		private Integer level;
		
		public ExecutionLevel(Integer l, String d){
			this.level = l;
			this.description = d;
		}
		
		public String getDescription() {
			return description;
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
		
		public Integer getLevel() {
			return level;
		}
		
		public void setLevel(Integer level) {
			this.level = level;
		}
		
		@Override 
		public String toString(){
			return this.description;
		}
		
	}
	
	public RUbioSeqFile getConfigFile() {
		return configFile;
	}
	
	@NotifyChange({"experimentType", "configFile", "executionLevel", "executionMode"})
	public void setConfigFile(RUbioSeqFile configFile) {
		clearStatus();
		this.configFile = configFile;
		File experimentFile = this.configFile.getFile();
		try{
			this.setExperimentType(ExperimentUtils.getExperimentType(experimentFile));
			if(experimentFile.isDirectory() || !experimentFile.getName().toLowerCase().endsWith(".xml")){
				clearStatus();
				Messagebox
						.show("Invalid input file. You have to select a .xml file.",
								"Invalid input file", Messagebox.OK,
								Messagebox.ERROR);
			} else if (!experimentFile.canRead()){
				clearStatus();
				Messagebox.show("Can't read the input file.",
						"Invalid input file", Messagebox.OK, Messagebox.ERROR);
			}
		} catch(InvalidRubioSeqParameterException ex){
			clearStatus();
			Messagebox
			.show(ex.getMessage(),
					"Invalid input file", Messagebox.OK,
					Messagebox.ERROR);
			
		}
		try {
			RUbioSeqExperiment experiment = getExperimentConfiguration();
			if (!experiment.checkConfiguration()) {
				clearStatus();
				Messagebox
						.show("Invalid input file. The loaded parameters are not valid, please, fix your file and load it again.",
								"Invalid configuration", Messagebox.OK,
								Messagebox.ERROR);
			}
		} catch (InvalidRubioSeqParameterException e) {
			clearStatus();
			Messagebox.show("Can't read the input file.", "Invalid input file",
					Messagebox.OK, Messagebox.ERROR);
		}
	}
	
	private final void clearStatus(){
		this.configFile = null;
		this.experimentType = null;
		this.executionMode = null;
		this.executionLevel = null;
	}
	
	@DependsOn({"configFile"})
	public String getConfigurationFileLabelClass(){
		if (this.getConfigFile() != null
				&& !this.getConfigFile().equals("")
				&& this.getConfigFile().getFile().getName().toLowerCase().endsWith(".xml")){
			return "";
		}
		return Utils.SCLASS_INVALID_PATH;
	}
	
	public ExperimentType getExperimentType() {
		return experimentType;
	}
	
	@NotifyChange({"executionLevel", "experimentType"})
	public void setExperimentType(ExperimentType experimentType) {
		this.experimentType = experimentType;
		this.executionLevel = null;
	}
	
	public List<ExperimentType> getExperimentTypeValues(){
		return Arrays.asList(ExperimentType.values());
	}
	
	public ExecutionMode getExecutionMode() {
		return executionMode;
	}
	
	public void setExecutionMode(ExecutionMode executionMode) {
		this.executionMode = executionMode;
	}
	
	public List<ExecutionMode> getExecutionModes(){
		return Arrays.asList(ExecutionMode.values());
	}
	
	public ExecutionLevel getExecutionLevel() {
		return executionLevel;
	}
	
	public void setExecutionLevel(ExecutionLevel executionLevel) {
		this.executionLevel = executionLevel;
	}
	
	@DependsOn("experimentType")
	public List<ExecutionLevel> getExecutionLevels(){
		List<ExecutionLevel> toret = new LinkedList<ExecutionLevel>();
		if(this.experimentType == null){
			return toret;
		}
		if(this.experimentType.equals(ExperimentType.SNV)){
			return snvExecutionLevels;
		} else if(this.experimentType.equals(ExperimentType.CNV)){
			return cnvExecutionLevels;
		} else if(this.experimentType.equals(ExperimentType.CHIPSeq)){
			return chipseqExecutionLevels;
		} else if(this.experimentType.equals(ExperimentType.Methylation)){
			return methylationExecutionLevels;
		}
		return toret;
	}
	
	@DependsOn({"executionMode","experimentType"})
	public boolean isExecutionLevelRowVisible(){
		return this.executionMode==null?false:this.executionMode.equals(ExecutionMode.Level)&&this.isExperimentTypeRowVisible();
	}
	
	@DependsOn("experimentType")
	public boolean isExperimentTypeRowVisible(){
		return this.experimentType != null;
	}
	
	@Init
	public void initModel(){
		ExperimentFile configFile = (ExperimentFile) Sessions.getCurrent().getAttribute(EXPERIMENT_FILE);
		if (configFile!=null){
			this.experimentType = configFile.getExperimentType();
			this.configFile = configFile.getFile();
		}
		this.user = (User) Sessions.getCurrent().getAttribute("user");
	}
	
	@DependsOn({"experimentType", "executionMode", "executionLevel", "configFile"})
	public boolean isRunExperimentEnabled(){
		if (this.getConfigFile() != null
				&& !this.getConfigFile().equals("")
				&& this.getConfigFile().getFile().getName().toLowerCase().endsWith(".xml")
				&& this.experimentType != null
				&& this.executionMode != null
				&& (this.executionMode.equals(ExecutionMode.Complete) || this.executionLevel != null)) {
			return true;
		}
		return false;
	}
	
	@Command
	public void runExperiment(){
		
		this.rubioseqConfiguration = (RUbioSeqConfiguration) this.em
		.createQuery("SELECT c FROM RUbioSeqConfiguration c")
		.getResultList().get(0);
		
		/*
		 * 	Save the experiment in the DB 
		 */
		
		Experiment newExperiment = new Experiment();
		newExperiment.setWorkingDirectory(getWorkingDirectory());
		newExperiment.setUser(this.user);
		newExperiment.setExecutionLevel(this.executionMode.equals(ExecutionMode.Complete)?0:this.executionLevel.getLevel());
		newExperiment.setType(experimentType);
		newExperiment.setStatus(ExperimentStatus.Running);
		
		this.em.getTransaction().begin();
		this.em.persist(newExperiment);
		this.em.flush();
		this.em.getTransaction().commit();
	
		/*
		 * Create the command and run it
		 */
		
		StringBuilder commandSB = new StringBuilder();
		commandSB
			.append(this.rubioseqConfiguration.getRubioseqCommand());
		
		commandSB
			.append(" --analysis ");
		
		
		if(this.experimentType.equals(ExperimentType.SNV)){
			commandSB
				.append("variantCalling");
		} else if(this.experimentType.equals(ExperimentType.CNV)){
				commandSB
				.append("cnvCalling");
			} else if(this.experimentType.equals(ExperimentType.Methylation)){
					commandSB
					.append("methylationCalling");
			} else if(this.experimentType.equals(ExperimentType.CHIPSeq)){
						commandSB
							.append("ChIPseq");
				}
		
		commandSB
			.append(" --config ")
			.append(this.configFile.getFile().getAbsolutePath());
		
		if(this.executionMode.equals(ExecutionMode.Level)){
			commandSB
				.append(" --level ")
				.append(this.executionLevel.getLevel());
		}
		
		Sessions.getCurrent().setAttribute("experiment", newExperiment);
		RubioSeqCommand command = new RubioSeqCommand(commandSB.toString(),
				newExperiment.getWorkingDirectory());
//		System.err.println("newExperiment.getWorkingDirectory() = " + newExperiment.getWorkingDirectory());
//		System.err.println(commandSB.toString());
		command.execCommand();
		Executions.sendRedirect("/experiments/view-experiment.zul");
	}

	private String getWorkingDirectory() {
		if(this.experimentType.equals(ExperimentType.SNV)){
			return SingleNucleotideVariantExperiment.getWorkingDirectory(this.getConfigFile().getFile());
		} else if(this.experimentType.equals(ExperimentType.CNV)){
				return CopyNumberVariationExperiment.getWorkingDirectory(this.getConfigFile().getFile());
			} else if(this.experimentType.equals(ExperimentType.Methylation)){
					return MethylationExperiment.getWorkingDirectory(this.getConfigFile().getFile());
			} else if(this.experimentType.equals(ExperimentType.CHIPSeq)){
						return ChipSeqExperiment.getWorkingDirectory(this.getConfigFile().getFile());
				}
		return "N/A";
	}
	
	@DependsOn("runExperimentEnabled")
	public String getRunButtonClass(){
		if(isRunExperimentEnabled()){
			return "actionButton";
		}else{
			return "";
		}
	}
	
	public boolean isExperimentExecutable(){
		return Utils.isValidFile(DBUtils.getConfiguration(this.em).getRubioseqCommand());
	}

	public RUbioSeqExperiment getExperimentConfiguration()
			throws InvalidRubioSeqParameterException {
		File configFile = this.getConfigFile().getFile();
		if (this.experimentType.equals(ExperimentType.SNV)) {
			SingleNucleotideVariantExperiment experiment = new SingleNucleotideVariantExperiment();
			experiment.loadDataFromFile(configFile);
			return experiment;
		} else if (this.experimentType.equals(ExperimentType.CNV)) {
			CopyNumberVariationExperiment experiment = new CopyNumberVariationExperiment();
			experiment.loadDataFromFile(configFile);
			return experiment;
		} else if (this.experimentType.equals(ExperimentType.Methylation)) {
			MethylationExperiment experiment = new MethylationExperiment();
			experiment.loadDataFromFile(configFile);
			return experiment;
		} else if (this.experimentType.equals(ExperimentType.CHIPSeq)) {
			ChipSeqExperiment experiment = new ChipSeqExperiment();
			experiment.loadDataFromFile(configFile);
			return experiment;
		} else {
			return null;
		}
	}
}
