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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.commons.beanutils.BeanUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zul.Messagebox;

import es.uvigo.ei.sing.rubioseq.Experiment;
import es.uvigo.ei.sing.rubioseq.ExperimentType;
import es.uvigo.ei.sing.rubioseq.User;
import es.uvigo.ei.sing.rubioseq.gui.macros.RUbioSeqFile;
import es.uvigo.ei.sing.rubioseq.gui.view.models.EditExperimentModel;
import es.uvigo.ei.sing.rubioseq.gui.view.models.ExecuteExperimentModel;

/**
 * 
 * @author hlfernandez
 *
 */
public class ChipSeqExperimentModel {
	
	@PersistenceContext(type=PersistenceContextType.EXTENDED) 
	private EntityManager em;
	
	private ChipSeqExperiment experiment = new ChipSeqExperiment();

	private RUbioSeqFile outputFilePathRSFile;
	private boolean selectOutputFile;
	private String outputFilePath = "/home/hlfernandez/Tmp";
	private String outputFileName = "experiment.xml";

	private RUbioSeqFile currentExperimentFile;

	private boolean executeExperiment = false;
	
	@Init
	public void init(){
		RUbioSeqFile editFile = (RUbioSeqFile) Sessions.getCurrent().getAttribute(EditExperimentModel.EDIT_EXPERIMENT_FILE);
		if(editFile!=null){
			this.outputFilePathRSFile = new RUbioSeqFile(new File(editFile.getFile().getParent()), editFile.getDatastore());
			this.outputFilePath = editFile.getFile().getParent();
			this.outputFileName = editFile.getFile().getName();
			Object data = Sessions.getCurrent().getAttribute(EditExperimentModel.EDIT_EXPERIMENT_DATA);
			if(data!=null){
				this.experiment = (ChipSeqExperiment) data;
			}
//			try{
//				this.experiment.loadDataFromFile(editFile.getFile());
//			} catch(InvalidRubioSeqParameterException e){
//				Sessions.getCurrent().setAttribute(EditExperimentModel.EDIT_EXPERIMENT_FILE, editFile);
//				Sessions.getCurrent().setAttribute(EditExperimentModel.EDIT_EXPERIMENT_ERROR_MSG, e.getMessage());
//				Executions.getCurrent().sendRedirect("edit-experiment.zul");
//				return;
//			}
		}
		Sessions.getCurrent().removeAttribute(EditExperimentModel.EDIT_EXPERIMENT_DATA);
		Sessions.getCurrent().removeAttribute(EditExperimentModel.EDIT_EXPERIMENT_FILE);
	}
	
	@SuppressWarnings("unchecked")
	public List<Experiment> getUserexperiments(){
		final Session webSession = Sessions.getCurrent();
		String username = ((User) webSession.getAttribute("user")).getUsername();
		return (List<Experiment>) this.em.createQuery(
				"SELECT e FROM Experiment e where e.user='" + username + "'")
				.getResultList();
	}
	
	@NotifyChange({"selectOutputFile"})	
	@Command
	public void selectOutputFile(){
		this.selectOutputFile = true;
	}
	
	@NotifyChange({"selectOutputFile"})	
	@Command 
	public void cancelSelectOutputFile(){
		this.selectOutputFile = false;
	}
	
	public boolean isSelectOutputFile() {
		return selectOutputFile;
	}
	
	public RUbioSeqFile getOutputFilePath() {
		return outputFilePathRSFile;
	}
	
	public void setOutputFilePath(RUbioSeqFile outputFilePathRSFile) {
		this.outputFilePathRSFile = outputFilePathRSFile;
		this.outputFilePath = this.outputFilePathRSFile.getFile().getAbsolutePath(); 
	}
	
