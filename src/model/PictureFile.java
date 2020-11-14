package model;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PictureFile{
	
	private String name;
	private String date;
	private File imageFile;
	private String caption;
	private ArrayList<Tags> tags = new ArrayList<>();
	private long lastModifiedDate;
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	
	public PictureFile(File imageFile) { 
		
		this.imageFile = imageFile;
		this.name = imageFile.getName();
		this.date = sdf.format(imageFile.lastModified());
		this.lastModifiedDate = imageFile.lastModified();
		
		if(name.equals("")) {
			name = imageFile.getPath();
		}
	}
	
    public PictureFile(String imagePath) {
    	this(new File(imagePath));
    }

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getCaption() {
		return caption;
	}

	public boolean isPicture() {
    	if(this.name.toLowerCase().endsWith(".jpg")||
    	   this.name.toLowerCase().endsWith(".jpge")||
    	   this.name.toLowerCase().endsWith(".png")||
    	   this.name.toLowerCase().endsWith(".bmp")||
    	   this.name.toLowerCase().endsWith(".gif")	) {
    		return true;
    	}
    	return false;
    }
    
    public boolean isDirectory() {
    	return imageFile.isDirectory();
    }
    
    public boolean isFile() {
    	return imageFile.isFile();
    }
    
    public long length() {
    	return imageFile.length();
    }
    
    public String toString() {
		return name;
	}

	public long getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(long lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getDate() {
    	return this.date;
    }
    
    public File getImageFile() {
		return imageFile;
	}
    
    public String getName() {
		return name;
	}

	public ArrayList<Tags> getTags() {
		return tags;
	}
	
	public void addTags(String name,String value){
		tags.add(new Tags(name,value));
	}
	
	public void removeTags(String name,String value){
		tags.removeIf(tag->tag.getName().equals(name)&&tag.getValue().equals(value));
	}
	

	public void setName(String name) {
		this.name = name;
	}
	
	public void setImageFile(File file) {
		this.imageFile = file;
	}
	
	public void setTags(ArrayList<Tags> tags) {
		this.tags = tags;
	}
	
	public PictureFile copyPicture() {
		
		PictureFile temp = new PictureFile(this.getImageFile());
		
		temp.setCaption(this.getCaption());
		temp.setLastModifiedDate(this.getLastModifiedDate());
		temp.setName(this.getName());
		temp.setTags(this.getTags());
		
		return temp;
	}

}

