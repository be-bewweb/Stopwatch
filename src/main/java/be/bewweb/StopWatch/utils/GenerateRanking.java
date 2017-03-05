package be.bewweb.StopWatch.utils;

import be.bewweb.StopWatch.dao.beans.Course;
import be.bewweb.StopWatch.dao.beans.Team;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

/**
 * Created by Quentin on 17-02-16.
 */
public class GenerateRanking {

    private Course course;
    private ArrayList<Team> familyA;
    private ArrayList<Team> familyB;
    private ArrayList<Team> youngA;
    private ArrayList<Team> youngB;
    private ArrayList<Team> youngC;
    private ArrayList<Team> adult;

    private ArrayList<Team> young;
    private ArrayList<Team> senior;
    private ArrayList<Team> veteranA;
    private ArrayList<Team> veteranB;
    private ArrayList<Team> women;
    private ArrayList<Team> mixed;
    private ArrayList<Team> mainRanking;


    public GenerateRanking(Course course) {
        this.course = course;
    }

    private void generateCategories() {
        familyA = new ArrayList<>();
        familyB = new ArrayList<>();
        youngA = new ArrayList<>();
        youngB = new ArrayList<>();
        youngC = new ArrayList<>();
        adult = new ArrayList<>();

        young = new ArrayList<>();
        senior = new ArrayList<>();
        veteranA = new ArrayList<>();
        veteranB = new ArrayList<>();
        women = new ArrayList<>();
        mixed = new ArrayList<>();

        mainRanking = new ArrayList<>();


        int sAdult = course.getCategory().getAdult();
        int sChild = course.getCategory().getChild();

        int sYoung = course.getCategory().getYoung();
        int sSenior = course.getCategory().getSenior();
        int sVeteranA = course.getCategory().getVeteranA();

        for (Team team : course.getTeams()) {
            if (team.getEndTime().size() >= course.getNumberOfTurns() && team.isRegistrationValidated()) {
                //mainRanking
                mainRanking.add(team);

                //Familles A : A [0;12] et B ]16;+[.
                if (team.getRunner1().getAge() <= sChild && team.getRunner2().getAge() > sAdult) {
                    familyA.add(team);
                }
                if (team.getRunner2().getAge() <= sChild && team.getRunner1().getAge() > sAdult) {
                    familyA.add(team);
                }

                //Familles B : A ]12;16] et B ]16;+[.
                if (team.getRunner1().getAge() > sChild && team.getRunner1().getAge() <= sAdult && team.getRunner2().getAge() > sAdult) {
                    familyB.add(team);
                }
                if (team.getRunner2().getAge() > sChild && team.getRunner2().getAge() <= sAdult && team.getRunner1().getAge() > sAdult) {
                    familyB.add(team);
                }

                //Jeunes A : A et B [0;12].
                if (team.getRunner1().getAge() <= sChild && team.getRunner2().getAge() <= sChild) {
                    youngA.add(team);
                }

                //Jeunes B : A [0;12] - B ]12;16].
                if (team.getRunner1().getAge() > sChild && team.getRunner1().getAge() <= sAdult && team.getRunner2().getAge() <= sChild) {
                    youngB.add(team);
                }
                if (team.getRunner2().getAge() > sChild && team.getRunner2().getAge() <= sAdult && team.getRunner1().getAge() <= sChild) {
                    youngB.add(team);
                }

                //Jeunes C : A et B ]12;16].
                if (team.getRunner1().getAge() > sChild && team.getRunner1().getAge() <= sAdult && team.getRunner2().getAge() > sChild && team.getRunner2().getAge() <= sAdult) {
                    youngC.add(team);
                }

                //Adultes : A et B ]16;+[.
                if (team.getRunner1().getAge() > sAdult && team.getRunner2().getAge() > sAdult) {
                    adult.add(team);
                }
                //Un classement général avec mention des catégories (moyenne d?âge des deux équipiers).
                float avrAge = ((float) team.getRunner1().getAge() + team.getRunner2().getAge()) / ((float) 2);

                //Jeunes : moins de 21 ans
                if (avrAge < sYoung && team.getRunner2().isMan() && team.getRunner1().isMan()) {
                    young.add(team);
                }

                //Seniors : plus ou égal à 21 ans et moins de 40 ans
                if (avrAge >= sYoung && avrAge < sSenior && team.getRunner2().isMan() && team.getRunner1().isMan()) {
                    senior.add(team);
                }

                //Vétérans A : plus ou égal à 40 ans et moins de 50 ans
                if (avrAge >= sSenior && avrAge < sVeteranA && team.getRunner2().isMan() && team.getRunner1().isMan()) {
                    veteranA.add(team);
                }
                //Vétérans B : plus de 50 ans
                if (avrAge >= sVeteranA && team.getRunner2().isMan() && team.getRunner1().isMan()) {
                    veteranB.add(team);
                }

                //Dame
                if (!team.getRunner1().isMan() && !team.getRunner2().isMan()) {
                    women.add(team);
                }

                //Mixte
                if (!team.getRunner1().isMan() && team.getRunner2().isMan()) {
                    mixed.add(team);
                }
                if (team.getRunner1().isMan() && !team.getRunner2().isMan()) {
                    mixed.add(team);
                }
            }
        }
    }

