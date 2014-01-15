<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Avalia��es de Projetos</h2>
	
 <h:form id="form">
	<center>
		<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Projeto
				<h:graphicImage value="/img/view2.gif"style="overflow: visible;"/>: Visualizar Avalia��o
				<h:graphicImage value="/img/seta.gif"style="overflow: visible;" rendered="#{acesso.avaliadorAcoesAssociadas || acesso.comissaoIntegrada || acesso.comissaoPesquisa}"/>
					<h:outputText value=": Avaliar" rendered="#{acesso.avaliadorAcoesAssociadas || acesso.comissaoIntegrada || acesso.comissaoPesquisa}"/>
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
				<h:commandLink title="Visualizar Projeto" action="#{ projetoBase.view }" immediate="true">
				        <f:param name="id" value="#{avaliacao.projeto.id}"/>
			    		<h:graphicImage url="/img/view.gif" />
				</h:commandLink>
		</t:column>
		
		<t:column>
			<h:commandButton action="#{avaliacaoProjetoBean.view}" image="/img/view2.gif" 
				value="Visualizar Avalia��o" id="btViewAvaliacao" title="Visualizar Avalia��o"/>
		</t:column>
		
		<t:column>
			<h:commandButton action="#{avaliacaoProjetoBean.iniciarAvaliacao}" image="/img/seta.gif" 
				value="Avaliar" id="btAvaliar"  title="Avaliar"
				rendered="#{(acesso.avaliadorAcoesAssociadas || acesso.comissaoIntegrada || acesso.comissaoPesquisa) && avaliacao.projeto.permitidoAvaliar}"/>
		</t:column>
		
 	</h:dataTable>
	<center><h:outputText  value="N�o h� avalia��es para serem realizadas." rendered="#{empty avaliacaoProjetoBean.avaliacoes}"/></center>

	<table class="formulario" width="100%">
		<tfoot>
			<tr>
				<td>					
					<h:commandButton id="btCancelar" value="Cancelar" action="#{avaliacaoProjetoBean.cancelar}" onclick="#{confirm}" immediate="true"/>
				</td>
			</tr>
		</tfoot>
	</table>
	
 </h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>