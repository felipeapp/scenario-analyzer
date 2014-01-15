<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:region rendered="#{ propostaGrupoPesquisaMBean.portalDocente }">
<%@include file="/portais/docente/menu_docente.jsp"%>
</a4j:region>

<h2><ufrn:subSistema /> &gt; Lista das Propostas de Grupos de Pesquisa </h2>

	<h:form id="form">
	
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Continuar Cadastro
			<h:graphicImage url="/img/table_go.png" style="overflow: visible;"/>: Visualizar Assinaturas
			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar
			<h:graphicImage value="/img/view2.gif"style="overflow: visible;"/>: Visualizar Comprovante
		    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
		</div>
		<br/>
	
		<h:dataTable value="#{ propostaGrupoPesquisaMBean.gruposPesquisa }" var="item" rendered="#{not empty propostaGrupoPesquisaMBean.gruposPesquisa}"  styleClass="listagem" 
				width="100%" rowClasses="linhaPar, linhaImpar" footerClass="rodape">

			<f:facet name="caption">
				<f:verbatim>Propostas de Criação de Grupo de Pesquisa Cadastradas</f:verbatim>
			</f:facet>
			
			<h:column headerClass="colData">
				<f:facet name="header"><f:verbatim>Título</f:verbatim></f:facet>
				<h:outputText value="#{item.nome}"/>
			</h:column>

			<h:column headerClass="colData">
				<f:facet name="header"><f:verbatim>Líder</f:verbatim></f:facet>
				<h:outputText value="#{item.coordenador.pessoa.nome}"/>
			</h:column>

			<h:column headerClass="colData">
				<f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
				<h:outputText value="#{item.statusString}"/>
			</h:column>
			
			<h:column headerClass="colData">
				<h:commandLink title="Continuar Cadastro" id="continuar" action="#{ propostaGrupoPesquisaMBean.continuar }" rendered="#{ item.exibeBotoes }">
					<h:graphicImage url="/img/seta.gif" />
				</h:commandLink>
			</h:column>

			<h:column headerClass="colData">
				<h:commandLink title="Visualizar Assinaturas" id="verificar" action="#{ propostaGrupoPesquisaMBean.verAssinaturas }" rendered="#{ item.exibeBotoes }">
					<h:graphicImage url="/img/table_go.png" />
				</h:commandLink>
			</h:column>

			<h:column headerClass="colData">
				<h:commandLink title="Visualizar" id="visualizar" action="#{ propostaGrupoPesquisaMBean.visualizar }">
					<h:graphicImage url="/img/view.gif" />
				</h:commandLink>
			</h:column>
			
			<h:column headerClass="colData">
				<h:commandLink title="Visualizar Comprovante" id="comprovante" action="#{ propostaGrupoPesquisaMBean.verComprovante }" immediate="true" rendered="#{ !item.exibeBotoes }">
		    		<h:graphicImage url="/img/view2.gif" />
				</h:commandLink>
			</h:column>

			<h:column headerClass="colData">
				<h:commandLink title="Remover" id="remover" action="#{ propostaGrupoPesquisaMBean.inativar }" rendered="#{ item.exibeBotoes }" onclick="#{confirmDelete}">
					<h:graphicImage url="/img/delete.gif" />
				</h:commandLink>
			</h:column>
	
		</h:dataTable>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>