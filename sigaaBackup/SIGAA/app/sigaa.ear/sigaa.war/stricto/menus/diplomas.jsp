	<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<ul>
	<li> Opera��es Administrativas 
		<ul>
		<li> <h:commandLink action="#{parametrosRegistroDiploma.atualizar}" value="Listar/Alterar Par�metros"  onclick="setAba('diplomas')"/> </li>
		<li> <h:commandLink action="#{logGeracaoDiploma.auditarImpressao}" value="Auditar a Gera��o de Diplomas"  onclick="setAba('diplomas')"/> </li>
		<li> <h:commandLink action="#{requisicaoNumeroRegistro.listar}" value="Auditar a Requisi��o de N�meros para Registro de Diplomas"  onclick="setAba('diplomas')"/> </li>
		</ul>
	</li>
	<li> Livro de Registro de Diplomas
		<ul>
		<li> <h:commandLink action="#{livroRegistroDiplomas.preCadastrar}" value="Abrir Livro"  onclick="setAba('diplomas')"/> </li>
		<li> <h:commandLink action="#{livroRegistroDiplomas.listar}" value="Fechar/Imprimir Livro"  onclick="setAba('diplomas')"/> </li>
		</ul>
	</li>
	<li> Registro de Diplomas
		<ul>
		<!-- <li> <h:commandLink action="#{requisicaoNumeroRegistro.preCadastrar}" value="Requisitar N�mero para Registro de Diploma Externo"  onclick="setAba('diplomas')"/> </li> -->
		<li> <h:commandLink action="#{registroDiplomaIndividual.preCadastrar}" value="Registrar Diploma Individual"  onclick="setAba('diplomas')"/> </li>
		<li> <h:commandLink action="#{buscaRegistroDiploma.iniciarBusca}" value="Buscar por Registros de Diplomas"  onclick="setAba('diplomas')"/> </li>
		<li> <h:commandLink action="#{buscaRegistroDiploma.iniciarEditarObservacao}" value="Editar Observa��o do Registro de Diploma"  onclick="setAba('diplomas')"/> </li>
		</ul>
	</li>
	<li> Cadastro de Registro de Diplomas Antigos
		<ul>
		<li> <h:commandLink action="#{registroDiplomaIndividual.preCadastrarRegistroAntigo}" value="Registro de Diploma Antigo"  onclick="setAba('diplomas')"/> </li>
		<li> Registro de Diploma Externo Antigo (Indipon�vel)<!-- <h:commandLink action="#{registroDiplomaIndividual.preCadastrarRegistroExternoAntigo}" value="Registro de Diploma Externo Antigo"  onclick="setAba('diplomas')"/>  --></li>
		</ul>
	</li>
	<li> Impress�o de Diplomas
		<ul>
		<li> <h:commandLink action="#{impressaoDiploma.iniciarImpressaoDiplomaColetivo}" value="Impress�o de Diplomas Coletivo"  onclick="setAba('diplomas')"/> </li>
		<li> <h:commandLink action="#{impressaoDiploma.iniciarImpressaoDiplomaIndividual}" value="Impress�o de Diplomas Individual"  onclick="setAba('diplomas')"/> </li>
		<li> <h:commandLink action="#{impressaoDiploma.iniciarImpressaoSegundaVia}" value="Impress�o de Segunda Via"  onclick="setAba('diplomas')"/> </li>
		</ul>
	</li>
	<li>Dados de Alunos
		<ul>
		<li> <h:commandLink	action="#{historico.buscarDiscente}"	value="Emitir Hist�rico" onclick="setAba('diplomas')"/> </li>
		<li> <h:commandLink action="#{alteracaoDadosDiscente.iniciar}" value="Atualizar Dados Pessoais" onclick="setAba('diplomas')"/> </li>
		</ul>
	</li>
	
</ul>