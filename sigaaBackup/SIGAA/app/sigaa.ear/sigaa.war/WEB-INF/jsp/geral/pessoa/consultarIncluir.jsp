<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
 
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 

<br/>
<h2>
	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link> >
		<fmt:message key="titulo.listar">
		<fmt:param value="Pessoa"/>
	</fmt:message>
</h2>
<hr>
<%@include file="/WEB-INF/jsp/include/mensagem.jsp"%>

<style>
<!--
.areaDeDados .dados .texto {
  margin-left: 25em;
}

.areaDeDados .dados .head {
  margin-left: 13em;
-->
</style>

<html:form action="/geral/consultarIncluir?forward=consultar_incluir" method="post" focus="iniciaisNome">
	<div class="areaDeDados">
		<h2>Busca por Pessoa</h2>
		<div class="dados">
            <div class="head">
				<html:hidden property="tipoBusca" value="3"/>
				Iniciais do Nome</br></br>
				Data de Nascimento
            </div>
            <div class="texto">
            	<html:text property="iniciaisNome" size="10" value="" onfocus="javascript:forms[0].tipoBusca[0].checked = true;"></html:text></br></br>
            	<html:text property="dataNascimento" size="10" value="" onkeypress="return formataData(this,event)"></html:text>
            </div>
            <br/>
           
            <div class="botoes">
                <html:submit><fmt:message key="botao.buscar" /></html:submit>
            </div>
		</div>
	</div>
</html:form>

<c:if test="${not empty pessoas}">
<br>
<div class="infoAltRem">
    <html:img page="/img/alterar.gif" style="overflow: visible;"/>
    : Alterar
</div>
<br>

<div class="areaDeDados lista">
    <h2>Pessoa(s) já cadastrada(s)</h2>
    <table>
        <thead>
        <th>Nome</th>
        <th>CPF</th>

        <th colspan="2">&nbsp;</th>

        <tbody>

		<c:forEach items="${pessoas}" var="pessoa">
			<tr>
				<html:form action="/geral/detalharPessoa?origem=consulta" method="post">
					<html:hidden property="acao" value="alterar"/>
					<html:hidden name="pessoa" property="id"/>
					<td> ${pessoa.nome} </td>
					<td> ${pessoa.cpf} </td>
					<td width="15">
						<html:image style="border: none" page="/img/alterar.gif" alt="Alterar dados da Pessoa" title="Alterar dados da Pessoa" value="Alterar"></html:image>
					</td>
				</html:form>
			</tr>
		</c:forEach>
		</tbody>  
    </table>
</div>
</c:if>

<br><br>
<center>
	<html:link action="/verMenuEnsino?tipoEnsino=${tipoEnsino}">
		Menu do Ensino <fmt:message key="label.ensino.${tipoEnsino}" />
	</html:link>
</center>