    private void sortCategory() {
        familyA.sort((o1, o2) -> (int) ((o1.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o1.getStartTime().getMillis()) - (o2.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o2.getStartTime().getMillis())));
        familyB.sort((o1, o2) -> (int) ((o1.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o1.getStartTime().getMillis()) - (o2.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o2.getStartTime().getMillis())));
        youngA.sort((o1, o2) -> (int) ((o1.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o1.getStartTime().getMillis()) - (o2.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o2.getStartTime().getMillis())));
        youngB.sort((o1, o2) -> (int) ((o1.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o1.getStartTime().getMillis()) - (o2.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o2.getStartTime().getMillis())));
        youngC.sort((o1, o2) -> (int) ((o1.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o1.getStartTime().getMillis()) - (o2.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o2.getStartTime().getMillis())));
        adult.sort((o1, o2) -> (int) ((o1.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o1.getStartTime().getMillis()) - (o2.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o2.getStartTime().getMillis())));
        young.sort((o1, o2) -> (int) ((o1.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o1.getStartTime().getMillis()) - (o2.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o2.getStartTime().getMillis())));
        senior.sort((o1, o2) -> (int) ((o1.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o1.getStartTime().getMillis()) - (o2.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o2.getStartTime().getMillis())));
        veteranA.sort((o1, o2) -> (int) ((o1.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o1.getStartTime().getMillis()) - (o2.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o2.getStartTime().getMillis())));
        veteranB.sort((o1, o2) -> (int) ((o1.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o1.getStartTime().getMillis()) - (o2.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o2.getStartTime().getMillis())));
        women.sort((o1, o2) -> (int) ((o1.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o1.getStartTime().getMillis()) - (o2.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o2.getStartTime().getMillis())));
        mixed.sort((o1, o2) -> (int) ((o1.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o1.getStartTime().getMillis()) - (o2.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o2.getStartTime().getMillis())));
        mainRanking.sort((o1, o2) -> (int) ((o1.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o1.getStartTime().getMillis()) - (o2.getEndTime().get(course.getNumberOfTurns() - 1).getMillis() - o2.getStartTime().getMillis())));
    }

