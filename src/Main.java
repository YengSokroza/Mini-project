import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    static Scanner input = new Scanner(System.in);
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String RESET = "\u001B[0m";

    private static int rows;
    private static int columns;
    private static String[] historySeats;
    private static int[] historyStudentIds;
    private static String[] historyDates;
    private static String[] historyHall;
    private static int historyIndex = 0;

    //System start method
    public static void start(){
        gTable(true,"Hall Booking System");
        rows = Integer.parseInt(isValidNum("-> Config total rows in hall : ")) ;
        columns = Integer.parseInt(isValidNum("-> Config total columns in hall : ")) ;

        historySeats = new String[rows * columns * 3];
        historyStudentIds = new int[rows * columns * 3];
        historyDates = new String[rows * columns * 3];
        historyHall = new String[rows * columns * 3];

        menu();
    }

    //Menu
    public static void menu(){
         String[][] hallASeat = new String[rows][columns];
         String[][] hallBSeat = new String[rows][columns];
         String[][] hallCSeat = new String[rows][columns];

        initializeHallSeats(hallASeat,rows,columns);
        initializeHallSeats(hallBSeat,rows,columns);
        initializeHallSeats(hallCSeat,rows,columns);

        boolean isContinue = true;
        do {
            MenuTable();
            String opt = isValidChar("-> Please select menu no: ");

            switch (opt) {
                case "a" -> {
                    Table(true,"DAILY SHOWTIME OF CSTAD HALL","A.) Morning (10:00am - 12.30pm)","B.) Afternoon (3:00pm - 5.30pm)","C.) Evening (7:00pm - 9.30pm)");
                    bookingHall(hallASeat,hallBSeat,hallCSeat);
                }
                case "b" -> displayHall(hallASeat,hallBSeat,hallCSeat,false);
                case "c" -> Table(true,"DAILY SHOWTIME OF CSTAD HALL","A.) Morning (10:00am - 12.30pm)","B.) Afternoon (3:00pm - 5.30pm)","C.) Evening (7:00pm - 9.30pm)");
                case "d" -> {
                    initializeHallSeats(hallASeat,rows,columns);
                    initializeHallSeats(hallBSeat,rows,columns);
                    initializeHallSeats(hallCSeat,rows,columns);
                    historyIndex = 0;
                    gTable(true,"Start Rebooting hall...", GREEN + "Reboot successfully");
                }
                case "e" -> displayHistory();
                case "f" -> {
                    gTable(false,"THANK YOU FOR USING OUR SERVICE!","Exiting...");
                    isContinue = false;
                }
                default -> System.out.println(RED + "Invalid Option" + RESET);
            }

        }while(isContinue);

    }
    //Feature
    public static void bookingHall(String[][] hallASeat,String[][] hallBSeat,String[][] hallCSeat){
        displayHall(hallASeat,hallBSeat,hallCSeat,true);
    }

    public static void bookingSeat(String[][] hallSeats,String hallName) {
        Table(false,"INSTRUCTION","Single: C-1","Multiple (separate by comma): C-1,B-2");
        String inputSeatsString = validateInput("-> Please select available seats: ","^[A-Za-z]-\\d+([,][A-Za-z]-\\d+)*$").toUpperCase();
        String[] inputSeatsArray = inputSeatsString.split(",");

        for (int i = 0; i < inputSeatsArray.length; i++) {
            inputSeatsArray[i] = inputSeatsArray[i].trim();
        }

        int studId = Integer.parseInt(isValidNum("-> Please enter student ID: "));
        String opt = validateInput(BLUE + "# Are you sure to book? (Y/N) : " + RESET, "^[yYnN]$").toLowerCase();
        boolean allSeatsFound = true;
        if (opt.equals("y")) {
            for (String inputSeat : inputSeatsArray) {
                boolean seatFound = false;

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        if (hallSeats[i][j].equals("|" + inputSeat + "::AV|")) {
                            hallSeats[i][j] = "|" + inputSeat + "::BO|";
                            seatFound = true;
                            break;
                        }
                    }
                    if (seatFound) {
                        break;
                    }
                }
                allSeatsFound &= seatFound;

                if (!seatFound) {
                    if(!isValidSeatRange(inputSeatsString)){
                        gTable(false,inputSeat + " out of range!");
                    }else{
                        gTable(false,inputSeat + " is already booked!", "[ " +  inputSeat + " ] cannot be booked because of unavailability!");
                    }
                }
            }
            if(allSeatsFound){
                if(hallName.equals("a")){
                    hallName = "Hall A";
                }else if(hallName.equals("b")){
                    hallName = "Hall B";
                }else{
                    hallName = "Hall C";
                }
                gTable( false,inputSeatsString + " booked Successfully");
                addToHistory(inputSeatsArray, studId, new Date(),hallName);
            }

        } else {
            menu();
        }
    }

    public static void addToHistory(String[] seats, int studId, Date date,String hallName) {
        if (historyIndex < historySeats.length) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = dateFormat.format(date);

            historySeats[historyIndex] = Arrays.toString(seats);
            historyStudentIds[historyIndex] = studId;
            historyDates[historyIndex] = dateString;
            historyHall[historyIndex] = hallName;

            historyIndex++;
        } else {
            System.out.println("Booking history is full. Cannot add more entries.");
        }

    }

    public static void displayHistory() {
        if(historyIndex == 0){
            gTable(true,"There is no history!");
        }
        for (int i = 0; i < historyIndex; i++) {
            String seatsString = historySeats[i].substring(1, historySeats[i].length() - 1);
            System.out.println("-+".repeat(25));
            System.out.println("NO: " + (i + 1) + "\n" + "SEATS: " + seatsString + "\n" + "HALL\t\tSTUD.ID\t\tCREATED AT\n"  + historyHall[i] + "\t\t" + historyStudentIds[i] + "\t\t\t" + historyDates[i]);
            System.out.println("-+".repeat(25));
        }
    }

    public static void displayHall(String[][] hallASeat,String[][] hallBSeat,String[][] hallCSeat,boolean selected){
        if(selected){
            String inputUser = isValidChar("-> Please select show time (A | B | C): ");
            switch (inputUser) {
                case "a" -> {
                    gTable(true,"Hall A - Morning");
                    displaySeats(hallASeat);
                    bookingSeat(hallASeat, inputUser);
                }
                case "b" -> {
                    gTable(true,"Hall B - Afternoon");
                    displaySeats(hallBSeat);
                    bookingSeat(hallBSeat, inputUser);
                }
                case "c" -> {
                    gTable(true,"Hall C - Evening");
                    displaySeats(hallCSeat);
                    bookingSeat(hallCSeat, inputUser);
                }
                default ->
                        System.out.println(RED + "[Invalid Input] Please select available choices :  A | B | C !" + RESET);
            }
        }else{
            gTable(true,"Hall A - Morning");
            displaySeats(hallASeat);
            gTable(true,"Hall B - Afternoon");
            displaySeats(hallBSeat);
            gTable(true,"Hall C - Evening");
            displaySeats(hallCSeat);
        }
    }

    private static void initializeHallSeats(String[][] hallSeats,int rows,int columns) {
        for (int i = 0; i < rows; i++) {
            char ch = (char) ('A' + i);
            for (int j = 0; j < columns; j++) {
                hallSeats[i][j] = "|" + ch + "-" + (j + 1) + "::AV|";
            }
        }
    }

    private static void displaySeats(String[][] hallSeats) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print(hallSeats[i][j] + " ");
            }
            System.out.println();
        }
    }

    //Validation
    public static boolean isValidSeatRange(String inputSeatsString) {
        String[] seatsArray = inputSeatsString.split(",");
        for (String seat : seatsArray) {
            String[] seatParts = seat.split("-");
            int row = Integer.parseInt(String.valueOf(seatParts[0].toUpperCase().charAt(0) - 'A')) + 1;
            int col = Integer.parseInt(seatParts[1]);

            if (row < 1 || row > rows || col < 1 || col > columns) {
                return false; // Seat is out of range
            }
        }
        return true; // All seats are within range
    }
    private static String validateInput(String message, String regex){
        while (true){
            System.out.print(message);
            String userInput = input.nextLine();

            Pattern pattern = Pattern.compile(regex);
            if(pattern.matcher(userInput).matches()){
                return userInput;
            }else {
                System.out.println(RED + "Invalid Format!" + RESET);
            }
        }
    }
    private static String isValidNum(String message){
        String regex = "^[1-9]\\d*$";
        String regexNegative = "^-[1-9]\\d*$";
        String regexZero = "^0$";

        while (true){
            System.out.print(message);
            String userInput = input.nextLine();

            if(Pattern.compile(regex).matcher(userInput).matches()){
                return userInput;
            }else if(Pattern.compile(regexNegative).matcher(userInput).matches()){

                System.out.println(RED + "[Invalid Format]:  Negative number not allowed! " + RESET);
            }else if(Pattern.compile(regexZero).matcher(userInput).matches()){
                System.out.println(RED + "[Invalid Format]: Zero not allowed!" + RESET);
            }else{

                System.out.println(RED + "[Invalid Format]: Character not allowed!" + RESET);
            }
        }


    }

    private static String isValidChar(String message){
        String regex = "[a-zA-Z]";
        String regexInteger = "^[1-9]\\d*$";
        String regexNegative = "^-[1-9]\\d*$";
        String regexZero = "^0$";

        while (true){
            System.out.print(message);
            String userInput = input.nextLine();

            if(Pattern.compile(regex).matcher(userInput).matches()){
                return userInput.toLowerCase();
            }else if(Pattern.compile(regexInteger).matcher(userInput).matches()){
                System.out.println(RED + "[Invalid Format]: number not allowed! Please enter a valid character." + RESET);
            } else if(Pattern.compile(regexNegative).matcher(userInput).matches()){
                System.out.println(RED + "[Invalid Format]: Negative number not allowed! Please enter a valid character." + RESET);
            }else if(Pattern.compile(regexZero).matcher(userInput).matches()){
                System.out.println(RED + "[Invalid Format]: Zero not allowed! Please enter a valid character." + RESET);
            }else{
                System.out.println(RED + "[Invalid Format]: Character not allowed! Please enter a valid character." + RESET);
            }
        }


    }

    //Table
    public static void gTable(boolean blue,String... messages){
        Table systemNameT = new Table(1, BorderStyle.CLASSIC_WIDE, ShownBorders.SURROUND);

        if(blue){
            for(String m : messages){
                systemNameT.setColumnWidth(0,50,60);
                systemNameT.addCell(" ".repeat(15)+ m);
            }
            System.out.println(BLUE + systemNameT.render() + RESET);
        }else{
            for(String m : messages){
                systemNameT.setColumnWidth(0,50,60);
                systemNameT.addCell(" " + m);
            }
            System.out.println(GREEN + systemNameT.render() + RESET);
        }

    }

    public static void MenuTable(){
        Table menuT = new Table(1, BorderStyle.UNICODE_ROUND_BOX, ShownBorders.SURROUND_HEADER_AND_COLUMNS);
        menuT.setColumnWidth(0,50,60);
        menuT.addCell(" ".repeat(18) + "Application Menu");
        menuT.addCell(" A.) Booking");
        menuT.addCell(" B.) Hall");
        menuT.addCell(" C.) Showtime");
        menuT.addCell(" D.) Rebooting Showtime");
        menuT.addCell(" E.) History");
        menuT.addCell(" F.) Exit");
        System.out.println(BLUE + menuT.render() + RESET);
    }

    public static void Table(boolean green,String header,String... lists){
        Table showT = new Table(1, BorderStyle.UNICODE_ROUND_BOX, ShownBorders.SURROUND_HEADER_AND_COLUMNS);
        showT.setColumnWidth(0,50,60);
        showT.addCell(" " + header);
        for (String m : lists){
            showT.addCell(" " + m);
        }
        if(green){
            System.out.println(GREEN + showT.render() + RESET);
        }else{
            //instruction table
            System.out.println(YELLOW + showT.render() + RESET);
        }

    }

    public static void main(String[] args) {
        start();
    }
}
