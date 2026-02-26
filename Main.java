import java.util.*;
public class Main // Don't tell mom I use java
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
        
        double fatigue = 0;
        
        public Party(String name, int ideology, boolean isActive, String color){
            this.name = name;
            this.ideology = ideology;
            this.isActive = isActive;
            this.color = color;
            failcount = 0;
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
            fatigue +=0.05;
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
            int driftspeed = 3;
            if(this.ideology < targetIdeo) this.ideology+= driftspeed;
            if(this.ideology> targetIdeo) this.ideology-= driftspeed;
            
            this.ideology += ra.nextInt(3)-ra.nextInt(3);
            
            int minsat = 1000;
            ideoGroup minGroup = null;
            for(ideoGroup gro : allGroups){
                if(gro.getSatisfaction()< minsat){
                    minsat = gro.getSatisfaction();
                    minGroup = gro;
                }
            }
            targetIdeo = minGroup.getIdeology();
            driftspeed = 2;
            if(this.ideology < targetIdeo) this.ideology+= driftspeed;
            if(this.ideology> targetIdeo) this.ideology-= driftspeed;
            int avgideo = 0;
            if(rulingCoalition !=null && rulingCoalition.getMemberList().contains(this)){
                for(Party par: rulingCoalition.getMemberList()){
                    avgideo += par.getIdeology();
                    avgideo = avgideo/rulingCoalition.getMemberList().size();
                }
            }
            if(this.ideology < avgideo) this.ideology+= driftspeed;
            if(this.ideology> avgideo) this.ideology-= driftspeed;
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
    
    public static Coalition rulingCoalition;
    
    public static int approvalRatingChange;
    
    
    
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
    allGroups.add(new ideoGroup("Monarchists", "All-Peoples Congress for Tradition", 15, 10));
    allGroups.add(new ideoGroup("Illiberal Republicans", "Nationalist Peoples Assembly", 10, 25));
    
    allGroups.add(new ideoGroup("Unitary Monarchists", "Restoration Party", 5, 10));
    allGroups.add(new ideoGroup("Particularists", "National Particularist Peoples Congress", 5, 12));
    
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
    
    int diceroll = (ra.nextInt(12))/3;
    // roll far right unity
    if(diceroll == 4){
        allParties.add(new Party("National Unity Party", 15, true, assignColor(15)));
    }else if(diceroll == 3){
        allParties.add(new Party("National Unity Party", 25, true, assignColor(15)));
    }else if(diceroll == 2){
        allParties.add(new Party("Peasants Republican Party", 35, true, assignColor(30)));
    }else{
        
    }
    diceroll = (ra.nextInt(12))/3;
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
    diceroll = (ra.nextInt(12))/3;
    if(diceroll == 4){
        allParties.add(new Party("Left Republican Party", 60, true, assignColor(65)));
    }else if(diceroll == 3){
        allParties.add(new Party("Left Republican Party", 65, true, assignColor(65)));
    }else if(diceroll == 2){
       allParties.add(new Party("Left Republican Party", 65, true, assignColor(65)));
        allParties.add(new Party("Workers Democratic Party", 75, true, assignColor(75)));
    }else{
        allParties.add(new Party("Left Republican Party", 65, true, assignColor(65)));
        allParties.add(new Party("Revolutionary Democratic Party", 85, true, assignColor(85)));
        allParties.add(new Party("Workers Democratic Party", 70, true, assignColor(70)));
    }
    
    
    }
    
    public static void updateGroupSize(){
        int changeby = 0;
        for(ideoGroup gro: allGroups){
            changeby = (Math.abs(gro.getIdeology()-50)>20)? 5:10;
            gro.updateSize(ra.nextInt(changeby));
        }
    }
    
    public static void election(){
        for(Party par: allParties){
            par.resetElectionData();
        }
        
        Map<ideoGroup, Integer> acceptables = new HashMap<>();
        int tresh = 65;
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
        
        for(ideoGroup gro: allGroups){
            boolean hasvoted = false;
            int maxProximity = 0;
            
            for(Party par: allParties){
                if(gro.proximityWith(par)>tresh){
                    hasvoted = true;
                    if(gro.proximityWith(par)>maxProximity){
                        maxProximity = gro.proximityWith(par);
                    }
                    int toAdd = (gro.getSize()*gro.proximityWith(par))/100;
                    if(rulingCoalition!= null){
                        if(rulingCoalition.getMemberList().contains(par)){
                            toAdd -= (int) (toAdd*Math.abs(approvalRatingChange))/1000;
                        }
                    }
                    toAdd += (int) toAdd* par.getRecognition();
                    if(toAdd>0){
                        toAdd-=(int) toAdd*par.getFatigue();
                    }else{
                        toAdd+=(int) toAdd*par.getFatigue();
                    }
                    int pctginAccept = ((par.getPercent()+1)*100)/(acceptables.get(gro)+1);
                    toAdd = (toAdd*pctginAccept)/100;
                    toAdd = IdeoCheck(toAdd, par);
                    par.addVotes(toAdd/(ra.nextInt(4)+1));
                    //par.addVotes(toAdd);
                    par.recordVotes(gro,toAdd);
                }
            }
            int satischange = -1*(5-(maxProximity/20));
            if(!hasvoted){
                satischange-=ra.nextInt(10);
            }
            satischange+= ra.nextInt(3)-ra.nextInt(3);
            gro.updateSatisfaction(satischange);
            
        }
        
        // set percentages
        int totalVotes = 0;
        for(Party par: allParties){
            par.setPercent(0);
            totalVotes += par.getScore();
            if(par.getPercent()>20){
                par.incrementRecognition();
            }
            
            int notoadd = par.getPercent()/20;
            for(int i =0; i<notoadd;i++){
                par.incrementRecognition();
            }
        }
        // proportional
        
        
        /*for(Party par: allParties){
            
            int pctg = (int) (par.getScore()*100)/ totalVotes;
            par.setPercent(pctg);
            par.setApproval(pctg);
            
        }*/
        
        
        
        
        //seat distribution
        /*for(int i=0; i<50;i++){ // simulation of first past the post
            int maxnum =0;
            Party maxpar = null;
            for(Party par: allParties){
                int curscore = par.getScore()/ (ra.nextInt(4)+1);
                if(curscore > maxnum){
                    maxnum = curscore;
                    maxpar = par;
                }
            }
            //if (totalVotes <= 0) return;
            maxpar.setPercent(maxpar.getPercent()+1); 
        }*/
        
        //dhondt
        for(int i=0; i<100;i++){
            int maxnum=-1;
            Party maxpar=null;
            for(Party par: allParties){
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
        int tresh = 30;
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
            
            points+= ra.nextInt((100-par.getPercent())+1);
            points += points*par.getRecognition();
            points -= (points*par.getFatigue())/2;
            
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
        
        for(ideoGroup gro: allGroups){
            boolean hasvoted = false;
            int maxProximity = 0;
            
            for(Party par: candidates){
                if(gro.proximityWith(par)>tresh){
                    hasvoted = true;
                    if(gro.proximityWith(par)>maxProximity){
                        maxProximity = gro.proximityWith(par);
                    }
                    int toAdd = (gro.getSize()*gro.proximityWith(par))/100;
                    if(rulingCoalition!= null){
                        if(rulingCoalition.getMemberList().contains(par)){
                            toAdd -= (int) (toAdd*Math.abs(approvalRatingChange))/1000;
                        }
                    }
                    toAdd += (int) toAdd* par.getRecognition();
                    if(toAdd>0){
                        toAdd-=(int) toAdd*par.getFatigue();
                    }else{
                        toAdd+=(int) toAdd*par.getFatigue();
                    }
                    int pctginAccept = ((par.getPercent()+1)*100)/(acceptables.get(gro)+1);
                    toAdd = (toAdd*pctginAccept)/100;
                    toAdd = IdeoCheck(toAdd, par);
                    if(toAdd<0){
                        toAdd =0;
                    }
                    par.addVotes(toAdd/(ra.nextInt(4)+1));
                    //par.addVotes(toAdd);
                    par.recordVotes(gro,toAdd);
                }
            }
            int satischange = -1*(5-(maxProximity/20));
            if(!hasvoted){
                satischange-=ra.nextInt(10);
            }
            satischange+= ra.nextInt(3)-ra.nextInt(3);
            gro.updateSatisfaction(satischange);
            
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
        System.out.println("==========Presidential Election==========");
        System.out.println("Round 1");
        int ordinal = 0;
        int majorvotes = 0;
        for(Party par: candidates){
            if(ordinal <3){
                System.out.print(par.getName()+ par.ideoDisplay()+" ["+((par.getScore()*100)/totvotes)+"%] | ");
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
        
        for(ideoGroup gro: allGroups){
            boolean hasvoted = false;
            int maxProximity = 0;
            
            for(Party par: candidates){
                if(gro.proximityWith(par)>tresh){
                    hasvoted = true;
                    if(gro.proximityWith(par)>maxProximity){
                        maxProximity = gro.proximityWith(par);
                    }
                    int toAdd = (gro.getSize()*gro.proximityWith(par))/100;
                    if(rulingCoalition!= null){
                        if(rulingCoalition.getMemberList().contains(par)){
                            toAdd -= (int) (toAdd*Math.abs(approvalRatingChange))/1000;
                        }
                    }
                    toAdd += (int) toAdd* par.getRecognition();
                    if(toAdd>0){
                        toAdd-=(int) toAdd*par.getFatigue();
                    }else{
                        toAdd+=(int) toAdd*par.getFatigue();
                    }
                    int pctginAccept = ((par.getPercent()+1)*100)/(acceptables.get(gro)+1);
                    toAdd = (toAdd*pctginAccept)/100;
                    toAdd = IdeoCheck(toAdd, par);
                    if(toAdd<0){
                        toAdd =0;
                    }
                    par.addVotes(toAdd/(ra.nextInt(4)+1));
                    //par.addVotes(toAdd);
                    par.recordVotes(gro,toAdd);
                }
            }
            int satischange = -1*(5-(maxProximity/20));
            if(!hasvoted){
                satischange-=ra.nextInt(10);
            }
            satischange+= ra.nextInt(3)-ra.nextInt(3);
            gro.updateSatisfaction(satischange);
            
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
            
            System.out.print(par.getName()+ par.ideoDisplay()+" ["+((par.getScore()*100)/totvotes)+"%] | ");
        }
        }
        
        for(Party par: candidates){
            par.incrementRecognition();
        }
        President = winner;
        President.incrementRecognition();
        System.out.println("\n===============\nElected President: "+ President.getColor()+President.getName()+RESET+" "+ President.ideoDisplay());
        
    }
    
    public static int IdeoCheck(int toAdd, Party par){
        if(lean == "Republic"){
                        if(par.getIdeology()>35 && par.getIdeology()< 65){
                            toAdd+= toAdd/2;
                        }else{
                            toAdd/=2;
                        }
                    }else if(lean == "Reaction"){
                        if(par.getIdeology()>35 && par.getIdeology()< 65){
                            toAdd/=2;
                        }else{
                            if(par.getIdeology()<35){
                                toAdd += toAdd/2;
                            }else{
                                toAdd/=2;
                            }
                        }
                    }else{
                        if(par.getIdeology()>35 && par.getIdeology()< 65){
                            toAdd/=2;
                        }else{
                            if(par.getIdeology()<35){
                                toAdd/=2;
                            }else{
                                toAdd += toAdd/2;
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
                potentialPartners.sort(Comparator.comparingInt(p -> 
                Math.abs(p.getIdeology() - winner.getIdeology())
                ));
                int down = 0;
                for(Party par: potentialPartners){
                    if(totalSeats>50){ got50 = true; break;}
                    int tresh = 50+(par.getPercent()/2);
                    tresh += Math.abs(par.getIdeology()-50)/4;
                    tresh += Math.abs(winner.getIdeology()-50)/4;
                    tresh -= down*3;
                    tresh += par.getPercent()/5;
                    
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
                System.out.println("Government formed by "+ rulingCoalition.getLeader().getName());
                System.out.println("Seats held by Government: "+ totGovSeats+"%");
        for(Party par: rulingCoalition.getMemberList()){
            if(par.getPercent()>0){
                
                
            System.out.print(par.getColor()+"o"+ RESET+ " - "+ par.getName() + par.ideoDisplay()+ " ["+par.getPercent()+"%]");
            
            if(par == rulingCoalition.getLeader()){
                System.out.println(" - Leader");
            }else{
                System.out.println();
            }
            
            par.incrementRecognition();
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
        
        LOTO = maxpar;
        if(LOTO !=null){
            System.out.println("\nLargest Opposition Party: "+ LOTO.getColor()+"o"+RESET+" - "+ LOTO.getName()+LOTO.ideoDisplay()+" ["+ LOTO.getPercent()+"%]");
            LOTO.incrementRecognition();
        }else{
            System.out.println("\nLargest Opposition Party: None");
        }
        for(Party par: allParties){
            if(!rulingCoalition.containsParty(par)){
                par.decreaseFatigue();
            }else{
                par.addFatigue();
            }
            if(par == rulingCoalition.getLeader()){
                par.addFatigue();
            }
        }
        
        
    }
    public static Party LOTO;
    
    public static void checkForNewParties() {
    for (ideoGroup gro : allGroups) {
        
        if (gro.getSatisfaction() < 30) {
            
            
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
    int partyId = ra.nextInt(100);
    
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

    int variance = (partyId * 12345) % (ra.nextInt(100)+1); 
    
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
    for(Party par: allParties){
        if(par.getPercent()<5){
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
       
        switch(ra.nextInt(7)){
            case 0:
                System.out.println("Economic Crisis!");
            for(ideoGroup gro : allGroups){
                if(gro.getIdeology()> 80 || gro.getIdeology()< 20){
                    gro.updateSize(ra.nextInt((gro.getSize()/10)+1));
                    approvalRatingChange -= ra.nextInt(5);
                    gro.updateSatisfaction(-1*ra.nextInt(25));
                }
                radicalizeVoters();
            }
                break;
            case 1:
                System.out.println("Economic Boom!");
            for(ideoGroup gro : allGroups){
                if(gro.getIdeology()< 80 || gro.getIdeology()> 20){
                    gro.updateSize(ra.nextInt((gro.getSize()/10)+1));
                    approvalRatingChange += ra.nextInt(5);
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
            par.setRecog(par.getRecognition() * -1.5); 
        } else {
            
            par.setRecog(0.5); 
        }
        
        
    }
   
    for(Party member : rulingCoalition.getMemberList()) {
        for(ideoGroup gro : allGroups) {
            if(gro.proximityWith(member) > 70) {
                gro.updateSatisfaction(-10);
            }
        }
    }
    
    for(ideoGroup gro: allGroups){
        gro.updateSatisfaction(-2);
    }
        }
        
            break;
            
            case 5:
                    Party targetpar = allParties.get(ra.nextInt(allParties.size()));
                    System.out.println("Political Scandal in "+ targetpar.getName()+ "!");
                    for(int i=0; i<targetpar.getPercent()/10;i++){
                        targetpar.addFatigue();
                    }
                    targetpar.setRecog(targetpar.getRecognition()-(targetpar.getPercent()/10));
                    
                    for(ideoGroup gro : allGroups){
                        if(gro.proximityWith(targetpar)>85){
                            gro.updateSatisfaction(-50);
                        }
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
            
            ideoGroup target;
            
            if (gro.getIdeology() > 50) {
                target = findClosestGroup(gro.getIdeology() - 15);
            } else {
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
            if (gro.getIdeology() > 50) {
                target = findClosestGroup(gro.getIdeology() + 15);
            } else {
                target = findClosestGroup(gro.getIdeology() - 15);
            }
            if (target != null) target.updateSize(defectors);
        }
    }
}
    
    public static void updateTick(){
        events();
        checkFails();
        updateGroupSize();
        radicalizeVoters();
        checkForNewParties();
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
    
    
    
    
    public static String getDynamicColor(int ideo) {
    int colorCode;
    String ideoname;
    // RIGHT-WING: Blue/Navy spectrum
    if (ideo < 20){ colorCode = 18; ideoname = "Far-Right";       // Navy Blue (Reactionary/Far-Right)
    }else if (ideo < 35){ colorCode = 27; ideoname= "Right-Wing";  // Royal Blue (Conservative)
    
    // CENTER: Yellow/Gold/Orange spectrum
    }else if (ideo < 45){ colorCode = 214; ideoname = "Center-Right"; // Orange-Yellow (Liberal/Center-Right)
    }else if (ideo < 55){ colorCode = 226; ideoname = "Centrist"; // Bright Yellow (Pure Centrist)
    }else if (ideo < 65){ colorCode = 203; ideoname = "Center-Left";// Light Red (Center-Left/Green)
    
    // LEFT-WING: Red/Crimson spectrum
    //else if (ideo < 80) colorCode = 203; // Light Red (Social Democrat)
    }else if (ideo < 80){ colorCode = 196; ideoname = "Left-Wing"; // Pure Red (Socialist)
    }else{ colorCode = 88; ideoname = "Far-Left";                // Dark Crimson (Communist/Far-Left)
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
	    
		
		int interval  =4;
		int electionsToSimulate = 44;
		
		for(int i=0; i<electionsToSimulate;i++){
		    System.out.println(year+ "=========================");
		    election();
		    electLeadParty();
		    electPresident();
		    
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
    System.out.println("Spectrum: [R] " + String.valueOf(spectrum) + " [L]");
		    for(ideoGroup gro : allGroups){
		        //System.out.println(gro.getName()+ " "+ gro.getSize() + " "+ gro.getSatisfaction());
		    }
		    double totalRecog = 0;
for(Party p : allParties) totalRecog += p.getRecognition();
System.out.println("Establishment Strength: " + String.format("%.2f", totalRecog));
nationalLean();
seeDominant();
		    sc.nextLine();
		    updateTick();
		    year+=interval;
		    
		}
		
	}
}
