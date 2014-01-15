<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Evento</h2>

	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/><h:commandLink action="#{tipoEvento.preCadastrar}" value="Cadastrar"/><h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
		</div>
	</h:form>
	<h:outputText value="#{tipoEvento.create}" />
<c:set var="lista" value="${tipoEvento.allAtivos}" />
<c:if test="${empty lista}">
	<br />
	<center>
	<span style="color:red;">Nenhum Tipo Evento Encontrado.</span>
	</center>
</c:if>
<c:if test="${not empty lista}">	

	
	<table class="listagem">
		<caption class="listagem">Lista de Tipos de Evento</caption>
		<thead>
			<td>Descrição</td>
			<td></td>
			<td></td>
		</thead>
		<c:forEach items="${tipoEvento.allAtivos}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>


				<td width=20>
				 <h:form>
				  <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/alterar.gif" value="Alterar"
						action="#{tipoEvento.atualizar}" />
				  </h:form>
				 </td>

				<td width=25>
				 <h:form>
				   <input type="hidden" value="${item.id}" name="id" /> <h:commandButton
						image="/img/delete.gif" alt="Remover"
						action="#{tipoEvento.remover}" onclick="#{confirmDelete}"/>
				 </h:form>
				</td>

			</tr>
		</c:forEach>
	</table>

</c:if>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
