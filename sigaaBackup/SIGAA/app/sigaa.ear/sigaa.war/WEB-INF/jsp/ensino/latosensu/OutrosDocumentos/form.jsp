<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina">
	<ufrn:subSistema />&gt;
	Cadastro de Outros Documentos
</h2>

<html:form action="/ensino/latosensu/cadastroOutrosDocumentos?dispatch=persist" method="post" focus="obj.curso.nome" styleId="form" >
    <table class="formulario" width="90%">
		<caption>Dados do Documento</caption>
		<html:hidden property="obj.id" />

		<tbody>
			<tr>
				<td></td>
				<td></td>
 				<th nowrap="nowrap">Curso:</th>
	    	        <td>
	    	        	<c:set var="idAjax" value="obj.curso.id"/>
				    	<c:set var="nomeAjax" value="obj.curso.nome"/>
				    	<c:set var="tipoCurso" value="lato"/>
						<%@include file="/WEB-INF/jsp/include/ajax/curso_tecnico.jsp" %>
						<span class="required">&nbsp;</span>
					</td>
        	</tr>
			<tr>
				<td></td>
				<td></td>
 				<th nowrap="nowrap">Descrição:</th>
				<td>
				<html:text property="obj.descricao" maxlength="100" size="80" onkeyup="CAPS(this)"/>
				<span class="required">&nbsp;</span>
				</td>
            </tr>

           <tr>
				<td></td>
				<td></td>
 				<th nowrap="nowrap">Outras Informações:</th>
            	<td><html:text property="obj.outrasInformacoes" maxlength="100" size="80" onkeyup="CAPS(this)"/></td>
           </tr>

           	<tr>
				<td></td>
				<td></td>
 				<th nowrap="nowrap">Data:</div>
    	        <td>
    	        <ufrn:calendar property="data" />
    	        <span class="required">&nbsp;</span>
    	        </td>
    	    </tr>
        </tbody>
        <tfoot>
			<tr>

				<td colspan="4" align="center">
					<html:submit value="Confirmar" />
                	<input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
                </td>

			</tr>
		</tfoot>
	</table>
</html:form>

<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>