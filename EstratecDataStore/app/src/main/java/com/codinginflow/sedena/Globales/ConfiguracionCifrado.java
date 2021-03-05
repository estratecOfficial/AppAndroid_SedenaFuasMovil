package com.codinginflow.sedena.Globales;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConfiguracionCifrado {
	// Server user login url
	public static String URL_LOGIN = "http://appestratec.com/appestratec/login_encripted/login.php";
	public static String URL_CONEXION_SYNC = "https://appestratec.com/appestratec/syncSEDENA/SubjectFullForm.php";
	public static String URL_SYN_SERIES = "https://appestratec.com/appestratec/syncSEDENA/Sync.php";
	public static String URL_UPDATE_GODADDY = "https://appestratec.com/appestratec/syncSEDENA/upload.php";


	public static class URLServer {
		// Guarda la fotos del FUA url
		public static String URL_GUARDAR_FOTO_FUA = "https://appestratec.com/appestratec/syncSEDENA/SubirFotos/SubirFotoFUA.php";

		// Guarda la fotos del contador url
		public static String URL_GUARDAR_FOTO_CONTADOR = "https://appestratec.com/appestratec/syncSEDENA/SubirFotos/SubirFotoContador.php";

	}


	public static boolean compruebaConexion(Context context) {

		boolean connected = false;
		ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// Recupera todas las redes (tanto móviles como wifi)
		NetworkInfo[] redes = connec.getAllNetworkInfo();
		for (int i = 0; i < redes.length; i++) {
			// Si alguna red tiene conexión, se devuelve true
			if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
				connected = true;
			}
		}
		return connected;
	}

	public static Boolean isOnlineNet() {

		try {
			Process p = Runtime.getRuntime().exec("ping -c 1 www.google.es");

			int val = p.waitFor();
			boolean reachable = (val == 0);
			return reachable;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
