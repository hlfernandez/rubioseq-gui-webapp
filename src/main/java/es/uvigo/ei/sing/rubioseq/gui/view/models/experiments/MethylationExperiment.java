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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import es.uvigo.ei.sing.rubioseq.gui.macros.RUbioSeqFile;
import es.uvigo.ei.sing.rubioseq.gui.util.Utils;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.Measurable;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.RUbioSeqEvent;

/**
 * 
 * @author hlfernandez
 *
 */
public class MethylationExperiment implements Measurable{

	public static final String CONFIG_DATA = "configData";
	public static final String REFERENCEPATH = "referencePath";
	public static final String INTERVALS = "intervals";
	public static final String PLATTFORM = "platform";
	public static final String PROJECT_COMPLETE_PATH = "projectCompletePath";
	public static final String SAMPLE1 = "sample1";
	public static final String SAMPLE2 = "sample2";
	public static final String READSPATH = "readsPath";
	public static final String SEED_LENGTH = "seed_length";
	public static final String NUM_MIS = "num_mis";
	public static final String TRIMTAGLENGTH = "trimTagLength";
	public static final String MINQUAL = "minQual";
	public static final String DEPTHFILTER = "depthFilter";
	public static final String METHYLTYPE = "methylType";
	public static final String CONTEXT = "context";
	public static final String MULTIEXEC = "multiexec";
	public static final String FASTQC = "fastqc";
	public static final String QUEUESGEPROJECT = "queueSGEProject";
	
	private RUbioSeqFile referencePath;
	private RUbioSeqFile intervalsPath;
	private RUbioSeqFile intervalsPath_DV = null;
	private PlattformTech plattform;
	private RUbioSeqFile projectCompletePath;
	private List<MethylationSample> samples = new LinkedList<MethylationSample>();
	private RUbioSeqFile readsPath;
	private Integer seedLength = 28; /* Minimum: 5 */
	private Integer seedLength_DV = 28;
	private Integer numMis = 2; /* 0..3 */
	private Integer numMis_DV = 2;
	private String trimTagLength = "";
	private String trimTagLength_DV = "";
	private Double minQual = 0.0;
	private Double minQual_DV = 0.0;
	private Double depthFilter = 0.0;
	private Double depthFilter_DV = 0.0;
	private MethylType methylType;
	private ContextType contextType = ContextType.ALL;
	private ContextType contextType_DV = ContextType.ALL;
	private Integer multiExec = 0;
	private Integer multiExec_DV = 0;
	private Integer fastqc = 0;
	private Integer fastqc_DV = 0;
	private String queueSGEProject = "";
	private String queueSGEProject_DV = "";
	
	public List<Integer> getMultiExecValues(){
		return Arrays.asList(new Integer[]{0,1});
	}
	
	public List<Integer> getFastqcValues(){
		return Arrays.asList(new Integer[]{0,1});
	}
	
	public enum ContextType {
		ALL("ALL"), 
		CPG("CpG"), 
		CHG("CHG"), 
		CHH("CHH");
		
		private final String displayName;
		
		private ContextType(String displayName) {
			this.displayName = displayName;
		}	
		
		public String getDisplayName() {
			return displayName;
		}
	}
	
	public List<ContextType> getContextTypeValues(){
		return Arrays.asList(ContextType.values());
	}
	
	public enum MethylType {
		LISTER("Lister"), 
		COKUS("Cokus");
		
		private final String displayName;
		
		private MethylType(String displayName) {
			this.displayName = displayName;
		}
		
		
		public String getDisplayName() {
			return displayName;
		}
	}
	
	public List<MethylType> getMethylTypeValues(){
		return Arrays.asList(MethylType.values());
	}
	
	public enum PlattformTech {
		ILLUMINA("illumina"), 
		FASTQ("fastq");
		
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

	public RUbioSeqFile getReferencePath() {
		return referencePath;
	}

	public void setReferencePath(RUbioSeqFile referencePath) {
		this.referencePath = referencePath;
	}

	public RUbioSeqFile getIntervalsPath() {
		return intervalsPath;
	}

	public void setIntervalsPath(RUbioSeqFile intervalsPath) {
		this.intervalsPath = intervalsPath;
	}

	public PlattformTech getPlattform() {
		return plattform;
	}

	public void setPlattform(PlattformTech plattform) {
		this.plattform = plattform;
	}

	public RUbioSeqFile getProjectCompletePath() {
		return projectCompletePath;
	}

