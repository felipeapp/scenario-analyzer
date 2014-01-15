<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>

<h2>Relatório de Docentes Doutores sem Atividades e Orientandos na Pós Graduação</h2>
<div id="parametrosRelatorio">
	<table >
		<tr>
			<th>Departamento:</th>
			<td>
				<h:outputText value="TODOS" rendered="#{relatorioAtividadesDocente.idUnidade == 0}" />
				<h:outputText value="#{relatorioAtividadesDocente.obj.unidade.nome}" rendered="#{relatorioAtividadesDocente.idUnidade != 0}" />
			</td>
		</tr>
		<tr>
			<th>Ano-Período:</th>
			<td><h:outputText value="#{relatorioAtividadesDocente.ano}"/>.<h:outputText value="#{relatorioAtividadesDocente.periodo}"/></td>
		</tr>
	</table>
</div>
</br>

<c:set var="grupoUnidade" value="-1" />
<c:set var="fechaTabela" value="false" />

<c:forEach items="#{relatorioAtividadesDocente.docentes}" var="item">
	<c:set var="loopUnidade" value="${item.unidade.id}" />
	<c:if test="${loopUnidade != grupoUnidade}">
		<c:set var="grupoUnidade" value="${loopUnidade }"/>
		<c:if test="${fechaTabela}">
			</tbody>
			</table>
			<br/>
		</c:if>
		<c:set var="fechaTabela" value="true" />
		<table class="tabelaRelatorio" style="width: 100%">
			<caption>${item.unidade.nome}</caption>
			<thead>
				<tr>
					<th style="text-align: right;" width="10%">SIAPE</th>
					<th>Nome</th>
					<th width="15%">Titulação</th>
				</tr>
			</thead>
			<tbody>
	</c:if>
			<tr>
				<td style="text-align: right;">${item.siape}</td>
				<td>${item.nome}</td>
				<td>${item.formacao.tipoDescricaoTitulo}</td>
			</tr>
</c:forEach>
<c:if test="${fechaTabela}">
	</tbody>
	</table>
	<br/>
</c:if>
<div align="center">Total de registros: ${fn:length(relatorioAtividadesDocente.docentes) }</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>