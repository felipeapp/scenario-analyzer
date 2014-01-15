<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	<h2><ufrn:subSistema /> &gt; Quantitativo de Inscritos em Cursos e Eventos de Extens�o pela �rea P�blica</h2>
    
    
    <div class="descricaoOperacao">
    	Lista de inscritos por ordem de Data de Cadastro.<br/>
    	Significados das situa��es dos inscritos na A��o de Extens�o:
    	<br/>
    	<br/>    	
    	<b>Aceitas:</b> Indica que a inscri��o foi aceita pela coordena��o da a��o de extens�o e assegura a participa��o da pessoa no curso/evento.<br/>
    	<b>Confirmadas:</b> Indica que o participante confirmou a participa��o respondendo ao e-mail que recebeu do sistema. Sua inscri��o poder� ser aceita ou recusada pela coordena��o do curso/evento.<br/>
    	<b>Pendentes:</b> Indica que o participante ainda n�o respondeu ao e-mail que recebeu do sistema.<br/>
    	<b>Recusadas:</b> Indica que a coordena��o do curso/evento negou a inscri��o da pessoa.<br/>
    	<b>Canceladas:</b> Indica que o participante se inscreveu, mas desistiu do evento e cancelou a inscri��o atrav�s do portal p�blico.<br/>
    </div>
    
    
    <c:if test="${ not empty inscricaoAtividade.obj.atividade }">
    	<c:set var="atv" value="${inscricaoAtividade.obj.atividade}" />
    </c:if>
    
    <c:if test="${ not empty inscricaoAtividade.obj.subAtividade }">
    	<c:set var="atv" value="${inscricaoAtividade.obj.subAtividade.atividade}" />
    </c:if>
    
	
		<table class="formulario" width="80%">
		  <caption>Dados da a��o de Extens�o</caption>
			<tr>			
				<th><b>C�digo:</b></th>
				<td>${atv.codigo}</td>
			</tr>
			<tr>
				<th><b>T�tulo:</b></th>
				<td>${atv.titulo}</td>
			</tr>
			
			 <c:if test="${ not empty inscricaoAtividade.obj.subAtividade }">
				<tr>
					<th><b>Mini Atividade:</b></th>
					<td>${inscricaoAtividade.obj.subAtividade.titulo}</td>
				</tr>
			</c:if>
			
			<tr>
				<th><b>Coordena��o:</b></th>
				<td>${atv.coordenacao.servidor.pessoa.nome}</td>
			</tr>
			<tr>
				<th><b>Per�odo:</b></th>
				<td>
					<fmt:formatDate value="${atv.dataInicio}" pattern="dd/MM/yyyy"/>										 
					at� 
					<fmt:formatDate value="${atv.dataFim}" pattern="dd/MM/yyyy"/>
					
				</td>
			</tr>
		</table>
<br/>


    <div class="infoAltRem">
        <h:graphicImage value="/img/monitoria/businessman_view.png" style="overflow: visible;" id="labViewInscricao"/> Visualizar Inscri��o
        <h:graphicImage value="/img/email_go.png" style="overflow: visible;" id="labReenviarInscricao"/>: Reenviar Senha de Acesso
        <h:graphicImage value="/img/view.gif" style="overflow: visible;" id="labVisualizarArquivo"/>: Visualizar Arquivo
        <br/>
        <h:graphicImage value="/img/extensao/user1_refresh.png" style="overflow: visible;" id="labAlterarDadosInscricao"/>: Alterar Dados da Inscri��o
        <h:graphicImage value="/img/questionario.png" style="overflow: visible;" id="labViewQuestionario"/> Visualizar Question�rio
    </div>

	<c:set value="#{inscricaoAtividade.inscricoesAceitas}" var="aceitos" />
	<c:set value="#{inscricaoAtividade.inscricoesConfirmadas}" var="confirmados" />
	<c:set value="#{inscricaoAtividade.inscricoesCanceladas}" var="cancelados" />
	<c:set value="#{inscricaoAtividade.inscricoesRecusadas}" var="recusados" />
	<c:set value="#{inscricaoAtividade.inscricoesNaoConfirmadas}" var="naoConfirmados" />
	
