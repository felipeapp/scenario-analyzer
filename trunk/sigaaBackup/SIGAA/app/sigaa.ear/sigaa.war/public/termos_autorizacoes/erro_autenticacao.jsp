 <%-- JSP que representa o documento de Termo de Autoriza��o de Publica��o de Tese/Disserta��o --%>

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

	<h4>Termo de Autoriza��o para Publica��o de Teses e Disserta��es
	Eletr�nicas (TOE) na Biblioteca Digital de Teses e Disserta��es (BDTD)</h4>
	
	<div id="divDados">	
		<div id="div1"> Erro na gera��o do TEDE.</div>

		<div id="div2"> As informa��es do documento s�o inv�lidas </div>
	</div>
	

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>