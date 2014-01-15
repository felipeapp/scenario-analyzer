<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.colDenominacao{text-align: left;}
	.colPeriodo{width: 20%;text-align: center !important;}
	.colCargaHoraria{text-align: right; width: 10%}
	.colIcone{text-align: right !important; width: 16px;}
</style>

<f:view>
	<c:set var="confirmGrupo" 
		value="if (!confirm('Ao remover este grupo, todas as Atividades cadastradas e inscrições realizadas serão removidas. Deseja confirmar esta operação?')) return false" scope="application"/>

	<h2 class="title"><ufrn:subSistema /> > Grupo de Atividades</h2>
	
	<div class="descricaoOperacao">
		<p>Caro usuário,</p>
		<p>a listagem abaixo exibe todos os grupos de atividades de atualização pedagógica cadastrados pelo Gestor do PAP.</p>
	</div>
	
	<h:form id="formListagemParticipacaoAP">


		<c:if test="${not empty grupoAtividadesAP.all}">
		<center>
				<h:messages/>
				<div class="infoAltRem">
				    
					<h:commandLink action="#{grupoAtividadesAP.preCadastrar}" id="btnCadastrarNovoGrupoAtividade">
						<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/> Cadastrar
					</h:commandLink>	  				   
				    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
				    <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar
				    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
				</div>
		</center>
		<table class="listagem"  width="80%" >
			<caption>Listagem(${fn:length(grupoAtividadesAP.all)})</caption>
		</table>
		</c:if>
		<t:dataTable value="#{grupoAtividadesAP.all}" rendered="#{not empty grupoAtividadesAP.all}"
			 var="_reg" styleClass="listagem"  width="80%" headerClass="colPeriodo,colCargaHoraria,colData,colIcone" 
			 columnClasses="colPeriodo,colCargaHoraria,colData,colIcone,colIcone" rowClasses="linhaPar, linhaImpar">
					<t:column styleClass="colDenominacao" headerstyleClass="colDenominacao">
						<f:facet name="header">
							<f:verbatim>Denominação</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.denominacao}"/>
					</t:column>

					<t:column styleClass="colPeriodo" headerstyleClass="colPeriodo">
						<f:facet name="header">
							<f:verbatim>Período das Inscrições</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.inicioInscricao}"/> a
						<h:outputText value="#{_reg.fimInscricao}"/>
					</t:column>

					<t:column styleClass="colPeriodo" headerstyleClass="colPeriodo">
						<f:facet name="header">
							<f:verbatim>Período de Atividades</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.inicio}"/> a
						<h:outputText value="#{_reg.fim}"/>
					</t:column>
					
					<t:column styleClass="colIcone" headerstyleClass="colIcone">
						<f:facet name="header">
							<f:verbatim>&nbsp;</f:verbatim>
						</f:facet>
			
						<h:commandLink styleClass="noborder" title="Visualizar" id="visualizarRegistro"
							action="#{grupoAtividadesAP.view}">
							<h:graphicImage url="/img/view.gif" />
							<f:param name="id" value="#{_reg.id}" />
						</h:commandLink>
					</t:column>
					
					<t:column styleClass="colIcone" headerstyleClass="colIcone">
						<f:facet name="header">
							<f:verbatim>&nbsp;</f:verbatim>
						</f:facet>
						<h:commandLink styleClass="noborder" title="Alterar" id="atualizarRegistro"
							action="#{grupoAtividadesAP.atualizar}">
							<h:graphicImage url="/img/alterar.gif" />
							<f:param name="id" value="#{_reg.id}" />
						</h:commandLink>
					</t:column>
					
					<t:column styleClass="colIcone" headerstyleClass="colIcone">
						<f:facet name="header">
							<f:verbatim>&nbsp;</f:verbatim>
						</f:facet>
						<h:commandLink styleClass="noborder" title="Remover" id="removerRegistro"
							onclick="#{confirmGrupo}" 
								action="#{grupoAtividadesAP.remover}">
							<h:graphicImage url="/img/delete.gif" />
							<f:param name="id" value="#{_reg.id}" />
						</h:commandLink>
					</t:column>		
		</t:dataTable>
		<center>
			<h:outputText  rendered="#{empty grupoAtividadesAP.all}" value="Não existem registros cadastrados até o momento."></h:outputText>
		</center>
	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
