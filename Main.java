import java.util.*;
public class Main 
{
    public static Scanner sc = new Scanner(System.in);
    public static Random ra = new Random();
    
    public static class ideoGroup {
        String name;
        String splintername;
        int size;
        int ideology;
        int satisfaction;
        boolean hasSplintered = false;
        
        
        public ideoGroup(String name, String splintername, int size, int ideology){
            this.name = name;
            this.splintername = splintername;
            this.size = size;
            this.ideology = ideology;
            this.satisfaction = 100;
        }
        
        public String getSplinterName(){return splintername;}
        public String getName(){return name;}
        public int getSize(){return size;}
        public int getIdeology(){return ideology;}
        public int getSatisfaction(){return satisfaction;}
        
        public boolean hasGroupSplintered(){return hasSplintered;}
        public void toggleSplinter(){
            hasSplintered = true;
        }
        
        public int proximityWith(Party par){
            return 100-Math.abs(par.getIdeology()-ideology);
        }
        public int proximityWith(int num){
            return 100-Math.abs(num-ideology);
        }
        
        public void updateSize(int toAdd){
            size+= toAdd;
        }
        
        public void updateSatisfaction(int toAdd){
            satisfaction += toAdd;
        }
        
    }
    
    public static class Party{
        String name;
        int ideology; // goes from 0 - 100 0- most rightwing, 100 - most left-wing
        boolean isActive;
        int score=0; // total raw popularity score
        int percent = 0; // percentage
        int popularity=0; // approval rating
        Map<ideoGroup, Integer> demographics = new HashMap<>();
        double recognition = 0;// how established a party is
        int failcount = 0;
        String color;
        int delegates = 0;
        Map<Party, Integer> relations = new HashMap<>();
        
        Person standardBearer; // for president 
        Person chairman; // for prime minister
        Person forSpeaker; 
        
        
        
        double fatigue = 0;
        
        public Party(String name, int ideology, boolean isActive, String color){
            this.name = name;
            this.ideology = ideology;
            this.isActive = isActive;
            this.color = color;
            failcount = 0;
        }
        
        public void determineLeadership(){
            List<Person> memberPersons = new ArrayList();
            for(Person per: activePersons){
                if(per.getCurrentParty() == this){
                    memberPersons.add(per);
                }
            }
            
            int maxnum = Integer.MIN_VALUE;
            Person maxper=null;
            for(Person per: memberPersons){
                int points= per.getProminence();
                if(points> maxnum){
                    maxnum = points;
                    maxper = per;
                }
            }
            
            standardBearer = maxper;
            maxnum = Integer.MIN_VALUE;
            maxper=null;
            for(Person per: memberPersons){
                int points= per.getProminence();
                if(points> maxnum && per!=standardBearer){
                    maxnum = points;
                    maxper = per;
                }
            }
            chairman = maxper;
            maxnum = Integer.MIN_VALUE;
            maxper=null;
            for(Person per: memberPersons){
                int points= per.getProminence();
                if(points> maxnum && per!=standardBearer && per!=chairman){
                    maxnum = points;
                    maxper = per;
                }
            }
            forSpeaker = maxper;
        }
        
        public void incrementFail(){failcount++;}
        public int getFailCount(){return failcount;}
        public void resetFail(){failcount=0;}
        
        public String getColor(){
            return color;
        }
        
        public String getName(){return name;}
        public int getIdeology(){return ideology;}
        public boolean isPartyActive(){return isActive;}
        public int getScore(){return score;}
        public int getPopularity(){return popularity;}
        public int getPercent(){return percent;}
        
        public int proximityWith(int num){
            return 100-Math.abs(num-ideology);
        }
        
        public void updateRelations(){
            if(relations == null){
                for(Party par: allParties){
                    relations.put(par, this.proximityWith(par));
                }
            }
            for(Party par: allParties){
                if(!relations.containsKey(par)){
                    relations.put(par,this.proximityWith(par));
                }
            }
            List<Party> toRemove = new ArrayList<>();
            for(Party par: relations.keySet()){
                if(!allParties.contains(par)){
                    toRemove.add(par);
                }
            }
            
            for(Party par: toRemove){
                relations.remove(par);
            }
            if(rulingCoalition.getMemberList().contains(this)){
                for(Party par: allParties){
                    if(rulingCoalition.getMemberList().contains(par) && par!=this){
                        int boost = 15 + (int)(rulingCoalition.getMemberList().size() * 2);
                        relations.put(par, (relations.get(par)+20)+boost);
                    }else{
                        relations.put(par, relations.get(par)-10);
                    }
                }
            }
            
            if(LOTO == this){
                for(Party par: allParties){
                    if(rulingCoalition.getMemberList().contains(par)){
                        relations.put(par, relations.get(par)-25);
                    }
                }
            }
            
            for(Party par: allParties){
                if(this.proximityWith(par)>=80){
                    relations.put(par, relations.get(par)+ 20);
                }else if (this.proximityWith(par) <= 20 ){
                    relations.put(par, relations.get(par)- 20);
                }
                
                if(this.getIdeology()>25 && this.getIdeology()<75){
                    if(par.getIdeology()<25 && par.getIdeology()>75){
                        relations.put(par, relations.get(par)-25);
                    }
                }else{
                    if(par.getIdeology()>25 || par.getIdeology()<75){
                        relations.put(par, relations.get(par)-25);
                    }
                }
            }
            
            
            
            
            
            if(!rulingCoalition.getMemberList().contains(this)){
                for(Party par: rulingCoalition.getMemberList()){
                    if(par!=this){
                        for(Party targetPar: allParties){
                            if(par.relationWith(targetPar)<50){
                                relations.put(targetPar, relations.get(targetPar)+1);
                            }
                        }
                    }
                }
            }
            
            for(Party par: allParties){
                    if(par!=this){
                        if(this.relationWith(par)<50){
                        for(Party targetPar: allParties){
                            if(par.relationWith(targetPar)<50 && this!=targetPar){
                                relations.put(targetPar, relations.get(targetPar)+1);
                            }
                        }
                        }
                        if(this.getPercent()>30){
                            relations.put(par, relations.get(par)-(par.getPercent()/3));
                        }
                    }
                }
            
            
            for(Party par: relations.keySet()){
                if(relations.get(par)>100){
                    relations.put(par, 100);
                }
                if(relations.get(par)< 0){
                    relations.put(par, 0);
                }
            }
            
        }
        
        public int relationWith(Party par){
            int retVal = 0;
            if(relations.containsKey(par)){
                retVal = relations.get(par);
            }
            return retVal;
        }
        
        public int getDelegates(){
            return delegates;
        }
        
        public void resetDel(){
            delegates = 0;
        }
        
        public void addNoOfDels(int toAdd){
            delegates+=toAdd;
        }
        
        public void addDelegates(){
            delegates++;
        }
        
        public String ideoDisplay(){
            return RESET+" ("+getDynamicColor(this.ideology)+")";
        }
        
        public double getFatigue(){
            return fatigue;
        }
        
        public void addFatigue(){
            fatigue +=0.01;
        }
        public void decreaseFatigue(){
            if(fatigue>0){
            fatigue -= 0.01;
            }
        }
        
        public double getRecognition(){return recognition;}
        public void setRecog(double newVal){
            recognition = newVal;
        }
        
        public void incrementRecognition(){
            recognition+=0.02;
        }
        
        public void setPercent(int newVal){
            percent = newVal;
        }
        
        public int proximityWith(ideoGroup gro){
            return 100-Math.abs(gro.getIdeology()-ideology);
        }
        
        public int proximityWith(Party par){
            return 100-Math.abs(par.getIdeology()-ideology);
        }
        
        public void resetScore(){ score = 0;}
        public void addToScore(int toAdd){
            score+= toAdd;
        }
        
        public void setApproval(int newVal){
            popularity = newVal;
        }
        
        public void updateApproval(int newVal){
            popularity += newVal;
        }
        
        public void resetElectionData(){
            score = 0;
            demographics.clear();
        }
        public void recordVotes(ideoGroup gro, int amt){
            demographics.put(gro, amt);
        }
        
        public void addVotes(int toAdd){
            score+= toAdd;
        }
        
        public Person getStandardB(){
            return standardBearer;
        }
        public Person getChair(){
            return chairman;
        }
        public Person getForSpeak(){
            return forSpeaker;
        }
        
        public void ideoDriftOld(){
            if(score == 0) return;
            double weightedIdeologySum = 0;
            for(Map.Entry<ideoGroup,Integer> entry : demographics.entrySet()){
                ideoGroup gro = entry.getKey();
                int votesGot = entry.getValue();
                
                weightedIdeologySum += (gro.getIdeology()*votesGot);
            }
            
            int targetIdeo = (int) (weightedIdeologySum / score);
            int driftspeed = 5;
            if(this.ideology < targetIdeo) this.ideology+= driftspeed;
            if(this.ideology> targetIdeo) this.ideology-= driftspeed;
            if(this.ideology> 100){
                this.ideology = 100;
            }
            if(this.ideology< 0){
                this.ideology = 0;
            }
        }
        
        
        public void ideoDrift(){
            if(score == 0) return;
            double weightedIdeologySum = 0;
            ideoGroup maxGroup = null;
            int maxnum=-100000;
            for(Map.Entry<ideoGroup,Integer> entry : demographics.entrySet()){
                ideoGroup gro = entry.getKey();
                int votesGot = entry.getValue();
                if(entry.getValue()> maxnum){
                    maxGroup = entry.getKey();
                    maxnum = entry.getValue();
                }
            }
            
            int targetIdeo = maxGroup.getIdeology();
            int driftspeed = (this.ideology >25 && this.ideology <75)? 2:1;
            
            if(ra.nextInt(10)<5){
                if(this.ideology < targetIdeo) this.ideology+= driftspeed;
                if(this.ideology> targetIdeo) this.ideology-= driftspeed;
            }
            
            
            this.ideology += ra.nextInt(1)-ra.nextInt(1);
            
            int minsat = 1000;
            ideoGroup minGroup = null;
            for(ideoGroup gro : allGroups){
                if(gro.getSatisfaction()< minsat){
                    minsat = gro.getSatisfaction();
                    minGroup = gro;
                }
            }
            targetIdeo = minGroup.getIdeology();
            driftspeed = (this.ideology >25 && this.ideology <75)? 2:1;
            
            if(ra.nextInt(10)<5){
                if(this.ideology < targetIdeo) this.ideology+= driftspeed;
                if(this.ideology> targetIdeo) this.ideology-= driftspeed;
            }
            
            
            int avgideo = 0;
            if(rulingCoalition !=null && rulingCoalition.getMemberList().contains(this)){
                double totalWeightedIdeology = 0;
                int totalSeats = 0;
            
                for (Party par : rulingCoalition.getMemberList()) {
                    int seats = par.getPercent(); 
                    totalWeightedIdeology += (par.getIdeology() * seats);
                    totalSeats += seats;
                }
            
                if (totalSeats > 0) {
                    avgideo =(int) (totalWeightedIdeology / totalSeats);
                }
            }
            if(ra.nextInt(10)<5){
                if(this.ideology < avgideo) this.ideology+= driftspeed/2;
                if(this.ideology> avgideo) this.ideology-= driftspeed/2;
            }
            
            if(ra.nextInt(10)<5){
                if(this.ideology>75) this.ideology++;
                if(this.ideology<25) this.ideology--;
            }
            
            
            Party maxpar=null;
            Party minpar=null;
            int maxrel=Integer.MIN_VALUE;
            int minrel=Integer.MAX_VALUE;
            for(Party par: relations.keySet()){
                if(relations.get(par)>maxrel){
                    maxrel = relations.get(par);
                    maxpar = par;
                }
                
                if(relations.get(par)<minrel){
                    minrel = relations.get(par);
                    minpar=par;
                }
            }
            if(ra.nextInt(10)<5){
                if(this.ideology > maxpar.getIdeology()){
                this.ideology--;
                }else{
                    this.ideology++;
                }
                
                if(this.ideology > minpar.getIdeology()){
                    this.ideology++;
                }else{
                    this.ideology--;
                }
            }
            
            if(this.ideology> 100){
                this.ideology = 100;
            }
            if(this.ideology< 0){
                this.ideology = 0;
            }
        }
    }
    
