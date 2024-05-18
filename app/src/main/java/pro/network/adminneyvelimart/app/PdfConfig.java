package pro.network.adminneyvelimart.app;

import android.content.Context;
import android.text.format.DateFormat;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;

import java.io.IOException;
import java.util.Date;

import pro.network.adminneyvelimart.order.Order;
import pro.network.adminneyvelimart.product.Product;


public class PdfConfig {

    private static final BaseColor greenBase = new BaseColor(0, 0, 255);
    private static final BaseColor whiteBase = new BaseColor(255, 255, 255);
    private static final BaseColor greenLightBase = new BaseColor(142, 204, 74);
    private static final BaseColor gray = new BaseColor(237, 237, 237);
    private static BaseFont urName;
    private static final Font nameFont = new Font(urName, 10, Font.BOLD);
    private static final Font catFont = new Font(urName, 6, Font.BOLD);
    private static final Font catNormalFont = new Font(urName, 6, Font.NORMAL);
    private static final Font subFont = new Font(urName, 5, Font.NORMAL);

    {
        try {
            urName = BaseFont.createFont("font/sans.TTF", "UTF-8", BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addMetaData(Document document) {
        document.addTitle("My first PDF");
        document.addSubject("Using iText");
        document.addKeywords("Java, PDF, iText");
        document.addAuthor("Lars Vogel");
        document.addCreator("Lars Vogel");
    }

    public static void addContent(Document document, Order mainbean, Context context) throws Exception {
        PdfPTable table1 = new PdfPTable(1);
        table1.setWidthPercentage(100);
        table1.setWidths(new int[]{1});

        table1.addCell(createTextbottomlow("N MART", nameFont, false));
        table1.addCell(createTextbottomlow("Ph:919894831091", catFont, false));
        table1.addCell(createTextCellCenter(mainbean.getShopname(), catNormalFont, true));

        table1.setSplitLate(false);
        document.add(table1);

        PdfPTable table2 = new PdfPTable(1);
        table2.setWidthPercentage(100);
        table2.setWidths(new int[]{1});

        table2.addCell(createTextLeft("Customer Name: " + mainbean.getName(), catNormalFont, false));
        table2.addCell(createTextLeft("Mobile No: " + mainbean.getPhone(), catNormalFont, false));
        String date = "";

        Date d = new Date();
        CharSequence s = DateFormat.format("dd-MM-yyyy", d.getTime());
        date = s.toString();

        table2.addCell(createTextLeft("Date: " + date.split(" ")[0], catNormalFont, true));
        table2.setSplitLate(false);
        document.add(table2);

        PdfPTable table3 = new PdfPTable(4);
        table3.setWidthPercentage(100);
        table3.setWidths(new float[]{0.5f, 2f, 0.5f, 1});
        table3.addCell(createTextLeft("SNo", catFont, false));
        table3.addCell(createTextLeft("Name", catFont, false));
        table3.addCell(createTextRight("Qty", catFont));
        table3.addCell(createTextRight("Price", catFont));

        for (int i = 0; i < mainbean.getProductBeans().size(); i++) {
            table3.addCell(createTextLeft((i + 1) + "", catNormalFont, false));
            Product productListBean = mainbean.getProductBeans().get(i);
            table3.addCell(createTextLeft(productListBean.getBrand() + "_" + productListBean.getModel()
                    + "\n" + "Shop-" + productListBean.shopname, catNormalFont, false));
            table3.addCell(createTextRight(productListBean.getQty(), catNormalFont));
            table3.addCell(createTextRight("Rs." + productListBean.getMrp() + ".00", catNormalFont));
        }


        table3.setSplitLate(false);
        document.add(table3);


        PdfPTable table7 = new PdfPTable(4);
        table7.setWidthPercentage(100);
        table7.setWidths(new float[]{0.0f, 0.0f, 3f, 1});

        table7.addCell(createTextLeft("", catNormalFont, false));
        table7.addCell(createTextLeft("", catNormalFont, false));
        table7.addCell(createTextRight("Total", catNormalFont));
        table7.addCell(createTextRight("Rs." + mainbean.getTotal(), catNormalFont));

        table7.addCell(createTextLeft("", catNormalFont, false));
        table7.addCell(createTextLeft("", catNormalFont, false));
        table7.addCell(createTextRight("Shipping Charge", catNormalFont));
        table7.addCell(createTextRight("Rs." + mainbean.getDcharge(), catNormalFont));

//        table7.addCell(createTextLeft("", catNormalFont, false));
//        table7.addCell(createTextLeft("", catNormalFont, false));
//        table7.addCell(createTextRight("Wallet Amount", catNormalFont));
//        table7.addCell(createTextRight("₹35.00", catNormalFont));


        table7.addCell(createTextLeft("", catNormalFont, false));
        table7.addCell(createTextLeft("", catNormalFont, false));
        table7.addCell(createTextRight("Coupon Amount", catNormalFont));
        table7.addCell(createTextRight("Rs." + mainbean.getCouponAmt(), catNormalFont));

//        table7.addCell(createTextLeft("", catNormalFont, false));
//        table7.addCell(createTextLeft("", catNormalFont, false));
//        table7.addCell(createTextRight("Packing Charges", catNormalFont));
//        table7.addCell(createTextRight("₹10.00", catNormalFont));

        int totalPrice;


        table7.addCell(createTextLeft("", catNormalFont, false));
        table7.addCell(createTextLeft("", catNormalFont, false));
        table7.addCell(createTextRight("Grand Total", catNormalFont));
        table7.addCell(createTextRight("Rs." + mainbean.getPrice(), catNormalFont));

        table7.setSplitLate(false);
        document.add(table7);

        PdfPTable table5 = new PdfPTable(1);
        table5.setWidthPercentage(100);
        table5.setWidths(new float[]{1});
        table5.addCell(createTextcenter("\n", catNormalFont, true));
        table5.addCell(createTextcenter("E & OE", subFont, false));
        table5.addCell(createTextcenter("FOR EXCHANGE POLICY PLEASE VISIT N MART", subFont, true));
        table5.addCell(createTextcenter("Tax Invoice/Bill of Supply -Sale", subFont, false));
        table5.addCell(createTextcenter("Bill No: " + Appconfig.intToString(Integer.parseInt(mainbean.getId()), 5) + " Date : " + date, subFont, true));
        table5.addCell(createTextcenter("Its is a computer generated invoice generated original / customer copy", subFont, false));
        table5.addCell(createTextcenter("***Thank you for you purchase***", subFont, false));
        table5.setSplitLate(false);
        document.add(table5);
    }


    public static PdfPCell createTextRight(String text, Font font) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_RIGHT);
        cell.addElement(p);
        cell.setPaddingLeft(5);
        cell.setVerticalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }


    public static PdfPCell createTextLeft(String text, Font font, boolean isBorder) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_LEFT);
        cell.addElement(p);
        cell.setVerticalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        if (isBorder) {
            cell.setPaddingBottom(10);
            cell.setCellEvent(new DottedCell(Rectangle.BOTTOM));
        }
        return cell;
    }

    public static PdfPCell createTextcenter(String text, Font font, boolean isBorder) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        cell.setVerticalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.NO_BORDER);
        if (isBorder) {
            cell.setPaddingBottom(10);
            cell.setCellEvent(new DottedCell(Rectangle.BOTTOM));
        }
        return cell;
    }

    public static PdfPCell createTextbottomlow(String text, Font font, boolean isBorder) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(3);
        if (isBorder) {
            cell.setCellEvent(new DottedCell(Rectangle.BOTTOM));
        }
        return cell;
    }

    public static PdfPCell createTextCellCenter(String text, Font font, boolean isBorder) throws DocumentException, IOException {
        PdfPCell cell = new PdfPCell();
        Paragraph p = new Paragraph(text, font);
        p.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(p);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPaddingBottom(10);
        if (isBorder) {
            cell.setCellEvent(new DottedCell(Rectangle.BOTTOM));
        }
        return cell;
    }

    static class DottedCell implements PdfPCellEvent {
        private int border = 0;

        public DottedCell(int border) {
            this.border = border;
        }

        public void cellLayout(PdfPCell cell, Rectangle position,
                               PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
            canvas.saveState();
            canvas.setColorStroke(greenBase);
            canvas.setLineDash(1, 3, 1);
            if ((border & PdfPCell.TOP) == PdfPCell.TOP) {
                canvas.moveTo(position.getRight(), position.getTop());
                canvas.lineTo(position.getLeft(), position.getTop());
            }
            if ((border & PdfPCell.BOTTOM) == PdfPCell.BOTTOM) {
                canvas.moveTo(position.getRight(), position.getBottom());
                canvas.lineTo(position.getLeft(), position.getBottom());
            }
            if ((border & PdfPCell.RIGHT) == PdfPCell.RIGHT) {
                canvas.moveTo(position.getRight(), position.getTop());
                canvas.lineTo(position.getRight(), position.getBottom());
            }
            if ((border & PdfPCell.LEFT) == PdfPCell.LEFT) {
                canvas.moveTo(position.getLeft(), position.getTop());
                canvas.lineTo(position.getLeft(), position.getBottom());
            }
            canvas.stroke();
            canvas.restoreState();
        }
    }
}
