<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Visualizar Avalia��es do Projeto</h2>
	
 <h:form id="form">
	<center>
		<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar
		</div>
	</center>
	
  	<h:dataTable id="dtAvaliacoes"  value="#{avaliacaoProjetoBean.avaliacoes}" 
 		var="avaliacao" binding="#{avaliacaoProjetoBean.uiData}" 
 		width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
 		
		<f:facet name="caption">
			<h:outputText value="Lista de Avalia��es Dispon�veis" />
		</f:facet>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Projeto</f:verbatim>
			</f:facet>
			<h:outputText value="#{avaliacao.projeto.anoTitulo}" />
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim>�rea do CNPq</f:verbatim>
			</f:facet>
			<h:outputText value="#{avaliacao.projeto.areaConhecimentoCnpq.nome}" />
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Tipo de Avalia��o</f:verbatim>
			</f:facet>
				<h:outputText value="#{avaliacao.distribuicao.modeloAvaliacao.tipoAvaliacao.descricao}" />		
		</t:column>
		
		<t:column>
			<f:facet name="header">
				<f:verbatim>Tipo de Avaliador</f:verbatim>
			</f:facet>
				<h:outputText value="#{avaliacao.distribuicao.tipoAvaliador.descricao}" />		
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Avalia��o</f:verbatim>
			</f:facet>
				<p align="right">
					<h:outputText value="#{avaliacao.nota}" escape="false">
						<f:convertNumber pattern="#0.0"/>
					</h:outputText>
				</p>		
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Situa��o</f:verbatim>
			</f:facet>
				<h:outputText value="#{avaliacao.situacao.descricao}" />		
		</t:column>

		<t:column>
			<h:commandButton action="#{avaliacaoProjetoBean.view}" image="/img/view.gif" 
				value="Visualizar" id="btViewAvaliacao" title="Visualizar"/>
		</t:column>
		
 	</h:dataTable>
	<center><h:outputText  value="N�o h� avalia��es para o projeto selecionado." rendered="#{empty avaliacaoProjetoBean.avaliacoes}"/></center>

	<table class="formulario" width="100%">
		<tfoot>
			<tr>
				<td>		
					<h:commandButton id="btVoltar" value="<< Voltar" action="#{projetoBase.listarMeusProjetos}" immediate="true"/>			
					<h:commandButton id="btCancelar" value="Cancelar" action="#{avaliacaoProjetoBean.cancelar}" onclick="#{confirm}" immediate="true"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
 </h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>