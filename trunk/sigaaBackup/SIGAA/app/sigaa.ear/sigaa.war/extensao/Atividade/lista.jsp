<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%> 

<script type="text/javascript">var J = jQuery.noConflict();</script>
<script type="text/javascript">
	JAWR.loader.script('/javascript/jquery.tablesorter.min.js');
</script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<style>
	.em_andamento {
		color: gray;
	}
</style>

<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>

	<h:messages/>
	<h2> <ufrn:subSistema /> > Consultar Ações de Extensão</h2>
	
	<div class="descricaoOperacao">
		
			Senhor(a) Usuário(a),
			<p>	
				Esta é uma funcionalidade do sistema que permite a busca de Ações de Extensão de acordo
			com diversas combinações de critérios. <br/><br/>
			
			<h:panelGroup rendered="#{not  acesso.acessibilidade }">
				<b>Atenção:</b><br/>
				</p>
				<p>	
					Algumas Ações estão com a cor 'Cinza Claro' e significa que é uma Ação de Extensão Isolada e com a Situação 'Cadastro em Andamento'. 
				</p>
			</h:panelGroup>
	</div> 

	<%@include file="/extensao/form_busca_atividade.jsp"%>

	<h:panelGroup rendered="#{ not empty atividadeExtensao.atividadesLocalizadas}">
	
	<div class="infoAltRem">
		 <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Ação de Extensão	
		 <h:graphicImage value="/img/extensao/printer.png" style="overflow: visible;"/>: Versão para Impressão
		 <c:if test="${acesso.extensao }">
		 	<h:graphicImage value="/img/extensao/financiamento_faex.png" width="16px" height="16px" style="overflow: visible;"/>: Visualizar Orçamento Concedido
		  </c:if>  	
	</div>

	<br/>
	
	<h:form id="form">
		<c:set var="SUBMETIDO" value="<%= String.valueOf(TipoSituacaoProjeto.EXTENSAO_SUBMETIDO) %>" scope="application"/>
		<c:set var="CADASTRO_EM_ANDAMENTO" value="<%= String.valueOf(TipoSituacaoProjeto.EXTENSAO_CADASTRO_EM_ANDAMENTO) %>" scope="application"/>
	
		 <table class="listagem tablesorter" id="listagem">
		    <caption>Ações de extensão localizadas (<h:outputText value="#{ fn:length(atividadeExtensao.atividadesLocalizadas) }" />)</caption>
	
		      <thead>
		      	<tr>
		      		<th>Código</th>
		        	<th width="50%">Título</th>
		        	<th>Unidade</th>
		        	<th>Situação</th>
		        	<th>Dimensão Acadêmica</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        	<h:panelGroup rendered="#{acesso.extensao }">
		        		<th>&nbsp;</th>
		        	</h:panelGroup>
		        </tr>
		 	</thead>
		 	<tbody>

		 		<a4j:repeat var="atividade" value="#{atividadeExtensao.atividadesLocalizadas}" rowKeyVar="index">
		 			<h:panelGroup rendered="#{index % 2 == 0}">
		 				<tr class="linhaPar">
		 			</h:panelGroup>
		 			<h:panelGroup rendered="#{index % 2 != 0}">
		 				<tr class="linhaImpar">
		 			</h:panelGroup>
		 					
		 					<h:panelGroup rendered="#{atividade.situacaoProjeto.id == CADASTRO_EM_ANDAMENTO }">
		 						<td class="em_andamento"><h:outputText value="#{atividade.codigo}"/></td>
			                    <td class="em_andamento">
			                    	<h:outputText value="#{atividade.titulo}"/>		                     	
									<h:outputText value="<br/><i>Coordenador(a): #{atividade.coordenacao.pessoa.nome}</i>" rendered="#{not empty atividade.coordenacao}" escape="false"/> 							
			                    </td>
			                    <td class="em_andamento"><h:outputText value="#{atividade.siglaUnidadeInsertida}"/></td>
								<td class="em_andamento"><h:outputText value="#{atividade.situacaoProjeto.descricao}"/></td>
								<td class="em_andamento"><h:outputText value="#{atividade.projetoAssociado ? 'ASSOCIADO' : 'EXTENSÃO'}"/></td>
			 				</h:panelGroup>
			 				
			 				<h:panelGroup rendered="#{atividade.situacaoProjeto.id != CADASTRO_EM_ANDAMENTO }">
			 					<td><h:outputText value="#{atividade.codigo}"/></td>
			                    <td>
			                    	<h:outputText value="#{atividade.titulo}"/>		                     	
									<h:outputText value="<br/><i>Coordenador(a): #{atividade.coordenacao.pessoa.nome}</i>" rendered="#{not empty atividade.coordenacao}" escape="false"/> 							
			                    </td>
			                    <td><h:outputText value="#{atividade.siglaUnidadeInsertida}"/></td>
								<td><h:outputText value="#{atividade.situacaoProjeto.descricao}"/></td>
								<td><h:outputText value="#{atividade.projetoAssociado ? 'ASSOCIADO' : 'EXTENSÃO'}"/></td>
			 				</h:panelGroup>

							<%-- comandos liberados inclusive para ALUNOS --%>
							<td>					
								<h:commandLink id="visualizar" title="Visualizar Ação" action="#{ atividadeExtensao.view }" immediate="true">
							        <f:param name="id" value="#{atividade.id}" />
							        <f:param name="print" value="false" />
						    		<h:graphicImage url="/img/view.gif" />
								</h:commandLink>
							</td>
							
							<td>
								<h:commandLink id="imprimir" title="Visualizar Ação" action="#{ atividadeExtensao.view }" immediate="true">
							        <f:param name="id" value="#{atividade.id}" />
							        <f:param name="print" value="true" />
						    		<h:graphicImage url="/img/extensao/printer.png" />
								</h:commandLink>
							</td>
							
						     <td>
                                <h:commandButton id="orcamento" title="Visualizar Orçamento Concedido" rendered="#{acesso.extensao}" style="width:16px;height:16px;"
                                			action="#{ atividadeExtensao.view }" immediate="true" image="/img/extensao/financiamento_faex.png">
                                        <f:setPropertyActionListener target="#{atividadeExtensao.atividadeSelecionada.id}" value="#{atividade.id}"/>
                                        <f:param name="orcamentoAprovado" value="true"/>
                                </h:commandButton>
                             </td>
		              </tr>
		 		</a4j:repeat>
		          
		 	</tbody>
		 </table>
		 <rich:jQuery selector="#listagem" query="tablesorter( {headers: {5: { sorter: false },6: { sorter: false },7: { sorter: false },8: { sorter: false } } });" timing="onload" /> 
	</h:form>

	</h:panelGroup>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>