	public void setProjectCompletePath(RUbioSeqFile projectCompletePath) {
		this.projectCompletePath = projectCompletePath;
	}

	public List<MethylationSample> getSamples() {
		return samples;
	}
	
	public void setSamples(List<MethylationSample> samples) {
		this.samples = samples;
	}

	public RUbioSeqFile getReadsPath() {
		return readsPath;
	}

	public void setReadsPath(RUbioSeqFile readsPath) {
		this.readsPath = readsPath;
	}

	public Integer getSeedLength() {
		return seedLength;
	}

	public void setSeedLength(Integer seedLength) {
		this.seedLength = seedLength;
	}

	public Integer getSeedLength_DV() {
		return seedLength_DV;
	}
	
	public Integer getNumMis() {
		return numMis;
	}

	public void setNumMis(Integer numMis) {
		this.numMis = numMis;
	}

	public Double getMinQual() {
		return minQual;
	}

	public void setMinQual(Double minQual) {
		this.minQual = minQual;
	}

	public Double getDepthFilter() {
		return depthFilter;
	}

	public void setDepthFilter(Double depthFilter) {
		this.depthFilter = depthFilter;
	}

	public MethylType getMethylType() {
		return methylType;
	}

	public void setMethylType(MethylType methylType) {
		this.methylType = methylType;
	}

	public ContextType getContextType() {
		return contextType;
	}

	public void setContextType(ContextType contextType) {
		this.contextType = contextType;
	}

	public Integer getMultiExec() {
		return multiExec;
	}

	public void setMultiExec(Integer multiExec) {
		this.multiExec = multiExec;
	}

	public Integer getFastqc() {
		return fastqc;
	}

	public void setFastqc(Integer fastqc) {
		this.fastqc = fastqc;
	}

	public String getQueueSGEProject() {
		return queueSGEProject;
	}

	public void setQueueSGEProject(String queueSGEProject) {
		this.queueSGEProject = queueSGEProject;
	}

	public RUbioSeqFile getIntervalsPath_DV() {
		return intervalsPath_DV;
	}

	public Integer getNumMis_DV() {
		return numMis_DV;
	}

	public Double getMinQual_DV() {
		return minQual_DV;
	}

	public Double getDepthFilter_DV() {
		return depthFilter_DV;
	}

	public ContextType getContextType_DV() {
		return contextType_DV;
	}

	public Integer getMultiExec_DV() {
		return multiExec_DV;
	}

	public Integer getFastqc_DV() {
		return fastqc_DV;
	}

	public String getQueueSGEProject_DV() {
		return queueSGEProject_DV;
	}

	public String getTrimTagLength() {
		return trimTagLength;
	}

	public void setTrimTagLength(String trimTagLength) {
		this.trimTagLength = trimTagLength;
	}

	public String getTrimTagLength_DV() {
		return trimTagLength_DV;
	}

