package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import java.io.*;
import java.nio.file.Path;

class OpenRs2ToRuneLiteXteaFormat {
    public static void main(String[] args) throws IOException {
        try (
                var bw = new BufferedWriter(new FileWriter(Path.of(System.getProperty("user.home"), "Desktop", "xteas.json").toString()));
                var br = new BufferedReader(new FileReader(Path.of(System.getProperty("user.home"), "Desktop", "openrs2xteas.json").toString()))
        ) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("archive") || line.contains("group") || line.contains("name_hash") || line.contains("name")) {
                    continue;
                }

                if (line.contains("mapsquare")) {
                    line = line.replace("mapsquare", "region");
                } else if (line.contains("key")) {
                    line = line.replace("key", "keys");
                }

                bw.newLine();
                bw.write(line);
            }
        }
    }
}
