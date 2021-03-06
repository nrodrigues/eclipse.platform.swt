/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.tests.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Supplier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.browser.CloseWindowListener;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.OpenWindowListener;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.browser.VisibilityWindowAdapter;
import org.eclipse.swt.browser.VisibilityWindowListener;
import org.eclipse.swt.browser.WindowEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

/**
 * Automated Test Suite for class org.eclipse.swt.browser.Browser
 *
 * @see org.eclipse.swt.browser.Browser
 */
public class Test_org_eclipse_swt_browser_Browser extends Test_org_eclipse_swt_widgets_Composite {

	// CONFIG
	/** This forces tests to display the shell/browser for a brief moment. Useful to see what's going on with broken jUnits */
	boolean debug_show_browser = false;
	int     debug_show_browser_timeout_seconds = 2;

	boolean debug_print_test_names = true; // Useful to figure out which jUnit caused vm crash.
	boolean debug_verbose_output = false;

	int secondsToWaitTillFail = Math.max(5, debug_show_browser_timeout_seconds);
	// CONFIG END

	@Rule
	public TestName name = new TestName();

	Browser browser;
	boolean isWebkit1 = false;
	boolean isWebkit2 = false;


@Override
@Before
public void setUp() {
	super.setUp();

//	 Print test name if running on hudson. This makes it easier to tell in the logs which test case caused crash.
	if (SwtTestUtil.isRunningOnEclipseOrgHudsonGTK || debug_print_test_names) {
		System.out.println("Running Test_org_eclipse_swt_browser_Browser#" + name.getMethodName());
	}

	shell.setLayout(new FillLayout());
	browser = new Browser(shell, SWT.NONE);

	String shellTitle = name.getMethodName();
	if (SwtTestUtil.isGTK && browser.getBrowserType().equals("webkit")) {

		// Note, webkitGtk version is only available once Browser is instantiated.
		String webkitGtkVersionStr = System.getProperty("org.eclipse.swt.internal.webkitgtk.version"); //$NON-NLS-1$

		shellTitle = shellTitle + " Webkit version: " + webkitGtkVersionStr;

		String[] webkitGtkVersionStrParts = webkitGtkVersionStr.split("\\.");
		int[] webkitGtkVersionInts = new int[3];
		for (int i = 0; i < 3; i++) {
			webkitGtkVersionInts[i] = Integer.parseInt(webkitGtkVersionStrParts[i]);
		}

		// webkitgtk 2.5 and onwards uses webkit2.
		if (webkitGtkVersionInts[0] == 1 || (webkitGtkVersionInts[0] == 2 && webkitGtkVersionInts[1] <= 4)) {
			isWebkit1 = true;
		} else if (webkitGtkVersionInts[0] == 2 && webkitGtkVersionInts[1] > 4) {
			isWebkit2 = true;
		}
	}
	shell.setText(shellTitle);
	setWidget(browser); // For browser to occupy the whole shell, not just half of it.
}


/**
 * Test that if Browser is constructed with the parent being "null", Browser throws an exception.
 */
@Override
@Test(expected = IllegalArgumentException.class)
public void test_ConstructorLorg_eclipse_swt_widgets_CompositeI() {
	Browser browser = new Browser(shell, SWT.NONE);
	browser.dispose();
	browser = new Browser(shell, SWT.BORDER);
	// System.out.println("Test_org_eclipse_swt_browser_Browser#test_Constructor*#getBrowserType(): " + browser.getBrowserType());
	browser.dispose();
	browser = new Browser(null, SWT.NONE); // Should throw.
}

@Override
@Test
public void test_getChildren() {
	// Win32's Browser is a special case. It has 1 child by default, the OleFrame.
	// See Bug 499387 and Bug 511874
	if (SwtTestUtil.isWindows) {
		int childCount = composite.getChildren().length;
		String msg = "Browser on Win32 is a special case, the first child is an OleFrame (ActiveX control). Actual child count is: " + childCount;
		assertTrue(msg, childCount == 1);
	} else {
		super.test_getChildren();
	}
}

@Test
public void test_CloseWindowListener_closeShell() {
	Display display = Display.getCurrent();
	Shell shell = new Shell(display);
	Browser browser = new Browser(shell, SWT.NONE);
	browser.addCloseWindowListener(event -> {}); // shouldn't throw
	shell.close();
}

@Test(expected = IllegalArgumentException.class)
public void test_CloseWindowListener_addWithNullArg() {
	browser.addCloseWindowListener(null);
}

@Test(expected = IllegalArgumentException.class)
public void test_CloseWindowListener_removeWithNullArg() {
	browser.removeCloseWindowListener(null);
}

@Test
public void test_CloseWindowListener_addAndRemove () {
	CloseWindowListener listener = event -> {};
	for (int i = 0; i < 100; i++) browser.addCloseWindowListener(listener);
	for (int i = 0; i < 100; i++) browser.removeCloseWindowListener(listener);
}

@Test
public void test_LocationListener_adapter_closeShell() {
	Display display = Display.getCurrent();
	Shell shell = new Shell(display);
	Browser browser = new Browser(shell, SWT.NONE);
	LocationAdapter adapter = new LocationAdapter() {};
	browser.addLocationListener(adapter); // shouldn't throw
	shell.close();
}

@Test(expected = IllegalArgumentException.class)
public void test_LocationListener_addWithNullArg() {
	browser.addLocationListener(null);
}

@Test(expected = IllegalArgumentException.class)
public void test_LocationListener_removeWithNullArg() {
	browser.removeLocationListener(null);
}

@Test
public void test_LocationListener_addAndRemove() {
	LocationListener listener = new LocationListener() {
		@Override
		public void changed(LocationEvent event) {
		}
		@Override
		public void changing(LocationEvent event) {
		}
	};
	for (int i = 0; i < 100; i++) browser.addLocationListener(listener);
	for (int i = 0; i < 100; i++) browser.removeLocationListener(listener);
}

@Test
public void test_LocationListener_changing() {
	AtomicBoolean changingFired = new AtomicBoolean(false);
	browser.addLocationListener(new LocationAdapter() {
		@Override
		public void changing(LocationEvent event) {
			changingFired.set(true);
		}
	});
	shell.open();
	browser.setText("Hello world");
	boolean passed = waitForPassCondition(() -> changingFired.get());
	assertTrue("LocationListener.changing() event was never fixed", passed);
}
@Test
public void test_LocationListener_changed() {
	AtomicBoolean changedFired = new AtomicBoolean(false);
	browser.addLocationListener(new LocationAdapter() {
		@Override
		public void changed(LocationEvent event) {
			changedFired.set(true);
		}
	});
	shell.open();
	browser.setText("Hello world");
	boolean passed = waitForPassCondition(() -> changedFired.get());
	assertTrue("LocationListener.changing() event was never fixed", passed);
}
@Test
public void test_LocationListener_changingAndOnlyThenChanged() {
	// Test proper order of events.
	// Check that 'changed' is only fired after 'changing' has fired at least once.
	AtomicBoolean changingFired = new AtomicBoolean(false);
	AtomicBoolean changedFired = new AtomicBoolean(false);
	AtomicBoolean changedFiredTooEarly = new AtomicBoolean(false);
	AtomicBoolean finished = new AtomicBoolean(false);

	browser.addLocationListener(new LocationListener() {
		@Override
		public void changing(LocationEvent event) { // Multiple changing events can occur during a load.
				changingFired.set(true);
		}
		@Override
		public void changed(LocationEvent event) {
			if (!changingFired.get())
				changedFiredTooEarly.set(true);

			changedFired.set(true);
			finished.set(true);
		}
	});
	shell.open();
	browser.setText("Hello world");
	waitForPassCondition(() -> finished.get());

	if (finished.get() && changingFired.get() && changedFired.get() && !changedFiredTooEarly.get()) {
		return; // pass
	} else if (!finished.get()) {
		fail("Test timed out. 'changed()' never fired");
	} else {
		if (changedFiredTooEarly.get())
			fail("changed() was fired before changing(). Wrong signal order");
		else if (!changingFired.get())
			fail("changing() was never fired");
		else  {
			fail("LocationListener test failed. changing():" + changingFired.get()
			+ "  changed():" + changedFired.get() + " changedFiredTooEarly:" + changedFiredTooEarly.get());
		}
	}
}

@Test
public void test_OpenWindowListener_closeShell() {
	Display display = Display.getCurrent();
	Shell shell = new Shell(display);
	Browser browser = new Browser(shell, SWT.NONE);
	browser.addOpenWindowListener(event -> {});
	shell.close();
}

@Test(expected = IllegalArgumentException.class)
public void test_OpenWindowListener_addWithNulArg() {
	browser.addOpenWindowListener(null);
}

@Test(expected = IllegalArgumentException.class)
public void test_OpenWindowListener_removeWithNullArg() {
	browser.removeOpenWindowListener(null);
}

@Test
public void test_OpenWindowListener_addAndRemove() {
	OpenWindowListener listener = event -> {};
	for (int i = 0; i < 100; i++) browser.addOpenWindowListener(listener);
	for (int i = 0; i < 100; i++) browser.removeOpenWindowListener(listener);
}

@Test
public void test_ProgressListener_newProgressAdapter() {
	new ProgressAdapter() {};
}

@Test
public void test_ProgressListener_newProgressAdapter_closeShell() {
	Display display = Display.getCurrent();
	Shell shell = new Shell(display);
	Browser browser = new Browser(shell, SWT.NONE);
	browser.addProgressListener(new ProgressAdapter() {});
	shell.close();
}

@Test
public void test_ProgressListener_newListener_closeShell() {
	Display display = Display.getCurrent();
	Shell shell = new Shell(display);
	Browser browser = new Browser(shell, SWT.NONE);
	browser.addProgressListener(new ProgressListener() {
		@Override
		public void changed(ProgressEvent event) {
		}
		@Override
		public void completed(ProgressEvent event) {
		}
	});
	shell.close();
}

@Test(expected = IllegalArgumentException.class)
public void test_ProgressListener_addWithNullArg() {
	browser.addProgressListener(null);
}

@Test(expected = IllegalArgumentException.class)
public void test_ProgressListener_removeWithNullArg() {
	browser.removeProgressListener(null);
}

@Test
public void test_ProgressListener_addAndRemove() {
	ProgressListener listener = new ProgressListener() {
		@Override
		public void changed(ProgressEvent event) {
		}
		@Override
		public void completed(ProgressEvent event) {
		}
	};
	for (int i = 0; i < 100; i++) browser.addProgressListener(listener);
	for (int i = 0; i < 100; i++) browser.removeProgressListener(listener);
}

@Test(expected = IllegalArgumentException.class)
public void test_StatusTextListener_addWithNull() {
	browser.addStatusTextListener(null);
}

@Test(expected = IllegalArgumentException.class)
public void test_StatusTextListener_removeWithNullArg() {
	browser.removeStatusTextListener(null);
}

@Test
public void test_StatusTextListener_addAndRemove() {
	StatusTextListener listener = event -> {
	};
	for (int i = 0; i < 100; i++) browser.addStatusTextListener(listener);
	for (int i = 0; i < 100; i++) browser.removeStatusTextListener(listener);
}

/**
 * Test if hovering over a hyperlink triggers status Text change listener.
 * Logic:
 * 1) Create a page that has a hyper link (covering the whole page)
 * 2) Move shell to top left corner
 * 3) Upon compleation of page load, move cursor across whole shell.
 *    (Note, in current jUnit, browser sometimes only takes up half the shell).
 * 4) StatusTextListener should get triggered. Test passes.
 * 5) Else timeout & fail.
 *
 * Set variable "debug_show_browser" to true to see this being performed at human-observable speed.
 *
 * Note: Historically one could execute some javascript to change status bar (window.status=txt).
 * But most browsers don't support this anymore. Only hovering over a hyperlink changes status.
 *
 * StatusTextListener may be triggerd upon page load also. So this test can pass if
 * a page load sets the status text (on older browsers) or passes when the mouse hovers
 * over the hyperlink (newer Webkit2+) browser.
 */
@Test
public void test_StatusTextListener_hoverMouseOverLink() {
	AtomicBoolean statusChanged = new AtomicBoolean(false);
	int size = 500;

	// 1) Create a page that has a hyper link (covering the whole page)
	Browser browser = new Browser(shell, SWT.NONE);
	StringBuilder longhtml = new StringBuilder();
	for (int i = 0; i < 200; i++) {
		longhtml.append("text text text text text text text text text text text text text text text text text text text text text text text text<br>");
	}
	browser.setText("<a href='http://localhost'>" + longhtml + "</a>");

	// 2) Move shell to top left corner
	shell.setLocation(0, 0);
	shell.setSize(size, size);

	browser.addProgressListener(new ProgressListener() {
		@Override
		public void completed(ProgressEvent event) {
			// * 3) Upon compleation of page load, move cursor across whole shell.
			// *    (Note, in current jUnit, browser sometimes only takes up half the shell).
			Display display = event.display;
			Point cachedLocation = display.getCursorLocation();
			display.setCursorLocation(20, 10);
			browser.getBounds();
			for (int i = 0; i < size; i=i+5) {
				display.setCursorLocation(i, i);
				waitForMilliseconds(debug_show_browser ? 3 : 1); // Move mouse slower during debug.
			}
			display.setCursorLocation(cachedLocation); // for convenience of developer. Not needed for test.

		}
		@Override
		public void changed(ProgressEvent event) {}
	});

	browser.addStatusTextListener(event -> {
		statusChanged.set(true);
	});

	shell.open();
	boolean passed = waitForPassCondition(()->statusChanged.get());
	String msg = "Mouse movent over text was suppose to trigger StatusTextListener. But it didn't";
	assertTrue(msg, passed);
}

@Test
public void test_TitleListener_addListener_closeShell() {
	Display display = Display.getCurrent();
	Shell shell = new Shell(display);
	Browser browser = new Browser(shell, SWT.NONE);
	browser.addTitleListener(event -> {
	});
	shell.close();
}

@Test(expected = IllegalArgumentException.class)
public void test_TitleListener_addwithNull() {
	browser.addTitleListener(null);
}

@Test(expected = IllegalArgumentException.class)
public void test_TitleListener_removeWithNullArg() {
	browser.removeTitleListener(null);
}

@Test
public void test_TitleListener_addAndRemove() {
	TitleListener listener = event -> {};
	for (int i = 0; i < 100; i++) browser.addTitleListener(listener);
	for (int i = 0; i < 100; i++) browser.removeTitleListener(listener);
}

@Test
public void test_VisibilityWindowListener_newAdapter() {
	new VisibilityWindowAdapter() {};
}

@Test
public void test_VisibilityWindowListener_newAdapter_closeShell() {
	Display display = Display.getCurrent();
	Shell shell = new Shell(display);
	Browser browser = new Browser(shell, SWT.NONE);
	browser.addVisibilityWindowListener(new VisibilityWindowAdapter(){});
	shell.close();
}

@Test
public void test_VisibilityWindowListener_newListener_closeShell() {
	Display display = Display.getCurrent();
	Shell shell = new Shell(display);
	Browser browser = new Browser(shell, SWT.NONE);
	browser.addVisibilityWindowListener(new VisibilityWindowListener() {
		@Override
		public void hide(WindowEvent event) {
		}
		@Override
		public void show(WindowEvent event) {
		}
	});
	shell.close();
}

@Test(expected = IllegalArgumentException.class)
public void test_VisibilityWindowListener_addWithNull() {
	browser.addVisibilityWindowListener(null);
}

@Test(expected = IllegalArgumentException.class)
public void test_VisibilityWindowListener_removeWithNullArg() {
	browser.removeVisibilityWindowListener(null);
}

@Test
public void test_VisibilityWindowListener_addAndRemove() {
	VisibilityWindowListener listener = new VisibilityWindowListener() {
		@Override
		public void hide(WindowEvent event) {
		}
		@Override
		public void show(WindowEvent event) {
		}
	};
	for (int i = 0; i < 100; i++) browser.addVisibilityWindowListener(listener);
	for (int i = 0; i < 100; i++) browser.removeVisibilityWindowListener(listener);
}

@Override
@Test
public void test_isVisible() {
	// Note. This test sometimes crashes with webkit1 because shell.setVisible() calls g_main_context_iteration(). See Bug 509411
	// To reproduce, try running test suite 20 times in a loop.
	super.test_isVisible();
}

/**
 * Test that going back in history, when no new pages were visited, returns false.
 */
@Test
public void test_back() {
	for (int i = 0; i < 2; i++) {
		browser.back();
	}
	/* returning 10 times in history - expecting false is returned */
	boolean result = browser.back();
	assertFalse(result);
}

@Test(expected = IllegalArgumentException.class)
public void test_setTextNull() {
	browser.setText(null);
}

@Test(expected = IllegalArgumentException.class)
public void test_setUrlWithNullArg() {
	browser.setUrl(null);
}


/**
 * Logic:
 * - Load a page. Turn off javascript (which takes effect on next pageload)
 * - Load a second page. Try to execute some javascript. If javascript is exectuted then fail.
 */
@Test
public void test_setJavascriptEnabled() {
	AtomicInteger pageLoadCount = new AtomicInteger(0);
	AtomicBoolean testFinished = new AtomicBoolean(false);
	AtomicBoolean testPassed = new AtomicBoolean(false);

	browser.addProgressListener(new ProgressAdapter() {
		@Override
		public void completed(ProgressEvent event) {
			pageLoadCount.incrementAndGet();
			if (pageLoadCount.get() == 1) {
				browser.setJavascriptEnabled(false);
				browser.setText("Second page with javascript dissabled");
			} else if (pageLoadCount.get() == 2) {
				Boolean expectedNull = null;
				try {
					expectedNull = (Boolean) browser.evaluate("return true");
				} catch (Exception e) {
					fail("1) if javascript is dissabled, browser.evaluate() should return null. But an Exception was thrown");
				}
				assertTrue("2) Javascript should not have executed. But not-null was returned:"+expectedNull, expectedNull == null);

				testPassed.set(true);
				testFinished.set(true);
			}
		}
	});

	shell.open();
	browser.setText("First page with javascript enabled. This should not be visiable as a second page should load");

	waitForPassCondition(testFinished::get);
	assertTrue("3) Javascript was executed on the second page. But it shouldn't have", testPassed.get());
}

/** Check that if there are two browser instances, turning off JS in one instance doesn't turn off JS in the other instance. */
@Test
public void test_setJavascriptEnabled_multipleInstances() {

	AtomicInteger pageLoadCount = new AtomicInteger(1);
	AtomicInteger pageLoadCountSecondInstance = new AtomicInteger(1);

	AtomicBoolean instanceOneFinishedCorrectly = new AtomicBoolean(false);
	AtomicBoolean instanceTwoFinishedCorrectly = new AtomicBoolean(false);


	Browser browserSecondInsance = new Browser(shell, SWT.None);

	browser.addProgressListener(new ProgressAdapter() {
		@Override
		public void completed(ProgressEvent event) {
			if (pageLoadCount.get() == 1) {
				browser.setJavascriptEnabled(false);

				pageLoadCount.set(2);
				browser.setText("First instance, second page (with javascript turned off)");

				pageLoadCountSecondInstance.set(2);
				browserSecondInsance.setText("Second instance, second page (javascript execution not changed)");
			} else if (pageLoadCount.get() == 2) {
				pageLoadCount.set(3);

				Boolean shouldBeNull = (Boolean) browser.evaluate("return true");
				assertTrue("1) Evaluate execution should be null, but 'true was returned'", shouldBeNull == null);
				instanceOneFinishedCorrectly.set(true);
			}
		}
	});

	browserSecondInsance.addProgressListener(new ProgressAdapter() {
		@Override
		public void completed(ProgressEvent event) {
			if (pageLoadCountSecondInstance.get() == 2) {
				pageLoadCountSecondInstance.set(3);

				Boolean shouldBeTrue = (Boolean) browserSecondInsance.evaluate("return true");
				assertTrue("2) Javascript should be executable in second instance (as javascript was not turned off), but it was not. "
						+ "Expected:'someStr', Actual:"+shouldBeTrue, shouldBeTrue);
				instanceTwoFinishedCorrectly.set(true);
			}
		}
	});

	browser.setText("First Instance, first page");
	browserSecondInsance.setText("Second instance, first page");

	shell.open();
	boolean passed = waitForPassCondition(() -> {return instanceOneFinishedCorrectly.get() && instanceTwoFinishedCorrectly.get();});

	String message = "3) Test timed out. Debug Info:\n" +
			"InstanceOneFinishedCorrectly: " + instanceOneFinishedCorrectly.get() + "\n" +
			"InstanceTwoFinishedCorrectly: " + instanceTwoFinishedCorrectly.get() + "\n" +
			"Instance 1 & 2 page counts: " + pageLoadCount.get() + " & " + pageLoadCountSecondInstance.get();

	assertTrue(message, passed);
}

/**
*  This test replicates what happens internally
*  if you click on a link in a javadoc popup hoverbox.
*  I.e, in a location listener, evaluation() is performed.
*
*  The goal of this test is to ensure there are no 'Freezes'/deadlocks if
*  javascript evaluation is invoked inside an SWT listener.
*
*  At time of writing, it also highlights that evaluation inside SWT listeners
*  is not consistent across browsers.
*/
@Test
public void test_LocationListener_evaluateInCallback() {
	assumeTrue(isWebkit2 || SwtTestUtil.isCocoa || SwtTestUtil.isWindows);
	// On Webki1 this test works, but is prone to crashes. See Bug 509411

	AtomicBoolean changingFinished = new AtomicBoolean(false);
	AtomicBoolean changedFinished = new AtomicBoolean(false);
	browser.addLocationListener(new LocationListener() {
		@Override
		public void changing(LocationEvent event) {
			browser.evaluate("SWTchanging = true");  // Broken on Webkit1. I.e evaluate() in a 'changing()' signal doesn't do anything.
			changingFinished.set(true);
		}
		@Override
		public void changed(LocationEvent event) {
			browser.evaluate("SWTchanged = true");
			changedFinished.set(true);
		}
	});

	browser.setText("<body>Hello <b>World</b></body>");

	// Wait till both listeners were fired.
	if (SwtTestUtil.isWindows) {
		waitForPassCondition(changingFinished::get); // Windows doesn't reach changedFinished.get();
	} else
		waitForPassCondition(() -> (changingFinished.get() && changedFinished.get()));

	// Inspect if evaluate() was executed correctly.
	Boolean changed = false;
	try { changed = (Boolean) browser.evaluate("return SWTchanged"); } catch (SWTException e) {}
	Boolean changing = false;
	try { changing = (Boolean) browser.evaluate("return SWTchanging"); } catch (SWTException e) {}


	String errMsg = "\n  changing:  fired:" +  changingFinished.get() + "    evaluated:" + changing +
				    "\n  changed:   fired:" + changedFinished.get() + "    evaluated:" + changed;
	boolean passed = false;

	if (isWebkit2) {
		// Evaluation works in all cases.
		passed = changingFinished.get() && changedFinished.get() && changed && changing;
	} else if (isWebkit1) {
		// On Webkit1, evaluation in 'changing' fails.
		passed = changingFinished.get() && changedFinished.get() && changed; // && changing (broken)
	} else if (SwtTestUtil.isCocoa) {
		// On Cocoa, evaluation in 'changing' fails.
		passed = changingFinished.get() && changedFinished.get() && changed; // && changing (broken)
	} else if (SwtTestUtil.isWindows) {
		// On Windows, evaluation inside SWT listeners fails altogether.
		// Further, only 'changing' is fired if evaluation is invoked inside listeners.
		passed = changingFinished.get();
	}
	assertTrue(errMsg, passed);
}

/** Verify that evaluation works inside an OpenWindowListener */
@Test
public void test_OpenWindowListener_evaluateInCallback() {
	assumeTrue(!isWebkit1); // This works on Webkit1, but can sporadically fail, see Bug 509411
	AtomicBoolean eventFired = new AtomicBoolean(false);
	browser.addOpenWindowListener(event -> {
		browser.evaluate("SWTopenListener = true");
		eventFired.set(true);
		event.required = true;
	});
	shell.open();
	browser.evaluate("window.open()");
	boolean fired = waitForPassCondition(() -> eventFired.get());
	boolean evaluated = false;
	try { evaluated = (Boolean) browser.evaluate("return SWTopenListener"); } catch (SWTException e) {}
	boolean passed = fired && evaluated;
	String errMsg = "Event fired:" + fired + "   evaluated:" + evaluated;
	assertTrue(errMsg, passed);
}

/**
 * Test that going forward in history (without having gone back before) returns false.
 */
@Test
public void test_forward() {
	for (int i = 0; i < 2; i++) {
		browser.forward();
	}
	/* going forward 10 times in history - expecting false is returned */
	boolean result = browser.forward();
	assertFalse(result);
}

/**
 * Test that getUrl() returns a non-null string.
 */
@Test
public void test_getUrl() {
	String string = browser.getUrl();
	assertTrue(string != null);
}


/**
 * Test of 'back in history' api.
 * - Test isBackEnabled() and back() return the same value.
 * - Test that going isBackEnabled still returns false if back was called multiple times.
 */
@Test
public void test_isBackEnabled() {

	/* back should return the same value that isBackEnabled previously returned */
	assertEquals(browser.isBackEnabled(), browser.back());

	for (int i = 0; i < 2; i++) {
		browser.back();
	}
	/* going back 10 times in history - expecting false is returned */
	boolean result = browser.isBackEnabled();
	assertFalse(result);
}

/**
 * Test of 'forward in history' api.
 * - Test isForwardEnabled() and forward() return the same value.
 * - Test that going isBackEnabled still returns false if back was called multiple times.
 */
@Test
public void test_isForwardEnabled() {
	/* forward should return the same value that isForwardEnabled previously returned */
	assertEquals(browser.isForwardEnabled(), browser.forward());

	for (int i = 0; i < 10; i++) {
		browser.forward();
	}
	/* going forward 10 times in history - expecting false is returned */
	boolean result = browser.isForwardEnabled();
	assertFalse(result);
}

/**
 * Test that refresh executes without throwing exceptions.
 * (Maybe we should actually load a page first?)
 */
@Test
public void test_refresh() {
	for (int i = 0; i < 2; i++) {
		browser.refresh();
	}
}



/**
 * Test that HTML can be loaded into the browser.
 * Assertion is based on the return value of setText().
 * (A true return value doesn't neccessarily mean the text is actually rendered,
 * You should see a browser page with 'That is a test line...' printed many times.)
 */
@Test
public void test_setTextLjava_lang_String() {
	// Note, this test sometimes crashes on webkit1. See Bug 509411

	String html = "<HTML><HEAD><TITLE>HTML example 2</TITLE></HEAD><BODY><H1>HTML example 2</H1>";
	for (int i = 0; i < 1000; i++) {
		html +="<P>That is a test line with the number "+i+"</P>";
	}
	html += "</BODY></HTML>";
	boolean result = browser.setText(html);
	assertTrue(result);
	waitForMilliseconds(2000);
}

/**
 * Test that setUrl() finishes without throwing an error.
 */
@Test
public void test_setUrl() {
	// Note, this test sometimes crashes on webkit1. See Bug 509411

	/* THIS TEST REQUIRES WEB ACCESS! How else can we really test the http:// part of a browser widget? */
	assert(browser.setUrl("http://www.eclipse.org/swt"));
	waitForMilliseconds(2000);
	// TODO - it would be good to verify that the page actually loaded. ex download the webpage etc..
}


/**
 * Test that a page load an be stopped (stop()) without throwing an exception.
 */
@Test
public void test_stop() {
	/* THIS TEST REQUIRES WEB ACCESS! How else can we really test the http:// part of a browser widget? */
	browser.setUrl("http://www.eclipse.org/swt");
	waitForMilliseconds(1000);
	browser.stop();
}

@Test(expected = IllegalArgumentException.class)
public void test_execute_withNullArg() {
	browser.execute(null);
}

/**
 * Test execute and windowCloseListener.
 * Close listener used to tell if execute actually worked in some meaningful way.
 */
@Test
public void test_execute_and_closeListener () {
	AtomicBoolean hasClosed = new AtomicBoolean(false);

	browser.setText("You should not see this page, it should have been closed by javascript");
	browser.addCloseWindowListener(e -> {
		hasClosed.set(true);
	});

	browser.execute("window.close()");

	shell.open();
	boolean passed = waitForPassCondition(() -> hasClosed.get());
	if (passed)
		disposedIntentionally = true;
	String message = "Either browser.execute() did not work (if you still see the html page) or closeListener Was not triggered if "
			+ "browser looks disposed, but test still fails.";
	assertTrue(message, passed);
}


/**
 * Test the evaluate() api that returns a String type. Functionality based on Snippet308.
 * Only wait till success. Otherwise timeout after 3 seconds.
 */
@Test
public void test_evaluate_string() {
	// Run locally, skip on hudson. see Bug 509411
	// This test sometimes crashes on webkit1, but it's useful to test at least one 'evaluate' situation.
	assumeFalse(webkit1SkipMsg(), (SwtTestUtil.isRunningOnEclipseOrgHudsonGTK && isWebkit1));

	final AtomicReference<String> returnValue = new AtomicReference<>();
	browser.addProgressListener(new ProgressListener() {
		@Override
		public void changed(ProgressEvent event) {
		}
		@Override
		public void completed(ProgressEvent event) {
			String evalResult = (String) browser.evaluate("return document.getElementById('myid').childNodes[0].nodeValue;");
			returnValue.set(evalResult);
			if (debug_verbose_output)
				System.out.println("Node value: "+ evalResult);
		}
	});

	browser.setText("<html><body><p id='myid'>HelloWorld</p></body></html>");
	shell.open();
	boolean passed = waitForPassCondition(()-> "HelloWorld".equals(returnValue.get()));
	assertTrue("Evaluation did not return a value. Or test timed out.", passed);
}

/**
 * Test the evaluate() api that returns a number (Double). Functionality based on Snippet308.
 * Only wait till success. Otherwise timeout after 3 seconds.
 */
@Test
public void test_evaluate_number_normal() {
	assumeFalse(webkit1SkipMsg(), isWebkit1); // Bug 509411
	Double testNum = 123.0;
	boolean passed = evaluate_number_helper(testNum);
	assertTrue("Failed to evaluate number: " + testNum.toString(), passed);
}

/**
 * Test the evaluate() api that returns a number (Double). Functionality based on Snippet308.
 * Only wait till success. Otherwise timeout after 3 seconds.
 */
@Test
public void test_evaluate_number_negative() {
	assumeFalse(webkit1SkipMsg(), isWebkit1); // Bug 509411

	Double testNum = -123.0;
	boolean passed = evaluate_number_helper(testNum);
	assertTrue("Failed to evaluate number: " + testNum.toString(), passed);
}

/**
 * Test the evaluate() api that returns a number (Double). Functionality based on Snippet308.
 * Only wait till success. Otherwise timeout after 3 seconds.
 */
@Test
public void test_evaluate_number_big() {
	assumeFalse(webkit1SkipMsg(), isWebkit1); // Bug 509411

	Double testNum = 10000000000.0;
	boolean passed = evaluate_number_helper(testNum);
	assertTrue("Failed to evaluate number: " + testNum.toString(), passed);
}

boolean evaluate_number_helper(Double testNum) {
	final AtomicReference<Double> returnValue = new AtomicReference<>();
	browser.addProgressListener(new ProgressListener() {
		@Override
		public void changed(ProgressEvent event) {
		}
		@Override
		public void completed(ProgressEvent event) {
			Double evalResult = (Double) browser.evaluate("return " +testNum.toString());
			returnValue.set(evalResult);
			if (debug_verbose_output)
				System.out.println("Node value: "+ evalResult);
		}
	});

	browser.setText("<html><body>HelloWorld</body></html>");
	shell.open();
	boolean passed = waitForPassCondition(() -> testNum.equals(returnValue.get()));
	return passed;
}

/**
 * Test the evaluate() api that returns a boolean. Functionality based on Snippet308.
 * Only wait till success. Otherwise timeout after 3 seconds.
 */
@Test
public void test_evaluate_boolean() {
	assumeFalse(webkit1SkipMsg(), isWebkit1); // Bug 509411
	final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
	browser.addProgressListener(new ProgressListener() {
		@Override
		public void changed(ProgressEvent event) {
		}
		@Override
		public void completed(ProgressEvent event) {
			Boolean evalResult = (Boolean) browser.evaluate("return true");
			atomicBoolean.set(evalResult);
			if (debug_verbose_output)
				System.out.println("Node value: "+ evalResult);
		}
	});

	browser.setText("<html><body>HelloWorld</body></html>");
	shell.open();
	boolean passed = waitForPassCondition(() -> atomicBoolean.get());
	assertTrue("Evaluation did not return a boolean. Or test timed out.", passed);
}

/**
 * Test the evaluate() api that returns null. Functionality based on Snippet308.
 * Only wait till success. Otherwise timeout after 3 seconds.
 */
@Test
public void test_evaluate_null() {
	assumeFalse(webkit1SkipMsg(), isWebkit1); // Bug 509411
	 // Boolen only used as dummy placeholder so the object is not null.
	final AtomicReference<Object> returnValue = new AtomicReference<>(new Boolean(true));
	browser.addProgressListener(new ProgressListener() {
		@Override
		public void changed(ProgressEvent event) {
		}
		@Override
		public void completed(ProgressEvent event) {
			Object evalResult = browser.evaluate("return null");
			returnValue.set(evalResult);
			if (debug_verbose_output)
				System.out.println("Node value: "+ evalResult);
		}
	});

	browser.setText("<html><body>HelloWorld</body></html>");
	shell.open();
	boolean passed = waitForPassCondition(() -> returnValue.get() == null);
	assertTrue("Evaluate did not return a null. Timed out.", passed);
}

/**
 * Test the evaluate() api that throws the invalid return value exception. Functionality based on Snippet308.
 * Only wait till success. Otherwise timeout after 3 seconds.
 */
@Test
public void test_evaluate_invalid_return_value() {
	assumeFalse(webkit1SkipMsg(), isWebkit1); // Bug 509411

	if (SwtTestUtil.isWindows) {
		/* Bug 508210 . Inconsistent beahiour on windows at the moment.
		 * Fixing requires deeper investigation. Disabling newly added test for now.
		 */
		return;
	}

	final AtomicInteger exception = new AtomicInteger(-1);
	browser.addProgressListener(new ProgressListener() {
		@Override
		public void changed(ProgressEvent event) {}
		@Override
		public void completed(ProgressEvent event) {
			try {
			browser.evaluate("return new Date()"); //Date is not supoprted as return value.
			} catch (SWTException e) {
				exception.set(e.code);
			}
		}
	});

	browser.setText("<html><body>HelloWorld</body></html>");
	shell.open();

	AtomicBoolean wrongExceptionCode = new AtomicBoolean(false);
	boolean passed = waitForPassCondition(() -> {
		if (exception.get() != -1) {
			if (exception.get() == SWT.ERROR_INVALID_RETURN_VALUE) {
				return true;
			} else if (exception.get() == SWT.ERROR_FAILED_EVALUATE) {
				wrongExceptionCode.set(true);
				return true;
			}
		}
		return false;
	});
	if (wrongExceptionCode.get()) {
		System.err.println("SWT Warning: test_evaluate_invalid_return_value threw wrong exception code."
				+ " Expected ERROR_INVALID_RETURN_VALUE but got ERROR_FAILED_EVALUATE");
	}
	String message = exception.get() == -1 ? "Exception was not thrown. Test timed out" : "Exception thrown, but wrong code: " + exception.get();
	assertTrue(message, passed);
}

/**
 * Test the evaluate() api that throws the evaluation failed exception. Functionality based on Snippet308.
 * Only wait till success. Otherwise timeout after 3 seconds.
 */
@Test
public void test_evaluate_evaluation_failed_exception() {
	assumeFalse(webkit1SkipMsg(), isWebkit1); // Bug 509411
	final AtomicInteger exception = new AtomicInteger(-1);
	browser.addProgressListener(new ProgressListener() {
		@Override
		public void changed(ProgressEvent event) {}
		@Override
		public void completed(ProgressEvent event) {
			try {
			browser.evaluate("return runSomeUndefinedFunctionInJavaScriptWhichCausesUndefinedError()");
			} catch (SWTException e) {
				exception.set(e.code);
			}
		}
	});

	browser.setText("<html><body>HelloWorld</body></html>");
	shell.open();
	AtomicReference<String> additionalErrorInfo = new AtomicReference<>("");
	boolean passed = waitForPassCondition(() -> {
		if (exception.get() != -1) {
			if (exception.get() == SWT.ERROR_FAILED_EVALUATE) {
				return true;
			} else  {
				additionalErrorInfo.set("Invalid exception thrown: " + exception.get());
			}
		}
		return false;
	});
	String message = "".equals(additionalErrorInfo.get()) ? "Javascript did not throw an error. Test timed out" :
		"Javascript threw an error, but not the right one." + additionalErrorInfo.get();
	assertTrue(message, passed);
}

/**
 * Test the evaluate() api that returns an array of numbers. Functionality based on Snippet308.
 * Only wait till success. Otherwise timeout after 3 seconds.
 */
@Test
public void test_evaluate_array_numbers() {
	assumeFalse(webkit1SkipMsg(), isWebkit1); // Bug 509411

	// Small note:
	// evaluate() returns 'Double' type. Java doesn't have AtomicDouble
	// for convienience we simply convert double to int as we're dealing with integers anyway.
	final AtomicIntegerArray atomicIntArray = new AtomicIntegerArray(3);
	atomicIntArray.set(0, -1);
	browser.addProgressListener(new ProgressListener() {
		@Override
		public void changed(ProgressEvent event) {
		}
		@Override
		public void completed(ProgressEvent event) {
			Object[] evalResult = (Object[]) browser.evaluate("return new Array(1,2,3)");
			atomicIntArray.set(0, ((Double) evalResult[0]).intValue());
			atomicIntArray.set(1, ((Double) evalResult[1]).intValue());
			atomicIntArray.set(2, ((Double) evalResult[2]).intValue());
			if (debug_verbose_output)
				System.out.println("Node value: "+ evalResult);
		}
	});

	browser.setText("<html><body><p id='myid'>HelloWorld</p></body></html>");
	shell.open();
	AtomicReference<String> additionalErrorInfo = new AtomicReference<>("");
	boolean passed = waitForPassCondition(() -> {
		if (atomicIntArray.get(0) != -1) {
			if (atomicIntArray.get(0) == 1 && atomicIntArray.get(1) == 2 && atomicIntArray.get(2) == 3) {
				return true;
			} else {
				additionalErrorInfo.set("Resulting numbers in the array are not as expected");
			}
		}
		return false;
	});
	String message = "".equals(additionalErrorInfo.get()) ? "Javascript did not call java" : "Javasscript called java, but passed wrong values: " + additionalErrorInfo.get();
	assertTrue(message, passed);
}

/**
 * Test the evaluate() api that returns an array of strings. Functionality based on Snippet308.
 * Only wait till success. Otherwise timeout after 3 seconds.
 */
@Test
public void test_evaluate_array_strings () {
	assumeFalse(webkit1SkipMsg(), isWebkit1); // Bug 509411

	final AtomicReferenceArray<String> atomicStringArray = new AtomicReferenceArray<>(3);
	atomicStringArray.set(0, "executing");
	browser.addProgressListener(new ProgressListener() {
		@Override
		public void changed(ProgressEvent event) {
		}
		@Override
		public void completed(ProgressEvent event) {
			Object[] evalResult = (Object[]) browser.evaluate("return new Array(\"str1\", \"str2\", \"str3\")");
			atomicStringArray.set(0, (String) evalResult[0]);
			atomicStringArray.set(1, (String) evalResult[1]);
			atomicStringArray.set(2, (String) evalResult[2]);
			if (debug_verbose_output)
				System.out.println("Node value: "+ evalResult);
		}
	});

	browser.setText("<html><body><p id='myid'>HelloWorld</p></body></html>");
	shell.open();
	AtomicReference<String> additionalErrorInfo = new AtomicReference<>("");
	boolean passed = waitForPassCondition(() -> {
		if (! "executing".equals(atomicStringArray.get(0))) {
			if (atomicStringArray.get(0).equals("str1")
					&& atomicStringArray.get(1).equals("str2")
					&& atomicStringArray.get(2).equals("str3")) {
				return true;
			} else
				additionalErrorInfo.set("Resulting strings in array are not as expected");
		}
		return false;
	});
	String message = "".equals(additionalErrorInfo.get()) ?
			"Expected an array of strings, but did not receive array or got the wrong result."
			: "Received a callback from javascript, but: " + additionalErrorInfo.get() + " : " + atomicStringArray.toString();
	assertTrue(message, passed);
}

/**
 * Test the evaluate() api that returns an array of mixed types. Functionality based on Snippet308.
 * Only wait till success. Otherwise timeout after 3 seconds.
 */
@Test
public void test_evaluate_array_mixedTypes () {
	assumeFalse(webkit1SkipMsg(), isWebkit1); // Bug 509411
	final AtomicReferenceArray<Object> atomicArray = new AtomicReferenceArray<>(3);
	atomicArray.set(0, "executing");
	browser.addProgressListener(new ProgressListener() {
		@Override
		public void changed(ProgressEvent event) {
		}
		@Override
		public void completed(ProgressEvent event) {
			Object[] evalResult = (Object[]) browser.evaluate("return new Array(\"str1\", 2, true)");
			atomicArray.set(2, evalResult[2]);
			atomicArray.set(1, evalResult[1]);
			atomicArray.set(0, evalResult[0]); // should be set last. to avoid loop below ending & failing to early.
			if (debug_verbose_output)
				System.out.println("Node value: "+ evalResult);
		}
	});


	browser.setText("<html><body><p id='myid'>HelloWorld</p></body></html>");
	shell.open();
	AtomicReference<String> additionalErrorInfo = new AtomicReference<>("");
	boolean passed = waitForPassCondition(() -> {
		if (! "executing".equals(atomicArray.get(0))) {
			if (atomicArray.get(0).equals("str1")
					&& ((Double) atomicArray.get(1)) == 2
					&& ((Boolean) atomicArray.get(2))) {
				return true;
			} else
				additionalErrorInfo.set("Resulting String are not as exected");
		}
		return false;
	});
	String message = "".equals(additionalErrorInfo.get()) ? "Javascript did not call java" : "Javascript called java but passed wrong values: " + atomicArray.toString();
	assertTrue(message, passed);
}


ProgressListener callCustomFunctionUponLoad = new ProgressListener() {
	@Override
	public void changed(ProgressEvent event) {}
	@Override
	public void completed(ProgressEvent event) {
		browser.execute("callCustomFunction()");
	}
};

/**
 * Test that javascript can call java.
 * loosely based on Snippet307.
 */
@Test
public void test_BrowserFunction_callback () {
	// On webkit1, this test works if ran on it's own. But sometimes in test-suite with other tests it causes jvm crash.
	// culprit seems to be the main_context_iteration() call in shell.setVisible().
	// See Bug 509587.  Solution: Webkit2.
	assumeFalse(webkit1SkipMsg(), isWebkit1);

	AtomicBoolean javaCallbackExecuted = new AtomicBoolean(false);

	class JavascriptCallback extends BrowserFunction { // Note: Local class defined inside method.
		JavascriptCallback(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			javaCallbackExecuted.set(true);
			return null;
		}
	}

	String htmlWithScript = "<html><head>\n"
			+ "<script language=\"JavaScript\">\n"
			+ "function callCustomFunction() {\n"  // Define a javascript function.
			+ "     document.body.style.backgroundColor = 'red'\n"
			+ "		jsCallbackToJava()\n"        // This calls the javafunction that we registered.
			+ "}"
			+ "</script>\n"
			+ "</head>\n"
			+ "<body> I'm going to make a callback to java </body>\n"
			+ "</html>\n";

	browser.setText(htmlWithScript);
	new JavascriptCallback(browser, "jsCallbackToJava");

	browser.addProgressListener(callCustomFunctionUponLoad);

	shell.open();
	boolean passed = waitForPassCondition(() ->  javaCallbackExecuted.get());
	String message = "Java failed to get a callback from javascript. Test timed out";
	assertTrue(message, passed);
}

/**
 * Test that javascript can call java and pass an integer to java.
 * loosely based on Snippet307.
 */
@Test
public void test_BrowserFunction_callback_with_integer () {
	// On webkit1, this test works if ran on it's own. But sometimes in test-suite with other tests it causes jvm crash.
	// culprit seems to be the main_context_iteration() call in shell.setVisible().
	// See Bug 509587.  Solution: Webkit2.
	// It's useful to run at least one function test on webkit1 locally.
	assumeFalse(webkit1SkipMsg(), (SwtTestUtil.isRunningOnEclipseOrgHudsonGTK && isWebkit1)); // run locally. Skip on hudson that runs webkit1.

	AtomicInteger returnInt = new AtomicInteger(0);

	class JavascriptCallback extends BrowserFunction { // Note: Local class defined inside method.
		JavascriptCallback(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			Double returnedDouble = (Double) arguments[0];
			returnInt.set(returnedDouble.intValue()); // 5.0 -> 5
			return null;
		}
	}

	String htmlWithScript = "<html><head>\n"
			+ "<script language=\"JavaScript\">\n"
			+ "function callCustomFunction() {\n"  // Define a javascript function.
			+ "     document.body.style.backgroundColor = 'red'\n"
			+ "		jsCallbackToJava(5)\n"        // This calls the javafunction that we registered ** with value of 5.
			+ "}"
			+ "</script>\n"
			+ "</head>\n"
			+ "<body> I'm going to make a callback to java </body>\n"
			+ "</html>\n";

	browser.setText(htmlWithScript);
	new JavascriptCallback(browser, "jsCallbackToJava");

	browser.addProgressListener(callCustomFunctionUponLoad);

	shell.open();
	boolean passed = waitForPassCondition(() -> returnInt.get() == 5);
	String message = "Javascript should have passed an integer to java. But this did not happen";
	assertTrue(message, passed);
}



/**
 * Test that javascript can call java and pass a Boolean to java.
 * loosely based on Snippet307.
 */
@Test
public void test_BrowserFunction_callback_with_boolean () {
	// On webkit1, this test works if ran on it's own. But sometimes in test-suite with other tests it causes jvm crash.
	// culprit seems to be the main_context_iteration() call in shell.setVisible().
	// See Bug 509587.  Solution: Webkit2.
	assumeFalse(webkit1SkipMsg(), isWebkit1);

	AtomicBoolean javaCallbackExecuted = new AtomicBoolean(false);

	class JavascriptCallback extends BrowserFunction { // Note: Local class defined inside method.
		JavascriptCallback(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			Boolean returnBool = (Boolean) arguments[0];
 			javaCallbackExecuted.set(returnBool);
			return null;
		}
	}

	String htmlWithScript = "<html><head>\n"
			+ "<script language=\"JavaScript\">\n"
			+ "function callCustomFunction() {\n"  // Define a javascript function.
			+ "     document.body.style.backgroundColor = 'red'\n"
			+ "		jsCallbackToJava(true)\n"        // This calls the javafunction that we registered.
			+ "}"
			+ "</script>\n"
			+ "</head>\n"
			+ "<body> I'm going to make a callback to java </body>\n"
			+ "</html>\n";

	browser.setText(htmlWithScript);
	new JavascriptCallback(browser, "jsCallbackToJava");

	browser.addProgressListener(callCustomFunctionUponLoad);

	shell.open();
	boolean passed = waitForPassCondition(() -> javaCallbackExecuted.get());
	String message = "Javascript did not pass a boolean back to java";
	assertTrue(message, passed);
}


/**
 * Test that javascript can call java and pass a String to java.
 * loosely based on Snippet307.
 */
@Test
public void test_BrowserFunction_callback_with_String () {
	// On webkit1, this test works if ran on it's own. But sometimes in test-suite with other tests it causes jvm crash.
	// culprit seems to be the main_context_iteration() call in shell.setVisible().
	// See Bug 509587.  Solution: Webkit2.
	assumeFalse(webkit1SkipMsg(), isWebkit1);

	final AtomicReference<String> returnValue = new AtomicReference<>();
	class JavascriptCallback extends BrowserFunction { // Note: Local class defined inside method.
		JavascriptCallback(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			String returnString = (String) arguments[0];
			returnValue.set(returnString);
			return null;
		}
	}

	String htmlWithScript = "<html><head>\n"
			+ "<script language=\"JavaScript\">\n"
			+ "function callCustomFunction() {\n"  // Define a javascript function.
			+ "     document.body.style.backgroundColor = 'red'\n"
			+ "		jsCallbackToJava('hellojava')\n"        // This calls the javafunction that we registered.
			+ "}"
			+ "</script>\n"
			+ "</head>\n"
			+ "<body> I'm going to make a callback to java </body>\n"
			+ "</html>\n";

	browser.setText(htmlWithScript);
	new JavascriptCallback(browser, "jsCallbackToJava");

	browser.addProgressListener(callCustomFunctionUponLoad);

	shell.open();
	boolean passed = waitForPassCondition(() -> "hellojava".equals(returnValue.get()));
	String message = "Javascript was suppose to call java with a String. But it seems java did not receive the call or wrong value was passed";
	assertTrue(message, passed);
}


/**
 * Test that javascript can call java and pass multiple values to java.
 * loosely based on Snippet307.
 */
@Test
public void test_BrowserFunction_callback_with_multipleValues () {
	// On webkit1, this test works if ran on it's own. But sometimes in test-suite with other tests it causes jvm crash.
	// culprit seems to be the main_context_iteration() call in shell.setVisible().
	// See Bug 509587.  Solution: Webkit2.
	assumeFalse(webkit1SkipMsg(), isWebkit1);

	final AtomicReferenceArray<Object> atomicArray = new AtomicReferenceArray<>(3); // Strin, Double, Boolean
	atomicArray.set(0, "executing");

	class JavascriptCallback extends BrowserFunction { // Note: Local class defined inside method.
		JavascriptCallback(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			atomicArray.set(1, arguments[1]);
			atomicArray.set(2, arguments[2]);
			atomicArray.set(0, arguments[0]); // item at index 0 should be set last for this test case.
			return null;
		}
	}

	String htmlWithScript = "<html><head>\n"
			+ "<script language=\"JavaScript\">\n"
			+ "function callCustomFunction() {\n"  // Define a javascript function.
			+ "     document.body.style.backgroundColor = 'red'\n"
			+ "		jsCallbackToJava('hellojava', 5, true)\n"        // This calls the javafunction that we registered.
			+ "}"
			+ "</script>\n"
			+ "</head>\n"
			+ "<body> I'm going to make a callback to java </body>\n"
			+ "</html>\n";

	browser.setText(htmlWithScript);
	new JavascriptCallback(browser, "jsCallbackToJava");

	browser.addProgressListener(callCustomFunctionUponLoad);

	shell.open();
//	Screenshots.takeScreenshot(getClass(), "test_BrowserFunction_callback_with_multipleValues__BeforeWaiting"); // Useful if investigating build failures on Hudson

	boolean passed = waitForPassCondition(() -> {
		if (atomicArray.get(0).equals("hellojava")
				&& ((Double) atomicArray.get(1)) == 5
				&& ((Boolean) atomicArray.get(2))) {
			return true;
		} else {
			return false;
		}
	});
//	Screenshots.takeScreenshot(getClass(), "test_BrowserFunction_callback_with_multipleValues__AfterWaiting");  // Useful if investigating build failures on Hudson

	String msg = "Values not set. Test timed out. Array should be [\"hellojava\", 5, true], but is: " + atomicArray.toString();
	assertTrue(msg, passed);
}


/**
 * Test that javascript can call java, java returns an Integer back to javascript.
 *
 * It's a bit tricky to tell if javascript actually received the correct value from java.
 * Solution: make a second function/callback that is called with the value that javascript received from java.
 *
 * Logic:
 *  1) Java registers function callCustomFunction() by setting html body.
 *  2) which in turn calls JavascriptCallback, which returns value 42 back to javascript.
 *  3) javascript then calls JavascriptCallback_javascriptReceivedJavaInt() and passes it value received from java.
 *  4) Java validates that the correct value (42) was passed to javascript and was passed back to java.
 *
 * loosely based on Snippet307.
 */
@Test
public void test_BrowserFunction_callback_with_javaReturningInt () {
	// On webkit1, this test works if ran on it's own. But sometimes in test-suite with other tests it causes jvm crash.
	// culprit seems to be the main_context_iteration() call in shell.setVisible().
	// See Bug 509587.  Solution: Webkit2.
	assumeFalse(webkit1SkipMsg(), isWebkit1);

	// Skip till Bug 510905 is implemented.
	assumeFalse("Skipping test_BrowserFunction_callback_with_javaReturningInt. Java's callback to Javascript doesn't support return yet", isWebkit2);

	AtomicInteger returnInt = new AtomicInteger(0);

	class JavascriptCallback extends BrowserFunction { // Note: Local class defined inside method.
		JavascriptCallback(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			return 42;
		}
	}

	class JavascriptCallback_javascriptReceivedJavaInt extends BrowserFunction { // Note: Local class defined inside method.
		JavascriptCallback_javascriptReceivedJavaInt(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			Double returnVal = (Double) arguments[0];
			returnInt.set(returnVal.intValue());  // 4)
			return null;
		}
	}

	String htmlWithScript = "<html><head>\n"
			+ "<script language=\"JavaScript\">\n"
			+ "function callCustomFunction() {\n"  // Define a javascript function.
			+ "     document.body.style.backgroundColor = 'red'\n"
			+ "     var retVal = jsCallbackToJava()\n"  // 2)
			+ "		document.write(retVal)\n"        // This calls the javafunction that we registered. Set HTML body to return value.
			+ "     jsSuccess(retVal)\n"				// 3)
			+ "}"
			+ "</script>\n"
			+ "</head>\n"
			+ "<body> If you see this, javascript did not receive anything from Java. This page should just be '42' </body>\n"
			+ "</html>\n";
	// 1)
	browser.setText(htmlWithScript);
	new JavascriptCallback(browser, "jsCallbackToJava");
	new JavascriptCallback_javascriptReceivedJavaInt(browser, "jsSuccess");

	browser.addProgressListener(callCustomFunctionUponLoad);

	shell.open();
	boolean passed = waitForPassCondition(() -> returnInt.get() == 42);
	String message = "Java should have returned something back to javascript. But something went wrong";
	assertTrue(message, passed);
}


/**
 * Test that a callback works even after a new page is loaded.
 * I.e, BrowserFunctions should have to be re-initialized after a page load.
 *
 * Logic:
 * - load a page.
 * - Register java callback.
 * - call java callback from javascript. (exec)
 *
 * - java callback instantiates new page load.
 * - new page load triggers 'completed' listener
 * - completed listener calls the registered function again.
 *
 * - once regiseterd function is called a 2nd time, it sets the test to pass.
 */
@Test
public void test_BrowserFunction_callback_afterPageReload() {
	// On webkit1, this test works if ran on it's own. But sometimes in test-suite with other tests it causes jvm crash.
	// culprit seems to be the main_context_iteration() call in shell.setVisible().
	// See Bug 509587.  Solution: Webkit2.
	assumeFalse(webkit1SkipMsg(), isWebkit1);

	AtomicBoolean javaCallbackExecuted = new AtomicBoolean(false);
	AtomicInteger callCount = new AtomicInteger(0);

	class JavascriptCallback extends BrowserFunction { // Note: Local class defined inside method.
		JavascriptCallback(Browser browser, String name) {
			super(browser, name);
		}

		@Override
		public Object function(Object[] arguments) {
			if (callCount.get() == 0) {
				callCount.set(1);
				browser.setText("2nd page load");
			} else {
				javaCallbackExecuted.set(true);
			}
			return null;
		}
	}
	browser.setText("1st (initial) page load");
	new JavascriptCallback(browser, "jsCallbackToJava");
	browser.execute("jsCallbackToJava()");

	browser.addProgressListener(new ProgressListener() {
		@Override
		public void completed(ProgressEvent event) {
			// see if function still works after a page change:
			browser.execute("jsCallbackToJava()");
		}
		@Override
		public void changed(ProgressEvent event) {}
	});

	shell.open();
	boolean passed = waitForPassCondition(() -> javaCallbackExecuted.get());
	String message = "A javascript callback should work after a page has been reloaded. But something went wrong";
	assertTrue(message, passed);
}


/* custom */
/**
 * Wait for passTest to return true. Timeout otherwise.
 * @param passTest a Supplier lambda that returns true if pass condition is true. False otherwise.
 * @return true if test passes, false on timeout.
 */
private boolean waitForPassCondition(final Supplier<Boolean> passTest) {
	return waitForPassCondition(passTest, 1000 * secondsToWaitTillFail);
}

private boolean waitForPassCondition(final Supplier<Boolean> passTest, int millisecondsToWait) {
	final AtomicBoolean passed = new AtomicBoolean(false);
	final Instant timeOut = Instant.now().plusMillis(millisecondsToWait);
	final Instant debug_showBrowserTimeout = Instant.now().plusSeconds(debug_show_browser_timeout_seconds);
	final Display display = shell.getDisplay();

	// This thread tests the pass-condition periodically.
	// Triggers fail if timeout occurs.
	new Thread(() -> {
		while (Instant.now().isBefore(timeOut)) {
			if (passTest.get()) {
				passed.set(true);
				break;
			}
			try {Thread.sleep(2);} catch (InterruptedException e) {e.printStackTrace();}
		}

		// If debug_show_browser is enabled, it only wakes up the display thread after the timeout occured.
		while (debug_show_browser && Instant.now().isBefore(debug_showBrowserTimeout)) {
			try {Thread.sleep(500);} catch (InterruptedException e) {e.printStackTrace();}
		}
		display.wake(); // timeout. Test failed by default.
	}).start();

	while (Instant.now().isBefore(timeOut)) {
		if (passed.get()) { // Logic to show browser window for longer if enabled.
			if (!debug_show_browser) break;
			if (Instant.now().isAfter(debug_showBrowserTimeout)) break;
		}

		if (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	return passed.get();
}

/** Contrary to Thread.wait(), this method allows swt's display to carry out actions. */
void waitForMilliseconds(final int milliseconds) {
	waitForPassCondition(() -> false, milliseconds);

}

private String webkit1SkipMsg() {
	return "Test_org_eclipse_swt_browser. Bug 509411. Skipping test on Webkit1 due to sporadic crash: "+ name.getMethodName();
}

}
