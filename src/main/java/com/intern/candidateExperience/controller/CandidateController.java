package com.intern.candidateExperience.controller;

import com.intern.candidateExperience.dao.CandidateDao;
import com.intern.candidateExperience.dao.QuestionDataDao;
import com.intern.candidateExperience.model.Candidate;
import com.intern.candidateExperience.model.DateAdded;
import com.intern.candidateExperience.model.Question;
import com.intern.candidateExperience.model.QuestionData;
import com.intern.candidateExperience.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/feedback")
//@CrossOrigin(origins = "http://localhost:4200")
public class CandidateController {

    String input1 = "2017-01-01" ;
    String input2 = "2018-12-31" ;

    DateTimeFormatter f = DateTimeFormatter.ofPattern( "yyyy-MM-dd" ) ;

    LocalDate ld1 = LocalDate.parse( input1 , f ) ;
    LocalDate ld2 = LocalDate.parse( input2 , f ) ;

    @Autowired
    private CandidateService candidateService;

    @Autowired
    private CandidateDao candidateDao;

    @Autowired
    private QuestionDataDao questionDataDao;

    @GetMapping("/all")
    public List<Candidate> getAllCandidates(){
        return candidateService.getAllCandidates();
    }


    @PostMapping(produces= MediaType.APPLICATION_JSON_VALUE)
    public Candidate insert(@RequestBody Candidate candidate){
        LocalDate localDate = LocalDate.parse( candidate.getDateString() , f ) ;

        candidate.setLocalDate(localDate);
        DateAdded dateAdded =  new DateAdded(localDate.getDayOfMonth(),localDate.getMonthValue(),localDate.getYear());
        candidate.setDate(dateAdded);

       candidateService.save(candidate);
       return candidate;
    }

    @GetMapping("/range")
    public HashMap<Integer,Double> findByRange(){
        refresh();
        List<Candidate> candidatesFilter = candidateDao.findBetweenRangeOfDate(ld1,ld2);
        HashMap<Integer,Double> answer = avgAnalysis(candidatesFilter);
        return answer;
    }

    @GetMapping("/yearly")
    public HashMap<Integer,HashMap<Integer,Double>>getByYear(){
        refresh();
        HashMap<Integer,HashMap<Integer,Double>> yearAnalysis = new  HashMap<Integer,HashMap<Integer,Double>>();
        for(int i=2017;i<=2019;i++){
            List<Candidate> candidatesFilterByYear = candidateDao.findByYear(i);
            //this.candidatesMain = inSync(candidatesFilterByYear);
            HashMap<Integer,Double> questionAverage  = avgAnalysis(candidatesFilterByYear);
            yearAnalysis.put(i,questionAverage);
        }
//        List<Candidate> candidatesFilterByYear = candidateDao.findByYear(2018);
//        HashMap<Integer,Double> questionAverage  = avgAnalysis(candidatesFilterByYear);
        return yearAnalysis;

    }

    @GetMapping("/monthly")
    public HashMap<Integer,HashMap<Integer,Double>>getByMonth(){
        refresh();
        HashMap<Integer,HashMap<Integer,Double>> monthAnalysis = new  HashMap<Integer,HashMap<Integer,Double>>();
        for(int i=1;i<=12;i++){
            List<Candidate> candidatesFilterByMonth = candidateDao.findByMonth(i);
            HashMap<Integer,Double> questionAverage  = avgAnalysis(candidatesFilterByMonth);
            monthAnalysis.put(i,questionAverage);
        }

        return monthAnalysis;
    }

    public HashMap<Integer,Double> avgAnalysis(List<Candidate> candidates){

//        List<Candidate> candidates = inSync(candidates1);
        HashMap<Integer, Double> questionAverage = new HashMap<>();

        //Candidate[] candidateArray = candidates.toArray(new Candidate[candidates.size()]);

        Candidate[] candidateArray = new Candidate[candidates.size()];

        for(int k=0; k<candidates.size(); k++){
            candidateArray[k] = candidates.get(k);
        }
        //candidateArray = (Candidate[])candidates.toArray();

        //Length of Filtered Candidate List based on DateAdded range
        int len = candidateArray.length;

        //Number of questions in the DB
        int qcount = (int)questionDataDao.count();

        long sum = 0;
        double avg = 0.0;

        //Number of people attempted the question
        long countC = 0;


        for(int j=0; j<qcount; j++){
            for(int i=0; i<len; i++){
                try{
                    int val = candidateArray[i].getQuestionsAttempted().get(j).getQuestionRating();
                    sum = sum + val;
                    if(val>0){
                        countC++;
                    }
                }catch (ArrayIndexOutOfBoundsException e){
                    e.printStackTrace();
                }
            }
            if(countC>0){
                avg = ((double)sum/countC);
            }
            questionAverage.put(j+1,avg);
            sum = 0;
            countC = 0;
            avg = 0.0;

        }

        return questionAverage;
    }

    private List<Candidate> inSync(List<Candidate> candidates) {
        long currentCount = questionDataDao.count();
        for(int i=0; i<candidates.size(); i++){
            Candidate candidate = candidates.get(i);
            long count = candidate.getQuestionsAttempted().size();

            for(long j=count; j<currentCount;j++){
                if(j>=count) {
                    Question sampleQuestion = new Question((int)(j+1),"",0,"");
                    candidate.getQuestionsAttempted().add(sampleQuestion);
                    candidateService.save(candidate);
                }

            }
        }
        return candidateService.getAllCandidates();
    }

    public void refresh(){
        inSync(candidateService.getAllCandidates());
    }
}