<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="manifestacaoOuvidoria" />

<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>

<f:view>
	<h2>
		<ufrn:subSistema /> &gt; Cadastro de Manifestação
	</h2>
	
	<div id="ajuda" class="descricaoOperacao">
		<p>Para prosseguir com o cadastro, favor preencher o formulário abaixo com os dados da manifestação.</p>
		<p style="left: 5%;">Obs:O arquivo que pode ser anexado a manifestação deverá ter no máximo 50Mb de tamanho.</p>
	</div>

	<h:form enctype="multipart/form-data" id="form">
		<c:if test="${manifestacaoOuvidoria.comunidadeInterna }">
		<table class="visualizacao" style="width: 90%">
			<tr>
				<td width="14%"></td>
				<th width="3%" style="text-align: right;" nowrap="nowrap">Categoria do Solicitante:</th>
				<td style="text-align: left;">${manifestacaoOuvidoria.obj.interessadoManifestacao.categoriaSolicitante.descricao }</td>
			</tr>
			<tr>
				<td width="14%"></td>
				<th width="3%" style="text-align: right;">Nome:</th>
				<td style="text-align: left;">
					<c:if test="${manifestacaoOuvidoria.categoriaDiscente }">
						${manifestacaoOuvidoria.discente.pessoa.nome }
					</c:if>
					<c:if test="${!manifestacaoOuvidoria.categoriaDiscente }">
						${manifestacaoOuvidoria.servidor.pessoa.nome }
					</c:if>
				</td>
			</tr>
			<c:if test="${manifestacaoOuvidoria.categoriaDiscente }">
				<tr>
					<td width="14%"></td>
					<th width="3%" style="text-align: right;">Matrícula:</th>
					<td style="text-align: left;">
							${manifestacaoOuvidoria.discente.matricula }
					</td>
				</tr>
				<tr>
					<td width="14%"></td>
					<th width="3%" style="text-align: right;">Curso:</th>
					<td style="text-align: left;">
							${manifestacaoOuvidoria.discente.curso.descricao }
					</td>
				</tr>
			</c:if>
			<c:if test="${!manifestacaoOuvidoria.categoriaDiscente }">
				<tr>
					<td width="14%"></td>
					<th width="3%" style="text-align: right;">SIAPE:</th>
					<td style="text-align: left;">
							${manifestacaoOuvidoria.servidor.siape }
					</td>
				</tr>
				<tr>
					<td width="14%"></td>
					<th width="3%" style="text-align: right;">Lotação:</th>
					<td style="text-align: left;">
							${manifestacaoOuvidoria.servidor.unidade }
					</td>
				</tr>
			</c:if>
		</table>
		<br />
		</c:if>
		<table class="formulario" width="90%">
			<caption>Informações sobre a manifestação</caption>
			<tbody>
				<tr>
					<th class="required" width="25%">Categoria do Assunto:</th>
					<td>
						<h:selectOneMenu id="categoria" value="#{manifestacaoOuvidoria.obj.assuntoManifestacao.categoriaAssuntoManifestacao.id }" onchange="submit()" style="width: 95%">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{categoriaAssuntoManifestacao.allCategoriasAtivasCombo }" />
							<a4j:support event="onselect" reRender="assunto" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="required">Assunto:</th>
					<td>
						<h:selectOneMenu id="assunto" value="#{manifestacaoOuvidoria.obj.assuntoManifestacao.id }" style="width: 95%">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{manifestacaoOuvidoria.allAssuntosByCategoriaCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="required">Tipo da Manifestação:</th>
					<td>
						<h:selectOneRadio id="tipo" value="#{manifestacaoOuvidoria.obj.tipoManifestacao.id }" style="width: 95%">
							<f:selectItems value="#{tipoManifestacao.allTiposManifestacaoCombo }" />
						</h:selectOneRadio>
					</td>
				</tr>
				<tr>
					<th class="required">Título:</th>
					<td>
						<h:inputText id="titulo" value="#{manifestacaoOuvidoria.obj.titulo }" maxlength="50" style="width: 95%" />
					</td>
				</tr>
				
				<tr>
					<th>Arquivo:</th>
					<td>
						<t:inputFileUpload id="ifuArquivo" size="70" storage="file" value="#{manifestacaoOuvidoria.arquivo}" />
					</td>
				</tr>
				
				<tr>
					<td style="text-align: right; vertical-align: top;">
						<h:selectBooleanCheckbox value="#{manifestacaoOuvidoria.obj.anonima}" styleClass="noborder" id="anonima" />
					</td>
					<td>
						<label for="anonima" onclick="$('form:anonima').checked = !$('form:anonima').checked;">
							Sigilo solicitado.
						</label>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<table class="subFormulario" width="100%">
							<caption style="text-align: center;">Mensagem <h:graphicImage url="/img/required.gif" /></caption>
							<tr>
								<td style="padding-right: 1%;">
									<h:inputTextarea id="mensagem" value="#{manifestacaoOuvidoria.obj.mensagem }" style="width: 100%;" rows="15" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btn_anterior" value="<< Passo Anterior" action="#{manifestacaoOuvidoria.voltarDadosUsuario }" />
						<h:commandButton id="btn_cancelar" value="Cancelar"  action="#{manifestacaoOuvidoria.cancelar }" onclick="#{confirm}"/>
						<h:commandButton id="btn_proximo" value="Próximo Passo >>" action="#{manifestacaoOuvidoria.submeterDadosManifestacao }" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<script type="text/javascript">
ConsultadorCep.init('/sigaa/consultaCep', 'form:endCEP', 'form:logradouro', 'form:endBairro', 'form:endMunicipio', 'form:ufEnd', function() {
	$('form:ufEnd').onchange();
} );
</script>