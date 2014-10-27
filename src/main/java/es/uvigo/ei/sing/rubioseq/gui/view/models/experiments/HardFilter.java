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
public class HardFilter {
	private String name;
	private String rule;
	private HFilterType type;
	
	public enum HFilterType {
		 SNP("SNP"), 
		 INDEL("INDEL");
		
		private final String displayName;
		
		private HFilterType(String displayName) {
			this.displayName = displayName;
		}
		
		public String getDisplayName() {
			return displayName;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getRule() {
		return rule;
	}
	
	public void setRule(String rule) {
		this.rule = rule;
	}
	
	public HFilterType getType() {
		return type;
	}
	
	public void setType(HFilterType type) {
		this.type = type;
	}
	
	public List<HFilterType> gethFilterTypes(){
		return Arrays.asList(HFilterType.values());
	}
}