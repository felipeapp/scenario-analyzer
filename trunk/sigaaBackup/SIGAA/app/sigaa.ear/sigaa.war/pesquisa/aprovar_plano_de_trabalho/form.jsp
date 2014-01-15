<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> > Aprovar Planos de Trabalhos Corrigidos</h2>
	<h:outputText value="#{aprovarPlanoTrabalho.create}" />
	<h:form id="form">
		<div class="descricaoOperacao">
			<p align="center">
				Selecione os Planos de Trabalhos para definir o status como aprovado.
			</p>
		</div>
		<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" /> :Visualizar 
			</div>
		<table class="listagem">
			<caption class="listagem">Lista de Planos de Trabalhos Corrigidos</caption>
			<tr>
				<td>
					<c:if test="${not empty aprovarPlanoTrabalho.all }">
						<t:dataTable value="#{aprovarPlanoTrabalho.all}" var="item" rowIndexVar="index" width="100%" rowClasses="linhaPar, linhaImpar" styleClass="listagem">
							
							<tr>
								<td>
									<t:column>
										<f:facet name="header">
											<f:verbatim></f:verbatim>
										</f:facet>
										<h:selectBooleanCheckbox value="#{item.selecionado}" />  
									</t:column>
								<td>
								<td>
									<t:column>
										<f:facet name="header">
											<f:verbatim>Título</f:verbatim>
										</f:facet>
								 		<h:outputText value="#{item.titulo}" />
									</t:column>
								</td>
								<td>
									<t:column> 
										<f:facet name="header">
											<f:verbatim>Orientador</f:verbatim>
										</f:facet>
										<h:outputText value="#{item.orientador.pessoa.nome}"/>
									</t:column>
								</td>
								<td> 
									<t:column>
										<f:facet name="header">
											<f:verbatim>Discente&nbsp&nbsp</f:verbatim>
										</f:facet>
										<h:outputText value="#{item.membroProjetoDiscente.discente.pessoa.nome}"/>
									</t:column>
								</td>
								<td> 
									<t:column>
										<f:facet name="header">
											<f:verbatim>Status</f:verbatim>
										</f:facet>
										<h:outputText value="#{item.statusString}" />
									</t:column>
								</td>
								<td> 
									<t:column>
										<f:facet name="header">
											<f:verbatim></f:verbatim>
										</f:facet>
										<h:commandLink title="Visualizar" action="#{aprovarPlanoTrabalho.visualizarPlano}">
											<f:param name="id" value="#{item.id}"/>
											<h:graphicImage url="/img/view.gif"/>
										</h:commandLink>
									</t:column>
								</td>
							</tr>
						</t:dataTable>
					</c:if>
					<c:if test="${empty aprovarPlanoTrabalho.all}">
						<br />
						<p align="center">
							Nenhum Plano de Trabalho com status corrigido foi encontrado.
						</p>
						<br />
					</c:if>
				</td>	
			</tr>
						
			<tfoot align="center">
				<tr>
					<td colspan="5">
						<h:commandButton value="Aprovar" action="#{aprovarPlanoTrabalho.aprovar}" /> 
						<h:commandButton value="Cancelar" action="#{aprovarPlanoTrabalho.cancelar}" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
