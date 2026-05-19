package fr.abes.cerclebaconapi.entity;

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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getForceOption() {
        return forceOption;
    }

    public void setForceOption(String forceOption) {
        this.forceOption = forceOption;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getLogsFilename() {
        return logsFilename;
    }

    public void setLogsFilename(String logsFilename) {
        this.logsFilename = logsFilename;
    }

    public String getErrorsFilename() {
        return ErrorsFilename;
    }

    public void setErrorsFilename(String errorsFilename) {
        ErrorsFilename = errorsFilename;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
