package phonebook;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.FileOutputStream;
import javafx.collections.ObservableList;
import javax.swing.text.StyleConstants;


public class PdfGeneration {
    
    public void pdfGeneration(String fileName, ObservableList<Person> data) {
       Document document = new Document();
       
       try{
           PdfWriter.getInstance(document, new FileOutputStream(fileName + ".pdf")); //documentum készítésem fájl létrehozása
           document.open(); //documentum megnyitása
           
           //kép hozzáadása a dokumentumhoz
           Image image1 = Image.getInstance(getClass().getResource("/logo.jpg"));
           image1.scaleToFit(150, 56); //kép méretezése
           image1.setAbsolutePosition(280f, 750f); //kép pozició beállítás
           document.add(image1); //hozzáadás a dokumentumhoz
           
           /*új paragrafus hozzáadása a dokumentumhoz, maga a tartalom
           document.add(new Paragraph("\n\n\n\n\n\n" + text, FontFactory.getFont("betutipus", BaseFont.IDENTITY_H, BaseFont.EMBEDDED)));
*/           
           
           //Sortörések hozzáadása (ne takarja a táblázat a logót
           document.add(new Paragraph("\n\n\n\n"));
           
           //Táblázat létrehozása
           float[] columnWidths = {2, 4, 4, 6}; //különböző oszlopok mennyire legyenek szélesek (arány)
           PdfPTable table = new PdfPTable(columnWidths);
           table.setWidthPercentage(100); //maga a tábla 100% széles legyen, töltse ki a teret
           //header létrehozása
           PdfPCell cell = new PdfPCell(new Phrase("Kontakt Lista"));
           cell.setBackgroundColor(GrayColor.MAGENTA); //háttérszín beállítás
           cell.setHorizontalAlignment(Element.ALIGN_CENTER); //vízszintes pozició állása
           cell.setColspan(4); //hány oszlop széles legyen
           table.addCell(cell); //hozzáadjuk a táblához
           //a három oszlop megadása
           table.getDefaultCell().setBackgroundColor(GrayColor.PINK); //az összes további cella színe
           table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER); //az összes további cella pozició
           table.addCell("Sorszám");
           table.addCell("Vezetéknév");
           table.addCell("Keresztnév");
           table.addCell("E-mail cím");
           table.setHeaderRows(1); //hány darab header sort szeretnénk látni
           table.getDefaultCell().setBackgroundColor(GrayColor.GRAYWHITE);
           table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
           //adatokkal való feltöltés
           for(int i = 1; i <= data.size(); i++){
               Person actualPerson = data.get(i - 1);
               
               table.addCell("" + i);
               table.addCell(actualPerson.getLastName());
               table.addCell(actualPerson.getFirstName());
               table.addCell(actualPerson.getEmail());
           }
           
           document.add(table); //a dokumentumhozz hozzáadjuk a táblát

           //chunk - darab vagy adag valami hozzáadása...ebben az esetben aláírás hozzáadása
           Chunk signature = new Chunk("\n\n Generálva a Telefonkönyv alkalmazás segítségével.");
           // c.setBackground(BaseColor.BLUE);
           Paragraph base = new Paragraph(signature);
           document.add(base);
           
       }catch (Exception e){
           e.printStackTrace(); //írja ki ha valami gond van
       }
       document.close(); //dokumentum lezárása!!
    }
}
