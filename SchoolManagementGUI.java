import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Person class (superclass)
class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}

// Student class (inherits Person)
class Student extends Person {
    private String grade;

    public Student(String name, int age, String grade) {
        super(name, age);
        this.grade = grade;
    }

    public String getGrade() {
        return grade;
    }

    @Override
    public String toString() {
        return "Student [Name: " + getName() + ", Age: " + getAge() + ", Grade: " + grade + "]";
    }
}

// Teacher class (inherits Person)
class Teacher extends Person {
    private String subject;

    public Teacher(String name, int age, String subject) {
        super(name, age);
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return "Teacher [Name: " + getName() + ", Age: " + getAge() + ", Subject: " + subject + "]";
    }
}

// School class (manages students and teachers)
class School {
    private List<Student> students = new ArrayList<>();
    private List<Teacher> teachers = new ArrayList<>();

    public void addStudent(Student student) {
        students.add(student);
        System.out.println("Student added successfully!");
    }

    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
        System.out.println("Teacher added successfully!");
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }
}

// SchoolManagementGUI class for GUI
public class SchoolManagementGUI extends JFrame {

    private School school;
    private Connection conn;

    public SchoolManagementGUI() {
        // Initialize school management system and GUI setup
        school = new School();
        initializeDatabaseConnection();

        // Frame setup
        setTitle("School Management System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1));

        // Buttons
        JButton addStudentButton = new JButton("Add Student");
        JButton addTeacherButton = new JButton("Add Teacher");
        JButton displayStudentsButton = new JButton("Display Students");
        JButton displayTeachersButton = new JButton("Display Teachers");

        // Button listeners
        addStudentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        addTeacherButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addTeacher();
            }
        });

        displayStudentsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayStudents();
            }
        });

        displayTeachersButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayTeachers();
            }
        });

        // Add buttons to frame
        add(addStudentButton);
        add(addTeacherButton);
        add(displayStudentsButton);
        add(displayTeachersButton);

        setVisible(true);
    }

    private void initializeDatabaseConnection() {
        // Database connection setup
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/school_db", "root", "root");
            System.out.println("Connected to database successfully!");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }

    private void addStudent() {
        // Input dialogs for adding student
        String name = JOptionPane.showInputDialog("Enter student name:");
        int age = Integer.parseInt(JOptionPane.showInputDialog("Enter student age:"));
        String grade = JOptionPane.showInputDialog("Enter student grade:");

        Student student = new Student(name, age, grade);
        school.addStudent(student);

        // Insert into database
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO students (name, age, grade) VALUES (?, ?, ?)")) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, grade);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student added to database successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to add student to database: " + e.getMessage());
        }
    }

    private void addTeacher() {
        // Input dialogs for adding teacher
        String name = JOptionPane.showInputDialog("Enter teacher name:");
        int age = Integer.parseInt(JOptionPane.showInputDialog("Enter teacher age:"));
        String subject = JOptionPane.showInputDialog("Enter subject taught:");

        Teacher teacher = new Teacher(name, age, subject);
        school.addTeacher(teacher);

        // Insert into database
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO teachers (name, age, subject) VALUES (?, ?, ?)")) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, subject);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Teacher added to database successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to add teacher to database: " + e.getMessage());
        }
    }

    private void displayStudents() {
        StringBuilder studentList = new StringBuilder("Students:\n");

        // Query database for students
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {
            while (rs.next()) {
                studentList.append("Name: ").append(rs.getString("name"))
                        .append(", Age: ").append(rs.getInt("age"))
                        .append(", Grade: ").append(rs.getString
                                ("grade"))
                        .append("\n");
            }
        } catch (SQLException e) {
            studentList.append("Failed to retrieve students: ").append(e.getMessage());
        }

        JOptionPane.showMessageDialog(this, studentList.toString());
    }

    private void displayTeachers() {
        StringBuilder teacherList = new StringBuilder("Teachers:\n");

        // Query database for teachers
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM teachers")) {
            while (rs.next()) {
                teacherList.append("Name: ").append(rs.getString("name"))
                        .append(", Age: ").append(rs.getInt("age"))
                        .append(", Subject: ").append(rs.getString("subject"))
                        .append("\n");
            }
        } catch (SQLException e) {
            teacherList.append("Failed to retrieve teachers: ").append(e.getMessage());
        }

        JOptionPane.showMessageDialog(this, teacherList.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SchoolManagementGUI());
    }
}
