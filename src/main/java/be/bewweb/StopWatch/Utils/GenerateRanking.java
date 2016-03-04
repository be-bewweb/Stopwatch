package be.bewweb.StopWatch.Utils;

import be.bewweb.StopWatch.Modele.Course;
import be.bewweb.StopWatch.Modele.Team;

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


        int sAdult = course.getCategory().getAdult();
        int sChild = course.getCategory().getChild();

        int sYoung = course.getCategory().getYoung();
        int sSenior = course.getCategory().getSenior();
        int sVeteranA = course.getCategory().getVeteranA();

        for (Team team : course.getTeams()) {
            if (team.getEndTime().size() == course.getNumberOfTurns() && team.isRegistrationValidated()) {
                //Familles A : A moins de 12 ans - B plus de 16 ans.
                if (team.getRunner1().getAge() < sChild && team.getRunner2().getAge() > sAdult) {
                    familyA.add(team);
                }
                if (team.getRunner2().getAge() < sChild && team.getRunner1().getAge() > sAdult) {
                    familyA.add(team);
                }

                //Familles B : A entre 12 et 16 ans - B plus de 16 ans.
                if (team.getRunner1().getAge() >= sChild && team.getRunner1().getAge() <= sAdult && team.getRunner2().getAge() > sAdult) {
                    familyB.add(team);
                }
                if (team.getRunner2().getAge() >= sChild && team.getRunner2().getAge() <= sAdult && team.getRunner1().getAge() > sAdult) {
                    familyB.add(team);
                }

                //Jeunes A : A et B moins de 12 ans.
                if (team.getRunner1().getAge() < sChild && team.getRunner2().getAge() < sChild) {
                    youngA.add(team);
                }

                //Jeunes B : A moins de 12 - B entre 12 et 16 ans.
                if (team.getRunner1().getAge() >= sChild && team.getRunner1().getAge() <= sAdult && team.getRunner2().getAge() < sChild) {
                    youngB.add(team);
                }
                if (team.getRunner2().getAge() >= sChild && team.getRunner2().getAge() <= sAdult && team.getRunner1().getAge() < sChild) {
                    youngB.add(team);
                }

                //Jeunes C : A et B entre 12 et 16 ans.
                if (team.getRunner1().getAge() >= sChild && team.getRunner1().getAge() <= sAdult && team.getRunner2().getAge() >= sChild && team.getRunner2().getAge() <= sAdult) {
                    youngC.add(team);
                }

                //Adultes : les équipes dont les 2 ont plus de 16 ans seront reprises dans un classement à part et seront récompensées.
                if (team.getRunner1().getAge() > sAdult && team.getRunner2().getAge() > sAdult) {
                    adult.add(team);
                }

                //Un classement général avec mention des catégories (moyenne d?âge des deux équipiers).
                float avrAge = ((float) team.getRunner1().getAge() + team.getRunner2().getAge()) / ((float) 2);

                //Jeunes : moins de 21 ans
                if (avrAge < sYoung) {
                    young.add(team);
                }

                //Seniors : plus ou égal à 21 ans et moins de 40 ans
                if (avrAge >= sYoung && avrAge < sSenior) {
                    senior.add(team);
                }

                //Vétérans A : plus ou égal à 40 ans et moins de 50 ans
                if (avrAge >= sSenior && avrAge < sVeteranA) {
                    veteranA.add(team);
                }
                //Vétérans B : plus de 50 ans
                if (avrAge >= sVeteranA) {
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
        familyA.sort((o1, o2) -> (int) ((o1.getEndTime().get(o1.getEndTime().size() - 1) - o1.getStartTime()) - (o2.getEndTime().get(o2.getEndTime().size() - 1) - o2.getStartTime())));
        familyB.sort((o1, o2) -> (int) ((o1.getEndTime().get(o1.getEndTime().size() - 1) - o1.getStartTime()) - (o2.getEndTime().get(o2.getEndTime().size() - 1) - o2.getStartTime())));
        youngA.sort((o1, o2) -> (int) ((o1.getEndTime().get(o1.getEndTime().size() - 1) - o1.getStartTime()) - (o2.getEndTime().get(o2.getEndTime().size() - 1) - o2.getStartTime())));
        youngB.sort((o1, o2) -> (int) ((o1.getEndTime().get(o1.getEndTime().size() - 1) - o1.getStartTime()) - (o2.getEndTime().get(o2.getEndTime().size() - 1) - o2.getStartTime())));
        youngC.sort((o1, o2) -> (int) ((o1.getEndTime().get(o1.getEndTime().size() - 1) - o1.getStartTime()) - (o2.getEndTime().get(o2.getEndTime().size() - 1) - o2.getStartTime())));
        adult.sort((o1, o2) -> (int) ((o1.getEndTime().get(o1.getEndTime().size() - 1) - o1.getStartTime()) - (o2.getEndTime().get(o2.getEndTime().size() - 1) - o2.getStartTime())));
        young.sort((o1, o2) -> (int) ((o1.getEndTime().get(o1.getEndTime().size() - 1) - o1.getStartTime()) - (o2.getEndTime().get(o2.getEndTime().size() - 1) - o2.getStartTime())));
        senior.sort((o1, o2) -> (int) ((o1.getEndTime().get(o1.getEndTime().size() - 1) - o1.getStartTime()) - (o2.getEndTime().get(o2.getEndTime().size() - 1) - o2.getStartTime())));
        veteranA.sort((o1, o2) -> (int) ((o1.getEndTime().get(o1.getEndTime().size() - 1) - o1.getStartTime()) - (o2.getEndTime().get(o2.getEndTime().size() - 1) - o2.getStartTime())));
        veteranB.sort((o1, o2) -> (int) ((o1.getEndTime().get(o1.getEndTime().size() - 1) - o1.getStartTime()) - (o2.getEndTime().get(o2.getEndTime().size() - 1) - o2.getStartTime())));
        women.sort((o1, o2) -> (int) ((o1.getEndTime().get(o1.getEndTime().size() - 1) - o1.getStartTime()) - (o2.getEndTime().get(o2.getEndTime().size() - 1) - o2.getStartTime())));
        mixed.sort((o1, o2) -> (int) ((o1.getEndTime().get(o1.getEndTime().size() - 1) - o1.getStartTime()) - (o2.getEndTime().get(o2.getEndTime().size() - 1) - o2.getStartTime())));
    }

    private String getHTMLRanking(String name, ArrayList<Team> ranking) {
        String res = "<html><head><meta charset=\"UTF-8\" /><link rel=\"stylesheet\" href=\"bootstrap.min.css\" /></head><body><div class=\"table-responsive\"><table class=\"table table-striped\"><thead><tr><th colspan=\"9\">" +
                "<h1>" + name + "</h1>" +
                "</th></tr></thead><tbody><tr><th width=\"5%\"></th><th width=\"5%\">Dos.</th><th width=\"15%\">Nom</th><th width=\"15%\">Prénom</th><th width=\"15%\">Nom</th><th width=\"15%\">Prénom</th><th width=\"10%\">Temps</th></tr>";
        int i = 1;
        for (Team team : ranking) {
            DateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String time = sdf.format(team.getEndTime().get(team.getEndTime().size() - 1) - team.getStartTime());

            res += "<tr><td>" + i + "</td><td>" + team.getDossard() + "</td><td>" + team.getRunner1().getName() + "</td><td>" + team.getRunner1().getFirstname() + "(" + team.getRunner1().getAge() + "ans)</td><td>" + team.getRunner2().getName() + "</td><td>" + team.getRunner2().getFirstname() + " (" + team.getRunner2().getAge() + "ans)</td><td>" + time + "</td></tr>";
            i++;
        }

        res += "</tbody></table></div></body></html>";
        return res;
    }

    private void generateFile(ArrayList<Team> ranking, String rankingName, String path) throws IOException {
        if (ranking.size() > 0) {
            Writer writer = new OutputStreamWriter(new FileOutputStream(new File(path)), Charset.forName("UTF-8"));
            writer.write(this.getHTMLRanking(rankingName, ranking));
            writer.close();
        }
    }

    private void extractFile(InputStream source, File dest) throws IOException {

        Path temp = Files.createTempFile("resource-", ".ext");
        Files.copy(source, temp, StandardCopyOption.REPLACE_EXISTING);
        FileInputStream sourceFile = new FileInputStream(temp.toFile());

        java.io.FileOutputStream destinationFile = null;

        destinationFile = new FileOutputStream(dest);

        // Lecture par segment de 0.5Mo
        byte buffer[] = new byte[512 * 1024];
        int nbLecture;

        while ((nbLecture = sourceFile.read(buffer)) != -1) {
            destinationFile.write(buffer, 0, nbLecture);
        }
        destinationFile.close();
        sourceFile.close();
    }

    public boolean generate(String path) {
        generateCategories();
        sortCategory();
        new File(path + "/" + course.getName()).mkdir();
        String completPath = path + File.separator.toString() + course.getName() + File.separator.toString();
        try {
            extractFile(getClass().getResourceAsStream("/be/bewweb/StopWatch/resources/bootstrap.min.css"), new File(completPath + "bootstrap.min.css"));
            generateFile(familyA, "Famille A", completPath + "familyA.html");
            generateFile(familyB, "Famille B", completPath + "familyb.html");
            generateFile(youngA, "Jeune A", completPath + "youngA.html");
            generateFile(youngB, "Jeune B", completPath + "youngB.html");
            generateFile(youngC, "Jeune C", completPath + "youngC.html");
            generateFile(adult, "Adulte", completPath + "adult.html");
            generateFile(young, "Jeune", completPath + "young-master.html");
            generateFile(senior, "Senior", completPath + "senior-master.html");
            generateFile(veteranA, "Vétéran A", completPath + "veteranA-master.html");
            generateFile(veteranB, "Vétéran B", completPath + "veteranB-master.html");
            generateFile(women, "Fille", completPath + "women-master.html");
            generateFile(mixed, "Mixte", completPath + "mixed-master.html");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
