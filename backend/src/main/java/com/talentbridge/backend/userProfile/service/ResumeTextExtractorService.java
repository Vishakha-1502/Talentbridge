package com.talentbridge.backend.userProfile.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ResumeTextExtractorService {

    public String extractText(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        }

        if ("pdf".equals(extension)) {
            return extractTextFromPdf(file.getInputStream());
        } else if ("docx".equals(extension)) {
            return extractTextFromDocx(file.getInputStream());
        } else if ("txt".equals(extension) || "text".equals(extension)) {
            return extractTextFromTxt(file.getInputStream());
        } else {
            // Attempt PDF first, then fallback to text
            try {
                return extractTextFromPdf(file.getInputStream());
            } catch (Exception e) {
                return extractTextFromTxt(file.getInputStream());
            }
        }
    }

    private String extractTextFromPdf(InputStream inputStream) throws Exception {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            pdfStripper.setSortByPosition(true);
            return pdfStripper.getText(document);
        }
    }

    private String extractTextFromDocx(InputStream inputStream) throws Exception {
        StringBuilder textBuilder = new StringBuilder();
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if ("word/document.xml".equals(entry.getName())) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream, StandardCharsets.UTF_8));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Strip XML tags: replace <w:p> with newline and other tags with space
                        String clean = line.replaceAll("<w:p[^>]*>", "\n")
                                           .replaceAll("<[^>]+>", " ")
                                           .replaceAll("&amp;", "&")
                                           .replaceAll("&lt;", "<")
                                           .replaceAll("&gt;", ">")
                                           .replaceAll("&quot;", "\"")
                                           .replaceAll("&apos;", "'");
                        textBuilder.append(clean);
                    }
                    break;
                }
            }
        }
        return textBuilder.toString();
    }

    private String extractTextFromTxt(InputStream inputStream) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        }
    }
}
