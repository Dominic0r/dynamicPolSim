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
                if(per.hasBeenPresidentBefore()){
                    if(per.noOfTimesBecamePresident() == 1){
                        points*=100;
                    }
                }
                if(points> maxnum){
                    maxnum = points;
                    maxper = per;
                }
            }
            
            standardBearer = maxper;
            maxnum = Integer.MIN_VALUE;
            
            for(Person per: memberPersons){
                int points= per.getProminence();
                if(per == chairman){
                    points *=2;
                }
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
        
        boolean hasbeenPresident=false;
        int prescount=0;
        
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
        
        public boolean hasBeenPresidentBefore(){return hasbeenPresident;}
        public int noOfTimesBecamePresident(){return prescount;}
        
        public void incrementPrescount(){
            prescount++;
        }
        
        public void setPresToTrue(){ hasbeenPresident=true;}
        
        public void determineParty(){
            Party maxpar=null;
            int maxnum = Integer.MIN_VALUE;
            
            for(Party par: allParties){
                int points = ((proximityWith(par)/4)*3) + (25/ ((par.memCount()*5)+1));
                if(par == currentParty){
                    points += points/2;
                }
                if(this == par.getStandardB() || this== par.getChair() || this == par.getForSpeak()){
                    points += points/2;
                }
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
                
                if(hasbeenPresident){
                    prominence*=3;
                    if(prescount==1){
                        prominence*=10;
                    }else{
                        prominence/=10;
                    }
                }
                
                prominence -= (year-startyear)/5;
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
        allPersons.add(new Person("Samuel Horton",1849,1883,0));
allPersons.add(new Person("Gabriel Hall",1846,1871,4));
allPersons.add(new Person("Ian Fowler",1850,1888,0));
allPersons.add(new Person("Bernard Farmer",1848,1873,0));
allPersons.add(new Person("E.J. Fields",1850,1903,3));
allPersons.add(new Person("Justin Harvey",1850,1864,11));
allPersons.add(new Person("Clark Griffith II",1846,1889,16));
allPersons.add(new Person("Clarence Leonard",1847,1904,16));
allPersons.add(new Person("Christopher V. Bryant",1848,1880,16));
allPersons.add(new Person("Harvey Burke",1847,1873,16));
allPersons.add(new Person("Terrence Beck",1849,1903,23));
allPersons.add(new Person("Luke Barnett",1848,1884,25));
allPersons.add(new Person("A.Z. Day",1849,1899,24));
allPersons.add(new Person("Colin Henry",1849,1889,26));
allPersons.add(new Person("Danny Harvey Jr.",1847,1865,24));
allPersons.add(new Person("M.Q. Gray",1849,1881,37));
allPersons.add(new Person("Justin I. Coleman IV",1850,1880,35));
allPersons.add(new Person("Clarence Cross",1849,1871,30));
allPersons.add(new Person("Charles Q. Arnold",1848,1890,33));
allPersons.add(new Person("Sean Ball",1848,1877,36));
allPersons.add(new Person("Mark T. Hughes",1850,1877,42));
allPersons.add(new Person("B.T. Jenkins",1847,1873,42));
allPersons.add(new Person("Robert Q. Dutton Sr.",1849,1908,40));
allPersons.add(new Person("Ralph Kennedy",1848,1908,46));
allPersons.add(new Person("George Bryant",1850,1894,45));
allPersons.add(new Person("Peter Arnold",1849,1897,57));
allPersons.add(new Person("Jerome Y. Hayes",1848,1897,54));
allPersons.add(new Person("Shawn Gregory IV",1847,1872,56));
allPersons.add(new Person("Samuel Gilbert Jr.",1847,1864,50));
allPersons.add(new Person("Gerald Gates",1847,1881,52));
allPersons.add(new Person("Samuel Hicks",1846,1895,64));
allPersons.add(new Person("Jack B. Carroll",1850,1889,61));
allPersons.add(new Person("Wayne I. Dunn",1847,1896,64));
allPersons.add(new Person("G.O. Clements",1847,1899,61));
allPersons.add(new Person("R.Q. Abbott",1850,1888,63));
allPersons.add(new Person("Victor M. Bishop III",1847,1860,75));
allPersons.add(new Person("Maxwell Coleman",1847,1865,73));
allPersons.add(new Person("Jeremy Fitzgerald",1848,1893,75));
allPersons.add(new Person("Caleb Gordon",1850,1866,77));
allPersons.add(new Person("John Y. Little",1847,1887,76));
allPersons.add(new Person("Jacob Chandler III",1848,1892,84));
allPersons.add(new Person("Albert Chapman",1847,1874,84));
allPersons.add(new Person("W.B. Alexander",1850,1860,83));
allPersons.add(new Person("Robert S. Brooks",1850,1879,81));
allPersons.add(new Person("Herbert Macdonald",1847,1891,87));
allPersons.add(new Person("Jonathan Z. Austin",1850,1875,91));
allPersons.add(new Person("Steven Higgins",1846,1865,92));
allPersons.add(new Person("Noah Hill",1848,1888,97));
allPersons.add(new Person("I.C. Griffin III",1850,1894,94));
allPersons.add(new Person("Carl Fields",1846,1898,97));
allPersons.add(new Person("Roger Lane",1859,1906,3));
allPersons.add(new Person("F.A. Hudson",1858,1917,0));
allPersons.add(new Person("Christopher Mackenzie",1860,1919,3));
allPersons.add(new Person("Maurice Hardy",1857,1918,2));
allPersons.add(new Person("Louis Duncan",1856,1870,7));
allPersons.add(new Person("Raymond Z. Clarke",1856,1880,17));
allPersons.add(new Person("Patrick Coleman",1856,1919,14));
allPersons.add(new Person("John Johnson",1859,1895,10));
allPersons.add(new Person("Jack Brooks",1859,1891,14));
allPersons.add(new Person("Y.S. Lloyd",1857,1893,11));
allPersons.add(new Person("Eugene Ellis",1859,1878,21));
allPersons.add(new Person("Bryan Lloyd",1856,1884,23));
allPersons.add(new Person("Gregory Adams",1858,1894,20));
allPersons.add(new Person("Scott Y. Glover",1859,1891,20));
allPersons.add(new Person("Dennis Abbott",1857,1908,24));
allPersons.add(new Person("Gabriel Blake I",1856,1899,33));
allPersons.add(new Person("Ethan R. Dawson",1859,1891,37));
allPersons.add(new Person("Richard Case",1859,1906,34));
allPersons.add(new Person("Evan L. Gardner",1858,1915,30));
allPersons.add(new Person("Bernard Garrett",1859,1901,30));
allPersons.add(new Person("David Johnston",1857,1880,45));
allPersons.add(new Person("Clark Berry",1860,1911,47));
allPersons.add(new Person("Jacob Abbott",1856,1897,41));
allPersons.add(new Person("George T. Barnes",1856,1881,42));
allPersons.add(new Person("M.X. Freeman",1857,1873,42));
allPersons.add(new Person("Bryan Chapman",1857,1873,52));
allPersons.add(new Person("Zachary Davidson",1856,1879,56));
allPersons.add(new Person("I.Q. Clark",1860,1907,50));
allPersons.add(new Person("Edwin D. Dawson",1858,1880,55));
allPersons.add(new Person("Connor H. Lambert II",1858,1881,53));
allPersons.add(new Person("Ernest Brewer",1858,1879,62));
allPersons.add(new Person("Clifford H. Adams",1857,1883,67));
allPersons.add(new Person("X.V. Day",1857,1893,66));
allPersons.add(new Person("Samuel Barnett",1858,1899,67));
allPersons.add(new Person("Keith Chandler",1857,1914,64));
allPersons.add(new Person("Seth Ingram",1858,1905,77));
allPersons.add(new Person("Logan T. Chandler",1856,1885,72));
allPersons.add(new Person("Anthony H. Bryant Jr.",1856,1904,75));
allPersons.add(new Person("F.O. Evans",1860,1890,75));
allPersons.add(new Person("Vincent Abbott",1859,1882,75));
allPersons.add(new Person("Jonathan Freeman Jr.",1859,1875,86));
allPersons.add(new Person("Ross Z. Duncan",1859,1906,80));
allPersons.add(new Person("Douglas Coleman",1856,1902,80));
allPersons.add(new Person("Victor Barnes",1860,1893,80));
allPersons.add(new Person("Justin Carpenter",1857,1919,86));
allPersons.add(new Person("Austin Chase",1859,1913,93));
allPersons.add(new Person("Blake Armstrong I",1859,1885,90));
allPersons.add(new Person("Arthur Horton",1858,1919,95));
allPersons.add(new Person("Warren George",1859,1879,92));
allPersons.add(new Person("Maxwell Carpenter",1856,1891,90));
allPersons.add(new Person("Howard Griffin",1868,1893,0));
allPersons.add(new Person("Ryan E. Bradley",1869,1898,1));
allPersons.add(new Person("Jerome Lyons",1868,1906,3));
allPersons.add(new Person("Patrick Duncan",1870,1888,5));
allPersons.add(new Person("Jeremy Logan",1866,1901,0));
allPersons.add(new Person("Adam Carter",1866,1893,12));
allPersons.add(new Person("H.D. Burton",1866,1899,14));
allPersons.add(new Person("Anthony Cole",1869,1900,14));
allPersons.add(new Person("X.K. Brewer",1867,1890,15));
allPersons.add(new Person("O.D. Andrews",1870,1909,11));
allPersons.add(new Person("Joel Little",1868,1923,26));
allPersons.add(new Person("David Hubbard",1866,1889,23));
allPersons.add(new Person("Raymond Caldwell II",1870,1908,22));
allPersons.add(new Person("Howard Grant",1868,1902,27));
allPersons.add(new Person("Tyler W. Jensen",1868,1903,21));
allPersons.add(new Person("Bradley Clayton Sr.",1869,1883,31));
allPersons.add(new Person("Caleb Gregory III",1867,1895,32));
allPersons.add(new Person("Jason Carpenter",1870,1915,32));
allPersons.add(new Person("Ethan X. Little",1867,1912,32));
allPersons.add(new Person("Benjamin Ingram",1868,1899,36));
allPersons.add(new Person("Warren Kelly",1870,1912,47));
allPersons.add(new Person("Luke J. Ford",1866,1902,47));
allPersons.add(new Person("Justin Jones",1870,1916,42));
allPersons.add(new Person("Danny N. Black",1866,1896,42));
allPersons.add(new Person("Dennis Holt II",1870,1916,41));
allPersons.add(new Person("Henry Long II",1869,1889,50));
allPersons.add(new Person("Austin N. Kelly",1866,1910,50));
allPersons.add(new Person("Timothy Hicks",1870,1890,55));
allPersons.add(new Person("Albert Glass",1870,1924,50));
allPersons.add(new Person("Edward Z. Hawkins",1869,1907,53));
allPersons.add(new Person("Jeffrey Bush",1868,1912,61));
allPersons.add(new Person("Frederick Dean III",1868,1917,65));
allPersons.add(new Person("Joel Byrd",1866,1924,62));
allPersons.add(new Person("Colin Clark",1867,1920,63));
allPersons.add(new Person("B.G. Holmes",1870,1884,60));
allPersons.add(new Person("Clark Burton Jr.",1866,1896,75));
allPersons.add(new Person("Dean Hale III",1866,1924,76));
allPersons.add(new Person("Bernard Hunter",1868,1882,71));
allPersons.add(new Person("Mitchell Douglas Sr.",1866,1902,70));
allPersons.add(new Person("Brandon Baldwin",1868,1927,70));
allPersons.add(new Person("Dale Glover",1867,1899,83));
allPersons.add(new Person("Joshua Arnold",1870,1893,82));
allPersons.add(new Person("William M. Elliott",1869,1899,82));
allPersons.add(new Person("Logan Burns",1869,1901,83));
allPersons.add(new Person("Philip Andrews",1870,1899,81));
allPersons.add(new Person("Stanley S. Knight",1870,1929,96));
allPersons.add(new Person("N.L. Macdonald",1867,1923,93));
allPersons.add(new Person("Noah Kent",1869,1923,94));
allPersons.add(new Person("Darrell Carr",1870,1892,91));
allPersons.add(new Person("Carl Fuller",1870,1905,94));
allPersons.add(new Person("Leonard Adams",1880,1895,7));
allPersons.add(new Person("Gilbert Ellis",1877,1902,1));
allPersons.add(new Person("Noah Hughes IV",1879,1900,4));
allPersons.add(new Person("Jesse Bryant",1877,1926,7));
allPersons.add(new Person("Blake Gilbert",1878,1899,7));
allPersons.add(new Person("Hunter C. Griffith",1878,1894,11));
allPersons.add(new Person("Norman Kennedy",1879,1922,14));
allPersons.add(new Person("Dustin Fleming",1878,1919,11));
allPersons.add(new Person("A.X. Henry",1877,1907,16));
allPersons.add(new Person("Harry Drake",1880,1913,14));
allPersons.add(new Person("Gerald Hammond",1880,1893,23));
allPersons.add(new Person("Carl Clarke",1879,1893,21));
allPersons.add(new Person("C.F. Gardner",1879,1891,23));
allPersons.add(new Person("A.S. Hamilton",1877,1929,26));
allPersons.add(new Person("A.A. Drake",1876,1930,22));
allPersons.add(new Person("Logan Douglas",1878,1895,32));
allPersons.add(new Person("Oscar Harris Sr.",1878,1927,30));
allPersons.add(new Person("Bradley Hunt",1879,1893,32));
allPersons.add(new Person("Stephen Fields",1876,1913,36));
allPersons.add(new Person("Gilbert H. Hunter",1879,1903,36));
allPersons.add(new Person("T.P. Boyd",1879,1906,40));
allPersons.add(new Person("Curtis E. Jordan",1879,1905,41));
allPersons.add(new Person("Warren Clements",1876,1931,43));
allPersons.add(new Person("Nicholas Boyd Jr.",1876,1925,41));
allPersons.add(new Person("Alexander Hill",1878,1895,41));
allPersons.add(new Person("Raymond Barker",1878,1916,50));
allPersons.add(new Person("M.K. Boyd",1879,1936,50));
allPersons.add(new Person("Clarence Hicks III",1880,1934,51));
allPersons.add(new Person("Maurice Hardy",1877,1919,53));
allPersons.add(new Person("N.D. Blake",1880,1900,50));
allPersons.add(new Person("William Drake",1878,1916,64));
allPersons.add(new Person("Ryan C. Davidson",1878,1894,64));
allPersons.add(new Person("Frederick H. Black",1880,1930,60));
allPersons.add(new Person("L.P. Brady",1880,1937,66));
allPersons.add(new Person("David Burgess IV",1876,1939,60));
allPersons.add(new Person("Caleb Barker",1879,1931,73));
allPersons.add(new Person("Leonard O. Daniels",1878,1892,72));
allPersons.add(new Person("Daniel Dawson",1878,1914,73));
allPersons.add(new Person("Thomas Hale",1877,1922,74));
allPersons.add(new Person("Ian Y. Caldwell",1876,1920,74));
allPersons.add(new Person("Gilbert O. Kerr",1878,1918,84));
allPersons.add(new Person("John Dixon",1879,1908,87));
allPersons.add(new Person("Daniel Brown",1877,1892,86));
allPersons.add(new Person("Gilbert X. Bennett II",1876,1893,87));
allPersons.add(new Person("Gregory H. Harvey IV",1877,1913,83));
allPersons.add(new Person("James K. Butler",1880,1915,90));
allPersons.add(new Person("George Leonard",1876,1910,96));
allPersons.add(new Person("Curtis C. Jones",1878,1896,90));
allPersons.add(new Person("Ian Armstrong",1876,1917,90));
allPersons.add(new Person("Maurice Case",1876,1905,96));
allPersons.add(new Person("Wesley G. Holland",1888,1907,3));
allPersons.add(new Person("Anthony Green",1887,1916,5));
allPersons.add(new Person("Mark Leonard I",1890,1920,0));
allPersons.add(new Person("Vincent K. Hubbard",1889,1935,5));
allPersons.add(new Person("Peter Atkinson II",1886,1916,3));
allPersons.add(new Person("Richard Chambers",1886,1905,10));
allPersons.add(new Person("Wesley O. Hunter Sr.",1888,1930,16));
allPersons.add(new Person("Q.L. Carlson",1887,1940,10));
allPersons.add(new Person("Joel Brewer IV",1889,1919,11));
allPersons.add(new Person("Joseph Dunn",1888,1924,15));
allPersons.add(new Person("Clayton Black IV",1886,1908,22));
allPersons.add(new Person("Herbert Chapman",1886,1906,22));
allPersons.add(new Person("Paul Gardner",1888,1949,27));
allPersons.add(new Person("Brian Beck Jr.",1887,1941,21));
allPersons.add(new Person("K.M. Ingram III",1889,1935,21));
allPersons.add(new Person("Daniel Chambers",1886,1931,30));
allPersons.add(new Person("O.N. Lucas I",1888,1948,31));
allPersons.add(new Person("Ralph Barrett",1886,1912,35));
allPersons.add(new Person("Peter Gates I",1886,1944,34));
allPersons.add(new Person("G.S. Cole",1887,1942,34));
allPersons.add(new Person("Ernest S. Hodges IV",1887,1942,42));
allPersons.add(new Person("Nicholas Jensen",1889,1939,44));
allPersons.add(new Person("Russell R. Dutton",1888,1925,44));
allPersons.add(new Person("Logan E. Hines",1889,1907,42));
allPersons.add(new Person("Harold Z. Collins",1887,1916,47));
allPersons.add(new Person("Brandon X. Lawrence",1890,1904,57));
allPersons.add(new Person("Colin Bishop",1888,1914,57));
allPersons.add(new Person("Bruce L. Cross",1887,1923,51));
allPersons.add(new Person("Carl Barnett",1886,1901,57));
allPersons.add(new Person("Cody Macdonald",1890,1944,51));
allPersons.add(new Person("Vincent Hardy",1890,1942,66));
allPersons.add(new Person("Clark R. George",1887,1939,64));
allPersons.add(new Person("Walter V. Cox",1887,1915,67));
allPersons.add(new Person("Todd Grant",1887,1901,60));
allPersons.add(new Person("Shane Dean",1889,1947,61));
allPersons.add(new Person("Jonathan C. Henry",1887,1903,71));
allPersons.add(new Person("Alan Collins",1886,1928,74));
allPersons.add(new Person("Connor Franklin",1890,1946,72));
allPersons.add(new Person("N.V. Farmer Jr.",1887,1912,71));
allPersons.add(new Person("M.V. Glover",1886,1906,74));
allPersons.add(new Person("Carl F. Fletcher",1887,1914,81));
allPersons.add(new Person("A.U. Fraser",1889,1940,80));
allPersons.add(new Person("Jeremy Adams",1886,1912,83));
allPersons.add(new Person("Terrence Jenkins",1890,1943,85));
allPersons.add(new Person("Victor Hale",1888,1900,83));
allPersons.add(new Person("Arthur O. Alexander I",1889,1907,95));
allPersons.add(new Person("Dean Glover III",1889,1920,93));
allPersons.add(new Person("Arthur Bowman",1887,1941,93));
allPersons.add(new Person("Lewis Baldwin",1886,1942,93));
allPersons.add(new Person("Earl Griffin",1888,1913,94));
allPersons.add(new Person("Keith Adams",1896,1938,6));
allPersons.add(new Person("Austin G. Berry",1896,1928,4));
allPersons.add(new Person("Gilbert Boyd IV",1898,1921,6));
allPersons.add(new Person("Q.D. Ingram",1896,1946,5));
allPersons.add(new Person("Y.H. James",1899,1918,1));
allPersons.add(new Person("Curtis Fleming",1896,1949,17));
allPersons.add(new Person("Dennis Graham",1897,1915,17));
allPersons.add(new Person("Cody Byrd",1896,1951,17));
allPersons.add(new Person("William R. Alexander",1898,1933,13));
allPersons.add(new Person("Roger Kelley",1898,1937,10));
allPersons.add(new Person("Terrence Baker",1900,1957,24));
allPersons.add(new Person("Donald F. Cox I",1900,1949,20));
allPersons.add(new Person("Tyler Lambert",1896,1910,27));
allPersons.add(new Person("N.P. Kelly",1900,1932,22));
allPersons.add(new Person("Thomas Z. Alexander Jr.",1900,1922,27));
allPersons.add(new Person("Ronald Hines",1897,1918,33));
allPersons.add(new Person("Dennis F. Dutton",1900,1920,32));
allPersons.add(new Person("Kyle Hunter",1900,1946,37));
allPersons.add(new Person("Jerome Armstrong",1896,1936,34));
allPersons.add(new Person("Ryan Leonard",1898,1955,31));
allPersons.add(new Person("Philip Jackson",1897,1954,44));
allPersons.add(new Person("Oscar Ellis I",1900,1926,44));
allPersons.add(new Person("Jordan Chapman",1897,1922,43));
allPersons.add(new Person("Thomas Dean",1898,1929,47));
allPersons.add(new Person("William Lawrence",1900,1930,41));
allPersons.add(new Person("X.V. Jennings",1898,1947,54));
allPersons.add(new Person("Brian Hopkins",1899,1942,51));
allPersons.add(new Person("Clifford Harris",1900,1931,55));
allPersons.add(new Person("L.D. Fleming",1896,1958,52));
allPersons.add(new Person("Jonathan A. Freeman",1899,1936,50));
allPersons.add(new Person("Frederick Gates",1899,1933,64));
allPersons.add(new Person("Travis Daniels",1898,1939,63));
allPersons.add(new Person("Gordon Carlson",1898,1951,66));
allPersons.add(new Person("Jeremy Chapman",1898,1914,62));
allPersons.add(new Person("Nathan Foster",1897,1928,60));
allPersons.add(new Person("Ernest Chapman",1899,1922,75));
allPersons.add(new Person("W.S. Hughes Jr.",1900,1940,75));
allPersons.add(new Person("Ralph Carr",1899,1918,74));
allPersons.add(new Person("Cody Jackson",1900,1948,77));
allPersons.add(new Person("Timothy Lee Jr.",1897,1937,76));
allPersons.add(new Person("Keith F. Collins Sr.",1900,1934,81));
allPersons.add(new Person("Jack C. Fisher",1896,1943,82));
allPersons.add(new Person("Scott C. Bishop",1899,1939,82));
allPersons.add(new Person("X.X. Brooks",1896,1940,87));
allPersons.add(new Person("Gilbert Burton",1900,1938,87));
allPersons.add(new Person("Herbert Caldwell",1896,1910,90));
allPersons.add(new Person("Bernard Bowen",1896,1915,93));
allPersons.add(new Person("Colin Ferguson",1898,1939,95));
allPersons.add(new Person("Andrew Clements",1899,1946,96));
allPersons.add(new Person("Gary Anderson",1896,1925,90));
allPersons.add(new Person("Clarence H. King",1910,1962,3));
allPersons.add(new Person("Jason Little Sr.",1907,1932,6));
allPersons.add(new Person("Louis Baker",1908,1936,4));
allPersons.add(new Person("L.G. Howell Jr.",1910,1934,2));
allPersons.add(new Person("Dustin E. Glass",1906,1927,2));
allPersons.add(new Person("Gary Black",1909,1936,11));
allPersons.add(new Person("Stephen Graham",1906,1940,16));
allPersons.add(new Person("Gerald Jensen",1906,1956,14));
allPersons.add(new Person("Joshua C. Lynch",1908,1939,16));
allPersons.add(new Person("Ronald Kelley",1907,1967,17));
allPersons.add(new Person("Frank Bailey III",1908,1937,21));
allPersons.add(new Person("D.W. Hammond",1906,1960,22));
allPersons.add(new Person("Scott Gilbert",1910,1965,26));
allPersons.add(new Person("Louis Foster",1906,1963,24));
allPersons.add(new Person("Harry X. Fields IV",1907,1936,24));
allPersons.add(new Person("Henry Bush",1909,1936,31));
allPersons.add(new Person("Ryan Gregory",1909,1952,33));
allPersons.add(new Person("Jordan Haynes IV",1910,1943,32));
allPersons.add(new Person("Jonathan Lynch",1909,1926,34));
allPersons.add(new Person("Jonathan Copeland",1910,1949,30));
allPersons.add(new Person("Martin I. Craig",1906,1965,40));
allPersons.add(new Person("T.Q. Blake",1910,1936,45));
allPersons.add(new Person("Evan Bailey Sr.",1910,1966,43));
allPersons.add(new Person("Isaac Lucas",1910,1957,47));
allPersons.add(new Person("Warren Carter III",1910,1952,44));
allPersons.add(new Person("A.S. Hampton",1909,1956,50));
allPersons.add(new Person("Joel Hubbard",1910,1955,56));
allPersons.add(new Person("Earl Craig",1907,1952,56));
allPersons.add(new Person("Harvey Ferguson",1906,1938,52));
allPersons.add(new Person("Martin S. Duncan",1910,1963,53));
allPersons.add(new Person("Jeremy Blair",1907,1928,62));
allPersons.add(new Person("Kenneth Davis",1910,1946,66));
allPersons.add(new Person("Patrick P. Burton Sr.",1907,1936,67));
allPersons.add(new Person("Peter S. Garrett Jr.",1908,1952,60));
allPersons.add(new Person("Gregory Johnson",1907,1951,64));
allPersons.add(new Person("Kyle James",1907,1946,77));
allPersons.add(new Person("Cody Adkins",1909,1935,71));
allPersons.add(new Person("Todd Gates",1908,1950,77));
allPersons.add(new Person("Andrew Hughes",1906,1955,71));
allPersons.add(new Person("Bernard V. Doyle",1908,1964,72));
allPersons.add(new Person("Ethan X. Daniel",1910,1942,83));
allPersons.add(new Person("Francis G. Elliott",1910,1922,87));
allPersons.add(new Person("Logan Copeland",1909,1929,87));
allPersons.add(new Person("Logan Barnes",1908,1939,82));
allPersons.add(new Person("Travis Cobb IV",1907,1920,85));
allPersons.add(new Person("Robert Clark",1910,1930,91));
allPersons.add(new Person("Gordon Boyd",1907,1935,91));
allPersons.add(new Person("Harold Hodges",1909,1952,92));
allPersons.add(new Person("Ernest X. Greene",1907,1955,95));
allPersons.add(new Person("Peter Harper",1910,1945,96));
allPersons.add(new Person("Gary U. Burgess Jr.",1917,1935,1));
allPersons.add(new Person("W.F. Brewer",1916,1934,7));
allPersons.add(new Person("Gilbert Cobb",1919,1938,6));
allPersons.add(new Person("Bruce G. Hughes",1919,1941,0));
allPersons.add(new Person("Frederick Fletcher Sr.",1919,1975,2));
allPersons.add(new Person("Stephen Hall",1920,1946,13));
allPersons.add(new Person("Henry Davis",1917,1950,15));
allPersons.add(new Person("Scott Lynch",1920,1971,16));
allPersons.add(new Person("Frederick Ellis",1917,1970,14));
allPersons.add(new Person("Jesse Griffin",1918,1975,14));
allPersons.add(new Person("Jesse Hawkins",1917,1953,23));
allPersons.add(new Person("Brian Carlson",1917,1936,23));
allPersons.add(new Person("Edwin Hodges",1919,1958,20));
allPersons.add(new Person("Harvey Gilbert",1916,1954,24));
allPersons.add(new Person("Lawrence Brooks II",1917,1940,24));
allPersons.add(new Person("Francis Elliott IV",1919,1958,30));
allPersons.add(new Person("William Kent",1918,1954,32));
allPersons.add(new Person("Martin O. Howard Sr.",1916,1945,34));
allPersons.add(new Person("Howard Copeland",1919,1962,33));
allPersons.add(new Person("James Evans",1920,1955,34));
allPersons.add(new Person("R.J. Holland",1919,1940,42));
allPersons.add(new Person("X.A. Chambers Jr.",1919,1954,42));
allPersons.add(new Person("M.L. Blair",1920,1955,43));
allPersons.add(new Person("Clifford Barnes II",1919,1974,44));
allPersons.add(new Person("Jeremy Armstrong",1916,1972,41));
allPersons.add(new Person("Jordan Clements",1916,1977,53));
allPersons.add(new Person("Dennis Burton",1920,1930,57));
allPersons.add(new Person("Franklin Ferguson IV",1918,1962,55));
allPersons.add(new Person("Harvey G. Henderson Jr.",1919,1945,52));
allPersons.add(new Person("Harold Copeland",1920,1952,53));
allPersons.add(new Person("Russell Dawson",1919,1964,63));
allPersons.add(new Person("Thomas Lynch",1916,1935,64));
allPersons.add(new Person("Warren V. Glover",1916,1942,65));
allPersons.add(new Person("Danny Brooks III",1919,1955,62));
allPersons.add(new Person("Owen Hudson",1920,1946,66));
allPersons.add(new Person("Andrew Hicks",1920,1933,71));
allPersons.add(new Person("Harvey Crawford III",1917,1937,70));
allPersons.add(new Person("Jordan Johnson II",1918,1966,73));
allPersons.add(new Person("Caleb Anderson Sr.",1920,1936,72));
allPersons.add(new Person("Danny Y. Clements",1916,1976,71));
allPersons.add(new Person("Caleb Ferguson",1918,1973,80));
allPersons.add(new Person("Walter Doyle",1916,1978,81));
allPersons.add(new Person("Todd Hampton",1916,1969,81));
allPersons.add(new Person("Z.Q. Jennings",1916,1942,84));
allPersons.add(new Person("Jordan Barnett I",1919,1959,84));
allPersons.add(new Person("Timothy Long",1917,1950,92));
allPersons.add(new Person("Patrick Q. Brooks",1916,1936,90));
allPersons.add(new Person("B.O. Clayton",1917,1977,91));
allPersons.add(new Person("U.D. Hubbard",1920,1961,97));
allPersons.add(new Person("Derek B. Ellis",1916,1936,90));
allPersons.add(new Person("Travis Burgess III",1928,1970,3));
allPersons.add(new Person("Logan Edwards",1926,1964,5));
allPersons.add(new Person("Sean Bowman I",1930,1978,6));
allPersons.add(new Person("U.B. Armstrong",1928,1960,4));
allPersons.add(new Person("Kevin Burton",1926,1969,6));
allPersons.add(new Person("P.C. Daniel",1929,1963,17));
allPersons.add(new Person("Nicholas Love",1930,1948,14));
allPersons.add(new Person("Connor F. Atkinson",1927,1983,17));
allPersons.add(new Person("Anthony Kent",1930,1969,17));
allPersons.add(new Person("Ryan George III",1930,1951,16));
allPersons.add(new Person("Todd Black",1926,1948,24));
allPersons.add(new Person("Jordan E. Glass I",1927,1986,26));
allPersons.add(new Person("Ian Fletcher",1928,1949,23));
allPersons.add(new Person("W.L. Cunningham",1930,1967,26));
allPersons.add(new Person("George X. Cox",1928,1954,25));
allPersons.add(new Person("Danny S. Cole",1928,1984,33));
allPersons.add(new Person("Stanley Graham",1926,1951,35));
allPersons.add(new Person("Hunter Holloway",1928,1987,32));
allPersons.add(new Person("Douglas B. Byrd",1929,1940,31));
allPersons.add(new Person("Franklin Hayes",1929,1983,34));
allPersons.add(new Person("Matthew N. Duncan",1930,1969,42));
allPersons.add(new Person("Shane E. Doyle",1930,1943,43));
allPersons.add(new Person("Martin Carr I",1928,1962,47));
allPersons.add(new Person("Kenneth Blake",1926,1950,44));
allPersons.add(new Person("Bruce U. Gates",1930,1986,42));
allPersons.add(new Person("Donald H. Clayton",1927,1969,55));
allPersons.add(new Person("Colin Bailey",1926,1945,50));
allPersons.add(new Person("Paul Long",1926,1968,52));
allPersons.add(new Person("William B. Dawson",1927,1982,51));
allPersons.add(new Person("Maxwell Glover",1929,1989,57));
allPersons.add(new Person("Russell Blair",1927,1941,62));
allPersons.add(new Person("Oscar Gregory",1927,1950,67));
allPersons.add(new Person("Bernard Fowler",1929,1956,61));
allPersons.add(new Person("Jack X. Jenkins",1927,1975,66));
allPersons.add(new Person("Maxwell Hammond",1930,1957,61));
allPersons.add(new Person("Chad Howard",1927,1946,74));
allPersons.add(new Person("Edward Green",1927,1948,72));
allPersons.add(new Person("Charles Blake",1930,1949,76));
allPersons.add(new Person("Bernard Knight",1929,1964,70));
allPersons.add(new Person("Kenneth Beck",1926,1980,72));
allPersons.add(new Person("U.M. Cole",1928,1960,86));
allPersons.add(new Person("Roy Atkinson",1929,1965,86));
allPersons.add(new Person("Arthur Brady",1926,1985,83));
allPersons.add(new Person("Raymond Carroll",1927,1960,80));
allPersons.add(new Person("Caleb Day",1926,1952,80));
allPersons.add(new Person("Donald Allen",1927,1974,97));
allPersons.add(new Person("X.Z. Lowe",1929,1960,93));
allPersons.add(new Person("Norman Garrett",1927,1940,97));
allPersons.add(new Person("Noah Bowman",1928,1949,92));
allPersons.add(new Person("Joshua Horton",1928,1973,91));
allPersons.add(new Person("W.D. Griffith",1939,1956,4));
allPersons.add(new Person("Arthur Graham",1936,1986,6));
allPersons.add(new Person("Ronald Little",1937,1975,7));
allPersons.add(new Person("S.M. Gray IV",1938,1982,6));
allPersons.add(new Person("N.M. Brewer",1939,1998,7));
allPersons.add(new Person("Craig Carter",1936,1989,16));
allPersons.add(new Person("Curtis Anderson",1938,1998,13));
allPersons.add(new Person("Cameron Lambert",1940,1965,13));
allPersons.add(new Person("Jesse Burton",1938,1959,11));
allPersons.add(new Person("Glen Barnett I",1938,1994,15));
allPersons.add(new Person("Eric Dean II",1939,1989,23));
allPersons.add(new Person("Craig Cooper II",1937,1973,25));
allPersons.add(new Person("Marie O. Abbott",1937,1983,23));
allPersons.add(new Person("Aaron Hill",1937,1972,23));
allPersons.add(new Person("W.Y. Blair",1939,1954,24));
allPersons.add(new Person("E.Q. Dawson",1936,1963,34));
allPersons.add(new Person("Ralph Love II",1940,1990,35));
allPersons.add(new Person("N.B. Butler",1937,1954,33));
allPersons.add(new Person("Ralph Q. Ingram",1940,1954,31));
allPersons.add(new Person("T.B. Chase",1938,1964,32));
allPersons.add(new Person("Gordon Lewis",1938,1972,45));
allPersons.add(new Person("Nicholas Barnett",1936,1983,41));
allPersons.add(new Person("John Arnold",1937,1962,41));
allPersons.add(new Person("Raymond Carlson",1940,1998,47));
allPersons.add(new Person("Steven Greene III",1938,1961,46));
allPersons.add(new Person("John Bradley",1936,1974,53));
allPersons.add(new Person("Keith Ingram I",1940,1958,53));
allPersons.add(new Person("P.B. Alexander",1937,1996,56));
allPersons.add(new Person("Peter J. Alexander",1938,1993,55));
allPersons.add(new Person("Michael Bennett Sr.",1940,1960,52));
allPersons.add(new Person("Frank Hawkins",1938,1968,61));
allPersons.add(new Person("Carl Glover",1937,1954,66));
allPersons.add(new Person("Connor Higgins",1937,1996,61));
allPersons.add(new Person("Austin Franklin",1938,1994,66));
allPersons.add(new Person("Bruce Austin II",1937,1955,65));
allPersons.add(new Person("Henry Lowe",1939,1974,70));
allPersons.add(new Person("Corey B. Ellis",1939,1965,74));
allPersons.add(new Person("Zachary Mackenzie",1937,1986,71));
allPersons.add(new Person("Jeremy R. Edwards",1937,1963,74));
allPersons.add(new Person("Bernard Caldwell",1939,1990,76));
allPersons.add(new Person("Nicholas Fraser",1939,1955,87));
allPersons.add(new Person("Mark Hunt",1939,1965,81));
allPersons.add(new Person("Gerald Kent",1939,1978,85));
allPersons.add(new Person("Russell V. Lyons",1938,1981,84));
allPersons.add(new Person("David Hunter",1936,1964,84));
allPersons.add(new Person("Joshua A. Harrison",1939,1957,97));
allPersons.add(new Person("Jeremy Jones",1940,1972,92));
allPersons.add(new Person("Steven Howard",1936,1957,93));
allPersons.add(new Person("Arthur Lawson",1938,1992,91));
allPersons.add(new Person("Andrew Foster",1936,1952,95));
allPersons.add(new Person("Anthony U. Brady",1949,1974,5));
allPersons.add(new Person("Kayla G. Hill",1948,1991,5));
allPersons.add(new Person("Stephen Burns I",1946,2001,4));
allPersons.add(new Person("Gerald Brooks",1947,1977,1));
allPersons.add(new Person("Zachary Jackson IV",1949,1964,3));
allPersons.add(new Person("H.X. Carter III",1948,1978,17));
allPersons.add(new Person("Ian Clark",1950,1960,10));
allPersons.add(new Person("Oscar Bates",1946,2002,10));
allPersons.add(new Person("Harry O. Henry",1948,1983,12));
allPersons.add(new Person("B.M. Harper",1946,1986,12));
allPersons.add(new Person("Travis Knight",1948,2007,25));
allPersons.add(new Person("Louis U. Barnett",1950,1973,24));
allPersons.add(new Person("Gregory Hughes",1949,1978,23));
allPersons.add(new Person("Timothy Chase Sr.",1947,2009,27));
allPersons.add(new Person("Darrell Henderson II",1947,1971,26));
allPersons.add(new Person("Norman Ingram",1949,1967,36));
allPersons.add(new Person("Alan Davis",1949,1995,32));
allPersons.add(new Person("Kevin B. Bell II",1949,2009,30));
allPersons.add(new Person("Earl U. Jordan II",1947,1993,35));
allPersons.add(new Person("John Graham",1947,1971,34));
allPersons.add(new Person("Kenneth Lambert I",1947,1988,41));
allPersons.add(new Person("Albert E. Baldwin",1946,1969,45));
allPersons.add(new Person("Jacob Bell",1948,1992,45));
allPersons.add(new Person("Clayton Kelly",1949,1976,43));
allPersons.add(new Person("Frank Lawson",1949,1997,43));
allPersons.add(new Person("Margaret Johnston-Barker",1947,1991,57));
allPersons.add(new Person("G.M. Macdonald",1950,1967,52));
allPersons.add(new Person("Gary Holland",1949,1980,56));
allPersons.add(new Person("Emma Hughes",1946,1980,53));
allPersons.add(new Person("Curtis Lucas I",1947,1961,53));
allPersons.add(new Person("Brandon Lyons",1948,1977,62));
allPersons.add(new Person("J.F. Johnson",1948,1960,60));
allPersons.add(new Person("Norman H. Carlson",1947,2008,65));
allPersons.add(new Person("Peter Griffin",1948,1968,62));
allPersons.add(new Person("Henry Lynch III",1946,2006,61));
allPersons.add(new Person("G.E. Drake II",1950,1990,70));
allPersons.add(new Person("Lawrence Giles I",1948,1992,72));
allPersons.add(new Person("Mabel Greene",1948,1998,77));
allPersons.add(new Person("Gregory Glass",1946,1961,77));
allPersons.add(new Person("A.W. Hayes",1947,1990,74));
allPersons.add(new Person("Dean Bell",1948,2007,87));
allPersons.add(new Person("Connor Hunt",1949,1963,83));
allPersons.add(new Person("I.W. Hunter",1950,1978,82));
allPersons.add(new Person("Joshua Gates III",1949,1998,84));
allPersons.add(new Person("Oscar Douglas IV",1946,1986,86));
allPersons.add(new Person("Barbara U. Hopkins",1949,2004,96));
allPersons.add(new Person("Tyler James",1946,2008,95));
allPersons.add(new Person("Todd Edwards",1948,2007,93));
allPersons.add(new Person("Melanie Carroll",1950,1983,97));
allPersons.add(new Person("Caleb Clayton II",1946,1988,94));
allPersons.add(new Person("Eugene Coleman",1958,1985,0));
allPersons.add(new Person("Chad Jennings",1959,1994,7));
allPersons.add(new Person("Ann Joseph",1959,1988,0));
allPersons.add(new Person("Andrew Brady III",1957,1974,0));
allPersons.add(new Person("Leonard Garrett",1959,1983,4));
allPersons.add(new Person("Clayton Glass",1956,1984,15));
allPersons.add(new Person("Andrew Beck Sr.",1960,2017,17));
allPersons.add(new Person("Leslie Henry",1959,1993,11));
allPersons.add(new Person("Jason Brady",1960,2014,16));
allPersons.add(new Person("Dean W. Bennett",1958,2019,17));
allPersons.add(new Person("Zachary Chapman I",1957,2003,24));
allPersons.add(new Person("Joseph Jordan",1956,1989,21));
allPersons.add(new Person("Michael K. Hudson I",1958,1985,20));
allPersons.add(new Person("Brandon Jackson",1956,2018,20));
allPersons.add(new Person("T.D. Lawson Jr.",1960,1981,23));
allPersons.add(new Person("Samuel D. Carroll",1958,1977,32));
allPersons.add(new Person("Victor Ellis",1956,2001,32));
allPersons.add(new Person("Wayne Dunn",1956,2016,35));
allPersons.add(new Person("Herbert Hudson",1960,2005,33));
allPersons.add(new Person("Raymond Mackenzie Sr.",1960,1976,34));
allPersons.add(new Person("Roger Brewer",1958,1990,47));
allPersons.add(new Person("Judith Harper",1959,2000,44));
allPersons.add(new Person("Cody Fraser Sr.",1958,1998,43));
allPersons.add(new Person("Tyler Y. Coleman",1956,1970,42));
allPersons.add(new Person("Ryan Griffin",1960,1988,46));
allPersons.add(new Person("Y.X. Hoffman",1960,2009,57));
allPersons.add(new Person("Gabriel Ball",1957,1994,56));
allPersons.add(new Person("Carol Garrett",1956,1996,52));
allPersons.add(new Person("Leah Boyd",1957,1997,52));
allPersons.add(new Person("Harry Jensen",1956,1986,55));
allPersons.add(new Person("Scott Fitzgerald",1959,2017,64));
allPersons.add(new Person("Joel Jackson",1958,1970,60));
allPersons.add(new Person("Gabriel Adams",1960,1988,63));
allPersons.add(new Person("Q.Y. James",1956,1982,67));
allPersons.add(new Person("Margaret W. Grant",1956,1984,64));
allPersons.add(new Person("Donald Z. Macdonald IV",1957,2007,72));
allPersons.add(new Person("M.L. Barnett",1958,2014,77));
allPersons.add(new Person("Thomas U. Kaufman",1959,1993,76));
allPersons.add(new Person("William H. Clarke",1956,1994,75));
allPersons.add(new Person("Harold G. Ferguson",1956,2009,73));
allPersons.add(new Person("Frederick Holt II",1956,1982,85));
allPersons.add(new Person("W.D. Gates",1958,1981,85));
allPersons.add(new Person("Steven Johnson Jr.",1958,1986,85));
allPersons.add(new Person("Edith Hodges-Caldwell",1960,2013,81));
allPersons.add(new Person("Marion A. Coleman-Graham",1957,1977,84));
allPersons.add(new Person("Charles Carpenter III",1958,1994,96));
allPersons.add(new Person("E.R. Kennedy",1960,2013,90));
allPersons.add(new Person("Warren Case Jr.",1959,1977,97));
allPersons.add(new Person("Arthur Hunt",1960,2017,93));
allPersons.add(new Person("B.C. Lyons",1958,1984,94));
allPersons.add(new Person("Todd Harrison",1966,1991,1));
allPersons.add(new Person("Donna Holland",1968,1980,4));
allPersons.add(new Person("Danielle V. Campbell",1969,1985,0));
allPersons.add(new Person("Marjorie Doyle-Burton",1969,2024,4));
allPersons.add(new Person("Franklin Arnold",1967,2006,5));
allPersons.add(new Person("Darrell Farmer",1969,2018,17));
allPersons.add(new Person("Connor E. Austin",1966,1981,13));
allPersons.add(new Person("Edward Little",1969,2005,10));
allPersons.add(new Person("X.U. Kennedy",1969,1995,14));
allPersons.add(new Person("Hunter T. Jenkins Jr.",1969,1984,15));
allPersons.add(new Person("Samuel Gibson",1966,2028,23));
allPersons.add(new Person("Thomas Barnett",1966,2007,24));
allPersons.add(new Person("Mitchell Hodges",1969,1986,26));
allPersons.add(new Person("Zachary Hardy",1966,1990,22));
allPersons.add(new Person("Clayton Hardy",1970,2010,24));
allPersons.add(new Person("Alexander Logan",1967,1985,31));
allPersons.add(new Person("Loretta Barnett-Fields",1966,2011,37));
allPersons.add(new Person("Joyce U. Black",1966,2015,31));
allPersons.add(new Person("Matthew Chandler",1969,2016,31));
allPersons.add(new Person("Noah R. Collins III",1968,2002,35));
allPersons.add(new Person("Owen A. Abbott Jr.",1970,2024,46));
allPersons.add(new Person("Alan Z. Doyle",1966,1980,45));
allPersons.add(new Person("Gladys Lambert",1968,2028,40));
allPersons.add(new Person("Norma Hodges",1970,1983,41));
allPersons.add(new Person("Craig Haynes",1969,2000,42));
allPersons.add(new Person("Brian Kelly IV",1968,1981,51));
allPersons.add(new Person("Martin Gross",1970,1982,56));
allPersons.add(new Person("David Burgess",1970,1997,57));
allPersons.add(new Person("Annette Hopkins",1968,2020,52));
allPersons.add(new Person("Kyle Bennett",1968,1988,52));
allPersons.add(new Person("Ralph X. Kennedy",1966,2006,66));
allPersons.add(new Person("Clifford V. Clayton",1970,2016,62));
allPersons.add(new Person("Karen E. Case",1967,2019,65));
allPersons.add(new Person("Walter Burgess",1970,2008,64));
allPersons.add(new Person("Gerald George",1967,2015,63));
allPersons.add(new Person("K.S. Jensen",1967,2028,73));
allPersons.add(new Person("Kenneth Lawson IV",1968,2019,76));
allPersons.add(new Person("Dean Logan",1968,2015,73));
allPersons.add(new Person("Jeffrey Y. Hicks",1969,2003,77));
allPersons.add(new Person("Dorothy Daniel",1966,2023,75));
allPersons.add(new Person("Clifford X. Kelly",1966,1992,83));
allPersons.add(new Person("Clifford Ingram",1969,2011,85));
allPersons.add(new Person("Richard Greene III",1968,2023,87));
allPersons.add(new Person("Ernest Lawrence Jr.",1969,1984,83));
allPersons.add(new Person("Jacob Jackson II",1969,1985,80));
allPersons.add(new Person("Lucy Carpenter-Fields",1969,2014,94));
allPersons.add(new Person("Eugene Cox",1968,2001,96));
allPersons.add(new Person("Linda Jacobs-Macdonald",1966,2010,94));
allPersons.add(new Person("M.I. Bush",1968,1980,97));
allPersons.add(new Person("Jack Carpenter",1970,1996,94));
allPersons.add(new Person("Stephen B. Daniel",1978,1993,1));
allPersons.add(new Person("Caleb Brady",1978,2027,7));
allPersons.add(new Person("Colin Davidson III",1978,2014,4));
allPersons.add(new Person("Mark Crawford",1979,2004,4));
allPersons.add(new Person("Ellen Holland",1978,2031,3));
allPersons.add(new Person("Roy R. Hammond",1980,1993,13));
allPersons.add(new Person("Henry Andrews",1976,2009,15));
allPersons.add(new Person("Amy Jackson-Daniel",1979,1995,15));
allPersons.add(new Person("April Fox",1977,2015,17));
allPersons.add(new Person("S.L. Brooks",1980,2038,14));
allPersons.add(new Person("T.R. Jordan III",1980,2005,23));
allPersons.add(new Person("Samuel Brown",1978,2035,27));
allPersons.add(new Person("Ian Chase",1980,2023,27));
allPersons.add(new Person("Thomas Davis",1978,2017,27));
allPersons.add(new Person("Luke Hopkins",1978,2020,24));
allPersons.add(new Person("Patrick Haynes",1980,2030,30));
allPersons.add(new Person("Barbara S. Henderson",1976,2039,33));
allPersons.add(new Person("Harvey M. Lee",1976,2007,33));
allPersons.add(new Person("Edith Hogan-Hawkins",1977,2036,33));
allPersons.add(new Person("Roy Hubbard",1978,2009,31));
allPersons.add(new Person("Jordan P. Hopkins",1978,1993,47));
allPersons.add(new Person("Mitchell Hale",1976,2022,46));
allPersons.add(new Person("Joshua Barker",1976,1999,42));
allPersons.add(new Person("Robert Cook Sr.",1980,1990,45));
allPersons.add(new Person("Timothy Bell",1976,1996,43));
allPersons.add(new Person("Deborah Bryant-Hoffman",1979,2035,51));
allPersons.add(new Person("Corey Copeland",1979,2004,53));
allPersons.add(new Person("Ernest Farmer",1980,2027,51));
allPersons.add(new Person("Jordan Jensen I",1980,2037,51));
allPersons.add(new Person("Scott Allen",1977,2014,55));
allPersons.add(new Person("Francis Copeland IV",1976,2037,65));
allPersons.add(new Person("Lawrence Hunt",1976,2021,61));
allPersons.add(new Person("Ronald T. Coleman",1979,2014,65));
allPersons.add(new Person("Kenneth Drake",1976,1996,62));
allPersons.add(new Person("O.M. Barrett IV",1977,2030,62));
allPersons.add(new Person("Jennifer Hicks-Case",1980,2034,74));
allPersons.add(new Person("Matthew Carlson",1977,2036,71));
allPersons.add(new Person("Q.C. Hubbard III",1979,2002,72));
allPersons.add(new Person("Alan Graham",1979,2026,76));
allPersons.add(new Person("Colin Bush",1976,2015,74));
allPersons.add(new Person("Derek Hudson",1978,2039,81));
allPersons.add(new Person("Jeremy Harvey",1978,2037,85));
allPersons.add(new Person("Mitchell Gibson",1979,1998,86));
allPersons.add(new Person("Victor Doyle",1978,2027,81));
allPersons.add(new Person("D.D. Crawford I",1979,2014,82));
allPersons.add(new Person("Michael Clayton",1980,2000,92));
allPersons.add(new Person("Samuel Hall",1980,2030,96));
allPersons.add(new Person("P.G. Griffin",1977,2022,96));
allPersons.add(new Person("J.H. Brown",1979,2009,93));
allPersons.add(new Person("Gerald Holloway IV",1978,2035,96));
allPersons.add(new Person("W.N. Carter III",1988,2004,6));
allPersons.add(new Person("Jordan Knight",1988,2045,2));
allPersons.add(new Person("U.H. Lambert",1986,2012,4));
allPersons.add(new Person("William A. Barker",1990,2030,2));
allPersons.add(new Person("Zachary Freeman IV",1987,2037,4));
allPersons.add(new Person("Clifford Burgess",1986,2027,12));
allPersons.add(new Person("Z.E. Blake",1990,2002,16));
allPersons.add(new Person("Mary Bowen",1990,2002,11));
allPersons.add(new Person("Jamie Bowen",1989,2010,15));
allPersons.add(new Person("Kimberly Byrd-Mackenzie",1986,2036,12));
allPersons.add(new Person("Edith Drake",1989,2042,25));
allPersons.add(new Person("Lawrence G. Beck",1987,2004,21));
allPersons.add(new Person("Brian Clayton",1990,2000,23));
allPersons.add(new Person("G.M. Baldwin III",1989,2005,23));
allPersons.add(new Person("Mabel Hunter",1990,2048,20));
allPersons.add(new Person("O.N. Brooks",1987,2009,36));
allPersons.add(new Person("Grace Gordon",1989,2015,36));
allPersons.add(new Person("Philip Evans",1989,2029,32));
allPersons.add(new Person("Cody P. Cross II",1987,2025,35));
allPersons.add(new Person("C.E. Burgess",1986,2009,30));
allPersons.add(new Person("Diane Hicks",1990,2033,45));
allPersons.add(new Person("Judith T. Drake",1987,2007,41));
allPersons.add(new Person("Charles Duncan",1988,2016,47));
allPersons.add(new Person("Luke Boyd",1986,2043,46));
allPersons.add(new Person("Linda Jacobs",1986,2047,47));
allPersons.add(new Person("Jonathan Crawford",1988,2004,51));
allPersons.add(new Person("Joyce Jacobs-Bailey",1989,2003,57));
allPersons.add(new Person("W.M. Fleming",1988,2008,52));
allPersons.add(new Person("Y.Q. Johnson",1989,2030,55));
allPersons.add(new Person("Derek J. Lawson",1990,2014,50));
allPersons.add(new Person("Martin Farmer",1990,2017,65));
allPersons.add(new Person("Mabel Bates-Hines",1988,2038,65));
allPersons.add(new Person("Jane Cobb",1990,2016,63));
allPersons.add(new Person("June Glass",1990,2011,65));
allPersons.add(new Person("Shawn Davis",1990,2014,63));
allPersons.add(new Person("Keith Hogan",1988,2028,75));
allPersons.add(new Person("Esther U. Cook",1988,2028,76));
allPersons.add(new Person("Lewis R. Jacobs",1986,2046,73));
allPersons.add(new Person("Joel G. Hall",1987,2015,77));
allPersons.add(new Person("James Kennedy",1986,2004,75));
allPersons.add(new Person("Stephen Clark",1986,2027,80));
allPersons.add(new Person("Dustin Coleman",1989,2000,81));
allPersons.add(new Person("Joseph Hogan",1990,2047,81));
allPersons.add(new Person("Abigail Barnes",1988,2045,83));
allPersons.add(new Person("Steven M. Brooks",1988,2042,85));
allPersons.add(new Person("Danny Hart",1987,2028,92));
allPersons.add(new Person("Jason T. Graham",1989,2027,92));
allPersons.add(new Person("Madeline Lawson-Atkinson",1990,2028,95));
allPersons.add(new Person("Anna Case",1988,2010,90));
allPersons.add(new Person("Natalie Freeman",1987,2009,93));
allPersons.add(new Person("Jack Austin",1996,2028,3));
allPersons.add(new Person("Jack Kelley",1998,2046,2));
allPersons.add(new Person("Laurie Kelly",1998,2033,4));
allPersons.add(new Person("Janet Kennedy",1999,2020,4));
allPersons.add(new Person("Maxwell Black",1998,2020,4));
allPersons.add(new Person("Evelyn Harrison",1999,2016,13));
allPersons.add(new Person("Terrence Lee",2000,2052,13));
allPersons.add(new Person("D.B. Byrd",1996,2044,12));
allPersons.add(new Person("John M. Gordon",2000,2036,16));
allPersons.add(new Person("Janet Lynch",2000,2038,16));
allPersons.add(new Person("Craig Brown",1996,2056,25));
allPersons.add(new Person("Lisa Coleman",2000,2011,26));
allPersons.add(new Person("Daisy Dixon-Coleman",1999,2014,24));
allPersons.add(new Person("Howard Brewer",1998,2042,23));
allPersons.add(new Person("Douglas Jennings IV",1998,2020,24));
allPersons.add(new Person("April Howard",1997,2018,30));
allPersons.add(new Person("Keith I. Jacobs",1996,2016,37));
allPersons.add(new Person("Hunter N. Bailey III",1996,2036,30));
allPersons.add(new Person("Barbara James",1997,2016,32));
allPersons.add(new Person("Kenneth Jacobs",1997,2055,31));
allPersons.add(new Person("Joyce Hart",1999,2059,40));
allPersons.add(new Person("Lucille Bush",1999,2014,46));
allPersons.add(new Person("Louis Lane",1997,2058,41));
allPersons.add(new Person("Brenda Hudson",1996,2018,43));
allPersons.add(new Person("Y.Q. Hudson II",1997,2040,47));
allPersons.add(new Person("Thomas Fowler I",1996,2014,53));
allPersons.add(new Person("Nancy Hicks",1999,2013,57));
allPersons.add(new Person("A.C. Greene",1999,2011,54));
allPersons.add(new Person("Edwin P. Drake",1998,2042,52));
allPersons.add(new Person("Patrick Hampton",2000,2040,55));
allPersons.add(new Person("Jane Boyd",1996,2029,62));
allPersons.add(new Person("Edward P. Howard",1998,2020,66));
allPersons.add(new Person("Brian S. Coleman",1996,2025,66));
allPersons.add(new Person("Joyce Kennedy-Greene",2000,2053,67));
allPersons.add(new Person("Jesse Dutton Jr.",1998,2026,62));
allPersons.add(new Person("Carla Lawson-Hubbard",1998,2041,74));
allPersons.add(new Person("Nancy Boyd",1999,2048,74));
allPersons.add(new Person("Linda D. Dutton",2000,2047,72));
allPersons.add(new Person("Walter Kelly II",2000,2010,77));
allPersons.add(new Person("Stanley Jones",1997,2015,72));
allPersons.add(new Person("Lucy Hubbard-Cook",1997,2013,81));
allPersons.add(new Person("Marie I. Collins",1997,2038,84));
allPersons.add(new Person("Dustin J. Bowman II",2000,2017,83));
allPersons.add(new Person("April Chambers",1997,2044,82));
allPersons.add(new Person("Paul Henry",1997,2019,87));
allPersons.add(new Person("Maria Hamilton",2000,2058,94));
allPersons.add(new Person("J.G. Fox",1998,2031,95));
allPersons.add(new Person("E.D. Macdonald",2000,2015,97));
allPersons.add(new Person("Anna Burns",1998,2050,96));
allPersons.add(new Person("Scott Carroll",1998,2021,96));
allPersons.add(new Person("Richard Brewer",2008,2055,3));
allPersons.add(new Person("Joshua Z. Kelley",2009,2044,4));
allPersons.add(new Person("Howard Hines",2006,2064,3));
allPersons.add(new Person("Darrell Farmer",2007,2055,1));
allPersons.add(new Person("Diane Anderson",2009,2064,3));
allPersons.add(new Person("Joanne Fitzgerald",2009,2063,11));
allPersons.add(new Person("Wayne W. Bryant",2006,2066,16));
allPersons.add(new Person("John Dixon Jr.",2010,2023,12));
allPersons.add(new Person("X.X. Grant",2010,2060,14));
allPersons.add(new Person("Robert Anderson Jr.",2010,2032,13));
allPersons.add(new Person("Carol Boyd-Harrison",2010,2051,27));
allPersons.add(new Person("Bradley Gregory",2006,2026,22));
allPersons.add(new Person("Harry B. Ellis",2007,2057,26));
allPersons.add(new Person("Josephine Fleming",2006,2041,21));
allPersons.add(new Person("Glen Ball",2010,2065,23));
allPersons.add(new Person("Dean Lee",2008,2037,30));
allPersons.add(new Person("Martha Hines",2006,2065,35));
allPersons.add(new Person("Richard Cox",2006,2025,31));
allPersons.add(new Person("Lucas X. Case",2007,2057,37));
allPersons.add(new Person("Andrew Elliott",2008,2023,33));
allPersons.add(new Person("Janet Ball",2007,2026,40));
allPersons.add(new Person("G.H. Hamilton",2008,2050,40));
allPersons.add(new Person("Gordon Glover",2006,2044,41));
allPersons.add(new Person("Edna U. Fowler",2010,2054,42));
allPersons.add(new Person("Christina Bowman",2006,2054,40));
allPersons.add(new Person("Curtis S. Hale",2007,2068,50));
allPersons.add(new Person("Leah Hogan-Glass",2006,2027,57));
allPersons.add(new Person("P.I. Griffin",2007,2036,57));
allPersons.add(new Person("Lewis Anderson",2009,2045,54));
allPersons.add(new Person("Melissa Lyons",2007,2026,55));
allPersons.add(new Person("Kathleen L. Caldwell",2010,2027,62));
allPersons.add(new Person("Timothy K. Jacobs",2007,2023,63));
allPersons.add(new Person("Ronald Hill",2008,2035,64));
allPersons.add(new Person("Y.W. Ford",2008,2026,60));
allPersons.add(new Person("Travis X. Gordon",2006,2057,61));
allPersons.add(new Person("Clarence Duncan",2009,2068,73));
allPersons.add(new Person("Iris Davidson",2008,2065,74));
allPersons.add(new Person("Ann H. Gates-Cross",2007,2050,70));
allPersons.add(new Person("Louis Burke",2006,2042,75));
allPersons.add(new Person("Maureen Lynch",2010,2062,74));
allPersons.add(new Person("Marilyn Cross",2008,2028,82));
allPersons.add(new Person("M.E. Hampton",2009,2036,85));
allPersons.add(new Person("Derek Freeman Sr.",2006,2045,85));
allPersons.add(new Person("Norman Mackenzie",2006,2029,87));
allPersons.add(new Person("Philip Davidson",2007,2049,87));
allPersons.add(new Person("Matthew Bailey",2007,2040,94));
allPersons.add(new Person("Dawn Lane",2006,2048,96));
allPersons.add(new Person("Dale Foster",2010,2032,96));
allPersons.add(new Person("Hannah E. Barnes",2006,2044,94));
allPersons.add(new Person("Evan Copeland",2010,2049,90));
allPersons.add(new Person("Eugene Crawford",2019,2031,3));
allPersons.add(new Person("Frank Hunter",2019,2042,4));
allPersons.add(new Person("Dana Hammond",2019,2037,1));
allPersons.add(new Person("Pamela Gibson-Johnston",2016,2073,2));
allPersons.add(new Person("Daniel Kaufman",2017,2050,2));
allPersons.add(new Person("Wayne Fraser",2017,2074,13));
allPersons.add(new Person("Jill Edwards",2017,2066,14));
allPersons.add(new Person("Nicole Holt-Hicks",2016,2044,13));
allPersons.add(new Person("Daniel S. Joseph",2019,2058,11));
allPersons.add(new Person("Carl Austin",2017,2042,14));
allPersons.add(new Person("Z.D. Mackenzie",2017,2033,20));
allPersons.add(new Person("Katherine Brady-Fraser",2020,2031,24));
allPersons.add(new Person("A.P. Edwards",2016,2033,26));
allPersons.add(new Person("Vincent F. Clayton",2018,2054,25));
allPersons.add(new Person("Ross Harrison",2018,2047,25));
allPersons.add(new Person("E.Z. Hudson",2019,2076,36));
allPersons.add(new Person("Colleen Fraser-Johnston",2018,2073,30));
allPersons.add(new Person("Madeline Jensen",2019,2046,30));
allPersons.add(new Person("Isaac Elliott",2016,2072,35));
allPersons.add(new Person("Jennifer Howard",2018,2070,35));
allPersons.add(new Person("Catherine D. Lee",2017,2051,43));
allPersons.add(new Person("Raymond Brooks",2018,2076,40));
allPersons.add(new Person("Grace Holloway",2016,2049,41));
allPersons.add(new Person("Jackie Johnston-Carter",2017,2052,46));
allPersons.add(new Person("Elaine Logan",2016,2066,46));
allPersons.add(new Person("Jennifer Griffith",2019,2065,51));
allPersons.add(new Person("Norman Little",2017,2038,54));
allPersons.add(new Person("Timothy Gordon II",2016,2037,55));
allPersons.add(new Person("Warren Lambert",2017,2072,56));
allPersons.add(new Person("Joanne Elliott",2020,2051,55));
allPersons.add(new Person("Zachary M. Cobb",2019,2079,64));
allPersons.add(new Person("Erin Graham-Garrett",2016,2065,60));
allPersons.add(new Person("Connor Leonard",2019,2077,64));
allPersons.add(new Person("Andrew Chapman",2019,2054,67));
allPersons.add(new Person("Abigail Green",2019,2045,61));
allPersons.add(new Person("Clayton K. Harrison",2017,2036,75));
allPersons.add(new Person("Janice Dutton",2020,2078,72));
allPersons.add(new Person("Diane Henderson-Hawkins",2017,2045,72));
allPersons.add(new Person("Y.H. Alexander",2018,2071,73));
allPersons.add(new Person("Louis Lucas",2017,2056,71));
allPersons.add(new Person("O.S. Gordon",2018,2046,83));
allPersons.add(new Person("Michelle Bradley",2018,2055,83));
allPersons.add(new Person("Isaac Clements",2019,2036,83));
allPersons.add(new Person("Gordon S. Daniels Jr.",2016,2059,87));
allPersons.add(new Person("Iris Daniels",2019,2032,85));
allPersons.add(new Person("Sean Fisher",2020,2075,92));
allPersons.add(new Person("Loretta Cunningham",2019,2043,93));
allPersons.add(new Person("L.U. Bishop",2018,2045,96));
allPersons.add(new Person("Marjorie Barnett",2020,2061,97));
allPersons.add(new Person("Jane U. Edwards",2016,2052,91));
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
            
            if(par == President){
                points*=5;
            }
            
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
        tresh = 70+ra.nextInt(20);
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
        
        tresh = 65+ra.nextInt(25);
        
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
        President.getStandardB().setPresToTrue();
        President.getStandardB().incrementPrescount();
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

public static boolean eCrisis = false;
public static int eCrisisCdown = 0;

public static boolean eBoom = false;
public static int eBoomCdown = 0;



public static void events(){
    boolean eventHappened = false;
    if(ra.nextInt(10)<5){
       
        switch(ra.nextInt(8)){
            case 0:
                if(!eBoom){
                if(!eCrisis){
                    eCrisis = true;
                    eCrisisCdown= ra.nextInt(2)+1;
                }else{
                    eCrisisCdown+= ra.nextInt(2)+1;
                }
                }
            
                break;
            case 1:
                if(!eCrisis){
                if(!eBoom){
                    eBoom = true;
                    eBoomCdown= ra.nextInt(2)+1;
                }else{
                    eBoomCdown+= ra.nextInt(2)+1;
                }
                
                }
                break;
            case 2:
                System.out.println("Labor Strikes!");
            leftShift();
                break;
            case 3:
                System.out.println("Immigration Crisis!");
            rightShift();
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

public static void leftShift(){
    for(ideoGroup gro: allGroups){
        int defectors = gro.getSize()/50;
        gro.updateSize(-defectors);
        
        ideoGroup target=null;
        target = findClosestGroup(gro.getIdeology()+10);
        
        if(target!=null) target.updateSize(defectors);
    }
}
public static void rightShift(){
    for(ideoGroup gro: allGroups){
        int defectors = gro.getSize()/50;
        gro.updateSize(-defectors);
        
        ideoGroup target=null;
        target = findClosestGroup(gro.getIdeology()-10);
        
        if(target!=null) target.updateSize(defectors);
    }
}

public static void centerShift(){
    for(ideoGroup gro: allGroups){
        int defectors = gro.getSize()/50;
        gro.updateSize(-defectors);
        
        ideoGroup target=null;
        if(gro.getIdeology()<50){
            target = findClosestGroup(gro.getIdeology()+10);
        }else{
            target = findClosestGroup(gro.getIdeology()-10);
        }
        
        
        if(target!=null) target.updateSize(defectors);
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

public static void nationalState(){
    int economy = ra.nextInt(11);
    int peoplesApproval = ra.nextInt(11);
    int stability = ra.nextInt(11);
    int agenda = ra.nextInt(11);
    
    agenda += rulingCoalition.getSize()/20;
    
    if(!rulingCoalition.getMemberList().contains(President)) agenda-=2;
    
    stability += rulingCoalition.getSize()/10;
    
    economy += (stability-5);
    economy += (agenda-5);
    
    if(eCrisis){
        eCrisisCdown--;
        eCrisis = eCrisisCdown>0;
        economy--;
    }
    
    if(eBoom){
        eBoomCdown--;
        eBoom= eBoomCdown>0;
        economy++;
    }
    
    peoplesApproval += (economy-5);
    peoplesApproval+= (agenda-5);
    
    if(stability > 6){
        centerShift();
    }
    if(stability<5){
        leftShift();
        rightShift();
    }
    
    if(stability<0){
        stability = 0;
    }
    if(stability>10){
        stability = 10;
    }
    
    if(peoplesApproval<0){
        peoplesApproval=0;
    }
    if(peoplesApproval>10){
        peoplesApproval=10;
    }
    if(economy<0){
        economy = 0;
    }
    if(economy>10){
        economy = 10;
    }
    if(agenda<0){
        agenda = 0;
    }
    if(agenda>10){
        agenda = 10;
    }
    
    for(Party par: rulingCoalition.getMemberList()){
        if(peoplesApproval >5){
            for(int i=0; i< peoplesApproval-5;i++){
                par.incrementRecognition();
            }
        }else{
            for(int i=0; i< 5-peoplesApproval;i++){
                par.addFatigue();
                par.addFatigue();
            }
        }
    }
    
    String[] colors = {
    "\u001B[38;5;196m", // 0: Pure Red
    "\u001B[38;5;202m", // 1: Red-Orange
    "\u001B[38;5;208m", // 2: Orange
    "\u001B[38;5;214m", // 3: Orange-Yellow
    "\u001B[38;5;220m", // 4: Yellow-Gold
    "\u001B[38;5;226m", // 5: Pure Yellow
    "\u001B[38;5;190m", // 6: Yellow-Green (Lime)
    "\u001B[38;5;154m", // 7: Light Green
    "\u001B[38;5;118m", // 8: Bright Green
    "\u001B[38;5;46m",  // 9: Primary Green
    "\u001B[38;5;34m"   // 10: Deep Success Green
};
String reset = "\u001B[0m";
System.out.println("==========================================================================================");
    System.out.print(reset+"The Economy is " + colors[economy]);
    switch(economy){
        case 0: System.out.println("Collapsing");
            break;
        case 1:System.out.println("Depressing");
            break;
        case 2:System.out.println("Crashing");
            break;
        case 3:System.out.println("Shrinking");
            break;
        case 4:System.out.println("Stagnating");
            break;
        case 5:System.out.println("Stagnating");
            break;
        case 6:System.out.println("Stagnating");
            break;
        case 7:System.out.println("Growing");
            break;
        case 8:System.out.println("Rising");
            break;
        case 9:System.out.println("Expanding");
            break;
        case 10:System.out.println("Booming");
    }
    
    System.out.print(reset+"Our Public Approval is " + colors[peoplesApproval]);
    switch(peoplesApproval){
        case 0: System.out.println("In Hell");
            break;
        case 1:System.out.println("Underwater");
            break;
        case 2:System.out.println("Crashing");
            break;
        case 3:System.out.println("Dropping");
            break;
        case 4:System.out.println("Downward");
            break;
        case 5:System.out.println("Steady");
            break;
        case 6:System.out.println("Upward");
            break;
        case 7:System.out.println("Rising");
            break;
        case 8:System.out.println("Flying");
            break;
        case 9:System.out.println("Sky High");
            break;
        case 10:System.out.println("In Space");
    }
    
    System.out.print(reset+"The Nation is " + colors[stability]);
    switch(stability){
        case 0: System.out.println("Burning");
            break;
        case 1:System.out.println("In Crisis");
            break;
        case 2:System.out.println("Tearing by the seams");
            break;
        case 3:System.out.println("Losing it");
            break;
        case 4:System.out.println("Pannicking");
            break;
        case 5:System.out.println("Quiet");
            break;
        case 6:System.out.println("Content");
            break;
        case 7:System.out.println("Happy");
            break;
        case 8:System.out.println("Peaceful");
            break;
        case 9:System.out.println("Orderly");
            break;
        case 10:System.out.println("Completely Unified");
    }
    
    System.out.print(reset+"Our Agenda is " + colors[agenda]);
    switch(agenda){
        case 0: System.out.println("Ripped to shreds");
            break;
        case 1:System.out.println("In the trash bin");
            break;
        case 2:System.out.println("Dead in the water");
            break;
        case 3:System.out.println("Dead on arrival");
            break;
        case 4:System.out.println("On life support");
            break;
        case 5:System.out.println("Compromised");
            break;
        case 6:System.out.println("realized in the Bare Minimum");
            break;
        case 7:System.out.println("Somewhat realized");
            break;
        case 8:System.out.println("Partially realized");
            break;
        case 9:System.out.println("Mostly realized");
            break;
        case 10:System.out.println("Fully realized");
    }
    System.out.println(reset+"==========================================================================================");
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
        List<Person> governmentFigures = new ArrayList<>();
        List<Person> oppositionFigures = new ArrayList<>();
        for(int i=0; i<5;i++){
            Person maxper=null;
            int maxnum=Integer.MIN_VALUE;
            
            for(Person per: activePersons){
                int points = per.getProminence();
                if(points > maxnum && !governmentFigures.contains(per) && rulingCoalition.getMemberList().contains(per.getCurrentParty())){
                    maxnum = points;
                    maxper = per;
                }
            }
            
                governmentFigures.add(maxper);
            
        }
        
        for(int i=0; i<5;i++){
            Person maxper=null;
            int maxnum=Integer.MIN_VALUE;
            
            for(Person per: activePersons){
                int points = per.getProminence();
                if(points > maxnum && !oppositionFigures.contains(per) && !rulingCoalition.getMemberList().contains(per.getCurrentParty())){
                    maxnum = points;
                    maxper = per;
                }
            }
            
                oppositionFigures.add(maxper);
            
        }
        System.out.println("Prominent Pro-Government Politicians: ");
        for(Person per: governmentFigures){
            if(per !=null){
            System.out.print(per+ " | ");
            }
        }
        System.out.println("\nProminent Opposition Politicians: ");
        for(Person per: oppositionFigures){
            if(per!=null){
            System.out.print(per+ " | ");
            }
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
    nationalState();
    //displayRegionResults();
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
