<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Participação em Sociedade</h2>
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
			 <h:commandLink action="#{tipoParticipacaoSociedade.preCadastrar}"
			 value="Cadastrar"/><h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
		</div>
	</h:form>
	<h:outputText value="#{tipoParticipacaoSociedade.create}" />
<c:set var="lista" value="${tipoParticipacaoSociedade.allAtivos}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span style="color:red;">Nenhum Tipo Participação Sociedade Encontrado.</span>
	</center>
</c:if>
<c:if test="${not empty lista}">	
	
	<table class="listagem">
		<caption class="listagem">Lista de Tipos de Participação em Sociedade</caption>
		<thead>
			<td>Descrição</td>
			<td></td>
			<td></td>
		</thead>
		<c:forEach items="${tipoParticipacaoSociedade.allAtivos}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>


				<td width=20>
					<h:commandLink action="#{tipoParticipacaoSociedade.atualizar}" >
						<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>						
				<td width=25>
					<h:commandLink action="#{tipoParticipacaoSociedade.remover}" onclick="#{confirmDelete}">
						<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
						<f:param name="id" value="#{item.id}"/>
					</h:commandLink>
				</td>

			</tr>
		</c:forEach>
	</table>

</c:if>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>