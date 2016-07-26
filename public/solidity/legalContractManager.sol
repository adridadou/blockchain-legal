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

	mapping(string => address) owners;
	mapping(string => mapping(string => mapping(string => string))) context;

	modifier nameSpaceOwner(string namespace, address owner) {
		if(owners[namespace] != owner) throw;
		_
	}

	function LegalContractManager() {
        owner = currentUser();
	}

	function getSource(string namespace, string name, string version) constant returns (string) {
		return context[namespace][name][version];
	}

	function canWrite(string namespace, address user) constant returns (bool) {
		return owners[namespace] == user;
	}

	function register(string namespace, string name, string version, string ipfs) {
		if(bytes(getSource(namespace,name,version)).length != 0 || !canWrite(namespace,currentUser())) throw;

		context[namespace][name][version] = ipfs;
	}

	function createNamespace(string namespace, address owner) onlyowner nameSpaceOwner(namespace,0) {
		owners[namespace] = owner;
	}

	function changeOwner(string namespace, address newOwner) nameSpaceOwner(namespace,currentUser()){
		owners[namespace] = newOwner;
	}
}
