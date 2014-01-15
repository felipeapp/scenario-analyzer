<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<h2 class="tituloPagina">
	<html:link action="/ensino/cadastroCoordenacaoCurso?dispatch=cancel">
		<ufrn:subSistema semLink="true"/>
	</html:link>
	&gt; Coordenação de Curso
</h2>


<html:form action="/ensino/cadastroCoordenacaoCurso?dispatch=persist" method="post" focus="obj.curso.id" styleId="form">

	<html:hidden property="obj.id" />

    <table class="formulario" width="80%">
    <caption>Dados da Coordenação do Curso</caption>

	<tbody>
 	<tr>
        <th>Curso:</th>
       	<td>
       		<html:select property="obj.curso.id">
                <html:option value="">-- SELECIONE --</html:option>
                <html:options collection="cursos" property="id" labelProperty="nome"/>
            </html:select>
               <span class="required">&nbsp;</span>
       	</td>
    </tr>
    <tr>
		<th>Docente:</th>
		<td>
			<c:set var="idAjax" value="obj.servidor.id"/>
			<c:set var="nomeAjax" value="obj.servidor.pessoa.nome"/>
			<%@include file="/WEB-INF/jsp/include/ajax/servidor.jsp" %>
		</td>
	</tr>
	<tr>
        <th>Função:</th>
       	<td>
       		<ufrn:radios property="obj.cargoAcademico.id" collection="${ cargos }"
       				valueProperty="id" labelProperty="descricao" />
            <span class="required">&nbsp;</span>
       	</td>
    </tr>
	<tr>
		<th>Início de Mandato:</th>
		<td>
			<ufrn:calendar property="dataInicioMandato"/>
			<span class="required">&nbsp;</span><br/>
		</td>
	</tr>
	<tr>
		<th>Término de Mandato:</th>
		<td>
			<ufrn:calendar property="dataFimMandato"/>
		</td>
	</tr>
	</tbody>
	<tfoot>
		<tr><td colspan="2">
<%--			<html:button dispatch="persist" value="Cadastrar"/>
			<input type="hidden" name="page" value="${param.page}"/>
			<html:button dispatch="cancel" value="Cancelar"/>		--%>
			<html:submit>Confirmar</html:submit>
			<input type="hidden" name="page" value="${param.page}"/>
	    	<input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
	      	</td>
	    </tr>
	</tfoot>
</table>
</html:form>

<br>
<center>
<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>
<br>

<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp" %>