    public static class Coalition{
        Party leader;
        int size;
        List<Party> members = new ArrayList<>();
        
        public Coalition(Party leader){
            this.leader = leader;
            size = leader.getPercent();
            members.add(leader);
        }
        
        public Party getLeader(){return leader;}
        
        public int getSize(){ return size;}
        
        public void addSize(int toAdd){
            size+=toAdd;
        }
        
        public void resetList(){
            members.clear();
        }
        
        public boolean invitation(Party other){
            if(leader.proximityWith(other)> 50){
                return true;
            }else{
                return false;
            }
        }
        
        public void addParty(Party toAdd){
            members.add(toAdd);
        }
        
        public boolean containsParty(Party par){
            return members.contains(par);
        }
        
        public List<Party> getMemberList(){
            return members;
        }
    }
    
    public static class Region{
        String name;
        int ideology;
        Party winPar=null;
        
        public Region(String name, int ideology){
            this.name=name;
            this.ideology=ideology;
        }
        
        public void displayWinner(){
            Party maxpar=null;
            int maxnum=Integer.MIN_VALUE;
            int divider=1;
            int curscore=0;
            for(Party par: allParties){
                if(par.getPercent()>0){
                divider = (Math.abs(par.getIdeology()-ideology)/10)+1;
                curscore = (par.getScore()/divider);
                
                if(curscore>maxnum){
                    maxnum=curscore;
                    maxpar = par;
                }
                }
            }
            
            if(maxpar!=null){
                System.out.println(name+ ": "+maxpar.getColor()+maxpar.getName()+RESET+maxpar.ideoDisplay());
                winPar = maxpar;
            }else{
                System.out.println("nowinner");
            }
        }
        
        public String getName(){
            return name;
        }
        
        public Party getWinner(){
            return winPar;
        }
    }
    
    public static class Person{
        String name;
        int startYear, endYear;
        int ideology;
        int prominence;
        Party currentParty;
        
        public Person(String name, int startYear, int endYear, int ideology){
            this.name=name;
            this.startYear=startYear;
            this.endYear=endYear;
            this.ideology=ideology;
        }
        public int proximityWith(Party par){
            return 100-Math.abs(par.getIdeology()-ideology);
        }
        public int proximityWith(int num){
            return 100-Math.abs(num-ideology);
        }
        
        public Party getCurrentParty(){
            return currentParty;
        }
        
        public void determineParty(){
            Party maxpar=null;
            int maxnum = Integer.MIN_VALUE;
            
            for(Party par: allParties){
                int points = proximityWith(par);
                if(points> maxnum){
                    maxnum = points;
                    maxpar = par;
                }
            }
            
            currentParty = maxpar;
        }
        
        public void determineProminence(){
            
            if(currentParty!=null){
                
                prominence = currentParty.getPercent()/4;
                prominence+= proximityWith(currentParty)/2;
                prominence+= ra.nextInt(25);
                
            }
            
            if(prominence>100){
                prominence = 100;
            }
            
            if(prominence<0){
                prominence=0;
            }
        }
        
        public boolean withinActiveYears(){
            return year>=startYear && year<=endYear;
        }
        
        public int getProminence(){
            return prominence;
        }
        
        public String getName(){
            return name;
        }
        
        @Override
        public String toString(){
            return name + " - " + currentParty.getColor()+ currentParty.getName()+ RESET+ currentParty.ideoDisplay();
        }
    }
    
    public static List<Person> allPersons = new ArrayList<>();
    public static List<Person> activePersons = new ArrayList<>();
    
