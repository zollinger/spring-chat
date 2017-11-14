import React, { Component } from 'react';
import './App.css';

import AppState from './AppState';
import ApiClient from './ApiClient';
import AuthenticationService from './AuthenticationService';
import LoginView from './LoginView';
import ChatView from './ChatView';
import MessageService from "./MessageService";

const baseUrl = 'http://localhost:8080';
const socket = baseUrl + '/ws';

const appState = new AppState();
const apiClient = new ApiClient(appState, baseUrl);
const authenticationService = new AuthenticationService(apiClient);
const messageService = new MessageService(appState, socket);

class App extends Component {
  constructor(props) {
    super(props);

    this.login = this.login.bind(this);
    this.updateState = this.updateState.bind(this);
    this.initChat = this.initChat.bind(this);

    this.state = appState.getState();
  }

  login(username, password) {
    authenticationService.login(username, password)
      .then(result => appState.setAuthenticationToken(result.accessToken))
      .then(() => this.updateState())
      .catch(error => {
        appState.setError(error);
        this.updateState();
      })
  }

  sendMessage(message) {
    messageService.sendMessage(message);
  }

  updateState() {
    this.setState(appState.getState());
  }

  initChat() {
    messageService.connect(appState.getAuthenticationToken())
      .then(() => {
        messageService.subscribe(message => {
          appState.addMessage(message);
          this.updateState();
        })
      });
  }

  render() {
    const view = appState.isAuthenticated() ? <ChatView messages={this.state.messages} onInit={this.initChat} onSendMessage={this.sendMessage}/> : <LoginView onSubmit={this.login} />;

    return (
      <div className="app">
        <header>
          <h1>A Simple Chat</h1>
          <p>
            Made with Spring and React to look into Spring and Websockets.
          </p>
        </header>
        <main>
          {view}
        </main>
      </div>
    );
  }
}

export default App;
