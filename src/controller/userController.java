package controller;



import java.io.*;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Album;
import model.PictureFile;
import model.Tags;
import model.User;

public class userController implements Initializable{

	User currentUser;
	String currentUserName;
	Stage mainStage;
	int slideCounter;
	Album currentAlbum;
	PictureFile currentPic;
	PictureFile copiedPic;
	ImageView selectedPicture;

	@FXML Label welcome; 
	@FXML FlowPane preview;
	@FXML FlowPane searchDisplay;
	@FXML Button create;
	@FXML Button delete;
	@FXML Button rename;
	@FXML Button open;
	@FXML Button copy;
	@FXML Button move;
	@FXML Button paste;
	@FXML Button logout;
	@FXML Button quit;
	@FXML Button addPhoto;
	@FXML Button captionAddButton;
	@FXML Button addTag;
	@FXML Button deleteTag;
	@FXML Button last;
	@FXML Button next;
	@FXML Button search;
	@FXML Button searchTag;
	@FXML Button searchCreate;
	@FXML Text photoName;
	@FXML Text dateTaken;
	@FXML Text caption;
	@FXML TextField tagName;
	@FXML TextField tagValue;
	@FXML TextField type1;
	@FXML TextField type2;
	@FXML TextField value1;
	@FXML TextField value2;
	@FXML ImageView display;
	@FXML ListView<String> albumList;
	@FXML TableView<Tags> tagTable;
	@FXML CheckBox single;
	@FXML CheckBox multiple;
	@FXML CheckBox and;
	@FXML CheckBox or;
	@FXML DatePicker earliest;
	@FXML DatePicker latest;
	
	
	

	private ObservableList<String> displayList;
	private ArrayList<String> allAlbumName = new ArrayList<String>();
	Album searchList = new Album("searchList");
	
	
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

		try {
			initial();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			//System.out.println("currentUser.txt not found");
		}
		
        try {
            preload();
        } catch (IOException e) {
            //System.out.println("Album not found");
        }

