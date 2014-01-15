<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2>Atribuir permissões a um usuário</h2>

	<h:form id="form">
		<h:messages showDetail="true" showSummary="true" />
		<div style="visibility: hidden;"><t:selectOneRadio
			value="#{ atribuirPapeis.tipoBusca }" id="opcoes" forceId="true"
			forceIdIndex="false">
			<f:selectItem itemValue="1" itemLabel="Login: " />
			<f:selectItem itemValue="2" itemLabel="Nome:" />
			<f:selectItem itemValue="3" itemLabel="Papel:" />
		</t:selectOneRadio></div>

		<table class="formulario">
			<caption>Busca de Usuários</caption>
			<tr>
				<td><t:radio index="0" for="opcoes" id="radLogin" /></td>
				<td><h:inputText value="#{ atribuirPapeis.login }" id="login" />
				</td>
			</tr>
			<tr>
				<td><t:radio index="1" for="opcoes" id="radNome" /></td>
				<td><h:inputText value="#{ atribuirPapeis.nome }" id="nome"
					size="40" /></td>
			</tr>
			<tr>
				<td><t:radio index="2" for="opcoes" id="radPapel" /></td>
				<td><h:selectOneMenu value="#{ atribuirPapeis.papel.id }" id="papel">
					<f:selectItems value="#{ atribuirPapeis.allPapeis }"/>
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2" align="center"><h:commandButton value="Buscar"
						action="#{ atribuirPapeis.buscar }" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{ atribuirPapeis.cancelar }" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<c:if test="${ not empty atribuirPapeis.usuarios }">
		<br />&nbsp;

<table class="listagem">
			<thead>
				<tr>
					<td>Foto </td>
					<td>Nome</td>
					<td>Login</td>
					<td>Unidade</td>
					<td></td>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="usr" items="${ atribuirPapeis.usuarios }"
					varStatus="status">
					<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td>
							<c:if test="${usr.idFoto != null}">
								<img src="${ctx}/verFoto?idFoto=${usr.idFoto}" width="60" height="110"/>
							</c:if>
						</td>
						<td>${ usr.pessoa.nome }</td>
						<td>${ usr.login }</td>
						<td>${ usr.unidade.sigla }</td>
						<td align="right" width="10%"><h:form>
							<input type="hidden" name="id" value="${ usr.id }" />
							<h:commandButton action="#{ atribuirPapeis.escolher }"
								value="Escolher" />
						</h:form></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
