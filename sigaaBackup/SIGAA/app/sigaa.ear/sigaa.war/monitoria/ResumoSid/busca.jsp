<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h2><ufrn:subSistema /> > Localizar Resumos do Seminário de Iniciação à Docência -  SID</h2>

	<h:outputText value="#{resumoSid.create}"/>
	<h:outputText value="#{projetoMonitoria.create}"/>	

	
 	<h:messages showDetail="true" showSummary="true" />
 	
 	<h:form id="formBusca">

	<table class="formulario" width="90%">
	<caption>Busca por Resumos de Projeto</caption>
	<tbody>
	
	
	
		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{resumoSid.checkBuscaAno}" id="selectBuscaAno"  styleClass="noborder"/>

			</td>
	    	<td> <label for="anoProjeto"> Ano do Projeto </label> </td>
	    	<td> <h:inputText value="#{resumoSid.buscaAnoProjeto}" size="10" maxlength="4" onkeyup="return formatarInteiro(this)" onchange="javascript:$('formBusca:selectBuscaAno').checked = true;"/></td>
	    </tr>
	
	
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{resumoSid.checkBuscaProjeto}" id="selectBuscaProjeto" styleClass="noborder"/>
			</td>
		
			<td><label> Projeto: </label></td>
			<td>				 
				<h:inputText id="tituloProjeto" value="#{ resumoSid.tituloProjeto }" size="60"  onchange="javascript:$('formBusca:selectBuscaProjeto').checked = true;"/>									
			</td>
		</tr>		


	    <tr>
			<td>
			<h:selectBooleanCheckbox value="#{resumoSid.checkBuscaServidor}" id="selectBuscaServidor" styleClass="noborder"/>
			</td>
	    	<td> <label for="nome"> Coordenador(a): </label> </td>
	    	<td> 
	    	
				<h:inputText value="#{resumoSid.buscaServidor.pessoa.nome}" id="nome" size="60" onchange="javascript:$('formBusca:selectBuscaServidor').checked = true;"/>
				<h:inputHidden value="#{resumoSid.buscaServidor.id}" id="idServidor"/>
	
				<ajax:autocomplete source="formBusca:nome" target="formBusca:idServidor"
					baseUrl="/sigaa/ajaxServidor" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=todos,inativos=true"
					parser="new ResponseXmlToHtmlListParser()" />
	
				<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>	    	
	    	
	    	</td>
	    </tr>




		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{resumoSid.checkBuscaAnoSid}" id="selectBuscaAnoSid"  styleClass="noborder"/>

			</td>
	    	<td> <label for="anoSid"> Ano do SID </label> </td>
	    	<td> <h:inputText value="#{resumoSid.buscaAnoSid}" size="10" maxlength="4" onkeyup="return formatarInteiro(this)" onchange="javascript:$('formBusca:selectBuscaAnoSid').checked = true;"/></td>
	    </tr>

	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Buscar" action="#{ resumoSid.buscar }"/>
			<h:commandButton value="Cancelar" action="#{ resumoSid.cancelar }" onclick="#{confirm}"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>

	</h:form>

	<br/>
	<br/>


	<c:set var="resumos" value="#{resumoSid.resumos}"/>
	<c:set var="urlAfterSearch" value="#{resumoSid.urlAfterSearch}" />


	<c:if test="${empty resumos}">
		<center><i> Nenhum resumo encontrado para as opções de busca acima! </i></center>
	</c:if>
	
	<c:if test="${not empty resumos && urlAfterSearch == null}">

		<div class="infoAltRem">
		    <h:graphicImage value="/img/monitoria/form_blue.png" style="overflow: visible;" rendered="#{acesso.monitoria || acesso.comissaoMonitoria || acesso.comissaoCientificaMonitoria}"/>
		    <h:outputText rendered="#{acesso.monitoria || acesso.comissaoMonitoria || acesso.comissaoCientificaMonitoria}" value=": Visualizar Resumo"/>	    	
		    <h:graphicImage value="/img/seta.gif" style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText rendered="#{acesso.monitoria}" value=": Registrar Freqüência de Discentes no SID"/>
		    <h:graphicImage value="/img/listar.gif" style="overflow: visible;" rendered="#{acesso.monitoria}"/><h:outputText rendered="#{acesso.monitoria}" value=": Listar Participantes"/>
		    <h:graphicImage value="/img/view.gif"style="overflow: visible;" />: Visualizar Projeto
	    </div>
		<br/>
	
	
		
		
		<h:form>
		 <table class="listagem">
		    <caption>Resumos SID de Projetos de Ensino Encontrados (${fn:length(resumos)})</caption>
	
		      <thead>
		      	<tr>
		        	<th width="60%">Projeto</th>
		        	<th>Ano SID</th>
		        	<th>Situação Resumo</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        	
		        	
		        		<th>&nbsp;</th>
		        	
		        	
		        	<th>&nbsp;</th>  	
		        	
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
	
	       	<c:forEach items="#{resumos}" var="resumo" varStatus="status">	       	
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	
	                    <td> ${resumo.projetoEnsino.anoTitulo} </td>
	                    <td> ${resumo.anoSid}</td>
						<td> ${resumo.status.descricao} </td>
						
						<td>
								<h:commandLink  title="Visualizar Resumo Sid" action="#{ resumoSid.view }"  id="btVerResumo" styleClass="noborder" 
									rendered="#{acesso.monitoria || acesso.comissaoMonitoria || acesso.comissaoCientificaMonitoria}">
								   	<f:param name="id" value="#{resumo.id}"/>				    	
									<h:graphicImage url="/img/monitoria/form_blue.png"/>
								</h:commandLink>
						</td>
						
						<td>		
								<h:commandLink title="Registrar Freqüência dos discentes" action="#{ resumoSid.listarParticipacoesSid }" id="btIniciarRegistrarFrequencia" styleClass="noborder" rendered="#{acesso.monitoria}">
									   	<f:param name="idProjeto" value="#{resumo.projetoEnsino.id}"/>				    	
									   	<f:param name="registrarFrequencia" value="true"/>									   	
										<h:graphicImage url="/img/seta.gif" />
								</h:commandLink>
						</td>
						
						<c:if test="${resumo.autorizarEmissaoCertificado}">
							<td>
																																		
									<h:commandLink title="Listar Participantes" action="#{ resumoSid.listarParticipacoesSid }" id="btListarParticipacoes" styleClass="noborder" rendered="#{acesso.monitoria}">
								   		<f:param name="idProjeto" value="#{resumo.projetoEnsino.id}"/>			
								   		<f:param name="registrarFrequencia" value="false"/>	    	
										<h:graphicImage url="/img/listar.gif" />
									</h:commandLink>
																		
							</td>
						</c:if>
						
						
						<td>								
							<h:commandLink  title="Visualizar Projeto de Ensino" action="#{ projetoMonitoria.view }" id="btView" styleClass="noborder">
								   	<f:param name="id" value="#{resumo.projetoEnsino.id}"/>				    	
									<h:graphicImage url="/img/view.gif"/>
							</h:commandLink>
						</td>
						
						<c:if test="${not resumo.autorizarEmissaoCertificado}">
							<td></td>
						</c:if>
												
	              </tr>
	          </c:forEach>
		 	</tbody>
		 </table>
	</h:form>
<br/>

	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>