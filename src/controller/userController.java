package controller;



import java.io.*;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
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
import javafx.stage.Stage;
import model.Album;
import model.PictureFile;
import model.Tags;
import model.User;

import javax.swing.*;

public class userController implements Initializable{

	User currentUser;
	String currentUserName;
	Stage mainStage;
	Album currentAlbum;
	PictureFile currentPic;
	ImageView selectedPicture;
	@FXML Label welcome; 
	@FXML FlowPane preview;
	@FXML ImageView display;
	@FXML Button create;
	@FXML Button delete;
	@FXML Button rename;
	@FXML Button open;
	@FXML Button logout;
	@FXML Button quit;
	@FXML ListView<String> albumList;
	@FXML Button addPhoto;
	@FXML TextField photoDirct;
	@FXML Text dateTaken;
	@FXML Text caption;
	@FXML TableView<Tags> tagTable;
	@FXML TextField captionAddField;
	@FXML Button captionAddButton;
	@FXML Text photoName;
	@FXML TextField tagName;
	@FXML TextField tagValue;
	@FXML Button addTag;
	@FXML Button deleteTag;

	private ObservableList<String> displayList;
	private ArrayList<String> allAlbumName = new ArrayList<String>();
	
	
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

		try {
			initial();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("currentUser.txt not found");
		}
		
        try {
            preload();
        } catch (IOException e) {
            System.out.println("Album not found");
        }


