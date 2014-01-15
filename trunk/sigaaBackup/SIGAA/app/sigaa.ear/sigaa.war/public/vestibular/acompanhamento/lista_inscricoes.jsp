<%@include file="/public/include/_esconder_entrar_sistema.jsp"%>
<%@include file="/public/include/cabecalho.jsp" %>
<f:view>
<h:form id="form">
<h2>Vestibular</h2>
	<div class="descricaoOperacao">
		<p>Caro(a) <b><h:outputText value="#{acompanhamentoVestibular.obj.nome}" /></b> (CPF: <h:outputText value="#{acompanhamentoVestibular.obj.cpfCnpjFormatado}" />)</p><br/>
		<p>As inscrições abaixo listadas são referentes ao <h:outputText value="#{acompanhamentoVestibular.processoSeletivo.nome}" /></p>
		<p>Clique nos ícones correspondentes para reimprimir a GRU Cobrança, para pagamento da taxa de inscrição, ou ver seu Comprovante de Inscrição</p>
	</div>
	<br/>
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
		<br/>
		<div align="center">
		<h:commandLink value="<< Voltar à minha Área Pessoal" action="#{acompanhamentoVestibular.paginaAcompanhamento}" id="paginaAcompanhamento2" style="font-weight: bold;" styleClass="naoImprimir"/>
		</div>
	</c:if>
</h:form>
<script type="text/javascript" src="/shared/javascript/public/menu.js"> </script>
</f:view>
<%@include file="/public/include/rodape.jsp" %>