	public String getOutputFileName() {
		return outputFileName;
	}
	
	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}
	
	@NotifyChange({"selectOutputFile", "currentExperimentFile"})
	@Command
	public void generateXMLConfigurationFile(){
		File output = new File(this.outputFilePath, this.outputFileName);
		File outputDirectory = new File(this.outputFilePath);
		if (outputDirectory.isDirectory()){
			if (outputDirectory.canWrite()){
				try {
					experiment.generateXMLConfigurationFile(output);
					this.currentExperimentFile = new RUbioSeqFile(output, outputFilePathRSFile.getDatastore());
					Messagebox.show("Configuration saved", "OK", Messagebox.OK,
							Messagebox.INFORMATION);
				} catch (IOException e) {
					Messagebox.show("Error saving configuration", "Error", Messagebox.OK,
							Messagebox.ERROR);
					this.currentExperimentFile = null;
					e.printStackTrace();
				}
				this.selectOutputFile = false;
			} else{
				this.currentExperimentFile = null;
				Messagebox.show("Cannot write to file " + output.getName(),
						"Invalid file", Messagebox.OK, Messagebox.ERROR);
			}				
		} else{
			this.currentExperimentFile = null;
			Messagebox.show(this.outputFilePath + " is not a directory.",
					"Invalid file", Messagebox.OK, Messagebox.ERROR);
		}
		if(this.executeExperiment){
			this.executeExperiment();
			this.executeExperiment  = false;
		}
	}
	
	public ChipSeqExperiment getExperiment() {
		return experiment;
	}
	
	public void setExperiment(ChipSeqExperiment experiment) {
		this.experiment = experiment;
	}
	
	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}
	
	@DependsOn({"experiment.genRefPath", "experiment.plattform", "experiment.dirOutBase", 
		"experiment.projectId", "experiment.dataInDirpreProcess", "experiments"})
	public boolean isEnabledExportButton(){
		if(experiment.getGenRefPath() == null || 
				experiment.getPlattform() == null ||
				experiment.getDirOutBase() == null ||
				experiment.getProjectId() == null ||
				experiment.getDataInDirpreProcess() == null ||
				!experiment.checkExperiments()){
			return false;
		}
		return true;
	}
	
	public boolean isStringOk(String str){
		if(str == null || str.equals("")){
			return false;
		}
		return true;
	}
	
	@NotifyChange("experiment")
	@Command("restoreCheckCasava")
	public void restoreCheckCasava(){
		experiment.setCheckCasava(experiment.getCheckCasava_DV());
	}
	
	@NotifyChange("experiment")
	@Command("restoreUserName")
	public void restoreUserName(){
		experiment.setUserName(experiment.getUserName_DV());
	}
	
	@NotifyChange("experiment")
	@Command("restorePeakAnalysisType")
	public void restorePeakAnalysisType(){
		experiment.setPeakAnalysisType(experiment.getPeakAnalysisType_DV());
	}
	
	@NotifyChange("experiment")
	@Command("restoreccatConfigFile")
	public void restoreccatConfigFile(){
		experiment.setCcatConfigFile(experiment.getCcatConfigFile_DV());
	}
	
	@NotifyChange("experiment")
	@Command("restoremacsExtraArgs")
	public void restoremacsExtraArgs(){
		experiment.setMacsExtraArgs(experiment.getMacsExtraArgs());
	}
	
	@NotifyChange("experiment")
	@Command("restoreFastqc")
	public void restoreFastqc(){
		experiment.setFastqc(experiment.getFastqc());
	}
	
	@NotifyChange("experiment")
	@Command("restoreannotFile")
	public void restoreannotFile(){
		experiment.setAnnotFile(experiment.getAnnotFile_DV());
	}
	
	@NotifyChange("experiment")
	@Command("restorequeueSGEProject")
	public void restorequeueSGEProject(){
		experiment.setQueueSGEProject(experiment.getQueueSGEProject_DV());
	}
	
	@DependsOn("currentExperimentFile")
	public boolean isEnabledExecuteExperimentButton(){
		return this.currentExperimentFile!=null;
	}
	
	@NotifyChange({"selectOutputFile"})	
	@Command
	public void saveBeforeExecuteExperiment(){
		this.selectOutputFile = true;
		this.executeExperiment  = true;
	}
	
	@Command
	public void executeExperiment(){
		Sessions.getCurrent().setAttribute(
				ExecuteExperimentModel.EXPERIMENT_FILE,
				new ExecuteExperimentModel.ExperimentFile(
						this.currentExperimentFile, ExperimentType.CHIPSeq));
		Executions.getCurrent().sendRedirect("/experiments/execute-experiment.zul");
	}
	
	/*
	 * Experiments 
	 */
	
	public List<CSExperiment> getExperiments(){
		return this.experiment.getExperiments();
	}
	
	public void setExperiments(List<CSExperiment> experiments){
		this.experiment.setExperiments(experiments);
	}

	@NotifyChange({"experiments", "mandatoryHeight"})
	@Command
	public void addExperiment(){
		this.experiment.getExperiments().add(new CSExperiment());
	}
	
	@NotifyChange({"experiments","mandatoryHeight"})
	@Command
	public void deleteExperiment(@BindingParam("experiment") CSExperiment experiment){
		this.experiment.getExperiments().remove(experiment); 
	}
	
	@NotifyChange({"experiments", "mandatoryHeight"})
	@Command
	public void addChipSeqUnit(@BindingParam("experiment") CSExperiment experiment){
		ChipSeqUnit csUnit = new ChipSeqUnit();
		experiment.getChipSeqUnits().add(csUnit); 
	}
	
	@NotifyChange({"experiments", "mandatoryHeight"})
	@Command
	public void deleteChipSeqUnit(@BindingParam("experiment") CSExperiment experiment,
			@BindingParam("unit") ChipSeqUnit unit){
		experiment.getChipSeqUnits().remove(unit); 
	}
	
	/* 
	 * Samples Management 
	 */
	private Sample currentSample;
	private Sample currentTmpSample;
	@SuppressWarnings("unused")
	private boolean edit = false;
	
	public Sample getCurrentSample() {
		return currentSample;
	}

	public void setCurrentSample(Sample currentSample) {
		this.currentSample = currentSample;
	}
	
	public Sample getCurrentTmpSample() {
		return currentTmpSample;
	}
	
	public void setCurrentTmpSample(Sample currentTmpSample) {
		this.currentTmpSample = currentTmpSample;
	}
	
	@NotifyChange({"currentSample", "experiments"})	
	@Command
	public void saveSample(){
		this.edit = false;
		this.currentSample = null;
		this.currentTmpSample = null;
	}
	
	@NotifyChange({"currentSample"})	
	@Command
	public void cancelEdit(){		
		this.currentSample = null;
		this.currentTmpSample = null;
	}
	
	@DependsOn({"currentTmpSample.sampleType","currentTmpSample.sampleName",
		"currentTmpSample.sampleFiles","currentTmpSample.sampleSuffix"})
	public boolean isEnabledSaveSampleButton(){
		if (this.currentTmpSample == null){
			return false;
		}
		if (this.currentTmpSample.getSampleFiles() != null &&
				this.currentTmpSample.getSampleName() != null &&
				this.currentTmpSample.getSampleSuffix() != null &&
				this.currentTmpSample.getSampleType() != null &&
				!this.currentTmpSample.getSampleFiles().equals("") &&
				!this.currentTmpSample.getSampleName().equals("") &&
				!this.currentTmpSample.getSampleSuffix().equals("")){
			return true;
		} else return false;
	}
	
	@NotifyChange({"currentSample"})	
	@Command
	public void editChipSeqUnitSampleInput(@BindingParam("experiment") CSExperiment experiment,
			@BindingParam("unit") ChipSeqUnit unit) {
		this.edit = true;
		this.currentSample = unit.getSampleInput();
		this.currentTmpSample = new Sample();
		try {
			BeanUtils.copyProperties(this.currentTmpSample, this.currentSample);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@NotifyChange({"currentSample"})	
	@Command
	public void editChipSeqUnitSampleTreatment(@BindingParam("experiment") CSExperiment experiment,
			@BindingParam("unit") ChipSeqUnit unit) {
		this.edit = true;
		this.currentSample = unit.getSampleTreatment();
		this.currentTmpSample = new Sample();
		try {
			BeanUtils.copyProperties(this.currentTmpSample, this.currentSample);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@NotifyChange("experiments")
	@Command
	public void restoreChipSeqUnitSampleInput(@BindingParam("experiment") CSExperiment experiment,
			@BindingParam("unit") ChipSeqUnit unit){
		Sample sampleInput = unit.getSampleInput();
		sampleInput.setSampleFiles(null);
		sampleInput.setSampleName(null);
		sampleInput.setSampleSuffix(null);
		sampleInput.setSampleType(null);
	}

	private static final int minMandatoryHeight=550;

	private int getExperimentsSize(){
		int toret = 0;
		for(CSExperiment e : this.experiment.getExperiments()){
			toret+=80 + e.getChipSeqUnits().size()*120;
		}
		return toret;
	}
	
	private int getMinMandatoryHeight() {
		int toret = minMandatoryHeight + this.getExperimentsSize();
		return toret;
	}
	
	public String getMandatoryHeight(){
		return String.valueOf(getMinMandatoryHeight())+"px";
	}
	
	@DependsOn("enabledExportButton")
	public String getExportButtonClass(){
		if(isEnabledExportButton()){
			return "actionButton";
		}else{
			return "";
		}
	}
}