<h:form id="form">
	<table class="listagem" width="100%">
			<caption>Lista das Inscri��es nessa A��o</caption>
			<thead>
				<tr>
					<th width="1%">N�</th>
					<th width="17%">Data do Cadastro</th>
					<th width="40%">Nome Completo</th>
					<th width="20%">E-mail</th>
					<th width="20%">Institui��o</th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td colspan="10" class="subFormulario">Inscri��es ACEITAS (${fn:length(aceitos)})</td>
				</tr>
				<c:if test="${empty aceitos}">
					<tr>
						<td colspan="9">N�o existem inscri��es <b>aceitas</b> nesta a��o</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="#{aceitos}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${status.count}</td>
						<td><fmt:formatDate value="${participante.dataCadastro}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'N�o informada' : participante.instituicao}</td>
						<td width="2%">
							<h:commandLink title="Visualizar inscri��o" immediate="true" id="viewInformacoesAceito"
								action="#{inscricaoAtividade.visualizarDadosInscrito}">
								<f:param name="id" value="#{participante.id}" />
								<h:graphicImage url="/img/monitoria/businessman_view.png" />
							</h:commandLink>
						</td>
						<td width="2%">
							<h:commandLink title="Alterar Dados da Inscri��o" immediate="true" id="alterarDadosInformacoesAceito"
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
							<h:commandLink title="Visualizar Question�rio" id="cmdLinkVisualizarQuestionarioAceita" 
									rendered="#{participante.questionarioRespostas != null}"
									action="#{inscricaoParticipantes.viewRespostaParticipanteQuestionario}">
									<f:param name="idQuestionario" value="#{participante.questionarioRespostas.id}" />
								<h:graphicImage url="/img/questionario.png"/>
							</h:commandLink>
						 </td>									
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="10" class="subFormulario">Inscri��es CONFIRMADAS (${fn:length(confirmados)})</td>
				</tr>
				<c:if test="${empty confirmados}">
					<tr>
						<td colspan="9">N�o existem inscri��es <b>confirmadas</b> nesta a��o</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="#{confirmados}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${status.count}</td>
						<td><fmt:formatDate value="${participante.dataCadastro}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'N�o informada' : participante.instituicao}</td>
						<td width="2%">
                            <h:commandLink title="Visualizar inscri��o" immediate="true" id="viewInformacoesConfirmado"
                                action="#{inscricaoAtividade.visualizarDadosInscrito}">
                                <f:param name="id" value="#{participante.id}" />
                                <h:graphicImage url="/img/monitoria/businessman_view.png" />
                            </h:commandLink>
                        </td>
                        <td width="2%">
							<h:commandLink title="Alterar Dados da Inscri��o" immediate="true" id="alterarDadosInformacoesConfirmado"
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
							<h:commandLink title="Visualizar Question�rio" id="cmdLinkVisualizarQuestionarioConfirmado" 
								rendered="#{participante.questionarioRespostas != null}"
								action="#{inscricaoParticipantes.viewRespostaParticipanteQuestionario}">
								<h:graphicImage url="/img/questionario.png"/>
								<f:param name="idQuestionario" value="#{participante.questionarioRespostas.id}" />
							</h:commandLink>
						 </td>								
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="10" class="subFormulario">Inscri��es RECUSADAS (${fn:length(recusados)})</td>
				</tr>
				<c:if test="${empty recusados}">
					<tr>
						<td colspan="9">N�o existem inscri��es <b>recusadas</b> nesta a��o</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="#{recusados}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${status.count}</td>
						<td><fmt:formatDate value="${participante.dataCadastro}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'N�o informada' : participante.instituicao}</td>
                        <td width="2%">
                            <h:commandLink title="Visualizar inscri��o" immediate="true" id="viewInformacoesRecusado"
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
							<h:commandLink title="Visualizar Question�rio" id="cmdLinkVisualizarQuestionarioRecusados" 
								rendered="#{participante.questionarioRespostas != null}"
								action="#{inscricaoParticipantes.viewRespostaParticipanteQuestionario}">
								<h:graphicImage url="/img/questionario.png"/>
								<f:param name="idQuestionario" value="#{participante.questionarioRespostas.id}" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="10" class="subFormulario">Inscri��es PENDENTES (${fn:length(naoConfirmados)})</td>
				</tr>
				<c:if test="${empty naoConfirmados}">
					<tr>
						<td colspan="9">N�o existem inscri��es <b>pendentes de confirma��o</b> nesta a��o</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="#{naoConfirmados}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${status.count}</td>
						<td><fmt:formatDate value="${participante.dataCadastro}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'N�o informada' : participante.instituicao}</td>
                        <td width="2%">
                            <h:commandLink title="Visualizar inscri��o" immediate="true" id="viewInformacoesInscritos"
                                action="#{inscricaoAtividade.visualizarDadosInscrito}">
                                <f:param name="id" value="#{participante.id}" />
                                <h:graphicImage url="/img/monitoria/businessman_view.png" />
                            </h:commandLink>
                        </td>
                        <td width="2%">
							<h:commandLink title="Alterar Dados da Inscri��o" immediate="true" id="alterarDadosInformacoesPendentes"
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
							<h:commandLink title="Visualizar Question�rio" id="cmdLinkVisualizarQuestionarioPendentes" 
									rendered="#{participante.questionarioRespostas != null}"
									action="#{inscricaoParticipantes.viewRespostaParticipanteQuestionario}">
								<h:graphicImage url="/img/questionario.png"/>
								<f:param name="idQuestionario" value="#{participante.questionarioRespostas.id}" />
							</h:commandLink>
						</td>	
					</tr>
				</c:forEach>
				
				<tr>
					<td colspan="10" class="subFormulario">Inscri��es CANCELADAS (${fn:length(cancelados)})</td>
				</tr>
				<c:if test="${empty cancelados}">
					<tr>
						<td colspan="9">N�o existem inscri��es <b>canceladas</b> pelo usu�rio nesta a��o</td>
					</tr>
				</c:if>
				<c:forEach var="participante" items="#{cancelados}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>${status.count}</td>
						<td><fmt:formatDate value="${participante.dataCadastro}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td>${participante.nome}</td>
						<td>${participante.email}</td>
						<td>${empty participante.instituicao ? 'N�o informada' : participante.instituicao}</td>
                        <td width="2%">
                            <h:commandLink title="Visualizar inscri��o" immediate="true" id="viewInformacoesCancelado"
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
							<h:commandLink title="Visualizar Question�rio" id="cmdLinkVisualizarQuestionarioCancelado" 
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