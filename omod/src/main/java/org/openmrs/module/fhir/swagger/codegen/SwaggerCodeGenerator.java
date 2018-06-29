/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p/>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p/>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.fhir.swagger.codegen;

import io.swagger.codegen.ClientOptInput;
import io.swagger.codegen.DefaultGenerator;
import io.swagger.codegen.config.CodegenConfigurator;
import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;
import io.swagger.util.Json;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.fhir.exception.FHIRModuleOmodException;
import org.openmrs.module.fhir.swagger.utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class SwaggerCodeGenerator {

    protected Log log = LogFactory.getLog(getClass());
    private Map<String, String> sdkLanguages = new HashMap<>();
    private static final String ZIP_ARCHIVE_NAME_PREFIX = "OPENMRS-FHIR-CLIENT";
    public static final String JSON_EXTENSION = ".json";


    public SwaggerCodeGenerator() {
        //Supported SDK languages
        sdkLanguages.put("java", "io.swagger.codegen.languages.JavaClientCodegen");
        sdkLanguages.put("android", "io.swagger.codegen.languages.AndroidClientCodegen");
        sdkLanguages.put("csharp", "io.swagger.codegen.languages.CSharpClientCodegen");
        sdkLanguages.put("cpp", "io.swagger.codegen.languages.CppRestClientCodegen");
        sdkLanguages.put("dart", "io.swagger.codegen.languages.DartClientCodegen");
        sdkLanguages.put("flash", "io.swagger.codegen.languages.FlashClientCodegen");
        sdkLanguages.put("go", "io.swagger.codegen.languages.GoClientCodegen");
        sdkLanguages.put("groovy", "io.swagger.codegen.languages.GroovyClientCodegen");
        sdkLanguages.put("javascript", "io.swagger.codegen.languages.JavascriptClientCodegen");
        sdkLanguages.put("jmeter", "io.swagger.codegen.languages.JMeterCodegen");
        sdkLanguages.put("nodejs", "io.swagger.codegen.languages.NodeJSServerCodegen");
        sdkLanguages.put("perl", "io.swagger.codegen.languages.PerlClientCodegen");
        sdkLanguages.put("php", "io.swagger.codegen.languages.PhpClientCodegen");
        sdkLanguages.put("python", "io.swagger.codegen.languages.PythonClientCodegen");
        sdkLanguages.put("ruby", "io.swagger.codegen.languages.RubyClientCodegen");
        sdkLanguages.put("scala", "io.swagger.codegen.languages.ScalaClientCodegen");
        sdkLanguages.put("swift", "io.swagger.codegen.languages.SwiftCodegen");
        sdkLanguages.put("clojure", "io.swagger.codegen.languages.ClojureClientCodegen");
        sdkLanguages.put("aspNet5", "io.swagger.codegen.languages.AspNet5ServerCodegen");
        sdkLanguages.put("asyncScala", "io.swagger.codegen.languages.AsyncScalaClientCodegen");
        sdkLanguages.put("spring", "io.swagger.codegen.languages.SpringCodegen");
        sdkLanguages.put("csharpDotNet2", "io.swagger.codegen.languages.CsharpDotNet2ClientCodegen");
        sdkLanguages.put("haskell", "io.swagger.codegen.languages.HaskellServantCodegen");
    }


    /**
     * Generates the swagger SDK of given language
     *
     * @param language preferred language to generate the SDK
     * @throws FHIRModuleOmodException if failed to generate the SDK
     */
    public String generateSDK(String language, String swaggerJson) throws FHIRModuleOmodException {

        Swagger swaggerDoc = new SwaggerParser().parse(swaggerJson);

        if (swaggerDoc == null) {
            log.error("Error while parsing retrieved swagger definition");
        }

        //Format the swagger definition as a string before writing to the file.
        String formattedSwaggerDef = Json.pretty(swaggerDoc);
        Path tempSdkGenDir = null;
        File swaggerDefFile = null;

        try {
            //Create a temporary directory to store the API files
            tempSdkGenDir = Files.createTempDirectory(ZIP_ARCHIVE_NAME_PREFIX + "_" + language + "_");

            //Create a temporary file to store the swagger definition
            swaggerDefFile = Files.createTempFile(tempSdkGenDir,
                    ZIP_ARCHIVE_NAME_PREFIX + "_" + language,
                    JSON_EXTENSION).toFile();
        } catch (IOException e) {
            throw new FHIRModuleOmodException("Error creating temporary directory or " +
                                                                                "json file for swagger definition!", e);
        }

        String tempZipFilePath = "";
        if (swaggerDefFile.exists()) {

            try (Writer swaggerFileWriter = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(swaggerDefFile.getAbsoluteFile()), "UTF-8"))) {

                swaggerFileWriter.write(formattedSwaggerDef);

                log.debug("Writing the swagger definition was sucessful to file " + swaggerDefFile.getAbsolutePath());
            } catch (IOException e) {
                throw new FHIRModuleOmodException("Error writing swagger definition to file in " + tempSdkGenDir, e);
            }

            //Generate the SDK for the specified language
            generateSdkForSwaggerDef(language, swaggerDefFile.getAbsolutePath(), tempSdkGenDir.toString());
            log.debug("Generating SDK for the swagger definition"
                                    + swaggerDefFile.getAbsolutePath() + "was successful.");
            String archiveName = ZIP_ARCHIVE_NAME_PREFIX + "_" + language;
            tempZipFilePath = tempSdkGenDir + File.separator + archiveName + ".zip";
            Utils.archiveDirectory(tempSdkGenDir.toString(),
                    tempSdkGenDir.toString(),
                    archiveName);
            log.debug("Generating the archive was successful for directory" +  tempSdkGenDir.toString());
        } else {
            log.info("Swagger definition file not found!");
        }

        try {
            //Set deleteOnExit property to generated SDK directory, all sub directories and files.
            recursiveDeleteOnExit(tempSdkGenDir);
        } catch (IOException e) {
            log.error("Error occurred while deleting temporary directory ", e);
        }
        return tempZipFilePath;

    }

    /**
     * This method generates the SDK for the specified swagger definition file
     *
     * @param language Preferred SDK language
     * @param swaggerDefLocation Location of the swagger definition file
     * @param tempOutputDirectory Output directory for the generated SDK files
     */
    private void generateSdkForSwaggerDef(String language, String swaggerDefLocation, String tempOutputDirectory) {
        CodegenConfigurator codegenConfigurator = new CodegenConfigurator();
        codegenConfigurator.setInputSpec(swaggerDefLocation);
        codegenConfigurator.setOutputDir(tempOutputDirectory);
        codegenConfigurator.setLang(sdkLanguages.get(language));
        final ClientOptInput clientOptInput = codegenConfigurator.toClientOptInput();
        new DefaultGenerator().opts(clientOptInput).generate();
    }

    /**
     * This method used to delete the directory that created during the sdk generation
     *
     * @param path Path to the root directory which needs to be deleted on JVM exit.
     * @throws IOException if something went wrong
     */
    public static void recursiveDeleteOnExit(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

            public FileVisitResult visitFile(Path file,
                                             @SuppressWarnings("unused") BasicFileAttributes attrs) {
                file.toFile().deleteOnExit();
                return FileVisitResult.CONTINUE;
            }

            public FileVisitResult preVisitDirectory(Path dir,
                                                     @SuppressWarnings("unused") BasicFileAttributes attrs) {
                dir.toFile().deleteOnExit();
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
