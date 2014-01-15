<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	
	<h2><ufrn:subSistema /> > Sub-Tipo Artístico</h2>
	<h:outputText value="#{subTipoArtistico.create}" />
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/><h:commandLink action="#{subTipoArtistico.preCadastrar}" value="Cadastrar Sub-Tipos Artístico"/><h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Sub-Tipos Artístico
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Sub-Tipos Artístico
		</div>
	</h:form>
	
	<c:set var="lista" value="${subTipoArtistico.allAtivos}" />
	<c:if test="${empty lista}">
		<br />
		<center>
		<span style="color:red;">Nenhum SubTipo-Artístico Encontrado.</span>
		</center>
	</c:if>
	<c:if test="${not empty lista}">	
	
	<table class="listagem">
		<caption class="listagem">Lista de Sub-Tipos Artístico</caption>
		<thead>
			<td>Descrição</td>
			<td>Tipo Produção</td>
			<td></td>
			<td></td>
		</thead>
		<c:forEach items="${subTipoArtistico.allAtivos}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td>${item.tipoProducao.descricao}</td>

				<td width=20>
				 <h:form>
				  <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar"
						action="#{subTipoArtistico.atualizar}" />
				 </h:form>
				</td>

				<td width=25>
				 <h:form>
				  <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{subTipoArtistico.remover}" onclick="#{confirmDelete}"/>
				 </h:form>
				</td>

			</tr>
		</c:forEach>
	</table>

</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>