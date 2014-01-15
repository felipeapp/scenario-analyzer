<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Questão Socioeconômico</h2>

	<center><h:messages /></center>

	<h:form>
		<table class="formulario" width="50%">
			<caption>Selecione um Processo Seletivo</caption>
			<tr>
				<th class="required">Processo Seletivo:</th>
				<td><h:selectOneMenu id="processoSeletivo" immediate="true"
					value="#{questaoSE.obj.processoSeletivo.id}" onchange="submit()">
					<f:selectItem itemValue="0"
						itemLabel="SELECIONE UM PROCESSO SELETIVO" />
					<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
				</h:selectOneMenu></td>
			</tr>
		</table>
		<br>
		<center>
		<div class="infoAltRem"><h:graphicImage value="/img/alterar.gif"
			style="overflow: visible;" />: Alterar<h:graphicImage
			value="/img/delete.gif" style="overflow: visible;" />: Remover <br />
		</div>
		</center>
		<table class=listagem>
			<caption class="listagem">Lista de Questões socioeconômicas</caption>
			<thead>
				<tr>
					<th>Questão</th>
					<th colspan="2"></th>
				</tr>
			</thead>
			<c:forEach items="#{questaoSE.all}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>${item.ordem} - ${item.pergunta}</td>
					<td width="2%"><h:commandLink title="Alterar"
						action="#{questaoSE.atualizar}" style="border: 0;">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/alterar.gif" />
					</h:commandLink></td>
					<td width="2%"><h:commandLink title="Remover"
						action="#{questaoSE.preRemover}" style="border: 0;">
						<f:param name="id" value="#{item.id}" />
						<h:graphicImage url="/img/delete.gif" />
					</h:commandLink></td>
				</tr>
			</c:forEach>
		</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>