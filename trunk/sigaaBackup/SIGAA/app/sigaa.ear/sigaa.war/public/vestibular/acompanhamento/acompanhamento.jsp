<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp" %>
<style>
#opcoes-modulo div.novaInscricao {
	background-image: url(../../images/icones/regulamento.gif);
}
#opcoes-modulo div.dadosPessoais {
	background-image: url(../../images/icones/curso-graduacao.gif);
}
#opcoes-modulo div.logoff {
	background-image: url(../../images/icones/exit.png);
}
#opcoes-modulo div.senhaAcesso{
	background-image: url(../../images/acesso.gif);
}
#opcoes-modulo div.extratoDesempenho{
	background-image: url(../../images/icones/estatisticas.gif);
}
#opcoes-modulo div.segundaVia{
	background-image: url(../../images/icones/boleto.png);
}

#opcoes-modulo div.comprovanteInscricao{
	background-image: url(../../images/icones/autenticacao.gif);
}

#opcoes-modulo div.localProva{
	background-image: url(../../images/icones/processo-graduacao.gif);
}

</style>
<f:view>
<h:form id="form">
<%-- Verificar se existem dados cadastrados para acessar a área de acompanhamento --%>
${acompanhamentoVestibular.verificarPersistenciaInscricao}
<h2>Vestibular</h2>
	<div class="descricaoOperacao">
		<p>Caro(a) <b><h:outputText value="#{acompanhamentoVestibular.obj.nome}" /></b> (CPF: <h:outputText value="#{acompanhamentoVestibular.obj.cpfCnpjFormatado}" />)</p><br/>
		<p><b>Bem vindo à sua Área Pessoal!</b></p>
		<p>Nesta página você terá acesso exclusivo aos seus dados pessoais e extrato de desempenho nos Processos Seletivos.</p>
	</div>
	
	<div id="menu">
	<div id="bg-top">&nbsp;</div>
	<div id="bg-bottom">
		<div id="modulos">
			<div id="menu-opcoes" class="item item-over" >
				<span> ${acompanhamentoVestibular.processoSeletivo.sigla } </span>
			</div>
		</div>
		<div id="opcoes-modulo"  >
			<div id="opcoes-slider" >
				<div id="p-academico" class="painel" >
					<dl>
						<dt>
							<div class="opcao novaInscricao">
							<h:commandLink	action="#{acompanhamentoVestibular.realizarNovaInscricao }" id="realizarNovaInscricao">
								<h3>Realizar Nova Inscrição</h3>
								Preencha o formulário de inscrição on-line.
							</h:commandLink>
							</div>
						</dt>
						<dt>
							<div class="opcao dadosPessoais">
							<h:commandLink action="#{acompanhamentoVestibular.atualizarDadosPessoais }" id="autalizarDadosPessoais">
							<h3>Alteração de Dados Pessoais</h3>
								Atualize seus Dados Pessoais.
							</h:commandLink>
							</div>
						</dt>
						
						<dt>
							<div class="opcao segundaVia">
							<h:commandLink action="#{acompanhamentoVestibular.listarInscricoes }" id="listarInscricoes">
								<h3>Segunda via da GRU</h3>
								Reimprima uma segunda via da GRU para pagamento da taxa de Inscrição. 
							</h:commandLink>
							</div>
						</dt>
						<dt>
							<div class="opcao comprovanteInscricao">
							<h:commandLink action="#{acompanhamentoVestibular.listarInscricoes}" id="listarInscricoes2">
								<h3>Comprovante de Inscrição</h3>
								Acompanhe o andamento da sua inscrição e imprima seu Comprovante de Inscrição. 
							</h:commandLink>
							</div>
						</dt>
						
						<dt>
							<div class="opcao senhaAcesso">
							<h:commandLink action="#{acompanhamentoVestibular.alterarSenha }" id="alterarSenha">
								<h3>Alterar Senha de Acesso</h3>
								Altere sua senha de acesso.
							</h:commandLink>
							</div>
						</dt>
						<dt>
							<div class="opcao localProva">
								<h3>Local de Prova</h3>
								Visualize o seu local de Prova no sítio da <a href="http://www.comperve.ufrn.br/" target="_blank">COMPERVE</a> 
							</div>
						</dt>
							
						<dt>
							<div class="opcao logoff">
							<h:commandLink action="#{acompanhamentoVestibular.logoff}" id="logOff">
								<h3>Sair da Área Pessoal</h3>
								Encerre sua sessão e saia com segurança da sua área pessoal. 
							</h:commandLink>
							</div>
						</dt>
						
					</dl>
				</div>
			</div>
		</div>
	</div>
	<br clear="all">
