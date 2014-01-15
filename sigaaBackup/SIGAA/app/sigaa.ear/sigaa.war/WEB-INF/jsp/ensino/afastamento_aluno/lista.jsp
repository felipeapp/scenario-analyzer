<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<h2 class="tituloPagina">
	<ufrn:subSistema />
&gt; Afastamentos de Alunos </h2>
<html:form action="/ensino/cadastroAfastamentoAluno.do?dispatch=list">
<html:hidden property="buscar" value="true"/>
	<table class="formulario" width="80%">
			<html:hidden property="tipoLista"/>
		<caption>Busca Afastamento</caption>
		<tbody>
       	<tr>
       		<td>
       			<html:radio property="tipoBusca" styleId="checkAluno" value="discente" styleClass="noborder" />
       		</td>
       		<th><label for="checkAluno">Aluno:</label></th>
       		<td>
				<c:set var="idAjax" value="obj.discente.id" />
				<c:set var="nomeAjax" value="obj.discente.pessoa.nome" />
				<%@include file="/WEB-INF/jsp/include/ajax/discente.jsp"%>
			</td>
       	</tr>
       	<tr>
       		<td>
       			<html:radio property="tipoBusca" styleId="checkTipo" value="tipo" styleClass="noborder" />
       		</td>
       		<th><label for="checkTipo"> Tipo de Afastamento: </label></th>
       		<td>
       			<html:select property="obj.tipoMovimentacaoAluno.id" onfocus="$('checkTipo').checked = true">
                <html:option value=""> Opções </html:option>
                <html:options collection="tipoAfastamentoAlunoTecs" property="id" labelProperty="descricao" />
                </html:select>
            </td>
       	</tr>
       	<tr>
       		<td>
       			<html:radio property="tipoBusca" styleId="checkTodos" value="todos" styleClass="noborder" />
       		</td>
       		<th><label for="checkTodos">Todos</label></th>
       		<td></td>
       	</tr>
       	<tfoot>
       	<tr>
       		<td colspan="3" align="center">
       			<html:submit>Buscar</html:submit>
       		</td>
       	</tr>
        </tfoot>
    </table>
</html:form>


<c:if test="${ param.tipoLista <= 1}">
<br>
<c:if test="${not empty lista }">
	<div class="infoAltRem">
		<html:img page="/img/alterar.gif" style="overflow: visible;"/>
		: Alterar Afastamento
		<html:img page="/img/delete.gif" style="overflow: visible;"/>
		: Desativar Afastamento
	</div>
</c:if>
<ufrn:table collection="${lista}" properties="discente.pessoa.nome, discente.matricula, tipoAfastamentoAluno.descricao, dataAfastamento"
headers="Aluno, Matricula, Afastamento, Data do Afastamento"
title="Afastamentos" crud="false"
links="src='${ctx}/img/alterar.gif',?id={id}&dispatch=edit;
	   src='${ctx}/img/delete.gif',?id={id}&dispatch=desativar"
linksRoles="<%=new int[][] {
		   new int[] {SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO},
		   new int[] {SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_LATO}}%>"/>
</c:if>

<c:if test="${ param.tipoLista == 2}">
<c:if test="${ not empty lista}">
	<br>
	<div class="infoAltRem">
		<html:img page="/img/avancar.gif"/> : Registrar Retorno
	</div>
</c:if>
<br>

<ufrn:table collection="${lista}" properties="discente.matricula,discente.pessoa.nome,  tipoMovimentacaoAluno.descricao, dataOcorrencia"
headers="Matricula, Nome do Aluno,  Afastamento, Data do Afastamento"
title="Afastamentos sem Retorno" crud="false" links="src='${ctx}/img/avancar.gif',?id={id}&tipoLista=2&dispatch=dadosRetorno"/>
</c:if>
<br><br>
<script type="text/javascript">
	function discenteOnFocus() {
		marcaCheckBox('checkAluno');
	}
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>