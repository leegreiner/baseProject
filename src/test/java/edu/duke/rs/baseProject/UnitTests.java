package edu.duke.rs.baseProject;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
@SelectPackages("edu.duke.rs.baseProject")
@IncludeClassNamePatterns({"^.*UnitTest?$"})
public class UnitTests {
}
