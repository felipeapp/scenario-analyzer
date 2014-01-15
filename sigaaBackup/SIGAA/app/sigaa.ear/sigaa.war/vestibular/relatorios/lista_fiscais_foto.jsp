<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
tr.header td {
	padding: 3px;
	border-bottom: 1px solid #555;
	background-color: #eee;
}

tr.foot td {
	padding: 3px;
	border-bottom: 1px solid #555;
	background-color: #eee;
	font-weight: bold;
	font-size: 13px;
}

tr.componentes td {
	padding: 4px 2px 2px;
	border-bottom: 1px dashed #888;
}
</style>
<f:view>
	<hr>
	<h2 class="tituloTabela"><b>Ficha de avaliação de Fiscais <h:outputText
		value="#{relatoriosVestibular.obj.nome}" /> <br>
	${nomeLocalAplicacao}</h2>
	<c:set var="total" value="0" />
	<c:forEach items="#{fiscais}" var="item">
		<table cellspacing="1" width="100%"
			style="font-size: 10px; page-break-inside: avoid;">
			<tr class="header">
				<td align="left" width="90%" colspan="2"><b>Nome:
				${item.pessoa.nome}</b></td>
				<td align="center"></td>
			</tr>
			<tr>
				<td valign="middle" width="15%">
				<table border="1">
					<tr>
						<td align="center" colspan="2">CONCEITO</td>
					</tr>
					<tr>
						<td align="center">( )</td>
						<td>Suficiente</td>
					</tr>
					<tr>
						<td align="center">( )</td>
						<td>Insuficiente</td>
					</tr>
					<tr>
						<td align="center">( )</td>
						<td>Ausente</td>
					</tr>
				</table>
				</td>
				<td valign="top" align="left"><c:if
					test="${not empty item.discente}">
					<b>Matricula: </b> ${item.discente.matricula}
			<b>Curso: </b> ${item.discente.curso.descricao}
			</c:if> <c:if test="${not empty item.servidor}">
					<b>Matricula: </b> ${item.servidor.siape}
			<b>(Servidor)</b>
				</c:if> <br>
				<p>Justificativa caso conceito <b>INSUFICIENTE</b> / outras
				observações:<br />
				<table border="1" width="100%">
					<tr>
						<td height="25pt" width="100%">&nbsp;</td>
					</tr>
					<tr>
						<td height="25pt" width="100%">&nbsp;</td>
					</tr>
					<tr>
						<td height="25pt" width="100%">&nbsp;</td>
					</tr>
					<tr>
						<td height="25pt" width="100%">&nbsp;</td>
					</tr>
				</table>
				</p>
				</td>
				<td align="right">
					<c:if test="${not empty item.inscricaoFiscal.idFoto}">
						<img src="${ctx}/verFoto?idArquivo=${item.inscricaoFiscal.idFoto}&key=${ sf:generateArquivoKey(item.inscricaoFiscal.idFoto) }" style="width: 100px; height: 125px" />
					</c:if>
					<c:if test="${empty item.inscricaoFiscal.idFoto}">
						<c:if test="${not empty item.discente}">
							<img src="${ctx}/verFoto?idArquivo=${item.discente.idFoto}&key=${ sf:generateArquivoKey(item.discente.idFoto) }"
								style="width: 100px; height: 125px" />
						</c:if>
						<c:if test="${not empty item.servidor}">
							<img src="${ctx}/verFoto?idArquivo=${item.servidor.idFoto}&key=${ sf:generateArquivoKey(item.servidor.idFoto) }"
								style="width: 100px; height: 125px" />
						</c:if>
					</c:if>
				</td>
			</tr>
		</table>
		<br />
	</c:forEach>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
