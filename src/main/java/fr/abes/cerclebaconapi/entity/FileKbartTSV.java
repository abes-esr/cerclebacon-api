package fr.abes.cerclebaconapi.entity;

import lombok.Data;

@Data
public class FileKbartTSV {
    private String provider;
    private String zone;
    private String packageName;
    private String forceOption;
    private String linesUrl;
    private String logsUrl;
    private String ErrorsUrl;
    private String date;

    public FileKbartTSV(String provider, String zone, String packageName, String forceOption, String date) {
        this.provider = provider;
        this.zone = zone;
        this.packageName = packageName;
        this.forceOption = forceOption;
        this.date = date;
    }
}
