import Stomp from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export default class MessageService {
  constructor(appState, socket){
    this.appState = appState;
    this.socket = socket;
    this.stompClient = null;
  }

  connect() {
    const socket = new SockJS(this.socket);
    this.stompClient = Stomp.over(socket);

    return new Promise((resolve, reject) => {
      this.stompClient.connect(
        this.getHeaders(),
        frame => resolve(frame),
        error => reject(error)
      );
    });
  }

  subscribe(cb) {
    return this.stompClient.subscribe('/topic/messages', message => cb(JSON.parse(message.body)));
  }

  sendMessage(message) {
    const data = {
      body: message
    };
    this.stompClient.send('/app/message', this.getHeaders(), JSON.stringify(data));
  }

  getHeaders(additionalHeaders = {}) {
    return Object.assign({ Authentication: this.appState.getAuthenticationToken() }, additionalHeaders);
  }
}