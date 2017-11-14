import React, { Component } from 'react';

export default class ChatView extends Component {
  constructor(props) {
    super(props);
    this.state = {
      messages: []
    };

    this.updateMessage = this.updateMessage.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleInputKey = this.handleInputKey.bind(this);
  }

  componentDidMount() {
    this.props.onInit();
  }

  updateMessage(event) {
    this.setState({
      message: event.target.value
    });
  }

  handleSubmit(event) {
    event.preventDefault();
    this.sendMessage();
  }

  handleInputKey(event) {
    if (event.which === 13
      && !event.getModifierState('Shift')) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  sendMessage() {
    this.props.onSendMessage(this.state.message);
    this.setState({
      message: ''
    });

  }

  renderMessage(message, index) {
    const timestamp = new Date(message.timestamp.epochSecond * 1000).toLocaleTimeString();
    return (
      <div key={index} className="chat-message">
        <div className="chat-message-meta">
          {timestamp}&nbsp;
          {message.sender}
        </div>
        <div className="chat-message-body">
          {message.body.split('\n').map((item, key) => <span key={key}>{item}<br/></span>)}
        </div>
      </div>
    )
  }

  render() {
    return (
      <div className="chat">
        <div className="chat-messages">
          <div className="chat-messages-wrapper">
            {this.props.messages.map(this.renderMessage)}
          </div>
        </div>
        <div className="chat-message-input">
          <form onSubmit={this.handleSubmit}>
            <textarea onKeyUp={this.handleInputKey} onChange={this.updateMessage} value={this.state.message}></textarea>
            <button>Send</button>
          </form>
        </div>
      </div>
    )
  }
}

