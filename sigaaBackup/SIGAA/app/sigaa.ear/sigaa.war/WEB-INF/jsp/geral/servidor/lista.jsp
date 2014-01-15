<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
 
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 
<style>
.areaDeDados .dados .texto {
  margin-left: 15em;
}
</style>

<br/>
<h2>
	<html:link action="/verMenuPrinciapl">Principal</html:link> >
	<fmt:message key="titulo.listar"><fmt:param value="Servidor"/></fmt:message>
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>

<html:form action="/geral/listarServidores" method="post" focus="matricula">
	<div class="areaDeDados">
		<h2>Busca por Servidor</h2>
		<div class="dados">
            <div class="head"><input type="radio" name="tipoBusca" value="1" class="noborder">Nome</input></div>
            <div class="texto"><html:text property="nome" size="30" value="" onfocus="javascript:forms[0].tipoBusca[0].checked = true;"></html:text></div>
            <br/>
                
            <div class="head"><input type="radio" name="tipoBusca" value="2" class="noborder">Matrícula</input></div>
            <div class="texto"><html:text property="matricula" size="30" value="" onfocus="javascript:forms[0].tipoBusca[1].checked = true;"></html:text></div>
            <br/>
                
            <div class="head"><input type="radio" name="tipoBusca" value="3" class="noborder"> Todos</input></div>
            <br/>
            
            <div class="botoes">
                <html:submit><fmt:message key="botao.buscar" /></html:submit>
            </div>
		</div>
    </div>
</html:form>

<br/>
	<div class="infoAltRem">
	    <html:img page="/img/alterar.gif" style="overflow: visible;"/>
	    : Alterar dados do Servidor
	    <html:img page="/img/delete.gif" style="overflow: visible;"/>
	    : Remover Servidor </b>
	</div>
<br>

<div class="areaDeDados lista">
    <h2>Servidores Cadastrados</h2>
    <table>
        <thead>
        <th>Matrícula</th>
        <th>Nome</th>

        <th colspan="2">&nbsp;</th>

        <tbody>

        <c:forEach items="${servidores}" var="servidor">
            <tr>
                <html:form action="/geral/detalharServidor" method="post">
                    <html:hidden name="servidor" property="id"/>
                    <html:hidden property="acao" value="alterar"/>
                    <td>${servidor.siape} </td>
                    <td>${servidor.pessoa.nome} </td>
                    <td width="15">
                        <html:image style="border: none" page="/img/alterar.gif" alt="Alterar dados do Sevidor" title="Alterar dados do Sevidor" value="Alterar"></html:image>
                    </td>
                </html:form>
                <html:form action="/geral/detalharServidor" method="post">
                    <html:hidden name="servidor" property="id"/>
                    <html:hidden property="acao" value="remover"/>
                    <td width="15">
                        <html:image style="border: none" page="/img/delete.gif" alt="Remover Sevidor" title="Remover Sevidor" value="Remover"></html:image>
                    </td>
                </html:form>
            </tr>
        </c:forEach>  
    </table>
</div>
<br><br>
<center>
	<html:link action="/verMenuEnsinoPrincipal.do">Menu do Ensino Principal</html:link>
</center>
