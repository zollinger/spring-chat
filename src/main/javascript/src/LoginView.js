import React, { Component } from 'react';

export default class LoginView extends Component {
  constructor(props) {
    super(props);
    this.state = {
      username: '',
      password: '',
    };

    this.updatePassword = this.updatePassword.bind(this);
    this.updateUserName = this.updateUserName.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  updateUserName(event) {
    this.setState({
      username: event.target.value
    });
  }

  updatePassword(event) {
    this.setState({
      password: event.target.value
    });
  }

  handleSubmit(event) {
    event.preventDefault();
    this.props.onSubmit(this.state.username, this.state.password);
  }

  render() {
    return (
      <div className="login-form">
        <form onSubmit={this.handleSubmit}>
          <p>
            <label>
              Name:
              <input onChange={this.updateUserName}  type="text" name="username" value={this.state.username} />
            </label>
          </p>
          <p>
            <label>
              Password:
              <input onChange={this.updatePassword}  type="password" name="password" value={this.state.password} />
            </label>
          </p>
          <p>
            <button>Login</button>
          </p>
        </form>
      </div>
    )
  }
}

