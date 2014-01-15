<%@include file="/public/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<style>
	tr.agrupador {background: #C8D5EC;font-weight: bold;padding-left: 20px;}
	td.foto {padding: 2px 5px;}
	span.programa {display: block;}
	span.pagina {display: block;margin-top: 10px;}
	span.pagina a {padding-left: 20px;background: url(/sigaa/public/images/docente/icones/perfil.png) no-repeat;color: #D59030;}
</style>
<f:view>
	<h2> Departamentos da ${ configSistema['siglaInstituicao'] } </h2>		
	<h:outputText value="#{portalPublicoDepartamento.create}"/>

	<div class="descricaoOperacao">
		Nesta página você encontrará  os departamentos da ${ configSistema['siglaInstituicao'] }, organizados pelos seus respectivos centros.
	</div>

	<h:form id="form">
	<h:messages showDetail="true"></h:messages>
			<table class="formulario" align="center" width="455px">
			<caption class="listagem">Informe os critérios de consulta</caption>
				<tr>
					<td align="right" width="78px">&nbsp;Centros:</td>
					<td >
						<h:selectOneMenu id="programas" value="#{portalPublicoDepartamento.unidade.id}" >
							<f:selectItem itemLabel=" -- TODOS -- " itemValue="0"/>
							<f:selectItems value="#{unidade.allCentroCombo}"/>
						</h:selectOneMenu>
					</td>
				</tr>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton id="buscar" action="#{portalPublicoDepartamento.buscarDepartamentosDetalhes}" value="Buscar"/>
						<h:commandButton id="cancelar" action="#{portalPublicoDepartamento.cancelar}" onclick="#{confirm}" value="Cancelar"/>
					</td>
				</tr>
			</tfoot>
			</table>
	</h:form>
	
	<br>
	<table class="listagem" style="width: 75%;">
	<c:if test="${not empty departamentos}">
	<caption>DEPARTAMENTOS ENCONTRADOS</caption>
	<tbody>
		<c:set var="ultCentro" value=""/>
			<c:forEach var="departamento" items="${departamentos}" varStatus="status">
				<c:if test="${ultCentro!=departamento.gestora}">
					<tr>
					<td class="subListagem" colspan="2">
						<span class="departamento">${departamento.gestora.nome}</span>
					</td>
					</tr>
				</c:if>
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td>
						<a href="portal.jsf?lc=${portalPublicoDepartamento.lc}&id=${departamento.id}" style="link">
						${departamento.nome}<br/>
						</a>
						<c:if test="${departamento.detalhesSite.url}">
						<br/>${departamento.detalhesSite.url}
						</c:if>
					</td>
						<td width="18px" class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<a title="Clique aqui para acessar a página pública deste departamento" 
							href="/sigaa/public/departamento/portal.jsf?id=${departamento.id}">
							<h:graphicImage url="/img/view.gif"/>
							</a>
						</td>
					</tr>
					<c:set var="ultCentro" value="${departamento.gestora}"/>
		 	</c:forEach>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" align="center"> <b>${fn:length(departamentos)} Departamento(s) encontrado(s) </b></td>
				</tr>
			</tfoot>
	</c:if>
	</table>
	<br>
	<center>
		<a href="javascript: history.go(-1);"> << Voltar </a>
	</center>
</f:view>
<br />
<%@include file="/public/include/rodape.jsp" %>