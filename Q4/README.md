# Q4 Drawing-pad event forensics

## Diagnosis and failing interaction
Java names are case-sensitive. mouseclicked does not override mouseClicked, so MouseAdapter's empty callback is invoked instead. The original handler also never updates `status`, even if the name is corrected. Add one point per mouseClicked event, update the label, then request repaint. `@Override` makes accidental overloads compiler errors.

The immediate drawing variant fixes the callback and label but still draws through getGraphics. Click a dot, minimize, restore or cover/uncover the window. The dot disappears because the application's retained state contains no point to redraw; a transient Graphics context is not the drawing model. ImmediatePad.java closes that context but intentionally retains the transient-drawing defect for evidence.

## Completed program
DrawPad.java changes only the two supplied TODO regions. TODO 1 overrides mouseClicked, stores the Point, updates the count and calls repaint. TODO 2 calls super.paint and redraws every saved point as a six-pixel circle centred at x-3,y-3. The supplied main is preserved. For new code, constructing and showing AWT/Swing windows through EventQueue.invokeLater is preferable. In this exercise the supplied main was deliberately left unchanged as required.

## Compiler experiment and contradiction in the question
The assignment says to remove @Override, misspell mouseclicked and record the compiler message. That exact variant **compiles successfully**; there is no compiler diagnostic. Its screenshot misspelled.png and GUI check show Clicks: 0 after a real mouse click. variants/WithOverride keeps @Override with the misspelling; compilation fails with “method does not override or implement a method from a supertype”. Both actual compiler exit codes and the diagnostic are saved in results/compiler.txt. Inventing an error for the annotation-free version would be incorrect.

## Repaint evidence
before-minimize.png and after-restore.png show the same three stored dots and Clicks: 3. The automated test performs real minimize/restore operations and verifies a dot pixel after restoration. immediate-before.png and immediate-after.png show the disappearing-dot failure separately. Each test uses a fresh window.

## BufferedImage alternative
```text
ON CLICK(x, y)
    DRAW filled circle INTO retained offscreen image
    INCREMENT count; UPDATE label; REQUEST repaint
ON PAINT(graphics)
    CALL superclass paint
    COPY retained image TO graphics
```
Point storage is O(n) and full repaint visits O(n) points; drawing one six-pixel dot adds constant raster work. An image stores O(W*H) pixels (roughly four bytes per ARGB pixel), and repaint copies O(W*H) pixels, even though the Java code makes one drawImage call. A cached raster avoids revisiting all n historical points. When resized, define whether the bitmap preserves coordinates, expands with a copy, or scales; scaling can blur dots. Keeping both points and an image adds both costs but permits editing and rebuilding.

## Evidence and execution

`results/compiler.txt` records actual compilation. `results/gui-tests.txt` records assertions against running desktop windows. Screenshots are real screen captures and their filenames identify the requirement demonstrated. Tests were executed in the workspace; personally rerun every required task and understand the design before submission.
