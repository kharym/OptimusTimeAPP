package com.example.karim.optimustime;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import java.util.ArrayList;
import java.util.*;
import java.io.File;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class Tiempo extends AppCompatActivity {

    Spinner listaInicio;
    Spinner listaFinal;
    Button botonCalcularT;
    TextView resultado;
    int valorI, valorF, tiempo;
    MediaPlayer mp;
    SQLiteDatabase optimusTimeDB = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiempo);

        //intanciar variables
        listaInicio = (Spinner)findViewById(R.id.spinnerInicio);
        listaFinal = (Spinner)findViewById(R.id.spinnerFinal);
        botonCalcularT=(Button)findViewById(R.id.botonCalcular);
        mp = MediaPlayer.create(this,R.raw.sonidoboton);
        resultado=(TextView)findViewById(R.id.jsonData);

        //Creacion y llenado de base de datos
        optimusTimeDB = createDatabase();
        fillDatabase(optimusTimeDB);
        getStreets(optimusTimeDB);


        botonCalcularT.setOnClickListener(

                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final TextView results;

                        int idColumInicio,street_nameColumnInicio,latitudeColumnInicio,longitudeColumnInicio;
                        int idColumUltimo,street_nameColumnUltimo,latitudeColumnUltimo,longitudeColumnUltimo;

                        String inicio,idInicio, latitudeInicio, longitudeInicio, street_nameInicio;
                        String ultimo,idUltimo,latitudeUltimo,longitudeUltimo,street_nameUltimo;
                        Cursor cursor,cursor2;

                        mp.start();

                        inicio = listaInicio.getSelectedItem().toString();
                        ultimo = listaFinal.getSelectedItem().toString();

                        cursor = optimusTimeDB.rawQuery("SELECT * FROM streets WHERE street_name = '"+inicio+"';",null);

                        idColumInicio = cursor.getColumnIndex("id");
                        street_nameColumnInicio = cursor.getColumnIndex("street_name");
                        latitudeColumnInicio = cursor.getColumnIndex("latitude");
                        longitudeColumnInicio = cursor.getColumnIndex("longitude");

                        cursor.moveToFirst();

                        idInicio = cursor.getString(idColumInicio);
                        street_nameInicio = cursor.getString(street_nameColumnInicio);
                        latitudeInicio = cursor.getString(latitudeColumnInicio);
                        longitudeInicio = cursor.getString(longitudeColumnInicio);

                        cursor2 = optimusTimeDB.rawQuery("SELECT * FROM streets WHERE street_name = '"+ultimo+"';",null);

                        idColumUltimo = cursor2.getColumnIndex("id");
                        street_nameColumnUltimo = cursor2.getColumnIndex("street_name");
                        latitudeColumnUltimo = cursor2.getColumnIndex("latitude");
                        longitudeColumnUltimo = cursor2.getColumnIndex("longitude");

                        cursor2.moveToFirst();

                        idUltimo = cursor2.getString(idColumUltimo);
                        street_nameUltimo = cursor2.getString(street_nameColumnUltimo);
                        latitudeUltimo = cursor2.getString(latitudeColumnUltimo);
                        longitudeUltimo = cursor2.getString(longitudeColumnUltimo);

                        /*System.out.println(street_nameInicio);
                        System.out.println(street_nameUltimo);*/

                        //Si el ID del primero es mayor que el segundo, entonces es correcto
                        if(idUltimo.compareTo(idInicio) > 0){
                            //Retornar los datos
                            Toast aviso= Toast.makeText(getApplicationContext(),"Realizando consulta.....",Toast.LENGTH_LONG);
                            aviso.show();

                            //	CAMBIOS

                            Calendar fecha = new GregorianCalendar();
                            long dias = System.currentTimeMillis() /86400000;
                            double hora = fecha.get(Calendar.HOUR_OF_DAY);
                            double minuto = fecha.get(Calendar.MINUTE);
                            double segundo = fecha.get(Calendar.SECOND);
                            double totalSegundos = (hora*3600+minuto*60+segundo)/86400;
                            System.out.println(totalSegundos);
                            double parametroFecha = dias +25568 +totalSegundos;
                            String parametroFinal = String.valueOf(parametroFecha);

                            ///FIN CAMBIOS

                            //Metodo para entrefarle cosas a la API
                            String JsonURL = "http://192.168.1.38:5555/tiempo?latitudActual="+latitudeColumnInicio+"&latitudFinal="+latitudeColumnUltimo+"&longitudActual="+longitudeColumnInicio+"&longitudFinal="+longitudeColumnUltimo+"&fechaActual="+parametroFecha;
                            RequestQueue requestQueue;

                            Toast coord= Toast.makeText(getApplicationContext(),"latitud inicio: "+latitudeColumnInicio+" y longitud inicio: "+longitudeColumnInicio+"\n latitud final: "+latitudeColumnUltimo+" y longitudFinal: "+longitudeColumnUltimo+"\n fecha actual:"+parametroFecha ,Toast.LENGTH_LONG);
                            coord.show();

                            requestQueue = Volley.newRequestQueue(getBaseContext().getApplicationContext());
                            results = (TextView) findViewById(R.id.jsonData);
                            JsonArrayRequest arrayreq = new JsonArrayRequest(JsonURL,
                                    // The second parameter Listener overrides the method onResponse() and passes
                                    //JSONArray as a parameter
                                    new Response.Listener<JSONArray>() {

                                        // Takes the response from the JSON request
                                        @Override
                                        public void onResponse(JSONArray response) {

                                            String data = "";

                                            try {

                                                JSONObject colorObj = response.getJSONObject(0);


                                                JSONArray colorArry = colorObj.getJSONArray("Tiempo");
                                                for (int i = 0; i < colorArry.length(); i++) {
                                                    JSONObject jsonObject = colorArry.getJSONObject(i);
                                                    int tiempo = jsonObject.getInt("time");

                                                    if(tiempo<3600){

                                                        int min=(int) tiempo/60;
                                                        String mintxt =String.valueOf(min);


                                                        results.setText(mintxt+" MINUTOS");
                                                    }

                                                    if (tiempo>=3600){

                                                        float aux =tiempo/3600;
                                                        int hora = (int)aux;
                                                        float aux2= Math.abs(aux-hora);
                                                        float aux3= aux2*60;
                                                        int min=(int)aux3;
                                                        String mintxt =String.valueOf(min);
                                                        String horatxt = String.valueOf(hora);


                                                        results.setText(horatxt +" HORA Y"+mintxt+" MINUTOS");

                                                    }


                                                }

                                            }
                                            catch (JSONException e) {
                                                // If an error occurs, this prints the error to the log
                                                e.printStackTrace();
                                                results.setText("Error: No se puede validar la respuesta del servidor");
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        // Handles errors that occur due to Volley
                                        public void onErrorResponse(VolleyError error) {

                                            Log.e("Volley", "Error");
                                            results.setText("Error: Verifique su conexion al servidor");
                                        }
                                    }
                            );
                            requestQueue.add(arrayreq);


                        }
                        else
                        {
                            Toast aviso= Toast.makeText(getApplicationContext(),"Datos incorrectos, intente nuevamente.",Toast.LENGTH_LONG);
                            aviso.show();
                        }

                    }
                }
        );

    }

    private void getStreets(SQLiteDatabase optimus)
    {
        //Primero definimos las variables a utilizar
        List<String> lista = new ArrayList<String>();
        ArrayAdapter<String> adapter;

        Cursor cursor = optimus.rawQuery("SELECT * FROM streets",null);

        int street_nameColumn = cursor.getColumnIndex("street_name");

        cursor.moveToFirst();

        if(cursor != null && (cursor.getCount() > 0))
        {
            do{
                lista.add(cursor.getString(street_nameColumn));

            }while(cursor.moveToNext());
        }

        adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.spinner_datos,lista);
        adapter.setDropDownViewResource(R.layout.spinner_datos);
        listaInicio.setAdapter(adapter);
        listaFinal.setAdapter(adapter);
        cursor.close();
    }

    private SQLiteDatabase createDatabase() {

        SQLiteDatabase optimusTime = null;

        try
        {

            optimusTime = this.openOrCreateDatabase("optimusTime_db",MODE_PRIVATE,null);

            optimusTime.execSQL("CREATE TABLE IF NOT EXISTS streets "+"(id integer primary key, street_name VARCHAR, latitude FLOAT, longitude FLOAT);");

            File database = getApplicationContext().getDatabasePath("optimusTime.db");

            if (!database.exists())
            {
                Toast.makeText(this,"Bienvenido",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this,"Database Missing",Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e)
        {
            Log.e("CONTACTS ERROR","Error Creating Databse");
        }

        return optimusTime;
    }

    private void fillDatabase(SQLiteDatabase optimus)
    {
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Ricardo Leonidas Ribas',-30.14737,-51.131);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Doze',-30.15031667,-51.13286833);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Alameda H',-30.15271667,-51.13561167);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Economista Nilo Wulff',-30.15423667,-51.13834167);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Belize',-30.15527167,-51.14135);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Eugenio Rodrigues',-30.15577333,-51.14281);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Clara Nunes',-30.15795667,-51.14549167);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Pronto Socorro de Pneus',-30.16026167,-51.14830167);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Estrada do Barro',-30.16266,-51.15024333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Gordogao Restinga',-30.16445,-51.15242667);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Multipla Escolha',-30.16527,-51.15418);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Aviario Iraci',-30.16556,-51.15452333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Napoleao Jacques da Rosa',-30.16457833,-51.15538833);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Torres Automarca',-30.16360333,-51.15567333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Boca Motos',-30.16078,-51.159895);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Madetek',-30.15944167,-51.160475);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Abrasivos Abrazil',-30.15762333,-51.16189333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('7 Seiques',-30.156415,-51.16432833);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Jose Oscar Galves',-30.1564,-51.166075);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Agro Mari',-30.15625667,-51.16955);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Germano Bolow Filho',-30.15586833,-51.17275167);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Super Khan',-30.15550333,-51.17522167);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Foca Lavagem',-30.15536167,-51.17901833);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Rua Baldohino',-30.15556167,-51.18149);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Saque e page',-30.156255,-51.18380833);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Padre Roberto Landell',-30.15671,-51.187125);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Zuzu Angel',-30.15822,-51.189575);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Dr. Hermes Pacheco',-30.15837167,-51.19371333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Celestino Bertolucci',-30.15653833,-51.19719667);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Alberto do Canto',-30.15398667,-51.20010667);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Bangalo',-30.15065667,-51.20138);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Rua Cames Bocacio',-30.147885,-51.20393167);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Alameda Trintra e Tres',-30.14752667,-51.20514333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Andre Rimo',-30.14738667,-51.20773833);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Jesus de Praga',-30.14618167,-51.21103);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Agropal',-30.14426333,-51.21461167);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Oligario Di Maciel',-30.142935,-51.21699667);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Animal Vet',-30.141605,-51.21893667);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Coelho Parreira',-30.13951,-51.22029667);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Espetao na Braza',-30.13796,-51.22004167);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Parque Res. Ipanema',-30.13658,-51.21944167);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Igreja Bautista Ipanema',-30.13349833,-51.218215);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Enio Aveline',-30.13054167,-51.21817);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Atilio Superti',-30.12820333,-51.21905333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('R.H.',-30.12554667,-51.22005667);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Monte Cristo',-30.12348833,-51.22083833);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Posto de Gasolina',-30.12189167,-51.22227);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Anhanguera Porto Alegre',-30.11933667,-51.22416667);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('C Lorandi',-30.11727333,-51.224445);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Rua Familia',-30.114485,-51.22597333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Joao Vedana',-30.11106833,-51.22677);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('R. Liberal',-30.10881667,-51.22701333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Piastrella',-30.107625,-51.227395);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Dr. Mario Totta',-30.105705,-51.22947333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Joao E.F. Da Costa',-30.10297167,-51.23059333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Otto Niemeyer',-30.10246833,-51.230885);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('CEF',-30.10015833,-51.23126);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Rua Stephan Zweig',-30.09847833,-51.23149333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Santa Flora',-30.097475,-51.23077333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Rua Salvador Calamucci',-30.09656833,-51.22728);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Vicente Monteggia',-30.095435,-51.22590667);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Marcio Dias',-30.093965,-51.22409667);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Menezes Paredes',-30.09165167,-51.221055);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Belem',-30.08014833,-51.20971667);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Ze Pneus',-30.07805167,-51.208055);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Prof. Carvalho de Freitas',-30.07710333,-51.20782667);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('CFC Pradao',-30.07611833,-51.20793333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Igreja Assembleia de Deus',-30.07335333,-51.20897167);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Cantinho das Pizzas',-30.07120667,-51.21012167);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Fonseca Ramos',-30.06734,-51.21067833);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('R. Cel. Neves',-30.06554333,-51.212155);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Tv. Matto Grosso',-30.05979667,-51.21207333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('R. Germano Hasslocher',-30.05625667,-51.21470167);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('R. Baraho no Triunfo',-30.05295167,-51.21376333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Rua Dr. Ramiro D Avila',-30.04967333,-51.21047333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('R. Luis Manoel',-30.04606333,-51.21252333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Jeronimo de Ornelas',-30.04346667,-51.21446667);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('R. Olavo Bilac',-30.04218,-51.21549167);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Rua Octavio Correa',-30.038935,-51.21801);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Loureiro da Silva',-30.03531333,-51.220745);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('R. Avai',-30.03365333,-51.22209333);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Des. Andre da Rocha',-30.03293833,-51.22265667);");
        optimus.execSQL("INSERT INTO streets (street_name,latitude,longitude) VALUES ('Rua Professor Annes Dias',-30.03158167,-51.223865);");
    }

}
