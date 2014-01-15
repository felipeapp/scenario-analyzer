<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp" %>
<style>
.unidade {
	font-size: 0.9em;
	font-style: italic;
	line-height: 1.3em;
}

.nivel {
	text-transform: uppercase;
}

.periodo {
	font-size: 1.1em;
	color: #292;
	font-weight: bold;
}

table.visualizacao td.texto {
	padding: 10px 20px;
	line-height: 1.3em;
}

table.visualizacao td.texto p {
	text-indent: 3em;
}

table.visualizacao td.inscrever {
	background: #CFC;
	text-align: center;
}

table.visualizacao td.inscrever a {
	color: #292;
	font-size: 1.2em;
	font-variant: small-caps;
	text-decoration: underline;
	line-height: 1.5em;
}
table.formulario, table.subFormulario {
	width: 100%;
}

table.subFormulario table.bt_vestibular {
	display: block;
	width: 100%;
	border: #dedfe3 1px solid;
}

.destaque {
	font-size: 13px;
	font-weight: bold;
}

.destaque a {
	color: #d99c44;
}

.destaque a:hover {
	color: #404e82;
	text-decoration: underline;
}

.dados_pessoais {
	color: #333333;
	font-style: italic;
}

.dados_pessoais a {
	cursor: pointer;
}

.dados_pessoais a:hover {
	text-decoration: none;
}

.dados_pessoais ul {
	float: left;
	position: relative;
	margin: 0 0 0 -75px;
	*margin: 0 0 0 -80px;
}

.dados_pessoais ul li {
	padding: 1px 0 0 15px;
}

pre {
	white-space: normal;
}
</style>

<f:view>

<h2> ${processoSeletivoVestibular.obj.nome}</h2>
<h:form>

	<table class="visualizacao" style="width: 95%;">
		<caption>${processoSeletivoVestibular.obj.nome}</caption>
		<tr>
			<th>Período de Inscrições:</th>
			<td class="periodo">
				<ufrn:format type="data" name="processoSeletivoVestibular"
						property="obj.inicioInscricaoCandidato" /> a <ufrn:format
						type="data" name="processoSeletivoVestibular"
						property="obj.fimInscricaoCandidato" />
			</td>
		</tr>
		<tr>
			<td width="40%">
				<table class="subFormulario">
					<caption>Menu de Opções</caption>
					<c:if test="${not empty processoSeletivoVestibular.obj.idEdital}">
						<tr>
							<td>
								<table class="bt_vestibular">
									<tr>
										<td valign="middle" align="center">
											<h:graphicImage value="../images/vestibular/edital.png" style="overflow: visible;" />
										</td>
										<td class="destaque">
										<a href="${ctx}/verProducao?idProducao=${ processoSeletivoVestibular.obj.idEdital}&key=${ sf:generateArquivoKey(processoSeletivoVestibular.obj.idEdital) }"
											target="_blank">Clique AQUI para ler o Edital!</a></td>
									</tr>
								</table>
							</td>
						</tr>
					</c:if>

					<c:if test="${not empty processoSeletivoVestibular.obj.idManualCandidato}">
						<tr>
							<td>
								<table class="bt_vestibular">
									<tr>
										<td valign="middle" align="center">
											<h:graphicImage value="../images/vestibular/edital.png" style="overflow: visible;" />
										</td>
										<td class="destaque">
										<a href="${ctx}/verProducao?idProducao=${ processoSeletivoVestibular.obj.idManualCandidato}&key=${ sf:generateArquivoKey(processoSeletivoVestibular.obj.idManualCandidato) }"
											target="_blank">Clique AQUI para ler o Manual do Candidato!</a></td>
									</tr>
								</table>
							</td>
						</tr>
					</c:if>
			
					<c:if test="${empty comprovante and processoSeletivoVestibular.obj.inscricoesCandidatoAbertas}">
						<tr>
							<td>
								<table class="bt_vestibular">
									<tr>
										<td class="destaque" valign="middle" align="center">
											<h:graphicImage value="../images/vestibular/inscricao.png" style="overflow: visible;" />
										</td>
										<td class="destaque" valign="center" align="left">
											<h:commandLink title="Inscrever-se neste processo seletivo"
												value="Clique AQUI para inscrever-se!" id="inscriverProcessoSeletivoLink2"
												action="#{acompanhamentoVestibular.instrucoesGeraisInscricao}">
												<f:param name="id" value="#{processoSeletivoVestibular.obj.id}" />
											</h:commandLink>
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</c:if>

					<tr>
						<td>
							<table class="bt_vestibular">
								<tr>
									<td class="destaque" valign="middle" align="center">
										<h:graphicImage value="../images/vestibular/dados_pessoais.png" style="overflow: visible;" />
									</td>
									<td class="destaque">
										<h:commandLink title="Página de Acompanhamento"
											id="paginaAcompanhamentoLink2"
											action="#{acompanhamentoVestibular.acompanhamento}">
											Área Pessoal do Candidato
											<f:param name="id" value="#{processoSeletivoVestibular.obj.id}" />
										</h:commandLink>
									</td>
								</tr>
								<tr>
									<td></td>
									<td class="dados_pessoais">
										<h:commandLink title="Página de Acompanhamento"
											id="paginaAcompanhamentoLink3"
											action="#{acompanhamentoVestibular.acompanhamento}">
											<ul>
												Clique aqui para realizar as seguintes operações:
												<li>- Alteração de dados pessoais,</li>
												<li>- Alteração/reimpressão de dados de pagamento,</li>
												<li>- Obtenção do comprovante de inscrição,</li>
												<li>- Outras opções.</li>
											</ul>
											<f:param name="id" value="#{processoSeletivoVestibular.obj.id}" />
										</h:commandLink>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
			</td>
			<td valign="top">
				<table  class="subFormulario" width="100%">
					<caption>Informações, Avisos e Notícias</caption>
					<tr><td colspan="2">
					<c:if test="${not empty processoSeletivoVestibular.obj.avisosPublicaveis}">
						<table class="listagem">
						<thead>
							<tr>
								<td style="text-align: center;">
									<b>Data</b>
								</td>
								<td>
									<b>Aviso/Notícia</b>
								</td>
								<td style="text-align: center;">
									<b>Anexos</b>
								</td>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="item" items="#{processoSeletivoVestibular.obj.avisosAtivos}" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td class="texto" width="10%" align="center">
										<h:outputText value="#{item.inicioPublicacao}">
											<f:convertDateTime dateStyle="short" timeStyle="short" />
										</h:outputText>
									</td>
									<td width="75%">
										${item.chamada}<br />
										${item.corpo}
									</td>
									<td width="15%" align="center">
										<c:if test="${not empty item.idArquivo}">
												<a href="${ctx}/verProducao?idProducao=${item.idArquivo}&key=${ sf:generateArquivoKey(item.idArquivo)}" target="_blank">
													<h:graphicImage value="/img/icones/page_white_magnify.png" style="vertical-align: middle; overflow: visible;" /><br />
													Veja o anexo!										
												</a>
										</c:if>
									</td>
								</tr>
							</c:forEach>
						</tbody>
						</table>
					</c:if>
					<h:outputText rendered="#{empty processoSeletivoVestibular.obj.avisos}" value="Não há notícias cadastradas"/>
				</td></tr>
				</table>
			</td>
		</tr>
	</table>
</h:form>

<c:if test="${empty comprovante}">
	<br />
	<center>
	<a href="javascript: history.go(-1);"> << Voltar </a>
	</center>
</c:if>
</f:view>
<%@include file="/public/include/rodape.jsp" %>