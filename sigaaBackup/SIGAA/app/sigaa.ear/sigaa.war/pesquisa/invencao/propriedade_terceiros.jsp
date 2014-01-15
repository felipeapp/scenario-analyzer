<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
#abas-descricao .aba {
	padding: 10px;
}

p.descricao {
	margin: 5px 100px;
	text-align: center;
	font-style: italic;
}
</style>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> &gt; Notificação de Invenção &gt; Propriedades de Terceiros</h2>

	<h:form id="form">
		
		<table class="formulario" width="100%">
			<caption>Utilização de Propriedades de Terceiros</caption>
			
			<tr>
				<td colspan="2" class="subFormulario"> Utilização de material biológico/genético </td>
			</tr>
			
			<tr>
				<td colspan="2">
					<div class="descricaoOperacao">
						<p>
							Informar se, durante a execução das pesquisas que geraram a presente invenção,
							foi utilizado algum material biológico/genético que seja de propriedade de terceiros
							p. ex.: anticorpo, plasmídeo, DNA, seqüências de DNA, proteínas ou composto químico
							esclarecendo em que condições isso ocorreu: informalmente ou formalmente - p. ex.:
							Compra, Acordo de Transferência de Material, etc. 
						</p>
					</div> 
				</td>
			</tr>
			
			<tr>
				<td align="center">
					<h:inputTextarea id="utilizacao-material" value="#{invencao.obj.utilizacaoMaterial}" cols="2" rows="10" style="width: 95%"/>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario"> Utilização de software </td>
			</tr>
			
			<tr>
				<td colspan="2">
					<div class="descricaoOperacao">
						<p>
							Informar se, durante a execução das pesquisas que geraram a presente invenção,
							foi utilizado algum software específico para a obtenção da solução técnica da invenção,
							que seja de propriedade de terceiros, e que teria potencial restrição de uso na presente invenção.
							Em caso afirmativo, favor anexar acordo, licença ou descrição da potencial restrição.  
						</p>
					</div> 
				</td>
			</tr>
			
			<tr>
				<td align="center">
					<h:inputTextarea id="utilizacao-software" value="#{invencao.obj.utilizacaoSoftware}" cols="2" rows="10" style="width: 95%"/>
				</td>
			</tr>
									
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="<< Voltar" action="#{invencao.telaRevelacoes}" />
						<h:commandButton value="Cancelar" action="#{invencao.cancelar}" onclick="#{confirm}" immediate="true"/>
						<h:commandButton value="Avançar >>" action="#{invencao.submeterPropriedadesTerceiros}" /> 
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
