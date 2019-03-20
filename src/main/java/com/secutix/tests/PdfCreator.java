package com.secutix.tests;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class PdfCreator {

	private static final PdfCreator INSTANCE = new PdfCreator();
	private static final String PDF_TITLE = "TEST-MYTICKET-TICKGEN.pdf";

	public static PdfCreator getInstance() {
		return INSTANCE;
	}

	public void createPDf() {
		Document document = new Document();
		try {
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(PDF_TITLE));
			document.open();
			document.add(new Paragraph("A Hello World PDF document."));
			document.close();
			writer.close();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
