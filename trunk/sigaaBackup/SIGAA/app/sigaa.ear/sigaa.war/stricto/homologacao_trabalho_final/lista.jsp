<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<a4j:keepAlive beanName="homologacaoTrabalhoFinal"></a4j:keepAlive>

<%@include file="/stricto/menu_coordenador.jsp" %>
<h2> <ufrn:subSistema /> &gt; Solicitações de Homologação de Diploma Pendentes</h2>

<h:form>
<div class="infoAltRem">
       <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar solicitação
       <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar solicitação
</div>

<table class="listagem">
	<caption>Solicitações de Homologação de Diploma Pendentes</caption>
	<thead>
		<tr>
			<td width="6%">Nº Processo</td>
			<td width="30%">Discente</td>
			<td width="40%">Título do trabalho</td>
			<td width="10%">Data da solicitação</td>
			<td width="2%"></td>
			<td width="2%"></td>
		</tr>
	</thead>
	<tbody>
	<c:forEach items="#{homologacaoTrabalhoFinal.lista}" var="_solicitacao" varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td>${_solicitacao.numProcesso}</td>
			<td>${_solicitacao.banca.dadosDefesa.discente.nome}</td>
			<td>${_solicitacao.banca.dadosDefesa.titulo}</td>
			<td><ufrn:format type="data" valor="${_solicitacao.criadoEm}"/></td>
			<td>
			<h:commandLink title="Alterar solicitação" action="#{homologacaoTrabalhoFinal.atualizar}" id="linkAlterarSolicitacao">
				<h:graphicImage url="/img/alterar.gif"/>
				<f:param name="id" value="#{_solicitacao.id}" id="IDSolicitacao"/>
			</h:commandLink>
			</td>
			<td>
			<h:commandLink title="Visualizar Componente de Solicitação" action="#{relatorioHomologacaoStricto.selecionaHomologacao}" id="linkVisualizarComponenteSolicitacao">
				<h:graphicImage url="/img/view.gif"/>
				<f:param name="id" value="#{_solicitacao.id}"/>
			</h:commandLink>
			</td>
		</tr>
	</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="6" align="center">
				<h:commandButton value="Cancelar" action="#{relatorioHomologacaoStricto.cancelar}" id="botaoParaCancelar"/>
			</td>
		</tr>
	</tfoot>
	</table>

<%-- 

<t:dataTable value="#{homologacaoTrabalhoFinal.lista}" var="_solicitacao"  
	styleClass="listagem" rowClasses="linhaPar, linhaImpar">
	<f:facet name="caption"> <h:outputText value="Solicitações Enviadas"></h:outputText> </f:facet>

	<t:column>
		<f:facet name="header"><f:verbatim>Ano</f:verbatim></f:facet>
		<h:outputText value="#{_solicitacao.anoProcesso}"/>
	</t:column>
	
	<t:column>
		<f:facet name="header"><f:verbatim>Nº Processo</f:verbatim></f:facet>
		<h:outputText value="#{_solicitacao.numProcesso}"/>
	</t:column>
	
	<t:column>
		<f:facet name="header"><f:verbatim>Status</f:verbatim></f:facet>
		<h:outputText value="#{_solicitacao.banca.dadosDefesa.discente.statusString}"/>
	</t:column>

	<t:column">
		<f:facet name="header"><f:verbatim>Título do trabalho</f:verbatim></f:facet>
		<h:outputText value="#{_solicitacao.banca.dadosDefesa.titulo}"/>
	</t:column>
	
	<t:column>
		<f:facet name="header"><h:outputText value="Data da Solicitação" /></f:facet>
		<h:outputText value="#{_solicitacao.criadoEm}"> 
			<f:convertDateTime/>
		</h:outputText>
	</t:column>
	
	<t:column>
		<h:commandLink title="Alterar solicitação" action="#{homologacaoTrabalhoFinal.atualizar}">
			<h:graphicImage url="/img/alterar.gif"/>
			<f:param name="id" value="#{_solicitacao.id}"/>
		</h:commandLink>
	</t:column>
	
	<t:column>
		<h:commandLink title="Visualizar Componente de Solicitação" action="#{relatorioHomologacaoStricto.selecionaHomologacao}">
			<h:graphicImage url="/img/view.gif"/>
			<f:param name="id" value="#{_solicitacao.id}"/>
		</h:commandLink>
	</t:column>	
					
</t:dataTable>
--%>
	
</h:form>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>