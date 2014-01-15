<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title">Menu de Consultas</h2>

	<h:form id="menuConsulta">
	<table align="center">
		<tr>
		<td align="center">
			<h:commandButton id="consultaDiscente" image="/img/icones/medio.gif" action="#{ consultaDiscente.iniciar }"/>
			<br><h3>Consultar Discentes</h3>
		</td>
		<td>&nbsp;</td>
		<td>&nbsp;</td>
		<td align="center"> 
			<a href="${ ctx }/administracao/cadastro/Servidor/lista.jsf"> <img src="${ ctx }/img/icones/posgrad.gif"> </a> 
			<br><h3>Consultar Servidores</h3>
		</td>

		</tr>
	</table>
	</h:form>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
