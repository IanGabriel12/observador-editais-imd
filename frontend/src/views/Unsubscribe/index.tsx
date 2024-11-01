import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import SubscriptionService from "../../SubscriptionService";

function Unsubscribe() {
    const [searchParams, setSearchParams] = useSearchParams();
    const [message, setMessage] = useState('');

    useEffect(handlePageLoad, []);

    function handlePageLoad() {
        const token = searchParams.get('token');
        if(!token) {
            setMessage('Não foi possível cancelar a inscrição');
        } else {
            unsubscribeEmail(searchParams.get('token')!);
        }

        return () => {}
    }

    function unsubscribeEmail(token: string) {
        setMessage('Cancelando inscrição...')
        SubscriptionService
            .getInstance()
            .unsubscribeEmail(token)
            .then(() => setMessage('Inscrição removida com sucesso'))
            .catch(() => setMessage('Não foi possível cancelar a inscrição'))
    }

    return <h1 className='unsubscribe-message'>{message}</h1>
}

export default Unsubscribe;