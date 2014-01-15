<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>

	<table width="100%" style="font-size: 12px;">
		<caption><b>RELATÓRIO DE FALTAS DOS DOCENTES
	</table>
	<br />
	<c:set var="docente" value="${faltaDocente.checkBuscaServidor}"/>
	<c:set var="data" value="${faltaDocente.checkBuscaAno}"/>
	<c:set var="nomeUnidade" value="${faltaDocente.checkBuscaUnidade}"/>
	
	<table>

	<c:if test="${docente == true}">
		<c:if test="${data == true}">
		<tr>
			<td width="25%"><b>Docente: </b></td>
			<td><h:outputText value="#{faltaDocente.nomeDocente}"/></td>
		</tr>
		</c:if>
		<c:if test="${data == false}">
		<tr>
			<td width="15%"><b>Docente: </b></td>
			<td><h:outputText value="#{faltaDocente.nomeDocente}"/></td>
		</tr>
		</c:if>
	</c:if>
	
	<c:if test="${data == true}">
		<tr>
			<td width="25%"><b>Ano - Período: </b></td> 
			<td><h:outputText value="#{faltaDocente.buscaAno}"/>.<h:outputText value="#{faltaDocente.buscaPeriodo}"/></td>	
			
		</tr>
	</c:if>
	
	<c:if test="${nomeUnidade == true}">
		<c:if test="${data == true}">
		<tr>
			<td width="25%"><b>Unidade: </b></td>
			<td><h:outputText value="#{faltaDocente.nome}"/></td>
		</tr>
		</c:if>
		<c:if test="${data == false}">
		<tr>
			<td width="15%"><b>Unidade: </b></td>
			<td><h:outputText value="#{faltaDocente.nome}"/></td>
		</tr>
		</c:if>
	</c:if>
	
	</table>
	<hr style="margin-top: 0px;">
	<br />
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	<c:set var="docente_"/>
	<c:forEach var="falta" items="${faltaDocente.faltasDetalhado}" varStatus="status">
		<c:set var="docenteAtual" value="${falta.docenteNome}"/>
	   <c:if test="${docente_ != docenteAtual}">
		<c:set var="docente_" value="${docenteAtual}"/>
			<tr>
				<td colspan="4">
					<br>
					<b>${falta.docenteNome}</b>
					<hr>
				</td>
			</tr>
			<tr>
				<td align="justify"><b>Disciplina</b></td>
				<td align="center"><b>Turma</b></td>
				<td align="center"><b>Quantidade Denúncias</b></td>
				<td align="center"><b>Data da Aula</b></td>
			</tr>
	   </c:if>
			<tr>
				<td align="justify">${falta.turmaNome}</td>
				<td align="center">${falta.codigo}</td>
				<td align="center">${falta.qtdFalta}</td>
				<td align="center"><fmt:formatDate value="${falta.dataAula}" pattern="dd/MM/yyyy"/></td>
			</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>