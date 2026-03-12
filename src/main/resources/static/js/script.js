// Copy password to clipboard

function copyPassword(id){

let password=document.getElementById(id).innerText;

navigator.clipboard.writeText(password);

alert("Password copied!");
}



// Password strength indicator

function checkStrength(){

let password=document.getElementById("password").value;

let strengthText=document.getElementById("strength");

let strength="Weak";

if(password.length>8){
strength="Medium";
}

if(password.match(/[A-Z]/) && password.match(/[0-9]/) && password.length>10){
strength="Strong";
}

if(password.match(/[!@#$%^&*]/) && password.length>12){
strength="Very Strong";
}

strengthText.innerHTML=strength;

}



// Toggle password visibility

function togglePassword(){

let field=document.getElementById("password");

if(field.type==="password"){
field.type="text";
}else{
field.type="password";
}

}



// Confirm delete

function confirmDelete(){

return confirm("Are you sure you want to delete this password?");

}