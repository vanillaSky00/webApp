package com.blurnest.imageuploader.entity;

import jakarta.persistence.*;//use jpa id
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

@Entity
@Table(name= "image_data")
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
    //@Lob, it is BIGINT, mismatch to BYTEA
    @Column(name = "image_data", columnDefinition = "BYTEA", length = 1048576)// 1MB = 1024 * 1024 bytes
    private byte[] imageData;
}
