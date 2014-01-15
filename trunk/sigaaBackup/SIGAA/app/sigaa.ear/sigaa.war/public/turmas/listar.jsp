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
	
	
	<%-- Descrição e orientações para a consulta --%>
	<div class="descricaoOperacao">
	Caro usuário,
	<p>esta página permite consultar as turmas
	oferecidas pela instituição.</p>
	<p>Utilize o formulário abaixo para filtrar as turmas de acordo com
	os critérios desejados.</p>
	</div>
	<%@ include file="turma.jsp" %>
		<br clear="all"/>
	<center>
	  <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<%@include file="/public/include/voltar.jsp"%>
</f:view>
<%@include file="/public/include/rodape.jsp"%>