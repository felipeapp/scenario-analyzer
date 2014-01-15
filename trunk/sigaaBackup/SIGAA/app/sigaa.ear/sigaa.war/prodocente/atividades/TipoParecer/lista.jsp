<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Parecer</h2>
	<br>
	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
			 <h:commandLink action="#{tipoParecer.preCadastrar}"
			 value="Cadastrar Tipo de Parecer"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Tipo de Parecer
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Tipo de Parecer<br/>
		</div>
	</h:form>
	<h:outputText value="#{tipoParecer.create}" />
	<table class="listagem">
		<caption class="listagem">Lista de Tipos de Parecer</caption>
		<thead>

			<td>Descrição</td>
			<td></td>
			<td></td>
		</thead>
		<c:forEach items="${tipoParecer.allAtivos}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.descricao}</td>
				<td width=20>
					<h:form><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/alterar.gif" value="Alterar"
						action="#{tipoParecer.atualizar}" />
					</h:form>
				</td>

				<td width=25>
					<h:form><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/delete.gif" alt="Remover"
						action="#{tipoParecer.remover}" onclick="#{confirmDelete}" immediate="true"/>
					</h:form>
				</td>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
