<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> > Servidores</h2>
	<h:outputText value="#{servidor.create}" />

	<h:form id="form">
		<table class="formulario">
			<caption>Busca de Servidores</caption>
			<tr>
				<td>Nome:</td>
				<td><h:inputText value="#{servidor.nome}" size="40" /></td>
				<td><h:commandButton actionListener="#{servidor.buscar}" id="btnBuscar"
					value="Buscar" />
			</tr>
		</table>
	</h:form>

	<br>

	<c:if test="${not empty servidor.lista}">

	<center>
	<div class="infoAltRem"><h:graphicImage
		value="/img/icones/page_white_magnify.png" style="overflow: visible;" /> : Visualizar Dados Pessoais <br />
	</div>
	</center>	

	<h:form prependId="false">
		<table class="listagem">
			<caption>Lista de Servidores</caption>
			<thead>
				<td>SIAPE</td>
				<td>Nome</td>
				<td>Cargo</td>
				<td>Categoria</td>
				<td>Ativo</td>
				<td>Formação</td>
				<td>Unidade</td>
				<td></td>
			</thead>

			<c:forEach items="#{servidor.lista}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.siape}-${item.digitoSiape}<br>
					(${item.id})</td>
					<td>${item.nome} (${item.pessoa.id})</td>
					<td>${item.cargo.denominacao}</td>
					<td>${item.categoria.descricao}</td>
					<td>${item.ativo.descricao}</td>
					<td>${item.formacao.denominacao}</td>
					<td>${item.unidade.codigo}-${item.unidade.nome}</td>
					
					<td>
										
						<h:commandLink action="#{servidor.exibirDadosPessoais}" title="Visualizar Dados Pessoais" id="dados">
							<h:graphicImage value="/img/icones/page_white_magnify.png"/>
							<f:param name="id" value="#{item.id}"/> 
						</h:commandLink>
						
																		
					</td>
				</tr>
			</c:forEach>
		</table>
	</h:form>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
