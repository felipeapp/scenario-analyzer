<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> > Programa de Residência Médica</h2>

	<h:form>
		<div class="infoAltRem">
			 <h:graphicImage value="/img/adicionar.gif"style="overflow: visible;" /> <h:commandLink action="#{programaResidencia.preCadastrar}" value="Cadastrar Novo Programa"></h:commandLink>
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Programa
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Programa <br/>
		</div>
	</h:form>


	<h:outputText value="#{programaResidencia.create}" />
	<table class=listagem width="100%">
		<caption class="listagem">Lista de Programas de Residência Médicas</caption>
		<thead>
			<tr>
				<td>Nome</td>
				<td>Hospital</td>
				<td>Unidade do Programa</td>
				<td></td>
				<td></td>
			</tr>
		</thead>

		<c:set var="lista" value="${programaResidencia.allAtivos}" />
		<c:if test="${empty lista}">
			<tr>
			<td colspan="6">
			<br />
			<center>
			<span style="color:red;">Nenhum Programa de Residência Médica Encontrada.</span>
			</center>
			</td>
			</tr>
		</c:if>
		<c:forEach items="${programaResidencia.allAtivos}" var="item" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${item.nome}</td>
				<td>${item.hospital.nome}</td>
				<td>${item.unidadePrograma.nome}</td>
				<td width=20>
					<h:form><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/alterar.gif" value="Alterar"
						alt="Alterar Programa" title="Alterar Programa" action="#{programaResidencia.atualizar}" />
					</h:form>
				</td>
				<td width=25>
					<h:form><input type="hidden" value="${item.id}" name="id" />
					<h:commandButton image="/img/delete.gif" alt="Remover Programa"
						title="Remover Programa" action="#{programaResidencia.inativar}" 
							onclick="#{confirmDelete}" immediate="true"/>
					</h:form>
				</td>
			</tr>
		</c:forEach>
	</table>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>