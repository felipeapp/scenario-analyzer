<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
.scrollbar {
	margin-left: 155px;
	width: 98%;
	overflow: auto;
}
</style>


<script type="text/javascript">
	function acaoRealizada(acaoRealizada) {
		var motivo = document.getElementById("motivoNaoRealizada") ;
		
		if (acaoRealizada.value == 'TRUE') {
			motivo.style.display = "none";
		} else {
			motivo.style.display = "";
		}		
	}
	
</script>


<f:view>

	<h2><ufrn:subSistema /> &gt; Relatório de Cursos &amp; Eventos de Extensão</h2>

	<h:form id="form" enctype="multipart/form-data">
		<table class="formulario" width="100%">
		
			<caption class="listagem">CADASTRO DE ${relatorioCursoEvento.obj.tipoRelatorio.descricao} DE CURSOS & EVENTOS DE EXTENSÃO</caption>
			
			<tr>
				<td colspan="2">
					<b>Código:</b>
					<h:outputText value="#{relatorioCursoEvento.obj.atividade.codigo}"/>
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<b>Título:</b>
					<h:outputText value="#{relatorioCursoEvento.obj.atividade.titulo}" />
				</td>
			</tr>


			<tr>
				<td colspan="2">
					<b>Unidade Proponente:</b>
					<h:outputText value="#{relatorioCursoEvento.obj.atividade.unidade.nome}" />
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<b>Fontes de Financiamento:</b>
					<h:outputText value="#{relatorioCursoEvento.obj.atividade.fonteFinanciamentoString}" />
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<b><h:outputText>Nº de Discentes Envolvidos:</h:outputText></b>
					<h:outputText value="#{relatorioCursoEvento.obj.atividade.totalDiscentes}" />
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<b><h:outputText>Esta Ação foi realizada:</h:outputText></b>
					<h:outputText value="SIM" rendered="#{relatorioCursoEvento.obj.acaoRealizada}" />
					<h:outputText value="NÃO" rendered="#{not relatorioCursoEvento.obj.acaoRealizada}" />
				</td>
			</tr>


			<tr>
				<td colspan="2" class="subFormulario">Detalhamento das atividades desenvolvidas:</td>
			</tr>

			<tr>
				<td colspan="2">
					<div id="motivoNaoRealizada"
						style="display: ${relatorioCursoEvento.obj.acaoRealizada == null ? 'none' : (relatorioCursoEvento.obj.acaoRealizada ? 'none' : '')};">
						<b> Motivo da não realização desta ação: </b>
						<br />
						<h:outputText style="width:99%" value="#{relatorioCursoEvento.obj.motivoAcaoNaoRealizada}" />
					</div>
				</td>
			</tr>

			<tr>
				<td colspan="2"><b> Atividades Realizadas: </b>
					<br />
					<h:outputText style="width:99%" value="#{relatorioCursoEvento.obj.atividadesRealizadas}" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b> Resultados Obtidos: qualitativos </b>
					<br />
					<h:outputText style="width:99%" value="#{relatorioCursoEvento.obj.resultadosQualitativos}" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b> Resultados Obtidos: quantitativos </b>
					<br />
					<h:outputText style="width:99%" value="#{relatorioProjeto.obj.resultadosQuantitativos}" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b> Dificuldades Encontradas: </b>
					<br />
					<h:outputText style="width:99%" value="#{relatorioCursoEvento.obj.dificuldadesEncontradas}" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b> Ajustes Realizados Durante a Execução da Ação: </b>
					<br />
					<h:outputText style="width:99%" value="#{relatorioCursoEvento.obj.ajustesDuranteExecucao}" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b> Público Estimado:</b>
					<h:outputText value="#{relatorioCursoEvento.obj.atividade.publicoEstimado}" id="publicoEstimado" /> pessoas
					<ufrn:help img="/img/ajuda.gif">Público estimado informado durante o cadastro da proposta do projeto.</ufrn:help>
				</td>
			</tr>

			<tr>
				<td colspan="2"><b> Público Real Atingido: </b>
					<h:outputText title="Público real atingido"
						value="#{relatorioCursoEvento.obj.publicoRealAtingido}"
						id="publicoRealAtingidoReadOnly" /> pessoas
				</td>
			</tr>

			<tr>
				<td colspan="2"><b>Total de concluintes:</b>
				<h:outputText title="Total de concluintes"
					value="#{relatorioCursoEvento.obj.numeroConcluintes}"
					id="totalConcluintes"/> pessoas
				</td>
			</tr>
			
			
			<tr>
				<td colspan="2"><b>Valor da Taxa de matrícula:</b>
				<h:outputText title="Taxa de matrícula"
					value="#{relatorioCursoEvento.obj.taxaMatricula}"
					id="taxaMatricula"/> R$
				</td>
			</tr>
			
			<tr>
				<td colspan="2"><b>Valor arrecadado:</b>
				<h:outputText title="Taxa de matrícula"
					value="#{relatorioCursoEvento.obj.totalArrecadado}"
					id="valorArrecadado"/> R$
				</td>
			</tr>


			<tr>
				<td colspan="2" class="subFormulario">Detalhamento de utilização dos recursos financeiros</td>
			</tr>

			<tr>
				<td colspan="2">
					<table class="listagem">
						<thead>
							<tr>
								<td><c:if test="${not empty relatorioCursoEvento.obj.detalhamentoRecursos}">
										<t:dataTable id="dt" value="#{relatorioCursoEvento.obj.detalhamentoRecursos}"
											var="consolidacao" width="100%" styleClass="listagem"
											rowClasses="linhaPar, linhaImpar">
											<t:column>
												<f:facet name="header">
													<f:verbatim>Descrição</f:verbatim>
												</f:facet>
												<h:outputText value="#{consolidacao.elemento.descricao}" />
											</t:column>

											<t:column>
												<f:facet name="header">
													<f:verbatim>FAEx (Interno)</f:verbatim>
												</f:facet>
												<f:verbatim>R$ </f:verbatim>
												<h:outputText value="#{consolidacao.faex}" id="consolidacaoFaex"/>
											</t:column>

											<t:column>
												<f:facet name="header">
													<f:verbatim>Funpec</f:verbatim>
												</f:facet>
												<f:verbatim>R$ </f:verbatim>
												<h:outputText value="#{consolidacao.funpec}" id="consolidacaoFunpec"/>
											</t:column>


											<t:column>
												<f:facet name="header">
													<f:verbatim>Outros (Externo)</f:verbatim>
												</f:facet>
												<f:verbatim>R$ </f:verbatim>
												<h:outputText value="#{consolidacao.outros}" id="outros"/>
											</t:column>
										</t:dataTable>
									</c:if></td>
							</tr>
						</thead>

						<c:if test="${empty relatorioCursoEvento.obj.detalhamentoRecursos}">
							<tr>
								<td colspan="6" align="center"><font color="red">Não há itens de despesas cadastrados</font></td>
							</tr>
						</c:if>

					</table>
				</td>
			</tr>

			<tr>
				<td colspan="3">
					<div class="infoAltRem">
						<h:graphicImage value="/img/view.gif" style="overflow: visible;" styleClass="noborder" /> : Ver Arquivo
					</div>
				</td>
			</tr>

			<tr>
				<td colspan="2">

					<table class="formulario" style="width: 100%">
						<thead>
							<tr>
								<td>Descrição do Arquivo</td>
								<td width="5%"></td>
							</tr>
						</thead>

						<c:forEach items="#{relatorioCursoEvento.arquivosRelatorio}" var="anexo" varStatus="status">
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td>${anexo.descricao}</td>
								<td>
									<h:commandLink action="#{relatorioCursoEvento.viewArquivo}" target="_blank">
										<h:graphicImage value="/img/view.gif" style="overflow: visible;" title="Ver Arquivo" />
										<f:param name="idArquivo" value="#{anexo.idArquivo}" />
									</h:commandLink>
								</td>
							</tr>
						</c:forEach>
					</table>
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<c:if test="${empty relatorioCursoEvento.arquivosRelatorio}">
						<center>
							<font color='red'>Não há arquivos adicionados</font>
						</center>
					</c:if>
				</td>
			</tr>


			<tr>
				<td colspan="2" class="subFormulario">Lista de Participantes</td>
			</tr>

			<tr>
				<td colspan="2">
					<div class="infoAltRem">
						<h:graphicImage value="/img/extensao/user1_view.png" style="overflow: visible;" styleClass="noborder" />
						: Visualizar
					</div>
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<table class="listagem">
						<thead>
							<tr>
								<th style="text-align: right;">Nº</th>
								<th style="text-align: center;">CPF</th>
								<th>Nome</th>
								<th>Participação</th>
								<th style="text-align: center;">Certificado</th>
								<th></th>
							</tr>
						</thead>

						<tbody>
							<c:forEach items="#{relatorioCursoEvento.obj.atividade.participantes}"
								var="participante" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>${status.count}</td>
									<td>${participante.cadastroParticipante.cpf}</td>
									<td>${participante.cadastroParticipante.nome}</td>
									<td>${participante.tipoParticipacao.descricao}</td>
									<td>${participante.autorizacaoCertificado ? 'SIM' : 'NÃO'}</td>
									<td width="2%">
										<h:commandLink title="Visualizar"
											action="#{gerenciarInscritosCursosEEventosExtensaoMBean.visualizarDadosParticipante}" style="border: 0;">
											<f:param name="idCadastroParticipante" value="#{participante.cadastroParticipante.id}" />
											<h:graphicImage url="/img/extensao/user1_view.png" />
										</h:commandLink>
									</td>
								</tr>
							</c:forEach>

							<c:if test="${empty relatorioCursoEvento.obj.atividade.participantes}">
								<tr>
									<td colspan="6">
										<center><i>Não há participantes cadastrados</i></center>
									</td>
								</tr>
							</c:if>
						</tbody>
					</table>
				</td>
			</tr>


			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton
							value="#{relatorioCursoEvento.confirmButton}"
							action="#{relatorioCursoEvento.removerRelatorio}"
							onclick="return confirm('Deseja remover este Relatório?')"
							immediate="true" />
						<h:commandButton value="Cancelar"
							action="#{relatorioAcaoExtensao.cancelar}" onclick="#{confirm}"
							immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>