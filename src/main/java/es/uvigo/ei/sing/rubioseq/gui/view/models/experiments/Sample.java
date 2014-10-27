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

import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author hlfernandez
 *
 */
public class Sample implements Comparable<Sample>{
	
	private String sampleName;
	private String sampleFiles;
	private String sampleSuffix;
	private SampleType sampleType;
	
	public enum SampleType {
		SingleEnd("1"),
		PairedEnd("2"); 
		
	    private final String displayName;

	    private SampleType(String displayName) {
	      this.displayName = displayName;
	    }

	    public String getDisplayName() {
	      return displayName;
	    }
	}
	
	public List<SampleType> getSampleTypes(){
		return Arrays.asList(SampleType.values());
	}
	
	public String getSampleName() {
		return sampleName;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public String getSampleFiles() {
		return sampleFiles;
	}

	public void setSampleFiles(String sampleFiles) {
		this.sampleFiles = sampleFiles;
	}

	public String getSampleSuffix() {
		return sampleSuffix;
	}

	public void setSampleSuffix(String sampleSuffix) {
		this.sampleSuffix = sampleSuffix;
	}

	public SampleType getSampleType() {
		return sampleType;
	}

	public void setSampleType(SampleType sampleType) {
		this.sampleType = sampleType;
	}
	
	public boolean checkSample(){
		return (this.getSampleName()!=null && !this.getSampleName().equals("") &&
				this.getSampleFiles()!=null && !this.getSampleFiles().equals("") &&
				this.getSampleSuffix()!=null && !this.getSampleSuffix().equals("") &&
				this.getSampleType()!=null);
	}

	@Override
	public int compareTo(Sample o) {
		return this.getSampleName().compareTo(o.getSampleName());
	}
}
