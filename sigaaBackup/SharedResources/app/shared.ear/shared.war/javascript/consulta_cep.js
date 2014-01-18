var ConsultadorCep = function () {
	var urlConsulta = null;
	var endereco = null;
	var campos = {};
	var posProcessamento = null;

	var popularDados = function( transport ) {
		endereco = eval('(' + transport.responseText + ')');
		campos.cep.value = endereco.cep;
		campos.logradouro.value = endereco.logradouro;
		campos.bairro.value = endereco.bairro;
		if (campos.municipio) {
			campos.municipio.value = endereco.idMunicipio;
		}
		campos.uf.value = endereco.idUf;
		if (campos.municipio.value != endereco.idMunicipio) {
			campos.municipio[campos.municipio.options.length] = new Option(endereco.nomeMunicipio, endereco.idMunicipio, false, true );
		}
		
		if ( posProcessamento ) {
			posProcessamento();
		}
	};

	var showIndicator = function(transport) {
		$('cepIndicator').show();
	};

	var hideIndicator = function(transport) {
		$('cepIndicator').hide();
	};

	return {
		// O construtor recebe os ids dos campos referentes ao endereco que podem ser populados pelo CEP
		init : function (url, cep, logradouro, bairro, municipio, uf, posConsulta) {
			urlConsulta = url;
			campos = {
				'cep': $(cep),
				'logradouro' : $(logradouro),
				'bairro' : $(bairro),
				'municipio' : $(municipio),
				'uf' : $(uf)
			};
			posProcessamento = posConsulta;
		},
		consultar : function () {
			showIndicator();
			new Ajax.Request(urlConsulta, {
		 		method: 'get',
		 		parameters: {cep: campos.cep.value },
				onSuccess: popularDados,
				onComplete: hideIndicator });
		},
		getEndereco : function() { return endereco; },
		getCampos : function() { return campos; }
	};
}();

var ConsultadorCepNome = function () {
	var urlConsulta = null;
	var endereco = null;
	var campos = {};

	var popularDados = function( transport ) {
		endereco = eval('(' + transport.responseText + ')');
		campos.cep.value = endereco.cep;
		campos.logradouro.value = endereco.logradouro;
		campos.bairro.value = endereco.bairro;
		
		if(campos.municipio){
			if(campos.municipio.type == 'select-one'){
				for(var i = 0; i < campos.municipio.options.length; i++){
					if(campos.municipio.options[i].id == idLocalidade){
						campos.municipio.value = endereco.idLocalidade;
						campos.municipio.options.selectedIndex = i;
					}
				}
	
			}else{
				campos.municipio.value = endereco.nomeMunicipio;
			}
		}

		if(campos.uf.type == 'select-one'){
			campos.uf.value = endereco.idUf;
			for(var i = 0; i < campos.uf.options.length; i++){
				if(campos.uf.options[i].id == idUf){
					campos.uf.options.selectedIndex = i;
				}
			}

		}else{
			campos.uf.value = endereco.nomeUf;
		}
		if (campos.municipio.value != endereco.idMunicipio) {
			campos.municipio[campos.municipio.options.length] = new Option(endereco.nomeMunicipio, endereco.idMunicipio, false, true );
		}
	};

	var showIndicator = function(transport) {
		$('cepIndicator').show();
	};

	var hideIndicator = function(transport) {
		$('cepIndicator').hide();
	};

	return {
		// O construtor recebe os ids dos campos referentes ao endereco que podem ser populados pelo CEP
		init : function (url, cep, logradouro, bairro, municipio, uf) {
			urlConsulta = url;
			campos = {
				'cep': $(cep),
				'logradouro' : $(logradouro),
				'bairro' : $(bairro),
				'municipio' : $(municipio),
				'uf' : $(uf)
			};
		},
		consultar : function () {
			showIndicator();
			new Ajax.Request(urlConsulta, {
		 		method: 'get',
		 		parameters: {cep: campos.cep.value },
				onSuccess: popularDados,
				onComplete: hideIndicator });
		},
		getEndereco : function() { return endereco; },
		getCampos : function() { return campos; }
	};
}();