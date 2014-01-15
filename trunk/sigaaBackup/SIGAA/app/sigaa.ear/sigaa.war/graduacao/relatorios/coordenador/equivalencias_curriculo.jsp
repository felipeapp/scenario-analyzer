<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<%@ taglib uri="/tags/sigaa" prefix="sigaa" %>

<style>
	#relatorio h3.semRegistro {color: red;}
	#relatorio tr.item td {padding: 1px 0 0 ;}
	#relatorio tr.nivel td {padding: 15px 0 0 0; border-bottom: 1px solid #555; font-size: small;}
	#relatorio tr.componente td {padding: 3px ; background-color: #eee; border-bottom: 1px solid #888; border-top: 1px solid #888}
	#relatorio tr.equivalentes td {padding: 1px 1px 3px 15px; border-top: 1px dashed #888;}
</style>
<f:view>
	<hr>
	<h2><b>Relatório de Equivalências do Currículo</b></h2>
	<table>
			<tr class="item">
				<td>Curso: <b><h:outputText value="#{relatoriosCoordenador.curso.descricao}"/></b></td>
			</tr>
			<tr class="item">
				<td>Matriz Curricular: <b><h:outputText value="#{relatoriosCoordenador.matriz.descricaoMin}"/></b></td>
			</tr>
			<tr class="item">
				<td>Currículo: <b><h:outputText value="#{relatoriosCoordenador.curriculo.descricao}"/></b></td>
			</tr>
	</table>
	<hr>
		<c:if test="${fn:length(relatoriosCoordenador.equivalentes) == 0}">
		    <h3 class="semRegistro"><b>Nenhum Registro Encontrado</b></h3>
		</c:if>

	<table cellspacing="1" width="100%" style="font-size: 10px;">
	
	<c:forEach items="#{relatoriosCoordenador.equivalentes}" var="item">
		<c:set var="nvlAtual" value="${item.curriculoComponente.semestreOferta}"/>
		<c:if test="${nvl != nvlAtual}">
			<c:set var="nvl" value="${nvlAtual}"/>
			<tr class="nivel">
				<td>
					<b>${nvl }º Nível</b><br />
				</td>
			</tr>
		</c:if>
		<tr class="componente">
			<td>
			${ item.curriculoComponente.descricao }
			<h:outputText rendered="#{ item.curriculoComponente.obrigatoria }"><i>(Obrigatória)</i></h:outputText>
			<h:outputText rendered="#{ !item.curriculoComponente.obrigatoria }"><i>(Optativa)</i></h:outputText>
			<br />
			<i>Equivalente a:</i> <sigaa:expressao expr="${item.curriculoComponente.componente.equivalencia}"/>
			</td>
		</tr>
		<c:forEach items="#{item.sugestoes}" var="sug">
			<tr class="equivalentes">
				<td>${sug.atividade.descricaoResumida }</td>
			</tr>
		</c:forEach>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>