        displayAlbum();
    }
		
		//WORKING QUEUE: 
		//show other info, 这个最后在说， 可以为内存大小

		
	
	
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
			System.out.println("SUCCESS: PRELOAD GALLERY SIZE: "+currentUser.getGallery().size());
		}else if(list.size()>0){
			//preload gallery
			for(int i=0; i<list.size(); i++) {
				String currentLoadingAlbumName = list.get(i);
				Album currentAlbum = new Album(currentLoadingAlbumName);
				System.out.println(currentLoadingAlbumName);
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
										value = currentLine.substring(currentLine.indexOf(" "));

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
			
			
			System.out.println("SUCCESS: PRELOAD GALLERY SIZE: "+currentUser.getGallery().size());
			
		}else {
			System.out.println("FAIL: PRELOAD: UNKNOWN");
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
		System.out.println("SUCCESS: DISPLAYED ALL ALBUMS");
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
				System.out.println("FAIL: CANNOT CREATE ALBUM: CANCELED");
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
				writeFile(path, allAlbumName);
				//writeFileAppend(writePath, "\n"+newAlbumName.get());
				//File file = new File("src/users/"+currentUserName+"/"+newAlbumName.get()+"/"+"photoInfo.txt");
				//file.createNewFile();
				System.out.println("SUCCESS: ADD NEW ALBUM TO GALLERY: "+newAlbumName.get());
			}else {
				//如果setFolder失败，弹出警告，不可以建立新的album
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error Dialog");
				alert.setHeaderText("Ooops, there was an error!");
				alert.setContentText("You cannot create an existing Album");
				alert.showAndWait();
				
				System.out.println("FAIL: CANNOT BUILD A NEW ALBUM, MAY DUPLICATED");
			}
			
		}else {
			System.out.println("FAIL: CREATE: UNKNOWN");
		}
		
	}
		
	public void deleteAlbum(ActionEvent e) throws IOException {
		Button b = (Button)e.getSource();
		
		if( b == delete) {
			
			
			int index = albumList.getSelectionModel().getSelectedIndex();
			if(index == -1) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error occurs....");
				alert.setHeaderText("Deletion Fails");
				alert.setContentText("Select A Valid Album");
				alert.showAndWait();
				
				System.out.println("FAIL: CANNOT DELETE THE ALBUM: "+ index);
				return;
				
			}else {
				//delete index of album
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Confirmation");
				alert.setContentText("Delete Album:  "+ currentUser.getGallery().get(index).getName());
				Optional<ButtonType> result = alert.showAndWait();
				
				if (result.get() == ButtonType.OK){
					
					System.out.println("SUCCESS: DELETE THE ALBUM:  " + currentUser.getGallery().get(index).getName() );
					currentUser.deleteAlbum(index);
					
					displayList.remove(index);
					allAlbumName.remove(index);
					String path = "src/users/"+currentUser.getName()+"/gallery.txt";
					writeFile(path, allAlbumName);
					
				}else {
					
					System.out.println("FAIL: DELETE: CANCELED");
					return;
					
				}
				
			}
			
			
		}else {
			System.out.println("FAIL: DELETE: UNKNOWN");
		}
		
	}
	
	public void renameAlbum(ActionEvent e) throws IOException{
		Button b = (Button)e.getSource();
		
		if(b == rename) {
			
			int index = albumList.getSelectionModel().getSelectedIndex();
			
			if(index == -1) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Error occurs....");
				alert.setHeaderText("Rename Fails");
				alert.setContentText("Select A Valid Album");
				alert.showAndWait();
				
				System.out.println("FAIL: CANNOT RENAME THE ALBUM:  "+ index);
				return;
				
			}else {
				
				TextInputDialog dialog = new TextInputDialog("Beijing Tour");
				dialog.initOwner(this.mainStage); 
				dialog.setTitle("Rename an Album");
				dialog.setHeaderText("Give your Album a new name");
				dialog.setContentText("Enter name: ");
				Optional<String> newAlbumName = dialog.showAndWait();
				
				if(!newAlbumName.isPresent()) { //cancel the creation
					
					System.out.println("FAIL: CANNOT RENAME THE ALBUM: CANCELED");
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
				System.out.println("SUCCESS: RENAME THE ALBUM:  "+currentUser.getGallery().get(index).getName());
				
			}
		}else {
			System.out.println("FAIL: RENAME: UNKNOWN");
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
				
				System.out.println("FAIL: cannot open the album: "+ index);
				return;
				
			}else {
				//先选择到这个album
				currentAlbum = currentUser.getGallery().get(index);

				//说明currentAlbum已经是当前要打开的album：映射正确
				System.out.println("SUCCESS: catch the album: "+currentAlbum.getName());
				//需要扫描当前album, 获取当前album位置，扫描出所有照片
				//如果空的相册 不执行打开命令，弹出alert
				if(currentAlbum.getSize()==0) {
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("Error occurs....");
					alert.setHeaderText("Open Album Fails");
					alert.setContentText("Empty Album");
					alert.showAndWait();
				}
				//相册不为空，可以打开相册展示缩略图
				File file = new File("src/users/"+currentUser.getName()+"/"+currentAlbum.getName());
				File[] fs = file.listFiles();
				//initial();
				//preload();
				//displayAlbum();
				
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
			System.out.println("FAIL: UNKNOWN");
		}
	}

	/**
	 * this method add photo to current opining album
	 * @param e action event
	 * @throws IOException io expection
	 */
	public void addPhoto(ActionEvent e) throws IOException {
		String photoAdress = photoDirct.getText();
		File f = new File(photoAdress);
		//int index = albumList.getSelectionModel().getSelectedIndex();
		if (currentAlbum == null) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Error occurs....");
			alert.setHeaderText("Add photo FAILED");
			alert.setContentText("Album not selected");
			alert.showAndWait();
		} else {

			String destDirc = "src/users/" + currentUser.getName() + "/" + currentAlbum.getName()+"/"+f.getName();
			Path destPath = Paths.get(destDirc);
			if (f.isFile() && isPicture(f)) {
				try {
					Files.copy(f.toPath(), destPath);
					File destFilePointer = new File(destDirc);
					Image image = new Image(destFilePointer.toURI().toString());
					ImageView temp = new ImageView();
					temp.setImage(image);
					temp.setFitHeight(100);
					temp.setFitWidth(100);
					temp.setPreserveRatio(true);
					PictureFile pictureFile = new PictureFile(destFilePointer);
					currentAlbum.addPhoto(pictureFile);
					File info = new File("src/users/" + currentUser.getName() + "/" + currentAlbum.getName()+"/"+f.getName()+".txt");
					String date = pictureFile.getDate();
					long lastModified = pictureFile.getLastModifiedDate();
					FileWriter fw = new FileWriter(info,true);
					BufferedWriter out = new BufferedWriter(fw);
					out.write("NAME: "+f.getName()+"\n");
					out.write("DATE: "+date+"\n");
					out.write("LAST: "+lastModified+"\n");
					out.write("CAPTION: \n");
					out.write("TAGS: \n");
					out.close();

					//change the photo number in albumList
					displayAlbum();
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
	public void quit (ActionEvent e) throws IOException{
		Button b = (Button) e.getSource();
		
		if(b==quit) {
			String path = "src/users/currentUser.txt";
		    clearInfoForFile(path);
			Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
			stage.close();
			System.out.println("SUCCESS: QUIT");
			
		}else {
			System.out.println("FAIL: QUIT: UNKNOWN");
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
			 System.out.println("FAIL: LOGOUT: UNKNOWN");
		 }
	     
	}
	
	private void writeFile(String path, ArrayList<String> allalbumname) throws IOException {
		
		PrintWriter writer = new PrintWriter(path);
		writer.print("");
		writer.close();
		
		
		File fout = new File(path);
		FileOutputStream fos = new FileOutputStream(fout);
	 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	 
		for (int i = 0; i < allalbumname.size(); i++) {
			bw.write(allalbumname.get(i));
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
    	   fileName.toLowerCase().endsWith(".jpge")||
    	   fileName.toLowerCase().endsWith(".png")||
    	   fileName.toLowerCase().endsWith(".bmp")||
    	   fileName.toLowerCase().endsWith(".gif")	) {
    		return true;
    	}
    	
    	return false;
    }
    public void addCaption(ActionEvent e) throws IOException {
		String newCaption = captionAddField.getText();
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
		String tagString = tagNameString + " " + tagValueString;
		System.out.println("tagString: "+tagString);
		System.out.println("src/users/"+currentUserName+"/"+currentAlbum.getName()+"/"+currentPic.getName()+".txt");
		File fp = new File("src/users/"+currentUserName+"/"+currentAlbum.getName()+"/"+currentPic.getName()+".txt");
		File f = new File("src/users/"+currentUserName+"/"+currentAlbum.getName()+"/"+currentPic.getName());
		BufferedReader reader = new BufferedReader(new FileReader("src/users/"+currentUserName+"/"+currentAlbum.getName()+"/"+currentPic.getName()+".txt"));
		String currentLine;
		boolean ifExist = false;
		//check if tag exist
		while((currentLine = reader.readLine())!=null){
			if(currentLine.equals(tagString)){
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText("add tag faild");
				alert.setContentText("tag already exist");
				alert.showAndWait();
				ifExist = true;
				break;
			}
		}
		if(!ifExist){
			//tag is not duplicated, add to .txt file
			FileWriter fw = new FileWriter(fp,true);
			BufferedWriter out = new BufferedWriter(fw);
			out.write(tagString+"\n");
			out.close();
			fw.close();
			//update pictureFile
			//Tags tag = new Tags(tagNameString,tagValueString);
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
		}
		else{
			return;
		}

	}
	public void printTags(PictureFile pf){
		for(Tags tag:pf.getTags()){
			System.out.println(tag.getName()+"  "+tag.getValue());
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
		System.out.println(tagNum);


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

			Tags tag = tagTable.getSelectionModel().getSelectedItem();
			String tagString = tag.getName()+" "+tag.getValue();
			System.out.println(tagString);

			currentPic.removeTags(tag.getName(),tag.getValue());
			System.out.println(currentPic.getName());
			displayPhoto(currentPic);
			String currentLine;
			File inputFile = new File("src/users/"+currentUserName+"/"+currentAlbum.getName()+"/"+currentPic.getName()+".txt");
			File tempFile =  new File("src/users/"+currentUserName+"/"+currentAlbum.getName()+"/"+"temp.txt");
			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

			while((currentLine = reader.readLine()) != null) {
				// trim newline when comparing with lineToRemove
				String trimmedLine = currentLine.trim();
				if(trimmedLine.equals(tagString)) continue;
				writer.write(currentLine + System.getProperty("line.separator"));
			}
			writer.close();
			reader.close();
			tempFile.renameTo(inputFile);
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
		//String path = "src/users/"+currentUser.getName()+"/"+currentAlbum.getName()+"/"+currentPic.getName();
		//System.out.println(path);
		//preview.getChildren().remove(new ImageView(new Image(new FileInputStream(path))));
		preview.getChildren().remove(selectedPicture);
		String name = currentPic.getName();
		File file = currentPic.getImageFile();
		file.delete();
		String text = currentPic.getImageFile().getPath()+".txt";
		File textFile = new File(text);
		currentAlbum.getPhotoCollection().remove(currentPic);
		textFile.delete();
		display.setImage(null);
		displayAlbum();

	}





}
