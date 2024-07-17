package com.shop.admin.user;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.http.HttpServletResponse;

public class AbstractExporter {

    public void setResponseHeader(HttpServletResponse response, String contentType, String extension) throws IOException {
	DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
	String timestamp = dateFormatter.format(new Date());
	String fileName = "사용자_" + timestamp + extension;

	String encodedFilename = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());

	response.setContentType(contentType);
	response.setCharacterEncoding("UTF-8");

	String headerKey = "Content-Disposition";
	String headerValue = "attachment; filename=" + encodedFilename;

	response.setHeader(headerKey, headerValue);
    }
}
