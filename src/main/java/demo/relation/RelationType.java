package demo.relation;

import java.util.ArrayList;
import java.util.List;

public class RelationType {
	boolean call = false;
	boolean include = false;
	boolean define = false;
	boolean return_type = false;
	boolean extend = false;
	boolean override = false;
	boolean parameter = false;
	
	public RelationType() {
		
	}
	public List<String> getType() {
		List<String> typelist = new ArrayList<String>();

		if(getcall()) typelist.add("Call");
		if(getinclude()) typelist.add("Include");
		if(getdefine()) typelist.add("Define");
		if(getreturn()) typelist.add("Return");
		if(getextend()) typelist.add("Extend");
		if(getoverride()) typelist.add("Override");
		if(getparameter()) typelist.add("Parameter");
		
		return typelist;
		
	}
	public boolean getcall() {return call;}
	public boolean getinclude() {return include;}
	public boolean getdefine() {return define;}
	public boolean getreturn() {return return_type;}
	public boolean getextend() {return extend;}
	public boolean getoverride() {return override;}
	public boolean getparameter() {return parameter;}
	
	
	public void setcallTrue() {
		call = true;
	}
	public void setincludeTrue() {
		include = true;
	}
	public void setdefineTrue() {
		define = true;
	}
	public void setreturnTrue() {
		return_type = true;
	}
	public void setextendTrue() {
		extend = true;
	}
	public void setoverrideTrue() {
		override = true;
	}
	public void setparameterTrue() {
		parameter = true;
	}
	
}
