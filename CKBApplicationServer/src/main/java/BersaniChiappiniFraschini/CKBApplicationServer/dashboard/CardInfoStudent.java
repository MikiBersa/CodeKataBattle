package BersaniChiappiniFraschini.CKBApplicationServer.dashboard;

import java.util.Date;
import java.util.List;

public record CardInfoStudent(
        String tournament_title,
        String battle_title,
        int current_group_score,
        String group_leader,
        Date last_update,
        Date submission_deadline,
        List<Student> students,
        String API_Token

) implements CardInfo{
    record Student(String username/*, String profile_img_url*/){}
}
