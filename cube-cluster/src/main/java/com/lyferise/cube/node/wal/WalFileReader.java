//package com.lyferise.cube.node.wal;
//
//import lombok.SneakyThrows;
//
//import java.io.File;
//import java.io.RandomAccessFile;
//
//public class WalFileReader {
//
//    public WalFileReader(final String path) {
//        this(new File(path));
//    }
//
//    @SneakyThrows
//    public WalFileReader(final File file) {
//        this.file = new RandomAccessFile(file, "rw");
//    }
//}