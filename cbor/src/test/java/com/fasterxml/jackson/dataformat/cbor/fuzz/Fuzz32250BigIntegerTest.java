package com.fasterxml.jackson.dataformat.cbor.fuzz;

import java.math.BigInteger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.CBORTestBase;

public class Fuzz32250BigIntegerTest extends CBORTestBase
{
    private final ObjectMapper MAPPER = cborMapper();

    public void testInvalidShortText() throws Exception
    {
        final byte[] input = new byte[] {
                (byte) 0xC3,
                0x5F, (byte) 0xFF
        };
        JsonNode root = MAPPER.readTree(input);
        assertTrue(root.isNumber());
        assertTrue(root.isBigInteger());
        assertEquals(BigInteger.ZERO, root.bigIntegerValue());
    }
}
