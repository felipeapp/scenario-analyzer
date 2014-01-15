<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>


<f:view>
	<h:messages showDetail="true"  showSummary="true"/>
	<h2><ufrn:subSistema /> > Visualizar Relatórios de Projetos de Ensino</h2>

	<h:outputText value="#{relatorioProjetoMonitoria.create}"/>
	<h:outputText value="#{projetoMonitoria.create}"/>	
	
 	<h:form id="formBusca">

	<table class="formulario" width="90%">
	<caption>Busca por Relatórios de Projeto</caption>
	<tbody>
	
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{relatorioProjetoMonitoria.checkBuscaProjeto}" id="selectBuscaProjeto"/>
			</td>
		
			<td><label> Projeto: </label></td>
			<td>
				 <%--	
				 <h:inputHidden id="idProjeto" value="#{ relatorioProjetoMonitoria.buscaProjetoEnsino.id}"/>
				 --%>
				 
				 <h:inputText id="tituloProjeto" value="#{ relatorioProjetoMonitoria.tituloProjeto }" size="60"  onchange="javascript:$('formBusca:selectBuscaProjeto').checked = true;"/>
		
				<%--
				<ajax:autocomplete source="formBusca:tituloProjeto" target="formBusca:idProjeto"
					baseUrl="/sigaa/ajaxProjetoMonitoria" className="autocomplete"
					indicator="indicatorProjeto" minimumCharacters="3" parameters="nivel=ufrn"
					parser="new ResponseXmlToHtmlListParser()" />
		
				<span id="indicatorProjeto" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
				--%>					
			</td>
		</tr>		


	    <tr>
			<td>
			<h:selectBooleanCheckbox value="#{relatorioProjetoMonitoria.checkBuscaServidor}" id="selectBuscaServidor"/>
			</td>
	    	<td> <label for="nome"> Coordenador(a): </label> </td>
	    	<td> 
	    	
				<h:inputText value="#{relatorioProjetoMonitoria.buscaServidor.pessoa.nome}" id="nome" size="60" onchange="javascript:$('formBusca:selectBuscaServidor').checked = true;"/>
				<h:inputHidden value="#{relatorioProjetoMonitoria.buscaServidor.id}" id="idServidor"/>
	
				<ajax:autocomplete source="formBusca:nome" target="formBusca:idServidor"
					baseUrl="/sigaa/ajaxServidor" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=todos,inativos=true"
					parser="new ResponseXmlToHtmlListParser()" />
	
				<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>	    	
	    	
	    	</td>
	    </tr>

		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{relatorioProjetoMonitoria.checkBuscaAno}" id="selectBuscaAno" />

			</td>
	    	<td> <label for="anoProjeto"> Ano do Projeto </label> </td>
	    	<td> <h:inputText value="#{relatorioProjetoMonitoria.buscaAnoProjeto}" size="10" onchange="javascript:$('formBusca:selectBuscaAno').checked = true;"/></td>
	    </tr>


		<tr>
			<td>
			
			<h:selectBooleanCheckbox value="#{relatorioProjetoMonitoria.checkBuscaTipoRelatorio}"  id="selectBuscaTipoRelatorio" />
			</td>
	    	<td> <label for="tipoRelatorio">Tipo de Relatório </label> </td>
	    	<td>

	    	 <h:selectOneMenu value="#{relatorioProjetoMonitoria.buscaTipoRelatorio.id}" onchange="javascript:$('formBusca:selectBuscaTipoRelatorio').checked = true;">
	    	 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
	    	 	<f:selectItem itemValue="-1" itemLabel="RELATÓRIO PARCIAL PENDENTE" />
	    	 	<f:selectItem itemValue="-2" itemLabel="RELATÓRIO FINAL PENDENTE" />
				<f:selectItems value="#{relatorioProjetoMonitoria.allTiposRelatoriosProjetoCombo}"/>
 			 </h:selectOneMenu>

	    	 </td>
	    </tr>

	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Buscar" action="#{ relatorioProjetoMonitoria.localizarProjetoComRelatorio }"/>
			<h:commandButton value="Cancelar" action="#{ relatorioProjetoMonitoria.cancelar }"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>

	</h:form>

	<br/>
	<br/>

<br/>
	<div class="infoAltRem">
	    <h:graphicImage value="/img/monitoria/form_blue.png" style="overflow: visible;"/>: Visualizar Relatório
	    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Projeto<br/>	   
	    <h:graphicImage value="/img/arrow_undo.png" style="overflow: visible;"/>: Devolver Relatório para Coordenador(a)
	</div>
<br/>

	<c:set var="relatorios" value="#{relatorioProjetoMonitoria.relatoriosLocalizados}"/>

	<c:if test="${empty relatorios}">
	<center><i> Nenhum Relatório localizado </i></center>
	</c:if>

	<c:if test="${not empty relatorios}">

		<h:form>
			 <table class="listagem">
			    <caption>Relatórios de Projetos de Ensino Encontrados (${fn:length(relatorios)})</caption>
		
			      <thead>
			      	<tr>
			        	<th width="40%">Ano - Projeto</th>
			        	<th>Coordenador</th>
			        	<th>Data Envio</th>
			        	<th>Tipo Relatório</th>	        	
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>	        	
			        	<th>&nbsp;</th>
			        </tr>
			 	</thead>
			 	<tbody>
		       	<c:forEach items="#{relatorios}" var="relatorio" varStatus="status">
		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		
		                    <td> ${relatorio.projetoEnsino.anoTitulo}</td>
		                    <td> ${relatorio.projetoEnsino.projeto.coordenador.pessoa.nome}</td>
							<td> <fmt:formatDate value="${relatorio.dataEnvio}" pattern="dd/MM/yyyy"/> </td>
		                    <td> ${relatorio.tipoRelatorio.descricao} </td>
							
							<td width="2%">	
								<h:commandLink  title="Devolver Relatório para o Coordenador(a)" 
									action="#{ relatorioProjetoMonitoria.devolverRelatorioCoordenador }"
									rendered="#{relatorio.permitidoDevolverCoordenador && acesso.monitoria}"
									onclick="return confirm('Tem certeza que deseja Devolver este relatório para Coordenador(a)?');">
									   	<f:param name="id" value="#{relatorio.id}"/>				    	
										<h:graphicImage url="/img/arrow_undo.png"/>
								</h:commandLink>									
							</td>		
							<td width="2%">
								<h:commandLink title="Visualizar" action="#{ relatorioProjetoMonitoria.view }" 
									rendered="#{ not relatorio.permitidoAlterar }">
								   	<f:param name="id" value="#{relatorio.id}"/>				    	
									<h:graphicImage url="/img/monitoria/form_blue.png"/>
								</h:commandLink>
							</td>			
							<td width="2%">
							    <h:commandLink title="Visualizar Projeto de Monitoria" action="#{ projetoMonitoria.view }" id="btView">
								   	<f:param name="id" value="#{relatorio.projetoEnsino.id}"/>				    	
									<h:graphicImage url="/img/view.gif"/>
								</h:commandLink>
							</td>			
		              </tr>
		          </c:forEach>
			 	</tbody>
			 </table>
		 </h:form>

	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>