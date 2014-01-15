<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Selecionar Inscritos</h2>
	
	
	<div class="descricaoOperacao"> 
		<p>Abaixo estão listadas as inscrições existentes para a atividade selecionada.</p>	
		
		<p>Quando o participante realiza a inscrição ele não está automaticamente matriculado na atividade. 
		É preciso o coordenador aceitar a inscrição. <br/>
		Para aceitar deve-se observar os documentos enviados pelo participante e caso exija-se uma cobança de taxa de matrícula, se ela foi paga pelo participante. <br/>
		Só quando essas exigências forem atendidas as inscrições devem ser aceitas.<br/>
		Após a aceitação, o participante está oficialmente participando da atividade.
		</p>	
		<br/>
		<c:if test="${inscricaoAtividade.obj.exigeCombrancaTaxa}">
			<p> São mostradas as seguintes informações com relação ao pagamento:
				<ul>
					<li>NÃO GERENCIADO: O pagamento da atividade não é gerenciada do pelo sistema. Não é possível determinar se o usuário realizou o pagamento ou não.</li>
					<li>EM ABERTO: A confirmação do pagamento da atividade ainda não foi realizada no sistema.</li>
					<li>CONFIRMADO: O pagamento da GRU foi confirmado no sistema.</li>
				</ul>
			</p>
		</c:if>
		<c:if test="${! inscricaoAtividade.obj.exigeCombrancaTaxa}">
			<p> Essa atividade não possui cobança de taxa de matrícula!</p>
		</c:if>
		
	</div>
	
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar arquivo
		<h:graphicImage value="/img/extensao/businessman_view.png" style="overflow: visible;"/>: Visualizar inscrição			
		<h:graphicImage url="/img/pagamento.png"/>: Confirmar o pagamento manualmente			
	</div>
	<h:form id="form1">
		<c:choose>
			<c:when test="${not empty inscricaoAtividade.obj.participantesInscritos}">
				<table id="tabelaParticipantes" width="100%" class="listagem">
					<caption>Lista de Inscritos na ordem de inscrição</caption>
					<thead>
						<tr>
							<th style="text-align:center" width="8%">
								<a id="marcar" href="javascript:void(0)" onclick="marcarTodosCheckboxes();">TODOS</a>
							</th>
							<th>Nome Completo</th>
							<th>E-mail</th>
							<th style="text-align:center">Data de Nascimento</th>
							<th>Instituição</th>
							
							<th>Status do Pagamento</th>
							<th></th>
							
							<th />
							<th />
						</tr>
					</thead>
					<tbody>
						<c:forEach items="#{inscricaoAtividade.obj.participantesInscritos}" 
								var="participante" varStatus="count">
							<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td style="text-align: center;">
									<h:selectBooleanCheckbox value="#{participante.marcado}" />
								</td>
								<td>${participante.nome}</td>
								<td>${participante.email}</td>
								<td style="text-align:center">
									<h:outputText value="#{participante.dataNascimento}">
										<f:convertDateTime pattern="dd/MM/yyyy" />
									</h:outputText> 
								</td>
								<td>${empty participante.instituicao ? 'Não informada' : participante.instituicao}</td>
								
								
								<td style="text-align:center; ${participante.statusPagamento.confirmadoManualmente ? 'color:green;' : 
								            (participante.statusPagamento.confirmadoAutomaticamente ? 'color:darkgreen; font-weight:bold;' :
								            (participante.statusPagamento.aberto ? 'color:red;' : '') ) }">
									<c:if test="${inscricaoAtividade.obj.exigeCombrancaTaxa}">
										${participante.statusPagamento.descricao}
									</c:if>
									<c:if test="${! inscricaoAtividade.obj.exigeCombrancaTaxa}">
										- - -
									</c:if>
								</td>
								
								<td>
									<h:commandLink title="Confirmar o Pagamento Manualmente" immediate="true" id="cmdLinkConfirmaPagamentoManual"
												action="#{inscricaoAtividade.preConfirmarPagamentoManualmente}" 
												rendered="#{inscricaoAtividade.obj.exigeCombrancaTaxa && participante.statusPagamento.aberto}">
											<f:param name="idParticipante" value="#{participante.id}" />
											<h:graphicImage url="/img/pagamento.png"/>
									</h:commandLink>
								</td>
									
								
								<td align="center">
									<h:commandLink title="Visualizar arquivo: #{ participante.descricaoArquivo }" immediate="true" id="visualizarArquivo"
											action="#{inscricaoParticipantes.viewArquivo}" 
											rendered="#{participante.idArquivo > 0}">
										<f:param name="idArquivo" value="#{participante.idArquivo}" />
										<h:graphicImage url="/img/view.gif"/>
									</h:commandLink>
								</td>								
								<td align="center">
									<h:commandLink title="Visualizar inscrição" immediate="true" id="visualizarInformacoes"
											action="#{inscricaoAtividade.visualizarDadosInscrito}">
										<f:param name="id" value="#{participante.id}" />
										<h:graphicImage url="/img/monitoria/businessman_view.png"/>
									</h:commandLink>
								</td>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="9">
								<center>
									<h:commandButton value="Aceitar Inscrições" action="#{inscricaoAtividade.aprovarParticipantes}" id="aceitarInscricoes" />&nbsp;
									<h:commandButton value="Recusar Inscrições" actionListener="#{inscricaoAtividade.informarMotivo}" id="recusarInscricoes" />&nbsp;
									<h:commandButton value="Cancelar" action="#{inscricaoAtividade.voltaTelaGerenciaInscricoes}" id="voltar" onclick="#{confirm}" immediate="true"/>
								</center>
							</td>
						</tr>
					</tfoot>
				</table>
				
			</c:when>
			<c:otherwise>
				<br /><br />
				<span style="color: red">Não existem inscritos para este período de inscrição</span>
				<br /><br /><br />
				<center>
					<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)" id="voltar" />
				</center>
			</c:otherwise>
		</c:choose>
	</h:form>
	
	<c:if test="${inscricaoAtividade.exibirPainel}">
		<div id="div-form">
			<div class="ydlg-hd">Informações adicionais</div>
			<div class="ydlg-bd">
			<h:form id="motivoForm">
				<table class="formulario" width="100%" style="border: 0;">
					<caption>Informe o(s) Motivo(s) da Recusa</caption>
					<tr>
						<td colspan="2" style="color: red; font-style: italic; text-align: center;">${inscricaoAtividade.erroMotivo}</td>
					</tr>
					<tbody>
						<tr>
							<th>Motivo:</th>
							<td><h:inputTextarea value="#{inscricaoAtividade.obj.motivoCancelamento}" id="motivo" cols="40" rows="3" /></td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td colspan="2" align="center">
								<h:commandButton value="Enviar"	action="#{inscricaoAtividade.recusarParticipantes}" id="enviar" />
								<h:commandButton value="Cancelar" actionListener="#{inscricaoAtividade.cancelarMotivo}" id="cancelar"
										 onclick="#{confirm}" immediate="true" />
							</td>
						</tr>
					</tfoot>
				</table>
			</h:form>
			</div>
		</div>
	</c:if>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">

var mark = false;

function marcarTodosCheckboxes(){
	var checkboxes = document.getElementsByTagName('INPUT');
	mark = mark ? false : true;
	$('marcar').innerHTML = mark ? 'NENHUM' : 'TODOS';
	for (i in checkboxes) {
		var input = checkboxes[i];
		if(input.type == 'checkbox')
			input.checked = mark;
	}
}

var PainelMotivo = (function() {
	var painel;
	return {
        show : function(){
   	 		painel = new YAHOO.ext.BasicDialog("div-form", {
                modal: true,
                width: 400,
                height: 170,
                shadow: false,
                fixedcenter: true,
                resizable: false,
                closable: false
            });
       	 	painel.show();
        }
	};
})();

<c:if test="${inscricaoAtividade.exibirPainel}">
		PainelMotivo.show();
		$('motivoForm:motivo').value = '';
		$('motivoForm:motivo').focus();
</c:if>

</script>