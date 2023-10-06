package awesome.console.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * For a Configurable implementation correctly declared using an EP, the implementation's
 * constructor is not invoked by the IntelliJ Platform until a user chooses the corresponding
 * Settings displayName in the Settings Dialog menu.
 *
 * A Configurable instance's lifetime ends when OK or Cancel is selected in the Settings
 * Dialog. An instance's Configurable.disposeUIResources() is called when the Settings
 * Dialog is closing.
 *
 * ref: https://plugins.jetbrains.com/docs/intellij/settings-guide.html
 */
public class AwesomeConsoleConfig implements Configurable {

	private AwesomeConsoleConfigForm form;

	private final AwesomeConsoleStorage storage;

	public AwesomeConsoleConfig() {
		this.storage = AwesomeConsoleStorage.getInstance();
	}

	private void initFromConfig() {
		form.limitLineMatchingByCheckBox.setSelected(storage.LIMIT_LINE_LENGTH);

		form.matchLinesLongerThanCheckBox.setEnabled(storage.LIMIT_LINE_LENGTH);
		form.matchLinesLongerThanCheckBox.setSelected(storage.SPLIT_ON_LIMIT);

		form.searchForURLsFileCheckBox.setSelected(storage.SEARCH_URLS);

		form.maxLengthTextField.setText(String.valueOf(storage.LINE_MAX_LENGTH));
		form.maxLengthTextField.setEnabled(storage.LIMIT_LINE_LENGTH);
		form.maxLengthTextField.setEditable(storage.LIMIT_LINE_LENGTH);
	}

	private void showErrorDialog() {
		JOptionPane.showMessageDialog(form.mainpanel, "Error: Please enter a positive number.", "Invalid value", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Configurable
	 */
	@Nls
	@Override
	public String getDisplayName() {
		return "Awesome Console";
	}

	@Nullable
	@Override
	public String getHelpTopic() {
		return "help topic";
	}

	@Nullable
	@Override
	public JComponent createComponent() {
		form = new AwesomeConsoleConfigForm();
		initFromConfig();
		return form.mainpanel;
	}

	@Override
	public boolean isModified() {
		final String text = form.maxLengthTextField.getText().trim();
		if (text.length() < 1) {
			return true;
		}
		final int len;
		try {
			len = Integer.parseInt(text);
		} catch (final NumberFormatException nfe) {
			return true;
		}
		return form.limitLineMatchingByCheckBox.isSelected() != storage.LIMIT_LINE_LENGTH
				|| len != storage.LINE_MAX_LENGTH
				|| form.matchLinesLongerThanCheckBox.isSelected() != storage.SPLIT_ON_LIMIT
				|| form.searchForURLsFileCheckBox.isSelected() != storage.SEARCH_URLS;
	}

	@Override
	public void apply() {
		final String text = form.maxLengthTextField.getText().trim();
		if (text.length() < 1) {
			showErrorDialog();
			return;
		}
		final int maxLength;
		try {
			maxLength = Integer.parseInt(text);
		} catch (final NumberFormatException nfe) {
			showErrorDialog();
			return;
		}
		if (maxLength < 1) {
			showErrorDialog();
			return;
		}

		storage.LIMIT_LINE_LENGTH = form.limitLineMatchingByCheckBox.isSelected();
		storage.LINE_MAX_LENGTH = maxLength;
		storage.SPLIT_ON_LIMIT = form.matchLinesLongerThanCheckBox.isSelected();
		storage.SEARCH_URLS = form.searchForURLsFileCheckBox.isSelected();
	}

	@Override
	public void reset() {
		initFromConfig();
	}

	@Override
	public void disposeUIResources() {
		form = null;
	}
}
