package com.example.reporting_service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.text.SimpleDateFormat;

public class HeaderFooterPageEvent extends PdfPageEventHelper {
    private final Image logo;

    public HeaderFooterPageEvent() throws Exception {
        byte[] logoBytes = getLogoBytes();
        this.logo = Image.getInstance(logoBytes);
        this.logo.scaleToFit(50, 50);
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte canvas = writer.getDirectContent();
        Rectangle pageSize = document.getPageSize();

        try {
            logo.setAbsolutePosition(pageSize.getLeft() + 30, pageSize.getTop() - 80);
            canvas.addImage(logo);
        } catch (Exception e) {
            throw new RuntimeException("Error adding logo", e);
        }

        ColumnText.showTextAligned(
                canvas,
                Element.ALIGN_RIGHT,
                new Phrase(new SimpleDateFormat("dd-MM-yyyy").format(new java.util.Date()), FontFactory.getFont(FontFactory.HELVETICA, 10)),
                pageSize.getRight() - 30,
                pageSize.getTop() - 30,
                0
        );

        ColumnText.showTextAligned(
                canvas,
                Element.ALIGN_CENTER,
                new Phrase(String.format("Page %d", writer.getPageNumber()), FontFactory.getFont(FontFactory.HELVETICA, 10)),
                (pageSize.getLeft() + pageSize.getRight()) / 2,
                pageSize.getBottom() + 20,
                0
        );
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        document.setMargins(document.leftMargin(), document.rightMargin(), 100, document.bottomMargin());
    }

    private byte[] getLogoBytes() {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            return classLoader.getResourceAsStream("logo.jfif").readAllBytes();
        } catch (Exception e) {
            throw new RuntimeException("Error loading logo from resources", e);
        }
    }
}
