<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
	<h:form id="form">
	
			
			<h2><ufrn:subSistema /> > Meus projetos</h2>
		
		
		<!-- PROJETOS GRAVADOS PELO USUARIO LOGADO-->
			<div class="infoAltRem">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Continuar Cadastro
				<h:graphicImage url="/img/table_go.png" style="overflow: visible;"/>: Verificar Pendências
				<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
			    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
			</div>
			<br/>
		
			<h:dataTable id="dtProjetosGravados" value="#{projetoBase.projetosGravados}" var="projGravado" 
					width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
				<f:facet name="caption">
					<h:outputText value="Lista de Projetos Pendentes de Envio" />
				</f:facet>

				<t:column>
					<f:facet name="header">
						<f:verbatim>Ano</f:verbatim>
					</f:facet>
					<h:outputText value="#{projGravado.ano}" />
				</t:column>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>Título</f:verbatim>
					</f:facet>
					<h:outputText value="#{projGravado.titulo}" />
				</t:column>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>Situação</f:verbatim>
					</f:facet>
					<h:outputText value="#{projGravado.situacaoProjeto.descricao}" />
				</t:column>
		
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink title="Continuar Cadastro" action="#{ projetoBase.preAtualizar }" immediate="true">
					        <f:param name="id" value="#{projGravado.id}"/>
				    		<h:graphicImage url="/img/seta.gif" />
					</h:commandLink>
				</t:column>
				
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink title="Verificar Pendências" action="#{ projetoBase.verificarPendencias }" immediate="true" rendered="#{projGravado.cadastroEmAndamento}">
					        <f:param name="id" value="#{projGravado.id}"/>
				    		<h:graphicImage url="/img/table_go.png" />
					</h:commandLink>
				</t:column>
				
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink title="Visualizar" action="#{ projetoBase.view }" immediate="true">
						        <f:param name="id" value="#{projGravado.id}"/>
					    		<h:graphicImage url="/img/view.gif" />
					</h:commandLink>
				</t:column>

				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink title="Remover Cadastro" action="#{ projetoBase.preRemover }" immediate="true" rendered="#{projGravado.cadastroEmAndamento}">
					        <f:param name="id" value="#{projGravado.id}"/>
				    		<h:graphicImage url="/img/delete.gif" />
					</h:commandLink>
				</t:column>
				
			</h:dataTable>
			
		<c:if test="${(empty projetoBase.projetosGravados)}">
			<center><font color='red'>Não há ações acadêmicas associadas.</font></center>
		</c:if>
			
			
		<br/>
		<!-- FIM DAS PROJETOS GRAVADOS PELO USUARIO LOGADO-->
		
				<div class="descricaoOperacao">
					A execução dos projetos só poderá ser solicitada nos projetos com a situação APROVADO COM RECURSO ou APROVADO SEM RECURSO
				</div>
		<!-- PROJETO QUE PARTICIPO -->
				<div class="infoAltRem">
					<h:graphicImage value="/img/report.png"style="overflow: visible;"/>: Definir Execução do Projeto
					<h:graphicImage url="/img/projeto/orcamento.png" width="16" height="16" />: Orçamento
					<h:graphicImage value="/img/view2.gif"style="overflow: visible;"/>: Avaliações
			    	<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar 
			    	<h:graphicImage value="/img/add_cal.png"style="overflow: visible;"/>: Atualizar Cronograma
			    	<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover 
					<h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar
				</div>
				<br/>
				<h:dataTable id="dtMeusProjetos" value="#{projetoBase.meusProjetos}" var="meuProj"
						width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
		
						<f:facet name="caption">
							<h:outputText value="Lista dos Projetos dos Quais Participo" />
						</f:facet>
		
						<t:column>
							<f:facet name="header">
								<f:verbatim>Ano</f:verbatim>
							</f:facet>
							<h:outputText value="#{meuProj.ano}" />
						</t:column>
		
						<t:column>
							<f:facet name="header">
								<f:verbatim>Título</f:verbatim>
							</f:facet>
							<h:outputText value="#{meuProj.titulo}" />
						</t:column>
		
						<t:column>
							<f:facet name="header">
								<f:verbatim>Situação</f:verbatim>
							</f:facet>
							<h:outputText value="#{meuProj.situacaoProjeto.descricao}" />
						</t:column>
					
						<t:column>
							<h:commandLink title="Definir Execução do Projeto" action="#{ projetoBase.viewExecutar }" immediate="true" rendered="#{meuProj.aprovadoComSemRecurso}" >
								        <f:param name="id" value="#{meuProj.id}"/>
							    		<h:graphicImage url="/img/report.png" />
							</h:commandLink>
						</t:column>

						<t:column width="5%" styleClass="centerAlign">
							<h:commandLink title="Orçamento" action="#{ projetoBase.viewOrcamento }" immediate="true">
								        <f:param name="id" value="#{meuProj.id}"/>
							    		<h:graphicImage url="/img/projeto/orcamento.png" width="16" height="16" />
							</h:commandLink>
						</t:column>
						
						<t:column>
							<h:commandLink title="Avaliações" action="#{ avaliacaoProjetoBean.listarAvaliacoesProjeto }" immediate="true" rendered="#{meuProj.permitidoVisualizarAvaliacao}">
										<f:param name="id" value="#{meuProj.id}"/>
							    		<h:graphicImage url="/img/view2.gif"/>
							</h:commandLink>
						</t:column>
						
						<t:column>
							<h:commandLink title="Alterar Cadastro" action="#{ projetoBase.preAtualizar }" immediate="true" rendered="#{meuProj.cadastroEmAndamento}">
								        <f:param name="id" value="#{meuProj.id}"/>
							    		<h:graphicImage url="/img/alterar.gif" />
							</h:commandLink>
						</t:column>

						<t:column>
							<h:commandLink title="Atualizar Cronograma" action="#{ projetoBase.iniciarAtualizarCronograma }" immediate="true">
								        <f:param name="id" value="#{meuProj.id}"/>
							    		<h:graphicImage url="/img/add_cal.png" />
							</h:commandLink>
						</t:column>


						<t:column>
							<h:commandLink title="Remover Cadastro" action="#{ projetoBase.preRemover }" immediate="true" rendered="#{meuProj.passivelRemocao}">
								        <f:param name="id" value="#{meuProj.id}"/>
							    		<h:graphicImage url="/img/delete.gif" />
							</h:commandLink>
						</t:column>
						
					
						<t:column width="5%" styleClass="centerAlign">
							<h:commandLink title="Visualizar" action="#{ projetoBase.view }" immediate="true">
							        <f:param name="id" value="#{meuProj.id}"/>
						    		<h:graphicImage url="/img/view.gif" />
							</h:commandLink>
						</t:column>
				</h:dataTable>
			<!-- FIM DOS PROJETOS QUE PARTICIPO -->
			
		<c:if test="${(empty projetoBase.meusProjetos)}">
			<center><font color='red'>Não há ações acadêmicas associadas.</font></center>
		</c:if>
			
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>