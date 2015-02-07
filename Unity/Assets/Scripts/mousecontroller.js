#pragma strict

var leftwheel: GameObject;
var rightwheel: GameObject;

function Update () {
	var speed: float = 0.1;
	var x = speed * Input.GetAxis ("Mouse X");
	var y = speed * Input.GetAxis ("Mouse Y");
	var mean = ((x+y)/2)*1;
	
	transform.Rotate(0, -x, 0);
	transform.Rotate(0, y, 0);
	
	if ((x > 0) && (y > 0)){
		transform.Translate(Vector3(0, 0, -mean)*Time.deltaTime);
	}
	else if ((x < 0) && (y < 0)){
		transform.Translate(Vector3(0, 0, -mean)*Time.deltaTime);
	}
	
	if (x > 0){
		leftwheel.transform.Rotate(0, 0, 50);
	}
	else if (x < 0){
		leftwheel.transform.Rotate(0, 0, -x);
	}
		
}