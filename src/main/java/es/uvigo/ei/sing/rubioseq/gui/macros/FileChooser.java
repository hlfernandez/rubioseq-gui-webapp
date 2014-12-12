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
package es.uvigo.ei.sing.rubioseq.gui.macros;

import org.zkoss.bind.BindUtils;
import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.annotation.ComponentAnnotation;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Label;
import org.zkoss.zul.Layout;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * 
 * @author hlfernandez, dgpena
 *
 */
@ComponentAnnotation("path:@ZKBIND(ACCESS=both,SAVE_EVENT=onEdited)")
public class FileChooser extends HtmlMacroComponent {
	private static final long serialVersionUID = 1129457125979891008L;

	@Wire("#fileselector #pathbox") 
	private Textbox pathbox;

	@Wire
	private Label pathlabel;
	
	@Wire
	private Layout mainlayout;
	
	@Wire
	private Window fileselector;
	
	private String type;

	private RUbioSeqFile selectedFile;
	
	public RUbioSeqFile getPath(){
		return selectedFile;
//		return pathlabel.getValue();
	}
	
	public void setPath(RUbioSeqFile path){
		this.selectedFile = path;
		if(path!=null){
			this.pathlabel.setValue(path.getAbsolutePathWithDatastoreName());
			this.pathbox.setText(path.getAbsolutePathWithDatastoreName());
			this.mainlayout.setSclass(path.getSclass());
		} else{
			this.pathlabel.setValue("");
			this.pathbox.setText("");
		}
	}
	
	public String getType(){
		return this.type;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	@Listen("onClick=#browsebutton")
	public void onBrowse(){
		BindUtils.postGlobalCommand(null, null, "updateFileChooserVM", null);
		this.fileselector.doModal();
	}
	
	@Listen("onClick=#fileselector #acceptbutton")
	public void onAccept(){
		this.fileselector.setVisible(false);
		doEdited();
	}
	
	@Listen("onClick=#fileselector #cancelbutton")
	public void onCancel(){
		this.fileselector.setVisible(false);
	}
	
	public void doEdited() {
		this.pathlabel.setValue(this.pathbox.getText());
		Events.postEvent("onEdited", this, null);
	}

	 public Textbox getPathbox() {
		return pathbox;
	}
	 
	public void setSelectedFile(RUbioSeqFile rUbioSeqFile) {
		this.selectedFile = rUbioSeqFile;
		this.getPathbox().setText(this.selectedFile.getAbsolutePathWithDatastoreName());
		
	}
}
