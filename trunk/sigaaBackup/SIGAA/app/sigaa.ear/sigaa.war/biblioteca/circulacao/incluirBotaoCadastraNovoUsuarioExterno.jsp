<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>


<%--  P�gina que cont�m os bot�es com as a��es extras utilizadas na p�gina de busca de  --%>


<style>
	.menu-botoes ul li a h5 {
		left: 60px !important;
	}
	
	.menu-botoes ul li a p {
		background-image: url('/sigaa/img/novo_usuario.gif');
		padding-left: 50px !important;
	}
</style>



<div class="menu-botoes"  style="width: 350px; margin: 0 auto; margin-top:10px; padding-bottom: 80px; margin-bottom: 10px;">
	<ul class="menu-interno">
		<li class="botao-grande plano;">
	    	<h:commandLink action="#{usuarioExternoBibliotecaMBean.iniciarCadastroDadosPessoa}">
				<h5>Cadastrar Novo Usu�rio Externo</h5> 
				<p>Clique aqui para cadastrar um novo usu�rio externo.</p>
			</h:commandLink> 
		</li>
	</ul>
</div>
	

	
	
