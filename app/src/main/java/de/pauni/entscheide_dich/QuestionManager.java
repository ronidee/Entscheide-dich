package de.pauni.entscheide_dich;


import java.util.concurrent.ThreadLocalRandom;
import java.util.Arrays;
import java.util.List;

/**
 *
 * Der QuestionManager ist die Schnittstelle zwischen der MainActivity und dem DatabaseHelper
 * Hier werden die Frage-Objekte bereitgestellt und automatisch der nächste Eintrag
 * aus der Datenbank geholt.
 * Außerdem wird der Fragenverlauf (laufzeitlang) protokolliert, damit man in den Fragen
 * auch zurückgehen kann
 *
 */

public class QuestionManager {
    private List<int[]> previousQuestions;

    private static String youtubeLink;

    public QuestionManager() {
        //I'm only using the constructor to init things. Bad habit?
        favoritenMarkieren();
    }

    Question getCurrent() {
        youtubeLink = youtubeLinks[SharedPrefs.getListNumber()-1];
        return getList(SharedPrefs.getListNumber()).get(SharedPrefs.getEntryNumber());
    }
    Question getNext() {
        int entryNumber = SharedPrefs.getEntryNumber();
        int listNumber  = SharedPrefs.getListNumber();
        List<Question> aktuelleListe = getList(listNumber);

        //Wählt die jeweilige Frage der jeweiligen Liste aus.
        //Ist eine Liste durchgearbeitet, wird mit der nächsten Liste begonnen.
        //Sind alle Listen durchgearbeitet, wird mit Liste 1 weitergemacht.
        if (entryNumber < aktuelleListe.size()-1) {
            SharedPrefs.saveEntryNumber(entryNumber+1);
        } else if(listNumber <14) {
            SharedPrefs.saveEntryNumber(0);
            SharedPrefs.saveListNumber(listNumber+1);
            aktuelleListe = getList(SharedPrefs.getListNumber());
        } else {
            SharedPrefs.saveEntryNumber(0);
            SharedPrefs.saveListNumber(1);
            aktuelleListe = getList(SharedPrefs.getListNumber());
        }

        //update the youtubelink with the variable, as sharedprefs are +1 ahead when lst changes

        youtubeLink = youtubeLinks[SharedPrefs.getListNumber()-1];
        return aktuelleListe.get(SharedPrefs.getEntryNumber());
    }
    Question getPrevious() {
        int entryNumber = SharedPrefs.getEntryNumber();
        int listNumber  = SharedPrefs.getListNumber();
        List<Question> aktuelleListe   = getList(listNumber);

        if(entryNumber != 0) {
            SharedPrefs.saveEntryNumber(entryNumber-1);
        } else if (listNumber != 1) {
            //verringere die Listnummer und aktualisiere die aktuelleListe
            SharedPrefs.saveListNumber(listNumber-1);
            aktuelleListe = getList(SharedPrefs.getListNumber());
            //EntryNumber auf die Größe der neuen Liste(-1, da inkl. 0)
            SharedPrefs.saveEntryNumber(aktuelleListe.size()-1);
        } else {
            //die letzte Liste auswählen
            SharedPrefs.saveListNumber(14);
            aktuelleListe = getList(SharedPrefs.getListNumber());
            SharedPrefs.saveEntryNumber(aktuelleListe.size()-1);
        }
        youtubeLink = youtubeLinks[SharedPrefs.getListNumber()-1];
        return aktuelleListe.get(SharedPrefs.getEntryNumber());


    }
    Question getRandom() {
        int[] listSizes = {
                0,
                fragenListe1.size(),
                fragenListe2.size(),
                fragenListe3.size(),
                fragenListe4.size(),
                fragenListe5.size(),
                fragenListe6.size(),
                fragenListe7.size(),
                fragenListe8.size(),
                fragenListe9.size(),
                fragenListe10.size(),
                fragenListe11.size(),
                fragenListe12.size(),
                fragenListe13.size(),
                fragenListe14.size(),
        };

        int randomListNumber    = ThreadLocalRandom.current().nextInt(1, 14 + 1);
        //generating a random number in the range of the respective list.
        //without +1 because lists are 0 inclusive.
        int randomEntryNumber   = ThreadLocalRandom.current().nextInt(0, listSizes[randomListNumber]);

        //storing the new question-parameters and calling getNextQuestion. Doesn't matter, since random
        SharedPrefs.saveEntryNumber(randomEntryNumber);
        SharedPrefs.saveListNumber(randomListNumber);


        youtubeLink = youtubeLinks[SharedPrefs.getListNumber()-1];
        return getNext();
    }

