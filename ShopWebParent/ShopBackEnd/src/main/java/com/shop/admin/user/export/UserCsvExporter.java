package com.shop.admin.user.export;

import java.io.IOException;
import java.util.List;

import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shop.common.entity.User;

import jakarta.servlet.http.HttpServletResponse;

public class UserCsvExporter extends AbstractExporter {

    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
	super.setResponseHeader(response, "text/csv; utf-8", ".csv");

	ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);

	String[] csvHeader = { "ID", "메일", "이름", "권한", "활성화" };
	String[] fieldMapping = { "id", "email", "name", "roles", "enabled" };

	csvWriter.writeHeader(csvHeader);

	for (User user : listUsers) {
	    csvWriter.write(user, fieldMapping);
	}

	csvWriter.close();
    }
}
