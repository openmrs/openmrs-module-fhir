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
package org.openmrs.module.fhir.swagger.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.fhir.exception.FHIRModuleOmodException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Utils {

    protected static  Log log = LogFactory.getLog(Utils.class);

    /**
     * Method used to create the archive from the given directory
     *
     * @param sourceDirectory directory to create zip archive from
     * @param archiveLocation path to the archive location, excluding archive name
     * @param archiveName     name of the archive to create
     * @throws FHIRModuleOmodException if an error occurs while creating the archive
     */
    public static void archiveDirectory(String sourceDirectory, String archiveLocation, String archiveName)
                                                                                        throws FHIRModuleOmodException {

        File directoryToZip = new File(sourceDirectory);

        List<File> fileList = new ArrayList<>();
        getAllFiles(directoryToZip, fileList);
        try {
            writeArchiveFile(directoryToZip, fileList, archiveLocation, archiveName);
        } catch (IOException e) {
            String errorMsg = "Error while writing archive file " + directoryToZip.getPath() + " to archive " +
                    archiveLocation;
            log.error(errorMsg, e);
            throw new FHIRModuleOmodException(errorMsg, e);
        }
        if (log.isDebugEnabled()) {
            log.debug("Archived SDK Zip file generated successfully" + archiveName);
        }

    }

    /**
     * Get the list of directories available under a root directory path
     *
     * @param path full path of the root directory
     * @return Set of directory path under the root directory given by path
     * @throws FHIRModuleOmodException if an error occurs while listing directories
     */
    public static Set<String> getDirectoryList(String path) throws FHIRModuleOmodException {
        Set<String> directoryNames = new HashSet<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(path))) {
            for (Path directoryPath : directoryStream) {
                directoryNames.add(directoryPath.toString());
            }
        } catch (IOException e) {
            String errorMsg = "Error while listing directories under " + path;
            log.error(errorMsg, e);
            throw new FHIRModuleOmodException(errorMsg, e);
        }
        return directoryNames;
    }

    /**
     * Queries all files under a directory recursively
     *
     * @param sourceDirectory full path to the root directory
     * @param fileList        list containing the files
     */
    private static void getAllFiles(File sourceDirectory, List<File> fileList) {
        File[] files = sourceDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                fileList.add(file);
                if (file.isDirectory()) {
                    getAllFiles(file, fileList);
                }
            }
        }
    }

    /**
     * Write the file archive
     *
     * @param directoryToZip directory location which directory should be zipped
     * @param fileList list of files
     * @param archiveLocation location which archive should be created
     * @param archiveName zip archive name
     * @throws IOException if any error occurred
     */
    private static void writeArchiveFile(File directoryToZip, List<File> fileList, String archiveLocation,
                                         String archiveName) throws IOException {

        try (FileOutputStream fileOutputStream = new FileOutputStream(archiveLocation +
                File.separator + archiveName + ".zip");
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
            for (File file : fileList) {
                if (!file.isDirectory()) {
                    addToArchive(directoryToZip, file, zipOutputStream);
                }
            }
        }
    }

    /**
     * Add files to zip archive
     *
     * @param directoryToZip directory location which directory should be zipped
     * @param file file to be added to the zip
     * @param zipOutputStream zip output stream
     * @throws IOException if any error occurred
     */
    private static void addToArchive(File directoryToZip, File file, ZipOutputStream zipOutputStream)
            throws IOException {
        // Add a file to archive
        try (FileInputStream fileInputStream = new FileInputStream(file)) {

            // Get relative path from archive directory to the specific file
            String zipFilePath = file.getCanonicalPath()
                    .substring(directoryToZip.getCanonicalPath().length() + 1, file.getCanonicalPath().length());
            if (File.separatorChar != '/') {
                zipFilePath = zipFilePath.replace(File.separatorChar, '/');
            }
            ZipEntry zipEntry = new ZipEntry(zipFilePath);
            zipOutputStream.putNextEntry(zipEntry);

            IOUtils.copy(fileInputStream, zipOutputStream);
            zipOutputStream.closeEntry();
        }
    }
}
