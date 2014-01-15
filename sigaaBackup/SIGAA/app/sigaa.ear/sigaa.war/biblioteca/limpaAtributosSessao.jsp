

<%-- Essa pagina é incluida na pagina inicial do sub sistemas da biblioteca --%>
<%-- Sempre que o usuário passa pela página pricipal retirar da sessão aqueles Mbean --%>
<%-- que não foi possível colocar em request--%>




<%-- Circulação --%>

<c:remove var="usuarioExternoBibliotecaMBean" scope="session"/> 