    public static void addPersons(){
        // format: allPersons.add(new Person("",startyear, endyear, ideology));
        allPersons.add(new Person("Nicholas Moore", 1830, 1849, 12));
allPersons.add(new Person("Eric Wright", 1831, 1867, 68));
allPersons.add(new Person("Nicholas Morales", 1832, 1854, 18));
allPersons.add(new Person("Jason Taylor", 1833, 1867, 10));
allPersons.add(new Person("Kevin Lopez", 1834, 1859, 11));
allPersons.add(new Person("Joseph Edwards", 1834, 1852, 30));
allPersons.add(new Person("Justin Martinez", 1835, 1873, 51));
allPersons.add(new Person("William Thomas", 1835, 1872, 68));
allPersons.add(new Person("Timothy Hernandez", 1837, 1857, 38));
allPersons.add(new Person("Cynthia Campbell", 1838, 1868, 57));
allPersons.add(new Person("John Young", 1839, 1874, 77));
allPersons.add(new Person("George Perez", 1840, 1860, 71));
allPersons.add(new Person("Donna Murphy", 1840, 1871, 1));
allPersons.add(new Person("Scott Martin", 1841, 1872, 55));
allPersons.add(new Person("Robert Evans", 1841, 1865, 67));
allPersons.add(new Person("Jeffrey Hill", 1843, 1871, 58));
allPersons.add(new Person("Brandon Anderson", 1844, 1868, 95));
allPersons.add(new Person("Joshua Parker", 1845, 1874, 3));
allPersons.add(new Person("Justin Rivera", 1845, 1884, 47));
allPersons.add(new Person("Charles Rogers", 1845, 1869, 54));
allPersons.add(new Person("Michael Williams", 1847, 1866, 56));
allPersons.add(new Person("Kevin Perez", 1847, 1882, 49));
allPersons.add(new Person("Jacob Miller", 1847, 1875, 27));
allPersons.add(new Person("Daniel Martin", 1852, 1886, 78));
allPersons.add(new Person("Robert Morris", 1852, 1883, 48));
allPersons.add(new Person("Donald Harris", 1853, 1879, 56));
allPersons.add(new Person("Joshua Diaz", 1853, 1895, 48));
allPersons.add(new Person("Donald Ramirez", 1854, 1872, 67));
allPersons.add(new Person("Timothy Ortiz", 1855, 1897, 73));
allPersons.add(new Person("Jeffrey Nelson", 1855, 1894, 92));
allPersons.add(new Person("Anthony Green", 1855, 1890, 82));
allPersons.add(new Person("Brian Adams", 1857, 1872, 8));
allPersons.add(new Person("Jonathan Martinez", 1857, 1889, 86));
allPersons.add(new Person("Anthony Williams", 1857, 1875, 62));
allPersons.add(new Person("Christopher Robinson", 1858, 1873, 27));
allPersons.add(new Person("Richard Smith", 1862, 1877, 87));
allPersons.add(new Person("Matthew Allen", 1863, 1901, 10));
allPersons.add(new Person("Larry King", 1863, 1903, 57));
allPersons.add(new Person("Michael Roberts", 1864, 1883, 55));
allPersons.add(new Person("Mark Morgan", 1864, 1881, 36));
allPersons.add(new Person("Justin Wilson", 1867, 1889, 45));
allPersons.add(new Person("Kenneth Ortiz", 1867, 1884, 92));
allPersons.add(new Person("James Torres", 1868, 1893, 20));
allPersons.add(new Person("Robert Brown", 1869, 1907, 58));
allPersons.add(new Person("Anthony Morgan", 1869, 1889, 39));
allPersons.add(new Person("George Morris", 1870, 1887, 92));
allPersons.add(new Person("George Clark", 1871, 1910, 52));
allPersons.add(new Person("Charles Miller", 1872, 1893, 16));
allPersons.add(new Person("Kenneth Morgan", 1872, 1899, 88));
allPersons.add(new Person("Jacob Turner", 1873, 1912, 29));
allPersons.add(new Person("Michael Carter", 1873, 1908, 80));
allPersons.add(new Person("Joseph Lewis", 1875, 1896, 25));
allPersons.add(new Person("Michael Martin", 1876, 1899, 34));
allPersons.add(new Person("Scott Scott", 1876, 1917, 62));
allPersons.add(new Person("Edward Nguyen", 1877, 1916, 82));
allPersons.add(new Person("Amanda Adams", 1877, 1914, 64));
allPersons.add(new Person("Paul Flores", 1877, 1899, 83));
allPersons.add(new Person("Robert Adams", 1878, 1913, 98));
allPersons.add(new Person("Richard Thompson", 1881, 1921, 77));
allPersons.add(new Person("Eric Morris", 1882, 1911, 87));
allPersons.add(new Person("Kathleen Hill", 1882, 1902, 95));
allPersons.add(new Person("Robert Clark", 1882, 1912, 77));
allPersons.add(new Person("Jacob Green", 1883, 1912, 25));
allPersons.add(new Person("Ryan Evans", 1883, 1923, 75));
allPersons.add(new Person("James Thomas", 1886, 1906, 74));
allPersons.add(new Person("Michael Harris", 1887, 1907, 17));
allPersons.add(new Person("Andrew Smith", 1888, 1911, 84));
allPersons.add(new Person("Scott Morris", 1888, 1915, 16));
allPersons.add(new Person("Mark Young", 1888, 1922, 39));
allPersons.add(new Person("Gary Allen", 1892, 1933, 54));
allPersons.add(new Person("Jeffrey Lopez", 1892, 1920, 56));
allPersons.add(new Person("Stephen Turner", 1892, 1913, 24));
allPersons.add(new Person("Larry Adams", 1893, 1934, 25));
allPersons.add(new Person("John Roberts", 1894, 1927, 4));
allPersons.add(new Person("Brian Rodriguez", 1894, 1918, 89));
allPersons.add(new Person("Joshua Taylor", 1895, 1921, 83));
allPersons.add(new Person("Betty Williams", 1896, 1936, 88));
allPersons.add(new Person("Anthony Robinson", 1896, 1937, 42));
allPersons.add(new Person("Nicholas Johnson", 1899, 1938, 9));
allPersons.add(new Person("Jacob Carter", 1900, 1933, 45));
allPersons.add(new Person("John Robinson", 1900, 1930, 84));
allPersons.add(new Person("Michael Brown", 1901, 1922, 70));
allPersons.add(new Person("Gary Cooper", 1902, 1930, 69));
allPersons.add(new Person("Charles Collins", 1904, 1922, 26));
allPersons.add(new Person("William Anderson", 1904, 1946, 75));
allPersons.add(new Person("Donald Turner", 1905, 1941, 15));
allPersons.add(new Person("Daniel Hill", 1905, 1944, 66));
allPersons.add(new Person("Christopher Lopez", 1907, 1948, 7));
allPersons.add(new Person("Brian Thomas", 1907, 1934, 42));
allPersons.add(new Person("Robert Nguyen", 1908, 1937, 58));
allPersons.add(new Person("Nicholas Thompson", 1909, 1948, 61));
allPersons.add(new Person("Jonathan Torres", 1911, 1933, 76));
allPersons.add(new Person("Donald Wright", 1912, 1942, 90));
allPersons.add(new Person("Donald King", 1912, 1939, 14));
allPersons.add(new Person("Jason Roberts", 1913, 1948, 48));
allPersons.add(new Person("Mark Edwards", 1913, 1937, 18));
allPersons.add(new Person("Timothy Lopez", 1913, 1935, 7));
allPersons.add(new Person("Robert Lewis", 1914, 1955, 89));
allPersons.add(new Person("Stephen Hernandez", 1915, 1945, 17));
allPersons.add(new Person("Paul Carter", 1915, 1931, 27));
allPersons.add(new Person("Paul Ramirez", 1916, 1934, 40));
allPersons.add(new Person("Jonathan Gutierrez", 1917, 1949, 35));
allPersons.add(new Person("Edward Turner", 1917, 1937, 16));
allPersons.add(new Person("Edward Thomas", 1919, 1942, 59));
allPersons.add(new Person("Melissa Flores", 1919, 1950, 15));
allPersons.add(new Person("Stephen White", 1922, 1946, 8));
allPersons.add(new Person("Sandra Cooper", 1922, 1953, 23));
allPersons.add(new Person("Margaret Baker", 1922, 1950, 30));
allPersons.add(new Person("Joseph Flores", 1925, 1961, 47));
allPersons.add(new Person("Andrew Thompson", 1925, 1947, 27));
allPersons.add(new Person("Eric Smith", 1925, 1964, 15));
allPersons.add(new Person("Steven Flores", 1926, 1942, 90));
allPersons.add(new Person("Joshua Rogers", 1926, 1964, 54));
allPersons.add(new Person("Richard Hill", 1926, 1955, 17));
allPersons.add(new Person("Kathleen Gomez", 1929, 1954, 43));
allPersons.add(new Person("Donald Edwards", 1929, 1971, 44));
allPersons.add(new Person("Brandon Parker", 1930, 1969, 92));
allPersons.add(new Person("Kenneth Garcia", 1931, 1959, 46));
allPersons.add(new Person("Edward Morris", 1932, 1967, 9));
allPersons.add(new Person("Brandon Collins", 1933, 1972, 76));
allPersons.add(new Person("Scott Cooper", 1933, 1955, 14));
allPersons.add(new Person("James Phillips", 1934, 1958, 0));
allPersons.add(new Person("Michael Thompson", 1935, 1967, 34));
allPersons.add(new Person("Ronald Roberts", 1937, 1971, 61));
allPersons.add(new Person("Steven Diaz", 1938, 1962, 97));
allPersons.add(new Person("James Diaz", 1938, 1957, 66));
allPersons.add(new Person("Richard Morales", 1939, 1980, 1));
allPersons.add(new Person("Gary Garcia", 1939, 1980, 8));
allPersons.add(new Person("Amanda Clark", 1939, 1980, 84));
allPersons.add(new Person("Jacob Ramirez", 1940, 1973, 25));
allPersons.add(new Person("Deborah Martin", 1943, 1977, 26));
allPersons.add(new Person("Donna Moore", 1944, 1962, 29));
allPersons.add(new Person("Stephen Carter", 1945, 1987, 31));
allPersons.add(new Person("Mark Cook", 1946, 1962, 93));
allPersons.add(new Person("Jacob Hill", 1948, 1980, 16));
allPersons.add(new Person("Brian Hill", 1949, 1985, 87));
allPersons.add(new Person("Nicholas Sanchez", 1949, 1980, 19));
allPersons.add(new Person("Jeffrey Rodriguez", 1950, 1968, 87));
allPersons.add(new Person("Matthew Murphy", 1950, 1970, 96));
allPersons.add(new Person("Matthew Gutierrez", 1953, 1994, 7));
allPersons.add(new Person("Jason Phillips", 1953, 1980, 43));
allPersons.add(new Person("Joseph Scott", 1956, 1997, 86));
allPersons.add(new Person("Daniel Rodriguez", 1957, 1976, 98));
allPersons.add(new Person("Joshua Anderson", 1957, 1984, 73));
allPersons.add(new Person("Charles Campbell", 1958, 1975, 28));
allPersons.add(new Person("Jonathan Jackson", 1959, 1975, 27));
allPersons.add(new Person("William Harris", 1960, 1989, 36));
allPersons.add(new Person("Karen Allen", 1960, 1988, 22));
allPersons.add(new Person("James Jackson", 1960, 1996, 61));
allPersons.add(new Person("Jason Williams", 1961, 1985, 34));
allPersons.add(new Person("Joshua Williams", 1961, 1997, 62));
allPersons.add(new Person("Angela Young", 1961, 1993, 5));
allPersons.add(new Person("John Morgan", 1961, 1996, 30));
allPersons.add(new Person("Amanda Reyes", 1962, 1982, 92));
allPersons.add(new Person("Matthew Rodriguez", 1965, 1980, 91));
allPersons.add(new Person("Charles Jones", 1967, 1982, 6));
allPersons.add(new Person("Michelle Cooper", 1967, 2004, 38));
allPersons.add(new Person("Brian Thompson", 1968, 2001, 79));
allPersons.add(new Person("Jonathan Evans", 1969, 1990, 1));
allPersons.add(new Person("Andrew Diaz", 1969, 1996, 6));
allPersons.add(new Person("Stephen Brown", 1970, 1991, 25));
allPersons.add(new Person("Karen Robinson", 1970, 1989, 49));
allPersons.add(new Person("Larry Cooper", 1972, 1990, 29));
allPersons.add(new Person("Thomas Reyes", 1973, 2000, 41));
allPersons.add(new Person("Donald Jones", 1973, 2003, 45));
allPersons.add(new Person("Gary Flores", 1975, 2012, 64));
allPersons.add(new Person("Larry Brown", 1977, 1994, 96));
allPersons.add(new Person("Paul Perez", 1977, 2002, 95));
allPersons.add(new Person("Paul Wright", 1977, 2012, 3));
allPersons.add(new Person("Mary Mitchell", 1977, 2008, 6));
allPersons.add(new Person("Carol Miller", 1980, 2008, 21));
allPersons.add(new Person("Brenda Robinson", 1980, 2013, 85));
allPersons.add(new Person("Gary Robinson", 1980, 2004, 77));
allPersons.add(new Person("Barbara Williams", 1981, 2012, 94));
allPersons.add(new Person("Jacob Rodriguez", 1982, 2007, 42));
allPersons.add(new Person("Angela Moore", 1982, 2015, 40));
allPersons.add(new Person("David Collins", 1983, 2025, 93));
allPersons.add(new Person("Emily Mitchell", 1984, 2007, 82));
allPersons.add(new Person("Carol Gomez", 1987, 2026, 48));
allPersons.add(new Person("Mary Murphy", 1987, 2015, 28));
allPersons.add(new Person("Patricia Cook", 1989, 2026, 88));
allPersons.add(new Person("Eric Scott", 1990, 2013, 32));
allPersons.add(new Person("Daniel Harris", 1991, 2018, 32));
allPersons.add(new Person("Anthony Diaz", 1991, 2021, 70));
allPersons.add(new Person("Anthony White", 1991, 2026, 33));
allPersons.add(new Person("Eric Jones", 1992, 2026, 81));
allPersons.add(new Person("Carol Mitchell", 1993, 2011, 19));
allPersons.add(new Person("Nicole Roberts", 1993, 2021, 78));
allPersons.add(new Person("Melissa Cruz", 1994, 2016, 100));
allPersons.add(new Person("Helen Lopez", 1995, 2026, 4));
allPersons.add(new Person("Anthony Taylor", 1995, 2026, 31));
allPersons.add(new Person("Robert King", 1997, 2025, 97));
allPersons.add(new Person("Betty Adams", 1998, 2017, 76));
allPersons.add(new Person("Donald Martinez", 1998, 2024, 97));
allPersons.add(new Person("Kevin Garcia", 1999, 2024, 42));
allPersons.add(new Person("Margaret Turner", 1999, 2026, 79));
allPersons.add(new Person("Linda Perez", 1999, 2026, 0));
allPersons.add(new Person("Mark Rodriguez", 2000, 2020, 63));
allPersons.add(new Person("Karen Jackson", 2002, 2026, 99));
allPersons.add(new Person("Stephanie Hill", 2004, 2026, 3));
    }
    
    
    public static void allDetLeadership(){
        for(Party par: allParties){
            par.determineLeadership();
        }
    }
    public static void checkForActives(){
        List<Person> toAdd = new ArrayList<>();
        for(Person per: allPersons){
            if(per.withinActiveYears()){
                toAdd.add(per);
            }
        }
        
        activePersons.addAll(toAdd);
    }
    public static void checkForInactives(){
        List<Person> toRemove = new ArrayList<>();
        for(Person per: allPersons){
            if(!per.withinActiveYears()){
                toRemove.add(per);
            }
        }
        
        activePersons.removeAll(toRemove);
    }
    
