package fr.abes.cerclebaconapi.entity;

import lombok.Data;

@Data
public class FileKbartTSV {
    private String provider;
    private String zone;
    private String packageName;
    private String forceOption;
    private String filename;
    private String logsFilename;
    private String ErrorsFilename;
    private String date;

    public FileKbartTSV(String filename, String provider, String zone, String packageName, String forceOption, String date) {
        this.filename = filename;
        this.provider = provider;
        this.zone = zone;
        this.packageName = packageName;
        this.forceOption = forceOption;
        this.date = date;
    }
}
