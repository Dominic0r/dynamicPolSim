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
                System.out.print(name+ ": "+maxpar.getColor()+maxpar.getName()+RESET+maxpar.ideoDisplay() + " |");
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
                
                prominence = currentParty.getPercent()/2;
                prominence+= proximityWith(currentParty)/2;
                prominence+= ra.nextInt(15);
                
                if(this == currentParty.getStandardB() || this == currentParty.getChair() || this == currentParty.getForSpeak()){
                    prominence += prominence/2;
                }
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
        allPersons.add(new Person("Martin Bates Sr.",1838,1855,0));
allPersons.add(new Person("D.C. Dixon Jr.",1836,1853,1));
allPersons.add(new Person("Roger Hogan",1837,1853,6));
allPersons.add(new Person("Terrence Johnston",1839,1856,7));
allPersons.add(new Person("Gordon Graham",1837,1857,5));
allPersons.add(new Person("Frederick Macdonald",1838,1854,13));
allPersons.add(new Person("Clark Bailey",1838,1852,11));
allPersons.add(new Person("Lucas Craig II",1840,1856,12));
allPersons.add(new Person("B.G. Burke IV",1836,1857,16));
allPersons.add(new Person("Bradley W. Hardy",1838,1853,12));
allPersons.add(new Person("Jordan Campbell",1837,1852,20));
allPersons.add(new Person("Leonard C. Garrett",1839,1854,23));
allPersons.add(new Person("Todd Elliott",1838,1855,27));
allPersons.add(new Person("Bruce Higgins",1840,1853,24));
allPersons.add(new Person("Ryan Hall",1840,1850,27));
allPersons.add(new Person("H.D. Carroll",1836,1853,37));
allPersons.add(new Person("Chad Glass",1838,1855,33));
allPersons.add(new Person("H.O. Fraser",1840,1852,32));
allPersons.add(new Person("Norman Daniel Sr.",1839,1858,32));
allPersons.add(new Person("Corey L. Holloway",1838,1853,30));
allPersons.add(new Person("Eric Fleming IV",1836,1859,47));
allPersons.add(new Person("Brandon Glover",1836,1858,42));
allPersons.add(new Person("Clark Hudson I",1838,1855,45));
allPersons.add(new Person("Stephen Barnes",1836,1853,41));
allPersons.add(new Person("Ethan Ellis",1837,1852,41));
allPersons.add(new Person("Edward S. Kennedy",1840,1855,54));
allPersons.add(new Person("William George",1840,1858,51));
allPersons.add(new Person("Bradley U. Allen",1836,1854,53));
allPersons.add(new Person("Logan Clark",1837,1854,54));
allPersons.add(new Person("Kenneth Carpenter I",1839,1855,57));
allPersons.add(new Person("Robert P. Chase",1839,1857,65));
allPersons.add(new Person("Keith Dawson",1837,1850,66));
allPersons.add(new Person("Roger Logan",1836,1856,63));
allPersons.add(new Person("Sean Lynch",1839,1858,64));
allPersons.add(new Person("Adam George Jr.",1836,1850,61));
allPersons.add(new Person("Walter Long",1838,1859,71));
allPersons.add(new Person("Ryan Holmes Sr.",1840,1850,75));
allPersons.add(new Person("Nathan L. Garrett",1837,1853,76));
allPersons.add(new Person("Bruce Ingram",1839,1852,77));
allPersons.add(new Person("Peter Jackson",1836,1855,73));
allPersons.add(new Person("Curtis Andrews",1837,1854,86));
allPersons.add(new Person("Kyle Blake",1836,1854,84));
allPersons.add(new Person("O.F. Harding",1836,1855,87));
allPersons.add(new Person("Isaac Hopkins",1839,1856,85));
allPersons.add(new Person("Brandon Fletcher Sr.",1838,1852,80));
allPersons.add(new Person("Chad F. Fitzgerald",1836,1852,97));
allPersons.add(new Person("Kenneth Hoffman",1838,1851,96));
allPersons.add(new Person("Tyler D. Brooks",1837,1852,90));
allPersons.add(new Person("Dean Blair III",1839,1854,97));
allPersons.add(new Person("D.W. Lowe",1837,1857,95));
allPersons.add(new Person("Jason Ingram",1849,1862,0));
allPersons.add(new Person("Bradley Leonard",1849,1860,3));
allPersons.add(new Person("Michael X. Chambers",1846,1861,5));
allPersons.add(new Person("M.Z. Farmer Sr.",1850,1868,0));
allPersons.add(new Person("Lawrence V. Graham III",1846,1862,0));
allPersons.add(new Person("Colin Harris",1848,1862,10));
allPersons.add(new Person("Jason Gray",1847,1863,13));
allPersons.add(new Person("Clayton Edwards",1848,1868,16));
allPersons.add(new Person("Joel Hamilton",1847,1866,11));
allPersons.add(new Person("Sean Fowler II",1849,1860,17));
allPersons.add(new Person("Raymond A. Gates",1846,1868,20));
allPersons.add(new Person("Curtis Drake III",1849,1861,26));
allPersons.add(new Person("Joel O. Cox",1846,1865,20));
allPersons.add(new Person("Tyler Lynch Jr.",1846,1869,23));
allPersons.add(new Person("A.A. Henry",1849,1865,25));
allPersons.add(new Person("Steven Y. Harding",1847,1863,36));
allPersons.add(new Person("David Fowler Sr.",1849,1862,30));
allPersons.add(new Person("Danny J. Berry",1846,1868,37));
allPersons.add(new Person("Samuel Cunningham",1846,1860,31));
allPersons.add(new Person("Jeffrey Barnett",1846,1861,35));
allPersons.add(new Person("Owen Gardner",1849,1867,40));
allPersons.add(new Person("Louis Burgess",1847,1860,45));
allPersons.add(new Person("Howard P. Griffith",1848,1868,42));
allPersons.add(new Person("Eugene W. Clayton",1846,1866,41));
allPersons.add(new Person("Glen Mackenzie",1850,1862,45));
allPersons.add(new Person("K.E. Lyons",1849,1865,51));
allPersons.add(new Person("Alan Black",1849,1868,57));
allPersons.add(new Person("Dustin P. Atkinson",1847,1867,51));
allPersons.add(new Person("Roger Case",1846,1868,55));
allPersons.add(new Person("Leonard Garrett",1848,1861,52));
allPersons.add(new Person("Jordan Campbell",1846,1864,62));
allPersons.add(new Person("U.Q. Fox",1847,1860,64));
allPersons.add(new Person("Jack Kerr",1850,1865,66));
allPersons.add(new Person("Adrian Beck I",1848,1866,62));
allPersons.add(new Person("Frederick Bowman",1846,1868,66));
allPersons.add(new Person("K.J. Dixon",1846,1863,71));
allPersons.add(new Person("E.P. Butler III",1847,1869,70));
allPersons.add(new Person("Justin Clayton",1846,1864,77));
allPersons.add(new Person("Austin Ford II",1849,1867,72));
allPersons.add(new Person("Edward S. Holmes Jr.",1848,1865,70));
allPersons.add(new Person("Derek Baker",1846,1867,86));
allPersons.add(new Person("C.U. Abbott",1848,1861,81));
allPersons.add(new Person("Alan Black",1850,1865,82));
allPersons.add(new Person("Dennis Barrett",1848,1866,80));
allPersons.add(new Person("Wayne B. Ford II",1846,1868,84));
allPersons.add(new Person("Stephen K. Hughes Jr.",1848,1863,91));
allPersons.add(new Person("X.X. Gardner",1850,1869,95));
allPersons.add(new Person("Kyle Copeland",1846,1862,96));
allPersons.add(new Person("Martin Fowler",1850,1867,93));
allPersons.add(new Person("Edwin Carlson Sr.",1848,1864,91));
allPersons.add(new Person("V.Q. Crawford I",1857,1874,6));
allPersons.add(new Person("Alexander Joseph",1858,1874,0));
allPersons.add(new Person("Clark Clayton",1859,1877,1));
allPersons.add(new Person("David K. Carpenter",1860,1873,2));
allPersons.add(new Person("Franklin D. Cox",1858,1876,2));
allPersons.add(new Person("A.L. Ferguson",1857,1875,11));
allPersons.add(new Person("Cody Coleman",1859,1874,16));
allPersons.add(new Person("Evan Fraser",1860,1870,17));
allPersons.add(new Person("Gilbert Chase",1856,1878,11));
allPersons.add(new Person("N.O. Copeland",1860,1879,13));
allPersons.add(new Person("U.O. Howell",1856,1871,27));
allPersons.add(new Person("Christopher Drake",1856,1871,23));
allPersons.add(new Person("X.J. Harding",1857,1879,27));
allPersons.add(new Person("Roy E. Holloway",1858,1870,20));
allPersons.add(new Person("George E. Jackson III",1859,1871,20));
allPersons.add(new Person("U.K. Hampton",1857,1874,33));
allPersons.add(new Person("Tyler Gross",1858,1876,37));
allPersons.add(new Person("Adam F. Hodges",1860,1871,35));
allPersons.add(new Person("Albert M. James",1856,1870,30));
allPersons.add(new Person("Brian Davidson Sr.",1858,1873,36));
allPersons.add(new Person("Paul K. Clark",1858,1870,45));
allPersons.add(new Person("Z.M. Brewer",1856,1874,43));
allPersons.add(new Person("T.A. Jackson",1860,1879,43));
allPersons.add(new Person("Jesse G. Kelly",1858,1873,45));
allPersons.add(new Person("C.C. Carr Jr.",1860,1876,43));
allPersons.add(new Person("Z.V. Alexander",1857,1877,55));
allPersons.add(new Person("Luke Y. Giles",1860,1878,57));
allPersons.add(new Person("Cody Ball II",1858,1871,53));
allPersons.add(new Person("Glen L. Cox",1858,1875,56));
allPersons.add(new Person("Donald Graham",1858,1870,52));
allPersons.add(new Person("D.U. Baker",1856,1873,64));
allPersons.add(new Person("Mark Jenkins",1859,1873,67));
allPersons.add(new Person("Lucas Hines",1858,1873,61));
allPersons.add(new Person("Howard Q. Blair",1860,1876,65));
allPersons.add(new Person("Dustin Carlson",1860,1870,62));
allPersons.add(new Person("Carl Day",1858,1871,71));
allPersons.add(new Person("M.J. Higgins",1857,1876,72));
allPersons.add(new Person("H.W. Copeland Jr.",1860,1872,77));
allPersons.add(new Person("K.J. Hughes",1860,1878,76));
allPersons.add(new Person("Mark T. Cobb",1857,1871,76));
allPersons.add(new Person("Gerald Gardner",1856,1876,82));
allPersons.add(new Person("Alexander Barker",1858,1873,85));
allPersons.add(new Person("Brian Chapman",1856,1871,81));
allPersons.add(new Person("Carl Harris",1856,1875,84));
allPersons.add(new Person("Sean Greene IV",1858,1876,82));
allPersons.add(new Person("Ernest Burke",1857,1870,91));
allPersons.add(new Person("Stanley Daniel Sr.",1858,1873,91));
allPersons.add(new Person("F.P. Holloway",1859,1878,95));
allPersons.add(new Person("C.M. Caldwell",1857,1870,94));
allPersons.add(new Person("D.M. Lambert",1857,1873,95));
allPersons.add(new Person("Clarence Hunter",1867,1881,0));
allPersons.add(new Person("X.J. Butler",1870,1883,3));
allPersons.add(new Person("Eugene Gilbert",1869,1884,0));
allPersons.add(new Person("Jeffrey Harding",1869,1887,4));
allPersons.add(new Person("Charles Clarke",1868,1882,2));
allPersons.add(new Person("Austin Knight",1870,1880,12));
allPersons.add(new Person("N.X. Hale",1869,1888,11));
allPersons.add(new Person("Ronald Cooper",1866,1887,14));
allPersons.add(new Person("Dennis Graham",1869,1881,17));
allPersons.add(new Person("Gordon Hodges",1868,1884,13));
allPersons.add(new Person("Z.C. Carroll III",1867,1884,27));
allPersons.add(new Person("Clark Crawford",1869,1889,26));
allPersons.add(new Person("Ross E. Davis",1869,1888,22));
allPersons.add(new Person("Jerome Hogan",1870,1881,25));
allPersons.add(new Person("Jeremy D. Curtis",1869,1881,24));
allPersons.add(new Person("Earl Day II",1868,1883,35));
allPersons.add(new Person("Charles Fowler",1870,1881,33));
allPersons.add(new Person("X.Y. Lawrence",1869,1889,34));
allPersons.add(new Person("Ross T. Hicks",1866,1885,36));
allPersons.add(new Person("Justin Barnett",1867,1886,31));
allPersons.add(new Person("K.E. Mackenzie",1867,1889,45));
allPersons.add(new Person("Samuel Harris",1868,1884,43));
allPersons.add(new Person("Jesse L. Adkins",1867,1888,47));
allPersons.add(new Person("B.A. Doyle",1866,1887,47));
allPersons.add(new Person("Henry Black",1870,1887,44));
allPersons.add(new Person("Roger C. Barker",1870,1880,55));
allPersons.add(new Person("Ernest Jackson",1870,1883,53));
allPersons.add(new Person("Hunter Arnold",1866,1887,51));
allPersons.add(new Person("Patrick X. Dutton",1866,1880,52));
allPersons.add(new Person("Victor E. Bennett",1866,1885,53));
allPersons.add(new Person("Victor Drake",1869,1882,67));
allPersons.add(new Person("Patrick Bowman",1867,1880,64));
allPersons.add(new Person("Ernest L. Graham",1868,1882,62));
allPersons.add(new Person("D.D. Hunter",1867,1882,63));
allPersons.add(new Person("Harvey Barker",1866,1883,67));
allPersons.add(new Person("Edward Alexander I",1870,1883,77));
allPersons.add(new Person("Alan I. Barker",1870,1886,72));
allPersons.add(new Person("Owen Baker II",1867,1887,71));
allPersons.add(new Person("Gordon A. Harris",1869,1889,70));
allPersons.add(new Person("Walter Graham",1867,1884,71));
allPersons.add(new Person("Ralph Hammond",1868,1882,85));
allPersons.add(new Person("Harold Kent",1867,1880,81));
allPersons.add(new Person("Oscar Harris",1868,1889,80));
allPersons.add(new Person("David Jennings",1869,1881,85));
allPersons.add(new Person("Philip Gates",1870,1889,83));
allPersons.add(new Person("D.M. Clayton",1868,1889,90));
allPersons.add(new Person("Andrew Arnold",1867,1881,93));
allPersons.add(new Person("A.A. Fox",1870,1884,91));
allPersons.add(new Person("Patrick Harding",1870,1889,93));
allPersons.add(new Person("Kevin Gross",1870,1888,91));
allPersons.add(new Person("Frederick Leonard II",1877,1891,5));
allPersons.add(new Person("Bruce Hart",1880,1896,1));
allPersons.add(new Person("Frederick Lowe",1880,1897,2));
allPersons.add(new Person("E.E. Atkinson",1880,1899,0));
allPersons.add(new Person("Evan E. Green",1879,1895,7));
allPersons.add(new Person("Ronald Alexander",1879,1892,13));
allPersons.add(new Person("Maurice Crawford",1878,1896,11));
allPersons.add(new Person("John Fox",1880,1891,10));
allPersons.add(new Person("Ian Jordan",1880,1895,11));
allPersons.add(new Person("Andrew Daniels",1880,1890,13));
allPersons.add(new Person("Ernest Fields",1877,1898,22));
allPersons.add(new Person("Donald Hampton IV",1878,1895,20));
allPersons.add(new Person("Michael J. Day",1877,1895,27));
allPersons.add(new Person("James Hardy III",1879,1893,24));
allPersons.add(new Person("Jack Kaufman Sr.",1876,1898,23));
allPersons.add(new Person("Philip F. Burke",1877,1891,30));
allPersons.add(new Person("M.A. Davis",1876,1899,34));
allPersons.add(new Person("Clayton Bates III",1877,1890,31));
allPersons.add(new Person("Douglas T. Bradley",1879,1896,32));
allPersons.add(new Person("James Ball II",1878,1890,33));
allPersons.add(new Person("Victor E. Daniel",1879,1890,40));
allPersons.add(new Person("F.K. Gordon",1879,1897,41));
allPersons.add(new Person("X.T. Barnes",1880,1897,46));
allPersons.add(new Person("C.E. Dutton",1879,1896,45));
allPersons.add(new Person("Ernest Johnston",1877,1894,45));
allPersons.add(new Person("Patrick Kelly Jr.",1877,1896,56));
allPersons.add(new Person("Dale Carter",1876,1897,55));
allPersons.add(new Person("Gerald Kennedy",1878,1896,55));
allPersons.add(new Person("Curtis Holt",1877,1890,55));
allPersons.add(new Person("P.J. Carter",1876,1894,53));
allPersons.add(new Person("Jerome Hale",1880,1891,60));
allPersons.add(new Person("Connor Gardner",1877,1890,65));
allPersons.add(new Person("Jason Little",1877,1894,67));
allPersons.add(new Person("Connor Chase",1877,1898,65));
allPersons.add(new Person("Y.A. Curtis",1877,1894,65));
allPersons.add(new Person("Zachary X. Bennett",1877,1891,70));
allPersons.add(new Person("Charles Q. Hall",1879,1890,75));
allPersons.add(new Person("Bradley Ford",1877,1895,72));
allPersons.add(new Person("Dale Griffith II",1876,1891,70));
allPersons.add(new Person("Brandon Kerr",1877,1897,77));
allPersons.add(new Person("Harvey Lambert IV",1879,1890,81));
allPersons.add(new Person("J.X. Burke Sr.",1877,1895,86));
allPersons.add(new Person("William Lucas",1878,1891,87));
allPersons.add(new Person("T.Q. Barker",1876,1896,87));
allPersons.add(new Person("Clifford A. Arnold",1878,1891,87));
allPersons.add(new Person("Roger Horton",1880,1898,95));
allPersons.add(new Person("Mark S. Kerr II",1880,1899,94));
allPersons.add(new Person("Colin Higgins I",1878,1894,97));
allPersons.add(new Person("Clarence Evans III",1876,1899,97));
allPersons.add(new Person("Bryan Love",1877,1893,92));
allPersons.add(new Person("Norman Lawson",1888,1907,4));
allPersons.add(new Person("J.X. Kerr",1886,1905,6));
allPersons.add(new Person("Lawrence Douglas",1889,1902,1));
allPersons.add(new Person("Lawrence Hammond",1889,1906,4));
allPersons.add(new Person("Clark Bradley",1889,1900,6));
allPersons.add(new Person("Logan Bradley",1889,1905,13));
allPersons.add(new Person("Bruce Andrews",1889,1908,10));
allPersons.add(new Person("Francis Brady",1887,1906,13));
allPersons.add(new Person("Kevin N. Dixon",1889,1900,12));
allPersons.add(new Person("Carl V. Barnes Jr.",1888,1907,12));
allPersons.add(new Person("Christopher V. Carroll",1889,1908,25));
allPersons.add(new Person("Francis Gates",1888,1908,22));
allPersons.add(new Person("Justin A. James",1889,1908,20));
allPersons.add(new Person("Logan Collins",1886,1905,21));
allPersons.add(new Person("Darrell Barnett",1887,1903,22));
allPersons.add(new Person("Keith Ellis II",1890,1900,32));
allPersons.add(new Person("Y.Y. Love",1888,1906,34));
allPersons.add(new Person("Bruce Dutton",1889,1909,37));
allPersons.add(new Person("Gordon Hunter",1887,1905,35));
allPersons.add(new Person("Derek Knight",1886,1903,33));
allPersons.add(new Person("N.R. Harvey",1886,1904,40));
allPersons.add(new Person("Isaac Gross",1889,1905,45));
allPersons.add(new Person("Keith C. Henderson",1889,1902,45));
allPersons.add(new Person("Jonathan Coleman",1887,1904,41));
allPersons.add(new Person("Lewis Jones",1886,1902,41));
allPersons.add(new Person("Gordon Hicks",1890,1901,55));
allPersons.add(new Person("E.X. Hodges",1890,1901,53));
allPersons.add(new Person("Arthur Hall",1890,1900,52));
allPersons.add(new Person("Robert Giles",1886,1903,54));
allPersons.add(new Person("Eugene Carlson",1890,1905,54));
allPersons.add(new Person("Owen X. Burgess",1886,1903,64));
allPersons.add(new Person("Walter U. Bailey",1890,1908,64));
allPersons.add(new Person("Douglas R. Beck",1887,1906,60));
allPersons.add(new Person("O.T. Baldwin",1890,1900,66));
allPersons.add(new Person("Ian C. Hunter III",1888,1908,67));
allPersons.add(new Person("Vincent Carter",1886,1906,77));
allPersons.add(new Person("Joseph E. Freeman",1888,1904,77));
allPersons.add(new Person("I.K. Chambers",1890,1902,72));
allPersons.add(new Person("Scott Hamilton",1887,1901,70));
allPersons.add(new Person("B.E. Dunn",1887,1907,70));
allPersons.add(new Person("William Coleman Jr.",1890,1906,87));
allPersons.add(new Person("E.A. Carroll",1886,1907,87));
allPersons.add(new Person("Gary Barnes",1888,1901,86));
allPersons.add(new Person("Terrence Gibson",1886,1905,84));
allPersons.add(new Person("J.F. Johnson",1887,1905,86));
allPersons.add(new Person("J.F. Joseph",1888,1907,97));
allPersons.add(new Person("Jerome Hubbard",1887,1907,94));
allPersons.add(new Person("Thomas George",1890,1905,92));
allPersons.add(new Person("William Ball",1886,1907,97));
allPersons.add(new Person("Herbert Hopkins",1886,1907,96));
allPersons.add(new Person("Owen Gray",1899,1919,4));
allPersons.add(new Person("Joseph Q. Drake",1896,1918,1));
allPersons.add(new Person("Kevin L. Ford II",1897,1915,2));
allPersons.add(new Person("Daniel Greene",1897,1914,2));
allPersons.add(new Person("Kenneth Hammond",1898,1918,2));
allPersons.add(new Person("Keith Alexander",1900,1915,17));
allPersons.add(new Person("Brian M. Bailey",1900,1915,10));
allPersons.add(new Person("Ross Elliott I",1899,1911,10));
allPersons.add(new Person("Terrence T. Joseph",1898,1919,13));
allPersons.add(new Person("Adrian Carlson",1898,1913,14));
allPersons.add(new Person("H.R. Duncan",1899,1916,22));
allPersons.add(new Person("Gordon King",1898,1912,26));
allPersons.add(new Person("Timothy Douglas Jr.",1900,1910,26));
allPersons.add(new Person("Lucas Caldwell",1898,1916,27));
allPersons.add(new Person("Kenneth Haynes",1898,1913,20));
allPersons.add(new Person("Martin H. Bell",1898,1917,30));
allPersons.add(new Person("Gabriel Y. Andrews",1899,1918,31));
allPersons.add(new Person("Keith Cunningham",1896,1912,34));
allPersons.add(new Person("Timothy George",1898,1918,35));
allPersons.add(new Person("Brandon Hudson",1896,1919,33));
allPersons.add(new Person("Anthony Hampton",1897,1915,42));
allPersons.add(new Person("Keith Douglas",1899,1916,42));
allPersons.add(new Person("Sean Barker",1897,1913,44));
allPersons.add(new Person("Connor N. Gardner IV",1898,1916,46));
allPersons.add(new Person("Keith Johnson",1896,1917,46));
allPersons.add(new Person("Jerome C. Atkinson II",1897,1912,51));
allPersons.add(new Person("Roger Adkins",1896,1913,55));
allPersons.add(new Person("Seth Burke",1899,1913,54));
allPersons.add(new Person("Joel Clayton",1896,1918,53));
allPersons.add(new Person("A.I. Clements",1899,1911,53));
allPersons.add(new Person("Clarence E. Lowe II",1896,1915,64));
allPersons.add(new Person("W.S. Fowler",1896,1914,64));
allPersons.add(new Person("Sean Ford",1898,1918,67));
allPersons.add(new Person("C.T. Greene",1898,1913,67));
allPersons.add(new Person("S.D. Graham",1899,1915,66));
allPersons.add(new Person("Logan S. Burton III",1898,1918,75));
allPersons.add(new Person("Owen Holloway I",1897,1915,76));
allPersons.add(new Person("Gary Ingram",1896,1914,73));
allPersons.add(new Person("Isaac Bowman",1899,1910,74));
allPersons.add(new Person("Curtis Fisher",1899,1910,72));
allPersons.add(new Person("Leonard Allen",1900,1912,86));
allPersons.add(new Person("Clark Hale",1896,1917,82));
allPersons.add(new Person("Owen N. Barnes",1898,1917,86));
allPersons.add(new Person("John Hudson",1899,1915,87));
allPersons.add(new Person("N.S. Hunt",1896,1914,80));
allPersons.add(new Person("Gordon Lee",1898,1911,91));
allPersons.add(new Person("Evan Carpenter",1900,1919,90));
allPersons.add(new Person("Aaron Leonard",1900,1913,96));
allPersons.add(new Person("Ian Davidson",1899,1915,92));
allPersons.add(new Person("Curtis Copeland",1900,1910,95));
allPersons.add(new Person("Joel Lynch",1909,1922,0));
allPersons.add(new Person("D.T. Lawson",1910,1928,7));
allPersons.add(new Person("L.C. Hammond",1907,1920,0));
allPersons.add(new Person("Vincent Bush",1910,1924,3));
allPersons.add(new Person("Clark Baldwin",1909,1928,5));
allPersons.add(new Person("John Barnett I",1908,1921,15));
allPersons.add(new Person("Kevin J. Cooper",1909,1923,11));
allPersons.add(new Person("Alexander Hampton",1908,1920,13));
allPersons.add(new Person("Philip Chambers",1910,1925,11));
allPersons.add(new Person("Isaac Hicks",1907,1924,14));
allPersons.add(new Person("Peter Alexander",1908,1920,24));
allPersons.add(new Person("S.Z. Hammond",1906,1926,24));
allPersons.add(new Person("Zachary Burgess",1906,1921,20));
allPersons.add(new Person("X.H. Jennings",1906,1926,27));
allPersons.add(new Person("Edward P. Hammond",1906,1920,27));
allPersons.add(new Person("Andrew Little",1909,1923,37));
allPersons.add(new Person("Justin B. Howard I",1907,1920,32));
allPersons.add(new Person("Charles Lane",1906,1923,35));
allPersons.add(new Person("Gilbert Q. Cobb",1908,1924,37));
allPersons.add(new Person("Henry Harrison",1906,1923,33));
allPersons.add(new Person("Stanley Kelley Sr.",1909,1929,43));
allPersons.add(new Person("Philip Lynch",1906,1922,42));
allPersons.add(new Person("Shawn Holland",1907,1929,44));
allPersons.add(new Person("John Crawford",1910,1922,40));
allPersons.add(new Person("Daniel Gilbert",1908,1922,43));
allPersons.add(new Person("A.J. Bennett III",1907,1920,57));
allPersons.add(new Person("Russell Douglas I",1909,1921,56));
allPersons.add(new Person("Frederick Kennedy I",1907,1925,55));
allPersons.add(new Person("Cameron Dixon",1908,1925,51));
allPersons.add(new Person("Cody Campbell",1906,1920,52));
allPersons.add(new Person("Norman Copeland Jr.",1907,1929,60));
allPersons.add(new Person("Douglas Jacobs IV",1910,1923,60));
allPersons.add(new Person("Kyle Hampton",1906,1929,61));
allPersons.add(new Person("Jason Clayton IV",1909,1926,60));
allPersons.add(new Person("Anthony Logan",1906,1924,63));
allPersons.add(new Person("Austin Jennings",1910,1922,72));
allPersons.add(new Person("A.E. Lawrence",1909,1928,72));
allPersons.add(new Person("Bruce B. Blake IV",1909,1928,73));
allPersons.add(new Person("Gerald Holloway IV",1906,1925,70));
allPersons.add(new Person("Nathan Farmer",1908,1929,76));
allPersons.add(new Person("John R. Lynch",1908,1927,82));
allPersons.add(new Person("Arthur Giles",1907,1922,84));
allPersons.add(new Person("Paul Chambers",1910,1920,81));
allPersons.add(new Person("Leonard K. Bailey",1906,1920,82));
allPersons.add(new Person("F.X. Fowler",1909,1925,87));
allPersons.add(new Person("N.Q. Craig",1909,1920,91));
allPersons.add(new Person("Steven Cook",1906,1923,93));
allPersons.add(new Person("Nicholas Chandler",1907,1921,92));
allPersons.add(new Person("Ernest Collins",1907,1923,93));
allPersons.add(new Person("Andrew V. Clements",1908,1927,92));
allPersons.add(new Person("Kenneth R. Gross IV",1919,1933,0));
allPersons.add(new Person("Earl Baker",1918,1932,3));
allPersons.add(new Person("Craig Lucas IV",1920,1932,3));
allPersons.add(new Person("Bernard Carroll",1917,1939,5));
allPersons.add(new Person("Gordon Alexander",1917,1938,1));
allPersons.add(new Person("Brian I. Johnston",1918,1937,17));
allPersons.add(new Person("Oscar S. Burgess",1917,1938,13));
allPersons.add(new Person("Tyler Evans",1920,1937,17));
allPersons.add(new Person("Mark Hayes",1916,1934,13));
allPersons.add(new Person("S.M. Henderson III",1916,1932,17));
allPersons.add(new Person("Dustin Douglas I",1917,1933,25));
allPersons.add(new Person("Justin E. Blair Jr.",1917,1937,24));
allPersons.add(new Person("Bryan Fields",1918,1932,21));
allPersons.add(new Person("Norman F. Hayes",1917,1936,23));
allPersons.add(new Person("Jesse Adkins",1919,1934,26));
allPersons.add(new Person("M.G. Cole",1919,1931,31));
allPersons.add(new Person("N.K. Gibson",1920,1933,33));
allPersons.add(new Person("Mitchell T. Gilbert",1919,1934,34));
allPersons.add(new Person("Charles Bates",1917,1936,31));
allPersons.add(new Person("Keith Knight",1918,1937,31));
allPersons.add(new Person("Herbert Hunter",1919,1933,42));
allPersons.add(new Person("Douglas Chapman",1916,1933,45));
allPersons.add(new Person("Earl Fletcher",1918,1932,43));
allPersons.add(new Person("Zachary Y. Clark",1919,1932,41));
allPersons.add(new Person("Eugene Burgess",1916,1936,45));
allPersons.add(new Person("Brian Brady",1918,1938,55));
allPersons.add(new Person("Gordon Curtis",1916,1930,54));
allPersons.add(new Person("Eric Crawford",1920,1939,50));
allPersons.add(new Person("Luke R. Fields",1918,1939,53));
allPersons.add(new Person("Ernest Burgess",1916,1938,57));
allPersons.add(new Person("Arthur Gray",1916,1934,64));
allPersons.add(new Person("Lucas Bush",1916,1932,62));
allPersons.add(new Person("Clifford Burke",1916,1932,62));
allPersons.add(new Person("Tyler Brewer Jr.",1917,1934,62));
allPersons.add(new Person("Richard Hardy",1918,1934,66));
allPersons.add(new Person("Edwin Boyd I",1919,1937,72));
allPersons.add(new Person("Mitchell C. Gardner",1918,1932,77));
allPersons.add(new Person("I.Z. Ingram",1916,1934,75));
allPersons.add(new Person("N.F. Coleman",1919,1934,73));
allPersons.add(new Person("Francis Berry",1919,1931,72));
allPersons.add(new Person("Jerome L. Jackson",1916,1939,86));
allPersons.add(new Person("Darrell Fowler I",1918,1933,85));
allPersons.add(new Person("Martin I. Barnes",1918,1935,83));
allPersons.add(new Person("Caleb Blake II",1916,1932,85));
allPersons.add(new Person("B.F. Jacobs I",1917,1934,83));
allPersons.add(new Person("Raymond Carter I",1919,1939,90));
allPersons.add(new Person("Clayton Douglas",1919,1931,96));
allPersons.add(new Person("Shane Jensen Jr.",1919,1932,96));
allPersons.add(new Person("Clifford Ingram",1918,1930,91));
allPersons.add(new Person("Norman Howard",1917,1934,93));
allPersons.add(new Person("Cameron Coleman II",1927,1949,3));
allPersons.add(new Person("Richard Hill I",1930,1946,0));
allPersons.add(new Person("Donald Haynes I",1928,1944,6));
allPersons.add(new Person("Timothy D. Jordan",1926,1941,4));
allPersons.add(new Person("Bernard Kennedy",1927,1943,6));
allPersons.add(new Person("David Coleman Jr.",1930,1949,12));
allPersons.add(new Person("Edward B. Arnold",1929,1940,11));
allPersons.add(new Person("Vincent Hunter",1928,1946,13));
allPersons.add(new Person("W.Y. Abbott",1927,1947,11));
allPersons.add(new Person("W.S. Evans",1926,1948,14));
allPersons.add(new Person("Shawn Jordan II",1929,1942,22));
allPersons.add(new Person("Owen Lucas",1927,1943,26));
allPersons.add(new Person("Jordan K. Bailey",1930,1940,21));
allPersons.add(new Person("Harold Cross",1926,1948,25));
allPersons.add(new Person("Darrell Fleming",1926,1946,27));
allPersons.add(new Person("Travis T. Davis Jr.",1928,1949,30));
allPersons.add(new Person("S.U. Curtis",1929,1940,37));
allPersons.add(new Person("Derek Berry",1927,1941,35));
allPersons.add(new Person("Gabriel Hoffman",1926,1947,30));
allPersons.add(new Person("Donald Douglas",1929,1947,34));
allPersons.add(new Person("Danny Hall",1928,1944,43));
allPersons.add(new Person("G.E. Lee Sr.",1928,1944,45));
allPersons.add(new Person("Kyle L. Fleming",1926,1945,46));
allPersons.add(new Person("Dennis G. Ferguson",1929,1949,41));
allPersons.add(new Person("A.E. Fitzgerald",1927,1947,44));
allPersons.add(new Person("Connor Long Jr.",1930,1946,51));
allPersons.add(new Person("Charles Collins",1929,1940,54));
allPersons.add(new Person("Christopher Carroll",1926,1942,52));
allPersons.add(new Person("Brandon Allen",1927,1949,52));
allPersons.add(new Person("George Leonard",1927,1948,53));
allPersons.add(new Person("I.Q. Lucas",1930,1944,62));
allPersons.add(new Person("Clayton Hodges",1926,1947,67));
allPersons.add(new Person("Adrian Brady",1930,1946,67));
allPersons.add(new Person("Harry Baldwin",1926,1945,64));
allPersons.add(new Person("Gilbert Dawson",1926,1949,61));
allPersons.add(new Person("Aaron Dawson",1929,1943,72));
allPersons.add(new Person("O.M. Chapman",1926,1948,72));
allPersons.add(new Person("Andrew Harrison",1929,1943,77));
allPersons.add(new Person("C.G. Hudson",1926,1948,72));
allPersons.add(new Person("I.K. Fitzgerald",1927,1948,72));
allPersons.add(new Person("Craig Jensen",1930,1949,85));
allPersons.add(new Person("O.T. Bradley",1927,1942,87));
allPersons.add(new Person("T.Q. Hawkins",1929,1945,80));
allPersons.add(new Person("Adam Allen Jr.",1930,1941,80));
allPersons.add(new Person("Glen K. Kennedy I",1929,1941,86));
allPersons.add(new Person("Colin Austin I",1930,1943,96));
allPersons.add(new Person("Earl G. Fisher IV",1927,1940,93));
allPersons.add(new Person("Q.P. Kelley",1929,1941,93));
allPersons.add(new Person("Mark James",1926,1940,95));
allPersons.add(new Person("Harvey T. Lee",1930,1942,92));
allPersons.add(new Person("Clarence Anderson",1940,1953,2));
allPersons.add(new Person("Chad Gibson",1939,1954,1));
allPersons.add(new Person("Jeffrey Q. Hardy",1940,1955,3));
allPersons.add(new Person("P.X. Gregory",1940,1959,2));
allPersons.add(new Person("Andrew Chambers",1939,1953,1));
allPersons.add(new Person("Jason L. Arnold",1936,1954,10));
allPersons.add(new Person("C.Y. Hoffman II",1938,1957,11));
allPersons.add(new Person("U.Q. Freeman",1939,1952,12));
allPersons.add(new Person("O.P. Brady",1936,1957,10));
allPersons.add(new Person("Clifford Brooks",1938,1950,16));
allPersons.add(new Person("Z.A. Glover I",1938,1958,21));
allPersons.add(new Person("Wesley Kent",1939,1951,25));
allPersons.add(new Person("James K. Hoffman",1938,1954,22));
allPersons.add(new Person("Bryan Copeland I",1939,1950,26));
allPersons.add(new Person("Ronald B. Hall",1937,1957,21));
allPersons.add(new Person("Owen H. Jordan I",1940,1953,35));
allPersons.add(new Person("I.T. Holmes",1936,1956,32));
allPersons.add(new Person("Jerome Hodges",1937,1951,33));
allPersons.add(new Person("Y.J. Burke",1938,1958,36));
allPersons.add(new Person("Adam H. Kerr",1939,1954,34));
allPersons.add(new Person("Eugene Harris",1939,1951,47));
allPersons.add(new Person("Eric W. Bailey",1937,1952,43));
allPersons.add(new Person("Andrew Clements I",1939,1957,41));
allPersons.add(new Person("Colin Baldwin",1940,1955,40));
allPersons.add(new Person("Todd Curtis",1938,1953,45));
allPersons.add(new Person("Mark F. Long",1936,1953,56));
allPersons.add(new Person("Gerald Brooks",1939,1958,53));
allPersons.add(new Person("Jack Adams III",1940,1951,54));
allPersons.add(new Person("Norman A. Dean",1939,1959,50));
allPersons.add(new Person("Ralph Griffith IV",1936,1958,57));
allPersons.add(new Person("Kevin U. Franklin",1940,1954,60));
allPersons.add(new Person("Ralph N. Daniels III",1939,1955,60));
allPersons.add(new Person("Gary N. Baldwin",1940,1959,65));
allPersons.add(new Person("Daniel Case",1936,1958,62));
allPersons.add(new Person("Herbert Glover IV",1936,1958,67));
allPersons.add(new Person("Herbert Beck",1938,1955,74));
allPersons.add(new Person("Lucy C. Cook",1937,1954,77));
allPersons.add(new Person("Bradley Hale",1936,1952,70));
allPersons.add(new Person("David O. Johnson",1940,1956,75));
allPersons.add(new Person("Nathan Franklin",1937,1956,72));
allPersons.add(new Person("Steven T. Franklin",1938,1952,83));
allPersons.add(new Person("Robert Greene Jr.",1940,1958,82));
allPersons.add(new Person("Charles F. Kelly",1938,1956,84));
allPersons.add(new Person("Ernest Cunningham",1936,1955,85));
allPersons.add(new Person("William Hammond",1938,1951,83));
allPersons.add(new Person("Corey V. Daniel",1939,1958,91));
allPersons.add(new Person("W.Z. Bishop",1936,1954,91));
allPersons.add(new Person("Ronald Hudson",1936,1958,91));
allPersons.add(new Person("Ian Horton",1938,1951,95));
allPersons.add(new Person("Daniel Dixon",1940,1955,90));
allPersons.add(new Person("Isaac Henderson II",1949,1966,1));
allPersons.add(new Person("V.A. Cunningham",1950,1961,5));
allPersons.add(new Person("L.H. Lowe",1950,1969,7));
allPersons.add(new Person("Francis Y. Ingram",1946,1969,4));
allPersons.add(new Person("Ross Hamilton",1946,1961,2));
allPersons.add(new Person("Diana Glover",1949,1967,16));
allPersons.add(new Person("Wayne Graham",1948,1969,10));
allPersons.add(new Person("Douglas Barnett",1950,1965,16));
allPersons.add(new Person("Jordan Barnes",1950,1961,12));
allPersons.add(new Person("Stanley Foster",1946,1965,13));
allPersons.add(new Person("Albert C. Cobb",1949,1968,23));
allPersons.add(new Person("Joel Hart",1946,1967,21));
allPersons.add(new Person("Andrew Glover",1948,1969,23));
allPersons.add(new Person("Cody Coleman",1946,1965,23));
allPersons.add(new Person("Eric Griffin",1950,1966,25));
allPersons.add(new Person("B.S. Hogan",1948,1961,33));
allPersons.add(new Person("Derek Z. Carter II",1949,1967,31));
allPersons.add(new Person("Frederick Hodges",1946,1968,33));
allPersons.add(new Person("Michael Logan",1950,1969,32));
allPersons.add(new Person("Gabriel H. Jenkins Sr.",1948,1969,36));
allPersons.add(new Person("Luke Hale",1949,1965,41));
allPersons.add(new Person("Justin Bishop",1947,1968,46));
allPersons.add(new Person("Nicholas L. Lane",1948,1964,45));
allPersons.add(new Person("Lawrence Farmer",1948,1960,41));
allPersons.add(new Person("Arthur Hodges",1947,1966,41));
allPersons.add(new Person("Connor Horton",1949,1960,51));
allPersons.add(new Person("Norman U. Carpenter III",1946,1969,57));
allPersons.add(new Person("Philip Arnold IV",1949,1968,53));
allPersons.add(new Person("Steven Hunt",1947,1962,54));
allPersons.add(new Person("David Y. Fisher",1949,1967,57));
allPersons.add(new Person("Dennis E. Hart III",1949,1962,64));
allPersons.add(new Person("Noah Lawrence",1950,1967,63));
allPersons.add(new Person("Edith Lambert",1949,1963,64));
allPersons.add(new Person("Jeffrey Daniels",1950,1969,60));
allPersons.add(new Person("Colin Graham",1947,1965,62));
allPersons.add(new Person("Christopher Carter",1949,1963,72));
allPersons.add(new Person("Gilbert N. Carter",1946,1969,71));
allPersons.add(new Person("Lewis Austin",1947,1960,77));
allPersons.add(new Person("Gilbert A. Cunningham",1947,1969,75));
allPersons.add(new Person("Angela Fraser-Carlson",1947,1963,74));
allPersons.add(new Person("Kyle Glover",1950,1968,85));
allPersons.add(new Person("W.K. Lawson IV",1948,1966,84));
allPersons.add(new Person("K.Y. Lloyd Jr.",1947,1962,85));
allPersons.add(new Person("Glen Brady II",1947,1962,83));
allPersons.add(new Person("Cheryl Atkinson",1947,1962,83));
allPersons.add(new Person("Andrew Higgins",1948,1969,90));
allPersons.add(new Person("Blake Evans",1950,1963,93));
allPersons.add(new Person("Anne Kent",1950,1962,91));
allPersons.add(new Person("Raymond Burton II",1950,1969,96));
allPersons.add(new Person("Norman Bradley",1949,1963,94));
allPersons.add(new Person("Robert Burgess I",1957,1971,4));
allPersons.add(new Person("Harry Barnes",1956,1973,5));
allPersons.add(new Person("Megan L. Jones",1957,1979,4));
allPersons.add(new Person("Blake Lawrence",1960,1974,2));
allPersons.add(new Person("L.P. Cunningham Sr.",1960,1973,0));
allPersons.add(new Person("Eva Evans",1960,1973,16));
allPersons.add(new Person("Colleen Jacobs",1956,1979,14));
allPersons.add(new Person("Z.S. Ferguson Jr.",1959,1972,12));
allPersons.add(new Person("Oscar Hogan III",1959,1970,15));
allPersons.add(new Person("Sean Brown",1957,1970,13));
allPersons.add(new Person("Hannah Baker",1957,1978,22));
allPersons.add(new Person("Blake Holloway",1960,1972,22));
allPersons.add(new Person("Holly S. Higgins",1959,1976,27));
allPersons.add(new Person("Henry Y. Cole",1960,1978,24));
allPersons.add(new Person("V.G. Baker",1956,1977,20));
allPersons.add(new Person("David Jones",1959,1972,36));
allPersons.add(new Person("Amanda J. Daniel-Howell",1957,1975,31));
allPersons.add(new Person("Martin Green",1957,1979,30));
allPersons.add(new Person("Danielle Cooper-Ford",1956,1973,36));
allPersons.add(new Person("Ronald Gray I",1960,1971,31));
allPersons.add(new Person("Cameron F. Haynes",1958,1974,41));
allPersons.add(new Person("Lewis Fletcher Jr.",1959,1974,47));
allPersons.add(new Person("Evan Hall",1957,1973,47));
allPersons.add(new Person("J.A. Holloway",1958,1974,40));
allPersons.add(new Person("S.J. George",1956,1971,45));
allPersons.add(new Person("Tyler Cross",1957,1977,55));
allPersons.add(new Person("Sean L. Duncan",1960,1972,50));
allPersons.add(new Person("Annette Curtis",1960,1970,57));
allPersons.add(new Person("Christopher R. Lyons",1956,1976,57));
allPersons.add(new Person("R.G. Hicks",1958,1972,51));
allPersons.add(new Person("Raymond K. Little",1958,1970,62));
allPersons.add(new Person("Jane Edwards",1959,1977,61));
allPersons.add(new Person("Hazel I. Hughes-Burgess",1956,1972,62));
allPersons.add(new Person("Henry Ford",1957,1978,67));
allPersons.add(new Person("Anne Hawkins",1957,1977,64));
allPersons.add(new Person("Glen Greene",1958,1979,70));
allPersons.add(new Person("Marilyn Dawson",1960,1979,70));
allPersons.add(new Person("Raymond Caldwell",1960,1974,74));
allPersons.add(new Person("Alan L. Glass",1956,1979,74));
allPersons.add(new Person("Lawrence Z. Harvey",1960,1972,77));
allPersons.add(new Person("Raymond Griffith",1957,1976,81));
allPersons.add(new Person("Melissa Carroll-Bowman",1957,1972,87));
allPersons.add(new Person("James Hunter",1956,1978,87));
allPersons.add(new Person("Z.X. Hunter",1960,1971,86));
allPersons.add(new Person("Amber Clark",1957,1979,85));
allPersons.add(new Person("Matthew Greene",1959,1972,92));
allPersons.add(new Person("Jerome V. Barnett Jr.",1956,1977,91));
allPersons.add(new Person("Ronald Z. Davidson II",1960,1978,97));
allPersons.add(new Person("Aaron Holland",1957,1979,93));
allPersons.add(new Person("Edward Kelly Jr.",1959,1972,93));
allPersons.add(new Person("Arthur Chambers",1967,1989,2));
allPersons.add(new Person("Adam Griffith IV",1970,1989,4));
allPersons.add(new Person("Michael H. Long III",1970,1986,5));
allPersons.add(new Person("I.H. Hunter",1969,1985,3));
allPersons.add(new Person("Madeline Howell-Haynes",1970,1985,5));
allPersons.add(new Person("Jennifer Farmer",1969,1989,14));
allPersons.add(new Person("Ross Chapman",1970,1989,13));
allPersons.add(new Person("M.D. Elliott",1967,1989,14));
allPersons.add(new Person("Joshua J. Lawson",1966,1987,14));
allPersons.add(new Person("Isaac M. Bowen",1966,1981,12));
allPersons.add(new Person("Erica Lowe",1969,1981,21));
allPersons.add(new Person("Julie Lynch-Armstrong",1970,1984,25));
allPersons.add(new Person("Gregory D. Anderson",1967,1985,21));
allPersons.add(new Person("Gordon Jenkins",1968,1982,24));
allPersons.add(new Person("Craig Glass",1969,1982,27));
allPersons.add(new Person("Darrell L. Gardner II",1969,1984,37));
allPersons.add(new Person("Joseph Hammond",1967,1985,34));
allPersons.add(new Person("Lydia Chandler",1967,1989,30));
allPersons.add(new Person("Peter Howell",1970,1983,32));
allPersons.add(new Person("Edna Caldwell",1970,1989,37));
allPersons.add(new Person("Wesley Edwards Sr.",1967,1984,46));
allPersons.add(new Person("C.G. Hawkins",1969,1986,47));
allPersons.add(new Person("Warren X. Howard",1969,1985,40));
allPersons.add(new Person("Jerome Coleman",1967,1989,42));
allPersons.add(new Person("Nicholas Hill",1966,1982,47));
allPersons.add(new Person("Robert S. Brady IV",1966,1983,55));
allPersons.add(new Person("Derek Hines",1969,1985,52));
allPersons.add(new Person("Martin Henry II",1970,1982,50));
allPersons.add(new Person("Lawrence Hall",1969,1988,53));
allPersons.add(new Person("Shawn Glover",1966,1987,51));
allPersons.add(new Person("Seth D. Griffin",1969,1980,62));
allPersons.add(new Person("Cynthia Byrd",1969,1980,62));
allPersons.add(new Person("Edward Gardner",1969,1983,66));
allPersons.add(new Person("Bryan Douglas",1967,1988,66));
allPersons.add(new Person("Dennis Hines",1968,1983,61));
allPersons.add(new Person("Glen Hogan",1968,1988,75));
allPersons.add(new Person("Stephen Coleman",1967,1988,73));
allPersons.add(new Person("A.C. Carpenter Sr.",1969,1987,72));
allPersons.add(new Person("Howard Brady",1967,1980,72));
allPersons.add(new Person("Alan Copeland",1969,1987,76));
allPersons.add(new Person("Alexander Logan",1968,1988,86));
allPersons.add(new Person("Adam X. Dixon",1966,1983,84));
allPersons.add(new Person("Harry Z. Clark",1968,1989,82));
allPersons.add(new Person("J.E. Adkins",1966,1983,83));
allPersons.add(new Person("Jason Cross",1967,1986,84));
allPersons.add(new Person("Dean Kerr",1969,1989,95));
allPersons.add(new Person("Q.Q. Curtis",1967,1985,97));
allPersons.add(new Person("Tyler Jensen",1968,1988,92));
allPersons.add(new Person("O.N. Carpenter II",1966,1980,94));
allPersons.add(new Person("Julia Bowen",1968,1989,97));
allPersons.add(new Person("Gary C. Lawson",1980,1993,7));
allPersons.add(new Person("Adam N. Kelley II",1976,1990,6));
allPersons.add(new Person("Marie Gilbert",1978,1992,0));
allPersons.add(new Person("Lewis Chambers",1976,1995,2));
allPersons.add(new Person("Logan Barrett",1980,1999,4));
allPersons.add(new Person("Howard Glover",1980,1991,11));
allPersons.add(new Person("Danielle Fuller",1977,1998,10));
allPersons.add(new Person("Gilbert George II",1980,1996,14));
allPersons.add(new Person("Abigail Harding",1980,1992,16));
allPersons.add(new Person("Harold James",1977,1995,11));
allPersons.add(new Person("Janice Harvey",1976,1995,24));
allPersons.add(new Person("Cheryl S. Leonard",1976,1994,20));
allPersons.add(new Person("Edward Giles",1976,1994,20));
allPersons.add(new Person("Z.E. Jennings",1978,1997,26));
allPersons.add(new Person("Owen Clark IV",1978,1997,21));
allPersons.add(new Person("June Clarke",1979,1993,36));
allPersons.add(new Person("Colin Burgess",1979,1995,32));
allPersons.add(new Person("Craig Kennedy",1977,1995,37));
allPersons.add(new Person("Peter J. Ball",1978,1995,32));
allPersons.add(new Person("Kevin F. Brady",1977,1993,34));
allPersons.add(new Person("Corey Elliott Sr.",1980,1997,41));
allPersons.add(new Person("Edna Hampton-Dutton",1980,1994,47));
allPersons.add(new Person("G.H. Drake",1979,1990,41));
allPersons.add(new Person("Gerald Clark",1979,1993,46));
allPersons.add(new Person("Lauren Henry-Kent",1978,1996,42));
allPersons.add(new Person("Isaac N. Jackson",1977,1990,56));
allPersons.add(new Person("William Burke",1976,1999,54));
allPersons.add(new Person("Diana E. Horton",1977,1993,50));
allPersons.add(new Person("Scott Barrett",1980,1999,54));
allPersons.add(new Person("Justin Gordon",1976,1990,51));
allPersons.add(new Person("Keith B. Kent",1979,1995,61));
allPersons.add(new Person("Craig Fitzgerald III",1977,1997,67));
allPersons.add(new Person("M.G. Giles",1976,1993,60));
allPersons.add(new Person("Russell Copeland",1980,1991,64));
allPersons.add(new Person("Melanie Barrett",1979,1998,64));
allPersons.add(new Person("Z.L. Burke",1979,1991,72));
allPersons.add(new Person("A.Y. Bennett",1976,1998,75));
allPersons.add(new Person("Jack Davidson",1978,1997,73));
allPersons.add(new Person("Jesse Holloway II",1980,1993,73));
allPersons.add(new Person("Mark Bradley",1976,1991,76));
allPersons.add(new Person("Evan Kelley",1977,1990,85));
allPersons.add(new Person("Corey Jennings",1980,1991,85));
allPersons.add(new Person("Natalie Hines-Cross",1979,1991,80));
allPersons.add(new Person("Iris Chapman",1980,1998,87));
allPersons.add(new Person("Nicholas Bell",1979,1996,81));
allPersons.add(new Person("T.T. Kennedy",1976,1991,96));
allPersons.add(new Person("Mildred Glover",1980,1998,93));
allPersons.add(new Person("Kelly Hampton",1976,1990,96));
allPersons.add(new Person("Henry E. Chapman",1980,1998,90));
allPersons.add(new Person("Kimberly M. Greene",1978,1993,96));
allPersons.add(new Person("A.R. Hodges II",1989,2003,2));
allPersons.add(new Person("Travis Beck",1990,2005,6));
allPersons.add(new Person("Norma Butler-Johnson",1987,2007,7));
allPersons.add(new Person("Walter Barnett",1989,2007,4));
allPersons.add(new Person("Wayne W. Lynch III",1986,2008,5));
allPersons.add(new Person("C.B. Lane",1986,2005,10));
allPersons.add(new Person("Andrew O. Chandler I",1988,2004,15));
allPersons.add(new Person("Luke Kaufman",1990,2000,15));
allPersons.add(new Person("Lucille Fraser-Howard",1987,2008,10));
allPersons.add(new Person("Iris Allen",1987,2002,15));
allPersons.add(new Person("Robert U. Dunn",1990,2008,26));
allPersons.add(new Person("Donald Hogan",1987,2006,22));
allPersons.add(new Person("Herbert Johnson",1986,2005,20));
allPersons.add(new Person("Alan T. Bishop",1990,2000,22));
allPersons.add(new Person("Z.Y. Jensen",1986,2008,23));
allPersons.add(new Person("Kathy Brady",1989,2007,33));
allPersons.add(new Person("Anthony Kelly",1990,2005,31));
allPersons.add(new Person("Ross J. Berry Sr.",1989,2004,33));
allPersons.add(new Person("Y.E. Ball",1986,2009,31));
allPersons.add(new Person("Denise Graham",1987,2004,31));
allPersons.add(new Person("Brandon Harvey",1989,2000,43));
allPersons.add(new Person("F.K. Johnston",1990,2001,40));
allPersons.add(new Person("Dale Cook",1989,2004,45));
allPersons.add(new Person("Eugene Jacobs",1989,2005,42));
allPersons.add(new Person("Brandon Leonard",1988,2007,45));
allPersons.add(new Person("Noah E. Fisher",1986,2000,52));
allPersons.add(new Person("Jerome Byrd",1989,2008,50));
allPersons.add(new Person("Kathryn Arnold",1986,2002,51));
allPersons.add(new Person("Kimberly Elliott",1986,2007,51));
allPersons.add(new Person("Adrian Bailey",1988,2006,51));
allPersons.add(new Person("Frank Y. Ellis",1989,2006,64));
allPersons.add(new Person("U.L. Coleman",1989,2009,62));
allPersons.add(new Person("Adrian Jackson II",1986,2003,66));
allPersons.add(new Person("Dorothy S. Gray",1986,2005,60));
allPersons.add(new Person("Stephen Greene Jr.",1990,2001,67));
allPersons.add(new Person("Donald Gray",1986,2001,72));
allPersons.add(new Person("M.N. Griffith",1989,2004,70));
allPersons.add(new Person("Philip R. Freeman",1989,2006,76));
allPersons.add(new Person("Samuel Crawford",1989,2005,71));
allPersons.add(new Person("J.Z. Atkinson",1989,2004,75));
allPersons.add(new Person("Bryan U. Edwards",1987,2007,86));
allPersons.add(new Person("Colin Mackenzie",1987,2005,82));
allPersons.add(new Person("Martha Bailey",1990,2008,80));
allPersons.add(new Person("Ernest Collins",1988,2008,80));
allPersons.add(new Person("Eugene Greene",1987,2004,87));
allPersons.add(new Person("Craig Copeland",1986,2009,94));
allPersons.add(new Person("Isaac Butler",1987,2003,97));
allPersons.add(new Person("Audrey Grant-Bush",1987,2009,97));
allPersons.add(new Person("Lillian R. Fletcher",1986,2001,90));
allPersons.add(new Person("Ian Lambert IV",1987,2006,93));
allPersons.add(new Person("Gregory E. Hamilton",1999,2010,0));
allPersons.add(new Person("Paul King",1996,2017,7));
allPersons.add(new Person("Philip L. Griffin II",1997,2019,2));
allPersons.add(new Person("Anna Carter",1998,2015,4));
allPersons.add(new Person("Bernard Bennett",1997,2012,3));
allPersons.add(new Person("M.Y. Fisher",1998,2016,10));
allPersons.add(new Person("Abigail Henderson",1998,2010,13));
allPersons.add(new Person("R.Z. Bailey",1998,2016,11));
allPersons.add(new Person("Eleanor Copeland-Fletcher",1996,2014,11));
allPersons.add(new Person("April P. Craig",1996,2018,16));
allPersons.add(new Person("Raymond Dunn",1998,2011,20));
allPersons.add(new Person("I.D. Chandler",1997,2011,25));
allPersons.add(new Person("Michelle Horton",2000,2011,22));
allPersons.add(new Person("Mitchell S. Davis",1996,2017,24));
allPersons.add(new Person("Derek Bush",1999,2017,20));
allPersons.add(new Person("Janice Hunt-Farmer",1997,2017,33));
allPersons.add(new Person("Nathan Gates",1997,2010,31));
allPersons.add(new Person("Anne Burke",1997,2013,32));
allPersons.add(new Person("Dennis T. Craig",1999,2018,37));
allPersons.add(new Person("Lillian Baldwin-Bishop",1998,2012,35));
allPersons.add(new Person("Samuel V. Hicks",1999,2013,46));
allPersons.add(new Person("C.N. Brooks Jr.",2000,2019,46));
allPersons.add(new Person("Pamela Evans",1999,2010,44));
allPersons.add(new Person("T.N. Chambers",1997,2011,44));
allPersons.add(new Person("Tyler Bishop",1998,2014,46));
allPersons.add(new Person("D.T. Howell",2000,2018,50));
allPersons.add(new Person("Evan D. Harvey Jr.",1996,2017,56));
allPersons.add(new Person("Ross Holloway",1997,2013,55));
allPersons.add(new Person("Lauren Leonard-Giles",1999,2014,57));
allPersons.add(new Person("Clara Byrd",1999,2016,55));
allPersons.add(new Person("B.N. Clements",1997,2017,61));
allPersons.add(new Person("Eugene Love",1999,2011,63));
allPersons.add(new Person("Charles L. Jenkins",1997,2013,67));
allPersons.add(new Person("Jeffrey Glass",1997,2019,62));
allPersons.add(new Person("Jackie Ferguson",1999,2011,64));
allPersons.add(new Person("Edna Ellis-Farmer",1999,2010,72));
allPersons.add(new Person("Lewis Doyle I",2000,2017,70));
allPersons.add(new Person("Gary Gross",2000,2014,75));
allPersons.add(new Person("Eva Hodges",1998,2010,72));
allPersons.add(new Person("Connor Green",2000,2012,73));
allPersons.add(new Person("K.M. Jensen",1997,2017,87));
allPersons.add(new Person("Keith W. Crawford",1999,2018,86));
allPersons.add(new Person("Gregory Haynes",1999,2014,81));
allPersons.add(new Person("Audrey G. Dunn",1997,2016,84));
allPersons.add(new Person("Lillian Jenkins",1998,2019,81));
allPersons.add(new Person("Cody O. Barnett",1997,2013,97));
allPersons.add(new Person("Debra E. Harper-Burke",2000,2019,94));
allPersons.add(new Person("Angela Craig",1996,2019,96));
allPersons.add(new Person("Herbert Holloway II",1997,2016,92));
allPersons.add(new Person("Cameron Hamilton",1999,2017,93));
allPersons.add(new Person("Jesse Bowman",2006,2022,1));
allPersons.add(new Person("Lucille W. Ford",2007,2026,0));
allPersons.add(new Person("Z.A. Henderson",2006,2028,7));
allPersons.add(new Person("Steven Hale",2008,2022,5));
allPersons.add(new Person("Maurice F. Clayton",2009,2027,6));
allPersons.add(new Person("Ernest Hoffman",2010,2026,16));
allPersons.add(new Person("Madeline Hudson-Johnson",2009,2029,10));
allPersons.add(new Person("Dean Adkins II",2010,2025,13));
allPersons.add(new Person("Darrell L. Harvey",2007,2024,13));
allPersons.add(new Person("Grace Leonard",2006,2022,16));
allPersons.add(new Person("Marjorie Y. Dawson",2008,2025,23));
allPersons.add(new Person("Connie Hughes",2007,2024,24));
allPersons.add(new Person("Maria Griffin-Fuller",2006,2021,20));
allPersons.add(new Person("Debra Day",2008,2028,22));
allPersons.add(new Person("Scott Hicks",2006,2028,27));
allPersons.add(new Person("Isaac O. Hammond Sr.",2010,2029,33));
allPersons.add(new Person("Zachary Gates",2008,2029,35));
allPersons.add(new Person("Lydia Kent",2006,2023,35));
allPersons.add(new Person("Hunter Fleming",2008,2026,33));
allPersons.add(new Person("Caleb Burton Jr.",2009,2029,35));
allPersons.add(new Person("Laura Lane",2007,2028,41));
allPersons.add(new Person("Kayla Carpenter",2008,2029,46));
allPersons.add(new Person("Scott L. Fuller IV",2009,2026,43));
allPersons.add(new Person("Marian Z. Clayton",2007,2027,47));
allPersons.add(new Person("Curtis Hawkins",2009,2021,45));
allPersons.add(new Person("Victor W. Jordan",2010,2026,52));
allPersons.add(new Person("Martha K. Hogan",2010,2020,51));
allPersons.add(new Person("Aaron Abbott",2006,2026,57));
allPersons.add(new Person("Janice N. Lloyd",2009,2021,51));
allPersons.add(new Person("Heather Bradley",2010,2022,55));
allPersons.add(new Person("Brandon E. Lawrence II",2010,2025,65));
allPersons.add(new Person("Y.V. Howard",2009,2020,60));
allPersons.add(new Person("Kevin Gordon",2008,2027,63));
allPersons.add(new Person("Frank Bates I",2009,2029,60));
allPersons.add(new Person("Arthur Burgess",2010,2024,60));
allPersons.add(new Person("Audrey B. Green",2007,2022,70));
allPersons.add(new Person("Ronald I. Abbott",2008,2021,77));
allPersons.add(new Person("Katie Caldwell",2006,2024,70));
allPersons.add(new Person("Bradley Gardner",2006,2028,73));
allPersons.add(new Person("Richard Howell",2007,2027,71));
allPersons.add(new Person("Brittany R. Jensen",2006,2023,86));
allPersons.add(new Person("Adam Lloyd",2009,2022,87));
allPersons.add(new Person("Alice Allen",2009,2020,82));
allPersons.add(new Person("Annette Fitzgerald",2009,2025,82));
allPersons.add(new Person("Jason Cooper",2006,2024,87));
allPersons.add(new Person("Kelly Davidson",2007,2024,91));
allPersons.add(new Person("Evelyn N. Doyle",2006,2028,90));
allPersons.add(new Person("Y.X. Ford",2010,2021,92));
allPersons.add(new Person("Alice Bowman",2008,2022,96));
allPersons.add(new Person("Kyle Lane",2006,2025,95));
allPersons.add(new Person("Isaac Hall",2020,2033,3));
allPersons.add(new Person("Lawrence Lyons II",2017,2038,1));
allPersons.add(new Person("Lucy Hardy",2016,2030,2));
allPersons.add(new Person("Debra Fraser",2018,2037,3));
allPersons.add(new Person("John Jordan",2019,2035,4));
allPersons.add(new Person("U.I. Lewis Jr.",2020,2038,11));
allPersons.add(new Person("Maria Byrd",2018,2031,10));
allPersons.add(new Person("Dennis Kelly",2020,2034,14));
allPersons.add(new Person("Jessica Franklin",2017,2032,14));
allPersons.add(new Person("H.I. Clarke",2020,2036,13));
allPersons.add(new Person("Loretta C. Collins",2016,2031,24));
allPersons.add(new Person("Melanie Bradley",2020,2037,22));
allPersons.add(new Person("Danielle Barnett",2019,2033,22));
allPersons.add(new Person("Louis Fraser",2018,2031,26));
allPersons.add(new Person("Jane Howard",2018,2037,21));
allPersons.add(new Person("Ellen M. Holt",2017,2036,31));
allPersons.add(new Person("James L. Brewer",2020,2030,36));
allPersons.add(new Person("Katherine Gates",2017,2030,31));
allPersons.add(new Person("John Allen",2019,2034,36));
allPersons.add(new Person("Brenda Y. Carroll-Gordon",2016,2032,35));
allPersons.add(new Person("Jason Brown IV",2020,2033,46));
allPersons.add(new Person("Doris Allen",2020,2037,46));
allPersons.add(new Person("Harvey C. Hunt",2016,2038,47));
allPersons.add(new Person("Pamela Knight",2020,2037,41));
allPersons.add(new Person("Benjamin X. Allen",2016,2031,42));
allPersons.add(new Person("Janice Foster",2018,2039,54));
allPersons.add(new Person("Glen Bennett",2016,2038,52));
allPersons.add(new Person("Janice Bailey",2019,2038,50));
allPersons.add(new Person("Audrey H. Chapman",2019,2033,53));
allPersons.add(new Person("Irene Green",2016,2038,56));
allPersons.add(new Person("Jocelyn Hale",2018,2034,62));
allPersons.add(new Person("E.C. Foster",2019,2036,63));
allPersons.add(new Person("Danny A. Franklin",2017,2039,63));
allPersons.add(new Person("Travis Gordon",2018,2032,63));
allPersons.add(new Person("Lucy W. Little-Bradley",2016,2037,61));
allPersons.add(new Person("Megan E. Hubbard",2019,2039,77));
allPersons.add(new Person("Tyler Ferguson",2020,2030,76));
allPersons.add(new Person("Alexander Carr",2016,2031,71));
allPersons.add(new Person("Maureen Kelly",2019,2031,77));
allPersons.add(new Person("Alexander Butler",2020,2032,75));
allPersons.add(new Person("Ernest Hill",2018,2039,84));
allPersons.add(new Person("Linda Clark",2018,2033,84));
allPersons.add(new Person("Glen Gross II",2018,2032,82));
allPersons.add(new Person("Marilyn Cross",2020,2030,87));
allPersons.add(new Person("Clarence U. Copeland",2019,2037,82));
allPersons.add(new Person("Owen R. Crawford",2020,2039,94));
allPersons.add(new Person("Sean Allen",2020,2030,91));
allPersons.add(new Person("Jocelyn Dunn",2017,2033,97));
allPersons.add(new Person("Darrell T. Garrett",2018,2037,93));
allPersons.add(new Person("Jack Hamilton",2020,2036,96));
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
    
    public static void displayMostProminent(){
        List<Person> mostProminents = new ArrayList<>();
        for(int i=0; i<10;i++){
            Person maxper=null;
            int maxnum=Integer.MIN_VALUE;
            
            for(Person per: activePersons){
                int points = per.getProminence();
                if(points > maxnum && !mostProminents.contains(per)){
                    maxnum = points;
                    maxper = per;
                }
            }
            mostProminents.add(maxper);
        }
        System.out.println("\nMost Prominent Politicians of "+ year+ ": ");
        for(Person per: mostProminents){
            System.out.print(per+ " | ");
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
    assessProminence();
    displayMostProminent();
    System.out.println();
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
