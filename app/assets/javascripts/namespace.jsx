let App = React.createClass({
  onChange: function(event) {
    this.setState({value: event.target.value});
  },
  getInitialState: function() {
    return {value: ''};
  },
  getNamespace: function(name) {
    return this.props.namespaces.filter((namespace) => name === namespace.name)[0];
  },
  render: function() {
    var createButton = <div></div>;
    if(this.state.value !== '' && this.getNamespace(this.state.value) === undefined) {
      createButton = <CreateNamespace name={this.state.value} />;
    }
    return (<div><div><label>New namespace:<input type="text" onChange={this.onChange} /></label></div><div>{createButton}</div><NamespaceTable namespaces={this.props.namespaces} /></div>);
  }
 });

let CreateNamespace = React.createClass({
    render: function() {
        return <a href={'/namespace/create/' + this.props.name} className="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored mdl-color-text--white" >Create {this.props.name}</a>;
    }
});

let NamespaceTable = React.createClass({
    render: function() {
        let lines = this.props.namespaces.map((namespace) => (<tr key={namespace.name}>
            <td>{namespace.name}</td>
            <td>{namespace.owner}</td>
            <td>{namespace.nbProjects}</td>
            <td><a href={'/project/' + namespace.name} className="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored mdl-color-text--white" >Projects</a></td>
        </tr>))

        return (
        <table className="responstable">
            <thead>
                 <tr>
                   <th>Name</th>
                   <th>owner</th>
                   <th>nb projects</th>
                   <th>Action</th>
                 </tr>
             </thead>
             <tbody>
                 {lines}
             </tbody>
       </table>);
    }
});


$.get('/namespaces').then((namespaces) => {
    ReactDOM.render(<App namespaces={namespaces}/>, document.getElementById('content'));
});
