package uk.gov.gchq.maestro;

import org.junit.Test;

import uk.gov.gchq.maestro.exception.SerialisationException;
import uk.gov.gchq.maestro.jsonserialisation.JSONSerialiser;

import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;

public abstract class MaestroObjectTest {
    @Test
    public void shouldJSONSerialise() throws SerialisationException {
        final Executor executor = getTestObject();

        final String executorString = getJSONString();
        requireNonNull(executorString);
        final byte[] serialise = JSONSerialiser.serialise(executor, true);
        assertEquals(executorString, new String(serialise));
        assertEquals(executor, JSONSerialiser.deserialise(serialise, Executor.class));
    }

    protected abstract String getJSONString();

    protected abstract Executor getTestObject();
}
