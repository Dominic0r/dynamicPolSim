 import java.io.FileWriter;
import java.io.IOException;
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
        
        Party favParty = null;
        
        int activeyear = 0;
        
        public ideoGroup(String name, String splintername, int size, int ideology, int activeyear){
            this.name = name;
            this.splintername = splintername;
            this.size = size;
            this.ideology = ideology;
            this.satisfaction = 100;
            this.activeyear = activeyear;
            
        }
        
        public String getSplinterName(){return splintername;}
        public String getName(){return name;}
        public int getSize(){return size;}
        public int getIdeology(){return ideology;}
        public int getSatisfaction(){return satisfaction;}
        public int getActiveYear(){return activeyear;}
        
        public void findFavParty(){
            Party maxPar = favParty;
            int maxnum = Integer.MIN_VALUE;
            int tresh =10;
            
            for(Party par: allParties){
                if(par.proximityWith(this.ideology)> maxnum){
                    if(favParty!=null && par!=favParty){
                        int favscore = favParty.proximityWith(this.ideology);
                        favscore += (favscore*favParty.getRecognition())/2;
                        favscore -= (favscore*favParty.getFatigue())/2;
                        favscore += favParty.getPercent()/20;
                        
                        
                        int curparscore = par.proximityWith(this.ideology);
                        curparscore += (curparscore*par.getRecognition())/2;
                        curparscore -= (curparscore*par.getFatigue())/2;
                        curparscore += par.getPercent()/20;
                        //System.out.println("curparscore "+ curparscore);
                        
                        //System.out.println("favscore "+ favscore);
                        if(curparscore-favscore>10){
                            
                            maxnum = par.proximityWith(this.ideology);
                            maxPar = par;
                        }
                    }else{
                        maxnum = par.proximityWith(this.ideology);
                        maxPar = par;
                    }
                }
            }
            
            if(maxPar!=null){
                favParty = maxPar;
            }
        }
        
        public Party getFavPar(){return favParty;}
        
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
            if(size<1){
                size =1;
            }
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
        
        ideoGroup largestGroup = null;
        
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
            popularity = 100;
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
                    }else if(per.noOfTimesBecamePresident() >1){
                        if(lean.equalsIgnoreCase("Republic")){
                            points /=10;
                        }
                    }
                }
                
                if(!lean.equalsIgnoreCase("Republic")){
                    points *=100;
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
                    points *=100;
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
            if(largestGroup!=null){
            return RESET+" ("+getDynamicColor(this.ideology) + ", "+ largestGroup.getName() +")";
            }else{
                return RESET+" ("+getDynamicColor(this.ideology) +")";
            }
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
            if(popularity>100){
                popularity=100;
            }
            if(popularity<1){
                popularity=1;
            }
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
            int allIdeoTargets = 0;
            int totalweight = 0;
            
            int finaltargetIdeo = 0; // = allIdeoTargets/totalweight
            
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
            largestGroup = maxGroup;
            int targetIdeo = maxGroup.getIdeology();
            allIdeoTargets += targetIdeo*5;
            totalweight+=5;
            
            
            
            int minsat = 1000;
            ideoGroup minGroup = null;
            for(ideoGroup gro : allGroups){
                if(gro.getSatisfaction()< minsat){
                    minsat = gro.getSatisfaction();
                    minGroup = gro;
                }
            }
            
            targetIdeo = minGroup.getIdeology();
            allIdeoTargets += targetIdeo*3;
            totalweight+=3;
            
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
                allIdeoTargets += (avgideo)*3;
                totalweight+=3;
            
            
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
            
            
                if(this.ideology > maxpar.getIdeology()){
                //this.ideology--;
                
                allIdeoTargets += (this.ideology-1)*1;
                totalweight+=1;
                
                }else{
                    allIdeoTargets += (this.ideology+1)*1;
                    totalweight+=1;
                }
                
                if(this.ideology > minpar.getIdeology()){
                    allIdeoTargets += (this.ideology+1)*1;
                    totalweight+=1;
                }else{
                    allIdeoTargets += (this.ideology-1)*1;
                    totalweight+=1;
                }
            
            
            if(standardBearer!=null){
                if(this.ideology > standardBearer.getIdeology()){
                    allIdeoTargets += (this.ideology-1)*2;
                    totalweight+=2;
                }else{
                    allIdeoTargets += (this.ideology+1)*2;
                    totalweight+=2;
                }
            }
            
            if(chairman!=null){
                if(this.ideology > chairman.getIdeology()){
                    allIdeoTargets += (this.ideology-1)*2;
                    totalweight+=2;
                }else{
                    allIdeoTargets += (this.ideology+1)*2;
                    totalweight+=2;
                }
            }
            
            finaltargetIdeo = allIdeoTargets/totalweight;
            
            if(this.ideology> finaltargetIdeo){
                this.ideology -= ra.nextInt(2)+1;
            }else{
                this.ideology += ra.nextInt(2)+1;
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
    
    
    
    public static class Person{
        String name;
        int startYear, endYear;
        int ideology;
        int prominence;
        Party currentParty;
        
        int loyalty; 
        int ambition; // adds 5 points for every 10
        int charisma; // multiplies for every 20+1
        int corruption; // random chance to just get nuked in prominence
        int pragmatism; // low pragmatism incurrs larger penalties for the distance between a person and their aprty's ideology;
        
        boolean hasbeenPresident=false;
        int prescount=0;
        
        public Person(String name, int startYear, int endYear, int ideology, int loyalty, int ambition, int charisma, int corruption, int pragmatism){
            this.name=name;
            this.startYear=startYear;
            this.endYear=endYear- ((endYear-startYear)/2);
            this.ideology=ideology;
            this.loyalty= loyalty;
            this.ambition=ambition;
            this.charisma = charisma;
            this.corruption = corruption;
            this.pragmatism = pragmatism;
            
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
        public int getIdeology(){return ideology;}
        
        public void incrementPrescount(){
            prescount++;
        }
        
        public void setPresToTrue(){ hasbeenPresident=true;}
        
        public void determineParty(){
            Party maxpar=null;
            int maxnum = Integer.MIN_VALUE;
            
            for(Party par: allParties){
                int points = ((proximityWith(par)/4)*3);
                if(par == currentParty){
                    points += points/2;
                    points *= (loyalty/20)+1;
                    
                    if(pragmatism<50){
                        points -= ((50-pragmatism)/10)* (points/20);
                    }
                    
                }
                if(pragmatism>50 && currentParty!=null){
                        points += (points/20)* (currentParty.getPercent()/(((100-pragmatism)/2)+1));
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
                    //prominence += prominence/2;
                }
                
                if(!lean.equalsIgnoreCase("Republic")){
                    if(this == President.getStandardB()){
                        prominence+=100;
                    }
                }
                
                prominence += (ambition/10)*5;
                prominence+= (prominence/10)*(charisma/10);
                if(ra.nextInt(100)<corruption){
                    prominence/= (corruption/10)+1;
                }
                prominence *= (year-startyear)/5;
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
    
    public static class Policy{
        String name;
        int position;
        public Policy(String name){
            this.name = name;
            this.position=50;
        }
        
        public int getPosition(){
            return position;
        }
        
        public String getName(){
            return name;
        }
        
        public void updatePos(int toAdd){
            position+=toAdd;
            if(position>100){
                position=100;
            }
            if(position<1){
                position=1;
            }
            
        }
        
        
        
        /*
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
        */
        @Override
        public String toString(){
            String pos="[L]";
            for(int i=20; i>0;i--){
                pos+="\u001B[38;5;";
                if(i<4){
                    pos+=NAVYBLUE;
                }else if(i<7){
                    pos+=BLUE;
                }else if(i<9){
                    pos+=ORANGE;
                }else if(i<11){
                    pos+=YELLOW;
                }else if(i<13){
                    pos+=LIGHTRED;
                }else if(i<16){
                    pos+=RED;
                }else{
                    pos+=DARKRED;
                }
                pos+="m";
                if(i!=position/5){
                    
                    pos+="-";
                }else{
                    pos+="o";
                }
                pos+=RESET;
            }
            pos+="[R]";
            int maxsize=-1;
            
            for(Policy pol: allPolicies){
                if(pol.getName().length() > maxsize){
                    maxsize = pol.getName().length();
                }
            }
            
            maxsize -= this.name.length();
            maxsize++;
            String space ="";
            for(int i=0; i<maxsize;i++){
                space+=" ";
            }
            
            return name+space+pos;
        }
        
    }
    
    public static class SCJustice{
            int ideology;
            int timeleft;
            int bias;
            int conservatism;
            
            public SCJustice(int ideology){
                this.ideology=ideology;
                this.timeleft=ra.nextInt(8)+5;
                bias = ra.nextInt(5);
                if(ideology<35 || ideology>65){
                    bias += ra.nextInt(3)+2;
                }
                int avgpol=0;
                for(Policy pol: allPolicies){
                    avgpol = pol.getPosition();
                }
                avgpol /= allPolicies.size();
                
                int alignswithpres = (President==null)?0 :President.proximityWith(avgpol)/20;
                conservatism = ra.nextInt(5)+ alignswithpres;
                if(alignswithpres<2 && President!=null){
                    conservatism/=2;
                }
            }
            public int getIdeology(){ return ideology;}
            public int getTimeLeft(){ return ideology;}
            public int getBias(){return bias*10;}
            public int getCons(){return conservatism*5;}
            public void countDown(){ timeleft--;}
            public boolean hasRunOut(){
                return timeleft==0;
            }
        }
        
        public static List<SCJustice> supremeCourt = new ArrayList<>();
        public static int SCsize = 9;
        public static void SCJCountdown(){
            for(SCJustice jus : supremeCourt){
                jus.countDown();
            }
        }
        
        public static void checkSCCCdown(){
            List<SCJustice> toRemove = new ArrayList<>();
            for(SCJustice jus : supremeCourt){
                if(jus.hasRunOut()){
                    toRemove.add(jus);
                }
            }
            supremeCourt.removeAll(toRemove);
        }
        public static boolean checkVacancies(){
            return supremeCourt.size()>=SCsize;
        }
        
        public static void fillVacancies(){
            int ideology=0;
            int preswei = 3;
            int presideo = President.getIdeology()*preswei;
            
            int pmwei = 1;
            int pmideo = rulingCoalition.getLeader().getIdeology()*pmwei;
            
            int speakerwei = 2;
            int speakerideo = speaker.getIdeology()*speakerwei;
            
            int finalideo = (presideo+pmideo+speakerideo)/(preswei+pmwei+speakerwei);
            supremeCourt.add(new SCJustice(finalideo));
            
        }
        
        public static void initSetup(){
            for(int i=0; i<SCsize;i++){
                supremeCourt.add(new SCJustice(50));
            }
        }
        
        public static void SCCheck(){
            if(!checkVacancies()){
                do{
                    fillVacancies();
                }while(!checkVacancies());
            }
        }
        
        public static void displaySC(){
            String sccomp = "[";
            int left=0,right=0,center=0;
            for(SCJustice jus: supremeCourt){
                if(jus.getIdeology()<65 && jus.getIdeology()>35){
                    sccomp+="\u001B[38;5;226m";
                    center++;
                }else{
                    if(jus.getIdeology()<=35){
                        sccomp+="\u001B[38;5;18m";
                        right++;
                    }else{
                        sccomp+="\u001B[38;5;88m";
                        left++;
                    }
                }
                sccomp+= "o"+RESET;
            }
            sccomp+="]";
            
            //System.out.println("DEBUG SC Real Size"+ supremeCourt.size());
            if(center> SCsize/2){
                System.out.print("The Supreme Court leans towards \u001B[38;5;226mdemocracy"+RESET);
            }else if(left > SCsize/2){
                System.out.print("The Supreme Court leans \u001B[38;5;88mleft"+RESET);
            }else if(right > SCsize/2){
                System.out.print("The Supreme Court leans \u001B[38;5;18mright"+RESET);
            }else{
                System.out.print("The Supreme Court is divided");
            }
            System.out.println(" "+sccomp);
            int reformism=0; // 0 most radical, 50 most conservative; 50*9 = 460 max conservatism
            for(SCJustice jus: supremeCourt){
                reformism+=jus.getCons();
            }
            
            if(reformism<150){
                System.out.print("The Supreme Court leans towards reformism");
            }else if(reformism>=150 && reformism< 300){
                System.out.print("The Supreme Court is neutral on reforms");
            }else{
                System.out.print("The Supreme Court leans towards conservatism");
            }
            System.out.println("("+ reformism+")");
        }
    
    public static List<Policy> allPolicies = new ArrayList<>();
    
    public static void addPolicies(){
        /*allPolicies.add(new Policy("Labor Laws"));
        allPolicies.add(new Policy("Agricultural Laws"));
        allPolicies.add(new Policy("Education"));
        allPolicies.add(new Policy("State Pensions"));
        allPolicies.add(new Policy("Healthcare"));
        allPolicies.add(new Policy("Criminal Justice"));
        allPolicies.add(new Policy("Immigration"));
        allPolicies.add(new Policy("Land Reform"));
        allPolicies.add(new Policy("Taxes"));
        allPolicies.add(new Policy("Foreign Trade"));
        allPolicies.add(new Policy("Minority Rights"));
        allPolicies.add(new Policy("Environmental Laws"));*/
        allPolicies.add(new Policy("Labor"));
        allPolicies.add(new Policy("Fiscal Laws"));
        allPolicies.add(new Policy("State Services"));
        allPolicies.add(new Policy("Criminal Justice"));
        allPolicies.add(new Policy("Foreign Policy"));
        allPolicies.add(new Policy("Industrial Laws"));
        allPolicies.add(new Policy("Land Reform"));
        allPolicies.add(new Policy("Rural Laws"));
        
    }
    
    public static void displayPolicies(){
        int counter = 0;
        int perrow=2;
        for(Policy pol: allPolicies){
            counter++;
            System.out.print(pol);
            if(perrow== counter){
                counter=0;
                System.out.println();
            }else{
                System.out.print(" | ");
                if(pol == allPolicies.get(allPolicies.size()-1)){
                    System.out.println();
                }
            }
        }
    }
    
    public static void shouldChangePolicy(){
        int refDesire = 0;
        int addByRef = 100/allPolicies.size();
        for(Policy pol : allPolicies){
            if(rulingCoalition.getLeader().proximityWith(pol.getPosition()) < 90){
                refDesire+=addByRef;
                
            }
        }
        if(!lean.equalsIgnoreCase("Republic") && (refDesire/addByRef)>3){
                    refDesire*=5;
                }
        
        if(ra.nextInt(50)<refDesire){
            choosePolicy();
        }
    }
    
    public static void choosePolicy(){
        int maxPrio=Integer.MIN_VALUE;
        Policy maxPol = null;
        
        for(Policy pol: allPolicies){
            int points=0;
            points += 100-rulingCoalition.getLeader().proximityWith(pol.getPosition());
            points += ra.nextInt(50);
            
            
            if(points > maxPrio){
                maxPrio = points;
                maxPol= pol;
            }
        }
        
        int confidence =(rulingCoalition.getSize()/5);
        if(President!= rulingCoalition.getLeader()){
            confidence/=2;
        }
        int moveBy = 3*confidence;
        if(rulingCoalition.getLeader().proximityWith(maxPol.getPosition())>90){
            moveBy=0;
        }
        int goal =0;
        if(rulingCoalition.getLeader().getIdeology()> maxPol.getPosition()){
            if(rulingCoalition.getLeader().getIdeology()< 50){
                moveBy=0;
            }
            goal = maxPol.getPosition()+moveBy;
            
        }else{
            if(rulingCoalition.getLeader().getIdeology()> 50){
                moveBy=0;
            }
            goal = maxPol.getPosition()-moveBy;
            
        }
        
        
        if(goal > 100){
            goal = 100;
        }
        if(goal < 0){
            goal = 0;
        }
        
        if(moveBy==0){
            return;
        }
        System.out.println("==============================");
        System.out.println("Landmark Policy: "+ maxPol.getName()+ " | "+ maxPol.getPosition()+" >> "+ goal);
        int yesVotes =0;
        int prespartyyesvotes=0;
        for(Party par: allParties){
            if(par.getPercent()>0){
                int divider = 100;
                if(rulingCoalition.getMemberList().contains(par)){
                   
                }else{
                    divider+=50;
                }
                
                if(par == LOTO){
                    divider+=50;
                }
                
                
                divider -= par.relationWith(rulingCoalition.getLeader())/2;
                
                int genDistance = (100-par.proximityWith(rulingCoalition.getLeader()))/10;
                divider*=genDistance;
                
                if(par.proximityWith(rulingCoalition.getLeader()) <50){
                    divider*=10;
                }
                
                int votesToAdd = (par.getPercent()*par.proximityWith(goal))/(divider+1);
                if(votesToAdd > par.getPercent()){
                    votesToAdd = par.getPercent();
                }
                System.out.println(par.getColor()+ par.getName() + RESET+ par.ideoDisplay() + " - "+votesToAdd+" / "+ par.getPercent());
                if(par == President){
                    prespartyyesvotes = votesToAdd;
                }
                yesVotes += votesToAdd;
            }
        }
        System.out.println("Total: "+ yesVotes+" / 100 (51 needed to pass)");
        if(yesVotes >50){
            boolean passedLegandEx = false;
            System.out.println("Succesful Vote!");
            
            int tersh = 100;
            
            if(President != rulingCoalition.getLeader()){
                tersh -= (50-President.relationWith(rulingCoalition.getLeader()))/2;
            }
            
            tersh -= ((prespartyyesvotes*100)/ (President.getPercent()+1))/2;
            
            
            int goalDistFromPres = Math.abs(President.getIdeology()-goal);
                int initposDistFromPres = Math.abs(President.getIdeology()-maxPol.getPosition());
                
            
            int presdif = 100- (Math.abs(President.getIdeology()-goal)); 
            presdif += (initposDistFromPres-goalDistFromPres)*2;
            tersh -= (50-Math.abs(President.getIdeology()-50))/2;
            //System.out.println("presdif: " +presdif);
            //System.out.println("tersh: " +tersh);
            if(presdif < tersh){
                if(yesVotes<75){
                    System.out.println("The President has vetoed the bill");
                    President.setApproval(President.getPopularity()- (President.getPopularity()/5));
                    for(int i=0; i<5;i++){
                        President.addFatigue();
                    }
                }else{
                    System.out.println("The President has vetoed the bill, but the veto is overruled by a supermajority");
                        
                        for(int i=0; i<5;i++){
                            President.addFatigue();
                        }
                        passedLegandEx = true;
                    
                }
                
            }else{
                System.out.println("The President has approved the bill");
                
                passedLegandEx = true;
            }
            int chalfactor = ra.nextInt(50);
            for(SCJustice jus : supremeCourt){
                
                int goalDistFromJus = Math.abs(jus.getIdeology()-goal);
                int initposDistFromJus = Math.abs(jus.getIdeology()-maxPol.getPosition());
                
                chalfactor-= initposDistFromJus-goalDistFromJus;
                
                
                //chalfactor+= ((jus.getCons()/10)*Math.abs(goal-maxPol.getPosition()))/2;
            }
            if(lean.equalsIgnoreCase("Republic")){
                    if(passedLegandEx){
                        if(chalfactor> 0){
                            //System.out.println("DEBUG chalfactor: "+ chalfactor);
                            
                            System.out.println("A legal challenge against the bill has been presented");
                            
                            String yeavotes ="[", novotes="[";
                            
                            int SCapprove = 0;
                            
                            for(SCJustice jus : supremeCourt){
                                int tresh = 0 + ra.nextInt((jus.getCons()/2)+1);
                                
                                int goalDistFromJus = Math.abs(jus.getIdeology()-goal);
                int initposDistFromJus = Math.abs(jus.getIdeology()-maxPol.getPosition());
                                int poi = initposDistFromJus-goalDistFromJus;
                                
                                if(poi > tresh){
                                    SCapprove++;
                                    
                                    
                                    if(jus.getIdeology()<65 && jus.getIdeology()>35){
                                        yeavotes+="\u001B[38;5;226m";
                                    }else{
                                        if(jus.getIdeology()<=35){
                                            yeavotes+="\u001B[38;5;18m";
                                        }else{
                                            yeavotes+="\u001B[38;5;88m";
                                        }
                                    }
                                    yeavotes+="o"+RESET;
                                    
                                }else{
                                    if(jus.getIdeology()<65 && jus.getIdeology()>35){
                                        novotes+="\u001B[38;5;226m";
                                    }else{
                                        if(jus.getIdeology()<=35){
                                            novotes+="\u001B[38;5;18m";
                                        }else{
                                            novotes+="\u001B[38;5;88m";
                                        }
                                        
                                    }
                                    novotes+="o"+RESET;
                                }
                            }
                            yeavotes+="]";
                            novotes+="]";
                            System.out.println("Supreme Court votes ");
                            System.out.println("Yea - "+SCapprove+ " "+ yeavotes);
                            System.out.println("Nay - "+(SCsize-SCapprove) + " "+ novotes);
                            if(SCapprove> SCsize/2){
                                System.out.println("The Supreme Court has ruled the policy change constitutional in a "+ SCapprove+ " - "+(SCsize-SCapprove) + " ruling, allowing it to move forward");
                                maxPol.updatePos(moveBy * ((rulingCoalition.getLeader().getIdeology()> maxPol.getPosition())? 1:-1));
                            
                                for(Party par: rulingCoalition.getMemberList()){
                                    par.setApproval(par.getPopularity()+ (par.getPopularity()/10));
                                }
                            }else{
                                System.out.println("The Supreme Court has ruled the policy change unconstitutional in a "+ (SCsize-SCapprove)+ " - "+SCapprove + " ruling, thus blocking its passing");
                                
                            }
                        }else{
                            maxPol.updatePos(moveBy * ((rulingCoalition.getLeader().getIdeology()> maxPol.getPosition())? 1:-1));
                            
                                for(Party par: rulingCoalition.getMemberList()){
                                    par.setApproval(par.getPopularity()+ (par.getPopularity()/10));
                                }
                        }
                        
                }
            }else{
                maxPol.updatePos(moveBy * ((rulingCoalition.getLeader().getIdeology()> maxPol.getPosition())? 1:-1));
                
                    for(Party par: rulingCoalition.getMemberList()){
                        par.setApproval(par.getPopularity()+ (par.getPopularity()/10));
                    }
            }
        }else{
            System.out.println("Vote Failed!");
            for(Party par: rulingCoalition.getMemberList()){
                par.setApproval(par.getPopularity()- (par.getPopularity()/10));
                for(int i=0; i<3;i++){
                    par.addFatigue();
                }
            }
        }
        
        
        
        
    }
    
    public static List<Person> allPersons = new ArrayList<>();
    public static List<Person> activePersons = new ArrayList<>();
    
    public static void addPersons(){
        // format: allPersons.add(new Person("",startyear, endyear, ideology));
        allPersons.add(new Person("James Caldwell",1846,1884,6,91,54,89,13,70));
allPersons.add(new Person("Arthur S. Gray",1846,1898,4,99,98,3,46,45));
allPersons.add(new Person("Wayne N. Blair Jr.",1846,1864,1,7,49,81,76,70));
allPersons.add(new Person("Ross Clark",1847,1900,1,1,97,12,82,54));
allPersons.add(new Person("Joel Hawkins",1846,1906,3,5,37,19,66,6));
allPersons.add(new Person("Norman Hubbard",1847,1881,11,89,48,78,64,53));
allPersons.add(new Person("T.M. Douglas IV",1847,1900,10,15,43,6,7,85));
allPersons.add(new Person("Howard Coleman",1850,1906,11,70,23,57,76,65));
allPersons.add(new Person("Nathan Cook III",1848,1861,16,24,45,44,72,89));
allPersons.add(new Person("Kevin Bennett III",1848,1886,17,88,91,65,70,63));
allPersons.add(new Person("Luke Harper",1849,1906,21,83,48,49,44,67));
allPersons.add(new Person("Maxwell Hudson",1846,1885,26,50,71,61,85,14));
allPersons.add(new Person("Tyler Henry",1846,1876,23,99,46,19,20,20));
allPersons.add(new Person("Lucas Brewer",1846,1892,23,86,48,19,10,94));
allPersons.add(new Person("Eugene Dutton",1850,1882,20,67,79,78,10,14));
allPersons.add(new Person("Jordan Dixon III",1849,1869,35,44,1,66,68,25));
allPersons.add(new Person("Lucas Fuller",1848,1898,37,92,39,55,9,50));
allPersons.add(new Person("Brandon I. Hoffman IV",1847,1899,30,73,86,63,8,19));
allPersons.add(new Person("Stephen Fitzgerald",1848,1875,37,23,90,86,37,83));
allPersons.add(new Person("Terrence Holloway",1850,1865,37,30,38,24,57,77));
allPersons.add(new Person("Oscar Brown",1850,1879,46,61,34,98,43,88));
allPersons.add(new Person("T.S. Douglas",1846,1886,44,10,39,33,90,29));
allPersons.add(new Person("Austin Clements",1850,1861,43,76,72,19,23,91));
allPersons.add(new Person("Adrian Z. Ellis IV",1850,1907,47,25,54,70,72,9));
allPersons.add(new Person("Frank Glass I",1847,1887,47,20,5,84,11,44));
allPersons.add(new Person("X.V. Gilbert",1847,1890,55,56,57,92,17,55));
allPersons.add(new Person("Seth Lloyd",1850,1871,54,31,73,43,10,77));
allPersons.add(new Person("Leonard Fitzgerald",1850,1864,51,41,15,42,4,75));
allPersons.add(new Person("P.E. Griffin",1850,1896,51,2,73,50,45,31));
allPersons.add(new Person("L.X. Gray",1846,1869,54,47,78,85,59,34));
allPersons.add(new Person("Dean Curtis",1847,1891,63,24,26,53,66,7));
allPersons.add(new Person("Matthew Hayes",1850,1898,61,16,79,56,25,39));
allPersons.add(new Person("Edward X. Cobb",1848,1878,67,63,30,83,36,76));
allPersons.add(new Person("Aaron J. Higgins",1848,1892,67,57,44,27,27,3));
allPersons.add(new Person("Gregory Garrett",1850,1871,63,84,70,95,38,38));
allPersons.add(new Person("William Z. Harris",1850,1870,74,34,50,38,29,12));
allPersons.add(new Person("Howard Bradley",1849,1884,75,50,12,82,95,96));
allPersons.add(new Person("Q.Q. Jackson III",1848,1874,77,15,58,37,27,73));
allPersons.add(new Person("U.A. Clark",1850,1887,73,57,5,21,4,25));
allPersons.add(new Person("H.B. Ball",1849,1874,75,98,0,28,87,82));
allPersons.add(new Person("Cameron Z. Griffin",1850,1879,82,52,9,40,67,81));
allPersons.add(new Person("Maxwell N. Harrison",1849,1868,81,8,57,59,60,35));
allPersons.add(new Person("Caleb Arnold",1848,1879,86,95,12,40,88,51));
allPersons.add(new Person("Joshua Cole",1850,1869,84,46,1,74,88,2));
allPersons.add(new Person("Raymond Glass",1848,1898,85,16,35,4,9,66));
allPersons.add(new Person("Shawn O. Jensen Jr.",1848,1875,92,16,94,95,81,2));
allPersons.add(new Person("Seth Caldwell",1849,1877,94,6,83,90,76,97));
allPersons.add(new Person("Maxwell Hughes",1848,1862,93,1,43,64,88,14));
allPersons.add(new Person("Colin Holland",1846,1886,91,74,97,54,15,16));
allPersons.add(new Person("Lewis F. Clayton",1850,1908,92,78,80,36,0,58));
allPersons.add(new Person("Lewis Z. Gross",1860,1905,1,38,64,65,31,8));
allPersons.add(new Person("Carl C. Fields III",1856,1870,0,21,20,90,26,19));
allPersons.add(new Person("Chad W. Doyle Sr.",1857,1888,5,5,21,98,54,30));
allPersons.add(new Person("Samuel Douglas III",1858,1902,3,86,48,77,27,92));
allPersons.add(new Person("I.H. Chandler",1860,1887,3,59,92,78,45,88));
allPersons.add(new Person("Russell Holmes",1859,1917,17,29,35,72,45,19));
allPersons.add(new Person("Joel Cross",1858,1897,14,23,58,77,98,17));
allPersons.add(new Person("Ryan Carroll",1856,1885,15,6,95,72,83,28));
allPersons.add(new Person("Brandon Harris",1856,1890,15,34,59,68,96,11));
allPersons.add(new Person("Lucas Jordan",1859,1901,17,92,55,52,7,83));
allPersons.add(new Person("Warren N. Beck",1857,1898,20,81,10,44,9,29));
allPersons.add(new Person("Lucas Clark",1857,1898,26,76,77,8,82,60));
allPersons.add(new Person("Raymond Q. Griffin I",1857,1908,24,17,75,21,58,55));
allPersons.add(new Person("Harvey Lucas",1858,1886,21,29,38,86,28,60));
allPersons.add(new Person("Vincent F. Hayes",1860,1892,25,96,44,5,98,47));
allPersons.add(new Person("Nathan Hale",1857,1875,31,78,80,70,30,96));
allPersons.add(new Person("Adam Green",1860,1902,33,73,1,34,58,57));
allPersons.add(new Person("Blake Haynes",1860,1893,36,90,6,66,82,94));
allPersons.add(new Person("Caleb Blair Jr.",1859,1898,33,2,61,73,94,63));
allPersons.add(new Person("Harvey Howell III",1856,1880,34,18,1,98,93,68));
allPersons.add(new Person("Jerome V. Hodges",1856,1904,42,98,71,51,24,30));
allPersons.add(new Person("Danny Jordan",1859,1901,42,73,73,93,21,77));
allPersons.add(new Person("Patrick Dean",1858,1898,46,21,5,92,54,77));
allPersons.add(new Person("Timothy Bishop",1858,1902,46,14,68,65,85,70));
allPersons.add(new Person("Joshua Hart IV",1859,1899,43,15,43,45,73,16));
allPersons.add(new Person("Darrell Burke",1860,1887,56,65,88,6,25,83));
allPersons.add(new Person("Eric Burns",1860,1880,50,30,9,70,84,7));
allPersons.add(new Person("Richard Cox",1860,1877,53,60,3,9,96,56));
allPersons.add(new Person("Danny Evans III",1859,1891,51,84,45,72,34,53));
allPersons.add(new Person("Edwin Gardner",1860,1915,53,90,78,50,16,87));
allPersons.add(new Person("D.B. Johnson",1860,1890,62,66,30,53,85,39));
allPersons.add(new Person("Adrian Y. Macdonald",1860,1908,62,37,26,5,42,57));
allPersons.add(new Person("Donald Lawson",1858,1905,61,3,72,61,78,46));
allPersons.add(new Person("I.E. Fraser",1856,1909,66,15,92,11,11,17));
allPersons.add(new Person("Carl Hodges",1857,1890,60,55,21,95,49,44));
allPersons.add(new Person("Douglas Cross",1856,1919,71,33,60,12,74,2));
allPersons.add(new Person("D.Y. Hamilton",1859,1884,70,16,50,70,76,81));
allPersons.add(new Person("Timothy Bryant III",1860,1879,72,29,70,49,9,58));
allPersons.add(new Person("V.A. Hopkins",1858,1912,77,9,39,71,47,93));
allPersons.add(new Person("Norman Craig",1860,1877,75,49,44,54,32,31));
allPersons.add(new Person("Terrence Gates",1858,1900,87,56,51,52,36,44));
allPersons.add(new Person("Danny Hodges",1856,1898,84,81,43,14,12,89));
allPersons.add(new Person("Lucas Crawford III",1856,1875,86,93,77,27,25,77));
allPersons.add(new Person("Clark Jordan",1860,1903,85,42,73,23,27,31));
allPersons.add(new Person("Gary Kent I",1860,1918,84,78,75,34,32,14));
allPersons.add(new Person("Travis Gregory",1857,1872,97,41,49,51,31,4));
allPersons.add(new Person("Matthew Barnes",1856,1882,97,38,26,98,34,61));
allPersons.add(new Person("Owen Ingram",1857,1907,94,82,46,43,14,52));
allPersons.add(new Person("Seth Barrett",1856,1892,93,36,63,74,55,78));
allPersons.add(new Person("Daniel Chambers",1857,1919,92,28,12,29,13,69));
allPersons.add(new Person("Joseph James",1870,1920,4,56,34,31,31,35));
allPersons.add(new Person("Stephen Hunt",1866,1885,3,30,94,24,30,84));
allPersons.add(new Person("Vincent Johnson I",1869,1887,4,32,72,97,39,15));
allPersons.add(new Person("Warren Lowe",1867,1921,3,90,81,45,5,53));
allPersons.add(new Person("Maxwell Harris",1866,1896,7,90,21,96,22,41));
allPersons.add(new Person("Harold Baker",1870,1915,12,68,6,79,93,23));
allPersons.add(new Person("Jeffrey Day I",1868,1899,13,93,33,62,89,82));
allPersons.add(new Person("Owen O. Greene",1866,1883,15,63,67,14,23,26));
allPersons.add(new Person("Adam Long",1868,1890,17,45,1,78,5,35));
allPersons.add(new Person("D.I. Gross",1868,1921,15,99,53,0,6,45));
allPersons.add(new Person("Keith Horton Sr.",1870,1912,26,61,98,4,92,92));
allPersons.add(new Person("V.N. Hughes",1870,1896,20,45,39,22,88,81));
allPersons.add(new Person("Shawn Bowman III",1866,1900,25,57,27,34,36,0));
allPersons.add(new Person("X.C. Byrd",1867,1898,21,32,61,8,77,84));
allPersons.add(new Person("Russell Hudson",1868,1921,22,63,4,53,13,65));
allPersons.add(new Person("Maurice Y. Holland",1866,1893,33,1,48,2,5,39));
allPersons.add(new Person("Leonard Harrison",1869,1910,35,2,61,75,42,58));
allPersons.add(new Person("Lewis Bowen",1867,1929,30,4,73,99,95,59));
allPersons.add(new Person("L.J. Brown",1869,1922,32,44,30,3,15,90));
allPersons.add(new Person("M.O. Black",1868,1898,37,81,57,55,71,49));
allPersons.add(new Person("M.I. Holloway",1868,1929,47,16,77,98,86,15));
allPersons.add(new Person("Albert Lynch",1866,1920,41,35,88,83,93,71));
allPersons.add(new Person("Henry L. Glass II",1867,1890,46,93,90,86,90,68));
allPersons.add(new Person("S.Q. Lyons Sr.",1868,1892,45,66,68,72,55,42));
allPersons.add(new Person("Henry N. Glass",1866,1920,41,60,34,96,17,50));
allPersons.add(new Person("Roy Mackenzie",1868,1883,53,35,12,37,57,14));
allPersons.add(new Person("Curtis Fraser III",1869,1881,53,42,25,94,78,93));
allPersons.add(new Person("Frederick Little Sr.",1870,1925,50,27,41,73,92,55));
allPersons.add(new Person("Dustin Dean I",1870,1894,53,36,82,82,54,71));
allPersons.add(new Person("Gordon Henry",1869,1915,55,54,98,13,0,94));
allPersons.add(new Person("Russell U. Hunter",1869,1915,61,45,51,58,14,92));
allPersons.add(new Person("William Lawrence",1869,1886,62,98,61,35,10,81));
allPersons.add(new Person("David Barnes",1868,1929,60,33,22,11,45,88));
allPersons.add(new Person("Curtis Armstrong II",1868,1911,60,27,88,16,94,84));
allPersons.add(new Person("Derek Boyd",1866,1924,67,21,48,12,93,77));
allPersons.add(new Person("Chad Little",1867,1912,77,0,59,97,98,95));
allPersons.add(new Person("Earl Curtis",1869,1885,70,45,98,46,58,32));
allPersons.add(new Person("Edwin Jackson",1866,1880,75,28,17,65,67,47));
allPersons.add(new Person("Darrell Chandler",1868,1890,77,46,56,52,70,63));
allPersons.add(new Person("X.A. Ferguson",1869,1894,74,12,59,70,31,90));
allPersons.add(new Person("Terrence Doyle",1867,1900,82,42,27,36,33,26));
allPersons.add(new Person("W.A. Edwards",1869,1922,80,55,52,89,23,46));
allPersons.add(new Person("Brian Gordon",1866,1887,80,50,33,82,76,53));
allPersons.add(new Person("Joel Lawson",1870,1926,87,80,70,68,12,62));
allPersons.add(new Person("Matthew Fields",1866,1897,80,3,25,27,9,56));
allPersons.add(new Person("Bryan Logan Sr.",1869,1909,93,22,49,52,98,12));
allPersons.add(new Person("Victor Harper",1867,1899,97,0,26,13,42,82));
allPersons.add(new Person("Caleb Cole",1867,1923,95,79,42,80,60,97));
allPersons.add(new Person("Earl Gardner",1867,1919,96,25,29,74,63,46));
allPersons.add(new Person("Albert Franklin",1866,1904,91,91,8,38,15,57));
allPersons.add(new Person("I.X. Hines",1876,1933,5,48,83,27,4,18));
allPersons.add(new Person("Lawrence Howell",1880,1891,5,80,87,49,44,32));
allPersons.add(new Person("Dean V. Barnett",1876,1912,1,49,33,48,45,92));
allPersons.add(new Person("Jonathan Fields",1878,1920,7,25,51,29,54,53));
allPersons.add(new Person("Dennis Glover",1876,1902,4,17,56,96,1,57));
allPersons.add(new Person("Wesley Z. Grant III",1876,1907,12,45,20,39,3,36));
allPersons.add(new Person("T.Q. Blair",1877,1936,16,2,84,51,4,13));
allPersons.add(new Person("Adam A. Franklin",1880,1896,15,70,2,29,40,75));
allPersons.add(new Person("Corey Leonard",1877,1927,10,10,82,72,71,35));
allPersons.add(new Person("Harry Chandler III",1877,1929,16,62,3,36,76,41));
allPersons.add(new Person("George A. Hodges",1880,1912,20,69,58,17,50,24));
allPersons.add(new Person("Bruce Lyons Jr.",1878,1899,26,34,28,28,63,67));
allPersons.add(new Person("Steven Dawson I",1880,1911,21,74,72,26,72,27));
allPersons.add(new Person("Harvey T. Fitzgerald",1880,1929,27,81,25,13,51,15));
allPersons.add(new Person("Howard Kent",1879,1923,26,8,21,35,47,86));
allPersons.add(new Person("Jack Brown III",1880,1903,35,32,61,32,56,3));
allPersons.add(new Person("Donald Chapman",1878,1927,31,15,90,80,35,80));
allPersons.add(new Person("Jonathan Edwards",1879,1923,33,45,11,54,93,25));
allPersons.add(new Person("Harold Craig",1877,1912,33,66,4,66,51,51));
allPersons.add(new Person("Edward Day",1878,1918,32,84,28,8,19,2));
allPersons.add(new Person("C.W. Greene",1878,1912,41,87,82,68,4,29));
allPersons.add(new Person("Harold C. Collins",1879,1938,42,26,40,0,72,94));
allPersons.add(new Person("Zachary Bradley",1879,1895,42,17,80,59,88,38));
allPersons.add(new Person("Scott Brady I",1879,1926,43,42,68,59,47,3));
allPersons.add(new Person("Seth Gilbert IV",1880,1911,44,79,40,15,11,94));
allPersons.add(new Person("Michael K. Kelly Jr.",1878,1902,54,53,67,15,34,15));
allPersons.add(new Person("Henry Daniel",1879,1891,56,93,31,39,82,52));
allPersons.add(new Person("H.L. Carroll",1876,1892,55,15,81,61,96,72));
allPersons.add(new Person("Brian Harper",1880,1898,57,84,20,85,8,44));
allPersons.add(new Person("Adrian Day",1877,1933,51,68,0,70,52,6));
allPersons.add(new Person("Vincent P. Fuller Sr.",1878,1906,66,92,66,37,14,76));
allPersons.add(new Person("Harold D. Jackson",1879,1912,62,36,44,58,11,33));
allPersons.add(new Person("Dennis Kerr",1878,1898,63,28,99,83,77,87));
allPersons.add(new Person("Bradley Foster",1878,1899,64,28,60,98,83,19));
allPersons.add(new Person("Craig Allen",1878,1903,62,20,71,57,26,25));
allPersons.add(new Person("Justin T. Gordon",1879,1929,77,25,55,57,64,98));
allPersons.add(new Person("Wayne Brown",1880,1891,76,10,38,22,98,46));
allPersons.add(new Person("Harold Griffin",1880,1913,72,97,43,40,90,5));
allPersons.add(new Person("Stanley Alexander",1880,1893,76,72,92,7,8,43));
allPersons.add(new Person("Nicholas Johnston",1876,1891,74,63,53,40,83,81));
allPersons.add(new Person("Zachary Giles",1878,1931,80,79,76,83,14,56));
allPersons.add(new Person("James Carlson",1876,1892,81,12,65,11,11,43));
allPersons.add(new Person("Alan Freeman",1877,1918,85,9,89,14,50,61));
allPersons.add(new Person("Charles Lane",1876,1896,80,21,40,99,65,82));
allPersons.add(new Person("Ernest Jordan",1876,1897,86,68,25,30,42,17));
allPersons.add(new Person("Martin Hines",1880,1907,97,94,29,64,2,68));
allPersons.add(new Person("Brian X. Bailey",1877,1922,91,58,9,31,29,89));
allPersons.add(new Person("Clark U. Fowler",1878,1890,94,14,7,87,10,63));
allPersons.add(new Person("Gordon Fletcher",1876,1934,91,47,16,84,11,86));
allPersons.add(new Person("Joel Henderson",1878,1928,90,31,38,23,80,62));
allPersons.add(new Person("Terrence Coleman",1887,1945,5,47,8,40,1,98));
allPersons.add(new Person("Colin Day",1889,1916,1,62,40,8,20,27));
allPersons.add(new Person("Carl Hubbard",1889,1910,1,73,23,40,63,15));
allPersons.add(new Person("I.Y. Cox III",1889,1937,6,39,29,23,72,12));
allPersons.add(new Person("Hunter Atkinson",1886,1947,3,54,47,26,55,23));
allPersons.add(new Person("Hunter Q. Hubbard IV",1890,1913,12,98,54,85,78,14));
allPersons.add(new Person("Jesse Lynch",1887,1936,16,71,47,1,47,4));
allPersons.add(new Person("Frederick Gregory",1889,1928,13,17,64,70,2,87));
allPersons.add(new Person("Jonathan Butler",1886,1909,12,85,80,74,28,90));
allPersons.add(new Person("Louis Davis",1886,1935,13,42,58,52,65,43));
allPersons.add(new Person("Jerome Griffith",1888,1926,26,77,49,46,2,65));
allPersons.add(new Person("Michael Lyons",1886,1937,22,11,16,2,38,88));
allPersons.add(new Person("Mark Kelley",1888,1934,26,59,86,2,68,52));
allPersons.add(new Person("Edwin Z. Kaufman",1889,1941,20,35,68,4,2,7));
allPersons.add(new Person("Dennis E. Clark I",1886,1930,26,59,25,59,7,82));
allPersons.add(new Person("Mark Fox",1887,1901,33,8,82,30,94,79));
allPersons.add(new Person("Adam Johnson II",1889,1939,34,6,22,18,77,19));
allPersons.add(new Person("K.D. Berry",1887,1900,31,68,74,45,11,82));
allPersons.add(new Person("Jeremy Beck",1890,1945,34,37,85,29,96,64));
allPersons.add(new Person("Brandon U. Jensen III",1887,1912,34,25,41,41,66,80));
allPersons.add(new Person("F.M. Clements",1886,1912,45,23,82,11,81,55));
allPersons.add(new Person("V.U. Holloway",1888,1947,47,32,15,4,71,35));
allPersons.add(new Person("Walter Dawson",1887,1908,42,73,87,21,9,38));
allPersons.add(new Person("Frederick Kerr",1889,1905,44,24,47,58,67,17));
allPersons.add(new Person("Frederick Cox",1887,1921,42,38,72,46,57,66));
allPersons.add(new Person("Ralph G. James",1887,1924,54,72,78,61,96,67));
allPersons.add(new Person("J.L. Hale",1889,1935,52,84,59,83,90,38));
allPersons.add(new Person("Philip H. Hampton II",1890,1937,52,18,56,38,13,20));
allPersons.add(new Person("F.D. Berry",1889,1932,53,10,47,92,81,45));
allPersons.add(new Person("Gilbert B. Burgess",1890,1944,56,98,82,84,35,71));
allPersons.add(new Person("Michael U. Clark",1886,1926,67,57,90,41,87,10));
allPersons.add(new Person("Robert V. Allen",1886,1912,66,87,85,82,33,85));
allPersons.add(new Person("Owen Lynch IV",1886,1924,67,47,47,17,37,96));
allPersons.add(new Person("Benjamin Carr Sr.",1889,1941,63,54,37,53,13,62));
allPersons.add(new Person("Owen Gross",1888,1927,64,77,35,8,80,50));
allPersons.add(new Person("Maurice Holt",1890,1928,74,74,99,30,77,14));
allPersons.add(new Person("Eugene W. Greene Jr.",1889,1902,72,15,70,6,17,13));
allPersons.add(new Person("Jordan F. Craig",1890,1935,70,60,30,77,61,93));
allPersons.add(new Person("Thomas Blake Jr.",1886,1921,75,51,64,64,72,4));
allPersons.add(new Person("Bruce T. Coleman",1889,1931,74,22,37,30,86,13));
allPersons.add(new Person("Y.L. Henderson",1886,1924,80,77,78,75,43,2));
allPersons.add(new Person("Daniel Austin",1888,1943,86,63,6,57,54,39));
allPersons.add(new Person("George Harper",1890,1933,83,62,66,56,91,95));
allPersons.add(new Person("Gilbert Q. Kelley III",1887,1942,81,56,17,94,5,78));
allPersons.add(new Person("Noah Cole",1890,1920,83,62,11,25,5,37));
allPersons.add(new Person("Corey Caldwell Jr.",1888,1945,94,42,67,59,67,94));
allPersons.add(new Person("Leonard E. King",1889,1927,92,62,4,49,53,70));
allPersons.add(new Person("Jason Bradley",1887,1911,92,95,49,5,50,15));
allPersons.add(new Person("Jack L. Davidson",1890,1901,93,12,76,36,95,5));
allPersons.add(new Person("Keith King",1890,1947,97,23,12,49,72,49));
allPersons.add(new Person("U.S. Gregory Jr.",1898,1937,2,65,15,43,22,36));
allPersons.add(new Person("Arthur Lawson",1896,1940,4,63,60,72,43,70));
allPersons.add(new Person("Henry King",1900,1926,2,88,59,96,84,35));
allPersons.add(new Person("Wayne V. Jensen IV",1898,1914,4,72,83,74,30,76));
allPersons.add(new Person("Ernest Burgess",1899,1946,6,59,35,81,73,89));
allPersons.add(new Person("Robert Macdonald",1899,1945,16,2,8,86,92,44));
allPersons.add(new Person("Russell Higgins III",1898,1926,10,55,75,31,88,34));
allPersons.add(new Person("Zachary Alexander II",1898,1930,15,90,35,53,16,48));
allPersons.add(new Person("Ian Clements IV",1898,1954,10,41,11,62,58,40));
allPersons.add(new Person("L.G. Lambert",1896,1947,17,67,79,98,6,29));
allPersons.add(new Person("Colin Fletcher Jr.",1900,1955,21,72,32,54,19,58));
allPersons.add(new Person("Louis Haynes",1896,1939,27,47,98,7,79,33));
allPersons.add(new Person("James Glover",1899,1944,21,61,95,84,50,9));
allPersons.add(new Person("P.X. Lyons",1897,1926,27,5,80,58,99,5));
allPersons.add(new Person("I.P. Cole",1899,1914,24,35,4,81,78,37));
allPersons.add(new Person("Jacob Cox",1900,1928,35,84,37,52,82,18));
allPersons.add(new Person("Joshua O. Jenkins",1897,1912,35,34,28,56,49,8));
allPersons.add(new Person("Jerome Gardner I",1898,1920,32,0,80,86,67,94));
allPersons.add(new Person("X.N. Kent",1896,1942,32,55,1,27,43,62));
allPersons.add(new Person("Ian G. Holmes Sr.",1898,1950,37,71,48,13,69,11));
allPersons.add(new Person("Clayton L. Gates",1900,1918,42,20,83,0,4,93));
allPersons.add(new Person("Dustin Bennett Jr.",1898,1922,40,59,18,13,81,66));
allPersons.add(new Person("Lewis Gibson",1898,1950,43,0,58,18,9,84));
allPersons.add(new Person("Evan Barnes",1896,1951,40,47,25,77,76,4));
allPersons.add(new Person("Roger Barnes",1898,1952,40,34,52,55,85,4));
allPersons.add(new Person("Edwin U. Johnson",1896,1943,56,11,82,99,80,85));
allPersons.add(new Person("Samuel Lyons III",1899,1944,56,4,36,1,77,21));
allPersons.add(new Person("Dean Arnold",1900,1924,54,61,1,29,39,45));
allPersons.add(new Person("Lawrence J. Elliott Sr.",1896,1948,53,5,41,83,59,96));
allPersons.add(new Person("Curtis S. Duncan",1897,1957,50,18,96,54,6,46));
allPersons.add(new Person("K.V. Fox",1900,1952,62,61,23,51,66,2));
allPersons.add(new Person("Chad Brewer",1897,1944,62,94,7,22,1,50));
allPersons.add(new Person("Paul Fields",1897,1950,61,14,67,20,7,29));
allPersons.add(new Person("Stephen Alexander",1900,1923,62,16,58,3,63,33));
allPersons.add(new Person("Corey Joseph Jr.",1897,1913,62,35,49,17,48,50));
allPersons.add(new Person("Lewis C. Davis",1899,1922,75,20,10,44,24,48));
allPersons.add(new Person("Clarence Hill",1899,1950,70,19,16,31,43,82));
allPersons.add(new Person("B.K. Griffith",1899,1956,72,2,46,24,24,51));
allPersons.add(new Person("O.D. Douglas I",1900,1939,72,67,83,62,40,41));
allPersons.add(new Person("Justin Daniels",1898,1944,71,35,5,26,67,66));
allPersons.add(new Person("T.W. Grant Jr.",1897,1949,85,78,84,73,33,75));
allPersons.add(new Person("Henry E. Hicks IV",1897,1948,86,37,94,84,10,66));
allPersons.add(new Person("Evan Barnett",1899,1926,85,14,74,29,16,55));
allPersons.add(new Person("Warren Cunningham",1900,1921,84,94,92,16,24,93));
allPersons.add(new Person("Francis U. Butler",1899,1920,81,16,18,25,68,81));
allPersons.add(new Person("Warren Jennings",1896,1935,95,39,18,54,1,22));
allPersons.add(new Person("E.L. Holland",1898,1922,92,60,42,95,25,0));
allPersons.add(new Person("Russell Arnold",1896,1920,96,88,49,15,78,80));
allPersons.add(new Person("Bernard Ford",1898,1958,95,34,4,78,6,46));
allPersons.add(new Person("Howard Hicks",1899,1940,92,56,32,95,27,62));
allPersons.add(new Person("Patrick Brewer",1907,1937,5,12,11,78,71,78));
allPersons.add(new Person("Seth Chase Jr.",1910,1956,0,6,28,57,24,59));
allPersons.add(new Person("Jack Gray",1907,1932,3,54,82,64,73,93));
allPersons.add(new Person("Oscar King",1910,1925,7,64,4,16,63,9));
allPersons.add(new Person("Seth Chandler",1906,1932,7,42,49,36,30,35));
allPersons.add(new Person("B.Y. Burgess",1908,1939,13,77,83,54,41,9));
allPersons.add(new Person("Gerald U. Graham Jr.",1910,1933,12,21,37,11,16,20));
allPersons.add(new Person("Charles Lambert",1910,1936,16,32,15,89,62,54));
allPersons.add(new Person("Aaron Lee III",1908,1937,10,56,70,74,37,68));
allPersons.add(new Person("Nathan Lowe Sr.",1907,1951,13,70,84,95,30,31));
allPersons.add(new Person("Francis P. Adams IV",1907,1951,27,79,94,38,93,75));
allPersons.add(new Person("Ernest Foster",1909,1951,22,17,17,4,10,37));
allPersons.add(new Person("Ronald Jones",1910,1965,24,20,39,59,75,58));
allPersons.add(new Person("Noah Y. Hoffman",1908,1950,20,53,92,20,49,81));
allPersons.add(new Person("Walter D. Hamilton",1909,1960,21,99,78,86,43,9));
allPersons.add(new Person("E.G. Jackson",1906,1925,36,90,59,93,53,98));
allPersons.add(new Person("Samuel Fox",1910,1937,36,54,7,76,91,59));
allPersons.add(new Person("R.I. Kennedy",1909,1924,33,26,5,56,95,76));
allPersons.add(new Person("Charles Q. Collins",1907,1944,32,46,11,11,1,68));
allPersons.add(new Person("Kenneth Adams",1907,1937,30,17,50,81,19,17));
allPersons.add(new Person("Edward Jackson",1909,1957,43,65,75,10,20,9));
allPersons.add(new Person("Gerald E. Henry I",1906,1938,46,24,67,92,80,25));
allPersons.add(new Person("Gilbert Gregory",1906,1935,41,97,90,43,1,67));
allPersons.add(new Person("Leonard Lucas",1910,1954,44,8,75,74,86,17));
allPersons.add(new Person("Jesse Johnston",1908,1950,42,11,69,16,99,90));
allPersons.add(new Person("Luke T. Harris I",1906,1955,52,62,77,18,61,89));
allPersons.add(new Person("Q.N. Giles",1907,1947,51,12,3,24,87,92));
allPersons.add(new Person("Tyler Fowler I",1910,1929,54,28,85,42,77,82));
allPersons.add(new Person("Gary P. Jackson",1910,1929,54,75,39,2,99,19));
allPersons.add(new Person("Sean N. Bradley III",1907,1923,54,76,56,27,14,76));
allPersons.add(new Person("Shawn Kent",1906,1955,60,86,14,91,60,65));
allPersons.add(new Person("W.C. Copeland",1910,1934,61,28,54,22,24,64));
allPersons.add(new Person("Patrick Chambers",1907,1920,61,87,60,11,54,68));
allPersons.add(new Person("Robert Fletcher",1908,1943,64,87,19,41,51,22));
allPersons.add(new Person("Kyle Garrett",1908,1921,62,18,0,30,80,1));
allPersons.add(new Person("Dean F. Allen",1910,1945,72,97,33,14,35,74));
allPersons.add(new Person("Jonathan Lyons",1907,1938,77,6,40,47,98,11));
allPersons.add(new Person("Martin Gilbert Sr.",1908,1945,74,64,40,25,68,69));
allPersons.add(new Person("Alexander Holt",1907,1926,74,18,97,96,1,51));
allPersons.add(new Person("Blake Cross",1908,1945,75,97,30,87,95,33));
allPersons.add(new Person("Martin V. Franklin II",1909,1936,86,6,50,98,44,55));
allPersons.add(new Person("John Hoffman",1906,1927,80,35,46,88,70,70));
allPersons.add(new Person("Logan Jordan III",1908,1962,85,2,56,72,89,98));
allPersons.add(new Person("Nathan Q. Fowler II",1909,1957,84,6,49,15,77,17));
allPersons.add(new Person("Frederick Z. Kent",1909,1965,81,91,43,31,34,56));
allPersons.add(new Person("Darrell M. Gray",1907,1925,90,49,70,40,0,47));
allPersons.add(new Person("Mark Lane",1906,1943,97,22,89,99,15,75));
allPersons.add(new Person("Eric T. Gardner",1906,1942,94,96,12,3,85,18));
allPersons.add(new Person("Steven Lane",1909,1936,95,94,80,3,68,46));
allPersons.add(new Person("Z.P. Harrison",1909,1932,94,17,8,6,87,67));
allPersons.add(new Person("Stephen Evans",1917,1956,6,10,31,46,0,26));
allPersons.add(new Person("Dennis Kennedy",1917,1947,7,14,62,67,3,47));
allPersons.add(new Person("Shawn Gibson",1918,1972,3,64,37,78,21,67));
allPersons.add(new Person("Paul Hicks",1920,1954,7,22,95,60,92,22));
allPersons.add(new Person("T.W. Green",1917,1975,5,11,21,10,34,26));
allPersons.add(new Person("Roy Clayton",1917,1934,16,0,92,16,85,66));
allPersons.add(new Person("Seth James",1916,1933,14,79,92,74,3,68));
allPersons.add(new Person("Danny Bailey",1918,1956,15,32,87,91,71,80));
allPersons.add(new Person("Joseph Davidson",1920,1952,15,10,51,77,42,12));
allPersons.add(new Person("Blake Harrison",1920,1937,10,3,59,23,9,25));
allPersons.add(new Person("Lewis Franklin",1917,1975,26,39,64,16,41,47));
allPersons.add(new Person("Frederick D. Lane",1917,1957,22,48,42,54,5,18));
allPersons.add(new Person("Walter E. Lloyd",1920,1961,21,50,28,86,28,4));
allPersons.add(new Person("Joshua Garrett",1920,1976,26,55,2,8,31,8));
allPersons.add(new Person("Roy Horton",1917,1970,20,8,29,43,68,60));
allPersons.add(new Person("Lucas I. Anderson",1920,1948,34,6,65,50,62,14));
allPersons.add(new Person("Benjamin Baldwin",1919,1956,37,37,53,77,25,22));
allPersons.add(new Person("Lucas Hudson",1917,1938,33,59,46,16,48,44));
allPersons.add(new Person("Eric Burke",1919,1975,37,97,89,89,20,94));
allPersons.add(new Person("Kevin Davis",1918,1941,31,58,28,59,10,31));
allPersons.add(new Person("D.Q. Burgess",1920,1941,42,50,42,17,15,34));
allPersons.add(new Person("Stephen Lloyd",1918,1952,45,90,42,62,10,63));
allPersons.add(new Person("Russell Brady",1917,1964,41,60,76,8,13,13));
allPersons.add(new Person("Howard Brady",1916,1972,45,99,44,27,4,47));
allPersons.add(new Person("Kevin Bell",1920,1942,46,88,42,51,78,34));
allPersons.add(new Person("Jack Little",1919,1940,53,50,14,68,1,53));
allPersons.add(new Person("Peter Brown",1919,1946,55,81,42,72,12,11));
allPersons.add(new Person("Donald Hall",1917,1951,52,55,65,61,69,35));
allPersons.add(new Person("Gary Holloway",1917,1934,53,80,35,30,11,2));
allPersons.add(new Person("Eugene Black",1919,1970,51,93,97,30,33,0));
allPersons.add(new Person("Joseph M. Garrett",1919,1942,62,70,68,49,92,2));
allPersons.add(new Person("Evan Ford II",1919,1952,67,55,3,58,40,25));
allPersons.add(new Person("Andrew Adams",1920,1971,66,21,47,62,95,20));
allPersons.add(new Person("Wayne Barnes IV",1916,1939,60,74,48,13,71,82));
allPersons.add(new Person("P.U. Cox",1916,1949,60,92,88,49,97,25));
allPersons.add(new Person("E.S. Lloyd II",1919,1940,77,15,8,77,72,2));
allPersons.add(new Person("Paul Duncan",1916,1977,71,49,70,58,79,99));
allPersons.add(new Person("David Lynch",1916,1958,71,56,32,41,64,36));
allPersons.add(new Person("Oscar V. Garrett",1919,1978,75,76,27,96,54,31));
allPersons.add(new Person("Scott L. Hall",1919,1947,76,10,92,84,11,1));
allPersons.add(new Person("K.C. Andrews",1916,1969,80,0,18,12,71,63));
allPersons.add(new Person("Ralph Holloway Jr.",1920,1978,87,35,43,56,6,51));
allPersons.add(new Person("Patrick Alexander",1920,1956,86,71,71,34,55,11));
allPersons.add(new Person("Henry Barnett",1916,1971,84,62,29,33,10,17));
allPersons.add(new Person("Gregory X. Hudson",1916,1933,80,89,3,88,97,13));
allPersons.add(new Person("Martin Baldwin",1916,1968,95,48,78,80,46,60));
allPersons.add(new Person("X.H. Macdonald",1918,1940,95,92,81,88,52,52));
allPersons.add(new Person("D.M. Hammond",1916,1942,94,94,58,22,82,0));
allPersons.add(new Person("Samuel Z. Dunn",1917,1958,93,1,51,82,40,21));
allPersons.add(new Person("Corey Clayton I",1916,1945,92,43,72,37,95,78));
allPersons.add(new Person("Clarence Ball",1927,1959,6,15,30,46,95,99));
allPersons.add(new Person("Lawrence Baker IV",1926,1967,5,20,32,32,14,77));
allPersons.add(new Person("Peter Howell",1929,1948,1,28,73,77,13,91));
allPersons.add(new Person("Dale Hampton Sr.",1930,1941,1,21,1,29,14,64));
allPersons.add(new Person("Gerald Griffin",1930,1963,5,3,53,76,68,13));
allPersons.add(new Person("Vincent Beck",1930,1960,11,33,4,99,50,89));
allPersons.add(new Person("Sean A. Collins II",1927,1962,13,41,41,96,65,22));
allPersons.add(new Person("Joel Kelly",1927,1967,14,34,30,66,40,23));
allPersons.add(new Person("Bryan Lyons II",1927,1958,17,86,6,57,37,38));
allPersons.add(new Person("Ian Brown",1926,1948,12,17,82,59,11,67));
allPersons.add(new Person("Jacob Higgins",1927,1974,20,82,97,63,65,54));
allPersons.add(new Person("Frank Bennett",1927,1967,23,10,56,92,4,2));
allPersons.add(new Person("D.U. Davidson",1926,1952,27,22,78,72,46,9));
allPersons.add(new Person("Leonard Griffin",1928,1958,23,46,2,80,20,37));
allPersons.add(new Person("Seth E. Elliott",1928,1965,25,19,57,55,20,9));
allPersons.add(new Person("Wayne K. Kaufman",1929,1977,34,30,10,71,71,7));
allPersons.add(new Person("Dennis Kerr",1930,1965,37,11,78,87,12,59));
allPersons.add(new Person("Philip Horton III",1930,1948,36,60,95,94,80,39));
allPersons.add(new Person("F.C. Kennedy",1927,1963,32,45,99,86,39,4));
allPersons.add(new Person("Bradley Burgess III",1930,1983,37,52,16,78,52,8));
allPersons.add(new Person("X.W. Brown III",1926,1954,43,74,64,68,98,35));
allPersons.add(new Person("Herbert Fuller",1929,1981,45,79,43,42,13,41));
allPersons.add(new Person("Clifford P. Bell I",1926,1987,41,84,96,64,18,10));
allPersons.add(new Person("Thomas Gross",1926,1985,42,67,89,25,68,19));
allPersons.add(new Person("C.V. Mackenzie",1930,1941,42,63,96,1,85,76));
allPersons.add(new Person("Connor Beck Jr.",1926,1953,53,31,60,91,70,9));
allPersons.add(new Person("Jerome Kent",1927,1962,54,76,97,1,46,57));
allPersons.add(new Person("Ryan Bates Jr.",1926,1968,56,54,18,85,80,46));
allPersons.add(new Person("Gary Dean",1926,1957,57,85,2,89,6,58));
allPersons.add(new Person("Stephen Hampton",1929,1985,54,53,96,88,58,11));
allPersons.add(new Person("Ian F. Hill",1927,1969,64,81,85,66,8,56));
allPersons.add(new Person("W.C. Horton",1929,1963,64,94,74,61,8,10));
allPersons.add(new Person("Franklin E. Arnold",1930,1966,67,31,26,67,44,54));
allPersons.add(new Person("Ian H. Hamilton",1929,1940,61,52,24,47,50,97));
allPersons.add(new Person("Joel Kent",1928,1963,65,11,88,10,13,12));
allPersons.add(new Person("Louis Ford IV",1929,1978,76,64,9,53,69,2));
allPersons.add(new Person("Andrew I. Kelly",1926,1951,75,61,10,43,39,86));
allPersons.add(new Person("Nathan Hubbard IV",1928,1963,74,79,85,15,49,47));
allPersons.add(new Person("Bruce E. Jackson",1928,1983,77,10,19,55,38,44));
allPersons.add(new Person("Nathan Mackenzie I",1926,1943,76,44,30,27,97,26));
allPersons.add(new Person("X.D. Hall",1930,1962,81,18,6,98,8,4));
allPersons.add(new Person("U.S. Chambers II",1930,1981,86,74,85,79,72,44));
allPersons.add(new Person("Herbert Fleming",1926,1969,86,49,90,11,8,8));
allPersons.add(new Person("Travis Long",1930,1941,81,53,54,65,25,28));
allPersons.add(new Person("Seth Gilbert I",1927,1962,87,45,83,35,74,22));
allPersons.add(new Person("Lawrence Bishop",1929,1967,93,55,43,4,14,86));
allPersons.add(new Person("Bernard Foster",1926,1984,97,19,41,90,83,26));
allPersons.add(new Person("Connor Cobb",1930,1972,91,7,67,18,62,39));
allPersons.add(new Person("O.I. Dean Sr.",1926,1965,95,78,72,94,67,55));
allPersons.add(new Person("Shane Campbell",1930,1946,95,9,65,98,95,17));
allPersons.add(new Person("Eugene Cunningham",1936,1972,5,29,9,90,46,11));
allPersons.add(new Person("Lawrence Bell",1939,1968,5,50,61,94,10,1));
allPersons.add(new Person("Eugene A. Bush III",1940,1957,6,59,16,22,34,75));
allPersons.add(new Person("Norman W. Burns",1936,1975,4,94,57,69,26,50));
allPersons.add(new Person("Herbert T. Burgess",1936,1996,3,39,77,57,29,6));
allPersons.add(new Person("Jason X. Burke",1940,1984,10,50,44,47,43,85));
allPersons.add(new Person("Patrick K. Chandler",1936,1992,15,46,57,3,95,69));
allPersons.add(new Person("Victor Brewer",1937,1950,12,90,71,26,56,35));
allPersons.add(new Person("Ralph Fowler III",1938,1968,10,11,50,32,2,42));
allPersons.add(new Person("Ronald K. Holloway",1936,1976,15,0,71,43,25,36));
allPersons.add(new Person("A.B. Lambert IV",1937,1975,25,5,66,23,98,7));
allPersons.add(new Person("Robert Howell",1937,1974,27,53,31,73,92,39));
allPersons.add(new Person("Gary Blake",1937,1998,26,5,21,56,0,19));
allPersons.add(new Person("G.E. King",1938,1973,22,28,86,28,45,24));
allPersons.add(new Person("Timothy Harper",1939,1977,27,43,76,6,41,1));
allPersons.add(new Person("Russell Gardner",1940,1969,32,49,33,56,62,78));
allPersons.add(new Person("Zachary Giles I",1940,1998,31,30,25,56,31,82));
allPersons.add(new Person("Jeffrey I. Garrett",1937,1979,34,61,64,82,23,55));
allPersons.add(new Person("Jerome Horton I",1937,1996,37,64,20,65,88,75));
allPersons.add(new Person("Hunter Little",1937,1950,35,66,5,11,31,56));
allPersons.add(new Person("Zachary Armstrong",1936,1951,41,51,14,94,45,81));
allPersons.add(new Person("Derek Brown",1939,1958,44,38,86,4,68,52));
allPersons.add(new Person("Ronald Craig",1937,1960,45,31,16,10,70,83));
allPersons.add(new Person("Jacob Gibson",1937,1997,47,14,70,77,85,16));
allPersons.add(new Person("R.U. Jordan",1938,1976,42,28,19,75,47,37));
allPersons.add(new Person("Albert Dawson",1939,1972,57,10,48,41,6,67));
allPersons.add(new Person("Edwin James",1939,1975,53,48,52,73,46,79));
allPersons.add(new Person("G.T. Andrews Sr.",1939,1952,53,17,76,43,97,11));
allPersons.add(new Person("Nicholas O. Bowen",1938,1995,52,44,17,1,54,87));
allPersons.add(new Person("Douglas Love Sr.",1937,1957,54,83,73,54,40,32));
allPersons.add(new Person("Gilbert Hopkins",1939,1955,65,54,53,80,73,96));
allPersons.add(new Person("G.G. Brown",1936,1970,67,46,61,74,34,89));
allPersons.add(new Person("Ryan Lowe",1940,1989,60,50,17,73,21,83));
allPersons.add(new Person("Dennis Holt IV",1937,1955,63,2,15,25,67,72));
allPersons.add(new Person("Russell Campbell IV",1938,1979,65,20,82,56,72,69));
allPersons.add(new Person("Robert W. Chambers",1937,1992,73,60,44,51,65,32));
allPersons.add(new Person("Seth Holland",1939,1988,70,14,32,81,30,56));
allPersons.add(new Person("Ronald Bailey",1939,1952,74,1,66,54,79,13));
allPersons.add(new Person("Wayne Hines III",1938,1992,71,73,20,18,39,37));
allPersons.add(new Person("Gabriel Copeland I",1938,1971,74,3,57,60,2,84));
allPersons.add(new Person("Gerald Hodges",1939,1958,85,88,31,35,3,27));
allPersons.add(new Person("M.K. Davidson I",1936,1997,85,17,34,8,82,61));
allPersons.add(new Person("Nicholas L. Jordan IV",1938,1972,85,57,39,33,45,37));
allPersons.add(new Person("N.J. Abbott",1938,1999,82,3,69,38,82,3));
allPersons.add(new Person("Herbert C. Little",1936,1968,82,37,31,54,83,64));
allPersons.add(new Person("Philip A. Foster I",1938,1996,92,35,11,4,56,0));
allPersons.add(new Person("Martin Lawson Sr.",1937,1960,93,67,42,6,90,56));
allPersons.add(new Person("Aaron Horton",1940,1971,94,60,64,14,4,58));
allPersons.add(new Person("Austin Bishop",1939,1967,96,4,81,56,70,73));
allPersons.add(new Person("Maxwell Lowe Sr.",1940,1998,94,68,81,34,83,56));
allPersons.add(new Person("Samuel Curtis",1947,1971,7,36,48,16,57,22));
allPersons.add(new Person("M.R. Kent",1949,1964,5,41,30,95,29,22));
allPersons.add(new Person("Connie Greene",1950,1985,6,69,42,81,0,22));
allPersons.add(new Person("B.E. Logan Jr.",1946,1993,7,98,60,93,71,63));
allPersons.add(new Person("Luke E. Case",1949,2002,1,8,12,49,53,33));
allPersons.add(new Person("Arthur Fisher I",1946,1963,13,81,27,90,88,10));
allPersons.add(new Person("Eric Harvey",1950,1978,12,80,34,24,73,11));
allPersons.add(new Person("Clark Andrews",1948,2002,13,3,39,79,53,87));
allPersons.add(new Person("John X. Glover",1950,1993,10,84,31,11,89,79));
allPersons.add(new Person("R.J. Leonard",1947,1978,11,5,7,15,41,29));
allPersons.add(new Person("R.F. Kelley",1948,1978,27,46,38,84,27,66));
allPersons.add(new Person("Jeremy Clements",1949,1979,22,51,33,67,63,66));
allPersons.add(new Person("T.F. Jackson II",1950,1971,22,19,93,72,72,41));
allPersons.add(new Person("Adrian Johnson Jr.",1949,2003,20,13,44,62,2,30));
allPersons.add(new Person("Philip Doyle",1950,1990,23,61,98,70,6,39));
allPersons.add(new Person("Clifford J. Cole",1946,2000,30,44,49,74,9,72));
allPersons.add(new Person("Patrick M. Cole",1946,1967,32,48,99,36,53,64));
allPersons.add(new Person("Allison Hamilton",1948,1966,35,18,5,97,22,17));
allPersons.add(new Person("H.U. Harper I",1948,1967,35,42,28,12,85,62));
allPersons.add(new Person("Adam Blair",1950,1972,35,91,88,63,91,26));
allPersons.add(new Person("Maurice B. Hughes II",1948,1991,44,20,44,83,42,74));
allPersons.add(new Person("Kevin Beck",1949,1986,42,99,77,4,81,50));
allPersons.add(new Person("James Daniels",1946,1977,46,15,61,92,60,16));
allPersons.add(new Person("Ralph Hardy",1947,1993,45,2,57,98,84,72));
allPersons.add(new Person("David A. Baker",1947,2008,42,37,84,32,91,3));
allPersons.add(new Person("George Love",1946,1962,57,18,99,53,91,47));
allPersons.add(new Person("Danny Elliott III",1948,1964,54,11,82,55,76,15));
allPersons.add(new Person("Carla Fuller",1949,1989,52,82,61,8,38,14));
allPersons.add(new Person("Erica Adams-Kerr",1950,1968,52,58,77,83,79,68));
allPersons.add(new Person("James Brooks Sr.",1947,2006,56,55,81,5,51,94));
allPersons.add(new Person("Blake M. Barrett",1948,2007,67,36,88,22,88,35));
allPersons.add(new Person("Owen Harvey",1946,1963,64,96,33,16,72,74));
allPersons.add(new Person("V.L. Hill",1947,1968,61,41,15,12,29,52));
allPersons.add(new Person("Martin Brewer",1946,1987,63,77,70,57,45,37));
allPersons.add(new Person("Todd Hines",1948,1979,67,8,35,3,66,18));
allPersons.add(new Person("Owen L. Drake",1948,1965,73,54,27,10,98,23));
allPersons.add(new Person("Lucas Hunt",1946,2002,72,56,92,25,65,37));
allPersons.add(new Person("Aaron Burgess",1946,1971,74,79,62,0,16,32));
allPersons.add(new Person("Q.G. Carr",1950,1979,76,82,21,77,11,16));
allPersons.add(new Person("Todd U. Byrd",1948,1987,74,55,55,53,88,39));
allPersons.add(new Person("Eugene Baldwin",1949,1972,82,80,97,20,3,78));
allPersons.add(new Person("Earl Bradley",1946,2009,85,76,45,57,66,96));
allPersons.add(new Person("Blake Lee",1948,1976,87,1,70,38,62,95));
allPersons.add(new Person("C.E. Love",1948,2001,80,31,86,4,67,12));
allPersons.add(new Person("Albert Lowe",1946,1966,80,33,69,40,3,78));
allPersons.add(new Person("Amanda C. Carter-Jacobs",1948,1997,92,60,71,18,81,66));
allPersons.add(new Person("Timothy Daniels",1948,1966,96,9,78,22,41,24));
allPersons.add(new Person("Kenneth Dean",1949,1963,96,3,99,24,82,93));
allPersons.add(new Person("Lawrence Holt",1950,1968,95,17,44,32,17,1));
allPersons.add(new Person("Gabriel Jennings",1948,2003,93,47,51,49,16,77));
allPersons.add(new Person("Harry Harding",1960,2005,7,76,92,54,68,35));
allPersons.add(new Person("Corey Clark",1959,1977,7,24,57,43,8,69));
allPersons.add(new Person("Raymond Baker",1956,1993,3,60,86,28,45,62));
allPersons.add(new Person("Michelle Andrews-Cobb",1959,2006,0,19,83,39,47,6));
allPersons.add(new Person("Grace Hodges",1959,1983,7,59,23,65,44,70));
allPersons.add(new Person("Franklin F. Davis IV",1957,1970,15,26,84,66,5,44));
allPersons.add(new Person("Z.V. Lawrence",1958,1974,10,40,59,8,69,32));
allPersons.add(new Person("Marie Andrews",1960,1979,10,43,94,63,70,14));
allPersons.add(new Person("Travis Hale",1958,1980,17,43,85,64,40,25));
allPersons.add(new Person("Clark X. Carpenter",1960,1978,15,87,77,7,7,14));
allPersons.add(new Person("Janet E. Fuller",1960,1971,22,84,87,71,59,34));
allPersons.add(new Person("Eugene G. Anderson",1959,1980,21,37,91,75,62,5));
allPersons.add(new Person("Earl Bell",1956,1985,25,99,82,31,1,91));
allPersons.add(new Person("M.S. Austin",1960,2012,27,89,88,29,89,66));
allPersons.add(new Person("Leonard Joseph",1959,2016,26,28,80,48,65,77));
allPersons.add(new Person("Julia Hines",1959,1994,31,95,50,80,81,39));
allPersons.add(new Person("Shawn Butler",1956,2007,36,72,63,51,87,91));
allPersons.add(new Person("Eric Daniel",1956,1984,34,26,45,5,43,32));
allPersons.add(new Person("Alan H. Atkinson",1956,1980,37,50,54,95,67,87));
allPersons.add(new Person("Shawn M. Blair",1957,2012,30,3,41,10,84,69));
allPersons.add(new Person("F.N. Cox II",1957,1989,41,32,78,89,80,20));
allPersons.add(new Person("U.U. Armstrong I",1956,1996,42,16,21,67,26,95));
allPersons.add(new Person("Clark Bates",1957,2004,46,26,36,88,73,72));
allPersons.add(new Person("Gabriel Craig",1958,2003,44,7,7,36,87,25));
allPersons.add(new Person("Victor U. Craig",1957,1994,45,16,70,12,79,50));
allPersons.add(new Person("G.D. Hammond",1960,2007,56,69,50,97,59,79));
allPersons.add(new Person("Noah Q. Byrd Jr.",1957,1973,52,88,6,86,86,80));
allPersons.add(new Person("Marjorie Lucas",1958,2012,53,64,15,78,20,42));
allPersons.add(new Person("Justin Long",1960,1972,51,61,57,71,66,28));
allPersons.add(new Person("Anthony M. Fowler",1956,1992,50,68,37,8,34,35));
allPersons.add(new Person("Edward Kelly",1959,2002,66,63,63,85,82,10));
allPersons.add(new Person("Gabriel Green",1956,2002,66,94,57,65,21,57));
allPersons.add(new Person("Chad C. Hubbard",1958,1990,60,20,72,79,73,46));
allPersons.add(new Person("L.I. Harris",1956,2007,66,62,22,55,91,58));
allPersons.add(new Person("Janice Harding",1957,1989,65,41,75,86,83,81));
allPersons.add(new Person("C.A. Chambers",1957,1978,70,91,27,22,74,94));
allPersons.add(new Person("Paul Long Sr.",1959,2006,76,81,80,82,60,29));
allPersons.add(new Person("Dawn Carroll",1958,2014,77,0,90,87,6,76));
allPersons.add(new Person("E.Y. Hoffman I",1959,2000,74,28,2,43,76,85));
allPersons.add(new Person("Q.P. Craig",1956,2008,74,76,40,64,54,27));
allPersons.add(new Person("Anna Howard",1956,1974,87,16,9,47,84,57));
allPersons.add(new Person("Joel T. Bowman",1960,1985,82,43,78,33,38,37));
allPersons.add(new Person("Aaron Chandler III",1959,1977,86,95,98,17,8,75));
allPersons.add(new Person("Cameron Brewer",1960,2006,81,37,68,29,98,71));
allPersons.add(new Person("Michael V. Hunt",1957,2018,86,90,23,40,59,78));
allPersons.add(new Person("Y.O. Carter",1958,2014,93,29,20,43,40,35));
allPersons.add(new Person("Leonard Clayton",1957,1998,92,28,5,41,38,1));
allPersons.add(new Person("Emma Dixon",1957,1997,97,99,36,33,69,46));
allPersons.add(new Person("James Harris III",1958,1976,96,94,64,29,96,84));
allPersons.add(new Person("Andrew Dunn",1957,2000,91,64,20,96,32,47));
allPersons.add(new Person("Cheryl Fox-Hamilton",1966,2020,6,36,93,72,9,95));
allPersons.add(new Person("Lauren Bradley-Leonard",1970,1994,2,52,10,40,11,50));
allPersons.add(new Person("Raymond Daniel",1968,1997,4,28,90,47,23,66));
allPersons.add(new Person("Ernest Collins Sr.",1969,1988,7,35,52,17,96,72));
allPersons.add(new Person("Oscar Burke",1970,2027,2,71,11,27,46,96));
allPersons.add(new Person("Mitchell Beck",1967,1999,12,71,51,94,79,8));
allPersons.add(new Person("Melissa Hunter-Franklin",1967,2025,13,25,72,67,94,82));
allPersons.add(new Person("Jacob P. Long",1967,2028,16,54,96,47,94,53));
allPersons.add(new Person("Evelyn Lawrence",1969,1989,11,53,6,34,70,62));
allPersons.add(new Person("Oscar Hubbard",1966,1996,12,25,4,50,0,63));
allPersons.add(new Person("Timothy L. Gray",1970,2014,20,13,64,72,75,70));
allPersons.add(new Person("Scott Carr",1969,2009,20,77,60,61,73,88));
allPersons.add(new Person("Colin Garrett",1969,2011,23,61,46,24,62,90));
allPersons.add(new Person("Lois Andrews",1969,2023,20,19,27,17,70,49));
allPersons.add(new Person("Jordan Bowman",1966,1997,21,70,84,65,47,16));
allPersons.add(new Person("Christopher Brooks",1966,2028,33,33,85,98,58,73));
allPersons.add(new Person("Douglas Hart",1970,2023,32,1,91,5,4,30));
allPersons.add(new Person("Raymond Daniels Sr.",1970,2025,36,63,48,12,41,55));
allPersons.add(new Person("Walter Gordon",1969,2001,34,48,25,72,39,89));
allPersons.add(new Person("Kelly Adams",1968,2021,34,70,64,19,73,61));
allPersons.add(new Person("Philip Fletcher I",1969,1999,45,69,79,64,93,67));
allPersons.add(new Person("O.N. Ingram",1967,2018,47,62,87,93,71,77));
allPersons.add(new Person("Terrence Brown",1968,2029,44,54,12,21,24,62));
allPersons.add(new Person("Logan J. Johnson",1968,1989,47,79,35,77,58,12));
allPersons.add(new Person("Noah Barnett",1966,2017,45,3,44,29,28,30));
allPersons.add(new Person("Corey Gordon",1969,1986,54,17,96,0,89,71));
allPersons.add(new Person("Shane Clarke",1969,1994,55,22,25,6,36,64));
allPersons.add(new Person("Benjamin Hart",1968,2004,51,18,14,12,94,90));
allPersons.add(new Person("W.R. Allen",1969,1987,55,52,29,5,44,7));
allPersons.add(new Person("Corey Graham",1966,1993,54,99,9,48,94,48));
allPersons.add(new Person("Daniel Copeland",1967,2015,62,13,67,4,86,85));
allPersons.add(new Person("Nathan R. Harrison I",1968,1995,60,20,71,48,36,62));
allPersons.add(new Person("Carl Black",1966,2020,66,66,79,64,96,71));
allPersons.add(new Person("David Dutton",1966,2029,63,44,19,80,8,56));
allPersons.add(new Person("Carolyn Glass-Hines",1968,2022,62,87,24,22,92,16));
allPersons.add(new Person("Ethan Bailey",1970,2011,76,79,97,54,96,10));
allPersons.add(new Person("Anthony Giles",1967,2006,74,57,12,76,89,54));
allPersons.add(new Person("Edward Cross",1966,2014,75,50,20,92,73,32));
allPersons.add(new Person("Eugene Bryant",1969,2027,77,10,3,50,15,64));
allPersons.add(new Person("Iris C. Adams",1970,2025,70,8,16,30,75,81));
allPersons.add(new Person("Kelly Lloyd",1966,2029,82,85,13,59,83,83));
allPersons.add(new Person("Darrell N. Fowler",1970,2023,84,34,98,50,87,88));
allPersons.add(new Person("N.Y. Lewis",1970,2017,83,88,19,62,70,23));
allPersons.add(new Person("Cameron C. Day",1968,1997,85,36,95,85,38,56));
allPersons.add(new Person("Albert Burns",1968,1981,81,61,67,11,75,52));
allPersons.add(new Person("Raymond Clayton",1970,2010,95,19,33,59,52,85));
allPersons.add(new Person("Amanda V. Hughes",1970,2009,95,39,74,4,22,32));
allPersons.add(new Person("Harold I. Hall II",1968,2023,97,58,30,92,70,82));
allPersons.add(new Person("Shane Davidson",1968,1996,95,91,38,36,60,87));
allPersons.add(new Person("Mabel Joseph",1966,2003,96,37,14,35,31,84));
allPersons.add(new Person("D.Y. Knight",1980,2001,4,35,5,55,81,98));
allPersons.add(new Person("Dana Hodges",1976,2020,6,20,54,74,14,1));
allPersons.add(new Person("Brenda Higgins",1978,2038,7,86,55,12,62,87));
allPersons.add(new Person("Daniel Q. Ford IV",1979,2037,5,95,37,13,30,99));
allPersons.add(new Person("Marion Caldwell",1979,2032,4,58,49,61,51,54));
allPersons.add(new Person("Martin Hunt",1980,2005,11,91,89,86,60,62));
allPersons.add(new Person("Franklin Boyd",1979,2021,15,64,92,16,31,2));
allPersons.add(new Person("Daniel Fletcher",1980,2015,12,10,88,48,15,97));
allPersons.add(new Person("Grace D. Higgins",1980,2018,16,38,38,45,76,51));
allPersons.add(new Person("Stanley X. Howard",1979,2011,15,68,5,7,4,51));
allPersons.add(new Person("Kathy V. Barnes",1976,1998,22,81,62,65,68,41));
allPersons.add(new Person("Chad Davidson",1976,1996,24,24,5,58,36,9));
allPersons.add(new Person("Curtis Fisher II",1976,2010,24,11,57,32,27,76));
allPersons.add(new Person("Marion Andrews",1980,2030,22,14,37,59,65,89));
allPersons.add(new Person("Samuel Lawrence Sr.",1980,1999,23,21,42,35,69,80));
allPersons.add(new Person("Katherine Fleming",1977,2022,36,44,38,31,74,60));
allPersons.add(new Person("Dawn J. Higgins",1977,2015,35,78,30,44,23,65));
allPersons.add(new Person("Clifford Grant",1979,2035,34,61,29,64,44,44));
allPersons.add(new Person("Michael M. Copeland IV",1980,2009,35,24,78,89,62,81));
allPersons.add(new Person("Francis Brooks IV",1979,2003,37,48,57,54,28,22));
allPersons.add(new Person("Z.C. Hodges III",1979,2028,46,94,54,12,18,55));
allPersons.add(new Person("Richard Fleming",1977,2022,41,69,90,49,49,7));
allPersons.add(new Person("Lawrence Henderson",1976,2015,45,89,43,78,72,9));
allPersons.add(new Person("Joseph Z. Bennett III",1979,2017,42,55,97,8,26,22));
allPersons.add(new Person("Peter Blair",1980,1991,46,9,59,36,51,35));
allPersons.add(new Person("Colleen Crawford-Hammond",1978,2035,52,42,74,65,97,78));
allPersons.add(new Person("Thomas Y. Bennett",1979,1990,51,36,77,7,0,75));
allPersons.add(new Person("Edwin Hughes Sr.",1976,2031,56,43,32,82,57,26));
allPersons.add(new Person("Frederick Horton",1979,2008,56,86,56,70,12,24));
allPersons.add(new Person("Joshua Joseph I",1979,2027,50,13,65,39,71,63));
allPersons.add(new Person("Clifford Caldwell",1980,2026,67,34,55,80,61,32));
allPersons.add(new Person("Kenneth D. Holloway",1978,2035,63,23,49,33,45,82));
allPersons.add(new Person("Jeremy Z. Fisher",1976,2012,64,38,84,32,83,5));
allPersons.add(new Person("K.I. Drake",1979,2029,64,51,20,4,18,9));
allPersons.add(new Person("Q.N. Gates",1978,1996,63,7,64,5,2,3));
allPersons.add(new Person("Daniel Lambert",1980,1998,75,13,6,78,9,4));
allPersons.add(new Person("Glen B. Lee",1976,2035,73,41,29,84,86,4));
allPersons.add(new Person("Victor Coleman",1977,2018,73,90,26,69,63,31));
allPersons.add(new Person("Russell Hines",1976,1993,73,22,71,80,68,81));
allPersons.add(new Person("Danielle Z. Berry-Fisher",1977,2006,74,3,51,60,37,60));
allPersons.add(new Person("Seth Jacobs",1977,1990,86,37,95,72,42,0));
allPersons.add(new Person("Lucas Burton",1980,2028,87,33,66,8,67,20));
allPersons.add(new Person("Clark Bowen",1980,2037,81,27,26,21,87,57));
allPersons.add(new Person("Q.K. Cook",1978,2022,80,5,46,60,81,74));
allPersons.add(new Person("David Jackson",1979,2009,84,60,75,30,46,40));
allPersons.add(new Person("J.O. Dawson",1976,1990,95,6,10,76,62,8));
allPersons.add(new Person("George Craig",1976,2037,90,70,66,16,50,35));
allPersons.add(new Person("Vincent Henderson IV",1976,1998,90,40,68,34,59,16));
allPersons.add(new Person("Glen X. Hall",1979,2015,93,0,75,27,18,83));
allPersons.add(new Person("Earl Evans IV",1977,2013,92,5,82,70,15,63));
allPersons.add(new Person("Kathleen Ford",1987,2011,1,65,14,88,75,9));
allPersons.add(new Person("Victor Farmer",1988,2011,3,65,3,43,40,58));
allPersons.add(new Person("E.M. Dixon",1986,2041,0,73,63,4,77,39));
allPersons.add(new Person("Harvey Hammond",1988,2022,5,3,4,80,3,35));
allPersons.add(new Person("Kevin M. Cobb I",1988,2008,3,95,7,20,10,99));
allPersons.add(new Person("Martin Bradley",1986,2008,16,14,6,63,72,21));
allPersons.add(new Person("Alan M. Elliott",1989,2045,13,99,34,92,76,23));
allPersons.add(new Person("Owen Armstrong",1990,2040,11,10,73,26,55,65));
allPersons.add(new Person("Ralph Fletcher II",1987,2032,16,96,64,36,61,69));
allPersons.add(new Person("Franklin Barrett IV",1988,2023,17,30,38,60,25,87));
allPersons.add(new Person("Corey Fletcher",1989,2012,23,84,54,74,34,83));
allPersons.add(new Person("Benjamin K. Foster",1988,2033,25,68,47,15,5,11));
allPersons.add(new Person("Alice Lyons",1987,2047,26,7,36,3,57,5));
allPersons.add(new Person("Donald V. Atkinson IV",1986,2010,21,7,72,7,81,16));
allPersons.add(new Person("A.D. Carroll I",1989,2048,26,98,78,24,12,61));
allPersons.add(new Person("Michael Bishop",1988,2013,36,34,74,31,84,23));
allPersons.add(new Person("B.I. Cunningham",1989,2026,36,68,70,29,60,97));
allPersons.add(new Person("Matthew Brown IV",1990,2035,32,97,35,80,52,39));
allPersons.add(new Person("James Gibson",1988,2029,32,27,31,63,8,54));
allPersons.add(new Person("Walter Fields",1990,2044,35,42,4,36,64,66));
allPersons.add(new Person("Harry Haynes",1990,2029,42,31,38,39,88,19));
allPersons.add(new Person("Stanley Adams",1990,2038,41,3,9,0,36,81));
allPersons.add(new Person("Hazel Barnes",1988,2046,45,18,71,71,98,3));
allPersons.add(new Person("Lewis Bates",1987,2031,47,10,95,96,81,41));
allPersons.add(new Person("Grace Fox-Cole",1989,2012,41,19,79,94,28,62));
allPersons.add(new Person("Kathryn M. Henderson",1986,2003,50,82,78,77,32,97));
allPersons.add(new Person("Bonnie N. Bryant",1986,2005,54,91,30,98,74,39));
allPersons.add(new Person("Lisa Cross",1987,2047,53,34,65,49,85,62));
allPersons.add(new Person("Mary L. Chandler-Bates",1989,2008,55,31,42,56,70,74));
allPersons.add(new Person("Jonathan Burgess",1989,2033,56,75,35,43,14,88));
allPersons.add(new Person("Jacqueline Barrett",1990,2022,65,36,25,35,67,80));
allPersons.add(new Person("Bryan Elliott",1986,2027,62,21,83,53,52,21));
allPersons.add(new Person("Wesley L. Hogan",1990,2037,61,10,83,52,33,97));
allPersons.add(new Person("Isabel M. Baldwin",1988,2040,63,20,46,13,71,47));
allPersons.add(new Person("R.O. Clayton",1987,2038,66,4,6,9,55,91));
allPersons.add(new Person("Nicole Joseph",1987,2004,75,43,7,48,91,39));
allPersons.add(new Person("Lawrence Grant",1987,2036,74,87,85,96,86,47));
allPersons.add(new Person("Annette Dutton",1990,2031,74,57,94,0,26,73));
allPersons.add(new Person("Douglas Z. Douglas",1989,2020,75,35,68,38,68,37));
allPersons.add(new Person("Beatrice Ford",1990,2041,74,86,96,20,58,25));
allPersons.add(new Person("Eric Q. Evans",1987,2018,81,55,11,33,25,5));
allPersons.add(new Person("Roger Brown",1987,2049,82,87,58,14,42,12));
allPersons.add(new Person("A.T. Anderson",1989,2022,83,68,74,21,75,17));
allPersons.add(new Person("M.I. Cobb",1986,2010,81,32,3,70,65,27));
allPersons.add(new Person("M.M. Kaufman IV",1988,2022,87,40,22,43,25,92));
allPersons.add(new Person("Joseph Q. Cook",1990,2035,92,21,78,70,59,53));
allPersons.add(new Person("Earl O. Henry",1990,2000,90,77,41,34,16,10));
allPersons.add(new Person("Audrey Hudson",1987,2029,92,70,51,98,89,2));
allPersons.add(new Person("D.P. Clark",1990,2005,90,67,81,9,89,1));
allPersons.add(new Person("Kimberly Gibson",1988,2035,90,74,70,1,98,51));
allPersons.add(new Person("Harvey Bradley",1998,2030,5,97,51,4,98,30));
allPersons.add(new Person("Frances Bowman",1996,2056,3,71,26,63,86,13));
allPersons.add(new Person("Eric Hamilton",2000,2047,7,46,21,56,30,62));
allPersons.add(new Person("Jesse Blake",1998,2052,4,79,32,33,47,77));
allPersons.add(new Person("Abigail Kaufman",1999,2050,7,91,19,18,34,43));
allPersons.add(new Person("Katie Harrison",1997,2032,11,72,20,58,67,50));
allPersons.add(new Person("Bruce Cobb",1999,2053,13,30,90,7,32,9));
allPersons.add(new Person("Z.F. Allen",2000,2016,13,66,54,7,37,19));
allPersons.add(new Person("Frances D. Bennett",2000,2022,11,51,95,69,30,84));
allPersons.add(new Person("Sean F. Gibson IV",1998,2043,13,84,69,76,40,69));
allPersons.add(new Person("Lynn Hardy-Long",1996,2044,25,65,66,99,1,13));
allPersons.add(new Person("Julie X. Abbott-Mackenzie",2000,2036,20,30,1,33,11,68));
allPersons.add(new Person("Amber Jackson-Fitzgerald",1999,2049,25,86,79,44,54,35));
allPersons.add(new Person("Helen Kent",1997,2053,23,82,85,68,54,99));
allPersons.add(new Person("Georgia Douglas",1996,2049,21,6,26,41,87,97));
allPersons.add(new Person("Jeffrey Mackenzie",1996,2026,35,59,8,85,9,42));
allPersons.add(new Person("Norma R. Elliott",1997,2036,35,55,24,5,75,60));
allPersons.add(new Person("Diane Glass-King",1999,2023,30,21,39,82,83,39));
allPersons.add(new Person("Maureen O. Cunningham",1996,2019,34,39,52,94,9,2));
allPersons.add(new Person("Janet Long",1999,2019,30,81,55,74,74,54));
allPersons.add(new Person("Michael Brewer",1999,2042,40,58,83,49,53,19));
allPersons.add(new Person("Grace D. Lane",1997,2039,41,34,19,71,62,9));
allPersons.add(new Person("Dennis Caldwell",1997,2043,40,32,50,28,75,41));
allPersons.add(new Person("April Ferguson",1997,2023,40,41,57,87,58,58));
allPersons.add(new Person("Curtis Abbott",1999,2025,46,91,82,55,72,35));
allPersons.add(new Person("David Allen",1998,2045,55,77,25,61,30,1));
allPersons.add(new Person("Marilyn Gilbert",1996,2046,56,52,56,86,83,91));
allPersons.add(new Person("Julia K. Carroll",1997,2054,54,68,29,43,91,92));
allPersons.add(new Person("George Craig",1999,2012,50,65,99,74,56,59));
allPersons.add(new Person("Victor Horton",1996,2021,57,99,8,34,61,57));
allPersons.add(new Person("Clara O. Glover-Alexander",1996,2010,63,81,68,49,83,42));
allPersons.add(new Person("Gordon F. Atkinson",1997,2013,64,93,87,28,10,26));
allPersons.add(new Person("Madeline Farmer",1999,2045,62,53,35,95,89,99));
allPersons.add(new Person("Jerome M. Griffin",2000,2044,64,57,52,85,5,18));
allPersons.add(new Person("Howard Garrett",1996,2038,63,54,11,44,86,19));
allPersons.add(new Person("Elizabeth Daniels",1998,2033,76,52,56,67,50,69));
allPersons.add(new Person("Alicia Cox",1997,2049,71,86,26,59,22,7));
allPersons.add(new Person("Tyler Hampton Jr.",2000,2019,73,9,13,38,93,93));
allPersons.add(new Person("Stephen Douglas",1996,2047,76,97,88,8,55,59));
allPersons.add(new Person("Brenda Fisher",1998,2022,77,68,19,64,9,19));
allPersons.add(new Person("Katherine Doyle-Brown",1996,2056,84,52,49,79,66,21));
allPersons.add(new Person("K.T. Greene",2000,2020,85,11,97,95,81,44));
allPersons.add(new Person("Paul B. Austin",1999,2045,85,19,38,51,57,99));
allPersons.add(new Person("Mabel X. Leonard",1997,2058,80,71,69,70,28,76));
allPersons.add(new Person("Harold Chandler",1996,2057,80,83,7,69,55,25));
allPersons.add(new Person("Joanne R. Cook",1998,2015,90,27,20,82,74,45));
allPersons.add(new Person("Clifford I. Clark",1996,2051,97,45,38,95,80,3));
allPersons.add(new Person("Maxwell Barnett",1996,2039,94,7,77,9,84,12));
allPersons.add(new Person("Cynthia K. Ford-Johnson",1998,2020,90,92,27,29,65,99));
allPersons.add(new Person("Christine Garrett-Ellis",1998,2024,95,86,71,23,1,21));
allPersons.add(new Person("Kayla Kaufman",2006,2066,1,29,95,46,87,46));
allPersons.add(new Person("Bryan Green",2010,2048,2,46,75,73,20,88));
allPersons.add(new Person("D.D. Bush",2008,2055,5,86,87,79,48,4));
allPersons.add(new Person("Gabriel Bush",2007,2024,0,79,58,11,45,35));
allPersons.add(new Person("Colin Grant",2006,2065,6,61,90,45,16,70));
allPersons.add(new Person("Maria Drake",2009,2068,16,87,59,37,85,95));
allPersons.add(new Person("Marilyn Love",2009,2063,17,78,24,25,22,97));
allPersons.add(new Person("G.Z. Fowler",2009,2022,11,56,93,48,86,60));
allPersons.add(new Person("Samuel Bell",2006,2022,15,40,26,48,74,83));
allPersons.add(new Person("Derek Gilbert",2007,2063,16,18,72,91,36,55));
allPersons.add(new Person("Candice Daniel",2010,2031,24,0,52,6,93,8));
allPersons.add(new Person("Irene H. Higgins-Daniels",2006,2030,24,48,99,5,63,26));
allPersons.add(new Person("Alan Lowe",2010,2047,27,12,29,75,78,14));
allPersons.add(new Person("Amy Fuller",2008,2057,27,78,6,5,71,89));
allPersons.add(new Person("Pamela Gray",2009,2050,27,33,74,44,59,15));
allPersons.add(new Person("Mary Chapman",2007,2068,35,25,62,61,10,1));
allPersons.add(new Person("Philip Farmer",2007,2034,30,66,38,38,93,63));
allPersons.add(new Person("Bruce Lucas",2006,2069,36,77,2,88,12,84));
allPersons.add(new Person("Margaret F. Cross",2008,2037,35,66,20,34,67,81));
allPersons.add(new Person("S.F. Evans",2008,2044,35,53,93,37,45,38));
allPersons.add(new Person("Scott Curtis",2009,2065,43,23,26,78,31,46));
allPersons.add(new Person("Gertrude Copeland-Harris",2008,2020,44,45,68,80,73,51));
allPersons.add(new Person("Andrew A. Gordon",2010,2063,44,2,39,57,70,41));
allPersons.add(new Person("Roger Barker",2008,2042,41,7,84,88,15,4));
allPersons.add(new Person("Dana Berry",2009,2043,44,57,46,69,63,21));
allPersons.add(new Person("Gladys Gardner",2007,2033,51,18,91,41,34,51));
allPersons.add(new Person("Holly Hawkins",2006,2069,57,77,27,45,64,89));
allPersons.add(new Person("Warren H. Howard",2010,2054,56,38,59,24,15,54));
allPersons.add(new Person("Ann Brady",2010,2063,54,20,36,71,68,16));
allPersons.add(new Person("Loretta Z. Armstrong",2010,2034,50,47,92,96,63,14));
allPersons.add(new Person("Russell Hale",2007,2058,67,53,98,56,33,95));
allPersons.add(new Person("Gerald Ingram",2009,2045,62,47,71,74,93,52));
allPersons.add(new Person("Darrell Hart",2009,2051,64,97,48,69,19,95));
allPersons.add(new Person("Scott E. Holloway",2007,2050,64,41,20,50,69,32));
allPersons.add(new Person("Robert Barnett",2008,2033,64,34,70,64,95,91));
allPersons.add(new Person("Timothy Ellis Jr.",2006,2062,74,55,65,18,90,41));
allPersons.add(new Person("Bernard Harper",2007,2044,71,20,8,84,37,36));
allPersons.add(new Person("Jack M. Boyd",2008,2023,74,17,70,65,1,2));
allPersons.add(new Person("Melissa Dawson",2007,2046,75,13,33,24,99,85));
allPersons.add(new Person("Richard Abbott IV",2010,2053,72,49,74,44,24,39));
allPersons.add(new Person("Logan Hampton",2009,2031,84,92,28,51,32,20));
allPersons.add(new Person("Diane Blake",2009,2048,87,9,41,29,65,8));
allPersons.add(new Person("Douglas R. Lambert I",2010,2043,82,65,70,57,28,93));
allPersons.add(new Person("Ralph P. Lowe",2007,2053,83,5,75,80,46,10));
allPersons.add(new Person("James Barnett",2006,2038,87,83,84,9,84,22));
allPersons.add(new Person("Maureen Lucas",2010,2020,94,63,0,74,23,60));
allPersons.add(new Person("Jacqueline Evans",2009,2048,93,6,21,74,55,0));
allPersons.add(new Person("Leslie F. Barrett",2006,2066,93,81,71,48,18,42));
allPersons.add(new Person("Adam Johnson",2007,2061,92,48,18,17,52,24));
allPersons.add(new Person("S.T. Campbell Jr.",2010,2063,90,41,56,49,58,71));
allPersons.add(new Person("Melanie Gardner",2020,2038,0,75,99,0,9,8));
allPersons.add(new Person("Amanda Barnett",2020,2073,5,97,87,8,59,40));
allPersons.add(new Person("Loretta X. Alexander",2016,2070,6,9,11,14,7,18));
allPersons.add(new Person("Philip Adams",2019,2054,1,3,12,80,77,68));
allPersons.add(new Person("Bernard A. Holt III",2017,2032,7,28,6,51,77,57));
allPersons.add(new Person("Joyce Coleman",2019,2077,13,69,59,20,87,6));
allPersons.add(new Person("Bradley Foster",2018,2057,15,67,26,36,92,70));
allPersons.add(new Person("V.W. Gross",2018,2050,16,78,25,9,72,59));
allPersons.add(new Person("Corey Chapman Jr.",2020,2038,12,87,54,29,32,99));
allPersons.add(new Person("Kevin R. Harvey",2016,2076,14,40,98,98,97,96));
allPersons.add(new Person("Aaron Johnson",2016,2061,24,82,92,19,95,37));
allPersons.add(new Person("Paul Kelly",2019,2059,21,35,75,81,28,50));
allPersons.add(new Person("Kathryn Barrett",2020,2047,26,74,7,57,58,8));
allPersons.add(new Person("Herbert Fraser",2020,2065,23,82,57,60,35,7));
allPersons.add(new Person("Carla N. Byrd",2019,2062,22,16,32,21,90,66));
allPersons.add(new Person("Sean Brooks",2017,2073,34,23,1,79,73,49));
allPersons.add(new Person("Barbara Holland",2019,2045,31,93,13,92,38,19));
allPersons.add(new Person("Curtis C. Hampton",2020,2054,33,95,3,36,66,70));
allPersons.add(new Person("Travis L. Hoffman",2018,2068,34,93,99,26,62,81));
allPersons.add(new Person("Brian Fields",2017,2065,32,51,32,68,7,8));
allPersons.add(new Person("Peter F. Garrett",2016,2031,47,69,96,13,18,88));
allPersons.add(new Person("Kimberly H. Duncan",2020,2045,45,11,78,76,25,23));
allPersons.add(new Person("Travis Hammond",2020,2032,44,75,26,21,75,16));
allPersons.add(new Person("Adrian Joseph IV",2017,2034,41,4,32,48,42,63));
allPersons.add(new Person("Harvey Hoffman",2017,2054,40,95,28,89,73,30));
allPersons.add(new Person("Kathy Johnston",2018,2069,57,29,86,53,55,78));
allPersons.add(new Person("Gerald Bennett",2018,2074,57,84,7,79,77,85));
allPersons.add(new Person("Kathleen Hughes",2019,2074,55,70,21,76,33,93));
allPersons.add(new Person("Austin Henry III",2019,2041,53,67,71,95,24,44));
allPersons.add(new Person("Dustin Dutton",2019,2076,53,40,40,86,19,52));
allPersons.add(new Person("Dean Blake",2017,2065,61,68,67,32,92,22));
allPersons.add(new Person("Gary Fowler",2016,2076,63,68,66,39,45,86));
allPersons.add(new Person("Deborah Hart",2018,2067,67,95,93,77,56,42));
allPersons.add(new Person("Jordan Glass I",2017,2067,60,8,19,56,62,75));
allPersons.add(new Person("Melanie R. Hawkins",2018,2036,62,53,1,23,81,47));
allPersons.add(new Person("Jason Green III",2020,2068,77,27,34,69,70,40));
allPersons.add(new Person("Mildred Gordon",2016,2077,75,41,55,68,52,46));
allPersons.add(new Person("Albert Evans",2016,2032,72,65,70,8,37,99));
allPersons.add(new Person("Mary Fitzgerald",2017,2056,70,36,29,37,68,78));
allPersons.add(new Person("Abigail Holt",2018,2078,76,62,82,35,6,95));
allPersons.add(new Person("Iris Jordan",2020,2042,81,78,96,60,17,94));
allPersons.add(new Person("William Barnett",2019,2066,82,99,58,19,79,22));
allPersons.add(new Person("I.V. Cox Sr.",2016,2043,85,57,18,92,53,76));
allPersons.add(new Person("Jill Griffith",2019,2071,87,11,20,16,59,80));
allPersons.add(new Person("Doris K. Lawson",2018,2063,87,33,44,63,32,50));
allPersons.add(new Person("Todd Johnston",2020,2037,93,44,51,97,26,36));
allPersons.add(new Person("Gertrude M. Gregory",2018,2032,91,25,9,48,94,28));
allPersons.add(new Person("Franklin George",2018,2032,92,63,30,79,61,67));
allPersons.add(new Person("Harry Hubbard",2018,2035,90,59,13,93,90,40));
allPersons.add(new Person("Zachary R. Holland",2019,2031,95,91,12,69,18,39));
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
    
    public static List<ideoGroup> allGroups = new ArrayList<>();
    public static List<ideoGroup> allTotalGroups = new ArrayList<>();
    public static void addGroups(){
        /*allGroups.add(new ideoGroup("Communist",2,95));
        allGroups.add(new ideoGroup("Socialist",10,80));
        allGroups.add(new ideoGroup("Progressive",20,65));
        allGroups.add(new ideoGroup("Liberal",30,50));
        allGroups.add(new ideoGroup("Conservative",20,35));
        allGroups.add(new ideoGroup("Nationalist",10,20));
        allGroups.add(new ideoGroup("Fascist",2,5));*/
        //      allGroups.add(new ideoGroup("", "", 20, 60));
        
        //starter groups 
        allTotalGroups.add(new ideoGroup("Conservative Aristocrats", "Peoples Congress for Tradition", 15, 10, 1832));
        allTotalGroups.add(new ideoGroup("Landed Middle Class", "Nationalist Peoples Assembly", 15, 10, 1832));
        allTotalGroups.add(new ideoGroup("Rural Peasantry", "Conservative Peoples Party ", 25, 25, 1832));
        allTotalGroups.add(new ideoGroup("Conservative Clerical Class", "National Purity Party", 5, 8, 1832));
        allTotalGroups.add(new ideoGroup("Landowning Farmers", "National Independent Congress", 5, 18, 1832));
        allTotalGroups.add(new ideoGroup("Urban Middle Class", "Peoples Democratic Party", 15, 30, 1832));
        allTotalGroups.add(new ideoGroup("Reformist Aristocrats", "Democratic Reform Party", 20, 35, 1832));
        allTotalGroups.add(new ideoGroup("Liberal Intelligentsia", "Liberal Peoples Party", 18, 40, 1832));
        allTotalGroups.add(new ideoGroup("New Middle Class", "Democratic Action Movement", 25, 45, 1832));
        allTotalGroups.add(new ideoGroup("Religious Centrists", "National Center Party", 15, 50, 1832));
        allTotalGroups.add(new ideoGroup("Republican Core Supporters", "New Republican Movement", 24, 60, 1832));
        allTotalGroups.add(new ideoGroup("Urban Working Class", "Workers Democratic Party", 50, 70, 1832));
        allTotalGroups.add(new ideoGroup("Radicalized Citizens", "National Radical Alliance", 30, 75, 1832));
        allTotalGroups.add(new ideoGroup("Revolutionaries", "Sustained Revolution Movement", 30, 80, 1832));
        
        //Mid 19th Century
        allTotalGroups.add(new ideoGroup("Industrialists", "National Democratic Conservative Party", 5, 16, 1860));
        allTotalGroups.add(new ideoGroup("Unionists", "National Alliance of Unions", 30, 65, 1865));
        allTotalGroups.add(new ideoGroup("Socialist Intelligentsia", "Democratic Social Reform Party", 10, 63, 1868));
        allTotalGroups.add(new ideoGroup("Militarists", "National Order Party", 7, 17, 1874));
        allTotalGroups.add(new ideoGroup("Socialist Peasants", "Working Farmers Party", 20, 65, 1876));
        
        // Late 19th Century
        allTotalGroups.add(new ideoGroup("Technocratic Intelligentsia", "National Development Party", 10, 50, 1880));
        allTotalGroups.add(new ideoGroup("Revolutionaries", "Socialist Revolutionary Party", 10, 80, 1884));
        allTotalGroups.add(new ideoGroup("Reformist Socialists", "Democratic Revolution Party", 20, 70, 1889));
        allTotalGroups.add(new ideoGroup("Progressive Middle Class", "Democratic Unity Party", 25, 60, 1894));
        
        //Early 20th Century
        allTotalGroups.add(new ideoGroup("Democratic Socialists", "Democratic Socialist Party", 20, 70, 1902));
        allTotalGroups.add(new ideoGroup("Industrial Developmentalists", "National Prosperity Party", 15, 42, 1905));
        allTotalGroups.add(new ideoGroup("Progressive Populists", "Progressive Peoples Party", 25, 60, 1909));
        allTotalGroups.add(new ideoGroup("Fascists", "Peoples National Revolutionary Party", 15, 3, 1915));
        allTotalGroups.add(new ideoGroup("Social Democrats", "Social Democratic Party", 20, 65, 1918));
        allTotalGroups.add(new ideoGroup("Republican Conservatives", "Conservative Republican Party", 18, 38, 1918));
        allTotalGroups.add(new ideoGroup("Marxist Leninists", "Communist Revolutionary Party", 8, 90, 1923));
        allTotalGroups.add(new ideoGroup("Liberal Unionists", "Alliance of Republican Unions", 16, 53, 1927));
        
        // Mid 20th Century
        allTotalGroups.add(new ideoGroup("Maoists", "Communist Peoples Struggle Party", 13, 90, 1949));
        allTotalGroups.add(new ideoGroup("Religious Fundamentalists", "National Awakening Party", 20, 9, 1952));
        allTotalGroups.add(new ideoGroup("New Leftists", "New Social Democrats", 23, 58, 1961));
        allTotalGroups.add(new ideoGroup("Religious Socialists", "Holy Movement of Workers", 13, 68, 1965));
        
        // Late 20th Century
        allTotalGroups.add(new ideoGroup("Neoliberals", "Liberal Democratic Party", 16, 40, 1970));
        allTotalGroups.add(new ideoGroup("Revolutionary Conservatives", "New National Conservative Movement", 13, 35, 1976));
        allTotalGroups.add(new ideoGroup("Neo-Fascists", "Nationalist Peoples List", 10, 10, 1980));
        allTotalGroups.add(new ideoGroup("Neoconservatives", "Republican Democratic Party", 16, 39, 1988));
        allTotalGroups.add(new ideoGroup("Environemntalists", "National Green Movement", 15, 50, 1990));
        
        //21st Century
        allTotalGroups.add(new ideoGroup("Right Wing Populists", "Patriotic Liberty Party", 16, 30, 2008));
        allTotalGroups.add(new ideoGroup("Left Wing Populists", "Democratic Revolutionary Movement", 16, 80, 2010));
        
    }
    
    public static void addActiveGroups(){
        for(ideoGroup gro:allTotalGroups){
            if(gro.getActiveYear()< year&& !allGroups.contains(gro)){
                //System.out.println("DEBUG GROUP ACTIVATED: "+ gro.getName());
                allGroups.add(gro);
            }
        }
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
        allParties.add(new Party("Unified Revolutionary Movement", 85, true, assignColor(85)));
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
                if(gro.getIdeology()>65 || gro.getIdeology()<35){
                changeby-=(changeby/10)* (Math.abs(gro.getIdeology()-50)/10);
                }
            }else{
                if(lean.equalsIgnoreCase("Reaction")){
                    if(gro.getIdeology()>35){
                        changeby-=(changeby/10)* (Math.abs(gro.getIdeology()-35)/10);
                    }
                }else{
                    if(gro.getIdeology()<65){
                        changeby-=(changeby/10)* (Math.abs(gro.getIdeology()-65)/10);
                    }
                }
            }
            
            gro.updateSize(ra.nextInt(((changeby+1)/ (((year-gro.getActiveYear())/10)+1))+1));
        }
    }
    
    public static void election(){
        for(Party par: allParties){
            par.resetElectionData();
        }
        
        Map<ideoGroup, Integer> acceptables = new HashMap<>();
        int tresh = 85;
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

    // Groups determine what parties best align with their ideology
    for (Party par : allParties) {
        int currentProx = gro.proximityWith(par);
        
       
        if (currentProx > maxProximity) {
            maxProximity = currentProx;
        }

        if (currentProx > tresh) {
            double appeal = currentProx * (1 + (par.getRecognition() / 10.0));
            partyAppeals.put(par, appeal);
            totalAppealScore += appeal;
            hasvoted = true; 
        }
    }

    if (hasvoted) {
        for (Party par : partyAppeals.keySet()) {
            double shareOfGroup = partyAppeals.get(par) / totalAppealScore;
            int votesFromThisGroup = (int) (gro.getSize() * shareOfGroup);
            
            double fatigueFactor = Math.min(par.getFatigue(), 0.9);
            votesFromThisGroup -= (votesFromThisGroup * fatigueFactor);
            //votesFromThisGroup = IdeoCheck(votesFromThisGroup, par);
            
            votesFromThisGroup += (votesFromThisGroup*(par.getRecognition()));
            
            votesFromThisGroup = (votesFromThisGroup*par.getPopularity())/100;
            if(par == gro.getFavPar()){
                votesFromThisGroup += votesFromThisGroup/5;
            }
            
            if(par.getChair()!=null){
            //votesFromThisGroup += (votesFromThisGroup*par.getChair().getProminence())/100;
            }
            
            
            
            par.addVotes(votesFromThisGroup+1);
            par.recordVotes(gro, votesFromThisGroup+1);
        }
    }

    // satisfaction calculation
    int satischange = -1 * (5 - (maxProximity / 20)); 
    
    if (!hasvoted) {
        // penalty for having no one to vote for
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
            
            
        }
        // proportional
        
        Map<Party,Integer> percents = new HashMap<>();
        for(Party par: allParties){
            
            int pctg = (int) (par.getScore()*100)/ (totalVotes+1);
            percents.put(par, pctg);
            
        }
        
        
        
        int threshhold = 5;
        
        List<Party> partiesOverTresh = new ArrayList<>();
        
        for(Party par: allParties){
            if(percents.get(par)>threshhold){
                partiesOverTresh.add(par);
            }
        }
        
        //seat distribution
        for(int i=0; i<50;i++){ // simulation of first past the post
            int maxnum =-1;
            Party maxpar = null;
            for(Party par: allParties){
                int curscore = par.getScore() + ra.nextInt(par.getScore()/10 + 1) - ra.nextInt(par.getScore()/10 + 1);
                if(curscore > maxnum){
                    maxnum = curscore;
                    maxpar = par;
                }
            }
            //if (totalVotes <= 0) return;
            maxpar.setPercent(maxpar.getPercent()+1); 
        }
        
        //dhondt
        for(int i=0; i<50;i++){
            
            int maxnum=-1;
            Party maxpar=null;
            for(Party par: partiesOverTresh){
                    int parscore = ((par.getScore()*50)/(par.getPercent()+1))+1;
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
        Party largestPar = null;
        int mnum = Integer.MIN_VALUE;
        
        if(year !=1852){
            // governemnt primaries
        if(rulingCoalition.getMemberList().size()==1){
            candidates.add(rulingCoalition.getLeader());
        }else{
            for(Party par: rulingCoalition.getMemberList()){
                int points = 0;
                points += par.getPercent()*3;
                if(par.getStandardB()!=null){
                points += par.getStandardB().getProminence()*2;
                }else{
                    points =0;
                }
                
                if(par == President){
                    points*=2;
                }
                
                if(points >= mnum){
                    mnum = points;
                    largestPar = par;
                }
            }
            
            candidates.add(largestPar);
            
            for(Party par : rulingCoalition.getMemberList()){
                if(par!= largestPar){
                    if(par.relationWith(largestPar) < 30){
                        candidates.add(par);
                    }
                }
            }
        }
        
        //opposition primaries
        
        mnum = Integer.MIN_VALUE;
        
            for(Party par: allParties){
                if(!rulingCoalition.getMemberList().contains(par)){
                    int points = 0;
                points += par.getPercent()*3;
                if(par.getStandardB()!=null){
                points += par.getStandardB().getProminence()*2;
                }else{
                    points =0;
                }
                
                if(par == President){
                    points*=2;
                }
                    if(points >= mnum){
                        mnum = points;
                        largestPar = par;
                    }
                }
            }
            
            candidates.add(largestPar);
            
            for(Party par : allParties){
                if(!rulingCoalition.getMemberList().contains(par)){
                    if(par!= largestPar){
                        if(par.relationWith(largestPar) < 30){
                            candidates.add(par);
                        }
                    }
                }
                
            }
        }else{
            for(Party par: allParties){
                if(ra.nextInt(10)<5+ (10-allParties.size())){
                    candidates.add(par);
                }
            }
        }
        
        
        int tresh=0;
        
        int winvotes = 0;
        Party winner = null;
        
        for(Party par: candidates){
            par.resetScore();
        }
        Map<ideoGroup, Integer> acceptables = new HashMap<>();
        tresh = 95;
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

    for (Party par : allParties) {
        int currentProx = gro.proximityWith(par);
        
        if (currentProx > maxProximity) {
            maxProximity = currentProx;
        }

        if (currentProx > tresh) {
            double appeal = currentProx * (1 + (par.getRecognition() / 10.0));
            partyAppeals.put(par, appeal);
            totalAppealScore += appeal;
            hasvoted = true; 
        }
    }

    if (hasvoted) {
        for (Party par : partyAppeals.keySet()) {
            double shareOfGroup = partyAppeals.get(par) / totalAppealScore;
            int votesFromThisGroup = (int) (gro.getSize() * shareOfGroup);

            votesFromThisGroup -= (votesFromThisGroup * par.getFatigue()) / 100;
            votesFromThisGroup = (votesFromThisGroup*par.getPopularity())/100;
            if(par == gro.getFavPar()){
                votesFromThisGroup += votesFromThisGroup/5;
            }
            
            if(par.getStandardB()!=null){
            votesFromThisGroup += (votesFromThisGroup*par.getStandardB().getProminence())/200;
            }
            par.addVotes(votesFromThisGroup);
            par.recordVotes(gro, votesFromThisGroup/5);
        }
    }

    int satischange = -1 * (5 - (maxProximity / 20)); 
    
    if (!hasvoted) {
        satischange -= ra.nextInt(10);
    }
    
    satischange += ra.nextInt(3) - ra.nextInt(3);
    gro.updateSatisfaction(satischange/3);
}
        
        
        
        int totvotes = 1;
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
        
        tresh = 85;
        
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

    for (Party par : allParties) {
        int currentProx = gro.proximityWith(par);
        
        if (currentProx > maxProximity) {
            maxProximity = currentProx;
        }

        if (currentProx > tresh) {
            double appeal = currentProx * (1 + (par.getRecognition() / 10.0));
            partyAppeals.put(par, appeal);
            totalAppealScore += appeal;
            hasvoted = true; 
        }
    }

    if (hasvoted) {
        for (Party par : partyAppeals.keySet()) {
            double shareOfGroup = partyAppeals.get(par) / totalAppealScore;
            int votesFromThisGroup = (int) (gro.getSize() * shareOfGroup);

            votesFromThisGroup -= (votesFromThisGroup * par.getFatigue()) / 100;
            votesFromThisGroup = (votesFromThisGroup*par.getPopularity())/100;
            if(par == gro.getFavPar()){
                votesFromThisGroup += votesFromThisGroup/5;
            }
            if(par.getStandardB()!=null){
            votesFromThisGroup += (votesFromThisGroup*par.getStandardB().getProminence())/200;
            }
            par.addVotes(votesFromThisGroup);
            par.recordVotes(gro, votesFromThisGroup/5);
        }
    }

    int satischange = -1 * (5 - (maxProximity / 20)); 
    
    if (!hasvoted) {
        satischange -= ra.nextInt(10);
    }
    
    satischange += ra.nextInt(3) - ra.nextInt(3);
    gro.updateSatisfaction(satischange/2);
}
            winvotes = 0;
            totvotes=1;
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
        System.out.println("\n===============\nElected President: "+ President.getStandardB()+" "+"\n===============\n");
        President.getStandardB().setPresToTrue();
        President.getStandardB().incrementPrescount();
    }
    
    public static int IdeoCheck(int toAdd,Party par){
        int lefttresh = 70, righttresh = 30;
        int divileft = (Math.abs(100-par.getIdeology()))/2;
        int diviright = (Math.abs(0-par.getIdeology()))/2;
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
                    }else if(lean.equalsIgnoreCase("Revolution")){
                        if(par.getIdeology()>righttresh && par.getIdeology()< lefttresh){
                            toAdd/=divileft;
                        }else{
                            if(par.getIdeology()<lefttresh){
                                toAdd*=(divileft-100)/5;
                                
                            }else{
                                toAdd/=divileft;
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
                if(totGovSeats<50 && President ==null){
                    Party findLargest = null;
                    int findlargemax = Integer.MIN_VALUE;
                    for(Party par: allParties){
                        if(findlargemax< par.getPercent()){
                            findlargemax = par.getPercent();
                            findLargest = par;
                        }
                    }
                    Coalition gov = new Coalition(findLargest);
                    rulingCoalition=gov;
                }
                
                if(totGovSeats <50 && President!=null){
                    
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
                System.out.println("Government formed by "+ rulingCoalition.getLeader().getColor()+ rulingCoalition.getLeader().getName()+ RESET + rulingCoalition.getLeader().ideoDisplay());
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
            
        }while(winseats<50);
        
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
                if(lean.equalsIgnoreCase("Republic")){
                    double totalRecog = 0;
                    for(Party p : allParties) totalRecog += p.getRecognition();
                    if(totalRecog > 0.5){
                                    System.out.println("Populist Wave!");
                                    for(Party par : allParties) {
                           
                            if (par.getRecognition() > 0.1) {
                                par.setRecog(par.getRecognition()/5); 
                                for(int i=0; i<par.getPercent()/5;i++){
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
                }else{
                    int numofparsinNA = 0;
                    for(Party par: allParties){
                        if(par.getPercent()>0){
                            numofparsinNA++;
                        }
                    }
                    int chan = ra.nextInt(10) - numofparsinNA;
                    if(chan > 5){
                        System.out.println("Democratic Revolution!");
                        for(Party par: allParties){
                        if(par.getPercent()>0 && (par.getIdeology()> 65 || par.getIdeology()<35)){
                            for(int i=0; i<par.getPercent()/5;i++){
                                par.addFatigue();
                                par.addFatigue();
                                par.addFatigue();
                                par.addFatigue();
                                par.addFatigue();
                                par.addFatigue();
                            }
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

public static void shiftVoters(int magnitude) {
    Map<ideoGroup, Integer> changes = new HashMap<>();
    
    for (ideoGroup gro : allGroups) {
        int defectors = gro.getSize() / 100;
        
        changes.put(gro, changes.getOrDefault(gro, 0) - defectors);
        
        ideoGroup target = findClosestGroup(gro.getIdeology() + magnitude);
        
        if (target != null) {
            changes.put(target, changes.getOrDefault(target, 0) + defectors);
        }
    }

    for (Map.Entry<ideoGroup, Integer> entry : changes.entrySet()) {
        entry.getKey().updateSize(entry.getValue());
    }
}

public static void leftShift(){
    shiftVoters(10);
}
public static void rightShift(){
    shiftVoters(-10);
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

public static void updateBasedOnLean(){
    int distance =0;
    
    for(Party par: allParties){
        distance=0;
        if(lean.equalsIgnoreCase("Reaction")){
            if(par.getIdeology()>40){
                distance = (par.getIdeology()-40);
                par.setApproval(par.getPopularity()/10);
            }else{
                par.decreaseFatigue();
            }
        }else if(lean.equalsIgnoreCase("Revolution")){
            if(par.getIdeology()<60){
                distance = (60-par.getIdeology());
                par.setApproval(par.getPopularity()/10);
            }else{
                par.decreaseFatigue();
            }
        }else if(lean.equalsIgnoreCase("Republic")){
            if(par.getIdeology()>65 || par.getIdeology()<35){
                distance = Math.abs(par.getIdeology()-50);
            }
            
            
        }
        for(int i=0; i<distance;i++){
            par.addFatigue();
        }
    }
    
    
    
}
public static void checkNullLeadership(){
    for(Party par: allParties){
        if(par.getChair()==null|| par.getStandardB()==null || par.getForSpeak()==null){
            par.setApproval(0);
        }
    }
}
    public static void updateTick(){
        events();
        addActiveGroups();
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
        updateBasedOnLean();
        checkNullLeadership();
        SCJCountdown();
        checkSCCCdown();
        checkVacancies();
        SCCheck();
        allFavPar();
        
        for(Party par: allParties){
            if(rulingCoalition.getMemberList().contains(par)){
                approvalRatingChange = ra.nextInt(5)-ra.nextInt(10);
                approvalRatingChange*= (ra.nextInt(3))+1;
                par.updateApproval(approvalRatingChange);
                
                par.ideoDrift();
            }else{
                approvalRatingChange = ra.nextInt(10)-ra.nextInt(5);
                approvalRatingChange*= (ra.nextInt(3))+1;
                par.updateApproval(approvalRatingChange);
                
                par.ideoDrift();
            }
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
    
    public static class district{
        int ideology;
        int x, y; // coordinates
        public district(int ideology, int x, int y){
            this.ideology = ideology;
            if(ideology <0){
                ideology=0;
            }
            if(ideology >100){
                ideology=100;
            }
            this.x=x;
            this.y=y;
        }
        
        int getX(){return x;}
        int getY(){return y;}
        
        Party findNearestParty(){
            int maxnum=Integer.MIN_VALUE;
            Party maxpar=null;
            for(Party par: allParties){
                int rdne = (ra.nextInt(par.getPercent()+1)-ra.nextInt(par.getPercent()+1));
                int pts = (par.proximityWith(ideology)/2) + (par.getPercent()/4) + ra.nextInt(15);
                if(par.getPercent()==0){
                    pts = 0;
                }
                if(pts> maxnum){
                    
                    maxnum= pts;
                    maxpar=par;
                }
            }
            return maxpar;
        }
        
        @Override
        public String toString(){
            return findNearestParty().getColor()+"o"+RESET;
        }
    }
    public static List<district> allDists = new ArrayList<>();
    
    public static void mapGen(){
        Random fixedRandom = new Random(19862026);
        int width = uniwid, height = unihei;
        int numCities = 5;
        List<int[]> cityCenters = new ArrayList<>();
        for (int i = 0; i < numCities; i++) {
            int wid = fixedRandom.nextInt(width);
            int hei = fixedRandom.nextInt(height);
            cityCenters.add(new int[]{wid,hei});
            allDists.add(new district(ra.nextInt(30)+60, wid, hei));
        }
        List<double[]> landAnchors = new ArrayList<>();
landAnchors.add(new double[]{width * 0.4, height * 0.5}); // Left-center blob
landAnchors.add(new double[]{width * 0.6, height * 0.4}); // Right-upper blob
landAnchors.add(new double[]{width * 0.5, height * 0.7}); // Southern peninsula blob
        
        for(int x=0; x<width;x++){
            for(int y = 0; y<height;y++){
                double minDistanceFactor = Double.MAX_VALUE;
        for (double[] anchor : landAnchors) {
            // Scale x and y differences proportionally
            double normX = (x - anchor[0]) / (width / 2.0);
            double normY = (y - anchor[1]) / (height / 2.0);
            double distFactor = (normX * normX) + (normY * normY);
            
            if (distFactor < minDistanceFactor) {
                minDistanceFactor = distFactor;
            }
        }
        
        double roughness = (fixedRandom.nextDouble() * 0.45) - 0.225;
        if (minDistanceFactor + roughness > 0.65) {
            continue; 
        }
                
        double closestCityDist = Double.MAX_VALUE;
        for (int[] cit : cityCenters) {
            double dist = Math.sqrt(Math.pow(x - cit[0], 2) + Math.pow(y - cit[1], 2));
            if (dist < closestCityDist) {
                closestCityDist = dist;
            }
        }
        
        double falloff = 1.8; 
        double urbanInfluence = Math.exp(-closestCityDist / (falloff- ra.nextDouble(falloff/10)));

// Convert to your 0 - 100 scale
// 100 = Urban/Left (on a city), scaling down rapidly to 0 = Rural/Right
        int finalideo = (int) (urbanInfluence * 100);
        
        allDists.add(new district(finalideo, x, y));
                
                
                
                
            }
        }
        
        
    }
    public static int uniwid = 25, unihei=10;
    
    public static void distmap(){
        int width = uniwid;
        int height = unihei;
        String[][] map = new String[height][width];
        for(int x = 0; x<width;x++){
            for(int y = 0; y<height;y++){
                for(district dis: allDists){
                    if(x == dis.getX() && y==dis.getY()){
                        map[y][x] = dis.toString();
                        break;
                    }else{
                        map[y][x] = " ";
                    }
                }
            }
        }
        for(int y=0; y<height;y++){
            for(int x=0; x<width;x++){
                System.out.print(map[y][x]);
            }
            
                System.out.println();
            
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
    
}

    
    

public static String BLACK = "\u001B[30m";
public static String lean = "Republic";
public static void nationalLean(){
    int reaction =0,republic =0, revolution=0;
    int total =0;
    for(ideoGroup gro : allGroups){
        if(gro.getIdeology() <= 35){
            reaction+= gro.getSize();
        } else if(gro.getIdeology()>=65 ){
            revolution+=gro.getSize();
        }else{
            republic += gro.getSize();
        }
    }
    if(rulingCoalition.getMemberList().size() == 1){
    if(rulingCoalition.getLeader().getIdeology() <= 35){
            reaction+= reaction/8;
        } else if(rulingCoalition.getLeader().getIdeology()>=65 ){
            revolution+=revolution/8;
        }else{
            republic += republic/8;
        }
    }
     
            if(President.getIdeology() <= 35){
                    reaction+= reaction/6;
                } else if(President.getIdeology()>=65 ){
                    revolution+=revolution/6;
                }else{
                    republic += republic/6;
                }
        if(speaker.getIdeology() <= 35){
                    reaction+= reaction/10;
                } else if(speaker.getIdeology()>=65 ){
                    revolution+=revolution/10;
                }else{
                    republic += republic/10;
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
        reaction+= (reaction/10)* (((rightistseats-50)/10)+1);
    }
    if(leftistseats>= 50){
        revolution+=(revolution/10)* (((leftistseats-50)/10)+1);
    }
    republic += republic/2;
    
    for(SCJustice jus: supremeCourt){
        if(jus.getIdeology()>35 && jus.getIdeology()<65){
            republic += republic/10;
        }else{
            if(jus.getIdeology()<=35){
                reaction+= reaction/10;
            }else{
                revolution += revolution/10;
            }
        }
    }
    
    for(Policy pol : allPolicies){
        if(pol.getPosition()>35 && pol.getPosition()<65){
            republic += republic/20;
        }else{
            if(pol.getPosition()<=35){
                reaction+= reaction/20;
            }else{
                revolution += revolution/20;
            }
        }
    }
    
    System.out.print("The Nation leans towards");
    if(reaction> republic +revolution){
        System.out.println("\u001B[38;5;18m Reaction \u001B[0m");
        if(!lean.equalsIgnoreCase("Reaction")){
            for(Party par: allParties){
                if(par.getIdeology()>45){
                    par.addFatigue();
                    par.addFatigue();
                    par.addFatigue();
                }
            }
        }
        lean = "Reaction";
    }else if(republic>= reaction+revolution){
        System.out.println("\u001B[38;5;226m Republic \u001B[0m");
        if(!lean.equalsIgnoreCase("Republic")){
            for(Party par: allParties){
                if(par.getIdeology()>65 && par.getIdeology()<35){
                    par.addFatigue();
                    par.addFatigue();
                    par.addFatigue();
                }
            }
        }
        lean = "Republic";
    }else if(revolution> reaction+republic){
        System.out.println("\u001B[38;5;88m Revolution \u001B[0m");
        if(!lean.equalsIgnoreCase("Revolution")){
            for(Party par: allParties){
                if(par.getIdeology()<55){
                    par.addFatigue();
                    par.addFatigue();
                    par.addFatigue();
                }
            }
        }
        
        lean = "Revolution";
    }else{
        System.out.println("\u001B[38;5;226m Republic \u001B[0m");
        lean = "Republic";
    }
    String spectrum ="";
    int reactpercent=0, republicpercent=0, revpercent=0;
    reaction*=100;
    revolution*=100;
    republic*=100;
    for(int i=0;i< 100; i++){
        if(reaction/(reactpercent+1)> republic/(republicpercent+1) && reaction/(reactpercent+1) >= revolution/(revpercent+1)){
            reactpercent++;
        }else if(republic/(republicpercent+1)>= reaction/(reactpercent+1) && republic/(republicpercent+1)>= revolution/(revpercent+1)){
            republicpercent++;
        }
        else if(revolution/(revpercent+1)> reaction/(reactpercent+1) && revolution/(revpercent+1)> republic/(republicpercent+1)){
            revpercent++;
        }
        
    }
    spectrum+="\u001B[38;5;88m";
    for(int i=0; i<revpercent;i++){
        spectrum+="|";
    }
    spectrum+="\u001B[38;5;226m";
    for(int i=0; i<republicpercent;i++){
        spectrum+="|";
    }
    spectrum+="\u001B[38;5;18m";
    for(int i=0;i<reactpercent;i++){
        spectrum+="|";
    }
    
        spectrum+="\u001B[0m";
        
    
    System.out.println(spectrum);
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
    
    public static void allFavPar(){
        for(ideoGroup gro: allGroups){
            gro.findFavParty();
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
	    mapGen();
	    addGroups();
	    addActiveGroups();
	    addParties();
	    addPersons();
	    addPolicies();
	    checkForActives();
	    assessAffiliations();
        assessProminence();
        allDetLeadership();
        initSetup();
		
		int interval  =4;
		int electionsToSimulate = 44;
		
		for(int i=0; i<electionsToSimulate;i++){
		    System.out.println(year+ "=========================");
		    
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
    //distmap();
    electPresident();
    
    shouldChangePolicy();
    displayPolicies();
    displaySC();
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
//passageRate();
nationalLean();
seeDominant();

//DEBUGDisplayAllActive();

		    String upu = sc.nextLine();
		    updateTick();
		    year+=interval;
		    
		}
		
		       
		
	}
}
