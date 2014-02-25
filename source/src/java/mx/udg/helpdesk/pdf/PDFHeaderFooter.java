package mx.udg.helpdesk.pdf;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.IOException;
import java.util.Date;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import mx.udg.helpdesk.lang.LanguajeManager;

/**
 * This class creates header and footer for PDF files.
 *
 * @author Carlos Navapa
 */
public class PDFHeaderFooter extends PdfPageEventHelper {

    private final String title;
    private Phrase date;
    private int pageNumber;

    public PDFHeaderFooter(String title) {
        this.title = title;
    }

    /**
     * @see PdfPageEventHelper
     * @param writer
     * @param document
     */
    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        date = new Phrase(new Date().toString());
        pageNumber = 0;
    }

    /**
     * @see PdfPageEventHelper
     * @param writer
     * @param document
     */
    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        pageNumber++;
    }

    /**
     * @see PdfPageEventHelper
     * @param writer
     * @param document
     */
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        Font boldFont = new Font(FontFactory.getFont("arial", 9, Font.BOLD, BaseColor.BLACK));
        Font normalFont = new Font(FontFactory.getFont("arial", 9, Font.NORMAL, BaseColor.BLACK));

        ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        String imagesPath = servletContext.getRealPath("/") + "src/img";

        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT,
                new Phrase(date.getContent(), normalFont), 590, 400, 0);

        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                new Phrase(String.format(LanguajeManager.getProperty("page"), pageNumber), normalFont),
                300, 20, 0);

        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                new Phrase(LanguajeManager.getProperty("approver") + ": _____________________", normalFont),
                520, 20, 0);

        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                new Phrase(title, boldFont),
                350, 380, 0);

        Image logo;
        try {
            logo = Image.getInstance(imagesPath + "/logo.png");
            logo.setTransparency(new int[]{
                0x20, 0x50
            });
            logo.setAbsolutePosition(25f, 370f);
            logo.scaleAbsolute(70f, 50f);
            document.add(logo);
        } catch (BadElementException ex) {

        } catch (IOException | DocumentException ex) {
        }

    }

}
