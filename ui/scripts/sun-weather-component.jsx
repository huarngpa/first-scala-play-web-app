import React from 'react';
import axios from 'axios';

class SunWeatherComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      dateStr: null,
      sunrise: null,
      sunset: null,
      temperature: null,
      requests: null
    };
  }

  componentDidMount = () => {
    axios.get('/data').then((response) => {
      const json = response.data;
      this.setState({
        dateStr: json.dateStr,
        sunrise: json.sunInfo.sunrise,
        sunset: json.sunInfo.sunset,
        temperature: json.temperature,
        requests: json.requests
      });
    })
  };

  render = () => {
    return <table className="table is-bordered is-striped is-narrow is-hoverable is-fullwidth">
      <tbody>
      <tr>
        <td>Date</td>
        <td>{this.state.dateStr}</td>
      </tr>
      <tr>
        <td>Sunrise time</td>
        <td>{this.state.sunrise}</td>
      </tr>
      <tr>
        <td>Sunset time</td>
        <td>{this.state.sunset}</td>
      </tr>
      <tr>
        <td>Current temperature</td>
        <td>{this.state.temperature}</td>
      </tr>
      <tr>
        <td>Requests</td>
        <td>{this.state.requests}</td>
      </tr>
      </tbody>
    </table> 
  }
}

export default SunWeatherComponent;
