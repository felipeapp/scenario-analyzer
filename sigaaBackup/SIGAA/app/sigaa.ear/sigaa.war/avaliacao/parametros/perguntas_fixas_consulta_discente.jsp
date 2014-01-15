<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Perguntas Fixas no Relatório do Resultado Sintético por Departamento </h2>

	<h:form id="form">
		<div class="descricaoOperacao">
			<p>
				<b>Caro Usuário,</b>
			</p>
			<p>Na listagem abaixo você poderá selecionar as perguntas cuja as
				médias serão exibidas no Relatório Sintético da Avaliação
				Institucional por Departamento, que poderá ser visualizado pelos
				discentes.</p>
			<p>A pergunta selecionada abaixo só será exibida caso tenha
				constado no formulário de Avaliação do ano-período que estiver sendo
				consultado pelo discente.</p>
		</div>
		<table class="formulario" width="80%">
			<caption>Selecione as Perguntas</caption>
			<tbody>
				<tr>
					<td colspan="2">
						<h:selectManyCheckbox value="#{parametrosAvaliacaoInstitucionalBean.perguntasSelecionadas}" layout="pageDirection" immediate="true" id="perguntasSelecionadas">
							<f:selectItems value="#{parametrosAvaliacaoInstitucionalBean.perguntasComboBox}"/>
						</h:selectManyCheckbox>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Alterar Parâmetros" action="#{ parametrosAvaliacaoInstitucionalBean.cadastrarPerguntasFixas }" id="alterar"/> 
						<h:commandButton value="Cancelar" action="#{ parametrosAvaliacaoInstitucionalBean.cancelar }" onclick="#{confirm}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<br/>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>