# Q5 Currency-converter handler styles

## Aim and implementations
Four independent before/after pairs implement the same three-currency conversion and close with WindowAdapter. Fixed illustrative rates are USD 0.012 and EUR 0.011 per INR; these are assignment constants, not live exchange rates. Convert source to INR and then to the selected target. Invalid, negative and non-finite amounts receive Invalid amount. The supplied Converter.java remains a separate INR-to-USD/EUR TODO completion with result-label output.

| Style | Access to frame fields | Reuse and change |
|---|---|---|
| Frame implements ActionListener | Direct | Frame is also handler; use `this` for new triggers; mixes roles |
| Named inner class | Implicit enclosing frame reference | One reusable named object per window; clear for longer logic |
| Anonymous inner class | Captures enclosing instance | Reuse a stored ActionListener reference; verbose for a short action |
| Lambda | Captures fields through enclosing instance | Concise single-method handler; reuse the same stored reference |

The after versions add the same listener to the amount field and show conversion feedback in a dialog as well as the result label. Raw unified diffs and added/removed line counts are generated in results/change-effort.csv and results/changes.diff. Counts describe these implementations, not a universal property of a style. An extracted action method makes all four changes small.

## Complete the Code
supplied/Converter.java replaces only the two supplied TODO blocks: one lambda instance is registered for button and Enter; convert parses and formats two decimals or displays Invalid amount. Actual screenshots supplied-valid.png and supplied-invalid.png show 1000 INR → 12.00 USD and abc → Invalid amount. Four after-style screenshots additionally demonstrate Enter-triggered dialogs, and four before screenshots demonstrate button-triggered labels.

## Alternative shared Action and method reference
```java
private void convert(ActionEvent event) { convert(); }
ActionListener handler = this::convert;
button.addActionListener(handler);
amount.addActionListener(handler);

Action convertAction = new AbstractAction("Convert") {
    @Override public void actionPerformed(ActionEvent event) { convert(); }
};
button.setAction(convertAction);
menuItem.setAction(convertAction);
rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
    .put(KeyStroke.getKeyStroke("control ENTER"), "convert");
rootPane.getActionMap().put("convert", convertAction);
```
A no-argument convert() alone is not compatible with ActionListener's ActionEvent argument; the overload above supplies the correct method-reference signature. A shared Action centralizes the name, enabled state and behaviour for button, menu and shortcut. Adding a new trigger is one binding rather than another copy of conversion code.

## Evaluation and complexity
For a large application I would use a shared Action delegating to a separately testable conversion service. A lambda suits a small action; long handlers should become named methods/classes. All four styles have O(1) handler selection and O(L) input parsing/formatting for input length L (bounded currencies). The frame style uses the existing frame as listener, adding no separate handler object. Named/anonymous styles use one handler object per window; a capturing lambda or method reference represents one shared handler value, with physical allocation subject to JVM implementation. t triggers require O(t) registration references in all styles, even if only one logical handler/Action is shared. Currency selectors and conversion arithmetic do not become cheaper merely by changing listener syntax.

## Evidence and execution

`results/compiler.txt` records actual compilation. `results/gui-tests.txt` records assertions against running desktop windows. Screenshots are real screen captures and their filenames identify the requirement demonstrated. Tests were executed in the workspace; personally rerun every required task and understand the design before submission.
