export default class ApiClient {
  constructor(appState, baseUrl) {
    this.appState = appState;
    this.baseUrl = baseUrl;
    this.defaultHeaders = {
      'Accept': 'application/json, text/plain, */*',
      'Content-Type': 'application/json',
    }
  }

  getHeaders(headers) {
    return Object.assign({
      Authentication: this.appState.isAuthenticated() ? this.appState.getAuthenticationToken() : ''
    }, this.defaultHeaders, headers)

  }

  getUrl(url) {
    return this.baseUrl + url;
  }

  get(url) {
    const options = {
      method: 'GET'
    };

    return this.doFetch(url, options);
  }

  post(url, data) {
    const options = {
      method: 'POST',
      body: JSON.stringify(data)
    };

    return this.doFetch(url, options);
  }

  doFetch(url, { headers = {}, method = 'GET', body = null } = {}) {
    const options = {
      method,
      headers: this.getHeaders(headers),
    };

    if (body) {
      options.body = body;
    }

    return fetch(this.getUrl(url), options)
      .then(response => {
        if (!response.ok) {
          throw new Error(response.body);
        }

        return response.json()
      });
  }
}

