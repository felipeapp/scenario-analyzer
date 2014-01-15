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
<%@page import="br.ufrn.sigaa.pesquisa.form.RelatorioBolsaFinalForm"%>
<h2>
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	<c:out value="Relatórios Finais de Iniciação Científica"/>
</h2>

<html:form action="/pesquisa/relatorioBolsaFinal">
<table class="formulario">
<caption>Consulta de Relatórios Finais</caption>
<tr>
	<td><html:checkbox property="filtros" styleId="cota" value="<%= String.valueOf(RelatorioBolsaFinalForm.BUSCA_COTA) %>" styleClass="noborder"/></td>
	<td><label for="cota">Cota:</label> </td>	
	<td>
		<html:select property="cota.id" onchange="$('cota').checked = true;">
			<html:options collection="cotas" property="id" labelProperty="descricao"/>
		</html:select>
	</td>
</tr>
<tr>
	<td><html:checkbox property="filtros" styleId="centro" value="<%= String.valueOf(RelatorioBolsaFinalForm.BUSCA_CENTRO) %>" styleClass="noborder"/></td>
	<td><label for="centro">Centro:</label> </td>
	<td>
		<html:select property="centro.id" onchange="$('centro').checked = true;">
			<html:options collection="centros" property="unidade.id" labelProperty="unidade.codigoNome" />
		</html:select>
	</td>
</tr>
<tr>
	<td><html:checkbox property="filtros" value="<%= String.valueOf(RelatorioBolsaFinalForm.BUSCA_DEPARTAMENTO) %>" styleId="unidade" styleClass="noborder"/></td>
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
	<td> <html:checkbox property="filtros" styleId="aluno" value="<%="" + RelatorioBolsaFinalForm.BUSCA_ALUNO%>" styleClass="noborder"/> </td>
	<td> <label for="aluno">Aluno:</label> </td>
	<td>
         <c:set var="idAjax" value="discente.id"/>
         <c:set var="nomeAjax" value="discente.pessoa.nome"/>
         <c:set var="nivel" value="G"/>
         <c:set var="statusDiscente" value="todos"/>
         <%@include file="/WEB-INF/jsp/include/ajax/discente.jsp" %>
         <script type="text/javascript">
			function discenteOnFocus() {
				getEl('aluno').dom.checked = true;
			}
		</script>
	</td>
</tr>
<tr>
	<td><html:checkbox property="filtros" value="<%= String.valueOf(RelatorioBolsaFinalForm.BUSCA_ORIENTADOR) %>" styleId="orientador" styleClass="noborder"/></td>
	<td><label for="orientador">Orientador:</label> </td>
	<td>
		<c:set var="idAjax" value="orientador.id"/>
		<c:set var="nomeAjax" value="orientador.pessoa.nome"/>
		<c:set var="todosDocentes" value="true"/>
		<c:set var="somenteInternos" value="true"/>
		<%@include file="/WEB-INF/jsp/include/ajax/docente.jsp" %>
        <script type="text/javascript">
				function docenteOnChange() {
					getEl('orientador').dom.checked = true;
				}
		</script>
	</td>
</tr>
<tr>
	<td><html:checkbox property="filtros" styleId="modalidade" value="<%= String.valueOf(RelatorioBolsaFinalForm.BUSCA_MODALIDADE) %>" styleClass="noborder"/></td>
	<td><label for="modalidade">Modalidade da Bolsa:</label> </td>
	<td>
		<html:select property="modalidade" onchange="$('modalidade').checked = true;">
			<html:options collection="modalidades" property="key" labelProperty="value"/>
		</html:select>
	</td>
</tr>
<tr>
	<td><html:checkbox property="filtros" styleId="submetido" value="<%= String.valueOf(RelatorioBolsaFinalForm.BUSCA_SUBMETIDO) %>" styleClass="noborder"/></td>
	<td><label for="submetido">Submetido?</label></td>
	<td>
		<html:radio property="submetido" value="true" styleId="simSubmetido" onclick="$('submetido').checked = true;"/><label for="simSubmetido">Sim</label>
		<html:radio property="submetido" value="false" styleId="naoSubmetido" onclick="$('submetido').checked = true;"/><label for="naoSubmetido">Não</label>
	</td>
</tr>
<tr>
	<td><html:checkbox property="filtros" styleId="parecer" value="<%= String.valueOf(RelatorioBolsaFinalForm.BUSCA_PARECER) %>" styleClass="noborder"/></td>
	<td><label for="parecer">Parecer emitido?</label></td>
	<td>
		<html:radio property="parecer" value="true" styleId="simParecer" onclick="$('parecer').checked = true;"/><label for="simParecer">Sim</label>
		<html:radio property="parecer" value="false" styleId="naoParecer" onclick="$('parecer').checked = true;"/><label for="naoParecer">Não</label>
	</td>
</tr>
<tfoot>
<tr><td colspan="3">
<html:hidden property="buscar" value="true"/>
<html:button dispatch="relatorio" value="Buscar"/>
<html:button dispatch="cancelar" value="Cancelar" cancelar="true"  />

</td></tr>
</tfoot>
</table>
</html:form>
<br/>

