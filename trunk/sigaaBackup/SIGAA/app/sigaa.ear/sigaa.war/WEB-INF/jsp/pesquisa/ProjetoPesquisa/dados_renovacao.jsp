<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> &gt; Renovação de Projetos de Pesquisa &gt; Dados da Renovação </h2>

<html:form action="/pesquisa/projetoPesquisa/criarProjetoPesquisa" method="post">
	<table class="formulario">
		<caption>Dados do projeto a ser renovado</caption>
		<tbody>
			<c:set var="projeto" value="${ projetoPesquisaForm.obj }" />
			<%@include file="/WEB-INF/jsp/pesquisa/ProjetoPesquisa/include/dados_projeto.jsp"%>

			<tr>
				<td colspan="2" class="subFormulario"> Dados da Renovação </td>
			</tr>
			<c:if test="${ projetoPesquisaForm.obj.interno }">
				<tr>
					<th class="required">Edital:</th>
					<td>
						<c:set var="editaisAbertos" value="${projetoPesquisaForm.referenceData.editaisAbertos }" />
						<html:select property="obj.edital.id" style="width:90%">
					        <html:options collection="editaisAbertos" property="id" labelProperty="descricao" />
				        </html:select>
					</td>
				</tr>
			</c:if>
			<c:if test="${ not projetoPesquisaForm.obj.interno }">
				<tr>
					<th class="required">Novo Período do Projeto:</th>
					<td>
						<ufrn:calendar property="dataInicio"/> a <ufrn:calendar property="dataFim"/>
					</td>
				</tr>
			</c:if>
			
			<tr>
				<th class="required">Email:</th>
				<td>
					<html:text property="obj.email" maxlength="150" style="width: 90%" />
				</td>
			</tr>
			
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<html:button dispatch="listaRenovacao" value="<< Selecionar outro projeto"/>
					<html:button dispatch="cancelar" value="Cancelar"/>
					<c:if test="${ projetoPesquisaForm.obj.interno }">
						<html:button dispatch="docentes" value="Definir membros do projeto >>"/>
					</c:if>
					<c:if test="${ not projetoPesquisaForm.obj.interno }">
						<html:button dispatch="financiamentos" value="Definir financiamentos >>"/>
					</c:if>
				</td>
			</tr>
		</tfoot>
	</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>