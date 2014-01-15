<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Avaliações de Projetos</h2>
	
 <h:form id="form">
	<center>
		<div class="infoAltRem">
			<h:graphicImage value="/img/certificate.png" width="20px;" style="overflow: visible;"/>: Emitir Certificado
		</div>
	</center>
	
  	<h:dataTable id="dtAvaliacoes"  value="#{declaracaoAvaliadorMBean.avaliacoes}" 
 		var="avaliacao" 
 		width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
 		
		<f:facet name="caption">
			<h:outputText value="Lista de Avaliações Disponíveis" />
		</f:facet>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Projeto</f:verbatim>
			</f:facet>
			<h:outputText value="#{avaliacao.projeto.anoTitulo}" />
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Tipo de Avaliação</f:verbatim>
			</f:facet>
				<h:outputText value="#{avaliacao.distribuicao.modeloAvaliacao.tipoAvaliacao.descricao}" />		
		</t:column>
		
		<t:column>
			<f:facet name="header">
				<f:verbatim>Tipo de Avaliador</f:verbatim>
			</f:facet>
				<h:outputText value="#{avaliacao.distribuicao.tipoAvaliador.descricao}" />		
		</t:column>

		<t:column styleClass="text-align: center;" style="text-align: center;">
			<h:commandLink action="#{ declaracaoAvaliadorMBean.emitirCertificado }">
				<h:graphicImage value="/img/certificate.png" width="20px;" style="overflow: visible;" alt="Emitir Certificado" title="Emitir Certificado" />
			</h:commandLink>
		</t:column>

 	</h:dataTable>
	<center><h:outputText  value="Não há avaliações para serem realizadas." rendered="#{empty declaracaoAvaliadorMBean.avaliacoes}"/></center>

	<table class="formulario" width="100%">
		<tfoot>
			<tr>
				<td>					
					<h:commandButton id="btCancelar" value="Cancelar" action="#{declaracaoAvaliadorMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
 </h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>