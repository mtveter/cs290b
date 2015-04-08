package tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Permute {

    public static List<List<Integer>> permute(Integer...myInts){

        if(myInts.length==1){
            List<Integer> arrayList = new ArrayList<Integer>();
            arrayList.add(myInts[0]);
            List<List<Integer> > listOfList = new ArrayList<List<Integer>>();
            listOfList.add(arrayList);
            return listOfList;
        }

        Set<Integer> setOf = new HashSet<Integer>(Arrays.asList(myInts));   

        List<List<Integer>> listOfLists = new ArrayList<List<Integer>>();

        for(Integer i: myInts){
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            arrayList.add(i);

            Set<Integer> setOfCopied = new HashSet<Integer>();
            setOfCopied.addAll(setOf);
            setOfCopied.remove(i);

            Integer[] isttt = new Integer[setOfCopied.size()];
            setOfCopied.toArray(isttt);

            List<List<Integer>> permute = permute(isttt);
            Iterator<List<Integer>> iterator = permute.iterator();
            while (iterator.hasNext()) {
                List<java.lang.Integer> list = iterator.next();
                list.add(i);
                listOfLists.add(list);
            }
        }   

        return listOfLists;
    }

//    public static void main(String[] args) {
//        List<List<Integer>> permute = permute(1,2,3,4);
//        System.out.println(permute);
//    }

}
