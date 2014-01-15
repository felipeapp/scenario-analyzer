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
	<h2><ufrn:subSistema /> &gt; Notifica��o de Inven��o &gt; Propriedades de Terceiros</h2>

	<h:form id="form">
		
		<table class="formulario" width="100%">
			<caption>Utiliza��o de Propriedades de Terceiros</caption>
			
			<tr>
				<td colspan="2" class="subFormulario"> Utiliza��o de material biol�gico/gen�tico </td>
			</tr>
			
			<tr>
				<td colspan="2">
					<div class="descricaoOperacao">
						<p>
							Informar se, durante a execu��o das pesquisas que geraram a presente inven��o,
							foi utilizado algum material biol�gico/gen�tico que seja de propriedade de terceiros
							p. ex.: anticorpo, plasm�deo, DNA, seq��ncias de DNA, prote�nas ou composto qu�mico
							esclarecendo em que condi��es isso ocorreu: informalmente ou formalmente - p. ex.:
							Compra, Acordo de Transfer�ncia de Material, etc. 
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
				<td colspan="2" class="subFormulario"> Utiliza��o de software </td>
			</tr>
			
			<tr>
				<td colspan="2">
					<div class="descricaoOperacao">
						<p>
							Informar se, durante a execu��o das pesquisas que geraram a presente inven��o,
							foi utilizado algum software espec�fico para a obten��o da solu��o t�cnica da inven��o,
							que seja de propriedade de terceiros, e que teria potencial restri��o de uso na presente inven��o.
							Em caso afirmativo, favor anexar acordo, licen�a ou descri��o da potencial restri��o.  
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
						<h:commandButton value="Avan�ar >>" action="#{invencao.submeterPropriedadesTerceiros}" /> 
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<div class="obrigatorio"> Campos de preenchimento obrigat�rio. </div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
