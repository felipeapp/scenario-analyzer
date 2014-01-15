<%@page import="java.io.*,
                java.util.*"%>

<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" href="/shared/css/pagina_erros.css"/>

<%@page import="br.ufrn.arq.util.AmbienteUtils"%>
<% request.setAttribute("desenvolvimento", !AmbienteUtils.isServidorProducao()); %>

<script language="javascript">
	function showStackTrace() {
		var st = document.getElementById("detalhes-erro");
		var textoLink = document.getElementById("texto-link-stacktrace");
		if (st.style.display == 'block') {
			st.style.display = 'none';
			textoLink.innerHTML = "Exibir";
		} else {
			st.style.display = 'block';
			textoLink.innerHTML = "Ocultar";
		}
	}
</script>

<div id="painel-erro">
	
	<div id="mensagem-erro">
		<h3>Comportamento Inesperado!</h3>
		
		<p> O sistema comportou-se de forma inesperada e por isso não foi possível realizar com sucesso a operação selecionada. </p>
		<p> 
			A ${ configSistema['nomeResponsavelInformatica'] } já foi notificada sobre este problema
			através de um e-mail automático e providenciará em breve sua correção.
		</p>
		<p>
			<br />
			Pedimos desculpas por eventuais transtornos...
		</p>
	</div>
	
	<c:if test="${desenvolvimento || acesso.administradorSistema || (not empty acessoAnterior and acessoAnterior.administradorSistema)}">
		<a href="#" onclick="javascript:showStackTrace();" id="link-detalhes-erro">
			<span id="texto-link-stacktrace">Exibir</span> stacktrace 
		</a>
		<div id="detalhes-erro">
		<h3> Exceção </h3>
		
		<c:if test="${not empty erro}">
			<%
				Throwable e = (Throwable) request.getAttribute("erro");
				Throwable cause = e.getCause();
				PrintWriter printer = new PrintWriter(out);
				e.printStackTrace(printer);
	
				if (cause != null) {
			%>
			<h3> Causa </h3>
			<% 
				   cause.printStackTrace(printer);
				}
			%>
		</c:if>
		</div>
	</c:if>
	
</div>

<center>
	<a href="javascript:history.go(-1)"> Voltar </a>
</center>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>