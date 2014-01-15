<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<ul>
	<li> Opera��es Administrativas 
		<ul>
		<li> <h:commandLink action="#{logGeracaoDiploma.auditarImpressao}" value="Auditar a Gera��o de Certificados"  onclick="setAba('certificados')"/> </li>
		<li> <h:commandLink action="#{requisicaoNumeroRegistro.listar}" value="Auditar a Requisi��o de N�meros para Registro de Certificados"  onclick="setAba('certificados')"/> </li>
		</ul>
	</li>
	<li> Livro de Registro de Certificados
		<ul>
		<li> <h:commandLink action="#{livroRegistroDiplomas.preCadastrar}" value="Abrir Livro"  onclick="setAba('certificados')"/> </li>
		<li> <h:commandLink action="#{livroRegistroDiplomas.listar}" value="Fechar/Imprimir Livro"  onclick="setAba('certificados')"/> </li>
		</ul>
	</li>
	<li> Registro de Certificados
		<ul>
		<li> <h:commandLink action="#{registroDiplomaIndividual.preCadastrar}" value="Registrar Certificado Individual"  onclick="setAba('certificados')"/> </li>
		<li> <h:commandLink action="#{registroDiplomaColetivo.preCadastrar}" value="Registrar Certificado Coletivo (Turma de Conclus�o)"  onclick="setAba('certificados')"/> </li>
		<li> <h:commandLink action="#{buscaRegistroDiploma.iniciarBusca}" value="Buscar por Registros de Certificados"  onclick="setAba('certificados')"/> </li>
		<li> <h:commandLink action="#{registroDiplomaColetivo.iniciarBusca}" value="Buscar por Registros de Certificados Coletivo"  onclick="setAba('certificados')"/> </li>
		<li> <h:commandLink action="#{buscaRegistroDiploma.iniciarEditarObservacao}" value="Editar Observa��o do Registro de Certificado"  onclick="setAba('certificados')"/> </li>
		</ul>
	</li>
	<li> Cadastro de Registro de Certificados Antigos
		<ul>
		<li> <h:commandLink action="#{registroDiplomaIndividual.preCadastrarRegistroAntigo}" value="Registro de Certificado Antigo"  onclick="setAba('certificados')"/> </li>
		</ul>
	</li>
	<li> Impress�o de Certificados
		<ul>
		<li> <h:commandLink action="#{impressaoDiploma.iniciarImpressaoDiplomaColetivo}" value="Impress�o de Certificados Coletivo"  onclick="setAba('certificados')"/> </li>
		<li> <h:commandLink action="#{impressaoDiploma.iniciarImpressaoDiplomaIndividual}" value="Impress�o de Certificados Individual"  onclick="setAba('certificados')"/> </li>
		<li> <h:commandLink action="#{impressaoDiploma.iniciarImpressaoSegundaVia}" value="Impress�o de Segunda Via"  onclick="setAba('certificados')"/> </li>
		</ul>
	</li>
	<li>Dados de Alunos
		<ul>
		<li> <h:commandLink	action="#{historico.buscarDiscente}"	value="Emitir Hist�rico" onclick="setAba('certificados')"/> </li>
		</ul>
	</li>
</ul>