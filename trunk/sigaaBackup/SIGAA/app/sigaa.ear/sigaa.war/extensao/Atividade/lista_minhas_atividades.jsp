<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>
<link href="/sigaa/css/extensao/busca_acao.css" rel="stylesheet" type="text/css" />

<style type="text/css">
.headerClasse{
	background-color: #DEDFE3;
	border:  none;
}

.headerClasseInterno{
	background-color: #DEDFE3;
	font-weight: bold;
	border:  none;
}

.semBordar{
	border: none;
}

.linhaImparInterno {
    background-color: #F1F5FC;
}

.linhaParInterno {
    background-color: #F5F5F5;
}

</style>

<script type=text/javascript>
	function exibirOpcoes(atividade){
		var linha = 'trOpcoes'+ atividade;
		$(linha).toggle();
	}
</script>

<f:view>

<c:set var="GRAVADO" value="<%= String.valueOf(TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO) %>" scope="application" />


<c:if test="${requestScope._forcaAtualizacaoAtividades == true}">
 	<h:outputText value="#{atividadeExtensao.atualizaMinhasAtividades}" />
</c:if>

<%@include file="/portais/docente/menu_docente.jsp"%>
	
	<h:form id="formAtividade">
	
		<h2><ufrn:subSistema /> &gt; Minhas Ações de Extensão</h2>
		
		
		<div class="descricaoOperacao">
			<p> Caro Usuário, </p>
			<p> Abaixo são apresentadas três listagem: </p>
		    
		    <ul>
		    <li>A primeira se refere as atividades de extensão com cadastro em andamento que ainda não 
		    foram submetidas para avaliação dos departamentos.</li>
		     <li>A segunda listagem são todas as ações de extensão que o senhor coordena.</li>
		     <li>A terceira listagem são todas as ações de extensão que o senhor participa.</li>
		    </ul> 
			
			<h:outputText rendered="#{acesso.coordenadorExtensao}">
			<p> Para gerenciar todas as operações referentes às inscrições para essas atividades utilize essa opção: 
				<i> <h:commandLink value="Gerenciar Inscrições" action="#{gerenciarInscricoesCursosEventosExtensaoMBean.listarCursosEventosParaGerenciarInscricao}" 
				rendered="#{acesso.coordenadorExtensao}" /> </i>
			</p>
			</h:outputText>
			
			<br/>
			<p> <strong> Importante: </strong> Apenas atividades com cadastro em andamento podem ter seus dados alterados. 
			    Então certifique-se que todos os dados da atividade estão corretos, antes de enviar essa atividade para a avaliação do departamento responsável. 
			</p>
		</div>
		
		
		<!-- Ações GRAVADAS PELO USUARIO LOGADO-->
		<c:if test="${not empty atividadeExtensao.atividadesGravadas}">
			<div class="infoAltRem">
				<h:graphicImage value="/img/seta.gif"style="overflow: visible;" />: Continuar Cadastro
			    <h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover
			    <h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar
			    <h:graphicImage value="/img/extensao/printer.png" style="overflow: visible;" />: Versão para Impressão
			</div>
		
			<rich:dataTable id="datatableAtividadesGravadas" value="#{atividadeExtensao.atividadesGravadas}" var="atividade"  rowKeyVar="numeroLinha"
					width="100%" styleClass="listagem" headerClass="headerClasse" columnClasses="semBordar" rowClasses="linhaPar, linhaImpar"
					columnsWidth="10%, 51%, 15%, 20%, 1%, 1%, 1%, 1%">
				<f:facet name="caption">
					<h:outputText value="Lista das Ações de Extensão Pendentes de Envio" />
				</f:facet>

				<t:column>
					<f:facet name="header">
						<f:verbatim>Código</f:verbatim>
					</f:facet>
					<h:outputText value="#{atividade.codigo}" />
				</t:column>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>Título</f:verbatim>
					</f:facet>
					<h:outputText value="#{atividade.titulo}" />
				</t:column>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>Tipo Ação</f:verbatim>
					</f:facet>
					<h:outputText value="#{atividade.tipoAtividadeExtensao.descricao}  #{atividade.registro 
							? '<font color=red>(REGISTRO)</font>' : '(PROPOSTA)' }" escape="false" />
				</t:column>

				<t:column>
					<f:facet name="header">
						<f:verbatim>Situação</f:verbatim>
					</f:facet>
					<h:outputText value="#{atividade.situacaoProjeto.descricao}" />
				</t:column>
		
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink id="continuarCadastro" title="Continuar Cadastro" action="#{ atividadeExtensao.preAtualizar }" immediate="true">
						<f:param name="id" value="#{atividade.id}" />
				    	<h:graphicImage url="/img/seta.gif" />
					</h:commandLink>
				</t:column>
				
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink id="removerCadastro" title="Remover" action="#{ atividadeExtensao.preRemover }" immediate="true">
						<f:param name="id" value="#{atividade.id}" />
				    	<h:graphicImage url="/img/delete.gif" />
					</h:commandLink>
				</t:column>
		
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink id="visualizaAcao" title="Visualizar" action="#{ atividadeExtensao.view }" immediate="true">
						<f:param name="id" value="#{atividade.id}" />
					    <h:graphicImage url="/img/view.gif" />
					</h:commandLink>
				</t:column>
				
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink id="imprimir" title="Versão para impressão" action="#{ atividadeExtensao.view }" immediate="true">
						        <f:param name="id" value="#{atividade.id}" />
						        <f:param name="print" value="true" />
					    		<h:graphicImage url="/img/extensao/printer.png" />
					</h:commandLink>
				</t:column>
				
				<rich:subTable id="dtTblSubAtividade"  var="subAtividade" value="#{atividade.subAtividadesExtensao}" 
								headerClass="headerClasseInterno" columnClasses="semBordar" rowClasses="#{ numeroLinha % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							
					<rich:column>
					</rich:column>	
					<rich:column style="font-style: italic; color:green;">
						<h:outputText value="#{subAtividade.titulo}"/>
					</rich:column>
					<rich:column style="font-style: italic; color:green;">
						<h:outputText value="#{subAtividade.tipoSubAtividadeExtensao.descricao}" />
					</rich:column>
					<rich:column colspan="5">
					
					</rich:column>
					
				</rich:subTable>
				
			</rich:dataTable>
		<br/>
		<br/>
		</c:if>
		<!-- FIM DAS Ações GRAVADAS PELO USUARIO LOGADO-->

		<%@include file="include/atividades_minha_coordenacao.jsp"%>
		<br />
		<%@include file="include/atividades_minha_participacao.jsp"%>
			
		<c:if test="${(empty atividadeExtensao.atividadesGravadas) and (empty atividadeExtensao.atividadesMembroParticipa)}">
			<center><font color='red'>Não há ações de extensão cadastradas, coordenadas ou que o usuário atual faz parte.</font></center>
		</c:if>
		
		<br/>
		<br/>
		<br/>
		<br/>
			
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>