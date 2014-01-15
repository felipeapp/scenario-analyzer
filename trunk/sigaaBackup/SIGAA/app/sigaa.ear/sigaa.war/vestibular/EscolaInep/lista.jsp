<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Cadastro de Escolas do INEP</h2>

	<a4j:keepAlive beanName="escolaInep"></a4j:keepAlive>

	<h:form>
		<table class="formulario" width="100%">
		<caption>Selecione a UF e o Município para Exibir a Lista de Escolas</caption>
		<tbody>
			<tr>
				<th>Unidade Federativa:</th>
				<td><h:selectOneMenu
						valueChangeListener="#{escolaInep.unidadeFederativaListener}"
						value="#{escolaInep.obj.endereco.unidadeFederativa.id}"
						onchange="submit()">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{unidadeFederativa.allCombo}" />
					</h:selectOneMenu></td>
				
			</tr>
			<tr>
				<th>Município:</th>
				<td><h:selectOneMenu
						valueChangeListener="#{escolaInep.municipioListener}"
						value="#{escolaInep.obj.endereco.municipio.id}"
						onchange="submit()">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{escolaInep.municipiosCombo}" />
					</h:selectOneMenu></td>
			</tr>
		</tbody>
		</table>
		<br>
		<div align="center">
			<h:outputText value="Não foram encontradas Escolas cadastradas com o critério informado." 
				rendered="#{empty escolaInep.escolas}" /> 
		<c:if test="${not empty escolaInep.escolas}">
		<table class="listagem">
		<caption class="listagem">Lista de Escolas Cadastradas (${fn:length(escolaInep.escolas)})</caption>
			<thead>
				<tr>
					<td>Nome</td>
					<td>Bairro</td>
					<td>Rede de Ensino</td>
				</tr>
			</thead>
			<c:forEach items="#{escolaInep.escolas}" var="item"
				varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.nome}</td>
					<td>${item.endereco.bairro}</td>
					<td>${item.tipoRedeEnsino.descricao}</td>
				</tr>
			</c:forEach>
		</table>
		</c:if>
		<br><br>
			<h:commandLink immediate="true" value="<< Voltar" action="#{escolaInep.cancelar}" />
		</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>