    public static void assessAffiliations(){
        for(Person per: activePersons){
            per.determineParty();
        }
    }
    
    public static void assessProminence(){
        for(Person per: activePersons){
            per.determineProminence();
        }
    }
    
    
    
    
public static String[][] mapProgside = {
    {"-","-","N","N","N","N","N","N","N","-","-","-"},
    {"-","N","N","N","N","N","N","N","N","E","E","-"},
    {"W","W","N","N","C","C","C","N","E","E","E","E"},
    {"W","W","W","C","C","C","C","E","E","E","E","-"},
    {"-","W","W","C","C","C","C","-","-","E","E","-"},
    {"-","W","W","W","C","C","-","-","-","-","-","-"},
    {"-","-","W","W","S","S","S","-","-","-","-","-"},
    {"-","-","-","S","S","S","S","S","S","-","-","-"},
    {"-","-","-","S","S","S","S","S","S","S","-","-"}
    
    
    
};
    public static String[][] mapUserSide = {
        {"","","","","","","","","","",},
        {"","","","","","","","","","",},
        {"","","","","","","","","","",},
        {"","","","","","","","","","",},
        {"","","","","","","","","","",},
        {"","","","","","","","","","",},
        {"","","","","","","","","","",},
        {"","","","","","","","","","",},
        {"","","","","","","","","","",},
        {"","","","","","","","","","",}
    };
    
    public static Coalition rulingCoalition;
    
    public static int approvalRatingChange;
    
    public static List<Region> allRegions = new ArrayList<>();
    public static void addRegions(){
        allRegions.add(new Region("Capital",55));
        allRegions.add(new Region("Northern",75));
        allRegions.add(new Region("Southern",25));
        allRegions.add(new Region("Eastern",40));
        allRegions.add(new Region("Western",65));
    }
    
    
    
    public static List<ideoGroup> allGroups = new ArrayList<>();
    public static void addGroups(){
        /*allGroups.add(new ideoGroup("Communist",2,95));
        allGroups.add(new ideoGroup("Socialist",10,80));
        allGroups.add(new ideoGroup("Progressive",20,65));
        allGroups.add(new ideoGroup("Liberal",30,50));
        allGroups.add(new ideoGroup("Conservative",20,35));
        allGroups.add(new ideoGroup("Nationalist",10,20));
        allGroups.add(new ideoGroup("Fascist",2,5));*/
        //      allGroups.add(new ideoGroup("", "", 20, 60));
        // Reactionary Bloc
    allGroups.add(new ideoGroup("Monarchists", "Peoples Congress for Tradition", 15, 10));
    allGroups.add(new ideoGroup("Illiberal Republicans", "Nationalist Peoples Assembly", 10, 25));
    
    allGroups.add(new ideoGroup("Unitary Monarchists", "Restoration Party", 5, 10));
    allGroups.add(new ideoGroup("Particularists", "National Independent Congress", 5, 12));
    
    allGroups.add(new ideoGroup("Aristocratic Conservatives", "Conservative Peoples Party", 15, 20));
    allGroups.add(new ideoGroup("Corporatists", "National Democratic Conservative Party", 15, 17));
    
    // Republican Bloc
    allGroups.add(new ideoGroup("Big-Tent Conservatives", "Conservative Democratic Party", 20, 40));
    allGroups.add(new ideoGroup("Liberals", "Liberal Peoples Party", 35, 50));
    allGroups.add(new ideoGroup("Social Democrats", "Social Democratic Party", 20, 65));
    
    allGroups.add(new ideoGroup("Market Liberals", "Liberal Conservatives", 20, 45));
    allGroups.add(new ideoGroup("Agrarian Conservatives", "Conservative Farmers Union", 17, 35));
    allGroups.add(new ideoGroup("Social Conservatives", "Peoples Democratic Party", 20, 32));
    
    allGroups.add(new ideoGroup("Social Liberals", "Progressive Union", 20, 55));
    allGroups.add(new ideoGroup("Centrists", "Democratic Liberal Party", 25, 50));
    
    allGroups.add(new ideoGroup("Reformists", "New Social Democrats", 18, 60));
    allGroups.add(new ideoGroup("Unionists", "Social Labor Party", 18, 70));
    
    
    // Revolutionary Bloc
    allGroups.add(new ideoGroup("Participationists", "Socialist Peoples Party", 15, 70));
    allGroups.add(new ideoGroup("Anti-Participationists", "All-Communist Revolutionary Party", 10, 80));
    
    allGroups.add(new ideoGroup("Democratic Socialists", "Democratic Socialist Party", 17, 67));
    allGroups.add(new ideoGroup("Syndicalists", "National Labor Alliance", 15, 72));
    allGroups.add(new ideoGroup("Agrarian Socialists", "Agrarian Socialist Party", 15, 65));
    
    allGroups.add(new ideoGroup("Anarchists", "National Revolution", 5, 85));
    allGroups.add(new ideoGroup("Statists", "Communist Action Party", 5, 95));
    }
    
    public static List<Party> allParties = new ArrayList<>();
    
    public static void addParties(){
        //allParties.add(new Party("Socialist Party", 85,true));
        //allParties.add(new Party("Democratic Party", 65, true));
        //allParties.add(new Party("Republican Party", 45, true));
        //allParties.add(new Party("Nationalist Party",15,true));
        //allParties.add(new Party("National Unity Party", 15, true)); // Reactionary
        //allParties.add(new Party("Moderate Conservative Party", 45, true));     // Republican
    //allParties.add(new Party("Republican Party", 50, true));     // Republican
   // allParties.add(new Party("Left Republican Party", 85, true)); // Revolutionary
    
    int diceroll = (ra.nextInt(15))/3;
    // roll far right unity
    if(diceroll == 4){
        allParties.add(new Party("National Unity Party", 15, true, assignColor(15)));
    }else if(diceroll == 3){
        allParties.add(new Party("National Unity Party", 25, true, assignColor(25)));
    }else if(diceroll == 2){
        allParties.add(new Party("United Loyal Party", 5, true, assignColor(5)));
        allParties.add(new Party("Alliance of Nationalists", 15, true, assignColor(15)));
        allParties.add(new Party("National Representation Party", 25, true, assignColor(25)));
        allParties.add(new Party("Peasants Republican Party", 35, true, assignColor(35)));
    }else{
        
    }
    diceroll = (ra.nextInt(15))/3;
    if(diceroll == 4){
        allParties.add(new Party("National Republican Party", 50, true, assignColor(50)));  
    }else if(diceroll == 3){
        allParties.add(new Party("Republican Party", 50, true, assignColor(50)));  
        allParties.add(new Party("National Party", 48, true, assignColor(48)));  
    }else if(diceroll == 2){
       allParties.add(new Party("Republican Party", 50, true, assignColor(50)));  
        allParties.add(new Party("National Party", 48, true, assignColor(48)));  
        allParties.add(new Party("Peoples Party", 52, true, assignColor(52)));  
    }else{
        allParties.add(new Party("Republican Party", 50, true, assignColor(50)));  
        allParties.add(new Party("National Party", 48, true, assignColor(48)));  
        allParties.add(new Party("Peoples Party", 52, true, assignColor(52)));  
        allParties.add(new Party("Conservative Republican Party", 40, true, assignColor(40)));  
    }
    // Left-wing opposition unity
    diceroll = (ra.nextInt(15))/3;
    if(diceroll == 4){
        allParties.add(new Party("Democratic Popular Front", 60, true, assignColor(55)));
    }else if(diceroll == 3){
        allParties.add(new Party("Radical Republican Party", 60, true, assignColor(60)));
        allParties.add(new Party("National Alliance of Labor", 65, true, assignColor(65)));
    }else if(diceroll == 2){
       allParties.add(new Party("Radical Republican Party", 65, true, assignColor(65)));
        allParties.add(new Party("National Workers Party", 75, true, assignColor(75)));
        allParties.add(new Party("Peoples Revolutionary Council", 80, true, assignColor(80)));
    }else{
        allParties.add(new Party("National Workers Party", 75, true, assignColor(75)));
        allParties.add(new Party("Democratic Congress of the Revolution", 80, true, assignColor(80)));
        allParties.add(new Party("Revolutionary Vanguard Party", 85, true, assignColor(85)));
        allParties.add(new Party("Peoples Reform Council", 90, true, assignColor(90)));
    }
    
    
    }
    
    public static void updateGroupSize(){
        int changeby = 0;
        for(ideoGroup gro: allGroups){
            changeby = (Math.abs(gro.getIdeology()-50)>20)? 5:8;
            if(lean.equalsIgnoreCase("Republic")){
                if(gro.getIdeology()>70 || gro.getIdeology()<20){
                changeby/=2;
                }
            }else{
                if(lean.equalsIgnoreCase("Reaction")){
                    if(gro.getIdeology()>20){
                        changeby/=2;
                    }
                }else{
                    if(gro.getIdeology()<70){
                        changeby/=2;
                    }
                }
            }
            
            gro.updateSize(ra.nextInt(changeby+1));
        }
    }
    
