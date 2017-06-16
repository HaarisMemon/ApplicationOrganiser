package com.haarismemon.applicationorganiser.database;

import android.util.Log;

import com.haarismemon.applicationorganiser.model.ApplicationStage;
import com.haarismemon.applicationorganiser.model.Internship;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to seed hardcoded data into the database for testing.
 */
public class SeedApplications {

    private static String file = "Accenture,Technology Industrial Placement,12 Months,London\\Online Application,true,false,true,16/11/2016,16/11/2016,22/11/2016\\Online Test,true,false,true,null,16/11/2016,22/11/2016\\Online Interview,true,false,true,22/11/2016,23/11/2016,12/12/2016\\Assessment Centre,true,false,false,12/01/2017,12/01/2017,02/02/2017\n" +
            "JP Morgan,Technology Placement,12 months,London\\Online Application,true,false,false,22/11/2016,22/11/2016,27/03/2017\n" +
            "Morgan Stanley,Technology Placement Application Developer,12 months,London\\Online Application,true,false,false,25/11/2016,25/11/2016,16/12/2016\n" +
            "Citi,Technology Placement Analyst,Year,London\\Online Application,true,false,true,26/11/2016,26/11/2016,29/11/2016\\Online Test,true,false,false,29/11/2016,30/11/2016,17/12/2016\n" +
            "Autodesk,Software Engineer Intern,Year,Birmingham\\Online Application,true,false,false,26/11/2016,26/11/2016,null\n" +
            "Google,Summer Software Engineering Intern,Summer,North America\\Online Application,true,false,false,26/11/2016,26/11/2016,null\n" +
            "Microsoft,Development Engineering Summer Internship,Year,Redmond USA\\Online Application,true,false,false,26/11/2016,26/11/2016,null\n" +
            "Bank of America,Global Technology Industrial Placement,Year,London\\Online Application,true,false,true,27/11/2016,22/11/2016,null\\Online Test,true,false,false,27/11/2016,27/11/2016,21/02/2017\n" +
            "Credit Suisse,Technology Analyst Industrial Year,Year,London\\Online Application,true,false,false,27/11/2016,27/11/2016,14/01/2017\n" +
            "Cognito iQ,Software Developer Industrial Placement,Year,Newbury\\Online Application,true,false,true,30/11/2016,30/11/2016,21/12/2016\\Assessment Centre,true,false,true,17/01/2017,17/01/2017,23/01/2017\n" +
            "Atkins,Technology Industrial Placement,Year,Aldershot Bristol,null,null,null\\Online Application,true,false,true,30/11/2016,30/11/2016,05/12/2016\\Online Test,true,false,false,30/11/2016,01/12/2016,05/12/2016\n" +
            "GE,Software Engineer Industrial Placement,Year,Livingston/Farnborough/Groby/Cheltenham\\Online Application,true,false,true,30/11/2016,30/11/2016,06/12/2016\\Online Test,true,false,true,02/11/2016,03/12/2016,06/12/2016\\Online Interview,true,false,true,06/12/2016,07/12/2016,13/12/2016\\Final Live Interview,true,false,true,22/02/2017,22/02/2017,22/02/2017\n" +
            "Viagogo,Intern Developer,Year,London\\Online Application,true,false,true,01/12/2016,01/12/2016,05/12/2016\\Telephone Interview,true,false,false,06/12/2016,07/12/2016,19/12/2016\n" +
            "Amazon,SDE video placement,11 Months,London\\Online Application,true,false,false,01/12/2016,01/12/2016,null\n" +
            "Amazon,SDE Placement,3-6 months,Edinburgh\\Online Application,true,false,false,01/12/2016,01/12/2016,15/12/2016\n" +
            "Amazon,SDE summer internship,Summer,London\\Online Application,true,false,false,01/12/2016,01/12/2016,null\n" +
            "Barclays,Tech Spring Insight Programme,Spring,London,null,null,null\\Online Application,true,false,true,01/12/2016,01/12/2016,09/12/2016\\Online Interview,true,false,true,01/12/2016,04/12/2016,09/12/2016\n" +
            "Goldman Sachs,Tech Spring Insight Programme,Spring,London\\Online Application,true,false,false,01/12/2016,01/12/2016,07/02/2017\n" +
            "Bloomberg,Software Engineering Industrial Placement,3-6 months,Various\\Online Application,true,false,true,01/12/2016,01/12/2016,20/12/2016\\Telephone Interview,true,false,false,05/01/2017,05/01/2017,16/01/2017\n" +
            "Google,Summer Trainee Engineering Program,Summer,London\\Online Application,true,false,false,01/12/2016,01/12/2016,null\n" +
            "Facebook,Software Engineer Intern/Co-op Summer,Summer,London\\Online Application,true,false,false,02/12/2016,02/12/2016,null\n" +
            "Fujitsu,Software Developer Industrial Placement,Year,Various + European Travel,null,null,null\\Online Application,true,false,true,02/12/2016,05/12/2016,null\\Online Test,true,false,false,06/12/2016,07/12/2016,07/12/2016\n" +
            "Rolls Royce,Software Engineering Internship Programme,Year,Various,null,null,null\\Online Application,true,false,false,02/12/2016,05/12/2016,09/12/2016\\Online Test,true,false,false,05/12/2016,07/12/2016,09/12/2016\n" +
            "CA Technologies,Software Engineering Placement,Year,Datchet England\\Email Application,true,false,false,05/12/2016,05/12/2016,20/01/2017\n" +
            "IBM,Software Engineer Placement,1 Year,London\\Online Application,true,false,true,28/01/2017,28/01/2017,20/02/2017\\Test,true,false,true,null,null,20/02/2017\\Assessment Centre,true,false,true,07/03/2017,07/03/2017,16/03/2017\n" +
            "BT,Software Engineering Placement,1 Year,London,null,null,null\\Online Application,true,false,false,22/12/2016,22/12/2016,22/12/2016\\Online Test,true,false,false,22/12/2016,22/12/2016,22/12/2016\n" +
            "MHR,Software Undergraduate Internship Scheme 2017,null,Nottingham\\Online Application,true,false,true,10/12/2016,10/12/2016,15/02/2017\\Assessment Centre,true,false,false,23/02/2017,null,null\n" +
            "Amadeus,Software Development Intern,1 Year,Heathrow\\Online Application,true,false,false,10/12/2016,10/12/2016,null\n" +
            "HP Enterprise,Software Development Placement Year,1 Year,Bristol/Cambridge/Bracknell/Newcastle\\Online Application,true,false,false,11/12/2016,11/12/2016,null\n" +
            "PA Consulting,Software Developer Industrial Placement,Year long,London\\Online Application,true,false,false,18/12/2016,18/12/2016,06/01/2017\n" +
            "Atos,Software Development Internship,12 month,London\\Online Application,true,false,true,18/12/2016,31/12/2016,03/01/2017\\Online Test,true,false,false,03/01/2017,03/01/2017,06/01/2017\n" +
            "G Research,Software Development Internship,Year Long,London\\Online Application,true,false,false,18/12/2016,18/12/2016,04/01/2017\n" +
            "Expedia (29/04),Software Development Industrial Placement,Year long,London\\Online Application,true,false,false,31/12/2016,31/12/2016,20/02/2017\n" +
            "ASOS,Software Engineering,12 Month,London\\Online Application,true,false,true,29/11/2016,29/11/2016,21/12/2016\\Telephone Interview,true,false,true,12/01/2017,12/01/2017,12/01/2017\\Assessment Centre,true,false,false,20/01/2017,20/01/2017,15/02/2017\n" +
            "Schroders,Tech Industrial Placement Year Programme 2017,Year long starting on 26th June,London\\Online Application,true,false,true,22/12/2016,22/12/2016,03/01/2017\\Programmer/Analyst Aptitude Test,true,false,true,03/01/2017,03/01/2017,03/01/2017\\Online Interview,true,false,true,03/01/2017,04/01/2017,09/01/2017\\Occupational Personality Questionnaire,true,false,true,09/01/2017,09/01/2017,24/01/2017\\Assessment Centre,true,false,false,31/01/2017,31/01/2017,07/02/2017\n" +
            "Carwow,Software Developer Intern,At Least 6 Months,London\\Online Application,true,false,true,31/12/2016,31/12/2016,10/01/2017\\Telephone Interview,true,false,false,16/01/2017,16/01/2017,18/01/2017\n" +
            "Softwire,Software Developer Placement,12 Months,London/Bristol\\Online Application,true,false,true,30/12/2016,30/12/2016,04/01/2017\\Aptitude Tests,true,false,true,03/01/2017,03/01/2017,04/01/2017\\Telephone Interview,true,false,false,13/01/2017,13/01/2017,16/01/2017\n" +
            "Total Gas & Power Ltd,Junior Software Developer,52 weeks starting 5th June 2017,Canary Wharf London\\Online Application,true,false,false,01/01/2017,01/01/2017,null\n" +
            "Intel,Software Engineering Intern 2017,12 month,Aylesbury UK\\Online Application,true,false,false,01/01/2017,01/01/2017,null\n" +
            "Firmstep,Developer Role,Year long,London\\eTrust Application,true,false,false,22/12/2016,22/12/2016,null\\Developer Test,true,false,false,24/01/2017,24/01/2017,null\n" +
            "Nomura,Corporate Infrastructure - Information Technology Industrial Placement ,Year,London\\Online Application,true,false,false,30/01/2017,30/01/2017,27/02/2017\\Numerical and Logic Test,true,false,false,30/01/2017,30/01/2017,27/02/2017\n" +
            "Thomson Reuters,Technology Placement,1 Year,Canary Wharf\\Online Application,true,false,true,01/05/2017,01/05/2017,11/05/2017\\Situational Strength Test,true,false,true,11/05/2017,12/05/2017,25/05/2017\\Technical Assessment,true,false,true,25/05/2017,29/05/2017,15/06/2017\\Assessment Centre,false,false,null,21/06/2017,null,null\n";


