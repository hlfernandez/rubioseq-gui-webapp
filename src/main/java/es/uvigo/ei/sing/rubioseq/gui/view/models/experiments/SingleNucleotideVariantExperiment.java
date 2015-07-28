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

import es.uvigo.ei.sing.rubioseq.gui.macros.InvalidRUbioSeqFile;
import es.uvigo.ei.sing.rubioseq.gui.macros.RUbioSeqFile;
import es.uvigo.ei.sing.rubioseq.gui.util.Utils;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.HardFilter.HFilterType;
import es.uvigo.ei.sing.rubioseq.gui.view.models.experiments.Sample.SampleType;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.Measurable;
import es.uvigo.ei.sing.rubioseq.gui.view.models.progress.RUbioSeqEvent;

/**
 * 
 * @author hlfernandez
 *
 */
public class SingleNucleotideVariantExperiment implements Measurable, RUbioSeqExperiment {

	public static final String CONFIG_DATA = "configData";
	public static final String GENREF = "GenRef";
	public static final String DBSNPANNOT = "DbSnpAnnot";
	public static final String GENOMES1000ANNOT = "Genomes1000Annot";
	public static final String INDELANNOT = "IndelAnnot";
	public static final String INTERVALS = "Intervals";
	public static final String KNOWNINDELS = "KnownIndels";
	public static final String PLATTFORM = "Platform";
	public static final String CHECKCASAVA = "checkCasava";
	public static final String DIROUTBASE = "dirOutBase";
	public static final String PROJECTID = "ProjectId";
	public static final String USERNAME = "UserName";
	public static final String INDIRPREPROCESS = "InDirPreProcess";
	public static final String SAMPLE = "Sample";
	public static final String SAMPLE_NAME = "SampleName";
	public static final String SAMPLE_FILES = "SampleFiles";
	public static final String SAMPLE_SUFFIX = "SampleSuffix";
	public static final String SAMPLE_TYPE = "SampleType";
	public static final String CALLYING_TYPE = "CallingType";
	public static final String GATKOUTPUTMODE = "GATKoutputMode";
	public static final String DPMIN = "DPmin";
	public static final String MINQUAL = "minQual";
	public static final String RSFILTER = "rsFilter";
	public static final String RUBIOSEQMODE = "RUbioSeq_Mode";
	public static final String FASTQC = "fastqc";
	public static final String VEPFLAG = "VEPFlag";
	public static final String TCFLAG = "TCFlag";
	public static final String MDFLAG = "MDFlag";
	public static final String STANDCALLCONF = "standCallConf";
	public static final String STANDEMITCONF = "standEmitConf";
	public static final String QUEUESGEPROJECT = "queueSGEProject";
	public static final String VQSRBLOCK = "VQSR";
	public static final String MILLS = "Mills";
	public static final String HAPMAP = "HapMap";
	public static final String THOUSANDG = "ThousandG";
	public static final String HFILTERS = "HardFilters";
	public static final String HFILTER_DPMIN = "Dpmin";
	public static final String HFILTER_MINQUAL = "minQual";
	public static final String HFILTER_NAME_SNP = "HfilterNameSNP";
	public static final String HFILTER_RULE_SNP = "HfilterRuleSNP";
	public static final String HFILTER_NAME_INDEL = "HfilterNameINDEL";
	public static final String HFILTER_RULE_INDEL = "HfilterRuleINDEL";
	
