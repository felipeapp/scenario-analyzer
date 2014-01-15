<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
.alinharDireita{ 
	text-align:right !important;
}
.alinharEsquerda{ 
	text-align:left !important;
} 
.alinharCentro{ 
	text-align:center !important;
}
</style>
<%@page import="br.ufrn.sigaa.pesquisa.form.RelatorioBolsaParcialForm"%>
<h2>
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	<c:out value="Relatórios Parciais de Discentes"/>
</h2>

<html:form action="/pesquisa/relatorioBolsaParcial">
<table class="formulario">
<caption>Consulta de Relatórios Parciais</caption>

<tr>
	<td><html:checkbox property="filtroCentro" styleId="centro" value="true" styleClass="noborder"/></td>
	<td><label for="centro">Centro:</label> </td>
	<td>
		<html:select property="centro" onchange="$('centro').checked = true;">
			<html:options collection="centros" property="id" labelProperty="nome"/>
		</html:select>
	</td>
</tr>

<tr>
	<td><html:checkbox property="filtroDepartamento" value="true" styleId="unidade" styleClass="noborder"/></td>
	<td><label for="departamento">Departamento:</label> </td>
	<td>
		<c:set var="idAjax" value="unidade.id"/>
	   	<c:set var="nomeAjax" value="unidade.nome"/>
		<%@include file="/WEB-INF/jsp/include/ajax/unidade.jsp" %>
		<script type="text/javascript">
			function unidadeOnFocus() {
				getEl('unidade').dom.checked = true;
			}
		</script>
	</td>
</tr>
<tr>
	<td> <html:checkbox property="filtroAluno" styleId="aluno" value="true" styleClass="noborder"/> </td>
	<td> <label for="aluno">Aluno:</label> </td>
	<td>
         <c:set var="idAjax" value="discente.id"/>
         <c:set var="nomeAjax" value="discente.pessoa.nome"/>
         <c:set var="nivel" value="G"/>
         <%@include file="/WEB-INF/jsp/include/ajax/discente.jsp" %>
         <script type="text/javascript">
			function discenteOnFocus() {
				getEl('aluno').dom.checked = true;
			}
		</script>
	</td>
</tr>
<tr>
	<td><html:checkbox property="filtroModalidade" styleId="modalidade" value="true" styleClass="noborder"/></td>
	<td><label for="modalidade">Modalidade da Bolsa:</label> </td>
	<td>
		<html:select property="modalidade" onchange="$('modalidade').checked = true;">
			<html:options collection="modalidades" property="key" labelProperty="value"/>
		</html:select>
	</td>
</tr>
<tr>
	<td><html:checkbox property="filtroOrientador" value="true" styleId="orientador" styleClass="noborder"/></td>
	<td><label for="orientador">Orientador:</label> </td>
	<td>
		<c:set var="idAjax" value="orientador.id"/>
		<c:set var="nomeAjax" value="orientador.pessoa.nome"/>
		<c:set var="todosDocentes" value="true"/>
		<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
        <script type="text/javascript">
				function docenteOnFocus() {
					getEl('orientador').dom.checked = true;
				}
		</script>
	</td>
</tr>
<tr>
	<td><html:checkbox property="filtroCota" styleId="cota" value="true" styleClass="noborder"/></td>
	<td><label for="cota">Cota:</label> </td>
	<td>
		<html:select property="cota" onchange="$('cota').checked = true;">
			<html:options collection="cotas" property="id" labelProperty="descricao"/>
		</html:select>
	</td>
</tr>
<tr>
	<td><html:checkbox property="filtroTodos" styleId="todos" value="true" styleClass="noborder"/></td>
	<td><label for="todos">Todos</label> </td>
	<td></td>
</tr>
<tr>
	<td colspan="3" align="center"> Apenas relatórios com parecer:
	<html:radio property="parecer" value="true" styleId="simParecer"/><label for="simParecer">Sim</label>
	<html:radio property="parecer" value="false" styleId="naoParecer"/><label for="naoParecer">Não</label>
	</td>
</tr>
<tfoot>
<tr><td colspan="3">
<html:hidden property="buscar" value="true"/>
<html:button dispatch="relatorio" value="Buscar"/>
<html:button dispatch="cancelar" value="Cancelar" cancelar="true" />
</td></tr>
</tfoot>
</table>
</html:form>
<br/>&nbsp;
<c:if test="${not empty relatorios}">
<div class="infoAltRem">
	<html:img page="/img/pesquisa/view.gif" style="overflow: visible;"/>: Visualizar Relatório
	<html:img page="/img/pesquisa/remover_parecer.png" style="overflow: visible;"/>: Remover Parecer do Orientador<br/>
	<html:img page="/img/delete.gif" style="overflow: visible;"/>: Remover Relatório
</div>
<table class="listagem">
	<caption> Relatórios encontrados (${fn:length(relatorios)}) </caption>
	<thead>
		<tr>
			<th>Discente</th>
			<th>Orientador</th>
			<th>Parecer Emitido</th>
			<th class="alinharCentro">Data de Envio</th>
			<th></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<c:set var="count" value="0" />
		<c:forEach var="relatorio" items="${relatorios}" varStatus="status">
		<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td> ${relatorio.membroDiscente.discente.matriculaNome}</td>
			<td> ${relatorio.planoTrabalho.orientador.pessoa.nome} </td>
			<td align="center">
				<c:if test="${ relatorio.dataParecer == null }">Não</c:if>
				<c:if test="${ relatorio.dataParecer != null }">Sim
					<c:set var="count" value="${ count + 1 }" />
				</c:if>
			</td>
			<td nowrap="nowrap" align="center"> <ufrn:format type="dataHora" name="relatorio" property="dataEnvio" /></td>
			<td>
				<ufrn:link action="/pesquisa/relatorioBolsaParcial" param="idRelatorio=${relatorio.id}&dispatch=view">
					<img src="${ctx}/img/pesquisa/view.gif"
						alt="Visualizar Relatório"
						title="Visualizar Relatório"/>
				</ufrn:link>
			</td>
			<td nowrap="nowrap">
				<a href="#" onclick="javascript: if(confirm('Deseja realmente remover o parecer?')) location.href = '${ctx}/pesquisa/relatorioBolsaParcial.do?idRelatorio=${relatorio.id}&dispatch=removerParecer';">
					<img src="${ctx}/img/pesquisa/remover_parecer.png"
						alt="Remover Parecer do Orientador"
						title="Remover Parecer do Orientador"/>
				</a>
				<html:link action="/pesquisa/relatorioBolsaParcial.do?obj.id=${relatorio.id}&dispatch=remove" onclick="return confirm('Deseja realmente remover este relatório?');">
					<img src="${ctx}/img/delete.gif"
						alt="Remover Relatório"
						title="Remover Relatório"/>
				</html:link>
			</td>
		</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="6" align="center"><b> Total de relatórios submetidos: ${fn:length(relatorios)} de ${ count } (<fmt:formatNumber pattern="#0.0" value="${ ( count * 100 ) / (fn:length(relatorios)) }"/>%) </b> </td>
		</tr>
	</tfoot>
</table>
</c:if>

<c:if test="${empty relatorios}">
	<div align="center"> Nenhum relatório parcial foi submetido! </div>
</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>