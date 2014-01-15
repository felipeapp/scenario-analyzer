<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:steps/>
</h2>

<html:form action="/ensino/tecnico/criarQualificacao" method="post">
<table class="formulario" cellpadding="3" width=650">
	<caption class="listagem">Qualifica��o</caption>
	<tr>
		<th>
		<b>Curso:</b>
		</th>
		<td>
		${qualificacaoForm.obj.cursoTecnico.nome}
		</td>
	</tr>
	<tr>
		<th>
		<b>Descri��o:</b>
		</th>
		<td>
		${qualificacaoForm.obj.descricao}
		</td>
	</tr>
	<tr>
		<th>
		<b>Habilita��o:</b>
		</th>
		<td>
		<ufrn:format type="bool_sn" valor="${qualificacaoForm.obj.habilitacao}" />
		</td>
	</tr>

	<tr>
	<td colspan="2" align="center">
	<table class="subFormulario" width="100%">
	<caption>Indique quais o m�dulos s�o necess�rios para essa qualifica��o</caption>
	<tr>
		<th width="150" class="obrigatorio">
		M�dulo:
		</th>
		<td>
		<html:select property="modulo.id">
	    	<html:option value=""> -- SELECIONE -- </html:option>
	    	<html:options collection="modulos"
	    			property="id" labelProperty="descricao"/>
        </html:select>&nbsp;&nbsp;
        <html:button dispatch="adicionarModulo" value="Adicionar M�dulo"/>
		</td>
	</tr>
	</table>
	</td>
	</tr>

	<tr>
	<td colspan="2" align="center">
	<c:if test="${not empty qualificacaoForm.obj.modulos}">
	    <table class="subFormulario" width="90%">
	        <thead>
	        <td align="left"><b>
			M�dulos adicionados nesta qualifica��o
			</b></td>
	        <td width="20">&nbsp;</td>
	        <tbody>

	        <c:forEach items="${qualificacaoForm.obj.modulos}" var="modulo">
	            <tr>
	               <html:hidden property="modulo.id" value="${modulo.id}" />
	               <td>${modulo.fullDesc}</td>
	               <td align="right">
	                   <html:link action="/ensino/tecnico/criarQualificacao?dispatch=removerModulo&id=${ modulo.id }">
	                       <html:img page="/img/delete.gif" alt="Remover este M�dulo da Qualifica��o" title="Remover" border="0"/>
	                   </html:link>
	               </td>
	            </tr>
	        </c:forEach>
	    </table>
	</c:if>
	</td>
	</tr>

	<tfoot><tr>
	<td colspan="2">
	<br>
	<html:button view="dadosBasicos" value="<< Dados B�sicos da Qualifica��o"/>
	<html:button dispatch="cancelar" value="Cancelar"/>
	<html:button dispatch="resumo" value="Confirmar Qualifica��o >>"/>
	</td>
	</tfoot></tr>
</table>
</html:form>
<br>
<div class="obrigatorio"> Campos de preenchimento obrigat�rio. </div>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