    String   getYTLink() {
        return youtubeLink;
    }

    void     setFavorite(boolean favorite) {
        getCurrent().setFavorit(favorite);

        if (favorite) {
            SharedPrefs.addFavorite();
        } else {
            SharedPrefs.removeFavorite();
        }
    }

    private List<Question> getList(int number) {
        List<Question> list = null;
        switch (number) {
            case 1 : list = fragenListe1;  break;
            case 2 : list = fragenListe2;  break;
            case 3 : list = fragenListe3;  break;
            case 4 : list = fragenListe4;  break;
            case 5 : list = fragenListe5;  break;
            case 6 : list = fragenListe6;  break;
            case 7 : list = fragenListe7;  break;
            case 8 : list = fragenListe8;  break;
            case 9 : list = fragenListe9;  break;
            case 10: list = fragenListe10; break;
            case 11: list = fragenListe11; break;
            case 12: list = fragenListe12; break;
            case 13: list = fragenListe13; break;
            case 14: list = fragenListe14; break;
        }
        return list;
    }

    private void favoritenMarkieren() {
        int[][] ax = SharedPrefs.getFavorites();

        if (ax == null) {
            return;
        }

        int[] favListNumbers     = ax[0];
        int[] favEntryNumbers    = ax[1];

        for(int i=0; i<favListNumbers.length; i++) {
            //die entspr. Frageliste holen, davon die entspr. Frage holen, Frage als Fav markieren
            getList(favListNumbers[i]).get(favEntryNumbers[i]).setFavorit(true);
        }

    }









    /*
    *   HIER WERDEN ALLE FRAGEN GESPEICHERT
    *   FÜR JEDE SENDUNG WIRD EINE LISTE VOM TYP FRAGE ERSTELLT
    *   JEDE FRAGE ENTHÄLT DEN FRAGETEXT UND DEN NAMEN DES GASTES
    */



    //Sendung mit Schulz&Böhmermann
    private final String sundb          = "Schulz&Böhmermann";
    private List<Question> fragenListe1    = Arrays.asList(
            new Question("Hättest Du lieber Ratten in der Badewanne oder eine Dauerbaustelle auf dem Dach?",                   sundb),
            new Question("Hättest Du lieber einen Abszess am Po oder einen Prozess am Arsch?",                                 sundb),
            new Question("Wärst Du lieber eine Blattlaus mit Sacklaus oder ein Flusskrebs mit Brustkrebs?",                    sundb),
            new Question("Wärst Du lieber ohne Haftbefehl im türkischen Knast oder mit Haftbefehl in einer türkischen Sauna?", sundb),
            new Question("Hättest Du lieber acht Arme oder drei Füße?",                                                        sundb),
            new Question("Würdest Du lieber mit Geert Wilders wildern oder mit Marie Le Pens penn'?",                          sundb)
    );

    //Sendung mit Kollegah
    private final String kollegah       = "Kollegah";
    private List<Question> fragenListe2    = Arrays.asList(
            new Question("Wärst Du lieber die Kellerassel von Josef Fritzl oder der Ohrwurm von Niki Lauda?",          kollegah),
            new Question("Hättest Du lieber einen heftigen Computervirus von einer Ich-Email " +
                    "oder einen leichten Erkältungsvirus von einer Shemale?",                                           kollegah),
            new Question("Hättest Du lieber Blümchensex zur Musik von Fler oder Sex mit Fler zur Musik von Blümchen?", kollegah),
            new Question("Hättest Du lieber ein hämmerndes Hämatom oder ein funkelndes Furunkel?",                     kollegah),
            new Question("Hättest Du lieber eine Möhre im Anus oder eine Kartoffel als Kopf?",                         kollegah),
            new Question("Israel oder Palästina?",                                                                     kollegah),
            new Question("Würdest Du lieber Moslems die Einreise in die Vereinigten Staaten erlauben " +
                    "oder Chemtrails verbieten?",                                                                       kollegah),
            new Question("Würdest Du lieber Kurtis blowen oder Farid bangen?",                                         kollegah)
    );

