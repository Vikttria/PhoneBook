package phonebook;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DB {
    
    final String URL = "jdbc:derby:sampleDB;create=true";
    final String USERNAME ="";
    final String PASSWORD = "";
    
    Connection conn = null;
    Statement createStatement = null;
    DatabaseMetaData dbmd = null;
    
    //adatbázis létrehozása, ha nem létezik
    public DB(){
        //megpróbáljuk életre kelteni
        try {
           conn  = DriverManager.getConnection(URL);
           System.out.println("A híd létrejött");
        } catch (SQLException ex) {
            System.out.println("Valami baj van a connection (hid) létrehozásakor");
            System.out.println("" + ex);
        }
        
        //ha életre kelt, csinálunk egy megpakolható teherautót
        if(conn != null){
            try {
                createStatement = conn.createStatement();
            } catch (SQLException ex) {
                System.out.println("Valami baj van a createStatement (teherautó) létrehozásakor");
                System.out.println("" + ex);
            }
        }
        
        //megnézzük, hogy üres-e az adatbázis? megnézzük, hogy létezik-e az adatbázis
        try {
            dbmd = conn.getMetaData();
        } catch (SQLException ex) {
            System.out.println("Valami baj van a DatabaseMetaData (adatbázis leírása) létrehozásakor");
            System.out.println("" + ex);
        }
        
        try {
            ResultSet rs1 = dbmd.getTables(null, "APP", "CONTACTS", null);
            if(!rs1.next()){
                createStatement.execute("create table contacts(id INT not null primary key GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), lastName varchar(20), firstName varchar(20), email varchar(30))");
            }
        } catch (SQLException ex) {
            System.out.println("Valami baj van az adattáblák létrehozásakor");
            System.out.println("" + ex);
        }
    }
    
    public ArrayList<Person> getAllContacts(){
        String sql = "select * from contacts";
        ArrayList<Person> persons = null;
        try {
            ResultSet rs = createStatement.executeQuery(sql);
            persons = new ArrayList<>(); 
            
            while(rs.next()){
                Person actualPerson = new Person(rs.getInt("id"), rs.getString("lastName"), rs.getString("firstName"), rs.getString("email"));
                persons.add(actualPerson);
            }
        } catch (SQLException ex) {
            System.out.println("Valami baj van a contaktok kiolvasásakor");
            System.out.println("" + ex);
        }
        
        return persons;
    }
    
    //új felhasználó hozzáadása
    public void addContact(Person person){
        try {
            String sql = "insert into contacts (lastName, firstName, email) values (?,?,?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, person.getLastName());
            preparedStatement.setString(2, person.getFirstName());
            preparedStatement.setString(3, person.getEmail());
            preparedStatement.execute();
        } catch (SQLException ex) {
            System.out.println("Valami baj van a contact hozzáadásakor");
            System.out.println("" + ex);
        }
    }
    
    //felhasználó módosítása
    public void updateContact(Person person){
        try {
            String sql = "update contacts set lastName = ?, firstName = ?, email = ? where id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, person.getLastName());
            preparedStatement.setString(2, person.getFirstName());
            preparedStatement.setString(3, person.getEmail());
            preparedStatement.setInt(4, Integer.parseInt(person.getId()));
            preparedStatement.execute();
        } catch (SQLException ex) {
            System.out.println("Valami baj van a contact módosításakor");
            System.out.println("" + ex);
        }
    }
    
    //felhasználó törlése
    public void removeContact(Person person){
        try {
            String sql = "delete from contacts where id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, Integer.parseInt(person.getId()));
            preparedStatement.execute();
        } catch (SQLException ex) {
            System.out.println("Valami baj van a contact törlésekor");
            System.out.println("" + ex);
        }
    }
    
}
