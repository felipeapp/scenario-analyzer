<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form id="cadastroCurso" >

<h2 class="title"><ufrn:subSistema /> > Fechamento de Livro de Registro de ${livroRegistroDiplomas.obj.tipoRegistroDescricao}</h2>

<table class="visualizacao" width="99%">
	<caption>Dados do Livro</caption>
	<tbody>
		<tr>
			<th width="35%">Título:</th>
			<td>
				<h:outputText value="#{livroRegistroDiplomas.obj.titulo}"/>
			</td>
		</tr>
		<tr>
			<th>Número Sugerido de Folhas:</th>
			<td>
				<h:outputText value="#{livroRegistroDiplomas.obj.numeroSugeridoFolhas}" />
			</td>
		</tr>
		<tr>
			<th>Número de Páginas Utilizado:</th>
			<td>
				<h:outputText value="#{fn:length(livroRegistroDiplomas.obj.folhas)}" />
			</td>
		</tr>
		<tr>
			<th>Número de Registros por Página:</th>
			<td>
				<h:outputText value="#{livroRegistroDiplomas.obj.numeroRegistroPorFolha}" />
			</td>
		</tr>
		<c:if test="${not livroRegistroDiplomas.obj.registroExterno}">
			<tr>
				<th>Cursos Registrados:</th>
				<td>
					<h:outputText value="#{livroRegistroDiplomas.obj.descricaoCursos}" />
				</td>
			</tr>
		</c:if>
		<tr>
			<th>Instituição:</th>
			<td>
				<h:outputText value="#{livroRegistroDiplomas.obj.instituicao}" />
			</td>
		</tr>
	</tbody>
</table>
<table class="formulario" width="99%">
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="#{livroRegistroDiplomas.confirmButton}" action="#{livroRegistroDiplomas.cadastrar}" id="btnCadastrar"/>
				<h:commandButton value="<< Voltar" action="#{livroRegistroDiplomas.backList}" id="btnVoltar"/>
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{livroRegistroDiplomas.cancelar}" immediate="true" id="btnCancelar"/>
			</td>
		</tr>
	</tfoot>
</table>

<br>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
