<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp" %>
<f:view>

<style>
	*{
		font-family:"Verdana";
		color:#000000;
		text-decoration:none;
	}
	
	h1{
		font-size:11pt;
		margin:0px;
		font-weight:bold;
		text-align:center;
	}
	
	h2{
		font-size:12pt;
		margin:0px;
		color:#000000;
		border:none;
		background:none;
		text-align:center;
	}
	
	img{
		border:none;
	}

	.topico-aula{
		margin-top:10px;
	}
	
	.topico-aula h3{
		font-weight:bold;
		font-size:10pt;
		margin:0px;
	}
	
	.topico-aula .descricao-aula{
		margin:0px;
		font-size:9pt;
	}
	
	.topico-aula .materiais{
		list-style:none;
		font-size:9pt;
	}
	
	.topico-aula .materiais li{
		margin-top:5px;
	}
</style>


	<h:form>
		<%@include file="/ava/aulas.jsp" %>
	</h:form>
	
	<script>window.print();</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp" %>