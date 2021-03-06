/*
 * Copyright (C) 2015 PÂRIS Quentin
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.playonlinux.domain;

import org.apache.commons.lang.StringUtils;
import org.python.util.PythonInterpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

public class ScriptLegacy extends Script {
    private static final String BEGIN_PGP_KEY_BLOCK_LINE = "-----BEGIN PGP PUBLIC KEY BLOCK-----";
    private static final String END_PGP_KEY_BLOCK_LINE = "-----END PGP PUBLIC KEY BLOCK-----";


    protected ScriptLegacy(File script) {
        super(script);
    }

    @Override
    protected void executeScript(PythonInterpreter pythonInterpreter) {
        File v4wrapper = new File("src/main/python/v4wrapper.py");
        String filePath = v4wrapper.getAbsolutePath();
        pythonInterpreter.set("__file__", filePath);
        pythonInterpreter.set("__scriptToWrap__", this.getScriptFile().getAbsolutePath());
        pythonInterpreter.execfile(filePath);
    }

    @Override
    public String extractSignature() throws ParseException, IOException {
        BufferedReader bufferReader = new BufferedReader(new FileReader(this.getScriptFile()));
        StringBuilder signatureBuilder = new StringBuilder();

        String readLine;
        Boolean insideSignature = false;
        do {
            readLine = bufferReader.readLine();
            if(BEGIN_PGP_KEY_BLOCK_LINE.equals(readLine)) {
                insideSignature = true;
            }

            if(insideSignature) {
                signatureBuilder.append(readLine);
                signatureBuilder.append("\n");
            }

            if(END_PGP_KEY_BLOCK_LINE.equals(readLine)) {
                insideSignature = false;
            }
        } while(readLine != null);

        String signature = signatureBuilder.toString().trim()   ;

        if(StringUtils.isBlank(signature)) {
            throw new ParseException("The script has no valid signature!", 0);
        }
        return signature;
    }
}
