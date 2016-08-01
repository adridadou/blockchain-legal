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

contract LegalContractManager is mortal {

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

    function getNbNamespaces() constant returns (uint){
        return contexts.length;
    }

    function getNamespace(uint id) constant returns (string) {
        return contexts.ids[id];
    }

    function getNbProjects(string namespace) constant returns (uint){
        return contexts.spaces[namespace].nbProjects;
    }

    function getProject(string namespace, uint id) constant returns (string) {
        return contexts.spaces[namespace].names[id];
    }

    function getNbVersions(string namespace, string project) constant returns (uint) {
        return contexts.spaces[namespace].projects[project].nbVersions;
    }

    function getVersion(string namespace, string project, uint id) constant returns(string) {
        return contexts.spaces[namespace].projects[project].versions[id];
    }

    function getOwner(string namespace) constant returns(address) {
        return contexts.owners[namespace];
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
