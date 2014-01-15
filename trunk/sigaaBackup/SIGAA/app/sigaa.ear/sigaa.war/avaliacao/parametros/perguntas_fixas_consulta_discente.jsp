<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Perguntas Fixas no Relat�rio do Resultado Sint�tico por Departamento </h2>

	<h:form id="form">
		<div class="descricaoOperacao">
			<p>
				<b>Caro Usu�rio,</b>
			</p>
			<p>Na listagem abaixo voc� poder� selecionar as perguntas cuja as
				m�dias ser�o exibidas no Relat�rio Sint�tico da Avalia��o
				Institucional por Departamento, que poder� ser visualizado pelos
				discentes.</p>
			<p>A pergunta selecionada abaixo s� ser� exibida caso tenha
				constado no formul�rio de Avalia��o do ano-per�odo que estiver sendo
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
						<h:commandButton value="Alterar Par�metros" action="#{ parametrosAvaliacaoInstitucionalBean.cadastrarPerguntasFixas }" id="alterar"/> 
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