	private RUbioSeqFile genRefPath;// = "/media/hlfernandez/sd128GB/Investigacion/Colaboraciones/RubioSeq/data/VariantTestData/reference/hg19.chr20.fa";
	private RUbioSeqFile dbSnpAnnotPath;// = "/media/hlfernandez/sd128GB/Investigacion/Colaboraciones/RubioSeq/data/VariantTestData/reference/dbsnp_132.hg19.chr20.vcf";
	private RUbioSeqFile genomes1000AnnotPath;// = "/media/hlfernandez/sd128GB/Investigacion/Colaboraciones/RubioSeq/data/VariantTestData/reference/1000G_omni2.5.hg19.chr20.vcf";
	private RUbioSeqFile indelAnnotPath;// = "/media/hlfernandez/sd128GB/Investigacion/Colaboraciones/RubioSeq/data/VariantTestData/reference/refseqhg19.chr20.rod";
	private RUbioSeqFile intervalsPath;// = "/media/hlfernandez/sd128GB/Investigacion/Colaboraciones/RubioSeq/data/VariantTestData/reference/intervals_hg19_chr20.bed";
	private RUbioSeqFile intervalsPath_DV = null;	/* Optional parameter. Default Value */
	private List<KnownIndels> knownIndels = new LinkedList<KnownIndels>();
	private PlattformTech plattform;
	private Integer checkCasava = 1;
	private Integer checkCasava_DV = 1;
	private RUbioSeqFile dirOutBase;// = "/media/hlfernandez/sd128GB/Investigacion/Colaboraciones/RubioSeq/data/VariantTestData/";
	private String projectId = "experiment-output";
	private String userName = "Undefined";
	private String userName_DV = "Undefined";
	private RUbioSeqFile dataInDirpreProcess;// = "/media/hlfernandez/sd128GB/Investigacion/Colaboraciones/RubioSeq/data/VariantTestData/";
	private CallingType callingType = CallingType.BOTH;
	private CallingType callingType_DV = CallingType.BOTH;
	private GATKoutputMode gATKoutputMode = GATKoutputMode.EMIT_VARIANTS_ONLY;
	private GATKoutputMode gATKoutputMode_DV = GATKoutputMode.EMIT_VARIANTS_ONLY;
	private Integer rsFilter = 0;
	private Integer rsFilter_DV = 0;
	private Integer rUbioSeqMode = 0;
	private Integer rUbioSeqMode_DV = 0;
	private Integer fastqc = 0;
	private Integer fastqc_DV = 0;
	private Integer vEPFlag = 0;
	private Integer vEPFlag_DV = 0;
	private Integer tCFlag = 0;
	private Integer tCFlag_DV = 0;
	private Integer mDFlag = 1;
	private Integer mDFlag_DV = 1;
	private Double standCallConf = 30.0;
	private Double standCallConf_DV = 30.0;
	private Double standEmitConf = 30.0;
	private Double standEmitConf_DV = 30.0;
	private String queueSGEProject = "";
	private String queueSGEProject_DV = "";
	private RUbioSeqFile VQSRblockMills = null;
	private VQRSHardFiltersChoice choiceVqrsHardFilters = VQRSHardFiltersChoice.NONE;
	private RUbioSeqFile VQSRblockMills_DV = null;
	private RUbioSeqFile VQSRblockHapMAp = null;
	private RUbioSeqFile VQSRblockHapMAp_DV = null;
	private RUbioSeqFile VQSRblockThousandG = null;
	private RUbioSeqFile VQSRblockThousandG_DV = null;
	private Integer DPmin = 0;
	private Integer DPmin_DV = 0;
	private Integer minQual = 0;
	private Integer minQual_DV = 0;
	private List<HardFilter> hardFilters = new LinkedList<HardFilter>();
	private List<Sample> samples = new LinkedList<Sample>();
	
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
	
	public enum VQRSHardFiltersChoice {
		NONE("None"), 
		VQRS("VQRS"),
		HARDFILTERS("Hard Filters");
		
		private final String displayName;
		
		private VQRSHardFiltersChoice(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
	}
	
	public List<VQRSHardFiltersChoice> getVqrsHardFiltersChoices(){
		return Arrays.asList(VQRSHardFiltersChoice.values());
	}
	
	public enum CallingType {
		SNP("SNP"), 
		INDEL("INDEL"),
		BOTH("BOTH");
		
	    private final String displayName;

	    private CallingType(String displayName) {
	      this.displayName = displayName;
	    }

	    public String getDisplayName() {
	      return displayName;
	    }
	}
	
	public List<CallingType> getCallingTypes(){
		return Arrays.asList(CallingType.values());
	}
	
	public enum GATKoutputMode {
		 EMIT_VARIANTS_ONLY("EMIT_VARIANTS_ONLY"), 
		 EMIT_ALL_SITES("EMIT_ALL_SITES"),
		 EMIT_ALL_CONFIDENT_SITES("EMIT_ALL_CONFIDENT_SITES");
		
		private final String displayName;
		
		private GATKoutputMode(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
	}
	public List<GATKoutputMode> getGatkOutputModes(){
		return Arrays.asList(GATKoutputMode.values());
	}
	
	public List<Integer> getCasavaValues(){
		return Arrays.asList(new Integer[]{0,1});
	}
	
	public List<Integer> getrsFilterValues(){
		return Arrays.asList(new Integer[]{0,1});
	}
	
	public List<Integer> getRubioSeqModeValues(){
		return Arrays.asList(new Integer[]{0,1});
	}
	
	public List<Integer> getFastqcValues(){
		return Arrays.asList(new Integer[]{0,1});
	}
	
	public List<Integer> getVepFlagValues(){
		return Arrays.asList(new Integer[]{0,1});
	}
	
	public List<Integer> getTcFlagValues(){
		return Arrays.asList(new Integer[]{0,1});
	}
	
	public List<Integer> getMdFlagValues(){
		return Arrays.asList(new Integer[]{0,1});
	}

	public RUbioSeqFile getGenRefPath() {
		return genRefPath;
	}

	public void setGenRefPath(RUbioSeqFile genRefPath) {
		this.genRefPath = genRefPath;
	}
	
	public RUbioSeqFile getDbSnpAnnotPath() {
		return dbSnpAnnotPath;
	}

	public void setDbSnpAnnotPath(RUbioSeqFile dbSnpAnnotPath) {
		this.dbSnpAnnotPath = dbSnpAnnotPath;
	}

	public RUbioSeqFile getGenomes1000AnnotPath() {
		return genomes1000AnnotPath;
	}

	public void setGenomes1000AnnotPath(RUbioSeqFile genomes1000AnnotPath) {
		this.genomes1000AnnotPath = genomes1000AnnotPath;
	}

	public RUbioSeqFile getIndelAnnotPath() {
		return indelAnnotPath;
	}

	public void setIndelAnnotPath(RUbioSeqFile indelAnnotPath) {
		this.indelAnnotPath = indelAnnotPath;
	}

