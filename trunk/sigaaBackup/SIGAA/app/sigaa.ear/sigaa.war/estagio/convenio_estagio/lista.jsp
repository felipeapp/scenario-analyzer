<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> &gt; Consulta de Conv�nio de Est�gio</h2>
<h:form id="form">
<a4j:keepAlive beanName="convenioEstagioMBean" />

	<%@include file="include/_busca.jsp"%>
	
	<c:if test="${(not empty convenioEstagioMBean.listaConvenios) or (not empty convenioEstagioMBean.pendentesAnalise)}">
		<center>
			<div class="infoAltRem">
				<c:if test="${ !convenioEstagioMBean.modoSeletor }">
					<c:if test="${!convenioEstagioMBean.permiteAnalisarConvenio}">
						<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Conv�nio de Est�gio
					</c:if>	
					<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Termo de Conv�nio de Est�gio	
					<c:if test="${convenioEstagioMBean.permiteAnalisarConvenio}">
						<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Analisar Conv�nio de Est�gio
					</c:if>				
				</c:if>		
				<c:if test="${ convenioEstagioMBean.modoSeletor }">			
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Selecionar Conv�nio de Est�gio
				</c:if>
			</div>
		</center>
	</c:if>
		
	<c:set var="nenhumConvenio" value="#{true}"/>
	<c:if test="${!convenioEstagioMBean.cadastroOferta}">	
		<c:set var="convenios" value="#{convenioEstagioMBean.pendentesAnalise}"/>
		<c:if test="${not empty convenios}">
			<c:set var="nenhumConvenio" value="#{false}"/>
			<c:set var="nomeCaption" value="Conv�nios de Est�gio Pendentes de An�lise"/>	
			<%@include file="include/_lista.jsp"%>
			<br/>
		</c:if>	
	</c:if>		
	
	<c:set var="convenios" value="#{convenioEstagioMBean.listaConvenios}"/>
	<c:set var="nomeCaption" value="Conv�nios de Est�gio Encontrados"/>
	<c:if test="${not empty convenios}">
		<c:set var="nenhumConvenio" value="#{false}"/>
		<%@include file="include/_lista.jsp"%>	
	</c:if>		
	<c:if test="${nenhumConvenio}">
		<table class="listagem" style="width: 100%">
			<caption class="listagem">${nomeCaption}</caption>
				<tr>
					<td colspan="9" style="text-align: center;">
						<i>Nenhum Conv�nio de Est�gio encontrado.</i>
					</td>
				</tr>
		</table>
	</c:if>	
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>