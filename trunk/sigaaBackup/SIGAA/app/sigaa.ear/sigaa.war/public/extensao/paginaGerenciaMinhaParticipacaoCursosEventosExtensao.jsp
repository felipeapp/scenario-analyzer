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

	<h2>Gerenciar Inscrição</h2>	

	<h:form id="formGerenciarMinhasInscricoes">
	
	<c:set var="_isInscricaoAtividade" value="${gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.participanteAtividadeExtensao}" scope="request" />
	
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
		
	</div>
	
	
	<table class="visualizacao" style="margin-top: 40px;">
		
		<caption>Informações sobre sua Inscrição na ${_isInscricaoAtividade ? 'Atividade' : 'Mini Atividade'} </caption>
		
		<tbody>
		
			
			<tr>
				<th>  ${_isInscricaoAtividade ? 'Atividade' : 'Mini Atividade' }:</th>
				<td colspan="5">
					<c:if test="${_isInscricaoAtividade}">
						${gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.atividadeExtensao.titulo}
					</c:if>
					<c:if test="${! _isInscricaoAtividade}">
						${gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.subAtividade.titulo}
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
							<h:outputText value="#{gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.atividadeExtensao.dataInicio}">
								<f:convertDateTime pattern="dd/MM/yyyy" />
							</h:outputText>
						</span>
						<i> até </i>
						<span class="periodo">
							<h:outputText value="#{gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.atividadeExtensao.dataFim}">
								<f:convertDateTime pattern="dd/MM/yyyy" />
							</h:outputText>
						</span>
					</c:if>
					<c:if test="${! _isInscricaoAtividade}">
						<span class="periodo">
							<h:outputText value="#{gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.subAtividade.atividade.dataInicio}">
								<f:convertDateTime pattern="dd/MM/yyyy" />
							</h:outputText>
						</span>
						<i> até </i>
						<span class="periodo">
							<h:outputText value="#{gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.subAtividade.atividade.dataFim}">
								<f:convertDateTime pattern="dd/MM/yyyy" />
							</h:outputText>
						</span>
					</c:if>
				</td>
				
				<th style="width: 15%;">Tipo:</th>
				<td colspan="2" style="width: 35%;">
					<c:if test="${_isInscricaoAtividade}">
						${gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.atividadeExtensao.tipoAtividadeExtensao.descricao}
					</c:if>
					<c:if test="${! _isInscricaoAtividade}">
						${gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.subAtividade.tipoSubAtividadeExtensao.descricao}
					</c:if>
				</td>
			</tr>
			
			<tr>
				<th>Coordenador:</th>
				<td colspan="5">
					<c:if test="${_isInscricaoAtividade}">
						${gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.atividadeExtensao.coordenacao.pessoa.nome}
					</c:if>
					<c:if test="${! _isInscricaoAtividade}">
						${gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.subAtividade.atividade.coordenacao.pessoa.nome}
					</c:if>
				</td>
			</tr>

			
			
			<%-- Informações dos documentos que ele pode emitir --%>	   
		   <tr>
		   	   <th>Declaração Liberada:</th>
               <td>${gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.passivelEmissaoDeclaracaoParticipante ? 'SIM' : 'NÃO'}</td>
               <th>Certificado Liberado:</th>
               <td>${gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.passivelEmissaoCertificadoParticipante ? 'SIM' : 'NÃO'}</td>
               <th>Frequência:</th>
               <td>${gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.frequencia} % </td>
           </tr>
           
         
			
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
				
				<c:if test="${gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.passivelEmissaoDeclaracaoParticipante}">
					<li class="botao-grande imprimir-declaracao">
						<h:commandLink action="#{declaracaoExtensaoMBean.emitirDeclaracaoParticipante}" id="imprimirDeclaracao">
							<h5>Imprimir Declaração</h5> 
							<p>Imprima seu certificado de participação da ação após seu término</p>
							<f:setPropertyActionListener target="#{declaracaoExtensaoMBean.participante.id}" value="#{gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.id}" />
						</h:commandLink>	
					</li>
				</c:if>
				
				<c:if test="${gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.passivelEmissaoCertificadoParticipante}">
					<li class="botao-grande imprimir-certificado">
						<h:commandLink action="#{certificadoExtensaoMBean.emitirCertificadoParticipante}" id="imprimirCertificado">
							<h5>Imprimir Certificado</h5> 
							<p>Imprima seu certificado de participação da ação após seu término</p>
							<f:setPropertyActionListener target="#{certificadoExtensaoMBean.participante.id}" value="#{gerenciaMeusCursosEventosExtensaoMBean.participanteSelecionado.id}" />
						</h:commandLink>	
					</li>
				</c:if>
				
			</ul>
			<br clear="all" />
		</div>
</h:form>