<%  
	response.setHeader("Cache-Control", "must-revalidate");
	response.setHeader("Cache-Control", "max-age=1");
	response.setHeader("Cache-Control", "no-cache");
	response.setIntHeader("Expires", 0);
	response.setHeader("Pragma", "no-cache");
%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<style>
tr.curso td {
	padding: 15px 0 0;
	border-bottom: 1px solid #555
}

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

tr.discente td {
	border-bottom: 1px solid #888;
	font-weight: bold;
	padding-top: 7px;
}

tr.componentes td {
	padding: 4px 2px 2px;
	border-bottom: 1px dashed #888;
}

tr.componentes td.assinatura {
	padding: 2px;
	border-bottom: 1px solid #888;
	width: 40%;
}
</style>
<f:view>
	<a4j:keepAlive beanName="listaAssinaturasGraduandos"></a4j:keepAlive>
	<h2 class="tituloTabela"><b>Lista de assinatura para Colação de Grau<br>
	<h:outputText value="#{listaAssinaturasGraduandos.ano}.#{listaAssinaturasGraduandos.periodo}"/>
	<h:outputText value="- #{listaAssinaturasGraduandos.obj.descricao}"/></h2>
	<h2 class="tituloTabela"><b>Data: ____ /____ /______<br/><br/></b></h2>
	
	<c:set var="total" value="0" />
	<c:set var="modalidade" value="-" />
	<c:set var="habilitacao" value="-" />
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	
	<c:if test="${empty listaAssinaturasGraduandos.graduandos}">
	<center><font color="red">Não existem discentes graduandos nesse curso para esse semestre informado.</font></center>
	</c:if>
	
	<c:if test="${not empty listaAssinaturasGraduandos.graduandos}">
	<c:forEach items="#{listaAssinaturasGraduandos.graduandos}" var="linha">
		<c:if
			test="${modalidade != linha.matrizCurricular.grauAcademico.descricao || habilitacao != linha.matrizCurricular.habilitacao.nome}">
			<c:set var="modalidade"
				value="#{linha.matrizCurricular.grauAcademico.descricao}" />
			<c:set var="habilitacao"
				value="#{linha.matrizCurricular.habilitacao.nome}" />
				<tr class="header">
					<td align="left" colspan="2"><b> <c:if
						test="${modalidade != null}">Modalidade: ${modalidade}</c:if> <c:if
						test="${habilitacao != null}"> - Habilitação: ${habilitacao}</c:if></b></td>
				</tr>
				<tr class="header">
					<td align="left"><b>Discente</b></td>
					<td align="center"><b>Assinatura</b></td>
				</tr>
		</c:if>
		<tr class="componentes">
			<td>${linha.matricula} - ${linha.nomeOficial }</td>
			<td class="assinatura"></td>
		</tr>
		<c:set var="total" value="${total+1}" />
	</c:forEach>
	</c:if>
	
	<tr class="foot">
		<td colspan="2">Total: ${total}</td>
	<tr>
		</table>
		<br><br><br>
		<div align="center">
		<p class="assinatura">____________________________________________________<br>Diretor(a) da DRED</p><br><br>
		<p class="assinatura">____________________________________________________<br>Diretor(a) do DAE</p><br><br>
		<p class="assinatura">____________________________________________________<br>Coordenador(a) de Curso</p>
		</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>