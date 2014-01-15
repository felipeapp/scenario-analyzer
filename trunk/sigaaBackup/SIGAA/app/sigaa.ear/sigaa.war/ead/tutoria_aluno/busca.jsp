<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.ead.jsf.TutoriaAlunoMBean"%>
<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	
	div.form-botoes { border-top: 2px solid #CDCDCD; padding-top: 2px; margin-top: 10px; }
	div.botoes div.form-actions { float: left; padding-top: 2px; width: 150px; }
	
</style>

<f:view>
	<h:form id="formulario">

	<h2> <ufrn:subSistema /> > Cadastrar Orientação Acadêmica &gt; Buscar Discente</h2>

	<table class="formulario" style="width:80%;">
		<caption> Informe o critério de busca desejado</caption>
		<tbody>
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{tutoriaAluno.buscaMatricula }" styleClass="noborder" id="checkMatricula" />
				</td>
				<th>
					<label for="checkMatricula"	onclick="$('formulario:checkMatricula').checked = !$('formulario:checkMatricula').checked;">Matrícula: </label>
				</th>
				<td> <h:inputText value="#{tutoriaAluno.discente.matricula}" size="15" onchange="$('formulario:checkMatricula').checked = true;" onkeyup="return formatarInteiro(this);"/></td>
			</tr>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{tutoriaAluno.buscaNome }" styleClass="noborder" id="checkNome" />
				</td>
				<th>
					<label for="checkMatricula"	onclick="$('formulario:checkNome').checked = !$('formulario:checkNome').checked;">Nome do Discente: </label>
				</th>
				<td> <h:inputText value="#{tutoriaAluno.discente.pessoa.nome}" size="60" id="nomeDiscente" onchange="$('formulario:checkNome').checked = true;"/> </td>
			</tr>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{tutoriaAluno.buscaAnoPeriodo }" styleClass="noborder" id="checkAnoPeriodo" />
				</td>
				<th>
					<label for="checkAnoPeriodo" onclick="$('formulario:checkAnoPeriodo').checked = !$('formulario:checkAnoPeriodo').checked;">Ano - Periodo de Ingresso: </label>
				</th>
				<td> 
					<h:inputText value="#{tutoriaAluno.anoIngresso}" size="4" maxlength="4" onchange="$('formulario:checkAnoPeriodo').checked = true;" onkeyup="return formatarInteiro(this);"/> - 
					<h:inputText value="#{tutoriaAluno.periodoIngresso}" size="1" maxlength="1" onchange="$('formulario:checkAnoPeriodo').checked = true;" onkeyup="return formatarInteiro(this);"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{tutoriaAluno.buscarDiscente}" value="Buscar"/>
					<h:commandButton value="<< Voltar" action="#{tutoriaAluno.telaDadosOrientacao}"/>
					<h:commandButton action="#{tutoriaAluno.cancelar}" value="Cancelar" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
	
	<c:if test="${not empty tutoriaAluno.resultadoBusca}">
		
		<div class="descricaoOperacao">
			<p>
				Através deste recurso é possível adicionar os discentes selecionados diretamente no tutor clicando em <b>Adiconar Discentes ao Tutor</b>
				ou armazená-los em uma lista provisória, possibilitando a busca de outros discentes, clicando em: <b>Armazenar Discentes em Lista</b>
			</p>
		</div>
		<br/>
		
		<table class="listagem">
			<caption> Selecione os discentes que deseja adicionar na orientação acadêmica ou armazenar em lista </caption>
			<thead>
			<tr>
				<th> </th>
				<th> Aluno </th>
				<th> Status </th>
			</tr>
			</thead>
			<tbody>
				<tr>
					<td><input type="checkbox" name="checkTodos" id="checkTodos" onclick="marcarTodos(this)"/></td>
					<td colspan="2"><b>Selecionar Todos</b></td>
				</tr>

				<c:forEach items="${tutoriaAluno.resultadoBusca}" var="disc" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				
				<td><input type="checkbox" name="selecionados" id="${disc.id}" value="${disc.id}"/></td>
					<td> <label for="${disc.id}"> ${disc} </label> </td>
					<td>${disc.statusString}</td>
				</tr>
				
				</c:forEach>
				
			</tbody>
		</table>
	</c:if>
	
	<c:if test="${not empty tutoriaAluno.resultadoBusca}">
		<div class="form-botoes" align="center">
			<div class="form-actions">
				<h:commandButton value="Adicionar Discentes ao Tutor" action="#{tutoriaAluno.adicionarDiscentes}"  /> 
				<h:commandButton value="Armazenar Discentes em Lista" action="#{tutoriaAluno.adicionarDiscentesLista}"/>
				<c:if test="${not empty tutoriaAluno.listaDiscentes}">
					<h:commandButton value="Limpar Lista" action="#{tutoriaAluno.limparListaDiscentes}"/>
				</c:if>	
			</div>
		</div>
	</c:if>
	
	<c:if test="${not empty tutoriaAluno.listaDiscentes}">
		<br/>
		<table class="listagem">
			<caption> Selecione os discentes que deseja adicionar na orientação acadêmica </caption>
			<thead>
			<tr>
				<th> </th>
				<th> Aluno </th>
				<th> Status </th>
			</tr>
			</thead>
			<tbody>
				<tr>
					<td><input type="checkbox" name="checkTodos" id="checkTodos" onclick="marcarTodosLista(this)"/></td>
					<td colspan="2"><b>Selecionar Todos</b></td>
				</tr>

				<c:forEach items="${tutoriaAluno.listaDiscentes}" var="disc" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				
				<td><input type="checkbox" name="selecionados-lista" id="${disc.id}" value="${disc.id}"/></td>
					<td> <label for="${disc.id}"> ${disc} </label> </td>
					<td>${disc.statusString}</td>
				</tr>
				
				</c:forEach>
				
			</tbody>
		</table>
	</c:if>
	
	<c:if test="${not empty tutoriaAluno.listaDiscentes}">
		<div class="form-botoes" align="center">
			<div class="form-actions">
				<h:commandButton value="Adicionar Discentes ao Tutor" action="#{tutoriaAluno.adicionarDiscentes}"  /> 
				<h:commandButton value="Armazenar Discentes em Lista" action="#{tutoriaAluno.adicionarDiscentesLista}"/>
				<h:commandButton value="Limpar Lista" action="#{tutoriaAluno.limparListaDiscentes}"/>
			</div>
		</div>
	</c:if>
	</h:form>
</f:view>
<script type="text/javascript">
<!--
$('formulario:nomeDiscente').focus()

function marcarTodos(chk) {
	var checks = document.getElementsByName('selecionados');
	for (i=0;i<checks.length;i++) {
		checks[i].checked = chk.checked;
	}
}

function marcarTodosLista(chk) {
	var checks = document.getElementsByName('selecionados-lista');
	for (i=0;i<checks.length;i++) {
		checks[i].checked = chk.checked;
	}
}
//-->
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>