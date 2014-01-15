<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<ufrn:steps/>
</h2>
<div class="descricaoOperacao">
	O certificados serão emitido baseados nas qualificações cadastradas para um curso.<br>
	Uma qualificação representa um conjunto de Módulos que o discente do ensino técnico deve
	completar para ter direito a receber certificados de conclusão dos mesmos.
</div>
<html:form action="/ensino/tecnico/criarQualificacao" method="post" focus="obj.descricao">
    <table class="formulario" align="center" width="90%">
    <caption class="listagem">Dados da Qualificação</caption>
        <tr>
        	<th class="obrigatorio">
        	Curso:
        	</th>
        	<td>
        		<html:select property="obj.cursoTecnico.id">
	                <html:option value="">-- SELECIONE --</html:option>
	                <html:options collection="cursos" property="id" labelProperty="codigoNome"/>
                </html:select>
            <ufrn:help img="/img/ajuda.gif">Aqui são listados apenas cursos que possuam estrutura curricular ativa</ufrn:help>
        	</td>
        </tr>
        <tr>
        	<th class="obrigatorio">
        	Descrição:
        	</th>
        	<td>
        		<html:text  property="obj.descricao" size="55" maxlength="50" onkeyup="CAPS(this)"/>
        	</td>
        </tr>
        <tr>
        	<th class="obrigatorio">
        	Habilitação:
        	</th>
        	<td>
        		<html:radio property="obj.habilitacao" value="true" styleClass="noborder"/>Sim
            	<html:radio property="obj.habilitacao" value="false" styleClass="noborder"/>Não
            	<ufrn:help img="/img/ajuda.gif">Caso o certificado dessa qualificação
            	deva informar que ela permite o discente ser "Habilitado" em determinada área referente aos módulos</ufrn:help>
        	</td>
        </tr>
        <tr>
        	<th valign="top">
        	Texto para Certificado:
        	</th>
        	<td>
        		<ufrn:textarea property="obj.textoCertificado" maxlength="255" cols="90" rows="5" />
        	</td>
        </tr>
		<tfoot>
			<tr>
			<td colspan="2">
	    		<html:button dispatch="cancelar" value="Cancelar" />
	    		<html:button dispatch="modulos" value="Avançar >>" />
			</td>
			</tr>
		</tfoot>
</table>
</html:form>
<br/>
<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
