import { FormEvent, useState } from 'react'
import './styles.css'
import EmailIcon from '../../assets/email.svg';
import SubscribeIcon from '../../assets/subscribe.svg';
import CheckIcon from '../../assets/check.svg';
import ErrorIcon from '../../assets/error.svg';
import SubscriptionService from '../../SubscriptionService';

const CONFLICT = 409;

function selectHelperComponent(isLoading: boolean, isSuccess: boolean, isError: boolean, errorMessage: string) {
  const loadingComponent = (
    <div className='helper'>
      <div className="loader"></div>
      <span className='loading'>
        Realizando inscrição...
      </span>
    </div>
  );
  const successComponent = (
    <div className='helper'>
      <img src={CheckIcon} alt="" />
      <span className='success'>
        Inscrição feita com sucesso.
      </span>
    </div>
  );
  const errorComponent = (
    <div className='helper'>
      <img src={ErrorIcon} alt="" />
      <span className='error'>
        {errorMessage}
      </span>
    </div>
  );
  
  if(isLoading) return loadingComponent;
  if(isSuccess) return successComponent;
  return errorComponent;
}
function Home() {
  const [isLoading, setIsloading] = useState(false);
  const [isFirstTime, setIsFirstTime] = useState(true);
  const [isSuccess, setIsSuccess] = useState(false);
  const [isError, setIsError] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');
  const [email, setEmail] = useState('');

  function subscribeEmail(email: string) {
    return SubscriptionService.getInstance().subscribeEmail(email)
  }

  function resetStates() {
    setIsFirstTime(false);
    setIsloading(true);
    setIsSuccess(false);
    setIsError(false);
  }

  function handleFormSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    resetStates()
    subscribeEmail(email)
      .then(() => {
        setIsSuccess(true);
      })
      .catch((error) => {
        setIsError(true);
        if(error.status === CONFLICT) {
          setErrorMessage('Email já inscrito');
        } else {
          setErrorMessage('Erro no servidor');
        }
      })
      .finally(() => {
        setIsloading(false);
      })
  }

  let helperMessageElement: React.JSX.Element = selectHelperComponent(isLoading, isSuccess, isError, errorMessage);
  
  return (
    <div className='content'>
      <h1>Receba notificações de editais do Instituto Metrópole Digital</h1>
      <p>Informe o seu e-mail abaixo para receber notificações sobre novas oportunidades</p>
      <form onSubmit={handleFormSubmit}>
        <div className='email'>
          <div className='email-icon'>
            <img src={EmailIcon} alt="" />
          </div>
          <input type="email" placeholder='Email' className='email-input' value={email} onChange={(e) => setEmail(e.currentTarget.value)}/>
        </div>
        <button type='submit' disabled={isLoading} >
          <img src={SubscribeIcon} alt="" />
          Inscrever-se
        </button>
      </form>
      
      {!isFirstTime && helperMessageElement}
      
    </div>
  )
}

export default Home
