package com.pulkit.xlsxtosql.service;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pulkit.xlsxtosql.constants.ApplicationConstants.*;

@Service
public class XlsxToSqlConvertorService {

    public String convertFromFile(String path) throws IOException {

        File f = new File(path);
        FileInputStream file = new FileInputStream(f);

        Workbook workbook = new XSSFWorkbook(file);

        Sheet sheet = workbook.getSheetAt(0);

        Map<Integer, List<String>> data = new HashMap<>();

        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<String>());
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING:
                        data.get(i).add(cell.getStringCellValue());
                        break;
                    case NUMERIC:
                        data.get(i).add(String.valueOf(cell.getNumericCellValue()));
                        break;
                    default: data.get(i).add(cell.getStringCellValue());;
                }
            }
            i++;
        }

        file.close();



        System.out.println("Map Generated Is : ");
        for (Integer k: data.keySet()) {
            System.out.println("Key : " + k + " Value : " + data.get(k).toString());
        }

        File directory = new File(OUTPUT_DIR_PATH);
        if (! directory.exists()){
            directory.mkdir();
        }

        String tableName = f.getName().substring(0,f.getName().length() - XLSX_EXTENSION.length());
        String outputFilePath = OUTPUT_DIR_PATH + SLASH + tableName + SQL_EXTENSION;
        FileWriter fileWriter = new FileWriter(outputFilePath);
        BufferedWriter writer = new BufferedWriter(fileWriter);
        String header = "(" + data.get(0).toString().substring(1,data.get(0).toString().length()-1) + ")";
        writer.write("INSERT INTO " + tableName + " " + header + " VALUES \n");
        for(int k=1;k<data.size();k++){

            String line = "(";

            for(int l=0;l<data.get(k).size();l++){
                line += "'" + data.get(k).get(l) + "'" + ",";
            }
            line = line.substring(0,line.length()-1);
            if(k == data.size()-1)
                line += ")";
            else
                line += "),\n";

            writer.write(line);
        }

        writer.close();
        fileWriter.close();

        return outputFilePath;
    }

    public String convertFromUrl(String url) throws IOException {

        URL url1 = new URL(url);

        File directory = new File(INPUT_DIR_PATH);
        if (! directory.exists()){
            directory.mkdir();
        }

        String filePath = INPUT_DIR_PATH + SLASH + "Pulkit.xlsx";

        FileUtils.copyURLToFile(url1, new File(filePath));
        return "Hello : " + url1;
    }
}
