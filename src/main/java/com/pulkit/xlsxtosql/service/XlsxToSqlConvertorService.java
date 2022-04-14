package com.pulkit.xlsxtosql.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.exceptions.CsvException;
import com.pulkit.xlsxtosql.dto.ColumnMappingDTO;
import com.pulkit.xlsxtosql.dto.InputYamlDTO;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.net.URL;
import java.util.*;

import static com.pulkit.xlsxtosql.constants.ApplicationConstants.*;

@Service
public class XlsxToSqlConvertorService {

    public String convertFromFile(String path) throws IOException {

        File f = new File(path);
        FileInputStream file = new FileInputStream(f);

        Workbook workbook = new XSSFWorkbook(file);

        // By default, processing only 1st sheet
        Sheet sheet = workbook.getSheetAt(0);

        Map<Integer, List<String>> data = new HashMap<>();

        DataFormatter formatter = new DataFormatter();

        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<>());
            for (Cell cell : row) {
                String cellValue = formatter.formatCellValue(cell);
                data.get(i).add(cellValue.replaceAll("'","''"));
            }
            i++;
        }

        file.close();

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

    public String convertFromFile(String path, Map<String, ColumnMappingDTO> columnMappingsMap) throws IOException {
        File f = new File(path);
        FileInputStream file = new FileInputStream(f);

        Workbook workbook = new XSSFWorkbook(file);

        // By default, processing only 1st sheet
        Sheet sheet = workbook.getSheetAt(0);

        Map<Integer, List<String>> data = new HashMap<>();

        DataFormatter formatter = new DataFormatter();

        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<>());
            for (Cell cell : row) {
                String cellValue = formatter.formatCellValue(cell);
                data.get(i).add(cellValue.replaceAll("'","''"));
            }
            i++;
        }

        file.close();

        File directory = new File(OUTPUT_DIR_PATH);
        if (! directory.exists()){
            directory.mkdir();
        }

        String tableName = f.getName().substring(0,f.getName().length() - XLSX_EXTENSION.length());
        String outputFilePath = OUTPUT_DIR_PATH + SLASH + tableName + SQL_EXTENSION;
        FileWriter fileWriter = new FileWriter(outputFilePath);
        BufferedWriter writer = new BufferedWriter(fileWriter);

        Set<Integer> indexes = new HashSet<>();
        String header = "(";
        for(int x=0;x<data.get(0).size();x++){
            if(columnMappingsMap.containsKey(data.get(0).get(x))){
                header += columnMappingsMap.get(data.get(0).get(x)).getTgtCol();
                header += ",";
                indexes.add(x);
            }
        }
        header = header.substring(0,header.length()-1);
        header += ")";

        writer.write("INSERT INTO " + tableName + " " + header + " VALUES \n");
        for(int k=1;k<data.size();k++){

            String line = "(";

            for(int x : indexes){
                line += "'" + data.get(k).get(x) + "'" + ",";
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

    public String convertFromUrl(String url, Map<String, ColumnMappingDTO> columnMappingsMap) throws IOException {

        URL url1 = new URL(url);

        File directory = new File(INPUT_DIR_PATH);
        if (! directory.exists()){
            directory.mkdir();
        }

        String fileName = url1.getFile().substring(url1.getFile().lastIndexOf("/") + 1);
        String filePath = INPUT_DIR_PATH + SLASH + fileName;

        FileUtils.copyURLToFile(url1, new File(filePath));

        String outputFilePath = convertFromFile(filePath, columnMappingsMap);
        return outputFilePath;
    }

    public String convertFromUrl(String url) throws IOException {
        URL url1 = new URL(url);

        File directory = new File(INPUT_DIR_PATH);
        if (! directory.exists()){
            directory.mkdir();
        }

        String fileName = url1.getFile().substring(url1.getFile().lastIndexOf("/") + 1);
        String filePath = INPUT_DIR_PATH + SLASH + fileName;

        FileUtils.copyURLToFile(url1, new File(filePath));

        String outputFilePath = convertFromFile(filePath);
        return outputFilePath;
    }

    public void convertFromYaml() throws IOException, CsvException {
        File folder = new File(YAML_DIR);
        File[] listOfFiles = folder.listFiles();

        for (File file: listOfFiles) {
            if (file.isFile()) {
                Yaml yaml = new Yaml(new Constructor(InputYamlDTO.class));
                InputYamlDTO inputYaml = yaml.load(new FileReader(file));
                System.out.println("Input Yaml Content : " + inputYaml.toString());

                // Reading CSV
                FileReader filereader = new FileReader(inputYaml.getMappingPath());
                CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
                ColumnPositionMappingStrategy mappingStrategy = new ColumnPositionMappingStrategy();
                mappingStrategy.setType(ColumnMappingDTO.class);
                String[] columns = new String[]{"srcCol","tgtCol","tgtDataType"};
                mappingStrategy.setColumnMapping(columns);
                CsvToBean ctb = new CsvToBean();
                ctb.setMappingStrategy(mappingStrategy);
                ctb.setCsvReader(csvReader);
                List<ColumnMappingDTO> columnMappings = ctb.parse();
                Map<String, ColumnMappingDTO> columnMappingsMap = new HashMap<>();

                for (ColumnMappingDTO columnMapping: columnMappings) {
                    columnMappingsMap.put(columnMapping.getSrcCol().strip(),columnMapping);
                }

                convertFromUrl(inputYaml.getUrl(),columnMappingsMap);
            }
        }
    }
}
