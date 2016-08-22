let FileTable = React.createClass({
    render: function() {
        let lines = this.props.files.map((file) => (<tr key={file.name}>
            <td>{file.name}</td>
            <td><a href={'/ipfs/'+ namespace + '/' + project + '/' + file.name} className="mdl-button mdl-js-button mdl-button--raised mdl-js-ripple-effect mdl-button--colored mdl-color-text--white" >GET</a></td>
        </tr>))

        return (
        <table className="responstable">
            <thead>
                <tr><th colSpan="2">{'Packages of ' + project + ' in ' + namespace}</th></tr>
                 <tr>
                   <th>Version</th>
                   <th>Action</th>
                 </tr>
             </thead>
             <tbody>
                 {lines}
             </tbody>
       </table>);
    }
});


$.get('/package/' + namespace + '/'+ project).then((files) => {
    ReactDOM.render(<FileTable files={files} namespace={namespace}/>, document.getElementById('files'))
})
