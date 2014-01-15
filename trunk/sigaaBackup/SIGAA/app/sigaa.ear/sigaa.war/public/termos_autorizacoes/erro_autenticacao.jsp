 <%-- JSP que representa o documento de Termo de Autorização de Publicação de Tese/Dissertação --%>

<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style type="text/css">

h4{
	text-align: center;
}

#divDados {
	margin-bottom:30px;
}

#div1{
	font-weight: bold;
	color: red;
	width: 100%;
	text-align: center;
}

#div2{
	margin-top:  20px;  
	font-style: italic;
	width: 100%;
	text-align: center;
}

</style>

	<h4>Termo de Autorização para Publicação de Teses e Dissertações
	Eletrônicas (TOE) na Biblioteca Digital de Teses e Dissertações (BDTD)</h4>
	
	<div id="divDados">	
		<div id="div1"> Erro na geração do TEDE.</div>

		<div id="div2"> As informações do documento são inválidas </div>
	</div>
	

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>