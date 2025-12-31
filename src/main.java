/******************************************************************************

                            Online Java Compiler.
                Code, Compile, Run and Debug java program online.
Write your code in this editor and press "Run" button to execute it.

*******************************************************************************/

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalcApp().showUI());
    }
}

class CalcApp {
    JFrame f;
    CardLayout cl;
    JPanel cards;
    JTextField disp; 
    String op = "";
    double val = 0;
    boolean newNum = true;
    ArrayList<String> hist = new ArrayList<>();

    void showUI() {
        f = new JFrame("Simple Calculator - AP_Calculator");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(360, 480);
        cl = new CardLayout();
        cards = new JPanel(cl);

        cards.add(makeCalcPanel(), "calc");
        cards.add(makeHistoryPanel(), "hist");

        f.add(cards);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    JPanel makeCalcPanel() {
        JPanel p = new JPanel(new BorderLayout(5,5));
        disp = new JTextField("0");
        disp.setEditable(false);
        disp.setFont(new Font("SansSerif", Font.BOLD, 24));
        disp.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel top = new JPanel(new BorderLayout(5,5));
        JPanel leftBtnHolder = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton histBtn = new JButton("History");
        histBtn.addActionListener(e -> cl.show(cards, "hist"));
        leftBtnHolder.add(histBtn);
        top.add(leftBtnHolder, BorderLayout.WEST);
        top.add(disp, BorderLayout.CENTER);

        p.add(top, BorderLayout.NORTH);

        JPanel keys = new JPanel(new GridLayout(5,4,5,5));
        String[] btns = {
            "7","8","9","/",
            "4","5","6","*",
            "1","2","3","-",
            "0",".","+/-","+",
            "C","<-","="," "
        };
        for (String b : btns) {
            if (b.equals(" ")) { keys.add(new JLabel()); continue; }
            JButton btn = new JButton(b);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 18));
            btn.addActionListener(e -> press(b));
            keys.add(btn);
        }
        p.add(keys, BorderLayout.CENTER);
        return p;
    }

    JPanel makeHistoryPanel() {
        JPanel p = new JPanel(new BorderLayout(5,5));
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane sp = new JScrollPane(ta);
        p.add(sp, BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton back = new JButton("Back");
        back.addActionListener(e -> cl.show(cards, "calc"));
        JButton clear = new JButton("Clear History");
        clear.addActionListener(e -> { hist.clear(); ta.setText(""); });
        bot.add(clear);
        bot.add(back);
        p.add(bot, BorderLayout.SOUTH);

        cardsAddShowListener(p, () -> {
            StringBuilder sb = new StringBuilder();
            for (int i = hist.size()-1; i>=0; i--) {
                sb.append(hist.get(i)).append("\n");
            }
            ta.setText(sb.toString());
        });

        return p;
    }

    void cardsAddShowListener(JPanel panel, Runnable onShow) {
        panel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (panel.isShowing()) onShow.run();
            }
        });
    }

    void press(String s) {
        try {
            if (s.matches("[0-9]")) {
                if (newNum) { disp.setText(s); newNum = false; }
                else disp.setText(disp.getText() + s);
                return;
            }
            if (s.equals(".")) {
                if (newNum) { disp.setText("0."); newNum = false; }
                else if (!disp.getText().contains(".")) disp.setText(disp.getText() + ".");
                return;
            }
            if (s.equals("C")) {
                disp.setText("0"); val = 0; op = ""; newNum = true; return;
            }
            if (s.equals("<-")) {
                String t = disp.getText();
                if (!newNum && t.length()>0) {
                    t = t.substring(0, t.length()-1);
                    if (t.isEmpty()) { t = "0"; newNum = true; }
                    disp.setText(t);
                }
                return;
            }
            if (s.equals("+/-")) {
                String t = disp.getText();
                if (t.startsWith("-")) disp.setText(t.substring(1));
                else if (!t.equals("0")) disp.setText("-"+t);
                newNum = false;
                return;
            }
            if (s.equals("/")||s.equals("*")||s.equals("+")||s.equals("-")) {
                if (!op.isEmpty() && !newNum) {
                    compute();
                } else {
                    val = Double.parseDouble(disp.getText());
                }
                op = s;
                newNum = true;
                return;
            }
            if (s.equals("=")) {
                if (!op.isEmpty()) {
                    compute();
                    op = "";
                }
                newNum = true;
                return;
            }
        } catch (Exception ex) {
            disp.setText("Error"); newNum = true; op = "";
        }
    }

    void compute() {
        double b = Double.parseDouble(disp.getText());
        double res = val;
        switch (op) {
            case "+" : res = val + b; break;
            case "-" : res = val - b; break;
            case "*" : res = val * b; break;
            case "/" : res = (b==0) ? Double.NaN : val / b; break;
        }
        String rec = String.format("%s %s %s = %s", trimNum(val), op, trimNum(b), trimNum(res));
        hist.add(rec);
        disp.setText(trimNum(res));
        val = res;
    }

    String trimNum(double d) {
        if (Double.isNaN(d)) return "NaN";
        if (d == (long)d) return String.format("%d", (long)d);
        return String.format("%s", d);
    }
}
