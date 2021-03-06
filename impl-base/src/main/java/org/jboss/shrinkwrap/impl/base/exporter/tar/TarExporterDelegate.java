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
package org.jboss.shrinkwrap.impl.base.exporter.tar;

import java.io.InputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate;

/**
 * Implementation of an exporter for the TAR format
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
class TarExporterDelegate extends AbstractExporterDelegate<InputStream> {

    /**
     * Creates a new exporter delegate for exporting archives as TAR
     */
    TarExporterDelegate(final Archive<?> archive) {
        super(archive);
    }

    @Override
    protected void processNode(final ArchivePath path, final Node node) {
        // do nothing
    }

    @Override
    protected InputStream getResult() {
        return new TarOnDemandInputStream(getArchive());
    }

}
