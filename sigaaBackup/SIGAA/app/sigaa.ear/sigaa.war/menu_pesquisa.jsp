<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.pesquisa.form.MembroProjetoDiscenteForm"%>
<f:view>

		<h2>Pesquisa</h2>

		<!--  Scripts do YAHOO -->
		<link rel="stylesheet" type="text/css"
			href="/shared/javascript/yui/tabview/assets/tabs.css">
		<link rel="stylesheet" type="text/css"
			href="/shared/javascript/yui/tabview/assets/border_tabs.css">
		<script type="text/javascript"
			src="/shared/javascript/yui/tabview-min.js"></script>

		<script type="text/javascript">
var criarAbas = function() {
    var tabView = new YAHOO.widget.TabView('tabs-pesquisa');
};
criarAbas();
</script>

		<table cellpadding="3" class="subSistema">
			<tr>
				<td width="80%" valign="top">

				<div id="tabs-pesquisa" class="yui-navset">
				<ul class="yui-nav">
					<li class="selected"><a href="#projetos"><em>Projetos/Grupos</em></a>
					</li>
					<li><a href="#consultas"><em>Consultas/Relatórios</em></a></li>
					<li><a href="#cadastros"><em>Cadastros</em></a></li>
				</ul>

				<div class="yui-content">
				<div id="projetos subsistema">
				<ul>
					<li>Projeto de Pesquisa
					<ul>
						<li><html:link
							action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=popular">Cadastrar Projeto Externo</html:link></li>
						<li><html:link
							action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=popular&interno=true">Submeter Proposta de Projeto Interno</html:link></li>
						<li><html:link
							action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=listByCoordenador">Renovar Projeto de Pesquisa</html:link></li>
						<li><html:link
							action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?dispatch=list&popular=true">Buscar Projetos de Pesquisa</html:link></li>
					</ul>
					</li>
					<li>Planos de Trabalho
					<ul>
						<li><html:link
							action="/pesquisa/planoTrabalho/wizard?dispatch=popular&idProjeto=1">Solicitar Cotas</html:link></li>
						<li><html:link
							action="/pesquisa/planoTrabalho/wizard?dispatch=list&page=0">Listar/Alterar</html:link></li>
					</ul>
					</li>
					<li>Bolsas
					<ul>
						<li><html:link
							action="/pesquisa/indicarBolsista?dispatch=popular">Indicar/Substituir Bolsista</html:link></li>
					</ul>
					</li>
					<li>Relatório Final
					<ul>
						<li><html:link
							action="/pesquisa/cadastroRelatorioProjeto?dispatch=edit">Submeter</html:link></li>
						<li><html:link
							action="/pesquisa/cadastroRelatorioProjeto?dispatch=list">Listar/Alterar</html:link></li>
					</ul>
					</li>
					<li>Avaliações
					<ul>
						<li><html:link action="/verPortalConsultor">Portal do Consultor</html:link></li>
						<li><html:link
							action="/pesquisa/avaliarProjetoPesquisa?dispatch=listaAvaliacao&idConsultor=136195">Avaliar Projeto</html:link></li>
						<li><html:link
							action="/pesquisa/avaliarPlanoTrabalho?dispatch=list&idConsultor=136195"> Avaliar Plano de Trabalho </html:link></li>
						<li><html:link
							action="/pesquisa/avaliarRelatorioProjeto?dispatch=list&idConsultor=136195"> Avaliar Relatório do Projeto</html:link></li>
					</ul>
					</li>
				</div>
				<div id="consultas">
				<ul>
					<li>Projetos de Pesquisa
					<ul>
						<li><html:link
							action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=consulta&popular=true">Consultar</html:link>
						</li>
						<li><html:link
							action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=financiamentos&popular=true"> Projetos Financiados </html:link>
						</li>
						<li><html:link
							action="/pesquisa/buscarMembrosProjeto?dispatch=listRelatoriosFinais&popular=true"> Relatórios Finais </html:link></li>
					</ul>
					<li>Bolsistas e Voluntários
					<ul>
						<li><html:link
							action="/pesquisa/buscarMembroProjetoDiscente?dispatch=relatorio&popular=true"> Consultar </html:link></li>
						<li><html:link
							action="/pesquisa/relatorioSubstituicoesBolsistas?dispatch=popular"> Relatório de Substituições </html:link>
						</li>
						<li><html:link
							action="/pesquisa/buscarMembroProjetoDiscente?dispatch=resumosCongresso&popular=true"> Resumos do CIC </html:link></li>
					</ul>
					<li>Relatórios de Iniciação Científica
					<ul>
						<li><html:link
							action="/pesquisa/relatorioBolsaParcial?dispatch=relatorio&popular=true">Relatórios Parciais</html:link>
						</li>
						<li><html:link
							action="/pesquisa/cadastroRelatorioDiscenteFinal?dispatch=list">Relatórios Finais </html:link></li>
					</ul>
					</li>
					<li>Parecer de Relatórios de IC
					<ul>
						<li><ufrn:link action="/pesquisa/buscarMembroProjetoDiscente"
							param="<%= "dispatch=popular&finalidadeBusca=" +  MembroProjetoDiscenteForm.PARECER_RELATORIOS_PARCIAIS %>">
					        				Relatórios Parciais
					        		</ufrn:link></li>
						<li><ufrn:link action="/pesquisa/buscarMembroProjetoDiscente"
							param="<%= "dispatch=popular&finalidadeBusca=" +  MembroProjetoDiscenteForm.PARECER_RELATORIOS_FINAIS %>">
					        				Relatórios Finais
					        		</ufrn:link></li>
					</ul>
					</li>
				</ul>
				</div>
				<div id="cadastros">

				<ul>
					<li>Áreas de Conhecimento CNPQ
					<ul>
						<li><html:link
							action="/pesquisa/cadastroAreaConhecimento?dispatch=list&page=0">Listar</html:link></li>
					</ul>
					</li>
					<li>Grupos de Pesquisa
					<ul>
						<li><html:link
							action="/pesquisa/cadastroGrupoPesquisa?dispatch=edit">Cadastrar</html:link></li>
						<li><html:link
							action="/pesquisa/cadastroGrupoPesquisa?dispatch=list">Consultar</html:link></li>
					</ul>
					</li>
					<li>Linha de Pesquisa
					<ul>
						<li><html:link
							action="/pesquisa/cadastroLinhaPesquisa?dispatch=list&page=0">Alterar/Remover</html:link></li>
					</ul>
					</li>
					<li>Itens de Avaliação
					<ul>
						<li><html:link
							action="/pesquisa/cadastroItemAvaliacao?dispatch=edit">Cadastrar</html:link></li>
						<li><html:link
							action="/pesquisa/cadastroItemAvaliacao?dispatch=list">Listar/Alterar</html:link></li>
					</ul>
					</li>
					<li>Consultores
					<ul>
						<li>Atualizar Base de Consultores</li>
						<li><html:link
							action="/pesquisa/cadastroConsultor?dispatch=edit">Cadastrar Consultores Internos</html:link></li>
						<li><html:link
							action="/pesquisa/cadastroConsultor?dispatch=list&page=0">Listar/Alterar Consultores</html:link></li>
					</ul>
					</li>
				</ul>

				</div>
				</div>
				</div>
				<c:set var="hideSubsistema" value="true" /></td>
				<td width="300" valign="top">

						<table width="100%" class="listagem">
						<caption> Informativo Sintético </caption>
						<tr>
							<td colspan="3" class="subFormulario">
							Dados por Edital
							</td>
						</tr>
						<tr>
							<td colspan="3">

								<h:form>
									<h:selectOneMenu value="#{quantPesquisa.idEdital}"
										valueChangeListener="#{quantPesquisa.refreshDados}" onchange="submit()">
										<f:selectItem itemValue="0" itemLabel=">> SELECIONE UM EDITAL <<"  />
										<f:selectItems value="#{editalPesquisa.allCombo}"/>
									</h:selectOneMenu>
								</h:form>
							</td>
						</tr>
						<tr class="linhaImpar">
							<td> Projetos Internos Cadastrados </td>
							<td> <h:outputText value="#{quantPesquisa.projetosInternosCadastrados}"/> </td>
						</tr>
						<tr class="linhaImpar">
							<td> Projetos Externos Cadastrados </td>
							<td> <h:outputText value="#{quantPesquisa.projetosExternosCadastrados}"/> </td>
						</tr>
						<tr class="linhaImpar">
							<td> Projetos Internos Enviados </td>
							<td> <h:outputText value="#{quantPesquisa.projetosInternosEnviados}"/> </td>
						</tr>
						<tr class="linhaImpar">
							<td> Projetos Externos Enviados </td>
							<td> <h:outputText value="#{quantMonitoria.projetosExternosEnviados}"/> </td>
						</tr>
						<tr class="linhaPar">
							<td> Pend. Avaliação Consultores </td>
							<td> <h:outputText value="#{quantMonitoria.avaliacaoDepartamento}"/> </td>
							<h:form>
							<td>
								<h:commandButton image="/img/view.gif" action="#{quantMonitoria.detalhaLista}"/>
								<input type="hidden" name="atributoADetalhar" value="avaliacaoDepartamento"/>
							</td>
							</h:form>
						</tr>
						<tr class="linhaImpar">
							<td> Avaliados </td>
							<td> <h:outputText value="#{quantMonitoria.projetosAutorizados}"/> </td>
							<h:form>
							<td>
								<h:commandButton image="/img/view.gif" action="#{quantMonitoria.detalhaLista}"/>
								<input type="hidden" name="atributoADetalhar" value="projetosAutorizados"/>
							</td>
							</h:form>
						</tr>
						<tr class="linhaPar">
							<td> Cotas Solicitadas </td>
							<td> <h:outputText value="#{quantMonitoria.projetosAutorizados}"/> </td>
							<h:form>
							<td>
								<h:commandButton image="/img/view.gif" action="#{quantMonitoria.detalhaLista}"/>
								<input type="hidden" name="atributoADetalhar" value="projetosAutorizados"/>
							</td>
							</h:form>
						</tr>
						<tr>
							<td colspan="3" class="subFormulario">
							Dados Gerais
							</td>
						</tr>
						<tr class="linhaImpar">
							<td> Bases de Pesquisa </td>
							<td> <h:outputText value="#{quantMonitoria.projetosAutorizados}"/> </td>
							<h:form>
							<td>
								<h:commandButton image="/img/view.gif" action="#{quantMonitoria.detalhaLista}"/>
								<input type="hidden" name="atributoADetalhar" value="projetosAutorizados"/>
							</td>
							</h:form>
						</tr>
						<tr class="linhaImpar">
							<td colspan="3"> Bolsistas </td>
						</tr>
						<tr>
							<td colspan="3">
								<table>
									<tr>
										<td> PROPESQ </td>
										<td> 100 </td>
									</tr>
									<tr>
										<td> PIBIC </td>
										<td> 100 </td>
									</tr>
								</table>
							</td>
						</tr>
					</table>



				</td>
			</tr>
		</table>
<div class="linkRodape"><html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
