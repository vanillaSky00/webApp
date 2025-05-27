package com.vanillasky.imageuploader.model.image;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Inflater;


@Component
public class DecompressionProcessor implements ImageProcessor {

//    public static byte[] decompressImage(byte[] data) {
//        Inflater inflater = new Inflater();
//        inflater.setInput(data);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
//        byte[] tmp = new byte[4*1024];
//        try {
//            while (!inflater.finished()) {
//                int count = inflater.inflate(tmp);
//                outputStream.write(tmp, 0, count);
//            }
//            outputStream.close();
//        } catch (Exception ignored) {
//        }
//        return outputStream.toByteArray();
//    }

    @Override
    public String key() {
        return "decompress";
    }

    @Override
    public byte[] process(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream(data.length * 2)){
            byte[] buffer = new byte[4096];

            while(!inflater.finished()){
                int n;
                //inflate() can throw a checked DataFormatException, but deflater does not
                try {
                    n = inflater.inflate(buffer);
                } catch (Exception e){
                    throw new RuntimeException("Bad compressed data",e);
                }
                out.write(buffer, 0, n);
            }
            return out.toByteArray();

        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
