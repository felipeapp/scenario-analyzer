<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.log.*"%>

<style>
	tr.erros {
		background-color: #FFBB00;
	}

	table.listagem tr td {
		padding: 4px;
	}

	table.listagem tr.param td{
	}
</style>

<f:view>
<h:outputText value="#{userBean.create}"/>

<h2>Log de Operações do Usuário</h2>

<table class="listagem" style="width: 90%">
	<tr>
		<td width="20%"> <b>Registro de Entrada:</b> </td>
		<td width="20%"> ${registro.id} </td>
		<td width="20%"> <b>Login:</b> </td>
		<td> ${registro.usuario.login} </td>
	</tr>
	<tr>
		<td> <b>Usuário:</b>  </td>
		<td colspan="3">
			${registro.usuario.pessoa.nome}
			<c:if test="${not empty registro.usuario.ramal}"> <i>(Ramal: ${registro.usuario.ramal })</i></c:if>
		</td>
	</tr>
	<tr>
		<td> <b>Horário de Entrada:</b> </td>
		<td> <ufrn:format name="registro" property="data" type="dataHora"/> </td>
		<td> <b>Horário de Saída:</b> </td>
		<td> <ufrn:format name="registro" property="dataSaida" type="dataHora"/> </td>
	</tr>
	<tr>
		<td> <b>Servidor:</b> </td>
		<td> ${registro.server} </td>
		<td> <b>IP do Usuário:</b> </td>
		<td> ${registro.IP} </td>
	</tr>
	<tr>
		<td> <b>Browser:</b> </td>
		<td colspan="3"> ${registro.userAgent} </td>
	</tr>
</table>

<br />
<table class="listagem" align="center" width="100%">
<caption>Operações do Usuário na Sessão</caption>
<thead>
	<tr>
		<th width="18%">Hora</th>
		<th>Action</th>
		<th>Tempo</th>
	</tr>
</thead>

<tbody>
<c:forEach items="${userBean.operacoes}" var="log" varStatus="status">

	<c:set var="erro" value="" />
	<c:if test="${log.erro}">
		<c:set var="erro" value="erros" />
	</c:if>

	<tr class="${erro} ${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		<td>
			<ufrn:format name="log" property="hora" type="datahora" />
		</td>
		<td>
			<a href="showAtividade.do?idOperacao=${log.id}">
				${log.action}
			</a>
		</td>
		<td align=right>${log.tempoSegundos} s</td>
	</tr>

	<c:if test="${not empty log.parameters}">
		<tr class="param ${erro} ${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td> </td>
			<td colspan="2">
				<div style="width: 600px; overflow: auto;">
					${log.parameters}
				</div>
			</td>
		</tr>
	</c:if>
</c:forEach>
</tbody>
</table>

<br /><br />
<center><a href="javascript:history.go(-1)">Voltar</a> <br>
</center>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>