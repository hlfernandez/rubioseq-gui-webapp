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
import java.util.LinkedList;
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
public class CopyNumberVariationExperimentModel {
	
	@PersistenceContext(type=PersistenceContextType.EXTENDED) 
	private EntityManager em;
	
	private CopyNumberVariationExperiment experiment = new CopyNumberVariationExperiment();

	private RUbioSeqFile outputFilePathRSFile;
	private boolean selectOutputFile;
	private String outputFilePath = "/home/hlfernandez/Tmp";
	private String outputFileName = "experiment.xml";

	private RUbioSeqFile currentExperimentFile;
	
	@Init
	public void init(){
		RUbioSeqFile editFile = (RUbioSeqFile) Sessions.getCurrent().getAttribute(EditExperimentModel.EDIT_EXPERIMENT_FILE);
		if(editFile!=null){
			this.outputFilePathRSFile = new RUbioSeqFile(new File(editFile.getFile().getParent()), editFile.getDatastore());
			this.outputFilePath = editFile.getFile().getParent();
			this.outputFileName = editFile.getFile().getName();
			Object data = Sessions.getCurrent().getAttribute(EditExperimentModel.EDIT_EXPERIMENT_DATA);
			if(data!=null){
				this.experiment = (CopyNumberVariationExperiment) data;
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
			this.executeExperiment = false;
		}
	}
	
	public CopyNumberVariationExperiment getExperiment() {
		return experiment;
	}
	
	public void setExperiment(CopyNumberVariationExperiment experiment) {
		this.experiment = experiment;
	}
	
	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}
	
	/* 
	 * Samples Management 
	 */
	private Sample currentSample;
	private Sample currentTmpSample;
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

	@NotifyChange({"currentSample", "currentTmpSample"})
	@Command
	public void addSample(){
		this.edit = false;
		this.currentSample = new Sample();
		this.currentTmpSample = new Sample();
	}
	
