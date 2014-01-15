<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Livro de Registro de ${livroRegistroDiplomas.obj.tipoRegistroDescricao} </h2>

	<h:form id="form">
	<table class="formulario" width="70%">
		<caption>Informe os Parâmetros da Busca </caption>
		<tbody>
			<c:if test="${fn:length(livroRegistroDiplomas.niveisHabilitadosCombo) > 1}">
				<tr>
					<td width="5%">
						<h:selectBooleanCheckbox value="#{livroRegistroDiplomas.buscaNivel}" id="buscaNivel"/>
					</td>
					<td width="20%"><span class="obrigatorio">Nível de Ensino:</span></td>
					<td>
						<h:selectOneMenu value="#{livroRegistroDiplomas.obj.nivel}"
							onchange="submit()" id="nivelEnsino" onfocus="$('form:buscaNivel').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{livroRegistroDiplomas.niveisHabilitadosCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<c:if test="${fn:length(livroRegistroDiplomas.niveisHabilitadosCombo) == 1}">
				<tr>
					<td width="5%">
					</td>
					<td width="20%" class="rotulo"><b>Nível de Ensino:</b></td>
					<td>
						<h:outputText value="#{livroRegistroDiplomas.niveisHabilitadosDesc[0]}" converter="convertNivelEnsino" />
					</td>
				</tr>
			</c:if>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{livroRegistroDiplomas.buscaTitulo}" id="buscaTitulo"/>
				</td>
				<td>Título:</td>
				<td>
					<h:inputText value="#{livroRegistroDiplomas.obj.titulo}" id="titulo"  size="14" maxlength="12" onkeyup="CAPS(this)" 
					 onfocus="$('form:buscaTitulo').checked = true;" />
				</td>
			</tr>
			<c:if test="${livroRegistroDiplomas.obj.graduacao}">
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{livroRegistroDiplomas.buscaCurso}" id="buscaCurso"/>
					</td>
					<td>Curso:</td>
					<td>
						<h:selectOneMenu value="#{livroRegistroDiplomas.idCurso}" id="curso" style="width: 90%;"
							onfocus="$('form:buscaCurso').checked = true;" >
							<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
							<f:selectItems value="#{curso.allCursoGraduacaoCombo}"/>
						</h:selectOneMenu> 
					</td>
				</tr>
			</c:if>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{livroRegistroDiplomas.buscaSituacao}" id="buscaSituacao"/>
				</td>
				<td>Situação:</td>
				<td>
					<h:selectOneMenu value="#{livroRegistroDiplomas.obj.ativo}" id="situacao" style="width: 90%;"
						onfocus="$('form:buscaSituacao').checked = true;" >
						<f:selectItem itemValue="true" itemLabel="ABERTO" />
						<f:selectItem itemValue="false" itemLabel="FECHADO" />
					</h:selectOneMenu>
				</td>
			</tr>
			<c:if test="${livroRegistroDiplomas.obj.graduacao}">
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{livroRegistroDiplomas.buscaExterno}" id="buscaExterno"/>
					</td>
					<td>Interno/Externo:</td>
					<td>
						<h:selectOneMenu value="#{livroRegistroDiplomas.obj.registroExterno}" id="externo" style="width: 90%;"
							onfocus="$('form:buscaExterno').checked = true;" >
							<f:selectItem itemValue="false" itemLabel="LIVROS INTERNOS" />
							<f:selectItem itemValue="true" itemLabel="LIVROS EXTERNOS" />
						</h:selectOneMenu>
					</td>
				</tr>
			</c:if>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{livroRegistroDiplomas.buscaAntigo}" id="buscaAntigo"/>
				</td>
				<td>Forma do Registro:</td>
				<td>
					<h:selectOneMenu value="#{livroRegistroDiplomas.obj.livroAntigo}" id="formaRegistro" style="width: 90%;"
						onfocus="$('form:buscaAntigo').checked = true;" >
						<f:selectItem itemValue="false" itemLabel="REGISTRO NO SIGAA" />
						<f:selectItem itemValue="true" itemLabel="REGISTRO EM LIVROS (ANTERIOR AO SIGAA)" />
					</h:selectOneMenu>
				</td>
			</tr>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="3" align="center">
				<h:commandButton action="#{livroRegistroDiplomas.buscar}" value="Buscar" id="btnBuscar"/>
				<h:commandButton action="#{livroRegistroDiplomas.cancelar}" value="Cancelar" onclick="#{confirm}" id="btnCancelar"/>
			</td>
		</tr>
		</tfoot>
	</table>
	<br>
	<center><html:img page="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span> <br>
	</center>
	<br>
	<c:if test="${not empty livroRegistroDiplomas.resultadosBusca}">
		<div class="infoAltRem">
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />:Alterar Dados do Livro
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />:Excluir Livro
			<h:graphicImage value="/img/book_blue_delete_s.gif" style="overflow: visible;" />: Fechar Livro
			<h:graphicImage value="/img/pdf.png" style="overflow: visible;" />: Gerar Arquivo para Impressão
		</div>
		<table class="listagem" >
			<caption>Livros Encontrados (${ fn:length(livroRegistroDiplomas.resultadosBusca)})</caption>
			<thead>
				<tr>
					<th>Nível</th>
					<th>Título</th>
					<th style="text-align: right;">Páginas</th>
					<th>Situação</th>
					<th rowspan="2"></th>
					<th rowspan="2"></th>
					<th rowspan="2"></th>
					<th rowspan="2"></th>
				</tr>
			</thead>
			<c:forEach items="#{livroRegistroDiplomas.resultadosBusca}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td>
						${item.nivelDesc}
					</td>
					<td>
						${item.titulo} 
						<h:outputText rendered="#{item.livroAntigo}" value="(Livro Antigo)" />
					</td>
					<td style="text-align: right;">
						${item.numeroFolhas}
					</td>
					<td>
						<h:outputText value="ABERTO" rendered="#{item.ativo}" />
						<h:outputText value="FECHADO" rendered="#{not item.ativo}" />
					</td>
					<td width="2%" rowspan="2">
						<h:commandLink title="Alterar Dados do Livro" action="#{livroRegistroDiplomas.atualizar}" id="alterarLivro" style="border: 0;" rendered="#{item.ativo}">
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/alterar.gif" />
						</h:commandLink>
					</td>
					<td width="2%" rowspan="2">
						<h:commandLink title="Excluir Livro" action="#{livroRegistroDiplomas.remover}" id="removerLivro" style="border: 0;" onclick="#{confirmDelete}" rendered="#{item.vazio && item.ativo}">
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/delete.gif" />
						</h:commandLink>
					</td>
					<td width="2%" rowspan="2">
						<h:commandLink title="Fechar Livro" action="#{livroRegistroDiplomas.fecharLivro}" id="fecharLivro" style="border: 0;" rendered="#{item.ativo}">
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/book_blue_delete_s.gif" />
						</h:commandLink>
					</td>
					<td width="2%" rowspan="2">
						<h:commandLink title="Gerar Arquivo para Impressão" action="#{livroRegistroDiplomas.imprimirLivro}" id="imprimirLivro" style="border: 0;">
							<f:param name="id" value="#{item.id}" />
							<h:graphicImage url="/img/pdf.png" />
						</h:commandLink>
					</td>
				</tr>
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td></td>
					<c:if test="${item.graduacao}">
						<td colspan="3">
								<b>Curso(s) Registrado(s):</b>
								<h:outputText rendered="#{not item.registroExterno}" value="#{item.descricaoCursos}"/>
							<h:outputText rendered="#{item.registroExterno}" value="Instituição: #{item.instituicao}" />
						</td>
					</c:if>
				</tr>
			</c:forEach>
		</table>
	</c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>