package de.pauni.entscheide_dich;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Roni on 13.03.2017.
 */

public class DatabaseInitializer {

    public DatabaseInitializer(Context c) {

        DatabaseHelper db = new DatabaseHelper(c);

        SystemClock.sleep(200); // Gib ihm Zeit...

        for(int i=14; i>=1; i--) {
            String[] questions = getList(i);

            for (int j=0; j<questions.length; j++) {
                db.addQuestion(new Question());
            }
            Log.d("DatabaseInit>>>", "Liste: " + String.valueOf(15-i));

        }

    }


    private String[] getList(int number) {
        switch (number) {
            case 1 : return questions1;
            case 2 : return questions2;
            case 3 : return questions3;
            case 4 : return questions4;
            case 5 : return questions5;
            case 6 : return questions6;
            case 7 : return questions7;
            case 8 : return questions8;
            case 9 : return questions9;
            case 10: return questions10;
            case 11: return questions11;
            case 12: return questions12;
            case 13: return questions13;
            case 14: return questions14;
        }
        return null;
    }


    void load_init_data() {
        // TODO: Load all the question from a json file
        // do what do you want on your interface



        try {
            File yourFile = new File("path/to/the/file/inside_the_sdcard/textarabics.txt");

            FileInputStream stream = new FileInputStream(yourFile);
            String jsonStr = null;
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                jsonStr = Charset.defaultCharset().decode(bb).toString();
            }
            catch(Exception e){
                e.printStackTrace();
            }
            finally {
                stream.close();
            }




            /*
            {
                "data": [
                    {
                        "id": "1",
                        "title": "Farhan Shah",
                       "duration": 10
                    },
                    {
                        "id": "2",
                        "title": "Noman Shah",
                        "duration": 10
                    }
                ]
            }
            */



            JSONArray all_questions = new JSONArray(jsonStr);


            // looping through All nodes
            for (int i = 0; i < all_questions.length(); i++) {
                JSONObject c = all_questions.getJSONObject(i);



                /*
                String id = c.getString("id");
                String title = c.getString("title");
                String duration = c.getString("duration");
                //use >  int id = c.getInt("duration"); if you want get an int


                // tmp hashmap for single node
                HashMap<String, String> parsedData = new HashMap<String, String>();

                // adding each child node to HashMap key => value
                parsedData.put("id", id);
                parsedData.put("title", title);
                parsedData.put("duration", duration);


                // do what do you want on your interface

                */
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


















    }





    /*
    *   Alle Fragen, Gäste, Youtubelinks
    *   und Infos werden hier gesammelt
    */

    // ältester Gast zuerst
    String[] guests =
            {
                    "Katrin Bauerfeind",
                    "Herbert Feuerstein",
                    "Roger Willemsen",
                    "Jürgen Domian",
                    "Farin Urlaub",
                    "Dr. Mark Benecke",
                    "Olli Schulz",
                    "Mike Krüger",
                    "Gregor Gysi",
                    "Wolfang Bosbach",
                    "Klaas Heufer-Umlauf",
                    "Christopher Lauer",
                    "Kollegah",
                    "Schulz&Böhmermann"
            };


    // ältester Link zuerst
    String[] youtubeLinks = {
            "https://www.youtube.com/watch?v=p747P2pG2fs&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM&index=14",
            "https://www.youtube.com/watch?v=c3sy4cC8FCE&index=13&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
            "https://www.youtube.com/watch?v=Sf-U9I9Qvm0&index=12&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
            "https://www.youtube.com/watch?v=aj24kGMA-0M&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM&index=11",
            "https://www.youtube.com/watch?v=nZJ-ryXLavg&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM&index=10",
            "https://www.youtube.com/watch?v=VXWAhOnBOK8&index=9&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
            "https://www.youtube.com/watch?v=RHmYTNEoYzw&index=8&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
            "https://www.youtube.com/watch?v=21hMMygYeGc&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM&index=7",
            "https://www.youtube.com/watch?v=_4r2tKmMND0&index=6&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
            "https://www.youtube.com/watch?v=GO9j9UBrvHQ&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM&index=5",
            "https://www.youtube.com/watch?v=GQsEdwfsHw8&index=4&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
            "https://www.youtube.com/watch?v=FeWwabdrRZg&index=3&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
            "https://www.youtube.com/watch?v=llEc4KrHYOc&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM&index=2",
            "https://www.youtube.com/watch?v=gFNPBoxNll4&index=1&list=PLHeo4sayeLKrAtRmFIGBCwArkfWw7Z-nM",
    };



    //Sendung mit Schulz&Böhmermann
    private String[] questions14 =
            {
                    "Hättest Du lieber Ratten in der Badewanne oder eine Dauerbaustelle auf dem Dach?",
                    "Hättest Du lieber einen Abszess am Po oder einen Prozess am Arsch?",
                    "Wärst Du lieber eine Blattlaus mit Sacklaus oder ein Flusskrebs mit Brustkrebs?",
                    "Wärst Du lieber ohne Haftbefehl im türkischen Knast oder mit Haftbefehl in einer türkischen Sauna?",
                    "Hättest Du lieber acht Arme oder drei Füße?",
                    "Würdest Du lieber mit Geert Wilders wildern oder mit Marie Le Pens penn'?"
            };
    String[] info14 =
            {
                    null,
                    null,
                    null,
                    null,
                    null,
                    "Geert Wilders,Marie Le Pens"
            };


    // Sendung mit Kollegah
    private String[] questions13 =
            {
                    "Wärst Du lieber die Kellerassel von Josef Fritzl oder der Ohrwurm von Niki Lauda?",
                    "Hättest Du lieber einen heftigen Computervirus von einer Ich-Email " +
                            "oder einen leichten Erkältungsvirus von einer Shemale?",
                    "Hättest Du lieber Blümchensex zur Musik von Fler oder Sex mit Fler zur Musik von Blümchen?",
                    "Hättest Du lieber ein hämmerndes Hämatom oder ein funkelndes Furunkel?",
                    "Hättest Du lieber eine Möhre im Anus oder eine Kartoffel als Kopf?",
                    "Israel oder Palästina?",
                    "Würdest Du lieber Moslems die Einreise in die Vereinigten Staaten erlauben " +
                            "oder Chemtrails verbieten?",
                    "Würdest Du lieber Kurtis blowen oder Farid bangen?"
            };
    private String[] info13 =
            {
                    "Josef Fritzl,Niki Lauda",
                    null,
                    "Fler,Blümchen",
                    null,
                    null,
                    null,
                    "Chemtrails",
                    "Kurtis,Farid"

            };



    // Sendung mit Christopher Lauer
    private String[] questions12 =
            {
                    "Hättest Du lieber einen Flachmann von Lutz Bachmann oder Bauland neben Gauland?",
                    "Wärst Du lieber der Koch von Michael Schumacher oder der Schuhmacher von Samuel Koch?",
                    "Hättest Du lieber, dass Dir Klaus über die Leber oder eine Laus über den Kleber läuft?",
                    "Wärst Du lieber ein hässlicher und sehr unbeliebter Innensenator " +
                            "oder ein schöner und sehr beliebter Hausmeister?",
                    "Würdest Du lieber im Knast die Seife fallen lassen oder im Bundestag Dein Crystal Meth?",
                    "Hättest Du lieber deine letzte Sackkarre mit 44 oder Deine ersten Sackhaare mit 24?",
                    "Hättest Du lieber einen Sohn mit Barbara Hendricks der Jimi heißt " +
                            "oder eine Tochter mit Claudia Roth die Alarmstufe heißt?",
                    "Hättest Du lieber ein Gedächtnis von sieben Sekunden " +
                            "oder einen Ohrwurm von 7 seconds?",
                    "Würdest Du lieber der politischen Karriere wegen öffentlich Sigmar Gabriel loben " +
                            "oder als homosexueller Nationalspieler in einer Scheinehe leben?"
            };
    private String[] info12 =
            {
                    "Lutz Bachmann,Gauland",
                    "Michael Schumacher,Samuel Koch",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "Sigmar Gabriel"

            };



    // Sendung mit Klaas Heufer-Umlauf
    private String[] questions11    =
            {
                    "Wärst Du lieber eine Meerjungfrau mit dem Oberkörper eines Fisches " +
                            "oder ein Zentaur mit dem Unterkörper eines Menschen?",
                    "Hättest Du lieber schlecht pedikürte Hufe oder einen hübschen Pavianhintern?",
                    "Hättest Du lieber eine unangenehm anstrengende Lache " +
                            "oder dass jemand auf Twitter Deine private Handynummer veröffentlicht?",
                    "Hättest Du lieber ein Duett mit Oli P oder einen Dreier mit Olli Schulz?",
                    "Würdest Du lieber bei einem leichten Erdbeben von der Bettkante auf einen Kaktus fallen " +
                            "oder bei einem schweren Hausbrand aus dem dritten Stock auf Peter Altmaier?",
                    "Hättest Du lieber eine sehr schlechte Sicht auf das WM-Finale " +
                            "oder eine sehr gute Sicht auf Klöten-Ralles Klöten?"
            };
    private String[] info11 =
            {
                    null,
                    null,
                    null,
                    "Oli P,Olli Schulz",
                    "Peter Altmaier",
                    null
            };



    // Sendung mit Wolfang Bosbach
    private String[] questions10 =
            {
                    "Hättest Du lieber am helllichten Tag auf offener Straße Geschlechtsverkehr " +
                            "oder mitten in der Nacht im geschlossenen Schlafzimmer Berufsverkehr?",
                    "Würdest Du lieber von Haftbefehl gesucht werden oder von Xatar gefunden?",
                    "Hättest Du lieber die Mähne eines hässlichen Löwen " +
                            "oder den Rüssel eines äußerst attraktiven Elefanten?",
                    "Würdest Du lieber zehn Stunden im Gulli " +
                            "oder zehn Minuten in einem Fahrstuhl mit Oliver Pocher feststecken?",
                    "Würdest Du lieber mit Rainer Calmund Kanu fahren " +
                            "oder mit Christoph Daum einen Schneemann bauen?",
                    "Würdest Du lieber in einer Burka durch Dresden joggen " +
                            "oder in einem Mankini auf einem Einrad durch den Bundestag radeln?"
            };
    private String[] info10 =
            {
                    null,
                    "Haftbefehl,Xatar",
                    null,
                    null,
                    "Rainer Calmund,Christoph Daum",
                    "Mankini"
            };


    // Sendung mit Gregor Gysi
    private String[] questions9 =
            {
                    "Würdest Du den Weltfrieden schaffen, wenn Du dafür lebenslang im Körper " +
                            "eines nassen Otters gefangen wärst?",
                    "Würdest Du den Sexismus für immer beenden, wenn Du Dir dafür eine nackte " +
                            "Erika Steinbach auf die Brust tätowieren müsstest?",
                    "Würdest Du die Altersarmut abschaffen, wenn Du dafür " +
                            "immer die Frisur von Sahra Wagenknecht tragen müsstest?",
                    "Würdest Du das Flüchtlingsproblem lösen, wenn Du dafür " +
                            "Dein Leben lang in einem IKEA wohnen müsstest?",
                    "Würdest Du die Landminen abschaffen, wenn Du dafür wöchentlich " +
                            "Heavy Petting mit Angela Merkel haben müsstest?",
                    "Würdest Du die Welt vom Heuschnupfen befreien, wenn Du dafür " +
                            "bei jedem öffentlichen Auftritt eine gut sichtbare Erektion hättest?"
            };
    private String[] info9 =
            {
                    null,
                    "Erika Steinbach",
                    "Sahra Wagenknecht",
                    null,
                    null,
                    null
            };


    // Sendung mit Mike Krüger
    private final String krüger   = "Mike Krüger";
    private String[] questions8 =
            {
                    "Hätttest Du lieber eine große Nase oder einen großen Penis?",
                    "Wärst Du lieber der Vater von Miley Cyrus oder Tochter von Boris Becker?",
                    "Würdest Du lieber Schnick Schack Schnuck mit einem Hummer oder Strip-Poker " +
                            "mit einem Nacktmull spielen?",
                    "Hättest Du lieber einen gutartigen Tumor oder einen bösartigen Hamster?",
                    "Hättest Du lieber ein Display auf deiner Stirn, auf dem Deine Gedanken stehen " +
                            "oder Dein Leben lang ein Stück Zwiebel im Mund?",
                    "Hättest Du lieber Dein Leben lang eine grüne Spandex Hose an oder keine Beine mehr?",
                    "Hätetst Du lieber den Geruch von Thomas Gottschalk in der Nase " +
                            "oder den Geschmack von Thomas Gottschalk im Schrank?",
                    "Hättest Du lieber einen Ohrwurm von Satellit oder einen Bandwurm?",
            };
    private String[] info8 =
            {
                    null,
                    null,
                    "Nacktmull",
                    null,
                    null,
                    null,
                    null,
                    null
            };


    // Sendung mit Olli Schulz
    private String[] questions7 =
            {
                    "Wärst Du lieber ein Ausländer im Fanvon Dynamo Dresden " +
                            "oder ein Schwuler auf einem dordmunder Autobahnparkplatz?",
                    "Würdest Du lieber jeden Tag Dein Leben lang nur einen Song von Morrissey " +
                            "oder jeden Tag ein neues Album von Lena Meyer-Landrut?",
                    "Wärst Du lieber im Vorspann von Circus Halligalli oder nicht?",
                    "Wärst Du lieber mit Sebastian Edathy beim Kinderturnen oder mit Bill Cosby im Schlaflabor?",
                    "Würdest Du lieber Dinge zu Ende bringen können oder ... ?",
                    "Hättest Du lieber einen Toaster als einzigen Freund oder einen starken Bieber als Erzfreind?",
                    "Hättest Du lieber ein unerreichbares Jucken am Rücken oder eine fußläufig erreichbare Schwiegermutter?"
            };
    private String[] info7 =
            {
                    "Dynamo Dresten"
            };


    //  Sendung mit Dr. Mark Benecke
    private String[] questions6 =
            {
                    "Hättest Du lieber einen bedenklichen Auftritt beim Roten Sofa oder unbedenklichen roten Stuhl?",
                    "Würdest Du lieber ein Kaugummi von Rainer Calmund weiterkauen oder mit Tempo 10 gegen einen Baum fahren?",
                    "Wärst Du lieber ein Schwarzer in den USA oder ein Maikäfer im Juni?",
                    "Würdest Du lieber eine Kuh mit dem Mund melken oder einen Ochsen mit der Hand?",
                    "Hättest Du lieber eine Wurstbude mit Conchita Wurst oder einen Lieferdienst mit Jan Josef Liefers?",
                    "Würdest Du dich lieber von einem Hammerhai nageln lassen oder von einer Vogelspinne vögeln?",
                    "Hättest Du lieber ein Speeddate mit Sandra Bullock oder ein Blinddate mit Stevie Wonder?",
                    "Würdest Du lieber schwimmen mit Franzi van Almsick Uno mit dem UNO-Generalsekretär?",
                    "Hättest Du lieber einen Damenbart oder Herrentittchen?",
                    "Hättest Du lieber ein Kind das Frei.Wild hört oder ein Wildschwein, das Kinder beißt?"
            };

    //  Sendung Farin Urlaub
    private String[] questions5 =
            {
                    "Hättest Du lieber einen Affen und ein Pferd zu Hause oder einen Propeller auf dem Kopf?",
                    "Hättest Du lieber einen sehr kurzen Penis mit einem coolen Tribal " +
                            "oder ein Gesichtstattoo mit einem Rechtschreibfehler?",
                    "Würdest Du lieber in einer Talkshow auftreten in der geraucht und getrunken werden darf " +
                            "oder für Bob Geldof einen Song organisieren um Geld für Afrika zu sammeln?",
                    "Hättest Du lieber, dass ständig jemand vor Dir hergeht, der dich lautstark " +
                            "ankündigt oder einen stinkenden Bauchnabel?"
            };

    // Sendung Domian
    private String[] questions4 =
            {
                    "Hättest Du lieber einen Parka von Kanye West oder eine Weste von Peter Parker?",
                    "Wärst Du lieber ein sehr schlechter Fußball-Profi oder ein hoch angesehener Eiskunstläufer?",
                    "Hättest Du lieber eine Maultasche von Louis Vuitton oder eine Gürtelrose von Dolce&Gabbana?",
                    "Würdest Du lieber mit Sebastian Edathy eine Toys\"R\"Us Filiale eröffnen " +
                            "oder einen Stripclub mit Jack the Ripper?",
                    "Würdest Du lieber deine Festplatte korrekt auswerfen oder Deine Sackhaare sicher entfernen?",
                    "Hättest Du lieber eine leichte Erkältung, die sich gewaschen hat " +
                            "oder eine schwere Sexualpartnerin, die sich nicht gewaschen hat?",
                    "Würdest Du lieber Schäuble gegen's Schienbein treten oder einem süßen Ponny in's Gesicht schlagen?"
            };

    // Sendung mit Roger Willemsen
    private String[] questions3 =
            {
                    "Würdest Du lieber eine griesgrämig Robbe kloppen oder einen süß guckenden Hitler streicheln?",
                    "Hättest Du lieber zwanzig Finger oder zwei Nasen?",
                    "Wärst Du lieber ein heißer Urlaubsflirt von Elke Heidenreich " +
                            "oder ein unwichtiger One-Night-Stand von Hellmuth Karasek?",
                    "Hättest Du lieber einen Gürtel aus einem Murmeltier oder eine Murmel aus einem Gürteltier?",
                    "Würdest Du lieber mit Monica Lierhaus Mikado spielen oder mit Michael J. Fox ein Kartenhaus bauen?",
                    "Hätetst Du lieber dass der Tod Dich scheidet oder eine Scheide Dich tötet?",
                    "Würdest Du lieber mit einem Pferd verstecken spielen oder mit einem Chamäleon fangen?",
                    "Hättest Du lieber einen zurückhaltenden Stalker oder einen aufdringlichen Berner Sennenhund?",
                    "Würdest Du lieber eine nette Kuh schlachten oder ein böses Pferd?",
                    "Würdest Du bei Gefahr lieber Tinte aus Deiner Nase absondern oder Deinen Schwanz absondern?",
                    "Hättest Du lieber das Gehirn von Til Schweiger oder das Aussehen von Stephen Hawking?",
                    "Würdest Du lieber mit Reiner Calmund Tretboot oder mit Oscar Pistorius Tandem fahren?"
            };

    //Sendung mit Herbert Feuerstein
    private String[] questions2 =
            {
                    "Hätten Du lieber einen eingewachsenen Zehennagel oder einen ausgewachsenen Buckelwal?",
                    "Wärst Du lieber der Gynäkologe von Charlotte Roche oder der Proktologe von Harald Schmidt?",
                    "Würdest Du lieber mit einem Pedalo durch den Himmel fahren oder mit einem Segway durch die Hölle?",
                    "Hießt Du lieber Fickie Mac Bumsfick oder hättest Du lieber Dein Leben lang ein Stück Popcorn in der Backentasche?",
                    "Hättest Du lieber einen Röntgenblick bei adipösen Frauen oder einen tödlichen Laserblick kurz vor'm Orgasmus?",
                    "Würdest Du Dir eher für immer die Zähne mit Senf putzen oder den Po mit Schleifpapier abwischen?",
                    "Hättest Du lieber, dass schlechtes Bier aus Deinen Brustwarzen kommt oder guter Rotwein aus Deinem Penis?"
            };

    //Sendung mit Katrin Bauerfeind
    private String[] questions1 =
            {
                    "Hättest Du lieber eine dritte Brust oder einen starken sächsischen Dialekt?",
                    "Würdest Du lieber einen bösen Hitler umbringen oder mit einem netten Hitler einen schönen Abend verbringen?",
                    "Hättest Du lieber einen Penis oder fünf Penisse?",
                    "Hättest Du lieber Reinhold Messners Ohren oder Niki Laudas Füße?",
                    "Würdest Du lieber RTL von innen kaputt machen oder Pro7 von außen?",
                    "Würdest Du lieber einen Hoden am Kinn tragen oder ein wohlhabendes Opossum heiraten?",
                    "Wärst Du lieber die Frau von Helmut Kohl oder der Schlüpfer von Olivia Jones?",
                    "Würdest Du lieber eine Sendung mit Kraftklub gucken oder eine ohne Kraftklub?",
                    "Wärst Du lieber die Frau von Helmut Kohl oder der Schlüpfer von Olivia Jones?"
            };


}

