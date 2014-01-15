<h:messages showDetail="true" />

<fieldset>
	<legend>Visualização de Conteúdo</legend>
	
<table class="formAva">

	<tr>
		<th>Título:</th>
		<td>
			${conteudoTurma.object.titulo}
		</td>
	</tr>
	
	<tr>
		<th style="vertical-align:top;">Conteúdo:</th>
		<td>
			<h:outputText value="#{ conteudoTurma.object.conteudo }" escape="false" />
		</td>
	</tr>
	
	<tr>
		<th style="width:120px;">Data Cadastro:</th>
		<td>
			<h:outputText value="#{conteudoTurma.object.dataCadastro}">
				<f:convertDateTime pattern="dd/MM/yyyy" />
			</h:outputText>
		</td>
	</tr>

</table>

<div class="botoes-show" align="center">

	<input type="hidden" name="id" value="${ conteudoTurma.object.id }"/>

	<c:if test="${ turmaVirtual.docente }">
	<h:commandButton action="#{conteudoTurma.editar}" value="Editar">
	</h:commandButton>	
	</c:if>
	
	<input value="<< Voltar" type="button" onClick="history.go(-1)"/>
	
</div>

</fieldset>