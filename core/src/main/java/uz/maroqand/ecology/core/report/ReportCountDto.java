package uz.maroqand.ecology.core.report;

import lombok.Data;

import java.util.HashMap;

/**
 * Created by Utkirbek Boltaev on 22.08.2019.
 * (uz)
 * (ru)
 */
@Data
public class ReportCountDto {

    private HashMap<Integer, HashMap<Integer, Integer>> appealTaskHashMap;
    private HashMap<Integer, HashMap<Integer, Integer>> appealHashMap;
    private HashMap<Integer, Integer> appealTaskTotal;
    private HashMap<Integer, Integer> appealTotal;

    public ReportCountDto(
            HashMap<Integer, HashMap<Integer, Integer>> appealTaskHashMap,
            HashMap<Integer, HashMap<Integer, Integer>> appealHashMap,
            HashMap<Integer, Integer> appealTaskTotal,
            HashMap<Integer, Integer> appealTotal
    ){
        this.appealTaskHashMap = appealTaskHashMap;
        this.appealHashMap = appealHashMap;
        this.appealTaskTotal = appealTaskTotal;
        this.appealTotal = appealTotal;
    }

    private HashMap<Integer,HashMap<Integer, Integer>> problemAppealHashMap;
    private HashMap<Integer,HashMap<Integer, Integer>> problemTaskAppealHashMap;

    public ReportCountDto(
            HashMap<Integer, HashMap<Integer, Integer>> appealTaskHashMap,
            HashMap<Integer, HashMap<Integer, Integer>> appealHashMap,
            HashMap<Integer, Integer> appealTaskTotal,
            HashMap<Integer, Integer> appealTotal,
            HashMap<Integer,HashMap<Integer, Integer>> problemAppealHashMap,
            HashMap<Integer,HashMap<Integer, Integer>> problemTaskAppealHashMap
    ){
        this.appealTaskHashMap = appealTaskHashMap;
        this.appealHashMap = appealHashMap;
        this.appealTaskTotal = appealTaskTotal;
        this.appealTotal = appealTotal;

        this.problemAppealHashMap = problemAppealHashMap;
        this.problemTaskAppealHashMap = problemTaskAppealHashMap;
    }

}