        displayAlbum();
    }
		
	public void preload() throws IOException {
		
		//逻辑上，preload()只需要加载所有的album，包括album里的每张照片的修改日期，caption，tags

		this.currentUser = new User(this.currentUserName);
		currentUser.setAdmain(false);
		
		//gallery一定存在
		File galleryTxt = new File("src/users/"+this.currentUserName+"/gallery.txt");

		Scanner s = new Scanner(galleryTxt);
		//list内含所有album的名字
		ArrayList<String> list = new ArrayList<String>();
		while (s.hasNextLine()){
    		list.add(s.nextLine());
		}
		s.close();
		//用list初始化gallery
		if(list.size()==0) {
			//this user does not have any albums
			//System.out.println("SUCCESS: PRELOAD GALLERY SIZE: "+currentUser.getGallery().size());
		}else if(list.size()>0){
			//preload gallery
			for(int i=0; i<list.size(); i++) {
				String currentLoadingAlbumName = list.get(i);
				Album currentAlbum = new Album(currentLoadingAlbumName);
				//System.out.println(currentLoadingAlbumName);
				//扫描有哪些图，
				File file = new File("src/users/"+currentUser.getName()+"/"+currentLoadingAlbumName);
				File[] fs = file.listFiles();
				
				for(File f:fs) {
					if(!f.isDirectory()) {//扫面文件夹
						if(isPicture(f)) { // 如果是照片 添加到preview中
							String captionLoad = " ";
							BufferedReader reader = new BufferedReader(new FileReader("src/users/"+currentUser.getName()+"/"+currentLoadingAlbumName+"/"+f.getName()+".txt"));
							String currentLine;
							long lastModify = -1;
							PictureFile picture = new PictureFile(f);
							while((currentLine = reader.readLine())!=null){
								//System.out.println(currentLine);
								if(currentLine.substring(0,currentLine.indexOf(" ")).equals("CAPTION:")){
									captionLoad = currentLine.substring(8);
								}
								if(currentLine.substring(0,currentLine.indexOf(" ")).equals("LAST:")){
									lastModify = Long.parseLong(currentLine.substring(6));
								}
								if(currentLine.substring(0,currentLine.indexOf(" ")).equals("TAGS:")){
									//System.out.println("I'm here");
									while((currentLine = reader.readLine())!=null){
										String name,value;

										name = currentLine.substring(0,currentLine.indexOf(" "));
										value = currentLine.substring(currentLine.indexOf(" ")+1);

										picture.addTags(name,value);
									}
									break;
								}

							}
							reader.close();

							picture.setCaption(captionLoad);
							picture.setLastModifiedDate(lastModify);
							currentAlbum.addPhoto(picture);
						}	
					}
				}
				
				currentUser.addAlbum(currentAlbum);
			}
			
			
			//System.out.println("SUCCESS: PRELOAD GALLERY SIZE: "+currentUser.getGallery().size());
			
		}else {
			//System.out.println("FAIL: PRELOAD: UNKNOWN");
		}
		
		
		
	}
	
	public void initial() throws FileNotFoundException {
		File file = new File("src/users/currentUser.txt");
		Scanner s = new Scanner(file);
		this.currentUserName = s.nextLine();
		s.close();
		this.welcome.setText("Welcome, "+currentUserName);
		//System.out.println("current user name: "+ currentUserName);
		
	}
	
	public void displayAlbum() {
		//show all Album and name, the number of photos in it, and the range of dates
		ArrayList<Album> currentGallery = currentUser.getGallery();
		displayList = FXCollections.observableArrayList();   
		
		for(int i = 0; i<currentGallery.size(); i++) {
			this.allAlbumName.add(currentGallery.get(i).getName());
			displayList.add(currentGallery.get(i).getName()+", "
					+currentGallery.get(i).getSize()+ " photos, "+currentGallery.get(i).getRangeOfDate());
		}
				
		albumList.setItems(displayList);
		
		//System.out.println("SUCCESS: DISPLAYED ALL ALBUMS");
		//albumList.getSelectionModel().select(0);
	}
	
	public void createAlbum(ActionEvent e) throws IOException {
		
		Button b = (Button)e.getSource();
		
		if( b == create ) {
			
			TextInputDialog dialog = new TextInputDialog("New York Tour");
			dialog.initOwner(this.mainStage); 
			dialog.setTitle("Create a new Album");
			dialog.setHeaderText("Give your Album a name");
			dialog.setContentText("Enter name: ");
			Optional<String> newAlbumName = dialog.showAndWait();
			
			if(!newAlbumName.isPresent()) { //cancel the creation
				//System.out.println("FAIL: CANNOT CREATE ALBUM: CANCELED");
				return;
			}
			
			//从对话框得到用户要新建的相册名后，检查是否可以创建folder，如果可以，创建folder
			Album newAlbum = new Album(newAlbumName.get());
			newAlbum.setUser(currentUser);
			
		
			if(newAlbum.setFolder()) {
				//可以建立新的album
				currentUser.addAlbum(new Album(newAlbumName.get()));
				displayList.add(newAlbum.getName()+", "+newAlbum.getSize()+ " photos, "+newAlbum.getRangeOfDate());
				allAlbumName.add(newAlbum.getName());
				String  path = "src/users/"+currentUser.getName()+"/gallery.txt";
				clearInfoForFile(path);
				writeFile(path, allAlbumName);
				//writeFileAppend(writePath, "\n"+newAlbumName.get());
				//File file = new File("src/users/"+currentUserName+"/"+newAlbumName.get()+"/"+"photoInfo.txt");
				//file.createNewFile();
				//System.out.println("SUCCESS: ADD NEW ALBUM TO GALLERY: "+newAlbumName.get());
			}else {
				//如果setFolder失败，弹出警告，不可以建立新的album
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setHeaderText("Ooops, there was an error!");
				alert.setContentText("You cannot create an existing Album");
				alert.showAndWait();
				return;
				//System.out.println("FAIL: CANNOT BUILD A NEW ALBUM, MAY DUPLICATED");
			}
			
		}else {
			//System.out.println("FAIL: CREATE: UNKNOWN");
		}
		
	}
		
	public void deleteAlbum(ActionEvent e) throws IOException {
		
		
		Button b = (Button)e.getSource();
		
		
		if( b == delete) {
			
			//System.out.println("0: "+allAlbumName);
			int index = albumList.getSelectionModel().getSelectedIndex();
			if(index == -1) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error occurs....");
				alert.setHeaderText("DELETION FAILED: cannot delete a null album");
				alert.setContentText("Select A Valid Album");
				alert.showAndWait();
				
				//System.out.println("FAIL: CANNOT DELETE THE ALBUM: "+ index);
				return;
				
			}else {
				//delete index of album
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation");
				alert.setContentText("Delete Album:  "+ currentUser.getGallery().get(index).getName());
				Optional<ButtonType> result = alert.showAndWait();
				
				if (result.get() == ButtonType.OK){
					
				/*	if(currentAlbum.getName().compareTo(currentUser.getGallery().get(index).getName())==0) {
						PictureFile empty = new PictureFile();
						displayPhoto(empty);
						preview.getChildren().clear();
						display.setImage(null);
					}*/
					//System.out.println("SUCCESS: DELETE THE ALBUM:  " + currentUser.getGallery().get(index).getName() );
					currentUser.deleteAlbum(index);
					displayList.remove(index);
					//System.out.println("1: "+allAlbumName);
					allAlbumName.remove(index);
					
					
					
					
					String path = "src/users/"+currentUser.getName()+"/gallery.txt";
					clearInfoForFile(path);
					//System.out.println("2: "+allAlbumName);
					writeFile(path, allAlbumName);
					
				}else {
					
					//System.out.println("FAIL: DELETE: CANCELED");
					return;
					
				}
				
			}
			
			
		}else {
			//System.out.println("FAIL: DELETE: UNKNOWN");
		}
		
	}
	
	public void renameAlbum(ActionEvent e) throws IOException{
		Button b = (Button)e.getSource();
		
		if(b == rename) {
			
			int index = albumList.getSelectionModel().getSelectedIndex();
			
			if(index == -1) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error occurs....");
				alert.setHeaderText("RENAME FAILED: cannot rename a null album");
				alert.setContentText("Select A Valid Album");
				alert.showAndWait();
				
				//System.out.println("FAIL: CANNOT RENAME THE ALBUM:  "+ index);
				return;
				
			}else {
				
				TextInputDialog dialog = new TextInputDialog("Beijing Tour");
				dialog.initOwner(this.mainStage); 
				dialog.setTitle("Rename an Album");
				dialog.setHeaderText("Give your Album a new name");
				dialog.setContentText("Enter name: ");
				Optional<String> newAlbumName = dialog.showAndWait();
				
				if(!newAlbumName.isPresent()) { //cancel the creation
					
					//System.out.println("FAIL: CANNOT RENAME THE ALBUM: CANCELED");
					return;
				}
				
				
				for(int i=0; i<currentUser.getGallery().size(); i++) {
					if(currentUser.getGallery().get(i).getName().equals(newAlbumName.get())) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Error occurs....");
						alert.setHeaderText("Rename Fails");
						alert.setContentText("Duplicated Name Found");
						alert.showAndWait();
					}
				}
				
				//改list名字
				displayList.set(index, newAlbumName.get()+", "+
						currentUser.getGallery().get(index).getSize()+" photos, "+
						currentUser.getGallery().get(index).getRangeOfDate());
				
				allAlbumName.set(index, newAlbumName.get());
				String path = "src/users/"+currentUser.getName()+"/gallery.txt";
				writeFile(path, allAlbumName);
				//改folder名字
				String userPath = "src/users/"+currentUser.getName();
				File file = new File(userPath+"/"+ currentUser.getGallery().get(index).getName());
				file.renameTo(new File(userPath+"/"+newAlbumName.get()));
				//改ArrayList中的名字 setName()
				currentUser.getGallery().get(index).setName(newAlbumName.get());
				//System.out.println("SUCCESS: RENAME THE ALBUM:  "+currentUser.getGallery().get(index).getName());
				
			}
		}else {
			//System.out.println("FAIL: RENAME: UNKNOWN");
		}
			
		
		
	}
	
	public void openAlbum(ActionEvent e) throws IOException {
		Button b = (Button) e.getSource();
		
		if(b==open) {

			preview.getChildren().clear();
			display.setImage(null);
			
			int index = albumList.getSelectionModel().getSelectedIndex();
			
			if(index == -1) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error occurs....");
				alert.setHeaderText("Open Album Fails");
				alert.setContentText("Select A Valid Album");
				alert.showAndWait();
				
				//System.out.println("FAIL: cannot open the album: "+ index);
				return;
				
			}else {
				//先选择到这个album
				currentAlbum = currentUser.getGallery().get(index);

				//说明currentAlbum已经是当前要打开的album：映射正确
				//System.out.println("SUCCESS: catch the album: "+currentAlbum.getName());
				//需要扫描当前album, 获取当前album位置，扫描出所有照片
			
			
				//相册不为空，可以打开相册展示缩略图
				File file = new File("src/users/"+currentUser.getName()+"/"+currentAlbum.getName());
				File[] fs = file.listFiles();
				
				
				for(File f:fs) {

					if(!f.isDirectory()) {//扫面文件夹
						
						if(isPicture(f)) { // 如果是照片 添加到preview中
							String pathName = "src/users/"+currentUser.getName()+"/"+currentAlbum.getName()+"/"+f.getName();
							//System.out.println(pathName);
							Image image = new Image(new FileInputStream(pathName));
							ImageView temp = new ImageView();
							temp.setImage(image);
							temp.setFitHeight(100);
							temp.setFitWidth(100);
							temp.setPreserveRatio(true);
							//一但照片被点中，显示照片信息
							temp.setOnMouseClicked((MouseEvent event) -> {
								display.setImage(image);
								for(Album album:currentUser.getGallery()){
									for(PictureFile pictureFile:album.getPhotoCollection()){
										if(pictureFile.getName().equals(f.getName())){
											displayPhoto(pictureFile);
											currentPic = pictureFile;
											selectedPicture = temp;
										}
									}
								}
							});

							this.preview.getChildren().add(temp);	
						}	
					}
				}
			}
				
			
			
			
			
			
			
			
			
			
		}else {
			//System.out.println("FAIL: UNKNOWN");
		}
	}

	/**
	 * this method add photo to current opining album
	 * @param e action event
	 * @throws IOException io expection
	 */
	public void addPhoto(ActionEvent e) throws IOException {
	
		
		if (currentAlbum == null) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Error occurs....");
			alert.setHeaderText("ADD FAILED: null album opened");
			alert.setContentText("You need to open a valid album first");
			alert.showAndWait();
			return;
		} else {

			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("View Pictures");
			fileChooser.getExtensionFilters().addAll(
					new FileChooser.ExtensionFilter("All Images", "*.*"),
					new FileChooser.ExtensionFilter("JPG", ".jpg"),
					new FileChooser.ExtensionFilter("GIF", ".gif"),
					new FileChooser.ExtensionFilter("BMP", ".bmp"),
					new FileChooser.ExtensionFilter("PNG", ".png")	
					);
			
			File sourceFile = fileChooser.showOpenDialog(mainStage);
			File destFile = new File("src/users/"+currentUser.getName()+"/"
					+currentAlbum.getName()+"/"+sourceFile.getName());
			
		//	System.out.println(sourceFile.getPath());
		//	System.out.println(destFile.getPath());
			
			String destDirc = destFile.getPath();
			Path destPath = Paths.get(destDirc);
			if (sourceFile.isFile() && isPicture(sourceFile)) {
				try {
					Files.copy(sourceFile.toPath(), destPath);
					File destFilePointer = new File(destDirc);
					Image image = new Image(destFilePointer.toURI().toString());
					ImageView temp = new ImageView();
					temp.setImage(image);
					temp.setFitHeight(100);
					temp.setFitWidth(100);
					temp.setPreserveRatio(true);
					PictureFile pictureFile = new PictureFile(destFilePointer);
					currentAlbum.addPhoto(pictureFile);
					File info = new File("src/users/" + currentUser.getName() + "/" + currentAlbum.getName()+"/"+sourceFile.getName()+".txt");
					String date = pictureFile.getDate();
					long lastModified = pictureFile.getLastModifiedDate();
					FileWriter fw = new FileWriter(info,true);
					BufferedWriter out = new BufferedWriter(fw);
					out.write("NAME: "+sourceFile.getName()+"\n");
					out.write("DATE: "+date+"\n");
					out.write("LAST: "+lastModified+"\n");
					out.write("CAPTION: \n");
					out.write("TAGS: \n");
					out.close();

					//change the photo number in albumList
					displayAlbumRevise();
					temp.setOnMouseClicked((MouseEvent event) -> {
						display.setImage(image);
						currentPic = pictureFile;
						displayPhoto(pictureFile);
						selectedPicture = temp;


					});

					this.preview.getChildren().add(temp);


				}catch (FileAlreadyExistsException ex){
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Error occurs....");
					alert.setHeaderText("Add photo FAILED");
					alert.setContentText("File already exists");
					alert.showAndWait();
				}
			}
			else{
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error occurs....");
				alert.setHeaderText("Add photo FAILED");
				alert.setContentText("Wrong photo address");
				alert.showAndWait();
			}

		}
	}

	public void displayAlbumRevise() {
		ArrayList<Album> currentGallery = currentUser.getGallery();
		displayList = FXCollections.observableArrayList();   
		
		for(int i = 0; i<currentGallery.size(); i++) {
			displayList.add(currentGallery.get(i).getName()+", "
					+currentGallery.get(i).getSize()+ " photos, "+currentGallery.get(i).getRangeOfDate());
		}
				
		albumList.setItems(displayList);
	}
	
	public void quit (ActionEvent e) throws IOException{
		Button b = (Button) e.getSource();
		
		if(b==quit) {
			String path = "src/users/currentUser.txt";
		    clearInfoForFile(path);
			Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
			stage.close();
			//System.out.println("SUCCESS: QUIT");
			
		}else {
			//System.out.println("FAIL: QUIT: UNKNOWN");
		}
		
		 
	}
	
	public void logout(ActionEvent e) throws IOException {
		 Button b = (Button)e.getSource();
		 
		 if(b == logout) {
			 Parent view = FXMLLoader.load(getClass().getResource("../view/login.fxml"));
		        Scene tableView = new Scene(view);
		        Stage window = (Stage)((Node)e.getSource()).getScene().getWindow();
		        String path = "src/users/currentUser.txt";
		        clearInfoForFile(path);
		        
		        window.setScene(tableView);
		        window.setTitle("Login");
		        window.show();
		 }else {
			 //System.out.println("FAIL: LOGOUT: UNKNOWN");
		 }
	     
	}
	
	private void writeFile(String path, ArrayList<String> allAlbumName) throws IOException {
		
		PrintWriter writer = new PrintWriter(path);
		writer.print("");
		writer.close();
		
		
		File fout = new File(path);
		FileOutputStream fos = new FileOutputStream(fout);
	 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	 
		for (int i = 0; i < allAlbumName.size(); i++) {
			bw.write(allAlbumName.get(i));
			bw.newLine();
		}
	 
		bw.close();
		
	}
		
	public static void clearInfoForFile(String fileName) {
        File file =new File(fileName);
        try {
            if(!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	public boolean isPicture(File temp) {
		
		String fileName = temp.getName();
    	if(fileName.toLowerCase().endsWith(".jpg")||
    	   fileName.toLowerCase().endsWith(".jpeg")||
    	   fileName.toLowerCase().endsWith(".png")||
    	   fileName.toLowerCase().endsWith(".bmp")||
    	   fileName.toLowerCase().endsWith(".gif")	) {
    		return true;
    	}
    	
    	return false;
    }
	
    public void addCaption(ActionEvent e) throws IOException {
    	
    	String newCaption = null;
    	TextInputDialog dialog = new TextInputDialog("caption");
    	dialog.setTitle("Caption");
    	dialog.setHeaderText("Please input your caption");
    	dialog.setContentText("Caption: ");

    	// Traditional way to get the response value.
    	Optional<String> result = dialog.showAndWait();
    	if (result.isPresent()){
    	   newCaption = result.get();
    	}else {
    		return;
    	}

		currentPic.setCaption(newCaption);

		displayPhoto(currentPic);
		//File fp = new File("src/users/"+currentUserName+"/"+currentAlbum.getName()+"/"+currentPic.getName()+".txt");
		BufferedReader reader = new BufferedReader(new FileReader("src/users/"+currentUserName+"/"+currentAlbum.getName()+"/"+currentPic.getName()+".txt"));
		StringBuilder inputBuffer = new StringBuilder();
		String currentLine;
		while((currentLine = reader.readLine())!=null){
			//System.out.println(currentLine.trim());
			if(currentLine.substring(0,currentLine.indexOf(" ")).equals("CAPTION:")){
				currentLine = "CAPTION: "+newCaption;

			}
			//System.out.println(currentLine);
			inputBuffer.append(currentLine);
			inputBuffer.append('\n');
		}


		reader.close();
		FileOutputStream fileOut = new FileOutputStream("src/users/"+currentUserName+"/"+currentAlbum.getName()+"/"+currentPic.getName()+".txt");
		fileOut.write(inputBuffer.toString().getBytes());
		fileOut.close();

	}
    
	public void addTag(ActionEvent e) throws IOException {
		
		
		String tagNameString = tagName.getText();
		String tagValueString = tagValue.getText();
		System.out.println("tagValueString:"+tagValueString);
		String tagString = tagNameString + " "+ tagValueString;
		
		if(currentAlbum == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Ooops, there was an error!");
			alert.setContentText("You need to open a valid album");
			alert.showAndWait();
			return;
		}else if (currentPic == null) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Ooops, there was an error!");
			alert.setContentText("You need to select a valid photo");
			alert.showAndWait();
		}else if(tagNameString.length()<=0 || tagValueString.length()<=0) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Ooops, there was an error!");
			alert.setContentText("You cannot tag with null value");
			alert.showAndWait();
		}else {
			
			File fp = new File("src/users/"+currentUserName+"/"+currentAlbum.getName()+"/"+currentPic.getName()+".txt");
			File f = new File("src/users/"+currentUserName+"/"+currentAlbum.getName()+"/"+currentPic.getName());
			BufferedReader reader = new BufferedReader(new FileReader("src/users/"+currentUserName+"/"+currentAlbum.getName()+"/"+currentPic.getName()+".txt"));
			String currentLine;
		//	boolean ifExist = false;
				//check if tag exist
			while((currentLine = reader.readLine())!=null){
				if(currentLine.equals(tagString)){
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("add tag faild");
					alert.setContentText("tag already exist");
					alert.showAndWait();
		//			ifExist = true;
					return;
				}
				
			}
			
			//不存在tag value type完全相同的情况下
			//考虑添加哪种tag
			
			//tag is not duplicated, add to .txt file
			
			//update pictureFile
			//Tags tag = new Tags(tagNameString,tagValueString);
			
				
			
			
			if(single.isSelected()==true && multiple.isSelected()==false) {
				Boolean legal = true;
				for(int i=0; i<currentPic.getTags().size(); i++) {
					if(tagNameString.compareTo(currentPic.getTags().get(i).getName())==0) {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Error");
						alert.setHeaderText("Same tag type found");
						alert.setContentText("Change this tag to multiple");
						alert.showAndWait();
						legal = false;
					}
				}
				
				//System.out.println("legal: "+legal);
				if(legal) {
					//System.out.println("add: "+tagString);
					FileWriter fw = new FileWriter(fp,true);
					BufferedWriter out = new BufferedWriter(fw);
					out.write(tagString+"\n");
					out.close();
					fw.close();
					for(Album album:currentUser.getGallery()){
						for(PictureFile pictureFile:album.getPhotoCollection()){
							if(pictureFile.getName().equals(f.getName())){
								//System.out.println("find target picture");
								pictureFile.addTags(tagNameString,tagValueString);
								
								displayPhoto(pictureFile);
								//currentPic = pictureFile;

							}
						}
					}
				}else {
					return;
				}
				
				
				
				
			}else if(multiple.isSelected() == true && single.isSelected()==false) {
				//System.out.println("add: "+tagString);
				FileWriter fw = new FileWriter(fp,true);
				BufferedWriter out = new BufferedWriter(fw);
				out.write(tagString+"\n");
				out.close();
				fw.close();
				for(Album album:currentUser.getGallery()){
					for(PictureFile pictureFile:album.getPhotoCollection()){
						if(pictureFile.getName().equals(f.getName())){
							//System.out.println("find target picture");
							pictureFile.addTags(tagNameString,tagValueString);
							displayPhoto(pictureFile);
							//currentPic = pictureFile;

						}
					}
				}
				
				
			}else  {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Ooops, there was an error!");
				alert.setContentText("You need to choose exactly one mode of tag: single/multiple");
				alert.showAndWait();
			}
			
		
		
		
		
	//	System.out.println("tagString: "+tagString);
	//	System.out.println("src/users/"+currentUserName+"/"+currentAlbum.getName()+"/"+currentPic.getName()+".txt");
		
		}
	}
	
	public void printTags(PictureFile pf){
		for(Tags tag:pf.getTags()){
	//		System.out.println(tag.getName()+"  "+tag.getValue());
		}
	}
	
    public void displayPhoto(PictureFile pf){
		//display date
		dateTaken.setText(pf.getDate());
		photoName.setText(pf.getName());

		//display caption
		caption.setText(pf.getCaption());

		//display tags
		//tagTable = new TableView<Tags>();
		TableColumn<Tags,String> nameColumn = new TableColumn<>("name");
		TableColumn<Tags,String> valueColumn = new TableColumn<>("value");
		valueColumn.setMinWidth(100);
		nameColumn.setMinWidth(100);
		nameColumn.setCellValueFactory(new PropertyValueFactory<Tags,String>("name"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<Tags,String>("value"));
		int tagNum = pf.getTags().size();
	//	System.out.println(tagNum);


		tagTable.getColumns().clear();
		tagTable.setItems(getTags(pf,tagNum));
		tagTable.getColumns().addAll(nameColumn,valueColumn);

	}
    
	public ObservableList<Tags> getTags(PictureFile pf,int tagNum){
		ObservableList<Tags> tags = FXCollections.observableArrayList();
		for(int i = 0; i < tagNum; i++){
			tags.add(new Tags(pf.getTags().get(i).getName(),pf.getTags().get(i).getValue()));
		}
		return tags;
	}
	
	public void deleteTag() throws IOException {
		//delete TableView row and txt file and PictureFile tag
		
		try {
			if(currentAlbum == null) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Ooops, there was an error!");
				alert.setContentText("You need to open a valid album");
				alert.showAndWait();
				return;
			}else if (currentPic == null) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Ooops, there was an error!");
				alert.setContentText("You need to select a valid photo");
				alert.showAndWait();
			}else {
				
				Tags tag = tagTable.getSelectionModel().getSelectedItem();
				String tagString = tag.getName()+tag.getValue();
			//	System.out.println("NAME: "+tag.getName());
			//	System.out.println("VALUE: "+tag.getValue());

				currentPic.removeTags(tag.getName(),tag.getValue());
				//System.out.println(currentPic.getName());
				displayPhoto(currentPic);
				String currentLine;
				
				File inputFile = new File("src/users/"+currentUserName+"/"+currentAlbum.getName()+"/"+currentPic.getName()+".txt");
				File tempFile =  new File("src/users/"+currentUserName+"/"+currentAlbum.getName()+"/"+"temp.txt");
				BufferedReader reader = new BufferedReader(new FileReader(inputFile));
				BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			
			
				while((currentLine = reader.readLine()) != null) {
					// trim newline when comparing with lineToRemove
					String trimmedLine = currentLine.trim();
				//	System.out.println("currentLine: "+currentLine);
				//	System.out.println("trimmedLine: "+trimmedLine);
				//	System.out.println("tagString: "+tagString);
				//	System.out.println();
					if(!trimmedLine.equals(tagString)) {
						writer.write(currentLine + System.getProperty("line.separator"));
					}
					
				}
				
				writer.close();
				reader.close();
				tempFile.renameTo(inputFile);
			}
		}
		catch (NullPointerException e){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error occurs....");
			alert.setHeaderText("Delete Tag FAILED");
			alert.setContentText("Tag not selected");
			alert.showAndWait();
		}
	}

	public void deletePhoto(ActionEvent e) throws IOException{
		
		
		if(currentAlbum == null) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Error occurs....");
			alert.setHeaderText("DELETE FAILED: null album opened");
			alert.setContentText("You need to open a valid album first");
			alert.showAndWait();
			return;
			
		}
		
		preview.getChildren().remove(selectedPicture);
		String name = currentPic.getName();
		File file = currentPic.getImageFile();
		file.delete();
		String text = currentPic.getImageFile().getPath()+".txt";
		File textFile = new File(text);
		currentAlbum.deletePhoto(currentPic);
		displayAlbumRevise();
		textFile.delete();
		display.setImage(null);
		
		
		
	}

	public void movePhoto(ActionEvent e) throws IOException{
		Button b = (Button)e.getSource();
		
		
		
		if(b==copy) {
			if(currentAlbum == null) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error occurs....");
				alert.setHeaderText("COPY FAILED: null album opened");
				alert.setContentText("You need to open a valid album first");
				alert.showAndWait();
				return;
			}
			copiedPic = currentPic.copyPicture();
			//System.out.println("Selected photo: "+copiedPic.getName());
			
		}else if(b==move || b==paste) {
			//move copiedPic to destination
			
			//首先得到选中的相册，如果是同一个相册->alert error
			
			//得到相册后 进行修改
			
			if(currentAlbum == null) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error occurs....");
				alert.setHeaderText("COPY FAILED: null album opened");
				alert.setContentText("You need to open a valid album first");
				alert.showAndWait();
				return;
			}
			
			int index = albumList.getSelectionModel().getSelectedIndex();
			
			if(index == currentUser.getGallery().indexOf(currentAlbum) ) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error occurs....");
				alert.setHeaderText("COPY FAILED: cannot proceed in the same album");
				alert.setContentText("You need to select another album");
				alert.showAndWait();
				
				//System.out.println("FAIL: cannot copy the photo: Unvalid Album");
				return;
			}else if(copiedPic == null){
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error occurs....");
				alert.setHeaderText("COPY FAILED: null photo copied");
				alert.setContentText("You need to copy a valid photo first");
				alert.showAndWait();
				return;
			}else {
				
				//System.out.println("currentAlbum: "+currentUser.getGallery().get(index).getName());
				//选好相册后，进行照片名查重复
				for(int i=0; i<currentUser.getGallery().get(index).getSize(); i++) {
					if(copiedPic.getName().compareTo(currentUser.getGallery().get(index).getPhotoCollection().get(i).getName())==0) {
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("Error occurs....");
						alert.setHeaderText("Copy Photo Fails");
						alert.setContentText("Duplicated Photo Found");
						alert.showAndWait();
						
						//System.out.println("FAIL: cannot copy the photo: Duplicated Photo");
						return;
					}
				}
				
				//修改内存文件,图片文件和txt文件
				//修改闪存
				//修改txt
				copyFile(copiedPic.getImageFile().getPath(), 
						"src/users/"+currentUser.getName()+"/"+currentUser.getGallery().get(index).getName()+"/"+copiedPic.getName());
				copyFile("src/users/"+currentUser.getName()+"/"+currentAlbum.getName()+"/"+copiedPic.getName()+".txt", 
						 "src/users/"+currentUser.getName()+"/"+currentUser.getGallery().get(index).getName()+"/"+copiedPic.getName()+".txt");
				
				//System.out.println("src/users/"+currentUser.getName()+"/"+currentAlbum.getName()+"/"+copiedPic.getName()+".txt");
				//System.out.println();
				
				currentUser.getGallery().get(index).addPhoto(copiedPic);
				displayAlbumRevise();
			
				
			}
			
			
			//如果是move，还要删除原来的图 list txt
			if(b==move) {
				
				File temp = new File("src/users/"+currentUser.getName()+"/"+currentAlbum.getName()+"/"+currentPic.getName()+".txt");
				temp.delete();
				File temp2 = new File("src/users/"+currentUser.getName()+"/"+currentAlbum.getName()+"/"+currentPic.getName());
				temp2.delete();
				
				currentAlbum.deletePhoto(currentPic);
				displayAlbumRevise();
			}
			
		}else {
			//System.out.println("FAIL: NEITHER COPY NOR MOVE");
		}
		
		
		
	}

	public static void copyFile(String src,String target){	
		File srcFile = new File(src);  
		   File targetFile = new File(target);  
		   try {  
		       InputStream in = new FileInputStream(srcFile);   
		       OutputStream out = new FileOutputStream(targetFile);  
		       byte[] bytes = new byte[1024];  
		       int len = -1;  
		       while((len=in.read(bytes))!=-1)
		       {  
		           out.write(bytes, 0, len);  
		       }  
		       in.close();  
		       out.close();  
		   } catch (FileNotFoundException e) {  
		       e.printStackTrace();  
		   } catch (IOException e) {  
		       e.printStackTrace();  
		   }  
		   //System.out.println("文件复制成功"); 


	}
		 
	public void slideShow(ActionEvent e) throws IOException{
		Button b = (Button)e.getSource();
		
		
		if(b==next) {
			if(slideCounter <= currentAlbum.getSize()-2) {
			//	System.out.println("slideCounter: "+slideCounter);
			//	System.out.println("currentAlbumSize: "+currentAlbum.getSize());
				
				String path = "src/users/"+ currentUser.getName()+"/"+currentAlbum.getName()+"/"
					+currentAlbum.getPhotoCollection().get(slideCounter+1).getName();
				Image image = new Image(new FileInputStream(path));
				display.setImage(image);
				currentPic = currentAlbum.getPhotoCollection().get(slideCounter+1);
				displayPhoto(currentPic);
				slideCounter++;
				
			}else if(slideCounter == currentAlbum.getSize()-1){
				slideCounter = 0;
				String path = "src/users/"+ currentUser.getName()+"/"+currentAlbum.getName()+"/"
						+currentAlbum.getPhotoCollection().get(slideCounter).getName();
				Image image = new Image(new FileInputStream(path));
				display.setImage(image);
				currentPic = currentAlbum.getPhotoCollection().get(slideCounter);
				displayPhoto(currentPic);
	
			}
			
			
	
		}else if(b==last) {
			
			if(slideCounter >=1) {
				
				String path = "src/users/"+ currentUser.getName()+"/"+currentAlbum.getName()+"/"
					+currentAlbum.getPhotoCollection().get(slideCounter-1).getName();
				Image image = new Image(new FileInputStream(path));
				display.setImage(image);
				currentPic = currentAlbum.getPhotoCollection().get(slideCounter-1);
				displayPhoto(currentPic);
				slideCounter --;
				
			}else if(slideCounter ==0 ){
				slideCounter = currentAlbum.getSize()-1;
				String path = "src/users/"+ currentUser.getName()+"/"+currentAlbum.getName()+"/"
						+currentAlbum.getPhotoCollection().get(slideCounter).getName();
				Image image = new Image(new FileInputStream(path));
				display.setImage(image);
				currentPic = currentAlbum.getPhotoCollection().get(slideCounter);
				displayPhoto(currentPic);
				
			}
			
		}else {
			//System.out.println("FAIL: SLIDESHOW");
		}
	
		
	}

	public void searchPhotoByDateRange(ActionEvent e) throws IOException {
		
		Button b = (Button)e.getSource();
		if(b==search) {
			
			searchDisplay.getChildren().clear();
			LocalDate early = earliest.getValue();
			LocalDate late = latest.getValue();
			
			if(currentAlbum == null) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Ooops, there was an error!");
				alert.setContentText("You need to open a valid album");
				alert.showAndWait();
				return;
			
			}else if(compareDate(early, late)){
				
				//System.out.println("hello");
				//打开一个相册后，按着date搜索
				//1. 先得到用户需要的最早和最晚
				//用localdate和localdate做比较
				int currentPhotoDay;
				int currentPhotoMonth;
				int currentPhotoYear;
				ArrayList<PictureFile> searchResult = new ArrayList<PictureFile>();
				
				//对currentAlbum搜索每一张照片得到localdate
				for(int i=0; i<currentAlbum.getSize();i++) {
				
					PictureFile currentPhoto = currentAlbum.getPhotoCollection().get(i);
					currentPhotoDay = Integer.parseInt(currentPhoto.getDay());
					currentPhotoMonth = Integer.parseInt(currentPhoto.getMonth());
					currentPhotoYear = Integer.parseInt(currentPhoto.getYear());
					LocalDate temp = LocalDate.of(currentPhotoYear,currentPhotoMonth , currentPhotoDay);
		
					//现在要对比是否在earliest和latest中间，如果是，保留，
					if(compareDate(early,temp)&&compareDate(temp,late)) {
						searchResult.add(currentPhoto);
						//得到所有符合时间要求的photo-->来吧，展示 （也是用一个FlowPane: searchDisplay
						File file = currentPhoto.getImageFile();
						Image image = new Image(file.toURI().toString());
						PictureFile temp2 = currentPhoto.copyPicture();
						ImageView tempView = new ImageView();
						tempView.setImage(image);
						tempView.setFitHeight(100);
						tempView.setFitWidth(100);
						tempView.setPreserveRatio(true);	
						tempView.setOnMouseClicked((MouseEvent event) -> {
							display.setImage(image);
							currentPic = temp2;
							displayPhoto(temp2);
							selectedPicture = tempView;
						});
						
						this.searchDisplay.getChildren().add(tempView);
						
					}
				}
				
					
					
				

				
				
				//System.out.println("found one photo");
				
				
				
				
			}else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Ooops, there was an error!");
				alert.setContentText("The earliest date cannot exceed the latest date");
				alert.showAndWait();
				return;
			}
			
		}
		
		
	}

	private boolean compareDate(LocalDate a, LocalDate b) {
		if(a.getYear() > b.getYear()) {
			return false;
		}else if(a.getYear()==b.getYear()){
			if(a.getMonth().compareTo(b.getMonth())>0) {
				System.out.println("A month > b month");
				return false;
			}else if(a.getMonth()==b.getMonth()) {
				if(a.getDayOfMonth()>b.getDayOfMonth()) {
					return false;  // 如果搜索某特定一天 earliest=latest 可以 return true
				}
			}
			
		}
		
		
		return true;
	}

	public void searchPhotoByTags(ActionEvent e) throws IOException {
		
		
		Button b = (Button)e.getSource();
		if(b == searchTag) {
			searchDisplay.getChildren().clear();
			searchList = new Album("searchList");
			//System.out.println("hello");
			String type1String = type1.getText();
			String type2String = type2.getText();
			String value1String = value1.getText();
			String value2String = value2.getText();
				
			if(currentAlbum == null) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Ooops, there was an error!");
				alert.setContentText("You need to open a valid album");
				alert.showAndWait();
				return;
			}else if(type2.getText().length()<=0 || value2.getText().length()<=0) {
				
				for(int i=0; i<currentAlbum.getPhotoCollection().size(); i++) {	
				//每张图
					PictureFile currentPhoto = currentAlbum.getPhotoCollection().get(i);
					
					System.out.println("1. current Pic Name: "+currentPhoto.getName());
					
					for(int j=0; j<currentPhoto.getTags().size(); j++) {
						//每张图的每个tag
						Tags currentTag = currentPhoto.getTags().get(j);
						
						String currentTagType = currentTag.getName();	
						String currentTagValue= currentTag.getValue();
						
						System.out.println("current Tag:"+ currentTagType);
						System.out.println("current Value:"+currentTagValue);
						System.out.println("apinput Tag:"+ type1String);
						System.out.println("apinput Value:"+value1String);
						
						System.out.println();
						
						
						if(currentTagType.compareTo(type1String)==0 && currentTagValue.compareTo(value1String)==0) {
							//System.out.println(currentPhoto.getName());
							//得到所有符合时间要求的photo-->来吧，展示 （也是用一个FlowPane: searchDisplay
							searchList.addPhoto(currentPhoto);
							File file = currentPhoto.getImageFile();
							Image image = new Image(file.toURI().toString());
							PictureFile temp2 = currentPhoto.copyPicture();
							ImageView tempView = new ImageView();
							tempView.setImage(image);
							tempView.setFitHeight(100);
							tempView.setFitWidth(100);
							tempView.setPreserveRatio(true);	
							tempView.setOnMouseClicked((MouseEvent event) -> {
								display.setImage(image);
								currentPic = temp2;
								displayPhoto(temp2);
								selectedPicture = tempView;
							});
							
							this.searchDisplay.getChildren().add(tempView);
						
							
						}
					}
				
				}
				
				
			}else if(type2.getText().length()>0 && value2.getText().length()>0){
				if(and.isSelected()==true && or.isSelected()==true) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Ooops, there was an error!");
					alert.setContentText("Select exactly 1 box");
					alert.showAndWait();
				}else if(and.isSelected()==false && or.isSelected()==false) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Ooops, there was an error!");
					alert.setContentText("Select exactly 1 box");
					alert.showAndWait();
					return;
				}else if(and.isSelected() == false && or.isSelected()==true) {
					
					for(int i=0; i<currentAlbum.getPhotoCollection().size(); i++) {	
						//每张图
							
							PictureFile currentPhoto = currentAlbum.getPhotoCollection().get(i);
							for(int j=0; j<currentPhoto.getTags().size(); j++) {
								//每张图的每个tag
								Tags currentTag = currentPhoto.getTags().get(j);
								String currentTagType = currentTag.getName();	
								String currentTagValue= currentTag.getValue();
							
								if((currentTagType.compareTo(type1String)==0 && currentTagValue.compareTo(value1String)==0) ||
										(currentTagType.compareTo(type2String)==0 && currentTagValue.compareTo(value2String)==0)) {
									if(checkDuplicatedPhoto(searchList, currentPhoto)) {
										searchList.addPhoto(currentPhoto);
										//得到所有符合时间要求的photo-->来吧，展示 （也是用一个FlowPane: searchDisplay
										File file = currentPhoto.getImageFile();
										Image image = new Image(file.toURI().toString());
										PictureFile temp2 = currentPhoto.copyPicture();
										ImageView tempView = new ImageView();
										tempView.setImage(image);
										tempView.setFitHeight(100);
										tempView.setFitWidth(100);
										tempView.setPreserveRatio(true);	
										tempView.setOnMouseClicked((MouseEvent event) -> {
											display.setImage(image);
											currentPic = temp2;
											displayPhoto(temp2);
											selectedPicture = tempView;
										});
									
										this.searchDisplay.getChildren().add(tempView);
										
									}
									
								}
							}
						
						}
					
				}else if(and.isSelected()==true && or.isSelected() == false) {
					
					for(int i=0; i<currentAlbum.getPhotoCollection().size(); i++) {	
						//每张图
							Boolean passTag1 = false;
							Boolean passTag2 = false;
							PictureFile currentPhoto = currentAlbum.getPhotoCollection().get(i);
							for(int j=0; j<currentPhoto.getTags().size(); j++) {
							
								Tags currentTag = currentPhoto.getTags().get(j);
								String currentTagType = currentTag.getName();	
								String currentTagValue= currentTag.getValue();
							
								//满足了一个tag 
								if(currentTagType.compareTo(type1String)==0 && currentTagValue.compareTo(value1String)==0){
									passTag1 = true;
								}
							}
							
							for(int j=0; j<currentPhoto.getTags().size(); j++) {
								
								Tags currentTag = currentPhoto.getTags().get(j);
								String currentTagType = currentTag.getName();	
								String currentTagValue= currentTag.getValue();
							
								//满足了一个tag 
								if(currentTagType.compareTo(type2String)==0 && currentTagValue.compareTo(value2String)==0){
									passTag2 = true;
								}
							}
							
							if(passTag1==true && passTag2==true) {
								if(checkDuplicatedPhoto(searchList, currentPhoto)) {
									searchList.addPhoto(currentPhoto);
									//得到所有符合时间要求的photo-->来吧，展示 （也是用一个FlowPane: searchDisplay
									File file = currentPhoto.getImageFile();
									Image image = new Image(file.toURI().toString());
									PictureFile temp2 = currentPhoto.copyPicture();
									ImageView tempView = new ImageView();
									tempView.setImage(image);
									tempView.setFitHeight(100);
									tempView.setFitWidth(100);
									tempView.setPreserveRatio(true);	
									tempView.setOnMouseClicked((MouseEvent event) -> {
										display.setImage(image);
										currentPic = temp2;
										displayPhoto(temp2);
										selectedPicture = tempView;
									});
								
									this.searchDisplay.getChildren().add(tempView);
									
								}
								
								
								
								
							}
						
						}
					
				}
				
				
				
				
				
				
			}else {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("Ooops, there was an error!");
				alert.setContentText("Either input 1 tag and value or 2 tags and values");
				alert.showAndWait();
				return;
			}
			
			
		}
		
		
		
	}
	
	public Boolean checkDuplicatedPhoto(Album album, PictureFile pic) {
		for(int i=0; i<album.getPhotoCollection().size(); i++) {
			if(album.getPhotoCollection().get(i)==pic) {
				return false;
			}
		}
		
		return true;
	}

	public void createAlbumSearch(ActionEvent e) throws IOException{
		
		Button b = (Button)e.getSource();
		
		if( b == searchCreate ) {
			
			TextInputDialog dialog = new TextInputDialog("Copied from searching");
			dialog.initOwner(this.mainStage); 
			dialog.setTitle("Create a new Album");
			dialog.setHeaderText("Give your Album a name");
			dialog.setContentText("Enter name: ");
			Optional<String> newAlbumName = dialog.showAndWait();
			
			if(!newAlbumName.isPresent()) { //cancel the creation
				//System.out.println("FAIL: CANNOT CREATE ALBUM: CANCELED");
				return;
			}
			
			//从对话框得到用户要新建的相册名后，检查是否可以创建folder，如果可以，创建folder
			Album newAlbum = new Album(newAlbumName.get());
			newAlbum.setUser(currentUser);
			
		
			if(newAlbum.setFolder()) {
				//可以建立新的album
				currentUser.addAlbum(new Album(newAlbumName.get()));
				
				allAlbumName.add(newAlbum.getName());
				String  path = "src/users/"+currentUser.getName()+"/gallery.txt";
				clearInfoForFile(path);
				writeFile(path, allAlbumName);
				//writeFileAppend(writePath, "\n"+newAlbumName.get());
				//File file = new File("src/users/"+currentUserName+"/"+newAlbumName.get()+"/"+"photoInfo.txt");
				//file.createNewFile();
				//System.out.println("SUCCESS: ADD NEW ALBUM TO GALLERY: "+newAlbumName.get());
			}else {
				//如果setFolder失败，弹出警告，不可以建立新的album
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setHeaderText("Ooops, there was an error!");
				alert.setContentText("You cannot create an existing Album");
				alert.showAndWait();
				return;
				//System.out.println("FAIL: CANNOT BUILD A NEW ALBUM, MAY DUPLICATED");
			}
			
			//newAlbum
			
			
			
			for(int i=0; i<searchList.getPhotoCollection().size(); i++) {
				
				PictureFile currentPhoto = searchList.getPhotoCollection().get(i);
				
				
				copyFile(currentPhoto.getImageFile().getPath(), 
						"src/users/"+currentUser.getName()+"/"+newAlbum.getName()+"/"+currentPhoto.getName());
				copyFile(currentPhoto.getImageFile().getPath()+".txt", 
						 "src/users/"+currentUser.getName()+"/"+newAlbum.getName()+"/"+currentPhoto.getName()+".txt");
				
				//System.out.println("src/users/"+currentUser.getName()+"/"+currentAlbum.getName()+"/"+copiedPic.getName()+".txt");
				//System.out.println();
				
				newAlbum.addPhoto(currentPhoto);
				preload();
				
			}
			
			displayList.add(newAlbum.getName()+", "+newAlbum.getSize()+ " photos, "+newAlbum.getRangeOfDate());
			
			
		}else {
			//System.out.println("FAIL: CREATE: UNKNOWN");
		}
		
		
		//
		
		
		
	}


}
