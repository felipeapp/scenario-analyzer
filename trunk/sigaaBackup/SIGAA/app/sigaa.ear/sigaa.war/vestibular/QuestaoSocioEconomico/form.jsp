<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Questionario Socioeconômico</h2>
	<h:form id="form">
		<table class="formulario" width="60%">
			<caption>Questionário Socioeconômico</caption>
			<tbody>
				<tr>
					<td>
					<table class="formulario" width="100%">
						<caption>Processo Seletivo</caption>
						<tr>
							<th class="required">Processo seletivo:</th>
							<td><a4j:region>
								<h:selectOneMenu id="processoSeletivo" immediate="true"
									value="#{questaoSE.obj.processoSeletivo.id}"
									readonly="#{questaoSE.readOnly}">
									<f:selectItem itemValue="0"
										itemLabel="SELECIONE UM PROCESSO SELETIVO" />
									<f:selectItems
										value="#{processoSeletivoVestibular.allAtivoCombo}" />
									<a4j:support event="onchange" reRender="lista" />
								</h:selectOneMenu>
								<a4j:status>
									<f:facet name="start">
										<h:graphicImage value="/img/indicator.gif" />
									</f:facet>
								</a4j:status>
							</a4j:region></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td>
					<table class="formulario" width="100%">
						<caption>Pergunta</caption>
						<tr>
							<th class="required">Ordem:</th>
							<td><h:inputText value="#{questaoSE.ordemPergunta}" size="2"
								disabled="#{readOnly}" maxlength="2"
								readonly="#{questaoSE.readOnly}" onkeypress="return formatarInteiro(this)"
								onblur="formatarInteiro(this)" /></td>
							<th class="required">Pergunta:</th>
							<td><h:inputText value="#{questaoSE.obj.pergunta}" size="60"
								readonly="#{questaoSE.readOnly}" disabled="#{readOnly}"
								maxlength="120" /></td>
						</tr>
					</table>
					</td>
				</tr>
				<tr>
					<td>
					<table class="formulario" width="100%">
						<caption>Respostas</caption>
						<tr>
							<th class="required">Ordem:</th>
							<td><h:inputText value="#{questaoSE.ordemResposta}" size="2"
								readonly="#{questaoSE.readOnly}" maxlength="2"
								onkeypress="return formatarInteiro(this)"
								onblur="formatarInteiro(this)" /></td>
							<th class="required">Resposta:</th>
							<td><h:inputText value="#{questaoSE.resposta}" size="40"
								readonly="#{questaoSE.readOnly}" disabled="#{readOnly}"
								maxlength="120" /></td>
							<td align="center"><h:commandButton
								value="Adicionar resposta" disabled="#{questaoSE.readOnly}"
								action="#{questaoSE.adicionaResposta}" /></td>
						</tr>
						<tr>
							<th>Respostas:</th>
							<td colspan="3"><h:selectOneListbox id="respostas"
								readonly="#{questaoSE.readOnly}"
								value="#{questaoSE.respostaRemover}" immediate="true"
								style="margin-top:5px; height: 60px; width:400px; vertical-align: middle;">
								<f:selectItems value="#{questaoSE.respostas}" />
							</h:selectOneListbox></td>
							<td><h:commandButton action="#{questaoSE.removerResposta}"
								image="/img/prodocente/lixeira_selectmany.gif"
								disabled="#{questaoSE.readOnly}"
								title="Remover resposta da lista" /></td>
						</tr>
					</table>
					</td>
				</tr>

			</tbody>
			<tfoot>
				<tr>
					<td align="center"><h:commandButton
						value="#{questaoSE.confirmButton}" action="#{questaoSE.cadastrar}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}"
						action="#{questaoSE.cancelar}" immediate="true" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span></center>
	<br>
	<div align="center"><h:panelGrid id="lista" style="width: 100%;">
		<c:if test="${not empty questaoSE.all}">
			<table class=listagem width="60%" align="center">
				<caption class="listagem">Questões socioeconômicas
				cadastradas</caption>
				<thead>
					<tr>
						<th>Questão</th>
						<th>Respostas</th>
						<th>Exemplo de resposta</th>
					</tr>
				</thead>
				<c:forEach items="#{questaoSE.all}" var="item" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${item.ordem} - ${item.pergunta}</td>
						<td align="center">${fn:length(item.respostas)}</td>
						<td>${fn:substring(item.respostas[0].resposta,0,30) }...</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
	</h:panelGrid></div>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>