    //Sendung mit Christopher Lauer
    private final String lauer          = "Christopher Lauer";
    private List<Question> fragenListe3    = Arrays.asList(
            new Question("Hättest Du lieber einen Flachmann von Lutz Bachmann oder Bauland neben Gauland?",        lauer),
            new Question("Wärst Du lieber der Koch von Michael Schumacher oder der Schuhmacher von Samuel Koch?",  lauer),
            new Question("Hättest Du lieber, dass Dir Klaus über die Leber oder eine Laus über den Kleber läuft?", lauer),
            new Question("Wärst Du lieber ein hässlicher und sehr unbeliebter Innensenator " +
                    "oder ein schöner und sehr beliebter Hausmeister?",                                             lauer),
            new Question("Würdest Du lieber im Knast die Seife fallen lassen oder im Bundestag Dein Crystal Meth?",lauer),
            new Question("Hättest Du lieber deine letzte Sackkarre mit 44 oder Deine ersten Sackhaare mit 24?",    lauer),
            new Question("Hättest Du lieber einen Sohn mit Barbara Hendricks der Jimi heißt " +
                    "oder eine Tochter mit Claudia Roth die Alarmstufe heißt?",                                 lauer),
            new Question("Hättest Du lieber ein Gedächtnis von sieben Sekunden " +
                    "oder einen Ohrwurm von 7 seconds?",                                                        lauer),
            new Question("Würdest Du lieber der politischen Karriere wegen öffentlich Sigmar Gabriel loben " +
                    "oder als homosexueller Nationalspieler in einer Scheinehe leben?",                         lauer)
    );

    //Sendung mit Klaas Heufer-Umlauf
    private final String klaas          = "Klaas Heufer-Umlauf";
    private List<Question> fragenListe4    = Arrays.asList(
            new Question("Wärst Du lieber eine Meerjungfrau mit dem Oberkörper eines Fisches " +
                    "oder ein Zentaur mit dem Unterkörper eines Menschen?",                             klaas),
            new Question("Hättest Du lieber schlecht pedikürte Hufe oder einen hübschen Pavianhintern?",   klaas),
            new Question("Hättest Du lieber eine unangenehm anstrengende Lache " +
                    "oder dass jemand auf Twitter Deine private Handynummer veröffentlicht?",           klaas),
            new Question("Hättest Du lieber ein Duett mit Oli P oder einen Dreier mit Olli Schulz?",       klaas),
            new Question("Würdest Du lieber bei einem leichten Erdbeben von der Bettkante auf einen Kaktus fallen " +
                    "oder bei einem schweren Hausbrand aus dem dritten Stock auf Peter Altmaier?",      klaas),
            new Question("Hättest Du lieber eine sehr schlechte Sicht auf das WM-Finale " +
                    "oder eine sehr gute Sicht auf Klöten-Ralles Klöten?", klaas)

    );

    //Sendung mit Wolfang Bosbach
    private final String bosbach        = "Wolfang Bosbach";
    private List<Question> fragenListe5    = Arrays.asList(
            new Question("Hättest Du lieber am helllichten Tag auf offener Straße Geschlechtsverkehr " +
                    "oder mitten in der Nacht im geschlossenen Schlafzimmer Berufsverkehr?",        bosbach),
            new Question("Würdest Du lieber von Haftbefehl gesucht werden oder von Xatar gefunden?",   bosbach),
            new Question("Hättest Du lieber die Mähne eines hässlichen Löwen " +
                    "oder den Rüssel eines äußerst attraktiven Elefanten?",                         bosbach),
            new Question("Würdest Du lieber zehn Stunden im Gulli " +
                    "oder zehn Minuten in einem Fahrstuhl mit Oliver Pocher feststecken?",          bosbach),
            new Question("Würdest Du lieber mit Rainer Calmund Kanu fahren " +
                    "oder mit Christoph Daum einen Schneemann bauen?",                              bosbach),
            new Question("Würdest Du lieber in einer Burka durch Dresden joggen " +
                    "oder in einem Mankini auf einem Einrad durch den Bundestag radeln?",           bosbach)
    );

