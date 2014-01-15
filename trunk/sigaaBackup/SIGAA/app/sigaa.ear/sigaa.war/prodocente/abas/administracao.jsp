	<ul>
		<li> Produ��es
			<ul>
				<li><ufrn:link action="/prodocente/producao/SubTipoArtistico/lista.jsf" value="SubTipo Art�stico" aba="administracao"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoEvento/lista.jsf" value="Tipo de Evento" aba="administracao"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoInstancia/lista.jsf" value="Tipo de Inst�ncia" aba="administracao"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoMembroColegiado/lista.jsf" value="Tipo de Membro de Colegiado" aba="administracao"/></li>
				<li> <h:commandLink value="Tipo de Participa��o" action="#{tipoParticipacao.listar}" onclick="setAba('administracao')"/> </li>
				<%-- <li><ufrn:link action="/prodocente/producao/TipoParticipacao/lista.jsf" value="Tipo de Participa��o" aba="administracao"/></li>--%>
				<li><ufrn:link action="/prodocente/producao/TipoParticipacaoOrganizacaoEventos/lista.jsf" value="Tipo de Organiza��o em Eventos" aba="administracao"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoParticipacaoSociedade/lista.jsf" value="Tipo de Participa��o em Sociedade" aba="administracao"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoPeriodico/lista.jsf" value="Tipo de Peri�dico" aba="administracao"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoProducaoTecnologica/lista.jsf" value="Tipo de Produ��o Tecnol�gica" aba="administracao"/></li>
				<li><ufrn:link action="/prodocente/producao/TipoRegiao/lista.jsf" value="Tipo de Regi�o" aba="administracao"/></li>
			</ul>
	</ul>
	
	<ul>
		<li>Atividades
		<ul>
			<li>Tipo de Atividade de Extens�o**</li>
			<li><ufrn:link action="/prodocente/atividades/ProgramaResidenciaMedica/lista.jsf" value="Programa de Resid�ncia M�dica" aba="administracao"/></li>
			<li><ufrn:link action="/prodocente/atividades/TipoBolsaProdocente/lista.jsf" value="Tipo de Bolsa" aba="administracao"/></li>
			<li><ufrn:link action="/prodocente/atividades/TipoChefia/lista.jsf" value="Tipo de Chefia" aba="administracao"/></li>
			<li>Tipo de Membro de Atividade de Extens�o**</li>
			<li><ufrn:link action="/prodocente/atividades/TipoOrientacao/lista.jsf" value="Tipo de Orienta��o" aba="administracao"/></li>
			<li><ufrn:link action="/prodocente/atividades/TipoParecer/lista.jsf" value="Tipo de Parecer" aba="administracao"/></li>
			<li><ufrn:link action="/prodocente/atividades/TipoQualificacao/lista.jsf" value="Tipo de Qualifica��o" aba="administracao"/></li>
		</ul>
	</ul>
	
	<ul>
		<li>Relat�rios
		<ul>
			<li><ufrn:link action="prodocente/producao/relatorios/produtividade/ipi/lista.jsf" value="C�lculo do Ipi para um docente" aba="administracao"/></li>
			<li><ufrn:link action="prodocente/producao/relatorios/produtividade/cadastro/lista.jsf" value="Cadastro de Relat�rios" aba="administracao"/></li>
		</ul>
	</ul>
	
	<ul>
		<li>Configura��o Web Service CNPq
		<ul>
			<li><h:commandLink action="#{ atualizarCredenciaisCnpqMBean.iniciar }" value="Atualizar Credenciais" onclick="setAba('administracao')" /></li>
		</ul>
	</ul>