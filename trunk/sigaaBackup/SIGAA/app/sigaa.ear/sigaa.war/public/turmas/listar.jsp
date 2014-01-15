<%@include file="/public/include/cabecalho.jsp"%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<script>
	var marcar = function(idCheck) {
		$(idCheck).checked = true;
	}
</script>
<style>
	table.listagem  tr.agrupador {background: #C8D5EC;font-weight: bold;padding-left: 20px;}
</style>




<f:view>

	<h2 class="title">Ensino > Consulta de Turmas</h2>
	
	
	<%-- Descri��o e orienta��es para a consulta --%>
	<div class="descricaoOperacao">
	Caro usu�rio,
	<p>esta p�gina permite consultar as turmas
	oferecidas pela institui��o.</p>
	<p>Utilize o formul�rio abaixo para filtrar as turmas de acordo com
	os crit�rios desejados.</p>
	</div>
	<%@ include file="turma.jsp" %>
		<br clear="all"/>
	<center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
	</center>
	<%@include file="/public/include/voltar.jsp"%>
</f:view>
<%@include file="/public/include/rodape.jsp"%>