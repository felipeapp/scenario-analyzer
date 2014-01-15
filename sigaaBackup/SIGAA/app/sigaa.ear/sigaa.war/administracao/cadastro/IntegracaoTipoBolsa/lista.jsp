<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Definição dos tipos de Bolsa</h2>
<h:form>
	<center>
		<h:messages/>
		<div class="infoAltRem">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
				<h:commandLink action="#{integracaoTipoBolsaMBean.preCadastrar}" value="Cadastrar"/>
				<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Remover
		</div>
	</center>

	<table class=formulario width="90%">
		<caption class="listagem">Lista das associações cadastradas</caption>
		<thead>
		<tr>
			<td>Bolsa Sigaa</td>
			<td>Bolsa Sipac</td>
			<td style="text-align: center;">Unidade Federativa</td>
			<td>Municipio</td>
			<td></td>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="#{integracaoTipoBolsaMBean.allAtivos}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricaoBolsaSigaa}</td>
				<td>${item.descricaoBolsaSipac}</td>
				<td style="text-align: center;">${item.uf}</td>
				<td>${item.municipio}</td>
				<td width="20">
					<h:commandLink action="#{integracaoTipoBolsaMBean.inativar}" onclick="#{confirmDelete}" >
						<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>