	@NotifyChange({"currentSample", "samples", "mandatoryHeight"})	
	@Command
	public void saveSample(){
		if (!this.edit){
			experiment.getSamples().add(this.currentSample);
		} else{
		}
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
	
	public String getOutputFileName() {
		return outputFileName;
	}
	
	public void setOutputFileName(String outputFileName) {
		this.outputFileName = outputFileName;
	}
	
	public RUbioSeqFile getOutputFilePath() {
		return outputFilePathRSFile;
	}
	
	public void setOutputFilePath(RUbioSeqFile outputFilePathRSFile) {
		this.outputFilePathRSFile = outputFilePathRSFile;
		this.outputFilePath = this.outputFilePathRSFile.getFile().getAbsolutePath(); 
	}
	
	public List<Sample> getSamples() {
		return experiment.getSamples();
	}

	public void setSamples(List<Sample> samples) {
		experiment.setSamples(samples);
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
	
	@NotifyChange({"currentSample", "samples"})	
	@Command
	public void editSample(@BindingParam("sample") Sample sample) {
		this.edit = true;
		this.currentSample = sample;
		this.currentTmpSample = new Sample();
		try {
			BeanUtils.copyProperties(this.currentTmpSample, this.currentSample);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@NotifyChange({"samples", "mandatoryHeight"})	
	@Command
	public void deleteSample(@BindingParam("sample") Sample sample){
		this.experiment.getSamples().remove(sample);
	}


	/* 
	 * KnownIndels Management 
	 */
	private KnownIndels currentKnownIndels;
	private KnownIndels currentTmpKnownIndels;
	private boolean editKnownIndels = false;

	private boolean executeExperiment = false;
	
	public KnownIndels getCurrentKnownIndels() {
		return currentKnownIndels;
	}
	
	public void setCurrentKnownIndels(KnownIndels currentKnownIndels) {
		this.currentKnownIndels = currentKnownIndels;
	}
	
	public KnownIndels getCurrentTmpKnownIndels() {
		return currentTmpKnownIndels;
	}
	
	public void setCurrentTmpKnownIndels(KnownIndels currentKnownIndels) {
		this.currentKnownIndels = currentKnownIndels;
	}
	
	@NotifyChange({"currentKnownIndels", "currentTmpKnownIndels"})
	@Command
	public void addKnownIndels(){
		this.editKnownIndels = false;
		this.currentKnownIndels = new KnownIndels();
		this.currentTmpKnownIndels = new KnownIndels();
	}
	
	@NotifyChange({"currentKnownIndels", "knownIndels", "optionalHeight"})	
	@Command
	public void saveKnownIndels(){
		if (!this.editKnownIndels){
			experiment.getKnownIndels().add(this.currentKnownIndels);
		} else{
			// It is saved automatically by the @save annotation before saveHardFilter in the .zul
		}
		this.editKnownIndels = false;
		this.currentKnownIndels = null;
		this.currentTmpKnownIndels = null;
	}
	
	@NotifyChange({"currentKnownIndels"})	
	@Command
	public void cancelEditKnownIndels(){		
		this.currentKnownIndels = null;
		this.currentTmpKnownIndels = null;
	}
	
	@NotifyChange({"currentKnownIndels"})	
	@Command
	public void editKnownIndels(@BindingParam("knownIndels") KnownIndels knownIndels) {
		this.editKnownIndels = true;
		this.currentKnownIndels = knownIndels;
		this.currentTmpKnownIndels = new KnownIndels();
		try {
			BeanUtils.copyProperties(this.currentTmpKnownIndels, this.currentKnownIndels);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@NotifyChange({"knownIndels", "optionalHeight"})	
	@Command
	public void deleteKnownIndels(@BindingParam("knownIndels") KnownIndels knownIndels){
		this.experiment.getKnownIndels().remove(knownIndels);
	}
	
	public List<KnownIndels> getKnownIndels() {
		return experiment.getKnownIndels();
	}
	
	public void setKnownIndels(List<KnownIndels> knownIndels) {
		experiment.setKnownIndels(knownIndels);
	}

	
	@DependsOn({"currentTmpKnownIndels.file"})
	public boolean isEnabledSaveKnownIndelsButton(){
		if (this.currentTmpKnownIndels == null){
			return false;
		}
		if(this.currentTmpKnownIndels.getFile() != null && 
				!this.currentTmpKnownIndels.getFile().equals("")){
			return true;
		} else return false;
	}
	
	@DependsOn({"knownIndels"})
	public boolean isKnownIndelsGridVisible(){
		return this.experiment.getKnownIndels().size() > 0;
	}
	
	@DependsOn({"experiment.genRefPath", "experiment.dbSnpAnnotPath", "experiment.intervalsPath",
		"experiment.indelAnnotPath", "experiment.plattform", "experiment.dirOutBase", 
		"experiment.projectId", "experiment.dataInDirpreProcess", "samples"})
	public boolean isEnabledExportButton(){
		if(experiment.getGenRefPath() == null || 
				experiment.getDbSnpAnnotPath() == null ||
				experiment.getIntervalsPath() == null ||
				experiment.getIndelAnnotPath() == null ||
				experiment.getPlattform() == null ||
				experiment.getDirOutBase() == null ||
				experiment.getProjectId() == null ||
				experiment.getDataInDirpreProcess() == null ||
				this.getSamples().size() == 0){
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
	
	@NotifyChange({"experiment","knownIndels"})
	@Command("restoreKnownIndels")
	public void restoreKnownIndels(){
		experiment.setKnownIndels(new LinkedList<KnownIndels>());
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
	@Command("restoreFastqc")
	public void restoreFastqc(){
		experiment.setFastqc(experiment.getFastqc_DV());
	}
	
	
	@NotifyChange("experiment")
	@Command("restoreMDFlag")
	public void restoreMDFlag(){
		experiment.setmDFlag(experiment.getmDFlag_DV());
	}

	@NotifyChange("experiment")
	@Command("restoreExtraContra")
	public void restoreExtraContra(){
		experiment.setExtraContra(experiment.getExtraContra_DV());
	}
	
	@NotifyChange("experiment")
	@Command("restoreBaseline")
	public void restoreBaseline(){
		experiment.setBaseline(experiment.getBaseline_DV());
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
		this.executeExperiment   = true;
	}
	
	@Command
	public void executeExperiment(){
		Sessions.getCurrent().setAttribute(
				ExecuteExperimentModel.EXPERIMENT_FILE,
				new ExecuteExperimentModel.ExperimentFile(
						this.currentExperimentFile, ExperimentType.CNV));
		Executions.getCurrent().sendRedirect("/experiments/execute-experiment.zul");
	}
	
	private static final int minMandatoryHeight=680;
	private static final int minOptionalHeight=650;

	private int getMinOptionalHeight() {
		int toret = minOptionalHeight + this.experiment.getKnownIndels().size()*40;
		return toret;
	}

	private int getMinMandatoryHeight() {
		int toret = minMandatoryHeight + this.experiment.getSamples().size()*40;
		return toret;
	}
	
	public String getMandatoryHeight(){
		return String.valueOf(getMinMandatoryHeight())+"px";
	}
	
	public String getOptionalHeight(){
		return String.valueOf(getMinOptionalHeight())+"px";
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