    public static List<Internship> parse() {
        List<Internship> applications = new ArrayList<>();

        String[] internships = file.split("\\n");

        for(String internshipString : internships) {
            Internship internship = parseInternship(internshipString);
            applications.add(internship);
        }

        return applications;
    }


    public static Internship parseInternship(String internshipLine) {
        String[] internshipStages = internshipLine.split("\\\\");

        String[] internshipDetails = internshipStages[0].split(",");

        String companyName = internshipDetails[0];
        String role = internshipDetails[1];
        String length = internshipDetails[2];
        String location = internshipDetails[3];

        Internship internship = new Internship();
        internship.setCompanyName(companyName);
        internship.setRole(role);
        if(!length.equals("null")) internship.setLength(length);
        if(!location.equals("null")) internship.setLocation(location);

        if(internshipStages.length > 1) {
            for(int i = 1; i < internshipStages.length; ++i) {
                String[] internshipStage = internshipStages[i].split(",");

                ApplicationStage stage = parseStage(internshipStage);


                if(stage != null) {
                    internship.addStage(stage);
                }

            }
        }

        return internship;
    }

    public static ApplicationStage parseStage(String[] stageArray) {
        String stage_name = stageArray[0];
        String is_completed = stageArray[1];
        String is_waiting_for_response = stageArray[2];
        String is_successful = stageArray[3];
        String date_of_start = stageArray[4];
        String date_of_completed = stageArray[5];
        String date_of_reply = stageArray[6];

        ApplicationStage stage = new ApplicationStage();

        stage.setStageName(stage_name);

        if(is_completed.equals("true")) {
            if(!date_of_completed.equals("null"))  {
                stage.setCompleted(true);
                stage.setDateOfCompletion(date_of_completed);
            }
            else stage.setCompleted(true);
        }
        else if(is_completed.equals("false")) stage.setCompleted(false);


        if(is_waiting_for_response.equals("true")) {
            stage.setWaitingForResponse(true);
        }
        else if(is_waiting_for_response.equals("false")) stage.setWaitingForResponse(false);


        if(is_successful.equals("true")) {
            if(!date_of_reply.equals("null")) {
                stage.setSuccessful(true);
                stage.setDateOfReply(date_of_reply);
            }
            else stage.setSuccessful(true);
        }
        else if(is_successful.equals("false")) {
            stage.setSuccessful(false);
            stage.setDateOfReply(date_of_reply);
        }

        if(!date_of_start.equals("null")) stage.setDateOfStart(date_of_start);

        return stage;
    }

}