</div>
	<br />
	<c:if test="${not empty acompanhamentoVestibular.inscricoesRealizadas}">
		<div id="noticias">
			<div class="infoAltRem">
			    <h:graphicImage value="/img/imprimir.gif" style="overflow: visible;"/>: Imprimir Segunda Via da GRU 
			    <h:graphicImage value="/img/comprovante.png" style="overflow: visible;"/>: Ver Comprovante de Inscrição
			</div>
			<table class="listagem">
				<caption class="listagem">Inscrições Realizadas</caption>
				<thead>
					<tr>
						<th width="8%" style="text-align: center;">Data de Inscrição</th>
						<th width="5%" style="text-align: right;">Nº de Inscrição</th>
						<th>Opção(ões)</th>
						<th>Local Preferencial de Prova</th>
						<th width="10%">Status</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="#{acompanhamentoVestibular.inscricoesRealizadas}" var="inscricao"	varStatus="status" >
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td style="text-align: center;">
							<ufrn:format type="datahorasec" valor="${inscricao.dataInscricao}" />
						</td>
						<td style="text-align: right;">${inscricao.numeroInscricao}</td>
						<td>
							<c:forEach items="#{inscricao.opcoesCurso}" var="opcao" varStatus="statusOpcao">
								<c:if test="${statusOpcao.index > 0}"><br/></c:if>
								<b>Opção ${statusOpcao.index + 1}:</b> ${opcao.descricao}
							</c:forEach>
						</td>
						<td>${inscricao.regiaoPreferencialProva.denominacao}</td>
						<td>
							<h:outputText value="Pré-Validada" rendered="#{inscricao.validada and inscricao.valorInscricao == 0 && inscricao.processoSeletivo.inscricoesCandidatoAbertas}" />
							<h:outputText value="Validada" rendered="#{inscricao.validada and (inscricao.valorInscricao > 0 or inscricao.valorInscricao == 0 && not inscricao.processoSeletivo.inscricoesCandidatoAbertas)}" />
							<h:outputText value="Pagamento Confirmado. Aguardando validação da COMPERVE" rendered="#{inscricao.gruQuitada && !inscricao.validada}" />
							<h:outputText value="Aguardando confirmação de pagamento pela COMPERVE" rendered="#{!inscricao.gruQuitada && !inscricao.validada}" />
						</td>
						<td width="16">
							<h:commandLink title="Imprimir Segunda Via da GRU" action="#{acompanhamentoVestibular.imprimirGRU}" id="imprimirGRU" rendered="#{not inscricao.validada and inscricao.valorInscricao > 0}">
								<h:graphicImage url="/img/imprimir.gif" />
								<f:param name="id" value="#{inscricao.id}" id="idInscricao" />
								<f:param name="inscricao" value="#{inscricao.numeroInscricao}" id="numeroInscricao"/>
							</h:commandLink>
						</td>
						<td width="16">
							<h:commandLink title="Comprovante de Inscrição" action="#{acompanhamentoVestibular.verComprovante}" id="verComprovante">
								<h:graphicImage url="/img/comprovante.png" />
								<f:param name="id" value="#{inscricao.id}" />
								<f:param name="inscricao" value="#{inscricao.numeroInscricao}" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
		</div>
	</c:if>
</h:form>
<script>
	var ativo = Ext.get('${sessionScope.aba}');
	<c:if test="${not empty param.menuAtivo}">
		var ativo = Ext.get('${param.menuAtivo}');
	</c:if>
</script>
<script type="text/javascript" src="/shared/javascript/public/menu.js"> </script>
</f:view>
<%@include file="/public/include/rodape.jsp" %>