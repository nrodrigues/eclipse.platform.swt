package org.eclipse.swt.graphics;

import org.eclipse.swt.*;

interface ITextLayout {

	/**
	 * Draws the receiver's text using the specified GC at the specified
	 * point.
	 *
	 * @param gc the GC to draw
	 * @param x the x coordinate of the top left corner of the rectangular area where the text is to be drawn
	 * @param y the y coordinate of the top left corner of the rectangular area where the text is to be drawn
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the gc is null</li>
	 * </ul>
	 */
	void draw(GC gc, int x, int y);

	/**
	 * Draws the receiver's text using the specified GC at the specified
	 * point.
	 *
	 * @param gc the GC to draw
	 * @param x the x coordinate of the top left corner of the rectangular area where the text is to be drawn
	 * @param y the y coordinate of the top left corner of the rectangular area where the text is to be drawn
	 * @param selectionStart the offset where the selections starts, or -1 indicating no selection
	 * @param selectionEnd the offset where the selections ends, or -1 indicating no selection
	 * @param selectionForeground selection foreground, or NULL to use the system default color
	 * @param selectionBackground selection background, or NULL to use the system default color
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the gc is null</li>
	 * </ul>
	 */
	void draw(GC gc, int x, int y, int selectionStart, int selectionEnd, Color selectionForeground,
			Color selectionBackground);

	/**
	 * Draws the receiver's text using the specified GC at the specified
	 * point.
	 * <p>
	 * The parameter <code>flags</code> can include one of <code>SWT.DELIMITER_SELECTION</code>
	 * or <code>SWT.FULL_SELECTION</code> to specify the selection behavior on all lines except
	 * for the last line, and can also include <code>SWT.LAST_LINE_SELECTION</code> to extend
	 * the specified selection behavior to the last line.
	 * </p>
	 * @param gc the GC to draw
	 * @param x the x coordinate of the top left corner of the rectangular area where the text is to be drawn
	 * @param y the y coordinate of the top left corner of the rectangular area where the text is to be drawn
	 * @param selectionStart the offset where the selections starts, or -1 indicating no selection
	 * @param selectionEnd the offset where the selections ends, or -1 indicating no selection
	 * @param selectionForeground selection foreground, or NULL to use the system default color
	 * @param selectionBackground selection background, or NULL to use the system default color
	 * @param flags drawing options
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the gc is null</li>
	 * </ul>
	 *
	 * @since 3.3
	 */
	void draw(GC gc, int x, int y, int selectionStart, int selectionEnd, Color selectionForeground,
			Color selectionBackground, int flags);

	/**
	 * Returns the receiver's horizontal text alignment, which will be one
	 * of <code>SWT.LEFT</code>, <code>SWT.CENTER</code> or
	 * <code>SWT.RIGHT</code>.
	 *
	 * @return the alignment used to positioned text horizontally
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	int getAlignment();

	/**
	 * Returns the ascent of the receiver.
	 *
	 * @return the ascent
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #getDescent()
	 * @see #setDescent(int)
	 * @see #setAscent(int)
	 * @see #getLineMetrics(int)
	 */
	int getAscent();

	/**
	 * Returns the bounds of the receiver. The width returned is either the
	 * width of the longest line or the width set using {@link TextLayout#setWidth(int)}.
	 * To obtain the text bounds of a line use {@link TextLayout#getLineBounds(int)}.
	 *
	 * @return the bounds of the receiver
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #setWidth(int)
	 * @see #getLineBounds(int)
	 */
	Rectangle getBounds();

	/**
	 * Returns the bounds for the specified range of characters. The
	 * bounds is the smallest rectangle that encompasses all characters
	 * in the range. The start and end offsets are inclusive and will be
	 * clamped if out of range.
	 *
	 * @param start the start offset
	 * @param end the end offset
	 * @return the bounds of the character range
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	Rectangle getBounds(int start, int end);

	/**
	 * Returns the descent of the receiver.
	 *
	 * @return the descent
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #getAscent()
	 * @see #setAscent(int)
	 * @see #setDescent(int)
	 * @see #getLineMetrics(int)
	 */
	int getDescent();

	/**
	 * Returns the default font currently being used by the receiver
	 * to draw and measure text.
	 *
	 * @return the receiver's font
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	Font getFont();

	/**
	* Returns the receiver's indent.
	*
	* @return the receiver's indent
	*
	* @exception SWTException <ul>
	*    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	* </ul>
	*
	* @since 3.2
	*/
	int getIndent();

