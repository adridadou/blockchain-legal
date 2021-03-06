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
                        <td colSpan="2" className="button-table"><CreateProject name={this.state.name} namespace={namespace} version={this.state.version} /></td>
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
            <td><a href={'/package/page/' + this.props.namespace  + '/' + project.name } className="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored mdl-color-text--white" >Open</a></td>
        </tr>))

        return (
        <table className="responstable">
            <thead>
                <tr><th colSpan="3cd ta ">{'Projects in ' + this.props.namespace}</th></tr>
                 <tr>
                   <th>Name</th>
                   <th>nb versions</th>
                   <th>Action</th>
                 </tr>
             </thead>
             <tbody>
                 {lines}
             </tbody>
       </table>);
    }
});

let FilesTable = React.createClass({
    render: function() {
        let lines = this.props.files.map((file) => (<tr key={file}>
            <td>{file}</td>
            <td><a href={'/project/file/remove/' + this.props.namespace + '/' + file} className="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored mdl-color-text--white" >Remove file</a></td>
        </tr>))

        return (
        <table className="responstable">
            <thead>
                <tr><th colSpan="2">Files ready for the project</th></tr>
                 <tr>
                   <th>Filename</th>
                   <th >Action</th>
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

$.get('/project/files/list').then((files) => {
    ReactDOM.render(<FilesTable files={files} namespace={namespace}/>, document.getElementById('files'))
})
