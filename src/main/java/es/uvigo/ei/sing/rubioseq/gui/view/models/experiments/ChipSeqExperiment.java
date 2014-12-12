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
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import es.uvigo.ei.sing.rubioseq.gui.macros.InvalidRUbioSeqFile;
import es.uvigo.ei.sing.rubioseq.gui.macros.RUbioSeqFile;
import es.uvigo.ei.sing.rubioseq.gui.util.Utils;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.Sample.SampleType;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.Measurable;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.RUbioSeqEvent;

/**
 * 
 * @author hlfernandez
 *
 */
public class ChipSeqExperiment implements Measurable, RUbioSeqExperiment {

	public static final String CONFIG_DATA = "configData";
	public static final String GENREF = "GenRef";
	public static final String PLATTFORM = "Platform";
	public static final String CHECKCASAVA = "checkCasava";
	public static final String DIROUTBASE = "dirOutBase";
	public static final String PROJECTID = "ProjectId";
	public static final String USERNAME = "UserName";
	public static final String INDIRPREPROCESS = "InDirPreProcess";
	public static final String EXPERIMENT = "Experiment";
	public static final String REPLICATES_FLAG = "ReplicatesFlag";
	public static final String CHIPSEQUNIT = "ChipSeqUnit";
	public static final String SAMPLE_TREATMENT = "SampleTreatment";
	public static final String SAMPLE_INPUT = "SampleInput";
	public static final String SAMPLE_NAME = "SampleName";
	public static final String SAMPLE_FILES = "SampleFiles";
	public static final String SAMPLE_SUFFIX = "SampleSuffix";
	public static final String SAMPLE_TYPE = "SampleType";
	public static final String CHROMSIZE = "chromSize";
	public static final String PEAKANALYSIS = "peakAnalysis";
	public static final String CCAT_CONFIG_FILE = "CCAT_config_file";
	public static final String MACS_EXTRAARGS = "MACS_extraArgs";
	public static final String FASTQC = "fastqc";
	public static final String ANNOTFILE = "annotFile";
	public static final String QUEUESGEPROJECT = "queueSGEProject";
	
	private RUbioSeqFile genRefPath;// = "/media/hlfernandez/sd128GB/Investigacion/Colaboraciones/RubioSeq/data/VariantTestData/reference/hg19.chr20.fa";
	private PlattformTech plattform;
	private Integer checkCasava = 1;
	private Integer checkCasava_DV = 1;
	private RUbioSeqFile dirOutBase;// = "/media/hlfernandez/sd128GB/Investigacion/Colaboraciones/RubioSeq/data/VariantTestData/";
	private String projectId = "experiment-output";
	private String userName = "Undefined";
	private String userName_DV = "Undefined";
	private RUbioSeqFile dataInDirpreProcess;// = "/media/hlfernandez/sd128GB/Investigacion/Colaboraciones/RubioSeq/data/VariantTestData/";
	private List<CSExperiment> experiments = new LinkedList<CSExperiment>();
	private RUbioSeqFile chromSize;
	private PeakAnalysisType peakAnalysisType = PeakAnalysisType.SHARP;
	private PeakAnalysisType peakAnalysisType_DV = PeakAnalysisType.SHARP;
	private RUbioSeqFile ccatConfigFile;
	private RUbioSeqFile ccatConfigFile_DV = null;
	private String macsExtraArgs = "";
	private String macsExtraArgs_DV = "";
	private Integer fastqc = 0;
	private Integer fastqc_DV = 0;
	private RUbioSeqFile annotFile;
	private RUbioSeqFile annotFile_DV = null;
	private String queueSGEProject = "";
	private String queueSGEProject_DV = "";
	
	public enum PeakAnalysisType {
		SHARP("sharp"), 
		BROAD("broad"),
		BOTH("both");
		
		private final String displayName;
		
		private PeakAnalysisType(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
	}
	
	public List<PeakAnalysisType> getPeakAnalysisTypes(){
		return Arrays.asList(PeakAnalysisType.values());
	}
	
	public enum PlattformTech {
		ILLUMINA("illumina"), 
		SOLID("solid"),
		ION("ion");
		
		private final String displayName;
		
		private PlattformTech(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
	}
	
