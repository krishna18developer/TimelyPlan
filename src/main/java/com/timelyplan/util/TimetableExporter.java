package com.timelyplan.util;

import com.timelyplan.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.PageSize;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class TimetableExporter {
    private static final String[] DAYS = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"};
    private static final int PERIODS = 8;

    public static void exportToExcel(Timetable timetable, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(timetable.getSection().getName());
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Period");
            for (int i = 0; i < DAYS.length; i++) {
                headerRow.createCell(i + 1).setCellValue(DAYS[i]);
            }

            // Create period rows
            for (int period = 1; period <= PERIODS; period++) {
                Row row = sheet.createRow(period);
                row.createCell(0).setCellValue("Period " + period);
                
                for (int day = 0; day < DAYS.length; day++) {
                    TimeSlot slot = new TimeSlot(TimeSlot.DayOfWeek.valueOf(DAYS[day]), period);
                    Timetable.TimetableEntry entry = timetable.getEntry(slot);
                    
                    Cell cell = row.createCell(day + 1);
                    if (entry != null) {
                        cell.setCellValue(entry.getCourse().getCourseName() + "\n" + 
                                        entry.getInstructor().getName());
                    }
                }
            }

            // Auto-size columns
            for (int i = 0; i <= DAYS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }

    public static void exportToPDF(Timetable timetable, String filePath) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Add title
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
        Paragraph title = new Paragraph("Timetable - " + timetable.getSection().getName(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n"));

        // Create table
        PdfPTable table = new PdfPTable(DAYS.length + 1);
        table.setWidthPercentage(100);

        // Add header row
        table.addCell("Period");
        for (String day : DAYS) {
            table.addCell(day);
        }

        // Add period rows
        for (int period = 1; period <= PERIODS; period++) {
            table.addCell("Period " + period);
            
            for (String day : DAYS) {
                TimeSlot slot = new TimeSlot(TimeSlot.DayOfWeek.valueOf(day), period);
                Timetable.TimetableEntry entry = timetable.getEntry(slot);
                
                if (entry != null) {
                    table.addCell(entry.getCourse().getCourseName() + "\n" + 
                                entry.getInstructor().getName());
                } else {
                    table.addCell("");
                }
            }
        }

        document.add(table);
        document.close();
    }
} 