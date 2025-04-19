package com.vanillasky.imageuploader.entity;

import jakarta.persistence.*;//use jpa id
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name= "ImageData")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private String name;
    private String type;
    @Lob
    @Column(name = "imagedata", length = 1000)
    private String filePath;

    private byte[] imageData;
}
