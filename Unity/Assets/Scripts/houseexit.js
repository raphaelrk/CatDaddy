#pragma strict

var player: GameObject;

function OnTriggerEnter (other : Collider) {
	Application.LoadLevel("Main");
}

function Update (){
	if (Input.GetKeyDown(KeyCode.LeftArrow)){
		Application.LoadLevel("House");
	}
	if (Input.GetKeyDown(KeyCode.RightArrow)){
		Application.LoadLevel("Main");
	}
	if (player.transform.position.y < -115){
		Application.LoadLevel("House");
	}
}