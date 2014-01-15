<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Residência Médica</h2>

	<h:form>
		<div class="infoAltRem">
			<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;" /> 
			<h:commandLink action="#{residenciaMedica.preCadastrar}" value="Cadastrar Nova Residência Médica" />
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Detalhar Residência Médica
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Residência Médica<br />
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Residência Médica
		</div>
	

	<h:outputText value="#{residenciaMedica.create}" />
	<table class="listagem" width="100%">
	  <caption class="listagem">Lista de Residência Médicas</caption>
		<thead>
			<tr>
				<th>Programa</th>
				<th>Hospital</th>
				<th style="text-align: center;">Ano/Período</th>
				<th style="text-align: right;">CH Semanal</th>
				<th colspan="3"></th>
				
			</tr>
		</thead>

		<c:set var="lista" value="${residenciaMedica.allAtividades}" />
		<c:if test="${empty lista}">
			<tr>
			<td colspan="6">
			<br />
			<center>
			<span style="color:red;">Nenhuma Residência Médica Encontrada.</span>
			</center>
			</td>
			</tr>
		</c:if>
		<c:forEach items="${residenciaMedica.allAtividades}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.programaResidenciaMedica.nome}</td>
				<td>${item.programaResidenciaMedica.hospital.nome}</td>
				<td align="center">${item.ano}.${item.semestre}</td>
				<td align="right">${item.chSemanal}h</td>
				<td width=10 align="right"><input type="hidden" value="${item.id}" name="id" />
				<h:commandButton image="/img/view.gif" alt="Detalhar"
					action="#{residenciaMedica.detalhar}"/>
				</td>
				<td width="10" align="right">
				<input type="hidden" value="${item.id}" name="id" />
				<h:commandButton image="/img/alterar.gif" value="Alterar" action="#{residenciaMedica.atualizar}" />
				</td>
				<td width="10" align="right"><input type="hidden" value="${item.id}" name="id" />
				<h:commandButton image="/img/delete.gif" alt="Remover"
					action="#{residenciaMedica.remover}" onclick="#{confirmDelete}"/>
				</td>
			</tr>
		</c:forEach>
	</table>
 </h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>