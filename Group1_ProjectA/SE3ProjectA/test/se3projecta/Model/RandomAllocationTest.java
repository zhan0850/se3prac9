/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package se3projecta.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import se3projecta.GUI.Allocation;
import se3projecta.Persistence.ImportException;
import se3projecta.Repository;

/**
 * Test Suite for the function of randomly allocating seats. 
 * Consists of:<br>
 * 1. Test for seat type of allocated seats (63 test cases)<br>
 * 2. Test for continguousness of allocations within each row (63 test cases)<br>
 * 3. Test for non-existance of isolated seat allocation ()<br>
 * 
 * @author zhan0850
 */
public class RandomAllocationTest {
    
    static Repository repo; //for reading data from xml files
    static SeatType bronze, silver, gold;
    static ArrayList<TheatreSession> sessions;
    
    public RandomAllocationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        System.out.println("Random Allocation of Seats Test Suite");
        
        try {
            repo = new Repository();
        } catch (ImportException ex) {
            Logger.getLogger(RandomAllocationTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        ArrayList<SeatType> seatTypes = new ArrayList();
        seatTypes.addAll(repo.getSeatTypes());
        bronze = seatTypes.get(0);
        silver = seatTypes.get(2);
        gold = seatTypes.get(1);
        
        ArrayList<Movie> movies = new ArrayList();
        movies.addAll(repo.getMovies());
        
        ArrayList<SessionTime> times = new ArrayList();
        times.addAll(repo.getSessionTimes());
        
        sessions = new ArrayList();
        for(int i = 0; i < movies.size(); i++)
            for(int j = 0; j < times.size(); j++)
                sessions.addAll(repo.getTheatreSessions(movies.get(i), times.get(j)));
        
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        System.out.println();
    }
    
    @After
    public void tearDown() {
        for(TheatreSession session : sessions) 
            for(Seat seat : session.getSeats())
                if(seat.getState().equals(SeatState.Held))  //a seat state becomes "Held" if it is allocated by placeRandom()
                    seat.setState(SeatState.Empty);
        
    }


    /**
     * Tests of SeatType after placeRandom() method of class 
     * TheatreSession is invoked. A test is passed if and only if the type 
     * of all seats returned by the subject method is the requested seat type.
     */
    @Test
    public void testSeatType() {
        System.out.println("Test Seat Type");
        
        //test cases: bronze
        for(int sessionNo = 0; sessionNo < sessions.size(); sessionNo++) {
            int noOfSeats = (int)(Math.random() * sessions.get(sessionNo).getSeats(bronze, SeatState.Empty).length);    //random request
            
            testSeatType(sessionNo, bronze, noOfSeats); //test case
        }
        System.out.println();
        
        //test cases: silver
        for(int sessionNo = 0; sessionNo < sessions.size(); sessionNo++) {
            int noOfSeats = (int)(Math.random() * sessions.get(sessionNo).getSeats(silver, SeatState.Empty).length);
            
            testSeatType(sessionNo, silver, noOfSeats);
        }
        System.out.println();
        
        //test cases: gold
        for(int sessionNo = 0; sessionNo < sessions.size(); sessionNo++) {
            int noOfSeats = (int)(Math.random() * sessions.get(sessionNo).getSeats(gold, SeatState.Empty).length);
            
            testSeatType(sessionNo, gold, noOfSeats);
        }
        
        System.out.println("End Test Seat Type\n");
    }
    
    public void testSeatType(int sessionNo, SeatType type, int noOfSeats) {
        System.out.println("\tTest Case: session number " + sessionNo + ", seat type " + type + ", number of seats " + noOfSeats);
        
        TheatreSession instance = sessions.get(sessionNo);  //a particular session 
        
        Seat[] allocations = instance.placeRandom(type, noOfSeats);    //do random allocation
        
        boolean expResult = true;
        boolean result = true;
        for(Seat seat : allocations)
            if(! seat.getType().equals(type)) { //if any allocated seat has the wrong seat type, then fail the test
                result = false;
                break;
            }
        
        assertEquals(expResult, result);
    }
    
    /**
     * Tests of the contiguousness of seat allocation within all rows 
     * after placeRandom() method of class TheatreSession is invoked. 
     * A test is passed if and only if the allocated seats in the same row are 
     * contiguous when they should be. Seats should be allocated contiguously 
     * when:<br>
     * 1. they are of the same type<br>
     * 2. and they are allocated to the same row<br>
     * 3. and there exist enough contiguous empty seats of the requested type
     */
    @Test
    public void testContiguousAllocation() {
        System.out.println("Test Contiguous Allocation");
        
        //test cases: bronze
        for(int sessionNo = 0; sessionNo < sessions.size(); sessionNo++) {
            int noOfSeats = (int)(Math.random() * sessions.get(sessionNo).getSeats(bronze, SeatState.Empty).length);
            
            testContiguousAllocation(sessionNo, bronze, noOfSeats);
        }
        System.out.println();
        
        //test cases: silver
        for(int sessionNo = 0; sessionNo < sessions.size(); sessionNo++) {
            int noOfSeats = (int)(Math.random() * sessions.get(sessionNo).getSeats(silver, SeatState.Empty).length);
            
            testContiguousAllocation(sessionNo, silver, noOfSeats);
        }
        System.out.println();
        
        //test cases: gold
        for(int sessionNo = 0; sessionNo < sessions.size(); sessionNo++) {
            int noOfSeats = (int)(Math.random() * sessions.get(sessionNo).getSeats(gold, SeatState.Empty).length);
            
            testContiguousAllocation(sessionNo, gold, noOfSeats);
        }
        
        System.out.println("End Test Contiguous Allocation\n");
    }
    
    public void testContiguousAllocation(int sessionNo, SeatType type, int noOfSeats) {
        System.out.println("\tTest Case: session number " + sessionNo + ", seat type " + type + ", number of seats " + noOfSeats);
        
        TheatreSession instance = sessions.get(sessionNo);  //a particular session 
        
        Seat[] allocations = instance.placeRandom(type, noOfSeats);    //do random allocation
        
        Seat[][] seats = instance.getSeatRows();    //all seats in the theatre
        int[][] coordinates = new int[allocations.length][2];
        
        //use brute force to find out the coordinates of allocated seats since 
        //the source code doesn't have a method to get seat coordinate
        for(int i = 0; i < seats.length; i++)   
            for(int j = 0; j < seats[i].length; j++)
                for(int k = 0; k < allocations.length; k++)
                    if(seats[i][j].equals(allocations[k])) {
                        coordinates[k][0] = i;
                        coordinates[k][1] = j;
                        break;
                    }
        
        
        boolean expResult = true;
        boolean result = true;
        //the standard here is to see if there is any seat in the middle that is of the same type and is empty but is not allocated to the current request
        for(int left = 0, right = left; right < coordinates.length-1; right++) {
            if(coordinates[right][0] != coordinates[right+1][0]) 
                for(int i = left; i < right; i++)
                    if(coordinates[i][1]+1 != coordinates[i+1][1]) {
                        Seat[] missed = Arrays.copyOfRange(seats[coordinates[i][0]], coordinates[i][1]+1, coordinates[i+1][1]);
                        
                        for(Seat s : missed)
                            if(s.getType().equals(type) && s.getState().equals(SeatState.Empty)) {
                                result = false;
                                break;
                            }
                        
                        if(!result)
                            break;
                    }
            
            if(!result)
                break;
            
            left = right + 1;
        }
        
        assertEquals(expResult, result);
    }
    
    /**
     * Tests of the non-existance of an isolated seat allocation after 
     * placeRandom() method of class TheatreSession is invoked. A test 
     * is passed if and only if for any allocated seat for a request of at least 
     * two seats, there exists at least one seat next to it also allocated to 
     * the same request, unless for all seats next to it, these seats are either 
     * of other type or occupied. 
     */
    @Test
    public void testNonIsolation() {
        System.out.println("Test Non Isolation");
        
        //test cases: bronze 
        for(int noOfSeats = 2; noOfSeats <= sessions.get(0).getSeats(bronze, SeatState.Empty).length; noOfSeats++) {
//            for(int noOfSeats = 2; noOfSeats < 10; noOfSeats++) {
            testNonIsolation(0, bronze, noOfSeats);
            tearDown(); //because a session is used for many times
        }
        System.out.println();
        
        //test cases: silver
        for(int noOfSeats = 2; noOfSeats <= sessions.get(0).getSeats(silver, SeatState.Empty).length; noOfSeats++) {
//            for(int noOfSeats = 2; noOfSeats < 10; noOfSeats++) {
            testNonIsolation(0, silver, noOfSeats);
            tearDown();
        }
        System.out.println();
        
        //test cases: gold
        for(int noOfSeats = 2; noOfSeats <= sessions.get(0).getSeats(gold, SeatState.Empty).length; noOfSeats++) {
//            for(int noOfSeats = 2; noOfSeats < 10; noOfSeats++) {
            testNonIsolation(0, gold, noOfSeats);
            tearDown();
        }
        
        System.out.println("End Test Non Isolation\n");
    }
    
    public void testNonIsolation(int sessionNo, SeatType type, int noOfSeats) {
        System.out.println("\tTest Case: session number " + sessionNo + ", seat type " + type + ", number of seats " + noOfSeats);
        
        TheatreSession instance = sessions.get(sessionNo);  //a particular session 
         
        Seat[] allocations = instance.placeRandom(type, noOfSeats);    //do random allocation
        
        Seat[][] seats = instance.getSeatRows();    //all seats in the theatre
        boolean[][] allocationMap = new boolean[seats.length][seats[0].length];
        
        for(int i = 0; i < seats.length; i++)   //brute force again
            for(int j = 0; j < seats[i].length; j++)
                for (Seat allocation : allocations)
                    if (seats[i][j].equals(allocation)) {
                        allocationMap[i][j] = true;
    //                        System.out.println(i + " " + j);
                        break;
                    }
            
        
        boolean expResult = true;
        boolean result = true;
        for(int i = 0; i < allocationMap.length; i++) {
            for(int j = 0; j < allocationMap[i].length; j++)
                if(allocationMap[i][j]) {
                    
                    //only left seat is of same type and empty
                    if(j == allocationMap[i].length-1 || seats[i][j+1].getType() != type || seats[i][j+1].getState().equals(SeatState.Occupied)) {
                        if(j > 0 && seats[i][j-1].getType().equals(type) && !seats[i][j-1].getState().equals(SeatState.Occupied) && !allocationMap[i][j-1]) {
                            result = false;
                            System.out.println("\tFAILED! Type A fault. Isolated allocateion at: " + i + " " + j);
                        }
                            
                    }
                    //only right seat is of same type and empty
                    else if(j == 0 || seats[i][j-1].getType() != type || seats[i][j-1].getState().equals(SeatState.Occupied)) {
                        if(!allocationMap[i][j+1]) {
                            result = false;
                            System.out.println("\tFAILED! Type B fault. Isolated allocateion at: " + i + " " + j);
                        }
                            
                    }
                    //both sides are of same type and empty
                    else if(! (allocationMap[i][j-1] || allocationMap[i][j+1])) {
                        result = false;
                        System.out.println("\tFAILED! Type C fault. Isolated allocateion at: " + i + " " + j);
                    }
                        
                        
                    
                    if(!result)
                        break;
                }
            if (!result) 
                break;
        }
        
        assertEquals(expResult, result);
    }

}
