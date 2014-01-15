<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<ul>
	<c:if test="${acesso.ppg}">
		<li>Solicita��es de Bolsa
			<ul>
				<li> <h:commandLink value="Gerenciar Solicita��es Cadastradas" action="#{solicitacaoBolsasReuniBean.listBuscar}" onclick="setAba('reuni')" id="linkGerenciarSolicitacoesCadastradas"/> </li>
			</ul>
		</li>

		<li>Cadastros
			<ul>
				<li> <h:commandLink value="Editais" action="#{editalBolsasReuniBean.listar}" onclick="setAba('reuni')" id="linkListarEditais"/></li>
				<li> <h:commandLink value="Formas de Atua��o de Bolsistas" action="#{formaAtuacaoDocenciaAssistidaBean.iniciar}" onclick="setAba('reuni')" id="linkListarFormasAtuacaoBolsista"/></li>
				<li> <h:commandLink value="�reas de Atua��o em Ci�ncias e Tecnologia " action="#{areaConhecimentoCienciasTecnologiaBean.listar}" onclick="setAba('reuni')" id="linkListarAreaAtuacao"/></li>
				<li> <h:commandLink value="Identificar Membro CATP" action="#{membroApoioDocenciaAssistidaMBean.iniciar}" onclick="setAba('reuni')" id="linkIdentificarMembroCATP"/></li>
			</ul>
		</li>
	</c:if>
	
	<li>Consulta
		<ul>
			<li><h:commandLink value="Gerenciar Planos de Doc�ncia Assistida" action="#{planoDocenciaAssistidaMBean.iniciarBuscaGeral}" onclick="setAba('reuni')" id="linkRelatorioDocenciaAssistida"/></li>
			<li> <h:commandLink value="Planos de Doc�ncia Assistida Sem Indica��o" action="#{planoDocenciaAssistidaMBean.listarSemIndicao}" onclick="setAba('reuni')" id="linkListarSemIndicacao"/> </li>
		</ul>
	</li>
	
	<li>Relat�rios
		<ul>
			<li><h:commandLink value="Quantitativo de Planos de Doc�ncia Assistida por Atividade" action="#{relatoriosDocenciaAssistidaMBean.iniciarQuantitativoAtividades}" onclick="setAba('reuni')" id="linkRelatorioQuantAtividades"/></li>
			<li><h:commandLink value="Turmas Atendidas por Doc�ncia Assistida" action="#{relatorioTurmasDocenciaAssistida.iniciar}" onclick="setAba('reuni')"  id="relatorioTurmasDocenciaAssistida"/></li>
			<li><h:commandLink value="Quantitativo de Alunos Atendidos por Doc�ncia Assistida" action="#{relatoriosDocenciaAssistidaMBean.iniciarAlunosAtendidos}" onclick="setAba('reuni')" id="linkRelatorioQuantDiscentesDocenciaAssisitda"/></li>
			<li><h:commandLink value="�ndices dos Componentes Atendidos por Doc�ncia Assistida" action="#{relatoriosDocenciaAssistidaMBean.iniciarComponentesAtendidos}" onclick="setAba('reuni')" id="linkRelatorioQuantComponentesDocenciaAssisitda"/></li>
			<li><h:commandLink value="Quantitativo de Planos de Doc�ncia Assistida por Status" action="#{relatoriosDocenciaAssistidaMBean.iniciarQuantitativoStatus}" onclick="setAba('reuni')" id="linkRelatorioQuantStatus"/></li>
		</ul>
	</li>	
