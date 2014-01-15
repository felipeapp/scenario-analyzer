<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="manifestacaoDocente" />

<f:view>
	<h2>
		<ufrn:subSistema /> &gt; Entrar em Contato com a Ouvidoria
	</h2>

	<div id="ajuda" class="descricaoOperacao">
		<p>Caro docente, preencha o formul�rio abaixo com os dados necess�rios para entrar em contato com a Ouvidoria.</p>
		<p>Ap�s o cadastro de uma manifesta��o, voc� pode acompanh�-la atrav�s do caminho: </p>
		<p style="left: 5%;">SIGAA -> Portal do Docente -> Outros -> Ouvidoria -> Acompanhar Manifesta��es.</p>			
		<p style="left: 5%;">Obs: O arquivo que pode ser anexado a manifesta��o dever� ter no m�ximo 50Mb de tamanho.</p>
	</div>

	<h:form enctype="multipart/form-data"  id="formManifestacao">
		<table class="visualizacao" style="width: 90%">
			<tr>
				<td width="14%"></td>
				<th width="3%" style="text-align: right;" nowrap="nowrap">Categoria de Solicitante Identificada:</th>
				<td style="text-align: left;">${manifestacaoDocente.obj.interessadoManifestacao.categoriaSolicitante.descricao }</td>
			</tr>
			<tr>
				<td width="14%"></td>
				<th width="3%" style="text-align: right;" nowrap="nowrap">Nome:</th>
				<td style="text-align: left;">${manifestacaoDocente.servidorUsuario.nome }</td>
			</tr>
			<tr>
				<td width="14%"></td>
				<th width="3%" style="text-align: right;" nowrap="nowrap">SIAPE:</th>
				<td style="text-align: left;">${manifestacaoDocente.servidorUsuario.siape }</td>
			</tr>
		</table>
		<br />
		<table class="formulario" width="80%">
			<caption>Informa��es sobre a manifesta��o</caption>
			<tbody>
			<tr>
				<th class="required" width="30%">Categoria do Assunto:</th>
				<td>
					<h:selectOneMenu id="categoria" value="#{manifestacaoDocente.obj.assuntoManifestacao.categoriaAssuntoManifestacao.id }" onchange="submit()" style="width: 95%">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{categoriaAssuntoManifestacao.allCategoriasAtivasCombo }" />
						<a4j:support event="onselect" reRender="assunto" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Assunto:</th>
				<td>
					<h:selectOneMenu id="assunto" value="#{manifestacaoDocente.obj.assuntoManifestacao.id }" style="width: 95%">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{manifestacaoDocente.allAssuntosByCategoriaCombo }" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Tipo da Manifesta��o:</th>
				<td>
					<h:selectOneRadio id="tipo" value="#{manifestacaoDocente.obj.tipoManifestacao.id }" style="width: 95%">
						<f:selectItems value="#{tipoManifestacao.allTiposManifestacaoCombo }" />
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<th class="required">T�tulo:</th>
				<td>
					<h:inputText id="titulo" value="#{manifestacaoDocente.obj.titulo }" maxlength="50" style="width: 95%" />
				</td>
			</tr>
			<tr>
				<th>Arquivo:</th>
				<td>
					<t:inputFileUpload id="ifuArquivo" size="70" storage="file" value="#{manifestacaoDocente.arquivo}" />
				</td>
			</tr>
			<tr>
				<td style="text-align: right; vertical-align: top;">
					<h:selectBooleanCheckbox value="#{manifestacaoDocente.obj.anonima}" styleClass="noborder" id="anonima" />
				</td>
				<td>
					<label for="anonima" onclick="$('formManifestacao:anonima').checked = !$('formManifestacao:anonima').checked;">
						Desejo que meu sigilo seja mantido.
					</label>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<div class="descricaoOperacao" style="margin: 0; text-align: center;">
						<i>Caso solicitado, o sigilo <b>N�O</b> valer� para a Ouvidoria, mas <b>APENAS</b> para a Unidade respons�vel por resposta, se esse for o caso.<br />
						Segundo a resolu��o 024/02 - CONSAD, Art 3�. Inciso VII), cabe � Ouvidoria: "Guardar sigilo quanto � identidade dos denunciantes, se solicitado ou quando entender que a identifica��o possa-lhes causar transtornos."</i>
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
						<caption style="text-align: center;">Mensagem <h:graphicImage url="/img/required.gif" /></caption>
						<tr>
							<td style="text-align: center;">
								<h:inputTextarea id="mensagem" value="#{manifestacaoDocente.obj.mensagem }" rows="15" style="width: 99%;" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
			</tbody>				
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btn_notificar" value="Cadastrar Manifesta��o" action="#{manifestacaoDocente.cadastrar}" />
						<h:commandButton id="btn_cancelar" value="Cancelar"  action="#{manifestacaoDocente.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigat�rio. </span> <br>
	<br>
	</center>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
