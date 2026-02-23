package com.tav.progetto.analysis.ui;

import com.tav.progetto.analysis.core.ProjectAnalyzer;
import com.tav.progetto.analysis.core.TargetDescriptor;
import com.tav.progetto.analysis.core.TargetType;
import com.tav.progetto.analysis.core.ProjectAnalysisResult;
import com.tav.progetto.analysis.rules.AnalysisProfile;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class MainFrame extends JFrame {
    private final JTextField pathField = new JTextField(30);
    private final JButton browseBtn = new JButton("Browse");
    private final JButton runBtn = new JButton("Run Analysis");
    private final JTextField godMethodsField = new JTextField("20",4);
    private final JTextField godFieldsField = new JTextField("15",4);
    private final JTextField longParamsField = new JTextField("4",4);
    private final JLabel healthLabel = new JLabel("Health: -");
    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Class","Methods","Fields","Violations","Severity"},0);

    public MainFrame() {
        super("Analisi - Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,600);
        setLocationRelativeTo(null);

        JPanel top = new JPanel();
        top.add(new JLabel("Target:"));
        top.add(pathField);
        top.add(browseBtn);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        JTable table = new JTable(tableModel);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(new JLabel("God max methods:")); bottom.add(godMethodsField);
        bottom.add(new JLabel("God max fields:")); bottom.add(godFieldsField);
        bottom.add(new JLabel("Long params:")); bottom.add(longParamsField);
        bottom.add(runBtn);
        bottom.add(healthLabel);
        add(bottom, BorderLayout.SOUTH);

        browseBtn.addActionListener(this::onBrowse);
        runBtn.addActionListener(this::onRun);
    }

    private void onBrowse(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            pathField.setText(f.getAbsolutePath());
        }
    }

    private void onRun(ActionEvent e) {
        String path = pathField.getText().trim();
        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleziona una directory di .class o un file .jar");
            return;
        }
        File selected = new File(path);
        TargetType targetType;
        if (selected.isDirectory()) {
            targetType = TargetType.DIRECTORY;
        } else if (selected.isFile() && selected.getName().toLowerCase().endsWith(".jar")) {
            targetType = TargetType.JAR;
        } else {
            JOptionPane.showMessageDialog(this, "Seleziona una directory di .class o un file .jar");
            return;
        }
        AnalysisProfile profile = new AnalysisProfile();
        try { profile.godClassMaxMethods = Integer.parseInt(godMethodsField.getText()); } catch (Exception ex) {}
        try { profile.godClassMaxFields = Integer.parseInt(godFieldsField.getText()); } catch (Exception ex) {}
        try { profile.longParamListMaxParams = Integer.parseInt(longParamsField.getText()); } catch (Exception ex) {}

        ProjectAnalyzer pa = new ProjectAnalyzer();
        ProjectAnalysisResult res = pa.analyze(new TargetDescriptor(path, targetType), profile);

        tableModel.setRowCount(0);
        for (var cr : res.classResults) {
            String viols = cr.violations.isEmpty() ? "-" : String.join(",", cr.violations.stream().map(v->v.ruleId).toArray(String[]::new));
            tableModel.addRow(new Object[]{cr.metrics.className, cr.metrics.totalMethods, cr.metrics.fields, viols, cr.overallSeverity});
        }
        healthLabel.setText("Health: " + res.healthScore);
    }
}
