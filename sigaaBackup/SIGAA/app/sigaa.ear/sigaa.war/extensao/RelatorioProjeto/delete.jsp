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

	<h2>
		<ufrn:subSistema /> &gt; Relat�rio de Projetos de Extens�o
	</h2>

	<h:form id="form" enctype="multipart/form-data">
		<table class="formulario" width="100%">
		
			<caption class="listagem">EXCLUS�O DE ${relatorioProjeto.obj.tipoRelatorio.descricao} DE PROJETOS DE EXTENS�O</caption>
			
			<tr>
				<td colspan="2">
					<b>C�digo:</b>
					<h:outputText value="#{relatorioProjeto.obj.atividade.codigo}"/>
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<b>T�tulo:</b>
					<h:outputText value="#{relatorioProjeto.obj.atividade.titulo}" />
				</td>
			</tr>


			<tr>
				<td colspan="2">
					<b>Unidade Proponente:</b>
					<h:outputText value="#{relatorioProjeto.obj.atividade.unidade.nome}" />
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<b>Fontes Financiamento:</b>
					<h:outputText value="#{relatorioProjeto.obj.atividade.fonteFinanciamentoString}" />
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<b><h:outputText>N� Discentes Envolvidos:</h:outputText></b>
					<h:outputText value="#{relatorioProjeto.obj.atividade.totalDiscentes}" />
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<b><h:outputText>Esta A��o foi realizada:</h:outputText></b>
					<h:outputText value="SIM" rendered="#{relatorioProjeto.obj.acaoRealizada}" />
					<h:outputText value="N�O" rendered="#{not relatorioProjeto.obj.acaoRealizada}" />
				</td>
			</tr>


			<tr>
				<td colspan="2" class="subFormulario">Detalhamento das atividades desenvolvidas:</td>
			</tr>

			<tr>
				<td colspan="2">
					<div id="motivoNaoRealizada"
						style="display: ${relatorioProjeto.obj.acaoRealizada == null ? 'none' : (relatorioProjeto.obj.acaoRealizada ? 'none' : '')};">
						<b> Motivo da n�o realiza��o desta a��o: </b>
						<br />
						<h:outputText style="width:99%" value="#{relatorioProjeto.obj.motivoAcaoNaoRealizada}" />
					</div>
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<b> Existe rela��o objetiva entre a proposta pedag�gica do curso e a proposta do projeto de extens�o? Justifique: </b>
					<br />
					<h:outputText style="width:99%" value="#{relatorioProjeto.obj.relacaoPropostaCurso}" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b> Atividades Realizadas: </b>
					<br />
					<h:outputText style="width:99%" value="#{relatorioProjeto.obj.atividadesRealizadas}" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b> Resultados Obtidos: Qualitativos </b>
					<br />
					<h:outputText style="width:99%" value="#{relatorioProjeto.obj.resultadosQualitativos}" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b> Resultados Obtidos: Quantitativos </b>
					<br />
					<h:outputText style="width:99%" value="#{relatorioProjeto.obj.resultadosQuantitativos}" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b> Dificuldades Encontradas: </b>
					<br />
					<h:outputText style="width:99%" value="#{relatorioProjeto.obj.dificuldadesEncontradas}" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b> Ajustes Realizados Durante a Execu��o da A��o: </b>
					<br />
					<h:outputText style="width:99%" value="#{relatorioProjeto.obj.ajustesDuranteExecucao}" />
				</td>
			</tr>

			<tr>
				<td colspan="2"><b> P�blico Estimado:</b>
					<h:outputText value="#{relatorioProjeto.obj.atividade.publicoEstimado}" id="publicoEstimado" /> pessoas
					<ufrn:help img="/img/ajuda.gif">P�blico estimado informado durante o cadastro da proposta do projeto.</ufrn:help>
				</td>
			</tr>

			<tr>
				<td colspan="2"><b> P�blico Real Atingido: </b>
					<h:outputText
						title="P�blico real atingido"
						value="#{relatorioProjeto.obj.publicoRealAtingido}"
						id="publicoRealAtingidoReadOnly" /> pessoas
				</td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario">Outras a��es realizadas
					vinculadas ao projeto:</td>
			</tr>
			<tr>
				<td colspan="2"><t:dataList
						value="#{relatorioProjeto.outrasAcoes}" var="acao">
						<t:column>
							<f:verbatim>
								<span style="width: 180px; display: block; float: left;">
							</f:verbatim>
							<h:selectBooleanCheckbox value="#{acao.selecionado}"
								styleClass="noborder" disabled="#{relatorioProjeto.readOnly}" />
							<h:outputText value="#{acao.descricao}" />
							<f:verbatim>
								</span>
							</f:verbatim>
						</t:column>
					</t:dataList></td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario">Apresenta��o do projeto
					em eventos de extens�o:</td>
			</tr>
			<tr>
				<td colspan="2"><t:dataList
						value="#{ relatorioProjeto.apresentacoes }" var="apresentacao">
						<t:column>
							<f:verbatim>
								<span style="width: 180px; display: block; float: left;">
							</f:verbatim>
							<h:selectBooleanCheckbox value="#{apresentacao.selecionado}"
								styleClass="noborder" disabled="#{relatorioProjeto.readOnly}" />
							<h:outputText value="#{apresentacao.descricao}" />
							<f:verbatim>
								</span>
							</f:verbatim>
						</t:column>
					</t:dataList></td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario">Produ��o acad�mica
					gerada:</td>
			</tr>
			<tr>
				<td colspan="2">
					<div class="scrollbar" style="margin-left: 5px;"></div> <t:dataList
						value="#{ relatorioProjeto.producoesGeradas }" var="prod">
						<t:column>
							<f:verbatim>
								<span style="width: 350px; display: block; float: left;">
							</f:verbatim>
							<h:selectBooleanCheckbox value="#{prod.selecionado}"
								styleClass="noborder" disabled="#{relatorioProjeto.readOnly}" />
							<h:outputText value="#{prod.descricao}" />
							<f:verbatim>
								</span>
							</f:verbatim>
						</t:column>
					</t:dataList>
				</td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario">Conv�nios e Contratos:</td>
			</tr>

			<tr>
				<td align="center" colspan="2">
					<div class="descricaoOperacao">
						Se esta A��o de extens�o originou um conv�nio ou contrato da ${ configSistema['siglaInstituicao'] } 
						com outro �rg�o, � poss�vel visualizar abaixo os dados do conv�nio.
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<b>N� Conv�nio:</b>
					<h:outputText id="numeroConvenio" value="#{relatorioProjeto.obj.numeroConvenio}"/>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<b>Ano Conv�nio:</b>
					<h:outputText id="anoConvenio" value="#{relatorioProjeto.obj.anoConvenio}"/>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<b>N� Contrato:</b>
					<h:outputText id="numeroContrato" value="#{relatorioProjeto.obj.numeroContrato}"/>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<b>Ano Contrato:</b>
					<h:outputText id="anoContrato" value="#{relatorioProjeto.obj.anoContrato}" />
				</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Detalhamento de
					utiliza��o dos recursos financeiros</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="listagem">
						<tr>
							<td><c:if
									test="${not empty relatorioProjeto.obj.detalhamentoRecursos}">
									<t:dataTable id="dt"
										value="#{relatorioProjeto.obj.detalhamentoRecursos}"
										var="consolidacao" align="center" width="100%"
										styleClass="listagem" rowClasses="linhaPar, linhaImpar"
										rowIndexVar="index" forceIdIndex="true">
										<t:column>
											<f:facet name="header">
												<f:verbatim>Descri��o</f:verbatim>
											</f:facet>
											<h:outputText value="#{consolidacao.elemento.descricao}" />
										</t:column>

										<t:column>
											<f:facet name="header">
												<f:verbatim>FAEx (Interno)</f:verbatim>
											</f:facet>
											<f:verbatim>R$ </f:verbatim>
											<h:outputText value="#{consolidacao.faex}" id="fundo">
												<f:converter converterId="convertMoeda" />
											</h:outputText>
										</t:column>

										<t:column>
											<f:facet name="header">
												<f:verbatim>Funpec</f:verbatim>
											</f:facet>
											<f:verbatim>R$ </f:verbatim>
											<h:outputText value="#{consolidacao.funpec}" id="fundacao">
												<f:converter converterId="convertMoeda" />
											</h:outputText>
										</t:column>

										<t:column>
											<f:facet name="header">
												<f:verbatim>Outros (Externo)</f:verbatim>
											</f:facet>
											<f:verbatim>R$ </f:verbatim>
											<h:outputText value="#{consolidacao.outros}" id="outros">
												<f:converter converterId="convertMoeda" />
											</h:outputText>
										</t:column>
									</t:dataTable>
								</c:if></td>
						</tr>

						<c:if test="${empty relatorioProjeto.obj.detalhamentoRecursos}">
							<tr>
								<td colspan="6" align="center"><font color="red">N�o
										h� itens de despesas cadastrados</font></td>
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
								<td>Descri��o Arquivo</td>
								<td width="5%"></td>
							</tr>
						</thead>

						<c:forEach items="#{relatorioProjeto.arquivosRelatorio}"
							var="anexo" varStatus="status">
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td>${ anexo.descricao }</td>
								<td><h:commandLink action="#{relatorioProjeto.viewArquivo}"
										target="_blank">
										<h:graphicImage value="/img/view.gif"
											style="overflow: visible;" title="Ver Arquivo" />
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
					<c:if test="${empty relatorioProjeto.arquivosRelatorio}">
						<center>
							<font color='red'>N�o h� arquivos adicionados</font>
						</center>
					</c:if>
				</td>
			</tr>


			<tr>
				<td colspan="2" class="subFormulario">Lista de Participantes do Projeto</td>
			</tr>

			<tr>
				<td colspan="2">
					<div class="infoAltRem">
						<h:graphicImage value="/img/extensao/user1_view.png"
							style="overflow: visible;" styleClass="noborder" />
						: Visualizar
					</div>
				</td>
			</tr>

			<tr>
				<td colspan="2">
					<table class="listagem">
						<thead>
							<tr>
								<th style="text-align: right;">N�</th>
								<th style="text-align: center;">CPF</th>
								<th>Nome</th>
								<th>Participa��o</th>
								<th style="text-align: center;">Certificado</th>
								<th></th>
							</tr>
						</thead>

						<tbody>
							<c:forEach
								items="#{relatorioProjeto.obj.atividade.participantes}"
								var="participante" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>${status.count}</td>
									<td>${participante.cadastroParticipante.cpf}</td>
									<td>${participante.cadastroParticipante.nome}</td>
									<td>${participante.tipoParticipacao.descricao}</td>
									<td>${participante.autorizacaoCertificado ? 'SIM' : 'N�O'}</td>
									<td width="2%"><h:commandLink title="Visualizar"
											action="#{gerenciarInscritosCursosEEventosExtensaoMBean.visualizarDadosParticipante}" style="border: 0;">
											<f:param name="idCadastroParticipante" value="#{participante.cadastroParticipante.id}" />
											<h:graphicImage url="/img/extensao/user1_view.png" />
										</h:commandLink></td>
								</tr>
							</c:forEach>

							<c:if
								test="${empty relatorioProjeto.obj.atividade.participantes}">
								<tr>
									<td colspan="6"><center>
											<i>N�o h� participantes cadastrados</i>
										</center></td>
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
							value="#{relatorioProjeto.confirmButton}"
							action="#{relatorioProjeto.removerRelatorio}"
							onclick="return confirm('Deseja Remover este Relat�rio?')"
							immediate="true" />
						<h:commandButton
							value="Cancelar" action="#{relatorioAcaoExtensao.cancelar}"
							onclick="#{confirm }" immediate="true" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>