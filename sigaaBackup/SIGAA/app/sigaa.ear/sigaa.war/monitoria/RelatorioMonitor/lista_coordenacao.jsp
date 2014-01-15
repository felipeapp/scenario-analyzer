<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<%@page import="br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria"%>
	
	<h:messages/>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	<h2><ufrn:subSistema /> > Validação de Relatórios de Desligamento de Monitor</h2>

	<h:outputText value="#{relatorioMonitor.create}"/>
	<h:outputText value="#{consultarMonitor.create}"/>	

	<div class="infoAltRem">
	    <h:graphicImage value="/img/monitoria/form_blue.png" 	style="overflow: visible;"/>: Visualizar Relatório
	    <h:graphicImage value="/img/monitoria/user1_view.png" style="overflow: visible;"/>: Visualizar Dados do Monitor
		<h:graphicImage url="/img/seta.gif" style="overflow: visible;"/>: Validar Relatório
	</div>
	<br/>

	<h:form>
	<table class="listagem" width="100%">
	<caption>Lista de Relatórios (${fn:length(relatorioMonitor.relatoriosMonitoresUsuarioAtualCoordenador)})</caption>
	<thead>
		<tr>
			<th>Monitor</th>
			<th>Vínculo</th>
			<th>Situação do Relatório</th>
			<th></th>
		</tr>
	</thead>

	<c:set var="ASSUMIU_MONITORIA" value="<%= String.valueOf(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA) %>" scope="application"/>
	
	<c:if test="${empty relatorioMonitor.relatoriosMonitoresUsuarioAtualCoordenador}">
            <tr> <td colspan="6" align="center"> <font color="red">Não há Relatórios de Desligamento de Monitores para validar.</font> </td></tr>
	</c:if>

	<c:if test="${not empty  relatorioMonitor.relatoriosMonitoresUsuarioAtualCoordenador}">

		<c:forEach items="#{relatorioMonitor.relatoriosMonitoresUsuarioAtualCoordenador}" var="relatorio" varStatus="status">
               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		
					<td>
						${relatorio.discenteMonitoria.discente.nome}<br/>
						<i>${relatorio.discenteMonitoria.projetoEnsino.anoTitulo}</i>					
					</td>
					<td>${relatorio.discenteMonitoria.tipoVinculo.descricao}</td>
					<td>${relatorio.status.descricao}</td>
					
					<td width="8%">
						<h:commandLink id="btnVisualizar" title="Visualizar" action="#{relatorioMonitor.view}" style="border: 0;">
						   	<f:param name="id" value="#{relatorio.id}"/>				    	
							<h:graphicImage url="/img/monitoria/form_blue.png"/>
						</h:commandLink>

						<h:commandLink  title="Visualizar Monitor" action="#{ consultarMonitor.view }">
						   	<f:param name="id" value="#{relatorio.discenteMonitoria.id}"/>				    	
							<h:graphicImage url="/img/monitoria/user1_view.png"/>
						</h:commandLink>

						<h:commandLink  title="Validar Relatório de Desligamento" 
							action="#{ relatorioMonitor.iniciarCoordenacaoValidarRelatorioDesligamento }"
							rendered="#{relatorio.permitidoValidacaoCoordenacao}">								
							   	<f:param name="id" value="#{relatorio.id}"/>				    	
								<h:graphicImage url="/img/seta.gif"/>
						</h:commandLink>									
					</td>		
					
				</tr>			
		</c:forEach>		
	</c:if>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>