<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="respostasAutoAvaliacaoMBean" />
		
	<h2> <ufrn:subSistema /> &gt; Auto Avalia��o da P�s Gradua��o ${ respostasAutoAvaliacaoMBean.nivelDescricao } </h2>
	
	<div class="descricaoOperacao">
		${ respostasAutoAvaliacaoMBean.calendarioAutoAvaliacao.instrucoesGerais }	
	</div>
	
	<c:if test="${ not empty respostasAutoAvaliacaoMBean.obj.ultimoParecer }">
		<table class="listagem">
		<caption>�ltimo Parecer da Comiss�o de Avalia��o</caption>
		<tbody>
			<tr><td>
				${ respostasAutoAvaliacaoMBean.obj.ultimoParecer.parecer }
			</td></tr>
		</tbody>
		</table>
	</c:if>
	<br/>
	<h:form enctype="multipart/form-data" id="form">
	
	<rich:tabPanel switchType="client" >
 		<rich:tab label="Perguntas Gerais" >
			<table class="formulario" style="width: 100%;">
				<caption>Perguntas Gerais</caption>
				<tbody>
					<tr>
						<td colspan="2">
							<%@include file="/geral/questionario/_formulario_respostas.jsp" %>
						</td> 
					</tr>
				</tbody>
			</table>
			</rich:tab>
			<rich:tab label="Metas e A��es" >
				<div class="infoAltRem">
						<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: Adicionar Meta
						<h:graphicImage value="/img/cronograma/remover.gif" style="overflow: visible;"/>: Remover Meta
				</div>
				<table class="formulario" style="width: 100%;">
					<caption>Detalhe as Metas e A��es e Indique A��es para a Melhoria da Qualidade da P�s Gradua��o</caption>
					<tbody>
						<tr>
							<td colspan="2" class="subFormulario">Forma��o de Recursos Humanos</td>
						</tr>
						<tr>
							<td colspan="2"><b>Quais as metas e a��es de forma��o de Recursos Humanos para o Programa nos pr�ximos 5 anos?</b></td>
						</tr>
						<tr>
							<td colspan="2">
								<table width="100%">
									<thead>
										<tr>
											<th>Meta</th><th>A��o</th><th>Quantifica��o/Indicador</th><th></th>
										</tr>
									</thead>
									<tbody>
										<c:if test="${ not empty respostasAutoAvaliacaoMBean.obj.metasAcoesFormacaoRH }">
											<c:set var="indice" value="0" />
											<c:forEach items="#{ respostasAutoAvaliacaoMBean.obj.metasAcoesFormacaoRH }" var="item" varStatus="status">
												<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
													<td><h:outputText value="#{ item.meta }"     /></td>
													<td><h:outputText value="#{ item.acao }"     /></td>
													<td><h:outputText value="#{ item.indicador }"/></td>
													<td>
														<h:commandLink action="#{ respostasAutoAvaliacaoMBean.removerMetaAcaoFormacaoRH }" id="removerMetaAcaoFormacaoRH">
															<h:graphicImage value="/img/cronograma/remover.gif" style="overflow: visible;" title="Remover Meta"/>
															<f:param name="item" value="#{ item.hash }"/>
														</h:commandLink>
													</td>
												</tr>
											</c:forEach>
										</c:if>
										<tr>
											<td><h:inputTextarea value="#{ respostasAutoAvaliacaoMBean.metaAcaoFormacaoRH.meta }"      cols="40" rows="3" id="meta" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }"/></td>
											<td><h:inputTextarea value="#{ respostasAutoAvaliacaoMBean.metaAcaoFormacaoRH.acao }"      cols="40" rows="3" id="acao" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }"/></td>
											<td><h:inputTextarea value="#{ respostasAutoAvaliacaoMBean.metaAcaoFormacaoRH.indicador }" cols="40" rows="3" id="indicador" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }"/></td>
											<td>
												<h:commandLink action="#{ respostasAutoAvaliacaoMBean.adicionarMetaAcaoFormacaoRH }" id="adicionarMetaAcaoFormacaoRH"  disabled="#{ respostasAutoAvaliacaoMBean.readOnly }">
													<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" title="Adicionar Meta"/>
												</h:commandLink>
											</td>
										</tr>
									</tbody>
								</table>
							</td> 
						</tr>
						<tr>
							<td colspan="2" class="subFormulario">Forma��o Acad�mica</td>
						</tr>
						<tr>
							<td colspan="2"> 
								<b>Detalhe as metas de forma��o acad�mica e de produ��o cient�fica para o pr�ximo ano, para cada docente permanente e colaborador do Programa.</b>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<table width="100%">
									<thead>
										<tr>
											<th>Docente</th>
											<th style="text-align:right; width:12%">N�m. Mestres<br/>a serem Formados</th>
											<th style="text-align:right; width:12%">N�m. Doutores<br/>a serem Formados</th>
											<th style="text-align:right; width:12%">Artigos (A1/A2)</th>
											<th style="text-align:right; width:12%">Artigos (B1/B2)</th>
											<th style="text-align:right; width:12%">Demais Estratos</th>
											<th style="text-align:right; width:12%">Pedidos de Patente</th>
											<th width="2%"></th>
										</tr>
									</thead>
									<tbody>
										<c:if test="${ not empty respostasAutoAvaliacaoMBean.obj.metasAcoesFormacaoAcademica }">
											<c:forEach items="#{ respostasAutoAvaliacaoMBean.obj.metasAcoesFormacaoAcademica }" var="item" varStatus="status">
												<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
													<td style="text-align:left">
														<h:outputText value="#{ item.equipePrograma.nome }"  />
														<h:outputText value="#{ item.corpoDocente.nome }"  />
													</td>
													<td style="text-align:right"><h:outputText value="#{ item.numFormacaoDoutores }"  /></td>
													<td style="text-align:right"><h:outputText value="#{ item.numFormacaoMestres }"   /></td>
													<td style="text-align:right"><h:outputText value="#{ item.qtdArtigosQualisA1A2 }" /></td>
													<td style="text-align:right"><h:outputText value="#{ item.qtdArtigosQualisB1B2 }" /></td>
													<td style="text-align:right"><h:outputText value="#{ item.qtdDemaisArtigos }"     /></td>
													<td style="text-align:right"><h:outputText value="#{ item.qtdPedidosPatente }"    /></td>
													<td>
														<h:commandLink action="#{ respostasAutoAvaliacaoMBean.removerMetasAcoesFormacaoAcademica }" id="removerMetasAcoesFormacaoAcademica" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }">
															<h:graphicImage value="/img/cronograma/remover.gif" style="overflow: visible;" title="Remover Meta"/>
															<f:param name="item" value="#{ item.hash }"/>
														</h:commandLink>
													</td>
												</tr>
											</c:forEach>
										</c:if>
										<tr>
											<td style="text-align: left;">
												<h:selectOneMenu  id="docentePrograma"  disabled="#{ respostasAutoAvaliacaoMBean.readOnly }"
													value="#{ respostasAutoAvaliacaoMBean.idDocente }" >
													<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
													<f:selectItems value="#{ equipePrograma.docentesProgramaCombo}"/>
												</h:selectOneMenu>
											</td>
											<td style="text-align:right">
												<h:inputText value="#{ respostasAutoAvaliacaoMBean.metaAcaoFormacaoProducaoAcademica.numFormacaoDoutores }"  id="numFormacaoDoutores"  size="4" maxlength="3" immediate="true" onkeyup="formatarInteiro(this)" converter="#{ intConverter }" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }"/>
											</td>
											<td style="text-align:right">
												<h:inputText value="#{ respostasAutoAvaliacaoMBean.metaAcaoFormacaoProducaoAcademica.numFormacaoMestres }"   id="numFormacaoMestres"   size="4" maxlength="3" immediate="true" onkeyup="formatarInteiro(this)" converter="#{ intConverter }" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }"/>
											</td>
											<td style="text-align:right">
												<h:inputText value="#{ respostasAutoAvaliacaoMBean.metaAcaoFormacaoProducaoAcademica.qtdArtigosQualisA1A2 }" id="qtdArtigosQualisA1A2" size="4" maxlength="3" immediate="true" onkeyup="formatarInteiro(this)" converter="#{ intConverter }" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }"/>
											</td>
											<td style="text-align:right">
												<h:inputText value="#{ respostasAutoAvaliacaoMBean.metaAcaoFormacaoProducaoAcademica.qtdArtigosQualisB1B2 }" id="qtdArtigosQualisB1B2" size="4" maxlength="3" immediate="true" onkeyup="formatarInteiro(this)" converter="#{ intConverter }" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }"/>
											</td>
											<td style="text-align:right">
												<h:inputText value="#{ respostasAutoAvaliacaoMBean.metaAcaoFormacaoProducaoAcademica.qtdDemaisArtigos }"     id="qtdDemaisArtigos"     size="4" maxlength="3" immediate="true" onkeyup="formatarInteiro(this)" converter="#{ intConverter }" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }"/>
											</td>
											<td style="text-align:right">
												<h:inputText value="#{ respostasAutoAvaliacaoMBean.metaAcaoFormacaoProducaoAcademica.qtdPedidosPatente }"    id="qtdPedidosPatente"    size="4" maxlength="3" immediate="true" onkeyup="formatarInteiro(this)" converter="#{ intConverter }" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }"/>
											</td>
											<td style="text-align:right">
												<h:commandLink action="#{ respostasAutoAvaliacaoMBean.adicionarMetasAcoesFormacaoAcademica }" id="adicionarMetasAcoesFormacaoAcademica" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }">
													<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" title="Adicionar Meta"/>
												</h:commandLink>
											</td>
										</tr>
									</tbody>
								</table>
							</td> 
						</tr>
						<tr>
							<td colspan="2" class="subFormulario">Melhoria da Qualidade</td>
						</tr>
						<tr>
							<td colspan="2">
								<b>Quais as a��es que o Programa indica para a
								melhoria da qualidade de forma��o de Recursos Humanos e que
								contribua para o incremento do conceito do programa? Distribua as
								a��es propostas nos seguintes n�veis:</b>
							</td>
						</tr>
						<tr>
							<th valign="top">Docente:</th>
							<td> 
								<h:inputTextarea value="#{ respostasAutoAvaliacaoMBean.obj.sugestoesMelhoriaPrograma.docente}"
									id="docente" cols="100" rows="4" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }"/> </td>
						</tr>
						<tr>
							<th valign="top">Coordena��o do Programa:</th>
							<td> <h:inputTextarea value="#{ respostasAutoAvaliacaoMBean.obj.sugestoesMelhoriaPrograma.coordenacaoPrograma}"
									id="coordenacaoPrograma" cols="100" rows="4" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }"/> </td>
						</tr>
						<tr>
							<th valign="top">Dire��o do Centro:</th>
							<td> <h:inputTextarea value="#{ respostasAutoAvaliacaoMBean.obj.sugestoesMelhoriaPrograma.direcaoCentro}"
									id="direcaoCentro" cols="100" rows="4" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }"/> </td>
						</tr>
						<tr>
							<th valign="top">Pr�-reitoria de P�s-Gradua��o:</th>
							<td> <h:inputTextarea value="#{ respostasAutoAvaliacaoMBean.obj.sugestoesMelhoriaPrograma.proReitoriaPosGraduacao}"
									id="proReitoriaPosGraduacao" cols="100" rows="4" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }"/> </td>
						</tr>
						<tr>
							<th valign="top">Reitoria:</th>
							<td> <h:inputTextarea value="#{ respostasAutoAvaliacaoMBean.obj.sugestoesMelhoriaPrograma.reitoria}"
									id="reitoria" cols="100" rows="4" disabled="#{ respostasAutoAvaliacaoMBean.readOnly }"/> </td>
						</tr>
					</tbody>
				</table>
			</rich:tab>
			<rich:tab label="Pareceres Anteriores" rendered="#{ not empty respostasAutoAvaliacaoMBean.obj.pareceresAutoAvaliacao }">
				<table width="100%" class="formulario">
					<caption>Lista de Pareceres Cadastrados</caption>
					<tbody>
						<c:forEach items="#{ respostasAutoAvaliacaoMBean.obj.pareceresAutoAvaliacao }" var="item">
							<tr>
								<td class="subFormulario"><ufrn:format type="data" valor="${ item.cadastradoEm }" /> - Situa��o: ${ item.situacao }</td>
							</tr>
							<tr>
								<td>${ item.parecer }</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</rich:tab>
		</rich:tabPanel>
		<table class="formulario" width="100%">
		<tfoot>
			<tr>
				<td colspan="2"> 
					<h:commandButton value="Submeter e Enviar" action="#{respostasAutoAvaliacaoMBean.submeter}" id="cadastrar" rendered="#{ !respostasAutoAvaliacaoMBean.readOnly }"/>
					<h:commandButton value="Salvar" action="#{respostasAutoAvaliacaoMBean.salvar}" id="salvar" rendered="#{ !respostasAutoAvaliacaoMBean.readOnly }"/> 
					<h:commandButton value="<< Voltar" action="#{calendarioAplicacaoAutoAvaliacaoMBean.listar}" immediate="true" id="voltar" rendered="#{ respostasAutoAvaliacaoMBean.readOnly }"/>
					<h:commandButton value="Cancelar" action="#{respostasAutoAvaliacaoMBean.iniciarPreenchimento}" immediate="true" onclick="#{confirm}" id="cancelar" rendered="#{ !respostasAutoAvaliacaoMBean.readOnly }"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>	

	<br/>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	