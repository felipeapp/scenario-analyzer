<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%-- essa página não faz sento existir, já existe lista_sub_atividades.jsp que lista as inscrições, para que lista_inscricoes_sub_atividades ?
	<%@include file="/portais/docente/menu_docente.jsp" %>
	<h2><ufrn:subSistema /> &gt; Acompanhar Inscritos</h2>
	<div class="descricaoOperacao">
		<b>Atenção:</b>
		<br/>O número de vagas da inscrição pode ser aumentado através da operação "Alterar Inscrição" 
		localizado em "Abrir/Alterar Inscrições On-line".
	</div>
	<div class="infoAltRem">
		<h:graphicImage value="/img/clock.png" style="overflow: visible;"/>: Inscrição Aberta
	    <h:graphicImage url="/img/graduacao/concluir_programa.png" height="16" width="16" style="overflow: visible;"/>: Aceitar/Recusar Inscritos
	    <h:graphicImage url="/img/listar.gif" height="18" width="18" style="overflow: visible;"/>: Listar Inscritos
	    <h:graphicImage url="/img/biblioteca/sem_emprestimos_ativos.png" height="18" width="18" style="overflow: visible;"/>: Listar Inscrições Pendentes
	</div>

	<h:form id="form1">
		<c:set var="inscricoes" value="#{inscricaoAtividade.inscricoesAtividades}" />		
		<c:choose>
			<c:when test="${not empty inscricoes}">

				<table id="tabelaInscricao" width="100%" class="listagem">
					<caption>Lista de inscrições com período aberto ou que possuam inscritos(ATIVIDADES)</caption>
					<thead>
						<tr>
							<th rowspan="2" width="2%" />
							<th rowspan="2" width="5%" style="text-align:center" valign="bottom">Código</th>
							<th rowspan="2" width="10%" style="text-align:center" valign="bottom">Início das Inscrições</th>
							<th rowspan="2" width="10%" style="text-align:center" valign="bottom">Fim das Inscrições</th>
							<th colspan="2" width="8%" style="text-align:right; font-style: italic; font-size: 10px; padding-right: 10px;">Quantidade de Vagas</th>
							<th colspan="2" width="8%" style="text-align:right; font-style: italic; font-size: 10px; padding-right: 10px;">Quantidade de Inscrições</th>
							<th width="2%"/>
							<th width="2%"/>
							<th width="2%"/>
						</tr>
						<tr>
							<th width="5%" style="text-align:right">Total</th>
							<th width="3%" style="text-align:right">Restando</th>
							<th width="5%" style="text-align:right">Confirmadas</th>
							<th width="5%" style="text-align:right">Aceitas</th>
							<th width="2%"/>
							<th width="2%"/>
							<th width="2%"/>
						</tr>
					</thead>
					<tbody>
						<c:set var="atividade" value=""/>
						<c:set var="flag" value="false"/>
						<c:forEach items="#{inscricoes}" var="inscricao" varStatus="count">
							
						    <c:if test="${ atividade != inscricao.atividade.id }">
                                <c:set var="atividade" value="${ inscricao.atividade.id }"/>
								<tr>
									<td class="subFormulario" colspan="11">${inscricao.atividade.titulo}</td>
								</tr>
                            </c:if>
							
							<c:if test="${atividade == inscricao.atividade.id}">
								<c:set var="flag" value="${flag ? false : true}" />
								
							   <tr class="${flag ? 'linhaPar' : 'linhaImpar'}">
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
									<td align="center">
										<h:commandLink title="Aceitar/Recusar Inscritos" immediate="true" id="aceitarInscritos"
												action="#{inscricaoAtividade.listarParticipantesInscritos}" 
												rendered="#{inscricao.possuiInscritos && inscricao.quantidadeVagasRestantes > 0}">
											<f:param name="id" value="#{inscricao.id}" />
											<h:graphicImage url="/img/graduacao/concluir_programa.png" height="16" width="16" />
										</h:commandLink>
									</td>
									<td align="center">
								       <h:commandLink title="Listar Inscrições Pendentes" immediate="true" id="listarPendentes"
	                                               action="#{inscricaoAtividade.listarPendentes}">
	                                           <f:param name="idInscricao" value="#{inscricao.id}" />
	                                           <h:graphicImage url="/img/biblioteca/sem_emprestimos_ativos.png"/>
	                                    </h:commandLink>
									</td>
									<td align="center">
								       <h:commandLink title="Listar Inscritos" immediate="true" id="listarInscritos"
	                                               action="#{inscricaoAtividade.listarInscritos}">
	                                           <f:param name="idInscricao" value="#{inscricao.id}" />
	                                           <h:graphicImage url="/img/listar.gif"/>
	                                    </h:commandLink>
									</td>
								</tr>
							 </c:if>
							</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td align="center" colspan="11">
								<h:commandButton value="<< Voltar" action="#{inscricaoAtividade.iniciarProcesso}" />
							</td>
						</tr>
					</tfoot>
				</table>
			</c:when>
			<c:otherwise>
				<center><span style="color: red">Não existem inscrições criadas</span></center>
			</c:otherwise>
		</c:choose>
		
		
		
		<br/>
		<br/>
		<br/>
		<br/>
		
		
		
		
		
		
		<c:set var="inscricoesSubAtividades" value="#{inscricaoAtividade.inscricoesSubAtividades}" />		
		<c:choose>
			<c:when test="${not empty inscricoesSubAtividades}">

				<table id="tabelaInscricaoSubAtividade" width="100%" class="listagem">
					<caption>Lista de inscrições com período aberto ou que possuam inscritos(MINI ATIVIDADES)</caption>
					<thead>
						<tr>
							<th rowspan="2" width="2%" />
							<th rowspan="2" width="5%" style="text-align:center" valign="bottom">Código</th>
							<th rowspan="2" width="10%" style="text-align:center" valign="bottom">Início das Inscrições</th>
							<th rowspan="2" width="10%" style="text-align:center" valign="bottom">Fim das Inscrições</th>
							<th colspan="2" width="8%" style="text-align:right; font-style: italic; font-size: 10px; padding-right: 10px;">Quantidade de Vagas</th>
							<th colspan="2" width="8%" style="text-align:right; font-style: italic; font-size: 10px; padding-right: 10px;">Quantidade de Inscrições</th>
							<th width="2%"/>
							<th width="2%"/>
							<th width="2%"/>
						</tr>
						<tr>
							<th width="5%" style="text-align:right">Total</th>
							<th width="3%" style="text-align:right">Restando</th>
							<th width="5%" style="text-align:right">Confirmadas</th>
							<th width="5%" style="text-align:right">Aceitas</th>
							<th width="2%"/>
							<th width="2%"/>
							<th width="2%"/>
						</tr>
					</thead>
					<tbody>
						<c:set var="subAtividade" value=""/>
						<c:set var="flag" value="false"/>
						<c:forEach items="#{inscricoesSubAtividades}" var="inscricao" varStatus="count">
							
						    <c:if test="${ subAtividade != inscricao.subAtividade.id }">
                                <c:set var="subAtividade" value="${ inscricao.subAtividade.id }"/>
								<tr>
									<td class="subFormulario" colspan="11">
										Atividade: ${inscricao.atividade.titulo} <br/>
										Mini Atividade: ${inscricao.subAtividade.titulo}
									</td>
								</tr>
                            </c:if>
							
							<c:if test="${subAtividade == inscricao.subAtividade.id}">
								<c:set var="flag" value="${flag ? false : true}" />
								
							   <tr class="${flag ? 'linhaPar' : 'linhaImpar'}">
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
									<td align="center">
										<h:commandLink title="Aceitar/Recusar Inscritos" immediate="true" id="aceitarInscritosSubAtv"
												action="#{inscricaoAtividade.listarParticipantesInscritos}" 
												rendered="#{inscricao.possuiInscritos && inscricao.quantidadeVagasRestantes > 0}">
											<f:param name="id" value="#{inscricao.id}" />
											<h:graphicImage url="/img/graduacao/concluir_programa.png" height="16" width="16" />
										</h:commandLink>
									</td>
									<td align="center">
								       <h:commandLink title="Listar Inscrições Pendentes" immediate="true" id="listarPendentesSubAtividade"
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
								</tr>
							 </c:if>
							</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td align="center" colspan="11">
								<h:commandButton value="<< Voltar" action="#{inscricaoAtividade.iniciarProcesso}" />
							</td>
						</tr>
					</tfoot>
				</table>
			</c:when>
			<c:otherwise>
				<center><span style="color: red">Não existem inscrições criadas</span></center>
			</c:otherwise>
		</c:choose>
		
		
		
		<br/>
		<br/>
		
		
		--%>
		
		
		

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>