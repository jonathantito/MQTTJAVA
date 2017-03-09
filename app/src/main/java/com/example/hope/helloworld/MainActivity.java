/*
    Modif.1
    Se añade código para poder publicar con el protocolo MQTT en Android
    1 de Febrero de 2017
    Bibliografía:
    http://www.hivemq.com/blog/mqtt-client-library-enyclopedia-paho-android-service
    https://sango.shiguredo.jp/dashboard
* */
/*
    Modif.2
    Se añade código para poder suscribirse el protocolo MQTT en Android
    2 de Febrero de 2017
    Bibliografía:
* */
package com.example.hope.helloworld;
;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
//import android.util.Log; //Modif.1.A.old
//Modif.1 IMPORTACIONES AÑADIDAS inicio
import android.view.View;
import android.widget.Toast;
import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
//Modif.1 IMPORTACIONES AÑADIDAS fin
import android.widget.TextView;//Modif.2.ln
import android.media.Ringtone;//Modif.2.ln
import android.os.Vibrator;//Modif.2.ln
import android.media.RingtoneManager;//Modif.2.ln
import android.net.Uri;//Modif.2.ln

public class MainActivity extends AppCompatActivity {
    //Modif.1 DATOS DEL BROKER SANGO inicio
    //static String MQTTHOST = "tcp://jonathanetito.sytes.net:1883";//Modif.1.old.ln
    static String MQTTHOST = "tcp://192.168.1.7:1883";//Modif.1.new.ln
    static String USERNAME = "ruta";
    static String PASSWORD ="123abc456xyz";
    //String topicStr = "jonathantito@github/#"; //Modif.1.old.ln no se permiten wildcards en el tópico según python app
    //String topicStr = "ruta/Quito,Ec-Guayaqui,ec";//Modif.1.new.ln no permitido
    String topicStr = "ruta/test";
    //Modif.1 DATOS DEL BROKER SANGO fin
    MqttAndroidClient client; //Modif.1.B.new
    TextView subText;//Modif.2.ln
    MqttConnectOptions options;//Modif.2.ln
    Vibrator vibrator;//Modif.2.ln
    Ringtone myRingtone;//Modif.2.ln
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subText=(TextView)findViewById(R.id.subText);//Modif.2.ln
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);//Modif.2.ln
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//Modif.2.ln
        myRingtone = RingtoneManager.getRingtone(getApplicationContext(),uri);//Modif.2.ln
        //Modif.1 CÓDIGO AÑADIDO inicio
        String clientId = MqttClient.generateClientId();
        //MqttAndroidClient client =//Modif.1.B.old
        client =//Modif.1.B.new
                new MqttAndroidClient(this.getApplicationContext(),
                        //"tcp://broker.hivemq.com:1883",//Modif.1.B.old
                        MQTTHOST,//Modif.1.B.new
                        clientId);

        //MqttConnectOptions options = new MqttConnectOptions();//Modif.2.old.ln
        //options.setUserName(USERNAME);//Modif.2.old.ln
        //options.setPassword(PASSWORD.toCharArray());//Modif.2.old.ln

        options = new MqttConnectOptions();//Modif.2.new.ln
        options.setUserName(USERNAME);//Modif.2.new.ln
        options.setPassword(PASSWORD.toCharArray());//Modif.2.new.ln



        try {
            //IMqttToken token = client.connect();//Modif.1.C.old
            IMqttToken token = client.connect(options);//Modif.1.C.new
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // se ha conectado
                    //Log.d(TAG, "onSuccess");//Modif.1.A.old
                    Toast.makeText(MainActivity.this, "Conexión exitosa", //Modif.1.A.new
                            Toast.LENGTH_LONG).show();//Modif.1.A.new
                    setSubscription(); //Modif.2.ln
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Algo falló, posiblemente el tiempo de espera o el firewall dan problemas
                    //Log.d(TAG, "onFailure");//Modif.1.A.old
                    Toast.makeText(MainActivity.this, "Error en la conexión", //Modif.1.A.new
                            Toast.LENGTH_LONG).show();//Modif.1.A.new

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        //Modif.1 CÓDIGO AÑADIDO fin

        //Modif.2 Mensaje que arriba INICIO
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                subText.setText(new String(message.getPayload()));
                vibrator.vibrate(500);
                myRingtone.play();

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        //Modif.2 Mensaje que arriba FIN
    }
    //Modif.1 CÓDIGO AÑADIDO inicio
    public void pub(View v)
    {
        //String topic = "foo/bar";//Modif.1.D.old
        String topic = topicStr;//Modif.1.D.new
        //String payload = "the payload";//Modif.1.D.old
        String message = "Hello World!!!!!";//Modif.1.D.old
        byte[] encodedPayload = new byte[0];
        try {
            //encodedPayload = payload.getBytes("UTF-8");//Modif.1.D.old
            //MqttMessage message = new MqttMessage(encodedPayload);//Modif.1.D.old
            //client.publish(topic, message);//Modif.1.D.old
            client.publish(topic, message.getBytes(),0,false);//Modif.1.D.new
        //} catch (UnsupportedEncodingException | MqttException e) {//Modif.1.D.old
        } catch (MqttException e) {//Modif.1.D.new
            e.printStackTrace();
        }
    }
    //Modif.2 función para suscribirse por MQTT INICIO
    private void setSubscription()
    {
        try {
            client.subscribe(topicStr,0);
        }
        catch (MqttException e) {
            e.printStackTrace();
        }
    }
    //Modif.2 función para suscribirse por MQTT FIN
    public void conn(View v){
        try {
            //IMqttToken token = client.connect();//Modif.1.C.old
            IMqttToken token = client.connect(options);//Modif.1.C.new
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // se ha conectado
                    //Log.d(TAG, "onSuccess");//Modif.1.A.old
                    Toast.makeText(MainActivity.this, "Conexión exitosa", //Modif.1.A.new
                            Toast.LENGTH_LONG).show();//Modif.1.A.new
                    setSubscription(); //Modif.2.ln
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Algo falló, posiblemente el tiempo de espera o el firewall dan problemas
                    //Log.d(TAG, "onFailure");//Modif.1.A.old
                    Toast.makeText(MainActivity.this, "Error en la conexión", //Modif.1.A.new
                            Toast.LENGTH_LONG).show();//Modif.1.A.new

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconn(View v)
    {
        try {
            //IMqttToken token = client.connect();//Modif.1.C.old
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // se ha conectado
                    //Log.d(TAG, "onSuccess");//Modif.1.A.old
                    Toast.makeText(MainActivity.this, "Desconectado", //Modif.1.A.new
                            Toast.LENGTH_LONG).show();//Modif.1.A.new
                    //setSubscription(); //Modif.2.ln
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Algo falló, posiblemente el tiempo de espera o el firewall dan problemas
                    //Log.d(TAG, "onFailure");//Modif.1.A.old
                    Toast.makeText(MainActivity.this, "No se pudo desconectar", //Modif.1.A.new
                            Toast.LENGTH_LONG).show();//Modif.1.A.new

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    //Modif.1 CÓDIGO AÑADIDO fin
}
