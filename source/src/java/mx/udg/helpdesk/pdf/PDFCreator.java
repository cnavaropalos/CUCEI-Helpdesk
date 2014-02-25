package mx.udg.helpdesk.pdf;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

/**
 * This class creates PDF files on the fly with the required data.
 *
 * @author Carlos Navapa
 */
public class PDFCreator {

    /**
     * Creates a PDF file
     *
     * @param document
     * @param filePath
     * @throws IOException
     */
    public void exportPDFFly(Document document, String filePath) throws IOException {

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        response.setContentType("application/pdf");
        response.setHeader("Content-disposition", "inline=filereportName=" + filePath);

        response.getOutputStream().write(getBytesFromFile(new File(filePath)));
        response.getOutputStream().flush();
        response.getOutputStream().close();
        context.responseComplete();
    }

    /**
     * Converts PDF file to bytes array.
     *
     * @param file
     * @return PDF as a byte array.
     * @throws IOException
     */
    private byte[] getBytesFromFile(File file) throws IOException {
        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            throw new IOException();
        }
        byte[] ducumentBytes = new byte[(int) length];
        int offset = 0;
        int ducumentBytesReaded = 0;
        InputStream inputStream = new FileInputStream(file);

        while (offset < ducumentBytes.length
                && (ducumentBytesReaded = inputStream.read(ducumentBytes, offset, ducumentBytes.length - offset)) >= 0) {
            offset += ducumentBytesReaded;
        }

        inputStream.close();

        if (offset < ducumentBytes.length) {
            throw new IOException();
        }
        return ducumentBytes;
    }

    /**
     * Add blank lines to a paragraph.
     *
     * @param paragraph
     * @param numberLines
     */
    public void writeBlankLines(Paragraph paragraph, int numberLines) {
        for (int i = 0; i < numberLines; i++) {
            paragraph.add(Chunk.NEWLINE);
        }
    }

    /**
     * Add blanks lines to a document.
     *
     * @param document
     * @param numberLines
     * @throws DocumentException
     * @throws IOException
     */
    public void writeBlankLines(Document document, int numberLines) throws DocumentException, IOException {
        for (int i = 0; i < numberLines; i++) {
            document.add(Chunk.NEWLINE);
        }
    }

    /**
     * Add empty cells to the table. You can activate the flags if you want a
     * transparent cell or a cell with border.
     *
     * @param table
     * @param data
     * @param isTransparent
     * @param isTitle
     */
    public void addCell(PdfPTable table, String data, boolean isTransparent, boolean isTitle) {
        PdfPCell tempCell = new PdfPCell();
        Font font = new Font(FontFactory.getFont("arial", 7, Font.NORMAL, BaseColor.BLACK));
        if (isTransparent) {
            tempCell.setBorder(Rectangle.NO_BORDER);
        }
        if (isTitle) {
            font = new Font(FontFactory.getFont("arial", 7, Font.BOLD, BaseColor.BLACK));
        }
        tempCell.setPhrase(new Phrase(data, font));
        table.addCell(tempCell);
    }

    /**
     * Add new cell to the table but with an image.
     *
     * @param table
     * @param image
     * @param isTransparent
     */
    public void addCell(PdfPTable table, Image image, boolean isTransparent) {
        PdfPCell tempCell = new PdfPCell();
        Font font = new Font(FontFactory.getFont("arial", 10, Font.NORMAL, BaseColor.BLACK));
        if (isTransparent) {
            tempCell.setBorder(Rectangle.NO_BORDER);
        }

        tempCell.addElement(image);
        table.addCell(tempCell);
    }

    /**
     * Add empty cells to the table.
     *
     * @param table
     * @param numberCells
     */
    public void addEmptyCells(PdfPTable table, int numberCells) {
        for (int i = 0; i < numberCells; i++) {
            PdfPCell tempCell = new PdfPCell();
            tempCell.setBorder(Rectangle.NO_BORDER);

            tempCell.setPhrase(new Phrase("  "));
            table.addCell(tempCell);
        }
    }

    /**
     * Creates a report with the object data.
     *
     * @note Object MUST be changed to the report object.
     * @param object This will be changed
     * @param fileName
     * @param PDFCreator
     * @return
     * @throws FileNotFoundException
     * @throws DocumentException
     * @throws BadElementException
     * @throws IOException
     */
    public Document createObjectReport(Object object, String fileName, PDFCreator PDFCreator) throws FileNotFoundException, DocumentException, BadElementException, IOException {
        Document document = new Document(new Rectangle(623.5f, 434.5f), 30, 30, 100, 80);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
        PDFHeaderFooter headerFooterEvent = new PDFHeaderFooter("TLP to ZPN box movement report");
        writer.setBoxSize("report", document.getPageSize());
        writer.setPageEvent(headerFooterEvent);
        document.setMarginMirroringTopBottom(true);
        document.open();

        document.close();
        return document;
    }
}
