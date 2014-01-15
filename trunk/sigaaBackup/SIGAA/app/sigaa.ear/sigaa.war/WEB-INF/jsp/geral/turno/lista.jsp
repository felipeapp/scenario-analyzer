<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
 
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 

<br/>
<h2>
	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link> >
	<fmt:message key="titulo.listar">
		<fmt:param value="Turno"/>
	</fmt:message>
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>

<html:form action="/geral/listarTurnos" method="post" focus="codigo">
	<div class="areaDeDados">
		<h2>Busca por Turnos</h2>
		<div class="dados">
            <div class="head"><input type="radio" name="tipoBusca" value="1" class="noborder">Sigla</input></div>
            <div class="texto"><html:text property="sigla" size="30" value="" onfocus="javascript:forms[0].tipoBusca[0].checked = true;"></html:text></div>
            <br/>
            
            <div class="head"><input type="radio" name="tipoBusca" value="2" class="noborder"> Descrição</input></div>
            <div class="texto"><html:text property="descricao" size="30" value="" onfocus="javascript:forms[0].tipoBusca[1].checked = true;"></html:text></div>
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
    : Alterar dados do Turno
    <html:img page="/img/delete.gif" style="overflow: visible;"/>
    : Remover Turno </b>
</div>
<br>

<div class="areaDeDados lista">
    <h2>Turnos Cadastrados</h2>
    <table>
        <thead>
        <th>Sigla</th>
        <th>Nome</th>
		<th colspan="2">&nbsp;</th>

        <tbody>

        <c:forEach items="${turnos}" var="turno">
            <tr>
                <html:form action="/geral/detalharTurno" method="post">
                    <html:hidden name="turno" property="id"/>
                    <html:hidden property="acao" value="alterar"/>
                    <td> ${turno.sigla} </td>
                    <td> ${turno.descricao} </td>
                    <td width="15">
                        <html:image style="border: none" page="/img/alterar.gif" alt="Alterar dados do Turno" title="Alterar dados do Turno" value="Alterar"></html:image>
                    </td>
                </html:form>
                <html:form action="/geral/detalharTurno" method="post">
                    <html:hidden name="turno" property="id"/>
                    <html:hidden property="acao" value="remover"/>
                    <td width="15">
                        <html:image style="border: none" page="/img/delete.gif" alt="Remover Turno" title="Remover Turno" value="Remover"></html:image>
                    </td>
                </html:form>
            </tr>
        </c:forEach>  
    </table>
</div>
<br><br>
<center>
	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Menu do Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link>
</center>