    public static void election(){
        for(Party par: allParties){
            par.resetElectionData();
        }
        
        Map<ideoGroup, Integer> acceptables = new HashMap<>();
        int tresh = 60+ ra.nextInt(30);
        for(ideoGroup gro: allGroups){
            for(Party par: allParties){
                if(gro.proximityWith(par)> tresh){
                    if(!acceptables.containsKey(gro)){
                        acceptables.put(gro, par.getPercent()+1);
                    }else{
                        acceptables.put(gro, acceptables.get(gro)+(par.getPercent()+1));
                    }
                }
            }
        }
        
        for (ideoGroup gro : allGroups) {
    boolean hasvoted = false;
    int maxProximity = 0;
    double totalAppealScore = 0;
    Map<Party, Double> partyAppeals = new HashMap<>();

    // 1. Determine who is eligible and find the best match
    for (Party par : allParties) {
        int currentProx = gro.proximityWith(par);
        
        // Track the highest proximity even if they don't vote
        if (currentProx > maxProximity) {
            maxProximity = currentProx;
        }

        if (currentProx > tresh) {
            // Factor in proximity and recognition
            double appeal = currentProx * (1 + (par.getRecognition() / 10.0));
            partyAppeals.put(par, appeal);
            totalAppealScore += appeal;
            hasvoted = true; // They found at least one acceptable party
        }
    }

    // 2. Distribute votes only if there are acceptable parties
    if (hasvoted) {
        for (Party par : partyAppeals.keySet()) {
            double shareOfGroup = partyAppeals.get(par) / totalAppealScore;
            int votesFromThisGroup = (int) (gro.getSize() * shareOfGroup);

            // Apply fatigue 
            votesFromThisGroup -= (votesFromThisGroup * par.getFatigue());
            votesFromThisGroup = IdeoCheck(votesFromThisGroup, par);
            
            votesFromThisGroup += (votesFromThisGroup*(par.getRecognition()*2));
            
            par.addVotes(votesFromThisGroup);
            par.recordVotes(gro, votesFromThisGroup);
        }
    }

    // 3. Calculate Satisfaction
    // If maxProximity is low, satischange will be more negative
    int satischange = -1 * (5 - (maxProximity / 20)); 
    
    if (!hasvoted) {
        // Penalty for having no one to vote for
        satischange -= ra.nextInt(10);
    }
    
    satischange += ra.nextInt(3) - ra.nextInt(3);
    gro.updateSatisfaction(satischange);
}
        
        // set percentages
        int totalVotes = 0;
        for(Party par: allParties){
            par.setPercent(0);
            totalVotes += par.getScore();
            if(par.getPercent()>30){
                par.incrementRecognition();
                par.addFatigue();
            }
            for(int i=0; i<par.getPercent()/10;i++){
                par.incrementRecognition();
                par.addFatigue();
            }
            
        }
        // proportional
        
        Map<Party,Integer> percents = new HashMap<>();
        for(Party par: allParties){
            
            int pctg = (int) (par.getScore()*100)/ totalVotes;
            percents.put(par, pctg);
            
        }
        
        
        
        int threshhold = 5+ (allParties.size()/2);
        
        List<Party> partiesOverTresh = new ArrayList<>();
        
        for(Party par: allParties){
            if(percents.get(par)>threshhold){
                partiesOverTresh.add(par);
            }
        }
        
        //seat distribution
        for(int i=0; i<80;i++){ // simulation of first past the post
            int maxnum =0;
            Party maxpar = null;
            for(Party par: allParties){
                int curscore = par.getScore()/ (ra.nextInt(3)+1);
                if(curscore > maxnum){
                    maxnum = curscore;
                    maxpar = par;
                }
            }
            //if (totalVotes <= 0) return;
            maxpar.setPercent(maxpar.getPercent()+1); 
        }
        
        //dhondt
        for(int i=0; i<20;i++){
            
            int maxnum=-1;
            Party maxpar=null;
            for(Party par: partiesOverTresh){
                    int parscore = par.getScore()/(par.getPercent()+1);
                    if(parscore>maxnum){
                        maxnum=parscore;
                        maxpar = par;
                        
                    }
                
                
            }
            if(maxpar!=null){
            maxpar.setPercent(maxpar.getPercent()+1);
            }
        }
        
        
    }
    
    public static int startyear;
    public static int year = 1852;
    
    
    
    public static void electPresident(){
        List<Party> candidates = new ArrayList<>();
        int tresh = 100/ allParties.size();
        for(Party par: allParties){
            int points = par.getPercent();
            if(rulingCoalition!=null){
                if(rulingCoalition.getMemberList().contains(par)){
                    if(rulingCoalition.getLeader()!= par){
                        points/=2;
                    }else{
                        points*=2;
                    }
                }
                
                
            }
            
            //points+= ra.nextInt((100-par.getPercent())+1);
            points += (points*par.getRecognition())/2;
            //points -= (points*par.getFatigue())/2;
            
            
            if(points>= tresh){
                candidates.add(par);
            }
        }
        
        if(candidates.isEmpty()){
            if(President== null && LOTO == null){
                candidates.add(allParties.get(ra.nextInt(allParties.size())));
            candidates.add(allParties.get(ra.nextInt(allParties.size())));
            }else{
                candidates.add(President);
                candidates.add(LOTO);
                candidates.add(rulingCoalition.getLeader());
            }
            
        }
        
        if(lean.equalsIgnoreCase("Republic")){
            if(candidates.size() ==1){
                candidates.add(allParties.get(ra.nextInt(allParties.size())));
            }
        }
        int winvotes = 0;
        Party winner = null;
        
        for(Party par: candidates){
            par.resetScore();
        }
        Map<ideoGroup, Integer> acceptables = new HashMap<>();
        tresh = 75;
        for(ideoGroup gro: allGroups){
            for(Party par: candidates){
                if(gro.proximityWith(par)> tresh){
                    if(!acceptables.containsKey(gro)){
                        acceptables.put(gro, par.getPercent()+1);
                    }else{
                        acceptables.put(gro, acceptables.get(gro)+(par.getPercent()+1));
                    }
                }
            }
        }
        
        
        
        for (ideoGroup gro : allGroups) {
    boolean hasvoted = false;
    int maxProximity = 0;
    double totalAppealScore = 0;
    Map<Party, Double> partyAppeals = new HashMap<>();

    // 1. Determine who is eligible and find the best match
    for (Party par : allParties) {
        int currentProx = gro.proximityWith(par);
        
        // Track the highest proximity even if they don't vote
        if (currentProx > maxProximity) {
            maxProximity = currentProx;
        }

        if (currentProx > tresh) {
            // Factor in proximity and recognition
            double appeal = currentProx * (1 + (par.getRecognition() / 10.0));
            partyAppeals.put(par, appeal);
            totalAppealScore += appeal;
            hasvoted = true; // They found at least one acceptable party
        }
    }

    // 2. Distribute votes only if there are acceptable parties
    if (hasvoted) {
        for (Party par : partyAppeals.keySet()) {
            double shareOfGroup = partyAppeals.get(par) / totalAppealScore;
            int votesFromThisGroup = (int) (gro.getSize() * shareOfGroup);

            // Apply fatigue 
            votesFromThisGroup -= (votesFromThisGroup * par.getFatigue()) / 100;

            par.addVotes(votesFromThisGroup);
            par.recordVotes(gro, votesFromThisGroup);
        }
    }

    // 3. Calculate Satisfaction
    // If maxProximity is low, satischange will be more negative
    int satischange = -1 * (5 - (maxProximity / 20)); 
    
    if (!hasvoted) {
        // Penalty for having no one to vote for
        satischange -= ra.nextInt(10);
    }
    
    satischange += ra.nextInt(3) - ra.nextInt(3);
    gro.updateSatisfaction(satischange/3);
}
        
        
        
        int totvotes = 0;
        for(Party par: candidates){
            if(par.getScore()> winvotes){
                winvotes = par.getScore();
                winner = par;
            }
            totvotes+= par.getScore();
        }
        candidates.sort(Comparator.comparingInt(Party::getScore).reversed());
        System.out.println("Presidential Election");
        System.out.println("Round 1");
        int ordinal = 0;
        int majorvotes = 0;
        for(Party par: candidates){
            if(ordinal <3){
                System.out.print(par.getStandardB()+" ["+((par.getScore()*100)/totvotes)+"%] | ");
                majorvotes+= par.getScore();
            }
            ordinal++;
        }
        int others = totvotes-majorvotes;
        System.out.print("Others ["+((others*100)/(totvotes+1))+"%] | ");
        if(!(winvotes>totvotes/2)){
            candidates.sort(Comparator.comparingInt(Party::getScore).reversed());
            winvotes = -1;
            List<Party> toDelete = new ArrayList<>();
            for(Party par: candidates){
                if(par != candidates.get(0) && par != candidates.get(1)){
                    toDelete.add(par);
                }
            }
            candidates.removeAll(toDelete);
            
            for(Party par: candidates){
            par.resetScore();
        }
        acceptables.clear();
        
        tresh = 55;
        
        for(ideoGroup gro: allGroups){
            for(Party par: candidates){
                if(gro.proximityWith(par)> tresh){
                    if(!acceptables.containsKey(gro)){
                        acceptables.put(gro, par.getPercent()+1);
                    }else{
                        acceptables.put(gro, acceptables.get(gro)+(par.getPercent()+1));
                    }
                }
            }
        }
        
        for (ideoGroup gro : allGroups) {
    boolean hasvoted = false;
    int maxProximity = 0;
    double totalAppealScore = 0;
    Map<Party, Double> partyAppeals = new HashMap<>();

    // 1. Determine who is eligible and find the best match
    for (Party par : allParties) {
        int currentProx = gro.proximityWith(par);
        
        // Track the highest proximity even if they don't vote
        if (currentProx > maxProximity) {
            maxProximity = currentProx;
        }

        if (currentProx > tresh) {
            // Factor in proximity and recognition
            double appeal = currentProx * (1 + (par.getRecognition() / 10.0));
            partyAppeals.put(par, appeal);
            totalAppealScore += appeal;
            hasvoted = true; // They found at least one acceptable party
        }
    }

    // 2. Distribute votes only if there are acceptable parties
    if (hasvoted) {
        for (Party par : partyAppeals.keySet()) {
            double shareOfGroup = partyAppeals.get(par) / totalAppealScore;
            int votesFromThisGroup = (int) (gro.getSize() * shareOfGroup);

            // Apply fatigue 
            votesFromThisGroup -= (votesFromThisGroup * par.getFatigue()) / 100;

            par.addVotes(votesFromThisGroup);
            par.recordVotes(gro, votesFromThisGroup);
        }
    }

    // 3. Calculate Satisfaction
    // If maxProximity is low, satischange will be more negative
    int satischange = -1 * (5 - (maxProximity / 20)); 
    
    if (!hasvoted) {
        // Penalty for having no one to vote for
        satischange -= ra.nextInt(10);
    }
    
    satischange += ra.nextInt(3) - ra.nextInt(3);
    gro.updateSatisfaction(satischange/2);
}
            winvotes = 0;
            totvotes=0;
            for(Party par: candidates){
            if(par.getScore()> winvotes){
                winvotes = par.getScore();
                winner = par;
            }
            totvotes+= par.getScore();
            }
            
            System.out.println("\n\nRound 2");
        for(Party par: candidates){
            
            System.out.print(par.getStandardB()+" ["+((par.getScore()*100)/totvotes)+"%] | ");
        }
        }
        
        for(Party par: candidates){
            par.incrementRecognition();
        }
        President = winner;
        President.incrementRecognition();
        System.out.println("\n===============\nElected President: "+ President.getColor()+President.getName()+RESET+" "+ President.ideoDisplay());
        
    }
    