    //Sendung mit Gregor Gysi
    private final String gysi   = "Gregor Gysi";
    private List<Question> fragenListe6 = Arrays.asList(
            new Question("Würdest Du den Weltfrieden schaffen, wenn Du dafür lebenslang im Körper " +
                    "eines nassen Otters gefangen wärst?",                                  gysi),
            new Question("Würdest Du den Sexismus für immer beenden, wenn Du Dir dafür eine nackte " +
                    "Erika Steinbach auf die Brust tätowieren müsstest?",                   gysi),
            new Question("Würdest Du die Altersarmut abschaffen, wenn Du dafür " +
                    "immer die Frisur von Sahra Wagenknecht tragen müsstest?",              gysi),
            new Question("Würdest Du das Flüchtlingsproblem lösen, wenn Du dafür " +
                    "Dein Leben lang in einem IKEA wohnen müsstest?",                       gysi),
            new Question("Würdest Du die Landminen abschaffen, wenn Du dafür wöchentlich " +
                    "Heavy Petting mit Angela Merkel haben müsstest?",                      gysi),
            new Question("Würdest Du die Welt vom Heuschnupfen befreien, wenn Du dafür " +
                    "bei jedem öffentlichen Auftritt eine gut sichtbare Erektion hättest?", gysi)
    );

    //Sendung mit Mike Krüger
    private final String krüger   = "Mike Krüger";
    private List<Question> fragenListe7 = Arrays.asList(
            new Question("Hätttest Du lieber eine große Nase oder einen großen Penis?",                            krüger),
            new Question("Wärst Du lieber der Vater von Miley Cyrus oder Tochter von Boris Becker?",               krüger),
            new Question("Würdest Du lieber Schnick Schack Schnuck mit einem Hummer oder Strip-Poker " +
                    "mit einem Nacktmull spielen?",                                                             krüger),
            new Question("Hättest Du lieber einen gutartigen Tumor oder einen bösartigen Hamster?",                krüger),
            new Question("Hättest Du lieber ein Display auf deiner Stirn, auf dem Deine Gedanken stehen " +
                    "oder Dein Leben lang ein Stück Zwiebel im Mund?",                                          krüger),
            new Question("Hättest Du lieber Dein Leben lang eine grüne Spandex Hose an oder keine Beine mehr?",    krüger),
            new Question("Hätetst Du lieber den Geruch von Thomas Gottschalk in der Nase " +
                    "oder den Geschmack von Thomas Gottschalk im Schrank?",                                     krüger),
            new Question("Hättest Du lieber einen Ohrwurm von Satellit oder einen Bandwurm?",                       krüger)
    );

    //Sendung mit Olli Schulz
    private final String olli   = "Olli Schulz";
    private List<Question> fragenListe8 = Arrays.asList(
            new Question("Wärst Du lieber ein Ausländer im Fanvon Dynamo Dresden " +
                    "oder ein Schwuler auf einem dordmunder Autobahnparkplatz?",                                                olli),
            new Question("Würdest Du lieber jeden Tag Dein Leben lang nur einen Song von Morrissey " +
                    "oder jeden Tag ein neues Album von Lena Meyer-Landrut?",                                                   olli),
            new Question("Wärst Du lieber im Vorspann von Circus Halligalli oder nicht?",                                          olli),
            new Question("Wärst Du lieber mit Sebastian Edathy beim Kinderturnen oder mit Bill Cosby im Schlaflabor?",             olli),
            new Question("Würdest Du lieber Dinge zu Ende bringen können oder ... ?",                                              olli),
            new Question("Hättest Du lieber einen Toaster als einzigen Freund oder einen starken Bieber als Erzfreind?",           olli),
            new Question("Hättest Du lieber ein unerreichbares Jucken am Rücken oder eine fußläufig erreichbare Schwiegermutter?", olli)
    );

    //Sendung mit Dr. Mark Benecke
    private final String benecke = "Dr. Mark Benecke";
    private List<Question> fragenListe9 = Arrays.asList(
            new Question("Hättest Du lieber einen bedenklichen Auftritt beim Roten Sofa oder unbedenklichen roten Stuhl?",             benecke),
            new Question("Würdest Du lieber ein Kaugummi von Rainer Calmund weiterkauen oder mit Tempo 10 gegen einen Baum fahren?",   benecke),
            new Question("Wärst Du lieber ein Schwarzer in den USA oder ein Maikäfer im Juni?",                                        benecke),
            new Question("Würdest Du lieber eine Kuh mit dem Mund melken oder einen Ochsen mit der Hand?",                             benecke),
            new Question("Hättest Du lieber eine Wurstbude mit Conchita Wurst oder einen Lieferdienst mit Jan Josef Liefers?",         benecke),
            new Question("Würdest Du dich lieber von einem Hammerhai nageln lassen oder von einer Vogelspinne vögeln?",                benecke),
            new Question("Hättest Du lieber ein Speeddate mit Sandra Bullock oder ein Blinddate mit Stevie Wonder?",                   benecke),
            new Question("Würdest Du lieber schwimmen mit Franzi van Almsick Uno mit dem UNO-Generalsekretär?",                        benecke),
            new Question("Hättest Du lieber einen Damenbart oder Herrentittchen?",                                                     benecke),
            new Question("Hättest Du lieber ein Kind das Frei.Wild hört oder ein Wildschwein, das Kinder beißt",                       benecke),
            new Question("", benecke),
            new Question("", benecke)
    );

