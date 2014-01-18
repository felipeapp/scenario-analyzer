<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<%@ taglib uri="/tags/ufrn" prefix="ufrn"%>

<style type="text/css">

div.descricaoOperacao a:hover {
	color: blue;
	text-decoration: none;
}

.periodo {
	color: #292;
	font-weight: bold;
}

div.menu-botoes {
	width: 100%;
}

div.menu-botoes ul.menu-interno {
	margin: 0px 120px 0px;
}

div.menu-botoes li.botao-grande {
	float:left;
	width: 350px;
	height: 55px;
	margin-left: 5px;
	margin-bottom: 20px;
}

div.menu-botoes li.botao-grande a {
	display: block;
	height: 57px;
	background: transparent url('/sigaa/public/images/menu/botao-bank-menu.png') no-repeat top left;
	position: relative;
	font-family: arial;
	text-decoration: none;
}

div.menu-botoes li.botao-grande a:hover {
	background: transparent url('/sigaa/public/images/menu/botao-bank-menu.png') no-repeat bottom left;
	text-decoration: none;
	cursor: hand;
}

div.menu-botoes li.botao-grande a h5 {
	color: #404e82;
	font-size: 14px;
	font-weight: bold;
	position: absolute;
	left: 50px;
	top: 5px;
}

div.menu-botoes li.botao-grande a p {
	color: #014fb5;
	font-size: 12px;
	font-weight: normal;
	position: absolute;
	left: 10px;
	top: 0;
	height: 34px;
	background-repeat: no-repeat;
	background-position: center left;
	padding-left: 40px;
	padding-right: 25px;
	padding-top: 21px;
}

div.menu-botoes li.cancelar-inscricao a p {
	background-image: url('/sigaa/img/delete_old.gif');
}

div.menu-botoes li.imprimir-declaracao a p {
	background-image: url('/sigaa/img/view2.gif');
}

div.menu-botoes li.imprimir-certificado a p {
	background-image: url('/sigaa/img/certificate.png');
}

div.menu-botoes li.emitir-gru a p {
	background-image: url('/sigaa/img/imprimir.gif');
}


div.ignoreCss {
	padding-top: 5px;
}
div.descricaoOperacao div.ignoreCss ul,ol {
	margin-left: 20px;
}

div.descricaoOperacao div.ignoreCss ul li {
	list-style-type: disc;
}

div.descricaoOperacao div.ignoreCss ol li {
	list-style-type: decimal;
}

div.ignoreCss strong, strong em {
	font-weight: bold;
}

div.ignoreCss em, em strong {
	font-style: italic;
}

