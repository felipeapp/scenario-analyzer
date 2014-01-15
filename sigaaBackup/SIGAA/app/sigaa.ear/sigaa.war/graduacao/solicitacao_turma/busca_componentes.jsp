<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>

<h:messages showDetail="true" />
	<h2 class="title"><ufrn:subSistema /> > Solicitação de Abertura de Turma</h2>

	<div class="descricaoOperacao">
		<p>Caro Coordenador, <p>
		<p>
			Selecione neste passo o componente curricular (disciplina, módulo ou bloco) que deseja criar a turma.
		</p>
	</div>

	<h:form id="buscaComponente">
		<table class="formulario" width="50%">
		<caption class="formulario">Busca de Componentes Curriculares</caption>
		<tbody>
			<tr>
				<td width="5%"><input type="radio" name="paramBusca" value="codigo" id="checkCodigo" class="noborder"></td>
				<th width="10%"><label for="checkCodigo">Código:</label></th>
				<td><h:inputText value="#{solicitacaoTurma.obj.componenteCurricular.detalhes.codigo}" size="10" maxlength="10" id="param1"
					onfocus="marcaCheckBox('checkCodigo')" onkeyup="CAPS(this)" /></td>
			</tr>
			<tr>
				<td><input type="radio" id="checkNome" name="paramBusca" value="nome" class="noborder"></td>
				<th><label for="checkNome">Nome:</label></th>
				<td><h:inputText value="#{solicitacaoTurma.obj.componenteCurricular.detalhes.nome}" size="60" id="param2" maxlength="60"
					onfocus="marcaCheckBox('checkNome')" onkeyup="CAPS(this)" /></td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
				<h:commandButton value="Buscar" action="#{solicitacaoTurma.buscarComponente}" />
				</td>
			</tr>
		</tfoot>
		</table>
	</h:form>

		<br>
		<c:if test="${empty solicitacaoTurma.componentes}">
			<div style="text-align: center; font-style: italic;">Não foi encontrado nenhum componente com o critério especificado.</div>
		</c:if>
		<c:if test="${not empty solicitacaoTurma.componentes}">
		<center>
		<div class="infoAltRem"><h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:
		Selecionar Componente Curricular<br />
		</div>
		</center>
		<table class="listagem">
		<caption class="listagem">Componentes Curriculares Encontrados</caption>
			<thead>
				<tr>
					<td style="text-align: center;">Código</td>
					<td>Nome</td>
					<td style="text-align: right;">Créditos</td>
					<td style="text-align: right;">Carga Horária</td>
					<td>Tipo</td>
					<td></td>
				</tr>
			</thead>

			<c:if test="${not empty solicitacaoTurma.componentes}">
			<c:forEach items="${solicitacaoTurma.componentes}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td style="text-align: center;">${item.detalhes.codigo}</td>
					<td>${item.detalhes.nome}</td>
					<td style="text-align: right;">${item.detalhes.crTotal}</td>
					<td style="text-align: right;">${item.detalhes.chTotal}</td>
					<td>${item.tipoComponente.descricao}</td>


					<td width=20>
					<h:form>
						<input type="hidden" value="${item.id}" name="id" /> 
						<h:commandButton image="/img/seta.gif" styleClass="noborder" value="Avançar"
						action="#{solicitacaoTurma.selecionarComponente}" />
					</h:form>
					</td>
				</tr>
			</c:forEach>
			</c:if>

		</table>
		</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>