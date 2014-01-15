<%@include file="/public/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao" %>
<%@page import="br.ufrn.arq.util.AmbienteUtils"%>


<%-- Se não tiver com https, redireciona para a mesma página com https --%>
<% 
	if (AmbienteUtils.isNotSecure(request)) {
		String url  = "https://" + request.getServerName() + request.getContextPath()+"/public/extensao/loginCursosEventosExtensao.jsf"; 
		response.sendRedirect(url); 
		return;
	}
%>

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

div.menu-botoes li.imprimir-certificado a p {
	background-image: url('/sigaa/img/view2.gif');
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

<f:view>

	<a4j:keepAlive beanName="logonCursosEventosExtensaoMBean" />
	
	<h2>Área de Login para Acesso à Inscrição em Cursos e Eventos de Extensão</h2>

	<div class="descricaoOperacao">
		<p><b>Caro visitante,</b></p>
		<p>Para acessar a área de inscritos em cursos e eventos de extensão é necessário realizar o <i>login</i> no sistema. </p>
		<p>A partir da área de gerenciamento será possível se inscrever nos curso e eventos abertos, bem como acompanhar as suas inscrições já realizadas.</p>
	</div>
	
	<h:form id="formLoginCursosEventosExtensao">
	
		<table class="formulario" style="width: 50%;">
			<caption>Informe seu e-mail e senha</caption>
			<tbody>
				<tr>
					<th style="width: 40%;">E-mail:</th>
					<td><h:inputText value="#{logonCursosEventosExtensaoMBean.email}" id="email" size="30" maxlength="60"/></td>
				</tr>
				<tr>
					<th>Senha:</th>
					<td><h:inputSecret value="#{logonCursosEventosExtensaoMBean.senha}" id="senha" size="30" maxlength="20" /></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="entrar"  value="Entrar" action="#{logonCursosEventosExtensaoMBean.entrarAreaRetritaParticipante}" />
						<br/>
						<h:commandLink value="Esqueci minha senha!" action="#{cadastroParticipanteAtividadeExtensaoMBean.iniciarReenvioSenhaParticipante}" />
						&nbsp;&nbsp;&nbsp;
						<h:commandLink value="Ainda não possuo cadastro!" action="#{cadastroParticipanteAtividadeExtensaoMBean.iniciarCadastroNovoParticipante}" />
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>

	<br />
	<br />
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/public/home.jsf" style="color: #404E82;">&lt;&lt; voltar ao menu principal</a>
	</div>
	<br />

<%--
	
	<f:subview id="areaUsuario" rendered="#{inscricaoParticipantes.exibirArea}">
	
<c:if test="${not empty inscricaoParticipantes.obj.inscricaoAtividade.atividade}">
	
	
	<c:set var="acao" value="#{inscricaoParticipantes.obj.inscricaoAtividade.atividade.tipoAtividadeExtensao.descricao}" />
	<h2>Área para gerenciamento da Inscrição nos Cursos e Eventos de Extensão</h2>
	<div class="descricaoOperacao">
		<h3 style="text-align: center; font-style: italic">O coordenador desta ação fez as seguintes observações:</h3>
		<div class="ignoreCss">${inscricaoParticipantes.obj.inscricaoAtividade.observacoes}</div>
	</div>
	<br /><br />
	<table class="visualizacao">
		<caption>Informações sobre a Ação de Extensão</caption>
		<tbody>
			<tr>
				<th style="text-transform: capitalize;">${fn:toLowerCase(acao)}:</th>
				<td>${inscricaoParticipantes.obj.inscricaoAtividade.atividade.titulo}</td>
			</tr>
			<tr>
				<th>Período:</th>
				<td><span class="periodo">
						<h:outputText value="#{inscricaoParticipantes.obj.inscricaoAtividade.atividade.dataInicio}">
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText>
					</span>
					<i> até </i>
					<span class="periodo">
						<h:outputText value="#{inscricaoParticipantes.obj.inscricaoAtividade.atividade.dataFim}">
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText>
					</span>
				</td>
			</tr>
			<tr>
				<th>Coordenador:</th>
				<td>${inscricaoParticipantes.obj.inscricaoAtividade.atividade.coordenacao.pessoa.nome}</td>
			</tr>
			
			<tr>
                <th>Certificado Liberado:</th>
                <td>${inscricaoParticipantes.obj.participanteExtensao.passivelEmissaoCertificado ? 'SIM' : 'NÃO'}</td>
            </tr>

            <tr>
                <th>Frequência:</th>
                <td>${inscricaoParticipantes.obj.participanteExtensao.frequencia}%</td>
            </tr>
			
			<c:if test="${inscricaoParticipantes.atividadePossuiCobancaTaxaInscricao}">
				<tr>
                <th>Pagamento da GRU:</th>
                <td>${inscricaoParticipantes.obj.statusPagamento.descricao}</td>
            	</tr>
			</c:if>
			
			
		</tbody>
	</table>
	<h:form>
		<div class="menu-botoes" style="margin-top: 50px; margin-left: auto; margin-right: auto;" >
			<ul class="menu-interno">
				
				<li class="botao-grande cancelar-inscricao">
					<h:commandLink action="#{inscricaoParticipantes.cancelarInscricao}" id="cancelarInscricao"
							onclick="return confirm('Deseja cancelar sua inscrição? Sua inscrição para participante será cancelada!');">
						<h5>Cancelar Inscrição</h5> 
						<p>Cancele sua inscrição para participante na ação de extensão</p>
					</h:commandLink>	
				</li>
				
				<li class="botao-grande emitir-gru">
					<h:commandLink action="#{inscricaoParticipantes.emitirGRU}"  target="_black" id="emitirGRU">
						<h5>Imprimir/Reimprimir GRU</h5> 
						<p> Emita a GRU para pagamento da inscrição</p>
					</h:commandLink>	
				</li>
				
				<li class="botao-grande imprimir-certificado">
					<h:commandLink action="#{inscricaoParticipantes.imprimirCertificado}" id="imprimirCertificado">
						<h5>Imprimir Certificado</h5> 
						<p>Imprima seu certificado de participação da ação após seu término</p>
					</h:commandLink>	
				</li>
				
			</ul>
			<br clear="all" />
		</div>
	</h:form>
	
	<br /><br />
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/link/public/extensao/logoutAreaInscrito" style="color: #404E82;">SAIR</a>
	</div>
	<br />
	
</c:if>
	
	
	
	--%>
	
	
	
	<%--
	
	
<c:if test="${ not( not empty inscricaoParticipantes.obj.inscricaoAtividade.atividade ) }">
	
	
	<c:set var="acao" value="#{inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.atividade.tipoAtividadeExtensao.descricao}" />
	<h2>Área para gerenciamento da Inscrição nos Cursos e Eventos de Extensão</h2>
	<div class="descricaoOperacao">
		<h3 style="text-align: center; font-style: italic">O coordenador desta ação fez as seguintes observações:</h3>
		<div class="ignoreCss">${inscricaoParticipantes.obj.inscricaoAtividade.observacoes}</div>
	</div>
	<br /><br />
	<table class="visualizacao">
		<caption>Informações sobre a Mini Atividade </caption>
		<tbody>
			<tr>
				<th style="text-transform: capitalize;">${fn:toLowerCase(acao)}:</th>
				<td>${inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.atividade.titulo}</td>
			</tr>
			
			<tr>
				<th style="text-transform: capitalize;">Mini Atividade:</th>
				<td>${inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.titulo}</td>
			</tr>
			
			<tr>
				<th>Período:</th>
				<td><span class="periodo">
						<h:outputText value="#{inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.inicio}">
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText>
					</span>
					<i> até </i>
					<span class="periodo">
						<h:outputText value="#{inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.fim}">
							<f:convertDateTime pattern="dd/MM/yyyy" />
						</h:outputText>
					</span>
				</td>
			</tr>
			<tr>
				<th>Coordenador:</th>
				<td>${inscricaoParticipantes.obj.inscricaoAtividade.subAtividade.atividade.coordenacao.pessoa.nome}</td>
			</tr>
			
			<tr>
                <th>Certificado Liberado:</th>
                <td>${inscricaoParticipantes.obj.participanteExtensao.passivelEmissaoCertificado ? 'SIM' : 'NÃO'}</td>
            </tr>

            <tr>
                <th>Frequência:</th>
                <td>${inscricaoParticipantes.obj.participanteExtensao.frequencia}%</td>
            </tr>
			
			<c:if test="${inscricaoParticipantes.atividadePossuiCobancaTaxaInscricao}">
				<tr>
                <th>Pagamento da GRU:</th>
                <td>${inscricaoParticipantes.obj.statusPagamento.descricao}</td>
            	</tr>
			</c:if>
			
			
		</tbody>
	</table>
	<h:form>
		<div class="menu-botoes" style="margin-top: 50px; margin-left: auto; margin-right: auto;" >
			<ul class="menu-interno">
				
				<li class="botao-grande cancelar-inscricao">
					<h:commandLink action="#{inscricaoParticipantes.cancelarInscricao}" id="cancelarInscricao"
							onclick="return confirm('Deseja cancelar sua inscrição? Sua inscrição para participante será cancelada!');">
						<h5>Cancelar Inscrição</h5> 
						<p>Cancele sua inscrição para participante na ação de extensão</p>
					</h:commandLink>	
				</li>
				
				<li class="botao-grande emitir-gru">
					<h:commandLink action="#{inscricaoParticipantes.emitirGRU}"  target="_black" id="emitirGRU">
						<h5>Imprimir/Reimprimir GRU</h5> 
						<p> Emita a GRU para pagamento da inscrição</p>
					</h:commandLink>	
				</li>
				
				<li class="botao-grande imprimir-certificado">
					<h:commandLink action="#{inscricaoParticipantes.imprimirCertificado}" id="imprimirCertificado">
						<h5>Imprimir Certificado</h5> 
						<p>Imprima seu certificado de participação da ação após seu término</p>
					</h:commandLink>	
				</li>
				
			</ul>
			<br clear="all" />
		</div>
	</h:form>
	
	<br /><br />
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<a href="/sigaa/link/public/extensao/logoutAreaInscrito" style="color: #404E82;">SAIR</a>
	</div>
	<br />
	
</c:if>
	
	--%>
	
	

</f:view>

<%@include file="/public/include/rodape.jsp" %>