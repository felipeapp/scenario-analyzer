<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
.rich-progress-bar-width { width: 800px;}
.rich-progress-bar-uploaded-dig {font-size: 16px;}
.rich-progress-bar-shell-dig {font-size: 16px;}
</style>
<f:view>
<a4j:keepAlive beanName="convocacaoVestibular"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Convocação de Candidatos para Vagas Remanescentes &gt; Resumo</h2>

<div class="descricaoOperacao">
	<p><b>Caro Usuário,</b></p>
	<p>Esta operação permite realizar a convocação dos alunos aprovados para as vagas remanescentes do Vestibular.</p>
	<p>Informe os valores no formulário, <b>clique
	uma única vez</b> em "Confirmar Convocação", e aguarde até o fim
	do processamento.</p>
</div>
	
<h:form>
<div align="center">
	<a4j:region id="progressPanel">
		<rich:progressBar interval="500" id="progressBar" minValue="0" maxValue="100"
			value="#{ convocacaoVagasRemanescentesVestibularMBean.percentualProcessado }"
			label="#{ convocacaoVagasRemanescentesVestibularMBean.mensagemProgresso }"
			reRenderAfterComplete="progressPanel">
			<f:facet name="initial">
				<a4j:outputPanel>
					<table class="visualizacao">
						<caption>Resumo da Convocação</caption>
						<tr>
							<th width="30%">Processo Seletivo Vestibular:</th>
							<td style="text-align: left;"><h:outputText id="psVest" value="#{convocacaoVagasRemanescentesVestibularMBean.obj.processoSeletivo.nome}"/></td>
						</tr>
						<tr>
							<th>Descrição:</th>
							<td style="text-align: left;"><h:outputText id="descricao" value="#{convocacaoVagasRemanescentesVestibularMBean.obj.descricao}"/></td>
						</tr>
						<tr>
							<th>Data da Convocação:</th>
							<td style="text-align: left;">
								<h:outputText id="data" value="#{convocacaoVagasRemanescentesVestibularMBean.obj.dataConvocacao}" >
									<f:converter converterId="convertData" />
								</h:outputText>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<table class="subFormulario" width="100%">
									<caption>Convocações (${fn:length(convocacaoVagasRemanescentesVestibularMBean.convocacoes)})</caption>
									<thead>
										<tr>
											<th style="text-align: left; width: 5%;">Ordem</th>
											<th style="text-align: left; width: 5%;">Nº Inscrição</th>
											<th style="text-align: center; width: 12%;">CPF</th>
											<th style="text-align: left;">Nome</th>
											<th style="text-align: left;">Classificação</th>
											<th style="text-align: left; width: 10%;">Ingresso</th>
											<th style="text-align: left; width: 15%;">Tipo de Convocação</th>
											<th style="text-align: left; width: 5%;">Dentro das Vagas</th>
											<th style="text-align: left; width: 7%;">Grupo de Cotas</th>
										</tr>
									</thead>
									<c:choose>
										<c:when test="${not empty convocacaoVagasRemanescentesVestibularMBean.convocacoes}">
											<c:set var="_matriz_loop" />
											<c:set var="_ordem" value="1"/>
											<c:set var="legenda" value="${false}"/>
											<c:forEach items="#{convocacaoVagasRemanescentesVestibularMBean.convocacoes}" var="conv" varStatus="loop">
												<c:if test="${_matriz_loop != conv.discente.matrizCurricular.id}">
													<c:set var="_matriz_loop" value="${conv.discente.matrizCurricular.id}"/>
													<c:set var="_ordem" value="1"/>
													<tr>
														<td class="subFormulario" colspan="9">
															<h:outputText value="#{conv.discente.matrizCurricular.descricao}"/> 
														</td>
													</tr>
												</c:if>
											
												<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
													<td style="text-align: left;">
														${_ordem}
														<c:set var="_ordem" value="${_ordem + 1}"/>
													</td>
													<td style="text-align: left;"><h:outputText value="#{conv.inscricaoVestibular.numeroInscricao}" /></td>
													<td style="text-align: center;">
														<h:outputText value="#{conv.discente.pessoa.cpf_cnpj}" >
															<f:converter converterId="convertCpf"/>
														</h:outputText>
													</td>
													<td style="text-align: left;">
														<h:outputText value="#{conv.discente.pessoa.nome}"/>
														<c:if test="${conv.pendenteCancelamento}">
															<br/><span style="color:red;">(o discente não terá sua convocação anterior cancelada por estar matriculado em disciplinas)</span>
														</c:if> 
													</td>
													<td style="text-align: right;"><h:outputText value="#{conv.resultado.classificacao > 0 ? conv.resultado.classificacao : 'AMA'}"/></td>
													<td style="text-align: left;"><h:outputText value="#{conv.discente.periodoIngresso}"/>º semestre</td>
													<td style="text-align: left;"><h:outputText value="#{conv.tipoDesc}"/></td>
													<td style="text-align: left;"><ufrn:format type="simNao" valor="${conv.dentroNumeroVagas}"/></td>
													<td style="text-align: left;">
														<h:outputText value="#{conv.grupoCotaConvocado.descricao}" rendered="#{ conv.grupoCotaConvocado != null && !conv.grupoCotaRemanejado }"/>
														<h:outputText value="#{conv.grupoCotaConvocado.descricao}*" rendered="#{ conv.grupoCotaConvocado.descricao != null && conv.grupoCotaRemanejado }" title="Convocado para preenchimento de vaga remanescente no grupo de cotas"/>
														<h:outputText value="AC" rendered="#{ conv.grupoCotaConvocado == null && !conv.grupoCotaRemanejado }"/>
														<h:outputText value="AC+" rendered="#{ conv.grupoCotaConvocado == null && conv.grupoCotaRemanejado }" title="Candidato cotista convocado aprovado por mérito"/>
														<c:if test="${ not empty conv.grupoCotaConvocado.descricao }"><c:set var="legenda" value="${true}"/></c:if>
													</td>
												</tr>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<td colspan="7" style="text-align: center; color: red;">Nenhum candidato será convocado.</td>
										</c:otherwise>
									</c:choose>
								</table>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<table class="subFormulario" width="100%">
									<caption>Cancelamentos (${fn:length(convocacaoVagasRemanescentesVestibularMBean.cancelamentos)})</caption>
									<thead>
										<tr>
											<th style="text-align: center; width: 15%;">Matrícula</th>
											<th style="text-align: left;">Nome</th>
											<th style="text-align: left; ">Motivo do Cancelamento</th>
											<th style="text-align: left; ">Status</th>
										</tr>
									</thead>
									<c:choose>
										<c:when test="${not empty convocacaoVagasRemanescentesVestibularMBean.cancelamentos}">
											<c:set var="_matriz_loop" />
											
											<c:forEach items="#{convocacaoVagasRemanescentesVestibularMBean.cancelamentos}" var="c" varStatus="loop">
												<c:if test="${_matriz_loop != c.convocacao.discente.matrizCurricular.id}">
													<c:set var="_matriz_loop" value="${c.convocacao.discente.matrizCurricular.id}"/>
													<tr>
														<td class="subFormulario" colspan="4"> <h:outputText value="#{c.convocacao.discente.matrizCurricular.descricao}"/> </td>
													</tr>
												</c:if>
												
												<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
													<td style="text-align: center;">
														<h:outputText value="#{c.convocacao.discente.matricula}" />
														<h:outputText value="NÃO CADASTRADO" rendered="#{c.convocacao.discente.matricula == null}" />
													</td>
													<td style="text-align: left;"><h:outputText value="#{c.convocacao.discente.pessoa.nome}"/></td>
													<td style="text-align: left;"><h:outputText value="#{c.motivo.descricao}"/></td>
													<td style="text-align: left;"><h:outputText value="#{c.convocacao.discente.statusString}"/></td>
												</tr>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<td colspan="4" style="text-align: center; color: red;">Nenhuma convocação anterior será cancelada.</td>
										</c:otherwise>
									</c:choose>
								</table>
							</td>
						</tr>
						<tr>
							<td colspan="2" style="text-align: center">
								<c:set var="exibirApenasSenha" value="true" scope="request"/>
								<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
							</td>
						</tr>
						<tfoot>
							<tr>
								<td colspan="2" style="text-align: center;">
									<a4j:commandButton value="Confirmar Convocação" action="#{ convocacaoVagasRemanescentesVestibularMBean.confirmar }" id="btnConfirmar"
										rendered="#{not empty convocacaoVagasRemanescentesVestibularMBean.convocacoes}" reRender="progressPanel" onclick="this.disabled=true; this.value='Por favor, aguarde...'"/>
									<h:commandButton value="<< Voltar" action="#{ convocacaoVagasRemanescentesVestibularMBean.telaQuadroVagas }" id="btnVoltar"/>
									<h:commandButton value="Cancelar" action="#{ convocacaoVagasRemanescentesVestibularMBean.cancelar }" id="btnCancelar" onclick="#{confirm}"/>
								</td>
							</tr>
						</tfoot>
					</table>
					<c:if test="${ legenda }">
						<div style="text-align: left">
						<ul><b>LEGENDA:</b>
							<c:forEach items="#{ convocacaoVagasRemanescentesVestibularMBean.gruposCotas }" var="grupo">
								<li><b>${ grupo.descricao }</b> - ${ grupo.descricaoDetalhada }</li>
							</c:forEach>
							<li><b>AC</b> - Ampla Concorrência.</li>
							<li><b>*</b> - Convocado para preenchimento de vaga remanescente no grupo de cotas.</li>
							<li><b>+</b> - Candidato cotista convocado aprovado por mérito.</li>
						</ul>
						</div>
					</c:if>
				</a4j:outputPanel>
			</f:facet>
			<f:facet name="complete">
	        	<br/>
	        	<h:outputText>
	        		<br/><br/>
	            	<table class="formulario" width="80%">
						<caption>
							<c:if test="${convocacaoVagasRemanescentesVestibularMBean.errosConvocacao != null}">Ocorreu um problema no processamento das notas.</c:if>
	        				<c:if test="${convocacaoVagasRemanescentesVestibularMBean.errosConvocacao == null}">Processamento Concluído.</c:if>
						</caption>
						<tr>
							<th class="rotulo" width="25%">Processo Seletivo / Vestibular:</th>
							<td>
								<h:outputText value="#{convocacaoVagasRemanescentesVestibularMBean.obj.processoSeletivo.nome}"/>
							</td>
						</tr>
						<tr>
							<th class="rotulo">Descrição:</th>
							<td>
								<h:outputText value="#{convocacaoVagasRemanescentesVestibularMBean.obj.descricao}" />
							</td>
						</tr>
						<tr>
							<th class="rotulo">Data da Convocação:</th>
							<td>
								<h:outputText value="#{convocacaoVagasRemanescentesVestibularMBean.obj.dataConvocacao}"/>
							</td>
						</tr>
						<tfoot>
							<tr>
								<td colspan="2">
									<h:commandButton value="Cancelar" action="#{ convocacaoVagasRemanescentesVestibularMBean.cancelar }" id="btnCancelar2" onclick="#{confirm}" immediate="true"/>
								</td>
							</tr>
						</tfoot>
					</table>
	            </h:outputText>
	        </f:facet>
		</rich:progressBar>
	</a4j:region>
</div>

</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>