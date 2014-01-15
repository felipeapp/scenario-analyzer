

<%-- Um pequeno cabeçalho interno com as informações do usuário logado na área interna de cursos e eventos de extensão da área pública do sigaa --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>


<style>

#cabecalhoInternoCursosEventosExtensao{
	background: none repeat scroll 0 0 #404E82;
    border-top: 4px solid #D99C44;
    clear: both;
    color: #FFFFFF;
    line-height: 2em;
    position: relative;
    font-weight: bold;
    width: 100%;
}

</style>

<c:if test="${sessionScope.participanteCursosEventosExtensaoLogado == null}">
	<div style="color:red; font-weight: bold; text-align: center; margin-top: 100px; margin-bottom:  100px;"> Usuário não autenticado !</div>
	
	<h:form>
		<div style="margin: 0pt auto; width: 80%; text-align: center;">
			<h:commandLink action="#{logonCursosEventosExtensaoMBean.telaLoginCursosEventosExtensao}" immediate="true"> Ir Tela de Login >> </h:commandLink>
		</div>
	</h:form>
	<br />
	<br />
	<br />
	<br />
	<br />
	
</c:if>

<c:if test="${sessionScope.participanteCursosEventosExtensaoLogado != null}">
	
	<h:form>
	
		<div id="cabecalhoInternoCursosEventosExtensao">
			<div style="width: 50%; float: left; text-align: left; margin-left: 5%; ">
				Participante: ${sessionScope.participanteCursosEventosExtensaoLogado.email}
			</div>
			<div style="width: 45%;  margin-left: 50%; margin-right: 5%; text-align: right;">
				<h:commandLink style="color: white;" action="#{logonCursosEventosExtensaoMBean.sairAreaRetritaParticipante}" immediate="true"> SAIR </h:commandLink>
			</div>
		</div>
	
	</h:form>

</c:if>
