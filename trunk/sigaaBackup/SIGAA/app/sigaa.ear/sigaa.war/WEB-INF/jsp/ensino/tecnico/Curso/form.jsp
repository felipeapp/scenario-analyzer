<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="ensino/tecnico/cadastroCursoTecnico.do?dispatch=cancelar">
		<ufrn:subSistema semLink="true"/>
	</html:link>
	<c:choose><c:when test="${param.dispatch == 'remove' }">
	&gt; Remoção de Curso
	</c:when><c:otherwise>
	&gt; ${ (formCursoTecnico.obj.id == 0) ? "Cadastro" : "Atualização" } de Curso
	</c:otherwise></c:choose>
</h2>

<html:form action="ensino/tecnico/cadastroCursoTecnico.do?dispatch=persist" styleId="form" focus="obj.codigoInep">
	<html:hidden property="obj.id"/>
	<html:hidden property="obj.unidade.id" value="${sessionScope.usuario.unidade.id}" />

    <table class="formulario" width="90%">
        <caption>Dados do Curso</caption>

        <tbody>
            <tr>
            <th>Código no INEP: </th>
            <td>
                <html:text property="obj.codigoInep" size="6" maxlength="6" onkeyup="formatarInteiro(this);"/>
                (Código do Curso cadastrado no INEP/SETEC)
            </td>
            </tr>

            <tr>
            <th class="required">Código na ${ configSistema['siglaInstituicao'] }: </th>
            <td>
                <html:text property="obj.codigo" size="7" maxlength="7"  onkeyup="CAPS(this)" update="false"/>
            </td>
            </tr>

            <tr>
            <th class="required">Nome: </th>
            <td>
                <html:text property="obj.nome" size="70" maxlength="100" onkeyup="CAPS(this)"/>
            </td>
            </tr>

            <tr>
            <th class="required">Início do Funcionamento: </th>
            <td>
                <ufrn:calendar property="dataInicioFuncionamento" />
            </td>
            </tr>

            <tr>
            <th class="required">Carga Horária Mínima:</th>
            <td>
            	<html:text property="obj.chMinima" size="5" maxlength="6" onkeyup="formatarInteiro(this);"/>
                 (em horas)
            </td>
            </tr>

            <tr>
            <th class="required">Modalidade: </th>
            <td>
            <html:select property="obj.modalidadeCursoTecnico.id" style="width: 350px;">
            <html:options collection="modalidades" property="id" labelProperty="descricao"/>
            </html:select>
            </td>
            </tr>

            <tr>
            <th>Regime Letivo: </th>
            <td>
            <html:select property="obj.tipoRegimeLetivo.id" style="width: 350px;">
            <html:options collection="tiposRegime" property="id" labelProperty="descricao"/>
            </html:select>
            </td>
            </tr>

			<tr>
            <th>Sistema Curricular: </th>
            <td>
            <html:select property="obj.tipoSistemaCurricular.id" style="width: 350px;">
            <html:options collection="sistemas" property="id" labelProperty="descricao"/>
            </html:select>
            </td>
            </tr>

			<tr>
            <th>Situação do Curso: </th>
            <td>
            <html:select property="obj.situacaoCursoHabil.id" style="width: 350px;" >
            <html:options collection="situacoes" property="id" labelProperty="descricao" />
            </html:select>
            </td>
            </tr>

            <tr>
            <th>Situação do Diploma:</th>
            <td>
                 <html:select property="obj.situacaoDiploma.id" style="width: 350px;">
	                <html:options collection="tipoSituacoesDiplomas" property="id" labelProperty="descricao"/>
                </html:select>
            </td>
            </tr>

			<tr>
            <th class="required">Turno: </th>
            <td>
            <html:select property="obj.turno.id" style="width: 350px;">
            <html:options collection="turnos" property="id" labelProperty="descricao"/>
            </html:select>

            </td>
            </tr>

            <tr>
            <th class="required">Ativo:</th>
            <td>
            	<html:radio property="obj.ativo" value="true" styleId="simAtivo" styleClass="noborder"/> <label for="simAtivo">Sim</label>
            	<html:radio property="obj.ativo" value="false" styleId="naoAtivo" styleClass="noborder"/> <label for="naoAtivo">Não</label>

            </td>
            </tr>

            </tbody>

            <tfoot>
    		<tr>
    		<td colspan="2">
             <html:submit value="${ formCursoTecnico.obj.id == 0 ? 'Cadastrar' : 'Confirmar' }"></html:submit>
	    	<input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
		    </td>
		    </tr>
		    </tfoot>
	</table>
</html:form>


<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>


<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
	new Remocao('form');
</script>
</c:if>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>