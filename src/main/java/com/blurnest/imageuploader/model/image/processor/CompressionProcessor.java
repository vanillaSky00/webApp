package com.blurnest.imageuploader.model.image.processor;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;




@Component
public class CompressionProcessor implements ImageProcessor {

//    public static byte[] compressImage(byte[] data) {
//        Deflater deflater = new Deflater();
//        deflater.setLevel(Deflater.BEST_COMPRESSION);
//        deflater.setInput(data);
//        deflater.finish();
//
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
//        byte[] tmp = new byte[4*1024];
//        while (!deflater.finished()) {
//            int size = deflater.deflate(tmp);
//            outputStream.write(tmp, 0, size);
//        }
//        try {
//            outputStream.close();
//        } catch (Exception ignored) {
//
//        }
//        return outputStream.toByteArray();
//    }

    @Override
    public String key() {
        return "compress";
    }

    @Override
    public byte[] process(byte[] data) {
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(data); //feed the input
        deflater.finish();       //tell the deflater no more inout is coming

        // compressed result into memory
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(data.length)) {
            byte[] buffer = new byte[4096];//a 4 KB working chunk for each loop, not limit size for total data
            while(!deflater.finished()){
                out.write(buffer, 0, deflater.deflate(buffer)); //append each chunk to the output stream
            }
                return out.toByteArray();
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
