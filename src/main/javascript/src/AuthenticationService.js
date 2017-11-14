export default class AuthenticationService {
  constructor(apiClient) {
    this.apiClient = apiClient;
  }

  login(username, password) {
    return this.apiClient.post('/auth/login', { username, password });
  }
}