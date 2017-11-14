const defaultState = {
  authenticationToken: null,
  user: null,
  messages: []
};

const AUTH_TOKEN_KEY = 'chatAuthToken';

export default class AppState {
  constructor(state = {}) {
    this.state = Object.assign({}, defaultState, state);
  }

  addMessage(message) {
    this.state.messages.push(message);
  }

  getState() {
    return Object.assign({}, this.state);
  }

  setAuthenticationToken(token) {
    localStorage.setItem(AUTH_TOKEN_KEY, token);
  }

  getAuthenticationToken() {
    return localStorage.getItem(AUTH_TOKEN_KEY);
  }

  isAuthenticated() {
    return !!this.getAuthenticationToken();
  }

  setError(error) {
    this.state.error = error;
  }
}