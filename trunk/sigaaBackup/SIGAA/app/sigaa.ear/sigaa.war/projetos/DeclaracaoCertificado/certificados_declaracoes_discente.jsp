<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.esquerda{text-align: left;}
	.direita{text-align: right;}
	.centro{text-align: center;}
</style>

<f:view>
	<h2><ufrn:subSistema /> &gt; Declarações e Certificados de Participação em Projetos de Ações Associadas</h2>
	
 <h:form id="form">
	<center>
		<div class="infoAltRem">
			<h:graphicImage value="/img/extensao/businessman_view.png" style="overflow: visible;"/>: Visualizar
			<h:graphicImage value="/img/comprovante.png" width="20px;" style="overflow: visible;"/>: Emitir Declaração
			<h:graphicImage value="/img/certificate.png" height="19" width="19" style="overflow: visible;"/>: Emitir Certificado
		</div>
	</center>
	
	<table class="formulario" width="100%">
	<caption>Certificados e Declarações disponíveis</caption>
	
  	<h:dataTable id="dtAvaliacoes"  value="#{ declaracaoMembroProjIntegradoMBean.membros }" var="membro" 
 		width="100%" styleClass="subFormulario" rowClasses="linhaPar, linhaImpar">
 		
		<f:facet name="caption">
			<h:outputText value="Certificados e/ou Declarações de Membro do Projeto" />
		</f:facet>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Projeto</f:verbatim>
			</f:facet>
			<h:outputText value="#{membro.projeto.anoTitulo}" />
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Função</f:verbatim>
			</f:facet>
			<h:outputText value="#{membro.funcaoMembro.descricao}" />
		</t:column>

		<t:column styleClass="text-align: center;" style="text-align: center;">
			<h:commandLink title="Visualizar" action="#{membroProjeto.view}" style="border: 0;" id="view_membro_equipe_">
			   <f:param name="idMembro" value="#{membro.id}"/>
	           <h:graphicImage url="/img/extensao/businessman_view.png" alt="Visualizar"/>
			</h:commandLink>
		</t:column>

		<t:column styleClass="text-align: center;" style="text-align: center;">
			<h:commandLink title="Emitir Declaração" action="#{ declaracaoAssociadosMBean.emitirDeclaracao }" 
				immediate="true" rendered="#{ membro.passivelEmissaoDeclaracao }" id="emitir_declaracao_membro_equipe_">
			   <f:setPropertyActionListener target="#{ declaracaoAssociadosMBean.membro.id }" value="#{ membro.id }"/>
	                	   <h:graphicImage url="/img/comprovante.png" height="19" width="19" alt="Emitir Declaração"/>
			</h:commandLink>
		</t:column>

		<t:column styleClass="text-align: center;" style="text-align: center;">
			<h:commandLink title="Emitir Certificado" action="#{declaracaoMembroProjIntegradoMBean.emitir}" immediate="true" 
				id="emitir_certificado_membro_equipe_associados" rendered="#{ membro.passivelEmissaoCertificado }">
				<f:setPropertyActionListener target="#{declaracaoMembroProjIntegradoMBean.obj.id}" value="#{ membro.id }"/>
                		<h:graphicImage url="/img/certificate.png" height="19" width="19" alt="Emitir Certificado"/>
			</h:commandLink>
		</t:column>

 	</h:dataTable>
	<center><h:outputText  value="Não há certificado ou declaração disponível." rendered="#{empty declaracaoMembroProjIntegradoMBean.membros}"/></center>
  	
</table>

 </h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>