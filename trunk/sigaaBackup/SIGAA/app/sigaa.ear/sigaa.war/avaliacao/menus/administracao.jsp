<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<ul>
<li> Opera��es Administrativas
	<ul>
		<li><h:commandLink action="#{processamentoAvaliacaoInstitucional.liberaResultados}" value="Liberar/Remover Consulta ao Resultado da Avalia��o" onclick="setAba('administracao')" /></li>
	</ul>
</li>
<li> Formul�rios de Avalia��o Institucional
	<ul>
		<li><h:commandLink action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.preCadastrar}" value="Cadastrar" onclick="setAba('administracao')" /></li>
		<li><h:commandLink action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.listar}" value="Listar/Alterar" onclick="setAba('administracao')" /></li>
	</ul>
</li>
<li> Calend�rio de Avalia��o Institucional
	<ul>
		<li><h:commandLink action="#{calendarioAvaliacaoInstitucionalBean.preCadastrar}" value="Cadastrar" onclick="setAba('administracao')" /></li>
		<li><h:commandLink action="#{calendarioAvaliacaoInstitucionalBean.listar}" value="Listar/Alterar" onclick="setAba('administracao')" /></li>
	</ul>
</li>

<li> Modera��o de Observa��es
	<ul>
		<li><h:commandLink action="#{moderadorObservacaoBean.iniciarModeracaoDocenteTurma}" value="Moderar Observa��es de Discentes sobre Docentes de Turmas" onclick="setAba('administracao')" /></li>
		<li><h:commandLink action="#{moderadorObservacaoBean.iniciarModeracaoTrancamentos}" value="Moderar Observa��es de Discentes sobre Trancamento de Turmas" onclick="setAba('administracao')" /></li>
		<li><h:commandLink action="#{moderadorObservacaoBean.iniciarModeracaoTurma}" value="Moderar Observa��es de Docentes sobre Turmas" onclick="setAba('administracao')" /></li>
	</ul>
</li>

<c:if test="${acesso.administradorSistema}" >
	<li> Processamento
		<ul>
			<li><h:commandLink action="#{processamentoAvaliacaoInstitucional.iniciar}" value="Processamento da Avalia��o Institucional" onclick="setAba('administracao')" /></li>
		</ul>
	</li>
	<li> Par�metros
		<ul>
			<li><h:commandLink action="#{parametrosAvaliacaoInstitucionalBean.iniciarParametrosGerais}" value="Par�metros Gerais da Avalia��o Institucional" onclick="setAba('administracao')" /></li>
			<li><h:commandLink action="#{parametrosAvaliacaoInstitucionalBean.iniciarPerguntasFixasConsultaDiscente}" value="Perguntas Fixas no Relat�rio do Resultado Sint�tico por Departamento" onclick="setAba('administracao')" /></li>
		</ul>
	</li>
</c:if>
</ul>