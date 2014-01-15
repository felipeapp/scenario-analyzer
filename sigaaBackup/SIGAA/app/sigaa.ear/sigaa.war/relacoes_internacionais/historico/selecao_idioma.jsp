<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Emissão de Histórico do Discente &gt; Seleção de Idioma</h2>
	
	<div class="descricaoOperacao">
		Favor selecione o idioma que será utilizado para emitir o histórico do discente.
	</div>
	
	<h:form id="formulario">
		<table class=formulario width="60%">
			<caption class="listagem">Seleção de Idioma para Emissão de Histórico</caption>
			<tr>
				<td width="40%;"></td>
				<td align="left">
					<h:selectOneRadio id="idioma" value="#{historico.idioma}" layout="pageDirection">
						<f:selectItems value="#{historicoTraducaoMBean.allIdiomasCombo}" />
					</h:selectOneRadio>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton action="#{historico.selecionaIdioma}" value="Emitir Histórico" id="proximo" />
						<h:commandButton action="#{historico.voltar}" value="Cancelar" id="cancelar" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>	
	 </h:form>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>