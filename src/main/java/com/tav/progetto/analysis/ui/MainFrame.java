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
import java.util.ArrayList;

public class MainFrame extends JFrame {
    private final JTextField pathField = new JTextField(30);
    private final JButton browseBtn = new JButton("Browse");
    private final JButton runBtn = new JButton("Run Analysis");
    private final JTextField godMethodsField = new JTextField("20",4);
    private final JTextField godFieldsField = new JTextField("15",4);
    private final JTextField longParamsField = new JTextField("4",4);
    private final JTextField lazyMethodsField = new JTextField("2",4);
    private final JTextField lazyFieldsField = new JTextField("1",4);
    private final JTextField utilityRatioField = new JTextField("0.80",4);
    private final JTextField utilityInstanceFieldsField = new JTextField("1",4);
    private final JTextField badSingletonMutableFieldsField = new JTextField("0",4);
    private final JTextField switchPerClassField = new JTextField("2",4);
    private final JTextField switchCasesField = new JTextField("5",4);
    private final JTextField vendorPackagesField = new JTextField("oracle.,com.ibm.,com.vendor.",24);
    private final JTextField yoyoDepthField = new JTextField("3",4);
    private final JTextField constantFieldsField = new JTextField("2",4);
    private final JTextField constantMethodsField = new JTextField("1",4);
    private final JLabel healthLabel = new JLabel("Health: -");
    private final DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Class","Methods","Fields","Violations","Severity"},0);

    public MainFrame() {
        super("Analisi - Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(2140,600);
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

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("God max methods:")); bottom.add(godMethodsField);
        bottom.add(new JLabel("God max fields:")); bottom.add(godFieldsField);
        bottom.add(new JLabel("Long params:")); bottom.add(longParamsField);
        bottom.add(new JLabel("Lazy max methods:")); bottom.add(lazyMethodsField);
        bottom.add(new JLabel("Lazy max fields:")); bottom.add(lazyFieldsField);
        bottom.add(new JLabel("Utility static ratio:")); bottom.add(utilityRatioField);
        bottom.add(new JLabel("Utility max instance fields:")); bottom.add(utilityInstanceFieldsField);
        bottom.add(new JLabel("BadSingleton max mutable fields:")); bottom.add(badSingletonMutableFieldsField);
        bottom.add(new JLabel("Switch max/class:")); bottom.add(switchPerClassField);
        bottom.add(new JLabel("Switch max cases/method:")); bottom.add(switchCasesField);
        bottom.add(new JLabel("Vendor forbidden packages:")); bottom.add(vendorPackagesField);
        bottom.add(new JLabel("Yoyo max depth:")); bottom.add(yoyoDepthField);
        bottom.add(new JLabel("Const min fields:")); bottom.add(constantFieldsField);
        bottom.add(new JLabel("Const max methods:")); bottom.add(constantMethodsField);
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
            JOptionPane.showMessageDialog(this, "Seleziona una directory di .class, un file .class o un file .jar");
            return;
        }
        File selected = new File(path);
        TargetType targetType;
        if (selected.isDirectory()) {
            targetType = TargetType.DIRECTORY;
        } else if (selected.isFile() && selected.getName().toLowerCase().endsWith(".jar")) {
            targetType = TargetType.JAR;
        } else if (selected.isFile() && selected.getName().toLowerCase().endsWith(".class")) {
            targetType = TargetType.CLASS_FILE;
        } else {
            JOptionPane.showMessageDialog(this, "Seleziona una directory di .class, un file .class o un file .jar");
            return;
        }
        AnalysisProfile profile = new AnalysisProfile();
        try { profile.godClassMaxMethods = Integer.parseInt(godMethodsField.getText()); } catch (Exception ex) {}
        try { profile.godClassMaxFields = Integer.parseInt(godFieldsField.getText()); } catch (Exception ex) {}
        try { profile.longParamListMaxParams = Integer.parseInt(longParamsField.getText()); } catch (Exception ex) {}
        try { profile.lazyClassMaxMethods = Integer.parseInt(lazyMethodsField.getText()); } catch (Exception ex) {}
        try { profile.lazyClassMaxFields = Integer.parseInt(lazyFieldsField.getText()); } catch (Exception ex) {}
        try { profile.utilityMinStaticMethodRatio = Double.parseDouble(utilityRatioField.getText()); } catch (Exception ex) {}
        try { profile.utilityMaxInstanceFields = Integer.parseInt(utilityInstanceFieldsField.getText()); } catch (Exception ex) {}
        try { profile.badSingletonMaxMutableInstanceFields = Integer.parseInt(badSingletonMutableFieldsField.getText()); } catch (Exception ex) {}
        try { profile.switchManiaMaxSwitchesPerClass = Integer.parseInt(switchPerClassField.getText()); } catch (Exception ex) {}
        try { profile.switchManiaMaxCasesPerMethod = Integer.parseInt(switchCasesField.getText()); } catch (Exception ex) {}
        profile.vendorLockInForbiddenPackages = parseCommaSeparatedList(vendorPackagesField.getText());
        try { profile.yoyoMaxInheritanceDepth = Integer.parseInt(yoyoDepthField.getText()); } catch (Exception ex) {}
        try { profile.constantInterfaceMinConstantFields = Integer.parseInt(constantFieldsField.getText()); } catch (Exception ex) {}
        try { profile.constantInterfaceMaxMethods = Integer.parseInt(constantMethodsField.getText()); } catch (Exception ex) {}

        ProjectAnalyzer pa = new ProjectAnalyzer();
        ProjectAnalysisResult res = pa.analyze(new TargetDescriptor(path, targetType), profile);

        tableModel.setRowCount(0);
        for (var cr : res.classResults) {
            String viols = cr.violations.isEmpty() ? "-" : String.join(",", cr.violations.stream().map(v->v.ruleId).toArray(String[]::new));
            tableModel.addRow(new Object[]{cr.metrics.className, cr.metrics.totalMethods, cr.metrics.fields, viols, cr.overallSeverity});
        }
        healthLabel.setText("Health: " + res.healthScore);
    }

    private java.util.List<String> parseCommaSeparatedList(String raw) {
        java.util.List<String> values = new ArrayList<>();
        if (raw == null) return values;
        for (String token : raw.split(",")) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty()) values.add(trimmed);
        }
        return values;
    }
}
