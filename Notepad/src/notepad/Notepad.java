package notepad;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Notepad extends Application {

    Settings applicationSettings;
    File savedFile;
    Stage primaryStage;
    Scene scene;
    TextArea textArea;
    MenuBar menuBar;
    //File filename = new File("data.json");
    BorderPane root = new BorderPane();
    
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        createMenuBar();//created the menu bar with corresponding drop down menues and the items
        root.setTop(menuBar);
        createEditor();
        root.setCenter(textArea);
        primaryStage.setTitle("Lab 7 editor 3");//title of the editor          
        primaryStage.setScene(scene);
        primaryStage.show();//displaying it all
        /*
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
        @Override
        public void handle(WindowEvent w){
            saveState();
        }
        });
        */
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    public void createMenuBar(){
        //creates each menu with methods that create the items per drop down menu
        menuBar = new MenuBar();
        Menu File_menu = createFile_menu();//method that returns a file menu to be added to the menu bar
        Menu Edit_menu = createEdit_menu();//edit menu and so on
        Menu Format_menu = createFormat_menu();
        Menu View_menu = createView_menu();
        Menu Help_menu = createHelp_menu();
        
        menuBar.getMenus().addAll(File_menu, Edit_menu, Format_menu, View_menu, Help_menu);//adding each menu to the menuBAr
    }
    public Menu createFile_menu(){
        Menu menu_file = new Menu("_File");
        MenuItem item_new = new MenuItem("_New");
        MenuItem item_open = new MenuItem("_Open");
        MenuItem item_save = new MenuItem("_Save");
        MenuItem item_saveas = new MenuItem("Save _As");
        MenuItem item_print = new MenuItem("_Print");
        MenuItem item_printsetup = new MenuItem("Print Set_up");
        MenuItem item_exit = new MenuItem("E_xit");
        
        item_new.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        item_open.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        item_save.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));
        item_print.setAccelerator(KeyCombination.keyCombination("Ctrl+P"));
        
        
        item_open.setOnAction(new EventHandler<ActionEvent>(){ 
           @Override
           public void handle(ActionEvent event){
               FileChooser fileChooser = new FileChooser();
               fileChooser.setTitle("Open");
               File file = fileChooser.showOpenDialog(primaryStage);
               savedFile = file;
               if(file != null){
                   openNew_file(file);
               }
           }
        });
        item_save.setOnAction(new EventHandler<ActionEvent>(){ 
           @Override
           public void handle(ActionEvent event){
               if(savedFile != null){
                   try{
                        saveNew_File(textArea.getText(), savedFile);
                    } catch(IOException e){
                        System.out.println(e);
                    }
               }else{
                    FileChooser fileChooser = new FileChooser();
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
                    fileChooser.getExtensionFilters().add(extFilter);
                    fileChooser.setTitle("Save As");
                    File file = fileChooser.showSaveDialog(primaryStage);
                    savedFile = file;
                    try{
                        if(file != null){
                             saveNew_File(textArea.getText(), file);
                         }
                    } catch(IOException e){
                        System.out.println(e);
                    }
               }
           }
        });
        item_saveas.setOnAction(new EventHandler<ActionEvent>(){ 
           @Override
           public void handle(ActionEvent event){
               FileChooser fileChooser = new FileChooser();
               FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
               fileChooser.getExtensionFilters().add(extFilter);
               fileChooser.setTitle("Save As");
               File file = fileChooser.showSaveDialog(primaryStage);
               try{
                   if(file != null){
                        saveNew_File(textArea.getText(), file);
                    }
               } catch(IOException e){
                   System.out.println(e);
               }
           }
        });
        item_exit.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                System.exit(0);
            }
        });
        
        menu_file.getItems().addAll(item_new, item_open, item_save, item_saveas,new SeparatorMenuItem(),item_print,
        item_printsetup, new SeparatorMenuItem(), item_exit);
        
        return menu_file;
    }
    public Menu createEdit_menu(){
       Menu menu_edit = new Menu("_Edit");
       MenuItem item_undo = new MenuItem("_Undo");
       MenuItem item_cut = new MenuItem("Cu_t");
       MenuItem item_copy = new MenuItem("_Copy");       
       MenuItem item_paste = new MenuItem("_Paste");
       MenuItem item_delete = new MenuItem("De_lete");
       MenuItem item_find = new MenuItem("_Find...");
       MenuItem item_findnext = new MenuItem("Find _Next");
       MenuItem item_replace = new MenuItem("_Replace...");
       MenuItem item_goto = new MenuItem("_Go To...");
       MenuItem item_selectall = new MenuItem("Select _All");
       MenuItem item_date = new MenuItem("Time/_Date");
       
       //add edit item accelerators
       item_undo.setAccelerator(KeyCombination.keyCombination("Ctrl+Z"));
       item_cut.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
       item_copy.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
       item_paste.setAccelerator(KeyCombination.keyCombination("Ctrl+V"));
       item_delete.setAccelerator(KeyCombination.keyCombination("Del"));
       item_find.setAccelerator(KeyCombination.keyCombination("Ctrl+F"));
       item_findnext.setAccelerator(KeyCombination.keyCombination("F3"));
       item_replace.setAccelerator(KeyCombination.keyCombination("Ctrl+H"));
       item_goto.setAccelerator(KeyCombination.keyCombination("Ctrl+G"));
       item_selectall.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));
       item_date.setAccelerator(KeyCombination.keyCombination("F5"));
       
       menu_edit.getItems().addAll(item_undo, new SeparatorMenuItem(), 
               item_cut, item_copy, item_paste, item_delete, new SeparatorMenuItem(),
               item_find, item_findnext, item_replace, item_goto, new SeparatorMenuItem(),
               item_selectall, item_date);
       return menu_edit;
    }
    public Menu createFormat_menu(){
        Menu menu_format = new Menu("F_ormat");
        MenuItem item_wordwrap = new MenuItem("_Word Wrap");
        MenuItem item_font = new MenuItem("_Font...");
        menu_format.getItems().addAll(item_wordwrap, item_font);
        return menu_format;
    }
    public Menu createView_menu(){
        Menu menu_view = new Menu("_View");
        MenuItem item_status = new MenuItem("_Status Bar");
        menu_view.getItems().add(item_status);
        return menu_view;
    }
    public Menu createHelp_menu(){
        Menu menu_help = new Menu("_Help");
        MenuItem item_help = new MenuItem("View _Help");
        MenuItem item_about = new MenuItem("_About");
        menu_help.getItems().addAll(item_help, new SeparatorMenuItem(), item_about);
        return menu_help;
    }
    public void createEditor(){
         /*if(filename.exists()){
            textArea = new TextArea();//creating a new text area names textArea
            openFile();
            textArea.setText(applicationSettings.getText());
            scene = new Scene(root, applicationSettings.getWidth(), applicationSettings.getHeight());//setting the dimensions of the area
        }else{*/
            textArea = new TextArea();//creating a new text area names textArea
            textArea.setPromptText("Type here...");//adding the prompt text to the text area
            textArea.setFocusTraversable(false);
            scene = new Scene(root, 700, 500);//setting the dimensions of the initial area
        //}
    }
    public void openNew_file(File file) {
        Gson gson = new Gson();
        try(FileReader fr = new FileReader(file)){
            BufferedReader br = new BufferedReader(fr);
            applicationSettings = gson.fromJson(br.readLine(), Settings.class);
            textArea.setText(applicationSettings.getText());
            primaryStage.setHeight(applicationSettings.getHeight());
            primaryStage.setWidth(applicationSettings.getWidth());
            
        }catch(IOException e){
            System.out.println(e);
        }
    }
    public void saveNew_File(String contents, File file) throws IOException{
        applicationSettings = new Settings();
        applicationSettings.setHeight(scene.getHeight());
        applicationSettings.setWidth(scene.getWidth());
        applicationSettings.setText(textArea.getText());
        Gson gson = new Gson();
        String new_file = gson.toJson(applicationSettings);
        try(FileWriter fw = new FileWriter(file)){
            fw.write(new_file);
            fw.close();
        }catch(IOException e){
            System.out.println(e);
        }
        /*FileWriter writer = new FileWriter(file);
        writer.write(contents);
        writer.close();
        */
    }
        /*
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
    }*/
}
