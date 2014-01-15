<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Relatório Quantitativo de Monitores Por Projetos</h2>



	<h:outputText value="#{comissaoMonitoria.create}"/>
	
<h:form>
	<table class="listagem" width="100%">

		<c:set value="${comissaoMonitoria.projetos}" var="lista"/>
		<c:set value="${comissaoMonitoria.unidade}" var="centro"/>		
		<caption class="listagem"> Monitores Ativos Por Projetos <br/>
		 ${comissaoMonitoria.total} Monitores
		 	<c:if test="${comissaoMonitoria.checkBuscaTipoMonitor}">
				${comissaoMonitoria.idTipoMonitor == 1?'Não Remunerados':''}
				${comissaoMonitoria.idTipoMonitor == 2?'Bolsistas':''}
				${comissaoMonitoria.idTipoMonitor == 3?'Não Classificados':''}
				${comissaoMonitoria.idTipoMonitor == 4?'Em Espera':''}
			</c:if> 		
			<c:if test="${comissaoMonitoria.checkBuscaCurso}">
				do curso ${comissaoMonitoria.curso.descricao}
			</c:if>
			distribuídos em projetos 		
			<c:if test="${comissaoMonitoria.checkBuscaCentro}">
				  do ${comissaoMonitoria.unidade.sigla}
			</c:if> 	
			<c:if test="${comissaoMonitoria.checkBuscaAno}">
				 submetidos em ${comissaoMonitoria.ano}
			</c:if>
			 								
		</caption>		
<tbody>						


	<tr>
		<td colspan="2">

		<t:dataTable value="#{comissaoMonitoria.projetos}" var="projeto" align="center" rowClasses="linhaPar, linhaImpar" width="100%">
		

					<t:column rendered="#{projeto.totalMonitores != 0}">
						<f:facet name="header">
							<f:verbatim>Ano</f:verbatim>
						</f:facet>
						<h:outputText value="#{projeto.ano}" />						
					</t:column>

					<t:column rendered="#{projeto.totalMonitores != 0}">
						<f:facet name="header">
							<f:verbatim>Título</f:verbatim>
						</f:facet>
						<h:outputText value="#{projeto.titulo}" />						
					</t:column>

					<t:column rendered="#{projeto.totalMonitores != 0}">
						<f:facet name="header">
							<f:verbatim>Centro</f:verbatim>
						</f:facet>
						<h:outputText value="#{projeto.unidade.sigla}" />						
					</t:column>

					
					<t:column width="10%" rendered="#{projeto.totalMonitores != 0}">
						<f:facet name="header">
							<f:verbatim>Total</f:verbatim>
						</f:facet>
						<h:outputText value="#{projeto.totalMonitores}" />						
					</t:column>					
					
		</t:dataTable>
		</td>
	</tr>
		<tr>
			<td>TOTAL DE MONITORES ATIVOS</td>
	    	<td>${comissaoMonitoria.total}</td>
	    </tr>
	</tbody>
	
	<tfoot>
		<tr>
		<td align="center" colspan="2">
			<input type="button" value="Imprimir" onclick="javascript:window.print()"/>		
		</td>
	</tr>
	</tfoot>
</table>


</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>