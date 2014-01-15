<%@include file="/ava/cabecalho.jsp" %>

<f:view>

<%@include file="/ava/menu.jsp" %>
<a4j:keepAlive beanName="relatorioAcessoTurmaVirtualMBean" />
<h:form id="form">
<fieldset>	
	<legend>
		Relatório de Acesso a Turma Virtual
	</legend>
</fieldset>
<div class="infoAltRem">
	<img src="${ctx}/ava/img/zoom.png"/>: Detalhar
</div>

<c:set value="${relatorioAcessoTurmaVirtualMBean.logLeituraTurmaVirtual}" var="log"/>

<c:if test="${empty log}">
	<p align="center"><b>Nenhum registro localizado.</b></p>
</c:if>
	
<c:if test="${not empty log}">		
	<t:dataTable id="listaAcessos" styleClass="listing" value="#{relatorioAcessoTurmaVirtualMBean.logLeituraTurmaVirtual}" var="item" rowClasses="linhaPar,linhaImpar" style="width:90%;">
		<t:column styleClass="first">
			<f:facet name="header"><h:outputText value="Discente" /></f:facet>
			<h:outputText id="discente" value="#{item.nomeDiscente}"/>
		</t:column>
		<t:column style="text-align:right;">
			<f:facet name="header"><f:verbatim><p align="right"><h:outputText value="Acessos à Turma Virtual" /></p></f:verbatim></f:facet>
			<h:outputText id="entrouTurma" value="#{item.qntEntrouTurmaVirtual}"/>
		</t:column>
		<t:column style="text-align:right;">
			<f:facet name="header"><f:verbatim><p align="right"><h:outputText value="Arquivos" /></p></f:verbatim></f:facet>
			<h:outputText id="arquivos" value="#{item.qntArquivo}"/>
		</t:column>
		<t:column style="text-align:right;">
			<f:facet name="header"><f:verbatim><p align="right"><h:outputText value="Conteúdos" /></p></f:verbatim></f:facet>
			<h:outputText id="conteudos" value="#{item.qntConteudoTurma}"/>
		</t:column>
		<t:column>
				<p align="center">
				<a4j:commandLink id="bVisualizarInformacoesAcesso" title="Detalhar" actionListener="#{relatorioAcessoTurmaVirtualMBean.gerarRelatorioDetalhado}" reRender="nomeDiscente,listaDetalhes">
					<h:graphicImage value="/ava/img/zoom.png"/>
					<f:param name="id" value="#{item.idUsuario}" />
					<rich:componentControl for="panelAcesso" attachTo="bVisualizarInformacoesAcesso" operation="show" event="onclick" />
				</a4j:commandLink>
				</p>
		</t:column>
	</t:dataTable>

	<rich:modalPanel id="panelAcesso" autosized="true" minWidth="500" styleClass="panelPerfil">
		<f:facet name="header">
			<h:panelGroup>
				<h:outputText style="text-align:center" value="Detalhes sobre o Acesso" />
			</h:panelGroup>
		</f:facet>
		<f:facet name="controls">
			<h:panelGroup>
				<h:graphicImage value="/img/close.png" styleClass="hidelink"id="bFecharAcesso" />
				<rich:componentControl for="panelAcesso" attachTo="bFecharAcesso" operation="hide" event="onclick" />
			</h:panelGroup>
		</f:facet>
		<div style="width:100%; height: 300px; overflow: auto;">
	
		<table class="formulario" style="width:80%;">
			<caption><h:outputText id="nomeDiscente" value="#{relatorioAcessoTurmaVirtualMBean.logUsuario.nomeDiscente}"/></caption>
			<tbody><tr><td>
			<t:dataTable id="listaDetalhes" styleClass="subformulario" value="#{relatorioAcessoTurmaVirtualMBean.logUsuario.detalhes}" var="detalhe" rowClasses="linhaPar,linhaImpar" style="width:100%;">
				<t:column>
					<f:facet name="header"><h:outputText value="Tipo de Acesso" /></f:facet>
					<h:outputText id="tipoAcesso" value="#{detalhe.tipoDeAcessoTexto}"/>
				</t:column>
				<t:column>
					<f:facet name="header"><h:outputText value="Arquivos Visualizados" /></f:facet>
					<h:outputText id="arquivoVisualizado" value="#{detalhe.descricaoArquivoBaixado}"/>
				</t:column>
				<t:column style="text-align:center;">
					<f:facet name="header"><f:verbatim><p align="center"><h:outputText value="Data/Hora" /></p></f:verbatim></f:facet>
					<h:outputText id="data" value="#{detalhe.data}">
						<f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss"/>
					</h:outputText>
				</t:column>
			</t:dataTable>
			</td></tr></tbody>
			</table>
		</div>
	</rich:modalPanel>
		
	<div style="text-align: center; font-weight: bold;">
		${fn:length(relatorioAcessoTurmaVirtualMBean.logLeituraTurmaVirtual)} discente(s) localizado(s)
	</div>
</c:if>

</h:form>
</f:view>

<%@include file="/ava/rodape.jsp" %>