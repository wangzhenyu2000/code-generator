package com.example.codegenerator.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;

@Service
public class ZipService {

    public byte[] createZip(Map<String, String> fileMap) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            zos.setMethod(ZipOutputStream.DEFLATED);
            for (Map.Entry<String, String> entry : fileMap.entrySet()) {
                String path = entry.getKey();
                if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                String[] parts = path.split("/");
                if (parts.length > 1) {
                    StringBuilder dir = new StringBuilder();
                    for (int i = 0; i < parts.length - 1; i++) {
                        dir.append(parts[i]).append("/");
                    }
                    dir.setLength(dir.length() - 1);
                    ZipEntry dirEntry = new ZipEntry(dir.toString() + "/");
                    if (!fileMap.containsKey(dirEntry.getName())) {
                        try {
                            zos.putNextEntry(dirEntry);
                            zos.closeEntry();
                        } catch (Exception ignored) {
                        }
                    }
                }
                ZipEntry entry2 = new ZipEntry(path);
                zos.putNextEntry(entry2);
                byte[] bytes = entry.getValue().getBytes(StandardCharsets.UTF_8);
                zos.write(bytes);
                zos.closeEntry();
            }
        }
        return baos.toByteArray();
    }
}
