package com.timreset.arduino.test;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.timreset.arduino.parser.Parser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Test all files.
 *
 * @author Tim
 * @date 19.11.2015
 */
@RunWith(Parameterized.class)
public class ParserTest {

	@Parameterized.Parameters(name = "{index}: File name: {0}")
	public static Iterable<?> data() throws IOException {
		ClassPath classPath = ClassPath.from(ParserTest.class.getClassLoader());
		ImmutableSet<ClassPath.ClassInfo> classes = classPath.getTopLevelClasses("com.timreset.arduino.test.code");
		return classes.stream().map(ClassPath.ClassInfo::getSimpleName).collect(Collectors.toList());
		//		return classes;//Arrays.asList(new Object[]{"SerialExample", "Blink", "AnalogReadSerial"});
	}

	private final String fileName;

	public ParserTest(String fileName) {
		this.fileName = fileName;
	}

	@Test
	public void test() throws IOException {
		final String actualCode = Arrays.stream(
						new Parser(Paths.get("src/test/java/com/timreset/arduino/test/code/" + fileName + ".java"),
										"build/classes/main", "build/classes/test").parseWithVisitor().split("\n")).map(String::trim).filter(
						s -> !s.isEmpty()).collect(Collectors.joining("\n"));
		final String expectedCode = Files.readAllLines(
						Paths.get("src/test/resources/com/timreset/arduino/test/code/" + fileName + ".ino")).stream().map(
						String::trim).filter(s -> !s.isEmpty()).collect(Collectors.joining("\n"));
		Assert.assertEquals(expectedCode, actualCode);
	}
}
