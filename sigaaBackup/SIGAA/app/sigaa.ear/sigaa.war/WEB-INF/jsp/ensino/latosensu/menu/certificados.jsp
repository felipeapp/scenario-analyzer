<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<ul>
	<li> Operações Administrativas 
		<ul>
		<li> <h:commandLink action="#{logGeracaoDiploma.auditarImpressao}" value="Auditar a Geração de Certificados"  onclick="setAba('certificados')"/> </li>
		<li> <h:commandLink action="#{requisicaoNumeroRegistro.listar}" value="Auditar a Requisição de Números para Registro de Certificados"  onclick="setAba('certificados')"/> </li>
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
		<li> <h:commandLink action="#{registroDiplomaColetivo.preCadastrar}" value="Registrar Certificado Coletivo (Turma de Conclusão)"  onclick="setAba('certificados')"/> </li>
		<li> <h:commandLink action="#{buscaRegistroDiploma.iniciarBusca}" value="Buscar por Registros de Certificados"  onclick="setAba('certificados')"/> </li>
		<li> <h:commandLink action="#{registroDiplomaColetivo.iniciarBusca}" value="Buscar por Registros de Certificados Coletivo"  onclick="setAba('certificados')"/> </li>
		<li> <h:commandLink action="#{buscaRegistroDiploma.iniciarEditarObservacao}" value="Editar Observação do Registro de Certificado"  onclick="setAba('certificados')"/> </li>
		</ul>
	</li>
	<li> Cadastro de Registro de Certificados Antigos
		<ul>
		<li> <h:commandLink action="#{registroDiplomaIndividual.preCadastrarRegistroAntigo}" value="Registro de Certificado Antigo"  onclick="setAba('certificados')"/> </li>
		</ul>
	</li>
	<li> Impressão de Certificados
		<ul>
		<li> <h:commandLink action="#{impressaoDiploma.iniciarImpressaoDiplomaColetivo}" value="Impressão de Certificados Coletivo"  onclick="setAba('certificados')"/> </li>
		<li> <h:commandLink action="#{impressaoDiploma.iniciarImpressaoDiplomaIndividual}" value="Impressão de Certificados Individual"  onclick="setAba('certificados')"/> </li>
		<li> <h:commandLink action="#{impressaoDiploma.iniciarImpressaoSegundaVia}" value="Impressão de Segunda Via"  onclick="setAba('certificados')"/> </li>
		</ul>
	</li>
	<li>Dados de Alunos
		<ul>
		<li> <h:commandLink	action="#{historico.buscarDiscente}"	value="Emitir Histórico" onclick="setAba('certificados')"/> </li>
		</ul>
	</li>
</ul>