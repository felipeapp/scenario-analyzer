<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
.rich-progress-bar-width { width: 800px;}
.rich-progress-bar-uploaded-dig {font-size: 16px;}
.rich-progress-bar-shell-dig {font-size: 16px;}
</style>
<f:view>
<a4j:keepAlive beanName="convocacaoVestibular"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Encerramento do Cadastramento de Discentes &gt; Resumo</h2>

<div class="descricaoOperacao">
	<p><b>Caro Usuário,</b></p>
	<p>Esta operação irá encerrar o cadastramento dos discentes excluindo todos discentes com status PRÉ-CADASTRO.<b>clique
	uma única vez</b> em "Confirmar Convocação", e aguarde até o fim
	do processamento.</p>
</div>
	
<h:form>
<div align="center">
	<a4j:region id="progressPanel">
		<rich:progressBar interval="1000" id="progressBar" minValue="0" maxValue="100"
			value="#{ convocacaoVagasRemanescentesVestibularMBean.percentualProcessado }"
			label="#{ convocacaoVagasRemanescentesVestibularMBean.mensagemProgresso }"
			reRenderAfterComplete="progressPanel">
			<f:facet name="initial">
				<a4j:outputPanel>
					<table class="visualizacao">
						<caption>Dados do Encerramento</caption>
						<tr>
							<th width="30%">Processo Seletivo Vestibular:</th>
							<td style="text-align: left;"><h:outputText id="psVest" value="#{convocacaoVagasRemanescentesVestibularMBean.obj.processoSeletivo.nome}"/></td>
						</tr>
						<tr>
							<td colspan="2">
								<table class="subFormulario" width="100%">
									<caption>Discentes que serão excluídos (${fn:length(convocacaoVagasRemanescentesVestibularMBean.cancelamentos)})</caption>
									<thead>
										<tr>
											<th style="text-align: left; width: 5%;">Ordem</th>
											<th style="text-align: left; width: 7%;">Nº Inscrição</th>
											<th style="text-align: center; width: 12%;">CPF</th>
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
														<td class="subFormulario" colspan="6"> <h:outputText value="#{c.convocacao.discente.matrizCurricular.descricao}"/> </td>
														<c:set var="_ordem" value="1"/>
													</tr>
												</c:if>
												
												<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
													<td style="text-align: left;">
														${_ordem}
														<c:set var="_ordem" value="${_ordem + 1}"/>
													</td>
													<td style="text-align: left;"><h:outputText value="#{c.convocacao.inscricaoVestibular.numeroInscricao}" /></td>
													<td style="text-align: center;">
														<h:outputText value="#{c.convocacao.discente.pessoa.cpf_cnpj}" >
															<f:converter converterId="convertCpf"/>
														</h:outputText>
													</td>
													
													<td style="text-align: left;"><h:outputText value="#{c.convocacao.discente.pessoa.nome}"/></td>
													<td style="text-align: left;"><h:outputText value="#{c.motivo.descricao}"/></td>
													<td style="text-align: left;"><h:outputText value="#{c.convocacao.discente.statusString}"/></td>
												</tr>
											</c:forEach>
										</c:when>
										<c:otherwise>
											<td colspan="3" style="text-align: center; color: red;">Nenhuma convocação anterior será cancelada.</td>
										</c:otherwise>
									</c:choose>
								</table>
							</td>
						</tr>
						<tr>
							<td colspan="4">
								<c:set var="exibirApenasSenha" value="true" scope="request"/>
								<div style="text-align: center; width: 100%" >
									<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
								</div>
							</td>
						</tr>
						<tfoot>
							<tr>
								<td colspan="2" style="text-align: center;">
									<a4j:commandButton value="Confirmar Encerramento" action="#{ convocacaoVagasRemanescentesVestibularMBean.confirmarEncerramentoCadastramento }" id="btnConfirmar"
										rendered="#{not empty convocacaoVagasRemanescentesVestibularMBean.convocacoes}" reRender="progressPanel" onclick="this.disabled=true; this.value='Por favor, aguarde...'"/>
									<h:commandButton value="<< Voltar" action="#{ convocacaoVagasRemanescentesVestibularMBean.formEncerrarCadastramento }" id="btnVoltar"/>
									<h:commandButton value="Cancelar" action="#{ convocacaoVagasRemanescentesVestibularMBean.cancelar }" id="btnCancelar" onclick="#{confirm}"/>
								</td>
							</tr>
						</tfoot>
					</table>
					<c:if test="${ legenda }">
						<div style="text-align: left">
						<ul><b>LEGENDA:</b>
							<li><b>*</b> Convocado para preenchimento de vaga remanescente no grupo de cotas.</li>
						</ul>
						</div>
					</c:if>
				</a4j:outputPanel>
			</f:facet>
			<f:facet name="complete">
	        	<br/>
	        	<a4j:outputPanel>
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
	            </a4j:outputPanel>
	        </f:facet>
		</rich:progressBar>
	</a4j:region>
</div>

</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>