    //Sendung Farin Urlaub
    private final String farin = "Farin Urlaub";
    private List<Question> fragenListe10 = Arrays.asList(
            new Question("Hättest Du lieber einen Affen und ein Pferd zu Hause oder einen Propeller auf dem Kopf?",farin),
            new Question("Hättest Du lieber einen sehr kurzen Penis mit einem coolen Tribal " +
                    "oder ein Gesichtstattoo mit einem Rechtschreibfehler?",                                    farin),
            new Question("Würdest Du lieber in einer Talkshow auftreten in der geraucht und getrunken werden darf " +
                    "oder für Bob Geldof einen Song organisieren um Geld für Afrika zu sammeln?",               farin),
            new Question("Hättest Du lieber, dass ständig jemand vor Dir hergeht, der dich lautstark " +
                    "ankündigt oder einen stinkenden Bauchnabel?",                                              farin)
    );

    //Sendung Domian
    private final String domain = "Jürgen Domian";
    private List<Question> fragenListe11 = Arrays.asList(
            new Question("Hättest Du lieber einen Parka von Kanye West oder eine Weste von Peter Parker?",                     domain),
            new Question("Wärst Du lieber ein sehr schlechter Fußball-Profi oder ein hoch angesehener Eiskunstläufer?",        domain),
            new Question("Hättest Du lieber eine Maultasche von Louis Vuitton oder eine Gürtelrose von Dolce&Gabbana?",        domain),
            new Question("Würdest Du lieber mit Sebastian Edathy eine Toys\"R\"Us Filiale eröffnen " +
                    "oder einen Stripclub mit Jack the Ripper?",                                                            domain),
            new Question("Würdest Du lieber deine Festplatte korrekt auswerfen oder Deine Sackhaare sicher entfernen?",        domain),
            new Question("Hättest Du lieber eine leichte Erkältung, die sich gewaschen hat " +
                    "oder eine schwere Sexualpartnerin, die sich nicht gewaschen hat?",                                     domain),
            new Question("Würdest Du lieber Schäuble gegen's Schienbein treten oder einem süßen Ponny in's Gesicht schlagen?", domain)
    );

    //Sendung mit Roger Willemsen
    private final String willemsen = "Roger Willemsen";
    private List<Question> fragenListe12 = Arrays.asList(
            new Question("Würdest Du lieber eine griesgrämig Robbe kloppen oder einen süß guckenden Hitler streicheln?",       willemsen),
            new Question("Hättest Du lieber zwanzig Finger oder zwei Nasen?",                                                  willemsen),
            new Question("Wärst Du lieber ein heißer Urlaubsflirt von Elke Heidenreich " +
                    "oder ein unwichtiger One-Night-Stand von Hellmuth Karasek?",                                           willemsen),
            new Question("Hättest Du lieber einen Gürtel aus einem Murmeltier oder eine Murmel aus einem Gürteltier?",         willemsen),
            new Question("Würdest Du lieber mit Monica Lierhaus Mikado spielen oder mit Michael J. Fox ein Kartenhaus bauen?", willemsen),
            new Question("Hätetst Du lieber dass der Tod Dich scheidet oder eine Scheide Dich tötet?",                         willemsen),
            new Question("Würdest Du lieber mit einem Pferd verstecken spielen oder mit einem Chamäleon fangen?",              willemsen),
            new Question("Hättest Du lieber einen zurückhaltenden Stalker oder einen aufdringlichen Berner Sennenhund?",       willemsen),
            new Question("Würdest Du lieber eine nette Kuh schlachten oder ein böses Pferd?",                                  willemsen),
            new Question("Würdest Du bei Gefahr lieber Tinte aus Deiner Nase absondern oder Deinen Schwanz absondern?",        willemsen),
            new Question("Hättest Du lieber das Gehirn von Til Schweiger oder das Aussehen von Stephen Hawking?",              willemsen),
            new Question("Würdest Du lieber mit Reiner Calmund Tretboot oder mit Oscar Pistorius Tandem fahren?",              willemsen)
    );

