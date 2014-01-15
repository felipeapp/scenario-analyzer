<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<ul>
	<c:if test="${acesso.administradorDAE || acesso.ppg}">
		<li> Opera��es Administrativas 
			<ul>
			<li> <h:commandLink action="#{parametrosRegistroDiploma.atualizar}" value="Listar/Alterar Par�metros"  onclick="setAba('registro')"/> </li>
			<li> <h:commandLink action="#{logGeracaoDiploma.auditarImpressao}" value="Auditar a Gera��o de Diplomas"  onclick="setAba('registro')"/> </li>
			<li> <h:commandLink action="#{requisicaoNumeroRegistro.listar}" value="Auditar a Requisi��o de N�meros para Registro de Diplomas"  onclick="setAba('registro')"/> </li>
			<li> <h:commandLink action="#{buscaRegistroDiploma.iniciarAlterarRegistroDiploma}" value="Alterar um Registro de Diploma"  onclick="setAba('registro')"/> </li>
			</ul>
		</li>
		<li> Assinaturas no Diploma 
			<ul>
			<li> <h:commandLink action="#{responsavelAssinaturaDiplomasBean.preCadastrar}" value="Cadastrar"  onclick="setAba('registro')"/> </li>
			<li> <h:commandLink action="#{responsavelAssinaturaDiplomasBean.listar}" value="Listar/Alterar Nomes"  onclick="setAba('registro')"/> </li>
			</ul>
		</li>
	</c:if>
		<li> Dados do Discente
			<ul>
				<li> <h:commandLink action="#{ alteracaoDadosDiscente.iniciar}" value="Atualizar Dados Pessoais" onclick="setAba('registro')"/> </li>
				<c:if test="${acesso.dae}">
					<li> <h:commandLink action="#{historicoDiscente.iniciar}" value="Consultar Dados do Aluno" onclick="setAba('registro')"/> </li>
				</c:if>
			</ul>
		</li>
	<li> Livro de Registro de Diplomas
		<ul>
		<li> <h:commandLink action="#{livroRegistroDiplomas.preCadastrar}" value="Abrir Livro"  onclick="setAba('registro')"/> </li>
		<li> <h:commandLink action="#{livroRegistroDiplomas.listar}" value="Gerenciar Livros"  onclick="setAba('registro')"/> </li>
		</ul>
	</li>
	<li> Registro de Diplomas
		<ul>
		<ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO} %>">
			<li> <h:commandLink action="#{requisicaoNumeroRegistro.preCadastrar}" value="Requisitar N�mero para Registro de Diploma Externo"  onclick="setAba('registro')" /> </li>
		</ufrn:checkRole>
		<li> <h:commandLink action="#{registroDiplomaIndividual.preCadastrar}" value="Registrar Diploma Individual"  onclick="setAba('registro')"/> </li>
		<ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO, SigaaPapeis.GESTOR_DIPLOMAS_LATO} %>">
			<li> <h:commandLink action="#{registroDiplomaColetivo.preCadastrar}" value="Registrar Diploma Coletivo (Turma de Cola��o)"  onclick="setAba('registro')"/> </li>
		</ufrn:checkRole>
		<li> <h:commandLink action="#{buscaRegistroDiploma.iniciarBusca}" value="Buscar por Registros de Diplomas"  onclick="setAba('registro')"/> </li>
		<li> <h:commandLink action="#{registroDiplomaColetivo.iniciarBusca}" value="Buscar por Registros de Diplomas Coletivo"  onclick="setAba('registro')"/> </li>
		<li> <h:commandLink action="#{buscaRegistroDiploma.iniciarEditarObservacao}" value="Editar Observa��o do Registro de Diploma"  onclick="setAba('registro')"/> </li>
		<c:if test="${acesso.administradorDAE}">
			<li> <h:commandLink action="#{buscaRegistroDiploma.iniciarRemover}" value="Remover um Registro de Diploma"  onclick="setAba('registro')" /> </li>
		</c:if>
		<!-- <li>  Registro de Diplomas Externos (Indipon�vel) </li> -->
		</ul>
	</li>
	<li> Cadastro de Registro de Diplomas Antigos
		<ul>
		<li> <h:commandLink action="#{registroDiplomaIndividual.preCadastrarRegistroAntigo}" value="Registro de Diploma Antigo"  onclick="setAba('registro')"/> </li>
		</ul>
	</li>
	<li> Impress�o de Diplomas
		<ul>
		<li> <h:commandLink action="#{impressaoDiploma.iniciarImpressaoDiplomaIndividual}" value="Impress�o de Diploma Individual"  onclick="setAba('registro')"/> </li>
		<li> <h:commandLink action="#{impressaoDiploma.iniciarImpressaoDiplomaColetivo}" value="Impress�o de Diplomas Coletivo"  onclick="setAba('registro')"/> </li>
		<li> <h:commandLink action="#{impressaoDiploma.iniciarImpressaoSegundaVia}" value="Impress�o de Segunda Via"  onclick="setAba('registro')"/> </li>
		</ul>
	</li>
	<li>Dados de Alunos
		<ul>
		<li> <h:commandLink	action="#{historico.buscarDiscente}"	value="Emitir Hist�rico" onclick="setAba('registro')"/> </li>
		</ul>
	</li>
	<ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO} %>">
	<li>Relat�rios
		<ul>
			<li> <h:commandLink action="#{listaAssinaturasGraduandos.iniciar}" value="Assinaturas para Cola��o de Grau Coletiva" onclick="setAba('registro')" /> </li>
		</ul>
	</li>
	</ufrn:checkRole>
	
</ul>