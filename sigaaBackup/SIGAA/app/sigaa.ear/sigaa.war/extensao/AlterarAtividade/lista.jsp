<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%> 
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.arq.seguranca.sigaa.SipacPapeis"%>

<script type="text/javascript">var J = jQuery.noConflict();</script>
<script type="text/javascript">
	
	JAWR.loader.script('/javascript/jquery.tablesorter.min.js');
	
</script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Alterar Ação de Extensão</h2>

	<%@include file="/extensao/form_busca_atividade.jsp"%>


	<c:if test="${not empty atividadeExtensao.atividadesLocalizadas}">
	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Ação
	 	<ufrn:checkRole papeis="<%= new int[] {	SipacPapeis.GESTOR_BOLSAS_LOCAL	} %>">
			<h:graphicImage value="/img/pesquisa/indicar_bolsista.gif" style="overflow: visible;"/>: Indicar Discente Bolsista ou Voluntário
		</ufrn:checkRole>	 	
		
		<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO  } %>">
			<h:graphicImage value="/img/bolsas.png" style="overflow: visible;"/>: Alterar Bolsas Concedidas
            <h:graphicImage value="/img/projeto/orcamento.png" style="overflow: visible;" width="16" height="16"/>: Alterar Orçamento           
			<h:graphicImage url="/img/projetos/vincular_orcamento.png" width="16" height="16" style="overflow: visible;" />: Vincular Unidade Orçamentária
        </ufrn:checkRole>       
				
		<ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.GESTOR_EXTENSAO	} %>">		 
			<h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;"/>: Alterar Situação<br/>			
		 	<h:graphicImage value="/img/extensao/businessman_refresh.png" style="overflow: visible;"/>: Alterar Equipe
		 	<h:graphicImage value="/img/arrow_undo.png" style="overflow: visible;"/>: Devolver para Coordenador(a)
			<h:graphicImage value="/img/table_refresh.png" style="overflow: visible;"/>: Reativar Ação
		 	<h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Ação
		 	<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Ação		 	
		</ufrn:checkRole>
	</div>
	<br />
	</c:if>
	

	<c:set var="atividades" value="#{atividadeExtensao.atividadesLocalizadas}"/>

	<c:if test="${not empty atividades}">

	<h:form id="form">
	
		 <table class="listagem tablesorter" id="listagem">
		    <caption>Ações de extensão localizadas (${ fn:length(atividades) })</caption>
	
		      <thead>
		      	<tr>
		        	<th width="8%">Código</th>
		        	<th width="50%">Título</th>
		        	<th width="10%">Unidade</th>
		        	<th>Situação</th>
		        	<th style="padding-right: 14px;">Dimensão Acadêmica</th>
		        	<th>&nbsp;</th>
		        	<ufrn:checkRole papeis="<%= new int[] { SipacPapeis.GESTOR_BOLSAS_LOCAL } %>">
                    	<th>&nbsp;</th>
                    </ufrn:checkRole>  
                    <ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO  } %>">
                        <th>&nbsp;</th>                  	
                    	<th>&nbsp;</th>
                    	<th>&nbsp;</th>
                    </ufrn:checkRole>	
                    <ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.GESTOR_EXTENSAO	} %>">
                    	<th>&nbsp;</th>
                    	<th>&nbsp;</th>  
                    	<th>&nbsp;</th>
                    	<th>&nbsp;</th>
                    	<th>&nbsp;</th>
                    	<th>&nbsp;</th>	
                    	<th>&nbsp;</th>		
                    </ufrn:checkRole>
                           	
		        </tr>
		 	</thead>
		 	<tbody>
		 	
			 	 <c:forEach items="#{atividades}" var="atividade" varStatus="status">
		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		
		                    <td> ${atividade.codigo} </td>
		                    <td> ${atividade.titulo}
		                    	<h:outputText value="<br/><i>Coordenador(a): #{atividade.coordenacao.pessoa.nome}</i>" rendered="#{not empty atividade.coordenacao}" escape="false"/> 
		                    </td>
		                    <td> ${atividade.unidade.sigla} </td>
							<td width="10%"> ${atividade.situacaoProjeto.descricao} </td>
							<td>${atividade.projetoAssociado ? 'ASSOCIADO' : 'EXTENSÃO'}</td>
							<td>					
								<h:commandLink title="Visualizar Ação" action="#{ atividadeExtensao.view }" id="visualizar_acao_">
								         <f:param name="id" value="#{atividade.id}"/>
			                   			<h:graphicImage url="/img/view.gif" />
								</h:commandLink>
                            </td>
                            
                           <ufrn:checkRole papeis="<%= new int[] { SipacPapeis.GESTOR_BOLSAS_LOCAL } %>">
	                            <td>
                                    <h:commandLink action="#{planoTrabalhoExtensao.novoPlanoTrabalho}" style="border: 0;" title="Indicar Bolsista ou Voluntário" 
                                        id="cadastrar_plano_trabalho_" rendered="#{atividade.aprovadoEmExecucao}">
                                                <f:setPropertyActionListener target="#{planoTrabalhoExtensao.idAtividade}" value="#{atividade.id}"/>
                                                <h:graphicImage url="/img/pesquisa/indicar_bolsista.gif" />
                                    </h:commandLink>
	                            </td>
                            </ufrn:checkRole>
    
                            <ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO  } %>">                            
	                            <td>
                                    <h:commandLink title="Alterar Bolsas Concedidas" action="#{ movimentacaoCotasExtensao.selecionarAtividade }" 
                                              id="alterar_bolsas_acao_extensao_" rendered="#{atividade.aprovadoEmExecucao}">
                                            <f:param name="id" value="#{atividade.id}"/>
                                            <h:graphicImage url="/img/bolsas.png"/>
                                    </h:commandLink>                    
	                            </td>
	                            
	                            <td>
                                    <h:commandLink title="Alterar Orçamento" action="#{ alteracaoProjetoMBean.iniciarAlterarOrcamento }" immediate="true"
                                        rendered="#{atividade.projetoIsolado}">
                                       <f:param name="id" value="#{atividade.projeto.id}"/>
                                       <h:graphicImage url="/img/projeto/orcamento.png" width="16" height="16" />
                                    </h:commandLink>
	                            </td>

	                            <td>
	                                <h:commandLink title="Vincular Unidade Orçamentária" action="#{ vincularUnidadeOrcamentariaMBean.iniciar }" 
	                                   immediate="true" id="btnIniciarVincularUnidade" rendered="#{atividade.projetoIsolado}">
	                                     <f:param name="id" value="#{atividade.projeto.id}" id="idProjeto"/>
	                                     <h:graphicImage url="/img/projetos/vincular_orcamento.png" width="16" height="16" style="overflow: visible;"/>
	                                </h:commandLink>
    	                        </td>
	                        </ufrn:checkRole> 
	                        
                           	<ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.GESTOR_EXTENSAO	} %>">
                           	    <td>
	                            	<h:commandLink title="Alterar Ação de Extensão" action="#{ atividadeExtensao.preAtualizar }" id="alterar_acao_extensao">
									        <f:param name="id" value="#{atividade.id}"/>
				                   			<h:graphicImage url="/img/alterar.gif" />
									</h:commandLink>
        	                    </td>
                           	
	                            <td>
									<h:commandLink rendered="#{acesso.extensao}" title="Alterar Situação da Ação" action="#{ atividadeExtensao.iniciarAlterarSituacaoAtividade }" id="alterar_situacao_acao_">
										        <f:param name="id" value="#{atividade.id}"/>
									    		<h:graphicImage url="/img/alterar_old.gif"/>
									</h:commandLink>
    	                        </td>
        	                    <td>
									<h:commandLink rendered="#{acesso.extensao}" title="Alterar Membros da Equipe" action="#{ membroProjeto.gerenciarMembrosProjetoAcaoSelecionada }" id="alterar_membro_equipe_">
										        <f:param name="id" value="#{atividade.projeto.id}"/>
									    		<h:graphicImage url="/img/extensao/businessman_refresh.png"/>
									</h:commandLink>
            	                </td>
	                            <td>
									<h:commandLink rendered="#{acesso.extensao && atividade.propostaEnviada}" title="Devolver Proposta para Coordenador(a)" action="#{atividadeExtensao.reeditarProposta}" style="border: 0;" 
											onclick="return confirm('Tem certeza que deseja Devolver esta proposta para Coordenador(a)?');" 
											 id="devolver_coord_dpto_">
										         <f:param name="id" value="#{atividade.id}"/>
					                   			<h:graphicImage url="/img/arrow_undo.png"/>
									</h:commandLink>
    	                        </td>
			                    <td>
									<h:commandLink rendered="#{acesso.extensao && atividade.possivelReativar}" title="Reativar Ação" action="#{ atividadeExtensao.preReativarAcaoExtensao }" id="reativar_acao_">
                                                <f:param name="id" value="#{atividade.id}"/>
                                                <h:graphicImage url="/img/table_refresh.png"/>
                                    </h:commandLink>
            	                </td>
	                            <td>
									<h:commandLink rendered="#{acesso.extensao}" title="Remover Cadastro Ação" action="#{ atividadeExtensao.preRemover }" id="remover_acao_">
										        <f:param name="id" value="#{atividade.id}"/>
									    		<h:graphicImage url="/img/delete.gif"/>
									</h:commandLink>
								</td>
							</ufrn:checkRole>
		              </tr>
		          </c:forEach>
		          
		 	</tbody>
		 </table>
		 <rich:jQuery selector="#listagem" query="tablesorter( {headers:{ 5: { sorter: false},
		 	6: { sorter: false }, 7: { sorter: false }, 8: { sorter: false }, 9: { sorter: false }, 
		 	10: { sorter: false }, 11: { sorter: false }, 12: { sorter: false }, 13: { sorter: false }, 
		 	14: { sorter: false }, 15: { sorter: false }, 16: { sorter: false }  }});" timing="onload" />
	</h:form>

	</c:if>
		 	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
