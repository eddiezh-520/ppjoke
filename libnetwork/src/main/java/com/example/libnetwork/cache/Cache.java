package com.example.libnetwork.cache;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "cache")
public class Cache implements Serializable {
    @PrimaryKey
    private String key;

    private byte[] data;
}
