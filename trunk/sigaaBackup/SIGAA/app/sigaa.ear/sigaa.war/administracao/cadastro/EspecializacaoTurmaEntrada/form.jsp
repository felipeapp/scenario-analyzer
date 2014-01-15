<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Especialização de Turmas de Entrada</h2>

	<table class=formulario width="70%">
		<h:form>
			<caption class="listagem">Cadastro de Especialização de Turmas de Entrada</caption>
			<h:inputHidden value="#{especializacaoTurma.confirmButton}" />
			<h:inputHidden value="#{especializacaoTurma.obj.id}" />
			<tr>
				<th class="obrigatorio">Descrição:</th>
				<td><h:inputText readonly="#{especializacaoTurma.readOnly}" value="#{especializacaoTurma.obj.descricao}" size="60"
					maxlength="80" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton value="#{especializacaoTurma.confirmButton}"
						action="#{especializacaoTurma.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{especializacaoTurma.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>