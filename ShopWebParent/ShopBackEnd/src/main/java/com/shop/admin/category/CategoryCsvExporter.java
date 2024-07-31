package com.shop.admin.category;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shop.admin.AbstractExporter;
import com.shop.common.entity.Category;

import jakarta.servlet.http.HttpServletResponse;

public class CategoryCsvExporter extends AbstractExporter {

    public void export(List<Category> listCategories, HttpServletResponse response) throws IOException {
	super.setResponseHeader(response, "text/csv; utf-8", ".csv", "카테고리_");

	ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

	String[] csvHeader = { "ID", "이름" };
	String[] fieldMapping = { "id", "name" };

	csvWriter.writeHeader(csvHeader);

	for (Category user : listCategories) {
	    csvWriter.write(user, fieldMapping);
	}

	csvWriter.close();
    }
}
