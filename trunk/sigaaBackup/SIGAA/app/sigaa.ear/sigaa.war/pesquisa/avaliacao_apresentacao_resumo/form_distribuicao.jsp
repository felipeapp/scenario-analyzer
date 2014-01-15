<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Gerar Distribuição de Avaliações de Trabalhos do CIC</h2>
	<div class="descricaoOperacao">
		<p>Esta operação irá gerar uma distribuição de avaliações de trabalhos do Congresso de Iniciação Científica para os avaliadores cadastrados na base de dados.</p>
		<p>Apenas resumos com a situação APROVADO serão levados em conta na distribuição.</p>
	</div>
	<h:form id="formDistribuicao">
		<table class="formulario" width="50%">
		<caption>Dados para gerar a distribuição</caption>
			<tr>
				<th class="obrigatorio" width="50%">Número de avaliações por trabalho:</th>
				<td>
					<h:inputText id="numeroAvaliacoes" value="#{avaliacaoApresentacaoResumoBean.numeroAvaliacoes}" 
						size="2" maxlength="1" onkeyup="formatarInteiro(this);"/>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btnDistribuir" action="#{avaliacaoApresentacaoResumoBean.distribuirAvaliacoesApresentacaoResumo}" value="Gerar Distribuição"/>
						<h:commandButton id="btnCancelar" action="#{avaliacaoApresentacaoResumoBean.cancelar}" value="Cancelar" immediate="true" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>