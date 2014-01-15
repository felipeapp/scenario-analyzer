<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.colTitulo{text-align: left;}
	.colPeriodo{text-align: left; width: 20%}
	.colCargaHoraria{text-align: right; width: 10%}
	.colData{text-align: center; width: 12%}
	.colIcone{text-align: right; width: 1%}
</style>

<f:view>
	<a4j:keepAlive beanName="registroParticipacaoAP"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema /> > Participação em Atividades Pedagógicas</h2>

	<div class="descricaoOperacao">
		<p>Caro usuário,</p>
		<p>a listagem abaixo exibe todos os registros de participações anteriores em atividades de atualização pedagógica.</p>
	</div>

	<h:form id="formListagemParticipacaoAP">


		<c:if test="${not empty registroParticipacaoAP.all}">
		<center>
				<h:messages/>
				<div class="infoAltRem">
				    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
				    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar
				    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
				</div>
		</center>
		<table class="listagem"  width="80%" >
			<caption>Registro da Participação em Atividades Pedagógicas (${fn:length(registroParticipacaoAP.all)})</caption>
		</table>
		</c:if>
		<t:dataTable value="#{registroParticipacaoAP.all}" rendered="#{not empty registroParticipacaoAP.all}"
			 var="_reg" styleClass="listagem"  width="80%"
			columnClasses="colTitulo,colPeriodo,colCargaHoraria,colData,colIcone"
				  rowClasses="linhaPar, linhaImpar">
					
					<t:column styleClass="colTitulo" headerstyleClass="colTitulo">
						<f:facet name="header">
							<f:verbatim>Título</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.titulo}"/>
					</t:column>
					
					<t:column styleClass="colPeriodo" headerstyleClass="colPeriodo">
						<f:facet name="header">
							<f:verbatim>Período</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.dataInicio}"/> a
						<h:outputText value="#{_reg.dataFim}"/>
					</t:column>
					
					<t:column styleClass="colCargaHoraria" headerstyleClass="colCargaHoraria">
						<f:facet name="header">
							<f:verbatim>Carga Horária</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.cargaHoraria}h" rendered="#{not empty _reg.cargaHoraria}"/> 
						<h:outputText value="Não Informado" rendered="#{empty _reg.cargaHoraria}"/>
					</t:column>
	
					<t:column styleClass="colData" headerstyleClass="colData">
						<f:facet name="header">
							<f:verbatim>Data de Cadastro</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.dataCadastro}"/>
					</t:column>
					
					<t:column styleClass="colIcone" headerstyleClass="colIcone">
						<f:facet name="header">
							<f:verbatim>&nbsp;</f:verbatim>
						</f:facet>
			
						<h:commandLink styleClass="noborder" title="Visualizar" id="visualizarRegistro"
							action="#{registroParticipacaoAP.view}">
							<h:graphicImage url="/img/view.gif" />
							<f:param name="id" value="#{_reg.id}" />
						</h:commandLink>
					</t:column>	
					
					<t:column styleClass="colIcone">
						<f:facet name="header">
							<f:verbatim>&nbsp;</f:verbatim>
						</f:facet>
			
						<h:commandLink styleClass="noborder" title="Alterar" id="atualizarRegistro"
							action="#{registroParticipacaoAP.atualizar}">
							<h:graphicImage url="/img/alterar.gif" />
							<f:param name="id" value="#{_reg.id}" />
						</h:commandLink>
					
					</t:column>		
					<t:column styleClass="colIcone">
						<f:facet name="header">
							<f:verbatim>&nbsp;</f:verbatim>
						</f:facet>
			
						<h:commandLink styleClass="noborder" title="Remover" id="removerRegistro"
							action="#{registroParticipacaoAP.preRemover}">
							<h:graphicImage url="/img/delete.gif" />
							<f:param name="id" value="#{_reg.id}" />
						</h:commandLink>
					</t:column>		
		</t:dataTable>
		<center>
			<h:outputText  rendered="#{empty registroParticipacaoAP.all}" value="Não existem registros cadastrados até o momento."></h:outputText>
			<br/>
			<br/>
			<h:commandLink value="<< Voltar" action="#{registroParticipacaoAP.cancelar}" 
									  immediate="true" id="btnvoltar">
			</h:commandLink>
		</center>
	</h:form>
	

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