    public static int IdeoCheck(int toAdd,Party par){
        int lefttresh = 70, righttresh = 30;
        int divileft = (Math.abs(100-par.getIdeology()))/2;
        int diviright = (Math.abs(0-par.getIdeology()))/20;
        int divicenter =(Math.abs(50-par.getIdeology()))/2;
        if(lean.equalsIgnoreCase("Republic")){
                        if(par.getIdeology()>lefttresh && par.getIdeology()< righttresh){
                            toAdd/=divicenter;
                        }else{
                            toAdd*=(100-divicenter)/5;
                        }
                    }else if(lean.equalsIgnoreCase("Reaction")){
                        if(par.getIdeology()>righttresh && par.getIdeology()< lefttresh){
                            toAdd/=diviright;
                        }else{
                            if(par.getIdeology()<righttresh){
                                toAdd*=(100-diviright)/5;
                            }else{
                                toAdd/=diviright;
                            }
                        }
                    }else{
                        if(par.getIdeology()>righttresh && par.getIdeology()< lefttresh){
                            toAdd/=divileft;
                        }else{
                            if(par.getIdeology()<righttresh){
                                toAdd/=divileft;
                            }else{
                                toAdd*=(100-divileft)/10;
                            }
                        }
                    }
                    
                    return toAdd;
    }
    
    
    public static Party President = null;
    public static void electLeadParty(){
        
        int tries = 0;
        boolean got50 = false;
        List<Party> potLeaders = new ArrayList<>(allParties);
        potLeaders.sort(Comparator.comparingInt(p -> 
                100-p.getPercent()
                ));
                
            int partiesInParliament = 0;
            for(Party par: allParties){
                if(par.getPercent()>0){
                    partiesInParliament++;
                }
            }
        for(int i=0; i<potLeaders.size();i++){
            
        Party winner = potLeaders.get(tries);
        
            Coalition gov = new Coalition(winner);
            int totalSeats = winner.getPercent();
            if(totalSeats>50){
                got50 = true;
                rulingCoalition = gov;
            }else{
                List<Party> potentialPartners = new ArrayList<>(allParties);
                potentialPartners.remove(winner);
                potentialPartners.sort(Comparator.comparingInt((Party p)-> 
                
    ((winner.relationWith(p) * 2) - (100-winner.proximityWith(p))) + (p.getPercent()/5) // Higher relations, closer ideology preferred
                ).reversed());
                int down = 0;
                for(Party par: potentialPartners){
                    if(totalSeats>=50){ got50 = true; break;}
                    int tresh = 50+(par.getPercent()/2);
                    tresh += Math.abs(par.getIdeology()-50)/4;
                    tresh += Math.abs(winner.getIdeology()-50)/4;
                    tresh -= down*3;
                    tresh += par.getPercent()/5;
                    if(winner.relationWith(par)< 50){
                        tresh += (50-winner.relationWith(par))*5;
                    }
                    
                    if(winner.relationWith(par)>= 90){
                        tresh -= (winner.relationWith(par)-5)*5;
                    }
                    
                    if(partiesInParliament<5){
                        tresh -= 3* (5-partiesInParliament);
                    }
                    
                    if(partiesInParliament<5){
                        tresh -= 3* (5-partiesInParliament);
                    }
                    if(winner.proximityWith(par)>tresh){
                        gov.addParty(par);
                        totalSeats+=par.getPercent();
                        
                    }
                    down++;
                    
                }
                rulingCoalition = gov;
             
            }
            if(got50) break;
            tries++;
            
        }
        
        int totGovSeats = 0;
                for(Party pra: rulingCoalition.getMemberList())  totGovSeats+=pra.getPercent();
                
                if(totGovSeats <50){
                    Coalition gov = new Coalition(President);
                    List<Party> potentialPartners = new ArrayList<>(allParties);
                potentialPartners.remove(President);
                potentialPartners.sort(Comparator.comparingInt(p -> 
                Math.abs(p.getIdeology() - President.getIdeology())
                ));
                int down = 0;
                int totalSeats = President.getPercent();
                for(Party par: potentialPartners){
                    if(totalSeats>=50){ got50 = true; break;}
                    int tresh = 25+(par.getPercent()/2);
                    tresh += Math.abs(par.getIdeology()-50)/4;
                    tresh += Math.abs(President.getIdeology()-50)/4;
                    tresh -= down*3;
                    tresh += par.getPercent()/5;
                    
                    if(President.proximityWith(par)>tresh){
                        gov.addParty(par);
                        totalSeats+=par.getPercent();
                        
                    }
                    down++;
                    
                }
                rulingCoalition = gov;
                }
                totGovSeats=0;
                for(Party par: rulingCoalition.getMemberList()){
                    totGovSeats+= par.getPercent();
                }
                System.out.println("===============\nGovernment formed by "+ rulingCoalition.getLeader().getColor()+ rulingCoalition.getLeader().getName()+ RESET + rulingCoalition.getLeader().ideoDisplay());
                System.out.println("Seats held by Government: "+ totGovSeats+"%");
                System.out.println("Prime Minister: "+ rulingCoalition.getLeader().getChair());
        for(Party par: rulingCoalition.getMemberList()){
            if(par.getPercent()>0){
                
                
            System.out.print(par.getColor()+"o"+ RESET+ " - "+ par.getName() + par.ideoDisplay()+ " ["+par.getPercent()+"%]");
            
            if(par == rulingCoalition.getLeader()){
                System.out.println(" - Leader");
            }else{
                System.out.println();
            }
            
            //par.incrementRecognition();
            }
            
        }
        LOTO = null;
        Party maxpar = null;
        int maxnum=-10;
        for(Party par: allParties){
            if(par.getPercent()>maxnum && !rulingCoalition.containsParty(par)){
                maxnum = par.getPercent();
                maxpar = par;
            }
        }
        electSpeaker();
        System.out.println("===============\nSpeaker: "+ speaker.getForSpeak()); 
        speaker.incrementRecognition();
        
        
        LOTO = maxpar;
        if(LOTO !=null){
            System.out.println("===============\nLargest Opposition Party: "+ LOTO.getColor()+LOTO.getName() +RESET+LOTO.ideoDisplay()+" ["+ LOTO.getPercent()+"%]");
            LOTO.incrementRecognition();
        }else{
            System.out.println("\nLargest Opposition Party: None");
        }
        if(lean.equalsIgnoreCase("Republic")){
        for(Party par: allParties){
            if(!rulingCoalition.containsParty(par)){
                par.decreaseFatigue();
            }else{
                
                for(int i=0; i<totGovSeats/20;i++){
                    par.addFatigue();
                }
                
            }
            if(par == rulingCoalition.getLeader()){
                
                
                if(par.getPercent()> 50){
                    for(int i=0; i< par.getPercent()/10;i++){
                        par.addFatigue();
                    }
                }
            }
            
            
        }
        }
        
    }
    public static Party LOTO;
    
    
    public static Party speaker;
    public static void electSpeaker(){
        Map<Party, Integer> candidates = new HashMap<>();
        for(Party par: allParties){
            if(par.getPercent()>0){
                candidates.put(par, par.getPercent());
            }
        }
        
        Party winner = null;
        int winseats = 0;
        
        Party loser = null;
        int loseSeats = 1000;
        
        do{
            for(Party par: candidates.keySet()){
                candidates.put(par, par.getPercent());
            }
            
            for(Party par: allParties){
                if(!candidates.keySet().contains(par) && par.getPercent()> 0){
                    int maxres = -100;
                    Party maxpar = null;
                    for(Party pra: candidates.keySet()){
                        int points = par.proximityWith(pra);
                        if(rulingCoalition.getMemberList().contains(par)){
                            points *=2;
                        }
                        points += (par.relationWith(pra)*points)/2;
                        if(par.proximityWith(pra)> maxres){
                            maxres = par.proximityWith(pra);
                            maxpar = pra;
                        }
                    }
                    int cseats = candidates.get(maxpar);
                    candidates.put(maxpar, cseats+par.getPercent());
                }
            }
            
            loseSeats = 1000;
            for(Party par: candidates.keySet()){
                if(candidates.get(par) < loseSeats){
                    loser = par;
                    loseSeats = candidates.get(par);
                }
            }
            if(candidates.size() >1){
                candidates.remove(loser);
            }
            
            winseats = -1;
            for(Party par: candidates.keySet()){
                //System.out.println(candidates.get(par));
                if(candidates.get(par) > winseats){
                    winseats = candidates.get(par);
                    winner = par;
                }
            }
            
        }while(winseats<=50);
        
        speaker = winner;
        
    }
    
    
    public static void checkForNewParties() {
    for (ideoGroup gro : allGroups) {
        
        if (gro.getSatisfaction() < 100/allParties.size()) {
            
            
            boolean alreadyRepresented = false;
            for (Party par : allParties) {
                if (Math.abs(par.getIdeology() - gro.getIdeology()) < 10) {
                    alreadyRepresented = true;
                    break;
                }
            }

            
            if (!alreadyRepresented&& !gro.hasGroupSplintered()) {
                String newName = gro.getSplinterName();
                allParties.add(new Party(newName, gro.getIdeology(), true, assignColor(gro.getIdeology())));
                System.out.println("!!! NEW PARTY FORMED: " + newName + " !!!");
                gro.toggleSplinter();
                
                gro.updateSatisfaction(40); 
            }
        }
    }
}

public static String assignColor(int ideo){
    int r = 0, g = 0, b = 0;
    int partyId = ra.nextInt(500);
    
    if (ideo < 25) {        
        r = 20; g = 20; b = 150;
    } else if (ideo < 40) { 
        r = 50; g = 100; b = 255;
    } else if (ideo < 55) { 
        r = 255; g = 215; b = 0;
    } else if (ideo < 75) { 
        r = 255; g = 90; b = 50; 
    } else {                
        r = 200; g = 0; b = 0;
    }

    int variance = (partyId * 12345) % (ra.nextInt(200)+1); 
    
    r = Math.max(0, Math.min(255, r + (partyId % 3 == 0 ? variance : -variance)));
    g = Math.max(0, Math.min(255, g + (partyId % 3 == 1 ? variance : -variance)));
    b = Math.max(0, Math.min(255, b + (partyId % 3 == 2 ? variance : -variance)));

    return String.format("\u001B[38;2;%d;%d;%dm", r, g, b);

}

public static String detIdeo(Party par){
    int ideo = par.getIdeology()/20;
    switch(ideo){
        case 0: return  "Right-Wing";
            
        case 1: return "Center-Right";
            
        case 2:return "Centrist";
            
        case 3: return "Center-Left";
            
        case 4: return "Left-Wing";
        case 5: return "Left-Wing";
        
    }
    return"";
}

public static void checkFails(){
    List<Party> toRemove = new ArrayList<>();
    int losetresh = allParties.size();
    for(Party par: allParties){
        if(par.getPercent()<losetresh){
            par.incrementFail();
        }else{
            par.resetFail();
        }
        
        if(par.getFailCount()>=5){
            
            toRemove.add(par);
            System.out.println(par.getName()+ " Removed!");
        }
    }
    
    allParties.removeAll(toRemove);
    
    
}


public static void events(){
    boolean eventHappened = false;
    if(ra.nextInt(10)<5){
       
        switch(ra.nextInt(8)){
            case 0:
                System.out.println("Economic Crisis!");
                approvalRatingChange -= ra.nextInt(5);
            for(ideoGroup gro : allGroups){
                if(gro.getIdeology()> 80 || gro.getIdeology()< 20){
                    
                    gro.updateSatisfaction(-1*ra.nextInt(25));
                }
                radicalizeVoters();
            }
                break;
            case 1:
                System.out.println("Economic Boom!");
                approvalRatingChange += ra.nextInt(5);
            for(ideoGroup gro : allGroups){
                
                if(gro.getIdeology()< 80 || gro.getIdeology()> 20){
                    gro.updateSize(ra.nextInt((gro.getSize()/10)+1));
                    
                }
                moderateVoters();
            }
                break;
            case 2:
                System.out.println("Labor Strikes!");
            for(ideoGroup gro : allGroups){
                if(gro.getIdeology()> 60){
                    gro.updateSize(ra.nextInt((gro.getSize()/10)+1));
                }
            }
                break;
            case 3:
                System.out.println("Immigration Crisis!");
            for(ideoGroup gro : allGroups){
                if(gro.getIdeology()< 40){
                    gro.updateSize(ra.nextInt((gro.getSize()/10)+1));
                }
            }
                break;
            case 4:
                double totalRecog = 0;
for(Party p : allParties) totalRecog += p.getRecognition();
if(totalRecog > 0.5){
                System.out.println("Populist Wave!");
                for(Party par : allParties) {
       
        if (par.getRecognition() > 0.1) {
            par.setRecog(par.getRecognition()/10); 
            for(int i=0; i<par.getPercent()/2;i++){
                par.addFatigue();
            }
        } else {
            
            
        }
        
        
    }
   
    for(Party member : rulingCoalition.getMemberList()) {
        for(ideoGroup gro : allGroups) {
            if(gro.proximityWith(member) > 70) {
                gro.updateSatisfaction(-10);
            }
        }
    }
    
        }
        
            break;
            
            case 5:
                    Party targetpar = allParties.get(ra.nextInt(allParties.size()));
                    System.out.println("Political Scandal in "+ targetpar.getName()+ "!");
                    for(int i=0; i<targetpar.getPercent()/2;i++){
                        targetpar.addFatigue();
                    }
                    for(ideoGroup gro : allGroups){
                        if(gro.proximityWith(targetpar)>85){
                            gro.updateSatisfaction(-50);
                        }
                    }
            break;
            case 6:
                targetpar = allParties.get(ra.nextInt(allParties.size())); 
                int supSeats = 0;
                int tresh = 75;
                for(Party par : allParties){
                    supSeats+= (par.getPercent()*par.proximityWith(targetpar))/100;
                    
                }
                
                if(supSeats<50){
                    System.out.println("Landmark bill by "+ targetpar.getName());
                    targetpar.incrementRecognition();
                }
                break;
            default:
            
        }
    }
}

public static ideoGroup findClosestGroup(int toFind){
    int maxnum = 0;
    ideoGroup maxGroup=null;
    for(ideoGroup gro: allGroups){
        int curscore = 100-Math.abs(gro.getIdeology()-toFind);
        if(curscore> maxnum){
            maxnum = curscore;
            maxGroup = gro;
        }
    }
    return maxGroup;
}

public static void moderateVoters() {
    for (ideoGroup gro : allGroups) {
        // if satisfaction high 
        if (gro.getSatisfaction() > 70) {
            int moderates = gro.getSize() / 15; // move toward center
            gro.updateSize(-moderates);
            
            ideoGroup target=null;
            
            if (gro.getIdeology() > 60) {
                target = findClosestGroup(gro.getIdeology() - 15);
            } else if(gro.getIdeology()<40) {
                target = findClosestGroup(gro.getIdeology() + 15);
            }
            
            if (target != null) target.updateSize(moderates);
        }
    }
}

public static void radicalizeVoters() {
    for (ideoGroup gro : allGroups) {
      
        if (gro.getSatisfaction() < 25) {
            int defectors = gro.getSize() / 20; // 5% leave
            gro.updateSize(-defectors);
            
            
            ideoGroup target = null;
            if(gro.getIdeology()<60 && gro.getIdeology()> 40){
                if (gro.getIdeology() < 50) {
                    target = findClosestGroup(gro.getIdeology() + 15);
                } else {
                    target = findClosestGroup(gro.getIdeology() - 15);
                }
                if (target != null) target.updateSize(defectors);
            }
        }
    }
}

public static void updateRels(){
    for(Party par: allParties){
        par.updateRelations();
    }
}

public static void genSatis(){
    if(allParties.size()<4){
        int numOfPars = allParties.size();
        for(ideoGroup gro: allGroups){
            int toRem = (ra.nextInt(10)*(4-numOfPars))*-1;
            gro.updateSatisfaction(toRem);
        }
        
        
    }
    
}
    
