package com.mnghiem.projectmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HelpActivity extends BaseActivity {

    private ImageView btnBack;
    private LinearLayout helpContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        btnBack = findViewById(R.id.btnBack);
        helpContainer = findViewById(R.id.helpContainer);

        // Sự kiện quay lại
        btnBack.setOnClickListener(v -> finish());

        // Thêm các mục trợ giúp
        addHelp("About Project Manager", "Project Manager is an all-in-one task and project management application designed to streamline your workflow and enhance team collaboration. Whether you're managing personal to-dos or leading a complex project across multiple departments, Project Manager provides the tools you need to stay organized, efficient, and in control.\n" +
                "\n" +
                "With intuitive features such as hierarchical workspaces (groups and boards), customizable tasks, checklists, priority levels, due dates, and file attachments, users can structure their work to fit any scenario. Team members can be invited to specific tasks or groups, and roles such as \"admin\" or \"member\" help define permissions clearly.\n" +
                "\n" +
                "The application also offers real-time notifications, personal settings like dark mode and language preferences, and visual dashboards that track your performance through statistics such as completed, ongoing, or overdue tasks.\n" +
                "\n" +
                "Whether you're a student, freelancer, or part of a professional team, Project Manager is built to adapt to your workflow and help you achieve your goals with clarity and ease.");
        addHelp("Create project", "Creating a project in Project Manager allows you to define a focused area of work within a workspace. Each project is organized as a board (folder) and is designed to hold related tasks, making it easier to manage specific goals, deadlines, and responsibilities.\n" +
                "\n" +
                "When setting up a project, you can give it a meaningful name and description, assign it to a workspace, and customize its appearance with a distinct color. This helps visually differentiate projects and maintain a clean, organized structure.\n" +
                "\n" +
                "Projects serve as the backbone of your workflow. Inside each project, you can create tasks, set priorities, assign users, track deadlines, add checklists, upload attachments, and follow discussions through comments. This detailed structure ensures that all work is transparent and accessible to the team.\n" +
                "\n" +
                "Whether you're launching a marketing campaign, planning an event, or tracking product development, creating a project keeps everything centralized, coordinated, and aligned with your team's objectives.");
        addHelp("Create and Assign Task", "Creating and assigning tasks in Project Manager is at the core of managing work efficiently. Tasks represent specific actions or goals that need to be completed within a project. With just a few steps, you can define what needs to be done, who is responsible, and when it should be completed.\n" +
                "\n" +
                "When creating a task, you can enter a title, provide a detailed description, set a due date, assign a priority level (low, medium, high), and attach relevant files or links. You can also break the task into smaller steps using checklists for more granular tracking.\n" +
                "\n" +
                "Assigning the task to one or more team members ensures that responsibilities are clearly defined. Each assignee will be notified and can view the task details, update progress, and leave comments or questions.\n" +
                "\n" +
                "This feature enables smooth collaboration, accountability, and transparency across teams—whether you're working remotely, managing a team project, or handling personal responsibilities. By organizing and delegating tasks effectively, you keep your project on track and your team aligned.");
        addHelp("Track Progress", "Tracking progress in Project Manager helps you stay informed about how tasks and projects are advancing over time. It enables both individuals and teams to monitor completion rates, spot delays, and make timely decisions to keep everything on schedule.\n" +
                "\n" +
                "Each task is labeled with a clear status—such as “To Do,” “In Progress,” or “Completed” — and includes important details like due dates, priority levels, and assigned members. You can also use checklists within tasks to track sub-items as they are completed, offering a more detailed view of ongoing work.\n" +
                "\n" +
                "For a broader overview, the system provides personal and team-wide statistics, showing the number of tasks completed, those still in progress, overdue items, and even tasks that were canceled. These analytics are updated automatically and visually displayed, helping users easily grasp their performance and workload.\n" +
                "\n" +
                "Whether you're managing a personal to-do list or coordinating a complex team project, tracking progress in Project Manager ensures visibility, accountability, and consistent forward momentum.");
        addHelp("Personalization", "Personalization in Project Manager allows users to tailor the app experience to their individual preferences and working style. From interface appearance to language settings, you can adjust various elements to make the platform more comfortable and efficient for daily use.\n" +
                "\n" +
                "Users can switch between light and dark modes for better visibility in different environments, choose their preferred language, and update their profile information—including name, password, and profile picture. These settings help create a more personalized and user-friendly experience.\n" +
                "\n" +
                "Additionally, Project Manager remembers your recent activities and displays personalized statistics, such as the number of tasks you've completed, those currently in progress, or overdue. This data helps you reflect on your productivity and better plan your work.\n" +
                "\n" +
                "By enabling customization at the user level, Project Manager ensures that every individual—whether working solo or in a team - can feel more in control and connected to their workflow.");
    }

    private void addHelp(String title, String description) {
        LinearLayout helpItem = new LinearLayout(this);
        helpItem.setOrientation(LinearLayout.VERTICAL);
        helpItem.setPadding(24, 24, 24, 24);
        helpItem.setBackgroundResource(R.drawable.bg_help_card);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 24, 0, 24);
        helpItem.setLayoutParams(params);



        TextView tvTitle = new TextView(this);
        tvTitle.setText("• " + title);
        tvTitle.setTextSize(16);
        tvTitle.setTextColor(0xFF000000);
        tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitle.setFocusable(true);
        tvTitle.setContentDescription("Mục trợ giúp: " + title);

        TextView tvDesc = new TextView(this);
        tvDesc.setText(description);
        tvDesc.setTextSize(14);
        tvDesc.setTextColor(0xFF555555);
        tvDesc.setVisibility(View.GONE);
        tvDesc.setFocusable(true);
        tvDesc.setContentDescription("Nội dung trợ giúp: " + description);

        // Toggle hiển thị khi nhấn tiêu đề
        tvTitle.setOnClickListener(v -> {
            if (tvDesc.getVisibility() == View.VISIBLE) {
                tvDesc.setVisibility(View.GONE);
            } else {
                tvDesc.setVisibility(View.VISIBLE);
            }
        });

        helpItem.addView(tvTitle);
        helpItem.addView(tvDesc);
        helpContainer.addView(helpItem);
    }
}
