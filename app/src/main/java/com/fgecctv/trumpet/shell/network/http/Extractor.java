package com.fgecctv.trumpet.shell.network.http;

import java.io.File;
import java.io.IOException;

public interface Extractor {
    void extract(File zip, File to) throws IOException;
}
