package notepad;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Notepad extends Application {

    Settings applicationSettings;
    Scene scene;
    TextArea textArea;
    File filename = new File("data.json");
    
    @Override
    public void start(Stage primaryStage) {
        
        if(filename.exists()){
            primaryStage.setTitle("Text Editor");//title of the editor
            textArea = new TextArea();//creating a new text area names textArea
            openFile();
            textArea.setText(applicationSettings.getText());//adding the prompt text to the text area
            scene = new Scene(textArea, applicationSettings.getWidth(), applicationSettings.getHeight());//setting the dimensions of the are
            
            primaryStage.setScene(scene);
            primaryStage.show();//displaying it all
        }else{
            primaryStage.setTitle("Text Editor");//title of the editor
            textArea = new TextArea();//creating a new text area names textArea
            textArea.setPromptText("Type here...");//adding the prompt text to the text area
            scene = new Scene(textArea, 300, 250);//setting the dimensions of the initial area
            primaryStage.setScene(scene);
            primaryStage.show();//displaying it all
        }
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
        @Override
        public void handle(WindowEvent w){
            saveState();
        }
        });
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    public void openFile(){
        Gson gson = new Gson();
        try(FileReader fr = new FileReader("Data.json")){
            BufferedReader br = new BufferedReader(fr);
            applicationSettings = gson.fromJson(br.readLine(), Settings.class);
        }catch(IOException e){
            System.out.println(e);
        }
    }
    
    public void saveState(){
        applicationSettings = new Settings();
        applicationSettings.setHeight(scene.getHeight());
        applicationSettings.setWidth(scene.getWidth());
        applicationSettings.setText(textArea.getText());
        Gson gson = new Gson();
        String file = gson.toJson(applicationSettings);
        try(FileWriter fw = new FileWriter(filename)){
            fw.write(file);
            fw.close();
        }catch(IOException e){
            System.out.println(e);
        }
    }
}
