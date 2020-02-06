package com.ggp.connector.io;

import java.io.*;

/**
 * @author ggp
 * @Date 2020/2/6 20:59
 * @Description 自动刷新当调用print()或者println()方法时
 */
public class ResponseWriter extends PrintWriter {
    public ResponseWriter(OutputStreamWriter writer) {
        super(writer);
    }

    @Override
    public void print(boolean b) {
        super.print(b);
        super.flush();
    }

    @Override
    public void print(char c) {
        super.print(c);
        super.flush();
    }

    @Override
    public void print(char ca[]) {
        super.print(ca);
        super.flush();
    }

    @Override
    public void print(double d) {
        super.print(d);
        super.flush();
    }

    @Override
    public void print(float f) {
        super.print(f);
        super.flush();
    }

    @Override
    public void print(int i) {
        super.print(i);
        super.flush();
    }

    @Override
    public void print(long l) {
        super.print(l);
        super.flush();
    }

    @Override
    public void print(Object o) {
        super.print(o);
        super.flush();
    }

    @Override
    public void print(String s) {
        super.print(s);
        super.flush();
    }

    @Override
    public void println() {
        super.println();
        super.flush();
    }

    @Override
    public void println(boolean b) {
        super.println(b);
        super.flush();
    }

    @Override
    public void println(char c) {
        super.println(c);
        super.flush();
    }

    @Override
    public void println(char ca[]) {
        super.println(ca);
        super.flush();
    }

    @Override
    public void println(double d) {
        super.println(d);
        super.flush();
    }

    @Override
    public void println(float f) {
        super.println(f);
        super.flush();
    }

    @Override
    public void println(int i) {
        super.println(i);
        super.flush();
    }

    @Override
    public void println(long l) {
        super.println(l);
        super.flush();
    }

    @Override
    public void println(Object o) {
        super.println(o);
        super.flush();
    }

    @Override
    public void println(String s) {
        super.println(s);
        super.flush();
    }

    public void write(char c) {
        super.write(c);
        super.flush();
    }

    @Override
    public void write(char ca[]) {
        super.write(ca);
        super.flush();
    }

    @Override
    public void write(char ca[], int off, int len) {
        super.write(ca, off, len);
        super.flush();
    }

    @Override
    public void write(String s) {
        super.write(s);
        super.flush();
    }

    @Override
    public void write(String s, int off, int len) {
        super.write(s, off, len);
        super.flush();
    }
}
