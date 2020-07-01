
package phonebook;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;


public class ViewController implements Initializable {
    
//<editor-fold defaultstate="collapsed" desc="FXML items">
    @FXML
    TableView table;
    @FXML
    TextField inputLastName;
    @FXML
    TextField inputFirstName;
    @FXML
    TextField inputEmail;
    @FXML
    Button addNewContactButton;
    @FXML
    StackPane menuPane;
    @FXML
    Pane contactPane;
    @FXML
    Pane exportPane;
    @FXML
    TextField inputExportName;
    @FXML
    Button exportButton;
    @FXML
    SplitPane mainSplit;
    @FXML
    AnchorPane anchor;
//</editor-fold>
    
    
//<editor-fold defaultstate="collapsed" desc="class var">
    DB db = new DB();
    
    private final String MENU_CONTACTS = "Kontaktok";
    private final String MENU_LIST = "Lista";
    private final String MENU_EXPORT = "Export";
    private final String MENU_EXIT = "Kilépés";
//</editor-fold>
    
    
    private final ObservableList<Person> data = FXCollections.observableArrayList();
    
    //eseménykezelő létrehozása I.mód - összekötöm az FXML-lel
    //új kontakt hozzáadása
    @FXML
    private void addContact(ActionEvent event){
        String email = inputEmail.getText();
        
        if(email.length() > 3 && email.contains("@") && email.contains(".")){ //email cím ellenőrzése
            Person newPerson = new Person(inputFirstName.getText(), inputLastName.getText(), email);
            data.add(newPerson); //adatok hozzáadása a táblázathoz
            db.addContact(newPerson); //adatbázishoz is hozzáadjuk
        
            //input mezők törlése
            inputFirstName.clear();
            inputLastName.clear();
            inputEmail.clear();
        }else{
            alert("Adj meg egy valódi e-mail címet!");
        }
    }
    
    //PDF-be exportálás gomb
    @FXML
    private void exportList(ActionEvent event){
        //megvizsgáljuk, h írt-e fájlnevet a felhasználó
        String fileName = inputExportName.getText();
        fileName = fileName.replaceAll("\\s+",""); //a white space-eket kicserélem a semmire
        if (fileName != null && !fileName.equals("")){
            PdfGeneration pdfCreator = new PdfGeneration();
            pdfCreator.pdfGeneration(fileName, data);
        }else{
            alert("Adj meg egy fájl nevet!");
        }
        
        inputExportName.clear();
    }
    
