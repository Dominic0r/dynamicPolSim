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
            
            for(Person per: memberPersons){
                int points= per.getProminence();
                if(points> maxnum && per!=standardBearer){
                    maxnum = points;
                    maxper = per;
                }
            }
            chairman = maxper;
            
            maxnum = Integer.MIN_VALUE;
            
            for(Person per: memberPersons){
                int points= per.getProminence();
                if(points> maxnum && per!=standardBearer && per!=chairman){
                    maxnum = points;
                    maxper = per;
                }
            }
            forSpeaker = maxper;
        }
        
        public int memCount(){
            List<Person> memberPersons = new ArrayList();
            for(Person per: activePersons){
                if(per.getCurrentParty() == this){
                    memberPersons.add(per);
                }
            }
            return memberPersons.size();
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
                int points = proximityWith(par)- (par.memCount()*5);
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
        allPersons.add(new Person("Darrell Clark",1837,1850,3));
allPersons.add(new Person("Lawrence Gibson",1840,1857,2));
allPersons.add(new Person("Isaac Jensen",1839,1859,7));
allPersons.add(new Person("Gregory Greene",1839,1853,6));
allPersons.add(new Person("Timothy Graham",1837,1854,5));
allPersons.add(new Person("Jonathan Dawson",1840,1857,13));
allPersons.add(new Person("Cody Clayton",1840,1850,12));
allPersons.add(new Person("Joshua Chase",1836,1859,13));
allPersons.add(new Person("Henry Curtis",1839,1857,13));
allPersons.add(new Person("Maxwell Butler",1839,1858,16));
allPersons.add(new Person("Luke Byrd",1839,1857,21));
allPersons.add(new Person("Earl Fuller",1837,1857,27));
allPersons.add(new Person("Glen Johnson",1836,1855,25));
allPersons.add(new Person("Colin Craig",1838,1855,20));
allPersons.add(new Person("Ryan Brooks",1837,1854,20));
allPersons.add(new Person("Maurice Holland",1836,1853,30));
allPersons.add(new Person("Wesley Kennedy",1839,1852,36));
allPersons.add(new Person("Paul Barnett",1837,1851,34));
allPersons.add(new Person("Caleb Fletcher",1836,1852,37));
allPersons.add(new Person("Jason Griffith",1839,1854,37));
allPersons.add(new Person("Evan Bates",1838,1858,45));
allPersons.add(new Person("James Graham",1839,1859,40));
allPersons.add(new Person("Caleb Baker",1837,1852,41));
allPersons.add(new Person("Gordon Fraser",1837,1854,45));
allPersons.add(new Person("Philip Harper",1837,1850,45));
allPersons.add(new Person("Noah Barnett",1836,1850,53));
allPersons.add(new Person("Gerald Lee",1838,1858,54));
allPersons.add(new Person("Gerald Hale",1837,1858,51));
allPersons.add(new Person("Arthur Hoffman",1838,1858,57));
allPersons.add(new Person("Jonathan Campbell",1838,1851,56));
allPersons.add(new Person("Russell Ford",1838,1856,64));
allPersons.add(new Person("Justin Kerr",1840,1852,62));
allPersons.add(new Person("Mitchell Hardy",1838,1853,67));
allPersons.add(new Person("Benjamin Lane",1838,1850,60));
allPersons.add(new Person("Evan Greene",1839,1855,62));
allPersons.add(new Person("Joshua Davidson",1839,1857,71));
allPersons.add(new Person("Arthur Grant",1838,1855,71));
allPersons.add(new Person("Clayton Horton",1838,1853,70));
allPersons.add(new Person("Ronald Logan",1840,1857,72));
allPersons.add(new Person("Harold Holmes",1838,1852,76));
allPersons.add(new Person("Bruce Mackenzie",1836,1851,86));
allPersons.add(new Person("Bernard Dixon",1837,1853,82));
allPersons.add(new Person("Oscar Fox",1839,1855,84));
allPersons.add(new Person("Frederick Jackson",1836,1854,82));
allPersons.add(new Person("Harold Elliott",1836,1858,82));
allPersons.add(new Person("Lucas Hopkins",1840,1853,90));
allPersons.add(new Person("Vincent Hodges",1840,1858,93));
allPersons.add(new Person("Mark Barker",1838,1857,92));
allPersons.add(new Person("Martin Lawson",1839,1851,90));
allPersons.add(new Person("Brian Cox",1836,1851,91));
allPersons.add(new Person("Dennis Evans",1847,1861,3));
allPersons.add(new Person("David Cox",1847,1867,7));
allPersons.add(new Person("Scott Mackenzie",1850,1865,1));
allPersons.add(new Person("Bryan Curtis",1847,1869,7));
allPersons.add(new Person("Arthur Collins",1846,1860,3));
allPersons.add(new Person("Ernest Hampton",1846,1862,11));
allPersons.add(new Person("Mark Jacobs",1849,1868,16));
allPersons.add(new Person("Noah Davis",1849,1868,15));
allPersons.add(new Person("Hunter Bishop",1850,1868,11));
allPersons.add(new Person("Derek Fraser",1847,1865,15));
allPersons.add(new Person("Dale Ingram",1846,1867,20));
allPersons.add(new Person("Jason Bowman",1849,1869,20));
allPersons.add(new Person("Samuel Dutton",1846,1869,20));
allPersons.add(new Person("Harry Jones",1848,1869,22));
allPersons.add(new Person("Walter Fletcher",1850,1868,20));
allPersons.add(new Person("Keith Barrett",1847,1866,33));
allPersons.add(new Person("Logan Clayton",1848,1867,30));
allPersons.add(new Person("Howard Cross",1848,1869,30));
allPersons.add(new Person("Harold Lynch",1848,1860,37));
allPersons.add(new Person("Jacob Grant",1846,1869,36));
allPersons.add(new Person("Arthur Hammond",1848,1869,42));
allPersons.add(new Person("Joshua Jackson",1848,1862,47));
allPersons.add(new Person("Bernard Daniel",1846,1861,40));
allPersons.add(new Person("Jesse Fletcher",1847,1866,47));
allPersons.add(new Person("Dennis Barnes",1846,1862,46));
allPersons.add(new Person("Glen Coleman",1846,1868,57));
allPersons.add(new Person("Harold Andrews",1850,1865,56));
allPersons.add(new Person("Thomas Bell",1847,1865,56));
allPersons.add(new Person("Harold Long",1849,1866,54));
allPersons.add(new Person("Logan Dunn",1850,1860,51));
allPersons.add(new Person("Ralph Cole",1848,1862,67));
allPersons.add(new Person("Norman Fitzgerald",1850,1868,65));
allPersons.add(new Person("Henry Howell",1848,1860,67));
allPersons.add(new Person("Wesley Evans",1849,1869,63));
allPersons.add(new Person("Gordon Daniel",1847,1866,60));
allPersons.add(new Person("Tyler Gardner",1847,1866,74));
allPersons.add(new Person("Charles Bradley",1847,1860,77));
allPersons.add(new Person("Kenneth Abbott",1848,1862,72));
allPersons.add(new Person("Albert Dixon",1849,1864,74));
allPersons.add(new Person("Victor Hodges",1847,1866,76));
allPersons.add(new Person("Warren Long",1850,1864,87));
allPersons.add(new Person("George Gardner",1846,1867,80));
allPersons.add(new Person("Cody Long",1848,1868,84));
allPersons.add(new Person("Maurice Graham",1850,1867,82));
allPersons.add(new Person("Gary Day",1850,1866,81));
allPersons.add(new Person("Clayton Arnold",1850,1860,93));
allPersons.add(new Person("Gerald Kent",1846,1867,94));
allPersons.add(new Person("Glen Berry",1848,1861,90));
allPersons.add(new Person("Jacob King",1850,1865,92));
allPersons.add(new Person("Henry Lowe",1846,1863,95));
allPersons.add(new Person("Daniel Clements",1857,1877,0));
allPersons.add(new Person("Jack Gray",1857,1871,4));
allPersons.add(new Person("Francis Collins",1860,1874,5));
allPersons.add(new Person("Kevin Jackson",1860,1877,1));
allPersons.add(new Person("Caleb Hudson",1858,1871,3));
allPersons.add(new Person("Franklin Graham",1859,1873,14));
allPersons.add(new Person("Douglas Howell",1856,1871,14));
allPersons.add(new Person("Carl Cook",1859,1876,17));
allPersons.add(new Person("Gabriel Drake",1860,1874,17));
allPersons.add(new Person("Sean Chapman",1857,1877,17));
allPersons.add(new Person("Caleb Bailey",1860,1873,27));
allPersons.add(new Person("Evan Fraser",1858,1871,21));
allPersons.add(new Person("Gordon Gross",1858,1877,21));
allPersons.add(new Person("Austin Cole",1860,1873,26));
allPersons.add(new Person("Chad Holmes",1856,1872,27));
allPersons.add(new Person("Gerald Butler",1857,1870,36));
allPersons.add(new Person("Logan Ferguson",1857,1875,35));
allPersons.add(new Person("Steven Alexander",1858,1875,34));
allPersons.add(new Person("Thomas Chambers",1860,1871,36));
allPersons.add(new Person("Edward Ford",1856,1872,31));
allPersons.add(new Person("Clayton Logan",1857,1872,40));
allPersons.add(new Person("Danny Harding",1860,1873,40));
allPersons.add(new Person("Hunter Dixon",1859,1874,42));
allPersons.add(new Person("Noah Howell",1860,1877,41));
allPersons.add(new Person("Mitchell Lyons",1856,1877,47));
allPersons.add(new Person("Shane Barnes",1859,1873,52));
allPersons.add(new Person("Richard Henderson",1856,1874,53));
allPersons.add(new Person("Lawrence Long",1860,1871,56));
allPersons.add(new Person("Jason Hart",1856,1875,57));
allPersons.add(new Person("Jesse Fisher",1857,1873,57));
allPersons.add(new Person("Glen Lawrence",1857,1876,60));
allPersons.add(new Person("Vincent Jenkins",1858,1873,60));
allPersons.add(new Person("Herbert Freeman",1858,1876,67));
allPersons.add(new Person("Mitchell Dawson",1856,1879,61));
allPersons.add(new Person("Wesley Haynes",1856,1879,61));
allPersons.add(new Person("Alan Fleming",1856,1875,71));
allPersons.add(new Person("Mitchell Hall",1858,1875,70));
allPersons.add(new Person("Brandon Barnett",1858,1872,75));
allPersons.add(new Person("Stephen Cooper",1858,1872,72));
allPersons.add(new Person("Paul Cox",1859,1876,73));
allPersons.add(new Person("Thomas Harvey",1856,1873,84));
allPersons.add(new Person("Bryan Kaufman",1859,1878,87));
allPersons.add(new Person("Terrence Lucas",1859,1877,81));
allPersons.add(new Person("Austin Holloway",1859,1876,82));
allPersons.add(new Person("Victor Franklin",1859,1876,85));
allPersons.add(new Person("Cody Coleman",1856,1873,96));
allPersons.add(new Person("Alan Hogan",1857,1873,93));
allPersons.add(new Person("Tyler Daniel",1860,1874,94));
allPersons.add(new Person("Jesse Dunn",1856,1878,94));
allPersons.add(new Person("Franklin Hawkins",1859,1876,92));
allPersons.add(new Person("Jeffrey Higgins",1866,1888,2));
allPersons.add(new Person("Victor Giles",1868,1882,2));
allPersons.add(new Person("Harvey Cooper",1870,1888,0));
allPersons.add(new Person("Raymond Lewis",1870,1885,3));
allPersons.add(new Person("Jack Blair",1866,1884,4));
allPersons.add(new Person("Bernard Hamilton",1866,1883,12));
allPersons.add(new Person("Danny Kennedy",1870,1888,15));
allPersons.add(new Person("Brandon Case",1870,1888,17));
allPersons.add(new Person("Kevin Bennett",1867,1889,17));
allPersons.add(new Person("Shawn Burgess",1867,1881,13));
allPersons.add(new Person("Lawrence Jennings",1866,1884,26));
allPersons.add(new Person("Jeffrey Carr",1869,1880,20));
allPersons.add(new Person("Chad James",1867,1880,22));
allPersons.add(new Person("Norman Glass",1867,1884,20));
allPersons.add(new Person("Jerome Grant",1868,1887,24));
allPersons.add(new Person("Howard Alexander",1866,1880,32));
allPersons.add(new Person("Ross Jordan",1868,1882,35));
allPersons.add(new Person("Gordon Cook",1867,1884,37));
allPersons.add(new Person("Patrick Horton",1870,1882,31));
allPersons.add(new Person("Donald Jennings",1868,1882,31));
allPersons.add(new Person("Mitchell Collins",1867,1887,46));
allPersons.add(new Person("Dennis Chapman",1868,1888,45));
allPersons.add(new Person("Jonathan Byrd",1868,1882,41));
allPersons.add(new Person("Shane Baker",1867,1881,47));
allPersons.add(new Person("Jack Carroll",1868,1880,42));
allPersons.add(new Person("Michael Arnold",1869,1885,51));
allPersons.add(new Person("Lewis Henderson",1867,1882,53));
allPersons.add(new Person("Dennis Abbott",1867,1881,56));
allPersons.add(new Person("Glen Atkinson",1870,1881,53));
allPersons.add(new Person("Adrian Barnett",1870,1885,54));
allPersons.add(new Person("Christopher Dunn",1869,1883,66));
allPersons.add(new Person("Edward Lloyd",1870,1882,63));
allPersons.add(new Person("Harold Macdonald",1870,1882,60));
allPersons.add(new Person("Patrick Jones",1866,1882,67));
allPersons.add(new Person("Terrence Day",1869,1883,60));
allPersons.add(new Person("Gordon Fleming",1866,1883,74));
allPersons.add(new Person("Corey Hale",1870,1882,76));
allPersons.add(new Person("Caleb Hayes",1867,1883,77));
allPersons.add(new Person("Cameron Craig",1868,1887,74));
allPersons.add(new Person("Patrick Ferguson",1869,1881,71));
allPersons.add(new Person("Kenneth Fisher",1867,1883,86));
allPersons.add(new Person("Justin Harris",1868,1888,84));
allPersons.add(new Person("Nathan Barnett",1868,1880,86));
allPersons.add(new Person("Adam Drake",1867,1888,86));
allPersons.add(new Person("Craig Henderson",1869,1883,85));
allPersons.add(new Person("Gary Fox",1866,1882,91));
allPersons.add(new Person("Adrian Douglas",1869,1884,97));
allPersons.add(new Person("Francis Andrews",1867,1886,97));
allPersons.add(new Person("Caleb Adkins",1866,1884,96));
allPersons.add(new Person("Caleb Hale",1868,1884,92));
allPersons.add(new Person("Terrence Elliott",1879,1892,5));
allPersons.add(new Person("Aaron Carroll",1878,1897,2));
allPersons.add(new Person("Kenneth Berry",1878,1896,3));
allPersons.add(new Person("Mark Carr",1879,1898,4));
allPersons.add(new Person("Noah Holloway",1877,1890,6));
allPersons.add(new Person("Scott Barnes",1880,1891,15));
allPersons.add(new Person("Eric Alexander",1879,1891,15));
allPersons.add(new Person("Russell Carpenter",1880,1894,12));
allPersons.add(new Person("Walter Baker",1880,1899,11));
allPersons.add(new Person("Corey Fields",1878,1891,12));
allPersons.add(new Person("Benjamin Fleming",1878,1899,20));
allPersons.add(new Person("Shawn Brewer",1880,1896,26));
allPersons.add(new Person("Ethan Hale",1879,1897,22));
allPersons.add(new Person("Samuel Kelly",1880,1892,27));
allPersons.add(new Person("Louis Kerr",1880,1892,21));
allPersons.add(new Person("Harry Carter",1876,1893,31));
allPersons.add(new Person("Carl Burke",1878,1895,37));
allPersons.add(new Person("John Chapman",1879,1892,36));
allPersons.add(new Person("Blake Cooper",1878,1892,36));
allPersons.add(new Person("Bryan Dawson",1880,1896,37));
allPersons.add(new Person("Alexander Gibson",1877,1899,40));
allPersons.add(new Person("Anthony Craig",1876,1890,42));
allPersons.add(new Person("Charles Case",1876,1899,42));
allPersons.add(new Person("Jesse Kent",1876,1898,47));
allPersons.add(new Person("Benjamin Mackenzie",1879,1899,40));
allPersons.add(new Person("Anthony Hines",1877,1894,53));
allPersons.add(new Person("Joseph Bates",1878,1898,50));
allPersons.add(new Person("Frank Hampton",1877,1897,51));
allPersons.add(new Person("Martin Gates",1880,1896,52));
allPersons.add(new Person("George Long",1879,1893,54));
allPersons.add(new Person("Anthony Campbell",1877,1897,62));
allPersons.add(new Person("Travis Holland",1876,1892,65));
allPersons.add(new Person("Terrence Barrett",1878,1893,60));
allPersons.add(new Person("Louis Gross",1878,1892,63));
allPersons.add(new Person("Charles Logan",1878,1892,63));
allPersons.add(new Person("Cody Case",1877,1899,75));
allPersons.add(new Person("Paul Hunt",1877,1895,73));
allPersons.add(new Person("Gordon Barrett",1878,1898,72));
allPersons.add(new Person("Ronald Barrett",1877,1890,73));
allPersons.add(new Person("Norman Lowe",1879,1895,77));
allPersons.add(new Person("Bruce Chase",1880,1891,81));
allPersons.add(new Person("Jesse Howell",1878,1890,81));
allPersons.add(new Person("Justin Jensen",1879,1891,81));
allPersons.add(new Person("Alexander Adams",1876,1898,84));
allPersons.add(new Person("Wayne Drake",1877,1898,82));
allPersons.add(new Person("Terrence Bush",1880,1892,90));
allPersons.add(new Person("Jesse King",1878,1898,93));
allPersons.add(new Person("William Lewis",1878,1899,91));
allPersons.add(new Person("Charles Johnson",1878,1898,95));
allPersons.add(new Person("Adam Hall",1879,1893,92));
allPersons.add(new Person("Frank Gibson",1889,1901,0));
allPersons.add(new Person("Frederick Elliott",1889,1904,3));
allPersons.add(new Person("Kyle Carr",1889,1900,0));
allPersons.add(new Person("Ethan Bush",1889,1906,4));
allPersons.add(new Person("Samuel Bell",1887,1901,6));
allPersons.add(new Person("Joel Jones",1887,1906,14));
allPersons.add(new Person("Cody Hammond",1886,1907,11));
allPersons.add(new Person("Philip Douglas",1887,1906,10));
allPersons.add(new Person("Albert Foster",1889,1905,15));
allPersons.add(new Person("Vincent Henry",1887,1901,17));
allPersons.add(new Person("James Ball",1887,1907,25));
allPersons.add(new Person("Samuel Clark",1890,1903,20));
allPersons.add(new Person("Timothy George",1890,1908,25));
allPersons.add(new Person("Alan Adkins",1888,1908,23));
allPersons.add(new Person("Maxwell Green",1886,1902,27));
allPersons.add(new Person("Andrew Barnett",1890,1907,31));
allPersons.add(new Person("Maurice Fletcher",1887,1903,31));
allPersons.add(new Person("Gary Green",1888,1904,37));
allPersons.add(new Person("Brandon Hunter",1887,1905,36));
allPersons.add(new Person("Ross Howard",1889,1908,34));
allPersons.add(new Person("Herbert Armstrong",1886,1905,45));
allPersons.add(new Person("Cody Carroll",1889,1905,42));
allPersons.add(new Person("Ryan Holloway",1886,1903,47));
allPersons.add(new Person("Derek Gross",1890,1906,44));
allPersons.add(new Person("Isaac Barnett",1887,1903,46));
allPersons.add(new Person("Lewis Fletcher",1886,1904,55));
allPersons.add(new Person("Cameron Johnson",1889,1902,55));
allPersons.add(new Person("Shane Holt",1887,1906,50));
allPersons.add(new Person("Terrence Bailey",1887,1905,54));
allPersons.add(new Person("Earl Bates",1887,1908,56));
allPersons.add(new Person("Evan Higgins",1888,1905,60));
allPersons.add(new Person("Curtis Lawson",1887,1902,61));
allPersons.add(new Person("Danny Fowler",1888,1909,66));
allPersons.add(new Person("Noah Alexander",1886,1903,60));
allPersons.add(new Person("Robert Freeman",1889,1908,63));
allPersons.add(new Person("Curtis Bowen",1889,1908,72));
allPersons.add(new Person("Wayne Allen",1890,1901,71));
allPersons.add(new Person("Shawn Brady",1887,1906,75));
allPersons.add(new Person("Thomas Beck",1889,1903,70));
allPersons.add(new Person("Curtis Hoffman",1886,1904,73));
allPersons.add(new Person("Eugene Dawson",1890,1902,87));
allPersons.add(new Person("Dean Ford",1890,1903,86));
allPersons.add(new Person("Shane Hammond",1888,1905,80));
allPersons.add(new Person("Cody Harrison",1888,1900,82));
allPersons.add(new Person("Kyle Hardy",1888,1900,84));
allPersons.add(new Person("Stephen Griffith",1889,1906,92));
allPersons.add(new Person("Jordan Fitzgerald",1889,1904,97));
allPersons.add(new Person("Matthew Hampton",1886,1909,94));
allPersons.add(new Person("Alan Fowler",1888,1903,91));
allPersons.add(new Person("Gary Glover",1888,1903,94));
allPersons.add(new Person("Clayton Brewer",1899,1918,7));
allPersons.add(new Person("Mark Hopkins",1900,1919,0));
allPersons.add(new Person("Warren Glass",1900,1916,7));
allPersons.add(new Person("Mark Hogan",1896,1912,3));
allPersons.add(new Person("Howard Harvey",1899,1913,5));
allPersons.add(new Person("Wesley Bradley",1896,1914,13));
allPersons.add(new Person("Herbert Leonard",1898,1915,15));
allPersons.add(new Person("Noah Copeland",1898,1918,12));
allPersons.add(new Person("Carl Boyd",1896,1913,16));
allPersons.add(new Person("Roy Dawson",1898,1917,16));
allPersons.add(new Person("Earl Jennings",1899,1914,26));
allPersons.add(new Person("Bruce Gibson",1898,1918,20));
allPersons.add(new Person("Danny Adams",1900,1914,23));
allPersons.add(new Person("Harvey Fox",1896,1910,25));
allPersons.add(new Person("Lawrence Craig",1900,1910,27));
allPersons.add(new Person("Kevin Hughes",1896,1919,36));
allPersons.add(new Person("Alan Clark",1896,1912,31));
allPersons.add(new Person("Todd Gibson",1898,1917,37));
allPersons.add(new Person("Kyle Holt",1896,1917,33));
allPersons.add(new Person("Shawn Hopkins",1897,1910,36));
allPersons.add(new Person("Daniel Fowler",1896,1911,43));
allPersons.add(new Person("Isaac Gilbert",1897,1911,46));
allPersons.add(new Person("Charles Carr",1896,1911,40));
allPersons.add(new Person("Daniel Coleman",1897,1911,43));
allPersons.add(new Person("Maxwell Hill",1896,1919,47));
allPersons.add(new Person("Andrew Butler",1896,1911,54));
allPersons.add(new Person("Aaron Hubbard",1898,1914,55));
allPersons.add(new Person("Nathan Greene",1899,1913,57));
allPersons.add(new Person("Cameron Hodges",1899,1910,57));
allPersons.add(new Person("Eugene Harding",1898,1915,52));
allPersons.add(new Person("Evan Arnold",1900,1918,60));
allPersons.add(new Person("Stephen Bell",1898,1912,66));
allPersons.add(new Person("Austin Hunt",1897,1918,67));
allPersons.add(new Person("Stanley Dutton",1898,1911,66));
allPersons.add(new Person("Raymond Gilbert",1896,1911,64));
allPersons.add(new Person("Harold Hardy",1896,1916,76));
allPersons.add(new Person("Richard Graham",1897,1919,77));
allPersons.add(new Person("Isaac Holland",1899,1912,77));
allPersons.add(new Person("Christopher Lloyd",1896,1913,70));
allPersons.add(new Person("Cameron Gardner",1896,1913,70));
allPersons.add(new Person("Lucas Glover",1900,1919,86));
allPersons.add(new Person("Vincent Arnold",1899,1917,83));
allPersons.add(new Person("Wayne Long",1900,1912,80));
allPersons.add(new Person("Harry Gray",1899,1912,86));
allPersons.add(new Person("Vincent Cole",1898,1911,84));
allPersons.add(new Person("Clark Duncan",1900,1916,95));
allPersons.add(new Person("Kevin Baker",1898,1919,93));
allPersons.add(new Person("Dustin Horton",1900,1914,90));
allPersons.add(new Person("Edward Lloyd",1897,1918,94));
allPersons.add(new Person("Roy Grant",1898,1912,91));
allPersons.add(new Person("Zachary Doyle",1908,1929,3));
allPersons.add(new Person("Matthew Macdonald",1909,1926,4));
allPersons.add(new Person("Scott Cross",1907,1929,5));
allPersons.add(new Person("Arthur Dunn",1907,1921,5));
allPersons.add(new Person("Dustin Case",1907,1929,4));
allPersons.add(new Person("Jack Evans",1907,1925,16));
allPersons.add(new Person("Norman Abbott",1908,1921,11));
allPersons.add(new Person("Adrian Lynch",1908,1924,15));
allPersons.add(new Person("Caleb Crawford",1908,1928,17));
allPersons.add(new Person("Jeffrey Butler",1906,1925,15));
allPersons.add(new Person("Lawrence Hill",1907,1929,24));
allPersons.add(new Person("Stephen Macdonald",1906,1926,24));
allPersons.add(new Person("Evan Crawford",1907,1920,25));
allPersons.add(new Person("Ernest Gregory",1910,1920,22));
allPersons.add(new Person("Dale Hughes",1908,1924,24));
allPersons.add(new Person("Bernard Lawson",1907,1924,30));
allPersons.add(new Person("Owen Cox",1910,1924,33));
allPersons.add(new Person("Maxwell Hicks",1909,1921,31));
allPersons.add(new Person("Martin Caldwell",1909,1921,37));
allPersons.add(new Person("Adam Duncan",1908,1928,34));
allPersons.add(new Person("Norman Alexander",1909,1922,44));
allPersons.add(new Person("Ross Brown",1906,1921,47));
allPersons.add(new Person("Seth Cole",1910,1925,43));
allPersons.add(new Person("Ethan Adams",1906,1926,46));
allPersons.add(new Person("Thomas Burns",1909,1921,46));
allPersons.add(new Person("Peter Clarke",1906,1920,55));
allPersons.add(new Person("Nathan Fox",1909,1928,54));
allPersons.add(new Person("Chad Bryant",1908,1924,55));
allPersons.add(new Person("Roger Dutton",1907,1922,53));
allPersons.add(new Person("Scott Fisher",1907,1927,56));
allPersons.add(new Person("Steven Fowler",1906,1926,62));
allPersons.add(new Person("Joshua Griffith",1907,1921,62));
allPersons.add(new Person("Norman Bishop",1907,1924,62));
allPersons.add(new Person("Shane Hart",1909,1924,67));
allPersons.add(new Person("Timothy Howell",1908,1929,60));
allPersons.add(new Person("Isaac Henry",1908,1924,71));
allPersons.add(new Person("Jacob Clark",1908,1924,72));
allPersons.add(new Person("Travis Fitzgerald",1910,1929,72));
allPersons.add(new Person("Albert Coleman",1908,1927,72));
allPersons.add(new Person("Herbert Craig",1907,1923,74));
allPersons.add(new Person("Clark Doyle",1908,1926,83));
allPersons.add(new Person("Jordan Burns",1910,1920,82));
allPersons.add(new Person("Arthur Glass",1909,1928,84));
allPersons.add(new Person("Edward Fowler",1908,1924,81));
allPersons.add(new Person("Alan Garrett",1909,1920,82));
allPersons.add(new Person("Joshua Jordan",1908,1923,91));
allPersons.add(new Person("Zachary Graham",1908,1928,92));
allPersons.add(new Person("Adam Hunter",1910,1923,97));
allPersons.add(new Person("Cameron Howell",1907,1920,96));
allPersons.add(new Person("Maxwell Gray",1909,1924,93));
allPersons.add(new Person("Shawn Hunt",1920,1932,5));
allPersons.add(new Person("Christopher Holland",1920,1936,7));
allPersons.add(new Person("Christopher Fleming",1920,1934,7));
allPersons.add(new Person("Norman Lucas",1920,1933,0));
allPersons.add(new Person("Eugene Hamilton",1918,1939,0));
allPersons.add(new Person("Clayton Brewer",1918,1937,16));
allPersons.add(new Person("Gregory Doyle",1917,1938,13));
allPersons.add(new Person("Connor Dixon",1920,1938,12));
allPersons.add(new Person("Todd Giles",1920,1932,10));
allPersons.add(new Person("Logan Berry",1920,1933,17));
allPersons.add(new Person("Caleb Carroll",1918,1933,21));
allPersons.add(new Person("Jeffrey Daniel",1919,1934,25));
allPersons.add(new Person("Mark Johnson",1918,1930,26));
allPersons.add(new Person("Joshua Chase",1920,1937,23));
allPersons.add(new Person("Gary Graham",1916,1936,24));
allPersons.add(new Person("Charles Chandler",1916,1933,30));
allPersons.add(new Person("William Ferguson",1916,1932,36));
allPersons.add(new Person("Stanley Bishop",1920,1936,30));
allPersons.add(new Person("Joel Coleman",1919,1932,36));
allPersons.add(new Person("Logan Beck",1919,1937,36));
allPersons.add(new Person("Luke Bowen",1916,1937,46));
allPersons.add(new Person("Cody Byrd",1916,1939,44));
allPersons.add(new Person("Clayton Duncan",1919,1936,42));
allPersons.add(new Person("Ryan Clarke",1920,1932,44));
allPersons.add(new Person("Andrew Fisher",1916,1935,47));
allPersons.add(new Person("Francis Lewis",1917,1931,54));
allPersons.add(new Person("Lewis Doyle",1918,1934,55));
allPersons.add(new Person("Martin Jensen",1916,1935,55));
allPersons.add(new Person("Cameron Hines",1918,1932,57));
allPersons.add(new Person("Maurice Carr",1916,1931,56));
allPersons.add(new Person("Henry Case",1918,1930,62));
allPersons.add(new Person("Ernest Glass",1918,1939,64));
allPersons.add(new Person("Dennis Fisher",1919,1938,64));
allPersons.add(new Person("Ross Lawson",1920,1930,63));
allPersons.add(new Person("Harry Jennings",1917,1931,62));
allPersons.add(new Person("Thomas Duncan",1917,1933,71));
allPersons.add(new Person("Richard Atkinson",1917,1931,73));
allPersons.add(new Person("Austin Coleman",1916,1936,70));
allPersons.add(new Person("Edward Davidson",1920,1938,70));
allPersons.add(new Person("Dale Baker",1917,1933,71));
allPersons.add(new Person("Herbert Fisher",1916,1935,84));
allPersons.add(new Person("Mark Howell",1916,1930,86));
allPersons.add(new Person("Bruce Harris",1920,1936,85));
allPersons.add(new Person("Howard Dunn",1917,1933,82));
allPersons.add(new Person("Ryan Burgess",1920,1936,86));
allPersons.add(new Person("Daniel Carroll",1918,1935,93));
allPersons.add(new Person("Jack Little",1917,1935,95));
allPersons.add(new Person("Brian Day",1916,1939,92));
allPersons.add(new Person("Wayne Brady",1916,1936,94));
allPersons.add(new Person("Harry Caldwell",1918,1934,97));
allPersons.add(new Person("Shawn Bell",1926,1943,5));
allPersons.add(new Person("Wesley Barnett",1927,1944,6));
allPersons.add(new Person("Kenneth Douglas",1927,1943,1));
allPersons.add(new Person("Donald Burns",1927,1946,2));
allPersons.add(new Person("Henry Hardy",1928,1948,0));
allPersons.add(new Person("David Lambert",1929,1942,16));
allPersons.add(new Person("Albert Little",1928,1943,12));
allPersons.add(new Person("Robert Gray",1928,1949,10));
allPersons.add(new Person("Eugene Gordon",1929,1947,11));
allPersons.add(new Person("Albert Lynch",1928,1946,10));
allPersons.add(new Person("Harvey Ferguson",1926,1943,26));
allPersons.add(new Person("Ronald Anderson",1928,1947,26));
allPersons.add(new Person("Michael Hudson",1926,1947,26));
allPersons.add(new Person("Alexander Gregory",1928,1946,27));
allPersons.add(new Person("Warren Hart",1930,1943,23));
allPersons.add(new Person("Frank Chapman",1929,1945,32));
allPersons.add(new Person("Cody Lloyd",1930,1945,30));
allPersons.add(new Person("Raymond Holland",1930,1945,37));
allPersons.add(new Person("Noah Fletcher",1929,1942,31));
allPersons.add(new Person("Paul Copeland",1927,1945,36));
allPersons.add(new Person("Jacob Hammond",1929,1947,45));
allPersons.add(new Person("Noah Jacobs",1930,1945,40));
allPersons.add(new Person("Corey Berry",1930,1947,47));
allPersons.add(new Person("Franklin Jordan",1928,1947,45));
allPersons.add(new Person("Colin Ingram",1930,1941,46));
allPersons.add(new Person("Paul Bush",1928,1943,55));
allPersons.add(new Person("Joel Johnson",1930,1942,52));
allPersons.add(new Person("Danny Atkinson",1928,1945,56));
allPersons.add(new Person("Walter Bailey",1928,1946,53));
allPersons.add(new Person("Logan Allen",1926,1944,54));
allPersons.add(new Person("Derek Bradley",1927,1949,61));
allPersons.add(new Person("Ernest Cox",1928,1948,63));
allPersons.add(new Person("Russell Gibson",1926,1949,67));
allPersons.add(new Person("Carl Macdonald",1929,1946,65));
allPersons.add(new Person("Bradley Baldwin",1927,1942,66));
allPersons.add(new Person("Bernard Carter",1929,1946,74));
allPersons.add(new Person("Philip Mackenzie",1927,1942,71));
allPersons.add(new Person("Corey Clark",1928,1948,71));
allPersons.add(new Person("Francis Abbott",1926,1943,76));
allPersons.add(new Person("David Ingram",1929,1948,70));
allPersons.add(new Person("William Gates",1926,1949,81));
allPersons.add(new Person("Kyle Cook",1927,1949,85));
allPersons.add(new Person("Dennis Green",1928,1945,87));
allPersons.add(new Person("Earl Barnett",1929,1945,82));
allPersons.add(new Person("Dennis Lloyd",1928,1941,82));
allPersons.add(new Person("Steven Cole",1927,1943,91));
allPersons.add(new Person("Gabriel Gates",1927,1949,92));
allPersons.add(new Person("Alan Hicks",1928,1949,94));
allPersons.add(new Person("Raymond Cunningham",1930,1942,95));
allPersons.add(new Person("Jerome Boyd",1930,1947,95));
allPersons.add(new Person("Arthur Glass",1940,1950,1));
allPersons.add(new Person("Kenneth Davidson",1936,1952,0));
allPersons.add(new Person("Shawn Drake",1940,1958,3));
allPersons.add(new Person("Howard Fitzgerald",1937,1950,0));
allPersons.add(new Person("Danny Foster",1939,1953,2));
allPersons.add(new Person("Andrew Hubbard",1938,1952,17));
allPersons.add(new Person("Nathan Hill",1936,1953,15));
allPersons.add(new Person("Jonathan Fox",1938,1950,13));
allPersons.add(new Person("George Kerr",1938,1951,16));
allPersons.add(new Person("Mitchell Jensen",1939,1950,12));
allPersons.add(new Person("Kevin Coleman",1936,1959,21));
allPersons.add(new Person("Hunter Lloyd",1938,1955,25));
allPersons.add(new Person("Daniel Barnett",1940,1956,22));
allPersons.add(new Person("Clayton Jordan",1940,1952,20));
allPersons.add(new Person("Norman Bradley",1936,1953,21));
allPersons.add(new Person("Steven Haynes",1939,1957,32));
allPersons.add(new Person("Curtis Greene",1938,1952,31));
allPersons.add(new Person("Caleb Hardy",1939,1955,30));
allPersons.add(new Person("Frank Freeman",1936,1951,34));
allPersons.add(new Person("Luke Burke",1936,1952,31));
allPersons.add(new Person("Mark Ball",1938,1958,42));
allPersons.add(new Person("Robert Giles",1937,1956,44));
allPersons.add(new Person("Jerome Holmes",1936,1956,46));
allPersons.add(new Person("Gary King",1938,1952,40));
allPersons.add(new Person("Shane Hawkins",1937,1952,46));
allPersons.add(new Person("Jeffrey Brooks",1940,1954,57));
allPersons.add(new Person("Frank Davis",1937,1959,56));
allPersons.add(new Person("Harold Dunn",1938,1950,52));
allPersons.add(new Person("Roger Garrett",1937,1954,56));
allPersons.add(new Person("Shawn Barnes",1940,1952,53));
allPersons.add(new Person("Joseph Brewer",1937,1955,67));
allPersons.add(new Person("Jeffrey Edwards",1938,1951,62));
allPersons.add(new Person("Kyle Fitzgerald",1937,1953,62));
allPersons.add(new Person("Gabriel Blake",1940,1957,61));
allPersons.add(new Person("Adam Leonard",1940,1954,66));
allPersons.add(new Person("Benjamin Carpenter",1936,1958,73));
allPersons.add(new Person("Isaac Bates",1936,1952,74));
allPersons.add(new Person("Harry Carr",1937,1956,77));
allPersons.add(new Person("Joel Berry",1936,1956,73));
allPersons.add(new Person("Peter Lawson",1939,1959,70));
allPersons.add(new Person("Lewis Lambert",1938,1950,87));
allPersons.add(new Person("Clarence Day",1939,1956,81));
allPersons.add(new Person("Frederick Adams",1937,1958,81));
allPersons.add(new Person("Robert Hunter",1940,1958,84));
allPersons.add(new Person("Wayne Hampton",1940,1950,84));
allPersons.add(new Person("Samuel Holland",1939,1955,97));
allPersons.add(new Person("Nicholas Carr",1939,1954,95));
allPersons.add(new Person("Blake Hayes",1938,1959,93));
allPersons.add(new Person("Donald Harvey",1936,1959,93));
allPersons.add(new Person("Lois Gibson",1937,1956,90));
allPersons.add(new Person("Maurice Carroll",1948,1969,2));
allPersons.add(new Person("Jason Clarke",1948,1964,3));
allPersons.add(new Person("Gerald Jenkins",1946,1963,4));
allPersons.add(new Person("Maxwell Jensen",1950,1968,0));
allPersons.add(new Person("Walter Ford",1950,1963,3));
allPersons.add(new Person("Maxwell Crawford",1947,1960,15));
allPersons.add(new Person("James Lynch",1948,1969,12));
allPersons.add(new Person("Ross Carter",1950,1961,15));
allPersons.add(new Person("Joel Dawson",1949,1969,14));
allPersons.add(new Person("Nicholas Craig",1946,1968,17));
allPersons.add(new Person("Warren Lyons",1950,1962,21));
allPersons.add(new Person("Wayne Jacobs",1950,1960,20));
allPersons.add(new Person("Dennis Carroll",1947,1965,22));
allPersons.add(new Person("Evan Kelly",1946,1960,20));
allPersons.add(new Person("Douglas Freeman",1947,1964,20));
allPersons.add(new Person("Derek Hubbard",1949,1965,30));
allPersons.add(new Person("Blake Boyd",1948,1964,37));
allPersons.add(new Person("Darrell Abbott",1947,1967,33));
allPersons.add(new Person("Arthur Clarke",1949,1960,35));
allPersons.add(new Person("Herbert Bush",1948,1966,37));
allPersons.add(new Person("Ann Bishop",1948,1966,47));
allPersons.add(new Person("Alexander Dean",1946,1965,40));
allPersons.add(new Person("Edwin Elliott",1946,1965,44));
allPersons.add(new Person("Frederick Doyle",1947,1967,40));
allPersons.add(new Person("Craig Barnett",1950,1968,42));
allPersons.add(new Person("Clarence Gregory",1948,1963,57));
allPersons.add(new Person("Eugene Gardner",1947,1968,54));
allPersons.add(new Person("Victor Freeman",1948,1964,51));
allPersons.add(new Person("Frank Ellis",1948,1966,55));
allPersons.add(new Person("Cameron Lloyd",1949,1967,57));
allPersons.add(new Person("Ellen Fraser",1949,1967,61));
allPersons.add(new Person("Justin Griffin",1947,1960,64));
allPersons.add(new Person("Clark Burke",1948,1964,66));
allPersons.add(new Person("Lawrence Gordon",1950,1968,61));
allPersons.add(new Person("Joshua Jones",1946,1964,60));
allPersons.add(new Person("Douglas Arnold",1946,1961,75));
allPersons.add(new Person("Christopher Anderson",1948,1966,73));
allPersons.add(new Person("Vincent Carter",1947,1963,72));
allPersons.add(new Person("Isaac Cunningham",1947,1960,70));
allPersons.add(new Person("Warren Armstrong",1950,1964,74));
allPersons.add(new Person("Logan Hampton",1946,1967,85));
allPersons.add(new Person("Elaine Hayes",1948,1966,81));
allPersons.add(new Person("Gregory Austin",1948,1963,83));
allPersons.add(new Person("Christopher Jacobs",1950,1965,85));
allPersons.add(new Person("Carla Hall",1946,1968,83));
allPersons.add(new Person("Dennis Lyons",1947,1967,93));
allPersons.add(new Person("Herbert Lewis",1946,1963,93));
allPersons.add(new Person("Cody Hart",1946,1969,93));
allPersons.add(new Person("Stanley Dutton",1949,1962,93));
allPersons.add(new Person("Vincent Henry",1946,1964,90));
allPersons.add(new Person("Mitchell Hardy",1957,1971,6));
allPersons.add(new Person("Craig Cole",1956,1975,2));
allPersons.add(new Person("Todd Barnett",1960,1971,1));
allPersons.add(new Person("Beatrice Drake",1957,1973,4));
allPersons.add(new Person("Andrew Bell",1958,1973,7));
allPersons.add(new Person("Christopher Duncan",1958,1972,14));
allPersons.add(new Person("Owen Brown",1960,1970,13));
allPersons.add(new Person("Warren Fisher",1956,1972,16));
allPersons.add(new Person("Bruce Lowe",1956,1978,14));
allPersons.add(new Person("Jordan Fitzgerald",1958,1970,12));
allPersons.add(new Person("Evan Hudson",1956,1979,22));
allPersons.add(new Person("Mark Hogan",1957,1976,26));
allPersons.add(new Person("Ernest Knight",1958,1974,21));
allPersons.add(new Person("Dennis Griffin",1958,1970,24));
allPersons.add(new Person("Aaron Carpenter",1957,1973,26));
allPersons.add(new Person("Danny Caldwell",1956,1977,37));
allPersons.add(new Person("Aaron Lewis",1960,1979,37));
allPersons.add(new Person("Shane Fox",1958,1979,35));
allPersons.add(new Person("Nicholas Lawson",1959,1970,37));
allPersons.add(new Person("Linda Arnold",1958,1978,32));
allPersons.add(new Person("Shawn Alexander",1958,1975,41));
allPersons.add(new Person("Ernest Joseph",1957,1979,47));
allPersons.add(new Person("Stanley Crawford",1957,1976,45));
allPersons.add(new Person("Martin Lynch",1960,1978,44));
allPersons.add(new Person("Lawrence Johnson",1960,1972,41));
allPersons.add(new Person("Dale Ball",1957,1973,57));
allPersons.add(new Person("Jordan Long",1957,1978,52));
allPersons.add(new Person("Cody Carroll",1957,1977,57));
allPersons.add(new Person("Austin Carr",1956,1979,53));
allPersons.add(new Person("Luke Duncan",1960,1977,53));
allPersons.add(new Person("Audrey James",1957,1975,61));
allPersons.add(new Person("Keith Carter",1956,1976,65));
allPersons.add(new Person("Louis Kerr",1960,1971,60));
allPersons.add(new Person("Chad Allen",1956,1979,61));
allPersons.add(new Person("Danny Johnson",1956,1974,64));
allPersons.add(new Person("Clayton Chambers",1959,1976,71));
allPersons.add(new Person("Joshua Ball",1957,1977,70));
allPersons.add(new Person("Paul Cooper",1957,1970,77));
allPersons.add(new Person("Curtis Harding",1956,1977,77));
allPersons.add(new Person("Kathy Burgess",1959,1976,76));
allPersons.add(new Person("William Collins",1957,1978,85));
allPersons.add(new Person("Douglas Fitzgerald",1959,1977,85));
allPersons.add(new Person("Wesley Carroll",1956,1973,82));
allPersons.add(new Person("Timothy Kelly",1957,1979,82));
allPersons.add(new Person("Douglas Brady",1960,1976,85));
allPersons.add(new Person("Jacob Grant",1958,1977,92));
allPersons.add(new Person("William Byrd",1957,1975,93));
allPersons.add(new Person("Todd Burns",1959,1970,96));
allPersons.add(new Person("Cody Caldwell",1958,1978,97));
allPersons.add(new Person("Eric Dunn",1960,1971,92));
allPersons.add(new Person("Anthony Leonard",1967,1988,7));
allPersons.add(new Person("Ashley Kaufman",1970,1988,2));
allPersons.add(new Person("Darrell Gregory",1966,1981,0));
allPersons.add(new Person("Martha Carroll",1969,1984,6));
allPersons.add(new Person("Ryan Lloyd",1968,1988,2));
allPersons.add(new Person("Christopher Fowler",1968,1982,13));
allPersons.add(new Person("Maureen Clements",1969,1981,14));
allPersons.add(new Person("Stephen Chase",1967,1987,12));
allPersons.add(new Person("Jacob Burgess",1966,1983,15));
allPersons.add(new Person("Adam Daniels",1968,1980,13));
allPersons.add(new Person("Hunter Holland",1967,1980,27));
allPersons.add(new Person("Jacob Cole",1967,1982,20));
allPersons.add(new Person("Cody Garrett",1966,1983,23));
allPersons.add(new Person("Louise Glover",1968,1982,26));
allPersons.add(new Person("Leonard Douglas",1970,1981,26));
allPersons.add(new Person("Jonathan Green",1969,1982,32));
allPersons.add(new Person("Dustin Lynch",1970,1987,33));
allPersons.add(new Person("Katherine Bishop",1969,1981,31));
allPersons.add(new Person("Logan Grant",1969,1989,31));
allPersons.add(new Person("Jordan Cole",1968,1984,36));
allPersons.add(new Person("Joyce Franklin",1970,1986,44));
allPersons.add(new Person("John Cunningham",1968,1986,40));
allPersons.add(new Person("Cynthia Carlson",1970,1987,40));
allPersons.add(new Person("Louis Black",1970,1986,46));
allPersons.add(new Person("Arthur Lane",1967,1986,40));
allPersons.add(new Person("Alexander Berry",1969,1986,57));
allPersons.add(new Person("Gabriel Little",1970,1983,52));
allPersons.add(new Person("Gregory Clayton",1968,1983,53));
allPersons.add(new Person("George Brooks",1966,1981,52));
allPersons.add(new Person("Jerome Hunt",1967,1982,56));
allPersons.add(new Person("Margaret Bowen",1969,1982,67));
allPersons.add(new Person("Samuel Bryant",1968,1989,62));
allPersons.add(new Person("Norman Berry",1968,1988,63));
allPersons.add(new Person("Robert Hodges",1970,1981,62));
allPersons.add(new Person("Cameron Campbell",1967,1984,67));
allPersons.add(new Person("Victor Jennings",1966,1988,71));
allPersons.add(new Person("Christopher Carr",1969,1988,70));
allPersons.add(new Person("Seth Hodges",1966,1986,70));
allPersons.add(new Person("Roy Jones",1969,1983,76));
allPersons.add(new Person("Matthew Johnson",1968,1981,77));
allPersons.add(new Person("Evan Bates",1966,1984,85));
allPersons.add(new Person("Stephen Greene",1969,1983,87));
allPersons.add(new Person("Albert Harris",1970,1982,87));
allPersons.add(new Person("Carl Lee",1966,1989,80));
allPersons.add(new Person("Mitchell Farmer",1969,1987,80));
allPersons.add(new Person("Todd Chase",1968,1980,96));
allPersons.add(new Person("Donald Chase",1970,1987,96));
allPersons.add(new Person("Jocelyn Abbott",1970,1980,95));
allPersons.add(new Person("Francis Dean",1969,1981,91));
allPersons.add(new Person("Eric Hubbard",1966,1981,92));
allPersons.add(new Person("Frank Jennings",1979,1992,4));
allPersons.add(new Person("William Gates",1977,1994,4));
allPersons.add(new Person("Donald Love",1976,1998,1));
allPersons.add(new Person("Dale Giles",1980,1990,2));
allPersons.add(new Person("Joel Caldwell",1980,1994,6));
allPersons.add(new Person("Bruce Cook",1976,1993,15));
allPersons.add(new Person("Logan Hayes",1980,1992,17));
allPersons.add(new Person("Ryan Davidson",1976,1994,13));
allPersons.add(new Person("Gabriel Jennings",1978,1997,14));
allPersons.add(new Person("Dana Foster",1978,1992,16));
allPersons.add(new Person("Leslie Arnold",1976,1997,20));
allPersons.add(new Person("Dorothy Gates",1976,1990,27));
allPersons.add(new Person("Victor Doyle",1978,1990,23));
allPersons.add(new Person("Jacob Barrett",1978,1998,27));
allPersons.add(new Person("Christopher Cole",1980,1999,24));
allPersons.add(new Person("Brandon Clarke",1979,1993,30));
allPersons.add(new Person("Ryan Greene",1976,1990,34));
allPersons.add(new Person("Louis Allen",1978,1998,30));
allPersons.add(new Person("Charles Haynes",1980,1999,34));
allPersons.add(new Person("Victor Dunn",1977,1991,36));
allPersons.add(new Person("Brenda Farmer",1978,1995,41));
allPersons.add(new Person("Beverly Hale",1979,1995,47));
allPersons.add(new Person("Douglas Alexander",1977,1999,43));
allPersons.add(new Person("Howard Douglas",1977,1991,41));
allPersons.add(new Person("Walter Gray",1979,1998,41));
allPersons.add(new Person("Erica Brown",1980,1997,50));
allPersons.add(new Person("Anthony Bowman",1978,1992,52));
allPersons.add(new Person("Joshua Lewis",1976,1990,53));
allPersons.add(new Person("Kathy Hamilton",1976,1991,57));
allPersons.add(new Person("Caleb Anderson",1976,1996,51));
allPersons.add(new Person("Debra Craig",1980,1993,60));
allPersons.add(new Person("Blake Jones",1978,1994,67));
allPersons.add(new Person("Stephen Gordon",1977,1995,60));
allPersons.add(new Person("Josephine Bush",1976,1990,65));
allPersons.add(new Person("Aaron Higgins",1977,1998,63));
allPersons.add(new Person("Aaron Ford",1976,1990,72));
allPersons.add(new Person("Bradley Farmer",1980,1994,71));
allPersons.add(new Person("Arthur Cunningham",1979,1997,74));
allPersons.add(new Person("Derek Jensen",1977,1997,73));
allPersons.add(new Person("Hunter Chase",1979,1994,72));
allPersons.add(new Person("Laura Lowe",1978,1994,85));
allPersons.add(new Person("Logan Haynes",1977,1993,81));
allPersons.add(new Person("Bradley Ingram",1977,1994,85));
allPersons.add(new Person("Bruce Curtis",1978,1996,80));
allPersons.add(new Person("Frank Holloway",1978,1996,83));
allPersons.add(new Person("Beatrice Davis",1977,1994,93));
allPersons.add(new Person("Doris Joseph",1976,1994,90));
allPersons.add(new Person("Zachary Lane",1976,1991,92));
allPersons.add(new Person("Wayne Joseph",1980,1999,94));
allPersons.add(new Person("Shawn Hayes",1978,1999,96));
allPersons.add(new Person("Herbert Bennett",1988,2009,6));
allPersons.add(new Person("Colin Andrews",1989,2001,2));
allPersons.add(new Person("Derek Elliott",1988,2002,0));
allPersons.add(new Person("Jeffrey Griffin",1990,2002,5));
allPersons.add(new Person("Leonard Jennings",1986,2009,2));
allPersons.add(new Person("Gertrude Bates",1987,2005,11));
allPersons.add(new Person("Andrew Fletcher",1987,2008,15));
allPersons.add(new Person("Owen Kaufman",1990,2007,11));
allPersons.add(new Person("Harry Andrews",1990,2005,17));
allPersons.add(new Person("Sean Alexander",1986,2006,16));
allPersons.add(new Person("Earl Chandler",1986,2006,27));
allPersons.add(new Person("Terrence Grant",1990,2007,23));
allPersons.add(new Person("Joshua Butler",1990,2008,24));
allPersons.add(new Person("Bernard Ford",1986,2009,27));
allPersons.add(new Person("Holly Fletcher",1990,2009,20));
allPersons.add(new Person("Caroline Baldwin",1986,2009,32));
allPersons.add(new Person("Joseph Burns",1990,2006,33));
allPersons.add(new Person("Michael Bush",1990,2009,30));
allPersons.add(new Person("Eileen Arnold",1990,2008,33));
allPersons.add(new Person("Evan Bates",1989,2005,33));
allPersons.add(new Person("Jack Dean",1986,2008,40));
allPersons.add(new Person("James Crawford",1987,2008,40));
allPersons.add(new Person("Lucas George",1988,2003,42));
allPersons.add(new Person("George Armstrong",1989,2003,44));
allPersons.add(new Person("Jordan Henry",1988,2002,47));
allPersons.add(new Person("Erin Ball",1988,2002,55));
allPersons.add(new Person("Steven Jenkins",1988,2008,50));
allPersons.add(new Person("Crystal Glover",1989,2003,57));
allPersons.add(new Person("Clayton Garrett",1989,2000,56));
allPersons.add(new Person("Melissa Davidson",1989,2009,57));
allPersons.add(new Person("Harry Harding",1990,2004,62));
allPersons.add(new Person("Gregory Garrett",1989,2002,64));
allPersons.add(new Person("Ethel Giles",1989,2002,67));
allPersons.add(new Person("Joann Gray",1989,2002,65));
allPersons.add(new Person("Joseph Hardy",1986,2008,64));
allPersons.add(new Person("Arthur Gordon",1987,2005,74));
allPersons.add(new Person("Wayne Armstrong",1989,2006,75));
allPersons.add(new Person("Christopher Burton",1987,2008,73));
allPersons.add(new Person("Bruce Cunningham",1990,2004,76));
allPersons.add(new Person("Russell Dawson",1990,2006,72));
allPersons.add(new Person("Russell Giles",1986,2006,86));
allPersons.add(new Person("Maurice Dean",1987,2000,82));
allPersons.add(new Person("Michael Craig",1987,2000,83));
allPersons.add(new Person("John Franklin",1990,2008,80));
allPersons.add(new Person("Ralph Logan",1989,2000,86));
allPersons.add(new Person("Maurice Kent",1986,2001,96));
allPersons.add(new Person("Stephen Hodges",1987,2006,94));
allPersons.add(new Person("Clifford Bradley",1990,2004,94));
allPersons.add(new Person("Ross Graham",1989,2005,95));
allPersons.add(new Person("Edward Elliott",1990,2004,95));
allPersons.add(new Person("Mark Haynes",1998,2011,1));
allPersons.add(new Person("Martha Gilbert",1998,2012,0));
allPersons.add(new Person("Thomas Fletcher",1996,2016,2));
allPersons.add(new Person("John Kennedy",1998,2016,7));
allPersons.add(new Person("Clark Beck",1999,2010,2));
allPersons.add(new Person("Robert Clements",1997,2015,14));
allPersons.add(new Person("Nicole Dutton",1999,2017,15));
allPersons.add(new Person("Kenneth Baldwin",1996,2013,10));
allPersons.add(new Person("Charles Caldwell",1998,2018,16));
allPersons.add(new Person("Josephine Curtis",1997,2011,10));
allPersons.add(new Person("Eric Jensen",1996,2017,26));
allPersons.add(new Person("Joseph Adkins",2000,2013,26));
allPersons.add(new Person("Evelyn Jordan",1996,2019,27));
allPersons.add(new Person("Clark Mackenzie",1996,2016,20));
allPersons.add(new Person("Anthony Day",1999,2011,25));
allPersons.add(new Person("Emma Ball",1998,2012,34));
allPersons.add(new Person("Ann Carlson",1999,2010,36));
allPersons.add(new Person("Gilbert Kerr",1996,2014,31));
allPersons.add(new Person("Natalie Austin",1997,2015,34));
allPersons.add(new Person("Kimberly Campbell",1998,2019,34));
allPersons.add(new Person("Dale Lewis",1998,2017,41));
allPersons.add(new Person("Carl Kelley",1996,2013,42));
allPersons.add(new Person("Marilyn Bryant",2000,2011,40));
allPersons.add(new Person("Terrence Collins",1998,2018,42));
allPersons.add(new Person("Ronald Fields",2000,2018,40));
allPersons.add(new Person("Brandon Burke",1997,2017,57));
allPersons.add(new Person("Adrian Hubbard",1999,2016,54));
allPersons.add(new Person("Ian Gray",1998,2013,50));
allPersons.add(new Person("Harry Boyd",1999,2012,55));
allPersons.add(new Person("Ross Jennings",1997,2015,50));
allPersons.add(new Person("Isabel Clark",1997,2019,66));
allPersons.add(new Person("Alexander Holmes",1999,2010,63));
allPersons.add(new Person("Megan Lawson",1996,2013,63));
allPersons.add(new Person("Ernest Day",1997,2012,66));
allPersons.add(new Person("Tyler Austin",1999,2017,62));
allPersons.add(new Person("Amanda Kennedy",2000,2013,74));
allPersons.add(new Person("Frank Armstrong",1998,2011,73));
allPersons.add(new Person("Mary Jensen",1997,2010,74));
allPersons.add(new Person("Grace Adkins",1996,2016,71));
allPersons.add(new Person("Andrew Berry",2000,2019,70));
allPersons.add(new Person("Kyle Hamilton",1996,2015,87));
allPersons.add(new Person("Clifford George",1998,2016,81));
allPersons.add(new Person("Donna Cobb",1996,2011,86));
allPersons.add(new Person("Bernard Hardy",1996,2015,81));
allPersons.add(new Person("Gregory Holmes",1996,2017,83));
allPersons.add(new Person("Lillian Hunter",1997,2013,94));
allPersons.add(new Person("Ethel Burton",1997,2011,90));
allPersons.add(new Person("Scott Jones",2000,2011,93));
allPersons.add(new Person("Mildred Lewis",2000,2010,93));
allPersons.add(new Person("Florence Clayton",1998,2015,95));
allPersons.add(new Person("Logan Hodges",2010,2027,0));
allPersons.add(new Person("Gilbert Lynch",2007,2021,3));
allPersons.add(new Person("Mitchell Carter",2010,2027,5));
allPersons.add(new Person("Hazel James",2009,2021,5));
allPersons.add(new Person("Mitchell Kent",2009,2025,0));
allPersons.add(new Person("Louise Glover",2009,2024,12));
allPersons.add(new Person("Grace Crawford",2008,2022,11));
allPersons.add(new Person("Eric Gardner",2009,2029,17));
allPersons.add(new Person("Benjamin Hodges",2008,2020,17));
allPersons.add(new Person("Amanda Hayes",2007,2027,13));
allPersons.add(new Person("Anna Douglas",2009,2027,22));
allPersons.add(new Person("Holly Crawford",2008,2023,21));
allPersons.add(new Person("Mitchell Bowen",2007,2029,20));
allPersons.add(new Person("Ernest Boyd",2006,2021,21));
allPersons.add(new Person("Jill Holmes",2007,2028,22));
allPersons.add(new Person("Grace Day",2007,2020,34));
allPersons.add(new Person("Russell Fowler",2007,2028,31));
allPersons.add(new Person("Madeline Atkinson",2007,2022,35));
allPersons.add(new Person("Eric Barnes",2009,2027,32));
allPersons.add(new Person("Jocelyn Atkinson",2010,2025,33));
allPersons.add(new Person("Russell Abbott",2008,2029,47));
allPersons.add(new Person("Stanley Leonard",2007,2020,46));
allPersons.add(new Person("Maxwell Chambers",2009,2021,43));
allPersons.add(new Person("Oscar Jennings",2006,2023,44));
allPersons.add(new Person("Ellen Ellis",2007,2022,45));
allPersons.add(new Person("Emma Brooks",2007,2024,55));
allPersons.add(new Person("Beverly Alexander",2007,2027,57));
allPersons.add(new Person("Frederick Lynch",2010,2027,52));
allPersons.add(new Person("Aaron Gates",2008,2024,55));
allPersons.add(new Person("Joyce Kent",2009,2026,51));
allPersons.add(new Person("Ian Lee",2006,2026,60));
allPersons.add(new Person("Douglas Hale",2010,2025,65));
allPersons.add(new Person("Carla Hall",2010,2026,66));
allPersons.add(new Person("Howard Fowler",2008,2020,63));
allPersons.add(new Person("Blake Harvey",2010,2024,60));
allPersons.add(new Person("Evelyn Jones",2008,2028,73));
allPersons.add(new Person("Maria Jennings",2009,2028,71));
allPersons.add(new Person("Carla Fox",2010,2029,72));
allPersons.add(new Person("Frances Barker",2007,2025,75));
allPersons.add(new Person("Brian Carter",2010,2020,74));
allPersons.add(new Person("Edith Gates",2009,2026,87));
allPersons.add(new Person("Mary Burgess",2007,2024,83));
allPersons.add(new Person("Melissa Austin",2007,2028,86));
allPersons.add(new Person("Janet Giles",2010,2022,85));
allPersons.add(new Person("Evan Abbott",2008,2021,82));
allPersons.add(new Person("Noah Hoffman",2007,2021,90));
allPersons.add(new Person("Michael Lawrence",2007,2023,95));
allPersons.add(new Person("Paul Foster",2006,2027,91));
allPersons.add(new Person("Joshua Mackenzie",2010,2025,91));
allPersons.add(new Person("Christopher Dean",2008,2025,96));
allPersons.add(new Person("Clayton Macdonald",2016,2039,1));
allPersons.add(new Person("James Hodges",2019,2035,1));
allPersons.add(new Person("Cameron Brown",2017,2039,4));
allPersons.add(new Person("Adrian Boyd",2018,2036,6));
allPersons.add(new Person("Dean Carlson",2018,2039,6));
allPersons.add(new Person("Cameron Brown",2016,2033,13));
allPersons.add(new Person("Madeline Hopkins",2016,2038,16));
allPersons.add(new Person("Gail Harvey",2018,2039,10));
allPersons.add(new Person("Dana Holloway",2019,2033,14));
allPersons.add(new Person("Melissa Bryant",2019,2033,16));
allPersons.add(new Person("Justin Bradley",2018,2030,25));
allPersons.add(new Person("Roy Dutton",2020,2038,21));
allPersons.add(new Person("Margaret Clark",2017,2031,23));
allPersons.add(new Person("Benjamin Holt",2018,2038,20));
allPersons.add(new Person("Connor Jackson",2020,2034,22));
allPersons.add(new Person("Cody Brooks",2018,2036,33));
allPersons.add(new Person("Douglas Knight",2016,2033,32));
allPersons.add(new Person("Harold Jordan",2020,2038,32));
allPersons.add(new Person("Angela Harding",2017,2030,35));
allPersons.add(new Person("Donald Franklin",2018,2036,32));
allPersons.add(new Person("April Byrd",2019,2039,42));
allPersons.add(new Person("Crystal Campbell",2019,2039,41));
allPersons.add(new Person("Joel Chase",2017,2038,44));
allPersons.add(new Person("Marian Campbell",2019,2033,47));
allPersons.add(new Person("Alicia Brooks",2018,2039,43));
allPersons.add(new Person("Jordan Bailey",2018,2032,55));
allPersons.add(new Person("Carl Clarke",2019,2038,55));
allPersons.add(new Person("Erin Johnston",2020,2033,53));
allPersons.add(new Person("Allison Green",2019,2031,55));
allPersons.add(new Person("Amy Abbott",2020,2034,54));
allPersons.add(new Person("Marion Burke",2018,2032,62));
allPersons.add(new Person("Vincent Brewer",2016,2032,62));
allPersons.add(new Person("Ryan Dutton",2020,2030,67));
allPersons.add(new Person("Jesse Henry",2016,2031,60));
allPersons.add(new Person("Pamela Caldwell",2020,2033,62));
allPersons.add(new Person("Mabel Lawrence",2019,2039,77));
allPersons.add(new Person("Jerome Green",2020,2037,70));
allPersons.add(new Person("Dorothy Green",2017,2033,76));
allPersons.add(new Person("Debra Gibson",2016,2036,71));
allPersons.add(new Person("Grace Hoffman",2020,2034,76));
allPersons.add(new Person("Florence Carter",2017,2030,85));
allPersons.add(new Person("Gertrude Hunter",2018,2038,80));
allPersons.add(new Person("Douglas Lewis",2016,2035,83));
allPersons.add(new Person("Gary Higgins",2016,2032,80));
allPersons.add(new Person("Franklin Coleman",2020,2035,86));
allPersons.add(new Person("Beatrice Higgins",2019,2038,96));
allPersons.add(new Person("Lydia Hogan",2019,2030,95));
allPersons.add(new Person("Judy Lloyd",2016,2033,96));
allPersons.add(new Person("Holly Jones",2017,2031,96));
allPersons.add(new Person("Scott Griffin",2016,2031,92));
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
    
    public static int startyear=1852;
    public static int year = startyear;
    
    public static Set<String> allPresidents = new HashSet<>();
    public static Set<String> allPrimeMinisters = new HashSet<>();
    
    public static void electPresident(){
        List<Party> candidates = new ArrayList<>();
        int tresh = 90/ allParties.size();
        for(Party par: allParties){
            int points = par.getPercent();
            
            if(points == 0 && year==startyear){
                points += ra.nextInt(100);
            }
            
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
        System.out.println("\n===============\nElected President: "+ President.getStandardB()+" "+ President.ideoDisplay());
        
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
    
    public static void DEBUGDisplayAllActive(){
        for(Person per: activePersons){
            System.out.println(per);
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
//DEBUGDisplayAllActive();

		    String upu = sc.nextLine();
		    
		    updateTick();
		    year+=interval;
		    
		}
		
		       
		
	}
}
