/*
 * JBoss, Home of Professional Open Source  
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.impl.base.exporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Archives;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.impl.base.TestIOUtil;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.Assert;
import org.junit.Test;

/**
 * ExplodedExporterTestCase
 * 
 * TestCase to ensure that the {@link ExplodedExporter} correctly exports archive.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ExplodedExporterTestCase extends ExportTestBase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ExplodedExporterTestCase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensure an archive can be exported to an exploded directory.
    * 
    * @throws Exception
    */
   @Test
   public void testExportExploded() throws Exception
   {
      log.info("testExportExploded");

      // Get a temp directory
      File tempDirectory = createTempDirectory("testExportExploded");

      // Get an archive instance
      Archive<?> archive = createArchiveWithAssets();

      // Export as Exploded directory
      File explodedDirectory = archive.as(ExplodedExporter.class).exportExploded(tempDirectory);

      // Validate the exploded directory was created 
      Assert.assertNotNull(explodedDirectory);

      // Assert the directory has the correct name
      File expectedDirectory = new File(tempDirectory, archive.getName());
      Assert.assertEquals(expectedDirectory, explodedDirectory);

      // Validate entries were written out
      assertAssetInExploded(explodedDirectory, PATH_ONE, ASSET_ONE);
      assertAssetInExploded(explodedDirectory, PATH_TWO, ASSET_TWO);
   }

   /**
    * Ensure an archive exported to an exploded directory properly explodes nested archives.
    * 
    * @throws Exception
    */
   @Test
   public void testExportNestedExploded() throws Exception
   {
      log.info("testExportNestedExploded");

      // Get a temp directory
      File tempDirectory = createTempDirectory("testExportNestedExploded");

      // Get an archive instance
      Archive<?> archive = createArchiveWithNestedArchives();

      // Export as Exploded directory
      File explodedDirectory = archive.as(ExplodedExporter.class).exportExploded(tempDirectory);

      // Validate the exploded directory was created 
      Assert.assertNotNull(explodedDirectory);

      // Assert the directory has the correct name
      File expectedDirectory = new File(tempDirectory, archive.getName());
      Assert.assertEquals(expectedDirectory, explodedDirectory);

      // Validate nested archive entries were written out
      Path nestedArchivePath = new BasicPath(NAME_NESTED_ARCHIVE);

      assertAssetInExploded(explodedDirectory, new BasicPath(nestedArchivePath, PATH_ONE), ASSET_ONE);
      assertAssetInExploded(explodedDirectory, new BasicPath(nestedArchivePath, PATH_TWO), ASSET_TWO);

      Path nestedArchivePathTwo = new BasicPath(NESTED_PATH, NAME_NESTED_ARCHIVE_2);

      assertAssetInExploded(explodedDirectory, new BasicPath(nestedArchivePathTwo, PATH_ONE), ASSET_ONE);
      assertAssetInExploded(explodedDirectory, new BasicPath(nestedArchivePathTwo, PATH_TWO), ASSET_TWO);
   }

   /**
    * Ensure an baseDirectory is required to export.
    * 
    * @throws Exception
    */
   @Test(expected = IllegalArgumentException.class)
   public void testExportExplodedRequiresBaseDirectroy() throws Exception
   {
      log.info("testExportExplodedRequiresBaseDirectroy");

      Archives.create("test.jar", ExplodedExporter.class).exportExploded(null);
   }

   /**
    * Ensure an baseDirectory must exist is required to export.
    * 
    * @throws Exception
    */
   @Test(expected = IllegalArgumentException.class)
   public void testExportExplodedRequiresExistingDirectory() throws Exception
   {
      log.info("testExportExplodedRequiresExisitingDirectroy");

      final File directory = this.getNonexistantDirectory();
      Archives.create("test.jar", ExplodedExporter.class).exportExploded(directory);
   }

   /**
    * Ensure ExpolodedExporter requires a directory
    */
   @Test(expected = IllegalArgumentException.class)
   public void testExportExplodedRequiresValidDirectory() throws Exception
   {
      log.info("testExportExplodedRequiresValidDirectory");
      final File nonDirectory = new File(this.getTarget(), "tempFile.txt");
      Archives.create("test.jar", ExplodedExporter.class).exportExploded(nonDirectory);
   }

   /**
    * Ensure an ArchiveExportException is thrown when output directory can not be created
    */
   @Test(expected = ArchiveExportException.class)
   public void testExportExplodedOutpuDirCreationFails() throws Exception
   {
      log.info("testExportExplodedOutpuDirCreationFails");
      final File directory = createTempDirectory("testExportExplodedOutpuDirCreationFails");
      // Will cause the creation of Archive directory to fail
      final File existingFile = new File(directory, NAME_ARCHIVE);
      final boolean created = existingFile.createNewFile();
      TestCase.assertEquals("Could not create test file",true, created);
      Archives.create(NAME_ARCHIVE, ExplodedExporter.class).exportExploded(directory);
   }

   /**
    * Ensure ArchiveException is thrown if Asset can not be written
    */
   @Test(expected = ArchiveExportException.class)
   public void testExportExplodedThrowsExceptionOnAssetWrite() throws Exception
   {
      log.info("testExportExplodedThrowsExceptionOnAssetWrite");
      Archive<?> archive = createArchiveWithAssets();
      archive.add(new Asset()
      {

         @Override
         public InputStream openStream()
         {
            throw new RuntimeException("Mock Esception getting Stream");
         }

      }, new BasicPath("badAsset"));
      final File directory = createTempDirectory("testExportExplodedThrowsExceptionOnAssetWrite");

      archive.as(ExplodedExporter.class).exportExploded(directory);
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains a reference to a directory that does not exist
    */
   private File getNonexistantDirectory()
   {
      final File directory = new File(this.getTarget(), "someNonExistentDirectory");
      if (directory.exists())
      {
         TestIOUtil.deleteDirectory(directory);
      }
      Assert.assertTrue("Precondition Failure: Directory should not exist: " + directory, !directory.exists());
      return directory;
   }

   /**
    * Assert an asset is actually in the exploded directory
    * 
    * @throws FileNotFoundException 
    * @throws IOException 
    * @throws IllegalArgumentException 
    */
   private void assertAssetInExploded(File explodedDirectory, Path path, Asset asset) throws FileNotFoundException
   {
      File assetFile = new File(explodedDirectory, path.get());
      Assert.assertNotNull(assetFile);
      Assert.assertTrue(assetFile.exists());
      byte[] expectedContents = IOUtil.asByteArray(asset.openStream());

      InputStream inputStream = new FileInputStream(assetFile);

      byte[] actualContents = IOUtil.asByteArray(inputStream);
      Assert.assertArrayEquals(expectedContents, actualContents);
   }

}
