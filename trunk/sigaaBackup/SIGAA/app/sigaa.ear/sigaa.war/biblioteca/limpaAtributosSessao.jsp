

<%-- Essa pagina � incluida na pagina inicial do sub sistemas da biblioteca --%>
<%-- Sempre que o usu�rio passa pela p�gina pricipal retirar da sess�o aqueles Mbean --%>
<%-- que n�o foi poss�vel colocar em request--%>




<%-- Circula��o --%>

<c:remove var="usuarioExternoBibliotecaMBean" scope="session"/> 
