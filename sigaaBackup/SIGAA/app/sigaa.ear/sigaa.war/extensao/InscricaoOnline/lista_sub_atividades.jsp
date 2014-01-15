<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>
	<h2><ufrn:subSistema /> &gt; Gerenciar Inscri��es</h2>
	<div class="descricaoOperacao">
		<br />
		<p> <strong>IMPORTANTE</strong> Nas inscri��es que estejam com o n�mero de vagas esgotado, a quantidade pode 
		ser aumentada atrav�s da opera��o "Alterar Inscri��o". </p>
		<br/>
	</div>
	
	<h:form id="formGerenciarInscricoesMiniAtividades">
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/adicionar.gif" />
		<h:commandLink action="#{inscricaoAtividade.preCadastrarInscricaoMiniAtividade}" value="Criar Inscri��o">
			<f:param name="idSubAtividade" value="#{inscricaoAtividade.subAtividadeSelecionada.id}" />
		</h:commandLink>
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Relat�rio de Inscritos
		<br/>
		<h:graphicImage value="/img/graduacao/concluir_programa.png" style="overflow: visible;" height="16" width="16"/>: Aceitar/Recusar Inscritos
		<h:graphicImage value="/img/biblioteca/sem_emprestimos_ativos.png" style="overflow: visible;" />: Listar Inscri��es Pendentes
		<h:graphicImage value="/img/listar.gif" style="overflow: visible;" />: Listar Inscritos
		<br/>
		<h:graphicImage value="/img/clock.png" style="overflow: visible;" />: Inscri��o Aberta
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Inscri��o
		<h:graphicImage value="/img/pesquisa/finalizar_projeto.png" style="overflow: visible;" />: Suspender Inscri��o
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Excluir Inscri��o
		
	</div>

	
		<c:set var="inscricoes" value="#{inscricaoAtividade.inscricoesSubAtividades}" />
		<c:choose>
			<c:when test="${not empty inscricoes}">

				<table id="tabelaInscricao" width="100%" class="listagem">

					<caption>
							Inscri��es Existentes para a Mini Atividade <br/>
							<h:outputText value="#{inscricaoAtividade.subAtividadeSelecionada.titulo}" />
					</caption>
					
					<thead>
						<tr>
							<th rowspan="2" width="2%" />
							<th rowspan="2" width="5%" style="text-align:center" valign="bottom">C�digo</th>
							<th rowspan="2" width="10%" style="text-align:center" valign="bottom">In�cio das Inscri��es</th>
							<th rowspan="2" width="10%" style="text-align:center" valign="bottom">Fim das Inscri��es</th>
                            <th colspan="2" width="8%" style="text-align:right; font-style: italic; font-size: 10px; padding-right: 10px;">Quantidade de Vagas</th>
                            <th colspan="2" width="8%" style="text-align:right; font-style: italic; font-size: 10px; padding-right: 10px;">Quantidade de Inscri��es</th>
							<th colspan="6" rowspan="2" width="6%" />
						</tr>
						<tr>
							<th width="5%" style="text-align:right">Total</th>
							<th width="3%" style="text-align:right">Restando</th>
							<th rowspan="2" style="text-align:right">Confirmadas</th>
                            <th rowspan="2" style="text-align:right">Aceitas</th>							
						</tr>
					</thead>
					<tbody>
					
						<c:forEach items="#{inscricoes}" var="inscricao" varStatus="count">
							<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td align="center">
									<h:graphicImage url="/img/clock.png" rendered="#{inscricao.aberta}" />
								</td>
								<td style="text-align:center">${inscricao.codigo}</td>
								<td style="text-align:center">
									<h:outputText value="#{inscricao.periodoInicio}">
										<f:convertDateTime pattern="dd/MM/yyyy" />
									</h:outputText> 
								</td>
								<td style="text-align:center">
									<h:outputText value="#{inscricao.periodoFim}">
										<f:convertDateTime pattern="dd/MM/yyyy" />
									</h:outputText> 
								</td>
	                            <td style="text-align:right">${inscricao.quantidadeVagas}</td>
	                            <td style="text-align:right">${inscricao.quantidadeVagasRestantes}</td>
	                            <td style="text-align:right">${inscricao.quantidadeConfirmados + inscricao.quantidadeAceitos}</td>
	                            <td style="text-align:right">${inscricao.quantidadeAceitos}</td>
								
								<td style="text-align: center;">
									<h:commandLink title="Relat�rio de Inscritos" action="#{inscricaoAtividade.exibirRelatorioInscritosSubAtividade}" 
												rendered="#{inscricaoAtividade.subAtividadeSelecionada.numeroInscritos > 0}" id="relatorioInscrito">
										<f:param name="idSubAtividade" value="#{inscricaoAtividade.subAtividadeSelecionada.id}" />
										<h:graphicImage url="/img/view.gif" />
									</h:commandLink>
								</td>
								
								<%-- 
								<td style="text-align: center;">
									<h:commandLink title="Listar Inscri��es" action="#{inscricaoAtividade.listarInscricoesSubAtividade}"
												 rendered="#{inscricaoAtividade.subAtividadeSelecionada.numeroInscritos > 0}" id="listarInscritos">
										<f:param name="idSubAtividade" value="#{inscricaoAtividade.subAtividadeSelecionada.id}" />
										<h:graphicImage url="/img/table_go.png"/>
									</h:commandLink>
								</td> --%>
								
								<td align="center">
									<h:commandLink title="Aceitar/Recusar Inscritos" immediate="true" id="aceitarInscritosSubAtv"
											action="#{inscricaoAtividade.listarParticipantesInscritos}" 
											rendered="#{inscricao.possuiInscritos && inscricao.quantidadeVagasRestantes > 0}">
										<f:param name="id" value="#{inscricao.id}" />
										<h:graphicImage url="/img/graduacao/concluir_programa.png" height="16" width="16" />
									</h:commandLink>
								</td>
								<td align="center">
							       <h:commandLink title="Listar Inscri��es Pendentes" immediate="true" id="listarPendentesSubAtividade"
                                               action="#{inscricaoAtividade.listarPendentes}">
                                           <f:param name="idInscricao" value="#{inscricao.id}" />
                                           <h:graphicImage url="/img/biblioteca/sem_emprestimos_ativos.png"/>
                                    </h:commandLink>
								</td>
								<td align="center">
							       <h:commandLink title="Listar Inscritos" immediate="true" id="listarInscritosSubAtv"
                                               action="#{inscricaoAtividade.listarInscritos}">
                                           <f:param name="idInscricao" value="#{inscricao.id}" />
                                           <h:graphicImage url="/img/listar.gif"/>
                                    </h:commandLink>
								</td>
								 
								<td align="right">
									<h:commandLink title="Alterar Inscri��o" action="#{inscricaoAtividade.preAlterarSubAtividades}" id="alterarInscricao" 
											immediate="true">
										<f:param name="id" value="#{inscricao.id}" />
										<h:graphicImage url="/img/alterar.gif" />
									</h:commandLink>
								</td>
								<td align="center">
									<c:choose>
										<c:when test="${inscricao.quantidadeConfirmados > 0 || inscricao.aberta}">
											<h:commandLink title="Suspender Inscri��o" action="#{inscricaoAtividade.preSuspender}" 
													id="suspenderInscricao" immediate="true">
												<f:param name="id" value="#{inscricao.id}" />
												<h:graphicImage url="/img/pesquisa/finalizar_projeto.png" />
											</h:commandLink>
										</c:when>
										<c:otherwise>
											<h:commandLink title="Excluir Inscri��o" action="#{inscricaoAtividade.excluirSubAtividades}" id="excluirInscricao"
													immediate="true" onclick="return confirm('Deseja excluir essa inscri��o?');">
												<f:param name="id" value="#{inscricao.id}" />
												<h:graphicImage url="/img/delete.gif" />
											</h:commandLink>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="14">
								<center>
									<h:commandButton value="<< Voltar" action="#{inscricaoAtividade.iniciarProcesso}" id="voltar" />
								</center>
							</td>
						</tr>
					</tfoot>
				</table>
			</c:when>
			<c:otherwise>
				<div style="color: red; text-align: center; width: 100%;">N�o existem inscri��es criadas</div>
				<br /><br /><br />
				<center>
					<h:commandButton value="<< Voltar" action="#{inscricaoAtividade.iniciarProcesso}" id="voltar" />
				</center>
			</c:otherwise>
		</c:choose>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>