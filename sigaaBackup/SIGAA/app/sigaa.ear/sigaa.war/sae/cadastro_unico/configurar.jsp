<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Cadastro Único > Configurar</h2>

	<div class="descricaoOperacao">
		<p>
			Selecione o questionário que os alunos irão responder durante a adesão do cadastro único.
		</p>
	</div>


<f:view>
	<h:form id="form">
		<table class="formulario" width="90%">
			<caption>Parametros do Cadastro Único</caption>
			<tbody>
				<tr>
					<th class="required">Questionário:</th>
					<td>
						<h:selectOneMenu value="#{cadastroUnicoBolsa.obj.questionario.id}">
							<f:selectItems value="#{cadastroUnicoBolsa.questionariosCombo}"/>
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="#{cadastroUnicoBolsa.confirmButton}"	action="#{cadastroUnicoBolsa.persistir}" />
						<h:commandButton value="Cancelar" action="#{cadastroUnicoBolsa.cancelar}" 	id="btcancelar" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>