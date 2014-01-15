<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	<h2><ufrn:subSistema /> &gt; Quantitativo de Inscritos em Cursos e Eventos de Extensão pela Área Pública</h2>
    
    
    <div class="descricaoOperacao">
    	Lista de inscritos por ordem de Data de Cadastro.<br/>
    	Significados das situações dos inscritos na Ação de Extensão:
    	<br/>
    	<br/>    	
    	<b>Aceitas:</b> Indica que a inscrição foi aceita pela coordenação da ação de extensão e assegura a participação da pessoa no curso/evento.<br/>
    	<b>Confirmadas:</b> Indica que o participante confirmou a participação respondendo ao e-mail que recebeu do sistema. Sua inscrição poderá ser aceita ou recusada pela coordenação do curso/evento.<br/>
    	<b>Pendentes:</b> Indica que o participante ainda não respondeu ao e-mail que recebeu do sistema.<br/>
    	<b>Recusadas:</b> Indica que a coordenação do curso/evento negou a inscrição da pessoa.<br/>
    	<b>Canceladas:</b> Indica que o participante se inscreveu, mas desistiu do evento e cancelou a inscrição através do portal público.<br/>
    </div>
    
    
    <c:if test="${ not empty inscricaoAtividade.obj.atividade }">
    	<c:set var="atv" value="${inscricaoAtividade.obj.atividade}" />
    </c:if>
    
    <c:if test="${ not empty inscricaoAtividade.obj.subAtividade }">
    	<c:set var="atv" value="${inscricaoAtividade.obj.subAtividade.atividade}" />
    </c:if>
    
	
		<table class="formulario" width="80%">
		  <caption>Dados da ação de Extensão</caption>
			<tr>			
				<th><b>Código:</b></th>
				<td>${atv.codigo}</td>
			</tr>
			<tr>
				<th><b>Título:</b></th>
				<td>${atv.titulo}</td>
			</tr>
			
			 <c:if test="${ not empty inscricaoAtividade.obj.subAtividade }">
				<tr>
					<th><b>Mini Atividade:</b></th>
					<td>${inscricaoAtividade.obj.subAtividade.titulo}</td>
				</tr>
			</c:if>
			
			<tr>
				<th><b>Coordenação:</b></th>
				<td>${atv.coordenacao.servidor.pessoa.nome}</td>
			</tr>
			<tr>
				<th><b>Período:</b></th>
				<td>
					<fmt:formatDate value="${atv.dataInicio}" pattern="dd/MM/yyyy"/>										 
					até 
					<fmt:formatDate value="${atv.dataFim}" pattern="dd/MM/yyyy"/>
					
				</td>
			</tr>
		</table>
<br/>


    <div class="infoAltRem">
        <h:graphicImage value="/img/monitoria/businessman_view.png" style="overflow: visible;" id="labViewInscricao"/> Visualizar Inscrição
        <h:graphicImage value="/img/email_go.png" style="overflow: visible;" id="labReenviarInscricao"/>: Reenviar Senha de Acesso
        <h:graphicImage value="/img/view.gif" style="overflow: visible;" id="labVisualizarArquivo"/>: Visualizar Arquivo
        <br/>
        <h:graphicImage value="/img/extensao/user1_refresh.png" style="overflow: visible;" id="labAlterarDadosInscricao"/>: Alterar Dados da Inscrição
        <h:graphicImage value="/img/questionario.png" style="overflow: visible;" id="labViewQuestionario"/> Visualizar Questionário
    </div>

	<c:set value="#{inscricaoAtividade.inscricoesAceitas}" var="aceitos" />
	<c:set value="#{inscricaoAtividade.inscricoesConfirmadas}" var="confirmados" />
	<c:set value="#{inscricaoAtividade.inscricoesCanceladas}" var="cancelados" />
	<c:set value="#{inscricaoAtividade.inscricoesRecusadas}" var="recusados" />
	<c:set value="#{inscricaoAtividade.inscricoesNaoConfirmadas}" var="naoConfirmados" />
	
