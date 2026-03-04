package fr.abes.cerclebaconapi.controller;

import fr.abes.cerclebaconapi.dto.RenameFileRequestDto;
import fr.abes.cerclebaconapi.entity.FileKbartTSV;
import fr.abes.cerclebaconapi.service.FileNamingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public class FileController {

    private final FileNamingService fileNamingService;

    @Value("${path.toLoad}")
    private String pathToLoad;

    public FileController(FileNamingService fileNamingService) {
        this.fileNamingService = fileNamingService;
    }

    @GetMapping( "/all")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
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
                    file.setLogsFilename(fileLog.exists() ? fileLog.getName() : null );
                    file.setErrorsFilename(fileErr.exists() ? fileErr.getName() : null );
                    result.add(file);
                }
            }
        }
        return result;
    }

    @GetMapping(value = {"/file/{fileName}", "/file/{path}/{fileName}"})
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> getFile(@PathVariable(required = false) String path, @PathVariable String fileName) {
        boolean isReport = ((path != null) && path.equals("report"));

        if (fileName == null || fileName.isEmpty()) {
            return ResponseEntity.badRequest().body("Le paramètre fileName est vide.");
        } else if ((path != null) && !path.equals("report")){
            return ResponseEntity.badRequest().body("Le chemin est incorrect");
        }
        try {
            File fichier = isReport ? new File(pathToLoad + path + File.separator + fileName) : new File(pathToLoad + File.separator + fileName);

            if (!fichier.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Le fichier " + fileName + " est introuvable.");
            }

            FileInputStream fs = new FileInputStream(fichier);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fichier.getName());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); // Use setContentType for better clarity

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(fichier.length())
                    .body(new InputStreamResource(fs));

        } catch (FileNotFoundException e) {
            // This should ideally never happen given the file.exists() check, but it's good practice to keep it.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la lecture du fichier : " + e.getMessage());
        }
    }

    @PostMapping(value = "/renameFile")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<?> renameFile(@RequestBody RenameFileRequestDto renameFileRequestDto) throws IOException {
        if (!(renameFileRequestDto.getForceOption().equals("FORCE") || renameFileRequestDto.getForceOption().equals("BYPASS") || renameFileRequestDto.getForceOption().isEmpty())) {
            return ResponseEntity.badRequest().body("Le paramètre ForceOption est invalide.");
        }

        File fichierSource = new File(pathToLoad + File.separator + renameFileRequestDto.getFileName());
        Path pathSource = Path.of(fichierSource.getAbsolutePath());

        if (!fichierSource.exists()) {
            return ResponseEntity.badRequest().body("Le fichier n'existe pas");
        }

        String fileNameCible = fileNamingService.renameFile(renameFileRequestDto.getFileName(), renameFileRequestDto.getForceOption());

        File fichierCible = new File(pathToLoad + File.separator + fileNameCible);

        Path pathCible = Path.of(fichierCible.getAbsolutePath());
        Files.move(pathSource, pathCible, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok().build();
    }
}
