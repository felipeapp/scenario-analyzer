<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

	<h:panelGroup rendered="#{!acesso.extensao}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</h:panelGroup>
	
	<h2><ufrn:subSistema /> > Consultar Discentes de Extensão</h2>
	
	<%@include file="/extensao/DiscenteExtensao/_busca.jsp" %>
	
	<h:panelGroup rendered="#{not empty discentes}">
		<div class="infoAltRem">
			<h:graphicImage value="/img/email_go.png" style="overflow: visible;" />: Enviar Email
		    <h:graphicImage value="/img/extensao/user1_view.png" style="overflow: visible;"/>: Visualizar Dados do Discente
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Detalhes da Ação
		    <h:graphicImage value="/img/report.png" style="overflow: visible;"/>: Visualizar Plano de Trabalho
		</div>	
	
		<br/>
	</h:panelGroup>
	


	<h:panelGroup rendered="#{not empty discentes}">
		<h:form id="formLista">

			 <table class="listagem">
			    <caption>Discentes Encontrados (${ fn:length(discentes) })</caption>
		
			      <thead>
			      	<tr>
			        	<th>Discente</th>
			        	<th>Curso</th>
			        	<th>Vínculo</th>
			        	<th>Situação</th>		        	
			        	<th>Cadastrado em</th>
			        	<th>Início</th>
			        	<th>Fim</th>
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>		        
			        	<th>&nbsp;</th>
			        </tr>
			 	</thead>
			 	<tbody>
	
			 	<a4j:repeat value="#{discentes}" var="de" rowKeyVar="index">
			 	
			       			<h:panelGroup rendered="#{ (index == 0) || (discentes[index-1].planoTrabalhoExtensao.atividade.id != de.planoTrabalhoExtensao.atividade.id) }">
								<tr>
									<td colspan="9" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
										<h:outputText value="#{de.planoTrabalhoExtensao.atividade.codigoTitulo}" />
									</td>
									<td style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
										<h:commandButton id="visualizar" title="Visualizar Detalhes da Ação" action="#{ atividadeExtensao.view }" immediate="true" image="/img/view.gif">
											<f:setPropertyActionListener target="#{atividadeExtensao.atividadeSelecionada.id}" value="#{de.planoTrabalhoExtensao.atividade.id}"/>
										</h:commandButton>
									</td>
								</tr>						
							</h:panelGroup>
							
						<h:panelGroup rendered="#{index % 2 == 0}">
							<tr class="linhaPar">
						</h:panelGroup>
						<h:panelGroup rendered="#{index % 2 != 0}">
							<tr class="linhaImpar">
						</h:panelGroup>
		
		                    <td> <h:outputText value="#{de.discente.matriculaNome}" /></td>
		                    <td> <h:panelGroup rendered="#{not empty discenteExtensao.obj.discente.curso}"><h:outputText value="#{de.discente.curso.nomeCompleto}" /></h:panelGroup> </td>
							<td> <h:outputText value="#{de.tipoVinculo.descricao}" /> </td>
							<td> <h:outputText value="#{de.situacaoDiscenteExtensao.descricao}" /> </td>
							<td> <h:outputText value="#{de.dataCadastro}" /> </td>
							<td> <h:outputText value="#{de.dataInicio}" /> </td>
							<td> <h:outputText value="#{de.dataFim}" /> </td>
							
							<td width="3%">
								<h:commandLink action="#{ planoTrabalhoExtensao.preEnviarEmailIndicacao }" >
									<h:graphicImage value="/img/email_go.png" style="overflow: visible;" title="Enviar Email"/>
									<f:param name="id" value="#{ de.id }" />
								</h:commandLink>
							</td>
							
							<td width="2%">
								<h:commandLink title="Visualizar Discente" action="#{ discenteExtensao.view }" id="ver_discente">
								      <f:param name="idDiscenteExtensao" value="#{de.id}"/>
								      <h:graphicImage url="/img/extensao/user1_view.png" />
								</h:commandLink>
							</td>
							<td  width="2%">
									<h:commandLink action="#{planoTrabalhoExtensao.view}" id="ver_plano"
									title="Visualizar Plano de Trabalho" style="border: 0;" rendered="#{not empty de.planoTrabalhoExtensao}">
								       <f:param name="id" value="#{de.planoTrabalhoExtensao.id}"/>
						               <h:graphicImage url="/img/report.png" />
									</h:commandLink>
							</td>
		              </tr>		              
		          </a4j:repeat>
			 	</tbody>
			 </table>
		</h:form>
	
	</h:panelGroup>
	

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>