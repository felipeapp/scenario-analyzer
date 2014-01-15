<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<ufrn:steps/>
</h2>
<div class="descricaoOperacao">
	O certificados ser�o emitido baseados nas qualifica��es cadastradas para um curso.<br>
	Uma qualifica��o representa um conjunto de M�dulos que o discente do ensino t�cnico deve
	completar para ter direito a receber certificados de conclus�o dos mesmos.
</div>
<html:form action="/ensino/tecnico/criarQualificacao" method="post" focus="obj.descricao">
    <table class="formulario" align="center" width="90%">
    <caption class="listagem">Dados da Qualifica��o</caption>
        <tr>
        	<th class="obrigatorio">
        	Curso:
        	</th>
        	<td>
        		<html:select property="obj.cursoTecnico.id">
	                <html:option value="">-- SELECIONE --</html:option>
	                <html:options collection="cursos" property="id" labelProperty="codigoNome"/>
                </html:select>
            <ufrn:help img="/img/ajuda.gif">Aqui s�o listados apenas cursos que possuam estrutura curricular ativa</ufrn:help>
        	</td>
        </tr>
        <tr>
        	<th class="obrigatorio">
        	Descri��o:
        	</th>
        	<td>
        		<html:text  property="obj.descricao" size="55" maxlength="50" onkeyup="CAPS(this)"/>
        	</td>
        </tr>
        <tr>
        	<th class="obrigatorio">
        	Habilita��o:
        	</th>
        	<td>
        		<html:radio property="obj.habilitacao" value="true" styleClass="noborder"/>Sim
            	<html:radio property="obj.habilitacao" value="false" styleClass="noborder"/>N�o
            	<ufrn:help img="/img/ajuda.gif">Caso o certificado dessa qualifica��o
            	deva informar que ela permite o discente ser "Habilitado" em determinada �rea referente aos m�dulos</ufrn:help>
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
	    		<html:button dispatch="modulos" value="Avan�ar >>" />
			</td>
			</tr>
		</tfoot>
</table>
</html:form>
<br/>
<div class="obrigatorio"> Campos de preenchimento obrigat�rio. </div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
