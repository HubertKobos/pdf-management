package com.example.watermarkService;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Arrays;

public class CustomMultipartFile implements MultipartFile, Serializable {

    private String name;
    private String originalFilename;
    private String contentType;
    private byte[] bytes;
    private transient InputStream inputStream;

    public CustomMultipartFile(byte[] bytes, String name, String originalFilename, String contentType) {
        this.bytes = bytes;
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.inputStream = new ByteArrayInputStream(bytes);

    }

    public CustomMultipartFile(byte[] bytes, String name, String contentType) {
        this.bytes = bytes;
        this.name = name;

        this.contentType = contentType;
        this.inputStream = new ByteArrayInputStream(bytes);

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return bytes == null || bytes.length == 0;
    }

    @Override
    public long getSize() {
        return bytes.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return bytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try (FileOutputStream outputStream = new FileOutputStream(dest)) {
            outputStream.write(bytes);
        }
    }

    @Serial
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(name);
        out.writeObject(originalFilename);
        out.writeObject(contentType);
        out.writeInt(bytes.length);
        out.write(bytes);
    }

    @Serial
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        name = (String) in.readObject();
        originalFilename = (String) in.readObject();
        contentType = (String) in.readObject();
        int length = in.readInt();
        bytes = new byte[length];
        in.readFully(bytes);
        inputStream = new ByteArrayInputStream(bytes);
    }
}
