<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form>
	<a4j:keepAlive beanName="respostasAutoAvaliacaoMBean" />
	<h2> <ufrn:subSistema /> &gt; Auto Avaliação da Pós Graduação ${ respostasAutoAvaliacaoMBean.nivelDescricao } </h2>

	<div class="descricaoOperacao">
		<p> Caro Usuário, </p>
		<p> Abaixo estão listadas as respostas para o questionário selecionado. </p>
	</div>

	<table class="listagem" style="width: 90%;">
	<caption> Respostas da Auto Avaliação da Pós Graduação ${ respostasAutoAvaliacaoMBean.nivelDescricao } </caption>
	<tbody>
		<tr>
			<td colspan="2" class="subFormulario"> Perguntas Gerais </td>
		</tr>
		<c:forEach var="resposta" items="#{respostasAutoAvaliacaoMBean.obj.respostas.respostas}" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
			   <%-- Imprime as perguntas --%>
				<th style="text-align: left; font-weight: bold;" colspan="2">
					${status.index + 1}. ${resposta.pergunta.pergunta}
				</th>
			</tr>
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
				<td colspan="2">
					<h:outputText  value="#{resposta.respostaVfString}" rendered="#{resposta.pergunta.vf}" />
					<h:outputText  value="#{resposta.respostaDissertativa}" rendered="#{resposta.pergunta.dissertativa}" />
					<h:outputText  value="#{resposta.respostaNumericaString}" rendered="#{resposta.pergunta.numerica}" />
					<h:outputText  value="#{resposta.alternativa.alternativa}" rendered="#{resposta.pergunta.unicaEscolha}" />
					<h:outputText  value="#{resposta.alternativa.alternativaComPeso}" rendered="#{resposta.pergunta.unicaEscolhaAlternativaPeso}" />
					<c:if test="${fn:length(resposta.alternativas) > 0}"> 
						<c:forEach var="alternativa" items="#{resposta.alternativas}" varStatus="status2">
							${status2.index + 1}) <h:outputText  value="#{alternativa.alternativa}" /> <br/>
						</c:forEach>
					</c:if>
					<c:if test="${resposta.pergunta.arquivo && resposta.respostaArquivo != null}">
						<a target="_blank" href="${ configSistema['linkSigaa'] }/sigaa/verProducao?idProducao=${resposta.respostaArquivo}&key=${ sf:generateArquivoKey(resposta.respostaArquivo) }">
							<h:graphicImage url="/img/pdf.png" style="border:none" title="Arquivo de Respostas" />
							Arquivo de Respostas
						</a>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		<tr>
			<td colspan="2" class="subFormulario">Metas e ações de formação de Recursos Humanos para o Programa nos próximos 5 anos</td>
		</tr>
		<tr>
			<td colspan="2">
				<table width="100%">
					<thead>
						<tr>
							<th>Meta</th><th>Ação</th><th>Quantificação/Indicador</th>
						</tr>
					</thead>
					<tbody>
						<c:if test="${ not empty respostasAutoAvaliacaoMBean.obj.metasAcoesFormacaoRH }">
							<c:set var="indice" value="0" />
							<c:forEach items="#{ respostasAutoAvaliacaoMBean.obj.metasAcoesFormacaoRH }" var="item" >
								<tr>
									<td><h:outputText value="#{ item.meta }"     /></td>
									<td><h:outputText value="#{ item.acao }"     /></td>
									<td><h:outputText value="#{ item.indicador }"/></td>
								</tr>
							</c:forEach>
						</c:if>
						<c:if test="${ empty respostasAutoAvaliacaoMBean.obj.metasAcoesFormacaoRH }">
							<tr><td colspan="3" style="text-align: center">Não informado</td>
						</c:if>
					</tbody>
				</table>
			</td> 
		</tr>
		<tr>
			<td colspan="2" class="subFormulario"> 
				Detalhe das metas de formação acadêmica e de produção científica para o próximo ano, para cada docente permanente e colaborador do Programa.
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<table width="100%">
					<thead>
						<tr>
							<th>Docente</th>
							<th style="text-align:right; width:12%">Núm. Mestres<br/>a serem Formados</th>
							<th style="text-align:right; width:12%">Núm. Doutores<br/>a serem Formados</th>
							<th style="text-align:right; width:12%">Artigos<br/>(A1/A2)</th>
							<th style="text-align:right; width:12%">Artigos<br/>(B1/B2)</th>
							<th style="text-align:right; width:12%">Demais<br/>Estratos</th>
							<th style="text-align:right; width:12%">Pedidos de<br/>Patente</th>
						</tr>
					</thead>
					<tbody>
						<c:if test="${ not empty respostasAutoAvaliacaoMBean.obj.metasAcoesFormacaoAcademica }">
							<c:forEach items="#{ respostasAutoAvaliacaoMBean.obj.metasAcoesFormacaoAcademica }" var="item">
								<tr>
									<td style="text-align:left">
										<h:outputText value="#{ item.equipePrograma.nome }"  />
										<h:outputText value="#{ item.corpoDocente.nome }"  />
									</td>
									<td style="text-align:right"><h:outputText value="#{ item.numFormacaoMestres }"   /></td>
									<td style="text-align:right"><h:outputText value="#{ item.numFormacaoDoutores }"  /></td>
									<td style="text-align:right"><h:outputText value="#{ item.qtdArtigosQualisA1A2 }" /></td>
									<td style="text-align:right"><h:outputText value="#{ item.qtdArtigosQualisB1B2 }" /></td>
									<td style="text-align:right"><h:outputText value="#{ item.qtdDemaisArtigos }"     /></td>
									<td style="text-align:right"><h:outputText value="#{ item.qtdPedidosPatente }"    /></td>
								</tr>
							</c:forEach>
						</c:if>
						<c:if test="${ empty respostasAutoAvaliacaoMBean.obj.metasAcoesFormacaoAcademica }">
							<tr><td colspan="7" style="text-align: center">Não informado</td>
						</c:if>
					</tbody>
				</table>
			</td> 
		</tr>
		<tr>
			<td colspan="2" class="subFormulario">
				<b>Ações que o Programa indica para a
				melhoria da qualidade de formação de Recursos Humanos e que
				contribua para o incremento do conceito do programa, distribuída nos seguintes níveis:</b>
			</td>
		</tr>
		<tr>

			<th valign="top" width="25%" class="rotulo">Docente:</th>
			<td> 
				<h:outputText value="#{ respostasAutoAvaliacaoMBean.obj.sugestoesMelhoriaPrograma.docente}"/> </td>
		</tr>
		<tr>
			<th valign="top" class="rotulo">Coordenação do Programa:</th>
			<td> <h:outputText value="#{ respostasAutoAvaliacaoMBean.obj.sugestoesMelhoriaPrograma.coordenacaoPrograma}"/> </td>
		</tr>
		<tr>
			<th valign="top" class="rotulo">Direção do Centro:</th>
			<td> <h:outputText value="#{ respostasAutoAvaliacaoMBean.obj.sugestoesMelhoriaPrograma.direcaoCentro}"/> </td>
		</tr>
		<tr>
			<th valign="top" class="rotulo">Pró-reitoria de Pós-Graduação:</th>
			<td> <h:outputText value="#{ respostasAutoAvaliacaoMBean.obj.sugestoesMelhoriaPrograma.proReitoriaPosGraduacao}"/> </td>
		</tr>
		<tr>
			<th valign="top" class="rotulo">Reitoria:</th>
			<td> <h:outputText value="#{ respostasAutoAvaliacaoMBean.obj.sugestoesMelhoriaPrograma.reitoria}"/> </td>
		</tr>
		<c:if test="${ not empty respostasAutoAvaliacaoMBean.obj.pareceresAutoAvaliacao }">
			<tr>
				<td colspan="2">
					<table width="100%" class="subFormulario">
						<caption>Pareceres Dados pela Comissão</caption>
						<tbody>
							<c:forEach items="#{ respostasAutoAvaliacaoMBean.obj.pareceresAutoAvaliacao }" var="item">
								<tr>
									<td class="subFormulario"><ufrn:format type="data" valor="${ item.cadastradoEm }" /> - Situação: ${ item.situacao }</td>
								</tr>
								<tr>
									<td>${ item.parecer }</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</td>
			</tr>
		</c:if>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2" style="text-align: center"> 
					<h:commandButton value="<< Voltar" action="#{respostasAutoAvaliacaoMBean.formListaRespostas}" id="voltarRespostas" rendered="#{ respostasAutoAvaliacaoMBean.portalPpg || respostasAutoAvaliacaoMBean.portalLatoSensu }"/>
					<h:commandButton value="<< Voltar" action="#{respostasAutoAvaliacaoMBean.iniciarPreenchimento}" id="voltarPreenchimento" rendered="#{ respostasAutoAvaliacaoMBean.portalCoordenadorStricto || respostasAutoAvaliacaoMBean.portalCoordenadorLato }"/>
					<h:commandButton value="Cancelar" action="#{respostasAutoAvaliacaoMBean.cancelar}" immediate="true" onclick="#{confirm}" id="cancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>	

	<br/>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	