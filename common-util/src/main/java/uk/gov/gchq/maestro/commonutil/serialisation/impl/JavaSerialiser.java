/*
 * Copyright 2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.gchq.maestro.commonutil.serialisation.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.gchq.maestro.commonutil.exception.SerialisationException;
import uk.gov.gchq.maestro.commonutil.serialisation.ToBytesSerialiser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * This class is used to serialise and deserialise objects in java.
 */
public class JavaSerialiser implements ToBytesSerialiser<Object> {
    private static final Class<Serializable> SERIALISABLE = Serializable.class;
    private static final Logger LOGGER = LoggerFactory.getLogger(JavaSerialiser.class);
    private static final long serialVersionUID = 3304435151099053026L;

    @Override
    public byte[] serialise(final Object object) throws SerialisationException {
        ObjectOutputStream out = null;
        ByteArrayOutputStream byteOut = null;
        try {
            byteOut = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteOut);
            out.writeObject(object);
            return byteOut.toByteArray();
        } catch (final IOException e) {
            throw new SerialisationException("Unable to serialise given object of class: " + object.getClass().getName() + ", does it implement the serializable interface?", e);
        } finally {
            close(out);
            close(byteOut);
        }
    }

    @Override
    public Object deserialise(final byte[] allBytes, final int offset, final int length) throws SerialisationException {
        try (final ObjectInputStream is = new ObjectInputStream(new ByteArrayInputStream(allBytes, offset, length))) {
            return is.readObject();
        } catch (final ClassNotFoundException | IOException e) {
            throw new SerialisationException("Unable to deserialise object, failed to recreate object", e);
        }
    }

    @Override
    public Object deserialise(final byte[] bytes) throws SerialisationException {
        return deserialise(bytes, 0, bytes.length);
    }

    @Override
    public Object deserialiseEmpty() {
        return null;
    }

    private void close(final Closeable close) {
        if (null != close) {
            try {
                close.close();
            } catch (final IOException e) {
                LOGGER.warn("Resource leak: unable to close stream in JavaSerialiser.class", e);
            }
        }
    }

    @Override
    public boolean canHandle(final Class clazz) {
        return SERIALISABLE.isAssignableFrom(clazz);
    }

    @Override
    public boolean preservesObjectOrdering() {
        return false;
    }

    @Override
    public boolean isConsistent() {
        return false;
    }
}