    //Sendung mit Herbert Feuerstein
    private final String feuerstein = "Herbert Feuerstein";
    private List<Question> fragenListe13 = Arrays.asList(
            new Question("Hätten Du lieber einen eingewachsenen Zehennagel oder einen ausgewachsenen Buckelwal?",                              feuerstein),
            new Question("Wärst Du lieber der Gynäkologe von Charlotte Roche oder der Proktologe von Harald Schmidt?",                         feuerstein),
            new Question("Würdest Du lieber mit einem Pedalo durch den Himmel fahren oder mit einem Segway durch die Hölle?",                  feuerstein),
            new Question("Hießt Du lieber Fickie Mac Bumsfick oder hättest Du lieber Dein Leben lang ein Stück Popcorn in der Backentasche?",  feuerstein),
            new Question("Hättest Du lieber einen Röntgenblick bei adipösen Frauen oder einen tödlichen Laserblick kurz vor'm Orgasmus?",      feuerstein),
            new Question("Würdest Du Dir eher für immer die Zähne mit Senf putzen oder den Po mit Schleifpapier abwischen?",                   feuerstein),
            new Question("Hättest Du lieber, dass schlechtes Bier aus Deinen Brustwarzen kommt oder guter Rotwein aus Deinem Penis?",          feuerstein)
    );

    //Sendung mit Katrin Bauerfeind
    private final String katrin = "Katrin Bauerfeind";
    private List<Question> fragenListe14 = Arrays.asList(
            new Question("Hättest Du lieber eine dritte Brust oder einen starken sächsischen Dialekt?",                                 katrin),
            new Question("Würdest Du lieber einen bösen Hitler umbringen oder mit einem netten Hitler einen schönen Abend verbringen?", katrin),
            new Question("Hättest Du lieber einen Penis oder fünf Penisse?",                                                            katrin),
            new Question("Hättest Du lieber Reinhold Messners Ohren oder Niki Laudas Füße?",                                            katrin),
            new Question("Würdest Du lieber RTL von innen kaputt machen oder Pro7 von außen?",                                          katrin),
            new Question("Würdest Du lieber einen Hoden am Kinn tragen oder ein wohlhabendes Opossum heiraten?",                        katrin),
            new Question("Wärst Du lieber die Frau von Helmut Kohl oder der Schlüpfer von Olivia Jones?",                               katrin),
            new Question("Würdest Du lieber eine Sendung mit Kraftklub gucken oder eine ohne Kraftklub?",                               katrin),
            new Question("Wärst Du lieber die Frau von Helmut Kohl oder der Schlüpfer von Olivia Jones?",                               katrin)
    );

    static String[] youtubeLinks = {
            "https://www.youtube.com/watch?v=gFNPBoxNll4&index=1&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
            "https://www.youtube.com/watch?v=llEc4KrHYOc&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM&index=2",
            "https://www.youtube.com/watch?v=FeWwabdrRZg&index=3&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
            "https://www.youtube.com/watch?v=GQsEdwfsHw8&index=4&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
            "https://www.youtube.com/watch?v=GO9j9UBrvHQ&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM&index=5",
            "https://www.youtube.com/watch?v=_4r2tKmMND0&index=6&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
            "https://www.youtube.com/watch?v=21hMMygYeGc&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM&index=7",
            "https://www.youtube.com/watch?v=RHmYTNEoYzw&index=8&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
            "https://www.youtube.com/watch?v=VXWAhOnBOK8&index=9&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
            "https://www.youtube.com/watch?v=nZJ-ryXLavg&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM&index=10",
            "https://www.youtube.com/watch?v=aj24kGMA-0M&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM&index=11",
            "https://www.youtube.com/watch?v=Sf-U9I9Qvm0&index=12&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
            "https://www.youtube.com/watch?v=c3sy4cC8FCE&index=13&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
            "https://www.youtube.com/watch?v=p747P2pG2fs&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM&index=14",
    };
}
