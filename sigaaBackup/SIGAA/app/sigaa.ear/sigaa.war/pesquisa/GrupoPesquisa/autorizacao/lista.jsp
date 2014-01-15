<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:region rendered="#{ autorizacaoGrupoPesquisaMBean.portalDocente }">
<%@include file="/portais/docente/menu_docente.jsp"%>
</a4j:region>

<h2><ufrn:subSistema /> &gt; Lista das Propostas de Grupos de Pesquisa </h2>

	<h:form id="form">
	
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Emitir Parecer
			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
		</div>
		<br/>
	
		<h:dataTable value="#{ autorizacaoGrupoPesquisaMBean.gruposPesquisa }" var="item" styleClass="listagem" 
				width="100%" rowClasses="linhaPar, linhaImpar" footerClass="rodape">

		<f:facet name="caption"><f:verbatim>Propostas de Grupo de Pesquisa</f:verbatim></f:facet>
			
			<h:column headerClass="colData">
				<f:facet name="header"><f:verbatim>Título</f:verbatim></f:facet>
				<h:outputText value="#{item.nome}"/>
			</h:column>

			<h:column headerClass="colData">
				<f:facet name="header"><f:verbatim>Coordenador</f:verbatim></f:facet>
				<h:outputText value="#{item.coordenador.pessoa.nome}"/>
			</h:column>

			<h:column headerClass="colData">
				<f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
				<h:outputText value="#{item.statusString}"/>
			</h:column>

			<h:column headerClass="colData">
				<h:commandLink title="Visualizar" id="visualizar" action="#{ autorizacaoGrupoPesquisaMBean.visualizar }">
					<h:graphicImage url="/img/view.gif" />
				</h:commandLink>
			</h:column>

			<h:column headerClass="colData">
				<h:commandLink title="Emitir Parecer" id="continuar" action="#{ autorizacaoGrupoPesquisaMBean.enviarParecer }">
					<h:graphicImage url="/img/seta.gif" />
				</h:commandLink>
			</h:column>
		
		</h:dataTable>

		
		<table width="100%">            
			<tr>
				<td style="text-align: center; color: red;">
					<h:outputText value="Não há propostas de criação de grupo de pesquisa pendentes de parecer." 
							rendered="#{autorizacaoGrupoPesquisaMBean.semPendencias}"/>
				</td>
			</tr>
		</table>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>