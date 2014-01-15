<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2>Editar Perfil</h2>
	<h:outputText value="#{perfilCoordPolo.create}" />
	<h:form enctype="multipart/form-data">
		<table class="formulario" style="width: 97%;">
			<caption> Meu perfil </caption>
			<tbody>
			<tr>
				<td align="center" rowspan="2">
					<c:if test="${usuario.idFoto != null}">
						<img src="/sigaa/verFoto?idFoto=${usuario.idFoto}&key=${ sf:generateArquivoKey(usuario.idFoto) }" />
					</c:if>
					<c:if test="${usuario.idFoto == null}">
						<img src="/sigaa/img/no_picture.png" width="90" height="120" />
					</c:if>
					<br />
					Alterar Foto:<br>
					<t:inputFileUpload value="#{perfilCoordPolo.foto}" />
				</td>
			</tr>
			<tr>
				<td colspan="3">
					Descrição Pessoal (Breve descrição pessoal visualizada por outros usuários)
					<br />
					<h:inputTextarea value="#{perfilCoordPolo.obj.descricao}"	style="width: 98%;" rows="4" />
				</td>
			</tr>
			<tr>
				<td colspan="3">
					Formação acadêmica/profissional
					<br/>
					<h:inputTextarea value="#{perfilCoordPolo.obj.formacao}"	style="width: 98%;" rows="4" />
				</td>
			</tr>
			<tr>
				<td colspan="3">
					Áreas de Interesse
					<br/>
					<h:inputTextarea value="#{perfilCoordPolo.obj.areas}" style="width: 98%;" rows="3" />
				</td>
			</tr>
			<tr>
			<td colspan="3">Assinatura (utilizada nas mensagens da caixa postal) <br/>
				<h:inputTextarea value="#{perfilCoordPolo.obj.assinatura}" style="width: 98%;" rows="3" />
			</td>
			</tr>
			</tbody>
			<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{perfilCoordPolo.cadastrar}" value="Atualizar Perfil" />
					<h:commandButton action="#{perfilCoordPolo.cancelar}" value="Cancelar" />
				</td>
			</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
