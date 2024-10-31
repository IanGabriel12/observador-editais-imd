import { useState } from 'react'
import './App.css'
import EmailIcon from './assets/email.svg';
import SubscribeIcon from './assets/subscribe.svg';
import CheckIcon from './assets/check.svg';
import ErrorIcon from './assets/error.svg';

function App() {
  const [isLoading, setIsloading] = useState(false);
  const [isFirstTime, setIsFirstTime] = useState(true);
  const [isSuccess, setIsSuccess] = useState(false);
  const [isError, setIsError] = useState(false);
  const [errorMessage, setErrorMessage] = useState('Email já inscrito');


  let helperMessageElement: React.JSX.Element = (
    <div className='helper'>
      <img src={CheckIcon} alt="" />
      <span className='success'>
        Inscrição feita com sucesso.
      </span>
    </div>
  );
  
  if(isLoading) {
    helperMessageElement = (
      <div className='helper'>
        <div className="loader"></div>
        <span className='loading'>
          Realizando inscrição...
        </span>
      </div>
    );
  } else if (isSuccess) {
    helperMessageElement = (
      <div className='helper'>
        <img src={CheckIcon} alt="" />
        <span className='success'>
          Inscrição feita com sucesso.
        </span>
      </div>
    );
  } else {
    helperMessageElement = (
      <div className='helper'>
        <img src={ErrorIcon} alt="" />
        <span className='error'>
          {errorMessage}
        </span>
      </div>
    );
  }

  return (
    <div className='content'>
      <h1>Receba notificações de editais do Instituto Metrópole Digital</h1>
      <p>Informe o seu e-mail abaixo para receber notificações sobre novas oportunidades</p>
      <form>
        <div className='email'>
          <div className='email-icon'>
            <img src={EmailIcon} alt="" />
          </div>
          <input type="email" placeholder='Email' className='email-input'/>
        </div>
        <button type='submit' disabled={isLoading}>
          <img src={SubscribeIcon} alt="" />
          Inscrever-se
        </button>
      </form>
      
      {!isFirstTime && helperMessageElement}
      
    </div>
  )
}

export default App
