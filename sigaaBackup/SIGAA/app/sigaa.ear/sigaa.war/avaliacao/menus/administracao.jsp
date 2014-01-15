<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<ul>
<li> Operações Administrativas
	<ul>
		<li><h:commandLink action="#{processamentoAvaliacaoInstitucional.liberaResultados}" value="Liberar/Remover Consulta ao Resultado da Avaliação" onclick="setAba('administracao')" /></li>
	</ul>
</li>
<li> Formulários de Avaliação Institucional
	<ul>
		<li><h:commandLink action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.preCadastrar}" value="Cadastrar" onclick="setAba('administracao')" /></li>
		<li><h:commandLink action="#{cadastrarFormularioAvaliacaoInstitucionalMBean.listar}" value="Listar/Alterar" onclick="setAba('administracao')" /></li>
	</ul>
</li>
<li> Calendário de Avaliação Institucional
	<ul>
		<li><h:commandLink action="#{calendarioAvaliacaoInstitucionalBean.preCadastrar}" value="Cadastrar" onclick="setAba('administracao')" /></li>
		<li><h:commandLink action="#{calendarioAvaliacaoInstitucionalBean.listar}" value="Listar/Alterar" onclick="setAba('administracao')" /></li>
	</ul>
</li>

<li> Moderação de Observações
	<ul>
		<li><h:commandLink action="#{moderadorObservacaoBean.iniciarModeracaoDocenteTurma}" value="Moderar Observações de Discentes sobre Docentes de Turmas" onclick="setAba('administracao')" /></li>
		<li><h:commandLink action="#{moderadorObservacaoBean.iniciarModeracaoTrancamentos}" value="Moderar Observações de Discentes sobre Trancamento de Turmas" onclick="setAba('administracao')" /></li>
		<li><h:commandLink action="#{moderadorObservacaoBean.iniciarModeracaoTurma}" value="Moderar Observações de Docentes sobre Turmas" onclick="setAba('administracao')" /></li>
	</ul>
</li>

<c:if test="${acesso.administradorSistema}" >
	<li> Processamento
		<ul>
			<li><h:commandLink action="#{processamentoAvaliacaoInstitucional.iniciar}" value="Processamento da Avaliação Institucional" onclick="setAba('administracao')" /></li>
		</ul>
	</li>
	<li> Parâmetros
		<ul>
			<li><h:commandLink action="#{parametrosAvaliacaoInstitucionalBean.iniciarParametrosGerais}" value="Parâmetros Gerais da Avaliação Institucional" onclick="setAba('administracao')" /></li>
			<li><h:commandLink action="#{parametrosAvaliacaoInstitucionalBean.iniciarPerguntasFixasConsultaDiscente}" value="Perguntas Fixas no Relatório do Resultado Sintético por Departamento" onclick="setAba('administracao')" /></li>
		</ul>
	</li>
</c:if>
</ul>