	public void generateXMLConfigurationFile(File output) throws IOException {
		Document configurationFile = new Document();
		Element configData = new Element(CONFIG_DATA);
		configurationFile.setRootElement(configData);
		configData.setAttribute(ExperimentUtils.BRANCH, ExperimentUtils.BRANCH_METHYLATION);
		
		Element referencePath = new Element(REFERENCEPATH);
		referencePath.addContent(this.getReferencePath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(referencePath);
		
		Element readsPath = new Element(READSPATH);
		readsPath.addContent(this.getReadsPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(readsPath);
		
		Element projectCompletePath = new Element(PROJECT_COMPLETE_PATH);
		projectCompletePath.addContent(this.getProjectCompletePath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(projectCompletePath);
		
		Element plattform = new Element(PLATTFORM);
		plattform.addContent(this.getPlattform().getDisplayName());
		configurationFile.getRootElement().addContent(plattform);
		
		Element methylType = new Element(METHYLTYPE);
		methylType.addContent(this.getMethylType().toString());
		configurationFile.getRootElement().addContent(methylType);
		
		for(MethylationSample ms : this.getSamples()){
			Element sample1 = new Element(SAMPLE1);
			sample1.addContent(ms.getSample1().getFile().getName());
			configurationFile.getRootElement().addContent(sample1);
		}
		
		for(MethylationSample ms : this.getSamples()){
			if(ms.getSample2()!=null){
				Element sample2 = new Element(SAMPLE2);
				sample2.addContent(ms.getSample2().getFile().getName());
				configurationFile.getRootElement().addContent(sample2);
			}
		}
		
		if(!this.getSeedLength().equals(seedLength_DV)){
			Element seedLength = new Element(SEED_LENGTH);
			seedLength.addContent(this.getSeedLength().toString());
			configurationFile.getRootElement().addContent(seedLength);
		}
		
		if(!this.getNumMis().equals(numMis_DV)){
			Element numMiss = new Element(NUM_MIS);
			numMiss.addContent(this.getNumMis().toString());
			configurationFile.getRootElement().addContent(numMiss);
		}
		
		if(this.getIntervalsPath()!=intervalsPath_DV){
			Element intervalsPath = new Element(INTERVALS);
			intervalsPath.addContent(this.getIntervalsPath().getFile().getAbsolutePath());
			configurationFile.getRootElement().addContent(intervalsPath);
		}
		
		if(!this.getContextType().equals(contextType_DV)){
			Element contextType = new Element(CONTEXT);
			contextType.addContent(this.getContextType().toString());
			configurationFile.getRootElement().addContent(contextType);
		}
		
		if(!this.getTrimTagLength().equals(trimTagLength_DV)){
			Element trimTagLength = new Element(TRIMTAGLENGTH);
			trimTagLength.addContent(this.getTrimTagLength());
			configurationFile.getRootElement().addContent(trimTagLength);
		}
		
		if(!this.getMinQual().equals(minQual_DV)){
			Element minQual = new Element(MINQUAL);
			minQual.addContent(this.getMinQual().toString());
			configurationFile.getRootElement().addContent(minQual);
		}
		
		if(!this.getFastqc().equals(fastqc_DV)){
			Element fastqc = new Element(FASTQC);
			fastqc.addContent(this.getFastqc().toString());
			configurationFile.getRootElement().addContent(fastqc);
		}
		
		if(!this.getDepthFilter().equals(depthFilter_DV)){
			Element depthFilter = new Element(DEPTHFILTER);
			depthFilter.addContent(this.getDepthFilter().toString());
			configurationFile.getRootElement().addContent(depthFilter);
		}
		
		if(!this.getQueueSGEProject().equals(queueSGEProject_DV)){
			Element queueProject = new Element(QUEUESGEPROJECT);
			queueProject.addContent(this.getQueueSGEProject().toString());
			configurationFile.getRootElement().addContent(queueProject);
		}
		
		if(!this.getMultiExec().equals(multiExec_DV)){
			Element multiExec = new Element(MULTIEXEC);
			multiExec.addContent(this.getMultiExec().toString());
			configurationFile.getRootElement().addContent(multiExec);
		}

		XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(configurationFile, new FileWriter(output));
	}
	
	public static String getWorkingDirectory(File inputFile){
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			Document document = saxBuilder.build(inputFile);
			Element configData = document.getRootElement(); 
			
			Element projectCompletePath = configData.getChild(PROJECT_COMPLETE_PATH);

			return projectCompletePath.getValue();
			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String getWorkingDirectory(){
		return this.getProjectCompletePath().getFile().getAbsolutePath();
	}
	
	public boolean validElement(Element e){
		return e!=null && e.getValue()!=null && !e.getValue().equals("");
	}
	
	public void loadDataFromFile(File editFile) throws InvalidRubioSeqParameterException {
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			Document document = saxBuilder.build(editFile);
			Element configData = document.getRootElement(); 
			
			Element referencePath = configData.getChild(REFERENCEPATH);
			if(validElement(referencePath)){
				this.setReferencePath(Utils.getRUbioSeqFile(referencePath.getValue()));
			}

			Element intervals = configData.getChild(INTERVALS);
			if(validElement(intervals)){
				this.setIntervalsPath(Utils.getRUbioSeqFile(intervals.getValue()));
			}
			
			Element plattform = configData.getChild(PLATTFORM);
			if(validElement(plattform)){
				String plattformValue = plattform.getValue().toLowerCase();
				if(plattformValue.equals("illumina")){
					this.setPlattform(PlattformTech.ILLUMINA);
				} else if(plattformValue.equals("fastq")){
						this.setPlattform(PlattformTech.FASTQ);
					} 
			}

			Element projectCompletePath = configData.getChild(PROJECT_COMPLETE_PATH);
			if(validElement(projectCompletePath)){
				this.setProjectCompletePath(Utils.getRUbioSeqFile(projectCompletePath.getValue()));
			}
			
			Element readsPath = configData.getChild(READSPATH);
			if(validElement(readsPath)){
				this.setReadsPath(Utils.getRUbioSeqFile(readsPath.getValue()));
			}

			Iterator<Element> sample1It = configData.getChildren(SAMPLE1).iterator();
			Iterator<Element> sample2It = configData.getChildren(SAMPLE2).iterator();
			this.samples = new LinkedList<MethylationSample>();
			
			while(sample1It.hasNext()){
				
				MethylationSample newMS = new MethylationSample();
				
				Element sample1 = sample1It.next();
				if (validElement(sample1)) {
					newMS.setSample1(Utils.getRUbioSeqFile(Utils
							.getDirectoryNameWithFinalSlash(this.getReadsPath()
									.getFile().getAbsolutePath())
							+ sample1.getValue()));
				}

				if (sample2It.hasNext()) {
					Element sample2 = sample2It.next();
					if (validElement(sample2)) {
						newMS.setSample2(Utils.getRUbioSeqFile(Utils
								.getDirectoryNameWithFinalSlash(this
										.getReadsPath().getFile()
										.getAbsolutePath())
								+ sample2.getValue()));
					}
				}
				
				this.getSamples().add(newMS);
			}

			Element seedLength = configData.getChild(SEED_LENGTH);
			if(validElement(seedLength)){
				this.setSeedLength(Integer.valueOf(seedLength.getValue()));
			}
			
			Element numMiss = configData.getChild(NUM_MIS);
			if(validElement(numMiss)){
				this.setNumMis(Integer.valueOf(numMiss.getValue()));
			}
			
			Element minQual = configData.getChild(MINQUAL);
			if(validElement(minQual)){
				this.setNumMis(Integer.valueOf(minQual.getValue()));
			}
			
			Element depthFilter = configData.getChild(DEPTHFILTER);
			if(validElement(depthFilter)){
				this.setDepthFilter(Double.valueOf(depthFilter.getValue()));
			}
			
			Element methylType = configData.getChild(METHYLTYPE);
			if(validElement(methylType)){
				String methylTypeValue = methylType.getValue().toUpperCase();
				if(methylTypeValue.equals("LISTER")){
					this.setMethylType(MethylType.LISTER);
				} else if(methylTypeValue.equals("COKUS")){
						this.setMethylType(MethylType.COKUS);
					}
			}

			Element contextType = configData.getChild(CONTEXT);
			if(validElement(contextType)){
				String contextTypeValue = contextType.getValue();
				if(contextTypeValue.equals("ALL")){
					this.setContextType(ContextType.ALL);
				} else if(contextTypeValue.equals("CpG")){
						this.setContextType(ContextType.CPG);
					} else if(contextTypeValue.equals("CHG")){
							this.setContextType(ContextType.CHG);
						} else if(contextTypeValue.equals("CHH")){
								this.setContextType(ContextType.CHH);
							}
			}
			
			Element multiExec = configData.getChild(MULTIEXEC);
			if(validElement(multiExec)){
				this.setMultiExec(Integer.valueOf(multiExec.getValue()));
			}
			
			Element trimTagLenght = configData.getChild(TRIMTAGLENGTH);
			if(validElement(trimTagLenght)){
				this.setTrimTagLength(trimTagLenght.getValue());
			}

			Element fastqc = configData.getChild(FASTQC);
			if(validElement(fastqc)){
				this.setFastqc(Integer.valueOf(fastqc.getValue()));
			}
			
			Element queueSGEProject = configData.getChild(QUEUESGEPROJECT);
			if(validElement(queueSGEProject)){
				this.setQueueSGEProject(queueSGEProject.getValue());
			}
			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			throw new InvalidRubioSeqParameterException(InvalidRubioSeqParameterException.DEFAULT_MESSAGE);
		}
	}

	public boolean checkSamples() {
		int countSample1Instances = 0;
		int countSample2Instances = 0;
		for(MethylationSample ms : this.getSamples()){
			if(ms.getSample1() == null){
				return false;
			} else{
				countSample1Instances++;
			}
			if(ms.getSample2()!=null){
				countSample2Instances++;
			}
		}
		if(countSample2Instances == 0){
			return true;
		} else{
			return countSample1Instances == countSample2Instances;
		}
	}
	
	@Override
	public List<String> getPathsToWatch() {
		List<String> pathsToWatch = new LinkedList<String>();
		String workingDirectory = this.getWorkingDirectory();
		if(this.getMultiExec().equals(1) && this.getSamples().size() > 1){
			// Joint multisample mode --> All logs go to "multi"
			pathsToWatch.add(workingDirectory + "/" + "multi");
		} else{
			// Must watch one directory by <SampleName>
			for(MethylationSample s : this.getSamples()){
				pathsToWatch.add(workingDirectory + "/" + s.getSample1().getFile().getName());
			}
		}
		return pathsToWatch;
	}

	@Override
	public double getIncrement(RUbioSeqEvent event, int numStages) {
		System.err.print("\t[MethylationExperiment] Get increment of execution level " + event.getExecutionLevel());
		int k = event.getTotalSteps();
		switch (event.getExecutionLevel()) {
		case 0:
			return 0;
		case 1:
			/*
			 * Stage 1. Sequence alignment and methylation calling.
			 * By sample. This phase may have an optional extra substage if trimTagLength parameter has been
			 * configured. 
			 * Possible cases: 
			 * 	Without trimming: Total should be incremented in (1 / (samples x stages x k) ) x 100
			 * 	With trimming: Total should be incremented in (1 / (samples x stages x k) ) x 100
			 * 		In this case, if the experiment is not paired the number of samples should be samples*2 (one step
			 * for trim and another for aligment and duplicates marking) while if the experiment is paired the number
			 * of samples should be samples*2 (one step for trim pair1, another for trim pair2 and another one for the
			 * alignment and duplicates marking).
			 */
			if(!this.getTrimTagLength().equals("")){
				if(this.isPaired()){
					System.err.println("\t" + (double) 100 / (double) (3*getNumSamples()*numStages*k) + " [With trimming + Paired]");
					return (double) 100 / (double) (3*getNumSamples()*numStages*k);
				} else{
					System.err.println("\t" + (double) 100 / (double) (2*getNumSamples()*numStages*k) + " [With trimming + Not Paired]");
					return (double) 100 / (double) (2*getNumSamples()*numStages*k);
				}
			} else{
				System.err.println("\t" + (double) 100 / (double) (getNumSamples()*numStages*k) + " [Without trimming]");
				return (double) 100 / (double) (getNumSamples()*numStages*k);
			}
		case 2:
			/*
			 * 2. Methylation calls extraction.
			 * This stage has two substages (two log files): extraction of methylation calls and formatting of
			 * methylation files so that increment should be divided by two.
			 * Two possible cases: By sample if multiExec = 0 and by group of samples if multiExec = 1
			 * 	By sample Total should be incremented in (1 / (2 x samples x stages x k) ) x 100 (becauase formatting is done for each sample)
			 *  By group of samples Total should be incremented in (1 / (2 x (samples+1) stages x k) ) x 100 (because formatting is done for all the samples)
			 */

			if(getMultiExec().equals(0)){
				System.err.println("\t" + 100 / (2*getNumSamples()*numStages*k) + " [Multiexec = 0]");
				return (double) 100 / (double) (2*getNumSamples()*numStages*k);
			}else{
//				if(event.getSampleName().equals("multi")){
//					
//				}
				System.err.println("\t" +100 / ((getNumSamples()+1)*numStages*k) + " [Multiexec = 1]");
				return (double) 100 / (double) ((getNumSamples()+1)*numStages*k);
			}
		case 3:
			/*
			 * 3. Intervals methylation calculation.
			 * Two possible cases: By sample if multiExec = 0 and by group of samples if multiExec = 1
			 *  By sample Total should be incremented in (1 / (samples x stages x k) ) x 100
			 *  By group of samples Total should be incremented in (1 / (stages x k) ) x 100
			 */
			if(getMultiExec().equals(0)){
				System.err.println("\t" + 100 / (getNumSamples()*numStages*k) + " [Multiexec = 0]");
				return (double) 100 / (double) (getNumSamples()*numStages*k);
			}else{
				System.err.println("\t" +100 / (numStages*k) + " [Multiexec = 1]");
				return (double) 100 / (double) (numStages*k);
			}
		default:
			/*
			 * If execution level is not 1,2,3 or 4, we must se if it is an extra stage.
			 */
			return 0;
		}
	}
	
	public boolean isPaired(){
		return this.getSamples().iterator().next().getSample2() != null;
	}
	
	public int getNumSamples(){
		return this.getSamples().size();
	}
}
