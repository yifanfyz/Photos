package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class loginControl {
	
    @FXML
    private TextField textField;
    @FXML
    private Button loginButton;
    @FXML
    private Button quitButton;

    
   public void login(ActionEvent event) throws IOException {
       
       if(textField.getText().equals("admin")){
           Parent view = FXMLLoader.load(getClass().getResource("../view/adminWindow.fxml"));
           Scene tableView = new Scene(view);
           Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
           window.setScene(tableView);
           window.setTitle("Admin");
           window.show();
       }
       else{
           System.out.println(getClass());

           String username = textField.getText();
           boolean exists = false;
           File inputFile = new File("src/users/nameList.txt");
           BufferedReader reader = new BufferedReader(new FileReader(inputFile));
           String currentLine;

           while((currentLine = reader.readLine()) != null) {
               // trim newline when comparing with lineToRemove
               String trimmedLine = currentLine.trim();
               if(trimmedLine.equals(username)){
                   exists = true;
                   break;
               }

           }
           reader.close();
           
           if(!exists){
               Alert a = new Alert(Alert.AlertType.ERROR);
               a.setContentText("User not exists, please add user first");
               a.show();
           }
           else {
        	   Stage stage = new Stage();
               FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/userWindow.fxml"));
               
               String path = "src/users/currentUser.txt";
               writeFile(path, username);
               Parent parent = loader.load();
               Scene scene = new Scene(parent,1400,730);
               stage.setScene(scene);
               stage.setTitle("PrivateApp");
               stage.show();
               Stage loginStage = (Stage) ((Node)event.getSource()).getScene().getWindow();
               loginStage.close();
         
           }
       }

   }
   
   public void handleCloseButtonAction(javafx.event.ActionEvent event) {
	   String path = "src/users/currentUser.txt";
       adminController.clearInfoForFile(path);
       Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
       stage.close();
    }
    
   private void writeFile(String path, String username) throws IOException {
		
		PrintWriter writer = new PrintWriter(path);
		writer.print("");
		writer.close();
		
		
		File fout = new File(path);
		FileOutputStream fos = new FileOutputStream(fout);
	 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	 
		
		bw.write(username);
		//bw.newLine();
		
	 
		bw.close();
		
	}
    


}
