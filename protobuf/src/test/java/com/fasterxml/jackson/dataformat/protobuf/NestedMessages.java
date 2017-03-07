package com.fasterxml.jackson.dataformat.protobuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.protobuf.ProtobufMapper;
import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufSchema;
import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufSchemaLoader;

public class NestedMessages {

	private static final String PROTO = //
			"message TestObject {\n" + //
					"optional string a = 1;\n" + //
					"optional TestSub b = 2;\n" + //
					"}\n" + //
					"message TestSub {;\n" + //
					"optional string c = 2;\n" + //
					"optional string b = 3;\n" + //
					"optional TestSubSub d = 4;\n" + //
					"}\n" + //
					"message TestSubSub {;\n" + //
					"optional string a = 1;\n" + //
					"}\n"; //

	public static class TestObject {
		String a;
		TestSub b;

		public String getA() {
			return a;
		}

		public void setA(String a) {
			this.a = a;
		}

		public TestSub getB() {
			return b;
		}

		public void setB(TestSub b) {
			this.b = b;
		}

		// The following annotation to force "d" as first field is needed before
		// the fix
		// @JsonPropertyOrder(value={"d", "b", "c"})
		public static class TestSub {
			String b;
			String c;
			TestSubSub d;

			public String getB() {
				return b;
			}

			public void setB(String b) {
				this.b = b;
			}

			public String getC() {
				return c;
			}

			public void setC(String c) {
				this.c = c;
			}

			public TestSubSub getD() {
				return d;
			}

			public void setD(TestSubSub d) {
				this.d = d;
			}

			public static class TestSubSub {
				String a;

				public String getA() {
					return a;
				}

				public void setA(String a) {
					this.a = a;
				}
			}
		}
	}

	@Test
	public void testProto() throws IOException {

		TestObject testClass = new TestObject();
		ProtobufMapper om = new ProtobufMapper();
		ProtobufSchema s = ProtobufSchemaLoader.std.load(new ByteArrayInputStream(PROTO.getBytes()));
		ObjectReader r = om.readerFor(TestObject.class).with(s);
		ObjectWriter w = om.writer(s);
		ByteArrayOutputStream out = new ByteArrayOutputStream() {
			@Override
			public synchronized void write(byte[] b, int off, int len) {
				super.write(b, off, len);
				System.out.println("Off " + off + " len " + len);
			}
		};
		testClass.a = "value";
		testClass.b = new TestObject.TestSub();
		testClass.b.b = "value-b";
		testClass.b.c = "valc";
		// if this following row is commented out, test succeeds with old code
		testClass.b.d = new TestObject.TestSub.TestSubSub();
		testClass.b.d.a = "a-value!";

		w.writeValue(out, testClass);
		System.out.println("Size: " + out.size());

		TestObject res = r.readValue(out.toByteArray());

		Assert.assertEquals("value", res.a);
		Assert.assertEquals("valc", res.b.c);
		Assert.assertEquals("value-b", res.b.b);
	}

}
