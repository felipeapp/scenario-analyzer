<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Gerar Distribui��o de Avalia��es de Trabalhos do CIC</h2>
	<div class="descricaoOperacao">
		<p>Esta opera��o ir� gerar uma distribui��o de avalia��es de trabalhos do Congresso de Inicia��o Cient�fica para os avaliadores cadastrados na base de dados.</p>
		<p>Apenas resumos com a situa��o APROVADO ser�o levados em conta na distribui��o.</p>
	</div>
	<h:form id="formDistribuicao">
		<table class="formulario" width="50%">
		<caption>Dados para gerar a distribui��o</caption>
			<tr>
				<th class="obrigatorio" width="50%">N�mero de avalia��es por trabalho:</th>
				<td>
					<h:inputText id="numeroAvaliacoes" value="#{avaliacaoApresentacaoResumoBean.numeroAvaliacoes}" 
						size="2" maxlength="1" onkeyup="formatarInteiro(this);"/>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btnDistribuir" action="#{avaliacaoApresentacaoResumoBean.distribuirAvaliacoesApresentacaoResumo}" value="Gerar Distribui��o"/>
						<h:commandButton id="btnCancelar" action="#{avaliacaoApresentacaoResumoBean.cancelar}" value="Cancelar" immediate="true" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>