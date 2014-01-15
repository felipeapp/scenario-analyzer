<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<ul>
	<li> Registro de Atividades
		<ul>
		<li> <h:commandLink	action="#{registroAtividade.iniciarMatricula }" value="Matricular" onclick="setAba('matricula')" id="linkParaIniciarMatricula"/> </li>
		<li> <h:commandLink	action="#{registroAtividade.iniciarRenovacao }" value="Renovar Matrícula" onclick="setAba('matricula')" id="linkParaIniciarRenovacao"/> </li>
		<li> <h:commandLink action="#{registroAtividade.iniciarConsolidacao }" value="Consolidar" onclick="setAba('matricula')" id="linkParaIniciarConsolidacao"/> </li>
		<li> <h:commandLink action="#{registroAtividade.iniciarValidacao }" value="Validar" onclick="setAba('matricula')" id="linkParaIniciarvalidacao"/> </li>
		<li> <h:commandLink action="#{registroAtividade.iniciarExclusao }" value="Excluir" onclick="setAba('matricula')" id="linkParaIniciarExclusao"/> </li>
		<li> <h:commandLink action="#{registroAtividade.iniciarAlterarPeriodo }" value="Alterar Período" onclick="setAba('matricula')" id="linkParaIniciarAlteracaoPeriodo"/> </li>
		</ul>
	</li>

	<li> Turmas
		<ul>
		<li> <h:commandLink action="#{turmaStrictoSensuBean.preCadastrar}" value="Criar Turma" onclick="setAba('matricula')" id="preCadastrarturmaStructoSensu"/>	</li>
		<li> <h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Consultar, Alterar ou Remover" onclick="setAba('matricula')" id="consultarAlterarRemTurmas"/> </li>
		</ul>
	</li>
	<li> Vínculo com a Pós-Graduação
		<ul>
		<li> <h:commandLink action="#{movimentacaoAluno.iniciarTrancamentoPrograma }" value="Trancar Vínculo" onclick="setAba('matricula')" id="inicioDeTrancamentoDePrograma"/>	</li>
		<li> <h:commandLink action="#{movimentacaoAluno.iniciarRetorno }" value="Retorno Manual de Discente" onclick="setAba('matricula')" id="retornarDiscenteManualmente"/> </li>
		<li> <h:commandLink	action="#{movimentacaoAluno.iniciarCancelamentoTrancamento }" value="Cancelar Trancamentos Futuros" onclick="setAba('matricula')" id="cancelarTrancFuturos"/> </li>
		<li> <h:commandLink action="#{movimentacaoAluno.iniciarCancelamentoPrograma }" value="Cancelar Vínculo" onclick="setAba('matricula')" id="inicioCancelarPrograma"/> </li>
		<li> <h:commandLink action="#{movimentacaoAluno.iniciarConclusaoProgramaStricto }" value="Concluir Aluno" onclick="setAba('matricula')" id="iniciarConclusoesProgramaStricto"/> </li>
		<li> <h:commandLink action="#{movimentacaoAluno.iniciarEstorno }" value="Estornar Operação" onclick="setAba('matricula')" id="inicioEstornoOp"/> </li>
		</ul>
	</li>


</ul>