<h:form id="form">
	<table class="listagem" width="100%">
			<caption>Lista das Inscrições nessa Ação</caption>
			<thead>
				<tr>
					<th width="1%">Nº</th>
					<th width="17%">Data do Cadastro</th>
					<th width="40%">Nome Completo</th>
					<th width="20%">E-mail</th>
					<th width="20%">Instituição</th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="10" class="subFormulario">Inscrições ACEITAS (${fn:length(aceitos)})</td>
				</tr>
				<c:if test="${empty aceitos}">
					<tr>
						<td colspan="9">Não existem inscrições <b>aceitas</b> nesta ação</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="#{aceitos}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${status.count}</td>
						<td><fmt:formatDate value="${participante.dataCadastro}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'Não informada' : participante.instituicao}</td>
						<td width="2%">
							<h:commandLink title="Visualizar inscrição" immediate="true" id="viewInformacoesAceito"
								action="#{inscricaoAtividade.visualizarDadosInscrito}">
								<f:param name="id" value="#{participante.id}" />
								<h:graphicImage url="/img/monitoria/businessman_view.png" />
							</h:commandLink>
						</td>
						<td width="2%">
							<h:commandLink title="Alterar Dados da Inscrição" immediate="true" id="alterarDadosInformacoesAceito"
								action="#{inscricaoAtividade.preAlterarDadosInscrito}">
								<f:param name="id" value="#{participante.id}" />
								<h:graphicImage url="/img/extensao/user1_refresh.png" />
							</h:commandLink>
						</td>
						<td width="2%">
                           <h:commandLink title="Reenviar senha de acesso" immediate="true" id="reenviarSenhaAceito"
                                   action="#{inscricaoParticipantes.reenviarSenhaParticipanteOnline}" rendered="#{ !participante.cancelado }"
                                   onclick="return confirm('Confirma o Reenvio da Senha de Acesso?');">
                               <f:param name="id" value="#{participante.id}" />
                               <h:graphicImage url="/img/email_go.png"/>
                           </h:commandLink>
                         </td>
 						 <td width="2%">
							<h:commandLink title="Visualizar arquivo: #{ participante.descricaoArquivo }" immediate="true" id="visualizarArquivoAceito"
									action="#{inscricaoParticipantes.viewArquivo}" 
									rendered="#{participante.idArquivo > 0}">
								<f:param name="idArquivo" value="#{participante.idArquivo}" />
								<h:graphicImage url="/img/view.gif"/>
							</h:commandLink>
						 </td>
						 <td width="2%">
							<h:commandLink title="Visualizar Questionário" id="cmdLinkVisualizarQuestionarioAceita" 
									rendered="#{participante.questionarioRespostas != null}"
									action="#{inscricaoParticipantes.viewRespostaParticipanteQuestionario}">
									<f:param name="idQuestionario" value="#{participante.questionarioRespostas.id}" />
								<h:graphicImage url="/img/questionario.png"/>
							</h:commandLink>
						 </td>									
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="10" class="subFormulario">Inscrições CONFIRMADAS (${fn:length(confirmados)})</td>
				</tr>
				<c:if test="${empty confirmados}">
					<tr>
						<td colspan="9">Não existem inscrições <b>confirmadas</b> nesta ação</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="#{confirmados}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${status.count}</td>
						<td><fmt:formatDate value="${participante.dataCadastro}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'Não informada' : participante.instituicao}</td>
						<td width="2%">
                            <h:commandLink title="Visualizar inscrição" immediate="true" id="viewInformacoesConfirmado"
                                action="#{inscricaoAtividade.visualizarDadosInscrito}">
                                <f:param name="id" value="#{participante.id}" />
                                <h:graphicImage url="/img/monitoria/businessman_view.png" />
                            </h:commandLink>
                        </td>
                        <td width="2%">
							<h:commandLink title="Alterar Dados da Inscrição" immediate="true" id="alterarDadosInformacoesConfirmado"
								action="#{inscricaoAtividade.preAlterarDadosInscrito}">
								<f:param name="id" value="#{participante.id}" />
								<h:graphicImage url="/img/extensao/user1_refresh.png" />
							</h:commandLink>
						</td>
                        <td width="2%">
                           <h:commandLink title="Reenviar senha de acesso" immediate="true" id="reenviarSenhaConfirmado"
                                   action="#{inscricaoParticipantes.reenviarSenhaParticipanteOnline}" rendered="#{ !participante.cancelado }"
                                   onclick="return confirm('Confirma o Reenvio da Senha de Acesso?');">
                               <f:param name="id" value="#{participante.id}" />
                               <h:graphicImage url="/img/email_go.png"/>
                           </h:commandLink>
                         </td>
 						 <td width="2%">
							<h:commandLink title="Visualizar arquivo: #{ participante.descricaoArquivo }" immediate="true" id="visualizarArquivoConfirmado"
									action="#{inscricaoParticipantes.viewArquivo}" 
									rendered="#{participante.idArquivo > 0}">
								<f:param name="idArquivo" value="#{participante.idArquivo}" />
								<h:graphicImage url="/img/view.gif"/>
							</h:commandLink>
						 </td>
						 <td width="2%">
							<h:commandLink title="Visualizar Questionário" id="cmdLinkVisualizarQuestionarioConfirmado" 
								rendered="#{participante.questionarioRespostas != null}"
								action="#{inscricaoParticipantes.viewRespostaParticipanteQuestionario}">
								<h:graphicImage url="/img/questionario.png"/>
								<f:param name="idQuestionario" value="#{participante.questionarioRespostas.id}" />
							</h:commandLink>
						 </td>								
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="10" class="subFormulario">Inscrições RECUSADAS (${fn:length(recusados)})</td>
				</tr>
				<c:if test="${empty recusados}">
					<tr>
						<td colspan="9">Não existem inscrições <b>recusadas</b> nesta ação</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="#{recusados}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${status.count}</td>
						<td><fmt:formatDate value="${participante.dataCadastro}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'Não informada' : participante.instituicao}</td>
                        <td width="2%">
                            <h:commandLink title="Visualizar inscrição" immediate="true" id="viewInformacoesRecusado"
                                action="#{inscricaoAtividade.visualizarDadosInscrito}">
                                <f:param name="id" value="#{participante.id}" />
                                <h:graphicImage url="/img/monitoria/businessman_view.png" />
                            </h:commandLink>
                        </td>
                        <td width="2%">
                           <h:commandLink title="Reenviar senha de acesso" immediate="true" id="reenviarSenhaRecusado"
                                   action="#{inscricaoParticipantes.reenviarSenhaParticipanteOnline}" rendered="#{ !participante.cancelado }"
                                   onclick="return confirm('Confirma o Reenvio da Senha de Acesso?');">
                               <f:param name="id" value="#{participante.id}" />
                               <h:graphicImage url="/img/email_go.png"/>
                           </h:commandLink>
                         </td>
                         <td width="2%">
                         	&nbsp;
                         </td>
                         <td width="2%">
							<h:commandLink title="Visualizar Questionário" id="cmdLinkVisualizarQuestionarioRecusados" 
								rendered="#{participante.questionarioRespostas != null}"
								action="#{inscricaoParticipantes.viewRespostaParticipanteQuestionario}">
								<h:graphicImage url="/img/questionario.png"/>
								<f:param name="idQuestionario" value="#{participante.questionarioRespostas.id}" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="10" class="subFormulario">Inscrições PENDENTES (${fn:length(naoConfirmados)})</td>
				</tr>
				<c:if test="${empty naoConfirmados}">
					<tr>
						<td colspan="9">Não existem inscrições <b>pendentes de confirmação</b> nesta ação</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="#{naoConfirmados}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${status.count}</td>
						<td><fmt:formatDate value="${participante.dataCadastro}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'Não informada' : participante.instituicao}</td>
                        <td width="2%">
                            <h:commandLink title="Visualizar inscrição" immediate="true" id="viewInformacoesInscritos"
                                action="#{inscricaoAtividade.visualizarDadosInscrito}">
                                <f:param name="id" value="#{participante.id}" />
                                <h:graphicImage url="/img/monitoria/businessman_view.png" />
                            </h:commandLink>
                        </td>
                        <td width="2%">
							<h:commandLink title="Alterar Dados da Inscrição" immediate="true" id="alterarDadosInformacoesPendentes"
								action="#{inscricaoAtividade.preAlterarDadosInscrito}">
								<f:param name="id" value="#{participante.id}" />
								<h:graphicImage url="/img/extensao/user1_refresh.png" />
							</h:commandLink>
						</td>
                        <td width="2%">
                           <h:commandLink title="Reenviar senha de acesso" immediate="true" id="reenviarSenhaInscritos"
                                   action="#{inscricaoParticipantes.reenviarSenhaParticipanteOnline}" rendered="#{ !participante.cancelado }"
                                   onclick="return confirm('Confirma o Reenvio da Senha de Acesso?');">
                               <f:param name="id" value="#{participante.id}" />
                               <h:graphicImage url="/img/email_go.png"/>
                           </h:commandLink>
                         </td>
                         <td width="2%">
                         	&nbsp;
                         </td>
                         <td width="2%">
							<h:commandLink title="Visualizar Questionário" id="cmdLinkVisualizarQuestionarioPendentes" 
									rendered="#{participante.questionarioRespostas != null}"
									action="#{inscricaoParticipantes.viewRespostaParticipanteQuestionario}">
								<h:graphicImage url="/img/questionario.png"/>
								<f:param name="idQuestionario" value="#{participante.questionarioRespostas.id}" />
							</h:commandLink>
						</td>	
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="10" class="subFormulario">Inscrições CANCELADAS (${fn:length(cancelados)})</td>
				</tr>
				<c:if test="${empty cancelados}">
					<tr>
						<td colspan="9">Não existem inscrições <b>canceladas</b> pelo usuário nesta ação</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="#{cancelados}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${status.count}</td>
						<td><fmt:formatDate value="${participante.dataCadastro}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'Não informada' : participante.instituicao}</td>
                        <td width="2%">
                            <h:commandLink title="Visualizar inscrição" immediate="true" id="viewInformacoesCancelado"
                                action="#{inscricaoAtividade.visualizarDadosInscrito}">
                                <f:param name="id" value="#{participante.id}" />
                                <h:graphicImage url="/img/monitoria/businessman_view.png" />
                            </h:commandLink>
                        </td>
                        <td width="2%">
                           <h:commandLink title="Reenviar senha de acesso" immediate="true" id="reenviarSenhaCancelado"
                                   action="#{inscricaoParticipantes.reenviarSenhaParticipanteOnline}" rendered="#{ !participante.cancelado }"
                                   onclick="return confirm('Confirma o Reenvio da Senha de Acesso?');">
                               <f:param name="id" value="#{participante.id}" />
                               <h:graphicImage url="/img/email_go.png"/>
                           </h:commandLink>
                         </td>
                         <td width="2%">
                         	&nbsp;
                         </td>
                         <td width="2%">
							<h:commandLink title="Visualizar Questionário" id="cmdLinkVisualizarQuestionarioCancelado" 
									rendered="#{participante.questionarioRespostas != null}"
									action="#{inscricaoParticipantes.viewRespostaParticipanteQuestionario}">
								<h:graphicImage url="/img/questionario.png"/>
								<f:param name="idQuestionario" value="#{participante.questionarioRespostas.id}" />
							</h:commandLink>
						 </td>	
					</tr>
				</c:forEach>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="10" align="center">
						<h:commandButton value="Cancelar" action="#{inscricaoAtividade.voltaTelaGerenciaInscricoes}" id="cancelar" onclick="#{confirm}" immediate="true" />
					</td>
				</tr>
			</tfoot>
			
		</table>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>