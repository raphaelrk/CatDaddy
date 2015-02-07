#pragma strict

function Start () {
	var devices : WebCamDevice[] = WebCamTexture.devices;
	for( var i = 0 ; i < devices.length ; i++ )
		Debug.Log(devices[i].name);
	
	var webcamTexture : WebCamTexture = WebCamTexture();
	renderer.material.mainTexture = webcamTexture;
	webcamTexture.Play();	
}