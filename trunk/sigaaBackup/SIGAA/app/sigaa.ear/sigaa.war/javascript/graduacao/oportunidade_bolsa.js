function mostrarFiltrosOportunidades() {
	var select = $('busca:tipo');
	
	if (select.value == 1) {
		mostrarMonitoria();
	}
	if (select.value == 2) {
		mostrarExtensao();
	}
	if (select.value == 3) {
		mostrarPesquisa();
	}
	if (select.value == 4) {
		mostrarApoioTecnico();
	}
	if (select.value == 5){
		mostrarAssociadas();
	}
}

function mostrarMonitoria() {
	$('linhaCentro').show();
	$('linhaServidor').show();
	$('linhaCodComponente').show();
	
	$('linhaDepartamento').hide();
	$('linhaAreaConhecimento').hide();
	$('linhaUnidade').hide();
	$('linhaVoltadaCurso').hide();
	$('linhaAnoAtividade').hide();	
	$('linhaTipoProjeto').hide();
}

function mostrarExtensao() {
	$('linhaCentro').show();
	$('linhaDepartamento').show();
	$('linhaServidor').show();
	$('linhaTipoAtividade').show();
	$('linhaAnoAtividade').show();	
	
	$('linhaCodComponente').hide();
	$('linhaUnidade').hide();
	$('linhaVoltadaCurso').hide();
	$('linhaAreaConhecimento').hide();	
	$('linhaTipoProjeto').hide();
}	

function mostrarPesquisa() {
	
	$('linhaCentro').show();
	$('linhaDepartamento').show();
	$('linhaServidor').show();
	$('linhaAreaConhecimento').show();
	$('linhaTipoAtividade').show();
	
	$('linhaCodComponente').hide();
	$('linhaTipoAtividade').hide();
	$('linhaUnidade').hide();
	$('linhaVoltadaCurso').hide();
	$('linhaAnoAtividade').hide();	
	$('linhaTipoProjeto').hide();
}

function mostrarApoioTecnico() {

	$('linhaUnidade').show();
	$('linhaVoltadaCurso').show();
	
	$('linhaCentro').hide();
	$('linhaAreaConhecimento').hide();
	$('linhaServidor').hide();
	$('linhaDepartamento').hide();
	$('linhaTipoAtividade').hide();
	$('linhaCodComponente').hide();
	$('linhaTipoAtividade').hide();
	$('linhaAnoAtividade').hide();
	$('linhaTipoProjeto').hide();
	
}

function mostrarAssociadas() {
	
	$('linhaCentro').show();
	$('linhaDepartamento').show();
	$('linhaServidor').show();
	$('linhaAnoAtividade').show();
	
	$('linhaTipoAtividade').hide();
	$('linhaCodComponente').hide();
	$('linhaUnidade').hide();
	$('linhaVoltadaCurso').hide();
	$('linhaAreaConhecimento').hide();	
	$('linhaTipoProjeto').hide();
	
}	