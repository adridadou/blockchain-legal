let App = React.createClass({
  onChangeProjectName: function(event) {
    this.setState({
        name: event.target.value,
        version: this.state.version
    });
  },
  onChangeVersion: function(event) {
    this.setState({
        name: this.state.name,
        version: event.target.value
    });
  },
  getInitialState: function() {
    return {value: ''};
  },
  getProject: function(name) {
    return this.props.projects.filter((project) => name === project.name)[0];
  },
  render: function() {
    return (<table>
                <tbody>
                    <tr>
                        <td>project name:</td><td><input type="text" onChange={this.onChangeProjectName} /></td>
                    </tr>
                    <tr>
                        <td>version:</td><td><input type="text" onChange={this.onChangeVersion} /></td>
                    </tr>
                    <tr>
                        <td colSpan="2" className="button-table"><CreateProject name={this.state.value} namespace={namespace} /></td>
                    </tr>
                </tbody>
            </table>);
  }
 });

let CreateProject = React.createClass({
    render: function() {
        return <a href={'/project/create/' + this.props.namespace  + '/' + this.props.name + '/' + this.props.version } className="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored mdl-color-text--white" >Create new project</a>;
    }
});

let ProjectTable = React.createClass({
    render: function() {
        let lines = this.props.projects.map((project) => (<tr key={project.name}>
            <td>{project.name}</td>
            <td>{project.nbVersions}</td>
            <td><a href={'/project/' + project.name + '/create'} className="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored mdl-color-text--white" >new Project version</a></td>
        </tr>))

        return (
        <table className="responstable">
            <thead>
                 <tr>
                   <th>Name</th>
                   <th >nb versions</th>
                   <th>Action</th>
                 </tr>
             </thead>
             <tbody>
                 {lines}
             </tbody>
       </table>);
    }
});


$.get('/projects/' + namespace).then((projects) => {
    ReactDOM.render(<ProjectTable projects={projects} namespace={namespace} />, document.getElementById('table'));
    ReactDOM.render(<App projects={projects} namespace={namespace}/>, document.getElementById('header'));
});
