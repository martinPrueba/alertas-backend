package com.kim21.alertas.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GroupUtils {

    public static List<String> getCurrentUserGroups() 
    {
        List<String> groups = new ArrayList<>();
        try 
        {
            // Comando PowerShell completo
            String command = "powershell.exe -Command \"whoami /groups | Select-Object -Skip 2 | ForEach-Object { ($_ -split '\\s{2,}')[0] }\"";

            ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
            builder.redirectErrorStream(true);

            Process process = builder.start();

            // Leer la salida del comando
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) 
                    {
                String line;
                while ((line = reader.readLine()) != null) 
                {
                    if (!line.trim().isEmpty())
                     {
                        groups.add(line.trim());
                    }
                }
            }

            process.waitFor();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return groups;
    }
}