    private void generateHTMLfile(ArrayList<Team> ranking, String rankingName, String path) throws IOException {
        if (ranking.size() > 0) {
            String res = "<html><head><meta charset=\"UTF-8\" /><link rel=\"stylesheet\" href=\"bootstrap.min.css\" /></head><body><div class=\"table-responsive\"><table style=\"font-size: 10px!important;\" class=\"table table-bordered\"><thead><tr><th colspan=\"9\">" +
                    "<h1>" + rankingName + "</h1>" +
                    "</th></tr></thead><tbody><tr><th width=\"5%\"></th><th width=\"5%\">Dos.</th><th width=\"15%\">Nom</th><th width=\"15%\">Prénom</th><th width=\"15%\">Nom</th><th width=\"15%\">Prénom</th><th width=\"10%\">Temps</th><th width=\"10%\">Type</th></tr>";
            int i = 1;
            for (Team team : ranking) {
                DateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                String time = "n/a";
                if (team.getTime(course.getNumberOfTurns()) != null) {
                    time = sdf.format(team.getTime(course.getNumberOfTurns()));
                }

                res += "<tr><td>" + i + "</td><td>" + team.getBib() + "</td><td>" + team.getRunner1().getName() + "</td><td>" + team.getRunner1().getFirstname() + "</td><td>" + team.getRunner2().getName() + "</td><td>" + team.getRunner2().getFirstname() + "</td><td>" + time + "</td><td>" + team.getType() + "</td></tr>";
                i++;
            }

            res += "</tbody></table></div></body></html>";

            Writer writer = new OutputStreamWriter(new FileOutputStream(new File(path)), Charset.forName("UTF-8"));
            writer.write(res);
            writer.close();

        }
    }

    private void generateCSVfile(ArrayList<Team> ranking, String rankingName, String path) throws IOException {
        if (ranking.size() > 0) {
            String res = "#;Dossart;Parcours;Nom;Prénom;Nom;Prénom;Type;Catégorie;Temps" + System.getProperty("line.separator");
            int i = 1;
            for (Team team : ranking) {
                DateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

                String time = "n/a";
                if (team.getTime(course.getNumberOfTurns()) != null) {
                    time = sdf.format(team.getTime(course.getNumberOfTurns()));
                }

                res += "" + i + ";" + team.getBib() + ";" + course.getKm() + ";" + team.getRunner1().getName() + ";" + team.getRunner1().getFirstname() + ";" + team.getRunner2().getName() + ";" + team.getRunner2().getFirstname() + ";" + course.getCategory().getCategoryOfTeam(team) + ";" + team.getType() + ";" + time + System.getProperty("line.separator");
                i++;
            }

            Writer writer = new OutputStreamWriter(new FileOutputStream(new File(path)), Charset.forName("UTF-16LE"));
            writer.write(res);
            writer.close();

        }
    }


    public boolean generate(String path) {
        generateCategories();
        sortCategory();
        new File(path + "/" + course.getName()).mkdir();
        String completPath = path + File.separator.toString() + course.getName() + File.separator.toString();
        try {

            InputStream ddlStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("bootstrap.min.css");

            try (FileOutputStream fos = new FileOutputStream(completPath + "0-bootstrap.min.css")) {
                byte[] buf = new byte[2048];
                int r;
                while (-1 != (r = ddlStream.read(buf))) {
                    fos.write(buf, 0, r);
                }
            }
            if (!course.getCategory().isMaster()) {
                generateHTMLfile(familyA, "Famille A", completPath + "1-familyA.html");
                generateHTMLfile(familyB, "Famille B", completPath + "2-familyb.html");
                generateHTMLfile(youngA, "Jeune A", completPath + "3-youngA.html");
                generateHTMLfile(youngB, "Jeune B", completPath + "4-youngB.html");
                generateHTMLfile(youngC, "Jeune C", completPath + "5-youngC.html");
                generateHTMLfile(adult, "Adulte", completPath + "6-adult.html");
            } else {
                generateHTMLfile(young, "Jeune", completPath + "1-young-master.html");
                generateHTMLfile(senior, "Senior", completPath + "2-senior-master.html");
                generateHTMLfile(veteranA, "Vétéran A", completPath + "3-veteranA-master.html");
                generateHTMLfile(veteranB, "Vétéran B", completPath + "4-veteranB-master.html");
                generateHTMLfile(women, "Fille", completPath + "5-women-master.html");
                generateHTMLfile(mixed, "Mixte", completPath + "6-mixed-master.html");
            }
            generateHTMLfile(mainRanking, "7-Classement général", completPath + "general.html");
            generateCSVfile(mainRanking, "8-Classement général", completPath + "general.csv");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
