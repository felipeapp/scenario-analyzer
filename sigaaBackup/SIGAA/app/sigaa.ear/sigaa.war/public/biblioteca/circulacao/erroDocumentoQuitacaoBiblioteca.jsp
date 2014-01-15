
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

 <%-- JSP que representa o documento de quitacao da biblioteca --%>

<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style type="text/css">

h4{
	text-align: center;
}

h3{
	margin-top:20px;
	margin-bottom:30px;
	text-align: center;
}

#divDadosUsuario {
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

	<h4>${verificaSituacaoUsuarioBibliotecaMBean.descricaoSistemaBiblioteca}</h4>
	
	<h3>DECLARAÇÃO DE QUITAÇÃO</h3>
	
	<div id="divDadosUsuario">
	
		<div id="div1"> Erro na geração do documento de quitação </div>

		<div id="div2"> As informações do documento são inválidas </div>
		
	</div>
	

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>