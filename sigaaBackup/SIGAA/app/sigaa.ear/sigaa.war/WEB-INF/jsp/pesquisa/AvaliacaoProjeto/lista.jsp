<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.pesquisa.form.AvaliacaoProjetoForm"%>
<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Consulta de Avaliações de Projetos
</h2>

<html:form action="/pesquisa/avaliarProjetoPesquisa" method="post">

	<c:set var="centros" value="${ formAvaliacaoProjeto.referenceData.centros }"/>
	<c:set var="tipos" value="${ formAvaliacaoProjeto.referenceData.tipos }"/>
	<c:set var="status" value="${ formAvaliacaoProjeto.referenceData.status }"/>
	<c:set var="editaisPesquisa" value="${ formAvaliacaoProjeto.referenceData.editaisPesquisa }"/>

	<table class="formulario" align="center" style="width: 99%">
		<caption class="listagem">Critérios de Busca</caption>

		<tr>
			<td></td>
			<td> Ano dos Projetos: </td>
			<td> <html:text property="ano" size="5"></html:text> </td>
		</tr>
		<tr>
			<td> <html:checkbox property="filtros" styleId="consultorCheck" value="<%="" + AvaliacaoProjetoForm.CONSULTOR  %>" styleClass="noborder"/> </td>
			<td> <label for="consultorCheck">Consultor:</label> </td>
			<td>
				<c:set var="idAjax" value="obj.consultor.id"/>
				<c:set var="nomeAjax" value="obj.consultor.nome"/>
				<%@include file="/WEB-INF/jsp/include/ajax/consultor.jsp" %>
			</td>
		</tr>
		<tr>
			<td> <html:checkbox property="filtros" styleId="centroCheck" value="<%="" + AvaliacaoProjetoForm.CENTRO %>" styleClass="noborder"/> </td>
			<td> <label for="centroCheck">Centro:</label> </td>
			<td>
				<html:select property="obj.projetoPesquisa.centro.id" style="width: 450px;" styleId="centro" onchange="$('centroCheck').checked = true;">
					<html:options collection="centros" property="unidade.id" labelProperty="unidade.codigoNome" />
				</html:select>
			</td>
		</tr>
		<tr>
			<td> <html:checkbox property="filtros" styleId="tipoCheck" value="<%="" + AvaliacaoProjetoForm.TIPO_DISTRIBUICAO%>" styleClass="noborder"/> </td>
			<td> <label for="tipoCheck">Tipo da Distribuição:</label> </td>
			<td>
				<html:select property="obj.tipoDistribuicao" style="width: 200px;" styleId="tipoDistribuicao" onchange="$('tipoCheck').checked = true;">>
					<html:options collection="tipos" property="key" labelProperty="value" />
				</html:select>
			</td>
		</tr>
		<tr>
			<td> <html:checkbox property="filtros" styleId="statusCheck" value="<%="" + AvaliacaoProjetoForm.SITUACAO%>" styleClass="noborder"/> </td>
			<td> <label for="statusCheck">Status da Avaliação:</label> </td>
			<td>
				<html:select property="obj.situacao" style="width: 200px;" styleId="statusAvaliacao" onchange="$('statusCheck').checked = true;">>
					<html:options collection="status" property="key" labelProperty="value" />
				</html:select>
			</td>
		</tr>
		<tr>
			<td> <html:checkbox property="filtros" styleId="editalCheck" value="<%="" + AvaliacaoProjetoForm.EDITAL%>" styleClass="noborder"/> </td>
			<td> <label for="editalCheck">Edital:</label> </td>
			<td>
				<html:select property="obj.projetoPesquisa.edital.id" style="width: 450px;" styleId="editalPesquisa" onchange="$('editalCheck').checked = true;">>
					<html:options collection="editaisPesquisa" property="id" labelProperty="descricao" />
				</html:select>
			</td>
		</tr>

		<tfoot>
			<tr>
				<td colspan="3">
					<html:button dispatch="list" value="Buscar"/>
					<html:button dispatch="cancelar" value="Cancelar"/>
				</td>
			</tr>
		</tfoot>

	</table>

</html:form>


<c:if test="${ not empty lista }">
<style>
	table.listagem tr.centro td {
		background: #C4D2EB;
		font-weight: bold;
		text-align: center;
		font-size: 1.2em;
	}

	table.listagem tr.projeto td {
		border-bottom: 1px solid #BBB;
		background: #F2F2F2;
		font-style: italic;
	}

	table.listagem tr.consultor td{
		background: #FFF;
	}
</style>

	<br /><br />
	<table class="listagem">
		<caption> Avaliações de Projetos de Pesquisa (${ fn:length(lista)} encontradas)</caption>

		<c:set var="centro" value="" />
		<c:set var="projeto" value="" />
		<c:forEach var="avaliacao" items="${ lista }" varStatus="loop">

		<%-- Centros --%>
		<c:if test="${ avaliacao.projetoPesquisa.centro.id != centro}">
		<c:set var="centro" value="${ avaliacao.projetoPesquisa.centro.id }" />
		<tr class="centro">
			<td colspan="5"> ${ avaliacao.projetoPesquisa.centro.sigla } </td>
		</tr>
		</c:if>

		<%-- Projetos --%>
		<c:if test="${ avaliacao.projetoPesquisa.id != projeto}">
		<c:set var="projeto" value="${ avaliacao.projetoPesquisa.id }" />
		<tr class="projeto">
			<td valign="top"> ${ avaliacao.projetoPesquisa.codigo } </td>
			<td colspan="4">  ${ avaliacao.projetoPesquisa.titulo } </td>
		</tr>
		</c:if>

		<%-- Consultores --%>
		<tr class="consultor">
			<td> </td>
			<td width="50%"> <strong>c${ avaliacao.consultor.codigo }</strong></td>
			<td>
				${ avaliacao.statusAvaliacao }
				<c:if test="${avaliacao.situacao == 2 }">
				<small>em <ufrn:format type="data" name="avaliacao" property="dataAvaliacao" /></small>
				</c:if>
			</td>
			<td>
				<span style="${avaliacao.media < 5.0 ? "color: #922" : "color: #292" }; font-weight: bold;">
					<ufrn:format type="valor1" name="avaliacao" property="media" />
				</span>
			</td>
			<td>
				<ufrn:link action="/pesquisa/avaliarProjetoPesquisa" param="obj.id=${avaliacao.id}&dispatch=view">
					<img src="${ctx}/img/pesquisa/view.gif"
						alt="Visualizar Avaliação"
						title="Visualizar Avaliação" />
				</ufrn:link>
			</td>
		</tr>

		</c:forEach>
	</table>
</c:if>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>