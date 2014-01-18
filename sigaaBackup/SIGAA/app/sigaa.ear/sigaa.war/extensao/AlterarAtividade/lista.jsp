<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%> 
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.arq.seguranca.sigaa.SipacPapeis"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<link href="/sigaa/css/extensao/busca_acao.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<script type="text/javascript">var J = jQuery.noConflict();</script>
<script type="text/javascript">
	
	JAWR.loader.script('/javascript/jquery.tablesorter.min.js');
	
	function exibirOpcoes(atividade){
		var atividade = 'atividade'+ atividade;
		$(atividade).toggle();
	}

</script>

<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Alterar Ação de Extensão</h2>

	<%@include file="/extensao/form_busca_atividade.jsp"%>

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
		        </tr>
		 	</thead>
		 	<tbody>
 			 	 <c:forEach items="#{atividades}" var="atividade" varStatus="status">
		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		
		                    <td style="width: 10%"> ${atividade.codigo} </td>
		                    <td> ${atividade.titulo}
		                    	<h:outputText value="<br/><i>Coordenador(a): #{atividade.coordenacao.pessoa.nome}</i>" rendered="#{not empty atividade.coordenacao}" escape="false"/> 
		                    </td>
		                    <td> ${atividade.unidade.sigla} </td>
							<td width="10%"> ${atividade.situacaoProjeto.descricao} </td>
							<td>${atividade.projetoAssociado ? 'ASSOCIADO' : 'EXTENSÃO'}</td>

							<td style="text-align: right; width: 5%">
								<img src="${ctx}/img/biblioteca/emprestimos_ativos.png" 
									onclick="exibirOpcoes(${atividade.id});" style="cursor: pointer" title="Visualizar Menu"/>
							</td>
						</tr>

						<tr id="atividade${ atividade.id }" class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style="display: none">
							<td colspan="5">
								
								<table>
									<ul class="listaOpcoes">
										<li id="visualizar">
											<h:commandLink title="Visualizar Ação" action="#{ atividadeExtensao.view }" id="visualizar_acao_">
										        <f:param name="id" value="#{atividade.id}"/>
					                   			Visualizar Ação
											</h:commandLink>
										</li>
	
			                            <ufrn:checkRole papeis="<%= new int[] { SipacPapeis.GESTOR_BOLSAS_LOCAL } %>">
				                            <li id="indicarBolsista">
			                                    <a4j:region rendered="#{atividade.aprovadoEmExecucao}">
				                                    <h:commandLink action="#{planoTrabalhoExtensao.novoPlanoTrabalho}" style="border: 0;" title="Indicar Bolsista ou Voluntário" 
				                                        id="cadastrar_plano_trabalho_">
				                                         <f:setPropertyActionListener target="#{planoTrabalhoExtensao.idAtividade}" value="#{atividade.id}"/>
														Indicar Bolsista ou Voluntário
				                                    </h:commandLink>
			                                    </a4j:region>
				                            </li>
			                            </ufrn:checkRole>
			    
			                            <ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.PRESIDENTE_COMITE_EXTENSAO  } %>">
				                            <li id="orcamento">
				                            	<a4j:region rendered="#{atividade.projetoIsolado}">
				                                    <h:commandLink title="Alterar Orçamento" action="#{ alteracaoProjetoMBean.iniciarAlterarOrcamento }" immediate="true">
				                                       <f:param name="id" value="#{atividade.projeto.id}"/>
													   &nbsp; &nbsp;&nbsp;Alterar Orçamento
				                                    </h:commandLink>
			                                    </a4j:region>
				                            </li>
			
				                            <li id="vincularOrcamento">
				                                <a4j:region rendered="#{atividade.projetoIsolado}">
					                                <h:commandLink title="Vincular Unidade Orçamentária" action="#{ vincularUnidadeOrcamentariaMBean.iniciar }" 
					                                   immediate="true" id="btnIniciarVincularUnidade">
					                                     <f:param name="id" value="#{atividade.projeto.id}" id="idProjeto"/>
														 &nbsp;&nbsp;Vincular Unidade Orçamentária
					                                </h:commandLink>
				                                </a4j:region>
			    	                        </li>
				                        </ufrn:checkRole> 
				                        
			                           	<ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.GESTOR_EXTENSAO	} %>">
			                           	    <li id="alterarCadastro">
				                            	<h:commandLink title="Alterar Ação de Extensão" action="#{ atividadeExtensao.preAtualizar }" id="alterar_acao_extensao">
											        <f:param name="id" value="#{atividade.id}"/>
						                   			Alterar Ação de Extensão
												</h:commandLink>
			        	                    </li>
			                           	
				                            <li id="alterarSituacao">
				                            	<a4j:region rendered="#{acesso.extensao}">
													<h:commandLink title="Alterar Situação da Ação" action="#{ atividadeExtensao.iniciarAlterarSituacaoAtividade }" id="alterar_situacao_acao_">
												        <f:param name="id" value="#{atividade.id}"/>
											    		Alterar Situação da Ação
													</h:commandLink>
												</a4j:region>
			    	                        </li>
			        	                    <li id="gerenciarMembro">
			        	                    	<a4j:region rendered="#{acesso.extensao}">
													<h:commandLink title="Alterar Membros da Equipe" action="#{ membroProjeto.gerenciarMembrosProjetoAcaoSelecionada }" id="alterar_membro_equipe_">
												        <f:param name="id" value="#{atividade.projeto.id}"/>
														Alterar Membros da Equipe
													</h:commandLink>
												</a4j:region>
			            	                </li>
				                            <li id="devolverProposta">
				                            	<a4j:region rendered="#{acesso.extensao && atividade.propostaEnviada}">
													<h:commandLink title="Devolver Proposta para Coordenador(a)" action="#{atividadeExtensao.reeditarProposta}" id="devolver_coord_dpto_"
															style="border: 0;" onclick="return confirm('Tem certeza que deseja Devolver esta proposta para Coordenador(a)?');">
												        <f:param name="id" value="#{atividade.id}"/>
											            Devolver Proposta para Coordenador(a)       			
													</h:commandLink>
												</a4j:region>
			    	                        </li>
						                    <li id="reativarProposta">
						                    	<a4j:region rendered="#{acesso.extensao && atividade.possivelReativar}">
													<h:commandLink title="Reativar Ação" action="#{ atividadeExtensao.preReativarAcaoExtensao }" id="reativar_acao_">
				                                       <f:param name="id" value="#{atividade.id}"/>
				                                       Reativar Ação
				                                    </h:commandLink>
			                                    </a4j:region>
			            	                </li>
				                            <li id="removerAcao">
				                            	<a4j:region rendered="#{acesso.extensao}">
													<h:commandLink title="Remover Cadastro Ação" action="#{ atividadeExtensao.preRemover }" id="remover_acao_">
												        <f:param name="id" value="#{atividade.id}"/>
														Remover Cadastro Ação
													</h:commandLink>
												</a4j:region>
											</li>
										</ufrn:checkRole>
											<li style="clear: both; float: none; background-image: none;"></li>
									</ul>
								</table>
								
								<table style="width: 100%">
								
									<c:if test="${ not empty atividade.subAtividadesExtensao }">
									      <thead>
									      	<tr>
												<td class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"></td>
									        	<th>Título</th>
									        	<th>Tipo Sub Atividade</th>
									        	<th>&nbsp;</th>
									        </tr>
									 	</thead>

										<c:forEach var="subAtividade" items="#{ atividade.subAtividadesExtensao }">
											
											<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style="color:green;">
												<td style="width: 10%"></td>
												<td style="width: 55%"> ${subAtividade.titulo} </td>
												<td style="width: 40%"> ${subAtividade.tipoSubAtividadeExtensao.descricao} </td>
												<td style="text-align: right; width: 50%">
													<img src="${ctx}/img/biblioteca/emprestimos_ativos.png" 
														onclick="exibirOpcoes(${subAtividade.id});" style="cursor: pointer" title="Visualizar Menu"/>
												</td>
											</tr>

											<tr id="atividade${ subAtividade.id }" style="width: 100%; display: none;">
												<td colspan="5">
													<ul class="listaOpcoes">
														<li id="alterarMiniAtividade">
															<h:commandLink id="cmdLinkalterarSubAtividadeCoordenacao" title="Alterar" action="#{subAtividadeExtensaoMBean.preAtualizar }"> 
			 								        			<f:param name="idSubAtividade" value="#{subAtividade.id}" />
			 								        			<f:setPropertyActionListener target="#{subAtividadeExtensaoMBean.paginaRetorno}" value="/extensao/Atividade/lista_minhas_atividades.jsp" />
										    					Alterar
			 												</h:commandLink>
														</li>
															
														<li id="removerMiniAtividade">
															<h:commandLink id="cmdLinkRemoverSubAtividadeCoordenacao" title="Remover" action="#{subAtividadeExtensaoMBean.preRemoverSubAtividade}">
			 								        			<f:param name="idSubAtividade" value="#{subAtividade.id}" />
			 								        			<f:setPropertyActionListener target="#{subAtividadeExtensaoMBean.paginaRetorno}"  value="/extensao/Atividade/lista_minhas_atividades.jsp" />
			 							    					Remover
			 												</h:commandLink>
														</li>
					
														<li style="clear: both; float: none; background-image: none;"></li>
														
													</ul>
												</td>
											</tr>
											
										</c:forEach>
										
									</c:if>
																
								</table>

							</td>
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