contract abstract {}

contract owned is abstract {
  address owner;

  modifier onlyowner() {
    if (currentUser()==owner) _
  }

  function currentUser() returns (address) {
		return msg.sender;
	}
}

contract mortal is abstract, owned {
  function kill() {
    if (msg.sender == owner) suicide(owner);
  }
}

contract LegalContractManagerInterface is mortal {
	/*
		Get the source of the package. Right now this is an IPFS hash but it should be possible to have any way of retrieving the source (HTTP, FTP etc ...)

		params:
		- Namespace: The namespace of the package you are looking for
		- name: the name of the legal documents
		- version: the version of the legal documents
	*/
	function getSource(string namespace, string name, string version) constant returns (string);

	/*
		Register a new package.

		params:
		- Namespace: The namespace of the package you are looking for
		- name: the name of the legal documents
		- version: the version of the legal documents
	*/
	function register(string namespace, string name, string version, string ipfs);

	/*
		Create a new namespace and makes the tx.origin the owner of this namespace
	*/
	function createNamespace(string namespace, address owner);

	/*
		Passes the ownership of a namespace to someone else
	*/
	function changeOwner(string namespace, address newOwner);

	function canWrite(string namespace, address user) constant returns (bool);
}

contract LegalContractManager is LegalContractManagerInterface {

	struct project {
		uint nbVersions;
		mapping(uint => string) versions;
		mapping(string => string) packages;
	}

	struct namespace{
		uint nbProjects;
		mapping(uint => string) names;
		mapping(string => project) projects;
	}

	struct namespaces {
		uint length;
		mapping(uint => string) ids;
		mapping(string => address) owners;
		mapping(string => namespace) spaces;
	}

	namespaces contexts;

	modifier nameSpaceOwner(string namespace, address owner) {
		if(contexts.owners[namespace] != owner) throw;
		_
	}

	function LegalContractManager() {
        owner = currentUser();
	}

	function getSource(string namespace, string project, string version) constant returns (string) {
		return contexts.spaces[namespace].projects[project].packages[version];
	}

	function canWrite(string namespace, address user) constant returns (bool) {
		return contexts.owners[namespace] == user;
	}

	function register(string namespace, string project, string version, string ipfs) {
		if(bytes(getSource(namespace,project,version)).length != 0 || !canWrite(namespace,currentUser())) throw;

		registerProject(namespace,project);

		contexts.spaces[namespace].projects[project].packages[version] = ipfs;
		contexts.spaces[namespace].projects[project].versions[contexts.spaces[namespace].projects[project].nbVersions] = version;
		contexts.spaces[namespace].projects[project].nbVersions++;
	}

	function createNamespace(string namespace, address owner) onlyowner nameSpaceOwner(namespace,0) {
		contexts.owners[namespace] = owner;
		contexts.ids[contexts.length] = namespace;
		contexts.length++;
	}

	function registerProject(string namespace, string project) private {
		if(contexts.spaces[namespace].projects[project].nbVersions == 0) {
			contexts.spaces[namespace].names[contexts.spaces[namespace].nbProjects] = project;
			contexts.spaces[namespace].nbProjects++;
		}
	}

	function changeOwner(string namespace, address newOwner) nameSpaceOwner(namespace,currentUser()){
		contexts.owners[namespace] = newOwner;
	}
}
