<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<%@page import="br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria"%>
	
	<h:messages/>
	<h2><ufrn:subSistema /> > Validação de Relatórios de Desligamento de Monitor</h2>

	<h:outputText value="#{relatorioMonitor.create}"/>
	<h:outputText value="#{consultarMonitor.create}"/>	

	<h:form id="form">
			<table class="formulario" width="60%">
				<caption>Busca por Relatórios de Desligamento Não Validados</caption>
			<tbody>
				<tr>
					<td width="40%">
						<h:selectOneRadio value="#{relatorioMonitor.checkBuscaAno}" id="selectBuscaAno" layout="pageDirection">
							<f:selectItem itemValue="true" itemLabel="Ano do Projeto: " id="sim"/>
							<f:selectItem itemValue="false" itemLabel="Todos os Relatórios Pendentes" id="nao" />
						</h:selectOneRadio>
					</td>
					<td>
						<h:inputText value="#{relatorioMonitor.ano}"  size="10" id="anoProjeto"/>
						<br/>&nbsp
					</td>
				</tr>		
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="3">
					<h:commandButton id="btnBuscar" value="Buscar" action="#{relatorioMonitor.listarRelatoriosDesligamento}" />
					<h:commandButton value="Cancelar" action="#{ relatorioMonitor.cancelar }" onclick="#{confirm}"/>
			    	</td>
			    </tr>
			</tfoot>			
			
			</table>
	</h:form>
	<br/>

	<div class="infoAltRem">
	    <h:graphicImage value="/img/monitoria/form_blue.png" 	style="overflow: visible;"/>: Visualizar Relatório
	    <h:graphicImage value="/img/monitoria/user1_view.png" style="overflow: visible;"/>: Visualizar Dados do Monitor
		<h:graphicImage url="/img/seta.gif" style="overflow: visible;"/>: Validar Relatório
	</div>
	<br/>

	<h:form>
	<table class="listagem" width="100%">
	<caption>Relatórios de Desligamento Encontrados (${fn:length(relatorioMonitor.relatorios)})</caption>
	<thead>
		<tr>
			<th>Ano do Projeto</th>
			<th>Matrícula</th>
			<th>Discente</th>
			<th>Vínculo</th>
			<th>Enviado Em</th>
			<th></th>
		</tr>
	</thead>

	<c:set var="ASSUMIU_MONITORIA" value="<%= String.valueOf(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA) %>" scope="application"/>

		<c:forEach items="#{relatorioMonitor.relatorios}" var="relatorio" varStatus="status">
               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		
					<td>${relatorio.discenteMonitoria.projetoEnsino.projeto.ano}</td>
					<td>${relatorio.discenteMonitoria.discente.matricula}</td>
					<td>${relatorio.discenteMonitoria.discente.nome}</td>
					<td width="16%">${relatorio.discenteMonitoria.tipoVinculo.descricao}</td>
					<td><fmt:formatDate value="${relatorio.dataEnvio}" pattern="dd/MM/yyyy HH:mm"/> </td>
					
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
							action="#{ relatorioMonitor.iniciarProgradValidarRelatorioDesligamento }"
							rendered="#{acesso.monitoria && relatorio.permitidoValidacaoPrograd}">								
							   	<f:param name="id" value="#{relatorio.id}"/>				    	
								<h:graphicImage url="/img/seta.gif"/>
						</h:commandLink>									
															
					</td>		
					
				</tr>			
		</c:forEach>		
	
		<c:if test="${empty relatorioMonitor.relatorios}">
	           <tr> <td colspan="6" align="center"> <font color="red">Não há Relatórios de Desligamento de Monitores para validar.</font> </td></tr>
		</c:if>
	
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>