	/**
	* Returns the receiver's justification.
	*
	* @return the receiver's justification
	*
	* @exception SWTException <ul>
	*    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	* </ul>
	*
	* @since 3.2
	*/
	boolean getJustify();

	/**
	 * Returns the embedding level for the specified character offset. The
	 * embedding level is usually used to determine the directionality of a
	 * character in bidirectional text.
	 *
	 * @param offset the character offset
	 * @return the embedding level
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the character offset is out of range</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 */
	int getLevel(int offset);

	/**
	 * Returns the line offsets.  Each value in the array is the
	 * offset for the first character in a line except for the last
	 * value, which contains the length of the text.
	 *
	 * @return the line offsets
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	int[] getLineOffsets();

	/**
	 * Returns the index of the line that contains the specified
	 * character offset.
	 *
	 * @param offset the character offset
	 * @return the line index
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the character offset is out of range</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	int getLineIndex(int offset);

	/**
	 * Returns the bounds of the line for the specified line index.
	 *
	 * @param lineIndex the line index
	 * @return the line bounds
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the line index is out of range</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	Rectangle getLineBounds(int lineIndex);

	/**
	 * Returns the receiver's line count. This includes lines caused
	 * by wrapping.
	 *
	 * @return the line count
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	int getLineCount();

	/**
	 * Returns the font metrics for the specified line index.
	 *
	 * @param lineIndex the line index
	 * @return the font metrics
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the line index is out of range</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	FontMetrics getLineMetrics(int lineIndex);

	/**
	 * Returns the location for the specified character offset. The
	 * <code>trailing</code> argument indicates whether the offset
	 * corresponds to the leading or trailing edge of the cluster.
	 *
	 * @param offset the character offset
	 * @param trailing the trailing flag
	 * @return the location of the character offset
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #getOffset(Point, int[])
	 * @see #getOffset(int, int, int[])
	 */
	Point getLocation(int offset, boolean trailing);

	/**
	 * Returns the next offset for the specified offset and movement
	 * type.  The movement is one of <code>SWT.MOVEMENT_CHAR</code>,
	 * <code>SWT.MOVEMENT_CLUSTER</code>, <code>SWT.MOVEMENT_WORD</code>,
	 * <code>SWT.MOVEMENT_WORD_END</code> or <code>SWT.MOVEMENT_WORD_START</code>.
	 *
	 * @param offset the start offset
	 * @param movement the movement type
	 * @return the next offset
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the offset is out of range</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #getPreviousOffset(int, int)
	 */
	int getNextOffset(int offset, int movement);

	/**
	 * Returns the character offset for the specified point.
	 * For a typical character, the trailing argument will be filled in to
	 * indicate whether the point is closer to the leading edge (0) or
	 * the trailing edge (1).  When the point is over a cluster composed
	 * of multiple characters, the trailing argument will be filled with the
	 * position of the character in the cluster that is closest to
	 * the point.
	 *
	 * @param point the point
	 * @param trailing the trailing buffer
	 * @return the character offset
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the trailing length is less than <code>1</code></li>
	 *    <li>ERROR_NULL_ARGUMENT - if the point is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #getLocation(int, boolean)
	 */
	int getOffset(Point point, int[] trailing);

	/**
	 * Returns the character offset for the specified point.
	 * For a typical character, the trailing argument will be filled in to
	 * indicate whether the point is closer to the leading edge (0) or
	 * the trailing edge (1).  When the point is over a cluster composed
	 * of multiple characters, the trailing argument will be filled with the
	 * position of the character in the cluster that is closest to
	 * the point.
	 *
	 * @param x the x coordinate of the point
	 * @param y the y coordinate of the point
	 * @param trailing the trailing buffer
	 * @return the character offset
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the trailing length is less than <code>1</code></li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #getLocation(int, boolean)
	 */
	int getOffset(int x, int y, int[] trailing);

	/**
	 * Returns the orientation of the receiver.
	 *
	 * @return the orientation style
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	int getOrientation();

	/**
	 * Returns the previous offset for the specified offset and movement
	 * type.  The movement is one of <code>SWT.MOVEMENT_CHAR</code>,
	 * <code>SWT.MOVEMENT_CLUSTER</code> or <code>SWT.MOVEMENT_WORD</code>,
	 * <code>SWT.MOVEMENT_WORD_END</code> or <code>SWT.MOVEMENT_WORD_START</code>.
	 *
	 * @param offset the start offset
	 * @param movement the movement type
	 * @return the previous offset
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the offset is out of range</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #getNextOffset(int, int)
	 */
	int getPreviousOffset(int offset, int movement);

