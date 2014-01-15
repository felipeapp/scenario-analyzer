	<ul>
		<li> Produções
			<ul>
				<li><ufrn:link action="/prodocente/producao/SubTipoArtistico/lista.jsf" value="SubTipo Artístico" aba="administracao"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoEvento/lista.jsf" value="Tipo de Evento" aba="administracao"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoInstancia/lista.jsf" value="Tipo de Instância" aba="administracao"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoMembroColegiado/lista.jsf" value="Tipo de Membro de Colegiado" aba="administracao"/></li>
				<li> <h:commandLink value="Tipo de Participação" action="#{tipoParticipacao.listar}" onclick="setAba('administracao')"/> </li>
				<%-- <li><ufrn:link action="/prodocente/producao/TipoParticipacao/lista.jsf" value="Tipo de Participação" aba="administracao"/></li>--%>
				<li><ufrn:link action="/prodocente/producao/TipoParticipacaoOrganizacaoEventos/lista.jsf" value="Tipo de Organização em Eventos" aba="administracao"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoParticipacaoSociedade/lista.jsf" value="Tipo de Participação em Sociedade" aba="administracao"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoPeriodico/lista.jsf" value="Tipo de Periódico" aba="administracao"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoProducaoTecnologica/lista.jsf" value="Tipo de Produção Tecnológica" aba="administracao"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoRegiao/lista.jsf" value="Tipo de Região" aba="administracao"/></li>
			</ul>
	</ul>
	
	<ul>
		<li>Atividades
		<ul>
			<li>Tipo de Atividade de Extensão**</li>
			<li><ufrn:link action="/prodocente/atividades/ProgramaResidenciaMedica/lista.jsf" value="Programa de Residência Médica" aba="administracao"/></li>
			<li><ufrn:link action="/prodocente/atividades/TipoBolsaProdocente/lista.jsf" value="Tipo de Bolsa" aba="administracao"/></li>
			<li><ufrn:link action="/prodocente/atividades/TipoChefia/lista.jsf" value="Tipo de Chefia" aba="administracao"/></li>
			<li>Tipo de Membro de Atividade de Extensão**</li>
			<li><ufrn:link action="/prodocente/atividades/TipoOrientacao/lista.jsf" value="Tipo de Orientação" aba="administracao"/></li>
			<li><ufrn:link action="/prodocente/atividades/TipoParecer/lista.jsf" value="Tipo de Parecer" aba="administracao"/></li>
			<li><ufrn:link action="/prodocente/atividades/TipoQualificacao/lista.jsf" value="Tipo de Qualificação" aba="administracao"/></li>
		</ul>
	</ul>
	
	<ul>
		<li>Relatórios
		<ul>
			<li><ufrn:link action="prodocente/producao/relatorios/produtividade/ipi/lista.jsf" value="Cálculo do Ipi para um docente" aba="administracao"/></li>
			<li><ufrn:link action="prodocente/producao/relatorios/produtividade/cadastro/lista.jsf" value="Cadastro de Relatórios" aba="administracao"/></li>
		</ul>
	</ul>
	
	<ul>
		<li>Configuração Web Service CNPq
		<ul>
			<li><h:commandLink action="#{ atualizarCredenciaisCnpqMBean.iniciar }" value="Atualizar Credenciais" onclick="setAba('administracao')" /></li>
		</ul>
	</ul>