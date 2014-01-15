<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<%@page import="br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria"%>
	
	<h:messages/>
	<h2><ufrn:subSistema /> > Relatórios de Monitoria (Parciais, Finais e de Desligamento)</h2>
	<br/>

	<h:outputText value="#{relatorioMonitor.create}"/>
	<h:outputText value="#{consultarMonitor.create}"/>	

	<h:form id="formBuscaRelatorioMonitor">
			<table class="formulario" width="90%">
				<caption>Busca por Relatórios  dos Monitores</caption>
			<tbody>
		
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{relatorioMonitor.checkBuscaAno}" id="selectBuscaAno"/>
					</td>
				
					<td><label>Ano Projeto: </label></td>
					<td>
						<h:inputText value="#{relatorioMonitor.ano}"  size="10" onfocus="javascript:$('formBuscaRelatorioMonitor:selectBuscaAno').checked = true;" id="anoProjeto"/>			
					</td>
				</tr>		
			
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{relatorioMonitor.checkBuscaDiscente}" id="selectBuscaDiscente"/>
					</td>

					<td><label> Discente: </label></td>
					<td>
						
						 <h:inputHidden id="idDiscente" value="#{ relatorioMonitor.discente.id}"/>
						 <h:inputText id="nomeDiscente" value="#{ relatorioMonitor.discente.pessoa.nome }" size="60"  onfocus="javascript:$('formBuscaRelatorioMonitor:selectBuscaDiscente').checked = true;"/>
				
						<ajax:autocomplete source="formBuscaRelatorioMonitor:nomeDiscente" target="formBuscaRelatorioMonitor:idDiscente"
							baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
							indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=G"
							parser="new ResponseXmlToHtmlListParser()" />
				
						<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
											
					</td>
				</tr>		
				
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{relatorioMonitor.checkBuscaProjeto}" id="selectBuscaProjeto"/>
					</td>
				
					<td><label> Projeto: </label></td>
					<td>
						 <%--						 	
						 <h:inputHidden id="idProjeto" value="#{ relatorioMonitor.projetoEnsino.id}"/>
						 --%>
						 
						 <h:inputText id="tituloProjeto" value="#{ relatorioMonitor.tituloProjeto }" size="60"  onfocus="javascript:$('formBuscaRelatorioMonitor:selectBuscaProjeto').checked = true;"/>
				
						<%--			
						<ajax:autocomplete source="formBuscaRelatorioMonitor:tituloProjeto" target="formBuscaRelatorioMonitor:idProjeto"
							baseUrl="/sigaa/ajaxProjetoMonitoria" className="autocomplete"
							indicator="indicatorProjeto" minimumCharacters="3" parameters="nivel=ufrn"
							parser="new ResponseXmlToHtmlListParser()" />
				
						<span id="indicatorProjeto" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
						--%>
											
					</td>
				</tr>		
				
				
				<tr>
					<td>
					
					<h:selectBooleanCheckbox value="#{relatorioMonitor.checkBuscaTipoRelatorio}"  id="selectBuscaTipoRelatorio" />
					</td>
			    	<td> <label for="tipoRelatorio">Tipo de Relatório </label> </td>
			    	<td>
		
			    	 <h:selectOneMenu value="#{relatorioMonitor.buscaTipoRelatorio.id}" onfocus="javascript:$('formBuscaRelatorioMonitor:selectBuscaTipoRelatorio').checked = true;">
						<f:selectItems value="#{relatorioProjetoMonitoria.allTiposRelatoriosProjetoCombo}"/>
		 			 </h:selectOneMenu>
		
			    	 </td>
			    </tr>
				
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="3">
					<h:commandButton id="btnBuscar" value="Buscar" action="#{relatorioMonitor.buscar}" />
					<h:commandButton value="Cancelar" action="#{ relatorioMonitor.cancelar }"/>
			    	</td>
			    </tr>
			</tfoot>			
			
			</table>
	</h:form>

	<br/>
	<div class="infoAltRem">
	    <h:graphicImage url="/img/arrow_undo.png" style="overflow: visible;"/>: Devolver Relatório para o Monitor
	    <h:graphicImage value="/img/monitoria/form_blue.png" 	style="overflow: visible;"/>: Visualizar Relatório<br/>
	    <h:graphicImage value="/img/monitoria/user1_view.png" style="overflow: visible;"/>: Visualizar Dados do Monitor
	</div>
	<br/>

	<h:form>
	<table class="listagem" width="100%">
	<caption>Lista de Relatórios (${fn:length(relatorioMonitor.relatorios)})</caption>
	<thead>
		<tr>
			<th>Monitor</th>
			<th>Relatório</th>			
			<th>Vínculo</th>
			<th>Monitoria</th>			
			<th></th>
		</tr>
	</thead>

	<c:set var="ASSUMIU_MONITORIA" value="<%= String.valueOf(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA) %>" scope="application"/>
	
	<c:if test="${empty relatorioMonitor.relatorios}">
            <tr> <td colspan="6" align="center"> <font color="red">Não há Relatórios enviados com os critérios informados!</font> </td></tr>
	</c:if>

	<c:if test="${not empty  relatorioMonitor.relatorios}">

		<c:forEach items="#{relatorioMonitor.relatorios}" var="relatorio" varStatus="status">
               <tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
		
					<td>${relatorio.discenteMonitoria.discente.matriculaNome}</td>
					<td>${relatorio.tipoRelatorio.descricao}</td>
					<td>${relatorio.discenteMonitoria.tipoVinculo.descricao}</td>
					<td>
						<c:set value="${((relatorio.discenteMonitoria.situacaoDiscenteMonitoria.id == ASSUMIU_MONITORIA) and (relatorio.discenteMonitoria.ativo)) ? 'blue':'red'}" var="cor"/>
						<font color="${cor}">${((relatorio.discenteMonitoria.situacaoDiscenteMonitoria.id == ASSUMIU_MONITORIA) and (relatorio.discenteMonitoria.ativo)) ? 'ATIVO':'INATIVO'}</font>
					</td>					

					<td width="10%">

							<h:commandLink  title="Devolver Relatório para o Monitor(a)" 
								action="#{ relatorioMonitor.devolverRelatorioMonitor }"
								rendered="#{relatorio.permitidoDevolverMonitor && acesso.monitoria}"
								onclick="return confirm('Tem certeza que deseja devolver este relatório para monitor(a)?');">								
								   	<f:param name="id" value="#{relatorio.id}"/>				    	
									<h:graphicImage url="/img/arrow_undo.png"/>
							</h:commandLink>									

							<h:commandLink id="btnVisualizar" title="Visualizar" action="#{relatorioMonitor.view}" style="border: 0;">
							   	<f:param name="id" value="#{relatorio.id}"/>				    	
								<h:graphicImage url="/img/monitoria/form_blue.png"/>
							</h:commandLink>
					
							<h:commandLink  title="Visualizar Monitor" action="#{ consultarMonitor.view }">
							   	<f:param name="id" value="#{relatorio.discenteMonitoria.id}"/>				    	
								<h:graphicImage url="/img/monitoria/user1_view.png"/>
							</h:commandLink>

					</td>				
					
				</tr>			
		</c:forEach>		
	</c:if>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>