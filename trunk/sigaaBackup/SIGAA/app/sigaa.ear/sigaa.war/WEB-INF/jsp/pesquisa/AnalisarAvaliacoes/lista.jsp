<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.AnalisarAvaliacoesForm"%>
<h2>
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> >
	Análise de Projetos de Pesquisa
</h2>

<style>
	table.listagem tr.centro td {
		font-size: 1.3em;
		font-weight: bold;
		padding: 4px 30px 2px;
		letter-spacing: 1px;
		background: #C8D5EC;
		border-bottom: 1px solid #999;
	}
</style>

<html:form action="/pesquisa/analisarAvaliacoes" method="post">

		<table class="formulario" align="center" width="80%">
			<caption class="listagem">Busca de Avaliações </caption>
			<tr>
				<td><html:checkbox property="filtros" styleId="editalPesquisa" value="<%= String.valueOf(AnalisarAvaliacoesForm.UNIDADE_ACADEMICA) %>" styleClass="noborder"/></td>
				<td><label for="editalPesquisa">Edital:</label> </td>
				<td>
					<c:set var="editaisPesquisa" value="${formAnalisarAvaliacoes.referenceData.editaisPesquisa }"/>
					<html:select property="edital.id" style="width: 450px;" onchange="$('editalPesquisa').checked = true;">
						<html:options collection="editaisPesquisa" property="id" labelProperty="descricao"/>
					</html:select>
				</td>
			</tr>
			<tr>
				<td><html:checkbox property="filtros" styleId="centro" value="<%= String.valueOf(AnalisarAvaliacoesForm.UNIDADE_ACADEMICA) %>" styleClass="noborder"/></td>
				<td><label for="centro">Centro:</label> </td>
				<td>
					<c:set var="centros" value="${formAnalisarAvaliacoes.referenceData.centros }"/>
					<html:select property="unidadeAcademica.id" onchange="$('centro').checked = true;">
						<html:options collection="centros" property="unidade.id" labelProperty="unidade.codigoNome"/>
					</html:select>
				</td>
			</tr>
			<tr>
				<td><html:checkbox property="filtros" styleId="minimo" value="<%= String.valueOf(AnalisarAvaliacoesForm.MINIMO_AVALIACOES) %>" styleClass="noborder"/></td>
				<td colspan="2">
					Com no mínimo
					<html:text property="minimoAvaliacoes" size="2" onchange="$('minimo').checked = true;" style="text-align: center"/>
					avaliações realizadas.
				</td>
				</td>
			</tr>
			<tr>
				<td><html:checkbox property="filtros" styleId="formatoRelatorio" value="<%= String.valueOf(AnalisarAvaliacoesForm.FORMATO_RELATORIO) %>" styleClass="noborder"/></td>
				<td colspan="2">
					Gerar Relatório
				</td>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3">
						<html:button dispatch="list" value="Buscar"/>
						<html:hidden property="buscar" value="true"/>
						<html:button dispatch="cancelar" value="Cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
</html:form>

<c:if test="${ not empty formAnalisarAvaliacoes.analise}">
	<div class="descricaoOperacao">
		<p>Caro Gestor de Pesquisa,</p>
		<p>
			Abaixo estao listados os projetos de pesquisa que ainda necessitam da confirmacao de sua avaliacao.
		</p>
	</div>

	<html:form action="/pesquisa/analisarAvaliacoes" method="post">

	<table class="listagem" align="center" style="width:100%">
		<caption class="listagem">Projetos Encontrados (${fn:length(formAnalisarAvaliacoes.analise)})</caption>

		<thead>
		<tr>
			<th> Projeto </th>
			<th width="12%" style="text-align: center">Avaliações Distribuídas</th>
			<th width="12%" style="text-align: center">Avaliações Efetuadas</th>
			<th width="10%" style="text-align: right;">Média</th>

			<th style="text-align: center">Aprovado</th>
			<th style="text-align: center">Reprovado</th>
			<th style="text-align: center">Indefinido</th>
			<th></th>
		</tr>
		</thead>

		<tbody>
		<c:set var="centro" />

		<c:forEach items="${formAnalisarAvaliacoes.analise}" var="analise" varStatus="status">

		<c:set var="projeto" value="${ analise.key }" />
		<c:set var="aprovado" value=""/>
		<c:set var="reprovado" value=""/>
		<c:set var="indefinido" value=""/>

		<c:choose>
			<c:when test="${analise.value == 1}">
				<c:set var="aprovado" value="checked = checked"/>
			</c:when>
			<c:when test="${analise.value == 2}">
				<c:set var="reprovado" value="checked = checked"/>
			</c:when>
			<c:otherwise>
				<c:set var="indefinido" value="checked = checked"/>
			</c:otherwise>
		</c:choose>

		<c:if test="${ centro != projeto.centro.sigla }">
			<c:set var="centro" value="${projeto.centro.sigla}"/>
			<tr class="centro">
				<td colspan="9"> ${centro} </td>
			</tr>
		</c:if>

		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

			<td>
				<html:link action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=view&id=${projeto.id}">
				${projeto.codigo}
				</html:link>
			</td>
			<td align="center">${projeto.qtdAvaliacoesSubmetidas}</td>
			<td align="center">${projeto.qtdAvaliacoesRealizadas}</td>
			<td align="right">
				<span style="${projeto.media < 5.0 ? "color: #922" : "color: #292" }; font-weight: bold; font-size: 1.1em;">
				<ufrn:format type="valor1" name="projeto" property="media"/>
				</span>
				</td>
			<td align="center"> <input type="radio" name="analise_${projeto.id}" value="true" ${aprovado} /> </td>
			<td align="center"> <input type="radio" name="analise_${projeto.id}" value="false" ${reprovado}/>	</td>
			<td align="center"> <input type="radio" name="analise_${projeto.id}"  value="indefinido" ${indefinido}/> </td>
			<td>
				<html:link action="/pesquisa/avaliarProjetoPesquisa?dispatch=resumoAvaliacoes&obj.projetoPesquisa.id=${projeto.id}">
					<img src="${ctx}/img/view.gif"
						title="Visualizar Resumo das Avaliações"
						alt="Visualizar Resumo das Avaliações"/>
				</html:link>
			</td>
		</tr>
		</c:forEach>
		</tbody>

	<tfoot>
		<tr>
			<td colspan="9" align="center">
		   		<html:button dispatch="cancelar" value="Cancelar"/>
			    <html:button dispatch="analisar" value="Ver Resumo da Análise >>"/>
			</td>
		</tr>
	</tfoot>

	</table>

	</html:form>

</c:if>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>