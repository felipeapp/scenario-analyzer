<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<%@ taglib uri="/tags/sigaa" prefix="sigaa" %>

<style>
	#relatorio tr.item td {padding: 1px 0 0 ;}
	#relatorio tr.nivel td {padding: 15px 0 0 0; border-bottom: 1px solid #555; font-size: small;}
	#relatorio tr.discente th {padding: 3px ; border-bottom: 1px solid #888; border-top: 1px solid #888}
	#relatorio tr.componentes td {padding: 1px 1px 3px 15px; border-top: 1px dashed #888; background-color: #eee;}
</style>
<f:view>
	<hr>
	<h2><b>Relatório de Discentes Especiais</b></h2>
	<table>
			<tr class="item">
				<td><b>Programa:</b> <h:outputText value="#{relatorioDiscente.matrizCurricular.curso.unidade.nome}"/></td>
			</tr>
			<tr class="item">
				<td><b>Ano-Período:</b> <h:outputText value="#{relatorioDiscente.ano}.#{relatorioDiscente.periodo}"/></td>
			</tr>
	</table>
	<hr>

	<table cellspacing="1" width="100%" style="font-size: 10px;">
	
	<c:forEach items="#{relatorioDiscente.discentes}" var="discente">
		<c:set var="nvlAtual" value="${discente.nivel}"/>
		<c:if test="${nvl != nvlAtual}">
			<c:set var="nvl" value="${nvlAtual}"/>
			<tr class="nivel">
				<td>
					<b>${discente.nivelDesc }</b><br />
				</td>
			</tr>
		</c:if>
		<c:set var="matriculaAtual" value="${discente.matricula}"/>
		<c:if test="${matricula != matriculaAtual}">
			<c:set var="matricula" value="${discente.matricula}"/>
			<tr class="discente">
				<th>
					<table width="100%">
						<tr>
							<td width="35%"><b>Matrícula:</b> ${discente.matricula }<br /></td>
							<td><b>Nome:</b> ${discente.pessoa.nome }<br /></td>
						</tr>
						<tr>
							<td width="35%"><b>E-Mail:</b> ${discente.pessoa.email }<br /></td>
							<td><b>Endereço:</b> ${discente.pessoa.endereco }<br /></td>
						</tr>
						<tr>
							<td width="35%"><b>Telefone:</b> ${discente.pessoa.telefone }<br /></td>
							<td><b>Celular:</b> ${discente.pessoa.celular }<br /></td>
						</tr>
					</table>
				</th>
			</tr>
		</c:if>
		<c:forEach items="#{discente.matriculasDisciplina}" var="mat">
			<tr class="componentes">
				<td>${mat.componente.descricaoResumida }</td>
			</tr>
		</c:forEach>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>