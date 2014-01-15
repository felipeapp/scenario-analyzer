<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
 
 <h2><ufrn:subSistema /> > Residência Médica</h2>
   <table class=listagem width="40%">
 	 <caption class="listagem">Residência Médicas Detalhada</caption>
 	   <c:forEach items="${residenciaMedica.lista}" var="item" varStatus="status">
			<tr class="header">
				<tr><td><b>Servidor:</b> ${item.servidor.pessoa.nome}</td></tr>
				<tr><td><b>Programa:</b> ${item.programaResidenciaMedica.nome}</td></tr>
				<tr><td><b>Carga Horária Semanal Dispendida:</b> ${item.chSemanal} h</td></tr>
				<tr><td><b>Observações:</b> ${item.observacoes}</td></tr>
		</c:forEach>
	</table>
 </f:view>
 <p style="text-align: center"><a href="javascript:history.go(-1)"> << Voltar</a></p>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>