package edu.duke.rs.baseProject;

import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages("edu.duke.rs.baseProject")
@IncludeClassNamePatterns({"^.*UnitTest?$"})
public class UnitTestSuite {
}