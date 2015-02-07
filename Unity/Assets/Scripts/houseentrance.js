#pragma strict

var player: GameObject;

function OnTriggerEnter (other : Collider) {
	Application.LoadLevel("House");
}

function Update (){
	if (Input.GetKeyDown(KeyCode.LeftArrow)){
		Application.LoadLevel("Main");
	}
	if (Input.GetKeyDown(KeyCode.RightArrow)){
		Application.LoadLevel("House");
	}
}