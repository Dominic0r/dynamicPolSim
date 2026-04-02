import java.util.*;
public class Main
{
    public static String genName(){
        String[] maleFirstNames = {
    "Aaron", "Adam", "Adrian", "Alan", "Albert", "Alexander", "Andrew", "Anthony", "Arthur", "Austin",
    "Benjamin", "Bernard", "Blake", "Bradley", "Brandon", "Brian", "Bruce", "Bryan", "Caleb", "Cameron",
    "Carl", "Chad", "Charles", "Christopher", "Clarence", "Clark", "Clayton", "Clifford", "Cody", "Colin",
    "Connor", "Corey", "Craig", "Curtis", "Dale", "Daniel", "Danny", "Darrell", "David", "Dean",
    "Dennis", "Derek", "Donald", "Douglas", "Dustin", "Earl", "Edward", "Edwin", "Eric", "Ernest",
    "Ethan", "Eugene", "Evan", "Francis", "Frank", "Franklin", "Frederick", "Gabriel", "Gary", "George",
    "Gerald", "Gilbert", "Glen", "Gordon", "Gregory", "Harold", "Harry", "Harvey", "Henry", "Herbert",
    "Howard", "Hunter", "Ian", "Isaac", "Jack", "Jacob", "James", "Jason", "Jeffrey", "Jeremy",
    "Jerome", "Jesse", "Joel", "John", "Jonathan", "Jordan", "Joseph", "Joshua", "Justin", "Keith",
    "Kenneth", "Kevin", "Kyle", "Lawrence", "Leonard", "Lewis", "Logan", "Louis", "Lucas", "Luke",
    "Mark", "Martin", "Matthew", "Maurice", "Maxwell", "Michael", "Mitchell", "Nathan", "Nicholas", "Noah",
    "Norman", "Oscar", "Owen", "Patrick", "Paul", "Peter", "Philip", "Ralph", "Raymond", "Richard",
    "Robert", "Roger", "Ronald", "Ross", "Roy", "Russell", "Ryan", "Samuel", "Scott", "Sean",
    "Seth", "Shane", "Shawn", "Stanley", "Stephen", "Steven", "Terrence", "Thomas", "Timothy", "Todd",
    "Travis", "Tyler", "Victor", "Vincent", "Walter", "Warren", "Wayne", "Wesley", "William", "Zachary"
};
String[] femaleFirstNames = {
    "Abigail", "Alice", "Alicia", "Allison", "Amanda", "Amber", "Amy", "Andrea", "Angela", "Ann",
    "Anna", "Anne", "Annette", "April", "Ashley", "Audrey", "Barbara", "Beatrice", "Beverly", "Bonnie",
    "Brenda", "Brittany", "Brooke", "Candice", "Carla", "Carol", "Caroline", "Carolyn", "Catherine", "Cheryl",
    "Christina", "Christine", "Claire", "Clara", "Colleen", "Connie", "Courtney", "Crystal", "Cynthia", "Daisy",
    "Dana", "Danielle", "Dawn", "Deborah", "Debra", "Denise", "Diana", "Diane", "Donna", "Doris",
    "Dorothy", "Edith", "Edna", "Eileen", "Elaine", "Eleanor", "Elizabeth", "Ellen", "Emily", "Emma",
    "Erica", "Erin", "Esther", "Ethel", "Eva", "Evelyn", "Florence", "Frances", "Gail", "Georgia",
    "Gertrude", "Gladys", "Gloria", "Grace", "Hannah", "Hazel", "Heather", "Helen", "Holly", "Irene",
    "Iris", "Isabel", "Jackie", "Jacqueline", "Jamie", "Jane", "Janet", "Janice", "Jean", "Jennifer",
    "Jessica", "Jill", "Joann", "Joanne", "Jocelyn", "Josephine", "Joy", "Joyce", "Judith", "Judy",
    "Julia", "Julie", "June", "Karen", "Katherine", "Kathleen", "Kathryn", "Kathy", "Katie", "Kayla",
    "Kelly", "Kimberly", "Laura", "Lauren", "Laurie", "Leah", "Leslie", "Lillian", "Linda", "Lisa",
    "Lois", "Loretta", "Lori", "Louise", "Lucille", "Lucy", "Lydia", "Lynn", "Mabel", "Madeline",
    "Margaret", "Maria", "Marian", "Marie", "Marilyn", "Marion", "Marjorie", "Martha", "Mary", "Maureen",
    "Megan", "Melanie", "Melissa", "Michelle", "Mildred", "Nancy", "Natalie", "Nicole", "Norma", "Pamela"
};

String[] lastNames = {
    "Abbott", "Adams", "Adkins", "Alexander", "Allen", "Anderson", "Andrews", "Armstrong", "Arnold", "Atkinson",
    "Austin", "Bailey", "Baker", "Baldwin", "Ball", "Barker", "Barnes", "Barnett", "Barrett", "Bates",
    "Beck", "Bell", "Bennett", "Berry", "Bishop", "Black", "Blair", "Blake", "Bowen", "Bowman",
    "Boyd", "Bradley", "Brady", "Brewer", "Brooks", "Brown", "Bryant", "Burgess", "Burke", "Burns",
    "Burton", "Bush", "Butler", "Byrd", "Caldwell", "Campbell", "Carlson", "Carpenter", "Carr", "Carroll",
    "Carter", "Case", "Chambers", "Chandler", "Chapman", "Chase", "Clark", "Clarke", "Clayton", "Clements",
    "Cobb", "Cole", "Coleman", "Collins", "Cook", "Cooper", "Copeland", "Cox", "Craig", "Crawford",
    "Cross", "Cunningham", "Curtis", "Daniel", "Daniels", "Davidson", "Davis", "Dawson", "Day", "Dean",
    "Dixon", "Douglas", "Doyle", "Drake", "Duncan", "Dunn", "Dutton", "Edwards", "Elliott", "Ellis",
    "Evans", "Farmer", "Ferguson", "Fields", "Fisher", "Fitzgerald", "Fleming", "Fletcher", "Ford", "Foster",
    "Fowler", "Fox", "Franklin", "Fraser", "Freeman", "Fuller", "Gardner", "Garrett", "Gates", "George",
    "Gibson", "Gilbert", "Giles", "Glass", "Glover", "Gordon", "Graham", "Grant", "Gray", "Green",
    "Greene", "Gregory", "Griffin", "Griffith", "Gross", "Hale", "Hall", "Hamilton", "Hammond", "Hampton",
    "Harding", "Hardy", "Harper", "Harris", "Harrison", "Hart", "Harvey", "Hawkins", "Hayes", "Haynes",
    "Henderson", "Henry", "Hicks", "Higgins", "Hill", "Hines", "Hodges", "Hoffman", "Hogan", "Holland",
    "Holloway", "Holmes", "Holt", "Hopkins", "Horton", "Howard", "Howell", "Hubbard", "Hudson", "Hughes",
    "Hunt", "Hunter", "Ingram", "Jackson", "Jacobs", "James", "Jenkins", "Jennings", "Jensen", "Johnson",
    "Johnston", "Jones", "Jordan", "Joseph", "Kaufman", "Kelley", "Kelly", "Kennedy", "Kent", "Kerr",
    "King", "Knight", "Lambert", "Lane", "Lawrence", "Lawson", "Lee", "Leonard", "Lewis", "Little",
    "Lloyd", "Logan", "Long", "Love", "Lowe", "Lucas", "Lynch", "Lyons", "Macdonald", "Mackenzie"
};
boolean isfemale = ra.nextInt(100) > endyear-year && ra.nextBoolean();
String fname = (isfemale)? femaleFirstNames[ra.nextInt(femaleFirstNames.length)]:maleFirstNames[ra.nextInt(maleFirstNames.length)];


if(ra.nextInt(100)<25){
    char[] uppercaseAlphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    fname += " "+ uppercaseAlphabet[ra.nextInt(uppercaseAlphabet.length)]+".";
}

if(ra.nextInt(100)<15 && !isfemale){
    char[] uppercaseAlphabet = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    fname = uppercaseAlphabet[ra.nextInt(uppercaseAlphabet.length)]+".";
    fname += uppercaseAlphabet[ra.nextInt(uppercaseAlphabet.length)]+".";
}
String lname = lastNames[ra.nextInt(lastNames.length)];
if(!isfemale){
    String[] suffixes = {" Jr.", " Sr.", " II", " III", " IV", " I"};
    if(ra.nextInt(100)<20){
        lname += suffixes[ra.nextInt(suffixes.length)];
    }
}

if(isfemale){
    if(ra.nextInt(100)<25){
        lname+="-"+lastNames[ra.nextInt(lastNames.length)];
    }
    
}
return fname+" "+ lname;
    }
    public static Random ra = new Random();
    public static int year = 1850, endyear = 2030;
    
	public static void main(String[] args) {
		int increment = 10;
		do{
		    for(int i=0; i<10;i++){ // i is ideology. i*10
		        for(int c=0; c<5;c++){
		            System.out.println("allPersons.add(new Person(\""+genName()+"\","+(year-ra.nextInt(5))+","+(year+ (ra.nextInt(50)+10))+","+((i*10)+ra.nextInt(8))+"));");
		        }
		    }
		    year+=increment;
		}while(year!=endyear);
	}
}
