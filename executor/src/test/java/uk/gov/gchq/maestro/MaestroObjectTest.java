package uk.gov.gchq.maestro;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public abstract class MaestroObjectTest {
    @Test
    public void shouldJSONSerialise() throws SerialisationException {
        final Executor executor = getTestObject();

        final String executorString = getJSONString();

        final byte[] serialise = JSONSerialiser.serialise(executor, true);
        assertEquals(executorString, new String(serialise));
        assertEquals(executor, JSONSerialiser.deserialise(serialise, Executor.class));
    }

    protected abstract String getJSONString();

    protected abstract Executor getTestObject();
}