    public static void updateTick(){
        events();
        checkFails();
        updateGroupSize();
        genSatis();
        //radicalizeVoters();
        checkForNewParties();
        updateRels();
        checkForActives();
        checkForInactives();
        assessAffiliations();
        assessProminence();
        allDetLeadership();
        
        approvalRatingChange = ra.nextInt(5)-ra.nextInt(10);
        for(Party par: rulingCoalition.getMemberList()){
            approvalRatingChange*= (ra.nextInt(3))+1;
            par.updateApproval(approvalRatingChange);
            
            par.ideoDrift();
        }
        
        Collections.sort(allParties, Comparator.comparingInt(Party::getIdeology));
    }
    
    
    public static class Archive{
        String name;
        int start, end;
        public Archive(String name, int start, int end){
            this.name = name;
            this.start = start;
            this.end = end;
        }
        
        @Override
        public String toString(){
            return name + " "+start+"-"+end;
        }
    }
    
    
    public static List<Archive> leaderArchive = new ArrayList<>();
    
    //color set 
    public static int NAVYBLUE = 18;
    public static int BLUE = 27;
    public static int ORANGE = 214;
    public static int YELLOW = 226;
    public static int LIGHTRED = 203;
    public static int RED = 196;
    public static int DARKRED = 88;
    public static int GREEN = 10;
    
    public static String getDynamicColor(int ideo) {
    int colorCode;
    String ideoname;
    // RIGHT-WING: Blue/Navy spectrum
    if (ideo < 20){ colorCode = NAVYBLUE; ideoname = "Far-Right";       // Navy Blue (Reactionary/Far-Right)
    }else if (ideo < 35){ colorCode = BLUE; ideoname= "Right-Wing";  // Royal Blue (Conservative)
    
    // CENTER: Yellow/Gold/Orange spectrum
    }else if (ideo < 45){ colorCode = ORANGE; ideoname = "Center-Right"; // Orange-Yellow (Liberal/Center-Right)
    }else if (ideo < 55){ colorCode = YELLOW; ideoname = "Centrist"; // Bright Yellow (Pure Centrist)
    }else if (ideo < 65){ colorCode = LIGHTRED; ideoname = "Center-Left";// Light Red (Center-Left/Green)
    
    // LEFT-WING: Red/Crimson spectrum
    //else if (ideo < 80) colorCode = 203; // Light Red (Social Democrat)
    }else if (ideo < 80){ colorCode = RED; ideoname = "Left-Wing"; // Pure Red (Socialist)
    }else{ colorCode = DARKRED; ideoname = "Far-Left";                // Dark Crimson (Communist/Far-Left)
    }
    return "\u001B[38;5;" + colorCode + "m" + ""+ ideoname +RESET;
}
public static final String RESET = "\u001B[0m";
public static final String RESETBG = "\u001B[0m";
public static final String MEMBERBG = "\u001B[47m";
    
