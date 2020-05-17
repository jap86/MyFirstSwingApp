import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private TextPanel textPanel;
    private Toolbar toolbar;
    private FormPanel formPanel;

    public MainFrame() {
        super("Hello World");

        setLayout(new BorderLayout());

        textPanel = new TextPanel();
        toolbar = new Toolbar();
        formPanel = new FormPanel();

        toolbar.setStringListener(text -> textPanel.appendText(text));

        formPanel.setFormListener(formEvent -> {
            String name = "Name: " + formEvent.getName();
            String occupation = "Occupation: " + formEvent.getOccupation();
            String ageCategory = "Age category: " + formEvent.getAgeCategory();
            String maritalStatus = "Marital status: " + formEvent.getMaritalStatus();
            String gender = "Gender : " + formEvent.getGender();

            textPanel.appendText(
                    name + "\n"
                            + occupation + "\n"
                            + ageCategory + "\n"
                            + maritalStatus + "\n"
                            + getIsClubMemberString(formEvent) + "\n"
                            + gender + "\n\n"
            );
        });

        add(formPanel, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
        add(toolbar, BorderLayout.NORTH);

        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private String getIsClubMemberString(FormEvent formEvent) {
        String isClubMemberValue = formEvent.isClubMember() ? "Yes" : "No";
        String isClubMember = "Club member: " + isClubMemberValue;

        if (formEvent.isClubMember()) {
            isClubMember += "\nMember id: " + formEvent.getMemberID();
        }

        return isClubMember;
    }
}
