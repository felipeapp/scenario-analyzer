<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<link rel="stylesheet" media="all" href="/shared/css/relatorio_produtividade.css"/>

<f:view>
	<h3 id="titulo-relatorio">Relatório Individual do Docente para Progressão Funcional</h3>

	<h:outputText value="#{relatorioRID.create}"/>

	<table width="100%" id="identificacao">
		<tr align="left">
			<td style="font-weight: bold">Matrícula:</td>
			<td align="left"> 
				<h:outputText value="#{relatorioRID.servidor.siape }" />
			</td>
		</tr>
		<tr align="left">
			<td style="font-weight: bold">Nome:</td>
			<td align="left">
				<h:outputText value="#{relatorioRID.servidor.pessoa.nome }" />
			</td>
		</tr>
		<tr align="left">
			<td style="font-weight: bold">Centro:</td>
			<td align="left">
				<h:outputText value="#{relatorioRID.servidor.unidade.unidadeResponsavel.nome}" />
			</td>
		</tr>
		<tr align="left">
			<td style="font-weight: bold">Departamento:</td>
			<td align="left">
				<h:outputText value="#{relatorioRID.servidor.unidade.nome}" />
			</td>
		</tr>
		<tr align="left">
			<td style="font-weight: bold">Semestres Avaliados:</td>
			<td ="left">
				De <h:outputText value="#{relatorioRID.anoInicial}" />.<h:outputText value="#{relatorioRID.periodoInicial}" />
				a <h:outputText value="#{relatorioRID.anoFinal}" />.<h:outputText value="#{relatorioRID.periodoFinal}" />
			</td>
		</tr>
	
	</table>
	<hr>
	<br />

	<c:set var="_grupo" />
	<c:set var="_item" />
	<c:forEach items="#{relatorioRID.listaItensRID}" var="linha">

			<c:set var="grupoAtual" value="${linha.grupo}"/>
			<c:if test="${_grupo != grupoAtual}">
				<c:if test="${not empty _grupo}">
					${linha.rodape}
				</c:if>
				${linha.grupo}
				<c:set var="_item" />
				<c:set var="_grupo" value="${grupoAtual}"/>
			</c:if>

			<c:set var="itemAtual" value="${linha.item}"/>
			<c:if test="${_item != itemAtual}">
				<c:if test="${not empty _item}">
					${linha.rodape}
				</c:if>
				${linha.item}
				${linha.cabecalho}
				<c:set var="_item" value="${itemAtual}"/>
			</c:if>	
		${linha.body}
	</c:forEach>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>