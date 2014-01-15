<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > 
	<h:outputText value="Defini��o de Leiaute do Arquivo de Importa��o" rendered="#{ importaAprovadosOutrosVestibularesMBean.definicaoLeiaute }" />
	<h:outputText value="Importar Aprovados de Vestibulares Externos" rendered="#{ !importaAprovadosOutrosVestibularesMBean.definicaoLeiaute }" />
	</h2>

	<div class="descricaoOperacao">
		<p><b>Caro Usu�rio,</b></p>
		<c:if test="${ importaAprovadosOutrosVestibularesMBean.definicaoLeiaute }">
			<p>Este formul�rio permite definir um leiaute para o arquivo de dados de inscri��es de outros concursos para o SIGAA. O arquivo importado dever� estar no formato CSV (Comma-separated values).</p>
			<p><b>Antes de continuar, verifique se o arquivo contem as seguintes informa��es que s�o obrigat�rias para a importa��o de dados:</b>
			<ul>
				<c:forEach items="#{importaAprovadosOutrosVestibularesMBean.atributosMapeaveisObrigatorios }" var="item">
					<li><h:outputText value="#{item.descricao}"/>
						- <h:outputText value="#{item.tipoDado}"/></li>
				</c:forEach>
			</ul>
		</c:if>
		<c:if test="${ not importaAprovadosOutrosVestibularesMBean.definicaoLeiaute }">
			<p>Este formul�rio permite importar dados de inscri��es de outros concursos para o SIGAA. O arquivo importado dever� estar no formato CSV (Comma-separated values).</p>
		</c:if>
	</div>
	<br/>
	<h:form enctype="multipart/form-data" id="form">
		<table class="formulario" width="75%">
			<caption>Informe o Arquivo com os Dados para Importa��o</caption>
			<tbody>
				<c:if test="${ importaAprovadosOutrosVestibularesMBean.definicaoLeiaute }">
					<tr>
						<th class="obrigatorio">Forma de Ingresso:</th>
						<td>
							<h:selectOneMenu id="formaIngresso" 
								disabled="#{importaAprovadosOutrosVestibularesMBean.readOnly}"
								value="#{importaAprovadosOutrosVestibularesMBean.leiauteArquivoImportacao.formaIngresso.id}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{formaIngresso.allCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
					<tr>
						<th class="obrigatorio">Descri��o:</th>
						<td>
							<h:inputText value="#{importaAprovadosOutrosVestibularesMBean.leiauteArquivoImportacao.descricao}" 
								disabled="#{importaAprovadosOutrosVestibularesMBean.readOnly}"	
								size="60" maxlength="120" id="descricao" />
						</td>
					</tr>
				</c:if>
				<c:if test="${ not importaAprovadosOutrosVestibularesMBean.definicaoLeiaute }">
					<tr>
						<th class="obrigatorio">Processo Seletivo:</th>
						<td>
							<h:selectOneMenu id="processoSeletivo" 
								disabled="#{importaAprovadosOutrosVestibularesMBean.readOnly}"
								value="#{importaAprovadosOutrosVestibularesMBean.obj.processoSeletivo.id}"
								valueChangeListener="#{importaAprovadosOutrosVestibularesMBean.processoSeletivoListener}"
								onchange="submit()">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{processoSeletivoVestibular.allExternosCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
					<tr>
						<th class="obrigatorio">Leiaute do Arquivo:</th>
						<td>
							<h:selectOneMenu id="leiauteArquivo" 
								disabled="#{importaAprovadosOutrosVestibularesMBean.readOnly}"
								value="#{importaAprovadosOutrosVestibularesMBean.leiauteArquivoImportacao.id}">
								<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
								<f:selectItems value="#{importaAprovadosOutrosVestibularesMBean.leiauteImportacaoCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
				</c:if>
				<tr>
					<th class="obrigatorio">Arquivo:</th>
					<td>
						<t:inputFileUpload value="#{importaAprovadosOutrosVestibularesMBean.arquivoUpload}" styleClass="file" id="nomeArquivo"/>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Codifica��o do arquivo:</th>
					<td>
						<h:selectOneMenu value="#{importaAprovadosOutrosVestibularesMBean.codificacao }" id="codificacao">
							<f:selectItems value="#{importaAprovadosOutrosVestibularesMBean.codificacaoArquivoCombo }"  />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Dados Separados por:</th>
					<td>
						<h:selectOneRadio value="#{importaAprovadosOutrosVestibularesMBean.separador }" id="separador">
							<f:selectItems value="#{importaAprovadosOutrosVestibularesMBean.possiveisSeparadores }"  />
						</h:selectOneRadio>
					</td>
				</tr>
				<c:if test="${ importaAprovadosOutrosVestibularesMBean.definicaoLeiaute }">
					<tr>
						<th><h:selectBooleanCheckbox value="#{ importaAprovadosOutrosVestibularesMBean.leiauteArquivoImportacao.possuiCabecalho }"/> </th>
						<td>
							A primeira linha do arquivo possui cabe�alho.
						</td>
					</tr>
				</c:if>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Carregar Arquivo" action="#{importaAprovadosOutrosVestibularesMBean.carregarArquivo}" id="carregarArquivo"/>
						<h:commandButton value="Cancelar" action="#{importaAprovadosOutrosVestibularesMBean.cancelar}" onclick="#{confirm}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		</h:form>
	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
	</center>
	<br>
	<br>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>