    public void setTableData(){
        //dinamikusan létrehozunk táblázat oszlopokat (tableColumn)
        //first column
        TableColumn lastNameCol = new TableColumn("Vezetéknév"); //oszlop neve
        lastNameCol.setMinWidth(130); //minimum szélesség beállítása, 100px
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn()); //minden sorban ez a cella TextField legyen
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("lastName"));
        /*ha azt szeretnénk, h figyeljen milyen változás történt a cellábn
létre kell hoznunk egy eseménykezelőt (ami képes figyelni egy megadott dologra és 
meg lehet mondani, h mi történjen ha az adott dolog bekövetkezik) */
        //commit - valamit elküldök, jóváhagyok
        lastNameCol.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Person, String> t){
                    Person actualPerson = (Person) t.getTableView().getItems().get(t.getTablePosition().getRow()); //adott Person
                    actualPerson.setLastName(t.getNewValue()); //adott Person módósítás
                    db.updateContact(actualPerson); //átadom a módosítást az adatbázisnak
                }
            }
        );
        
        
        //second column
        TableColumn firstNameCol = new TableColumn("Keresztnév");
        firstNameCol.setMinWidth(130);
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Person, String>("firstName"));
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        
        firstNameCol.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Person, String> t){
                    Person actualPerson = (Person) t.getTableView().getItems().get(t.getTablePosition().getRow()); //adott Person
                    actualPerson.setFirstName(t.getNewValue()); //adott Person módósítás
                    db.updateContact(actualPerson); //átadom a módosítást az adatbázisnak
                }
            }
        );
        
        //third column
        TableColumn emailCol = new TableColumn("Email cím");
        emailCol.setMinWidth(250);
        emailCol.setCellValueFactory(new PropertyValueFactory<Person, String>("email"));
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        
        emailCol.setOnEditCommit(
            new EventHandler<TableColumn.CellEditEvent<Person, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<Person, String> t){
                    Person actualPerson = (Person) t.getTableView().getItems().get(t.getTablePosition().getRow()); //adott Person
                    actualPerson.setEmail(t.getNewValue()); //adott Person módósítás
                    db.updateContact(actualPerson); //átadom a módosítást az adatbázisnak
                }
            }
        );
        
        //törlés oszlop
        TableColumn removeCol = new TableColumn("Törlés");
        emailCol.setMinWidth(100);
        
        Callback<TableColumn<Person, String>, TableCell<Person, String>> cellFactory =
                new Callback<TableColumn<Person, String>, TableCell<Person, String>>()
                {
                    @Override
                    public TableCell call (final TableColumn<Person, String> param)
                    {
                        final TableCell<Person, String> cell = new TableCell<Person, String>()
                        {
                            final Button btn = new Button("Törlés");

                            @Override
                            public void updateItem(String item, boolean empty)
                            {
                                super.updateItem(item, empty);
                                if(empty){
                                    setGraphic(null);
                                    setText(null);
                                }else{
                                    btn.setOnAction( (ActionEvent event) ->
                                        {
                                            Person person = getTableView().getItems().get(getIndex() );
                                            data.remove(person); //a datából törli
                                            db.removeContact(person); //az adatbázisból törli
                                        });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        
        removeCol.setCellFactory(cellFactory);
        
        
        table.getColumns().addAll(lastNameCol, firstNameCol, emailCol, removeCol); //táblázat létrehozása
        data.addAll(db.getAllContacts());
        table.setItems(data); //adatok átadása
        
    }
    
    private void setMenuData() {
        TreeItem<String> treeItemRoot1 = new TreeItem<>("Menü");
        TreeView<String> treeView = new TreeView<>(treeItemRoot1); //fa struktúrát hoz létre. Ez a fő ág
        treeView.setShowRoot(false); //ne mutasd a fő ágat
        
        TreeItem<String> nodeItemA = new TreeItem<>(MENU_CONTACTS);
        TreeItem<String> nodeItemB = new TreeItem<>(MENU_EXIT);
        
        nodeItemA.setExpanded(true); //alapból ki van nyitva a menü
        
        /*kép hozzáadása a projekthez: src(=source=forrás) mappába, és oldalt látszik a projektnél
        ez biztonságosabb mintha netről szednénk le*/
        //kép átadása
        Node contactNode = new ImageView(new Image(getClass().getResourceAsStream("/contacts.png")));
        Node exportNode = new ImageView(new Image(getClass().getResourceAsStream("/export.png")));
        
        TreeItem<String> nodeItemA1 = new TreeItem<>(MENU_LIST, contactNode);
        TreeItem<String> nodeItemA2 = new TreeItem<>(MENU_EXPORT, exportNode);
        
        nodeItemA.getChildren().addAll(nodeItemA1, nodeItemA2);
        treeItemRoot1.getChildren().addAll(nodeItemA, nodeItemB); //hozzákapcsolás a menühöz (gyökérhez)
        
        menuPane.getChildren().add(treeView); //hozzáadom a Pane-hez
        
        //egy kattra is kinyiljon/becsukódjon a menü
        treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener(){
            public void changed(ObservableValue observable, Object oldValue, Object newValue){
                //kimentjük a menü nevét, h mire kattintott rá a felhasználó
                TreeItem<String> selectedItem = (TreeItem<String>) newValue;
                String selectedMenu;
                selectedMenu = selectedItem.getValue();
                
                if(null != selectedMenu){ 
                    switch (selectedMenu){
                        case MENU_CONTACTS:
                            selectedItem.setExpanded(true);
                            break;
                        case MENU_LIST:
                            contactPane.setVisible(true);
                            exportPane.setVisible(false);
                            break;
                        case MENU_EXPORT:
                            exportPane.setVisible(true);
                            contactPane.setVisible(false);
                            break;
                        case MENU_EXIT:
                            System.exit(0); //bezárja a programot
                            break;
                    }
                }
            }
        });
    }
        
    //hibaüzenetek
    public void alert(String text){
        mainSplit.setDisable(true);
        mainSplit.setOpacity(0.4);
        
        Label label = new Label(text);
        Button alertButton = new Button("OK");
        VBox vbox = new VBox(label, alertButton); //vertikális box létrehozása
        vbox.setAlignment(Pos.CENTER); //a vboxon belül középre zár
        
        //eseménykezelő létrehozása II. mód
        alertButton.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent e){
               mainSplit.setDisable(false);
               mainSplit.setOpacity(1);
               vbox.setVisible(false);
           }
        });
        
        anchor.getChildren().add(vbox);
        anchor.setTopAnchor(vbox, 300.0);
        anchor.setLeftAnchor(vbox, 300.0);
        

        
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setTableData();
        setMenuData();
        
    }    




    
}
