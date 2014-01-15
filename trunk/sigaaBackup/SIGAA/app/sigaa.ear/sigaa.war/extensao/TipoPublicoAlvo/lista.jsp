<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Tipo de Público Alvo</h2>
	<h:outputText value="#{tipoPublicoAlvo.create}" />
	
<h:form id="form">
	
	<center>
			<h:messages/>
			<div class="infoAltRem">
					<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>:
					<h:commandLink action="#{tipoPublicoAlvo.preCadastrar}" value="Cadastrar"/>
					<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
			        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover <br/>
			</div>
	</center>
		
	<table class="listagem">
		<caption class="listagem">Lista de Tipos de Público Alvo</caption>
		<thead>
			<tr>
				<th>Descrição</th>
				<th>Grupo</th>
				<th></th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{tipoPublicoAlvo.allTipoPublicoAlvo}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.descricao}</td>
					<td>${item.grupo.descricao}</td>
					<td width="2%">
						<h:commandLink title="Alterar" action="#{tipoPublicoAlvo.atualizar}" style="border: 0;" id="bt_alterar">
					       <f:param name="id" value="#{item.id}"/>
			               <h:graphicImage url="/img/alterar.gif" />
						</h:commandLink>
					</td>
					<td width="2%">
						<h:commandLink title="Remover" action="#{tipoPublicoAlvo.preInativar}" style="border: 0;" id="bt_remover">
					       <f:param name="id" value="#{item.id}"/>
			               <h:graphicImage url="/img/delete.gif" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>