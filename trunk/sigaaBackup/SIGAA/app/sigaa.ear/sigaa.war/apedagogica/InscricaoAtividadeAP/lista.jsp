<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
	.colAtividade{}
	.colPeriodo{text-align: center !important;width: 20%;}
	.colStatus{width: 8%;}
	.colCH{text-align: right !important;width: 15%;}
	.colIcone{text-align: right !important;width: 16px;}
	.inscrito{color: green;}
	.pendente{color: red;}
</style>

<f:view>
	<h2 class="title"><ufrn:subSistema /> > Consulta da Situa��o da Inscri��o</h2>

	<div class="descricaoOperacao">
		<p>Caro usu�rio,</p>
		<p>Na listagem abaixo s�o exibidas todas as inscri��es para participa��o em atividades de atualiza��o pedag�gica.</p>
		<p>A listagem � agrupada pela situa��o da inscri��o.</p>
	</div>
	
	<h:form id="formListagemParticipacaoAP">
		<center>
			<h:messages/>
			<div class="infoAltRem">
			    <h:commandLink action="#{inscricaoAtividadeAP.preCadastrar}" >
				    <h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>
				     Realizar Novas Inscri��es
			    </h:commandLink>
			    <f:verbatim><h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Detalhes</f:verbatim>
			    <f:verbatim><img src="/sigaa/img/certificate.png" width="16" />: Emitir Certificado</f:verbatim>
			    <f:verbatim><h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover</f:verbatim>
			</div>
		</center>

		<table class="listagem"  width="80%" >
			<caption>Inscri��es (${fn:length(inscricaoAtividadeAP.all)})</caption>
		</table>
		<t:dataTable value="#{inscricaoAtividadeAP.all}" rendered="#{not empty inscricaoAtividadeAP.all}"
			 var="_reg" styleClass="listagem"  width="80%" columnClasses="colAtividade,colPeriodo,colStatus,colIcone"
			  rowClasses="linhaPar, linhaImpar">
					<t:column styleClass="colAtividade" headerstyleClass="colAtividade">
						<f:facet name="header">
							<f:verbatim>Atividade</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.atividade.nome}"/>
					</t:column>
					
					<t:column styleClass="colAtividade" headerstyleClass="colAtividade">
						<f:facet name="header">
							<f:verbatim>Grupo</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.atividade.grupoAtividade.denominacao}"/>
					</t:column>

					<t:column styleClass="colCH" headerstyleClass="colCH">
						<f:facet name="header">
							<f:verbatim>Carga Hor�ria</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.atividade.ch}h" rendered="#{not empty _reg.atividade.ch}"/> 
						<h:outputText value="N�o Informado" rendered="#{empty _reg.atividade.ch}"/>
					</t:column>

					<t:column styleClass="colPeriodo" headerstyleClass="colPeriodo">
						<f:facet name="header">
							<f:verbatim>Per�odo</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.atividade.inicio}"/> a
						<h:outputText value="#{_reg.atividade.fim}"/>
					</t:column>
					
					
					<t:column styleClass="colStatus" headerstyleClass="colStatus">
						<f:facet name="header">
							<f:verbatim>Situa��o</f:verbatim>
						</f:facet>
						<h:outputText value="#{_reg.descricaoSituacao}" escape="false" styleClass="#{fn:toLowerCase(_reg.descricaoSituacao)}"/>
					</t:column>
					
					
					<t:column styleClass="colIcone" headerstyleClass="colIcone">
						<f:facet name="header">
							<f:verbatim>&nbsp;</f:verbatim>
						</f:facet>
							
												
						<h:commandLink styleClass="noborder" title="Detalhes" id="visualizarRegistro"
							action="#{inscricaoAtividadeAP.viewAtividade}">
							<h:graphicImage url="/img/view.gif" />
							<f:param name="id" value="#{_reg.atividade.id}" />
						</h:commandLink>
					</t:column>
					
					<t:column styleClass="colIcone" headerstyleClass="colIcone">
						<f:facet name="header">
							<f:verbatim>&nbsp;</f:verbatim>
						</f:facet>	
						<h:commandLink styleClass="noborder" title="Emitir Certificado" id="emitirCertificado"
							action="#{certificadoParticipacaoAP.emitirCertificado}" rendered="#{_reg.concluido}">
							<img src="/sigaa/img/certificate.png" width="16" />
							<f:param name="id" value="#{_reg.id}" />
						</h:commandLink>	
						<h:graphicImage url="/img/certificate_off.png"  width="16" title="Emitir Certificado (Inativo)" rendered="#{!_reg.concluido}" />
					</t:column>
					
					<t:column styleClass="colIcone" headerstyleClass="colIcone">
						<f:facet name="header">
							<f:verbatim>&nbsp;</f:verbatim>
						</f:facet>	 	
						<h:commandLink styleClass="noborder" title="Remover" id="removerInscricao"
							onclick="#{confirm}" action="#{inscricaoAtividadeAP.remover}" rendered="#{_reg.inscrito}">
							<h:graphicImage url="/img/delete.gif" />
							<f:param name="id" value="#{_reg.id}" />
						</h:commandLink>
						<h:graphicImage url="/img/delete_off.gif" title="Remover (Inativo)" rendered="#{!_reg.inscrito}" />
						
					</t:column>	
						
		</t:dataTable>
	
	
	<center>
		<br/>
		<h:commandLink value="<< Voltar" action="#{inscricaoAtividadeAP.cancelar}" immediate="true" id="btnvoltar">
		</h:commandLink>
	</center>

	</h:form>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
