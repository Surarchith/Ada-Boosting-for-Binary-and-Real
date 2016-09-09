package adaboosting;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class Gui
extends JFrame {
    private JPanel contentPane;
    protected static JTextField tInput;
    protected static JTextField tOutput;
    protected static JLabel lerror;
    protected static BufferedWriter bw;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable(){

            @Override
            public void run() {
                try {
                    Gui frame = new Gui();
                    frame.setVisible(true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Gui() {
        this.setDefaultCloseOperation(3);
        this.setBounds(100, 100, 500, 150);
        this.contentPane = new JPanel();
        this.contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        this.setContentPane(this.contentPane);
        this.contentPane.setLayout(null);
        this.setTitle("Binary AdaBoosting");
        JLabel lblInputFile = new JLabel("Input File Path: ");
        lblInputFile.setHorizontalAlignment(4);
        lblInputFile.setFont(new Font("Arial", 1, 16));
        lblInputFile.setBounds(10, 11, 143, 23);
        this.contentPane.add(lblInputFile);
        JLabel lblOutputFilePath = new JLabel("Output File Path: ");
        lblOutputFilePath.setHorizontalAlignment(4);
        lblOutputFilePath.setFont(new Font("Arial", 1, 16));
        lblOutputFilePath.setBounds(10, 42, 143, 23);
        this.contentPane.add(lblOutputFilePath);
        tInput = new JTextField();
        tInput.setBounds(156, 14, 318, 20);
        this.contentPane.add(tInput);
        tInput.setColumns(10);
        tOutput = new JTextField();
        tOutput.setColumns(10);
        tOutput.setBounds(156, 45, 318, 20);
        this.contentPane.add(tOutput);
        lerror = new JLabel("");
        lerror.setBounds(10, 86, 365, 14);
        this.contentPane.add(lerror);
        JButton btnNewButton = new JButton("Run");
        btnNewButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    Gui.bw = new BufferedWriter(new FileWriter(Gui.tOutput.getText().toString()));
                    RealAdaBoosting.initialize(Gui.tInput.getText().toString());
                    RealAdaBoosting.realAdaBoosting();
                    Gui.lerror.setText("Output written successfully");
                    Gui.bw.close();
                }
                catch (IOException e) {
                    Gui.lerror.setText("ERROR: IOException occured");
                }
            }
        });
        btnNewButton.setBounds(385, 77, 89, 23);
        this.contentPane.add(btnNewButton);
    }

}