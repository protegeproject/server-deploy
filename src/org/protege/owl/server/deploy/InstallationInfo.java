package org.protege.owl.server.deploy;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

public class InstallationInfo extends JPanel {
	private static final long serialVersionUID = -3092545187577980738L;
	
	public InstallationInfo(Configuration configuration, String message) {
		setLayout(new BorderLayout());
		JLabel label = new JLabel(message);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		add(label, BorderLayout.NORTH);
		String[] columns = { "Parameter", "Value" };
		String[][] data  = { 
				{ "Installation Directory", configuration.getParameterValue(Parameter.SERVER_PREFIX) },
				{ "Data Directory",         configuration.getParameterValue(Parameter.DATA_PREFIX) },
				{ "Log Directory",          configuration.getParameterValue(Parameter.LOG_PREFIX) },
				{ "Hostame",                configuration.getParameterValue(Parameter.HOSTNAME) }
		};
		JTable table = new JTable(data, columns);
		JScrollPane container = new JScrollPane(table);
		Dimension size = new Dimension();
		size.width  = 600;
		size.height = table.getRowCount() * table.getRowHeight();
		table.setPreferredScrollableViewportSize(size);
		add(container, BorderLayout.CENTER);
		validate();
	}


}
