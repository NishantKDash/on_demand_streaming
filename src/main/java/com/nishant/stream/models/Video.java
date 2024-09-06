package com.nishant.stream.models;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Owner owner;
    private String s3Path;
    private String bucket;
    public Video() {
    }

    public Video(Owner owner, String s3Path, String bucket) {
        this.owner = owner;
        this.s3Path = s3Path;
        this.bucket = bucket;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getS3Path() {
        return s3Path;
    }

    public void setS3Path(String s3Path) {
        this.s3Path = s3Path;
    }

    public String getBucket(){
        return bucket;
    }

    public void setBucket(String bucket){
        this.bucket = bucket;
    }
}
