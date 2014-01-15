<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<h:messages showDetail="true"></h:messages>
	<a4j:keepAlive beanName="alterarStatusResumos" />
	<h2><ufrn:subSistema /> > Alterar Status de Resumos CIC</h2>
	<h:form id="form">
		<div class="descricaoOperacao">
			<p align="center">
				Selecione um novo status para os resumos selecionados.
			</p>
		</div>
		
		
		<table class="listagem">
			<caption class="listagem">Lista de Resumos CIC</caption>
			<tr>
				<td>
					<c:if test="${not empty alterarStatusResumos.resumosSelecionados }">
						<t:dataTable value="#{alterarStatusResumos.resumosSelecionados}" var="item" rowIndexVar="index" width="100%" rowClasses="linhaPar, linhaImpar" styleClass="listagem">
							
							<tr>
								<td>
									<t:column>
										<f:facet name="header">
											<f:verbatim>Código</f:verbatim>
										</f:facet>
								 		<h:outputText value="#{item.codigo}" />
									</t:column>
								</td>
								<td>
									<t:column> 
										<f:facet name="header">
											<f:verbatim>Orientador</f:verbatim>
										</f:facet>
										<h:outputText value="#{item.orientador.nome}"/>
									</t:column>
								</td>
								<td> 
									<t:column>
										<f:facet name="header">
											<f:verbatim>Status</f:verbatim>
										</f:facet>
										<h:outputText value="#{item.statusString}"/>
									</t:column>
								</td>
								<td> 
									<t:column>
										<f:facet name="header">
											<f:verbatim></f:verbatim>
										</f:facet>
										<h:commandLink title="Visualizar" action="#{alterarStatusResumos.visualizarResumo}">
											<f:param name="id" value="#{item.id}"/>
											<h:graphicImage url="/img/view.gif"/>
										</h:commandLink>
									</t:column>
								</td>
							</tr>
						</t:dataTable>
					</c:if>
				</td>	
			</tr>
						
			
			
		</table>
		
		<table class="listagem">
				<tr>
					<th>
						Novo Status:
					</th>
					<td>
						<h:selectOneMenu id="stats" value="#{alterarStatusResumos.obj.status}" >
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{alterarStatusResumos.allStatusCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tfoot align="center">
				<tr>
					<td colspan="5">
						<h:commandButton value="Alterar Status" action="#{alterarStatusResumos.mudarStatus}" /> 
						<h:commandButton value="Cancelar" action="#{alterarStatusResumos.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>

		
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
