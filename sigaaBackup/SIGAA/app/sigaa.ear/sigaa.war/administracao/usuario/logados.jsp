<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

<h2> Registros de Entrada </h2>

	<h:messages showDetail="true" />
	<center><br>
	<h:form>
	<table class="formulario">
		<caption class="listagem">Informe os critérios de consulta</caption>
		<tr>
			<th>
				Tempo de Logon <small>(min)</small>:
			</th>
			<td>
				<h:inputText value="#{userBean.tempo}"/> <h:commandButton actionListener="#{userBean.buscaEntradasTempo}" value="Buscar"/>
			</td>
		</tr>
		<tr>
			<th>
				Tempo de Atividade <small>(min)</small>:
			</th>
			<td>
				<h:inputText value="#{userBean.tempoAtividade}"/> <h:commandButton actionListener="#{userBean.buscaTempoAtividades}" value="Buscar"/>
			</td>
		</tr>
		<tr>
			<th>
				Login:
			</th>
			<td>
				<h:inputText value="#{userBean.obj.login}"/> <h:commandButton actionListener="#{userBean.buscaEntradasLogin}" value="Buscar"/>
			</td>
		</tr>
		<tr>
			<td colspan="2" align="center">


			</td>
		</tr>
	</table>
	</h:form>

	<c:if test="${ not empty userBean.entradas }">
	<br>
	<br>
	<table width="100%" class="listagem">
		<caption class="listagem"> ${total} Registros de Entrada encontrados</caption>
		<thead>
		<tr>
			<td> Data </td>
			<td> Login </td>
			<td> Nome </td>
			<td> IP </td>
			<td> Servidor </td>
			<td> Passaporte </td>
			<td> <td>
		</tr>
		</thead>
	<c:forEach items="${userBean.entradas}" var="entrada" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<td nowrap="nowrap"> <ufrn:format name="entrada" property="data" type="datahora"/></td>
			<td> ${entrada.usuario.login}</td>
			<td> ${entrada.usuario.pessoa.nome}</td>
			<td align="center"> ${entrada.IP} </td>
			<td> ${entrada.server}</td>
			<td> ${entrada.passaporte}</td>
			<td>
				<h:form>
					<input type="hidden" value="${entrada.id}" name="idRegistro"/>
					<h:commandButton action="#{userBean.verRegistroEntrada}" image="/img/view.gif" />
				</h:form>
			</td>
	</c:forEach>
	</table>
	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