</style>

	<%@page import="br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante"%>

	<c:set var="STATUS_INSCRICAO_APROVADO" value="<%= StatusInscricaoParticipante.APROVADO %>" scope="request" />
	<c:set var="STATUS_INSCRICAO_CANCELADO" value="<%= StatusInscricaoParticipante.CANCELADO %>" scope="request" />
	<c:set var="STATUS_INSCRICAO_RECUSADO" value="<%= StatusInscricaoParticipante.RECUSADO %>" scope="request" />

	<h2>Gerenciar Inscrição</h2>
	
	
	<c:set var="_isInscricaoAtividade" value="${gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.inscricaoAtividade}" scope="request" />
	
	<div class="descricaoOperacao">
		<p>Caro(a) Participante,</p>
		<p>Por esta página é possível gerenciar sua inscrição nos cursos e eventos de extensão.</p>
		
		<br/><br/>
		
		<p><b>A emissão do certificado só será autorizada quando as seguintes condições forem atingidas:</b> </p>
  		<ol>
  			<li>A ação de extensão estiver finalizada <b>e</b> o projeto concluído <b>ou</b> o gestor autorizou a emissão antes de término da ação.</li>
  			<li>O participante deverá ter frequência satisfatória.</li>
  			<li>O participante deverá ter a emissão do certificado autorizada pela coordenação da ação. </li>
		</ol>
		
		<p> <b>A emissão da declaração só será autorizada quando as seguintes condições forem atingidas:</b> </p>
		<ol>
  			<li>A ação de extensão <b>não</b> estiver finalizada <b>e</b> o projeto <b>não</b> estiver concluído.</li>
  			<li>O participante deverá ter a emissão da declaração autorizada pela coordenação da ação.</li>
		</ol>
		
		
		<c:if test="${_isInscricaoAtividade}">
			<br/>
			<p><b>IMPORTANTE:</b> Caso cancele sua inscrição na atividade, todas as suas inscrições nas mini atividades associadas a essa atividade serão canceladas.
			</p>
		</c:if>
	</div>
	
	<div class="descricaoOperacao">
		<h3 style="text-align: center; font-style: italic">O coordenador desta ação fez as seguintes observações:</h3>
		<p>${gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.observacoes} </p>
	</div>	

	<h:form id="formGerenciarMinhasInscricoes">
	
	
	<table class="visualizacao" style="margin-top: 40px;">
		
		<caption>Informações sobre sua Inscrição na ${_isInscricaoAtividade ? 'Atividade' : 'Mini Atividade'} </caption>
		
		<tbody>
		
			
			<tr>
				<th>  ${_isInscricaoAtividade ? 'Atividade' : 'Mini Atividade' }:</th>
				<td colspan="5">
					<c:if test="${_isInscricaoAtividade}">
						${gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.atividade.titulo}
					</c:if>
					<c:if test="${! _isInscricaoAtividade}">
						${gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.subAtividade.titulo}
					</c:if>
				</td>
			</tr>
			
			<c:if test="${!_isInscricaoAtividade}">
				<th>Atividade:</th>
				<td colspan="5">
					${gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.subAtividade.atividade.titulo}
				</td>
			</c:if>
			
			<tr>
				<th style="width: 15%;">Período:</th>
				<td colspan="2" style="width: 35%;">
					<c:if test="${_isInscricaoAtividade}">
						<span class="periodo">
							<h:outputText value="#{gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.atividade.dataInicio}">
								<f:convertDateTime pattern="dd/MM/yyyy" />
							</h:outputText>
						</span>
						<i> até </i>
						<span class="periodo">
							<h:outputText value="#{gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.atividade.dataFim}">
								<f:convertDateTime pattern="dd/MM/yyyy" />
							</h:outputText>
						</span>
					</c:if>
					<c:if test="${! _isInscricaoAtividade}">
						<span class="periodo">
							<h:outputText value="#{gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.subAtividade.inicio}">
								<f:convertDateTime pattern="dd/MM/yyyy" />
							</h:outputText>
						</span>
						<i> até </i>
						<span class="periodo">
							<h:outputText value="#{gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.subAtividade.fim}">
								<f:convertDateTime pattern="dd/MM/yyyy" />
							</h:outputText>
						</span>
					</c:if>
				</td>
				
				<th style="width: 15%;">Tipo:</th>
				<td colspan="2" style="width: 35%;">
					<c:if test="${_isInscricaoAtividade}">
						${gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.atividade.tipoAtividadeExtensao.descricao}
					</c:if>
					<c:if test="${! _isInscricaoAtividade}">
						${gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.subAtividade.tipoSubAtividadeExtensao.descricao}
					</c:if>
				</td>
			</tr>
			
			<tr>
				<th>Coordenador:</th>
				<td colspan="5">
					<c:if test="${_isInscricaoAtividade}">
						${gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.atividade.coordenacao.pessoa.nome}
					</c:if>
					<c:if test="${! _isInscricaoAtividade}">
						${gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.subAtividade.atividade.coordenacao.pessoa.nome}
					</c:if>
				</td>
			</tr>
			
			<tr>
				<th>Status da Inscrição:</th>
				<td colspan="5" style="text-align:left; font-weight:bold; ${ gerenciaMeusCursosEventosExtensaoMBean.obj.statusInscricao.id == STATUS_INSCRICAO_APROVADO ? 'color:green;' 
						: ( gerenciaMeusCursosEventosExtensaoMBean.obj.statusInscricao.id == STATUS_INSCRICAO_CANCELADO || gerenciaMeusCursosEventosExtensaoMBean.obj.statusInscricao.id == STATUS_INSCRICAO_RECUSADO  ? 'color:red;' : '' ) }">
					${gerenciaMeusCursosEventosExtensaoMBean.obj.statusInscricao.descricao}
				</td>
			</tr>
			
			<tr>
				<th>Instituição:</th>
				<td colspan="5">
					${gerenciaMeusCursosEventosExtensaoMBean.obj.instituicao}
				</td>
			</tr>
			
			
			
			<c:if test="${gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.cobrancaTaxaMatricula}">
				<tr>
				   <th>Inscrito como:</th>
	               <td colspan="2">${gerenciaMeusCursosEventosExtensaoMBean.obj.molidadeParticipante.modalidadeParticipante.nome}</td>
	               <th>Data de vencimento da GRU:</th>
	               <td colspan="2"> <ufrn:format valor="${gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.dataVencimentoGRU}" type="data" /> </td>
	           	</tr>
	           	<tr>
				   <th>Valor:</th>
	               <td colspan="2">${gerenciaMeusCursosEventosExtensaoMBean.obj.valorTaxaMatriculaFormatado}</td>
	               <th>Status do Pagamento:</th>
	               <td colspan="2" style="${gerenciaMeusCursosEventosExtensaoMBean.obj.statusPagamento.aberto ? 'color:red;' : 'color:green;'}">
	               		${gerenciaMeusCursosEventosExtensaoMBean.obj.statusPagamento.descricao}
	                </td>
	           	</tr>
			</c:if>
			
			<c:if test="${gerenciaMeusCursosEventosExtensaoMBean.obj.statusInscricao.id == STATUS_INSCRICAO_APROVADO}">
			   
			   <tr>
			   	   <th>Declaração Liberada:</th>
               	   <td>${gerenciaMeusCursosEventosExtensaoMBean.obj.participanteExtensao.passivelEmissaoDeclaracaoParticipante ? 'SIM' : 'NÃO'}</td>
	               <th>Certificado Liberado:</th>
	               <td>${gerenciaMeusCursosEventosExtensaoMBean.obj.participanteExtensao.passivelEmissaoCertificadoParticipante ? 'SIM' : 'NÃO'}</td>
	               <th>Frequência:</th>
	               <td>
	               	 <c:if test="${empty gerenciaMeusCursosEventosExtensaoMBean.obj.participanteExtensao.frequencia}">
	               	 	0
	               	 </c:if>
	               	 <c:if test="${not empty gerenciaMeusCursosEventosExtensaoMBean.obj.participanteExtensao.frequencia}">
	               	 	${gerenciaMeusCursosEventosExtensaoMBean.obj.participanteExtensao.frequencia}
	               	 </c:if>
	               	 % 
	               	</td>
	           </tr>
	           
           </c:if>
           
         
			<c:if test="${gerenciaMeusCursosEventosExtensaoMBean.obj.participanteExtensao != null && ! gerenciaMeusCursosEventosExtensaoMBean.obj.participanteExtensao.ativo}">
				<tr>
					<td colspan="6" style="text-align: center; color: red;">
						A participação referente a essa inscrição foi cancelada pelo coordenador
					</td>
				</tr>
			</c:if>
				
			
		</tbody>
		
		<tfoot>
			<tr>
               <td colspan="6" style="text-align: center;">
               		<h:commandButton id="commandButtonCancelar" value="Cancelar" action="#{gerenciaMeusCursosEventosExtensaoMBean.telaListaMeusCursosEventosExtensao}" immediate="true" />&nbsp;
               </td>
	        </tr>
		</tfoot>
		
	</table>	
	


		<div class="menu-botoes" style="margin-top: 50px; margin-left: auto; margin-right: auto;" >
			<ul class="menu-interno">
				
				
				
				<%-- Pode imprimir gru se a atividade tiver cobrança, se a inscrição não tiver cancelada e se o pagamento ainda não tiver sido confirmado --%>
				<c:if test="${gerenciaMeusCursosEventosExtensaoMBean.obj.inscricaoAtividade.cobrancaTaxaMatricula
					&& gerenciaMeusCursosEventosExtensaoMBean.obj.statusInscricao.id != STATUS_INSCRICAO_CANCELADO
					&& gerenciaMeusCursosEventosExtensaoMBean.obj.statusInscricao.id != STATUS_INSCRICAO_RECUSADO
					&& gerenciaMeusCursosEventosExtensaoMBean.obj.statusPagamento.aberto  }">
					<li class="botao-grande emitir-gru">
						<h:commandLink action="#{gerenciaMeusCursosEventosExtensaoMBean.emitirGRUPagamentoInscricao}"  id="emitirGRU">
							<h5>Imprimir/Reimprimir GRU</h5> 
							<p> Emita a GRU para pagamento da inscrição</p>
						</h:commandLink>	
					</li>
				</c:if>
				
				<c:if test="${gerenciaMeusCursosEventosExtensaoMBean.obj.statusInscricao.id != STATUS_INSCRICAO_CANCELADO
				&& gerenciaMeusCursosEventosExtensaoMBean.obj.statusInscricao.id != STATUS_INSCRICAO_RECUSADO }">
				<li class="botao-grande cancelar-inscricao">
					<h:commandLink action="#{gerenciaMeusCursosEventosExtensaoMBean.cancelarInscricao}" id="cancelarInscricao"
							onclick="return confirm('Deseja cancelar sua inscrição? Você não participará mais dessa ação e essa operação não poderá ser desfeita ! ');">
						<h5>Cancelar Inscrição</h5> 
						<p>Cancele sua inscrição para participante na ação de extensão</p>
					</h:commandLink>	
				</li>
				</c:if>
				
				
				<c:if test="${gerenciaMeusCursosEventosExtensaoMBean.obj.statusInscricao.id == STATUS_INSCRICAO_APROVADO
					&& gerenciaMeusCursosEventosExtensaoMBean.obj.participanteExtensao.passivelEmissaoDeclaracaoParticipante}">
					<li class="botao-grande imprimir-declaracao">
						<h:commandLink action="#{declaracaoExtensaoMBean.emitirDeclaracaoParticipante}" id="imprimirDeclaracao">
							<h5>Imprimir Declaração</h5> 
							<p>Imprima sua declaração de inscrição na ação de extensão</p>
							<f:setPropertyActionListener target="#{declaracaoExtensaoMBean.participante.id}" value="#{gerenciaMeusCursosEventosExtensaoMBean.obj.participanteExtensao.id}" />
						</h:commandLink>	
					</li>
				</c:if>
				
				<c:if test="${gerenciaMeusCursosEventosExtensaoMBean.obj.statusInscricao.id == STATUS_INSCRICAO_APROVADO
					&& gerenciaMeusCursosEventosExtensaoMBean.obj.participanteExtensao.passivelEmissaoCertificadoParticipante}">
					<li class="botao-grande imprimir-certificado">
						<h:commandLink action="#{certificadoExtensaoMBean.emitirCertificadoParticipante}" id="imprimirCertificado">
							<h5>Imprimir Certificado</h5> 
							<p>Imprima seu certificado de participação da ação após seu término</p>
							<f:setPropertyActionListener target="#{certificadoExtensaoMBean.participante.id}" value="#{gerenciaMeusCursosEventosExtensaoMBean.obj.participanteExtensao.id}" />
						</h:commandLink>	
					</li>
				</c:if>
					
				
				
			</ul>
			<br clear="all" />
		</div>
</h:form>