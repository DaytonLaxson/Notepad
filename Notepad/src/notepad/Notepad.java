package notepad;

import com.google.gson.Gson;
import javafx.scene.image.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import static javafx.application.Application.launch;

public class Notepad extends Application {

    Settings applicationSettings;
    File savedFile;
    Stage primaryStage;
    Scene scene;
    TextArea textArea;
    MenuBar menuBar;
    ToolBar toolBar;
    VBox vbox;
    VBox statusBox;
    Stage secondaryStage;
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    BorderPane root = new BorderPane();
    Font oldFont;
    ListView<String> lV_font_styles;
    ListView<String> lV_font_choices;
    ListView<Double> lV_font_size;
    TextField font_search;
    TextField font_style_search;
    TextField font_size_search;
    Text font_sample;
    Text statusText;
    int index;
    ObservableList<String> font_styles;
    TextInputControl TIC;
    
    
    @Override
    public void start(Stage primaryStage) {
        //add the text editor file name as the name as stage
        //change the icon of the stage
        this.primaryStage = primaryStage;
        createMenuBar();//created the menu bar with corresponding drop down menues and the items
        createToolBar();
        createEditor();
        createVbox();
        addCSS(scene);
        TIC = (TextInputControl) textArea;
        actionStatusBox();
        root.setTop(vbox);
        root.setCenter(textArea);
        
        primaryStage.setTitle("Lab 11 editor 4");//title of the editor       
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("editor_icon.png"), 40, 40, true, true));
        primaryStage.setScene(scene);
        primaryStage.show();//displaying it all
        
        TIC.caretPositionProperty().addListener((ob, old1, new1) -> {
            actionStatusBox();
        });
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>(){
        @Override
        public void handle(WindowEvent w){
            //error when closing new editor and changing the text area
            //it needs to ask to save if they have changed the text area!!
            if( (savedFile == null && textArea.getText().equals("")) || (applicationSettings != null && textArea.getText().equals(applicationSettings.getText()))){
                primaryStage.close();
            }else{
                w.consume();
                saveOnClose();
            }
        }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public void createEditor(){
            textArea = new TextArea();//creating a new text area names textArea
            textArea.setPromptText("Type here...");//adding the prompt text to the text area
            textArea.setFocusTraversable(false);
            oldFont = textArea.getFont();
            //applicationSettings.setText("");
            scene = new Scene(root, 700, 500);//setting the dimensions of the initial area
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
        
        fileSave(item_save);
        fileOpen(item_open);
        fileSaveAs(item_saveas);
        fileExit(item_exit);
  
        menu_file.getItems().addAll(item_new, item_open, item_save, item_saveas,new SeparatorMenuItem(),item_print,
        item_printsetup, new SeparatorMenuItem(), item_exit);
        
        return menu_file;
    } 
    public void fileSave(MenuItem item_save){
        item_save.setOnAction(new EventHandler<ActionEvent>(){ 
           @Override
           public void handle(ActionEvent event){
               actionSave();
           }
        });
    }
    public void fileOpen(MenuItem item_open){
        item_open.setOnAction(new EventHandler<ActionEvent>(){ 
           @Override
           public void handle(ActionEvent event){
               actionOpen();
           }
        });
    }
    public void fileSaveAs(MenuItem item_saveas){
        item_saveas.setOnAction(new EventHandler<ActionEvent>(){ 
           @Override
           public void handle(ActionEvent event){
               actionSaveAs();
           }
        });
    }
    public void fileExit(MenuItem item_exit){
        item_exit.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent event){
                System.exit(0);
            }
        });
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
       
       createEditCut(item_cut);
       createEditCopy(item_copy);
       createEditPaste(item_paste);
       createEditDelete(item_delete);
       
       menu_edit.getItems().addAll(item_undo, new SeparatorMenuItem(), 
               item_cut, item_copy, item_paste, item_delete, new SeparatorMenuItem(),
               item_find, item_findnext, item_replace, item_goto, new SeparatorMenuItem(),
               item_selectall, item_date);
       return menu_edit;
    }
    public void createEditCut(MenuItem item_cut){
        item_cut.setOnAction(new EventHandler<ActionEvent>(){ 
           @Override
           public void handle(ActionEvent event){
               actionCut();
           }
        });
    }
    public void createEditCopy(MenuItem item_copy){
        item_copy.setOnAction(new EventHandler<ActionEvent>(){ 
           @Override
           public void handle(ActionEvent event){
               actionCopy();
           }
        });
    }
    public void createEditPaste(MenuItem item_paste){
        item_paste.setOnAction(new EventHandler<ActionEvent>(){ 
           @Override
           public void handle(ActionEvent event){
                actionPaste();
           }
        });
    }
    public void createEditDelete(MenuItem item_delete){
        item_delete.setOnAction(new EventHandler<ActionEvent>(){ 
           @Override
           public void handle(ActionEvent event){
                actionDelete();
           }
        });
    }
    
    public Menu createFormat_menu(){
        Menu menu_format = new Menu("F_ormat");
        MenuItem item_wordwrap = new MenuItem("_Word Wrap");
        MenuItem item_font = new MenuItem("_Font...");
        menu_format.getItems().addAll(item_wordwrap, item_font);
        
        createFormatFont(item_font);
        
        return menu_format;
    }
    
    public void createFormatFont(MenuItem item_font){
        item_font.setOnAction(new EventHandler<ActionEvent>(){ 
           @Override
           public void handle(ActionEvent event){
               fontDialog();
           }
        });
    }
    public void fontDialog(){
        //create the dialog which returns fontResults and updates the styles list acording to family
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Font");
        DialogPane dialogPane = dialog.getDialogPane();
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("editor_icon.png"), 40, 40, true, true));
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        createDialog(dialogPane);//add the list views to the dialog
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                actionChangeFont(lV_font_styles.getSelectionModel().getSelectedItem(), lV_font_size.getSelectionModel().getSelectedItem());
                index = lV_font_styles.getSelectionModel().getSelectedIndex();
            }
            return null;
        });
        lV_font_choices.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, 
                    String old_val, String new_val) {
                        font_styles = FXCollections.observableArrayList(javafx.scene.text.Font.getFontNames(new_val));
                        lV_font_styles.setItems(font_styles);
                        lV_font_styles.getSelectionModel().select(0);
                        lV_font_styles.scrollTo(0);
                        stage.show();
            }
        });
        lV_font_styles.getSelectionModel().selectedItemProperty().addListener(
            new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> ov, 
                    String old_val, String new_val) {
                        Font ft = new Font(new_val, 12);
                        font_sample.setFont(ft);
                        stage.show();
            }
        });
        dialog.showAndWait();
    }
    public void createDialog(DialogPane dialogPane){
        ObservableList<String> font_choices = FXCollections.observableArrayList(javafx.scene.text.Font.getFamilies());
        lV_font_choices = new ListView<>(font_choices);
        lV_font_choices.maxWidth(25);
        lV_font_choices.getSelectionModel().select(font_choices.indexOf(oldFont.getFamily()));
        lV_font_choices.getFocusModel().focus(font_choices.indexOf(oldFont.getFamily()));
        lV_font_choices.scrollTo(font_choices.indexOf(oldFont.getFamily()));
        
        font_styles = FXCollections.observableArrayList(javafx.scene.text.Font.getFontNames(oldFont.getFamily()));
        lV_font_styles = new ListView<>(font_styles);
        lV_font_styles.getSelectionModel().select(index);
        lV_font_styles.scrollTo(index);
        
        List<Double> range =  new ArrayList<Double>();
        for(int i =1; i <500;i++){range.add((double)i);}
        ObservableList<Double> font_size = FXCollections.observableArrayList(range);
        lV_font_size = new ListView<>(font_size);
        lV_font_size.getSelectionModel().select(font_size.indexOf(oldFont.getSize()));
        lV_font_size.getFocusModel().focus(font_size.indexOf(oldFont.getSize()));
        lV_font_size.scrollTo(font_size.indexOf(oldFont.getSize()));
        
        GridPane gp = createGridPane(lV_font_choices, lV_font_styles, lV_font_size);
        //creating gridpane for the layout of the dialog pane
        font_search.textProperty().addListener(((observable, oldValue, newValue) -> {
            searchFontFamily(newValue.toLowerCase(), font_choices);
            
        }));
        font_style_search.textProperty().addListener(((observable, oldValue, newValue) -> {
            searchFontStyle(newValue.toLowerCase());
            
        }));
        font_size_search.textProperty().addListener(((observable, oldValue, newValue) -> {
            searchFontSize(newValue, font_size);
            
        }));
        dialogPane.setContent(gp);
    }
    public void searchFontFamily(String newValue, ObservableList<String> font_choices){
        boolean stop = false;
            for(int i = 0; i < font_choices.size();i++){
                if(font_choices.get(i).toLowerCase().contains(newValue)){
                    for(int j = 0; j < newValue.length(); j++){
                        if(font_choices.get(i).toLowerCase().charAt(j) == newValue.charAt(j)){
                            lV_font_choices.getSelectionModel().select(i);
                            lV_font_choices.getFocusModel().focus(i);
                            lV_font_choices.scrollTo(i);
                            stop = true;
                        }
                        break;
                    }
                    if(stop){break;}
                }
            }
    }
    public void searchFontStyle(String newValue){
        boolean stop = false;
            for(int i = 0; i < font_styles.size();i++){
                if(font_styles.get(i).toLowerCase().contains(newValue)){
                    for(int j = 0; j < newValue.length(); j++){
                        if(font_styles.get(i).toLowerCase().charAt(j) == newValue.toLowerCase().charAt(j)){
                            lV_font_styles.getSelectionModel().select(i);
                            lV_font_styles.getFocusModel().focus(i);
                            lV_font_styles.scrollTo(i);
                            stop = true;
                        }
                        break;
                    }
                    if(stop){break;}
                }
            }
    }
    public void searchFontSize(String newValue, ObservableList<Double> font_size){
        try{
            double entry = Double.parseDouble(newValue);
            lV_font_size.getSelectionModel().select(font_size.indexOf(entry));
            lV_font_size.getFocusModel().focus(font_size.indexOf(entry));
            lV_font_size.scrollTo(font_size.indexOf(entry));
        }catch(NumberFormatException e){
            
        }
    }
    public GridPane createGridPane(ListView<String> lV_font_choices, ListView<String> lV_font_styles, ListView<Double> lV_font_size){
        GridPane gp = new GridPane();
        
        font_search = new TextField();
        VBox font = new VBox(new Text("Font:"), font_search);
        gp.add(font, 0, 0, 1, 1);
        gp.add(lV_font_choices, 0, 1, 1, 1);
        
        font_style_search = new TextField();
        VBox font_style = new VBox(new Text("Font Style:"), font_style_search);
        gp.add(font_style, 1, 0, 1, 1);
        gp.add(lV_font_styles, 1, 1, 1, 1);
        
        font_size_search = new TextField();
        VBox font_size = new VBox(new Text("Font size:"), font_size_search);
        gp.add(font_size, 2, 0, 1, 1);
        gp.add(lV_font_size, 2, 1, 1, 1);
        
        font_sample= new Text ("AaBbCcDd");
        Font ft = new Font(oldFont.getName(),12);
        font_sample.setFont(ft);
        VBox sample = new VBox(font_sample);
        sample.setAlignment(Pos.CENTER);
        sample.setMinSize(25, 25);
        sample.setStyle("-fx-border-color: black;");
        gp.add(sample ,2 ,2 ,1 ,1);
 
        gp.setHgap(15);
        gp.setVgap(2);
        return gp;
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
    
    public void createToolBar(){
        Button save = new Button();
        save.setMaxSize(50, 50);
        save.setTooltip(new Tooltip("Save"));
        
        Button open = new Button();
        open.setMaxSize(50, 50);
        open.setTooltip(new Tooltip("open"));
        
        Button cut = new Button();
        cut.setMaxSize(50, 50);
        cut.setTooltip(new Tooltip("cut"));
        
        Button copy = new Button();
        copy.setMaxSize(50, 50);
        copy.setTooltip(new Tooltip("copy"));
        
        Button paste = new Button();
        paste.setMaxSize(50, 50);
        paste.setTooltip(new Tooltip("paste"));
        
        saveButton(save);
        openButton(open);
        cutButton(cut);
        copyButton(copy);
        pasteButton(paste);
        toolBar = new ToolBar(save, open, new Separator(), cut, copy, paste);
    }
    public void saveButton(Button save){
        Image img_save = new Image(getClass().getResourceAsStream("save_icon.png"), 15, 15, true, true);
        save.setGraphic(new ImageView(img_save));
        shadowOnOff(save);
        save.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                actionSave();
            }
        });
    }
    public void openButton(Button open){
        Image img_save = new Image(getClass().getResourceAsStream("open_icon.png"), 15, 15, true, true);
        open.setGraphic(new ImageView(img_save));
        shadowOnOff(open);
        open.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                actionOpen();
            }
        });
    }
    public void cutButton(Button cut){
        Image img_save = new Image(getClass().getResourceAsStream("cut_icon.png"), 15, 15, true, true);
        cut.setGraphic(new ImageView(img_save));
        shadowOnOff(cut);
        cut.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                actionCut();
            }
        });
    }
    public void copyButton(Button copy){
        Image img_save = new Image(getClass().getResourceAsStream("copy_icon.png"), 15, 15, true, true);
        copy.setGraphic(new ImageView(img_save));
        shadowOnOff(copy);
        copy.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                actionCopy();
            }
        });
    }
    public void pasteButton(Button paste){
        Image img_save = new Image(getClass().getResourceAsStream("paste_icon.png"), 15, 15, true, true);
        paste.setGraphic(new ImageView(img_save));
        shadowOnOff(paste);
        paste.setOnAction(new EventHandler<ActionEvent>() {
        
            @Override
            public void handle(ActionEvent event) {
                actionPaste();
            }
        });
    } 
    
    public void shadowOnOff(Button but){
        DropShadow shadow = new DropShadow();
        //Adding the shadow when the mouse cursor is on
        but.addEventHandler(MouseEvent.MOUSE_ENTERED, 
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    but.setEffect(shadow);
                }
        });
        //Removing the shadow when the mouse cursor is off
        but.addEventHandler(MouseEvent.MOUSE_EXITED, 
            new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent e) {
                    but.setEffect(null);
                }
        });
    }
    
    public void createVbox(){
        vbox = new VBox();
        vbox.getChildren().add(menuBar);
        vbox.getChildren().add(toolBar);
    }
    public void openNew_file(File file) {
        Gson gson = new Gson();
        try(FileReader fr = new FileReader(file)){
            BufferedReader br = new BufferedReader(fr);
            applicationSettings = gson.fromJson(br.readLine(), Settings.class);
            textArea.setText(applicationSettings.getText());
            primaryStage.setHeight(applicationSettings.getHeight());
            primaryStage.setWidth(applicationSettings.getWidth());
            actionStatusBox();
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
    
    public void saveOnClose(){
        secondaryStage = new Stage();
        Button btn_yes = new Button();
        btn_yes.setStyle("-fx-background-color: #7ca7ef");
        btn_yes.setText("Yes");
        Button btn_no = new Button();
        btn_no.setStyle("-fx-background-color: #7ca7ef");
        btn_no.setText("No");
        Button btn_cancel = new Button();
        btn_cancel.setStyle("-fx-background-color: #7ca7ef");
        btn_cancel.setText("Cancel");
        btn_cancel.setMaxWidth(60);
        btn_no.setMaxWidth(60);
        btn_yes.setMaxWidth(60);
        
        StackPane rootClose = new StackPane();
        btn_yes.setTranslateX(-100);
        btn_cancel.setTranslateX(100);
        rootClose.getChildren().add(btn_yes);
        rootClose.getChildren().add(btn_no);
        rootClose.getChildren().add(btn_cancel);
        
        Scene sceneClose = new Scene(rootClose, 350, 100);
        addCSS(sceneClose);
        secondaryStage.setTitle("Do you want to save?");
        secondaryStage.setScene(sceneClose);
        secondaryStage.show();
        
        onCloseSaveYes(btn_yes);
        onCloseSaveNo(btn_no);
        onCloseSaveCancel(btn_cancel);
    }
    public void onCloseSaveYes(Button btn_yes){
        btn_yes.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                actionSave();
                secondaryStage.close();
                primaryStage.close();
            }
        });
    }
    public void onCloseSaveNo(Button btn_no){
        btn_no.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                secondaryStage.close();
                primaryStage.close();
            }
        });
    }
    public void onCloseSaveCancel(Button btn_cancel){
        btn_cancel.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                secondaryStage.close();
            }
        });
    }
    
    public void addCSS(Scene scene){
        scene.getStylesheets().add("button_styles.css");
        
    }
    
    public void actionSave(){
        try{
            if(savedFile != null){
                saveNew_File(textArea.getText(), savedFile);
            }else{
                actionSaveAs();
            }
        }catch(IOException e){
                 System.out.println(e);
             }
    }
    public void actionSaveAs(){
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Save As");
        File file = fileChooser.showSaveDialog(primaryStage);
        try{
            if(file != null){
                saveNew_File(textArea.getText(), file);
                savedFile=file;
                actionStatusBox();
             }
        } catch(IOException e){
            System.out.println(e);
        }
    }
    public void actionOpen(){
        FileChooser fileChooser = new FileChooser();
               fileChooser.setTitle("Open");
               File file = fileChooser.showOpenDialog(primaryStage);
               savedFile = file;
               if(file != null){
                   openNew_file(file);
               }
    }
    public void actionCopy(){
        content.putString(textArea.getSelectedText());
        clipboard.setContent(content);
    }
    public void actionCut(){
        content.putString(textArea.getSelectedText());
        clipboard.setContent(content);
        textArea.replaceSelection("");
        //delete selction from textarea?
    }
    public void actionPaste(){
        if (clipboard.hasString()) {
            textArea.replaceSelection(clipboard.getString());
            //replace selected text with string from clipbaord
        }
    }
    public void actionDelete(){
        textArea.replaceSelection("");
    }
    public void actionChangeFont(String style, double size){
        Font newFont = new Font(style, size);
        textArea.setFont(newFont);
        oldFont = newFont;
    }
    public void actionStatusBox(){
        DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter
				.ofPattern("yyyy/MM/dd HH:mm:ss z");
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        String formatter1 = dateTimeFormatter1.format(zonedDateTime);
        
        String statusfile;
        if(savedFile != null){
             statusfile = savedFile.getName();
        }else{
            statusfile = "";
        }
        
        int caret = TIC.getCaretPosition();
        String sel = TIC.getText(0, caret);
        String linesArray[] = sel.split("\\n", -1);
        int lineCount = linesArray.length;
        String last = linesArray[linesArray.length-1];
        int columnCount = last.length();
        
        statusBox = new VBox();
        statusText = new Text("Line: "+lineCount + "  Column: " + columnCount + "    File Name: " + 
                statusfile + "   Date and Time: " + formatter1);
        statusBox.getChildren().add(statusText);
        TIC.selectPositionCaret(caret);
        root.setBottom(statusBox);
    }

}
