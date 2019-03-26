package edu.duke.rs.baseProject;

public abstract class BaseWebController {
	private static final String VIEW_REDIRECT_PREFIX = "redirect:";
	
	protected String createRedirectViewPath(String path) {
        StringBuilder builder = new StringBuilder();
        builder.append(VIEW_REDIRECT_PREFIX);
        builder.append(path);
        return builder.toString();
    }

}
