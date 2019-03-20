package com.secutix.tests;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;

import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.apache.pdfbox.util.Matrix;

import static java.lang.System.err;

public class PdfBoxExample {

	// Examples from : https://svn.apache.org/viewvc/pdfbox/trunk/examples/src/main/java/org/apache/pdfbox/examples/pdmodel/

	private static final PdfBoxExample INSTANCE = new PdfBoxExample();
	private static final String PDF_TITLE = "TEST-MYTICKET-TICKGEN.pdf";

	public static PdfBoxExample getInstance() {
		return INSTANCE;
	}

	public void createPage(){
		final PDPage singlePage = new PDPage(PDRectangle.A4);
		printCoordinates(singlePage.getBBox());
		final PDFont courierBoldFont = PDType1Font.COURIER;
		final int fontSize = 12;
		try (final PDDocument document = new PDDocument()) {
			document.addPage(singlePage);
			final PDPageContentStream contentStream = new PDPageContentStream(document, singlePage);
			// BeginText and font are mandatory
			contentStream.beginText();
			// PDFBox also provides option to embed custom fonts
			contentStream.setFont(courierBoldFont, fontSize);

			contentStream.newLineAtOffset(300, 800); //Note that Y offset is relative to the bottom portion of the page.
			contentStream.showText("Shipment info");
			contentStream.endText();
			//importPdf(document, contentStream);

			importPdfs(document,contentStream);
			//importPdfAsImage(document, contentStream,0,100);

			//importPdfAsImage(document, contentStream,0,400);

			contentStream.close();  // Stream must be closed before saving document.
			document.save("HelloPDFBox.pdf");

		}
		catch (IOException ioEx)
		{
			err.println(
					"Exception while trying to create simple document - " + ioEx);
		}
	}

	private void importPdf(PDDocument document, PDPageContentStream contentStream) throws IOException {
		// Create a Form XObject from the source document using LayerUtility
		LayerUtility layerUtility = new LayerUtility(document);
		PDDocument sourceDoc = PDDocument.load(new File("test.pdf"));
		PDFormXObject form = layerUtility.importPageAsForm(sourceDoc, 1 - 1);
		// draw the full form
		contentStream.drawForm(form);
	}

	private void importPdfs(PDDocument document, PDPageContentStream contentStream) throws IOException {
		// Create a Form XObject from the source document using LayerUtility
		LayerUtility layerUtility = new LayerUtility(document);
		PDDocument firstDoc = PDDocument.load(new File("test.pdf"));
		PDDocument secondDoc = PDDocument.load(new File("test-1.pdf"));
		PDFormXObject firstForm = layerUtility.importPageAsForm(firstDoc, 0);

		PDFormXObject secondForm = layerUtility.importPageAsForm(secondDoc, 0);

		// These things can easily be done in a loop, too
		///AffineTransform affineTransform = new AffineTransform(); // Identity... your requirements may differ
		//layerUtility.appendFormAsLayer((PDPage) bigPages.get(0), firstForm, affineTransform, "Superimposed0");

		// Matrix is a transformation matrix =>
		// https://stackoverflow.com/questions/41810063/what-does-the-arguments-mean-in-pdfbox-matrix
		Matrix matrix = new Matrix();
		matrix.translate(0, 100);
		matrix.scale(0.5f,0.5f);


		contentStream.saveGraphicsState();
		contentStream.transform(matrix);
		contentStream.drawForm(firstForm);
		contentStream.restoreGraphicsState();

		Matrix matrix2 = new Matrix();
		matrix2.translate(0, 400);
		matrix2.scale(0.5f,0.5f);


		contentStream.saveGraphicsState();
		contentStream.transform(matrix2);
		contentStream.drawForm(secondForm);
		contentStream.restoreGraphicsState();
		//PDFormXObject form = layerUtility.importPageAsForm(sourceDoc, 1 - 1);
		// draw the full form
		//contentStream.drawForm(form);
	}

	private void importPdfAsImage(PDDocument document, PDPageContentStream contentStream, int x, int y) throws IOException {
		convertPDFToImage("test.pdf");

		// contentStream.drawImage(ximage, 20, 20 );
		// better method inspired by http://stackoverflow.com/a/22318681/535646
		// reduce this value if the image is too large
		PDImageXObject pdImage = PDImageXObject.createFromFile("test.pdf-1.png", document);
		float scale = 0.2f;
		System.out.println(pdImage.getWidth() + " x "+ pdImage.getHeight());
		contentStream.drawImage(pdImage, x, y, 595, 200);
	}

	private void printCoordinates(PDRectangle bBox) {
		System.out.println("Lower leftX" + bBox.getLowerLeftX());
		System.out.println("Lower leftY" + bBox.getLowerLeftY());
		System.out.println("Upper RightX" + bBox.getUpperRightX());
		System.out.println("Upper RightY" + bBox.getUpperRightY());
	}


	private void convertPDFToImage(final String pdfFilename) throws IOException{

		try (PDDocument document = PDDocument.load(new File(pdfFilename));) {
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			int page =0 ; // reading only first page
			BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);

			// suffix in filename will be used as the file format
			ImageIOUtil.writeImage(bim, pdfFilename + "-" + (page+1) + ".png", 300);
		}

	}
}
