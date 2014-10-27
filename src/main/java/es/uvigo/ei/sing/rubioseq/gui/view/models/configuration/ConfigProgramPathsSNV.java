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
package es.uvigo.ei.sing.rubioseq.gui.view.models.configuration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.jdom2.Comment;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import es.uvigo.ei.sing.rubioseq.gui.macros.RUbioSeqFile;
import es.uvigo.ei.sing.rubioseq.gui.util.Utils;

/**
 * 
 * @author hlfernandez
 *
 */
public class ConfigProgramPathsSNV {

	private static final String COMMENT_VARIANT_CALLER = "RUbioSeq variantCaller CONFIG FILE";
	public static final String CONFIG_DATA = "configData";
	public static final String BWA_PATH = "bwaPath";
	public static final String SAMTOOLS_PATH = "samtoolsPath";
	public static final String GATK_PATH = "gatkpath";
	public static final String PICARD_PATH = "picardPath";
	public static final String BFAST_PATH = "BFASTPath";
	public static final String FASTQC_PATH = "fastqcPath";
	public static final String NTHR = "nthr";
	public static final String JAVA_RAM = "javaRam";
	public static final String QUEUE_SYSTEM = "queueSystem";
	public static final String QUEUE_NAME = "queueName";
	public static final String MULTICORE_NAME = "multicoreName";
	public static final String MULTICORE_NUMBER = "multicoreNumber";

	private RUbioSeqFile bwaPath;
	private RUbioSeqFile samtoolsPath;
	private RUbioSeqFile gatkpath;
	private RUbioSeqFile picardPath;
	private RUbioSeqFile bfastPath;
	private RUbioSeqFile fastqcPath;
	private Integer nthr = 1;
	private Integer nthr_DV = 1;
	private String javaRam = "-Xmx16G";
	private String javaRam_DV = "-Xmx16G";
	private QueueSystem queueSystem;
	private String queueName = "";
	private String queueName_DV = "";
	private String multicoreName = "";
	private String multicoreName_DV = "";
	private Integer multicoreNumber = -1;
	private Integer multicoreNumber_DV = -1;
	
	public enum QueueSystem {
		SGE("SGE"), 
		PBS("PBS"),
		NONE("none");
		
		private final String displayName;
		
		private QueueSystem(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
	}
	
	public List<QueueSystem> getQueueSystemValues(){
		return Arrays.asList(QueueSystem.values());
	}
	
	public RUbioSeqFile getBwaPath() {
		return bwaPath;
	}

	public void setBwaPath(RUbioSeqFile bwaPath) {
		this.bwaPath = bwaPath;
	}

	public RUbioSeqFile getSamtoolsPath() {
		return samtoolsPath;
	}

	public void setSamtoolsPath(RUbioSeqFile samtoolsPath) {
		this.samtoolsPath = samtoolsPath;
	}

	public RUbioSeqFile getBfastPath() {
		return bfastPath;
	}

	public void setBfastPath(RUbioSeqFile bfastPath) {
		this.bfastPath = bfastPath;
	}

	public RUbioSeqFile getGatkpath() {
		return gatkpath;
	}

	public void setGatkpath(RUbioSeqFile gatkpath) {
		this.gatkpath = gatkpath;
	}

	public RUbioSeqFile getPicardPath() {
		return picardPath;
	}

	public void setPicardPath(RUbioSeqFile picardPath) {
		this.picardPath = picardPath;
	}

	public RUbioSeqFile getFastqcPath() {
		return fastqcPath;
	}

	public void setFastqcPath(RUbioSeqFile fastqcPath) {
		this.fastqcPath = fastqcPath;
	}

	public Integer getNthr() {
		return nthr;
	}

	public void setNthr(Integer nthr) {
		this.nthr = nthr;
	}

	public String getJavaRam() {
		return javaRam;
	}

	public void setJavaRam(String javaRam) {
		this.javaRam = javaRam;
	}

	public QueueSystem getQueueSystem() {
		return queueSystem;
	}

