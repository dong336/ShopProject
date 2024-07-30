package com.shop.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

public class FileUploadUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtil.class);
    
    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
	Path uploadPath = Paths.get(uploadDir);

	if (!Files.exists(uploadPath)) {
	    Files.createDirectories(uploadPath);
	}

	try (InputStream inputStream = multipartFile.getInputStream()) {
	    Path filePath = uploadPath.resolve(fileName);
	    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
	} catch (IOException e) {
	    throw new IOException("파일 저장 실패: " + fileName, e);
	}
    }

    public static void cleanDir(String dir) {
	Path dirPath = Paths.get(dir);

	try {
	    Files.list(dirPath).forEach(file -> {
		if (!Files.isDirectory(file)) {
		    try {
			Files.delete(file);
		    } catch (IOException e) {
			LOGGER.error("파일 삭제 실패: " + file);
		    }
		}
	    });
	} catch (IOException e) {
	    LOGGER.error("디렉토리 삭제 실패: " + dirPath);
	}
    }
    
    public static void removeDir(String dir) {
	cleanDir(dir);
	
	try {
	    Files.delete(Paths.get(dir));
	} catch (IOException e) {
	    LOGGER.error("이 디렉토리를 삭제할 수 없습니다 : " + dir);
	}
    }
}