	/**
	 * Gets the ranges of text that are associated with a <code>TextStyle</code>.
	 *
	 * @return the ranges, an array of offsets representing the start and end of each
	 * text style.
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #getStyles()
	 *
	 * @since 3.2
	 */
	int[] getRanges();

	/**
	 * Returns the text segments offsets of the receiver.
	 *
	 * @return the text segments offsets
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	int[] getSegments();

	/**
	 * Returns the segments characters of the receiver.
	 *
	 * @return the segments characters
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @since 3.6
	 */
	char[] getSegmentsChars();

	/**
	 * Returns the line spacing of the receiver.
	 *
	 * @return the line spacing
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	int getSpacing();

	/**
	 * Gets the style of the receiver at the specified character offset.
	 *
	 * @param offset the text offset
	 * @return the style or <code>null</code> if not set
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the character offset is out of range</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	TextStyle getStyle(int offset);

	/**
	 * Gets all styles of the receiver.
	 *
	 * @return the styles
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #getRanges()
	 *
	 * @since 3.2
	 */
	TextStyle[] getStyles();

	/**
	 * Returns the tab list of the receiver.
	 *
	 * @return the tab list
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	int[] getTabs();

	/**
	 * Gets the receiver's text, which will be an empty
	 * string if it has never been set.
	 *
	 * @return the receiver's text
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	String getText();

	/**
	 * Returns the text direction of the receiver.
	 *
	 * @return the text direction value
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 * @since 3.103
	 */
	int getTextDirection();

	/**
	 * Returns the width of the receiver.
	 *
	 * @return the width
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	int getWidth();

	/**
	* Returns the receiver's wrap indent.
	*
	* @return the receiver's wrap indent
	*
	* @exception SWTException <ul>
	*    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	* </ul>
	*
	* @since 3.6
	*/
	int getWrapIndent();

	/**
	 * Disposes of the operating system resources associated with
	 * this resource. Applications must dispose of all resources
	 * which they allocate.
	 * This method does nothing if the resource is already disposed.
	 */
	void dispose();

	/**
	 * Returns <code>true</code> if the text layout has been disposed,
	 * and <code>false</code> otherwise.
	 * <p>
	 * This method gets the dispose state for the text layout.
	 * When a text layout has been disposed, it is an error to
	 * invoke any other method (except {@link #dispose()}) using the text layout.
	 * </p>
	 *
	 * @return <code>true</code> when the text layout is disposed and <code>false</code> otherwise
	 */
	boolean isDisposed();

	/**
	 * Sets the text alignment for the receiver. The alignment controls
	 * how a line of text is positioned horizontally. The argument should
	 * be one of <code>SWT.LEFT</code>, <code>SWT.RIGHT</code> or <code>SWT.CENTER</code>.
	 * <p>
	 * The default alignment is <code>SWT.LEFT</code>.  Note that the receiver's
	 * width must be set in order to use <code>SWT.RIGHT</code> or <code>SWT.CENTER</code>
	 * alignment.
	 * </p>
	 *
	 * @param alignment the new alignment
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #setWidth(int)
	 */
	void setAlignment(int alignment);

	/**
	 * Sets the ascent of the receiver. The ascent is distance in pixels
	 * from the baseline to the top of the line and it is applied to all
	 * lines. The default value is <code>-1</code> which means that the
	 * ascent is calculated from the line fonts.
	 *
	 * @param ascent the new ascent
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the ascent is less than <code>-1</code></li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #setDescent(int)
	 * @see #getLineMetrics(int)
	 */
	void setAscent(int ascent);

	/**
	 * Sets the descent of the receiver. The descent is distance in pixels
	 * from the baseline to the bottom of the line and it is applied to all
	 * lines. The default value is <code>-1</code> which means that the
	 * descent is calculated from the line fonts.
	 *
	 * @param descent the new descent
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the descent is less than <code>-1</code></li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #setAscent(int)
	 * @see #getLineMetrics(int)
	 */
	void setDescent(int descent);

	/**
	 * Sets the default font which will be used by the receiver
	 * to draw and measure text. If the
	 * argument is null, then a default font appropriate
	 * for the platform will be used instead. Note that a text
	 * style can override the default font.
	 *
	 * @param font the new font for the receiver, or null to indicate a default font
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the font has been disposed</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	void setFont(Font font);

	/**
	 * Sets the indent of the receiver. This indent is applied to the first line of
	 * each paragraph.
	 *
	 * @param indent new indent
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #setWrapIndent(int)
	 *
	 * @since 3.2
	 */
	void setIndent(int indent);