	public void setQueueSystem(QueueSystem queueSystem) {
		this.queueSystem = queueSystem;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getMulticoreName() {
		return multicoreName;
	}

	public void setMulticoreName(String multicoreName) {
		this.multicoreName = multicoreName;
	}

	public Integer getMulticoreNumber() {
		return multicoreNumber;
	}

	public void setMulticoreNumber(Integer multicoreNumber) {
		this.multicoreNumber = multicoreNumber;
	}

	public Integer getNthr_DV() {
		return nthr_DV;
	}

	public String getJavaRam_DV() {
		return javaRam_DV;
	}

	public String getQueueName_DV() {
		return queueName_DV;
	}

	public String getMulticoreName_DV() {
		return multicoreName_DV;
	}


	public Integer getMulticoreNumber_DV() {
		return multicoreNumber_DV;
	}

	public boolean validElement(Element e) {
		return e != null && e.getValue() != null && !e.getValue().equals("");
	}
	
	public void loadDataFromFile(File editFile) {
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			Document document = saxBuilder.build(editFile);
			Element configData = document.getRootElement(); 
			
			Element bwaPath = configData.getChild(BWA_PATH);
			if(validElement(bwaPath)){
				this.setBwaPath(Utils.getRUbioSeqFile(bwaPath.getValue()));
			}
			
			Element samtoolsPath = configData.getChild(SAMTOOLS_PATH);
			if(validElement(samtoolsPath)){
				this.setSamtoolsPath(Utils.getRUbioSeqFile(samtoolsPath.getValue()));
			}
			
			Element gatkpath = configData.getChild(GATK_PATH);
			if(validElement(gatkpath)){
				this.setGatkpath(Utils.getRUbioSeqFile(gatkpath.getValue()));
			}

			Element picardPath = configData.getChild(PICARD_PATH);
			if(validElement(picardPath)){
				this.setPicardPath(Utils.getRUbioSeqFile(picardPath.getValue()));
			}
			
			Element BFASTPath = configData.getChild(BFAST_PATH);
			if(validElement(BFASTPath)){
				this.setBfastPath(Utils.getRUbioSeqFile(BFASTPath.getValue()));
			}
			
			Element fastqcPath = configData.getChild(FASTQC_PATH);
			if(validElement(fastqcPath)){
				this.setFastqcPath(Utils.getRUbioSeqFile(fastqcPath.getValue()));
			}
			
			Element nthr = configData.getChild(NTHR);
			if(validElement(nthr)){
				try{
					this.setNthr(Integer.valueOf(nthr.getValue()));
				} catch(Exception e){}
			}
			
			Element javaRam = configData.getChild(JAVA_RAM);
			if(validElement(javaRam)){
				this.setJavaRam(javaRam.getValue());
			}
			
			Element queueSystem = configData.getChild(QUEUE_SYSTEM);
			if(validElement(queueSystem)){
				String queueSystemValue = queueSystem.getValue().toUpperCase();
				if(queueSystemValue.equals("SGE")){
					this.setQueueSystem(QueueSystem.SGE);
				} else if(queueSystemValue.equals("PBS")){
						this.setQueueSystem(QueueSystem.PBS);
					} else if(queueSystemValue.equals("NONE")){
							this.setQueueSystem(QueueSystem.NONE);
						}
			}	
			
			Element queueName = configData.getChild(QUEUE_NAME);
			if(validElement(queueName)){
				this.setQueueName(queueName.getValue());
			}
			
			Element multicoreName = configData.getChild(MULTICORE_NAME);
			if(validElement(multicoreName)){
				this.setMulticoreName(multicoreName.getValue());
			}
			
			Element multicoreNumber = configData.getChild(MULTICORE_NUMBER);
			if(validElement(multicoreNumber)){
				try{
					this.setMulticoreNumber(Integer.valueOf(multicoreNumber.getValue()));
				} catch(Exception e){}
			}
			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void exportToXML(File output) throws IOException {
		Document configurationFile = new Document();
		Comment comment = new Comment(COMMENT_VARIANT_CALLER);
		configurationFile.addContent(comment);

		Element configData = new Element(CONFIG_DATA);
		configurationFile.setRootElement(configData);
		

		Element bwaPath = new Element(BWA_PATH);
		bwaPath.addContent(this.getBwaPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(bwaPath);
		
		Element samtoolsPath = new Element(SAMTOOLS_PATH);
		samtoolsPath.addContent(this.getSamtoolsPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(samtoolsPath);
		
		Element gatkpath = new Element(GATK_PATH);
		gatkpath.addContent(this.getGatkpath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(gatkpath);
		
		Element picardPath = new Element(PICARD_PATH);
		picardPath.addContent(this.getPicardPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(picardPath);
		
		Element BFASTPath = new Element(BFAST_PATH);
		BFASTPath.addContent(this.getBfastPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(BFASTPath);
		
		Element fastqcPath = new Element(FASTQC_PATH);
		fastqcPath.addContent(this.getFastqcPath().getFile().getAbsolutePath());
		configurationFile.getRootElement().addContent(fastqcPath);
		
		if(!this.getNthr().equals(nthr_DV)){
			Element nthr = new Element(NTHR);
			nthr.addContent(String.valueOf(this.getNthr()));
			configurationFile.getRootElement().addContent(nthr);
		}
		
		if(!this.getJavaRam().equals(javaRam_DV)){
			Element javaRam = new Element(JAVA_RAM);
			javaRam.addContent(this.getJavaRam());
			configurationFile.getRootElement().addContent(javaRam);
		}
		
		Element queueSystem = new Element(QUEUE_SYSTEM);
		queueSystem.addContent(this.getQueueSystem().getDisplayName());
		configurationFile.getRootElement().addContent(queueSystem);

		if(!this.getQueueName().equals(queueName_DV)){
			Element queueName = new Element(QUEUE_NAME);
			queueName.addContent(this.getQueueName());
			configurationFile.getRootElement().addContent(queueName);
		}
		
		if(!this.getMulticoreName().equals(multicoreName_DV)){
			Element multicoreName = new Element(MULTICORE_NAME);
			multicoreName.addContent(this.getMulticoreName());
			configurationFile.getRootElement().addContent(multicoreName);
		}
		
		if(!this.getMulticoreNumber().equals(multicoreNumber_DV)){
			Element multicoreNumber = new Element(MULTICORE_NUMBER);
			multicoreNumber.addContent(String.valueOf(this.getMulticoreNumber()));
			configurationFile.getRootElement().addContent(multicoreNumber);
		}

		XMLOutputter xmlOutput = new XMLOutputter();
		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(configurationFile, new FileWriter(output));
	}
	
}
