<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/ensino/latosensu/cadastroDocumentoLegal.do?dispatch=cancelar">
		<ufrn:subSistema semLink="true"/> 
	</html:link> &gt;
	Documentos Legais
</h2>

<html:form action="/ensino/latosensu/cadastroDocumentoLegal?dispatch=persist" method="post" focus="obj.curso.nome" styleId="form">

	<html:hidden property="obj.id" />

	<table class="formulario" width="80%">
		<caption>Dados do Documento Legal</caption>

		<tbody>
			<tr>
			<th>Curso:</th>
			<td>
			<html:select property="obj.curso.id">
            <html:option value="0">Escolha uma opção</html:option>
            <html:options collection="cursos" property="id" labelProperty="nome"/>
            </html:select>
            <span class="required">&nbsp;</span>
			</td>
			</tr>
			
			<tr>
            <th>Nome do Documento:</th>
            <td>
            <html:text property="obj.nomeDocumento" maxlength="100" size="50" onkeyup="CAPS(this)"/>
            <span class="required">&nbsp;</span>
            </td>
            </tr>
            
            <tr>
            <th>Número do Documento: </th>
			<td>
			<html:text property="obj.nroDocumento" size="22" maxlength="20" onkeyup="CAPS(this)"/>
			<span class="required">&nbsp;</span>
			</td>
			</tr>
            
            <tr>
           	<th>Data de Aprovação:</th>
			<td><ufrn:calendar property="dataAprovacao" /> (dd/mm/aaaa)</td>
			</tr>
            
            <tr>
            <th>Data de Publicação:</th>
            <td><ufrn:calendar property="dataPublicacao" /> (dd/mm/aaaa)</td>
            </tr>
            
            <tr>
            <th>Local de Publicação: </th>
            <td><html:text property="obj.localPublicacao" size="40" maxlength="80" onkeyup="CAPS(this)"/></td>
            </tr>
            
			<tr>
			<th>Nº do Parecer/Despacho: </th>
			<td><html:text property="obj.nroParecerDespacho" size="22" maxlength="20" onkeyup="CAPS(this)"/></td>
			</tr>
            
           	<tr>
           	<th>Data de Parecer/Despacho:</th>
   	        <td><ufrn:calendar property="dataParecerDespacho" /> (dd/mm/aaaa)</td>
   	        </tr>

            <tr>
            <th>Validade: </th>
            <td><html:text property="obj.validade" size="5" maxlength="4" /> (em anos)</td>
            </tr>

            <tr>
            <th>Tipo de Documento:</th>
            <td>
           	<html:select property="obj.tipoDocumentoLegal.id">
            <html:option value="0">Escolha uma opção</html:option>
            <html:options collection="tiposDocumentoLegal" property="id" labelProperty="descricao"/>
            </html:select>
            <span class="required">&nbsp;</span>
            </td>
            </tr>

            <tr>
            <th>IES Campus Unidade:</th>
            <td>
            <html:select property="obj.campusIes.id">
            <html:option value="0">Escolha uma opção</html:option>
            <html:options collection="campusUnidades" property="id" labelProperty="nome"/>
            </html:select>
            <span class="required">&nbsp;</span>
            </td>
            </tr>
		</tbody>
		
		<tfoot>
		<tr><td colspan="2">             
			<html:submit value="Confirmar"/>
	    	<input value="Cancelar" type="button" onclick="javascript:cancelar('form');"/>
		</td></tr>
		</tfoot>
		
		</table>
	

</html:form>
<br>
<center>
<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
</center>
<br/>

<c:if test="${ param['dispatch'] == 'remove' }">
<script type="text/javascript">
	new Remocao('form');
</script>	
</c:if>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
