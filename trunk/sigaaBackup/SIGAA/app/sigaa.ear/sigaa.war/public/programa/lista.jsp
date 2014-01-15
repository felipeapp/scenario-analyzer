<%@include file="/public/include/cabecalho.jsp" %>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<style>
	.legenda{width:75%;}
</style>
<h2> Programas de Pós-Graduação da ${ configSistema['siglaInstituicao'] } </h2>
<f:view locale="#{portalPublicoPrograma.lc}">
	<f:loadBundle basename="br.ufrn.sigaa.sites.jsf.Mensagens" var="idioma"/>
	<h:outputText value="#{portalPublicoPrograma.create}"/>
	
	<div class="descricaoOperacao">
		<p>
			<h:outputFormat escape="false" value="#{idioma.introducaoBuscaPrograma}">
				<f:param value="#{ configSistema['siglaInstituicao'] }" />
			</h:outputFormat>
		</p> 
	</div>

	<h:form id="form">
		<h:messages showDetail="true"></h:messages>
		
		<table class="formulario" align="center" width="50%" >
		<caption class="listagem"><h:outputText value="#{idioma.captionBusca}"/></caption>
			<tr>
				<td width="15%" align="right">&nbsp;<h:outputText value="#{idioma.programa}"/>:</td>
				<td><h:inputText id="nomePrograma" value="#{portalPublicoPrograma.unidade.nome}"
				 style="width:97%"/></td>
			</tr>
			<tr>
				<td align="right">&nbsp;<h:outputText value="#{idioma.centro}"/>:</td>
				<td>
					<h:selectOneMenu id="programas" value="#{portalPublicoPrograma.unidade.gestora.id}"
					 style="width:98%">
						<f:selectItem itemLabel=" -- #{idioma.todos} -- " itemValue="0"/>
						<f:selectItems value="#{unidade.allCentrosEscolasCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="buscar" action="#{portalPublicoPrograma.buscarProgramas}" 
					value="#{idioma.buscar}"/>&nbsp;
					<h:commandButton id="cancelar" action="#{portalPublicoPrograma.cancelar}" 
					onclick="#{confirm}" value="#{idioma.cancelar}"/>
				</td>
			</tr>
		</tfoot>
		</table>

		<br>
		<c:if test="${not empty programas}">
			<div class="legenda" >
				<f:verbatim><h:graphicImage url="/img/view.gif" />:</f:verbatim> <h:outputText value="#{idioma.visualizarPrograma}"/>
			</div>
			<br clear="all"/>
			<table class="listagem" style="width: 75%;">
				
				<caption> (${fn:length(programas)}) <h:outputText value="#{idioma.programasEncontrados}"/> </caption>
				
				<tbody>
				<c:set var="ultCentro" value=""/>
				
				<c:forEach var="programa" items="#{programas}" varStatus="status">
					
					<c:if test="${ultCentro!=programa.gestora}">
						<tr>
							<td colspan="2"  class="subListagem">
							<span class="programa">${programa.gestora}</span>
							</td>
						</tr>
					</c:if>
					
					<tr>
						<td class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<a href="portal.jsf?lc=${portalPublicoPrograma.lc}&id=${programa.id}">
								${programa.nome}
							</a>
						</td>
						<td width="18px" class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<a href="portal.jsf?lc=${portalPublicoPrograma.lc}&id=${programa.id}"
								title="${idioma.visualizarPrograma}">
								<h:graphicImage url="/img/view.gif"/>
							</a>
						</td>
					</tr>
						<c:set var="ultCentro" value="${programa.gestora}"/>
				</c:forEach>
				
				</tbody>
				
				<tfoot>
					<tr>
					<td colspan="3" align="center">
					 <b>(${fn:length(programas)}) <h:outputText value="#{idioma.programasEncontrados}"/> </b>
					 </td>
					</tr>
				</tfoot>
				
			</table>
		</c:if>	
		
	</h:form>	
		
	<br>
	<center>
		<a href="javascript: history.go(-1);"> << ${idioma.voltar} </a>
	</center>
</f:view>
<br/>
<%@include file="/public/include/rodape.jsp" %>