let App = React.createClass({
  onChange: function(event) {
    this.setState({value: event.target.value});
  },
  getInitialState: function() {
    return {value: ''};
  },
  getNamespace: function(name) {
    return this.props.namespaces[name] ? {
        name: name,
        owner: this.props.namespaces[name]
    } : undefined;
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

let NamespaceInfo = React.createClass({
    render: function(){ return (<ul className="namespace">
                    <li>{'name: ' + this.props.namespace.name}</li>
                    <li>{'owner: ' + this.props.namespace.owner}</li>
                  </ul>);
            }
});

let NamespaceTable = React.createClass({
    render: function() {
        let lines = this.props.namespaces.map((namespace) => (<tr key={namespace.name}>
            <td>{namespace.name}</td>
            <td>{namespace.owner}</td>
            <td><a href={'/project/' + namespace.name + '/create'} className="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored mdl-color-text--white" >new Project</a></td>
        </tr>))

        return (
        <table className="responstable">
            <thead>
                 <tr>
                   <th>Name</th>
                   <th >owner</th>
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