    public static void visualizeParliamentOld() {
    Collections.sort(allParties, Comparator.comparingInt(Party::getIdeology));

    System.out.println("\n      --- THE NATIONAL ASSEMBLY ---");
    
    List<String> allSeats = new ArrayList<>();
    List<String> oppoSeats = new ArrayList<>();
    for (Party par : allParties) {
        String color = getDynamicColor(par.getIdeology());
        for (int i = 0; i < par.getPercent(); i++) {
            if(rulingCoalition.containsParty(par)){
                allSeats.add(color + "o" + RESET);
            }else{
                oppoSeats.add(color + "o" + RESET);
            }
        }
    }

    
    while (allSeats.size() < 100) allSeats.add("·");

    for (int i = 0; i < allSeats.size(); i++) {
        System.out.print(allSeats.get(i) + " ");
        if ((i + 1) % 10 == 0) System.out.println(); 
    }
    System.out.println("\n");
for (int i = 0; i < oppoSeats.size(); i++) {
        System.out.print(oppoSeats.get(i) + " ");
        if ((i + 1) % 10 == 0) System.out.println(); 
    }
    System.out.println("-------------------------------------");
    
    for (Party par : allParties) {
        if (par.getPercent() > 0) {
            System.out.print(getDynamicColor(par.getIdeology()) + "o " + RESET 
                + par.getName() + " [" + par.getPercent() + "%]  ");
        }
    }
    System.out.println("\n");
}

public static void visualizeParliament() {
    Collections.sort(allParties, Comparator.comparingInt(Party::getIdeology));

    List<String> allSeats = new ArrayList<>();
    for (Party par : allParties) {
        String symbol = rulingCoalition.containsParty(par) ? "o" : "-";
        for (int i = 0; i < par.getPercent(); i++) {
            allSeats.add(par.getColor() + symbol + RESET);
        }
    }
    while (allSeats.size() < 100) allSeats.add("·");

    int rows = 10;
    int cols = 40; 
    String[][] canvas = new String[rows][cols];
    for (String[] row : canvas) Arrays.fill(row, " ");

    int seatIndex = 0;
    int totalSteps = 20; 
    int rings = 5;      

    
    for (int s = 0; s < totalSteps; s++) {
        double angle = (s * (Math.PI / (totalSteps - 1)));

        for (int ring = 0; ring < rings; ring++) {
            if (seatIndex >= allSeats.size()) break;

            double r = 10.0 - (ring * 1.3); 

            int x = (int) Math.round(cols / 2.0 + (r * Math.cos(angle) * 1.575));
            int y = (int) Math.round(rows - 1 - (r * Math.sin(angle))*0.855);

            if (y >= 0 && y < rows && x >= 0 && x < cols) {
                canvas[y][x] = allSeats.get(seatIndex);
            }
            seatIndex++;
        }
    }
    
    

    System.out.println("\n      --- THE NATIONAL ASSEMBLY ---");
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            System.out.print(canvas[i][j]);
        }
        System.out.println();
    }

    System.out.println("---------------------------------------------");
    for (int i = allParties.size() - 1; i >= 0; i--) {
        Party par = allParties.get(i);
        if (par.getPercent() > 0) {
            System.out.print(par.getColor() + "o " + RESET 
                + par.getName() + " [" + par.getPercent() + "%]  ");
        }
    }
    System.out.println("\n");
}

public static void passageRate(){
    String[] plots = new String[20];
    int supseats=0;
    int i=100;
    int index =0;
    while(i>0){
        supseats=0;
        for(Party par: allParties){
            
            supseats+= ((par.proximityWith(i)-20)*par.getPercent())/100;
            
        }
        
        if(supseats>50){
            plots[index] = "\u001B[38;5;"+GREEN+ "moo"+RESET;
        }else{
            plots[index] = "\u001B[38;5;"+RED+ "moo"+RESET;
        }
        i-=5;
        index++;
        //System.out.println(supseats);
    }
    
    System.out.print("Bill Passage Rate:");
    for(int p=0; p<20;p++){
        System.out.print(plots[p]);
    }
    System.out.println("");
    
}

public static void displayRegionResults(){
    Region North=null, South=null, East=null, West=null, Capital=null;
    for(Region reg: allRegions){
        reg.displayWinner();
        if(reg.getName().equalsIgnoreCase("Northern")){
            North = reg;
        }
        if(reg.getName().equalsIgnoreCase("Southern")){
            South = reg;
        }
        if(reg.getName().equalsIgnoreCase("Eastern")){
            East = reg;
        }
        if(reg.getName().equalsIgnoreCase("Western")){
            West = reg;
        }
        if(reg.getName().equalsIgnoreCase("Capital")){
            Capital = reg;
        }
    }
    
    /*int rows = mapProgside.length; WIP MAP SYSTEM
int cols = mapProgside[0].length;

for (int i = 0; i < rows; i++) {
    for (int c = 0; c < cols; c++) {
        String tile = mapProgside[i][c].toUpperCase();
        
        // Determine the color/symbol based on the ID
        String displayChar = switch (tile) {
            case "N" -> North.getWinner().getColor() + "o" + RESET;
            case "C" -> Capital.getWinner().getColor() + "o" + RESET;
            case "S" -> South.getWinner().getColor() + "o" + RESET;
            case "E" -> East.getWinner().getColor() + "o" + RESET;
            case "W" -> West.getWinner().getColor() + "o" + RESET;
            default  -> BLACK+ " "+ RESET;
        };

        System.out.print(displayChar + " ");
    }
    System.out.println(); // cleaner than System.out.print("\n")*/
}
    
    
    

public static String BLACK = "\u001B[30m";
public static String lean = "Republic";
public static void nationalLean(){
    int reaction =0,republic =0, revolution=0;
    int total =0;
    for(ideoGroup gro : allGroups){
        if(gro.getIdeology() <= 30){
            reaction+= gro.getSize();
        } else if(gro.getIdeology()>=70 ){
            revolution+=gro.getSize();
        }else{
            republic += gro.getSize();
        }
    }
    if(rulingCoalition.getMemberList().size() == 1 && rulingCoalition.getLeader().getPercent()>= 65){
    if(rulingCoalition.getLeader().getIdeology() <= 30){
            reaction+= reaction/2;
        } else if(rulingCoalition.getLeader().getIdeology()>=70 ){
            revolution+=revolution/2;
        }else{
            republic += republic/2;
        }
    }
     
            if(President.getIdeology() <= 25){
                    reaction+= reaction/3;
                } else if(President.getIdeology()>=75 ){
                    revolution+=revolution/3;
                }else{
                    republic += republic/3;
                }
        if(speaker.getIdeology() <= 25){
                    reaction+= reaction/5;
                } else if(speaker.getIdeology()>=75 ){
                    revolution+=revolution/5;
                }else{
                    republic += republic/5;
                }
        
    int leftistseats =0, rightistseats=0;
    for(Party par: allParties){
        if(par.getIdeology()<=35){
            rightistseats+=par.getPercent();
        }else if(par.getIdeology()>=65){
            leftistseats+=par.getPercent();
        }
    }
    
    if(rightistseats>= 50){
        reaction+= (reaction/5)* (((rightistseats-50)/10)+1);
    }
    if(leftistseats>= 50){
        revolution+=(revolution/5)* (((leftistseats-50)/10)+1);
    }
    
    System.out.print("The Nation leans towards");
    if(reaction> republic +revolution){
        System.out.println("\u001B[38;5;18m Reaction \u001B[0m");
        lean = "Reaction";
    }else if(republic>= reaction+revolution){
        System.out.println("\u001B[38;5;226m Republic \u001B[0m");
        lean = "Republic";
    }else if(revolution> reaction+republic){
        System.out.println("\u001B[38;5;88m Revolution \u001B[0m");
        lean = "Revolution";
    }else{
        System.out.println("\u001B[38;5;226m Republic \u001B[0m");
        lean = "Republic";
    }
}

public static void seeDominant(){
    int maxnum=0;
    Party maxpar = null;
    // Dominant on right 
    for(Party par: allParties){
        if(par.getIdeology()<=35){
            if(par.getPercent()> maxnum){
                maxnum = par.getPercent();
                maxpar = par;
            }
        }
    }
    
    System.out.print("Largest Party on the Right: ");
    
    if(maxpar != null){
        System.out.println(maxpar.getColor()+ maxpar.getName() + RESET+ maxpar.ideoDisplay());
    }else{
        System.out.println("None");
    }
    maxnum = 0;
    maxpar = null;
    // Dominant on center 
    for(Party par: allParties){
        if(par.getIdeology()>35 && par.getIdeology()<65){
            if(par.getPercent()> maxnum){
                maxnum = par.getPercent();
                maxpar = par;
            }
        }
    }
    
    System.out.print("Largest Party on the Center: ");
    if(maxpar != null){
        System.out.println(maxpar.getColor()+ maxpar.getName()+RESET+ maxpar.ideoDisplay());
    }else{
        System.out.println("None");
    }
    maxnum = 0;
    maxpar = null;
    // Dominant on left 
    for(Party par: allParties){
        if(par.getIdeology()>= 65){
            if(par.getPercent()> maxnum){
                maxnum = par.getPercent();
                maxpar = par;
            }
        }
    }
    
    System.out.print("Largest Party on the Left: ");
    if(maxpar != null){
        System.out.println(maxpar.getColor()+ maxpar.getName()+RESET+ maxpar.ideoDisplay());
    }else{
        System.out.println("None");
    }
}
    
    
    
	public static void main(String[] args) {
	    addGroups();
	    addParties();
	    addRegions();
	    addPersons();
	    checkForActives();
	    assessAffiliations();
        assessProminence();
        allDetLeadership();
		
		int interval  =4;
		int electionsToSimulate = 44;
		
		for(int i=0; i<electionsToSimulate;i++){
		    System.out.println(year+ "=========================");
		    electPresident();
		    election();
		    electLeadParty();
		    
		    
		    
		    //System.out.println("Winner: "+ rulingCoalition.getLeader().getName());
		    
		    /*for(Party par: allParties){
		        System.out.println(par.getName()+ " "+ par.getPercent()+"%");
		        System.out.println("Ideology: "+ detIdeo(par));
		        //System.out.println(par.getRecognition());
		        System.out.println("====================");
		    }
		    System.out.println("\n");*/
		    
		    char[] spectrum = new char[21];
    Arrays.fill(spectrum, '-');
    for (Party p : allParties) {
        int index = p.getIdeology() / 5;
        if (p.getPercent() > 20) spectrum[index] = 'X'; // maj Party
        else if (p.getPercent() > 5) spectrum[index] = 'o'; // min Party
    }
    
    visualizeParliament();
    displayRegionResults();
    String spectr =  new StringBuilder(String.valueOf(spectrum)).reverse().toString();
    System.out.println("Spectrum: [L] " + spectr + " [R]");
		    for(ideoGroup gro : allGroups){
		        //System.out.println(gro.getName()+ " "+ gro.getSize() + " "+ gro.getSatisfaction());
		    }
		    double totalRecog = 0;
for(Party p : allParties) totalRecog += p.getRecognition();
System.out.println("Establishment Strength: " + String.format("%.2f", totalRecog));
passageRate();
nationalLean();
seeDominant();

		    sc.nextLine();
		    updateTick();
		    year+=interval;
		    
		}
		
	}
}
