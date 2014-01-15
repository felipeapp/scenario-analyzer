<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
.scrollbar {
  	margin-left: 155px;
  	
	width: 98%;
	overflow:auto;
}
</style>

<f:view>

	<h:form id="formRelatorioProjetos">
		
		<h3 class="tituloTabelaRelatorio"> RELATÓRIO DE CURSOS E EVENTOS DE EXTENSÃO </h3>
		<table class="tabelaRelatorio" width="100%">
			<tr>
				<th width="30%">Código:</th>
				<td><h:outputText value="#{relatorioAcaoExtensao.obj.atividade.codigo}"/></td>
			</tr>

			<tr>
				<th>Título:</th>
				<td><h:outputText value="#{relatorioAcaoExtensao.obj.atividade.titulo}"/></td>
			</tr>
			
			<tr>
				<th>Tipo de ação:</th>
				<td><h:outputText value="#{relatorioAcaoExtensao.obj.atividade.tipoAtividadeExtensao.descricao}"/></td>
			</tr>
			
			
			<tr>
				<th><b> Área Temática:</b></th>
				<td><h:outputText	value="#{relatorioAcaoExtensao.obj.atividade.areaTematicaPrincipal.descricao}"/></td>
			</tr>
			
			<tr>
				<th>Coordenador(a):</th>
				<td><h:outputText value="#{relatorioAcaoExtensao.obj.atividade.coordenacao.pessoa.nome}"/></td>
			</tr>

			<tr>
				<th>Tipo de Relatório:</th>
				<td><h:outputText value="#{relatorioAcaoExtensao.obj.tipoRelatorio.descricao}"/></td>
			</tr>

			<tr>
				<th>Valor da taxa de matrícula:</th>
				<td>R$ 
					<h:outputText value="#{relatorioAcaoExtensao.obj.taxaMatricula}">
						<f:convertNumber pattern="#,##0.00"/>
					</h:outputText>
				</td>
			</tr>
		
			<tr>
				<th>Valor arrecadado:</th>
				<td>R$ 
					<h:outputText value="#{relatorioAcaoExtensao.obj.totalArrecadado}">
						<f:convertNumber pattern="#,##0.00"/>
					</h:outputText>
				</td>
			</tr>

			<tr>
				<th>Público Estimado:</th>
				<td><h:outputText value="#{relatorioAcaoExtensao.obj.atividade.publicoEstimado}"/> pessoas</td>
			</tr>

			<tr>
				<th>Público Real Atingido:</th>
				<td><h:outputText value="#{relatorioAcaoExtensao.obj.publicoRealAtingido}"/> pessoas</td>
			</tr>
		
			<tr>
				<th>Situação do Relatório:</th>
				<td>
					<h:outputText value="Enviado em " rendered="#{relatorioAcaoExtensao.obj.dataEnvio != null}" />
					<fmt:formatDate value="${relatorioAcaoExtensao.obj.dataEnvio}" pattern="dd/MM/yyyy HH:mm:ss" />
					<h:outputText value="<font color=red>CADASTRO EM ANDAMENTO<font>" escape="false" 
							rendered="#{relatorioAcaoExtensao.obj.dataEnvio == null}" />
				</td>
			</tr>
		
			<tr>
				<th>Esta ação foi realizada:</th>
				<td><h:outputText value="#{relatorioAcaoExtensao.obj.acaoRealizada ? 'SIM' : (relatorioAcaoExtensao.obj.acaoRealizada == null ? '-' : 'NÃO')}" id="acaoFoiRealizada" /></td>	
			</tr>
		
		
			<tr>
				<td colspan="2" class="subFormulario"> Detalhamento das atividades desenvolvidas:</td>
			</tr>	

			<c:if test="${relatorioAcaoExtensao.obj.acaoRealizada != null && !relatorioAcaoExtensao.obj.acaoRealizada }">
				<tr>
					<td colspan="2" style="text-align: justify;"><b>Motivo da não realização desta ação:</b><br />
						<h:outputText value="#{relatorioAcaoExtensao.obj.motivoAcaoNaoRealizada}" id="motivoNaoRealizacao" />
					</td>	
				</tr>
			</c:if>

			
			<c:choose>
			
				<c:when test="${ relatorioAcaoExtensao.novoFormulario }">

					<tr>
						<td colspan="2"><b> Apresentação em Eventos Científicos: </b>
							<h:outputText value="#{relatorioAcaoExtensao.obj.apresentacaoEventoCientifico}"/> apresentações.
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resumo sobre a apresentação: </b> <br />
							<p style="padding-left: 15px;"><h:outputText value="#{relatorioAcaoExtensao.obj.observacaoApresentacao}"/></p>
						</td>	
					</tr>
			
					<tr>
						<td colspan="2"><b> Artigos Científicos produzidos a partir da ação de extensão: </b>
							<h:outputText value="#{relatorioAcaoExtensao.obj.artigosEventoCientifico}" /> artigos
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resumo sobre o Artigo: </b> <br />
							<p style="padding-left: 15px;"><h:outputText value="#{relatorioAcaoExtensao.obj.observacaoArtigo}" /></p>
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" ><b> Outras produções geradas a partir da ação de Extensão: </b>
							<h:outputText value="#{relatorioAcaoExtensao.obj.producoesCientifico}" /> produções
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resumo sobre a Produção: </b> <br />
							<p style="padding-left: 15px;"><h:outputText value="#{relatorioAcaoExtensao.obj.observacaoProducao}" /></p>
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" class="subFormulario">Informações do Projeto</td>
					</tr>
			
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Dificuldades Encontradas: </b> <br />
							<p style="padding-left: 15px;"><h:outputText value="#{relatorioAcaoExtensao.obj.dificuldadesEncontradas}" /></p>
						</td>	
					</tr>
			
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Observações Gerais: </b> <br />
							<p style="padding-left: 15px;"><h:outputText value="#{relatorioAcaoExtensao.obj.observacoesGerais}" /></p>
						</td>	
					</tr>
								
				</c:when>
				<c:otherwise>
					<tr>
						<td colspan="2" style="text-align: justify;"><b>Atividades Realizadas:</b><br />
							<h:outputText value="#{relatorioAcaoExtensao.obj.atividadesRealizadas}" id="atividades" />
						</td>	
					</tr>
		
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resultados Obtidos: Qualitativos.</b><br />
							<h:outputText  value="#{relatorioAcaoExtensao.obj.resultadosQualitativos}" id="qualitativos" />
						</td>	
					</tr>
		
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Resultados Obtidos: Quantitativos.</b><br />
							<h:outputText value="#{relatorioAcaoExtensao.obj.resultadosQuantitativos}" id="quantitativos" />
						</td>	
					</tr>
		
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Dificuldades Encontradas:</b><br />
							<h:outputText value="#{relatorioAcaoExtensao.obj.dificuldadesEncontradas}" id="dificuldades" />
						</td>	
					</tr>
				
					<tr>
						<td colspan="2" style="text-align: justify;"><b> Ajustes Realizados Durante a Execução da Ação:</b><br />
							<h:outputText value="#{relatorioAcaoExtensao.obj.ajustesDuranteExecucao}" id="ajustes" />
						</td>	
					</tr>
				</c:otherwise>
			</c:choose>

			<tr>
				<td colspan="2" class="subFormulario"> Membros da Equipe </td>
			</tr>	
		
			<c:if test="${not empty relatorioAcaoExtensao.obj.atividade.membrosEquipe}">
				<tr>
					<td colspan="2">
						<table class="listagem" width="100%" id="tbEquipe">
							<thead>
								<tr>
									<th>Nome</th>
									<th>Categoria</th>
									<th>Função</th>
									<th style="text-align: center">Início</th>
									<th style="text-align: center">Fim</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="#{relatorioAcaoExtensao.obj.atividade.membrosEquipe}" var="membro" varStatus="status">
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
										<td><h:outputText value="#{membro.pessoa.nome}" /></td>
										<td><h:outputText value="#{membro.categoriaMembro.descricao}" /></td>
										<td><h:outputText value="<font color='red'>" rendered="#{membro.funcaoMembro.id == COORDENADOR}"  
													escape="false" />
											<h:outputText value="#{membro.funcaoMembro.descricao}" rendered="#{not empty membro.pessoa}" />
											<h:outputText value="</font>" rendered="#{membro.funcaoMembro.id == COORDENADOR}" escape="false" />
										</td>
										<td style="text-align: center"><h:outputText value="#{membro.dataInicio}" /></td>
										<td style="text-align: center"><h:outputText value="#{membro.dataFim}" /></td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</td>
				</tr>
			</c:if>
				
			<tr>
				<td colspan="2" class="subFormulario"> Planos de Trabalho Cadastrados </td>
			</tr>	
		
			<c:if test="${not empty relatorioAcaoExtensao.obj.atividade.planosTrabalho}">
					<tr>
						<td colspan="2">
							<table class="listagem" width="100%" id="plano_trabalho">
								<thead>
									<tr>
										<th>Nome</th>
										<th>Vínculo</th>
										<th style="text-align: center">Início</th>
										<th style="text-align: center">Fim</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach items="#{relatorioAcaoExtensao.obj.atividade.planosTrabalho}" var="plano" varStatus="status">
										<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
											<td><h:outputText value="#{plano.discenteExtensao.discente.pessoa.nome}" /></td>
											<td><h:outputText value="#{plano.discenteExtensao.tipoVinculo.descricao}" /></td>
											<td style="text-align: center"><h:outputText value="#{plano.dataInicio}" /></td>
											<td style="text-align: center"><h:outputText value="#{plano.dataFim}" /></td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</td>
					</tr>
			</c:if>
			<c:if test="${empty relatorioAcaoExtensao.obj.atividade.planosTrabalho}">
				<tr>
					<td colspan="6" align="center">
						<font color="red">Não há planos de trabalho cadastrados para os discentes desta ação</font>
					</td>
				</tr>
			</c:if>
		
			<tr>
				<td colspan="2" class="subFormulario">Lista de Arquivos</td>
			</tr>
		
			<tr>
				<td colspan="2">
					<input type="hidden" value="0" id="idArquivo" name="idArquivo"/>			
					<t:dataTable id="dataTableArq" value="#{relatorioAcaoExtensao.obj.arquivos}" var="anexo" align="center" width="100%" 
							styleClass="listagem" rowClasses="linhaPar, linhaImpar">
						<t:column width="97%">
							<h:outputText value="#{anexo.descricao}" />
						</t:column>
					</t:dataTable>
				</td>
			</tr>
		
			<c:if test="${empty relatorioAcaoExtensao.obj.arquivos}">
				<tr>
					<td colspan="6" align="center">
						<font color="red">Não há arquivos adicionados ao relatório</font>
					</td>
				</tr>
			</c:if>
		
			<tr>
				<td colspan="2" class="subFormulario"> Detalhamento de utilização dos recursos financeiros </td>
			</tr>	
	
			<tr>
				<td colspan="2">
					<c:if test="${not empty relatorioAcaoExtensao.obj.detalhamentoRecursos}">
						<table class="listagem" width="100%" id="dt">
							<thead>
								<tr>
									<th>Descrição</th>
									<th style="text-align: right">FAEx (Interno)</th>
									<th style="text-align: right">Funpec</th>
									<th style="text-align: right">Outros (Externo)</th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="#{relatorioAcaoExtensao.obj.detalhamentoRecursos}" var="consolidacao" varStatus="status">
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
										<td>${consolidacao.elemento.descricao}</td>
										<td style="text-align: right"> R$ ${consolidacao.faex}</td>
										<td style="text-align: right">R$ ${consolidacao.funpec}</td>
										<td style="text-align: right">R$ ${consolidacao.outros}</td>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</c:if>
				</td>
			</tr>

			<c:if test="${empty relatorioAcaoExtensao.obj.detalhamentoRecursos}">
				<tr>
					<td colspan="6" align="center">
						<font color="red">Não há itens de despesas cadastrados</font> 
					</td>
				</tr>
			</c:if>

			<tr>
				<td colspan="2" class="subFormulario">Validação do Departamento</td>
			</tr>

			<tr>
				<th width="20%"><b>Departamento:</b></th>
				<td><h:outputText value="#{relatorioAcaoExtensao.obj.atividade.unidade.nome}" /></td>
			</tr>		

			<tr>
				<th>Data Análise:</th>
				<td><fmt:formatDate value="${relatorioAcaoExtensao.obj.dataValidacaoDepartamento}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
			</tr>		

			<tr>
				<th><b>Avaliador(a):</b></th>
				<td><h:outputText value="#{relatorioAcaoExtensao.obj.registroEntradaDepartamento.usuario.nome}" 
							rendered="#{not empty relatorioAcaoExtensao.obj.registroEntradaDepartamento}" /></td>
			</tr>

			<tr>
				<th><b>Parecer Depto.:</b></th>
				<td><h:outputText id="tipoParecerDepartamento" 
						value="#{relatorioAcaoExtensao.obj.tipoParecerDepartamento != null ? relatorioAcaoExtensao.obj.tipoParecerDepartamento.descricao : 'NÃO ANALISADO' }" />
				</td>
			</tr>		
			<tr>			
				<th>Justificativa:</th>
				<td><h:outputText id="parecerDepartamento" value="#{relatorioAcaoExtensao.obj.parecerDepartamento}" /></td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario">Validação da Proex</td>
			</tr>
		
			<tr>
				<th>Data Análise:</th>
				<td><fmt:formatDate value="${relatorioAcaoExtensao.obj.dataValidacaoProex}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
			</tr>		
		
			<tr>
				<th>Avaliador(a):</th>
				<td><h:outputText value="#{relatorioAcaoExtensao.obj.registroEntradaProex.usuario.nome}" /></td>
			</tr>
		
			<tr>
				<th>Parecer PROEx:</th>
				<td><h:outputText id="tipoParecerProex" 
						value="#{relatorioAcaoExtensao.obj.tipoParecerProex != null ? relatorioAcaoExtensao.obj.tipoParecerProex.descricao : 'NÃO ANALISADO' }" />
			</tr>		
			<tr>			
				<th>Justificativa:</th>
				<td><h:outputText  id="parecerProex" value="#{relatorioAcaoExtensao.obj.parecerProex}" /></td>
			</tr>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>