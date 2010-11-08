package jprobe;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jprobe.thread.ThreadBranch;
import jprobe.thread.ThreadLeaf;
import jprobe.tracker.TimedStackTraceElement;
import jprobe.tracker.Tracker;

/**
 * The Probe servlet provides a very basic web interface for inspecting the java
 * thread structure in the current JVM. It's minimal both in the way it acquires
 * the thread information (simply using Thread and ThreadGroup), and in the way
 * it displays this information.
 */
public class ProbeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Pattern FILTER_PATTERN = Pattern.compile("com\\..*");
	private static final int DIFF_MILLIS = 1000000;
	private static final int MAX_STACK_TRACE_LENGTH = 16;
	private static final String PASSWORD_KEY = "password";
	private static final String ID_KEY = "id";
	private static final String NAME_KEY = "name";
	private static final String SHOW_THREAD_KEY = "showthread";
	private static final String SHOW_GROUP_KEY = "showgroup";
	private static final String INTERRUPT_THREAD_KEY = "interruptthread";
	private static final String INTERRUPT_GROUP_KEY = "interruptgroup";
	private String password;
	private boolean allowInterrupt = true;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	    ServletContext context = getServletContext();
		password = context.getInitParameter(PASSWORD_KEY);
		System.out.println(password);
		allowInterrupt = "true".equals(context.getInitParameter("allow-interrupt"));
	}

	private void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
		htmlBegin(request, response);
		String passwordValue = request.getParameter(PASSWORD_KEY);
		if (password != null && !password.equals(passwordValue)) {
			handleNoPassword(request, response);
		} else {
			if (request.getParameter(INTERRUPT_THREAD_KEY) != null) {
				handleInterruptThread(request, response);
			}
			if (request.getParameter(INTERRUPT_GROUP_KEY) != null) {
				handleInterruptGroup(request, response);
			}
			if (request.getParameter(SHOW_THREAD_KEY) != null) {
				handleShowId(request, response);
			} else if (request.getParameter(SHOW_GROUP_KEY) != null) {
				handleShowGroup(request, response);
			} else {
				getListThreadsView(response.getWriter());
			}
		}
		htmlEnd(request, response);
	}

	/**
	 * @param request
	 */
	private void handleNoPassword(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter printWriter = response.getWriter();
		printWriter.write("<form action=\"\" method=\"post\">");
		printWriter.write("<p>");
		printWriter.write("<input name=\"" + PASSWORD_KEY + "\" type=\"password\" size=\"40\" />");
		printWriter.write("<input name=\"enter\" type=\"submit\" value=\"Enter\" />");
		printWriter.write("</p>");
		printWriter.write("</form>");
	}

	private void handleInterruptThread(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (allowInterrupt) {
			String idString = request.getParameter(ID_KEY);
			int id = -1;
			try {
				id = Integer.parseInt(idString);
			} catch (NumberFormatException e) {
			}
			if (id >= 0) {
				doInterruptThread(id, response.getWriter());
			} else {
				response.getWriter().write("Invalid thread id specified.");
			}
		} else {
			response.getWriter().write("Interrupt not activated.");
		}
	}

	private void handleInterruptGroup(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (allowInterrupt) {
			String name = request.getParameter(NAME_KEY);
			if (name != null && name.length() > 0) {
				doInterruptGroup(name, response.getWriter());
			} else {
				response.getWriter().write("No group name specified.");
			}
		} else {
			response.getWriter().write("Interrupt not activated.");
		}
	}

	private void handleShowGroup(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String groupString = request.getParameter(SHOW_GROUP_KEY);
		if (groupString.length() > 0) {
			getGroupView(groupString, System.currentTimeMillis(), response.getWriter());
		} else {
			response.getWriter().write("No group name specified.");
		}
	}

	private void handleShowId(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String idString = request.getParameter(SHOW_THREAD_KEY);
		int id = -1;
		try {
			id = Integer.parseInt(idString);
		} catch (NumberFormatException e) {
		}
		if (id >= 0) {
			getThreadView(id, response.getWriter());
		} else {
			response.getWriter().write("Invalid thread id specified.");
		}
	}

	/**
	 * @param request
	 */
	private void doInterruptThread(int id, PrintWriter printWriter) {
		ThreadLeaf thread = ThreadLeaf.getLeaf(id);
		if (thread != null) {
			boolean success = thread.doInterrupt();
			if (success) {
				printWriter.write("Thread " + thread.getFullName() + " was interrupted");
			} else {
				printWriter.write("Unable to interrupt thread " + thread.getFullName());
			}
		} else {
			printWriter.write("No thread with id " + id);
		}
	}

	/**
	 * @param request
	 */
	private void doInterruptGroup(String name, PrintWriter printWriter) {
		ThreadBranch group = ThreadBranch.getBranch(name);
		if (group != null) {
			boolean success = group.doInterrupt();
			if (success) {
				printWriter.write("Group " + group.getFullName() + " was interrupted");
			} else {
				printWriter.write("Unable to interrupt thread " + group.getFullName());
			}
		} else {
			printWriter.write("No group with name " + name);
		}
	}

	/**
	 * @param request
	 */
	private void htmlBegin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		PrintWriter printWriter = response.getWriter();
		printWriter.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" ");
		printWriter.write("\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
		printWriter.write("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><title>");
		printWriter.write("Probe");
		printWriter.write("</title><meta http-equiv=\"pragma\" content=\"no-cache\" />");
		printWriter.write("<link type=\"text/css\" rel=\"stylesheet\" href=\"probe.css\" />");
		printWriter.write("<script type=\"text/javascript\" src=\"probe.js\"></script>");
		printWriter.write("</head><body>");
		printWriter.flush();
	}

	/**
	 * @param request
	 */
	private void htmlEnd(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter printWriter = response.getWriter();
		printWriter.write("</body>");
		printWriter.write("</html>");
		printWriter.flush();
	}

	private void getListThreadsView(PrintWriter printWriter) {

		printWriter.write("<h3>Probe</h3>");
		printWriter.write("<p><a href=\"#\" onclick=\"return doPost('',{'" + PASSWORD_KEY + "':'" + password
				+ "'});\">refresh</a></p>");
		printWriter.write("<table>");
		printWriter.write("<tr class=\"head\">");
		printWriter.write("<td>Id</td>");
		printWriter.write("<td>Path</td>");
		printWriter.write("<td>Name</td>");
		printWriter.write("<td>Class</td>");
		printWriter.write("<td>Priority</td>");
		printWriter.write("<td>State</td>");
		printWriter.write("<td>Deamon</td>");
		printWriter.write("<td>Destroyed / Interrupted</td>");
		printWriter.write("<td>Accessible</td>");
		printWriter.write("<td class=\"stack\">Stack</td>");
		printWriter.write("<td>Details</td>");
		if (allowInterrupt) {
			printWriter.write("<td>Action</td>");
		}
		printWriter.write("</tr>");
		addThreadGroup(ThreadLeaf.getCurrent().getRoot(), System.currentTimeMillis(), printWriter);
		printWriter.write("</table>");
	}

	private void getThreadView(int id, PrintWriter printWriter) {
		printWriter.write("<p><a href=\"#\" onclick=\"return doPost('',{'" + PASSWORD_KEY + "':'" + password
				+ "'});\">back</a></p>");
		ThreadLeaf thread = ThreadLeaf.getLeaf(id);
		if (thread != null) {
			addThreadPlain(thread, System.currentTimeMillis(), printWriter);
		} else {
			printWriter.write("No thread with id " + id);
		}
	}

	private void getGroupView(String name, long timeMillis, PrintWriter printWriter) {
		printWriter.write("<p><a href=\"#\" onclick=\"return doPost('',{'" + PASSWORD_KEY + "':'" + password
				+ "'});\">back</a></p>");
		boolean written = false;
		ThreadBranch group = ThreadBranch.getBranch(name);
		if (group != null) {
			for (ThreadLeaf thread : group.getThreadList()) {
				addThreadPlain(thread, timeMillis, printWriter);
				printWriter.write("\n");
				written = true;
			}
		}
		if (!written) {
			printWriter.write("No threads in group " + group);
		}
	}

	private void addThreadGroup(ThreadBranch group, long timeMillis, PrintWriter printWriter) {
		addGroupHtml(group, printWriter);
		for (ThreadLeaf t : group.getThreadList()) {
			addThreadHtml(t, timeMillis, printWriter);
		}
		for (ThreadBranch g : group.getGroupList()) {
			addThreadGroup(g, timeMillis, printWriter);
		}
	}

	private void addThreadHtml(ThreadLeaf thread, long timeMillis, PrintWriter printWriter) {
		TimedStackTraceElement[] trace = Tracker.getStackTrace(thread.getThread());
		String highlight = null;
		for (TimedStackTraceElement element : trace) {
			if (FILTER_PATTERN.matcher(element.getClassName()).matches()) {
				if (element.getStartMillis() < timeMillis - DIFF_MILLIS) {
					highlight = "alarm";
					break;
				}
				highlight = "high";
			}
		}
		if (highlight != null) {
			printWriter.write("<tr class=\"" + highlight + "\">");
		} else {
			printWriter.write("<tr>");
		}
		printWriter.write("<td>" + thread.getId() + "</td>");
		printWriter.write("<td>" + thread.getPath() + "</td>");
		printWriter.write("<td>" + thread.getName() + "</td>");
		printWriter.write("<td>" + thread.getClassName() + "</td>");
		printWriter.write("<td>" + thread.getPriority() + "</td>");
		printWriter.write("<td>" + thread.getState() + "</td>");
		printWriter.write("<td class=\"" + thread.isDaemon() + "\">" + thread.isDaemon() + "</td>");
		printWriter.write("<td class=\"" + thread.isInterrupted() + "\">" + thread.isInterrupted() + "</td>");
		printWriter.write("<td class=\"" + thread.isAccessible() + "\">" + thread.isAccessible() + "</td>");
		printWriter.write("<td class=\"stack\">");
		if (trace.length > 0) {
			int index = 0;
			for (TimedStackTraceElement e : trace) {
				printWriter.write(e.toString(timeMillis) + "<br/>");
				if (++index >= MAX_STACK_TRACE_LENGTH) {
					printWriter.write("...");
					break;
				}
			}
		}
		printWriter.write("</td>");
		printWriter.write("<td>");
		printWriter.write("<a href=\"#\" onclick=\"return doPost('',{'" + PASSWORD_KEY + "':'" + password + "','"
				+ SHOW_THREAD_KEY + "':'" + thread.getId() + "'});\">show</a>");
		printWriter.write("</td>");
		if (allowInterrupt) {
			printWriter.write("<td>");
			printWriter.write("<a href=\"#\" onclick=\"return confirm('Are you sure to stop thread "
					+ thread.getFullName() + "?') && doPost('',{'" + PASSWORD_KEY + "':'" + password + "','"
					+ ID_KEY + "':'" + thread.getId() + "','" + INTERRUPT_THREAD_KEY + "':'" + true
					+ "'});\">interrupt</a>");
			printWriter.write("</td>");
		}
		printWriter.write("</tr>");
	}

	private void addGroupHtml(ThreadBranch group, PrintWriter printWriter) {
		printWriter.write("<tr>");
		printWriter.write("<td>" + "" + "</td>");
		printWriter.write("<td>" + group.getPath() + "</td>");
		printWriter.write("<td>" + group.getName() + "</td>");
		printWriter.write("<td>" + group.getClassName() + "</td>");
		printWriter.write("<td>" + group.getPriority() + "</td>");
		printWriter.write("<td>" + "" + "</td>");
		printWriter.write("<td class=\"" + group.isDaemon() + "\">" + group.isDaemon() + "</td>");
		printWriter.write("<td class=\"" + group.isDestroyed() + "\">" + group.isDestroyed() + "</td>");
		printWriter.write("<td class=\"" + group.isAccessible() + "\">" + group.isAccessible() + "</td>");
		printWriter.write("<td>" + "" + "</td>");
		printWriter.write("<td>");
		printWriter.write("<a href=\"#\" onclick=\"return doPost('',{'" + PASSWORD_KEY + "':'" + password + "','"
				+ SHOW_GROUP_KEY + "':'" + group.getName() + "'});\">show_all</a>");
		printWriter.write("</td>");
		if (allowInterrupt) {
			printWriter.write("<td>");
			printWriter.write("<a href=\"#\" onclick=\"return confirm('Are you sure to stop all threads in "
					+ group.getFullName() + "?') && doPost('',{'" + PASSWORD_KEY + "':'" + password + "','"
					+ NAME_KEY + "':'" + group.getName() + "','" + INTERRUPT_GROUP_KEY + "':'" + true
					+ "'});\">interrupt_all</a>");
			printWriter.write("</td>");
		}
		printWriter.write("</tr>");
	}

	private void addThreadPlain(ThreadLeaf thread, long timeMillis, PrintWriter printWriter) {
		TimedStackTraceElement[] trace = Tracker.getStackTrace(thread.getThread());
		printWriter.write("<pre>");
		printWriter.write("id         : " + thread.getId() + "\n");
		printWriter.write("name       : " + thread.getName() + "\n");
		printWriter.write("class      : " + thread.getClassName() + "\n");
		printWriter.write("priority   : " + thread.getPriority() + "\n");
		printWriter.write("state      : " + thread.getState() + "\n");
		printWriter.write("deamon     : " + thread.isDaemon() + "\n");
		printWriter.write("interrupted: " + thread.isInterrupted() + "\n");
		printWriter.write("alive      : " + thread.isAlive() + "\n");
		printWriter.write("accessible : " + thread.isAccessible() + "\n");
		printWriter.write("hashcode   : " + thread.hashCode() + "\n");
		printWriter.write("path       : " + thread.getPath() + "\n");
		printWriter.write("stacktrace : # = " + trace.length + "\n");
		int index = trace.length - 1;
		for (TimedStackTraceElement element : trace) {
			printWriter.write(String.format("  %3d: ", Integer.valueOf(index--)));
			printWriter.write(element.toString(timeMillis));
			printWriter.write("\n");
		}
		printWriter.write("</pre>");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handle(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		handle(request, response);
	}
}
