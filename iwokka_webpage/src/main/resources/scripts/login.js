function logIn (event) {
    event.preventDefault();
}

function register (event) {
    event.preventDefault();

}

function activeRegister() {
    
    const loginSection = document.querySelector("#logInSection");
    const registerSection = document.querySelector("#registerSection");
    
    setTimeout(() => {
      loginSection.style.opacity = 0;
      registerSection.style.opacity = 1;
      registerSection.style.pointerEvents = "auto";
      loginSection.style.pointerEvents = "none";
    }, 300); 
  }
  
  function activeLogIn() {
    const loginSection = document.querySelector("#logInSection");
    const registerSection = document.querySelector("#registerSection");
    
    setTimeout(() => {
      registerSection.style.opacity = 0;
      loginSection.style.opacity = 1;
      registerSection.style.pointerEvents = "none";
      loginSection.style.pointerEvents = "auto";
    }, 300);
  }