	public RUbioSeqFile getIntervalsPath() {
		return intervalsPath;
	}

	public void setIntervalsPath(RUbioSeqFile intervalsPath) {
		this.intervalsPath = intervalsPath;
	}

	public List<KnownIndels> getKnownIndels() {
		return knownIndels;
	}

	public void setKnownIndels(List<KnownIndels> knownIndels) {
		this.knownIndels = knownIndels;
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

	public CallingType getCallingType() {
		return callingType;
	}

	public void setCallingType(CallingType callingType) {
		this.callingType = callingType;
	}

	public GATKoutputMode getgATKoutputMode() {
		return gATKoutputMode;
	}

	public void setgATKoutputMode(GATKoutputMode gATKoutputMode) {
		this.gATKoutputMode = gATKoutputMode;
	}

	public Integer getRsFilter() {
		return rsFilter;
	}

	public void setRsFilter(Integer rsFilter) {
		this.rsFilter = rsFilter;
	}

	public Integer getrUbioSeqMode() {
		return rUbioSeqMode;
	}

	public void setrUbioSeqMode(Integer rUbioSeqMode) {
		this.rUbioSeqMode = rUbioSeqMode;
	}

	public Integer getFastqc() {
		return fastqc;
	}

	public void setFastqc(Integer fastqc) {
		this.fastqc = fastqc;
	}

	public Integer getvEPFlag() {
		return vEPFlag;
	}

	public void setvEPFlag(Integer vEPFlag) {
		this.vEPFlag = vEPFlag;
	}

	public Integer gettCFlag() {
		return tCFlag;
	}

	public void settCFlag(Integer tCFlag) {
		this.tCFlag = tCFlag;
	}

	public Integer getmDFlag() {
		return mDFlag;
	}

	public void setmDFlag(Integer mDFlag) {
		this.mDFlag = mDFlag;
	}

	public Double getStandCallConf() {
		return standCallConf;
	}

	public void setStandCallConf(Double standCallConf) {
		this.standCallConf = standCallConf;
	}

	public Double getStandEmitConf() {
		return standEmitConf;
	}

	public void setStandEmitConf(Double standEmitConf) {
		this.standEmitConf = standEmitConf;
	}

	public String getQueueSGEProject() {
		return queueSGEProject;
	}

	public void setQueueSGEProject(String queueSGEProject) {
		this.queueSGEProject = queueSGEProject;
	}

	public RUbioSeqFile getvQSRblockMills() {
		return VQSRblockMills;
	}

	public void setvQSRblockMills(RUbioSeqFile vQSRblockMills) {
		VQSRblockMills = vQSRblockMills;
	}

	public RUbioSeqFile getvQSRblockHapMAp() {
		return VQSRblockHapMAp;
	}

	public void setvQSRblockHapMAp(RUbioSeqFile vQSRblockHapMAp) {
		VQSRblockHapMAp = vQSRblockHapMAp;
	}

	public RUbioSeqFile getvQSRblockThousandG() {
		return VQSRblockThousandG;
	}

	public void setvQSRblockThousandG(RUbioSeqFile vQSRblockThousandG) {
		VQSRblockThousandG = vQSRblockThousandG;
	}
	
	public Integer getminQual() {
		return minQual;
	}
	
	public void setminQual(Integer minQual) {
		this.minQual = minQual;
	}
	
	public Integer getdPmin() {
		return DPmin;
	}
	
	public void setdPmin(Integer dPmin) {
		DPmin = dPmin;
	}

	public List<Sample> getSamples() {
		return samples;
	}

	public void setSamples(List<Sample> samples) {
		this.samples = samples;
	}
	
	public VQRSHardFiltersChoice getChoiceVqrsHardFilters() {
		return choiceVqrsHardFilters;
	}
	
	public void setChoiceVqrsHardFilters(
			VQRSHardFiltersChoice choiceVqrsHardFilters) {
		this.choiceVqrsHardFilters = choiceVqrsHardFilters;
	}

//	public String updatePath(String path){
//		String dataStore = path.substring(0, path.indexOf("/"));
//		System.out.println("updatePath: " + dataStore);
//		return path.replace(dataStore, Utils.getDatastorePath(dataStore));
//	}
	
	public void generateXMLConfigurationFile(File output) throws IOException {
		Document configurationFile = new Document();
		Element configData = new Element(CONFIG_DATA);
		configurationFile.setRootElement(configData);
		configData.setAttribute(ExperimentUtils.BRANCH, ExperimentUtils.BRANCH_SNV);
		
		Element genRef = new Element(GENREF);
		genRef.addContent(this.getGenRefPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(genRef);
		
		Element dbSnpAnnot = new Element(DBSNPANNOT);
		dbSnpAnnot.addContent(this.getDbSnpAnnotPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(dbSnpAnnot);
		
		Element genomes1000Annot = new Element(GENOMES1000ANNOT);
		genomes1000Annot.addContent(this.getGenomes1000AnnotPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(genomes1000Annot);
		
		Element indelAnnot = new Element(INDELANNOT);
		indelAnnot.addContent(this.getIndelAnnotPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(indelAnnot);
		
		if(this.getIntervalsPath()!=getIntervalsPath_DV()){
			Element intervals = new Element(INTERVALS);
			intervals.addContent(this.getIntervalsPath().getFile().getAbsolutePath());
			configurationFile.getRootElement().addContent(intervals);
		}

		if(this.getKnownIndels().size() > 0){
			for(KnownIndels kI : this.getKnownIndels()){
				Element knownIndels = new Element(KNOWNINDELS);
				knownIndels.addContent(kI.getFile().getFile().getAbsolutePath());
				configurationFile.getRootElement().addContent(knownIndels);
			}
		}
		
		Element plattform = new Element(PLATTFORM);
		plattform.addContent(this.getPlattform().getDisplayName());
		configurationFile.getRootElement().addContent(plattform);
		
		if(!this.getCheckCasava().equals(checkCasava_DV)){
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
		
		Element dataInDirpreProcess = new Element(INDIRPREPROCESS);
		dataInDirpreProcess.addContent(this.getDataInDirpreProcess().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(dataInDirpreProcess);
		
		for(Sample s : this.getSamples()){
			Element sample = new Element(SAMPLE);
			
			Element sampleName = new Element(SAMPLE_NAME);
			sampleName.addContent(s.getSampleName());
			sample.addContent(sampleName);

			Element sampleFiles = new Element(SAMPLE_FILES);
			sampleFiles.addContent(s.getSampleFiles());
			sample.addContent(sampleFiles);

			Element sampleSuffix = new Element(SAMPLE_SUFFIX);
			sampleSuffix.addContent(s.getSampleSuffix());
			sample.addContent(sampleSuffix);

			Element sampleType = new Element(SAMPLE_TYPE);
			sampleType.addContent(s.getSampleType().getDisplayName());
			sample.addContent(sampleType);
			
			configurationFile.getRootElement().addContent(sample);
		}

		if(!this.getCallingType().equals(callingType_DV)){
			Element callingType = new Element(CALLYING_TYPE);
			callingType.addContent(this.getCallingType().toString());
			configurationFile.getRootElement().addContent(callingType);
		}

		if(!this.getgATKoutputMode().equals(gATKoutputMode_DV)){
			Element gATKoutputModeoutputMode = new Element(GATKOUTPUTMODE);
			gATKoutputModeoutputMode.addContent(this.getgATKoutputMode().toString());
			configurationFile.getRootElement().addContent(gATKoutputModeoutputMode);
		}
		
		if(!this.getRsFilter().equals(rsFilter_DV)){
			Element rsFilter = new Element(RSFILTER);
			rsFilter.addContent(this.getRsFilter().toString());
			configurationFile.getRootElement().addContent(rsFilter);
		}

		if(!this.getrUbioSeqMode().equals(rUbioSeqMode_DV)){
			Element rUbioSeq_Mode = new Element(RUBIOSEQMODE);
			rUbioSeq_Mode.addContent(this.getrUbioSeqMode().toString());
			configurationFile.getRootElement().addContent(rUbioSeq_Mode);
		}

		if(!this.getFastqc().equals(fastqc_DV)){
			Element fastqc = new Element(FASTQC);
			fastqc.addContent(this.getFastqc().toString());
			configurationFile.getRootElement().addContent(fastqc);
		}
		
		if(!this.getvEPFlag().equals(vEPFlag_DV)){
			Element vEPFlag = new Element(VEPFLAG);
			vEPFlag.addContent(this.getvEPFlag().toString());
			configurationFile.getRootElement().addContent(vEPFlag);
		}
		
		if(!this.gettCFlag().equals(tCFlag_DV)){
			Element tCFlag = new Element(TCFLAG);
			tCFlag.addContent(this.gettCFlag().toString());
			configurationFile.getRootElement().addContent(tCFlag);
		}
		
		if(!this.getmDFlag().equals(mDFlag_DV)){
			Element mDFlag = new Element(MDFLAG);
			mDFlag.addContent(this.getmDFlag().toString());
			configurationFile.getRootElement().addContent(mDFlag);
		}
		
		if(!this.getStandCallConf().equals(standCallConf_DV)){
			Element standCallConf = new Element(STANDCALLCONF);
			standCallConf.addContent(this.getStandCallConf().toString());
			configurationFile.getRootElement().addContent(standCallConf);
		}
		
		if(!this.getStandEmitConf().equals(standEmitConf_DV)){
			Element standEmitConf = new Element(STANDEMITCONF);
			standEmitConf.addContent(this.getStandEmitConf().toString());
			configurationFile.getRootElement().addContent(standEmitConf);
		}
		
		if(!this.getQueueSGEProject().equals(queueSGEProject_DV)){
			Element queueSGEProject = new Element(QUEUESGEPROJECT);
			queueSGEProject.addContent(this.getQueueSGEProject().toString());
			configurationFile.getRootElement().addContent(queueSGEProject);
		}
		
		if(this.getChoiceVqrsHardFilters().equals(VQRSHardFiltersChoice.VQRS) &&
				this.getvQSRblockMills() != VQSRblockMills_DV &&
				this.getvQSRblockHapMAp() != VQSRblockHapMAp_DV &&
				this.getvQSRblockThousandG() != VQSRblockThousandG_DV){
		
			Element vQSR_block = new Element(VQSRBLOCK);
		
			Element mills = new Element(MILLS);
			mills.addContent(this.getvQSRblockMills().getFile().getAbsolutePath());
			vQSR_block.addContent(mills);

			Element hapmap = new Element(HAPMAP);
			hapmap.addContent(this.getvQSRblockHapMAp().getFile().getAbsolutePath());
			vQSR_block.addContent(hapmap);
		
			Element thousandg = new Element(THOUSANDG);
			thousandg.addContent(this.getvQSRblockThousandG().getFile().getAbsolutePath());
			vQSR_block.addContent(thousandg);
		
			configurationFile.getRootElement().addContent(vQSR_block);
		}
		
		if(this.getChoiceVqrsHardFilters().equals(VQRSHardFiltersChoice.HARDFILTERS) &&
				(!this.getminQual().equals(minQual_DV) ||
				!this.getdPmin().equals(DPmin_DV) || 
				this.getHardFilters().size() > 0)){
		
			Element hardFiltersBlock = new Element(HFILTERS);
			
			if(!this.getdPmin().equals(DPmin_DV)){
				Element dpMin = new Element(DPMIN);
				dpMin.addContent(this.getdPmin().toString());
				hardFiltersBlock.addContent(dpMin);
			}
			
			if(!this.getminQual().equals(minQual_DV)){
				Element minQual = new Element(MINQUAL);
				minQual.addContent(this.getminQual().toString());
				hardFiltersBlock.addContent(minQual);
			}	
			
			for(HardFilter hF : this.getHardFilters()){
				Element name = hF.getType().equals(HardFilter.HFilterType.SNP)?new Element(HFILTER_NAME_SNP):new Element(HFILTER_NAME_INDEL);
				name.addContent(hF.getName());
				Element rule = hF.getType().equals(HardFilter.HFilterType.SNP)?new Element(HFILTER_RULE_SNP):new Element(HFILTER_RULE_INDEL);
				rule.addContent(hF.getRule());
				hardFiltersBlock.addContent(name);
				hardFiltersBlock.addContent(rule);
			}
		
			configurationFile.getRootElement().addContent(hardFiltersBlock);
		}
		
		XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(configurationFile, new FileWriter(output));
	}
	
	public RUbioSeqFile getIntervalsPath_DV() {
		return intervalsPath_DV;
	}
	
	public Integer getCheckCasava_DV() {
		return checkCasava_DV;
	}
	
	public String getUserName_DV() {
		return userName_DV;
	}
	
	public CallingType getCallingType_DV() {
		return callingType_DV;
	}
	
	public GATKoutputMode getgATKoutputMode_DV() {
		return gATKoutputMode_DV;
	}
	
	public Integer getRsFilter_DV() {
		return rsFilter_DV;
	}
	
	public Integer getrUbioSeqMode_DV() {
		return rUbioSeqMode_DV;
	}
	
	public Integer getFastqc_DV() {
		return fastqc_DV;
	}
	
	public Integer getvEPFlag_DV() {
		return vEPFlag_DV;
	}
	
	public Integer gettCFlag_DV() {
		return tCFlag_DV;
	}
	
	public Integer getmDFlag_DV() {
		return mDFlag_DV;
	}
	
	public Double getStandCallConf_DV() {
		return standCallConf_DV;
	}
	
	public Double getStandEmitConf_DV() {
		return standEmitConf_DV;
	}
	
	public RUbioSeqFile getVQSRblockMills_DV() {
		return VQSRblockMills_DV;
	}
	
	public RUbioSeqFile getVQSRblockHapMAp_DV() {
		return VQSRblockHapMAp_DV;
	}
	
	public RUbioSeqFile getVQSRblockThousandG_DV() {
		return VQSRblockThousandG_DV;
	}
	
	public Integer getDPmin_DV() {
		return DPmin_DV;
	}
	
	public Integer getMinQual_DV() {
		return minQual_DV;
	}
	
	public String getQueueSGEProject_DV() {
		return queueSGEProject_DV;
	}
	
	public List<HardFilter> getHardFilters() {
		return hardFilters;
	}
	
	public void setHardFilters(List<HardFilter> hardFilters) {
		this.hardFilters = hardFilters;
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
	
	public String getWorkingDirectory() {
		return this.getDirOutBase().getFile().getAbsolutePath() + "/"
				+ this.getProjectId();
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
			
			Element dbSnpAnnot = configData.getChild(DBSNPANNOT);
			if(validElement(dbSnpAnnot)){
				this.setDbSnpAnnotPath(Utils.getRUbioSeqFile(dbSnpAnnot.getValue()));
			}

			Element genomes1000Annot = configData.getChild(GENOMES1000ANNOT);
			if(validElement(genomes1000Annot)){
				this.setGenomes1000AnnotPath(Utils.getRUbioSeqFile(genomes1000Annot.getValue()));
			}
			
			Element indelAnnot = configData.getChild(INDELANNOT);
			if(validElement(indelAnnot)){
				this.setIndelAnnotPath(Utils.getRUbioSeqFile(indelAnnot.getValue()));
			}
			
			Element intervals = configData.getChild(INTERVALS);
			if(validElement(intervals)){
				this.setIntervalsPath(Utils.getRUbioSeqFile(intervals.getValue()));
			}
			
			for(Element knownIndels : configData.getChildren(KNOWNINDELS)){
				if(validElement(knownIndels)){
					KnownIndels kI = new KnownIndels();
					kI.setFile(Utils.getRUbioSeqFile(knownIndels.getValue()));
					this.knownIndels.add(kI);
				}
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

			for(Element sampleElement : configData.getChildren(SAMPLE)){
				Sample newSample = new Sample();
				
				Element sampleName = sampleElement.getChild(SAMPLE_NAME);
				if(validElement(sampleName)){
					newSample.setSampleName(sampleName.getValue());
				}
				
				Element sampleFiles = sampleElement.getChild(SAMPLE_FILES);
				if(validElement(sampleFiles)){
					newSample.setSampleFiles(sampleFiles.getValue());
				}

				Element sampleSuffix = sampleElement.getChild(SAMPLE_SUFFIX);
				if(validElement(sampleSuffix)){
					newSample.setSampleSuffix(sampleSuffix.getValue());
				}

				Element sampleType = sampleElement.getChild(SAMPLE_TYPE);
				if(validElement(sampleType)){
					newSample.setSampleType(sampleType.getValue().equals("1")?SampleType.SingleEnd:SampleType.PairedEnd);
				}
				
				this.getSamples().add(newSample);
			}
			
			Element callingType = configData.getChild(CALLYING_TYPE);
			if(validElement(callingType)){
				String callingTypeValue = callingType.getValue().toUpperCase();
				if(callingTypeValue.equals("SNP")){
					this.setCallingType(CallingType.SNP);
				} else if(callingTypeValue.equals("INDEL")){
						this.setCallingType(CallingType.INDEL);
					} else if(callingTypeValue.equals("BOTH")){
							this.setCallingType(CallingType.BOTH);
						}
			}		

			Element gATKoutputModeoutputMode = configData.getChild(GATKOUTPUTMODE);
			if(validElement(gATKoutputModeoutputMode)){
				String gatkOutputModeValue = gATKoutputModeoutputMode.getValue().toUpperCase();
				if(gatkOutputModeValue.equals("EMIT_VARIANTS_ONLY")){
					this.setgATKoutputMode(GATKoutputMode.EMIT_VARIANTS_ONLY);
				} else if(gatkOutputModeValue.equals("EMIT_ALL_SITES")){
						this.setgATKoutputMode(GATKoutputMode.EMIT_ALL_SITES);
					} else if(gatkOutputModeValue.equals("EMIT_ALL_CONFIDENT_SITES")){
							this.setgATKoutputMode(GATKoutputMode.EMIT_ALL_CONFIDENT_SITES);
						}
			}	
			
			Element rsFilter = configData.getChild(RSFILTER);
			if(validElement(rsFilter)){
				this.setRsFilter(Integer.valueOf(rsFilter.getValue()));
			}
			
			Element rUbioSeq_Mode = configData.getChild(RUBIOSEQMODE);
			if(validElement(rUbioSeq_Mode)){
				this.setrUbioSeqMode(Integer.valueOf(rUbioSeq_Mode.getValue()));
			}

			Element fastqc = configData.getChild(FASTQC);
			if(validElement(fastqc)){
				this.setFastqc(Integer.valueOf(fastqc.getValue()));
			}

			Element vEPFlag = configData.getChild(VEPFLAG);
			if(validElement(vEPFlag)){
				this.setvEPFlag(Integer.valueOf(vEPFlag.getValue()));
			}
			
			Element tCFlag = configData.getChild(TCFLAG);
			if(validElement(tCFlag)){
				this.settCFlag(Integer.valueOf(tCFlag.getValue()));
			}
			
			Element mDFlag = configData.getChild(MDFLAG);
			if(validElement(mDFlag)){
				this.setmDFlag(Integer.valueOf(mDFlag.getValue()));
			}
			
			Element standCallConf = configData.getChild(STANDCALLCONF);
			if(validElement(standCallConf)){
				this.setStandCallConf(Double.valueOf(standCallConf.getValue()));
			}
			
			Element standEmitConf = configData.getChild(STANDEMITCONF);
			if(validElement(standEmitConf)){
				this.setStandEmitConf(Double.valueOf(standEmitConf.getValue()));
			}

			Element queueSGEProject = configData.getChild(QUEUESGEPROJECT);
			if(validElement(queueSGEProject)){
				this.setQueueSGEProject(queueSGEProject.getValue());
			}
			
			Element vqsrBlock = configData.getChild(VQSRBLOCK);
			if(vqsrBlock!=null){
				
				this.setChoiceVqrsHardFilters(VQRSHardFiltersChoice.VQRS);
				
				Element mills = vqsrBlock.getChild(MILLS);
				if(validElement(mills)){
					this.setvQSRblockMills(Utils.getRUbioSeqFile(mills.getValue()));
				}
				
				Element hapmap = vqsrBlock.getChild(HAPMAP);
				if(validElement(hapmap)){
					this.setvQSRblockHapMAp(Utils.getRUbioSeqFile(hapmap.getValue()));
				}
				
				Element thousandg = vqsrBlock.getChild(THOUSANDG);
				if(validElement(thousandg)){
					this.setvQSRblockThousandG(Utils.getRUbioSeqFile(thousandg.getValue()));
				}
				
			}
			
			Element hardFiltersBlock = configData.getChild(HFILTERS);
			if(hardFiltersBlock!=null){
				
				this.setChoiceVqrsHardFilters(VQRSHardFiltersChoice.HARDFILTERS);
				
				Element dpMin = hardFiltersBlock.getChild(DPMIN);
				if(validElement(dpMin)){
					this.setdPmin(Integer.valueOf(dpMin.getValue()));
				}

				Element minQual = hardFiltersBlock.getChild(MINQUAL);
				if(validElement(minQual)){
					this.setminQual(Integer.valueOf(minQual.getValue()));
				}
				
				Iterator<Element> hFilterNameSNP = hardFiltersBlock.getChildren(HFILTER_NAME_SNP).iterator();
				Iterator<Element> hFilterRuleSNP = hardFiltersBlock.getChildren(HFILTER_RULE_SNP).iterator();
				while (hFilterNameSNP.hasNext() && hFilterRuleSNP.hasNext()) {
					Element hFilterNameSNPElement = hFilterNameSNP.next();
					Element hFilterRuleSNPElement = hFilterRuleSNP.next();
					HardFilter newHardFilter = new HardFilter();
					newHardFilter.setName(hFilterNameSNPElement.getValue());
					newHardFilter.setRule(hFilterRuleSNPElement.getValue());
					newHardFilter.setType(HFilterType.SNP);
					this.getHardFilters().add(newHardFilter);
				}

				Iterator<Element> hFilterNameINDEL = hardFiltersBlock.getChildren(HFILTER_NAME_INDEL).iterator();
				Iterator<Element> hFilterRuleINDEL = hardFiltersBlock.getChildren(HFILTER_RULE_INDEL).iterator();
				while (hFilterNameINDEL.hasNext() && hFilterRuleINDEL.hasNext()) {
					Element hFilterNameINDELElement = hFilterNameINDEL.next();
					Element hFilterRuleINDELElement = hFilterRuleINDEL.next();
					HardFilter newHardFilter = new HardFilter();
					newHardFilter.setName(hFilterNameINDELElement.getValue());
					newHardFilter.setRule(hFilterRuleINDELElement.getValue());
					newHardFilter.setType(HFilterType.INDEL);
					this.getHardFilters().add(newHardFilter);
				}
				
			}
			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			throw new InvalidRubioSeqParameterException(InvalidRubioSeqParameterException.DEFAULT_MESSAGE);
		}
	}

	@Override
	public List<String> getPathsToWatch() {
		List<String> pathsToWatch = new LinkedList<String>();
		String workingDirectory = this.getWorkingDirectory();
		if(this.getrUbioSeqMode().equals(1) && this.getSamples().size() > 1){
			// Joint multisample mode --> All logs go to "multi"
			pathsToWatch.add(workingDirectory + "/" + "multi");
		} else{
			// Must watch one directory by <SampleName>
			for(Sample s : this.getSamples()){
				pathsToWatch.add(workingDirectory + "/" + s.getSampleName());
			}
		}
		if(this.gettCFlag().equals(1)){
			//TODO Add Tumor directory
		}
		return pathsToWatch;
	}

	@Override
	public double getIncrement(RUbioSeqEvent event, int numStages) {
		int k = event.getTotalSteps();
		System.err.println("\t[SingleNucleotideVariantExperiment] getNumSamples() = " + getNumSamples() + "\t numStages = " + numStages + "\t k = " + k);
		System.err.print("\t[SingleNucleotideVariantExperiment] Get increment of execution level " + event.getExecutionLevel());
		switch (event.getExecutionLevel()) {
		case 0:
			return 0;
		case 1:
			/*
			 * Stage 1. Short-read alignment with a combination of BWA and BFAST aligners.
			 * By sample.
			 * Possible cases: 
			 * 	- samples not splitted: Total should be incremented in (1 / (samples x stages x k) ) x 100
			 *  - samples splitted: Total should be incremented in (1 / ((sample_splits + 1 )x samples x stages x k) ) x 100
			 *  (sample_splits + 1: this 1 extra is for the concatenation step that will be received as a SPLIT_FILES_CONCAT event).
			 */
			int sampleSplits = getSampleSplits(event.getSampleName());
			if(sampleSplits == 1){
				System.err.println("\t" + (double) 100 / (double) (getNumSamples()*numStages*k) + " (sampleSplits = 1)");
				return (double) 100 / (double) (getNumSamples()*numStages*k);
			}else{
				System.err.println("\t" + (double) 100 / (double) (getNumSamples()*numStages*k*(sampleSplits+1)) + " (sampleSplits = " + sampleSplits + ")");
				return (double) 100 / (double) (getNumSamples()*numStages*k*(sampleSplits+1));
			}
		case 2:
			/*
			 * 2. Duplicates marking, GATK-based recalibration and realigning.
			 * By sample.
			 * Total should be incremented in (1 / (samples x stages x k) ) x 100
			 */
			System.err.println("\t" + 100 / (getNumSamples()*numStages*k));
			return (double) 100 / (double) (getNumSamples()*numStages*k);
		case 3:
			/*
			 * 3. GATK-based variant calling and filtration protocol for indels and SNPs.
			 * By sample if RUbioSeqMode = 0 and by group of samples if RUbioSeqMode = 1
			 * 	By sample Total should be incremented in (1 / (samples x stages x k) ) x 100
			 *  By group of samples Total should be incremented in (1 / (stages x k) ) x 100
			 */
			if(getrUbioSeqMode().equals(0)){
				System.err.println("\t" + 100 / (getNumSamples()*numStages*k) + " (rubioSeqMode = 0)");
			}else{
				System.err.println("\t" +100 / (1*numStages*k) + " (rubioSeqMode = 1)");
			}
			return getrUbioSeqMode().equals(0)?
					(double) 100 / (double) (getNumSamples()*numStages*k):
					(double) 100 / (double) (1*numStages*k);
		case 4:
			/*
			 * 4. User customized filters and variant effect predictor to annotate the variants.
			 * By sample if RUbioSeqMode = 0, by group of samples if RUbioSeqMode = 1 and by each pair of samples if TCFlag = 1
			 * 	- By sample Total should be incremented in (1 / (samples x stages x k) ) x 100
			 *  - By group of samples Total should be incremented in (1 / (stages x k) ) x 100 
			 *  - By pairs of samples Total should be incremented in (1 / (samples/2 x 3 x stages x k) ) x 100 (the factor 3 is 
			 *  needed to split this S4 stage in three substages because if TCFlag = 1 Post-Analysis is done for the three 
			 *  intersection VCF files (only control, only tumor and both) generated in the TC Analysis extra stage.
			 */
			if(this.isTCAnalysisStageEnabled()){
				System.err.println("\t" + (double) 100 / (double) (((double) getNumSamples()/(double) 2)*3*numStages*k) + " (tcFlag = 1)");
				return (double) 100 / (double) (((double) getNumSamples()/(double) 2)*3*numStages*k);	
			}
			if(getrUbioSeqMode().equals(0)){
				System.err.println("\t" + (double) 100 / (double) (getNumSamples()*numStages*k) + " (rubioSeqMode = 0)");
				return (double) 100 / (double) (getNumSamples()*numStages*k);
			} else{
				System.err.println("\t" + (double) 100 / (double) (numStages*k) + " (rubioSeqMode = 1)");
				return (double) 100 / (double) (numStages*k);
			}
		default:
			/*
			 * If execution level is not 1,2,3 or 4, we must se if it is an extra stage.
			 */
			if(event.getExtraStage().equals(RUbioSeqEvent.ExtraStage.SPLIT_FILES_CONCAT) &&
					event.getMessage().equals(RUbioSeqEvent.SPLIT_FILES_CONCAT_MESSAGE)){
				System.err.println("\t" + (double) 100 / (double) (getNumSamples()*numStages*k*(getSampleSplits(event.getSampleName())+1)) + " (CONCAT step)"+ " (sampleSplits = " + getSampleSplits(event.getSampleName()) + ")");
				return (double) 100 / (double) (getNumSamples()*numStages*k*(getSampleSplits(event.getSampleName())+1));
			} else if(event.getExtraStage().equals(RUbioSeqEvent.ExtraStage.SNV_TC_ANALYSIS)){
				System.err.println("\t" + (double) 100 / (double) (((double) getNumSamples()/(double) 2)*numStages*k)  + " (TC Analysis STEP)");
				return (double) 100 / (double) (((double) getNumSamples()/(double) 2)*numStages*k);
			}
			return 0;
		}
	}
	
	public int getNumSamples(){
		return this.getSamples().size();
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
	
	public boolean isTCAnalysisStageEnabled(){
		return this.gettCFlag().equals(1) && this.getNumSamples() > 1;
	}

	public boolean checkPaths() {
		if (this.getGenRefPath() instanceof InvalidRUbioSeqFile
				|| this.getDbSnpAnnotPath() instanceof InvalidRUbioSeqFile
				|| this.getGenomes1000AnnotPath() instanceof InvalidRUbioSeqFile
				|| this.getIndelAnnotPath() instanceof InvalidRUbioSeqFile
				|| (this.getIntervalsPath()!=null && this.getIntervalsPath() instanceof InvalidRUbioSeqFile)
				|| this.getDirOutBase() instanceof InvalidRUbioSeqFile
				|| this.getDataInDirpreProcess() instanceof InvalidRUbioSeqFile)
			return false;
		else
			return true;
	}
	
	@Override
	public boolean checkConfiguration(){
		return this.checkPaths();
	}

	@Override
	public int getStagesCount(int executionLevel) {
		int stagesCount;
		if (executionLevel > 0) {
			stagesCount = 1;
		} else {
			stagesCount = 4;
		}

		if (shouldAddTCAnalysisStage(executionLevel)) {
			stagesCount++;
		}
		
		return stagesCount;
	}

	private boolean shouldAddTCAnalysisStage(int executionLevel) {
		return  isTCAnalysisStage(executionLevel) && 
				isTCAnalysisStageEnabled();
	}

	private boolean isTCAnalysisStage(int executionLevel) {
		return executionLevel == 0 || executionLevel == 4;
	}
}
