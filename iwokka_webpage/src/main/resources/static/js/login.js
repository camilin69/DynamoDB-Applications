const OK_CLIENT_FOUND = "OK: CLIENT FOUND";
const ERROR_CLIENT_NOT_FOUND = "ERROR: CLIENT NOT FOUND";
const ERROR_PASSWORD_MISMATCH= "ERROR: PASSWORD MISMATCH FOR EMAIL GIVEN";

function logIn (event) {
    event.preventDefault();
    const email = document.getElementById('emailInputLogIn').value;
    const password = document.getElementById('passwordInputLogIn').value;

    const encodedEmail = encodeURIComponent(email);
    const encodedPassword = encodeURIComponent(password);

    fetch(`http://localhost:8080/api/clients/login?email=${encodedEmail}&password=${encodedPassword}`, {
      method : 'GET',
      headers: {
            'Content-Type': 'application/json',
      }
    })
    .then(response => {
      if(!response) {
        throw new Error('ERROR: LOGIN');
      }
      return response.json();
    })
    .then(user => {
      if (user) {
        localStorage.setItem('isAuthenticated', 'true');
        localStorage.setItem('user', JSON.stringify(user));
        window.location.href = '/index';
      }else {
          alert(ERROR_CLIENT_NOT_FOUND);
      } 
    })
    .catch(error => {
      console.log('Error Login: ', error)
    })
}

function register (event) {
    event.preventDefault();

     const clientData = {
        id: document.getElementById('idInputRegister').value,
        name: document.getElementById('nameInputRegister').value,
        email: document.getElementById('emailInputRegister').value,
        password: document.getElementById('passwordInputRegister').value,
        role: document.getElementById('roleSelectRegister').value,
        products: [] // Inicializar lista vacía según el modelo
    };

    fetch('http://localhost:8080/api/clients/save', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(clientData),
    })
    .then(response => {
        if (!response.ok) throw new Error('ERROR: SAVING CLIENT')
        return response.json();
    })
    .then(data => {
        alert(data);
        activeLogIn(); 
    })
    .catch(error => {
        console.error('Error Register: ', error);
    });
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