	/**
	 * Sets the wrap indent of the receiver. This indent is applied to all lines
	 * in the paragraph except the first line.
	 *
	 * @param wrapIndent new wrap indent
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #setIndent(int)
	 *
	 * @since 3.6
	 */
	void setWrapIndent(int wrapIndent);

	/**
	 * Sets the justification of the receiver. Note that the receiver's
	 * width must be set in order to use justification.
	 *
	 * @param justify new justify
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @since 3.2
	 */
	void setJustify(boolean justify);

	/**
	 * Sets the orientation of the receiver, which must be one
	 * of <code>SWT.LEFT_TO_RIGHT</code> or <code>SWT.RIGHT_TO_LEFT</code>.
	 *
	 * @param orientation new orientation style
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	void setOrientation(int orientation);

	/**
	 * Sets the offsets of the receiver's text segments. Text segments are used to
	 * override the default behavior of the bidirectional algorithm.
	 * Bidirectional reordering can happen within a text segment but not
	 * between two adjacent segments.
	 * <p>
	 * Each text segment is determined by two consecutive offsets in the
	 * <code>segments</code> arrays. The first element of the array should
	 * always be zero and the last one should always be equals to length of
	 * the text.
	 * </p>
	 * <p>
	 * When segments characters are set, the segments are the offsets where
	 * the characters are inserted in the text.
	 * <p>
	 *
	 * @param segments the text segments offset
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #setSegmentsChars(char[])
	 */
	void setSegments(int[] segments);

	/**
	 * Sets the characters to be used in the segments boundaries. The segments
	 * are set by calling <code>setSegments(int[])</code>. The application can
	 * use this API to insert Unicode Control Characters in the text to control
	 * the display of the text and bidi reordering. The characters are not
	 * accessible by any other API in <code>TextLayout</code>.
	 *
	 * @param segmentsChars the segments characters
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #setSegments(int[])
	 *
	 * @since 3.6
	 */
	void setSegmentsChars(char[] segmentsChars);

	/**
	 * Sets the line spacing of the receiver.  The line spacing
	 * is the space left between lines.
	 *
	 * @param spacing the new line spacing
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the spacing is negative</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	void setSpacing(int spacing);

	/**
	 * Sets the style of the receiver for the specified range.  Styles previously
	 * set for that range will be overwritten.  The start and end offsets are
	 * inclusive and will be clamped if out of range.
	 *
	 * @param style the style
	 * @param start the start offset
	 * @param end the end offset
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	void setStyle(TextStyle style, int start, int end);

	/**
	 * Sets the receiver's tab list. Each value in the tab list specifies
	 * the space in points from the origin of the text layout to the respective
	 * tab stop.  The last tab stop width is repeated continuously.
	 *
	 * @param tabs the new tab list
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	void setTabs(int[] tabs);

	/**
	 * Sets the receiver's text.
	 *<p>
	 * Note: Setting the text also clears all the styles. This method
	 * returns without doing anything if the new text is the same as
	 * the current text.
	 * </p>
	 *
	 * @param text the new text
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the text is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 */
	void setText(String text);

	/**
	 * Sets the text direction of the receiver, which must be one
	 * of <code>SWT.LEFT_TO_RIGHT</code>, <code>SWT.RIGHT_TO_LEFT</code>
	 * or <code>SWT.AUTO_TEXT_DIRECTION</code>.
	 *
	 * <p>
	 * <b>Warning</b>: This API is currently only implemented on Windows.
	 * It doesn't set the base text direction on GTK and Cocoa.
	 * </p>
	 *
	 * @param textDirection the new text direction
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 * @since 3.103
	 */
	void setTextDirection(int textDirection);

	/**
	 * Sets the line width of the receiver, which determines how
	 * text should be wrapped and aligned. The default value is
	 * <code>-1</code> which means wrapping is disabled.
	 *
	 * @param width the new width
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_INVALID_ARGUMENT - if the width is <code>0</code> or less than <code>-1</code></li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been disposed</li>
	 * </ul>
	 *
	 * @see #setAlignment(int)
	 */
	void setWidth(int width);

	/**
	 * Returns a string containing a concise, human-readable
	 * description of the receiver.
	 *
	 * @return a string representation of the receiver
	 */
	@Override
	String toString();

}