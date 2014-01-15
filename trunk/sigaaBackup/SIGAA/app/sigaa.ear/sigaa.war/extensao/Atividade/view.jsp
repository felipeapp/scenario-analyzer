<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.FuncaoMembro"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>
<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<style>
<!-- /* Paineis de opçoes */
.subtitulo {
	text-align: center;
	background: #EDF1F8;
	color: #333366;
	font-variant: small-caps;
	font-weight: bold;
	letter-spacing: 1px;
	margin: 1px 0;
	border-collapse: collapse;
	border-spacing: 2px;
	font-size: 1em;
	font-family: Verdana, sans-serif;
	font-size: 12px
}

-->
</style>

<f:view>
	<c:if test="${!acesso.extensao}">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</c:if>

	<h2><ufrn:subSistema /> &gt; Visualização da Ação de Extensão</h2>

	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Arquivo
		<h:graphicImage value="/img/view2.gif" style="overflow: visible;" />: Visualizar Plano de Trabalho
		<h:graphicImage value="/img/buscar.gif" style="overflow: visible;" />: Visualizar Ação Vinculada
	</div>
	<h:form>
		<c:set var="PROJETO" value="<%= String.valueOf(TipoAtividadeExtensao.PROJETO) %>" scope="application" />
		<c:set var="PROGRAMA" value="<%= String.valueOf(TipoAtividadeExtensao.PROGRAMA) %>" scope="application" />
		<c:set var="PRODUTO" value="<%= String.valueOf(TipoAtividadeExtensao.PRODUTO) %>" scope="application" />
		<c:set var="CURSO" value="<%= String.valueOf(TipoAtividadeExtensao.CURSO) %>" scope="application" />
		<c:set var="EVENTO"	value="<%= String.valueOf(TipoAtividadeExtensao.EVENTO) %>"	scope="application" />
		<c:set var="PRESTACAO_SERVICO" value="<%= String.valueOf(TipoAtividadeExtensao.PRESTACAO_SERVICO) %>" scope="application" />

		<table class="visualizacao" width="100%">
		<caption>DADOS DA AÇÃO DE EXTENSÃO</caption>
			<tbody>
				<%-- DADOS GERAIS, DE TODOS OS TIPOS DE AÇÂO --%>
				<tr>
					<td colspan="6" class="subFormulario">Dados Gerais</td>
				</tr>
				<tr>
					<th>Código:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.codigo}" /></td>
					<th>Título:</th>
					<td colspan="3"><h:outputText value="#{atividadeExtensao.atividadeSelecionada.titulo}" escape="false" /></td>
				</tr>

				<tr>
					<th>Ano:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.ano}" /></td>
					<th>Período:</th>
					<td>
						<h:outputText value="#{atividadeExtensao.atividadeSelecionada.dataInicio}" /> a
						<h:outputText value="#{atividadeExtensao.atividadeSelecionada.dataFim}" />
					</td>
					<th>Categoria:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.descricao}" /></td>
				</tr>

				<tr>
					<th>Unidade Proponente:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.unidade.nome}" /> 
						<h:outputText value=" / #{atividadeExtensao.atividadeSelecionada.unidade.gestora.sigla}" rendered="#{ not empty atividadeExtensao.atividadeSelecionada.unidade }"/></td>
                    <th>Unidade Orçamentária:</th>
                    <td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.projeto.unidadeOrcamentaria.nome}" />  
                        <h:outputText value=" / #{atividadeExtensao.atividadeSelecionada.projeto.unidadeOrcamentaria.gestora.sigla}" rendered="#{ not empty atividadeExtensao.atividadeSelecionada.projeto.unidadeOrcamentaria }"/></td>
					<th>Outras Unidades Envolvidas:</th>
					<td>
						<t:dataTable id="unidadesEnvolvidas" value="#{atividadeExtensao.atividadeSelecionada.unidadesProponentes}"
								var="atividadeUnidade" rendered="#{not empty atividadeExtensao.atividadeSelecionada.unidadesProponentes}">
							<t:column>
								<h:outputText value=" . #{atividadeUnidade.unidade.nome}" />
								<h:outputText value=" / #{atividadeUnidade.unidade.gestora.sigla}" />
							</t:column>
						</t:dataTable>
					</td>
				</tr>

				<tr>
					<th>Abrangência:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.tipoRegiao.descricao}" /></td>
					<th>Área do CNPq:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.areaConhecimentoCnpq.nome}" /></td>
					<th>Área Principal:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.areaTematicaPrincipal.descricao}" /></td>
				</tr>

				<tr>
					<th>Tipo de Cadastro:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.registro 
							? 'REGISTRO DE AÇÃO REALIZADA' : 'SUBMISSÃO DE NOVA PROPOSTA'}" />
					</td>
					<th>Convênio Funpec:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.convenioFunpec ? 'SIM':'NÃO'}" /></td>
					<c:choose>
						<c:when test="${atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == PROJETO}">
							<th>Grupo Permanente de Arte e Cultura:</th>
							<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.permanente ? 'SIM' : 'NÃO'}" /></td>
						</c:when>
						<c:otherwise><td colspan="2" /></c:otherwise>
					</c:choose>
				</tr>

				<tr>
					<th>Fonte de Financiamento:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.fonteFinanciamentoString}" /></td>
					<th>Renovação:</th>
					<td><h:outputText value="#{atividadeExtensao.obj.renovacao ? 'SIM' : 'NÃO'}" /></td>
					<th>Público Alvo Interno:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.publicoEstimado}" />
					</td>
				</tr>

				<tr>
					<th>Nº Bolsas Solicitadas:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.bolsasSolicitadas}" /></td>
					<th>Nº Bolsas Concedidas:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.bolsasConcedidas}" /></td>
					
					<c:choose>
						<c:when test="${(atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id != PROGRAMA)}">
							<th> Faz parte de Programa de Extensão?</th>
							<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.vinculoProgramaExtensao ? 'SIM':'NÃO'}" />
							<!-- <td><h:outputText value="#{not empty atividadeExtensao.atividadeSelecionada.atividadesPai ? 'SIM':'NÃO'}" /> -->
								<ufrn:help img="/img/prodocente/information.png">Indica se esta ação faz parte de algum Programa de Extensão</ufrn:help>
							</td>
						</c:when>
						<c:otherwise><td colspan="2" /></c:otherwise>
					</c:choose>
				</tr>
				<tr>
					<th>Público Alvo Externo:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.publicoExterno}" />
					</td>
					<td colspan="4">
					</td>
				</tr>

				<tr>
					<th>Público Alvo Interno:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.publicoAlvo}" /></td>
					<th>Público Alvo Externo:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.publicoAlvoExterno}" /></td>
					<td colspan="2" />
				</tr>

				<tr>
					<th>Público Estimado Interno:</th>
					<td>
					   <h:outputText value="#{atividadeExtensao.atividadeSelecionada.publicoEstimado} pessoas" rendered="#{atividadeExtensao.atividadeSelecionada.publicoEstimado > 0 }" /> 
                       <h:outputText value="Não informado" rendered="#{empty atividadeExtensao.atividadeSelecionada.publicoEstimado || atividadeExtensao.atividadeSelecionada.publicoEstimado == 0}" />
					</td>
					<th>Público Estimado Externo:</th>
					<td>
					   <h:outputText value="#{atividadeExtensao.atividadeSelecionada.publicoExterno} pessoas" rendered="#{atividadeExtensao.atividadeSelecionada.publicoExterno > 0 }" /> 
                       <h:outputText value="Não informado" rendered="#{empty atividadeExtensao.atividadeSelecionada.publicoExterno || atividadeExtensao.atividadeSelecionada.publicoExterno == 0}" />
					</td>
					<th>Público Real Atingido:</th>
					<td>
                        <h:outputText value="#{atividadeExtensao.atividadeSelecionada.publicoAtendido} pessoas" rendered="#{atividadeExtensao.atividadeSelecionada.publicoAtendido > 0 }" /> 
                        <h:outputText value="Não informado" rendered="#{empty atividadeExtensao.atividadeSelecionada.publicoAtendido || atividadeExtensao.atividadeSelecionada.publicoAtendido == 0}" /> 
    					<ufrn:help img="/img/prodocente/information.png">Este campo é atualizado após o envio dos primeiros 
							relatórios desta ação</ufrn:help>
					</td>
				</tr>

				<%-- 	DADOS ESPECIFICOS DE CURSO/EVENTO	--%>
				<c:if test="${(atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == CURSO) 
					or (atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == EVENTO)}">

					<!-- AÇÃO EH UM CURSO -->
					<c:if test="${atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == CURSO}">
						<tr>
							<th>Modalidade do Curso:</th>
							<td colspan="2">
								<h:outputText 
										value="#{atividadeExtensao.atividadeSelecionada.cursoEventoExtensao.modalidadeEducacao.descricao}" />
							</td>
							<th>Tipo do Curso:</th>
							<td colspan="2">
								<h:outputText value="#{atividadeExtensao.atividadeSelecionada.cursoEventoExtensao.tipoCursoEvento.descricao}" />
							</td>
						</tr>
					</c:if>

					<!-- AÇÃO EH UM EVENTO -->
					<c:if test="${atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == EVENTO}">
						<tr>
							<th>Tipo do Evento:</th>
							<td colspan="5">
								<h:outputText value="#{atividadeExtensao.atividadeSelecionada.cursoEventoExtensao.tipoCursoEvento.descricao}" />
							</td>
						</tr>
					</c:if>

					<tr>
						<th>Carga Horária:</th>
						<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.cursoEventoExtensao.cargaHoraria}" />	horas</td>
						<th>Previsão de Nº de Vagas:</th>
						<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.cursoEventoExtensao.numeroVagas}" /></td>
						<th></th>
						<td></td>
					</tr>

				</c:if>

				<%-- 	DADOS ESPECIFICOS DE PRODUTO (tipoAtividade = 4)	--%>
				<c:if test="${atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == PRODUTO}">
					<tr>
						<th>Tipo do Produto:</th>
						<td> <h:outputText value="#{atividadeExtensao.atividadeSelecionada.produtoExtensao.tipoProduto.descricao}" /></td>
						<th>Tiragem:</th>
						<td colspan="3">
							<h:outputText value="#{atividadeExtensao.atividadeSelecionada.produtoExtensao.tiragem}" /> exemplares</td>
					</tr>
				</c:if>

				<tr>
					<th>Situação:</th>
					<td colspan="5"><h:outputText value="#{atividadeExtensao.atividadeSelecionada.situacaoProjeto.descricao}" /></td>
				</tr>
				
				<tr>
					<td colspan="6" class="subFormulario">Município Realização</td>
				</tr>
				
				<tr>
					<td colspan="6">
							<table class="listagem" width="100%" id="tbLocaisRealizacao">
								<thead>
									<tr>
										<th style="text-align: left">Estado</th>
										<th style="text-align: left">Município</th>
										<th style="text-align: left">Bairro</th>
										<th style="text-align: left">Espaço Realização</th>
									</tr>
								</thead>

								<tbody>
									<tr>
										<td>${ local.municipio.unidadeFederativa.descricao }</td>
										<td>${ local.municipio.nome }</td>
										<td>${ local.bairro }</td>
										<td>${ local.descricao }</td>
									</tr>
								
									<c:forEach items="#{atividadeExtensao.atividadeSelecionada.locaisRealizacao}" var="local" varStatus="status">
										<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
											<td><h:outputText value="#{local.municipio.unidadeFederativa.descricao}" /></td>
											<td><h:outputText value="#{local.municipio.nome}" /></td>
											<td><h:outputText value="#{local.bairro}" /></td>
											<td><h:outputText value="#{local.descricao}" /></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							
						<h:outputText value="<font color='red'>Locais de realização ainda não definidos.</font>"
								rendered="#{empty atividadeExtensao.atividadeSelecionada.locaisRealizacao}" escape="false" />
					</td>
				</tr>				
				
				<%-- Fim DADOS GERAIS, DE TODOS OS TIPOS DE AÇÂO --%>
				
				<tr>
					<td colspan="6" class="subFormulario">Detalhes da Ação</td>
				</tr>

				<c:if test="${atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id != PRODUTO}">
					<tr>
						<th>Resumo:</th>
						<td colspan="5" style="text-align: justify;">
							<c:if test="${(atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id != CURSO) 
									and (atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id != EVENTO)}">
								<h:outputText value="#{atividadeExtensao.atividadeSelecionada.resumo}" escape="false" />
							</c:if> 
							<c:if test="${(atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == CURSO) 
									or (atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == EVENTO)}">
								<h:outputText value="#{atividadeExtensao.atividadeSelecionada.cursoEventoExtensao.resumo}" escape="false" />
							</c:if>
						</td>
					</tr>
				</c:if>

				<c:if test="${(atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == PROJETO) 
					or (atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == PRODUTO) }">
					<tr>
						<th>Justificativa:</th>
						<td colspan="5" style="text-align: justify;">
							<h:outputText value="#{atividadeExtensao.atividadeSelecionada.justificativa}" escape="false" />
						</td>
					</tr>
				</c:if>

				<c:if test="${(atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == PROJETO) }">
					<tr>
						<th>Metodologia:</th>
						<td colspan="5" style="text-align: justify;">
							<h:outputText value="#{atividadeExtensao.atividadeSelecionada.metodologia}" escape="false" />
						</td>
					</tr>
					<tr>
						<th>Referências:</th>
						<td colspan="5" style="text-align: justify;">
							<h:outputText value="#{atividadeExtensao.atividadeSelecionada.referencias}" escape="false" />
						</td>
					</tr>
				</c:if>

				<%-- 	DADOS ESPECIFICOS DE CURSO/EVENTO	--%>
				<c:if test="${(atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == CURSO) 
					or (atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == EVENTO)}">
					<tr>
						<th>Programação:</th>
						<td colspan="5" style="text-align: justify;">
							<h:outputText value="#{atividadeExtensao.atividadeSelecionada.cursoEventoExtensao.programacao}" escape="false" />
						</td>
					</tr>
				</c:if>
				
				<tr>
					<td colspan="6" class="subFormulario">Contato</td>
				</tr>
				<tr>
					<th>Coordenação:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.coordenacao.servidor.pessoa.nome}" /></td>
					<th>E-mail:</th>
					<td>
							<a href="mailto:<h:outputText value='#{atividadeExtensao.atividadeSelecionada.coordenacao.pessoa.email}'/>">
								<h:outputText value="#{atividadeExtensao.atividadeSelecionada.coordenacao.pessoa.email}"
										rendered="#{not empty atividadeExtensao.atividadeSelecionada.coordenacao.pessoa}" />
							</a>
					</td>
					<th>Telefone:</th>
					<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.coordenacao.pessoa.telefone}"
								rendered="#{not empty atividadeExtensao.atividadeSelecionada.coordenacao.pessoa && (acesso.extensao)}" />
					</td>
				</tr>

				<%-- 	LISTAS GERAIS, DE Ação 	--%>
				<tr>
					<td colspan="6" class="subFormulario">Membros da Equipe</td>
				</tr>
				<tr>
					<td colspan="6">
							<table class="listagem" width="100%" id="tbEquipe">
								<thead>
									<tr>
										<th style="text-align: left">Nome</th>
										<th style="text-align: left">Categoria</th>
										<th style="text-align: left">Função</th>
										<th style="text-align: left">Departamento</th>
										<th style="text-align: center">Início</th>
										<th style="text-align: center">Fim</th>
									</tr>
								</thead>

								<tbody>
									<c:forEach items="#{atividadeExtensao.atividadeSelecionada.membrosEquipe}"
											var="membro" varStatus="status">
										<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
											<td><h:outputText value="#{membro.pessoa.nome}" /></td>
											<td><h:outputText value="#{membro.categoriaMembro.descricao}" /></td>
											<td><h:outputText value="<font color='red'>" rendered="#{membro.coordenadorAtivo}" escape="false" />
												<h:outputText value="#{membro.funcaoMembro.descricao}" rendered="#{not empty membro.pessoa}" />
												<h:outputText value="</font>" rendered="#{membro.coordenadorAtivo}"	escape="false" />
											</td>
											<td>
												<h:outputText value="#{membro.servidor.unidade.sigla}" rendered="#{not empty membro.servidor}" />
											</td>
											<td style="text-align: center"><h:outputText value="#{membro.dataInicio}" /></td>
											<td style="text-align: center"><h:outputText value="#{membro.dataFim}" /></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
							
						<h:outputText value="<font color='red'>Membros da equipe ainda não foram cadastrados.</font>"
								rendered="#{empty atividadeExtensao.atividadeSelecionada.membrosEquipe}" escape="false" />
					</td>
				</tr>
				
				<tr>
					<td colspan="6" class="subFormulario">Participantes da Ação de Extensão</td>
				</tr>
				<tr>
					<td colspan="6" align="center">
						<h:commandLink action="#{atividadeExtensao.viewParticipantes}" style="border: 0;" >
							<f:param name="id" value="#{atividadeExtensao.atividadeSelecionada.id}" />
							<f:attribute name="acao" value="#{atividadeExtensao.atividadeSelecionada}" />
							Clique aqui para visualizar os participantes desta ação de extensão
						</h:commandLink>
					</td>
				</tr>

				<tr>
					<td colspan="6" class="subFormulario">Discentes com Planos de Trabalho</td>
				</tr>
				<tr>
					<td colspan="7">
						<table class="listagem" width="100%" id="tbDis">
									<thead>
										<tr>
											<th style="text-align: left">Nome</th>
											<th style="text-align: left">Vínculo</th>
											<th style="text-align: left">Situação</th>
											<th style="text-align: center">Início</th>
											<th style="text-align: center">Fim</th>
											<th></th>
										</tr>
									</thead>
		
									<tbody>
										<c:forEach items="#{atividadeExtensao.atividadeSelecionada.discentesSelecionados}" var="de" 
												varStatus="status">
											<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
												<td><h:outputText value="#{de.discente.matriculaNome}" /></td>
												<td><h:outputText value="#{de.tipoVinculo.descricao}" /></td>
												<td><h:outputText value="#{de.situacaoDiscenteExtensao.descricao}" /></td>
												<td style="text-align: center"><h:outputText value="#{de.dataInicio}" /></td>
												<td style="text-align: center"><h:outputText value="#{de.dataFim}" /></td>
												<td width="2%" >
													<h:commandLink action="#{planoTrabalhoExtensao.view}" style="border: 0;" title="Visualizar Plano de Trabalho" rendered="#{not empty de.planoTrabalhoExtensao}">
									       				<f:param name="id" value="#{de.planoTrabalhoExtensao.id}"/>
							               				<h:graphicImage url="/img/view2.gif" />
													</h:commandLink>
											</td>
											</tr>
										</c:forEach>
								</tbody>
						</table>
										
						<h:outputText value="<center><font color='red'>Discentes não informados</font></center>"
								rendered="#{empty atividadeExtensao.atividadeSelecionada.discentesSelecionados}" escape="false" />
					</td>
				</tr>

				<tr>
					<td colspan="6" class="subFormulario">Ações Vinculadas ao 
						<h:outputText value="#{atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.descricao}" />
					</td>
				</tr>
				<tr>
					<td colspan="7">
						<table class="listagem" width="100%" id="tbAtividades">
							<thead>
								<tr>
									<th style="text-align: left">Código - Título</th>
									<th style="text-align: left">Tipo</th>
									<th></th>
								</tr>
							</thead>
	
							<tbody>
								<c:forEach items="#{atividadeExtensao.atividadeSelecionada.atividades}" var="atividade" varStatus="status">
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<td><h:outputText value="#{atividade.codigoTitulo}" /></td>
										<td><h:outputText value="#{atividade.tipoAtividadeExtensao.descricao}" /></td>
										<td><h:commandLink  title="Visualizar Ação de Extensão Vinculada" action="#{ atividadeExtensao.view }" id="btView">
								      <f:param name="id" value="#{atividade.id}"/>
								      <h:graphicImage url="/img/buscar.gif" />
								</h:commandLink></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
						<c:if test="${empty atividadeExtensao.atividadeSelecionada.atividades}">
							<center><font color="red">Não há ações vinculadas</font></center>
						</c:if>
					</td>
				</tr>

				<tr>
					<td colspan="6" class="subFormulario">Ações das quais o 
						<h:outputText value="#{atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.descricao}" /> faz parte</td>
				</tr>
				<tr>
					<td colspan="6">
						<c:if test="${not empty atividadeExtensao.atividadeSelecionada.atividadesPai}">
							<table class="listagem" width="100%" id="tbAtividadesPai">
								<thead>
									<tr>
										<th style="text-align: left">Código - Título</th>
										<th style="text-align: left" width="10%">Tipo</th>
									</tr>
								</thead>
	
								<tbody>
									<c:forEach items="#{atividadeExtensao.atividadeSelecionada.atividadesPai}" var="atividade" 
											varStatus="status">
										<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
											<td><h:outputText value="#{atividade.codigoTitulo}" /></td>
											<td><h:outputText value="#{atividade.tipoAtividadeExtensao.descricao}" /></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</c:if>
						
						<c:if test="${atividadeExtensao.atividadeSelecionada.projetoAssociado}">
							<table align="center" width="100%" class="listagem"	id="tbProjetoAssociado">
								<thead>
									<tr>
										<th style="text-align: left">Título</th>
										<th style="text-align: left">Tipo</th>
										<th />
									</tr>
								</thead>
								<tbody>
									<tr>
										<td><h:outputText value="#{atividadeExtensao.atividadeSelecionada.projeto.titulo}" /></td>
										<td>AÇÃO ACADÊMICA ASSOCIADA</td>
										<td><h:commandLink title="Visualizar" action="#{ projetoBase.view }" immediate="true">
												<f:param name="id" value="#{atividadeExtensao.atividadeSelecionada.projeto.id}" />
												<h:graphicImage value="/img/view.gif" style="overflow: visible;" />
											</h:commandLink>
										</td>
									</tr>
								</tbody>
							</table>
						</c:if> 
		
						<c:if test="${(empty atividadeExtensao.atividadeSelecionada.atividadesPai) 
								and (not atividadeExtensao.atividadeSelecionada.projetoAssociado)}">
							<center>
								<font color="red">Esta ação não faz parte de outros projetos ou programas de extensão</font>
							</center>
						</c:if>
					</td>
				</tr>

				<%-- 	LISTAS EXPECIFICAS DE PROJETO (tipoAtividade = 1)	--%>
				<c:if test="${atividadeExtensao.atividadeSelecionada.tipoAtividadeExtensao.id == PROJETO}">

					<!-- OBJETIVOS / RESULTADOS ESPERADOS -->
					<c:if test="${not empty atividadeExtensao.atividadeSelecionada.objetivo}">
						<tr>
							<td colspan="6" class="subFormulario">Objetivos / Resultados Esperados</td>
						</tr>
						<tr>
							<td colspan="6">
								<table class="listagem" width="100%" id="tbObjetivos">
									<thead>
										<tr>
											<th style="text-align: left">Objetivos</th>
											<th style="text-align: left">Quantitativos</th>
											<th style="text-align: left">Qualitativos</th>
										</tr>
									</thead>
		
									<tbody>
										<c:forEach items="#{atividadeExtensao.atividadeSelecionada.objetivo}" var="objetivo" 
												varStatus="status">
											<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
												<td style="text-align: justify;"><h:outputText value="#{objetivo.objetivo}" escape="false" /></td>
												<td style="text-align: justify;"><h:outputText value="#{objetivo.quantitativos}" escape="false" /></td>
												<td style="text-align: justify;"><h:outputText value="#{objetivo.qualitativos}" escape="false" /></td>
											</tr>
										</c:forEach>
									</tbody>
								</table>
							</td>
						</tr>
					</c:if>

					<!-- CRONOGRAMA -->
					<c:if test="${not empty atividadeExtensao.atividadeSelecionada.objetivo}">
						<tr>
							<td colspan="6" class="subFormulario">Cronograma</td>
						</tr>
						<tr>
							<td colspan="6">
							<table class="formulario" width="100%">
								<thead>
									<tr>
										<th width="70%" style="text-align: left">Descrição das ativadades desenvolvidas</th>
										<th style="text-align: center">Período</th>
									</tr>
								</thead>

								<tbody>
									<c:forEach items="#{atividadeExtensao.atividadeSelecionada.objetivo}"
											var="objet" varStatus="status1">
										<c:forEach items="#{objet.atividadesPrincipais}" var="ativ"	varStatus="status2">
											<tr class="${(status1.index + status2.index ) % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
												<td style="text-align: justify;"><h:outputText value="#{ativ.descricao}"	escape="false" /></td>
												<td style="text-align: center">
													<fmt:formatDate value="${ativ.dataInicio}" pattern="dd/MM/yyyy" /> 
														<c:if test="${not empty ativ.dataFim}">
															&nbsp; a &nbsp; 
														</c:if> 
													<fmt:formatDate value="${ativ.dataFim}"	pattern="dd/MM/yyyy" />
												</td>
											</tr>
										</c:forEach>
									</c:forEach>
								</tbody>
							</table>
							</td>
						</tr>
					</c:if>
				</c:if>

				<!-- ORÇAMENTO DETALHADO -->
				<c:if test="${not empty atividadeExtensao.atividadeSelecionada.orcamentosDetalhados}">
					<tr>
						<td colspan="6" class="subFormulario">Orçamento Detalhado</td>
					</tr>
					<tr>
						<td colspan="6">
						<table class="listagem">
							<thead>
								<tr>
									<th style="text-align: left">Descrição</th>
									<th width="15%">Valor Unitário</th>
									<th width="10%">Quant.</th>
									<th width="15%">Valor Total</th>
								</tr>
							</thead>

							<tbody>

								<c:if test="${not empty atividadeExtensao.tabelaOrcamentaria}">
									<c:set value="${atividadeExtensao.tabelaOrcamentaria}" var="tabelaOrcamentaria" />
									<c:forEach items="#{tabelaOrcamentaria}" var="tabelaOrc">

										<tr style="background: #EFF0FF; font-weight: bold; padding: 2px 0 2px 5px;">
											<td colspan="5">${ tabelaOrc.key.descricao }</td>
										</tr>
										<c:set value="#{tabelaOrc.value.orcamentos}" var="orcamentos" />
										<c:forEach items="#{orcamentos}" var="orcamento" varStatus="status">
											<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
												<td style="padding-left: 20px">${orcamento.discriminacao}</td>
												<td align="right">
													<fmt:formatNumber currencySymbol="R$  " value="${orcamento.valorUnitario}"
															type="currency" /></td>
												<td align="right">${orcamento.quantidade}</td>
												<td align="right">
													<fmt:formatNumber currencySymbol="R$  " value="${orcamento.valorTotal}" type="currency" />
												</td>
											</tr>
										</c:forEach>

										<tr style="background: #EEE; padding: 2px 0 2px 5px;">
											<td colspan="2">SUB-TOTAL (${ tabelaOrc.key.descricao})</td>
											<td align="right">${ tabelaOrc.value.quantidadeTotal }</td>
											<td align="right"><fmt:formatNumber currencySymbol="R$  " type="currency"
													value="${tabelaOrc.value.valorTotalRubrica}" /></td>
										</tr>

										<tr>
											<td colspan="5">&nbsp;</td>
										</tr>

									</c:forEach>
								</c:if>

								<c:if test="${empty atividadeExtensao.atividadeSelecionada.orcamentosDetalhados}">
									<tr>
										<td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font></td>
									</tr>
								</c:if>

							</tbody>
						</table>
						</td>
					</tr>
				</c:if>

				<!-- ORÇAMENTO CONSOLIDADO -->
				<tr>
						<td colspan="6" class="subFormulario">Consolidação do Orçamento Solicitado</td>
				</tr>
				<tr>
						<td colspan="6">
							<table class="listagem" width="100%" id="dt">
								<thead>
									<tr>
										<th style="text-align: left">Descrição</th>
										<th>FAEx (Interno)</th>
										<th>Funpec</th>
										<th>Outros (Externo)</th>
										<th>Total Rubrica</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="#{atividadeExtensao.atividadeSelecionada.orcamentosConsolidados}" 
											var="consolidacao" varStatus="status">
										<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
											<td><h:outputText value="#{consolidacao.elementoDespesa.descricao}" /></td>
											<td align="right">
												<h:outputText value="#{consolidacao.fundo}">
													<f:convertNumber pattern="R$ #,##0.00" />
												</h:outputText>
											</td>
											<td align="right">
												<h:outputText value="#{consolidacao.fundacao}">
													<f:convertNumber pattern="R$ #,##0.00" />
												</h:outputText>
											</td>
											<td align="right">
												<h:outputText value="#{consolidacao.outros}">
													<f:convertNumber pattern="R$ #,##0.00" />
												</h:outputText>
											</td>
											<td align="right">
												<h:outputText value="#{consolidacao.totalConsolidado}">
													<f:convertNumber pattern="R$ #,##0.00" />
												</h:outputText>
											</td>
										</tr>
									</c:forEach>
									<c:if test="${empty atividadeExtensao.atividadeSelecionada.orcamentosConsolidados}">
                                        <tr>
                                            <td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font></td>
                                        </tr>
                                    </c:if>									
								</tbody>
							</table>
						</td>
				</tr>


                <!-- ORÇAMENTO APROVADO  -->
                <tr>
                        <td colspan="6" class="subFormulario">Orçamento Aprovado</td>
                </tr>
                <tr>
                        <td colspan="6">
                            <table class="listagem" width="100%" id="dt">
                                <thead>
                                    <tr>
                                        <th style="text-align: left">Descrição</th>
                                        <th>FAEx (Interno)</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="#{atividadeExtensao.atividadeSelecionada.orcamentosConsolidados}" 
                                            var="consolidacao" varStatus="status">
                                        <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
                                            <td><h:outputText value="#{consolidacao.elementoDespesa.descricao}" /></td>
                                            <td align="right">
                                                <h:outputText value="#{consolidacao.fundoConcedido}">
                                                    <f:convertNumber pattern="R$ #,##0.00" />
                                                </h:outputText>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty atividadeExtensao.atividadeSelecionada.orcamentosConsolidados}">
                                        <tr>
                                            <td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font></td>
                                        </tr>
                                    </c:if>                                 
                                </tbody>
                            </table>
                        </td>
                </tr>
                

				<c:if test="${not empty atividadeExtensao.atividadeSelecionada.arquivos}">
					<tr>
						<td colspan="6" class="subFormulario">Arquivos</td>
					</tr>
					<tr>
						<td colspan="6">
							<table class="listagem" width="100%" id="tbArquivo">
								<thead>
									<tr>
										<th style="text-align: left">Descrição Arquivo</th>
										<th />
									</tr>
								</thead>
								<tbody>
									<c:forEach items="#{atividadeExtensao.atividadeSelecionada.arquivos}" var="arquivo" varStatus="status">
										<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
											<td><h:outputText value="#{arquivo.descricao}" /></td>
											<td width="5%">
												<h:commandLink title="Visualizar Arquivo" action="#{atividadeExtensao.viewArquivo}" 
														immediate="true" id="visualizarArquivo">
													<f:param name="idArquivo" value="#{arquivo.idArquivo}" />
													<h:graphicImage url="/img/view.gif" />
												</h:commandLink>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</td>
					</tr>
				</c:if>

				<tr>
					<td colspan="6" class="subFormulario">Lista de Fotos</td>
				</tr>
				<tr>
					<td colspan="6">
						<table class="listagem" width="100%" id="dtFotos">
							<thead>
								<tr>
									<th style="text-align: left">Foto</th>
									<th style="text-align: left">Descrição</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="#{atividadeExtensao.atividadeSelecionada.fotos}" var="foto" varStatus="status">
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<td>
											<h:outputLink id="link_verfoto_original_" title="Click para ampliar"
													value="/sigaa/verFoto?idFoto=#{foto.idFotoOriginal}&key=#{sf:generateArquivoKey(foto.idFotoOriginal)}">
												<h:graphicImage	url="/verFoto?idFoto=#{foto.idFotoMini}&key=#{sf:generateArquivoKey(foto.idFotoMini)}"
														width="70" height="70" />
											</h:outputLink>
										</td>
										<td><h:outputText value="#{foto.descricao}" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					
						<c:if test="${empty atividadeExtensao.atividadeSelecionada.fotos}">
							<tr>
								<td colspan="6" align="center"><font color="red">Não há fotos cadastradas para esta ação</font></td>
							</tr>
						</c:if>
					</td>
				</tr>

				<tr>
					<td colspan="6" class="subFormulario">Lista de Departamentos Envolvidos na Autorização da Proposta</td>
				</tr>
				<tr>
					<td colspan="6">
						<c:set var="flag" value="${true}" />
						<table class="listagem" width="100%" id="tbAutorizacao">
							<thead>
								<tr>
									<th style="text-align: left">Autorização</th>
									<th style="text-align: center">Data/Hora Análise</th>
									<th style="text-align: center">Data da Reunião</th>
									<th style="text-align: center">Autorizado</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="#{atividadeExtensao.atividadeSelecionada.autorizacoesDepartamentos}" var="autorizacao">
									<c:if test="${autorizacao.ativo}">
										<tr class="${flag ? 'linhaPar' : 'linhaImpar'}">
											<c:set var="flag" value="${flag ? false : true}" />
											<td><h:outputText value="#{autorizacao.unidade.nome}" /></td>
											<td align="center">
												<h:outputText value="#{autorizacao.dataAutorizacao}">
													<f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss" />
												</h:outputText>
											</td>
											<td align="center"> <fmt:formatDate value="${autorizacao.dataReuniao}" pattern="dd/MM/yyy" var="dataReuniao"/>${dataReuniao} <c:if test="${empty autorizacao.dataReuniao}">-</c:if> </td>
											<td align="center"><h:outputText value="#{(autorizacao.autorizado == null) 
														? 'NÃO ANALISADO' :(autorizacao.autorizado ? 'SIM': 'NÃO')}" /></td>
										</tr>
									</c:if>
								</c:forEach>
							</tbody>
						</table>
						
						<c:if test="${empty atividadeExtensao.atividadeSelecionada.autorizacoesDepartamentos}">
                            <tr>
                                <td colspan="6" align="center"><font color="red">Este projeto ainda não foi enviado para aprovação dos departamentos envolvidos.</font></td>
                            </tr>
                        </c:if>
						
					</td>
				</tr>
				
				
				<tr>
					<td colspan="6" class="subFormulario">Mini Atividades</td>
				</tr>
				
				<tr>
					<td colspan="6">
						<table class="listagem" width="100%" id="dtFotos">
							<thead>
								<tr>
									<th style="text-align: left;   width: 30%;">Título</th>
									<th style="text-align: left;   width: 10%;">Tipo</th>
									<th style="text-align: center; width: 10%;">Data de Início</th>
									<th style="text-align: center; width: 10%;">Data de Términio</th>
									<th style="text-align: left;   width: 20%;">Local</th>
									<th style="text-align: left;   width: 20%;">Horário</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="#{atividadeExtensao.atividadeSelecionada.subAtividadesExtensao}" var="subAtividade" varStatus="status">
									<c:if test="${subAtividade.ativo}">
										<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
											<td><h:outputText value="#{subAtividade.titulo}" /></td>
											<td><h:outputText value="#{subAtividade.tipoSubAtividadeExtensao.descricao}" /></td>
											<td style="text-align: center;">
											<h:outputText value="#{subAtividade.inicio}" >
												<f:convertDateTime pattern="dd/MM/yyyy"/>
											</h:outputText>
											</td>
											<td  style="text-align: center;">
												<h:outputText value="#{subAtividade.fim}" >
													<f:convertDateTime pattern="dd/MM/yyyy"/>
												</h:outputText>
											</td>
											<td><h:outputText value="#{subAtividade.local}" /></td>
											<td><h:outputText value="#{subAtividade.horario}" /></td>
										</tr>
									</c:if>
								</c:forEach>
							</tbody>
						</table>
					</td>
				</tr>
				
				
				<tr>
					<td colspan="6" class="subFormulario">Histórico do Projeto</td>
				</tr>
				
				<tr>
					<td colspan="6">
						<table class="listagem" width="100%" id="dtFotos">
							<thead>
								<tr>
									<th style="text-align: center">Data/Hora</th>
									<th style="text-align: left">Situação</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="#{atividadeExtensao.atividadeSelecionada.projeto.historicoSituacao}" var="historico" varStatus="status">
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										
										<td align="center">
											<h:outputText value="#{historico.data}">
												<f:convertDateTime pattern="dd/MM/yyyy HH:mm:ss"/>
											</h:outputText>
										</td>
										<td><h:outputText value="#{historico.projeto.situacaoProjeto.descricao}" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					
						<c:if test="${empty atividadeExtensao.atividadeSelecionada.projeto.historicoSituacao}">
							<tr>
								<td colspan="6" align="center"><font color="red">Não há registros sobre o histórico do projeto</font></td>
							</tr>
						</c:if>
					</td>
				</tr>
				
				
				
				
				
				
			</tbody>

			<tfoot>
				<tr>
					<td colspan="6">
						<center><input type="button" value="<< Voltar" onclick="javascript:history.go(-1)" /></center>
					</td>
				</tr>
			</tfoot>
		</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>