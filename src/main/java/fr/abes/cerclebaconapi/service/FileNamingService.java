package fr.abes.cerclebaconapi.service;

import fr.abes.cerclebaconapi.entity.FileKbartTSV;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FileNamingService {

    private static final String REGEX = "(.+?)_(.+?)_(.+?)_([0-9]{4}-[0-1][0-9]-[0-3][0-9])(_FORCE|_BYPASS)?\\.(.*)";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    public FileKbartTSV getFileKbartTSV(String filename) {
        Matcher matcher = PATTERN.matcher(filename);// filename
        String provider = null;
        String pckgeZone = null;
        String pckge = null;
        String pckgeDate = null;
        String ext = null;
        String forceOption = null;
        if (matcher.find()) {
            // last group
            int last = matcher.groupCount();
            ext = matcher.group(last);
            forceOption = matcher.group(last - 1);
            pckgeDate = matcher.group(last - 2);
            pckge = matcher.group(last - 3);
            pckgeZone = matcher.group(last - 4);
            provider = matcher.group(last - 5);

            if (provider != null) {
                provider = provider.toUpperCase();
            }
            if (pckgeZone != null) {
                pckgeZone = pckgeZone.toUpperCase();
            }
            if (pckge != null) {
                pckge = pckge.toUpperCase();
            }
            if (pckgeDate != null) {
                pckgeDate = pckgeDate.toUpperCase();
            }
            if (ext != null) {
                ext = ext.toUpperCase();
            }
            if (forceOption == null) {
                forceOption = "";
            } else {
                forceOption = forceOption.replace("_", "");
                forceOption = forceOption.toUpperCase();
            }
        }
        return new FileKbartTSV(filename, provider, pckgeZone, pckge, forceOption, pckgeDate);
    }

    public String renameFile(String nomFichier, String forceOption) {

        Matcher matcher = PATTERN.matcher(nomFichier);

        if (matcher.find()) {
            String provider;
            String pckgeZone;
            String pckge;
            String pckgeDate;
            String ext;
//			String forceOption;
            // last group
            int last = matcher.groupCount();
            ext = matcher.group(last);
//			forceOption = matcher.group(last - 1); // l'ancien forceoption du fichier old
            pckgeDate = matcher.group(last - 2);
            pckge = matcher.group(last - 3);
            pckgeZone = matcher.group(last - 4);
            provider = matcher.group(last - 5);

            String prefixe = provider + "_" + pckgeZone + "_" + pckge + "_" + pckgeDate;

            if (forceOption != null && !forceOption.isEmpty()) {
                return prefixe + "_" + forceOption + "." + ext;
            } else {
                return prefixe + "." + ext;
            }
        }
        return nomFichier; // Retourne le nom original si le format ne correspond pas
    }

}
