import axios, { AxiosInstance } from "axios";

export default class SubscriptionService {
    public static instance: SubscriptionService

    private api: AxiosInstance = axios.create({
        baseURL: import.meta.env.VITE_API_URL
    });

    public static getInstance() {
        if(!this.instance) {
            this.instance = new SubscriptionService();
        }
        return this.instance;
    }

    public subscribeEmail(email: string) {
        return this.api.post('/subscription', {
            email
        });
    }

    public unsubscribeEmail(removeToken: string) {
        return this.api.delete('/subscription', {
            data: {
                removeToken
            }
        });
    }


}