	public List<PlattformTech> getPlattformValues(){
		return Arrays.asList(PlattformTech.values());
	}
	
	public List<Integer> getCasavaValues(){
		return Arrays.asList(new Integer[]{0,1});
	}
	
	public List<Integer> getFastqcValues(){
		return Arrays.asList(new Integer[]{0,1});
	}
	
	public RUbioSeqFile getGenRefPath() {
		return genRefPath;
	}

	public void setGenRefPath(RUbioSeqFile genRefPath) {
		this.genRefPath = genRefPath;
	}

	public PlattformTech getPlattform() {
		return plattform;
	}

	public void setPlattform(PlattformTech plattform) {
		this.plattform = plattform;
	}

	public Integer getCheckCasava() {
		return checkCasava;
	}

	public void setCheckCasava(Integer checkCasava) {
		this.checkCasava = checkCasava;
	}

	public RUbioSeqFile getDirOutBase() {
		return dirOutBase;
	}

	public void setDirOutBase(RUbioSeqFile dirOutBase) {
		this.dirOutBase = dirOutBase;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public RUbioSeqFile getDataInDirpreProcess() {
		return dataInDirpreProcess;
	}

	public void setDataInDirpreProcess(RUbioSeqFile dataInDirpreProcess) {
		this.dataInDirpreProcess = dataInDirpreProcess;
	}

	public List<CSExperiment> getExperiments() {
		return experiments;
	}

	public void setExperiments(List<CSExperiment> experiments) {
		this.experiments = experiments;
	}
	
	public RUbioSeqFile getChromSize() {
		return chromSize;
	}
	
	public void setChromSize(RUbioSeqFile chromSize) {
		this.chromSize = chromSize;
	}

	public PeakAnalysisType getPeakAnalysisType() {
		return peakAnalysisType;
	}

	public void setPeakAnalysisType(PeakAnalysisType peakAnalysisType) {
		this.peakAnalysisType = peakAnalysisType;
	}

	public RUbioSeqFile getCcatConfigFile() {
		return ccatConfigFile;
	}

	public void setCcatConfigFile(RUbioSeqFile ccatConfigFile) {
		this.ccatConfigFile = ccatConfigFile;
	}

	public String getMacsExtraArgs() {
		return macsExtraArgs;
	}

	public void setMacsExtraArgs(String macsExtraArgs) {
		this.macsExtraArgs = macsExtraArgs;
	}

	public Integer getFastqc() {
		return fastqc;
	}

	public void setFastqc(Integer fastqc) {
		this.fastqc = fastqc;
	}

	public RUbioSeqFile getAnnotFile() {
		return annotFile;
	}

	public void setAnnotFile(RUbioSeqFile annotFile) {
		this.annotFile = annotFile;
	}

	public String getQueueSGEProject() {
		return queueSGEProject;
	}

	public void setQueueSGEProject(String queueSGEProject) {
		this.queueSGEProject = queueSGEProject;
	}

	public Integer getCheckCasava_DV() {
		return checkCasava_DV;
	}

	public String getUserName_DV() {
		return userName_DV;
	}

	public PeakAnalysisType getPeakAnalysisType_DV() {
		return peakAnalysisType_DV;
	}

	public RUbioSeqFile getCcatConfigFile_DV() {
		return ccatConfigFile_DV;
	}

	public String getMacsExtraArgs_DV() {
		return macsExtraArgs_DV;
	}

	public Integer getFastqc_DV() {
		return fastqc_DV;
	}

	public RUbioSeqFile getAnnotFile_DV() {
		return annotFile_DV;
	}

	public String getQueueSGEProject_DV() {
		return queueSGEProject_DV;
	}

	public void generateXMLConfigurationFile(File output) throws IOException {
		Document configurationFile = new Document();
		Element configData = new Element(CONFIG_DATA);
		configurationFile.setRootElement(configData);
		configData.setAttribute(ExperimentUtils.BRANCH, ExperimentUtils.BRANCH_CHIPSEQ);
		
		Element genRef = new Element(GENREF);
		genRef.addContent(this.getGenRefPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(genRef);
		
		Element plattform = new Element(PLATTFORM);
		plattform.addContent(this.getPlattform().getDisplayName());
		configurationFile.getRootElement().addContent(plattform);
		
		if(!this.checkCasava.equals(checkCasava_DV)){
			Element checkCasava = new Element(CHECKCASAVA);
			checkCasava.addContent(this.getCheckCasava().toString());
			configurationFile.getRootElement().addContent(checkCasava);
		}
		
		Element dirOutBase = new Element(DIROUTBASE);
		dirOutBase.addContent(this.getDirOutBase().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(dirOutBase);
		
		Element projectId = new Element(PROJECTID);
		projectId.addContent(this.getProjectId());
		configurationFile.getRootElement().addContent(projectId);
		
		if(!this.getUserName().equals(userName_DV)){
			Element userName = new Element(USERNAME);
			userName.addContent(this.getUserName());
			configurationFile.getRootElement().addContent(userName);
		}
		
		Element inDirPreProcess = new Element(INDIRPREPROCESS);
		inDirPreProcess.addContent(this.getDataInDirpreProcess().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(inDirPreProcess);
		
		for(CSExperiment experiment : this.getExperiments()){
			Element chipSeqExperiment = new Element(EXPERIMENT);
			
			for(ChipSeqUnit csUnit : experiment.getChipSeqUnits()){
				Element csUnitElement = new Element(CHIPSEQUNIT);
				
				Sample sampleTreatment = csUnit.getSampleTreatment();
				
				Element sampleTreatmentElement = new Element(SAMPLE_TREATMENT);
				
				Element sampleName = new Element(SAMPLE_NAME);
				sampleName.addContent(sampleTreatment.getSampleName());
				sampleTreatmentElement.addContent(sampleName);

				Element sampleFiles = new Element(SAMPLE_FILES);
				sampleFiles.addContent(sampleTreatment.getSampleFiles());
				sampleTreatmentElement.addContent(sampleFiles);

				Element sampleSuffix = new Element(SAMPLE_SUFFIX);
				sampleSuffix.addContent(sampleTreatment.getSampleSuffix());
				sampleTreatmentElement.addContent(sampleSuffix);

				Element sampleType = new Element(SAMPLE_TYPE);
				sampleType.addContent(sampleTreatment.getSampleType().getDisplayName());
				sampleTreatmentElement.addContent(sampleType);
				
				csUnitElement.addContent(sampleTreatmentElement);
				
				Sample sampleInput = csUnit.getSampleInput();
				if(sampleInput.checkSample()){
					
					Element sampleInputElement = new Element(SAMPLE_INPUT);
					
					Element sampleInputName = new Element(SAMPLE_NAME);
					sampleInputName.addContent(sampleInput.getSampleName());
					sampleInputElement.addContent(sampleInputName);

					Element sampleInputFiles = new Element(SAMPLE_FILES);
					sampleInputFiles.addContent(sampleInput.getSampleFiles());
					sampleInputElement.addContent(sampleInputFiles);

					Element sampleInputSuffix = new Element(SAMPLE_SUFFIX);
					sampleInputSuffix.addContent(sampleInput.getSampleSuffix());
					sampleInputElement.addContent(sampleInputSuffix);

					Element sampleInputType = new Element(SAMPLE_TYPE);
					sampleInputType.addContent(sampleInput.getSampleType().getDisplayName());
					sampleInputElement.addContent(sampleInputType);
					
					csUnitElement.addContent(sampleInputElement);
				}
				
				chipSeqExperiment.addContent(csUnitElement);
			}
			
			Element replicatesFlag = new Element(REPLICATES_FLAG);
			replicatesFlag.addContent(experiment.isReplicatesFlag()?"1":"0");
			chipSeqExperiment.addContent(replicatesFlag);
			
			configurationFile.getRootElement().addContent(chipSeqExperiment);
		}
		
		Element chromSize = new Element(CHROMSIZE);
		chromSize.addContent(this.getChromSize().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(chromSize);
		
		if(!this.getPeakAnalysisType().equals(peakAnalysisType_DV)){
			Element peakAnalysis = new Element(PEAKANALYSIS);
			peakAnalysis.addContent(this.getPeakAnalysisType().getDisplayName());
			configurationFile.getRootElement().addContent(peakAnalysis);
		}
		
		if(this.getCcatConfigFile()!=ccatConfigFile_DV){
			Element ccaConfigFile = new Element(CCAT_CONFIG_FILE);
			ccaConfigFile.addContent(this.getCcatConfigFile().getFile().getAbsolutePath());
			configurationFile.getRootElement().addContent(ccaConfigFile);
		}
		
		if(!this.getMacsExtraArgs().equals(macsExtraArgs_DV)){
			Element macsExtraArgs = new Element(MACS_EXTRAARGS);	
			macsExtraArgs.addContent(this.getMacsExtraArgs());
			configurationFile.getRootElement().addContent(macsExtraArgs);
		}
		
		if(!this.getFastqc().equals(fastqc_DV)){
			Element fastqc = new Element(FASTQC);
			fastqc.addContent(this.getFastqc().toString());
			configurationFile.getRootElement().addContent(fastqc);
		}
		
		
		if(this.getAnnotFile()!=annotFile_DV){
			Element annotFile = new Element(ANNOTFILE);
			annotFile.addContent(this.getAnnotFile().getFile().getAbsolutePath());
			configurationFile.getRootElement().addContent(annotFile);
		}
		
		if(!this.getQueueSGEProject().equals(queueSGEProject_DV)){
			Element queueProject = new Element(QUEUESGEPROJECT);
			queueProject.addContent(this.getQueueSGEProject().toString());
			configurationFile.getRootElement().addContent(queueProject);
		}
		
		XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(configurationFile, new FileWriter(output));
	}
	
	public boolean checkExperiments(){
		if (this.getExperiments().size() == 0){
			return false;
		}else{
			boolean validExperiments = true;
			for(CSExperiment experiment : this.getExperiments()){
				if(validExperiments == false){
					break;
				}
				if(experiment.getChipSeqUnits().size() == 0){
					validExperiments = false;
					break;
				}else{
					for(ChipSeqUnit csUnit : experiment.getChipSeqUnits()){
						if(!csUnit.getSampleTreatment().checkSample()){
							validExperiments = false;
							break;
						}
					}
				}
			}
			return validExperiments;
		}
	}

	public static String getWorkingDirectory(File inputFile){
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			Document document = saxBuilder.build(inputFile);
			Element configData = document.getRootElement(); 
			
			Element dirOutBase = configData.getChild(DIROUTBASE);
			Element projectId = configData.getChild(PROJECTID);
			if(dirOutBase.getValue().endsWith("/")){
				return dirOutBase.getValue() + projectId.getValue();
			} else {
				return dirOutBase.getValue() + "/" + projectId.getValue();
			}
			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String getWorkingDirectory(){
		return this.getDirOutBase().getFile().getAbsolutePath() + "/" + this.getProjectId();
	}
	
	public boolean validElement(Element e){
		return e!=null && e.getValue()!=null && !e.getValue().equals("");
	}
	
	public void loadDataFromFile(File editFile) throws InvalidRubioSeqParameterException {
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			Document document = saxBuilder.build(editFile);
			Element configData = document.getRootElement(); 
			
			Element genRef = configData.getChild(GENREF);
			if(validElement(genRef)){
				this.setGenRefPath(Utils.getRUbioSeqFile(genRef.getValue()));
			}
			
			Element plattform = configData.getChild(PLATTFORM);
			if(validElement(plattform)){
				String plattformValue = plattform.getValue().toLowerCase();
				if(plattformValue.equals("illumina")){
					this.setPlattform(PlattformTech.ILLUMINA);
				} else if(plattformValue.equals("ion")){
						this.setPlattform(PlattformTech.ION);
					} else if(plattformValue.equals("solid")){
							this.setPlattform(PlattformTech.SOLID);
						}
			}
			
			Element checkCasava = configData.getChild(CHECKCASAVA);
			if(validElement(checkCasava)){
				this.setCheckCasava(Integer.valueOf(checkCasava.getValue()));
			}
			
			Element dirOutBase = configData.getChild(DIROUTBASE);
			if(validElement(dirOutBase)){
				this.setDirOutBase(Utils.getRUbioSeqFile(dirOutBase.getValue()));
			}
			
			Element projectId = configData.getChild(PROJECTID);
			if(validElement(projectId)){
				this.setProjectId(projectId.getValue());
			}
			
			Element userName = configData.getChild(USERNAME);
			if(validElement(userName)){
				this.setUserName(userName.getValue());
			}
			
			Element dataInDirpreProcess = configData.getChild(INDIRPREPROCESS);
			if(validElement(dataInDirpreProcess)){
				this.setDataInDirpreProcess(Utils.getRUbioSeqFile(dataInDirpreProcess.getValue()));
			}
			
			for(Element experimentElement : configData.getChildren(EXPERIMENT)){
				CSExperiment newExperiment = new CSExperiment();
				
				for(Element csUnitElement : experimentElement.getChildren(CHIPSEQUNIT)){
					ChipSeqUnit newCSUnit = new ChipSeqUnit();
					
					Sample sampleTreatment = new Sample();
					newCSUnit.setSampleTreatment(sampleTreatment);
					
					Element sampleTreatmentElement = csUnitElement.getChild(SAMPLE_TREATMENT);
					Element sampleName = sampleTreatmentElement.getChild(SAMPLE_NAME);
					sampleTreatment.setSampleName(sampleName.getValue());
					Element sampleFiles = sampleTreatmentElement.getChild(SAMPLE_FILES);
					sampleTreatment.setSampleFiles(sampleFiles.getValue());
					Element sampleSuffix = sampleTreatmentElement.getChild(SAMPLE_SUFFIX);
					sampleTreatment.setSampleSuffix(sampleSuffix.getValue());
					Element sampleType = sampleTreatmentElement.getChild(SAMPLE_TYPE);
					sampleTreatment.setSampleType(sampleType.getValue().equals("1")?SampleType.SingleEnd:SampleType.PairedEnd);
					
					
					Element sampleInputElement = csUnitElement.getChild(SAMPLE_INPUT);
					if(sampleInputElement!=null){
						Sample sampleInput = new Sample();
						newCSUnit.setSampleInput(sampleInput);
					
						Element sampleInputName = sampleInputElement.getChild(SAMPLE_NAME);
						sampleInput.setSampleName(sampleInputName.getValue());
						Element sampleInputFiles = sampleInputElement.getChild(SAMPLE_FILES);
						sampleInput.setSampleFiles(sampleInputFiles.getValue());
						Element sampleInputSuffix = sampleInputElement.getChild(SAMPLE_SUFFIX);
						sampleInput.setSampleSuffix(sampleInputSuffix.getValue());
						Element sampleInputType = sampleInputElement.getChild(SAMPLE_TYPE);
						sampleInput.setSampleType(sampleInputType.getValue().equals("1")?SampleType.SingleEnd:SampleType.PairedEnd);
					}
					
					
					newExperiment.getChipSeqUnits().add(newCSUnit);
				}
				
				Element replicatesFlag = experimentElement.getChild(REPLICATES_FLAG);
				newExperiment.setReplicatesFlag(replicatesFlag.getValue().equals("1")?true:false);
				
				this.getExperiments().add(newExperiment);
			}

			Element chromSize = configData.getChild(CHROMSIZE);
			if(validElement(chromSize)){
				this.setChromSize(Utils.getRUbioSeqFile(chromSize.getValue()));
			}
			
			Element peakAnalysis = configData.getChild(PEAKANALYSIS);
			if(validElement(peakAnalysis)){
				String peakAnalysisValue = peakAnalysis.getValue().toLowerCase();
				if(peakAnalysisValue.equals("sharp")){
					this.setPeakAnalysisType(PeakAnalysisType.SHARP);
				} else if(peakAnalysisValue.equals("broad")){
					this.setPeakAnalysisType(PeakAnalysisType.BROAD);
					} else if(peakAnalysisValue.equals("both")){
						this.setPeakAnalysisType(PeakAnalysisType.BOTH);
						}
			}

			Element ccaConfigFile = configData.getChild(CCAT_CONFIG_FILE);
			if(validElement(ccaConfigFile)){
				this.setCcatConfigFile(Utils.getRUbioSeqFile(ccaConfigFile.getValue()));
			}
			
			Element macsExtraArgs = configData.getChild(MACS_EXTRAARGS);
			if(validElement(macsExtraArgs)){
				this.setMacsExtraArgs(macsExtraArgs.getValue());
			}
			
			Element fastqc = configData.getChild(FASTQC);
			if(validElement(fastqc)){
				this.setFastqc(Integer.valueOf(fastqc.getValue()));
			}
			
			Element annotFile = configData.getChild(ANNOTFILE);
			if(validElement(annotFile)){
				this.setAnnotFile(Utils.getRUbioSeqFile(annotFile.getValue()));
			}
			
			Element queueSGEProject = configData.getChild(QUEUESGEPROJECT);
			if(validElement(queueSGEProject)){
				this.setQueueSGEProject(queueSGEProject.getValue());
			}
			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  catch(Exception e){
			throw new InvalidRubioSeqParameterException(InvalidRubioSeqParameterException.DEFAULT_MESSAGE);
		}
	}
	
	@Override
	public List<String> getPathsToWatch() {
		List<String> pathsToWatch = new LinkedList<String>();
		String workingDirectory = this.getWorkingDirectory();
		// Must watch one directory by <Experiment>
		int experimentsCount = 1;  
		for(@SuppressWarnings("unused") CSExperiment cse : this.getExperiments()){
			pathsToWatch.add(workingDirectory + "/Experiment" + String.valueOf(experimentsCount++));
		}
		return pathsToWatch;
	}

	@Override
	public double getIncrement(RUbioSeqEvent event, int numStages) {
		System.err.print("\t[ChipSeqExperimentExperiment] Get increment of execution level " + event.getExecutionLevel());
		int k = event.getTotalSteps();
		switch (event.getExecutionLevel()) {
		case 0:
			return 0;
		case 1:
			/*
			 * Stage 1. Alignment phase and FastQC analysis
			 * By sample.
			 * Possible cases: 
			 * 	- samples not splitted: Total should be incremented in (1 / (samples x stages x k) ) x 100
			 *  - samples splitted: Total should be incremented in (1 / ((sample_splits + 1 )x samples x stages x k) ) x 100
			 *  (sample_splits + 1: this 1 extra is for the concatenation step that will be received as a SPLIT_FILES_CONCAT event).
			 */
			int sampleSplits = getSampleSplits(event.getSampleName());
			if(sampleSplits == 1){
				System.err.println("\t" + (double) 100 / (double) (getNumSamples()*numStages*k));
				return (double) 100 / (double) (getNumSamples()*numStages*k);
			}else{
				System.err.println("\t" + (double) 100 / (double) (getNumSamples()*numStages*k*(sampleSplits+1)));
				return (double) 100 / (double) (getNumSamples()*numStages*k*(sampleSplits+1));
			}
		case 2:
			/*
			 * 2. Duplicates marking
			 * By sample.
			 * Total should be incremented in (1 / (samples x stages x k) ) x 100
			 */
			System.err.println("\t" + 100 / (getNumSamples()*numStages*k));
			return (double) 100 / (double) (getNumSamples()*numStages*k);
		case 3:
			/*
			 * 3. Normalization steps
			 * By Experiment.
			 * 	Total should be incremented in (1 / (experiments x stages x k) ) x 100
			 */
			System.err.println("\t" + (double) 100 / (double) (getNumExperiments()*numStages*k));
			return (double) 100 / (double) (getNumExperiments()*numStages*k);
		case 4:
			/*
			 * 4. ChIPseq calling , Peaks annotation and IDR control (if it corresponds).
			 * By CHipSeqUnit
			 * If replicafesFlag = 0 (no IDR analysis)
			 *	Total should be incremented in (1 / (getChipSeqUnits(experiment) x experiments x stages x k) ) x 100
			 * If replicatesFlag = 1 (IDR analysis is a substage of this experiment)
			 * 	Total should be incremented in (1 / ((getChipSeqUnits(experiment) + 1) x experiments x stages x k) ) x 100 
			 */
			String experimentName = event.getSampleName();
			int replicatesFlagStage = 0;
			if(getReplicatesFlag(experimentName) && 
					(getPeakAnalysisType().equals(PeakAnalysisType.SHARP) || getPeakAnalysisType().equals(PeakAnalysisType.BOTH))){
				replicatesFlagStage = 1;
			}
			System.err.println("\t" + (double) 100 / (double) ((getChipSeqUnits(experimentName) + replicatesFlagStage)*getNumExperiments() *numStages*k));
			return (double) 100 / (double) ((getChipSeqUnits(experimentName) + replicatesFlagStage)*getNumExperiments() *numStages*k);
		default:
			/*
			 * If execution level is not 1,2,3 or 4, we must se if it is an extra stage.
			 */
			if(event.getExtraStage().equals(RUbioSeqEvent.ExtraStage.SPLIT_FILES_CONCAT) &&
					event.getMessage().equals(RUbioSeqEvent.SPLIT_FILES_CONCAT_MESSAGE)){
				System.err.println("\t" + (double) 100 / (double) (getNumSamples()*numStages*k*(getSampleSplits(event.getSampleName())+1)));
				return (double) 100 / (double) (getNumSamples()*numStages*k*(getSampleSplits(event.getSampleName())+1));
			}
			return 0;
		}
	}
	
	public int getExperimentNumber(String experimentName){
		return Integer.valueOf(experimentName.replace("Experiment", ""))-1;
	}
	
	public boolean getReplicatesFlag(String experimentName){
		System.err.print("\t\tGetting the replicates flag for experimentName= "+ experimentName + " ... ");
		System.err.print(this.getExperiments().get(getExperimentNumber(experimentName)).isReplicatesFlag() + ".");
		System.err.println();
		return this.getExperiments().get(getExperimentNumber(experimentName)).isReplicatesFlag();
	}
	
	public int getChipSeqUnits(String experimentName){
		System.err.print("\t\tGetting the chipSeqUnits amount for experimentName= "+ experimentName + " ... ");
		System.err.print(this.getExperiments().get(getExperimentNumber(experimentName)).getChipSeqUnits().size() + ".");
		System.err.println();
		return this.getExperiments().get(getExperimentNumber(experimentName)).getChipSeqUnits().size();
	}

	public int getNumExperiments(){
		return this.getExperiments().size();
	}
	
	private int numSamples = -1;
	
	public int getNumSamples(){
		if(numSamples == -1){
			Set<String> sampleNames = new HashSet<String>();
			for(CSExperiment cse : this.getExperiments()){
				for(ChipSeqUnit csu : cse.getChipSeqUnits()){
					if(csu.getSampleInput().getSampleName()!=null){
						sampleNames.add(csu.getSampleInput().getSampleName());
					}
					if(csu.getSampleTreatment().getSampleName()!=null){
						sampleNames.add(csu.getSampleTreatment().getSampleName());
					}
				}
			}
			numSamples = sampleNames.size();
		}
		return numSamples;
	}
	
	private List<Sample> samples;
	
	private List<Sample> getSamples(){
		if(samples == null){
			samples = new LinkedList<Sample>();
			for(CSExperiment cse : this.getExperiments()){
				for(ChipSeqUnit csu : cse.getChipSeqUnits()){
					if(csu.getSampleInput().checkSample()) {
						samples.add(csu.getSampleInput());
					}
					if(csu.getSampleTreatment().checkSample()){
						samples.add(csu.getSampleTreatment());
					}
				}
			}
		}
		return samples;
	}
	
	public int getSampleSplits(String sampleName){
		for(Sample s : this.getSamples()){
			if(
					s.getSampleName().equals(sampleName) 		// We get the sampleName (case of log_CON_sampleName.txt) 
					|| s.getSampleFiles().contains(sampleName)	// We are passed the name of a sampleSplit (case of log_S1_sampleSplitName.txt)
				){
				try{
					String[] sampleSplit = s.getSampleFiles().split(":");
					return sampleSplit.length;
				}catch(Exception e){
					return 0;
				}
			}
		}
		return 0;
	}

	public boolean checkPaths() {
		if (this.getGenRefPath() instanceof InvalidRUbioSeqFile
				|| this.getDirOutBase() instanceof InvalidRUbioSeqFile
				|| this.getDataInDirpreProcess() instanceof InvalidRUbioSeqFile
				|| this.getChromSize() instanceof InvalidRUbioSeqFile
				|| (this.getCcatConfigFile()!= null && this.getCcatConfigFile() instanceof InvalidRUbioSeqFile)
				|| (this.getAnnotFile()!=null && this.getAnnotFile() instanceof InvalidRUbioSeqFile))
			return false;
		else
			return true;
	}
	
	@Override
	public boolean checkConfiguration(){
		return this.checkPaths();
	}
}
