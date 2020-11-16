package model;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Album{
	
	
	User currentUser;
    String name;
    String rangeOfDate; //earliest and latest date
    String path;
    String earlistDateFormat="-";
    String latestDateFormat="-";
    long earliestDate = Long.MAX_VALUE;
    long latestDate = Long.MIN_VALUE;
    ArrayList<PictureFile> photoCollection = new ArrayList<PictureFile>();
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
   
  
    public Album(String name){
 
        this.name = name;
        
    }

    public ArrayList<PictureFile> getPhotoCollection() {
        return photoCollection;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setUser(User c) {
    	this.currentUser = c;
    }
    
    public boolean setFolder() {
    	this.path = "src/users/" + currentUser.getName()+"/"+this.name;
    	File temp = new File(this.path);
    	Boolean success = temp.mkdir();
    	//String parent = temp.getPath();
    	//System.out.println("Path: "+parent + " \nSuccess: "+success);
    	return success;
    }
    
    public void addPhoto(PictureFile p) {
    	photoCollection.add(p);
    	
    	updateLastModifiedDate();
    	
    	rangeOfDate = earlistDateFormat+"--"+ latestDateFormat;
    }
    
    public void deletePhoto(PictureFile p){
        photoCollection.remove(p);
        updateLastModifiedDate();
        rangeOfDate = earlistDateFormat+"--"+ latestDateFormat;
   }
   
   	private void updateLastModifiedDate() {
	   earliestDate = Long.MAX_VALUE;
	   latestDate = Long.MIN_VALUE;
	   
	   if(photoCollection.size()==0) {
		   earlistDateFormat = "";
		   latestDateFormat = "";
		   return;
	   }
	   
	   
	   for (int i = 0; i<photoCollection.size(); i++) {
		   
		   
		   if( photoCollection.get(i).getLastModifiedDate() < earliestDate) {
	    		this.earliestDate  = photoCollection.get(i).getLastModifiedDate();
	    		this.earlistDateFormat = sdf.format(earliestDate);
	    	}
	    	
	    	if(photoCollection.get(i).getLastModifiedDate()>latestDate) {
	    		this.latestDate = photoCollection.get(i).getLastModifiedDate();
	    		this.latestDateFormat = sdf.format(latestDate);
	    	}
	   }
	   
   }
   
    public int getSize() {
    	return photoCollection.size();
    }
    
    public String getRangeOfDate() {
    	
    	if(this.rangeOfDate == null ) {
    		return ("--");
    	}else {
    		return rangeOfDate;
    	}
    	
    }
    
    public User getUser() {
    	return this.currentUser;
    }
    
    public String getName() {
    	return this.name;
    }
    
    
    
 
    

}