<c:if test="${not empty relatorios}">
<div class="infoAltRem">
	<html:img page="/img/pesquisa/view.gif" style="overflow: visible;"/>
    : Visualizar Relatório
	<html:img page="/img/monitoria/document_edit.png" style="overflow: visible;"/>
    : Emitir Parecer
	<html:img page="/img/pesquisa/remover_parecer.png" style="overflow: visible;"/>
    : Remover Parecer do Orientador<br/>
	<html:img page="/img/avaliar.gif" style="overflow: visible;"/>
    : Submeter Resumo para o CIC
	<html:img page="/img/alterar.gif" style="overflow: visible;"/>
    : Alterar Relatório
	<html:img page="/img/delete.gif" style="overflow: visible;"/>
    : Remover Relatório
</div>

<table class="listagem">
	<caption> Relatórios encontrados  (${fn:length(relatorios)}) </caption>
	<thead>
		<tr>
			<th width="35%">Discente</th>
			<th width="35%">Orientador</th>
			<th width="15%" class="alinharCentro">Última modificação</th>
			<th>Submetido</th>
			<th class="alinharCentro">Parecer Emitido</th>
			<th></th>
			<th></th>
			<th></th>
			<th></th>
			<th></th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="relatorio" items="${relatorios}" varStatus="status">
		<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td> ${relatorio.membroDiscente.discente.matriculaNome}</td>
			<td> ${relatorio.planoTrabalho.orientador.pessoa.nome} </td>
			<td align="center"> <ufrn:format type="dataHora" name="relatorio" property="dataEnvio" /></td>
			<td align="center">
				<c:if test="${ not relatorio.enviado }">Não</c:if>
				<c:if test="${ relatorio.enviado }">Sim</c:if>
			</td>
			<td align="center">
				<c:if test="${ relatorio.dataParecer == null }">Não</c:if>
				<c:if test="${ relatorio.dataParecer != null }">Sim</c:if>
			</td>
			<td>
				<ufrn:link action="/pesquisa/relatorioBolsaFinal" param="idRelatorio=${relatorio.id}&dispatch=view">
					<img src="${ctx}/img/pesquisa/view.gif"
						alt="Visualizar Relatório"
						title="Visualizar Relatório"/>
				</ufrn:link>
			</td>
			<td>
				<html:link action="/pesquisa/relatorioBolsaFinal.do?dispatch=selecionarBolsista&idRelatorio=${relatorio.id}">
					<img src="${ctx}/img/monitoria/document_edit.png"
						alt="Emitir Parecer"
						title="Emitir Parecer"/>
				</html:link>
			</td>
			<td>
				<a href="#" onclick="javascript: if(confirm('Deseja realmente remover o parecer?')) location.href = '${ctx}/pesquisa/relatorioBolsaFinal.do?idRelatorio=${relatorio.id}&dispatch=removerParecer';">
					<img src="${ctx}/img/pesquisa/remover_parecer.png"
						alt="Remover Parecer do Orientador"
						title="Remover Parecer do Orientador"/>
				</a>
			</td>
			<td>
				<html:link action="/pesquisa/resumoCongresso.do?dispatch=iniciarEnvio&idRelatorio=${relatorio.id}">
					<img src="${ctx}/img/avaliar.gif"
						alt="Submeter Resumo para o CIC"
						title="Submeter Resumo para o CIC"/>
				</html:link>
			</td>
			<td>
				<html:link action="/pesquisa/relatorioBolsaFinal.do?obj.id=${relatorio.id}&dispatch=edit">
					<img src="${ctx}/img/alterar.gif"
						alt="Alterar Relatório"
						title="Alterar Relatório"/>
				</html:link>
			</td>
			<td>
				<html:link action="/pesquisa/relatorioBolsaFinal.do?obj.id=${relatorio.id}&dispatch=remove" onclick="return confirm('Deseja realmente remover este relatório?');">
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
			<td colspan="11" align="center"><b> Total de relatórios encontrados: ${fn:length(relatorios)} </b> </td>
		</tr>
	</tfoot>
</table>
</c:if>

<c:if test="${empty relatorios and formRelatorioBolsaFinal.buscar}">
	<div align="center"> Nenhum relatório final foi encontrado com os parâmetros de busca especificados. </div>
</c:if>


<c:forEach items="${formRelatorioBolsaFinal.filtros}" var="filtro">
 	<jsp:useBean id="filtro" type="java.lang.Integer"/>
 	<c:if test="<%= filtro.intValue() == RelatorioBolsaFinalForm.BUSCA_COTA%>">
 		<script> $('cota').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == RelatorioBolsaFinalForm.BUSCA_CENTRO%>">
 		<script> $('centro').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == RelatorioBolsaFinalForm.BUSCA_DEPARTAMENTO%>">
 		<script> $('unidade').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == RelatorioBolsaFinalForm.BUSCA_ALUNO%>">
 		<script> $('aluno').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == RelatorioBolsaFinalForm.BUSCA_ORIENTADOR%>">
 		<script> $('orientador').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == RelatorioBolsaFinalForm.BUSCA_MODALIDADE%>">
 		<script> $('modalidade').checked = true;</script>
 	</c:if>
 	<c:if test="<%= filtro.intValue() == RelatorioBolsaFinalForm.BUSCA_PARECER%>">
 		<script> $('parecer').checked = true;</script>
 	</c:if>
</c:forEach>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>