package model;


import java.io.File;
import java.util.ArrayList;

public class User {
	
    
    String username;
    ArrayList<Album> gallery = new ArrayList<Album>();
    boolean isAdmain;
    
    
    public User(String username) {
        this.username = username;
    }
    
    public ArrayList<Album> getGallery(){
    	return this.gallery;
    }
    
    public boolean isAdmain() {
        return isAdmain;
    }

    public String getName() {
        return username;
    }
    
    public void addAlbum(Album album){
        gallery.add(album); 
    }
    
    public Boolean deleteAlbum(int index) {
    	String targetAlbumName = gallery.get(index).getName();
    	gallery.remove(index);
    	//修改文件夹
    	return deleteDir(new File("src/users/"+username+"/"+targetAlbumName));
    	
    }
    
    public void setAdmain(boolean isAdmain) {
        this.isAdmain = isAdmain;
    }
    
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir
                (new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        if(dir.delete()) {
            //System.out.println("目录已被删除！");
            return true;
        } else {
            //System.out.println("目录删除失败！");
            return false;
        }
    }
    
}
