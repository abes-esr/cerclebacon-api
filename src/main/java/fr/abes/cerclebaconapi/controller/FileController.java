package fr.abes.cerclebaconapi.controller;

import fr.abes.cerclebaconapi.entity.FileKbartTSV;
import fr.abes.cerclebaconapi.service.FileNamingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class FileController {

    private final FileNamingService fileNamingService;

    @Value("${pathToLoad}")
    private String pathToLoad;

    public FileController(FileNamingService fileNamingService) {
        this.fileNamingService = fileNamingService;
    }

    @GetMapping( "/all")
    public List<FileKbartTSV> getAll() {
        File dossier = new File(pathToLoad);
        File[] fichiers = dossier.listFiles();
        List<FileKbartTSV> result = new ArrayList<>();
        if (fichiers != null) {
            for (File fichier : fichiers) {
                if( fichier.getName().endsWith(".tsv")) {
                    FileKbartTSV file = fileNamingService.getFileKbartTSV(fichier.getName());
                    File fileLog = new File(fichier.getAbsolutePath().replace(".tsv", ".log"));
                    File fileErr = new File(fichier.getAbsolutePath().replace(".tsv", ".bad"));
                    file.setLogsUrl(fileLog.exists() ? fileLog.getAbsolutePath() : null );
                    file.setErrorsUrl(fileErr.exists() ? fileErr.getAbsolutePath() : null );
                    result.add(file);
                }
            }
        }
        return result;
    }
}
