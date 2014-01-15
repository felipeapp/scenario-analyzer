<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<script type="text/javascript" src="/shared/javascript/matricula.js"></script>
<style>
	table.listagem tr.destaque td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>
<h2> <ufrn:subSistema></ufrn:subSistema> &gt; Matricular </h2>
<br/>

<f:view>

<h:form id="matricula">
<h:messages showDetail="true"/>
<table class="formulario" width="70%">
<caption>Selecione um Discente</caption>
<tbody>
<tr>
	<th> Aluno: </th>
	<td> <h:inputHidden id="idDiscente" value="#{ matriculaTecnico.discente.id }"/>
		 <h:inputText id="nomeDiscente" value="#{ matriculaTecnico.discente.pessoa.nome }" size="60"/>

		<ajax:autocomplete source="matricula:nomeDiscente" target="matricula:idDiscente"
			baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
			indicator="indicatorDiscente" minimumCharacters="3" parameters="" postFunction="retrieveInfoTurmas"
			parser="new ResponseXmlToHtmlListParser()" />

		<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
	</td>
</tr>
</tbody>

<tfoot>
<tr>
	<td colspan="2">
		<h:commandButton value="Cancelar" action="#{ matriculaTecnico.cancelar }" immediate="true" onclick="#{confirm}"/>
		<h:commandButton value="Continuar >>" action="#{ matriculaTecnico.realizarMatricula }"/>
	</td>
</tfoot>
</table>
<br>
<div id="loading"></div>
<div id="turmas"></div>
<br>
	<table class="listagem" id="lista-turmas" style="width: 80%">
		<caption>Turmas Abertas de ${calendarioAcademico.anoPeriodo }</caption>

			<tbody>
			<c:set var="disciplinaAtual" value="0" />
			<c:forEach items="${matriculaTecnico.turmasSemestre}" var="t" varStatus="s">
				<c:if test="${ disciplinaAtual != t.disciplina.id}">
					<c:set var="disciplinaAtual" value="${t.disciplina.id}" />
					<tr class="destaque"><td colspan="10" style="font-variant: small-caps;">
						${t.disciplina.codigo} - ${t.disciplina.nome}
					</td></tr>
				</c:if>
					<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
						<td width="2%">
							<input type="checkbox" name="selecionadas" value="${s.index}" class="noborder" id="t${s.index }" >
						</td>
						<td width="8%">Turma ${t.codigo}</td>
						<td width="12%">${t.qtdMatriculados}/${t.capacidadeAluno} aluno(s)</td>
						<td>${t.docentesNomes}</td>
						<ufrn:subSistema teste="tecnico">
							<td>${t.especializacao.descricao }</td>
						</ufrn:subSistema>
						<td width="8%">${t.descricaoHorario}</td>
						<td width="10%">${t.local}</td>
					</tr>
			</c:forEach>
			</tbody>
	</table>
</h:form>

<br/>&nbsp;


</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
//Element.hide('turmas');
retrieveInfoTurmas();
</script>