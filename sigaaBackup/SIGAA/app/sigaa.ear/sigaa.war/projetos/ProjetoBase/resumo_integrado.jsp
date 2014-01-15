<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
	<h:form id="form">
	
			<h:messages/>
			<h2><ufrn:subSistema /> &gt; Resumo geral da propostas de ação acadêmica associada</h2>
						
			<div class="descricaoOperacao" id="aviso">			
			<table width="100%">
				<tr>
				<td width="10%" align="center"><html:img page="/img/warning.gif" width="30px" height="30px"/> </td>
				<td align="justify">				 
							Caro Docente, seu projeto foi gravado com sucesso. Ele está salvo em nosso banco de dados e poderá ser alterado por você a qualquer momento.
							<br/>
							<br/>
							
							Se esta proposta já contém todos os itens obrigatórios e se você NÃO pretende alterá-la depois, clique no botão abaixo para enviá-la para os chefes de departamentos e seguir com o processo de aprovação.
							<br/>
							<br/>
							
							Somente após o envio e autorização dos chefes de todos os departamentos envolvidos, é que sua proposta
							será encaminhada para análise do Comitê Integrado de Ensino Pesquisa e Extensão. 
							<br/>
							<br/>

							<b>Atenção</b>: ao clicar no botão 'Finalizar Edição e Enviar para os Departamentos' você NÃO poderá alterar esta Proposta depois.<br/>
							A submissão da proposta como um todo só será possível após o cadastro completo de todas as partes do pendentes.<br/>
				</td>
				</tr>
			</table>
			</div>
			<br/>			

			<div class="infoAltRem">
			    <h:graphicImage value="/img/check.png" style="overflow: visible;"/>: Cadastro concluído
			    <h:graphicImage value="/img/check_cinza.png" style="overflow: visible;"/>: Cadastro em andamento
				<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Iniciar/Continuar cadastro
			    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar proposta
			</div>

			
			
	<div>

		<table class="formulario" width="99%">
			<caption> Resumo geral da proposta de ação acadêmica associada </caption>
			
			<tr>
				<th width="25%"><b> Título: </b></th>
				<td><h:outputText value="#{projetoBase.obj.titulo}"
					escape="false" /></td>
			</tr>
		
			<tr>
				<th><b> Ano: </b></th>
				<td><h:outputText value="#{projetoBase.obj.ano}" /></td>
			</tr>
		
			<tr>
				<th><b> Período: </b></th>
				<td><h:outputText value="#{projetoBase.obj.dataInicio}" /> a
				<h:outputText value="#{projetoBase.obj.dataFim}" /></td>
			</tr>
		
			<tr>
				<th><b> Abrangência: </b></th>
				<td><h:outputText value="#{projetoBase.obj.abrangencia.descricao}" /></td>
			</tr>

			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario">Dimensão acadêmica da proposta
				</td>
			</tr>
			
			<c:if test="${projetoBase.obj.ensino}">
			<tr>
				<td><h3>Ensino</h3></td>
				<td>
					<table width="100%">					
						<c:if test="${projetoBase.obj.programaMonitoria}">			
							<tr>
								<td>
									<h:graphicImage value="/img/check.png" style="overflow: visible;" rendered="#{projetoBase.obj.programaMonitoriaSubmetido}"/>
									<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" rendered="#{!projetoBase.obj.programaMonitoriaSubmetido}"/>
								</td>
								<td width="90%" align="left">Projeto de monitoria</td>
								<td width="2%">								
									<h:commandLink title="Iniciar/Continuar cadastro" action="#{ projetoBase.iniciarProjetoMonitoriaAssociado }" immediate="true" rendered="#{!projetoBase.obj.programaMonitoriaSubmetido}">
							        	<f:param name="id" value="#{projetoBase.obj.id}"/>
										<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>
									</h:commandLink>
				    			</td>

								<td width="2%">								
									<h:commandLink title="Visualizar" action="#{ projetoMonitoria.viewMonitoriaAssociado }" immediate="true" rendered="#{projetoBase.obj.programaMonitoriaSubmetido}">
							        	<f:param name="id" value="#{projetoBase.obj.id}"/>
				    					<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>
									</h:commandLink>
				    			</td>
							</tr>
						</c:if>
						
						<c:if test="${projetoBase.obj.melhoriaQualidadeEnsino}">
							<tr>
								<td>
									<h:graphicImage value="/img/check.png" style="overflow: visible;" rendered="#{projetoBase.obj.melhoriaQualidadeEnsinoSubmetido}"/>
									<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" rendered="#{!projetoBase.obj.melhoriaQualidadeEnsinoSubmetido}"/>									
								</td>
								<td width="90%" align="left"> Projeto de melhoria da qualidade do ensino</td>
								<td width="2%">
									<h:commandLink title="Iniciar/Continuar cadastro" action="#{ projetoBase.iniciarProjetoMelhoriaQualidadeEnsinoAssociado }" immediate="true" rendered="#{!projetoBase.obj.melhoriaQualidadeEnsinoSubmetido}">
							        	<f:param name="id" value="#{projetoBase.obj.id}"/>
										<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>
									</h:commandLink>
				    			</td>
								<td width="2%">
									<h:commandLink title="Visualizar" action="#{ projetoMonitoria.viewMelhoriaAssociado }" immediate="true" rendered="#{projetoBase.obj.melhoriaQualidadeEnsinoSubmetido}">
							        	<f:param name="id" value="#{projetoBase.obj.id}"/>
				    					<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>
									</h:commandLink>
				    			</td>
							</tr>
						</c:if>						
					</table>					
				</td>
			</tr>
			</c:if>

			<c:if test="${projetoBase.obj.pesquisa}">
			<tr>
				<td><h3>Pesquisa</h3></td>
				<td>
					<table width="100%">						
						<c:if test="${projetoBase.obj.apoioGrupoPesquisa}">
							<tr>
								<td>
									<h:graphicImage value="/img/check.png" style="overflow: visible;" rendered="#{projetoBase.obj.apoioGrupoPesquisaSubmetido}" />
									<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" rendered="#{!projetoBase.obj.apoioGrupoPesquisaSubmetido}" />
								</td>
								<td width="90%" align="left"> Projeto de apoio a grupo de pesquisa</td>
								<td width="2%">
									<h:graphicImage value="/img/seta.gif" style="overflow: visible;" rendered="false"/>
				    			</td>
								<td width="2%">
				    				<h:graphicImage value="/img/view.gif" style="overflow: visible;" rendered="false"/>
				    			</td>
							</tr>
						</c:if>
						<c:if test="${projetoBase.obj.apoioNovosPesquisadores}">
							<tr>
								<td>
									<h:graphicImage value="/img/check.png" style="overflow: visible;" rendered="#{projetoBase.obj.apoioNovosPesquisadoresSubmetido}"/>
									<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" rendered="#{!projetoBase.obj.apoioNovosPesquisadoresSubmetido}"/>
								</td>
								<td width="90%" align="left"> Projeto de apoio a novos pesquisadores</td>
								<td width="2%">
									<h:graphicImage value="/img/seta.gif" style="overflow: visible;" rendered="false"/>
				    			</td>
								<td width="2%">
				    				<h:graphicImage value="/img/view.gif" style="overflow: visible;" rendered="false"/>
				    			</td>
							</tr>
						</c:if>
						<c:if test="${projetoBase.obj.iniciacaoCientifica}">
							<tr>
								<td>
									<h:graphicImage value="/img/check.png" style="overflow: visible;" rendered="#{projetoBase.obj.iniciacaoCientificaSubmetido}"/>
									<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" rendered="#{!projetoBase.obj.iniciacaoCientificaSubmetido}"/>
								</td>
								<td width="90%" align="left"> Programa institucional de bolsas de iniciação científica</td>
								<td width="2%">
									<h:commandLink  action="#{projetoBase.iniciarProjetoPesquisaAssociado}"	rendered="#{!projetoBase.obj.iniciacaoCientificaSubmetido}">
										<f:param name="id" value="#{projetoBase.obj.id}" />
										<f:param name="linkPesquisa" value="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=popularProjetoBase&interno=true&idProjetoBase=" />
										<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>
									</h:commandLink>
								</td>
								<td width="2%">
									<h:commandLink  action="#{projetoBase.visualizarProjetoPesquisaAssociado}" rendered="#{projetoBase.obj.iniciacaoCientificaSubmetido}">
										<f:param name="id" value="#{projetoBase.obj.id}" />
										<f:param name="linkPesquisa" value="/pesquisa/projetoPesquisa/criarProjetoPesquisa.do?dispatch=viewProjetoBase&idProjetoBase=" />
					    				<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>
									</h:commandLink>
				    			</td>
							</tr>
						</c:if>						
					</table>					
				</td>
			</tr>
			</c:if>
			
			<c:if test="${projetoBase.obj.extensao}">
			<tr>
				<td><h3>Extensão</h3></td>
				<td>
					<table width="100%">
					
						<c:if test="${projetoBase.obj.programaExtensao}">
						<tr>
							<td>
								<h:graphicImage value="/img/check.png" style="overflow: visible;" rendered="#{projetoBase.obj.programaExtensaoSubmetido}"/>
								<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" rendered="#{!projetoBase.obj.programaExtensaoSubmetido}"/>
							</td>
							<td width="90%" align="left"> Programa</td>
							<td width="2%">
									<h:commandLink title="Iniciar/Continuar cadastro" action="#{ projetoBase.iniciarProgramaExtensaoAssociado }" immediate="true" rendered="#{!projetoBase.obj.programaExtensaoSubmetido}">
							        	<f:param name="id" value="#{projetoBase.obj.id}"/>
										<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>
									</h:commandLink>
				    		</td>
							<td width="2%">
									<h:commandLink title="Visualizar" action="#{ projetoBase.view }" immediate="true" rendered="#{projetoBase.obj.programaExtensaoSubmetido}">
							        	<f:param name="id" value="#{projetoBase.obj.id}"/>
				    					<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>
									</h:commandLink>
				    		</td>
						</tr>
						</c:if>						
						<c:if test="${projetoBase.obj.projetoExtensao}">
							<tr>
								<td>
									<h:graphicImage value="/img/check.png" style="overflow: visible;" rendered="#{projetoBase.obj.projetoExtensaoSubmetido}"/>
									<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" rendered="#{!projetoBase.obj.projetoExtensaoSubmetido}"/>
								</td>
								<td width="90%" align="left"> Projeto</td>
								<td width="2%">
										<h:commandLink title="Iniciar/Continuar cadastro" action="#{ projetoBase.iniciarProjetoExtensaoAssociado }" immediate="true" rendered="#{!projetoBase.obj.projetoExtensaoSubmetido}">
								        	<f:param name="id" value="#{projetoBase.obj.id}"/>
											<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>
										</h:commandLink>
					    		</td>
								<td width="2%">
										<h:commandLink title="Visualizar" action="#{ projetoBase.view }" immediate="true" rendered="#{projetoBase.obj.projetoExtensaoSubmetido}">
								        	<f:param name="id" value="#{projetoBase.obj.id}"/>
					    					<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>
										</h:commandLink>
					    		</td>
							</tr>
						</c:if>
						<c:if test="${projetoBase.obj.cursoExtensao}">
							<tr>
								<td>
									<h:graphicImage value="/img/check.png" style="overflow: visible;" rendered="#{projetoBase.obj.cursoExtensaoSubmetido}"/>
									<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" rendered="#{!projetoBase.obj.cursoExtensaoSubmetido}"/>
								</td>
								<td width="90%" align="left"> Curso</td>
								<td width="2%">
										<h:commandLink title="Iniciar/Continuar cadastro" action="#{ projetoBase.iniciarCursoExtensaoAssociado }" immediate="true" rendered="#{!projetoBase.obj.cursoExtensaoSubmetido}">
								        	<f:param name="id" value="#{projetoBase.obj.id}"/>
											<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>
										</h:commandLink>
					    		</td>
								<td width="2%">
										<h:commandLink title="Visualizar" action="#{ projetoBase.view }" immediate="true" rendered="#{projetoBase.obj.cursoExtensaoSubmetido}">
								        	<f:param name="id" value="#{projetoBase.obj.id}"/>
					    					<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>
										</h:commandLink>
					    		</td>
							</tr>
						</c:if>
						
						<c:if test="${projetoBase.obj.eventoExtensao}">
							<tr>
								<td>
									<h:graphicImage value="/img/check.png" style="overflow: visible;" rendered="#{projetoBase.obj.eventoExtensaoSubmetido}"/>
									<h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" rendered="#{!projetoBase.obj.eventoExtensaoSubmetido}"/>
								</td>
								<td width="90%" align="left"> Evento</td>
								<td width="2%">
										<h:commandLink title="Iniciar/Continuar cadastro" action="#{ projetoBase.iniciarEventoExtensaoAssociado }" immediate="true" rendered="#{!projetoBase.obj.eventoExtensaoSubmetido}">
								        	<f:param name="id" value="#{projetoBase.obj.id}"/>
											<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>
										</h:commandLink>
					    		</td>
								<td width="2%">
										<h:commandLink title="Visualizar" action="#{ projetoBase.view }" immediate="true" rendered="#{projetoBase.obj.eventoExtensaoSubmetido}">
								        	<f:param name="id" value="#{projetoBase.obj.id}"/>
					    					<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>
										</h:commandLink>
					    		</td>
							</tr>
						</c:if>					
					</table>					
				</td>
			</tr>
			</c:if>
		</table>
	</div>

	<table class=formulario width="100%">
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="<< Voltar" action="#{ projetoBase.passoAnterior }" id="btPassoAnteriorSubmissao"/>		
					<h:commandButton value="Finalizar Edição e Enviar" action="#{ projetoBase.submeterProjeto }" id="btSubmeterProjeto" rendered="#{ !projetoBase.membroComiteAlterandoCadastro }" />		
					<h:commandButton value="Gravar (Rascunho)" action="#{ projetoBase.gravarFinalizar }" id="btGravarProjeto" />		
					<h:commandButton value="Cancelar" action="#{ projetoBase.cancelar }" id="btCancelar" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
			
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>