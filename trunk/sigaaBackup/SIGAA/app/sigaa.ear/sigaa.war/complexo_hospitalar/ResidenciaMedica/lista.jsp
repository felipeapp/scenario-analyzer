<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
 
 <h2><ufrn:subSistema /> > Coordenação Residência Médica</h2>
   <table class=listagem width="80%">
 	 <caption class="listagem">Residência Médicas Detalhada</caption>
 	   <c:forEach items="${residenciaMedica.lista}" var="item" varStatus="status">
		 <tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<tr class="header">
					<td><b>Programa:</b> ${item.nome}</td>
					<td><b>Hospital:</b> ${item.hospital}</td>
					<td><b>Ano/Período:</b> ${item.ano}.${item.semestre}</td>
				</tr>
				<tr class="header">
					<td colspan="2"><b>Servidor:</b> ${item.nome_servidor}</td>
					<td><b>Carga Horária:</b> ${item.ch_semanal}h</td>
				</tr>
				<tr class="header">
					<td colspan="3"><b>Obervação:</b> ${item.observacoes}</td>
		</c:forEach>
	</table>
 </f:view>
 <p style="text-align: center"><a href="javascript:history.go(-1)"> << Voltar</a></p>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>