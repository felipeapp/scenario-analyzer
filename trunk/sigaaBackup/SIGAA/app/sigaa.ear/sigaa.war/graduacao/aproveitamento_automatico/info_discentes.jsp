<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	.subFormulario th{
		font-weight: bold;
	}
</style>

<f:view>
	<h:outputText value="#{aproveitamentoAutomatico.create}" />
	<h2 class="title"><ufrn:subSistema /> > Aproveitamento Automático de Estudos</h2>
	<h:form id="formAproveitamento">
	<table class="formulario" width="70%">
	<caption>Discentes</caption>
	<tr><td>

		<table class="subFormulario" width="100%">
		<caption>Dados do discente de origem</caption>
		<tbody>


		<tr>
			<th>Matricula:</th>
			<td>${aproveitamentoAutomatico.discenteOrigem.matricula}</td>
		</tr>

		<tr>
			<th>Nome:</th>
			<td>${aproveitamentoAutomatico.discenteOrigem.nome}</td>
		</tr>

		<tr>
			<th>Data de Nascimento:</th>
			<td>
				<fmt:formatDate value="${aproveitamentoAutomatico.discenteOrigem.pessoa.dataNascimento}" pattern="dd/MM/yyyy"/>
			</td>
		</tr>


		<tr>
			<th>CPF:</th>
			<td>
			<ufrn:format type="cpf_cnpj" valor="${aproveitamentoAutomatico.discenteOrigem.pessoa.cpf_cnpj}"/>
			</td>

		</tr>

		<c:if test="${aproveitamentoAutomatico.discenteOrigem.curso.id > 0}">
		<tr>
			<th>Curso:</th>
			<td>${aproveitamentoAutomatico.discenteOrigem.curso}</td>
		</tr>
		</c:if>
		<c:if test="${aproveitamentoAutomatico.discenteOrigem.matrizCurricular.id > 0}">
		<tr>
			<th>Matriz:</th>
			<td>${aproveitamentoAutomatico.discenteOrigem.matrizCurricular.descricao}</td>
		</tr>
		</c:if>

		<tr>
			<th>Status:</th>
			<td>${aproveitamentoAutomatico.discenteOrigem.statusString}</td>
		</tr>
		<c:if test="${not aproveitamentoAutomatico.discenteOrigem.regular}">
		<tr>
			<td colspan="2" align="center">ALUNO ESPECIAL</td>
		</tr>
		</c:if>
		</tbody>
		</table>

	</td></tr>

	<tr><td>

		<table class="subFormulario" width="100%">
		<caption>Dados do discente de destino</caption>
		<tbody>


		<tr>
			<th>Matricula:</th>
			<td>${aproveitamentoAutomatico.discenteDestino.matricula}</td>
		</tr>

		<tr>
			<th>Nome:</th>
			<td>${aproveitamentoAutomatico.discenteDestino.nome}</td>
		</tr>

		<tr>
			<th>Data de Nascimento:</th>
			<td>
			<fmt:formatDate value="${aproveitamentoAutomatico.discenteDestino.pessoa.dataNascimento}" pattern="dd/MM/yyyy"/>
			</td>
		</tr>


		<tr>
			<th>CPF:</th>
			<td>
			<ufrn:format type="cpf_cnpj" valor="${aproveitamentoAutomatico.discenteDestino.pessoa.cpf_cnpj}"/>
			</td>

		</tr>


		<tr>
			<th>Curso:</th>
			<td>${aproveitamentoAutomatico.discenteDestino.curso}</td>
		</tr>

		<tr>
			<th>Matriz:</th>
			<td>${aproveitamentoAutomatico.discenteDestino.matrizCurricular.descricao}</td>
		</tr>

		<tr>
			<th>Status:</th>
			<td>${aproveitamentoAutomatico.discenteDestino.statusString}</td>
		</tr>

		</tbody>
		</table>

	</td></tr>

		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="<< Voltar" action="#{aproveitamentoAutomatico.telaDiscenteOrigem}" id="btnVoltar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{aproveitamentoAutomatico.cancelar}" id="btaoCancelarAproveitAutomatico"/>
					<h:commandButton value="Avançar >>" action="#{aproveitamentoAutomatico.exibirComponentes}" id="btnExibirComponentes"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
