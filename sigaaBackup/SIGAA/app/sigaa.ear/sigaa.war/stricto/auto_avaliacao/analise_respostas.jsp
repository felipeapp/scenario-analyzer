<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form>
	<a4j:keepAlive beanName="respostasAutoAvaliacaoMBean" />
	<h2> <ufrn:subSistema /> &gt; Auto Avaliação da Pós Graduação ${ respostasAutoAvaliacaoMBean.nivelDescricao } </h2>

	<div class="descricaoOperacao">
		<p> Caro Usuário, </p>
		<p> Avalie se as respostas dadas pelo coordenador e dê o seu parecer:
			<ul>
				<li>ACEITO: Indica que as respostas foram aceitas e que não poderão ser mais alteradas. </li>
				<li>RETORNADO: Indica que as respostas foram retornadas ao coordenador para adequação. </li>
				<li>REJEITADO: Indica que as respostas foram rejeitadas e que não poderão ser mais alteradas. </li>
			</ul>
		 </p>
	</div>
	
	<rich:tabPanel switchType="client" >
 		<rich:tab label="Auto Avaliação" >
			<table class="listagem" style="width: 90%;">
			<caption> Respostas da Auto Avaliação da Pós Graduação Stricto Sensu </caption>
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
									<th style="text-align:right; width:12%">Artigos (A1/A2)</th>
									<th style="text-align:right; width:12%">Artigos (B1/B2)</th>
									<th style="text-align:right; width:12%">Demais Estratos</th>
									<th style="text-align:right; width:12%">Pedidos de Patente</th>
								</tr>
							</thead>
							<tbody>
								<c:if test="${ not empty respostasAutoAvaliacaoMBean.obj.metasAcoesFormacaoAcademica }">
									<c:forEach items="#{ respostasAutoAvaliacaoMBean.obj.metasAcoesFormacaoAcademica }" var="item">
										<tr>
											<td style="text-align:left"><h:outputText value="#{ item.equipePrograma.nome }"  /></td>
											<td style="text-align:right"><h:outputText value="#{ item.numFormacaoDoutores }"  /></td>
											<td style="text-align:right"><h:outputText value="#{ item.numFormacaoMestres }"   /></td>
											<td style="text-align:right"><h:outputText value="#{ item.qtdArtigosQualisA1A2 }" /></td>
											<td style="text-align:right"><h:outputText value="#{ item.qtdArtigosQualisB1B2 }" /></td>
											<td style="text-align:right"><h:outputText value="#{ item.qtdDemaisArtigos }"     /></td>
											<td style="text-align:right"><h:outputText value="#{ item.qtdPedidosPatente }"    /></td>
										</tr>
									</c:forEach>
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
				</tbody>
			</table>
			</rich:tab>
			<rich:tab label="Pareceres Anteriores" rendered="#{ not empty respostasAutoAvaliacaoMBean.obj.pareceresAutoAvaliacao }">
				<table width="100%">
					<tbody>
						<c:forEach items="#{ respostasAutoAvaliacaoMBean.obj.pareceresAutoAvaliacao }" var="item">
							<tr>
								<td class="subFormulario">Situação: ${ item.situacao } / Cadastrado em <ufrn:format type="data" valor="${ item.cadastradoEm }" /></td>
							</tr>
							<tr>
								<td>${ item.parecer }</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</rich:tab>
		</rich:tabPanel>
	<table class="subFormulario" width="100%">
		<caption>Análise e submeta um parecer sobre a Auto Avaliação</caption>
		<tbody>
			<tr>
				<th width="20%">Situação:</th>
				<td>
					<h:selectOneMenu value="#{ respostasAutoAvaliacaoMBean.parecerAutoAvaliacao.situacao }" id="situacao">
						<f:selectItems value="#{ respostasAutoAvaliacaoMBean.situacaoParecerCombo }"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Parecer:</th>
				<td>
					<h:inputTextarea value="#{ respostasAutoAvaliacaoMBean.parecerAutoAvaliacao.parecer }" rows="5" cols="100" id="parecer"/>
				</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2" style="text-align: center"> 
					<h:commandButton value="Cadastrar" action="#{respostasAutoAvaliacaoMBean.emitirParecer}" id="cadastrar" />
					<h:commandButton value="<< Voltar" action="#{respostasAutoAvaliacaoMBean.formListaRespostas}" id="voltar"/> 
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
	