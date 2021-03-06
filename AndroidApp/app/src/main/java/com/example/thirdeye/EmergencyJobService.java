package com.example.thirdeye;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.net.Socket;

public class EmergencyJobService extends JobService {

    // Tag pour les messages dans les logs
    private static final String TAG = "EmergencyJobService";
    // Adresse du serveur et port de connexion
    private static final String HOST_ADDRESS = "13.40.96.149";
    private static final int HOST_PORT = 8080;

    // Période d'appel au serveur, en millisecondes.
    private static final int PERIODE_APPEL = 5000;

    // Instanciation de la tâche
    EmergencyTask tacheEmergency = new EmergencyTask();
    boolean shouldReschedule = false; // Faux par défaut


    /**
     * Méthode appelée quand la tâche est lancée
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "onStartJob id=" + params.getJobId());
        // Lancement de la mesure dans une AsyncTask
        tacheEmergency.execute();
        // Programmation de la tâche suivante, pour les raisons évoquées dans scheduleJobWiFi()


        jobFinished(params, shouldReschedule);
        return true;
    }

    /**
     * Méthode appelée quand la tâche est arrêtée par le scheduler
     * Retourne vrai si le scheduler doit relancer la tâche
      */
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob id=" + params.getJobId());
        // Arrêt de la tâche.
        tacheEmergency.cancel(true);
        return shouldReschedule;
    }


    /**
     * Gère le lancement des Job
     * Cet méthode est un copier/coller de la méthode dans Foreground service, en attendant de
     * trouver une solution plus propre
     */
    private void scheduleJobEmergency() {
        Log.d(TAG, "scheduleJobEmergency: debut");
        ComponentName serviceName = new ComponentName(this, EmergencyJobService.class);

        // Les versions récentes d'Android ne supportent pas des périodes inférieures à 15 minutes.
        // Pour contourner cela, on va "tricher" en appelant la méthode scheduleJobWiFi dans le
        //      job lui même, c'est à dire que chaque instance va programmer la suivante.
        // On utilise alors la propriété SetMinimumLatency pour forcer une durée inter-job
        //      minimale, et donc éviter leur prolifération.
        //      Cela correspond donc à la période effective.
        JobInfo jobInfo = new JobInfo.Builder(0, serviceName).setMinimumLatency(PERIODE_APPEL).build();

        JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int result = scheduler.schedule(jobInfo);
        if (result == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Schedule job avec succès");
        }
        Log.d(TAG, "scheduleJobEmergency: fin");
    }

    /**
     * Envoi de la notification d'alerte.
     */
    private void addNotification(){
        NotificationChannel chan = new NotificationChannel(
                "ChannelEmergency",
                "ChannelEmergency",
                NotificationManager.IMPORTANCE_MAX);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert mNotificationManager != null;
        mNotificationManager.createNotificationChannel(chan);

        Intent fullScreenIntent = new Intent(this, MainActivity.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "ChannelEmergency");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("ThirdEye")
                .setContentText("Une alerte a été déclenchée !")
                .setFullScreenIntent(fullScreenPendingIntent, true);

        int notificationID=1;
        mNotificationManager.notify(notificationID, mBuilder.build());
    }

    /**
     *   La tâche à exécuter
     */
    private class EmergencyTask extends AsyncTask<Void, Void, Character> {

        // Tag pour les messages dans les logs
        private static final String TAG = "EmergencyTask";

        /**
         * Appelle le serveur pour obtenir l'état
         * @return Le code réponse renvoyé par l'appel au serveur
         */
        @Override
        protected Character doInBackground(Void... voids) {

            Log.d(TAG, "doInBackground: debut");
            Character responseChar;

            try {
                // Création du socket TCP pour communiquer avec le serveur
                Socket socket = new Socket(HOST_ADDRESS, HOST_PORT);
                // Envoi de la requête au serveur
                socket.getOutputStream().write(((byte) 'R'));
                socket.getOutputStream().flush();
                // Récupération de la réponse
                int response = socket.getInputStream().read();
                responseChar = (char) response;
                // Affichage de la réponse dans les logs
                if (response == -1) {
                    Log.d(TAG, "Il n'y a rien à lire");
                } else {
                    String str = "Réponse obtenue = " + responseChar;
                    Log.d(TAG, str);
                }
                // Fermeture du socket
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Une exception a été levée !");
                // Valeur de réponse correspondant à l'erreur pour le post-traitement
                responseChar = 'Z';
            }

            Log.d(TAG, "doInBackground: fin");
            return responseChar;
        }

        /**
         *
         * @param result Le code réponse renvoyé par l'appel au serveur
         */
        @Override
        protected void onPostExecute(Character result) {
            super.onPostExecute(result);
            switch (result) {
                case 'Z':
                    Log.i(TAG, "Une exception a eu lieu, on n'a donc pas de résultat.");
                    Toast.makeText(EmergencyJobService.this, "Le serveur n'a pas pu être joint !", Toast.LENGTH_LONG).show();
                    break;
                case 'O':
                    Log.i(TAG, "Tout va bien !");
                    break;
                case 'N':
                    Log.i(TAG, "L'appel d'urgence a été passé !");
                    addNotification();
                    break;
                default:
                    Log.d(TAG, "Erreur, la réponse n'est pas reconnue. Réponse = " + result);
                    break;
            }
            // Programation de l'appel suivant au serveur
            scheduleJobEmergency();
        }
    }
}