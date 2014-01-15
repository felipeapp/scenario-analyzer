<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2><ufrn:subSistema /> > Tipo de Bolsa</h2>
<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
			 <h:commandLink action="#{tipoBolsa.preCadastrar}"
			 value="Cadastrar Tipo de Bolsa"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Tipo de Bolsa
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Tipo de Bolsa<br/>
		</div>
	</h:form>
<h:outputText value="#{tipoBolsa.create}"/>
<table class="listagem">
	<caption class="listagem"> Lista de Tipos de Bolsas</caption>
		<thead>
			<td>Descrição</td>
			<td></td>
			<td></td>
		</thead>
	<c:forEach items="${tipoBolsa.allAtivos}" var="item" varStatus="status">
	  <tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td>${item.descricao}</td>
			<td  width=20>
				<h:form><input type="hidden" value="${item.id}" name="id"/>
				<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{tipoBolsa.atualizar}"/>
				</h:form>
			</td>
			<td  width=25>
				<h:form><input type="hidden" value="${item.id}" name="id"/>
				<h:commandButton image="/img/delete.gif" alt="Remover" action="#{tipoBolsa.remover}"
					onclick="#{confirmDelete}" immediate="true"/>
				</h:form>
			</td>
	  </tr>
</c:forEach>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>