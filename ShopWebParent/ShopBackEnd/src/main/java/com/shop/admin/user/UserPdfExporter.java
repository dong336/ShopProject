package com.shop.admin.user;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.ClassPathResource;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shop.common.entity.User;

import jakarta.servlet.http.HttpServletResponse;

public class UserPdfExporter extends AbstractExporter {

    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
	super.setResponseHeader(response, "application/pdf; utf-8", ".pdf");

	Document document = new Document(PageSize.A4);
	PdfWriter.getInstance(document, response.getOutputStream());

	document.open();

	Paragraph paragraph = new Paragraph("사용자 목록", getStaticFont(18, Font.BOLD));

	paragraph.setAlignment(Paragraph.ALIGN_CENTER);

	document.add(paragraph);

	PdfPTable table = new PdfPTable(5);

	table.setWidthPercentage(100f);
	table.setSpacingBefore(10);
	table.setWidths(new float[] { 1.0f, 3.5f, 1.5f, 6.5f, 1.5f });

	writeTableHeader(table);
	writeTableData(table, listUsers);

	document.add(table);
	document.close();
    }

    private void writeTableData(PdfPTable table, List<User> listUsers) throws IOException {
	for (User user : listUsers) {
	    Font font = getStaticFont(12, Font.NORMAL);

	    table.addCell(new Phrase(String.valueOf(user.getId()), font));
	    table.addCell(new Phrase(user.getEmail(), font));
	    table.addCell(new Phrase(user.getName(), font));
	    table.addCell(new Phrase(user.getRoles().toString(), font));
	    table.addCell(new Phrase(String.valueOf(user.isEnabled()), font));
	}
    }

    private void writeTableHeader(PdfPTable table) throws IOException {
	PdfPCell cell = new PdfPCell();
	cell.setBackgroundColor(Color.BLUE);
	cell.setPadding(5);

	Font font = getStaticFont(12, Font.NORMAL);
	font.setSize(16);
	font.setColor(Color.WHITE);

	cell.setPhrase(new Phrase("ID", font));
	table.addCell(cell);

	cell.setPhrase(new Phrase("메일", font));
	table.addCell(cell);

	cell.setPhrase(new Phrase("이름", font));
	table.addCell(cell);

	cell.setPhrase(new Phrase("권한", font));
	table.addCell(cell);

	cell.setPhrase(new Phrase("활성화", font));
	table.addCell(cell);
    }

    private Font getStaticFont(int size, int fontStyle) throws IOException {
	ClassPathResource fontResource = new ClassPathResource("static/fonts/NanumGothic.ttf");
	InputStream fontStream = fontResource.getInputStream();
	BaseFont baseFont = BaseFont.createFont("NanumGothic.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, fontStream.readAllBytes(), null);

	return new Font(baseFont, size, fontStyle);
    }
}
